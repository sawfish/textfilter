package fiu.kdrg.wikipedia;

import java.util.List;

public abstract class WikiParagraph {
	
	protected String text; 
	
	abstract public boolean isSection();
	abstract public String title();
	public String text() {
		return text;
	}
	abstract public List<WikiParagraph> children();
	
}
