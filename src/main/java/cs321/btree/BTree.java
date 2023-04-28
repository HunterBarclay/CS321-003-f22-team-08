package cs321.btree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Stack;
import java.util.LinkedList;

/**
 * BTree creates a BTree data structure and store it in a given directory.
 * 
 * BTree is implemented in such a way that it can store a generic object and keep track
 * of how many times that given object (or key) was inserted into the data structure. It
 * stores a counter for each unique key.
 * 
 * Resources Used:
 *  - Used to understand iterable versus iterator: https://www.baeldung.com/java-iterator-vs-iterable
 */
public class BTree<E extends Comparable<E>> implements Serializable, Iterable<TreeObject<E>> {

    private static final String META_FILE_NAME = "meta.tree";
    private static final String NODE_FILE_EXTENSION = ".node";

    private int maxFilesPerDirectory = 1000;
    private long nextGuid = 0; // Only use positives
    private long rootGuid;
    private String treeDirectory;
    private int degree;
    private long numKeys;
    private int numNodes;

    /**
     * Constructs a BTree
     * 
     * @param treeDirectory Directory to store the tree in
     * @param degree        Maximum degree of the BTree
     */
    public BTree(String treeDirectory, int degree) {
        // Will always create a new BTree with constructor

        try {
            if (Paths.get(treeDirectory).toFile().exists()) {
                deleteDirectoryRecursive(Paths.get(treeDirectory));
            }
            Files.createDirectory(Paths.get(treeDirectory));
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.treeDirectory = treeDirectory;
        this.degree = degree;
        numKeys = 0;
        numNodes = 0;

        rootGuid = allocateNode(); // increments node count
        if (rootGuid == -1) {
            throw new RuntimeException();
        }
        
        updateMetaFile();
    }

    /**
     * Loads a stored BTree
     * 
     * @param <T>                       Generic type the BTree stores
     * @param treeDirectory             Directory with the BTree contents
     * @return                          Deserialized BTree object
     * @throws FileNotFoundException    If the directory doesn't exist
     */
    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> BTree<T> loadBTree(String treeDirectory) throws FileNotFoundException {
        File f = new File(String.format("%s/%s", treeDirectory, META_FILE_NAME));
        if (!f.exists())
            throw new FileNotFoundException();

        BTree<T> res = null;

        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            res = (BTree<T>)ois.readObject();
            ois.close();
            fis.close();
            res.treeDirectory = treeDirectory;
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return res;
    }

    /**
     * Inserts Object into the BTree
     * @param key Key to insert
     */
    public void insert(E key) {
    	BTreeNode node = readDisk(rootGuid);
    	TreeObject<E> obj = new TreeObject<E>(key);
        boolean result = node.insert(obj, this);
        if (result)
            numKeys++;

        updateMetaFile();
    }
    
    /**
     * returns the total number of keys
     * @return number of keys
     */
    public long getNumKeys() {
    	return numKeys;
    }
    
    /**
     * returns the total number of nodes
     * @return number of nodes
     */
    public int getNumNodes() {
    	return numNodes;
    }

    /**
     * Searches the BTree for a matching key and returns the TreeObject 
     * that contains that given key if found
     * @param key Key to find
     * @return TreeObject containing key
     */
    public TreeObject<E> search(E key) {
        return search(readDisk(rootGuid), key);
    }

     /**
     * Searches the BTree for a matching key and returns the TreeObject 
     * that contains that given key if found
     * @param key Key to find
     * @return TreeObject containing key
     */
    public TreeObject<E> search(E key, Cache cache) {
        return search(readDisk(rootGuid), key, cache);
    }

    /**
     * Public helper method that searches and returns found TreeObject 
     * given a node and a key
     * @param node the node to be searched in
     * @param key Key to find
     * @return TreeObject at given key
     */
    public TreeObject<E> search(BTreeNode node, E key) {
        int i = 0;
        while (i < node.getNumKeys() && key.compareTo(node.getKey(i).getKey()) > 0) {
            i++;
        }
        if (i < node.getNumKeys() && key.compareTo(node.getKey(i).getKey()) == 0) {
            TreeObject<E> copyTreeObject = new TreeObject<E>(key);
            copyTreeObject.setInstances(node.getKey(i).getInstances());
            return copyTreeObject;
        }
        else if (node.numChildren == 0) {
            return null;
        }
        else {
            return search(readDisk(node.children[i]), key);
        }
    }

    /**
     * Private helper method that searches and returns found TreeObject 
     * given a node and a key
     * @param node the node to be searched in
     * @param key Key to find
     * @return TreeObject at given key
     */
    public TreeObject<E> search(BTreeNode node, E key, Cache cache) {
        int i = 0;
        while (i < node.getNumKeys() && key.compareTo(node.getKey(i).getKey()) > 0) {
            i++;
        }
        if (i < node.getNumKeys() && key.compareTo(node.getKey(i).getKey()) == 0) {
            TreeObject<E> copyTreeObject = new TreeObject<E>(key);
            copyTreeObject.setInstances(node.getKey(i).getInstances());
            return copyTreeObject;
        }
        else if (node.numChildren == 0) {
            return null;
        }
        else {
            BTreeNode node2 = cache.getObject(node.children[i]);
            return search(node2, key, cache);
        }
    }

    /**
     * Allocates new disk space for a new BTreeNode
     * @return GUID for accessing BTreeNode
     */
    private long allocateNode() {
        long newGuid = nextGuid;
        nextGuid++;
        numNodes++;

        BTreeNode newNode = new BTreeNode(newGuid, degree);

        if (writeDisk(newNode)) {
            return newGuid;
        } else {
            return -1;
        }
    }

    /**
     * Fetches BTreeNode currently stored in disk memory
     * @param nodeGuid GUID for BTreeNode to access
     * @return BTreeNode read from disk
     */
    @SuppressWarnings("unchecked")
    private BTreeNode readDisk(long nodeGuid) {
        File f = getNodeFile(nodeGuid);

        if (!f.exists())
            throw new RuntimeException("No such file exists");

        BTreeNode res = null;
        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            res = (BTreeNode)ois.readObject();
            ois.close();
            fis.close();
        } catch (Exception e) { }

        return res;
    }

