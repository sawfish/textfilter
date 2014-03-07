package fiu.kdrg.bcin.citysafety.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fiu.kdrg.bcin.citysafety.core.Instance;

public class InstanceUtil {

	public static Logger logger = LoggerFactory.getLogger(InstanceUtil.class);

	public static Pattern lineP = Pattern
			.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)");

	
	
	public static void main(String[] args) {
		
		String line = "8_miami miami The hurricane continued traveling north-northwest;";
		Instance inst = InstanceUtil.parseNerLine2Inst(line);

		if(inst != null){
			System.out.println(inst.getSid());
			System.out.println(inst.getCity());
//			System.out.println(inst.getText());
			System.out.println(inst.getRemovedEntityText());
		}
	}

	public static Instance parseObjLine2Inst(String line) {

		Matcher m = lineP.matcher(line);
		if (m.find()) {
			Instance inst = new Instance();
			inst.setSid(Integer.parseInt(m.group(1)));
			inst.setCity(m.group(2));
			inst.setText(m.group(3));
			return inst;
		}

		logger.info(line + " parse failed.");
		return null;

	}
	
	
	public static Instance parseNerLine2Inst(String line){
		
		Matcher m = lineP.matcher(line);
		if (m.find()) {
			Instance inst = new Instance();
			inst.setSid(Integer.parseInt(m.group(1).split("_")[0]));
			inst.setCity(m.group(2));
			inst.setRemovedEntityText(m.group(3));
			return inst;
		}

		logger.info(line + " parse failed.");
		return null;
		
	}

}
