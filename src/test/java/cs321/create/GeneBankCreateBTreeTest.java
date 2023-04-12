package cs321.create;

import cs321.btree.BTree;
import cs321.common.ParseArgumentException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.sql.Connection;

import javax.print.attribute.standard.PrinterInfo;

public class GeneBankCreateBTreeTest {

    private String[] args;
    private GeneBankCreateBTreeArguments expectedConfiguration;
    private GeneBankCreateBTreeArguments actualConfiguration;

    @Test
    public void parse4CorrectArgumentsTest() throws ParseArgumentException {
        args = new String[4];
        args[0] = "--cache=0";
        args[1] = "--degree=20";
        args[2] = "--gbkfile=./data/files_gbk/test1.gbk";
        args[3] = "--length=13";

        expectedConfiguration = new GeneBankCreateBTreeArguments(false, 20, "./data/files_gbk/test1.gbk", 13, -1, 0);
        actualConfiguration = GeneBankCreateBTree.parseArguments(args);
        assertEquals(expectedConfiguration, actualConfiguration);
    }

    @Test
    public void btreeDatabaseCreationTest() {
        
        BTree<Long> tree = new BTree<Long>(System.getProperty("java.io.tmpdir") + "/btree-database-test", 5);
        tree.insert(SequenceUtils.dnaStringToLong("ATG"));
        tree.insert(SequenceUtils.dnaStringToLong("TTG"));
        
        Connection connection = null;
        try {
            connection = GeneBankCreateBTree.makeDatabaseConnection();
            GeneBankCreateBTree.writeToDatabase(tree, connection, 3);
            GeneBankCreateBTree.printDatabase(connection);
        } catch (Exception e) {
            fail("Exception encountered: " + e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

}
