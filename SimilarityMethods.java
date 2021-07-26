import java.util.Map;

public class SimilarityMethods {

	//Cos(x, y) = x . y / ||x|| * ||y||
    public double cosineSimilarity(double[] array1, double[] array2) {
    	if (array1.length != array2.length) {
    		return 2;
    	}

    	double dotProductAB = 0;
    	double sumA = 0;
    	double sumB = 0;
    	for (int i = 0; i < array1.length; i++) {
    		dotProductAB += array1[i]*array2[i];
    		sumA += Math.pow(array1[i], 2);
    		sumB += array2[i] * array2[i];
    	}
    	if (sumA == 0 && sumB == 0) {
    		return 2.0;
    	}
    	return dotProductAB / (Math.sqrt(sumA) * Math.sqrt(sumB));
    }
    
	
	public double euclideanDistance(double[] array1, double[] array2){
		
        double Sum = 0.0;
        for(int i=0;i<array1.length;i++) {
           Sum = Sum + Math.pow((array1[i]-array2[i]), 2.0);
        }
        return Math.sqrt(Sum);
    }
	
	

	//HELPERS
	
	public double dotProduct(double[] x, double[] y) {
	
		if (x.length != y.length)
		    throw new RuntimeException("ERROR: arrays should be same length");
		double sum = 0;
		for (int i = 0; i < x.length; i++)
		    sum += x[i] * y[i];
		return sum;
	}

	public double len(double[] x) {
		
		double l = 0;
		
		for (double e : x) {
			l += Math.pow(e, 2);
		}
		
		return Math.sqrt(l);
	}


}


