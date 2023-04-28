package cs321.create;

import cs321.common.GeneBankParser;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;

public class SequenceUtilsTest {

    @Test
    public void longToDNAStringTest() throws Exception {
	}

    @Test
    public void DNAStringToLongTest() throws Exception {
	}

	@Test
	public void getComplementTest() throws Exception {
	}

    @Test
    public void onlyValidSubsequencesTest() {
        GeneBankParser parser = null;
        try {
            parser = new GeneBankParser(31, "data/files_gbk/test5.gbk");
        } catch (Exception e) { fail(e.getMessage()); }

        try {
            long counter = 0;
            while (parser.hasNext()) {
                String sub = parser.next();
                if (sub.replace("a", "").replace("t", "").replace("g", "").replace("c", "").trim().length() > 0) {
                    fail(String.format("Invalid Subsequence -> %s", sub));
                }
                counter++;
            }

            System.out.println(String.format("\t===== Found %d Subsequences in \"test5.gbk\" =====", counter));
            assertTrue(true);
        } catch (Exception e) {
            fail("Unexpected Exception: " + e.getMessage());
        }
    }

    @Test
    public void currentNumberOfSubsequencesTest() {
        GeneBankParser parser = null;
        try {
            parser = new GeneBankParser(31, "data/files_gbk/test0.gbk");
        } catch (Exception e) { fail(e.getMessage()); }

        try {
            int counter = 0;
            while (parser.hasNext()) {
                parser.next();
                counter++;
            }

            assertEquals("Incorrect number of subsequences found", 4703, counter);
        } catch (Exception e) {
            fail("Unexpected Exception: " + e.getMessage());
        }
    }

    @Test
    public void fileNotFoundGeneBankParserTest() {
        try {
            GeneBankParser parser = new GeneBankParser(1, "some/file/that/dont/exists.lmfao");
            fail("No exception was encountered");
            parser.finalize(); // Removes warning
        } catch (FileNotFoundException fnfe) {
            assert(true);
        } catch (Exception e) {
            fail(String.format("Unknown exception encountered: %s", e.getMessage()));
        }
    }

    @Test
    public void invalidParameterGeneBankParserTest() {
        try {
            GeneBankParser parser = new GeneBankParser(40, "data/files_gbk/test0.gbk");
            fail("No exception was encountered");
            parser.finalize(); // Removes warning
        } catch (InvalidParameterException ipe) {
            assert(true);
        } catch (Exception e) {
            fail(String.format("Unknown exception encountered: %s", e.getMessage()));
        }
    }

    @Ignore("Comment this line out if you have the file downloaded")
    @Test
    public void parseYChromosomeTest() {
        Path yChromosome = Paths.get("data/files_gbk/hs_ref_chrY.gbk");
        if (!Files.exists(yChromosome)) {
            fail("GBK file doesn't exist");
        }

        try {
            GeneBankParser parser = new GeneBankParser(31, yChromosome.toString());
            long count = 0;
            while (parser.hasNext()) {
                parser.next();
                count++;
            }

            System.out.println(String.format("\t===== Found %d Subsequences in \"hs_ref_chrY.gbk\" =====", count));
            assertTrue(true);
        } catch (Exception e) {
            fail("Encountered Exception: " + e.getMessage());
        }
    }

    @Test
    public void geneBankParserIteratorHasNextTest() {
        GeneBankParser parser = null;
        try {
            parser = new GeneBankParser(31, "data/files_gbk/test0.gbk");
        } catch (Exception e) { fail(e.getMessage()); }

        while (parser.hasNext()) { parser.next(); }

        try {
            parser.next();
            fail("No exception encountered");
        } catch (RuntimeException re) {
            assertTrue(true);
        } catch (Exception e) {
            fail("Unknown exception encountered: " + e.getMessage());
        }
    }

    @Test
    public void geneBankParserSmallTest() {
        try {
            GeneBankParser parser = new GeneBankParser(1, "data/files_gbk/test0.gbk");
            for (String s : parser) {
                System.out.println(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Encountered exception");
        }
    }
}
