package cs321.search;

import cs321.common.ParseArgumentException;
import org.junit.Test;

import static org.junit.Assert.*;

public class GeneBankSearchBTreeTest {

    @Test
    public void queryParserTest() {
        try {
            QueryParser parser = new QueryParser("results/query-results/query2");
            int counter = 0;
            for (String query : parser) {
                counter++;
                System.out.println(query);
            }
            assertEquals(16, counter);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception was thrown");
        }
    }

    @Test
    public void queryResultParserTest() {
        try {
            QueryResult res = new QueryResult("aa 543");
            assertEquals("aa", res.getQuery());
            assertEquals(543, res.getResult());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception was thrown");
        }
    }

}
