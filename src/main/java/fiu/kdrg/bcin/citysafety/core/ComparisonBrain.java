package fiu.kdrg.bcin.citysafety.core;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComparisonBrain extends TrainedModel{

  private Logger logger = LoggerFactory.getLogger(ComparisonBrain.class);
  
  
  public ComparisonBrain(String cityOne, String cityTwo) {
    super(cityOne, cityTwo);
    loadModel();
  }
  
  
  //following are some function struts I will implement
  
  
  public List<Instance> queryInstances(String city, int disaster, int topic){
    return null;
  }
  
  public List<Instance> queryInstances(String city,int disaster){
    return null;
  } 

  public List<Instance> queryInstances(String city){
    return null;
  }
  
  
  public List<Edge> computeEdges(List<Instance> insts){
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
  
}
