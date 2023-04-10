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

    public BTree(String treeDirectory, int degree) {
        // Will always create a new BTree with constructor

        try {
            deleteDirectoryRecursive(Paths.get(treeDirectory));
            Files.createDirectory(Paths.get(treeDirectory));
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.treeDirectory = treeDirectory;
        this.degree = degree;

        rootGuid = allocateNode();
        if (rootGuid == -1) {
            throw new RuntimeException();
        }

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
     * Gets the degree of the tree
     * @return Degree of the tree
     */
    public int getDegree() { return degree; }
    
    /**
     * Node in the BTree that stores TreeObjects
     */
    private class BTreeNode implements Serializable {

        private int numKeys;
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
        public int getNumChildren() { return numKeys + 1; } // TODO: Needed?

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

        public void insert(TreeObject<E> object) {
            throw new RuntimeException();
        }

        public void split() {
            throw new RuntimeException();
        }
    }
}
