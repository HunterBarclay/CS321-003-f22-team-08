package cs321.btree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import org.junit.Test;

public class BTreeTest {

    private static final Long A =  0l;
    private static final Long B =  1l;
    private static final Long C =  2l;
    private static final Long D =  3l;
    private static final Long E =  4l;
    private static final Long F =  5l;
    private static final Long G =  6l;
    private static final Long H =  7l;
    private static final Long I =  8l;
    private static final Long J =  9l;
    private static final Long K = 10l;
    private static final Long L = 11l;
    private static final Long M = 12l;
    private static final Long N = 13l;
    private static final Long O = 14l;
    private static final Long P = 15l;

    @Test
    public void btreeDegree4Test()
    {
        // BTree tree = new BTree<Long>("TODO");

//        //TODO instantiate and populate a bTree object
//        int expectedNumberOfNodes = TBD;
//
//        // it is expected that these nodes values will appear in the tree when
//        // using a level traversal (i.e., root, then level 1 from left to right, then
//        // level 2 from left to right, etc.)
//        String[] expectedNodesContent = new String[]{
//                "TBD, TBD",      //root content
//                "TBD",           //first child of root content
//                "TBD, TBD, TBD", //second child of root content
//        };
//
//        assertEquals(expectedNumberOfNodes, bTree.getNumberOfNodes());
//        for (int indexNode = 0; indexNode < expectedNumberOfNodes; indexNode++)
//        {
//            // root has indexNode=0,
//            // first child of root has indexNode=1,
//            // second child of root has indexNode=2, and so on.
//            assertEquals(expectedNodesContent[indexNode], bTree.getArrayOfNodeContentsForNodeIndex(indexNode).toString());
//        }
    }

    @Test
    public void wtfTest() {
        System.out.println(((Long)5l).compareTo((Long)10l));
        System.out.println(((Integer)5).compareTo((Integer)10));
    }

    @Test
    public void btreeLoadTest() {

        String location = System.getProperty("java.io.tmpdir") + "/btree-load-test";

        {
            new BTree<Long>(location, 5);
        }

        try {
            BTree<Long> tree = BTree.loadBTree(location);
            assertEquals(5, tree.getDegree());
        } catch (Exception e) {
            fail("Encountered Exception: " + e.getMessage());
        }
    }

    @Test
    public void btreeLoadFileNotFoundTest() {
        try {
            BTree<Long> tree = BTree.loadBTree("doesnt-exist-tree");
            fail("No exception encountered");
        } catch (FileNotFoundException fnfe) {
            assertTrue(true);
        } catch (Exception e) {
            fail("Encounted unknown exception: " + e.getMessage());
        }
    }
    
    @Test
    public void btreeDegree4Empty_insertA() {

        String location = System.getProperty("java.io.tmpdir") + "/btree-empty4-insert1-test";

    	try {
            BTree<Long> tree = new BTree<Long>(location, 4);
            tree.insert(A);
            System.out.println(tree.toString());
            if(tree.getNumKeys() != 1) {
            	fail("Number of keys was not increased correctly");
            }
            if(tree.getNumNodes() != 1) {
            	fail("Number of nodes was not increased correctly");
            }
        } catch (Exception e) {
            fail("Encounted unknown exception: " + e.getMessage());
        }
    }
    @Test
    public void btreeDegree4A_load() {

        String location = System.getProperty("java.io.tmpdir") + "/btree-empty4-insert1-load-test";

        {
            BTree<Long> tree = new BTree<Long>(location, 4);
            tree.insert(A);
        }

    	try {
            BTree<Long> loadedTree = BTree.<Long>loadBTree(location);
            assertEquals(1, loadedTree.getNumKeys());
            assertEquals(1, loadedTree.getNumNodes());
        } catch (Exception e) {
            fail("Encounted unknown exception: " + e.getMessage());
        }
    }
    
    @Test
    public void btreeDegree4_insertABCDE() {

        String location = System.getProperty("java.io.tmpdir") + "/btree-empty4-insert4-test";

    	try {
            BTree<Long> tree = new BTree<Long>(location, 4);
            tree.insert(A);
            tree.insert(B);
            tree.insert(C);
            tree.insert(D);
            System.out.println(tree.toString());
            tree.insert(E);
            System.out.println(tree.toString());
            assertEquals(5, tree.getNumKeys());
            assertEquals(3, tree.getNumNodes());
        } catch (Exception e) {
        	e.printStackTrace();
            fail("Encounted unknown exception: " + e.getMessage());
        }
    }

