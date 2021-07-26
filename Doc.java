import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.simple.*;


public class Doc {
	
	private String docName;
	private List<CoreLabel> tokens;
	private String textString;
	private List<String> textArray;
	private CoreDocument coreDocument;
	private String encoding;
	
	private int clusterID;

	private Map<String, Integer> terms = new HashMap<>();
	private Map<List<String>, Integer> nGrams = new HashMap<>();
	
	Doc(File file) {
		
		try {
			
			this.docName = file.getName();
			
		    String content = FileUtils.readFileToString(file, encoding);
		    
		    this.textString = content;
		    
			this.coreDocument = new CoreDocument(content);
			
			this.textString = Preprocessing.removePunctuation(textString);
			
			this.textString = textString.toLowerCase();
			
		    List<List<String>> res = Preprocessing.nGrams(Preprocessing.removeStopWords(Preprocessing.getLemmasList(textString)), 2);
		    
		    this.nGrams = Preprocessing.mapNGrams(res, 5, nGrams);
		    		    
		    Preprocessing.addNGramsToTerms(nGrams, terms);
		    
		    this.terms = Preprocessing.mapTerms(Preprocessing.removeStopWords(Preprocessing.getLemmasList(textString.toLowerCase())), terms);
						
		} catch (IOException e) {
		    System.out.println("UNABLE TO OPEN FILE");
		}
		
	}
	
	  /**
	  * @param doc  list of strings
	  * @param term String represents a term
	  * @return term frequency of term in document
	  */
	public double tf(String term) {
		List<String> termsList = getTermsList();
	     double result = 0;
	     for (String word : termsList) {
	         if (term.equalsIgnoreCase(word))
	             result++;
	     }
	     return result / termsList.size();
	}
	
	
	//GETTERS
	
	public int getClusterID() {
		return this.clusterID;
	}
	
	
	public Map<String, Integer> getTermsMap() {
		return this.terms;
	}
	
	
	
	
	public List<String> getTermsList() {
		
		List<String> termsList = new ArrayList<String>();
		
        Iterator<Map.Entry<String, Integer>>
        iterator = terms.entrySet().iterator();
        
	        // Iterate over the HashMap
	        while (iterator.hasNext()) {
	  
	            // Get the entry at this iteration
	            Map.Entry<String, Integer> entry = iterator.next();
	            
	            termsList.add(entry.getKey());
	            
	        }
	        
	    return termsList;
	}
	
	
	public List<CoreLabel> getTokens() {
		return tokens;
	}
	
	
	public String getDocName() {
		return this.docName;
	}
	
	public String getTextString() {

		return textString;
	}
	
	public List<String> getTextArray() {
		
		return textArray;
	}
	
	public void setClusterID(int cID) {
		this.clusterID = cID;
	}
	

}
