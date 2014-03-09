package fiu.kdrg.bcin.citysafety.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jblas.DoubleMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fiu.kdrg.bcin.citysafety.util.Constants;
import fiu.kdrg.bcin.citysafety.util.InstanceUtil;
import fiu.kdrg.bcin.citysafety.util.MatrixUtil;

/**
 * 
 * @author zhouwubai
 * @date Mar 7, 2014
 * @email zhouwubai@gmail.com Apache Licence 2.0 Get topic trained data from
 *        text and parse them for latter use. All indices begin with 0
 */
public class TrainedModel {

	private static Logger logger = LoggerFactory.getLogger(TrainedModel.class);
	
	String cityOne;
	String cityTwo;
	Map<String, List<Instance>> instOfCities;
	Map<Integer,Map<String,Double>> topicWeightedWords;
	Map<String,Map<Integer,Map<Integer,Double>>> weightedTopics;
	int numOfcityOneInst;
        int numOfcityTwoInst;
        
	//number of words in one topic
	private int numTopicWords;
	private Map<Integer,Map<Integer,Double>> cityOneDocWeightedTopics;
        private Map<Integer,Map<Integer,Double>> cityTwoDocWeightedTopics;
//	private int numTopics;
	
	

	public TrainedModel(String cityOne, String cityTwo) {
		this.cityOne = cityOne;
		this.cityTwo = cityTwo;
		instOfCities = new HashMap<String, List<Instance>>();
		topicWeightedWords= new HashMap<Integer, Map<String,Double>>();
		cityOneDocWeightedTopics = new HashMap<Integer, Map<Integer,Double>>();
		cityTwoDocWeightedTopics = new HashMap<Integer, Map<Integer,Double>>();
		weightedTopics = new HashMap<String, Map<Integer,Map<Integer,Double>>>();
		weightedTopics.put(cityOne, cityOneDocWeightedTopics);
		weightedTopics.put(cityTwo, cityTwoDocWeightedTopics);
		//default value
		numTopicWords = 100;
	}


	/**
	 * 
	 * @param city 
	 * @return all sentence instances about this city
	 */
	public List<Instance> loadCityInstances(String city) {
		
		if(!instOfCities.containsKey(city))
			loadInstance();
		
		return instOfCities.get(city);
	}
	
	
	
	/**
	 * 
	 * @return words' distribution of each topic
	 */
	public Map<Integer, Map<String, Double>> getTopicWeightedWords() {
		if(topicWeightedWords.isEmpty())
			loadTopicWeightedWords();
		
		return topicWeightedWords;
	}
	
	
	/**
	 * 
	 * @param city
	 * @return a matrix stores instances' topic distribution. row corresponds to instance,
	 * and column corresponds to topics, value is the weight.
	 */
	public DoubleMatrix getCityDocsWeightedTopicsMatrix(String city){
	  return MatrixUtil.transformMapToMatrix(getCityDocsWeightedTopics(city));
	}
	

	/**
	 * 
	 * @param city
	 * @return a nested map stores instances' topic distribution. first level corresponds to instance,
	 * and second level corresponds to topics, value is the weight.
	 */
	public Map<Integer,Map<Integer,Double>> getCityDocsWeightedTopics(String city){
		if(cityOneDocWeightedTopics.isEmpty() || cityTwoDocWeightedTopics.isEmpty())
			loadDocWeightedTopics();
		
		return weightedTopics.get(city);
	}
	
	
	
