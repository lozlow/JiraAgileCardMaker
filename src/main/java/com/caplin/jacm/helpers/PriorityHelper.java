package com.caplin.jacm.helpers;

import com.caplin.jacm.functions.Utility;

public enum PriorityHelper {
	
	//BLOCKER("fa-ban", "red"),
	//CRITICAL("fa-arrow-circle-o-up", "red"),
	//MAJOR("fa-arrow-up", "red"),
	//MINOR("fa-arrow-down", "green"),
	//TRIVIAL("fa-caret-down", "green"),
	CRITICAL("fa-ban", "red"),
	HIGH("fa-arrow-up", "red"),
	MEDIUM("fa-arrow-right", "red"),
	LOW("fa-arrow-down", "green"),
	UNKNOWN("fa-question-circle", "gainsboro");
	
	private String className;
	private String colour;
	
	PriorityHelper(String className, String colour) {
		this.className = className;
		this.colour = colour;
	}
	
	public String getClassName() {
		return this.className;
	}
	
	public String getColour() {
		return this.colour;
	}
	
	public String getPriorityName() {
		return Utility.capitaliseWord(this.toString());
	}
	
	public static PriorityHelper getPriorityFromString(String priority) {
		PriorityHelper ret = null;
		try {
			ret = PriorityHelper.valueOf(priority.toUpperCase());
		} catch (IllegalArgumentException e) {
			ret = UNKNOWN;
		}
		return ret;
	}
	
}
