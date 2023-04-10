package cs321.btree;

import java.io.Serializable;

public class TreeObject<E extends Comparable<E>> implements Comparable<TreeObject<E>>, Serializable {

    private int instances;
    private E key;

    public TreeObject(E key) {
        this.key = key;
        instances = 1;
    }

    public int getInstances() {
        return instances;
    }

    public E getKey() {
        return key;
    }

    @Override
    public int compareTo(TreeObject<E> o) {
        return key.compareTo(o.key);
    }

}
