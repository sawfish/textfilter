package fiu.kdrg.bcin.citysafety;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import fiu.kdrg.textmining.data.*;
import fiu.kdrg.textmining.pipe.NGram2FeatureVectorNoStopwords;
import fiu.kdrg.textmining.pipe.Pipe;
import fiu.kdrg.textmining.pipe.Pipes;
import fiu.kdrg.textmining.pipe.SentenceSplitterByNewLine;
import fiu.kdrg.textmining.pipe.SentenceSplitterByOpenNLP;
import fiu.kdrg.textmining.pipe.TokenizerByBlank;
import fiu.kdrg.textmining.pipe.TokenizerByOpenNLP;

public class CityCorpusReaderTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testNext() {
    CorpusReader reader;
    try {
      reader = new CityCorpusReader("./", "miami", "disaster,storm,destroyed,damage,earthquake");
      DocumentCorpus miamiDocs = DocumentCorpus.loadACorpus(reader);
      Pipe ngram2Fv = new NGram2FeatureVectorNoStopwords(1);
      Pipes pipe = new Pipes(new SentenceSplitterByOpenNLP(), new TokenizerByOpenNLP(), ngram2Fv);
      pipe.process(miamiDocs);
      SentenceCorpus sentences = SentenceCorpus.buildFromDocumentCorpus(miamiDocs);
      System.out.println(sentences.getTextInstances().size());
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }

}
