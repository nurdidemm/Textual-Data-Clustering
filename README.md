# Textual-Data-Clustering


The program preprocesses each document in the dataset to auto-generate keywords, or topics using Stanford CoreNLP library and follows the  object oriented programming paradigm with Java. 


• a class to perform the pre-processing of textual data. 
• a class that takes care of generating the term-document matrix and applies the TF-IDF transformation
• a class that implements the similarity methods
• a class that is responsible for data clustering
 

 The code performs the following steps:


--> filters and removes stopwords

--> applies rokenization, stemming and lemmatization

--> applies named-entity extraction (NER)

--> identifies and tokenizes n-grams using a sliding window approach (words like “computer science”, “beauty pageant”) 

--> generates a Document-Term Matrix (each row in your matrix corresponds to one document in the input dataset, each column represents one term/key-phrase/keyword of the final set of terms across all documents)

--> the matrix is transformed using term frequency–inverse document frequency (TF-IDF) to down-weight terms that are frequent across all documents while promoting terms that occur frequently in the current document, but are generally rare

--> using the transformed matrix, generates keywords for each document folder by combining all the document vectors together and then sorting the terms for each folder based on their TF-IDF scores

--> implement the K-Means clustering algorithm on the final TF-IDF matrix








