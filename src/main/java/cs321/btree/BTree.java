package cs321.btree;

import java.lang.reflect.Array;

public class BTree<E extends Comparable<E>> {
    
    private long nextGuid = 0; // Only use positives
    private long rootGuid;

    public BTree(String treeDirectory) {

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
        throw new RuntimeException();
    }

    /**
     * Fetches BTreeNode currently stored in disk memory
     * @param nodeGuid GUID for BTreeNode to access
     * @return BTreeNode read from disk
     */
    private BTreeNode<E> readDisk(long nodeGuid) {
        throw new RuntimeException();
    }

    /**
     * Writes BTreeNode to disk
     * @param nodeGuid GUID for location of BTreeNode in disk
     * @return Whether or not write was successful
     */
    private boolean writeDisk(long nodeGuid) {
        throw new RuntimeException();
    }
    
    /**
     * Node in the BTree that stores TreeObjects
     */
    private class BTreeNode<E extends Comparable<E>> {

        private int size;
        private long parent;
        private long[] children;
        private TreeObject<E>[] keys;

        @SuppressWarnings("unchecked")
        public BTreeNode(int degree) {
            keys = (TreeObject<E>[])Array.newInstance(TreeObject.class, degree - 1);
            children = new long[degree];
        }
    }
}
