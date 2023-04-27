package cs321.search;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cs321.btree.BTree;
import cs321.common.ParseArgumentException;
import cs321.common.ParseArgumentUtils;

public class GeneBankSearchDatabase
{

    public static void main(String[] args) throws Exception {
        System.out.println("Hello world from cs321.search.GeneBankSearchDatabase.main");
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
