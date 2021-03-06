package fiu.kdrg.bcin.citysafety.servlet.helper;

import java.util.HashMap;
import java.util.Map;

import fiu.kdrg.bcin.citysafety.core.ComparisonBrain;

public class ModelCache {

	public static Map<String, ComparisonBrain> modelCacher;

	static {
		modelCacher = new HashMap<String, ComparisonBrain>();
	}

	
	
	public static ComparisonBrain query(String cityOne, String cityTwo) {

		if (!modelCacher.containsKey(generateKey(cityOne, cityTwo))) {
			ComparisonBrain newModel = new ComparisonBrain(cityOne, cityTwo);
			
			newModel.setTypeOfEdges(ComparisonBrain.UNNORMALIZED);
			newModel.setUsingThresholdApproach(false);
			newModel.setMaxApproachNumCandidate(2);
			newModel.setTopicDistThreshold(0.6);
			
			
			modelCacher.put(generateKey(cityOne, cityTwo), newModel);
		}
		return modelCacher.get(generateKey(cityOne, cityTwo));
	}

	
	private static String generateKey(String cityOne, String cityTwo) {
		return cityOne + "_" + cityTwo;
	}
	
	
	public static int size(){
		return modelCacher.size();
	}

}
