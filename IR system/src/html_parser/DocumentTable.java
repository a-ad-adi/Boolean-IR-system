package html_parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class DocumentTable {

private String title;
private String reviewer;
private String snippet;
private String rate;


public DocumentTable(String title, String reviewer, String snippet, String rate) {
	super();
	this.title = title;
	this.reviewer = reviewer;
	this.snippet = snippet;
	this.rate = rate;
}
public String getTitle() {
	return title;
}
public void setTitle(String title) {
	this.title = title;
}
public String getReviewer() {
	return reviewer;
}
public void setReviewer(String reviewer) {
	this.reviewer = reviewer;
}
public String getSnippet() {
	return snippet;
}
public void setSnippet(String snippet) {
	this.snippet = snippet;
}
public String getRate() {
	return rate;
}
public void setRate(String rate) {
	this.rate = rate;
}

public static void generateDocTable(Elements elementData, String fileName) {
	int i=0,matchedWordCount=0;
	String title="", reviewer="", snippet="", capsule="" , firstPara="", secondPara="";
	String type = "";
	Integer rate = null;
	fileName = fileName.replace(".html", "").replace(AssignmentP2.baseDir, "").replace("\\","").replace("/","");
	//System.out.println("File : " + fileName);
	try {
		while(i < elementData.size()) {
			//find title
			if(title == "" && elementData.get(i).tagName().toLowerCase().equals("title")) {
				title = elementData.get(i).text().toLowerCase();
				//System.out.println("title : " + title);
					
			}
		
			//find reviewer
			if(reviewer == "" && (elementData.get(i).tagName().toLowerCase().equals("h1") || elementData.get(i).tagName().toLowerCase().equals("h2") ||
					elementData.get(i).tagName().toLowerCase().equals("h3") || elementData.get(i).tagName().toLowerCase().equals("h1") ||
					elementData.get(i).tagName().toLowerCase().equals("h5") || elementData.get(i).tagName().toLowerCase().equals("h6")
					)) {
				if(elementData.get(i).text().toLowerCase().contains("reviewer")) {
					reviewer = elementData.get(i).text().toLowerCase().replace("reviewer", "");
					//System.out.println("reviewer : " + reviewer);
					
				}else if(elementData.get(i).text().toLowerCase().contains("reviewed by")) {
					reviewer = elementData.get(i).text().toLowerCase().replace("reviewed by", "");
					//System.out.println("reviewer : " + reviewer);
				}
				
				
			}
			
			//find rateing
			if(rate == null && (elementData.get(i).tagName().toLowerCase().equals("p") || elementData.get(i).tagName().toLowerCase().equals("pre"))) {
				rate = findRating(elementData.get(i).text().toLowerCase());
				if(rate != null)	type = " (scale based)";
			}
			//find capsule/snippet
			if(snippet == "" && elementData.get(i).tagName().toLowerCase().equals("p")) {
				if(elementData.get(i).text().toLowerCase().contains("capsule review")) {
					//System.out.println("review : " + elementData.get(i).text().toLowerCase());
					snippet = elementData.get(i).text();
					capsule = snippet;
				}
				
			}
			//first para
			if(/*title!="" && reviewer!="" && */firstPara == "" && elementData.get(i).tagName().toLowerCase().equals("p")) {
				if(!elementData.get(i).text().toLowerCase().contains("capsule") && !elementData.get(i).text().toLowerCase().contains("reviewer") && 
						!elementData.get(i).text().toLowerCase().contains("reviewed by")	
						) {
					int j=0;
					String[] paraWords = elementData.get(i).text().split(" ");
					while(j < paraWords.length) {
						if(AssignmentP2.ssWord.containsKey(paraWords[j]))	matchedWordCount++;
						j++;
					}
					
					firstPara = elementData.get(i).text();
					//System.out.println("first para : " + firstPara);
/*					if(matchedWordCount >3) {
						firstPara = elementData.get(i).text();
						System.out.println("Taken for snippet : " + firstPara);
						
					}else System.out.println("Paragraph ignored " + matchedWordCount);*/
				}
			}
			//para 2
			if(/*title!="" && reviewer!="" && */secondPara == "" && !firstPara.contains(elementData.get(i).text()) 
					&& elementData.get(i).tagName().toLowerCase().equals("p")) {
				
				if(!elementData.get(i).text().toLowerCase().contains("capsule") && !elementData.get(i).text().toLowerCase().contains("reviewer") && 
						!elementData.get(i).text().toLowerCase().contains("reviewed by")	
						) {
					
					secondPara = elementData.get(i).text(); 
					//System.out.println("second : " + secondPara);
				}
			}
			i++;
		}
		
		if(rate == null) {
			
			if(capsule.contains("Capsule")) {
				//recalculate rate
				rate = 0;
				//System.out.println("recalculating initial : " + AssignmentP2.rate);
				String prev2="", prev1="", prev="", prevSubStr="";
				String[] capsuleWords = capsule.split(" ");
				//System.out.println("capsule words : " + capsuleWords[0]);
				for(String token : capsuleWords) {
					//System.out.println("rate to " + token);
					rate = rate + findRating(removeSpChars(token).toUpperCase(), prevSubStr);
					prev2 = prev1;
					prev1 = prev;
					prev = token;
				}
				//System.out.println("new : " + rate);
				type = " (Heuristic - capsule)";
			}else {
				rate = AssignmentP2.rate;
				type = " (Heuristic - complete review)";
			}
		}

		
		
		//padd first paragraph to snippet
		try {
			String[] para1Words = firstPara.split(" ");
			String[] para2Words = secondPara.split(" ");
			String[] snippetWords = snippet.split(" ");
			//System.out.println("Snippet len: " + para2Words.length );
		if((snippetWords.length) < 50) {
			if((snippetWords.length + para1Words.length) > 50) {
				/*System.out.println("Appending para1");*/
				int reqLen = 50 - snippetWords.length;
				int p=0;
				while(p < reqLen) {
					snippet = snippet + " " + para1Words[p];
					p++;
				}
				//System.out.println("First append : " + snippet);
			}else if((snippetWords.length + para1Words.length + para2Words.length) > 50){
				//System.out.println("Appending para2");
				snippet = snippet + firstPara;
				snippetWords = null;
				snippetWords = snippet.split(" ");
				int reqLen = 50 - snippetWords.length;
				int p=0;
				while(p < reqLen) {
					snippet = snippet + " " + para2Words[p];
					p++;
				}
			}else snippet = snippet + firstPara + secondPara;
		}else {
			//System.out.println("No need of first para.");
			//System.out.println("could not found first para.");
		}
		
		if(rate != null) {			
			if(rate > 0) 
				AssignmentP2.docTable.put(fileName, new DocumentTable(title, reviewer, snippet,"P " +  rate.toString() + type));
			else
				AssignmentP2.docTable.put(fileName, new DocumentTable(title, reviewer, snippet,"N " +  rate.toString() + type));
		}
		else
			AssignmentP2.docTable.put(fileName, new DocumentTable(title, reviewer, snippet, "N/A"));
		
		}catch(StringIndexOutOfBoundsException siob) {
			siob.printStackTrace();
			System.out.println("some error");
		}catch(ArrayIndexOutOfBoundsException aiob) {
			aiob.printStackTrace();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}catch(Exception e) {
		e.printStackTrace();
	}
}

public static int findRating(String token, String prevSubStr) {
	int tokenWt = 0;

	if(AssignmentP2.ssWord.containsKey(token)) {
		if((AssignmentP2.ssWord.get(token) > 0 /*&& AssignmentP1.ssWord.get(token) >2*/) || 
				((AssignmentP2.ssWord.get(token) < 0/* && AssignmentP1.ssWord.get(token) < -2*/))) {
			tokenWt = (AssignmentP2.ssWord.get(token));
			//System.out.println("token : " +token + " " + tokenWt);
			for(String negWrd : AssignmentP2.negationWords) {
				if(prevSubStr.contains(negWrd)) {
					//System.out.println("Negating : ");
					tokenWt = -tokenWt;
					break;
				}else {
				
				}
			}

			
		}
	}else if(AssignmentP2.ssWord.containsKey(token + "#1")){
		if((AssignmentP2.ssWord.get(token + "#1") > 0 /*&& AssignmentP1.ssWord.get(token + "#1") >2*/) || 
				((AssignmentP2.ssWord.get(token + "#1") < 0 /*&& AssignmentP1.ssWord.get(token + "#1") < -2*/))) {
		//System.out.println("#1 ::" + token);
		
		tokenWt = (AssignmentP2.ssWord.get(token + "#1"));
		//System.out.println("token : " +token + " " + tokenWt);
		for(String negWrd : AssignmentP2.negationWords) {
			if(prevSubStr.contains(negWrd)) {
				//System.out.println("Negating : ");
				tokenWt = -tokenWt;
				break;
			}else {
				//System.out.println("le be :" + prevSubStr + " " + negWrd);
			}
		}

		}

	}/*else System.out.println("Not found ::" + token);*/


	
	return tokenWt;
} 

public static Integer findRating(String text) {
	Integer rate = null;
	text = text.replace(" ", "");
	if(text.contains("-4to+4") || text.contains("-4to4") || text.contains("4to-4") || text.contains("+4to-4")) {
		text = text.replaceAll("[^A-Za-z0-9+-.]+", "")/*.replaceAll("-4to+4", "").replaceAll("-4to4", "").replaceAll("4to-4", "").replaceAll("+4to-4", "")*/;
		String[] sentences = text.split("\\.");
		for(String sentence : sentences ) {
			if(sentence.contains("-4to+4") || sentence.contains("-4to4") || sentence.contains("4to-4") || sentence.contains("+4to-4")) {
				//System.out.println("Sentence with rating : " + sentence);
				sentence = sentence.replace("-4to+4", "")
						.replace("-4to4", "")
						.replace("4to-4", "")
						.replace("\\+4to-4", "");
				sentence = sentence.replaceAll("[^0-9+-]", "");
				
				try {
				rate = Integer.parseInt(sentence);
				//System.out.println("Final rating : " + rate);
				}catch(NullPointerException npe) {
					rate = null;
				}catch(NumberFormatException nfe) {
					sentence.replace("+", "");
					try {
						rate = Integer.parseInt(sentence);
						//System.out.println("Final rating : " + rate);
						}catch(Exception e) {
							rate = null;
						}
				}catch(Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
	/*else if(AssignmentP2.probRate != null) {
		rate  = AssignmentP2.probRate;
		type = " (weak rating)";
	}*/
	return rate;
}

public static String removeSpChars(String token) {
	return token.replaceAll("[^A-Za-z]+","");
}		
}
