package html_parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Parse_HTML {
	public static List<String> listOfStopWords = new ArrayList<String>(Arrays.asList("and", "a", "the", "an", "by",
			"from", "for", "hence", "of", "the", "with", "in", "within", "who", "when", "where", "why", "how", "whom",
			"have", "had", "has", "not", "for", "but", "do", "does", "done"));

	public static void tokenizer(String text) {

		String[] tokenArray;
		String prev2="", prev1="", prev="", prevSubStr="";
		
		tokenArray = text/* .replace("..",".").replace("  "," ") */.split("-| ");
		try {
			for (String token : tokenArray) { // spit words joined by - treat separately
				// convert to lower case and removes '

				token = token.trim().toLowerCase().replace("'", "").replace("\'", "");

				if (!listOfStopWords.contains(token) && token.length() > 1) {
					// check iteratively all the conditions
					while (token.endsWith(".") || token.endsWith(",") || token.endsWith("?") || token.endsWith(":")
							|| token.endsWith(";") || token.endsWith("!") || token.startsWith("(")
							|| token.startsWith("{") || token.startsWith("[") || token.startsWith("\'")
							|| token.startsWith("\"") || token.endsWith(")") || token.endsWith("}")
							|| token.endsWith("]") || token.endsWith("\'") || token.endsWith("\"")) {
						// remove .,?:;! if they are at the end of the token
						if (token.endsWith(".") || token.endsWith(",") || token.endsWith("?") || token.endsWith(":")
								|| token.endsWith(";") || token.endsWith("!")) {
							token = token.substring(0, token.length() - 1);
						}

						// remove parenthesis () {} [] '' " "
						if (token.startsWith("(") || token.startsWith("{") || token.startsWith("[")
								|| token.startsWith("\'") || token.startsWith("\"")) {
							token = token.substring(1, token.length() - 1);
						}

						if (token.endsWith(")") || token.endsWith("}") || token.endsWith("]") || token.endsWith("\'")
								|| token.endsWith("\"")) {
							token = token.substring(0, token.length() - 1);
						}
					}
					// Additional tokenizing
					 token =  stem(additionalTokenizing(token));

					// remove stop words and single character
					if (token.length() > 1) {
						if (!AssignmentP2.tokens.contains(token)) {
							AssignmentP2.tokens.add(token);
							AssignmentP2.tokenCount++;
						}

						updatePostingList(token.trim());
						updateDocTermList(token.trim());
						updateDictionary(token.trim());
						
						
						//calculate rate
						token = token.toUpperCase();
						if(AssignmentP2.ssWord.containsKey(token)) {
							if((AssignmentP2.ssWord.get(token) > 0 && AssignmentP2.ssWord.get(token) >2) || 
									((AssignmentP2.ssWord.get(token) < 0 && AssignmentP2.ssWord.get(token) < -2))) {
								int tokenWt = (AssignmentP2.ssWord.get(token));
								for(String negWrd : AssignmentP2.negationWords) {
									if(prevSubStr.contains(negWrd)) {
										System.out.println("Negating : ");
										tokenWt = -tokenWt;
										break;
									}else {
										
									}
								}
								AssignmentP2.rate = AssignmentP2.rate + tokenWt;
								//System.out.println("Rate :: " + AssignmentP1.rate + " " + token);
								
							}
						}else if(AssignmentP2.ssWord.containsKey(token + "#1")){
							if((AssignmentP2.ssWord.get(token + "#1") > 0 && AssignmentP2.ssWord.get(token + "#1") >2) || 
									((AssignmentP2.ssWord.get(token + "#1") < 0 && AssignmentP2.ssWord.get(token + "#1") < -2))) {
						
							
							int tokenWt = (AssignmentP2.ssWord.get(token + "#1"));
							for(String negWrd : AssignmentP2.negationWords) {
								if(prevSubStr.contains(negWrd)) {
									System.out.println("Negating : ");
									tokenWt = -tokenWt;
									break;
								}else {
									
								}
							}
							AssignmentP2.rate = AssignmentP2.rate + AssignmentP2.ssWord.get(token+"#1");
							//System.out.println("Rate :: " + AssignmentP1.rate + " " + token);
							}

						}else /*System.out.println("Not found ::" + token);*/

						prev2 = prev1;
						prev1 = prev;
						prev = token.toLowerCase();
						prevSubStr = prev2 + " " + prev1 + " " + prev;
					}

				} else {
					if (listOfStopWords.contains(token))
						AssignmentP2.stopWordCount++;
				}

				// exclude special characters and the words with single character
				if (token.length() > 1 || token.matches("[a-zA-Z0-9]"))
					AssignmentP2.wordCount++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error occured while tokenizing..");
		}
	}

	public static void stemmer() {
		String temp = "";
		try {
			for (String token : AssignmentP2.tokens) {
				temp = token;
				if (token.length() > 3 && token.endsWith("ies")
						&& !(token.endsWith("eies") && token.endsWith("aies"))) {
					// System.out.println("word found");
					token = token.substring(0, token.length() - 3) + "y";
				} else if (token.length() > 2 && token.endsWith("es")
						&& !(token.endsWith("aes") && token.endsWith("ees"))) {
					token = token.substring(0, token.length() - 2) + "e";
				} else if (token.length() > 2 && token.endsWith("s")
						&& !(token.endsWith("us") && token.endsWith("ss"))) {
					token = token.substring(0, token.length() - 1) + "";
				}
				if (!AssignmentP2.listOfstemmedWords.contains(token)) {
					AssignmentP2.listOfstemmedWords.add(token);
					AssignmentP2.stemmedWordCount++;
				}

				// word is stemmed
				if (!temp.equals(token)) {
					updatePostingList(temp.trim(), token.trim());
					updateDictionary(temp, token);
					updateDocTermList(temp, token);
					
/*					System.out.println("Word remvoed : " + temp + " added " + token);
					if (AssignmentP2.postingList.get(token) == null) {
						updatePostingList(temp.trim(), token.trim());
						System.out.println("Rem PS");
					}
					if (AssignmentP2.dictionary.get(token) == null) {
						updateDictionary(temp, token);
						System.out.println("Rem DT");						
					}
					if(AssignmentP2.dtl.get(token) == null)
					{
						updateDocTermList(temp, token);
						System.out.println("Rem DTL");						
					}
*/				}
			}
		} catch (Exception e) {
			// System.out.println("Error occured while stemming..");
			System.out.println("For string : " + temp);
			e.printStackTrace();
		}
	}

	public static void indexer() {
		java.util.Collections.sort(AssignmentP2.listOfstemmedWords);

	}

	// Additional functions
	public static String additionalTokenizing(String token) {
		token = token.replaceAll("[^A-Za-z0-9]+","");
/*		while (!token.toLowerCase().matches("(a|b|c).*") || !token.toLowerCase().endsWith("[a-zA-Z0-9]")) {
			System.out.println("token : " + token);
			break;
			
			 * if(token.startsWith("[a-zA-Z0-9]")) token = token.substring(1,
			 * token.length()-1); if(token.endsWith("[a-zA-Z0-9]")) token =
			 * token.substring(0, token.length()-1);
			 
		}*/
		return token;
	}

	public static String additionalStemming(String token) {
		String rToken = "";

		return rToken;
	}
	/////////////////////////

	// update data structure functions
	public static void updateDocTermList(String token) {
		if (AssignmentP2.dtl.get(AssignmentP2.currFile) == null) {
			AssignmentP2.dtl.put(AssignmentP2.currFile, new HashMap<String, Integer>());
			AssignmentP2.dtl.get(AssignmentP2.currFile).put(token, 1);
		} else if (AssignmentP2.dtl.get(AssignmentP2.currFile).get(token) == null) {
			AssignmentP2.dtl.get(AssignmentP2.currFile).put(token, 1);
		} else {
			int tf = AssignmentP2.dtl.get(AssignmentP2.currFile).get(token);
			AssignmentP2.dtl.get(AssignmentP2.currFile).put(token, tf + 1);
		}
	}

	public static void updateDocTermList(String oldStr, String newStr) {

		for (String docId : AssignmentP2.dtl.keySet()) {
			if (AssignmentP2.dtl.get(docId).get(oldStr) != null /*&& docId == AssignmentP2.currFile*/) {
				int tf = AssignmentP2.dtl.get(docId).get(oldStr);
				AssignmentP2.dtl.get(docId).remove(oldStr);
				AssignmentP2.dtl.get(docId).put(newStr, tf);
/*				System.out.println("Word remvoed : " + oldStr + " added " + newStr);
				System.out.println("Rem DTL\n\n\n");						
*/
			}else {
				
/*				if (AssignmentP2.dtl.get(AssignmentP2.currFile) == null) {
					AssignmentP2.dtl.put(AssignmentP2.currFile, new HashMap<String, Integer>());
					AssignmentP2.dtl.get(AssignmentP2.currFile).put(newStr, 1);
				} else if (AssignmentP2.dtl.get(AssignmentP2.currFile).get(newStr) == null) {
					AssignmentP2.dtl.get(AssignmentP2.currFile).put(newStr, 1);
				}	*/	
			}
		}
	}

	public static void updatePostingList(String token) {
		// add to posting list
		if (AssignmentP2.postingList.get(token) == null) {
			AssignmentP2.postingList.put(token, new ArrayList<String>());
			AssignmentP2.postingList.get(token).add(AssignmentP2.currFile);
			//System.out.println("Term added :" + token);
		} // update posting list
		else if (!AssignmentP2.postingList.get(token).contains(AssignmentP2.currFile)) {
			AssignmentP2.postingList.get(token).add(AssignmentP2.currFile);
			//System.out.println("Term added :" + token);
		}

	}

	public static void updatePostingList(String oldStr, String newStr) {
		if(AssignmentP2.postingList.containsKey(oldStr)) {
			ArrayList<String> list = AssignmentP2.postingList.get(oldStr);
			AssignmentP2.postingList.remove(oldStr);
			AssignmentP2.postingList.put(newStr, list);
			
/*			System.out.println("Word remvoed : " + oldStr + " added " + newStr);
				System.out.println("Rem PS");
*/		}/*else {
			AssignmentP2.postingList.put(newStr, new ArrayList<String>());
			AssignmentP2.postingList.get(newStr).add(AssignmentP2.currFile);
			
		}*/
		 //System.out.println("Update posting list : " + oldStr + " : " + newStr);
	}

	public static void updateDictionary(String token) {
		// add to global dictionary
		if (AssignmentP2.dictionary.get(token) == null) {
			// System.out.println("new Term :" + token);
			html_parser.Dictionary newTerm = new html_parser.Dictionary(1, 0);
			AssignmentP2.dictionary.put(token, newTerm);
			

		} else { // update dictionary : docFreq
			AssignmentP2.dictionary.get(token).setDocFreq(AssignmentP2.dictionary.get(token).getDocFreq() + 1);
			//System.out.println(token + " : "+ AssignmentP2.dictionary.get(token).getDocFreq());
		}
	}

	public static void updateDictionary(String oldStr, String newStr) {
		//System.out.println("Update dictionary : " + oldStr + " : " + newStr);
		html_parser.Dictionary dt;
		if(AssignmentP2.dictionary.containsKey(oldStr)) {
			dt = AssignmentP2.dictionary.get(oldStr);
			AssignmentP2.dictionary.remove(oldStr);			
			AssignmentP2.dictionary.put(newStr, dt);
			//System.out.println(AssignmentP2.currFile + " : " + oldStr + " : " + newStr + " " + dt.getDocFreq());
/*			System.out.println("Word remvoed : " + oldStr + " added " + newStr);
			System.out.println("Rem DT");												
*/			
		}/*else {
			html_parser.Dictionary newTerm = new html_parser.Dictionary(1, 0);
			AssignmentP2.dictionary.put(newStr, newTerm);
			
		}*/

	}

	// Generate data structure functions
	
	public static void generateDictionary() {
		for(String term : AssignmentP2.postingList.keySet()) {
			AssignmentP2.dictionary.put(term, new html_parser.Dictionary(AssignmentP2.postingList.get(term).size(), 0));
		}
	}
	
	public static void generateSortedDictionary() {
		AssignmentP2.sortedDictionary = new TreeMap<>(AssignmentP2.dictionary);
		AssignmentP2.sortedPostingList = new TreeMap<>(AssignmentP2.postingList);
		int offset = 0, prevDocF= 0;
		int i=0;
		for(String term : AssignmentP2.sortedPostingList.keySet()) {
			AssignmentP2.sortedDictionary.get(term).setOffset(offset);
			prevDocF = AssignmentP2.sortedDictionary.get(term).getDocFreq();
			offset = offset + prevDocF;
		}
	}
	
	public static void generateDataStructures() {
		generateDictionary();
		generateSortedDictionary();
		generatePostingFile();
		Parse_HTML.genPostingFile();
		File file = new File(AssignmentP2.projDir + "/Output");
		if(!file.exists()) file.mkdirs();
		FileIO.writeDictionaryFile(AssignmentP2.projDir + "/Output/Dictionary.txt", AssignmentP2.sortedDictionary);
		FileIO.writePostingFile(AssignmentP2.projDir + "/Output/PostingFile.txt", AssignmentP2.postingFile);
		FileIO.writeDocTable(AssignmentP2.projDir + "/Output/DocumetTable.txt", AssignmentP2.docTable);
		FileIO.writePostingFile(AssignmentP2.projDir + "/Output/PostingFile1.txt", AssignmentP2.postingFile1);
	}

	public static void generatePostingFile() {
		for (String dTerm : AssignmentP2.sortedDictionary.keySet()) {
			int count = AssignmentP2.sortedDictionary.get(dTerm).getDocFreq();
			int i = 0;
			for (String docId : AssignmentP2.dtl.keySet()) {
				if (AssignmentP2.dtl.get(docId).get(dTerm) != null) {
					AssignmentP2.postingFile.add(new PostingFile(docId, AssignmentP2.dtl.get(docId).get(dTerm)));
					i++;
				}//else System.out.println("Conflict : " + dTerm);
			}
			//if(count != (i))	System.out.println("Mismatch for : " + dTerm + " " + i + " " + count);
		}
	}

	//ch
	public static void genPostingFile() {
		for (String dTerm : AssignmentP2.sortedDictionary.keySet()) {
//			for(String w : AssignmentP2.postingList.keySet()) {
				
				if(AssignmentP2.postingList.containsKey(dTerm)) {		
					if(AssignmentP2.sortedDictionary.get(dTerm).getDocFreq() == AssignmentP2.postingList.get(dTerm).size()) {
						for(String docId : AssignmentP2.postingList.get(dTerm)) {
							if(AssignmentP2.dtl.containsKey(docId))
								if (AssignmentP2.dtl.get(docId).containsKey(dTerm)) {
									AssignmentP2.postingFile1.add(new PostingFile(docId, AssignmentP2.dtl.get(docId).get(dTerm)));			
								}//else System.out.println("Conflict : " + dTerm);
			
							}					
						}else {
						System.out.println("problem : " + dTerm);
					}
				}
//			}
		}
	}
	// Display functions
	public static void displayDictionary() {
/*		System.out.println("\n\nDictionary : ");
		for (String t : AssignmentP1.dictionary.keySet()) {
			// if(AssignmentP1.dictionary.get(t).getDocFreq() != -1) {
			System.out.println("Term : " + t);
			System.out.println("df : " + AssignmentP1.dictionary.get(t).getDocFreq());
			// }
		}
*/
		System.out.println("\n\n Sorted Dictionary : ");
		for (String t : AssignmentP2.sortedDictionary.keySet()) {
			// if(AssignmentP1.dictionary.get(t).getDocFreq() != -1) {
			System.out.println("Term : " + t);
			System.out.println("df : " + AssignmentP2.sortedDictionary.get(t).getDocFreq() + " " + AssignmentP2.sortedDictionary.get(t).getOffset());
			// }
		}
	}

	public static void displayPostingList() {
		int i = 0;
		//System.out.println(AssignmentP2.postingList.get("love"));
		//System.out.println(AssignmentP2.dtl.get("0003"));
		System.out.println("\n\nPosting list : ");
/*		for (String pt : AssignmentP2.postingList.keySet()) {
			System.out.println(i + " : " + pt + " : " + AssignmentP2.postingList.get(pt));
			i++;
		}
*/		System.out.println("\n\nSorted Posting list : ");
		i = 0;
		for (String pt : AssignmentP2.sortedPostingList.keySet()) {
			System.out.println(i + " : " + pt + " : " + AssignmentP2.sortedPostingList.get(pt));
			i++;
		}
	}

	public static void displayPostingFile() {
		System.out.println("\n\nPosting file : ");
		for (PostingFile pf : AssignmentP2.postingFile) {
			System.out.println(pf.getDocId() + " : " + pf.getTermFreq());
		}
	}

	public static void displaySsWordList() {
		System.out.println("Word Sample space :");
		for (String word : AssignmentP2.ssWord.keySet()) {
			System.out.println(word);
		}
	}
	
	public static void displayNegationWordList(){
		System.out.println("Negation word list :");
		for (String word : AssignmentP2.negationWords) {
			System.out.println(word + " : " + word);
		}
		
	}
	public static void displayDocTable() {
		System.out.println("\n\nDocument table : ");
		for(String docId : AssignmentP2.docTable.keySet()) {
			DocumentTable dt = AssignmentP2.docTable.get(docId);
			System.out.println("Document : " + docId);
			System.out.println("Title : " + dt.getTitle());
			System.out.println("Reviewer : " + dt.getReviewer());
			System.out.println("Rate : " + dt.getRate());
			System.out.println("Snippet : " + dt.getSnippet() + "\n\n");
		}
	}

	public static String stem(String token) {
		
		String temp = token;
		if (token.length() > 3 && token.endsWith("ies")
				&& !(token.endsWith("eies") && token.endsWith("aies"))) {
			// System.out.println("word found");
			token = token.substring(0, token.length() - 3) + "y";
		} else if (token.length() > 2 && token.endsWith("es")
				&& !(token.endsWith("aes") && token.endsWith("ees"))) {
			token = token.substring(0, token.length() - 2) + "e";
		} else if (token.length() > 2 && token.endsWith("s")
				&& !(token.endsWith("us") && token.endsWith("ss"))) {
			token = token.substring(0, token.length() - 1) + "";
		}
		
		return token;
	}
}
