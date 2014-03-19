package fiu.kdrg.bcin.citysafety.summary;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fiu.kdrg.bcin.citysafety.core.Instance;

public class Summarizer {

  private Logger logger = LoggerFactory.getLogger(Summarizer.class);
  
  
  /**
   * 
   * @param corpusOne
   * @param corpusTwo
   * @return
   *    A array which first element is summarization for corpusOne, second is for corpusTwo
   */
  public List<String> summarize(List<Instance> corpusOne, List<Instance> corpusTwo){
    List<String> summaries = new ArrayList<String>();
    summaries.add(summarizeInstance(corpusOne));
    summaries.add(summarizeInstance(corpusTwo));
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
  
  
}
