package com.caplin;

public enum CustomFieldHelper {

	EPIC_LINK(10870),
	ESTIMATED_STORY_POINTS(10243),
	ACTUAL_STORY_POINTS(10260);
	
	private int fieldNum;
	
	CustomFieldHelper(int fieldNum) {
		this.fieldNum = fieldNum;
	}
	
	public String getFieldName() {
		return "customfield_" + this.fieldNum;
	}
	
	public int getFieldId() {
		return this.fieldNum;
	}
	
}
