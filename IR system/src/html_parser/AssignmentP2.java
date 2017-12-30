package html_parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
public class AssignmentP2 {
	//file handling related
	public static String projDir;
	public static String baseDir;
	public static String currFile;
	public static Elements elementData;
	//counters
	public static int fileCount = 0, wordCount = 0, tokenCount = 0, stemmedWordCount = 0, stopWordCount = 0;
	//sample space words 
	public static HashMap<String, Integer> ssWord = new HashMap<String, Integer>();
	public static List<String> negationWords = new ArrayList<String>();
	//tokenization
	public static List<String> tokens = new ArrayList<String>(); 
	public static List<String> listOfstemmedWords = new ArrayList<String>();	
	//data structures to hold output tables
	public static HashMap<String, html_parser.Dictionary> dictionary= new HashMap<String,html_parser.Dictionary>();
	public static Map<String, html_parser.Dictionary> sortedDictionary;
	public static HashMap<String, ArrayList<String>> postingList = new HashMap<String,ArrayList<String>>(); 
	public static Map<String, ArrayList<String>> sortedPostingList;
	public static List<PostingFile> postingFile = new ArrayList<PostingFile>();
	public static List<PostingFile> postingFile1 = new ArrayList<PostingFile>();
	public static HashMap<String, HashMap<String,Integer>> dtl = new HashMap<String, HashMap<String, Integer>>();
	public static HashMap<String, DocumentTable> docTable = new HashMap<String, DocumentTable>();
	public static int rate;
	public static Integer probRate = null;
	
	public static void main(String[] args) {
		baseDir = args[0];
		projDir = System.getProperty("user.dir");
		
		
		// TODO Auto-generated method stub
		Document doc;

		File files[], dir;
		try {
		baseDir = args[0];
		projDir = System.getProperty("user.dir");
		
		System.out.println("Base dir : " + args[0]);
		dir = new File(args[0]);
		files = dir.listFiles();
		
		//Part 2 requirement
		//FileIO.loadWordset();
		//FileIO.loadNegationWords();
		//FileIO.loadXls();
		//take file one by one to add tokens to the dictonary
		for(File file: files) {
			rate = 0;
			try {
				if(file.toString().trim().toLowerCase().contains(".html")) {
					fileCount++;	
					System.out.println("File name : " + file);
					currFile = file.toString().replace(baseDir + "\\", "").replace(baseDir + "/", "").replace(".html", "");
					doc = Jsoup.parse(file, null);
					elementData = doc.getAllElements();
					int i = 0;
					//extract the contents enclosed in the html tags
					while(i < elementData.size()) {
						if(!elementData.get(i).tagName().equals("html") && !elementData.get(i).tagName().equals("#root")) {
							
							Parse_HTML.tokenizer(elementData.get(i).text());
							//Parse_HTML.stemmer();
						}
						i++;
					}				
					DocumentTable.generateDocTable(elementData, file.toString().replace(".html", ""));
				}
			}catch(FileNotFoundException fnf) {
				fnf.printStackTrace();
				System.out.println("File not found. Please check the file path.");
			}catch (IOException e) {
				System.out.println("Error opening file...");
				//e.printStackTrace();
			}finally {
	
			}
		}	
		
		Parse_HTML.stemmer();
		Parse_HTML.indexer();
		
		
		if(GenerateXML.generateXmlFile())	System.out.println("Lexicon is generated successfully.");
		else	System.out.println("Unable to create the lexicon.");
/*		System.out.println("\n\nDictionary terms : \n\n");
		for(String dToken: listOfstemmedWords) {
			System.out.println(dToken);
		}*/
		
		System.out.println("Number of files : " + fileCount);
		System.out.println("Total number of words : " + wordCount);
		System.out.println("After tokenization : " + tokenCount);
		System.out.println("After stemming : " + stemmedWordCount);
		System.out.println("Number of stop words removed : " + stopWordCount);		

		//generate tables
			Parse_HTML.generateDataStructures();
			
		
		//display
		//Parse_HTML.displaySsWordList();
		//Parse_HTML.displayNegationWordList();
		//Parse_HTML.displayDictionary();
		//Parse_HTML.displayPostingList();
		//Parse_HTML.displayPostingFile();
		//Parse_HTML.displayDocTable();
		
		//GenerateTxt.generateTxtFile();
			
			QR();	
	}catch(NoSuchElementException nse) {
		System.out.println("Invalid base direcotry path.");
	}catch(NullPointerException npe) {
		npe.printStackTrace();
		System.out.println("Please check the file path.");
	}catch(Exception e) {
		e.printStackTrace();
		System.out.println("Unknown error...");
	}finally {
		
	}
	}
	public static void QR() {
		html_parser.IRSystem.loadPostingFile();
		html_parser.IRSystem.loadDictionary();
		html_parser.IRSystem.loadReviewInfo();
		
		Scanner s = new Scanner(System.in);
		try {			
			boolean flag = true;
			while(flag) {
				System.out.println("Enter your query : \n");
				String query = s.nextLine();
				
				query(query);
				
				System.out.println("Do you want to continue? y/n");
				s = new Scanner(System.in);
				String ch = s.nextLine().toString().toLowerCase();
				if(ch.equals("n") || !ch.equals("y")) {
					flag = false;
					FileIO.generateOutput();
				}
			}
			
		}catch(Exception e) {
			
		}finally {
			s.close();
		}
	}
	
