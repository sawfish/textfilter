package fiu.kdrg.wikipedia;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class WikiPage {
	private String title;
	private String text;
	
	List<WikiParagraph> paragraphs;
	
	public WikiPage(String title, String text) {
		this.title = title;
		this.text = text;
		paragraphs = new ArrayList<WikiParagraph>();
	}
	
	public List<WikiParagraph> getParagraphs() {
		return paragraphs;
	}
	
	public static WikiPage parseWikiPage(String title, String page) {
		WikiPage wikipage = new WikiPage(title, page);
		
		page = page.replaceAll("\r", "");
		page = Pattern.compile("<!--.+?-->", Pattern.DOTALL).matcher(page).replaceAll("");
		page = removeTable(page);
		page = removeBraces(page);
		
		String[] paragraphs = page.split("\n\n");
		for(String paragraph : paragraphs) {
//			paragraph = paragraph.replaceAll("\\{\\{[^\\}]*\\}\\}", "");
			
			paragraph = paragraph.replaceAll("\\*[^\n]\n", "");
			
			paragraph = paragraph.replaceAll("\\[\\[([^\\|\\]\\[]+\\|)?([^\\|\\]\\[]+)\\]\\]", "$2");

			paragraph = paragraph.replaceAll("<ref[^>]*>[^<]*</ref>", "");
			paragraph = paragraph.replaceAll("<ref[^>/]*/>", "");
			
			paragraph = paragraph.replaceAll("\\[\\[Image:[^\\]]+\\]\\]", "");
			paragraph = paragraph.replaceAll("\\[\\[File:[^\\]]+\\]\\]", "");
			
			
			
			
			
			String[] lines = paragraph.split("\n");
			StringBuilder sb = new StringBuilder();
			for(String line : lines) {
				if (line.startsWith("=="))
					continue;
				if (line.startsWith("*"))
					continue;
				if (line.startsWith("; "))
					continue;
				if (line.startsWith("| "))
					continue;
				if (line.matches("\\w+\\:.+"))
					continue;
				
				line = line.replaceAll("&nbsp;", " ");
				if (line.length() > 30)
					sb.append(line + " ");
			}
			if (sb.length() > 100) {
				paragraph = sb.toString();
				paragraph = paragraph.replaceAll("'''", "\"");
				paragraph = paragraph.replaceAll("''", "\"");
				paragraph = paragraph.replaceAll("<[^>]+>", "");
				paragraph = paragraph.trim();
				wikipage.paragraphs.add(new WikiTextParagraph(paragraph));
			}
		}
		

//		String[] lines = page.split("\n");
//		
//		String buffer = "";
//		int level = 0;
//		for(int i = 0; i < lines.length; i++) {
//			if (buffer.length() == 0 && lines[i].startsWith("{{")) {
//				while(i < lines.length) {
//					if (lines[i].contains("}}"))
//						break;
//					i++;
//				}
//			} else if (lines[i].trim().length() != 0) {
//				buffer += lines[i];
//			} else if (level == 0) {
//				
//			}
//		}
		
		return wikipage;
	}

	private static String removeTable(String page) {
		StringBuilder sb = new StringBuilder(page.length());
		int braceNumIn = 0;
		for(int i = 0; i < page.length(); i++) {
			char c = page.charAt(i);
			if (c == '{' && (i < page.length() - 1 && page.charAt(i+1) == '|')) {
				i++;
				braceNumIn++;
			} else if (c == '|' && (i < page.length() - 1 && page.charAt(i+1) == '}')) {
				i++;
				braceNumIn--;
			} else if (braceNumIn == 0) 
				sb.append(c);
		}
		return sb.toString();
	}

	private static String removeBraces(String paragraph) {
		StringBuilder sb = new StringBuilder(paragraph.length());
		int braceNumIn = 0;
		for(int i = 0; i < paragraph.length(); i++) {
			char c = paragraph.charAt(i);
			if (c == '{' && (i < paragraph.length() - 1 && paragraph.charAt(i+1) == '{')) {
				i++;
				braceNumIn++;
			} else if (c == '}' && (i < paragraph.length() - 1 && paragraph.charAt(i+1) == '}')) {
				i++;
				braceNumIn--;
			} else if (braceNumIn == 0) 
				sb.append(c);
		}
		return sb.toString();
	}

}
