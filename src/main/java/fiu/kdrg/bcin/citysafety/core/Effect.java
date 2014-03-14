package fiu.kdrg.bcin.citysafety.core;

import java.util.List;

public class Effect {

	int id;
	List<Word> words;
	
	
	
	public Effect(int id, List<Word> words) {
		this.id = id;
		this.words = words;
	}
	
	
	
	
	public int getId(){
		return id;
	}

	
	
	public List<Word> getWords() {
		return words;
	}
	
	
	
}
