package com.caplin.functions;

public class Utility {

	public static Object emptyStrOnFalsey(Object obj) {
    	if (obj == null) {
    		return "";
    	} else {
    		return obj;
    	}
    }
    
	public static String emptyStrOnFalsey(Long lng) {
    	if (lng == null || lng == 0) {
    		return "";
    	} else {
    		return String.valueOf(lng);
    	}
    }
	
	public static String emptyStrOnFalsey(Double dbl) {
    	if (dbl == null || dbl == 0.0) {
    		return "";
    	} else {
    		return String.valueOf(dbl);
    	}
    }
	
	public static String capitaliseWord(String word) {
		word = word.trim();
		char first = Character.toUpperCase(word.charAt(0));
		word = word.substring(1).toLowerCase();
		return first + word;
	}
	
}
