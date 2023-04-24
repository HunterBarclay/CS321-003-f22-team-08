package cs321.btree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;

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

    @SuppressWarnings("unused")
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
    public void btreeDegree4_insertABCD() {

        String location = System.getProperty("java.io.tmpdir") + "/btree-empty4-insert4-test";

    	try {
            BTree<Long> tree = new BTree<Long>(location, 4);
            tree.insert(A);
            tree.insert(B);
            tree.insert(C);
            tree.insert(D);
            assertEquals(4, tree.getNumKeys());
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
            tree.insert(L);
            tree.insert(M);
            tree.insert(N);
            tree.insert(O);
            tree.insert(P);
            assertEquals(16, tree.getNumKeys());
            assertEquals(12, tree.getNumNodes());
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
            assertEquals(1, tree.getNumNodes());

        } catch (Exception e) {
            fail("Encounted unknown exception: " + e.getMessage());
        }
    }

    @Test
    public void btreeDegree2_insertAx100() {
        String location = System.getProperty("java.io.tmpdir") + "/btree-empty4-insertAx100-test";

        try {
            BTree<Long> tree = new BTree<>(location, 3);
            for (int i = 0; i < 100; i++) {
                tree.insert(A);
            }

            assertEquals(1, tree.getNumKeys());
            assertEquals(1, tree.getNumNodes());

        } catch (Exception e) {
            fail("Encounted unknown exception: " + e.getMessage());
        }
    }

    @Test
    public void btreeDegree4_insertABCAAD_SearchNumInstancesA() {
        String location = System.getProperty("java.io.tmpdir") + "/btree-empty4-insertABCAAD-search-test";

    	try {
            BTree<Long> tree = new BTree<>(location, 4);
            tree.insert(A);
            tree.insert(B);
            tree.insert(C);
            tree.insert(A);
            tree.insert(A);
            tree.insert(D);
        
            assertEquals(3, tree.search(A).getInstances());

        } catch (Exception e) {
            fail("Encounted unknown exception: " + e.getMessage());
        }
    }

    @Test
    public void btreeDegree4_insertABCDCBDEC_SearchNumInstancesABCDE() {
        String location = System.getProperty("java.io.tmpdir") + "/btree-empty4-insertABCDCBDEC-search-test";

    	try {
            BTree<Long> tree = new BTree<>(location, 4);
            tree.insert(A);
            tree.insert(B);
            tree.insert(C);
            tree.insert(D);
            tree.insert(C);
            tree.insert(B);
            tree.insert(D);
            tree.insert(E);
            tree.insert(C);
        
            assertEquals(1, tree.search(A).getInstances());
            assertEquals(2, tree.search(B).getInstances());
            assertEquals(3, tree.search(C).getInstances());
            assertEquals(2, tree.search(D).getInstances());
            assertEquals(1, tree.search(E).getInstances());

        } catch (Exception e) {
            e.printStackTrace();
            fail("Encounted unknown exception: " + e.getMessage());
        }
    }

    @Test
    public void btreeDegree3_insertAx50_SearchNumInstancesA() {
        String location = System.getProperty("java.io.tmpdir") + "/btree-empty4-insertAx50-search-test";

    	try {
            BTree<Long> tree = new BTree<>(location, 3);
            for (int i = 0; i < 50; i++) {
                tree.insert(A);
            }

            assertEquals(1, tree.getNumKeys());
            assertEquals(1, tree.getNumNodes());
            assertEquals(50, tree.search(A).getInstances());

        } catch (Exception e) {
            fail("Encounted unknown exception: " + e.getMessage());
        }
    }

}
