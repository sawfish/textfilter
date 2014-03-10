package fiu.kdrg.bcin.citysafety.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jblas.DoubleMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fiu.kdrg.bcin.citysafety.util.EdgeUtil;

public class ComparisonBrain extends TrainedModel {

	private Logger logger = LoggerFactory.getLogger(ComparisonBrain.class);
	private static String[] disaster = new String[] { "hurricane", "storm",
			"tornado" };

	private Map<String, List<Edge>> edgesOfCities;
	private int numTopics;

	public ComparisonBrain(String cityOne, String cityTwo) {
		super(cityOne, cityTwo);
		edgesOfCities = new HashMap<String, List<Edge>>();
		loadModel();
		numTopics = topicWeightedWords.size();
	}

	
	
	// queryInstances API
	public List<Instance> queryInstances(String city, int disasterIdx,
			int topicIdx) {
		DoubleMatrix docTopicM = getCityDocsWeightedTopicsMatrix(city);
		List<Instance> insts = queryInstances(city, disasterIdx);
		int[][] sortIdx = docTopicM.rowSortingPermutations();

		return filterInstanceByTopic(insts, sortIdx, topicIdx);

	}

	public List<Instance> queryInstances(String city, int disasterIdx) {
		return filterInstanceByKeyWords(queryInstances(city),
				disaster[disasterIdx]);
	}

	public List<Instance> queryInstances(String city) {
		return loadCityInstances(city);
	}

	
	
	
	//queryEdges API
	public List<Edge> queryEdges(String city) {
		if(edgesOfCities.isEmpty())
			computeAllEdges();
		
		return edgesOfCities.get(city);
	}
	
	
	public List<Edge> queryEdges(String city, int disaster){
		
		List<Edge> edges = queryEdges(city);
		List<Edge> qualifiedEdges = new ArrayList<Edge>();
		
		for(Edge edge : edges){
			if(edge.getSource() == disaster){
				qualifiedEdges.add(edge);
			}
		}
		
		return qualifiedEdges;
	}
	
	
	
	public List<Edge> queryEdges(String city, int disaster, int topic){
		List<Edge> edges = queryEdges(city, disaster);
		List<Edge> qualifiedEdges = new ArrayList<Edge>();
		
		for(Edge edge : edges){
			if(edge.getTarget() == topic){
				qualifiedEdges.add(edge);
			}
		}
		
		return qualifiedEdges;
	}
	
	
	
	
	private void loadModel() {

		loadCityInstances(cityOne);
		loadCityInstances(cityTwo);
		logger.info(String.format("Done loading instance for cities %s and %s",
				cityOne, cityTwo));
		getCityDocsWeightedTopics(cityOne);
		getCityDocsWeightedTopics(cityTwo);
		logger.info(String.format(
				"Done loading docs weighted topics for cities %s and %s",
				cityOne, cityTwo));

	}

	
	
	private List<Instance> filterInstanceByKeyWords(List<Instance> insts,
			String keyword) {
		List<Instance> rtn = new ArrayList<Instance>();
		for (Instance inst : insts) {
			if (StringUtils.containsIgnoreCase(inst.getText(), keyword))
				rtn.add(inst);
		}
		return rtn;
	}

	
	