    /**
     * Writes BTreeNode to disk
     * @param nodeGuid GUID for location of BTreeNode in disk
     * @return Whether or not write was successful
     */
    private boolean writeDisk(BTreeNode node) {
        File f = getNodeFile(node.guid);
        try {
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(node);
            oos.flush();
            oos.close();
            fos.close();

        } catch (Exception e) {
            return false;
        }

        return true;
    }
    
    /**
     * Update the main meta file to store the new core data such as the root node, size, etc.
     */
    private void updateMetaFile() {
        File treeFile = new File(String.format("%s/%s", treeDirectory, META_FILE_NAME));
        try {
            treeFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(treeFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.flush();
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns file location given a GUID
     * 
     * @param guid  GUID of node
     * @return      File location of node
     */
    private File getNodeFile(long guid) {
        long file = Math.floorMod(guid, maxFilesPerDirectory);
        long directory = Math.floorDiv(guid, maxFilesPerDirectory);

        String partitionDirectory = String.format("%s/%d/", treeDirectory, directory);
        if (!Files.exists(Paths.get(partitionDirectory))) {
            try {
                Files.createDirectory(Paths.get(partitionDirectory));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String paritionFile = String.format("%s/%d%s", partitionDirectory, file, NODE_FILE_EXTENSION);
        return new File(paritionFile);
    }

    /**
     * rm -rf for given directory.
     * 
     * @param directory Directory to delete
     */
    private void deleteDirectoryRecursive(Path directory) {
        try {
            Iterator<Path> iter = Files.newDirectoryStream(directory).iterator();
            while (iter.hasNext()) {
                Path entry = iter.next();
                if (Files.isDirectory(entry)) {
                    deleteDirectoryRecursive(entry);
                } else {
                    Files.delete(entry);
                }
            }
            Files.delete(directory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the degree of the tree
     * @return Degree of the tree
     */
    public int getDegree() { return degree; }

    @Override
    public String toString() {
        StringBuilder  builder = new StringBuilder();
        readDisk(rootGuid).toString(builder, 0);
        return builder.toString();
    }
    
    /**
     * To string without depth
     * 
     * @return  String representation of tree
     */
    public String toStringParseable() {
        StringBuilder  builder = new StringBuilder();
        readDisk(rootGuid).toStringParseable(builder);
        return builder.toString();
    }
    
    /**
     * Node in the BTree that stores TreeObjects
     */
    private class BTreeNode implements Serializable {

        private int numKeys;
        private int numChildren; //needed to check for leaves
        private long guid;
        private long parent;
        private long[] children;
        private TreeObject<E>[] keys;

        /**
         * Constructs a new BTreeNode
         * @param guid GUID used to reference the node
         * @param degree Degree of the node
         */
        @SuppressWarnings("unchecked")
        public BTreeNode(long guid, int degree) {
            keys = (TreeObject<E>[])Array.newInstance(TreeObject.class, degree); // extra room for splitting
            children = new long[degree + 1];

            this.guid = guid;
            parent = -1;
            numKeys = 0;
            numChildren = 0;
            for(int i = 0; i < degree; i++) {
            	children[i] = -1;
            }
        }

        /**
         * Get the parent BTreeNode
         * @return Parent's GUID
         */
        public long getParent() { return parent; }

        /**
         * Get the node's GUID
         * @return GUID of the node
         */
        public long getGuid() { return guid; }

        /**
         * Get the number of keys stored in the node
         * @return Number of keys stored in the node
         */
        public int getNumKeys() { return numKeys; }

        /**
         * Get the number of children this BTreeNode has
         * @return Number of children
         */
        public int getNumChildren() { return numChildren; } // TODO: Needed?

        /**
         * Gets a child node's GUID for this node
         * @param index Index of Child
         * @return GUID for child node
         */
        public long getChild(int index) {
            if (index < 0 || index >= numChildren)
                throw new IndexOutOfBoundsException();
            
            return children[index];
        }

        /**
         * Gets a specific key for this node
         * @param index Index of key
         * @return Key at Index
         */
        public TreeObject<E> getKey(int index) {
            if (index < 0 || index >= numKeys)
                throw new IndexOutOfBoundsException();
            
            return keys[index];
        }

        /**
         * inserts the given object into the node
         * @param object    object to insert
         * @param tree      Tree reference because Java is actually remarkably dumb
         * @return          True if a new key was added, false if the key already existed.
         */
        public boolean insert(TreeObject<E> object, BTree<E> tree) {
        	
            if (isLeaf()) {

                int i = 0;
                while (i < this.numKeys && object.compareTo(this.keys[i]) > 0) {
                    i++;
                }

                if (i < this.numKeys && object.compareTo(this.keys[i]) == 0) {
                    this.keys[i].incrementInstances();
                    writeDisk(this);
                    return false;
                } else {
                    TreeObject<E> tmp = this.keys[i];
                    this.keys[i] = object;
                    this.numKeys++;
                    i++;
                    while (i < this.numKeys) {
                        TreeObject<E> tmp2 = this.keys[i];
                        this.keys[i] = tmp;
                        tmp = tmp2;
                        i++;
                    }
                }

                writeDisk(this);

                if (numKeys >= degree && parent == -1) { // If root leaf
                    splitRoot(tree);
                }

                return true;

            } else {

                int childIndex = -1;

                if (object.compareTo(this.keys[numKeys - 1]) > 0) { // If key is greater than largest key in node
                    // readDisk(this.children[numChildren - 1]).insert(object);
                    childIndex = numChildren - 1;
                } else if (object.compareTo(this.keys[this.numKeys - 1]) == 0) {
                    this.keys[this.numKeys - 1].incrementInstances();
                    writeDisk(this);
                    return false;
                } else {
                    int i = this.numKeys - 2;
                    while (i >= 0 && object.compareTo(this.keys[i]) < 0) {
                        i--;
                    }

                    if (i >= 0 && object.compareTo(this.keys[i]) == 0) {
                        this.keys[i].incrementInstances();
                        writeDisk(this);
                        return false;
                    } else {
                        // readDisk(this.children[i + 1]).insert(object);
                        childIndex = i + 1;
                    }
                }

                BTreeNode childNode = readDisk(this.children[childIndex]);
                boolean result = childNode.insert(object, tree);
                if (childNode.numKeys >= degree) {
                    splitChild(childIndex, childNode, tree);
                }

                if (numKeys >= degree && parent == -1) {
                    splitRoot(tree);
                }

                return result;

            }

        }

        /**
         * Splits a specified child node
         * 
         * @param childIndex    Index of target child
         * @param child         Deserialized child node
         * @param tree          Tree reference because Java is actually remarkably dumb
         */
        public void splitChild(int childIndex, BTreeNode child, BTree<E> tree) {
            int medianIndex = child.numKeys / 2; // Right-Hand Median
            BTreeNode rightChild = readDisk(tree.allocateNode());

            // Move keys over
            rightChild.numKeys = (child.numKeys - medianIndex) - 1;
            for (int i = 0; i < rightChild.numKeys; i++) {
                rightChild.keys[i] = child.keys[i + medianIndex + 1];
                child.keys[i + medianIndex + 1] = null;
            }
            TreeObject<E> medianKey = child.keys[medianIndex];
            child.keys[medianIndex] = null;
            child.numKeys = (child.numKeys - rightChild.numKeys) - 1;

            // Move Children over
            if (!child.isLeaf()) {
                rightChild.numChildren = child.numChildren - (child.numKeys + 1);
                for (int i = 0; i < rightChild.numChildren; i++) {
                    rightChild.children[i] = child.children[i + (child.numKeys + 1)];
                    child.children[i + (child.numKeys + 1)] = -1;
                }
                child.numChildren = child.numChildren - rightChild.numChildren;
            }

            rightChild.parent = this.guid;

            // Reorganize parent
            for (int i = this.numKeys; i > childIndex; i--) {
                this.keys[i] = this.keys[i - 1];
                this.children[i + 1] = this.children[i];
            }
            this.keys[childIndex] = medianKey;
            this.children[childIndex + 1] = rightChild.guid;
            this.numKeys++;
            this.numChildren++;

            writeDisk(child);
            writeDisk(rightChild);
            writeDisk(this);
        }

        /**
         * Splits a node that is root
         * 
         * @param tree  Tree reference because Java is actually remarkably dumb
         */
        public void splitRoot(BTree<E> tree) {
            BTreeNode newRoot = readDisk(tree.allocateNode());
            parent = newRoot.guid;
            tree.rootGuid = newRoot.guid;
            newRoot.numChildren = 1;
            newRoot.children[0] = this.guid;
            newRoot.splitChild(0, this, tree);
        }
        
        /**
         * checks if the node is a leaf node
         * @return true if the node has no children
         */
        public boolean isLeaf() {
        	if (numChildren == 0) {
        		return true;
        	}
        	return false;
        }

        /**
         * To string method for BTreeNode that prints the elements with some spacing
         * out to show the depth of a given key
         * 
         * Example:
         *  -  - (0)0 1
         *  -  - (1)1 1
         *  - (0)2 1
         *  -  - (0)3 1
         *  -  - (1)4 1
         * 
         * @param builder   String builder to better construct the final result
         * @param depth     Depth tracker
         */
        public void toString(StringBuilder builder, int depth) {
            if (isLeaf()) {
                for (int i = 0; i < numKeys; i++) {
                    for (int j = 0; j < depth + 1; j++) {
                        builder.append(" - ");
                    }
                    builder.append(String.format("(%d)", i));
                    builder.append(keys[i].toString());
                    builder.append("\n");
                }
            } else {
                readDisk(children[0]).toString(builder, depth + 1);
                for (int i = 0; i < numKeys; i++) {
                    for (int j = 0; j < depth + 1; j++) {
                        builder.append(" - ");
                    }
                    builder.append(String.format("(%d)", i));
                    builder.append(keys[i].toString());
                    builder.append("\n");
                    readDisk(children[i + 1]).toString(builder, depth + 1);
                }
            }
        }
        
        /**
         * toString with no depth for easier use in parsing
         * @param builder builder to make the string
         */
        public void toStringParseable(StringBuilder builder) {
            if (isLeaf()) {
                for (int i = 0; i < numKeys; i++) {
                    builder.append(keys[i].toString());
                    builder.append("\n");
                }
            } else {
                readDisk(children[0]).toStringParseable(builder);
                for (int i = 0; i < numKeys; i++) {
                    builder.append(keys[i].toString());
                    builder.append("\n");
                    readDisk(children[i + 1]).toStringParseable(builder);
                }
            }    
        } 
        
    }

    /**
     * This is a class that creates a Cache object that uses a linked
     * list for its data structure. Its intended to speed up the B-tree
     */
    public class Cache {
        private int maxCacheSize;
        LinkedList<BTreeNode> cache;
        private int numCacheHits;
        private int numCacheReferences;
        private int size;
        private final DecimalFormat df = new DecimalFormat("0.00");
        
        /**
         * Constructor that creates a Cache Object which creates a
         * Linked List of a general object type.
         * @param cacheSize The size of available cache
         */
        public Cache(int cacheSize) {
            cache = new LinkedList<BTreeNode>();
            maxCacheSize = cacheSize;
            numCacheReferences = 0;
            numCacheHits = 0;
        }

        /**
         * Searches the Cache to see if the desired object is in the
         * cache. If it's not, it will add it to the front of the cache.
         * @return Desired object
         */
        public BTreeNode getObject(long nodeGuid) {
            numCacheReferences++;
            BTreeNode nodeToGet;
            for (int i = 0; i < cache.size(); i++) {
                nodeToGet = cache.get(i);
                if (nodeToGet.getGuid() == nodeGuid) {
                    cache.remove(i);
                    numCacheHits++;
                    this.addObject(nodeToGet);
                    return nodeToGet;
                } 
            }
            nodeToGet = readDisk(nodeGuid);
            this.addObject(nodeToGet);
            return nodeToGet;
        }
        /**
         * Private helper method used in getObject() method in order
         * to add objects to the cache.
         * @param element Element desired to have added to the cache.
         */
        private void addObject(BTreeNode element) {
            if(cache.size() == maxCacheSize) {
                cache.removeLast();
            }
            cache.addFirst(element);
        }
        
        /**
         * Private helper method for the getObject() method for removing
         * an element, especially when the cache gets too big.
         */
        private void removeObject(BTreeNode element) {
            cache.remove(element);
        }
        
        /**
         * Clears the cache.
         */
        public void clearCache() {
            cache.clear();
        }
        
        @Override
        public String toString() {
            Double percentHit = ((double)numCacheHits / (double)numCacheReferences) * 100;
            String returnString = "Total number of references:        " + numCacheReferences;
            returnString+= "\nTotal number of cache hits:        " + numCacheHits;
            returnString+= "\nCache hit ratio:                   " + df.format(percentHit) + "%\n";
            return returnString;
        }

    }

    @Override
    public Iterator<TreeObject<E>> iterator() {
        return new BTreeIterator();
    }

    /**
     * Iterator for iterating through the contents of the BTree in order
     */
    private class BTreeIterator implements Iterator<TreeObject<E>> {

        private Stack<PathStep> currentPath;
        private BTreeNode currentNode;
        private int currentNodeIndex;

        /**
         * Constructs the iterator
         */
        public BTreeIterator() {
            currentPath = new Stack<PathStep>();

            BTreeNode cursor = readDisk(rootGuid);
            while (!cursor.isLeaf()) {
                currentPath.add(new PathStep(cursor, 0));
                cursor = readDisk(cursor.children[0]);
            }

            currentNode = cursor;
            currentNodeIndex = 0;
        }

        @Override
        public boolean hasNext() {
            return !(currentPath.empty() && currentNodeIndex >= currentNode.numKeys);
        }

        @Override
        public TreeObject<E> next() {

            if (!hasNext())
                throw new RuntimeException("Nothing new");

            TreeObject<E> returnValue;

            if (currentNode.isLeaf()) {
                returnValue = currentNode.keys[currentNodeIndex];
                currentNodeIndex++;
                while (currentNodeIndex >= currentNode.numKeys && currentPath.size() > 0) {
                    PathStep step = currentPath.pop();
                    currentNode = step.node;
                    currentNodeIndex = step.lastVisitedChild;
                }
            } else {
                returnValue = currentNode.keys[currentNodeIndex];
                currentNodeIndex++;
                while (!currentNode.isLeaf()) {
                    currentPath.add(new PathStep(currentNode, currentNodeIndex));
                    currentNode = readDisk(currentNode.children[currentNodeIndex]);
                    currentNodeIndex = 0;
                }
            }

            return new TreeObject<E>(returnValue);
        }

        private class PathStep {
            public BTreeNode node;
            public int lastVisitedChild;

            public PathStep(BTreeNode node, int lastVisitedChild) {
                this.node = node;
                this.lastVisitedChild = lastVisitedChild;
            }
        }

    }

    /**
     * Establishes a database connection to the SQLite file
     * 
     * TODO: Adjust to make sure naming is as directed in README
     * 
     * @return  Connection to the database
     */
    public Connection makeDatabaseConnection() {
        // See: https://www.sqlitetutorial.net/sqlite-java/create-database/
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s/btree-sql.sqlite", treeDirectory));
            if (connection == null)
                throw new RuntimeException("Failed to create database");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

}
