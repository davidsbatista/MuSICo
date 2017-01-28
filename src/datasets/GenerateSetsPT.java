package datasets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import utils.nlp.PortuguesePOSTagger;
import utils.nlp.PortugueseVerbNormalizer;

public class GenerateSetsPT {

	public static void generateWikiPT() throws Exception {
		PortugueseVerbNormalizer.initialize();
		PortuguesePOSTagger.initialize();
		PrintWriter outTrain = new PrintWriter(new FileWriter("shingles/train-data-wikipt.txt"));
		PrintWriter outTest = new PrintWriter(new FileWriter("shingles/test-data-wikipt.txt"));
		System.out.println("Generating WikiPT data...");
		processWikiPT("datasets/WikiPT/DBpediaRelations-PT-0.2.txt",outTrain,outTest);
	}

    public static void processWikiPT(String file, PrintWriter outTrain, PrintWriter outTest) throws Exception {
        BufferedReader input = new BufferedReader(new FileReader(file));
        String aux;
        String sentence;
        String type = null;
        String e1 = null;
        String e2 = null;
        boolean checked;

        int count = 0;

        while ((aux = input.readLine()) != null) {

            if (aux.startsWith("SENTENCE")) {

                count++;

                if (count % 1000 == 0)
                    System.out.println(count);

                sentence = aux.split(": ")[1];
                sentence = sentence.replaceAll("&nbsp;", "").replaceAll("&mdash;", "—").replaceAll("&ndash", "–").replaceAll("&bull;", "•");
                sentence = sentence.replaceAll("\\[?URLTOKEN\\s?([A-Za-z0-9íÍÌìàÀáÁâÂâÂãÃçÇéÉêÊóÓõÕôÔúÚüÜ\\.\\s,\\+\\(\\)\\-]+)?\\]?", "");

                // trained portuguese SentenceDetector has bugs, fix them
                sentence = sentence.replaceAll(" ca\\. "," ca ").replaceAll(" etc\\. ", " etc ").replaceAll("\\(c\\. ", "(c ").replaceAll(" c\\. ", " c ");
                sentence = sentence.replaceAll(" Mrs\\. "," Mrs  ").replaceAll("Ph\\.D\\.", "PhD").replaceAll("LL\\.D\\.","LLD").replaceAll("Sc\\.D\\.","ScD").replaceAll(" Mr\\. "," Mr ");
                sentence = sentence.replaceAll("([0-9]+)\\.([0-9]+)\\.([0-9]+)\\.([0-9]+)","$1 $2 $3 $4");
                sentence = sentence.replaceAll("([0-9]+)\\.([0-9]+)\\.([0-9]+)","$1 $2 $3");
                sentence = sentence.replaceAll("([0-9]+)\\.([0-9]+)","$1 $2");
                sentence = sentence.replaceAll(" Lei nº\\. "," Lei nº ").replaceAll(" n°\\. ", " nº ").replaceAll(" nº\\. ", "  nº ").replaceAll("\\(n. ", "(nº ");
                sentence = sentence.replaceAll(" S\\.A\\. "," SA ").replaceAll("Inc\\.","Inc");

                aux = input.readLine();
                if (aux.equals("")) aux = input.readLine();
                if (aux.startsWith("MANUALLY CHECKED : TRUE"))
                    checked = true;
                else checked = false;

                aux = input.readLine();
                while (!aux.startsWith("*")) {
                    if (aux.startsWith("ENTITY1")) e1 = aux.split(": ")[1].trim();
                    if (aux.startsWith("ENTITY2")) e2 = aux.split(": ")[1].trim();
                    if (aux.startsWith("REL TYPE")) type = aux.split(": ")[1];
                    aux = input.readLine();
                }

                processRelations(sentence,e1,e2,type,checked,outTrain,outTest);
            }
        }

        outTrain.flush();
        outTrain.close();
        outTest.flush();
        outTest.close();
        input.close();

    }

