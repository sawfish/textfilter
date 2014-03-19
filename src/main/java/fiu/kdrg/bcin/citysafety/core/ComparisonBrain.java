package fiu.kdrg.bcin.citysafety.core;

import java.util.ArrayList;
import java.util.Collections;
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

	
	
	
	//query basic element about this topic
	public List<Disaster> queryAllDisaster(){
		
		List<Disaster> ds = new ArrayList<Disaster>();
		for(int i = 0; i < disaster.length; i++){
			ds.add(new Disaster(i, disaster[i]));
		}
		
		return ds;
	}
	
	
	
	
	/**
	 * 
	 * @param eSize eSize indicates how many words we will extract.remember eSize 
	 *  should be less than word size. Here we will skip this check.
	 * @return
	 */
	public List<Effect> queryAllEffect(int eSize){
		
		List<Effect> effects = new ArrayList<Effect>();
		
		for(Map.Entry<Integer, Map<String,Double>> en : getTopicWeightedWords().entrySet()){
			
			int did = en.getKey();
			Map<String,Double> words = en.getValue();
			List<Double> prob = new ArrayList<Double>();
			for(Map.Entry<String, Double> en2 : words.entrySet()){
				prob.add(en2.getValue());
			}
			Collections.sort(prob,Collections.reverseOrder());
			
			Double threshold = prob.get(eSize - 1);
			List<Word> tWords = new ArrayList<Word>();
			int count = 0;
			for(Map.Entry<String, Double> en2 : words.entrySet()){
				if(count >= eSize) break;
				if(en2.getValue() >= threshold){
					tWords.add(new Word(en2.getKey(), en2.getValue()));
					count ++;
				}
			}
			
			effects.add(new Effect(did, tWords));
			
		}
		
		return effects;
	}
	
	
	//default eSize 50
	public List<Effect> queryAllEffect(){
		return queryAllEffect(50);
	}
	
	
	
	// queryInstances API, return empty size array if none
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

	
	
	
	//queryEdges API, return empty size array if none, return null if none for single Edge
	/**
	 * @return
	 * 		return map containing edges about two cities.
	 */
	public Map<String, List<Edge>> queryAllEdges(){
		if(edgesOfCities.isEmpty())
			computeAllEdges();
		
		return edgesOfCities;
	}
	
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
	
	
	
	public Edge queryEdges(String city, int disaster, int topic){
		List<Edge> edges = queryEdges(city, disaster);
		
		for(Edge edge : edges){
			if(edge.getTarget() == topic){
				return edge;
			}
		}
		
		return null;
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
				if(docTopicCityOne.get(key,wEntry) <= 0) continue;
				
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
				if(docTopicCityTwo.get(key, wEntry) <= 0) continue;
				
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

	
	
	public void computeStat(){
		
		int numD = getNumDisasters();
		int numT = getNumTopics();
		System.out.println(String.format("number of disaster %d, number of topic %d.", numD,numT));
		
		//compute instance number level 1
		DoubleMatrix cities = new DoubleMatrix(2,numD);
		for(int d = 0; d < numD; d++){
			cities.put(0, d, queryInstances(cityOne, d).size());
			cities.put(1, d, queryInstances(cityTwo, d).size());
		}
		System.out.println(cities.toString("%2.0f"));
		
		//compute instance number
		DoubleMatrix cityOneM = new DoubleMatrix(numD, numT);
		for(int row = 0; row < numD; row ++){
			for(int col = 0; col < numT; col ++){
				cityOneM.put(row, col, queryInstances(cityOne, row, col).size());
			}
		}
		System.out.println(cityOneM.toString("%2.0f"));
		
		DoubleMatrix cityTwoM = new DoubleMatrix(numD, numT);
		for(int row = 0; row < numD; row ++){
			for(int col = 0; col < numT; col ++){
				cityTwoM.put(row, col, queryInstances(cityTwo, row, col).size());
			}
		}
		System.out.println(cityTwoM.toString("%2.0f"));
		
		
		
		//compute edge weight
		DoubleMatrix cityOneE = new DoubleMatrix(numD, numT);
		for(int row = 0; row < numD; row ++){
			for(int col = 0; col < numT; col ++){
				Edge edge = queryEdges(cityOne, row, col);
				if(edge != null && edge.getWeight() > 0)
					cityOneE.put(row, col, edge.getWeight());
			}
		}
		System.out.println(cityOneE.toString("%3.1f"));
		
		DoubleMatrix cityTwoE = new DoubleMatrix(numD, numT);
		for(int row = 0; row < numD; row ++){
			for(int col = 0; col < numT; col ++){
				Edge edge = queryEdges(cityTwo, row, col);
				if(edge != null && edge.getWeight() > 0)
					cityTwoE.put(row, col, edge.getWeight());
			}
		}
		System.out.println(cityTwoE.toString("%3.1f"));
		
	}
	
	
	public static void main(String[] args) {
		
		
		String cityOne = "los+angeles";
		String cityTwo = "philadelphia";
		ComparisonBrain brain = new ComparisonBrain(cityOne, cityTwo);
		System.out.println(String.format("city %s %s.", cityOne,brain.getNumOfcityOneInst()));
		System.out.println(String.format("city %s %s.", cityTwo,brain.getNumOfcityTwoInst()));
		
		
		List<Edge> edges = brain.queryEdges(cityOne);
		System.out.println(edges.size());
		EdgeUtil.printEdges(edges);
		
		edges = brain.queryEdges(cityTwo);
		System.out.println(edges.size());
		EdgeUtil.printEdges(edges);
		
		edges = brain.queryEdges(cityOne, 0);
		System.out.println(edges.size());
		EdgeUtil.printEdges(edges);
		
		Edge edge = brain.queryEdges(cityOne, 0, 0);
		if(edge != null)
			System.out.println(edge.toString());
//		EdgeUtil.printEdges(edges);
		brain.computeStat();
		
		
	}
	
	
	
	
}
