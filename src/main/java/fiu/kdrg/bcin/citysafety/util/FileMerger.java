package fiu.kdrg.bcin.citysafety.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author zhouwubai
 * @date Mar 19, 2014
 * @email zhouwubai@gmail.com
 * Apache Licence 2.0
 * 
 * miami-chicago
 * miami-los angeles
 * miami-philadelphia
 * chicago-los angeles
 * chicago-philadelphia
 * los angles-philadelphia
 * 
 */
public class FileMerger {

	private static Logger logger = LoggerFactory.getLogger(FileMerger.class);
	
	public static void main(String[] args) {
		
		String cityOne = "los+angeles";
		String cityTwo = "philadelphia";
		
		String input1 = String.format(Constants.dataBaseUrl + "disaster-%s-NER.txt", cityOne);
		String input2 = String.format(Constants.dataBaseUrl + "disaster-%s-NER.txt", cityTwo);
		String output = String.format(Constants.dataBaseUrl + "%s-%s-disasters-NER.txt", cityOne,cityTwo);
		
		FileMerger fileMerger = new FileMerger();
		fileMerger.merge(input1, input2, output);
		
	}
	
	
	
	public void merge(String input1,String input2,String output){
		
		BufferedReader br1 = null;
		BufferedReader br2 = null;
		BufferedWriter bw = null;
		
		try {
			br1 = new BufferedReader(new FileReader(input1));
			br2 = new BufferedReader(new FileReader(input2));
			bw = new BufferedWriter(new FileWriter(output));
			String line = "";
			
			while((line = br1.readLine()) != null){
				bw.append(line);
				bw.newLine();
			}
			
			while((line = br2.readLine()) != null){
				bw.append(line);
				bw.newLine();
			}
			
			bw.flush();
			
			br1.close();
			br2.close();
			bw.close();
			logger.info("merge file done!");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