    public static void processRelations(String sentence, String e1, String e2, String type, boolean checked, PrintWriter outTrain, PrintWriter outTest) throws IOException {

        String before = null;
        String between = null;
        String after = null;
        String direction = null;

        int e1_count = countWords(e1,sentence);
        int e2_count = countWords(e2,sentence);

        if ( (!(e1.contains(e2) || e2.contains(e1))) && (!e2.equals(e1)) && (e1_count==1) && (e2_count==1)) {

            int e1_start = sentence.indexOf(e1);
            int e1_finish = sentence.indexOf(e1)+e1.length();

            int e2_start = sentence.indexOf(e2);
            int e2_finish = sentence.indexOf(e2)+e2.length();

            if (e1_start!=-1 && e2_start!=-1) {

                // does the relationship as a direction or is symmetrical ?
                if (!(type.equals("other") || type.equals("partner"))) {

                    if (e1_finish < e2_start) direction = "(e1,e2)";
                    else if (e1_start > e2_finish) direction = "(e2,e1)";
                    type = type + direction;

                    if (direction==null) {
                        System.out.println("direction null:" + sentence);
                        System.out.println("e1 start: " + e1_start);
                        System.out.println("e1 finish: " + e1_finish);
                        System.out.println("e2 start: " + e2_start);
                        System.out.println("e2 finish: " + e2_finish);
                    }
                }

                try {
                    before = sentence.substring(0,Math.min(e1_finish, e2_finish));
                    between = sentence.substring(Math.min(e1_finish, e2_finish),Math.max(e1_start,e2_start));
                    after = sentence.substring(Math.max(e1_start, e2_start),sentence.length()-1);
                    before = before + " " + between;
                    after = between + " " + after;

                } catch (Exception e) {
                    System.out.println(sentence);
                    System.out.println("e1: " + e1 + '\t' + '\t' + "("+e1_start+","+e1_finish+")" + '\t' + e1_count);
                    System.out.println("e2: " + e2 + '\t' + '\t' + "("+e2_start+","+e2_finish+")" + '\t' + e2_count);
                    System.out.println(type + direction);
                    System.out.println();
                    System.out.println("before: " + before);
                    System.out.println("between: " + between);
                    System.out.println("after: " + after);
                    System.out.println();
                    System.out.println("\n========");
                    e.printStackTrace();
                    System.exit(0);
                }
            }

            if (!checked)
                processExample(before,after,between,type,outTrain);
            else
                processExample(before,after,between,type,outTest);
        }
    }

    public static void processExample(String before, String after,String between, String type, Writer out) throws IOException {

        int window = 3;
        int casing = 1;

        out.write(type);
        if (before.lastIndexOf(",") != -1 && before.lastIndexOf(",") < before.lastIndexOf(between)) before = before.substring(before.lastIndexOf(",") + 1);
        if (after.indexOf(",") != -1 && after.indexOf(",") > between.length()) after = after.substring(0, after.lastIndexOf(","));

        int betweenLength = PortuguesePOSTagger.tokenize(between).length;
        int beforeLength = PortuguesePOSTagger.tokenize(before).length;
        int afterLength = PortuguesePOSTagger.tokenize(after).length;

        if (beforeLength >= Math.max(betweenLength, afterLength)) out.write(" " + "LARGER_BEF");
        if (afterLength >= Math.max(betweenLength, beforeLength)) out.write(" " + "LARGER_AFT");
        if (betweenLength >= Math.max(afterLength, beforeLength)) out.write(" " + "LARGER_BET");

        if (beforeLength == 0) out.write(" " + "EMPTY_BEF");
        if (afterLength == 0) out.write(" " + "EMPTY_AFT");
        if (betweenLength == 0) out.write(" " + "EMPTY_BET");
        ArrayList<String> someCollection = new ArrayList<String>();
        for (String aux : new String[] { "BEF\t" + before, "BET\t" + between, "AFT\t" + after })
            someCollection.add(aux);
        for (String obj : someCollection) {
            String prefix = obj.substring(0, obj.indexOf("\t"));
            String str = obj.substring(obj.indexOf("\t") + 1);
            out.write(" " + generateNGrams(str, prefix, betweenLength, casing, window));
        }
        out.write("\n");
    }

