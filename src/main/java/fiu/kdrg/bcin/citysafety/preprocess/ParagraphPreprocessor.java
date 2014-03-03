package fiu.kdrg.bcin.citysafety.preprocess;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class ParagraphPreprocessor {

        private static Logger logger = LoggerFactory.getLogger(ParagraphPreprocessor.class);
	private static StanfordCoreNLP pipeline;
	
	static{
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
		props.put("ner.model.3class", "");
		props.put("ner.model.MISCclass", "");
		pipeline = new StanfordCoreNLP(props);
	}
	
	
	
	public static void main(String[] args) {
		
	  ParagraphPreprocessor preprocessor = new ParagraphPreprocessor();
	  preprocessor.removeEntity(Constants.testString);
		
	}
	
	
	public String removeEntity(String paragraph){
	  
	  Set<String> entityFilter = new HashSet<String>();
	  entityFilter.add(Constants.LOCATION_ENTITY);
	  return removeEntity(paragraph, entityFilter);
	  
	}
	
	
	public String removeEntity(String paragraph, Set<String> entityFilter){
		
		Annotation document = new Annotation(paragraph);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		StringBuilder sb = new StringBuilder();		
		
		for(CoreMap sentence : sentences){
			String type;
			String sentenceText = sentence.toString();
			for(CoreLabel token: sentence.get(TokensAnnotation.class)){
				type = token.get(NamedEntityTagAnnotation.class);
				logger.info(type);
				//remove this token from original text
				if(entityFilter.contains(type)){
					sentenceText = sentenceText.replace(token.get(TextAnnotation.class) + " ", "");
				}
			}
			
			sb.append(sentenceText);
			sb.append("\t\n");
		}
		
		return sb.toString();
	}
	
}
