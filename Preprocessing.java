import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.io.FileUtils;

import com.opencsv.CSVWriter;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.CoreNLPProtos.Document;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.ling.CoreAnnotations;
import opennlp.tools.ngram.NGramModel;
import opennlp.tools.util.StringList;
import edu.stanford.nlp.simple.*;


public class Preprocessing {
	private static List<CoreLabel> tokens;
	
	private static List<String> terms;
	private static List<Doc> documents = new ArrayList<Doc>();
	
	private static String textString;
    private static Map<String, Integer> freqMap = new HashMap<>();
    
	private static List<String> stopWordsList = new ArrayList<>();
	
    private static Map<List<String>, Integer> nGramsMap = new HashMap<>();
	
	private static List<String> textArray;
	private static CoreDocument coreDocument;
	private static String encoding;

	Preprocessing(String folderPath) {
		
		try {
			
			File folder = new File(folderPath);
			File[] listOfFiles = folder.listFiles();
			
			for (int i = 0; i < listOfFiles.length; i++) {
				File file = listOfFiles[i];
				if (file.isFile() && file.getName().endsWith(".txt")) {
					String content = FileUtils.readFileToString(file, encoding);
									
					textString += "\n//////////////\n";
					textString += content;

					Doc doc = new Doc(file);
					documents.add(doc);
					
				} 						 
			}
		
			textString = removePunctuation(textString);
			
			textString = textString.toLowerCase();
			
		    List<List<String>> result = nGrams(removeStopWords(getLemmasList(textString)), 2);
		    
		    nGramsMap = mapNGrams(result, 5, nGramsMap);
		    
		    addNGramsToTerms(nGramsMap, freqMap);
		    
			freqMap = mapTerms(removeStopWords(getLemmasList(textString.toLowerCase())), freqMap);
		    
			//allTerms.txt --> all terms sorted by order of frequency
			writeTermsToFile();

		} catch (IOException e) {
		    System.out.println("UNABLE TO OPEN FILE");
		}
		

	}

	public StanfordCoreNLP getPipeline() {
		
		Properties properties;
		String propertiesName = "tokenize, ssplit, pos, lemma, ner, regexner";
		StanfordCoreNLP coreNLP = null;

		
		properties = new Properties();
		properties.setProperty("annotators", propertiesName);
		
		if (coreNLP == null) {
			coreNLP = new StanfordCoreNLP(properties);
		}
		return coreNLP;
	}
	
	public static List<String> removeStopWords(List<String> allWords) {
		
		File stopwordsFile = new File("/Users/nurdidemmurtezaoglu/eclipse-workspace/TextualDataClustering/src/stopwords.txt");
		
		try {

		    //String[] allWords = textString.toLowerCase().trim().split(" ");

			String stopwordsText = FileUtils.readFileToString(stopwordsFile, encoding);
			String[] stopWords = stopwordsText.split("\n");
		
		    
			for (String sword : stopWords) {
				textString = textString.replaceAll("\n", "");
				textString = textString.replaceAll("\t", "");
			}
			
			List<String> newWordList = new ArrayList<String>();
			
		    for(String word : allWords) {
		    
		    	boolean isStopWord = false;
		    	
		    	for (String sword: stopWords) {
		    		if (word.trim().equals(sword)) {
						isStopWord = true;
		    		}
		    	}	
		    	
		    	if (!isStopWord) {
			    	newWordList.add(word);
		    	}
		    }
		    textArray = newWordList;
		    return newWordList;
		    
		} catch (IOException e){
			System.out.println("UNABLE TO OPEN STOPWORDS FILE");
			return null;
		}
		
	}
	
	public void POS() {
		for (CoreLabel coreLabel : tokens) {
			
			String pos = coreLabel.get(CoreAnnotations.PartOfSpeechAnnotation.class);
			
			System.out.println(coreLabel.originalText() + " = " + pos);
			
		}
	}
	
	public void NER() {
				
	    List<CoreEntityMention> entityMentions = coreDocument.entityMentions();
	    
	    System.out.println(entityMentions);

	}
	
	
	
	
	public static List<String> getLemmasList(String text) {
		  Sentence sentence = new Sentence(text);
		  return sentence.lemmas();
	}
	
