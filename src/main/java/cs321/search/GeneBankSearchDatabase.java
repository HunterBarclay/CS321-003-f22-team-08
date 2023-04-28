package cs321.search;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cs321.btree.BTree;
import cs321.common.ParseArgumentException;
import cs321.common.ParseArgumentUtils;
import cs321.create.GeneBankCreateBTree;
import cs321.create.SequenceUtils;

public class GeneBankSearchDatabase {

    public static void main(String[] args) throws Exception {
        
        GeneBankSearchDatabaseArguments arguments = null;
        
        try {
            arguments = new GeneBankSearchDatabaseArguments(args);
        } catch (ParseArgumentException e) {
            System.out.println("FAILED: " + e.getMessage());
            printUsage();
        }

        if (arguments == null)
            return;

        try {
            Connection connection = BTree.makeDatabaseConnection(arguments.getDatabaseFile());
            QueryParser queries = new QueryParser(arguments.getQueryFile());
            for (String query : queries) {
                System.out.println(String.format("%s %d",query,
                    queryDatabase(connection, query) + queryDatabase(connection, SequenceUtils.getComplement(query))
                ));
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("FAILED: Exception was thrown. See stack trace above...");
        }
    }

    public static void printUsage() {
        System.out.println("java -jar build/libs/GeneBankSearchDatabase.jar --database=<SQLite-database-path> --queryfile=<query-file>");
    }

    public static int queryDatabase(Connection connection, String subsequence) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet sqlResultSet = statement.executeQuery(String.format("SELECT instances FROM subsequences WHERE subsequences.key = '%s';", subsequence));
        int result = 0;
        if (sqlResultSet.next()) {
            result = sqlResultSet.getInt(1);
        }
        sqlResultSet.close();
        statement.close();
        return result;
    }

}
