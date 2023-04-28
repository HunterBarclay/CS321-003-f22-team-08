package cs321.search;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Iterator for iterating through a query results file. Lines will be parsed into QueryResult objects.
 */
public class QueryResultsParser implements Iterable<QueryResult>, Iterator<QueryResult> {

    private Scanner scanner;

    /*
     * Constructs a QueryResultsParser from a query results file
     */
    public QueryResultsParser(String path) {
        try {
            if (!Files.exists(Paths.get(path))) {
                throw new FileNotFoundException();
            }

            scanner = new Scanner(Paths.get(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasNext() {
        return scanner.hasNextLine();
    }

    @Override
    public QueryResult next() {
        return new QueryResult(scanner.nextLine());
    }

    @Override
    public Iterator<QueryResult> iterator() { return this; }

}
