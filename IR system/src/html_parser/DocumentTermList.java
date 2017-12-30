package html_parser;

import java.util.HashMap;

public class DocumentTermList {	
private HashMap<String, Integer> termList;

public DocumentTermList(HashMap<String, Integer> termList) {
	super();
	this.termList = termList;
}
public HashMap<String, Integer> getTermList() {
	return termList;
}
public void setTermList(HashMap<String, Integer> termList) {
	this.termList = termList;
}

}
