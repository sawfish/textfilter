package kdrg.wikipedia;

import static org.junit.Assert.*;

import java.io.FileReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.junit.Ignore;
import org.junit.Test;

import fiu.kdrg.wikipedia.WikiPage;
import fiu.kdrg.wikipedia.WikiParagraph;

public class WikiPageTest {
	
	@Test
	public void testParseWikiPageSmall() throws Exception {
		Document document;
    SAXReader saxReader = new SAXReader();
    
    document = saxReader.read(new FileReader("chunk-0001.xml"));
    document.normalize();
    Map<String, String> namespaceUris = new HashMap<String, String>();
    namespaceUris.put("mediawiki",
        "http://www.mediawiki.org/xml/export-0.3/");

    XPath xPath = DocumentHelper.createXPath("//mediawiki:page");
    xPath.setNamespaceURIs(namespaceUris);

    List list = xPath.selectNodes(document);
    Iterator iter = list.iterator();
    
    while (iter.hasNext()) {
      Element page = (Element) iter.next();
      Element textEle = page.element("revision").element("text");
      Element timeEle = page.element("revision").element("timestamp");
      
      if (page.element("redirect") != null)
        continue;
      if (page.element("restrictions") != null)
        continue;
      
      String title = page.elementText("title");
      if (title.startsWith("Wikipedia:"))
        continue;
      
      String text = textEle.getText();
      	
      if (text.startsWith("{{Wiktionary") || text.startsWith("{{wiktionary"))
      	continue;
      
      if (title.equals("Aircraft hijacking")) {
      	int asdf = 1234;
      }
      	
      
			WikiPage wikiPage = WikiPage.parseWikiPage(title, text);
      for(WikiParagraph para : wikiPage.paragraphs) {
      	System.out.println(para.text());
      }
    }
	}

}
