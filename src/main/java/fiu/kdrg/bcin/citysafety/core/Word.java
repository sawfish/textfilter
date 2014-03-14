package fiu.kdrg.bcin.citysafety.core;

public class Word {

	String text;
	double weight;
	
	public Word(String text,double weight) {
		this.text = text;
		this.weight = weight;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
	
}
