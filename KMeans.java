import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class KMeans {

	public SimilarityMethods dist = new SimilarityMethods();

	private double[][] DTM;
	private int numOfClusters;

	double[][] centroids;

	ArrayList<Cluster> clusters;

	List<Doc> documents = new ArrayList<Doc>();

	KMeans(double[][] M, int k, List<Doc> docs) {
		DTM = M;
		numOfClusters = k;

		documents = docs;

		centroids = new double[numOfClusters][DTM[0].length];

		clusters = new ArrayList<Cluster>();
		
		boolean keepGoing = true;

		// initially choose k centroids randomly
		randomCentroids();

		System.out.println("---INITIALIZED RANDOM CLUSTERS---");
		for (Cluster c : clusters) {

			c.setCentroid(normalizeVector(c.getCentroid()));

		}

		while(keepGoing) {

			assignDocsToClusters();

			keepGoing = reCalculateCentroids();

			System.out.println("");

			for (Doc d : documents) {
				System.out.println("DOC : " + d.getDocName() + " --> " + d.getClusterID());
			}
		}
		
		writeClustersToFile();

	}

	// while true
	// create k clusters by assigning each point to closest centroid
	// compute k new centroids by averaging examples in each cluster
	// if centroids do not change
	// break

	public void randomCentroids() {

		for (int i = 0; i < numOfClusters; i++) {
			for (int j = 0; j < this.DTM[0].length; j++) {
				centroids[i][j] = Math.random();
			}
			Cluster cluster = new Cluster(i);
			cluster.setCentroid(centroids[i]);
			clusters.add(cluster);
		}
	}

	public void assignDocsToClusters() {

		// loop through each document vector in the matrix
		for (int i = 0; i < DTM.length; i++) {

			double[] docVector = DTM[i];

			int clusterID = documents.get(i).getClusterID();

			double docDistance = Double.MAX_VALUE;

			System.out.println("");

			// check distance over all clusters' centroids
			for (int j = 0; j < numOfClusters; j++) {
				Cluster c = clusters.get(j);

				// calculate distance for each cluster
				double currentDistance = dist.cosineSimilarity(docVector, c.getCentroid());

				// if centroid is nearer than other centroids
				if (currentDistance < docDistance) {
					docDistance = currentDistance;
					clusterID = c.getID();
				}
			}
			documents.get(i).setClusterID(clusterID);
			clusters.get(clusterID).addDocVector(docVector);
			clusters.get(clusterID).addDocObject(documents.get(i));

		}
	}

	public boolean reCalculateCentroids() {
		
		boolean[] checkCentroidChange = {true, true, true};

		// for every cluster
		for (int clusterCount = 0; clusterCount < clusters.size(); clusterCount++) {

			Cluster c = clusters.get(clusterCount);
			
			double[] sum = new double[DTM[0].length];

			// get documents' feature vectors of documents in cluster
			List<double[]> dVectors = c.getDocVectors();

			// get number of documents in cluster
			int numOfDocs = dVectors.size();

			for (double[] dVect : dVectors) {

				for (int i = 0; i < dVect.length; i++) {
					sum[i] += dVect[i];
				}
			}

			double[] temp = new double[sum.length];

			// get mean of each feature in all document vectors in cluster
			for (int k = 0; k < sum.length; k++) {
				temp[k] = sum[k] / numOfDocs;
			}

			if (c.getCentroid() == temp) {
				checkCentroidChange[clusterCount] = false;
			} else {
				c.setCentroid(temp);
				c.clear();
			}

		}
		
		//centroids haven't changed
		if (checkCentroidChange[0] == true && checkCentroidChange[1] == true && checkCentroidChange[2] == true) {
			return false;
		} else {
			return true;
		}

	}

	// HELPERS

	public double[] normalizeVector(double[] a) {
		double scale = 0;
		for (int k = 0; k < a.length; k++) {
			scale += a[k] * a[k];
		}
		scale = 1 / Math.sqrt(scale);
		for (int k = 0; k < a.length; k++) {
			a[k] *= scale;
		}

		return a;
	}

	public void writeClustersToFile() {

		try {
			File myObj = new File("clusters.txt");
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());

				try {
					FileWriter myWriter = new FileWriter(
							"/Users/nurdidemmurtezaoglu/eclipse-workspace/TextualDataClustering/src/clusters.txt/");
					for (Doc d : documents) {

						myWriter.write(

								"DOC : " + d.getDocName() + " --> " + d.getClusterID()
							
						);
					}
					myWriter.close();
					System.out.println("Successfully created clusters.txt");
				} catch (IOException e) {
					System.out.println("An error occurred.");
					e.printStackTrace();
				}
			} else {
				try {
					FileWriter myWriter = new FileWriter(
							"/Users/nurdidemmurtezaoglu/eclipse-workspace/TextualDataClustering/src/clusters.txt/");
					for (Doc d : documents) {

						myWriter.write(

								"DOC : " + d.getDocName() + " --> " + d.getClusterID()
							
						);
					}
					myWriter.close();
					System.out.println("-- Successfully updated file clusters.txt --");
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

	// GETTERS

	public ArrayList<Cluster> getClusters() {
		return clusters;
	}

}
