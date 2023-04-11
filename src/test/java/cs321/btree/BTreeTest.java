package cs321.btree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;

import org.junit.Test;

/**
 * For getting temp folder: https://mkyong.com/java/how-to-get-the-temporary-file-path-in-java/
 */
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
            BTree<Long> tmp = BTree.loadBTree("doesnt-exist-tree");
            fail("No exception encountered");
        } catch (FileNotFoundException fnfe) {
            assertTrue(true);
        } catch (Exception e) {
            fail("Encounted unknown exception: " + e.getMessage());
        }
    }

}
