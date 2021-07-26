import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TEST {

	private static double[][] DTM;
	
	public static void main(String[] args) {

		Preprocessing preprocess = new Preprocessing("/Users/nurdidemmurtezaoglu/eclipse-workspace/TextualDataClustering/src/dataset/");
		
		DTM_Generator F1 = new DTM_Generator(preprocess.getDocuments(), preprocess.getTermsList());
		
		KMeans K = new KMeans(F1.getDTM(), 3, preprocess.getDocuments());
				
	}

}
