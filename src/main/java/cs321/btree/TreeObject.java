package cs321.btree;

import java.io.Serializable;

/**
 * Stores a generic object and an instanes counter for a given object.
 * Intended for usage within a BTree
 */
public class TreeObject<E extends Comparable<E>> implements Comparable<TreeObject<E>>, Serializable {

    private int instances;
    private E key;

    /**
     * Constructs a new TreeObject with a given key value
     * @param key   Key value of TreeObject
     */
    public TreeObject(E key) {
        this.key = key;
        instances = 1;
    }

    /**
     * Copy Constructor for TreeObject
     * 
     * @param original  Original TreeObject
     */
    public TreeObject(TreeObject<E> original) {
        instances = original.instances;
        key = original.key;
    }

    /**
     * Get the number of instances of the particular key
     * 
     * @return  Number of instances/duplicates
     */
    public int getInstances() {
        return instances;
    }

    /**
     * Increments the number of instances
     */
    public void incrementInstances() {
        instances++;
    }

    /**
     * Gives Key
     * 
     * @return  Key
     */
    public E getKey() {
        return key;
    }

    @Override
    public int compareTo(TreeObject<E> o) {
        return key.compareTo(o.key);
    }

    @Override
    public String toString() {
        return String.format("%s %d", key.toString(), instances);
    }

}
