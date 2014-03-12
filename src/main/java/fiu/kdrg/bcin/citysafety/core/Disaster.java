package fiu.kdrg.bcin.citysafety.core;

public class Disaster {

	int id;
	String text;
	
	public Disaster(int id, String text) {
		this.id = id;
		this.text = text;
	}
	

	public int getId() {
		return id;
	}

	public String getText() {
		return text;
	}
	
}
