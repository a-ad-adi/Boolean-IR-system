package html_parser;

public class Dictionary {
private int docFreq;
private int offset;

public Dictionary(int docFreq, int offset) {
	super();
	this.docFreq = docFreq;
	this.offset = offset;
}

public int getDocFreq() {
	return docFreq;
}
public void setDocFreq(int docFreq) {
	this.docFreq = docFreq;
}
public int getOffset() {
	return offset;
}
public void setOffset(int offset) {
	this.offset = offset;
}
}
