package bin;

import datasets.GenerateSetsEN;
import datasets.GenerateSetsPT;
import datasets.TestClassification;

public class Main {

    public static void main(String[] args) throws Exception {

        if (args.length < 5) {
            System.out.println("MuSICo.jar bin.Main dataset true|false #min-hash-sigs #bands #kNN [train_file] [test_file]");
            System.out.println();
            System.out.println("dataset           semeval|wiki|aimed|wikipt");
            System.out.println("true|false        generate shingles?");
            System.out.println("#min-hash-sigs    number of hash signatures");
            System.out.println("#bands            size of the LSH bands");
            System.out.println("#kNN              number of closest neighbors to consider");
            System.out.println();
	        System.exit(0);
        }

	  	else {
			
			TestClassification.signature = Integer.parseInt(args[2]);
			TestClassification.bands = Integer.parseInt(args[3]);
			TestClassification.knn = Integer.parseInt(args[4]);
			  
            System.out.println("Min-Hash Signatures Size  " + TestClassification.signature);
            System.out.println("Number LSH Bands          " + TestClassification.bands);
            System.out.println("k Nearest Neighbours      " + TestClassification.knn);
            System.out.println();

            String train_file = null;
            String test_file = null;
			  
            if (args.length==7) {
              train_file = args[5];
              test_file = args[6];
            }

            if (args[0].equalsIgnoreCase("semeval") && args[1].equalsIgnoreCase("false"))
                TestClassification.testSemEval(train_file,test_file);

            else if (args[0].equalsIgnoreCase("semeval") && args[1].equalsIgnoreCase("true")) {
                GenerateSetsEN.generateDataSemEval(train_file,test_file);
                TestClassification.testSemEval(null,null);
            }

            if (args[0].equalsIgnoreCase("aimed") && args[1].equalsIgnoreCase("false"))
            	TestClassification.testAIMED();
            else if (args[0].equalsIgnoreCase("aimed") && args[1].equalsIgnoreCase("true")) {
                GenerateSetsEN.generateDataAIMED();
                TestClassification.testAIMED();
            }

            if (args[0].equalsIgnoreCase("wiki") && args[1].equalsIgnoreCase("false"))
            	TestClassification.testWikiEN();
            else if (args[0].equalsIgnoreCase("wiki") && args[1].equalsIgnoreCase("true")) {
                GenerateSetsEN.generateDataWikiEn();
                TestClassification.testWikiEN();
            }

            if (args[0].equals("wikipt") && args[1].equalsIgnoreCase("false"))
                TestClassification.testWikiPT();
            else if (args[0].equals("wikipt") && args[1].equalsIgnoreCase("true")) {
                GenerateSetsPT.generateWikiPT();
                TestClassification.testWikiPT();
            }
		}
	}
}