    @Test
    public void btreeDegree4_insertABCDEFGHIJKLMNOP() {

        String location = System.getProperty("java.io.tmpdir") + "/btree-empty4-insertALot-test";

    	try {
            BTree<Long> tree = new BTree<Long>(location, 4);
            tree.insert(A);
            tree.insert(B);
            tree.insert(C);
            tree.insert(D);
            tree.insert(E);
            tree.insert(F);
            tree.insert(G);
            tree.insert(H);
            tree.insert(I);
            tree.insert(J);
            tree.insert(K);
            tree.insert(K);
            tree.insert(L);
            tree.insert(M);
            tree.insert(N);
            tree.insert(O);
            tree.insert(P);
            assertTrue(validateTree(tree));
        } catch (Exception e) {
            fail("Encounted unknown exception: " + e.getMessage());
        }
    }

    @Test
    public void btreeDegree4_insertPONMLKJIHGFEDCBA() {

        String location = System.getProperty("java.io.tmpdir") + "/btree-empty4-insertALot-test";

    	try {
            BTree<Long> tree = new BTree<Long>(location, 4);
            tree.insert(P);
            tree.insert(O);
            tree.insert(N);
            tree.insert(M);
            tree.insert(L);
            tree.insert(K);
            tree.insert(J);
            tree.insert(I);
            tree.insert(H);
            tree.insert(G);
            tree.insert(F);
            tree.insert(E);
            tree.insert(D);
            tree.insert(C);
            tree.insert(B);
            tree.insert(A);
            assertEquals(16, tree.getNumKeys());
            assertTrue(validateTree(tree));
            System.out.println(tree.toString());
        } catch (Exception e) {
            fail("Encounted unknown exception: " + e.getMessage());
        }
    }

    @Test
    public void btreeDegree3_insertABCDEFGHIJKLMNOP() {

        String location = System.getProperty("java.io.tmpdir") + "/btree-empty4-insertALot-test";

    	try {
            BTree<Long> tree = new BTree<Long>(location, 3);
            tree.insert(A);
            System.out.println(tree.toString());
            tree.insert(B);
            System.out.println(tree.toString());
            tree.insert(C);
            System.out.println(tree.toString());
            tree.insert(D);
            System.out.println(tree.toString());
            tree.insert(E);
            tree.insert(F);
            tree.insert(G);
            tree.insert(H);
            tree.insert(I);
            tree.insert(J);
            tree.insert(K);
            tree.insert(L);
            tree.insert(M);
            tree.insert(N);
            tree.insert(O);
            tree.insert(P);

            System.out.println(tree.toString());
            
            assertEquals(16, tree.getNumKeys());
            assertTrue(validateTree(tree));
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Encounted unknown exception: " + e.getMessage());
        }
    }

    @Test
    public void btreeDegree3_insertPONMLKJIHGFEDCBA() {

        String location = System.getProperty("java.io.tmpdir") + "/btree-empty4-insertALot-test";

    	try {
            BTree<Long> tree = new BTree<Long>(location, 3);
            tree.insert(P);
            tree.insert(O);
            tree.insert(N);
            tree.insert(M);
            tree.insert(L);
            tree.insert(K);
            tree.insert(J);
            tree.insert(I);
            tree.insert(H);
            tree.insert(G);
            tree.insert(F);
            tree.insert(E);
            tree.insert(D);
            tree.insert(C);
            tree.insert(B);
            tree.insert(A);
            assertEquals(16, tree.getNumKeys());
            assertTrue(validateTree(tree));
            System.out.println(tree.toString());
        } catch (Exception e) {
            fail("Encounted unknown exception: " + e.getMessage());
        }
    }

    @Test
    public void btreeDegree3_insertAAB() {
        String location = System.getProperty("java.io.tmpdir") + "/btree-empty4-insertAAB-test";

        try {
            BTree<Long> tree = new BTree<>(location, 3);
            tree.insert(A);
            tree.insert(A);
            tree.insert(B);

            assertEquals(2, tree.getNumKeys());
            assertTrue(validateTree(tree));
            System.out.println(tree.toString());
        } catch (Exception e) {
            fail("Encounted unknown exception: " + e.getMessage());
        }
    }

