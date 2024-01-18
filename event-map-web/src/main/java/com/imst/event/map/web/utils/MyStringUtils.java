package com.imst.event.map.web.utils;

import java.io.UnsupportedEncodingException;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class MyStringUtils {
	
	public static boolean containsString(String text, String query) {
		
		boolean result = false;
		try {
			text = text == null ? "" : text;
			query = query == null ? "" : query;
			result = toLower(text).contains(toLower(query));
		}catch (Exception e) {
			log.error(e);
		}
		
		return result;
		
	}
	
	public static String toLower(String text) {
		
		String lowercaseText = text.toLowerCase();
		
		// Iİıi -> i olarak lowercase yapılması sağlanacaktır.
		lowercaseText = lowercaseText.replace("ı", "i");		
		
		return lowercaseText;
	}
	
	public static String toUTF8(String encodedWithISO88591) {
		
		String decodedToUTF8 = encodedWithISO88591;
		try {
			decodedToUTF8 = new String(encodedWithISO88591.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error(e);
		}
		
		return decodedToUTF8;
	}
	
	public static String getStartAndEndWithSubstring(String path) {
		 
		 if(path.startsWith("/") ) {
			 path = path.substring(1, path.length());
		 }
		 
		 if(path.endsWith("/")) {
			 path = path.substring(0, path.length() -1);
		 }
		 
		 return path;
	 }

}
