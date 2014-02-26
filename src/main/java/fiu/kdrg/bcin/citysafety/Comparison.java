package fiu.kdrg.bcin.citysafety;

import java.io.*;

import fiu.kdrg.textmining.data.*;
import fiu.kdrg.textmining.pipe.NGram2FeatureVectorNoStopwords;
import fiu.kdrg.textmining.pipe.Pipe;
import fiu.kdrg.textmining.pipe.Pipes;
import fiu.kdrg.textmining.pipe.SentenceSplitterByOpenNLP;
import fiu.kdrg.textmining.pipe.TokenizerByOpenNLP;
import fiu.kdrg.textmining.summ.comparative.ComparativeSummary;
import fiu.kdrg.textmining.summ.comparative.ConceptBasedILP;

public class Comparison {
  public static void main(String[] args) throws Exception {
    String[] cities = new String[]{"miami", "new york", "san francisco", "los angeles", "boston", "seattle", "atlanta", "new orleans", "chicago", "houston", "philadelphia", "dallas"};
    
    for(String city : cities) {
      if (city.equals("miami"))
        continue;
      
      DocumentCorpus miami = loadDocumentCorpus("miami", "disaster,storm,destroyed,damage,earthquake");
      DocumentCorpus cityDocs = loadDocumentCorpus(city, "disaster,storm,destroyed,damage,earthquake");
      ConceptBasedILP compSumm = new ConceptBasedILP();
      ComparativeSummary sum = compSumm.summarize(miami, cityDocs);
      BufferedWriter bw = new BufferedWriter( new FileWriter("miami VS "+ city + ".result"));
      bw.write(sum.toString());
      bw.close();
    }
  }
  
  public static SentenceCorpus loadSentenceCorpus(String city, String query) throws Exception {
    CorpusReader reader = new CityCorpusReader("./", city, query);
    DocumentCorpus docs = DocumentCorpus.loadACorpus(reader);
    Pipe ngram2Fv = new NGram2FeatureVectorNoStopwords(1);
    Pipes pipe = new Pipes(new SentenceSplitterByOpenNLP(), new TokenizerByOpenNLP(), ngram2Fv);
    pipe.process(docs);
    SentenceCorpus sentences = SentenceCorpus.buildFromDocumentCorpus(docs);
    return sentences;
  }
  
  public static DocumentCorpus loadDocumentCorpus(String city, String query) throws Exception {
    CorpusReader reader = new CityCorpusReader("./", city, query);
    DocumentCorpus docs = DocumentCorpus.loadACorpus(reader);
    return docs;
  }
}