	/**
	 * insts and sortIdx must be about same city.
	 * 
	 * @param insts
	 *            instances about one city
	 * @param sortIdx
	 *            sorted topic distribution about this city
	 * @param topicIdx
	 * @return a list of instance which is most likely about topic indexed by
	 *         topicIdx
	 */
	private List<Instance> filterInstanceByTopic(List<Instance> insts,
			int[][] sortIdx, int topicIdx) {

		List<Instance> rtn = new ArrayList<Instance>();

		for (Instance inst : insts) {
			int instIdx = inst.getSid();
			// last element of sortIdx[instIdx] corresponds to this instance's
			// most likely topic
			if (sortIdx[instIdx][sortIdx[instIdx].length - 1] == topicIdx) {
				rtn.add(inst);
			}
		}

		return rtn;
	}

	
	
	
	private void computeAllEdges() {
		if (!edgesOfCities.isEmpty())
			return;

		// compute cityOne
		DoubleMatrix docTopicCityOne = getCityDocsWeightedTopicsMatrix(cityOne);
		int[][] sortIdxCityOne = docTopicCityOne.rowSortingPermutations();
		Map<Integer, List<Instance>> disasterInstsCityOne = new HashMap<Integer, List<Instance>>();

		for (int i = 0; i < disaster.length; i++) {
			disasterInstsCityOne.put(i, queryInstances(cityOne, i));
		}

		Map<String, Edge> edgesMCityOne = new HashMap<String, Edge>();
		for (Map.Entry<Integer, List<Instance>> entry : disasterInstsCityOne.entrySet()) {
			int key = entry.getKey();// disaster id
			List<Instance> value = entry.getValue(); // all related instance about this cityOne on this disaster

			for (Instance inst : value) {
				int wEntry = sortIdxCityOne[inst.getSid()][sortIdxCityOne[inst.getSid()].length - 1];
				Edge tmp = new Edge(cityOne, key, wEntry, docTopicCityOne.get(key,wEntry));
				if (edgesMCityOne.containsKey(tmp.genRealID())) {
					edgesMCityOne.get(tmp.genRealID()).addWeight(tmp.getWeight());
				} else {
					edgesMCityOne.put(tmp.genRealID(), tmp);
				}
			}

		}
		edgesOfCities.put(cityOne, new ArrayList<Edge>(edgesMCityOne.values()));
		logger.info(String.format("compute edges of %s city done!", cityOne));
		
		
		//compute cityTwo
		DoubleMatrix docTopicCityTwo = getCityDocsWeightedTopicsMatrix(cityTwo);
		int[][] sortIdxCityTwo = docTopicCityTwo.rowSortingPermutations();
		Map<Integer,List<Instance>> disasterInstsCityTwo = new HashMap<Integer, List<Instance>>();
		
		for (int i = 0; i < disaster.length; i++){
			disasterInstsCityTwo.put(i, queryInstances(cityTwo, i));
		}
		
		Map<String,Edge> edgesMCityTwo = new HashMap<String, Edge>();
		for(Map.Entry<Integer, List<Instance>> entry : disasterInstsCityTwo.entrySet()){
			int key = entry.getKey();//disaster id
			List<Instance> value = entry.getValue(); // all realted instance about this cityTwo on this disaster
			
			for(Instance inst : value){
				int wEntry = sortIdxCityTwo[inst.getSid()][sortIdxCityTwo[inst.getSid()].length - 1];
				Edge tmp = new Edge(cityTwo, key, wEntry, docTopicCityTwo.get(key, wEntry));
				if (edgesMCityTwo.containsKey(tmp.genRealID())){
					edgesMCityTwo.get(tmp.genRealID()).addWeight(tmp.getWeight());
				} else {
					edgesMCityTwo.put(tmp.genRealID(), tmp);
				}
			}
		}
		edgesOfCities.put(cityTwo, new ArrayList<Edge>(edgesMCityTwo.values()));
		logger.info(String.format("compute edges of %s city done!", cityTwo));
	}

	
	
	
	
	public int getNumTopics() {
		return numTopics;
	}
	
	
	public int getNumDisasters(){
		return disaster.length;
	}

	
	
	public static void main(String[] args) {
		
		
		String cityOne = "miami";
		String cityTwo = "chicago";
		ComparisonBrain brain = new ComparisonBrain(cityOne, cityTwo);
		
		List<Edge> edges = brain.queryEdges(cityOne);
		System.out.println(edges.size());
		EdgeUtil.printEdges(edges);
		
		edges = brain.queryEdges(cityTwo);
		System.out.println(edges.size());
		EdgeUtil.printEdges(edges);
		
		edges = brain.queryEdges(cityOne, 0);
		System.out.println(edges.size());
		EdgeUtil.printEdges(edges);
		
		edges = brain.queryEdges(cityOne, 0, 0);
		System.out.println(edges.size());
		EdgeUtil.printEdges(edges);
		
		
	}
	
	
	
	
}
