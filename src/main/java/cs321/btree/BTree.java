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
import java.util.Iterator;

/**
 * TODO
 */
public class BTree<E extends Comparable<E>> implements Serializable {

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
        	if(Paths.get(treeDirectory).toFile().exists()) {
            	deleteDirectoryRecursive(Paths.get(treeDirectory));
        	}
            Files.createDirectory(Paths.get(treeDirectory));
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.treeDirectory = treeDirectory;
        this.degree = degree;
        numKeys = 0;

        rootGuid = allocateNode();
        if (rootGuid == -1) {
            throw new RuntimeException();
        }
        

        File treeFile = new File(String.format("%s/%s", treeDirectory, META_FILE_NAME));
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
    	int i;
    	while(!node.isLeaf()) {
    		for(i = 0; i < node.getNumKeys(); i++) {
    			if(node.getKey(i).compareTo(obj) >= 0) {
    				break;
    			}
    		}
    		node = readDisk(node.getChild(i));
    	}
    	if(node.isFull()) {
    		splitNode(node);
    	}
    	node.insert(obj);
    	numKeys++;
    	writeDisk(node);
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

            System.out.println(String.format("[%s] -> %d bytes", f.getAbsolutePath(), f.length()));
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
    private BTreeNode splitNode(BTreeNode node) {
    	BTreeNode parentNode;
    	if(node.getParent() == -1) {
    		node.setParent(allocateNode());
    	}
    	parentNode = readDisk(node.getParent());
    	int index = node.getNumKeys() / 2 + 1;
    	if(parentNode.isFull()) {
    		splitNode(parentNode);
    	}
    	parentNode.insert(node.getKey(index));
    	BTreeNode newNode = readDisk(allocateNode());
    	for(int i = node.getNumKeys() / 2 + 2; i < node.getNumKeys(); i++) {
    		newNode.insert(node.getKey(i));
    		newNode.addChild(node.getChild(i));
    	}
    	if(node.getNumChildren() != 0) {
    		newNode.addChild(node.getChild(node.getNumKeys()));
    	}
    	newNode.setParent(parentNode.getGuid());
    	parentNode.addChild(newNode.getGuid());
    	writeDisk(newNode);
    	writeDisk(parentNode);
    	node.decreaseKeysAfterSplit();
        return parentNode;
    }

    /**
     * Gets the degree of the tree
     * @return Degree of the tree
     */
    public int getDegree() { return degree; }
    
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
            keys = (TreeObject<E>[])Array.newInstance(TreeObject.class, degree - 1);
            children = new long[degree];

            this.guid = guid;
            parent = -1;
            numKeys = 0;
            numChildren = 0;
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
            if (index < 0 || index > numKeys)
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
         */
        public void insert(TreeObject<E> object) {
        	if(!isFull()) {
        		int i;
        		for(i = 0; i < numKeys; i++) {
        			if(getKey(i).compareTo(object) >= 0) {
        				break;
        			}
        		}
        		for(int j = i + 1; j < numKeys; j++) {
        			keys[j] = keys[j - 1];
        		}
        		keys[i] = object;
        		numKeys++;
        	}
        }

        /**
         * Adds a child to the node.
         * @param nodeId node to add as a child
         * @return true if successful
         */
        public boolean addChild(long nodeId) {
        	if(isFull()) {
        		return false;
        	}
        	int i;
        	BTreeNode node = readDisk(nodeId);
        	for(i = 0; i < getNumKeys(); i++) {
        		if(node.getNumKeys() != 0) {
	    			if(getKey(i).compareTo(node.getKey(0)) >= 0) {
	    				break;
	    			}
        		}
    		}
        	children[i] = nodeId;
        	numChildren++;
        	return true;
        }
        
        /**
         * sets the parent id
         * @param parentid id of the parent
         */
        public void setParent(long parentId) {
        	parent = parentId;
        }
        
        /**
         * decreases the number of keys by half for after a split
         */
        public void decreaseKeysAfterSplit() {
        	numKeys = numKeys/2;
        }
        
        /**
         * checks if the node has a full list of keys
         * @return true if the key list is full
         */
        public boolean isFull() {
        	return numKeys == degree - 1;
        }
        
        /**
         * checks if the node is a leaf node
         * @return true if the node has no children
         */
        public boolean isLeaf() {
        	if(numChildren == 0) {
        		return true;
        	}
        	return false;
        }
        
    }
}
