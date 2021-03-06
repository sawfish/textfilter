package fiu.kdrg.bcin.citysafety.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.jblas.DoubleMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fiu.kdrg.bcin.citysafety.util.EdgeUtil;

public class ComparisonBrain extends TrainedModel {

	private Logger logger = LoggerFactory.getLogger(ComparisonBrain.class);
	private static String[] disaster = new String[] { "hurricane", "storm",
			"tornado","earthquake"};

	private Map<String, List<Edge>> edgesOfCities;
	private Map<String, List<Edge>> normEdgesByD;
	private Map<String, List<Edge>> normEdgesByE;
	private int numTopics;
	private int eSize = 50;//

	public ComparisonBrain(String cityOne, String cityTwo) {
		super(cityOne, cityTwo);
		edgesOfCities = new HashMap<String, List<Edge>>();
		normEdgesByD = new HashMap<String, List<Edge>>();
		normEdgesByE = new HashMap<String, List<Edge>>();
		loadModel();
		numTopics = topicWeightedWords.size();
	}

	// query basic element about this topic
	public List<Disaster> queryAllDisaster() {

		List<Disaster> ds = new ArrayList<Disaster>();
		for (int i = 0; i < disaster.length; i++) {
			ds.add(new Disaster(i, disaster[i]));
		}

		return ds;
	}

	/**
	 * 
	 * @param eSize
	 *            eSize indicates how many words we will extract.remember eSize
	 *            should be less than word size. Here we will skip this check.
	 * @return
	 */
	public List<Effect> queryAllEffect(int eSize) {

		List<Effect> effects = new ArrayList<Effect>();

		for (Map.Entry<Integer, Map<String, Double>> en : getTopicWeightedWords()
				.entrySet()) {

			int did = en.getKey();
			Map<String, Double> words = en.getValue();
			List<Double> prob = new ArrayList<Double>();
			for (Map.Entry<String, Double> en2 : words.entrySet()) {
				prob.add(en2.getValue());
			}
			Collections.sort(prob, Collections.reverseOrder());

			Double threshold = prob.get(eSize - 1);
			List<Word> tWords = new ArrayList<Word>();
			int count = 0;
			for (Map.Entry<String, Double> en2 : words.entrySet()) {
				if (count >= eSize)
					break;
				if (en2.getValue() >= threshold) {
					tWords.add(new Word(en2.getKey(), en2.getValue()));
					count++;
				}
			}

			effects.add(new Effect(did, tWords));

		}

		return effects;
	}

	// default eSize 50
	public List<Effect> queryAllEffect() {
		return queryAllEffect(eSize);
	}

	// queryInstances API, return empty size array if none
	/**
	 * Function query instances according disaster id and effect id. if
	 * disasterIdx == -1, filtering will not be imposed upon disaster, same for
	 * effect. If both equals to -1, it will query all instances about one city.
	 * 
	 * @param city
	 * @param disasterIdx
	 *            -1 means no disaster node select
	 * @param topicIdx
	 *            -1 means no topic node select
	 * @return A list of instances filtered by disaster and effect
	 */
	public List<Instance> queryInstances(String city, int disasterIdx,
			int topicIdx) {

		// some special case we take care of.
		if (disasterIdx == -1 || topicIdx == -1) {
			if (disasterIdx == -1 && topicIdx == -1) {
				return queryInstances(city);
			} else if (disasterIdx == -1) {
				return queryInstancesByTopic(city, topicIdx);
			} else {
				return queryInstancesByDisaster(city, disasterIdx);
			}
		}

		DoubleMatrix docTopicM = getCityDocsWeightedTopicsMatrix(city);
		List<Instance> insts = queryInstancesByDisaster(city, disasterIdx);
//		 int[][] sortIdx = docTopicM.rowSortingPermutations();

		return filterInstanceByTopic(insts, docTopicM, topicIdx);

	}

	public List<Instance> queryInstancesByDisaster(String city, int disasterIdx) {

		return filterInstanceByKeyWords(queryInstances(city),
				disaster[disasterIdx]);
	}

	public List<Instance> queryInstancesByTopic(String city, int topicIdx) {

		DoubleMatrix docTopicM = getCityDocsWeightedTopicsMatrix(city);
		List<Instance> insts = queryInstances(city);
//		 int[][] sortIdx = docTopicM.rowSortingPermutations();

		return filterInstanceByTopic(insts, docTopicM, topicIdx);
	}

	public List<Instance> queryInstances(String city) {
		return loadCityInstances(city);
	}

	// queryEdges API, return empty size array if none, return null if none for
	// single Edge
	public static int UNNORMALIZED = 1;
	public static int NORMALIZED_D = 2;
	public static int NORMALIZED_E = 3;
	private int typeOfEdges = NORMALIZED_D;// default value

