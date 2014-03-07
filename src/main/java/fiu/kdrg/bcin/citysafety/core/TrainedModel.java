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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fiu.kdrg.bcin.citysafety.util.Constants;
import fiu.kdrg.bcin.citysafety.util.InstanceUtil;

/**
 * 
 * @author zhouwubai
 * @date Mar 7, 2014
 * @email zhouwubai@gmail.com Apache Licence 2.0 Get topic trained data from
 *        text and parse them for latter use.
 */
public class TrainedModel {

	private static Logger logger = LoggerFactory.getLogger(TrainedModel.class);
	
	private String cityOne;
	private String cityTwo;
	private Map<String, List<Instance>> instOfCities;
	private Map<Integer,Map<String,Double>> topicWeightedWords;
	//number of words in one topic
	private int numTopicWords;

	public TrainedModel(String cityOne, String cityTwo) {
		this.cityOne = cityOne;
		this.cityTwo = cityTwo;
		instOfCities = new HashMap<String, List<Instance>>();
		//default value
		numTopicWords = 100;
	}

	
	
	public List<Instance> loadCityInstances(String city) {
		
		if(!instOfCities.containsKey(city))
			loadInstance();
		
		return instOfCities.get(city);
	}
	
	
	
	public Map<Integer, Map<String, Double>> getTopicWeightedWords() {
		if(topicWeightedWords == null)
			loadTopicWeightedWords();
		
		return topicWeightedWords;
	}
	
	

	
	
	
	
	
	
	
	
	
	
	
	private void loadTopicWeightedWords(){
		
		String path = String.format(Constants.dataBaseUrl 
				+ "%s-%s-topic-word-weights.txt", cityOne, cityTwo);
//		topicWordsMatrix = new DoubleMatrix(numTopicWords);
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			
			String line = "";
			topicWeightedWords= new HashMap<Integer, Map<String,Double>>();
			
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

}
