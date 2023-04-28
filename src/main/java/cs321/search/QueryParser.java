package cs321.search;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Scanner;

public class QueryParser implements Iterable<String>, Iterator<String> {

    private Scanner scanner;

    public QueryParser(String path) {
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
    public String next() {
        return scanner.nextLine().toLowerCase();
    }

    @Override
    public Iterator<String> iterator() { return this; }

}
