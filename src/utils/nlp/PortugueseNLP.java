package utils.nlp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ptstemmer.exceptions.PTStemmerException;



public class PortugueseNLP {

	/* 
	 * Além dos testes com aquele dataset da Wikipédia, como te disse hoje,
	 * também pode fazer sentido tentarmos usar o dataset do HAREM para testar o
	 * modelo.
	 * 
	 * http://www.linguateca.pt/aval_conjunta/HAREM/GlossarioReRelEM.html
	 * http://comum.rcaap.pt/bitstream/123456789/242/1/Capitulo_04-MotaSantos2008.pdf
	 */

	

	public static String generateNGrams(String source, String prefix, int betweenLenght, int casing, int window) throws PTStemmerException {
		
		String[] sourcePOS = PortuguesePOSTagger.posTags(source);
		String[] sourceTokens = PortuguesePOSTagger.tokenize(source);
		
		/*
		if ((sourcePOS!=null && sourceTokens!=null) && sourceTokens.length == sourcePOS.length) {
			for (int i = 0; i < sourceSteemed.length; i++) {
				System.out.println(sourceTokens[i] + "\t\t\t" + sourceSteemed[i] + "\t\t" + sourcePOS[i]);
			}
		}
		*/
		
		int weight = 1;		
		//List<Integer> features = Arrays.asList(1,2,3,4,5,6);
		List<Integer> features = Arrays.asList(5);
		
		/*
		 * 1 - unigramas de palavras (i.e., bag-of-words) 
		 * 2 - bi-gramas de palavras
		 * 3 - tri-gramas de palavras
		 * 6 - four-gramas de plalvras
		 * 4 - word bigrams with gaps   
		 * 5 - tri-gramas de caracteres
		 * - bi-gramas de stems de palavras 
		 */
		 
		/*
		 * - tri-gramas de caracteres + verbos 
		 * - tri-gramas de caracteres + verbos + preposições 
		 * - tri-gramas de caracteres + verbos + preposições + padrões_reverb (os 2 padrões)
		 */
		
		Set<String> set = new HashSet<String>();
		
		if ((sourcePOS!=null && sourceTokens!=null) && sourceTokens.length == sourcePOS.length) {
			for ( int i = 0 ; i < sourceTokens.length; i++ ) {				
				if ( prefix.startsWith("BEF") && sourceTokens.length - i > betweenLenght + window ) continue;
				if ( prefix.startsWith("AFT") && i > betweenLenght + window ) continue;				
				if ( sourcePOS[i].startsWith("verb") ) { 
					  set.add(sourceTokens[i] + "_" + ( i < sourceTokens.length - 1 ? sourceTokens[i+1] + "_VERB_" : "" ) + prefix);
					  
		  	      //ReVerb inspired: um verbo, seguido de vários nomes, adjectivos ou adverbios, terminando numa preposição.
		  		  if (i < sourceTokens.length - 2) {
		  			String pattern = sourceTokens[i];
		  			int j = i+1;				
					try {
						while ( ((j < sourceTokens.length - 2)) && ((sourcePOS[j].startsWith("adverb") || sourcePOS[j].startsWith("adjective")) || sourcePOS[j].startsWith("noun"))) {	  				
							pattern += "_" + sourceTokens[j];
							j++;				
						}
					} catch (Exception e) {
						System.out.println("i:" + i);
						System.out.println("j: " + j);
						System.out.println("aux.length-2: " + String.valueOf(sourceTokens.length-2));						
						System.out.println("prefix: " + prefix);
						System.out.println("source: " + source);
						System.out.print("sourcePOS: " + '\t');
						for (int k = 0; k < sourcePOS.length; k++) {
							System.out.print(sourcePOS[k] + '\t');
						}
						System.out.println();						
						e.printStackTrace();
						System.exit(0);
					}					
					if (sourcePOS[j].startsWith("preposition")) {
							pattern += "_" + sourceTokens[j];
							set.add(pattern + "_RVB_" + prefix);
						}
		  		  }
		  		  
		  		  //particípio passado composto
		  		  if ( (i < sourceTokens.length - 1) && sourcePOS[i+1].startsWith("pp") ) {
		  			set.add(sourceTokens[i] + "_" + sourceTokens[i+1] + "PP" + "_" + prefix);

		  			
		  		  }
		  		  
		  		  //proposições
		  		  else if (sourcePOS[i].startsWith("preposition")) {
					  set.add(sourceTokens[i] + "_" + ( i < sourceTokens.length - 1 ? sourceTokens[i+1] + "_PREP_" : "" ) + prefix);					  
		  		  }
				}
			}
		
			
			/*
			if (casing == 1) source = source.toLowerCase();

			for (int i = 0; i < aux.length; i++) {
				if (aux[i].matches("^[0-9][0-9]0-9][0-9]+$"))
					aux[i] = "NUMBER"; // NUMBER
				if (aux[i].matches("^[0-9][0-9]0-9][0-9]$"))
					aux[i] = "4DIG"; // 4DIG
				if (aux[i].matches("^[0-9][0-9]$"))
					aux[i] = "2DIG"; // 2DIG
			}
			*/
			
			if (casing == 2)
				for (String s : generateNGrams(source, prefix, betweenLenght, casing, window).split(" "))
					set.add(s);
			
			// word unigrams
			if (features.contains(1))
				for (String tok : sourceTokens)
					for (int i = 1; i <= weight; i++)
						set.add(tok + "_" + prefix + "_" + i);
			
			// word bigrams
			if (features.contains(2))
				for (int j = 0; j <= sourceTokens.length; j++) {
					String tok1 = j - 1 >= 0 ? sourceTokens[j - 1] : "_";
					String tok2 = j < sourceTokens.length ? sourceTokens[j] : "_";
					for (int i = 1; i <= weight; i++)
						set.add(tok1 + "_" + tok2 + "_" + prefix + "_" + i);
				}
			// word trigrams
			if (features.contains(3))
				for (int j = 0; j <= sourceTokens.length + 1; j++) {
					String tok1 = j - 2 >= 0 ? sourceTokens[j - 2] : "_";
					String tok2 = j - 1 >= 0 && j - 1 < sourceTokens.length ? sourceTokens[j - 1]: "_";
					String tok3 = j < sourceTokens.length ? sourceTokens[j] : "_";
					for (int i = 1; i <= weight; i++)
						set.add(tok1 + "_" + tok2 + "_" + tok3 + "_" + prefix + "_" + i);
				}
			
			// word bigrams with gaps
			if (features.contains(4))
				for (int j = 2; j < sourceTokens.length; j++) {
					String tok1 = sourceTokens[j - 2];
					String tok2 = "GAP";
					String tok3 = sourceTokens[j];
					for (int i = 1; i <= weight; i++)
						set.add(tok1 + "_" + tok2 + "_" + tok3 + "_" + prefix + "_" + i);
				}
			
			// character trigrams
			if (features.contains(5))
				for (int j = 0; j < source.length() + 3; j++) {
					String tok = "";
					for (int i = -3; i <= 0; i++) {
						char ch = (j + i) < 0 || (j + i) >= source.length() ? '_' : source.charAt(j + i);
						tok += ch == ' ' ? '_' : ch;
					}
					for (int i = 1; i <= weight; i++)
						set.add(tok + "_" + prefix + "_" + i);
				}
			// word fourgrams
			if (features.contains(6))
				for (int j = 0; j <= sourceTokens.length + 2; j++) {
					String tok1 = j - 3 >= 0 ? sourceTokens[j - 3] : "_";
					String tok2 = j - 2 >= 0 && j - 2 < sourceTokens.length ? sourceTokens[j - 2] : "_";
					String tok3 = j - 1 >= 0 && j - 1 < sourceTokens.length ? sourceTokens[j - 1] : "_";
					String tok4 = j < sourceTokens.length ? sourceTokens[j] : "_";
					for (int i = 1; i <= weight; i++)
						set.add(tok1 + "_" + tok2 + "_" + tok3 + "_" + tok4 + "_"
								+ prefix + "_" + i);
				}
			
			// word unigrams with POS
			// if ( n == 7 ) for ( int j = 0; j < aux.length; j++ ) for ( int i = 1;
			// i <= weight; i++) set.add(aux[j] + "_" + auxPOS[j] + "_" + prefix +
			// "_" + i);
			// word bigrams with POS
			/*
			 * if ( n == 8 ) for ( int j = 0; j <= aux.length; j++ ) { String tok1 =
			 * j-1 >=0 ? aux[j-1] + "_" + auxPOS[j-1] : "_"; String tok2 =
			 * j<aux.length ? aux[j] + "_" + auxPOS[j] : "_"; for ( int i = 1; i <=
			 * weight; i++) set.add(tok1 + "_" + tok2 + "_" + prefix + "_" + i); }
			 */
			
		} else {
			System.out.println("tokens[]!=tokensPOS : " + source);
		}
		
		String result = "";
		for (String tok : set)
			result += " " + tok;
		return result.trim();
	}
}