package main.fingerprintWinnowing;

import main.fingerprintWinnowing.WinnowingTextTransformer;
import main.fingerprintWinnowing.WinnowingWhitespaceFilter;

public class FilterTransform implements WinnowingWhitespaceFilter, WinnowingTextTransformer {

	public String doFilter(String sourceDocument){
		System.out.println("Before Filter = "+sourceDocument);
		sourceDocument=sourceDocument.replaceAll("\\s+", "");
		sourceDocument=sourceDocument.replace(".", "");
		sourceDocument=sourceDocument.replace(",", "");
		sourceDocument=sourceDocument.replace(":", "");
		System.out.println("After Filter = "+sourceDocument);
		return sourceDocument;
		
	}
	
	public String doTransformation(String sourceDocument){
		
		return sourceDocument;
	}
}
