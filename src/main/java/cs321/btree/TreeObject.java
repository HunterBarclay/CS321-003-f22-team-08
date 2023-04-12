package cs321.btree;

import java.io.Serializable;

public class TreeObject<E extends Comparable<E>> implements Comparable<TreeObject<E>>, Serializable {

    private int instances;
    private E key;

    public TreeObject(E key) {
        this.key = key;
        instances = 1;
    }

    public TreeObject(TreeObject<E> original) {
        instances = original.instances;
        key = original.key;
    }

    public int getInstances() {
        return instances;
    }

    public void incrementInstances() {
        instances++;
    }

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
