package fiu.kdrg.wikipedia;

import java.util.List;

public class WikiTextParagraph extends WikiParagraph {
 
	public WikiTextParagraph(String text) {
		this.text = text; 
	}
	@Override
	public boolean isSection() {
		return false;
	}

	@Override
	public String title() {
		return null;
	}

	@Override
	public List<WikiParagraph> children() {
		return null;
	}

}
