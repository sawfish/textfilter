package kdrg.wikipedia;

import java.util.List;

public class WikiSection extends WikiParagraph {
	
	private String title;
	private List<WikiParagraph> paragraphs;
	
	public WikiSection(String title, String text) {
		this.text = text;
		this.title = title;
		
	}

	@Override
	public boolean isSection() {
		return true;
	}

	@Override
	public String title() {
		return title;
	}

	@Override
	public List<WikiParagraph> children() {
		return paragraphs;
	}

}
