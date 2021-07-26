import java.util.ArrayList;
import java.util.List;

public class Cluster {

	private int id;
	private List<Doc> documentsInCluster = new ArrayList<Doc>();
	
	private double[] centroid;
	
	private List<double[]> docVectors = new ArrayList<double[]>();

	
	Cluster(int ID) {
		
		this.id = ID;
		
	}
	
	
	public void addDocVector(double[] docVec) {
		docVectors.add(docVec);
	}
	
	public void addDocObject(Doc doc) {
		this.documentsInCluster.add(doc);
	}
	
	public void clear() {
		for (int i=0; i < docVectors.size(); i ++) {
			this.docVectors.remove(i);
			this.documentsInCluster.remove(i);
		}
	}
	
	
	//SETTERS
	
	public void setCentroid(double[] c) {
		this.centroid = c;
	}
	
	//GETTERS
	
	public int getID() {
		return this.id;
	}
	
	public double[] getCentroid() {
		return centroid;
	}
	
	public List<Doc> getDocumentsInCluster() {
		return this.documentsInCluster;
	}
	
	public List<double[]> getDocVectors() {
		
		return this.docVectors;
	}

}