	public static Map<String, Integer> mapTerms(List<String> lemmaList, Map<String, Integer> terms) {
		for (String s : lemmaList) {
	        
			if (terms.containsKey(s)) {
	            Integer count = terms.get(s);
	            terms.put(s, count + 1);
	        } else{
	            terms.put(s, 1);
	        }
		}
		
	    terms = terms
	            .entrySet()
	            .stream()
	            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
	            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
	                    LinkedHashMap::new));
	    
	    return terms;
	}
	
	public static Map<List<String>, Integer> mapNGrams(List<List<String>> n_Grams, int nGramThreshold, Map<List<String>, Integer> ngrams) {
				
		for (List<String> e : n_Grams) {
	        
			if (ngrams.containsKey(e)) {
	            Integer count = ngrams.get(e);
	            ngrams.put(e, count + 1);
	        } else{
	            ngrams.put(e, 1);
	        }
		}
	    ngrams = ngrams
	            .entrySet()
	            .stream()
	            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
	            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
	                    LinkedHashMap::new));
	    
	    
        // Get the iterator over the HashMap
        Iterator<Map.Entry<List<String>, Integer>>
        iterator = ngrams.entrySet().iterator();
  
        // Iterate over the HashMap
        while (iterator.hasNext()) {
  
            // Get the entry at this iteration
            Map.Entry<List<String>, Integer> entry = iterator.next();
  
            // Check if this value is the required value
            if (entry.getValue() < nGramThreshold) {
                // Remove this entry from HashMap
                iterator.remove();
            }
        }
	    
        return ngrams;
	}
	
	public static <T> List<List<T>> nGrams(List<T> list, int n) {
	    return IntStream.range(0, list.size() - n + 1)
	                    .mapToObj(i -> new ArrayList<>(list.subList(i, i + n)))
	                    .collect(Collectors.toList());
	}
	
	public static void addNGramsToTerms(Map<List<String>, Integer> ngrams, Map<String, Integer> terms) {
		
        // Get the iterator over the HashMap
        Iterator<Map.Entry<List<String>, Integer>>
        iterator = ngrams.entrySet().iterator();
        
        // Iterate over the HashMap
        while (iterator.hasNext()) {
  
            // Get the entry at this iteration
            Map.Entry<List<String>, Integer> entry = iterator.next();
  
            terms.put(entry.getKey().get(0) + " " + entry.getKey().get(1), entry.getValue());

        }
	}
	

	
	//HELPERS
	
	public static String removePunctuation(String txt) {
		txt = txt.replaceAll("\\p{Punct}", "");
		return txt;
	}
	
	public void lowerCase() {
		textString = textString.toLowerCase();
	}
	
	public String coreLabelToString(CoreLabel coreLabel) {
						
			return coreLabel.originalText();
	}

	public List<String> getTokenList() {
		
		for (CoreLabel coreLabel : tokens) {
			terms.add(coreLabelToString(coreLabel));
		}
		
		return terms;
	}
	
    public void writeTermsToFile() {
        
        try {
            File myObj = new File("allTerms.txt");
            if (myObj.createNewFile()) {
              System.out.println("File created: " + myObj.getName());
              
	          	try {
	                FileWriter myWriter = new FileWriter("/Users/nurdidemmurtezaoglu/eclipse-workspace/TextualDataClustering/src/allTerms.txt/");
	                myWriter.write(
	                		
	                		getTermsList().toString()
	                		
	                );
	                myWriter.close();
	                System.out.println("Successfully created allTerms.txt");
	            } catch (IOException e) {
	                System.out.println("An error occurred.");
	                e.printStackTrace();
	            }
            } else {
            	try {
                    FileWriter myWriter = new FileWriter("/Users/nurdidemmurtezaoglu/eclipse-workspace/TextualDataClustering/src/allTerms.txt/");
                    myWriter.write(
                    		
	                		getTermsList().toString()

                    		
                    );
                    myWriter.close();
                    System.out.println("-- Successfully updated file allTerms.txt --");
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            }            
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    
    }
	
	
	
	
	//GETTERS
	
	public List<String> getTermsList() {
		
		terms = new ArrayList<String>(freqMap.keySet());
		
		return terms;
	}
	
	public List<Doc> getDocuments() {
		return documents;
	}
	
	
	public String getTextString() {
		return textString;
	}
}
