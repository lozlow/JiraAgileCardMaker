package com.caplin.jacm.functions;

public class Utility {

	public static String capitaliseWord(String word) {
		word = word.trim();
		char first = Character.toUpperCase(word.charAt(0));
		word = word.substring(1).toLowerCase();
		return first + word;
	}
	
	public static Object or(Object first, Object second) {
		if (first == null) {
			return second;
		} else {
			return first;
		}
	}
	
}