	public static void query(String q) {
		List<String> listOfStopWords = new ArrayList<String>(Arrays.asList("and", "a", "the", "an", "by",
				"from", "for", "hence", "of", "the", "with", "in", "within", "who", "when", "where", "why", "how", "whom",
				"have", "had", "has", "not", "for", "but", "do", "does", "done"));
		
		List<String> qOpr = new ArrayList<String>(Arrays.asList("(AND", "AND", "(OR", "OR", "(AND NOT"));
		List<String> orWords = new ArrayList<String>();
		List<String> andWords = new ArrayList<String>();
		List<String> andNotWords = new ArrayList<String>();
		String[] qWOrds = q.split(" ");
		System.out.println("Given Q : " + q);
		int i=0;
		String lastOpr = "";
		for(i=0; i< qWOrds.length; i++) {
			if(qOpr.contains(qWOrds[i])) {
				if((qWOrds[i].equals("AND") || qWOrds[i].equals("(AND")) && !qWOrds[i+1].equals("NOT")) {
					while((qWOrds[i].equals("AND") || qWOrds[i].equals("(AND")) && !qWOrds[i+1].equals("NOT")) {
						
						lastOpr = "&"; i++;
					}
					//System.out.println("opr");
				}
			}

			if(qOpr.contains(qWOrds[i])) {
				if((qWOrds[i].equals("AND") || qWOrds[i].equals("(AND")) && qWOrds[i+1].equals("NOT")) {
					while((qWOrds[i].equals("AND") || qWOrds[i].equals("(AND")) && qWOrds[i+1].equals("NOT")) {
						
						lastOpr = "!"; i+=2;
					}
					//System.out.println("opr");
				}
			}
			
			if(qOpr.contains(qWOrds[i])) {
				if(qWOrds[i].equals("OR") || qWOrds[i].equals("(OR")) {
					while(qWOrds[i].equals("OR") || qWOrds[i].equals("(OR")) {
						
						lastOpr = "|"; i++;
					}
					//System.out.println("opr");
				}
			}
			if(lastOpr == "&" && !listOfStopWords.contains(qWOrds[i])) {
				//System.out.println("word");
				if(!qWOrds[i].equals(""))
				andWords.add(Parse_HTML.stem(qWOrds[i].toLowerCase()));
			}
			
			if(lastOpr == "!" && !listOfStopWords.contains(qWOrds[i])) {
				//System.out.println("word");
				if(!qWOrds[i].equals(")"))
				andNotWords.add(Parse_HTML.stem(qWOrds[i].toLowerCase()));
			}
			
			if(lastOpr == "|" && !listOfStopWords.contains(qWOrds[i])) {
				//System.out.println("word");
				if(!qWOrds[i].equals(")"))
				orWords.add(Parse_HTML.stem(qWOrds[i].toLowerCase()));
			}
		}
		
/*		System.out.println("ANDs");
		for(String a: andWords)
			System.out.println(a);
		
		System.out.println("ORs");
		for(String o: orWords)
			System.out.println(o);
		
		System.out.println("Nots");
		for(String n: andNotWords)
			System.out.println(n);*/
		
		searchDocs(andWords, andNotWords, orWords, q);
		
	}
	
	public static void searchDocs(List<String> andWords,List<String> andNotWords,List<String> orWords, String q) {
		List<String> orD = new ArrayList<String>();
		List<String> andD = new ArrayList<String>();
		List<String> notD = new ArrayList<String>();
		
		if(orWords.size() > 0) {
			orD = orQuery(orWords);			
			System.out.println("  OR");
			for(String d : orD)
				System.out.println(d);
		}if(andWords.size() > 0) {
			andD = andQuery(andWords);
			System.out.println("  AND");
			for(String d : andD)
				System.out.println(d);			
		}
		
		if(andNotWords.size() > 0) {
			if(andNotWords.size() > 0) {
				notD = notQuery(andNotWords, andD);				
				System.out.println("  AND NOT");
				for(String d : notD)
					System.out.println(d);
			}else if(orD.size() > 0) {
				notD = notQuery(andNotWords, orD);
				System.out.println("  OR NOT");
				for(String d : notD)
					System.out.println(d);
			}				
		}
		
		if(andWords.size() > 0 && andNotWords.size() > 0 && orWords.size() == 0) {
			//and not
			//displayDocInfo(notD);
			//IRSystem.results.put(q, displayDocInfo(notD));
		}else if(orWords.size() > 0 && andNotWords.size() > 0 && andWords.size() == 0) {
			//or not
			IRSystem.results.put(q, displayDocInfo(notD));
		}else if(andWords.size() > 0 && orWords.size() == 0 && andNotWords.size() == 0) {
			//and
			IRSystem.results.put(q, displayDocInfo(andD));
		}else if(orWords.size() > 0 && andWords.size() == 0 && andNotWords.size() == 0) {
			//or
			IRSystem.results.put(q, displayDocInfo(orD));
		}else {
			System.out.println("Invalid query..");
		}
			
		//System.out.println("Results : ");
		;
	}
	
