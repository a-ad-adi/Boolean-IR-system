package html_parser;

import java.io.File;

import javax.xml.bind.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.events.Attribute;

import org.w3c.dom.Attr;

public class GenerateXML {
	private static DocumentBuilderFactory dbf;
	private static DocumentBuilder db;
	private static Element elem;
	private static Attribute attr;
	
	
	public static Boolean generateXmlFile() {
		try {
			//File xmlFile = new File(AssignmentP1.baseDir + "\\lexicon.txt");
			
			generateXmlStructure();
		}catch(Exception e) {
			
		}
		return true;
	}
	
	public static void generateXmlStructure() {
		
		try {
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch(Exception e) {
			
		}
	}
}
