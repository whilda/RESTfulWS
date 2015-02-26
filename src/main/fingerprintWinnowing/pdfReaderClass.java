package main.fingerprintWinnowing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;




import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
public class pdfReaderClass {
String address = "";


	
	public static void main(String[] args){
		PDFTextStripper pdfStripper = null;
		PDFTextStripper pdfStripper2 = null;
	    PDDocument pdDoc = null;
	    PDDocument pdDoc2 = null;
	    COSDocument cosDoc = null;
	    COSDocument cosDoc2 = null;
	    File file = new File("C:/Users/Yehezkiel/Downloads/Documents/The-Pros-and-Cons-of-the-Criminal-Jurisdiction-of-the-proposed-african-Court-of-justice-and-human-rights.pdf");
	    File file2 = new File("C:/Users/Yehezkiel/Downloads/Documents/The-Pros-and-Cons-of-the-Criminal-Jurisdiction-of-the-proposed-african-Court-of-justice-and-human-rights.pdf");
	    try {
	        PDFParser parser = new PDFParser(new FileInputStream(file));
	        PDFParser parser2 = new PDFParser(new FileInputStream(file2));
	        parser.parse();
	        parser2.parse();
	        cosDoc = parser.getDocument();
	        cosDoc2 = parser2.getDocument();
	        pdfStripper = new PDFTextStripper();
	        pdfStripper2 = new PDFTextStripper();
	        pdDoc = new PDDocument(cosDoc);
	        pdDoc2 = new PDDocument(cosDoc2);
	        pdfStripper.setStartPage(1);
	        pdfStripper.setEndPage(1);
	        pdfStripper2.setStartPage(1);
	        pdfStripper2.setEndPage(1);
	        String parsedText = pdfStripper.getText(pdDoc);
	        String parsedText2 = pdfStripper2.getText(pdDoc2);
	        System.out.println(parsedText);
	        System.out.println(parsedText2);
	        
	        
	        
	        
	     
	        //parsedText2.split("\\ ");
	       // RabinKarp Kreb = new RabinKarp(parsedText, parsedText2);
	        
	        
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	        } 
	    }
	}
