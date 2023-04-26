package cs321.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.Scanner;

/**
 * GeneBankParser is an iterator that iterates over a provided genebank file (.gbk)
 * and provides the subsequences of dna of a given length.
 * 
 * This class uses a Scanner and InputStream to slowly read parts of the file instead
 * of dumping the entire file into memory. That, along with the iterator nature, we actively
 * find each subsequence to remove the need to store many subsequence in memory at once.
 * 
 * Sources:
 * - Used for streaming the file in one line at a time: https://www.amitph.com/java-read-write-large-files-efficiently/
 * - Used for understanding iterable versus iterator: https://www.baeldung.com/java-iterator-vs-iterable
 */
public class GeneBankParser implements Iterator<String>, Iterable<String> {

    private Scanner fileStream;
    private StringBuilder subsequence;
    private int size;

    private String currentBank;
    private int bankIndex;
    private boolean inSequence;

    // This is an indicator for how many characters need to be added before we have valid data in the subsequence
    private int poison;
    
    /** 
     * Constructs a new GeneBank Parser
     * @param size Size of the subsequences you want to find
     * @param path Path to the Genebank (.gbk) file
     */
    public GeneBankParser(int size, String path) throws FileNotFoundException {

        if (size < 1 || size > 31) {
            throw new InvalidParameterException(String.format("%d is out of range of [1:31]", size));
        }

        // Get file and perform basic checks
        File geneBankFile = new File(path);

        if (!geneBankFile.exists())
            throw new FileNotFoundException();

        try {
            FileInputStream fis = new FileInputStream(geneBankFile);
            fileStream = new Scanner(fis); // Unsure if using the file directly will result in same memory effect
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }

        // Setup data and do a quick verification that we can locate some sequence data
        inSequence = false;
        if (seekNextValidLine() < 0)
            throw new InvalidGeneBankFileException();

        bankIndex = 0;
        this.size = size;
        subsequence = new StringBuilder(size);
        subsequence.append(new char[size]);
        poison = size;

        next(); // Get the ball rolling
    }

    /**
     * Seeks the next valid line containing dna data in the file.
     * 
     * @return Results a number correlating to the current status of where you're at in the data:
     *      -1 => No new sequence data
     *       0 => Just the next part of the sequence
     *       1 => New sequence
     */
    private int seekNextValidLine() {
        boolean found = false;
        boolean newSequence = false;
        bankIndex = 0;
        while (fileStream.hasNextLine() && !found) {
            currentBank = fileStream.nextLine().trim();

            // If we are in sequence, look for the end, if not look for the beginning of a new one
            if (inSequence) {
                if (currentBank.equalsIgnoreCase("//")) {
                    // Start looking for a new sequence
                    inSequence = false;
                    newSequence = true; 
                } else {
                    // Just another line with sequence data
                    found = true;
                }
            } else {
                // We only care if we've found a new sequence
                if (currentBank.equalsIgnoreCase("origin")) {
                    inSequence = true;
                    currentBank = fileStream.nextLine();
                    found = true;
                }
            }
        }

        // If nothing was found, that means we reached the end. Place data in a position where it will stop the iterator
        if (!found) {
            currentBank = "";
            return -1;
        }

        // We are gonna get rid of all the useless characters in the line
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < currentBank.length(); i++) {
            if (currentBank.charAt(i) > '9')
                builder.append(currentBank.charAt(i));
        }
        currentBank = builder.toString().toLowerCase();

        if (newSequence)
            return 1;
        else
            return 0;
    }

    /**
     * Deconstructor
     */
    public void finalize() {
        try {
            // Close the fileStream. I'd imagine this closes the original InputStream but I'm not sure
            fileStream.close();
        } catch (Exception e) { }
    }

    @Override
    public boolean hasNext() {
        return currentBank.length() > 0;
    }

    @Override
    public String next() {

        if (!hasNext())
            throw new RuntimeException("Nothing new");

        // We are going to have the next sequence stored preemptively to make it so hasNext works
        String result = subsequence.toString();

        while (poison > 0) {
            if (bankIndex >= currentBank.length()) {
                int seekRes = seekNextValidLine();

                if (seekRes == -1) {
                    poison = 0;
                    continue;
                } else if (seekRes == 1) {
                    poison = size;
                }
            }

            char nextChar = currentBank.charAt(bankIndex);
            bankIndex++;
            subsequence.delete(0, 1);
            subsequence.append(nextChar);

            if (nextChar == 'n') {
                poison = size;
            } else {
                poison--;
            }
        }
        poison++;
        

        return result;
    }

    @Override
    public Iterator<String> iterator() {
        return this;
    }

}
