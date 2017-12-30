package html_parser;

public class PostingFile {
private String docId;
private int termFreq;


public PostingFile(String docId, int termFreq) {
	super();
	this.docId = docId;
	this.termFreq = termFreq;
}
public String getDocId() {
	return docId;
}
public void setDocId(String docId) {
	this.docId = docId;
}
public int getTermFreq() {
	return termFreq;
}
public void setTermFreq(int termFreq) {
	this.termFreq = termFreq;
}
}
