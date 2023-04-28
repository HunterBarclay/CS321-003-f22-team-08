package cs321.search;

import java.io.File;
import java.util.Scanner;

import cs321.btree.BTree;
import cs321.btree.BTree.Cache;
import cs321.btree.TreeObject;
import cs321.common.ParseArgumentException;
import cs321.create.SequenceUtils;;

public class GeneBankSearchBTree
{

    public static void main(String[] args) throws Exception
    {
        GeneBankSearchBTreeArguments geneBankSearchBTreeArguments = parseArgumentsAndHandleExceptions(args);
        
        BTree<Long> loadedTree = BTree.<Long>loadBTree(geneBankSearchBTreeArguments.getBTreeFileName());

        File queryFile = new File(geneBankSearchBTreeArguments.getQueryFileName());
        Scanner fileScan = new Scanner(queryFile);

        //Getting gbk file name
        String gbankString = geneBankSearchBTreeArguments.getBTreeFileName();
        for (int i = 0; i < 4; i++) {
            int index = gbankString.lastIndexOf('.');
            gbankString = gbankString.substring(0, index);
        }

        Cache cache = loadedTree.new Cache(geneBankSearchBTreeArguments.getCacheSize());
            
        while (fileScan.hasNextLine()) {
            String line = fileScan.nextLine();
            Scanner lineScan = new Scanner(line);
            String subsequenceQueryString = lineScan.next();
            long subsequenceQueryLong = SequenceUtils.dnaStringToLong(subsequenceQueryString);
            subsequenceQueryString = SequenceUtils.longToDnaString(subsequenceQueryLong, geneBankSearchBTreeArguments.getSequenceLength());

            int numInstances = 0;

            TreeObject<Long> treeObj;
            if (geneBankSearchBTreeArguments.getUseCache()) {
                treeObj = loadedTree.search(subsequenceQueryLong, cache);
            } else {
                treeObj = loadedTree.search(subsequenceQueryLong);
            }

            if (treeObj != null) {

                String subsequenceComplimentString = SequenceUtils.getComplement(subsequenceQueryString);
                long subsequenceComplimentLong = SequenceUtils.dnaStringToLong(subsequenceComplimentString);

                TreeObject<Long> treeObj2;
                if (geneBankSearchBTreeArguments.getUseCache()) {
                    treeObj2 = loadedTree.search(subsequenceComplimentLong, cache);
                } else {
                    treeObj2 = loadedTree.search(subsequenceComplimentLong);
                }

                numInstances = treeObj.getInstances();
                if (treeObj2 != null) {
                    numInstances += treeObj2.getInstances();
                }
            }
                
            System.out.println(subsequenceQueryString + " " + numInstances);

            lineScan.close();

        }
        fileScan.close();

    }

    private static GeneBankSearchBTreeArguments parseArgumentsAndHandleExceptions(String[] args)
    {
        GeneBankSearchBTreeArguments geneBankSearchBTreeArguments = null;
        try
        {
            geneBankSearchBTreeArguments = parseArguments(args);
        }
        catch (ParseArgumentException e)
        {
            printUsageAndExit(e.getMessage());
        }
        return geneBankSearchBTreeArguments;
    }

    private static void printUsageAndExit(String errorMessage)
    {
        System.err.println(errorMessage);
        System.err.println("Usage: java -jar build/libs/GeneBankSearchBTree.jar --cache=<0/1> --degree=<btree-degree>" +
        " --btreefile=<b-tree-file> --length=<sequence-length> --queryfile=<query-file> " +
        " [--cachesize=<n>] [--debug=0|1]");
        System.exit(1);
    }

    public static GeneBankSearchBTreeArguments parseArguments(String[] args) throws ParseArgumentException
    {
        if (args.length < 5 || args.length > 7) {
            throw new ParseArgumentException("Error: Invalid number of arguments passed in.\n");
        }
        String[] option = new String[7];
        String[] value = new String[7];
        
        for (int i = 0; i < args.length; i++) {
            String[] tmpA = new String[2];
            tmpA = args[i].split("=");
            option[i] = tmpA[0];
            value[i] = tmpA[1];
        }

        boolean useCache = false;;
        int degree = 0;
        String btreeFileName = "";
        int subsequenceLength = 0;
        String queryFileName = "";
        int cacheSize = 0;
        int debugLevel = 0;

        for (int i = 0; i < args.length; i++) {
            switch (option[i]) {
                case "--cache":
                    if (Integer.parseInt(value[i]) == 1) {
                        useCache = true;
                    } else {
                        useCache = false;
                    }
                    break;
                case "--degree":
                    degree = Integer.parseInt(value[i]);
                    break;
                case "--btreefile":
                    btreeFileName = value[i];
                    break;
                case "--length":
                    subsequenceLength = Integer.parseInt(value[i]);
                    break;
                case "--queryfile": 
                    queryFileName = value[i];
                    break;
                case "--cachesize":
                    cacheSize = Integer.parseInt(value[i]);
                    break;
                case "--debug":
                    debugLevel = Integer.parseInt(value[i]);
                    break;
                default:
                    throw new ParseArgumentException("Error: Invalid argument(s)");
            }
        }
        if (useCache == true && cacheSize <= 0) {
            throw new ParseArgumentException("Error: Cache size must be specified if cache is being used");
        }
        else {
            GeneBankSearchBTreeArguments gBankSearchBTreeArguments= new GeneBankSearchBTreeArguments(useCache, degree, btreeFileName, subsequenceLength, queryFileName, cacheSize, debugLevel);
            return gBankSearchBTreeArguments;
        }
    }

}