	private void loadDocWeightedTopics(){
		
		String path = String.format(Constants.dataBaseUrl + "%s-%s-doc-topics.txt",cityOne,cityTwo);
		int numC1 = getNumOfcityOneInst();
		int numC2 = getNumOfcityTwoInst();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line = br.readLine();
			
			int count = 0;
			while((line = br.readLine()) != null){
				
				if(line.trim().isEmpty()) continue;
				String[] tmp = line.split("\\s+");
				count ++;
				
				if(count <= numC1){
					if(cityOneDocWeightedTopics.containsKey(Integer.parseInt(tmp[0]))){
						for(int i = 1; 2*i < tmp.length; i++){
							cityOneDocWeightedTopics.get(Integer.parseInt(tmp[0]))
								.put(Integer.parseInt(tmp[2*i]), Double.parseDouble(tmp[2*i+1]));
						}
					}else{
						cityOneDocWeightedTopics.put(Integer.parseInt(tmp[0]), new HashMap<Integer, Double>());
						for(int i = 1; 2*i < tmp.length; i++){
							cityOneDocWeightedTopics.get(Integer.parseInt(tmp[0]))
								.put(Integer.parseInt(tmp[2*i]), Double.parseDouble(tmp[2*i+1]));
						}
					}
				}else{
					if(cityTwoDocWeightedTopics.containsKey(Integer.parseInt(tmp[0]) - numC1)){
						for(int i = 1; 2*i < tmp.length; i++){
							cityTwoDocWeightedTopics.get(Integer.parseInt(tmp[0]) - numC1)
								.put(Integer.parseInt(tmp[2*i]), Double.parseDouble(tmp[2*i+1]));
						}
					}else{
						cityTwoDocWeightedTopics.put(Integer.parseInt(tmp[0]) - numC1, new HashMap<Integer, Double>());
						for(int i = 1; 2*i < tmp.length; i++){
							cityTwoDocWeightedTopics.get(Integer.parseInt(tmp[0]) - numC1)
								.put(Integer.parseInt(tmp[2*i]), Double.parseDouble(tmp[2*i+1]));
						}
					}
				}
			}
			
			br.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	
	private void loadTopicWeightedWords(){
		
		String path = String.format(Constants.dataBaseUrl 
				+ "%s-%s-topic-word-weights.txt", cityOne, cityTwo);
//		topicWordsMatrix = new DoubleMatrix(numTopicWords);
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			
			String line = "";
			
			while((line = br.readLine()) != null){
				
				if(line.trim().isEmpty()) continue;
				String[] tmp = line.split("\\s+");
				
				if(tmp.length != 3){
					logger.info("format error!");
					continue;
				}
				
				if(topicWeightedWords.containsKey(Integer.parseInt(tmp[0]))){
					topicWeightedWords.get(Integer.parseInt(tmp[0])).put(tmp[1], Double.parseDouble(tmp[2]));
				}else{
					topicWeightedWords.put(Integer.parseInt(tmp[0]), new TreeMap<String, Double>());
					topicWeightedWords.get(Integer.parseInt(tmp[0])).put(tmp[1], Double.parseDouble(tmp[2]));
				}
			}
			
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private void loadInstance() {

		String cityOnePath = String.format(Constants.dataBaseUrl
				+ "disaster-%s-OBJ.txt", cityOne);
		String cityTwoPath = String.format(Constants.dataBaseUrl
				+ "disaster-%s-OBJ.txt", cityTwo);
		Map<String, Instance> removedNeInsts = loadRemovedEntityInstance();

		try {
			//load instance of cityOne
			BufferedReader brOne = new BufferedReader(new FileReader(
					cityOnePath));
			List<Instance> cityOneInst = new ArrayList<Instance>();

			String line = brOne.readLine();
			while (line != null) {
				Instance inst = InstanceUtil.parseObjLine2Inst(line);
				if (inst != null) {
					inst.setRemovedEntityText(removedNeInsts.get(
							inst.genRealID()).getRemovedEntityText());
					cityOneInst.add(inst);
				}
				line = brOne.readLine();
			}
			
			Collections.sort(cityOneInst, new Comparator<Instance>() {
				@Override
				public int compare(Instance o1, Instance o2) {
					// TODO Auto-generated method stub
					return o1.getSid() - o2.getSid();
				}
			});
			instOfCities.put(cityOne, cityOneInst);
			brOne.close();
			
			//load instance of cityTwo
			BufferedReader brTwo = new BufferedReader(new FileReader(
					cityTwoPath));
			List<Instance> cityTwoInst = new ArrayList<Instance>();

			line = brTwo.readLine();
			while (line != null) {
				Instance inst = InstanceUtil.parseObjLine2Inst(line);
				if (inst != null) {
					inst.setRemovedEntityText(removedNeInsts.get(
							inst.genRealID()).getRemovedEntityText());
					cityTwoInst.add(inst);
				}
				line = brTwo.readLine();
			}
			
			Collections.sort(cityTwoInst, new Comparator<Instance>() {
				@Override
				public int compare(Instance o1, Instance o2) {
					// TODO Auto-generated method stub
					return o1.getSid() - o2.getSid();
				}
			});
			instOfCities.put(cityTwo, cityTwoInst);
			brTwo.close();
			
			numOfcityOneInst = instOfCities.get(cityOne).size();
			numOfcityTwoInst = instOfCities.get(cityTwo).size();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	private Map<String, Instance> loadRemovedEntityInstance() {

		String path = String.format(Constants.dataBaseUrl
				+ "%s-%s-disasters-NER.txt", cityOne, cityTwo);
		Map<String, Instance> instances = new HashMap<String, Instance>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line = br.readLine();
			while (line != null) {
				Instance inst = InstanceUtil.parseNerLine2Inst(line);
				if (inst != null) {
					instances.put(inst.genRealID(), inst);
				}
				line = br.readLine();
			}

			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return instances;
	}
	
	
	public void setNumTopicWords(int numTopicWords) {
		this.numTopicWords = numTopicWords;
	}



	public int getNumOfcityOneInst() {
		if(topicWeightedWords.isEmpty())
			loadTopicWeightedWords();
		
		
		return numOfcityOneInst;
	}



	public int getNumOfcityTwoInst() {
		if(topicWeightedWords.isEmpty())
			loadTopicWeightedWords();
		
		return numOfcityTwoInst;
	}


    public String getCityOne() {
      return cityOne;
    }


    public String getCityTwo() {
      return cityTwo;
    }
	
	
	

}
