package fiu.kdrg.bcin.citysafety;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import fiu.kdrg.textmining.data.CorpusReader;
import fiu.kdrg.textmining.data.Document;
import fiu.kdrg.textmining.data.DocumentCorpus;
import fiu.kdrg.textmining.data.Sentence;
import fiu.kdrg.textmining.data.SentenceCorpus;
import fiu.kdrg.textmining.data.TextInstance;
import fiu.kdrg.textmining.pipe.NGram2FeatureVectorNoStopwords;
import fiu.kdrg.textmining.pipe.Pipe;
import fiu.kdrg.textmining.pipe.Pipes;
import fiu.kdrg.textmining.pipe.SentenceSplitterByOpenNLP;
import fiu.kdrg.textmining.pipe.TokenizerByOpenNLP;

public class CityCorpusReader implements CorpusReader {
  
  public static void main(String[] args) throws Exception {
    DocumentCorpus miami = Comparison.loadDocumentCorpus("miami", "disaster,storm,destroyed,damage,earthquake");

    Pipes pipe = new Pipes(new SentenceSplitterByOpenNLP(), new TokenizerByOpenNLP());
    
    BufferedWriter bw = new BufferedWriter(new FileWriter("disaster-miami.txt"));
    pipe.process(miami);
    for(TextInstance doc : miami.getTextInstances()) {
      for(Sentence sent : ((Document) doc).getSentences()) {
        bw.write(sent.getText() + "\n");
      }
      bw.write("\n");
    }
    bw.close();
  }

  private List<Integer> relatedTopics = new ArrayList<Integer>();
  int n = 0;
  BufferedReader brDocTopics;
  BufferedReader brDocs;
  public CityCorpusReader(String dir, String city, String query) throws Exception {
    city = city.replaceAll(" ", "_");
    
    List<Pattern> keywords = new ArrayList<Pattern>();
    for(String q :query.split(",")) {
      keywords.add(Pattern.compile("\\b" + q + "\\b"));
    }
    String topicFile = String.format("%s/wiki-%s.topics", dir, city);
    BufferedReader br = new BufferedReader(new FileReader(topicFile));
    String line;
    while((line = br.readLine()) != null) {
      String[] flds = line.split("\t");
      if (flds.length < 3)
        continue;
      
      boolean match = false;
      for(Pattern keyword :keywords) {
        if (keyword.matcher(flds[2]).find()) {
          match = true;
          break;
        }
      }
      if (match) {
        relatedTopics.add(Integer.parseInt(flds[0]));
      }
    }
    br.close();
    
    String doctoipcFile = String.format("%s/wiki-%s.doctopics", dir, city);
    String docFile = String.format("%s/wiki-%s.txt", dir, city);
    
    
    brDocTopics = new BufferedReader(new FileReader(doctoipcFile));
    brDocTopics.readLine();
//    br.close();
    
    brDocs = new BufferedReader(new FileReader(docFile));
//    br.close();
    
    
  }
  @Override
  public Document next() {
    String line;
    try {
      while(n < 100 && (line = brDocTopics.readLine()) != null) {
        String[] flds = line.split("\t");
        String line2 = brDocs.readLine();
        boolean match = false;
        for(int i = 2; i < flds.length; i+=2) {
          if (relatedTopics.contains(Integer.parseInt(flds[i])) && Double.parseDouble(flds[i+1]) > 0.1) {
            match = true;
            break;
          }
        }
        if (match) {
          n++;
          return new Document(line2.substring(line2.indexOf('\t') + 1 ), "");
        }
      }
    } catch (NumberFormatException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return null;
  }

}