    @Test
    public void btreeDegree2_insertAx100() {
        String location = System.getProperty("java.io.tmpdir") + "/btree-empty4-insertAAB-test";

        try {
            BTree<Long> tree = new BTree<>(location, 3);
            for (int i = 0; i < 100; i++) {
                tree.insert(A);
            }

            assertEquals(1, tree.getNumKeys());
            assertTrue(validateTree(tree));
            System.out.println(tree.toString());

        } catch (Exception e) {
            fail("Encounted unknown exception: " + e.getMessage());
        }
    }
    
    @Test
    public void btreeDegree5_insertRandomSeed10x30() {

        String location = System.getProperty("java.io.tmpdir") + "/btree-empty4-insertALot-test";

    	try {
            BTree<Long> tree = new BTree<Long>(location, 5);
            Random rand = new Random(10);
            long l;
            for(int i = 0; i < 30; i++) {
            	l = Math.abs(rand.nextLong()) % 100;
            	tree.insert(l);
            }
            System.out.println(tree.toString());
            assertEquals(28, tree.getNumKeys());
            assertTrue(validateTree(tree));
        } catch (Exception e) {
            fail("Encounted unknown exception: " + e.getMessage());
        }
    }
    
    @Test
    public void btreeDegree10_insertRandomSeed10x30() {

        String location = System.getProperty("java.io.tmpdir") + "/btree-empty4-insertALot-test";

    	try {
            BTree<Long> tree = new BTree<Long>(location, 10);
            Random rand = new Random(10);
            long l;
            for(int i = 0; i < 30; i++) {
            	l = Math.abs(rand.nextLong()) % 100;
            	tree.insert(l);
            }
            System.out.println(tree.toString());
            assertEquals(28, tree.getNumKeys());
            assertTrue(validateTree(tree));
            
        } catch (Exception e) {
            fail("Encounted unknown exception: " + e.getMessage());
        }
    }

    @Test
    public void btreeDegree10_insertRandomSeed10x300() {

        String location = System.getProperty("java.io.tmpdir") + "/btree-empty4-insertALot-test";

    	try {
            BTree<Long> tree = new BTree<Long>(location, 10);
            Random rand = new Random(10);
            long l;
            for(int i = 0; i < 300; i++) {
            	l = Math.abs(rand.nextLong()) % 100;
            	tree.insert(l);
            }
            System.out.println(tree.toString());
            assertEquals(95, tree.getNumKeys());
            assertTrue(validateTree(tree));
        } catch (Exception e) {
            fail("Encounted unknown exception: " + e.getMessage());
        }
    }

    @Test
    public void btree_Insert1000_Load() {
        String location = System.getProperty("java.io.tmpdir") + "/btree-insert1000-load-test";

        String toStringContents;
        {
            BTree<Integer> btree = new BTree<Integer>(location, 5);

            Random rand = new Random(420);
            for (Integer i = 0; i < 1000; i++) {
                btree.insert(rand.nextInt(700));
            }

            toStringContents = btree.toString();
        }

        try {
            BTree<Integer> btree = BTree.<Integer>loadBTree(location);
            assertEquals(toStringContents, btree.toString());
        } catch (Exception e) {
            fail("Exception encountered: " + e.getMessage());
        }

    }

    public <E extends Comparable<E>> boolean validateTree(BTree<E> tree) {
        Iterator<TreeObject<E>> iter = tree.iterator();

        if (!iter.hasNext())
            return true;
        
        TreeObject<E> previous = iter.next();

        HashSet<E> dupeCheck = null;
        if (tree.getNumKeys() < 10000) {
            dupeCheck = new HashSet<E>((int)(tree.getNumKeys() * (1.0f / 0.8f)), 0.8f);
            dupeCheck.add(previous.getKey());
        }

        while (iter.hasNext()) {
            TreeObject<E> next = iter.next();
            if (previous.compareTo(next) >= 0) {
                System.out.println(String.format("Tree isn't in order: %s !> %s", next.getKey().toString(), previous.getKey().toString()));
                return false;
            }
            if (dupeCheck != null && !dupeCheck.add(next.getKey())) {
                System.out.println(String.format("Key '%s' detected more than once", next.getKey().toString()));
            }
            previous = next;
        }

        return true;
    }
}
