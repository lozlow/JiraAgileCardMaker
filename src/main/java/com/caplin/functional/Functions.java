package com.caplin.functional;

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
	
}
