package fiu.kdrg.bcin.citysafety.core;

import java.util.Map;

public class Effect {

	int id;
	Map<String,Double> words;
	
	
	
	public Effect(int id, Map<String,Double> words) {
		this.id = id;
		this.words = words;
	}
	
	
	
	
	public int getId(){
		return id;
	}

	
	
	public Map<String, Double> getWords() {
		return words;
	}
	
	
	
}
