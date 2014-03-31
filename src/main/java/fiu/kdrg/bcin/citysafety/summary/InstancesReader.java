package fiu.kdrg.bcin.citysafety.summary;

import java.util.List;

import fiu.kdrg.bcin.citysafety.core.Instance;
import fiu.kdrg.textmining.data.CorpusReader;
import fiu.kdrg.textmining.data.Document;

/**
 * This reader consider every sentence as single document, moreover, it reads
 * sentence iterating over a list of sentences initialized by constructor.
 * 
 * @author zhouwubai
 * 
 */
public class InstancesReader implements CorpusReader {

  private List<Instance> ss;
  private int counter;

  public InstancesReader(List<Instance> ss) {
    this.ss = ss;
    counter = 0;
  }

  @Override
  public Document next() {
    // TODO Auto-generated method stub
    if (counter <= ss.size()) {
      Document doc = new Document(ss.get(counter).getText(), String.format(
          "%s_%d", ss.get(counter).getCity(), ss.get(counter).getSid()));
      counter++;
      return doc;
    }
    
    return null;
  }

}
