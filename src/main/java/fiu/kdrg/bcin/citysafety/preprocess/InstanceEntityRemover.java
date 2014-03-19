package fiu.kdrg.bcin.citysafety.preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
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
import fiu.kdrg.bcin.citysafety.core.Instance;
import fiu.kdrg.bcin.citysafety.util.Constants;

public class InstanceEntityRemover {

	private static Logger logger = LoggerFactory
			.getLogger(InstanceEntityRemover.class);
	private static StanfordCoreNLP pipeline;

	static {
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
//		props.put("ner.model.3class", "");
//		props.put("ner.model.MISCclass", "");
		pipeline = new StanfordCoreNLP(props);
	}

	
	public static void main(String[] args) {
		
		InstanceEntityRemover entityRemover = new InstanceEntityRemover();
//		preprocessor.removeEntity(Constants.testString);
		String city = "san+francisco";
		String input = String.format(Constants.dataBaseUrl + "disaster-%s.txt", city);
		String mOutput = String.format(Constants.dataBaseUrl + "disaster-%s-NER.txt", city);
		String oOutput = String.format(Constants.dataBaseUrl + "disaster-%s-OBJ.txt", city);
//		entityRemover.removeEntity(input, mOutput);
		entityRemover.removeSentenceEntityWithFormat(input, mOutput, oOutput, city);
		
	}
	
	
	public void removeSentenceEntityWithFormat(String input, 
			String mOutput, String oOutput, String city){
		
		BufferedReader br = null;
		int count = 0;
		
		try{
			br = new BufferedReader(new FileReader(input));
			String line = br.readLine();
			List<Instance> sentences = new ArrayList<Instance>();
			
			while(line != null){
				if(line.trim().isEmpty()){
					line = br.readLine();
					continue;
				}
				
				Instance st = new Instance();
				st.setSid(count++);
				st.setCity(city);
				st.setText(line.trim());
				st.setRemovedEntityText(removeEntity(line).trim());
				
				logger.info(String.format("processing %d sentence of city %s done.", count,city));
				sentences.add(st);
				line = br.readLine();
			}
			
			BufferedWriter mBW = new BufferedWriter(new FileWriter(mOutput));
			BufferedWriter oBW = new BufferedWriter(new FileWriter(oOutput));
			for(Instance sentence : sentences){
				mBW.append(sentence.genMALLETStr());
				oBW.append(sentence.genSerializedStr());
				mBW.append("\n");
				oBW.append("\n");
			}
			
			mBW.flush();
			mBW.close();
			oBW.flush();
			oBW.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void removeEntity(String input, String output){
		
		BufferedReader br = null;
		BufferedWriter bw = null;
		
		try {
			br = new BufferedReader(new FileReader(input));
			bw = new BufferedWriter(new FileWriter(output));
			List<String> paragraphs = new ArrayList<String>();
			String line = br.readLine();
			String paragraph = "";
			
			boolean isNewPara = false;
			
			while(line != null){
				if(line.trim().isEmpty()){
					isNewPara = true;
				}else{
					isNewPara = false;
				}
				
				if(isNewPara){
					paragraphs.add(paragraph);
					paragraph = "";
				}else{
					paragraph += line + "\n";
				}
				line = br.readLine();
			}
			
			logger.info(String.format("paragraph numbers %d:", paragraphs.size()));
			int count = 0;
			for(String para : paragraphs){
				bw.append(removeEntity(para));
//				bw.append("\n");
				logger.info(String.format("paragraph %d done!", ++count));
			}
			
			bw.flush();
			bw.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	

	public String removeEntity(String paragraph) {

		Set<String> entityFilter = new HashSet<String>();
		entityFilter.add(Constants.LOCATION_ENTITY);
		entityFilter.add(Constants.DATE_ENTITY);
		entityFilter.add(Constants.PERSON_ENTITY);
		entityFilter.add(Constants.MISC_ENTITY);
		entityFilter.add(Constants.DURATION_ENTITY);
		entityFilter.add(Constants.NUMBER_ENTITY);
		entityFilter.add(Constants.ORGANIZATION_ENTITY);
		entityFilter.add(Constants.ORDINAL_ENTITY);
		return removeEntity(paragraph, entityFilter);

	}
	
	
	public String removeEntity(String instance, Set<String> entityFilter) {

		Annotation document = new Annotation(instance);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		StringBuilder sb = new StringBuilder();

		for (CoreMap sentence : sentences) {
			String type;
			String sentenceText = sentence.toString();
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				type = token.get(NamedEntityTagAnnotation.class);
//				logger.info(type);
				// remove this token from original text
				if (entityFilter.contains(type)) {
					sentenceText = sentenceText.replace(
							token.get(TextAnnotation.class), "");
				}
			}

			sb.append(sentenceText);
		}
		
		sb.append("\n");
//		System.out.println(sb.toString());
		return sb.toString();
	}

}