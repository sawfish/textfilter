package fiu.kdrg.bcin.citysafety.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jblas.DoubleMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComparisonBrain extends TrainedModel{

  private Logger logger = LoggerFactory.getLogger(ComparisonBrain.class);
  private static String[] disaster = new String[]{"hurricane","storm","tornado"};
  
  private Map<String,List<Edge>> edgesOfCities;
  private int numTopics;
  
  
  public ComparisonBrain(String cityOne, String cityTwo) {
    super(cityOne, cityTwo);
    edgesOfCities = new HashMap<String, List<Edge>>();
    loadModel();
    numTopics = topicWeightedWords.size();
  }
  
  
  
  public List<Instance> queryInstances(String city, int disasterIdx, int topicIdx){
    DoubleMatrix docTopicM = getCityDocsWeightedTopicsMatrix(city);
    List<Instance> insts = queryInstances(city, disasterIdx);
    int[][] sortIdx = docTopicM.rowSortingPermutations();

    return filterInstanceByTopic(insts, sortIdx, topicIdx);
    
  }
  
  
  public List<Instance> queryInstances(String city,int disasterIdx){
    return filterInstanceByKeyWords(queryInstances(city), disaster[disasterIdx]);
  } 

  
  public List<Instance> queryInstances(String city){
    return loadCityInstances(city);
  }
  
  
  
  
  public List<Edge> computeEdges(String city){
    return null;
  }
  
  
  
  public List<Edge> queryEdges(List<Edge> edges, String city, int disaster, int topic){
    return null;
  }
  
  
  private void loadModel(){
    
    loadCityInstances(cityOne);
    loadCityInstances(cityTwo);
    logger.info(String.format("Done loading instance for cities %s and %s", cityOne,cityTwo));
    getCityDocsWeightedTopics(cityOne);
    getCityDocsWeightedTopics(cityTwo);
    logger.info(String.format("Done loading docs weighted topics for cities %s and %s", cityOne,cityTwo));
    
  }
  
  
  private List<Instance> filterInstanceByKeyWords(List<Instance> insts, String keyword){
    List<Instance> rtn = new ArrayList<Instance>();
    for(Instance inst : insts){
      if(StringUtils.containsIgnoreCase(inst.getText(), keyword))
        rtn.add(inst);
    }
    return rtn;
  }
  
  
  
  /**
   * insts and sortIdx must be about same city.
   * @param insts instances about one city
   * @param sortIdx sorted topic distribution about this city
   * @param topicIdx
   * @return  a list of instance which is most likely about topic indexed by topicIdx
   */
  private List<Instance> filterInstanceByTopic(List<Instance> insts, int[][] sortIdx, int topicIdx){
    
    List<Instance> rtn = new ArrayList<Instance>();
    
    for(Instance inst : insts){
      int instIdx = inst.getSid();
      //last element of sortIdx[instIdx] corresponds to this instance's most likely topic
      if(sortIdx[instIdx][sortIdx[instIdx].length - 1] == topicIdx){
        rtn.add(inst);
      }
    }
    
    return rtn;
  }
  
  
  
  private void computeAllEdges(){
    if(!edgesOfCities.isEmpty())
      return;
    
    //compute cityOne
    DoubleMatrix docTopicM = getCityDocsWeightedTopicsMatrix(cityOne);
    List<Instance> insts = loadCityInstances(cityOne);
    
    int[][] sortIdx = docTopicM.rowSortingPermutations();
    Map<Integer, List<Instance>> disasterInstsCityOne = new HashMap<Integer, List<Instance>>();
    List<Edge> cityOneEdges = new ArrayList<Edge>();
    
    for(int i = 0; i < disaster.length; i++){
      disasterInstsCityOne.put(i, queryInstances(cityOne, i));
    }
    
    
    for(Map.Entry<Integer, List<Instance>> entry : disasterInstsCityOne.entrySet()){
      int key = entry.getKey();
      List<Instance> value = entry.getValue();
      
      
      
    }
  }
  
  
  
  
  
//  /**
//   * insts and sortIdx must be about same city.
//   * @param insts instances about one city
//   * @param sortIdx sorted topic distribution about this city
//   * @param dIdx
//   * @param tIdx
//   * @return  Edge between dIdx and tIdx, null if does not exist. 
//   */
//  private Edge computeEdge(List<Instance> insts, int[][] sortIdx, int dIdx, int tIdx){
//    
//    List<Instance> dInsts = filterInstanceByKeyWords(insts,);
//    
//    for(Instance inst : insts){
//      int instIdx = inst.getSid();
//      //last element of sortIdx[instIdx] corresponds to this instance's most likely topic
//      if(sortIdx[instIdx][sortIdx[instIdx].length - 1] == tIdx){
//        rtn.add(inst);
//      }
//    }
//    
//    
//  }




  public int getNumTopics() {
    return numTopics;
  }
  
}
