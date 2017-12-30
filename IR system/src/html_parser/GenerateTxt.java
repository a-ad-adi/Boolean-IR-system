package html_parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.xml.sax.ext.LexicalHandler;

public class GenerateTxt {
	public static void generateTxtFile() {
		FileWriter fw = null;
		BufferedWriter bw = null;
		File file = new File(AssignmentP2.baseDir + "/lexicon.txt");
		System.out.println("Contents of lexicon : \n\n");
		try {
			if(!file.exists()) {
				System.out.println("Creating a new file");
				file.createNewFile();
			}else {
				System.out.println("Overwriting the old lexicon.txt file");
			}
			
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			for(String str: AssignmentP2.listOfstemmedWords) {
				System.out.println(str);
				bw.write(str+"\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
				try {
					if(bw != null) bw.close();
					if(fw != null) fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
}
