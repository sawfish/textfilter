package fiu.kdrg.bcin.citysafety.core;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Effect {

	int id;
	List<Word> words;
	
	
	
	public Effect(int id, List<Word> words) {
		this.id = id;
		this.words = words;
		Collections.sort(this.words, new Comparator<Word>() {
		  
		  @Override
		  public int compare(Word o1, Word o2) {
		    // TODO Auto-generated method stub
		    return (new Double(o2.getWeight()).compareTo(o1.getWeight()));
		  }
		  
                });
	}
	
	
	
	
	public int getId(){
		return id;
	}

	
	
	public List<Word> getWords() {
		return words;
	}
	
	
	@Override
	public String toString() {
	  String str = "[";
	  for(Word w : words){
	    str += w.text + " ";
	  }
	  str = str.trim() + "]";
	  return str;
	}
	
}
