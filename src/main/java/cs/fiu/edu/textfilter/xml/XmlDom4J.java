package cs.fiu.edu.textfilter.xml;

import org.dom4j.Document;
import org.dom4j.Element;

import java.util.List;
import java.util.Iterator;

import java.io.*;

import org.dom4j.io.SAXReader;

import cs.fiu.edu.textfilter.custom.Constants;

public class XmlDom4J {

  public void modifyDocument(File inputXml) {

    try {
      SAXReader saxReader = new SAXReader();
      Document document = saxReader.read(inputXml);

      List list = document.selectNodes("//article");
      Iterator iter = list.iterator();
      System.out.println(list.size());
      
      while(iter.hasNext()){
        Element page = (Element)iter.next();
        Element textEle = page.element("revision").element("text");
        System.out.println(textEle.getTextTrim());
      }
      
     System.out.println(list.size());
      System.out.println();
      
    }catch (Exception e) {
      e.printStackTrace();
    }
  }
  

  public static void main(String[] argv) {

    XmlDom4J dom4jParser = new XmlDom4J();
    dom4jParser.modifyDocument(new File(Constants.XML_OUTPUT_PATH));

  }

}
