package fiu.kdrg.bcin.citysafety.core;

/**
 * 
 * @author zhouwubai
 * @date Mar 6, 2014
 * @email zhouwubai@gmail.com Apache Licence 2.0
 */
public class Sentence {

	private String text;
	private String removedEntityText;
	private String city;
	private int sid;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getRemovedEntityText() {
		return removedEntityText;
	}

	public void setRemovedEntityText(String removedEntityText) {
		this.removedEntityText = removedEntityText;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}
	
	
	public String genMALLETStr(){
		return sid + "_" + city + " " + city + " "  + removedEntityText; 
	}
	
	
	public String genSerializedStr(){
		return sid + " " + city + " " + text + " " + removedEntityText;
	}
	
	
	
}
