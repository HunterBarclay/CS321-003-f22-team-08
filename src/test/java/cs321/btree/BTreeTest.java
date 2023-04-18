package cs321.btree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

import org.junit.Test;

public class BTreeTest {

    // HINT:
    //  instead of checking all intermediate states of constructing a tree
    //  you can check the final state of the tree and
    //  assert that the constructed tree has the expected number of nodes and
    //  assert that some (or all) of the nodes have the expected values
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
        {
            new BTree<Long>("/tmp/btree-load-test", 5);
        }

        try {
            BTree<Long> tree = BTree.loadBTree("/tmp/btree-load-test");
            assertEquals(5, tree.getDegree());
        } catch (Exception e) {
            fail("Encountered Exception: " + e.getMessage());
        }
    }

    @Test
    public void btreeLoadFileNotFoundTest() {
        try {
            BTree<Long> tree = BTree.loadBTree("/tmp/dont-exist-tree");
            fail("No exception encountered");
        } catch (FileNotFoundException fnfe) {
            assertTrue(true);
        } catch (Exception e) {
            fail("Encounted unknown exception: " + e.getMessage());
        }
    }
    
    @Test
    public void btreeEmptyDeg4TreeInsertAgetNumKeys1andgetNumNodes1() {
    	try {
            BTree<Long> tree = new BTree<Long>("temp-BTree", 4);
            tree.insert((long)'A');
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
    public void btreeEmptyDeg4TreeInsertALoadTreeNoException() {
    	try {
            BTree<Long> tree = new BTree<Long>("temp-BTree", 4);
            tree.insert((long)'A');
            tree = BTree.<Long>loadBTree("temp-BTree");
        } catch (Exception e) {
            fail("Encounted unknown exception: " + e.getMessage());
        }
    }
    
    @Test
    public void btreeEmptyDeg4TreeInsertABCDgetNumKeys4andgetNumNodes3() {
    	try {
            BTree<Long> tree = new BTree<Long>("temp-BTree", 4);
            tree.insert((long)'A');
            tree.insert((long)'B');
            tree.insert((long)'C');
            tree.insert((long)'D');
            if(tree.getNumKeys() != 4) {
            	fail("Number of keys was not increased correctly");
            }
            if(tree.getNumNodes() != 3) {
            	fail("Number of nodes was not increased correctly");
            }
        } catch (Exception e) {
        	e.printStackTrace();
            fail("Encounted unknown exception: " + e.getMessage());
        }
    }
    @Test
    public void btreeEmptyDeg4TreeInsertABCDEFGHIJKLMNOPgetNumKeys16andgetNumNodes12() {
    	try {
            BTree<Long> tree = new BTree<Long>("temp-BTree", 4);
            tree.insert((long)'A');
            tree.insert((long)'B');
            tree.insert((long)'C');
            tree.insert((long)'D');
            tree.insert((long)'E');
            tree.insert((long)'F');
            tree.insert((long)'G');
            tree.insert((long)'H');
            tree.insert((long)'I');
            tree.insert((long)'J');
            tree.insert((long)'K');
            tree.insert((long)'L');
            tree.insert((long)'M');
            tree.insert((long)'N');
            tree.insert((long)'O');
            tree.insert((long)'P');
            if(tree.getNumKeys() != 16) {
            	fail("Number of keys was not increased correctly");
            }
            if(tree.getNumNodes() != 12) {
            	fail("Number of nodes was not increased correctly");
            }
        } catch (Exception e) {
            fail("Encounted unknown exception: " + e.getMessage());
        }
    }
   

}