	/**
	 * @return return map containing edges about two cities.
	 */
	public Map<String, List<Edge>> queryAllEdges() {
		if (edgesOfCities.isEmpty())
			computeAllEdges();

		Map<String, List<Edge>> edges = null;
		switch (typeOfEdges) {
		case 1:
			edges = edgesOfCities;
			break;
		case 2:
			edges = normEdgesByD;
			break;
		case 3:
			edges = normEdgesByE;
			break;

		default:
			edges = edgesOfCities;
			break;
		}

		return edges;
	}

	public List<Edge> queryEdges(String city) {
		return queryAllEdges().get(city);
	}

	public List<Edge> queryEdges(String city, int disaster) {

		List<Edge> edges = queryEdges(city);
		List<Edge> qualifiedEdges = new ArrayList<Edge>();

		for (Edge edge : edges) {
			if (edge.getSource() == disaster) {
				qualifiedEdges.add(edge);
			}
		}

		return qualifiedEdges;
	}

	public List<Edge> queryEdgesByEffect(String city, int topic) {

		List<Edge> edges = queryEdges(city);
		List<Edge> qualifiedEdges = new ArrayList<Edge>();

		for (Edge edge : edges) {
			if (edge.getTarget() == topic) {
				qualifiedEdges.add(edge);
			}
		}

		return qualifiedEdges;
	}

