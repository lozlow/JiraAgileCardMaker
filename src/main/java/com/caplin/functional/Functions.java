package com.caplin.functional;

import java.util.List;
import java.util.function.Consumer;

public class Functions {

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
	
	public static String emptyStrOnFalsey(Float flt) {
    	if (flt == null || flt == 0) {
    		return "";
    	} else {
    		return String.valueOf(flt);
    	}
    }
	
	public static String listToString(List<String> listItems) {
		final StringBuilder sb = new StringBuilder();
		listItems.forEach(new Consumer<String> () {

			@Override
			public void accept(String item) {
				sb.append(item);
				sb.append("\n");
			}
			
		});
		
		return sb.toString();
	}
	
}
