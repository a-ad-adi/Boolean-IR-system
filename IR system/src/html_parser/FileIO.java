package html_parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FileIO {
	private static File file;
	private static String line;
	public static List<String> fLines = new ArrayList<String>();
	public static List<String> readFile(String filePath) {
		file = new File(filePath);
		fLines =  new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			while((line = br.readLine()) != null){
				fLines.add(line);
			}
			return fLines;
		}catch(Exception e) {
			System.out.println("Error in reading file.");
			e.printStackTrace();
		}
		return fLines;
	}

	public static void writeDictionaryFile(String filePath, Map<String, html_parser.Dictionary> dictionary) {
		
		file = new File(filePath);
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			line = "";
			for(String term : dictionary.keySet()) {
				line = term + "," + dictionary.get(term).getDocFreq() + "," + dictionary.get(term).getOffset() + "\n";
				//System.out.println("le : " + line);	
				bw.write(line);
			}
			System.out.println("Dictionary generated successfully");
		}catch(Exception e) {
			System.out.println("Error in writing dictionary file.");
			e.printStackTrace();
		}finally {
			try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}

	public static void writePostingFile(String filePath,List<PostingFile> pf) {
		file = new File(filePath);
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			line = "";
			for(PostingFile tPf : pf) {
				line = tPf.getDocId() + "," + tPf.getTermFreq() + "\n";
				//System.out.println("le : " + line);	
				bw.write(line);
			}
			
			System.out.println("Posting file generated successfully \n");
		}catch(Exception e) {
			System.out.println("Error in writing posting file.");
			e.printStackTrace();
		}finally {
			try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	
	public static void writeDocTable(String filePath, HashMap<String, DocumentTable> docTable) {
		file = new File(filePath);
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			line = "";
			for(String docId : docTable.keySet()) {
				line ="DocId : " + docId + "\n" + 
					  "Title : " + docTable.get(docId).getTitle() + "\n" +
					  "Reviewer : " + docTable.get(docId).getReviewer() + "\n" +
					  "Rate : " + docTable.get(docId).getRate() + "\n" + 
					  "Snippet : " + docTable.get(docId).getSnippet() + "\n\n" ; 
				bw.write(line);		
			}
			
			System.out.println("Document table file generated successfully \n");
		}catch(Exception e) {
			System.out.println("Error in writing document table file.");
			e.printStackTrace();
		}finally {
			try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}				
	}
	public static void loadWordset() {
		System.out.println("Loading sample word set");
		File file = new File(AssignmentP2.projDir + "/wordset/wordset.txt");
		BufferedReader br;
		String line = "", word = "";
		Integer wt = 0;
		try {
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				word = line.split(",")[0].trim();
				wt = Integer.parseInt(line.split(",")[1].trim());
				AssignmentP2.ssWord.put(word, wt);
			}
			System.out.println("Word set loaded.");
		} catch (NullPointerException npe) {
			System.out.println("imporper format.");
			npe.printStackTrace();
		} catch (FileNotFoundException fnf) {
			System.out.println("File not found : " + AssignmentP2.projDir + "/wordset/wordset.txt");
		} catch (Exception e) {
			System.out.println("Error loading review words file.");
		} finally {
			file = null;
		}
	}

	public static void loadNegationWords() {
		File file = new File(AssignmentP2.projDir + "/wordset/negationWords.txt");
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				AssignmentP2.negationWords.add(line);
			}
		} catch (NullPointerException npe) {
			System.out.println("imporper format");
			npe.printStackTrace();
		} catch (FileNotFoundException fnf) {
			System.out.println("File not found : " + AssignmentP2.projDir + "/wordset/wordset.txt");
		} catch (Exception e) {
			System.out.println("Error loading review words file.");
		} finally {
			file = null;
		}
	}
	public static void loadXls() {
		FileInputStream fis;
		Workbook wb;
		Iterator<Row> r;
		Iterator<Cell> c; 
		String term = "", positiv = "", negativ= "", pstv = "", ngtv= "", hostile = "", strong = "", power="", weak="";
		try {
			System.out.println("Loading sample weighted word dictionary");
			fis = new FileInputStream(new File(AssignmentP2.projDir + "/wordset/inquirerbasic.xlsx"));
			 wb = new XSSFWorkbook(fis);
			 org.apache.poi.ss.usermodel.Sheet ws = wb.getSheetAt(0);
			 r = ws.iterator();
			 int i = 0;
			 while(r.hasNext()) {
			
				 	term = ""; positiv = ""; negativ= ""; pstv = ""; ngtv= ""; hostile = ""; strong = "";power=""; weak="";
					Row currRow = r.next();
					if(currRow.getCell(0) != null) term = currRow.getCell(0).toString();
					if(currRow.getCell(2) != null) positiv = currRow.getCell(2).toString();
					if(currRow.getCell(3) != null) negativ = currRow.getCell(3).toString();
					if(currRow.getCell(4) != null) pstv = currRow.getCell(4).toString();
					if(currRow.getCell(6) != null) ngtv = currRow.getCell(6).toString();
					if(currRow.getCell(7) != null) hostile = currRow.getCell(7).toString();
					if(currRow.getCell(8) != null) strong = currRow.getCell(8).toString();
					if(currRow.getCell(9) != null) power = currRow.getCell(9).toString();
					if(currRow.getCell(10) != null) weak = currRow.getCell(10).toString();
					i++;
					int wt = calculateWt(term, positiv, negativ, pstv, ngtv, hostile, strong, power, weak);
					AssignmentP2.ssWord.put(term, wt);
					//System.out.println(term + " " + positiv + " " + negativ + " " + hostile + " " + wt);
					
			 }
			 System.out.println("final count : " + i);
			 System.out.println("sample weighted word dictionary loaded.");
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch(Exception e) {
			
			e.printStackTrace();
		}

	}
	
	public static int calculateWt(String term, String positiv,String negativ, String pstv, String ngtv, String hostile, String strong, String power, String weak) {
		int wt = 0;
		if(positiv.toLowerCase().contains("positiv"))	wt++;
		if(pstv.toLowerCase().contains("pstv"))			wt++;
		if(negativ.toLowerCase().contains("negativ"))	wt--;
		if(ngtv.toLowerCase().contains("ngtv")) 		wt--;

		if(hostile.toLowerCase().contains("hostile")) 	wt-=2;

		if(ngtv.toLowerCase().contains("ngtv") || (negativ.toLowerCase().contains("negativ")) && strong.toLowerCase().contains("strong"))		wt-=1;
		else if(strong.toLowerCase().contains("strong")) wt+=2;
	
		if(weak.toLowerCase().contains("weak"))		wt-=1;

		if(ngtv.toLowerCase().contains("ngtv") || (negativ.toLowerCase().contains("negativ")) && power.toLowerCase().contains("power"))		wt-=1;
		else if(power.toLowerCase().contains("power")) wt+=1;
		
		return wt;
	}
	
	public static void generateOutput() {
		File f = new File(AssignmentP2.projDir + "/Output.txt");
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(f));
			line = "";
			for(String q : IRSystem.results.keySet()) {
				line = "Query : " + q + "\n";
				line = line + "----------\n";
				if(IRSystem.results.get(q) != null) {
					for(DocumentTable dt : IRSystem.results.get(q)) {
						line = line + dt.getTitle() + "\n"
									+ dt.getReviewer() + "\n"
									+ dt.getRate() + "\n"
									+ dt.getSnippet()+ "\n\n";
					}
				}
				bw.write(line);
			}
			System.out.println("Output generated successfully");
			System.out.println(AssignmentP2.projDir + "/Output.txt");
		}catch(Exception e) {
			System.out.println("Error in writing Output.txt file.");
			e.printStackTrace();
		}finally {
			try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
