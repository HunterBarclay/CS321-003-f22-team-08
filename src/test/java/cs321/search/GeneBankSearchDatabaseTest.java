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

    public String btreeTest1Location = System.getProperty("java.io.tmpdir") + "/btree-database-search-test";
    public String btreeTest2Location = System.getProperty("java.io.tmpdir") + "/btree-database-search-test-duplicates";

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
                System.out.println(test1.toString());
            } catch (Exception e) {
                e.printStackTrace();
                fail("Failed Setup");
            }
        }

        {
            try {
                BTree<Long> test2 = new BTree<Long>(btreeTest2Location, 5);
                GeneBankParser gbParser = new GeneBankParser(1, "data/files_gbk/test0.gbk");
                int counter = 0;
                for (String s : gbParser) {
                    if (s.equalsIgnoreCase("a"))
                        counter++;
                    test2.insert(SequenceUtils.dnaStringToLong(s));
                }
                System.out.println("'a' -> " + counter);
                GeneBankCreateBTree.writeToDatabase(test2, test2.makeDatabaseConnection(), 1);
                System.out.println(test2.toString());
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
            int instances = GeneBankSearchDatabase.queryDatabase(connection, "NONEXISTENT");
            assertEquals(0, instances);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Encountered exception: " + e.getMessage());
        }
    }

    @Test
    public void btreeDatabaseSearchForDuplicates() {
        try {
            BTree<Long> tree = BTree.<Long>loadBTree(btreeTest2Location);
            Connection connection = tree.makeDatabaseConnection();
            int instances = GeneBankSearchDatabase.queryDatabase(connection, "a");
            System.out.println(tree.toString());
            System.out.println(instances);
            assertEquals(1543, instances);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Encountered exception: " + e.getMessage());
        }
    }

}
