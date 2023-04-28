package cs321.search;

import cs321.btree.BTree;
import cs321.common.GeneBankParser;
import cs321.create.GeneBankCreateBTree;
import cs321.create.SequenceUtils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.sql.Connection;

/**
 * Resources Used:
 *  - @Before annotation for jUnit https://www.baeldung.com/junit-before-beforeclass-beforeeach-beforeall
 *  - Got the boolean switch from StackOverflow: https://stackoverflow.com/questions/12087959/junit-run-set-up-method-once
 */
public class GeneBankSearchDatabaseTest {

    private static boolean setupComplete = false;
    public String btreeTest1Location = System.getProperty("java.io.tmpdir") + "/btree-database-search-test-1";
    public String btreeTest1DBLocation = System.getProperty("java.io.tmpdir") + "/btree-database-search-test-1.db";
    public String btreeTest2Location = System.getProperty("java.io.tmpdir") + "/btree-database-search-test-2";
    public String btreeTest2DBLocation = System.getProperty("java.io.tmpdir") + "/btree-database-search-test-2.db";

    /**
     * Initialize some trees to use for testing in this test file
     */
    @Before
    public void initializeTestTrees() {

        if (!setupComplete) {

            System.out.println("Initializing Trees");

            {
                System.out.println("Generating Test BTree 1...");

                try {
                    BTree<Long> test1 = new BTree<Long>(btreeTest1Location, 3);
                    GeneBankParser gbParser = new GeneBankParser(4, "data/files_gbk/test0.gbk");
                    System.out.println("Inserting subsequences...");
                    long updateWait = 2000;
                    long counter = 0;
                    long lastCounter = 0;
                    long lastUpdate = System.currentTimeMillis();
                    for (String s : gbParser) {
                        test1.insert(SequenceUtils.dnaStringToLong(s));
                        counter++;
                        if (System.currentTimeMillis() > lastUpdate + updateWait) {
                            System.out.println(String.format("[%d] %s @ %d insertions per second", counter, s, (counter - lastCounter) / 2));
                            lastUpdate = System.currentTimeMillis();
                            lastCounter = counter;
                        }
                    }
                    System.out.println("Writing to database...");
                    GeneBankCreateBTree.writeToDatabase(test1, BTree.makeDatabaseConnection(btreeTest1DBLocation), 4);
                    // System.out.println(test1.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    fail("Failed Setup");
                }

                System.out.println("Test BTree 1 Generated");
            }

            {
                System.out.println("Generating Test BTree 2...");

                try {
                    BTree<Long> test2 = new BTree<Long>(btreeTest2Location, 5);
                    GeneBankParser gbParser = new GeneBankParser(1, "data/files_gbk/test0.gbk");
                    for (String s : gbParser) {
                        test2.insert(SequenceUtils.dnaStringToLong(s));
                    }
                    GeneBankCreateBTree.writeToDatabase(test2, BTree.makeDatabaseConnection(btreeTest2DBLocation), 1);
                    // System.out.println(test2.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    fail("Failed Setup");
                }

                System.out.println("Test BTree 2 Generated");
            }

            setupComplete = true;

            System.out.println("Initialization Done");
        }
    }

    @Test
    public void btreeSearchDatabaseParseArgumentsTest() {
        try {
            String[] args = new String[] { "--queryfile=results/query-results/query1", String.format("--database=%s", btreeTest1DBLocation) };
            GeneBankSearchDatabaseArguments arguments = new GeneBankSearchDatabaseArguments(args);
            GeneBankSearchDatabaseArguments expectedArguments = new GeneBankSearchDatabaseArguments("results/query-results/query1", btreeTest1DBLocation);
            assertEquals(expectedArguments, arguments);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Encountered Exception");
        }
    }

    @Test
    public void btreeDatabaseSearchForNonExistent() {
        try {
            Connection connection = BTree.makeDatabaseConnection(btreeTest2DBLocation);
            int instances = GeneBankSearchDatabase.queryDatabase(connection, "N");
            assertEquals(0, instances);
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Encountered exception: " + e.getMessage());
        }
    }

    /**
     * Expecting the following results for the single character subsequences:
     * a -> 1543
     * c -> 1115
     * g -> 866
     * t -> 1638
     */
    @Test
    public void btreeDatabaseSearchForDuplicates() {
        try {
            Connection connection = BTree.makeDatabaseConnection(btreeTest2DBLocation);
            assertEquals(1543, GeneBankSearchDatabase.queryDatabase(connection, "a"));
            assertEquals(1115, GeneBankSearchDatabase.queryDatabase(connection, "c"));
            assertEquals(866, GeneBankSearchDatabase.queryDatabase(connection, "g"));
            assertEquals(1638, GeneBankSearchDatabase.queryDatabase(connection, "t"));
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Encountered exception: " + e.getMessage());
        }
    }

    @Test
    public void btreeDatabaseSearch_Test0_Degree35_Length4_FullScaleTest() {
        try {
            Connection connection = BTree.makeDatabaseConnection(btreeTest1DBLocation);
            QueryResultsParser queries = new QueryResultsParser("results/query-results/query4-test0.gbk.out");
            for (QueryResult result : queries) {
                assertEquals(
                    result.getResult(), 
                    GeneBankSearchDatabase.queryDatabase(connection, SequenceUtils.getComplement(result.getQuery())) 
                        + GeneBankSearchDatabase.queryDatabase(connection, result.getQuery())
                );
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Encountered exception");
        }
    }

}
