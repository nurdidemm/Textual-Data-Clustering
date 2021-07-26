import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DTM_Generator {
	
	/*
	 * a document term matrix across all documents in a folder
	 * with each row corresponding to a document 
	 * and each column corresponding to a term/keyword
	 * each cell contains the number of times each term occurred in each document
	 */
	private static double[][] DTM;
	
	Map<String, Double> C1_terms = new HashMap<String, Double>();
	Map<String, Double> C4_terms = new HashMap<String, Double>();
	Map<String, Double> C7_terms = new HashMap<String, Double>();
	
	List<Doc> documents;
	List<String> terms;
	
	DTM_Generator(List<Doc> docs, List<String> t) {
		
		documents = docs;
		terms = t;
		
		DTM = new double[documents.size()][terms.size()];
		
		for (int docCounter = 0; docCounter < documents.size(); docCounter++) {
			
			for (int termCounter = 0; termCounter < terms.size(); termCounter++) {
				
				double tfidf = tfIdf(documents.get(docCounter), documents, terms.get(termCounter));
				
				//handle NaN
				if (tfidf != tfidf) {
					DTM[docCounter][termCounter] = 0.0;
				} else {
					DTM[docCounter][termCounter] = tfidf;
				}

			}
		}
		
		//generate topics per folder
		C1_terms = getTopicsPerFolder("c1", C1_terms);
		C4_terms = getTopicsPerFolder("c4", C4_terms);
		C7_terms = getTopicsPerFolder("c7", C7_terms);

		//write topics to topics.txt
		writeTopicsToFile();

	}
	
	
	public Map<String, Double> getTopicsPerFolder(String c, Map<String, Double> folderTermMap) {
		
		double[][] folder_DTM = new double[9][terms.size()];

		int counter = 0;
		
		for (int i = 0; i < documents.size(); i++) {
			if (documents.get(i).getDocName().contains(c)) {
								
				double[] docVect = DTM[i];
				folder_DTM[counter] = docVect;
				counter++;
			}
		}
		
		//Calculate sum of column vector for every row
		for(int cols=0; cols < folder_DTM[0].length; cols++)  {
		
			double sum = 0.0;
			
			for(int rows = 0; rows < folder_DTM.length; rows++) {
				
				sum += folder_DTM[rows][cols];

			}
			
			//add existing terms and their tf-idf to hashmap for folder
			if (sum > 0.0) {
				folderTermMap.put(terms.get(cols), sum);
			}

		}
		
	    folderTermMap = folderTermMap
        .entrySet()
        .stream()
        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                LinkedHashMap::new));
	    		
        // Get the iterator over the HashMap
        Iterator<Map.Entry<String, Double>>
        iterator = folderTermMap.entrySet().iterator();
  
        counter = 0;
        
        Map<String, Double> temp = new HashMap<String, Double>();
        
        // Iterate over the HashMap
        while (iterator.hasNext() && counter < 7) {
  
            // Get the entry at this iteration
            Map.Entry<String, Double> entry = iterator.next();
            temp.put(entry.getKey(), entry.getValue());
            counter ++;
        }
        
        folderTermMap = temp;
        
	    folderTermMap = folderTermMap
	            .entrySet()
	            .stream()
	            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
	            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
	                    LinkedHashMap::new));
	    
		return folderTermMap;

	}

	public void sumOfCols(double[][] M) {
		//Calculates sum of each column of given matrix  
		for (int i = 0; i < M.length; i++) {
		    double sumCol = 0;  
		    for (int j = 0; j < M[i].length; j++) {
		        sumCol += M[j][i];  
		        System.out.print(sumCol + " | ");  
		    }
		}
	}
	
    /**
     * @param docs list of list of strings represents the dataset
     * @param term String represents a term
     * @return the inverse term frequency of term in documents
     */
    public double idf(List<Doc> docs, String term) {
        double n = 0;
        List<List<String>> allDocTerms = new ArrayList<List<String>>();
        
        for (Doc doc : docs) {
        	allDocTerms.add(doc.getTermsList());
        }
        
        
        for (List<String> docTerms : allDocTerms) {
            for (String word : docTerms) {
                if (term.equalsIgnoreCase(word)) {
                    n++;
                    break;
                }
            }
        }
        return Math.log(allDocTerms.size() / n);
    }

    /**
     * @param doc  a text document
     * @param docs all documents
     * @param term term
     * @return the TF-IDF of term
     */
    public double tfIdf(Doc doc, List<Doc> docs, String term) {
    	
        return doc.tf(term) * idf(docs, term);

    }
	
    public void writeTopicsToFile() {
        
        try {
            File myObj = new File("topics.txt");
            if (myObj.createNewFile()) {
              System.out.println("File created: " + myObj.getName());
              
	          	try {
	                FileWriter myWriter = new FileWriter("/Users/nurdidemmurtezaoglu/eclipse-workspace/TextualDataClustering/src/topics.txt/");
	                myWriter.write(
	                		
	                		"C1 = " + C1_terms.keySet().toString() + "\n" + "C4 = " + C4_terms.keySet().toString() +
	                		"\n" + "C7 = " + C7_terms.keySet().toString()
	                		
	                );
	                myWriter.close();
	                System.out.println("Successfully created topics.txt");
	            } catch (IOException e) {
	                System.out.println("An error occurred.");
	                e.printStackTrace();
	            }
            } else {
            	try {
                    FileWriter myWriter = new FileWriter("/Users/nurdidemmurtezaoglu/eclipse-workspace/TextualDataClustering/src/topics.txt/");
                    myWriter.write(
                    		
	                		"C1 = " + C1_terms.keySet().toString() + "\n" + "C4 = " + C4_terms.keySet().toString() +
	                		"\n" + "C7 = " + C7_terms.keySet().toString()
                    		
                    );
                    myWriter.close();
                    System.out.println("-- Successfully updated file topics.txt --");
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
    
    public double[][] getDTM() {
    	
    	return DTM;
    }
	
}
