package main.fingerprintWinnowing;


import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

/**
 * Calculates Jaccard coefficient for two sets of items. 
 * 
 */
public class JaccardCoefficient implements SimilarityMeasure {

        private static final long serialVersionUID = -5051498381470492495L;

        public JaccardCoefficient() {
        // empty
    }

  /*  public double similarity(String[] x, String[] y) {
        double sim=0.0d;
        if ( (x!=null && y!=null) && (x.length>0 || y.length>0)) {
                        sim = similarity(Arrays.asList(x), Arrays.asList(y)); 
        } else {
                throw new IllegalArgumentException("The arguments x and y must be not NULL and either x or y must be non-empty.");
        }
        return sim;
    }*/
    

	public double similaritylist(Set<BigInteger> x, Set<BigInteger> y) {
        if( x.size() == 0 || y.size() == 0 ) {
            return 0.0;
        }
        
        Set<BigInteger> unionXY = new HashSet<BigInteger>();
        unionXY.addAll(x);
        unionXY.addAll(y);
        
        Set<BigInteger> intersectionXY = new HashSet<BigInteger>();
        intersectionXY.addAll(x);
        intersectionXY.retainAll(y);
        return (double) intersectionXY.size() / (double) unionXY.size(); 
    }
    
}