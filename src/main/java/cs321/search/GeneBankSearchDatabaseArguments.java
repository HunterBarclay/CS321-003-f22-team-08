package cs321.search;

import java.nio.file.Files;
import java.nio.file.Paths;

import cs321.common.ParseArgumentException;

public class GeneBankSearchDatabaseArguments {

    private String queryFile;
    private String databaseFile;

    public GeneBankSearchDatabaseArguments(String[] args) throws ParseArgumentException {

        if (args.length != 2) {
            throw new ParseArgumentException("Incorrect number of arguments");
        }

        for (String arg : args) {
            if (arg.startsWith("--database=")) {

                if (databaseFile != null)
                    throw new ParseArgumentException("Database already specified");

                databaseFile = arg.substring(11);

                if (!Files.exists(Paths.get(databaseFile)))
                    throw new ParseArgumentException("Database file doesn't exist");
            } else if (arg.startsWith("--queryfile=")) {

                if (queryFile != null)
                throw new ParseArgumentException("Query file already specified");

                queryFile = arg.substring(12);

                if (!Files.exists(Paths.get(queryFile)))
                    throw new ParseArgumentException("Query file doesn't exist");
            } else {
                throw new ParseArgumentException("Failed to read argument: " + arg);
            }
        }

        if (queryFile == null || databaseFile == null)
            throw new ParseArgumentException("One or more arguments were not specified");
    }
    
    public GeneBankSearchDatabaseArguments(String queryFile, String databaseFile) {
        this.queryFile = queryFile;
        this.databaseFile = databaseFile;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!obj.getClass().equals(this.getClass()))
            return false;

        GeneBankSearchDatabaseArguments args = (GeneBankSearchDatabaseArguments)obj;

        return
            args.databaseFile != null && args.queryFile != null
            && args.databaseFile.equals(databaseFile)
            && args.queryFile.equals(queryFile);
    }

    @Override
    public String toString() {
        return String.format("GeneBank Search Database Arguments:\n\tDatabase File: '%s'\n\tQuery File: '%s'\n", databaseFile, queryFile);
    }

    /**
     * Gets the query file path
     * 
     * @return  Path
     */
    public String getQueryFile() { return queryFile; }
    
    /**
     * Gets the database file path
     * 
     * @return  Path
     */
    public String getDatabaseFile() { return databaseFile; }

}
