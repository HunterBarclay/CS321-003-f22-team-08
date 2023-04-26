package cs321.search;

import cs321.btree.BTree;
import cs321.common.GeneBankParser;
import cs321.common.ParseArgumentException;
import cs321.create.GeneBankCreateBTree;
import cs321.create.SequenceUtils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.sql.Connection;

/**
 * Resources Used:
 *  - @Before annotation for jUnit https://www.baeldung.com/junit-before-beforeclass-beforeeach-beforeall
 */
public class GeneBankSearchDatabaseTest {

    // public String btreeTest1Location = System.getProperty("java.io.tmpdir") + "/btree-database-search-test";
    public String btreeTest1Location = "testTree";

    @Before
    public void initializeTestTrees() {

        System.out.println("Initializing Trees");

        {
            try {
                BTree<Long> test1 = new BTree<Long>(btreeTest1Location, 5);
                GeneBankParser gbParser = new GeneBankParser(20, "data/files_gbk/test2.gbk");
                for (String s : gbParser) {
                    test1.insert(SequenceUtils.dnaStringToLong(s));
                }
                GeneBankCreateBTree.writeToDatabase(test1, test1.makeDatabaseConnection(), 20);
            } catch (Exception e) {
                e.printStackTrace();
                fail("Failed Setup");
            }
        }

        System.out.println("Initialization Done");
    }

    @Test
    public void btreeDatabaseSearchForNonExistent() {
        try {
            BTree<Long> tree = BTree.<Long>loadBTree(btreeTest1Location);
            Connection connection = tree.makeDatabaseConnection();
            GeneBankSearchDatabase.useDatabase(connection);
            int instances = GeneBankSearchDatabase.queryDatabase(connection, "NONEXISTENT");
            assertEquals(0, instances);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Encountered exception: " + e.getMessage());
        }
    }

}