	public Edge queryEdges(String city, int disaster, int topic) {
		List<Edge> edges = queryEdges(city, disaster);

		for (Edge edge : edges) {
			if (edge.getTarget() == topic) {
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
	 * insts and sortIdx must be about same city. Here we only choose instances
	 * which give the highest probability on topic (this might be change to
	 * probability on this topic exceeds certain threshold)
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
			DoubleMatrix docTopicM, int topicIdx) {
		
		int[][] sortIdx = docTopicM.rowSortingPermutations();
		List<Instance> rtn = new ArrayList<Instance>();
		
		for (Instance inst : insts) {
			// int instIdx = inst.getSid();
			// last element of sortIdx[instIdx] corresponds to this instance's
			// most likely topic
			if(usingThresholdApproach){
				if (docTopicM.get(inst.getSid(), topicIdx) >= topicDistThreshold) {
					rtn.add(inst);
				}
			}else{
				for (int calN = 0; calN < maxApproachNumCandidate; calN++) {
					if(sortIdx[inst.getSid()][sortIdx[inst.getSid()].length - calN - 1] == topicIdx){
						rtn.add(inst);
					}
				}
			}
		}

		return rtn;
	}

	/**
	 * This three parameter is very important. It control how the instance and
	 * edge are filtered and generated.
	 */
	double topicDistThreshold = 0.5;
	boolean usingThresholdApproach = false;
	double maxApproachNumCandidate = 2;

	/**
	 * For every city, I first extract all instances related to disasters. for
	 * every disaster, there is an ArrayList<Instance> corresponds to it. for
	 * every instance, Only make contribution to the effect which it most likely
	 * belongs to (This might be changed in the future).
	 */
	private void computeAllEdges() {
		if (!edgesOfCities.isEmpty())
			return;

		// compute cityOne
		DoubleMatrix docTopicCityOne = getCityDocsWeightedTopicsMatrix(cityOne);
		int[][] sortIdxCityOne = docTopicCityOne.rowSortingPermutations();
		Map<Integer, List<Instance>> disasterInstsCityOne = new HashMap<Integer, List<Instance>>();

		for (int i = 0; i < disaster.length; i++) {
			disasterInstsCityOne.put(i, queryInstancesByDisaster(cityOne, i));
		}

		Map<String, Edge> edgesMCityOne = new HashMap<String, Edge>();
		for (Map.Entry<Integer, List<Instance>> entry : disasterInstsCityOne
				.entrySet()) {
			int key = entry.getKey();// disaster id
			List<Instance> value = entry.getValue(); // all related instance
														// about this cityOne on
														// this disaster

			for (Instance inst : value) {

				if (usingThresholdApproach) {

					for (int col = 0; col < docTopicCityOne.columns; col++) {

						if (docTopicCityOne.get(inst.getSid(), col) < topicDistThreshold) {
							continue;
						}

						Edge tmp = new Edge(cityOne, key, col,
								docTopicCityOne.get(inst.getSid(), col));
						if (edgesMCityOne.containsKey(tmp.genRealID())) {
							edgesMCityOne.get(tmp.genRealID()).addWeight(
									tmp.getWeight());
						} else {
							edgesMCityOne.put(tmp.genRealID(), tmp);
						}

					}

				} else {

					for (int calN = 0; calN < maxApproachNumCandidate; calN++) {

						int wEntry = sortIdxCityOne[inst.getSid()][sortIdxCityOne[inst
								.getSid()].length - calN - 1];
						if (docTopicCityOne.get(key, wEntry) <= 0)
							continue;

						Edge tmp = new Edge(cityOne, key, wEntry,
								docTopicCityOne.get(key, wEntry));
						if (edgesMCityOne.containsKey(tmp.genRealID())) {
							edgesMCityOne.get(tmp.genRealID()).addWeight(
									tmp.getWeight());
						} else {
							edgesMCityOne.put(tmp.genRealID(), tmp);
						}

					}
				}

			}

		}
		edgesOfCities.put(cityOne, new ArrayList<Edge>(edgesMCityOne.values()));
		logger.info(String.format("compute edges of %s city done!", cityOne));

		// compute cityTwo
		DoubleMatrix docTopicCityTwo = getCityDocsWeightedTopicsMatrix(cityTwo);
		int[][] sortIdxCityTwo = docTopicCityTwo.rowSortingPermutations();
		Map<Integer, List<Instance>> disasterInstsCityTwo = new HashMap<Integer, List<Instance>>();

		for (int i = 0; i < disaster.length; i++) {
			disasterInstsCityTwo.put(i, queryInstancesByDisaster(cityTwo, i));
		}

		Map<String, Edge> edgesMCityTwo = new HashMap<String, Edge>();
		for (Map.Entry<Integer, List<Instance>> entry : disasterInstsCityTwo
				.entrySet()) {
			int key = entry.getKey();// disaster id
			List<Instance> value = entry.getValue(); // all realted instance
														// about this cityTwo on
														// this disaster

			for (Instance inst : value) {

				if (usingThresholdApproach) {

					for (int col = 0; col < docTopicCityTwo.columns; col++) {

						if (docTopicCityTwo.get(inst.getSid(), col) < topicDistThreshold) {
							continue;
						}

						Edge tmp = new Edge(cityTwo, key, col,
								docTopicCityTwo.get(inst.getSid(), col));
						if (edgesMCityTwo.containsKey(tmp.genRealID())) {
							edgesMCityTwo.get(tmp.genRealID()).addWeight(
									tmp.getWeight());
						} else {
							edgesMCityTwo.put(tmp.genRealID(), tmp);
						}

					}

				} else {

					for (int calN = 0; calN < maxApproachNumCandidate; calN++) {
						
						int wEntry = sortIdxCityTwo[inst.getSid()][sortIdxCityTwo[inst
								.getSid()].length - calN - 1];
						if (docTopicCityTwo.get(key, wEntry) <= 0)
							continue;

						Edge tmp = new Edge(cityTwo, key, wEntry,
								docTopicCityTwo.get(key, wEntry));
						if (edgesMCityTwo.containsKey(tmp.genRealID())) {
							edgesMCityTwo.get(tmp.genRealID()).addWeight(
									tmp.getWeight());
						} else {
							edgesMCityTwo.put(tmp.genRealID(), tmp);
						}
					}

				}

			}
		}
		edgesOfCities.put(cityTwo, new ArrayList<Edge>(edgesMCityTwo.values()));
		logger.info(String.format("compute edges of %s city done!", cityTwo));

		normalizeEdges();
	}

	/**
	 * normalize all edges. There are two direction we do the normalization One:
	 * from disaster viewpoint. Two: from effect viewpoint
	 */
	@SuppressWarnings("unchecked")
	private void normalizeEdges() {

		// normalization from disaster viewpoint
		normEdgesByD.put(cityOne, (List<Edge>) SerializationUtils
				.clone((ArrayList<Edge>) edgesOfCities.get(cityOne)));
		normEdgesByD.put(cityTwo, (List<Edge>) SerializationUtils
				.clone((ArrayList<Edge>) edgesOfCities.get(cityTwo)));

		// cityOne
		Map<Integer, Double> N = new HashMap<Integer, Double>();
		for (Edge e : normEdgesByD.get(cityOne)) {
			int s = e.getSource();
			if (N.containsKey(s)) {
				N.put(s, N.get(s) + e.getWeight());
			} else {
				N.put(s, e.getWeight());
			}
		}
		for (Edge e : normEdgesByD.get(cityOne)) {
			e.setWeight(e.getWeight() / N.get(e.getSource()));
		}
		// cityTwo
		N.clear();
		for (Edge e : normEdgesByD.get(cityTwo)) {
			int s = e.getSource();
			if (N.containsKey(s)) {
				N.put(s, N.get(s) + e.getWeight());
			} else {
				N.put(s, e.getWeight());
			}
		}
		for (Edge e : normEdgesByD.get(cityTwo)) {
			e.setWeight(e.getWeight() / N.get(e.getSource()));
		}
		logger.info("normalization done from disaster view point");

		// normalization from effect viewpoint
		normEdgesByE.put(cityOne, (List<Edge>) SerializationUtils
				.clone((ArrayList<Edge>) edgesOfCities.get(cityOne)));
		normEdgesByE.put(cityTwo, (List<Edge>) SerializationUtils
				.clone((ArrayList<Edge>) edgesOfCities.get(cityTwo)));

		// cityOne
		N.clear();
		for (Edge e : normEdgesByE.get(cityOne)) {
			int s = e.getSource();
			if (N.containsKey(s)) {
				N.put(s, N.get(s) + e.getWeight());
			} else {
				N.put(s, e.getWeight());
			}
		}
		for (Edge e : normEdgesByE.get(cityOne)) {
			e.setWeight(e.getWeight() / N.get(e.getSource()));
		}
		// cityTwo
		N.clear();
		for (Edge e : normEdgesByE.get(cityTwo)) {
			int s = e.getSource();
			if (N.containsKey(s)) {
				N.put(s, N.get(s) + e.getWeight());
			} else {
				N.put(s, e.getWeight());
			}
		}
		for (Edge e : normEdgesByE.get(cityTwo)) {
			e.setWeight(e.getWeight() / N.get(e.getSource()));
		}
		logger.info("normalization done from effect view point");

	}

	public int getNumTopics() {
		return numTopics;
	}

	public int getNumDisasters() {
		return disaster.length;
	}

	public String[] getDisaster() {
		return disaster;
	}

	public void setTypeOfEdges(int typeOfEdges) {
		this.typeOfEdges = typeOfEdges;
	}

	public void setTopicDistThreshold(double topicDistThreshold) {
		this.topicDistThreshold = topicDistThreshold;
	}

	public void setUsingThresholdApproach(boolean usingThresholdApproach) {
		this.usingThresholdApproach = usingThresholdApproach;
	}

	public void setMaxApproachNumCandidate(double maxApproachNumCandidate) {
		this.maxApproachNumCandidate = maxApproachNumCandidate;
	}

	public void seteSize(int eSize) {
		this.eSize = eSize;
	}

	public void computeStat() {

		int numD = getNumDisasters();
		int numT = getNumTopics();
		System.out.println(String.format(
				"number of disaster %d, number of topic %d.", numD, numT));

		// compute instance number level 1
		DoubleMatrix cities = new DoubleMatrix(2, numD);
		for (int d = 0; d < numD; d++) {
			cities.put(0, d, queryInstancesByDisaster(cityOne, d).size());
			cities.put(1, d, queryInstancesByDisaster(cityTwo, d).size());
		}
		System.out.println(cities.toString("%2.0f"));

		// compute instance number
		DoubleMatrix cityOneM = new DoubleMatrix(numD, numT);
		for (int row = 0; row < numD; row++) {
			for (int col = 0; col < numT; col++) {
				cityOneM.put(row, col, queryInstances(cityOne, row, col).size());
			}
		}
		System.out.println(cityOneM.toString("%2.0f"));

		DoubleMatrix cityTwoM = new DoubleMatrix(numD, numT);
		for (int row = 0; row < numD; row++) {
			for (int col = 0; col < numT; col++) {
				cityTwoM.put(row, col, queryInstances(cityTwo, row, col).size());
			}
		}
		System.out.println(cityTwoM.toString("%2.0f"));

		// compute edge weight
		DoubleMatrix cityOneE = new DoubleMatrix(numD, numT);
		for (int row = 0; row < numD; row++) {
			for (int col = 0; col < numT; col++) {
				Edge edge = queryEdges(cityOne, row, col);
				if (edge != null && edge.getWeight() > 0)
					cityOneE.put(row, col, edge.getWeight());
			}
		}
		System.out.println(cityOneE.toString("%3.1f"));

		DoubleMatrix cityTwoE = new DoubleMatrix(numD, numT);
		for (int row = 0; row < numD; row++) {
			for (int col = 0; col < numT; col++) {
				Edge edge = queryEdges(cityTwo, row, col);
				if (edge != null && edge.getWeight() > 0)
					cityTwoE.put(row, col, edge.getWeight());
			}
		}
		System.out.println(cityTwoE.toString("%3.1f"));

	}

	public static void main(String[] args) {

		String cityOne = "los+angeles";
		String cityTwo = "philadelphia";
		ComparisonBrain brain = new ComparisonBrain(cityOne, cityTwo);
		System.out.println(String.format("city %s %s.", cityOne,
				brain.getNumOfcityOneInst()));
		System.out.println(String.format("city %s %s.", cityTwo,
				brain.getNumOfcityTwoInst()));

		brain.setTypeOfEdges(ComparisonBrain.NORMALIZED_D);
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
		if (edge != null)
			System.out.println(edge.toString());
		// EdgeUtil.printEdges(edges);
		brain.computeStat();

	}

}
