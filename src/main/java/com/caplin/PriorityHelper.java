package com.caplin;

public enum PriorityHelper {
	
	BLOCKER("fa-ban", "red"),
	CRITICAL("class", "colour"),
	MAJOR("class", "colour"),
	MINOR("class", "colour"),
	TRIVIAL("class", "colour"),
	UNKNOWN();
	
	private String className;
	private String colour;
	
	PriorityHelper() {
	}
	
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
		String myName = this.toString();
		char first = myName.charAt(0);
		myName = myName.substring(1).toLowerCase();
		return first + myName;
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
