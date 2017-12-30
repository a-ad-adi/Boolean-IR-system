package html_parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IRSystem {

	public static HashMap<String, html_parser.Dictionary> dictionary= new HashMap<String,html_parser.Dictionary>();
	public static Map<String, html_parser.Dictionary> sortedDictionary;
	public static HashMap<String, ArrayList<String>> postingList = new HashMap<String,ArrayList<String>>(); 
	public static Map<String, ArrayList<String>> sortedPostingList;
	public static List<PostingFile> postingFile = new ArrayList<PostingFile>();
	public static HashMap<String, HashMap<String,Integer>> dtl = new HashMap<String, HashMap<String, Integer>>();
	public static HashMap<String, DocumentTable> docTable = new HashMap<String, DocumentTable>();
	public static List<String> fLines = new ArrayList<String>();
	public static HashMap<String,List<DocumentTable>> results= new HashMap<String,List<DocumentTable>>();
	public static HashMap<String,List<DocumentTable>> pResults= new HashMap<String,List<DocumentTable>>();
	public static HashMap<String,List<DocumentTable>> nResults= new HashMap<String,List<DocumentTable>>();
	public static HashMap<String,List<DocumentTable>> naesults= new HashMap<String,List<DocumentTable>>();
	public static void searchQuery(String q) {
	}
	
	public static void loadPostingFile() {
		
		try {
			System.out.println("Loading posting file..");
			fLines = FileIO.readFile(AssignmentP2.projDir + "/Output/PostingFile1.txt");
			PostingFile pf;
			for(String s : fLines) {
				String[] pfLine = s.split(",");
				pf = new PostingFile(pfLine[0],Integer.parseInt(pfLine[1]));
				postingFile.add(pf);
			}
			System.out.println("Posting file loaded..");
			System.out.println("Length : " + postingFile.size());
			System.out.println("\n\n");
		}catch(Exception e) {
			e.printStackTrace();
		}
			
	}
	
	public static void loadDictionary() {
		html_parser.Dictionary dWord;
		fLines = new ArrayList<String>();
	try {
		System.out.println("Loading dictionary..");
		fLines = FileIO.readFile(AssignmentP2.projDir + "/Output/Dictionary.txt");
		
		for(String s: fLines) {
			String[] dLine = s.split(",");
			dWord = new Dictionary(Integer.parseInt(dLine[1]), Integer.parseInt(dLine[2]));
			dictionary.put(dLine[0], dWord);
		}
		System.out.println("Dictionary loaded..");
		System.out.println("Word count : " + dictionary.size());
		System.out.println("\n\n");
	}catch(Exception e) {
		System.out.println("le : ");
		e.printStackTrace();
	}
		
	}
	
	public static void loadReviewInfo() {
		try {
			System.out.println("Loading review table..");
			fLines = FileIO.readFile(AssignmentP2.projDir + "/Output/DocumetTable.txt");
			int i=0;
			System.out.println("Lines : "+ fLines.size());
			while(i < fLines.size()) {
				String docId = fLines.get(i++).replace("DocId :","").trim();
				String title = fLines.get(i++);/*.replace("Title :","").trim();*/
				String reviewer = fLines.get(i++);/*.replace("Reviewer :","").trim();*/
				String rate = fLines.get(i++);/*.replace("Rate :","").trim();*/
				String snippet = fLines.get(i++);/*.replace("Snippet :","")
											 .replace("(scale based)","")
											 .replace("(Heuristic - complete review)","")
											 .replace("(Heuristic - capsule)","")
											 .replaceAll("[0-9]", "")
											 .trim();*/
				i++;
			docTable.put(docId, new DocumentTable(title, reviewer, snippet, rate));							
			}
			System.out.println("Review table loaded..");
			System.out.println("Length : " + fLines.size());
			System.out.println("\n\n");
		}catch(Exception e) {
			e.printStackTrace();
		}
					
	}


}
