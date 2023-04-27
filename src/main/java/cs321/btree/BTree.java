package cs321.btree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Iterator;
import java.util.Stack;

/**
 * TODO
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
    	// long nodeGuid = node.getGuid();
    	// int i;
    	// while (!node.isLeaf()) {
    	// 	for(i = 0; i < node.getNumKeys(); i++) {
    	// 		if(node.getKey(i).compareTo(obj) == 0) {
    	// 			node.getKey(i).incrementInstances();
    	// 			return;
    	// 		}
    	// 		if(node.getKey(i).compareTo(obj) > 0) {
    	// 			break;
    	// 		}
    	// 	}
    	// 	node = readDisk(node.getChild(i));
    	// }
    	
        // if (node.insert(obj))
    	//     numKeys++;
        // if (node.isFull()) {
    	// 	node = splitNode(node);
    	// }
    	// writeDisk(node);
        // updateMetaFile();
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
     * Searchs and returns found TreeObject given key
     * @param element Key to find
     * @return TreeObject at given key
     */
    public TreeObject<E> search(E key) {
        throw new RuntimeException();
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
     * Split the given node, then return the id of the parent
     * @return parent node
     */
    // private BTreeNode splitNode(BTreeNode node) {
    // 	BTreeNode parentNode = null;
    //     // If node is the root
    // 	if (node.getParent() == -1) {
    // 		node.setParent(allocateNode());
    // 		parentNode = readDisk(node.getParent());
    // 		parentNode.addChild(node.getGuid());
    // 	}
    // 	if(parentNode == null) {
    // 		parentNode = readDisk(node.getParent());
    // 	}
    // 	int index = (node.getNumKeys() - 1) / 2;
    // 	parentNode.insert(node.getKey(index));
    // 	node.removeKey(index);
    	
    // 	if(node.getGuid() == rootGuid) {
	// 		rootGuid = parentNode.getGuid();
	// 	}
    	
    // 	BTreeNode newNode = readDisk(allocateNode());
    // 	int numKeys = node.getNumKeys();
    // 	for (int i = index + 1; i <= numKeys; i++) {
    // 		newNode.insert(node.getKey(index));
    // 		node.removeKey(index);
    // 		if (node.getNumChildren() != 0) {
    // 			newNode.addChild(node.getChild(index + 1));
    // 			node.removeChild(index + 1);
    // 		}
    		
    // 	}
    	
    // 	if (node.getNumChildren() > node.getNumKeys() + 1) {
    // 		newNode.addChild(node.getChild(node.getNumChildren() - 1));
    // 		node.removeChild(node.getNumChildren() - 1);
    // 	}
    // 	newNode.setParent(parentNode.getGuid());
    // 	parentNode.addChild(newNode.getGuid());
    // 	writeDisk(newNode);
    // 	writeDisk(parentNode);	
    // 	writeDisk(node);
    // 	if (parentNode.isFull()) {
    // 		splitNode(parentNode);
    // 	}
    	
    // 	//node.decreaseKeysAfterSplit();
    //     return parentNode;
    // }

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
         * @param object object to insert
         * @return True if a new key was added, false if the key already existed.
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

        public void splitRoot(BTree<E> tree) {
            BTreeNode newRoot = readDisk(tree.allocateNode());
            parent = newRoot.guid;
            tree.rootGuid = newRoot.guid;
            newRoot.numChildren = 1;
            newRoot.children[0] = this.guid;
            newRoot.splitChild(0, this, tree);
        }

        // /**
        //  * removes the child at the given index
        //  * @param index
        //  */
        // public void removeChild(int index) {
        // 	children[index] = -1;
        // 	for(int i = index + 1; i < numChildren; i++) {
        // 		children[i - 1] = children[i];
        // 	}
        // 	numChildren--;
        // }
        
        // /**
        //  * remove the key at the given index
        //  * @param index
        //  */
        // public void removeKey(int index) {
        // 	keys[index] = null;
        // 	for(int i = index; i < numKeys - 1; i++) {
        // 		keys[i] = keys[i + 1];
        // 	}
        // 	numKeys--;
        // }
        
        // /**
        //  * Adds a child to the node.
        //  * @param nodeId node to add as a child
        //  * @return true if successful
        //  */
        // public boolean addChild(long nodeId) {
        // 	if (numChildren == children.length) {
        // 		return false;
        // 	}
        // 	int i;
        // 	BTreeNode node = readDisk(nodeId);
        // 	for (i = 0; i < getNumKeys(); i++) {
        // 		if(children[i] == -1) {
    	// 			break;
    	// 		}else if (node.getNumKeys() != 0) {
        // 			if (getKey(i).compareTo(node.getKey(0)) >= 0) {
	    // 				break;
	    // 			}
        // 		}
    	// 	}
        // 	for (int j = numChildren; j > i; j--) {
        //         children[j] = children[j - 1];
        //     }
        // 	children[i] = nodeId;
        // 	node.setParent(getGuid());
        // 	writeDisk(node);
        // 	numChildren++;
        // 	return true;
        // }
        
        // /**
        //  * sets the parent id
        //  * @param parentid id of the parent
        //  */
        // public void setParent(long parentId) {
        // 	parent = parentId;
        // }
        
        /**
         * checks if the node has a full list of keys
         * @return true if the key list is full
         */
        public boolean isFull() {
        	return numKeys >= degree - 1;
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

    @Override
    public Iterator<TreeObject<E>> iterator() {
        return new BTreeIterator();
    }

    private class BTreeIterator implements Iterator<TreeObject<E>> {

        public Stack<PathStep> currentPath;
        public BTreeNode currentNode;
        // public boolean traverseNext;
        public int currentNodeIndex;

        public BTreeIterator() {
            currentPath = new Stack<PathStep>();

            BTreeNode cursor = readDisk(rootGuid);
            while (!cursor.isLeaf()) {
                currentPath.add(new PathStep(cursor, 0));
                cursor = readDisk(cursor.children[0]);
            }

            currentNode = cursor;
            // traverseNext = false;
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