	public static String generateNGrams(String source, String prefix, int betweenLenght, int casing, int window) {
		
		String[] sourcePOS = PortuguesePOSTagger.posTags(source);
		String[] sourceTokens = PortuguesePOSTagger.tokenize(source);

		/* - quadgrams de caracteres
		 * - quadgrams de caracteres + verbos 
		 * - quadgrams de caracteres + verbos + preposições 
		 * - quadgrams de caracteres + verbos + preposições + padrão_reverb
		 */
		
		Set<String> set = new HashSet<String>();
		
		if ((sourcePOS!=null && sourceTokens!=null) && sourceTokens.length == sourcePOS.length) {
			for ( int i = 0 ; i < sourceTokens.length; i++ ) {				
				if ( prefix.startsWith("BEF") && sourceTokens.length - i > betweenLenght + window ) continue;
				if ( prefix.startsWith("AFT") && i > betweenLenght + window ) continue;
				
				if ( sourcePOS[i].startsWith("verb") || sourcePOS[i].startsWith("pp") ) {
				  
				  //normalizar o verbo
				  //String verb = PortugueseVerbNormalizer.normalize(sourceTokens[i].toLowerCase());				  
				  //if (verb == null) verb = sourceTokens[i].toLowerCase();
				  String verb = sourceTokens[i].toLowerCase();
				  
				  //adiconar verbo normalizado + palavra à frente
				  set.add(verb + "_" + ( i < sourceTokens.length - 1 ? sourceTokens[i+1].toLowerCase() + "_" : "" ) +  prefix);
				 
				  //verbo 
				  set.add(verb + "_VERB_" + prefix);
				  
				  //se o verbo está no PP adicionar nessa forma
				  if ( sourcePOS[i].startsWith("pp")  ) set.add(sourceTokens[i].toLowerCase() + "_PP_" + prefix);
				  
				  //ReVerb inspired: um verbo, seguido de vários nomes, adjectivos ou adverbios, terminando numa preposição.
				  if (i < sourceTokens.length - 2) {		  			
		  			String pattern = verb;
		  			int j = i+1;				
					while ( ((j < sourceTokens.length - 2)) && ((sourcePOS[j].startsWith("adverb") || sourcePOS[j].startsWith("adjective")) || sourcePOS[j].startsWith("noun"))) {	  				
						pattern += "_" + sourceTokens[j].toLowerCase();
						j++;				
					}
					if (sourcePOS[j].startsWith("preposition")) {
							pattern += "_" + sourceTokens[j].toLowerCase();
							set.add(pattern + "_RVB_" + prefix);
					}
		  		  }		  	
				} else if (sourcePOS[i].startsWith("preposition")) set.add(sourceTokens[i].toLowerCase() + "_PREP_" + prefix);
			}
			
			// character quadgrams
			for (int j = 0; j < source.length() + 3; j++) {
					String tok = "";
					for (int i = -3; i <= 0; i++) {
						char ch = (j + i) < 0 || (j + i) >= source.length() ? '_' : source.charAt(j + i);
						tok += ch == ' ' ? '_' : ch;
					}
					set.add(tok + "_" + prefix);
				}
		} 
		String result = "";
		for (String tok : set)
			result += " " + tok;
		return result.trim();
	}

    public static int countWords(String entity, String sentence) {
        int count = 0;
        int result = sentence.indexOf(entity);
        while (result != -1) {
            result = sentence.indexOf(entity, result + 1);
            count++;
        }
        return count;
    }

}