	public static List<String> orQuery(List<String> w){
		List<String> l = new ArrayList<String>();
		for(String s : w) {
			if(IRSystem.dictionary.containsKey(s)) {
				int df = IRSystem.dictionary.get(s).getDocFreq();
				int ofs = IRSystem.dictionary.get(s).getOffset();
				//System.out.println("Word present");
				for(int i=ofs; i< (ofs + df); i++) {
				String docId = IRSystem.postingFile.get(i).getDocId();
					if(l.isEmpty())
						l.add(docId);
					else if(!l.contains(docId))
						l.add(docId);
				}
			}
		}
		return l;
	}
	
	public static List<String> andQuery(List<String> w){
		List<String> l = new ArrayList<String>();
		HashMap<String, List<String>> tab = new HashMap<String,List<String>>();
		//System.err.println("le..");
		for(String s : w) {
			if(IRSystem.dictionary.containsKey(s)) {
				int df = IRSystem.dictionary.get(s).getDocFreq();
				int ofs = IRSystem.dictionary.get(s).getOffset();
				//System.out.println("Word present");
				for(int i=ofs; i< (ofs + df); i++) {
				String docId = IRSystem.postingFile.get(i).getDocId();
					if(!tab.containsKey(s)) {
						tab.put(s, new ArrayList<String>());
						tab.get(s).add(docId);
					}
					else if(tab.containsKey(s) && !tab.get(s).contains(docId))
						tab.get(s).add(docId);
				}
			}
		}	
		
		
		
		
		for(String wd1: tab.keySet()) {
			//System.out.println("1");
			for(String dId : tab.get(wd1)) {
				//System.out.println("2");
				boolean flag = true;			
				for(String wd2 : tab.keySet()) {
					//System.out.println("3");
					if(!wd1.equals(wd2)) {
							if(!tab.get(wd2).contains(dId))
								flag = false;	
					}
				}
				if(flag) {
					if(l.isEmpty()) {
						l = new ArrayList<String>();
						l.add(dId);
					}else {
						if(!l.contains(dId))
							l.add(dId);
					}
				}
				
			}
			
		}
		
		return l;
	}
	
	public static List<String> notQuery(List<String> w, List<String> andD){
		List<String> l = new ArrayList<String>();
		List<String> tl = new ArrayList<String>();
		l = andQuery(w);
		
		for(int i=0; i< andD.size(); i++) {
			for(String nDid : l) {
				if(andD.get(i).equals(nDid))
					andD.remove(i);
			}
		}
		return andD;
	}

	public static List<DocumentTable> displayDocInfo(List<String> docIds) {
		List<DocumentTable> res = new ArrayList<DocumentTable>();
		HashMap<String,DocumentTable> pRes = new HashMap<String,DocumentTable>();
		HashMap<String,DocumentTable> nRes = new HashMap<String,DocumentTable>();
		HashMap<String,DocumentTable> naRes = new HashMap<String,DocumentTable>();
		
		System.out.println("Document summary : \n");
		for(String d : docIds) {
			if(IRSystem.docTable.containsKey(d)) {
				
				String rate = IRSystem.docTable.get(d).getRate().replace("Rate :","")
												 .replace("(scale based)","")
												 .replace("(Heuristic - complete review)","")
												 .replace("(Heuristic - capsule)","")
												 .replaceAll("[0-9+-]", "")
												 .trim();
				
				if(rate.equals("P")) {					
					//System.out.println("Positive : " + rate);
					pRes.put(IRSystem.docTable.get(d).getRate(),IRSystem.docTable.get(d));
				}
				else if(rate.equals("N")) {
					//System.out.println("Negetive : " + rate);
					nRes.put(IRSystem.docTable.get(d).getRate(),IRSystem.docTable.get(d));					
				}
				else {
					//System.out.println("NAAAAAAA : " + rate);
					naRes.put(IRSystem.docTable.get(d).getRate(),IRSystem.docTable.get(d));
				}
							
			}
		}
			Map<String,DocumentTable> ppRes = new TreeMap<>(pRes);
			Map<String,DocumentTable> nnRes = new TreeMap<>(nRes);
			Map<String,DocumentTable> nnaRes = new TreeMap<>(naRes);
			
			for(String dc : ppRes.keySet()) {
				res.add(ppRes.get(dc));
			}
			
			for(String dc : nnRes.keySet()) {
				res.add(nnRes.get(dc));
			}
			
			for(String dc : nnaRes.keySet()) {
				res.add(nnaRes.get(dc));
			}
		
			System.out.println("Relevent documents : \n\n");
			for(DocumentTable dd : res) {
				System.out.println(
						dd.getTitle() + "\n"
					 + dd.getReviewer() + "\n"
					 + dd.getSnippet()+ "\n"
					 + dd.getRate() + "\n"
						);
			}
	
		return res;
	}
}
