package fiu.kdrg.bcin.citysafety.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jblas.DoubleMatrix;

public class MathUtil {

	public static DoubleMatrix transformMapToMatrix(
			Map<Integer, Map<Integer, Double>> mapMatrix) {

		List<Integer> sortedKeys = new ArrayList<Integer>(mapMatrix.keySet());
		Collections.sort(sortedKeys);
		double[][] dm = new double[sortedKeys.get(sortedKeys.size() - 1) + 1][];

		for (Map.Entry<Integer, Map<Integer, Double>> entryOne : mapMatrix
				.entrySet()) {

			int key = entryOne.getKey();
			Map<Integer, Double> value = entryOne.getValue();
			sortedKeys = new ArrayList<Integer>(value.keySet());
			Collections.sort(sortedKeys);
			dm[key] = new double[sortedKeys.get(sortedKeys.size() - 1) + 1];

			for (Map.Entry<Integer, Double> entryTwo : value.entrySet()) {
				dm[key][entryTwo.getKey()] = entryTwo.getValue();
			}

		}

		return (new DoubleMatrix(dm));
	}

	public static final double log2 = Math.log(2);

	
	
	
	// calculate Kullback–Leibler divergence distance
	/**
     * Returns the KL divergence, K(p1 || p2).
     *
     * The log is w.r.t. base 2. <p>
     *
     * *Note*: If any value in <tt>p2</tt> is <tt>0.0</tt> then the KL-divergence
     * is <tt>infinite</tt>. Limin changes it to zero instead of infinite. 
     * 
     */
	public static double klDivergence(double[] p1, double[] p2) {

		double klDiv = 0.0;

		for (int i = 0; i < p1.length; ++i) {
			if (p1[i] == 0) {
				continue;
			}
			if (p2[i] == 0.0) {
				continue;
			} 

			klDiv += p1[i] * Math.log(p1[i] / p2[i]);
		}

		return klDiv / log2;
	}

}
