package testPack;
import main.preprocess;
import main.fingerprintWinnowing.*;

import java.security.NoSuchAlgorithmException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class testPlagiarism {

	public static void main(String[] args) {
		// TODO Auto-generated method stub	
		PDFTextStripper pdfStripper = null;
		PDFTextStripper pdfStripper2 = null;
	    PDDocument pdDoc = null;
	    PDDocument pdDoc2 = null;
	    COSDocument cosDoc = null;
	    COSDocument cosDoc2 = null;
	    String parsedText=null;
	    String parsedText2=null;
	    File file = new File("C:/update/xampp/htdocs/ta/A11.2011.05929.pdf");
	    File file2 = new File("C:/update/xampp/htdocs/ta/test.pdf");
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
	        pdfStripper.setEndPage(150);
	        pdfStripper2.setStartPage(1);
	        pdfStripper2.setEndPage(150);
	        parsedText = pdfStripper.getText(pdDoc);
	        parsedText2 = pdfStripper2.getText(pdDoc2);	        
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } 
		
		
		Set<BigInteger> fingerprint1=null, fingerprint2=null;
		FilterTransform ft=new FilterTransform();
		List<WinnowingWhitespaceFilter> ws=new ArrayList<WinnowingWhitespaceFilter>(1);
		List<WinnowingTextTransformer> wt=new ArrayList<WinnowingTextTransformer>(1);
		wt.add(ft);
		ws.add(ft);
		WinnowingFingerprinter WN = new WinnowingFingerprinter(ws, wt, 8, 12, "MD5");
		try {
			fingerprint1 = WN.fingerprint(parsedText);
			fingerprint2 = WN.fingerprint(parsedText2);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();		
		}
		
		
		JaccardCoefficient JC = new JaccardCoefficient();
		double similarity = JC.similaritylist(fingerprint1, fingerprint2);
		System.out.println(String.format( "%.2f",similarity*100));
		
	}

}

