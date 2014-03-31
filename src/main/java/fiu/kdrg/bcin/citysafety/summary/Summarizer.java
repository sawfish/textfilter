package fiu.kdrg.bcin.citysafety.summary;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fiu.kdrg.bcin.citysafety.core.ComparisonBrain;
import fiu.kdrg.bcin.citysafety.core.Instance;
import fiu.kdrg.bcin.citysafety.servlet.helper.ModelCache;
import fiu.kdrg.textmining.data.DocumentCorpus;
import fiu.kdrg.textmining.data.Sentence;
import fiu.kdrg.textmining.summ.comparative.CompAlg;
import fiu.kdrg.textmining.summ.comparative.ConceptBasedILP;

public class Summarizer {

  private Logger logger = LoggerFactory.getLogger(Summarizer.class);
  private List<String> summaries = new ArrayList<String>();
  
  
  /**
   * 
   * @param corpusOne
   * @param corpusTwo
   * @return
   *    A array which first element is summarization for corpusOne, second is for corpusTwo
   */
  public List<String> summarize(List<Instance> corpusOne, List<Instance> corpusTwo){
    if(summaries.size() == 2)
      return summaries;
    
    if(corpusOne.size() == 0 || corpusTwo.size() == 0){
      summaries.add("No Comparative Information.");
      summaries.add("No Comparative Information.");
    }
    
    DocumentCorpus a = DocumentCorpus.loadACorpus(new InstancesReader(corpusOne));
    DocumentCorpus b = DocumentCorpus.loadACorpus(new InstancesReader(corpusTwo));
    
    CompAlg comp = new ConceptBasedILP();
    extractSummaries(comp.summarize(a, b).toString());
    
    return summaries;
  } 
  
  
  
  private String summarizeInstance(List<Instance> insts){
    
    if(insts.isEmpty()) return "No Such Information!";
    StringBuilder sb = new StringBuilder();
    for(Instance inst : insts){
      sb.append(inst.getText());
    }
    
    // for test, only show the first 200 character.
    logger.info("summarize done for one corpus!");
    if(sb.toString().length() >= 500)
    	return sb.toString().substring(0,200);
    else
    	return sb.toString();
  }
  
  
  private String stritifySummary(List<Sentence> summary){
    String sum = "";
    for(Sentence se : summary){
      sum += se.getText() + "\n";
    }
    
    return sum;
  }
  
  
  private void extractSummaries(String ss){
    
    String[] sum = ss.split("\n\n");
    if(sum.length != 2){
      logger.info("something wrong here");
      summaries.add("No Comparative Information.");
      summaries.add("No Comparative Information.");
      return;
    }
    
    summaries.add(sum[0]);
    summaries.add(sum[1]);
  }
  
  
  
  public static void main(String[] args) {
	
	  	String cityOne = "miami";
	    String cityTwo = "chicago";
	    String dID = "1";
	    String eID = "2";
	    
	    
	    ComparisonBrain brain = ModelCache.query(cityOne, cityTwo);
	    List<Instance> cityOneInsts = null;
	    List<Instance> cityTwoInsts = null;
	    
	    if(dID.isEmpty() && eID.isEmpty()){
	      cityOneInsts = brain.queryInstances(cityOne);
	      cityTwoInsts = brain.queryInstances(cityTwo);
	    }else if(dID.isEmpty() || eID.isEmpty()){
	      
	      //this has been implemented yet
	      if(dID.isEmpty()){
	        
	      }
	      
	      if(eID.isEmpty()){
	        cityOneInsts = brain.queryInstances(cityOne, Integer.parseInt(dID));
	        cityTwoInsts = brain.queryInstances(cityTwo, Integer.parseInt(dID));
	      }
	      
	    }else{
	      
	      cityOneInsts = brain.queryInstances(cityOne, Integer.parseInt(dID), Integer.parseInt(eID));
	      cityTwoInsts = brain.queryInstances(cityTwo, Integer.parseInt(dID), Integer.parseInt(eID));
	      
	    }
	    
	    List<String> summaries = (new Summarizer()).summarize(cityOneInsts, cityTwoInsts);
	    System.out.println(summaries.get(0));
	    System.out.println();
	    System.out.println(summaries.get(1));
	  
}
  
  
  
  
}
