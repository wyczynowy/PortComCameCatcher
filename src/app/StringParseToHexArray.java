package app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringParseToHexArray {
	
	public StringParseToHexArray() {}
	
	static public List<Integer> parse(String string) throws NumberFormatException{
		List<String> stringBuffer = Arrays.asList(string.split(" "));
		for(String str : stringBuffer) {
			if(str.length() > 2) {
				stringBuffer.indexOf(str);
				stringBuffer.set(stringBuffer.indexOf(str), String.format("%02X",((int)str.toString().substring(1, 2).toCharArray()[0])));
			}
		}
		List<Integer> intBuffer = new ArrayList<Integer>();
		for(String s : stringBuffer) {
			intBuffer.add(Integer.parseInt(s, 16));
		}
		return intBuffer;
	}
	
}
