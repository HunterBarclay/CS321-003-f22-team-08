package cs321.search;

/**
 * Class to store a search query and the expected result
 */
public class QueryResult {
    private String query;
    private int result;

    /**
     * Construct QueryResult from the line in a query results file
     * 
     * @param s Line in the results file
     */
    public QueryResult(String s) {
        String[] arr = s.split(" ");
        query = arr[0];
        result = Integer.parseInt(arr[1]);
    }

    /**
     * Get the query for this QueryResult
     * 
     * @return  The subsequence
     */
    public String getQuery() { return query; }

    /**
     * Get the expected result for this query
     * 
     * @return  The expected number of instances of the subsequence added to its complements instances
     */
    public int getResult() { return result; }
}