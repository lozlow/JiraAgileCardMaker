package com.caplin.jacm.functions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.caplin.jacm.helpers.CustomFieldHelper;

public class IssueFunctions {
	
	public static boolean isEpic(Issue issue) {
		if (issue.getIssueTypeObject().getName().equals("Epic")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean hasParent(Issue issue) {
		if (issue.getParentObject() != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public static Issue getParent(Issue issue) {
    	Issue parent = issue.getParentObject();
    	if (parent == null) {
    		return null;
    	} else {
    		return parent;
    	}
    }
    
	public static final List<String> getSubTasks(Issue issue) {
    	final List<String> taskList = new ArrayList<String> ();
    	Collection<Issue> issueList;
    	
    	if (IssueFunctions.isEpic(issue)) {
    		try {
    			//TODO
	    		//issueList = new EpicLinkManagerImpl().getIssuesInEpic(issue);
    			throw new NullPointerException();
    		} catch (NullPointerException e) {
    			issueList = new ArrayList<Issue> ();
    		}
    	} else {
    		issueList = issue.getSubTaskObjects();
    	}
    	
    	for (Issue i: issueList) {
    		taskList.add(i.getKey());
    	}
    	
		return taskList;
	}
	
	public static boolean hasEpic(Issue issue, CustomFieldManager customFieldManager) {
		Issue epicIssue = null;
		try {
			epicIssue = (Issue) issue.getCustomFieldValue(
				customFieldManager.getCustomFieldObject(
						CustomFieldHelper.EPIC_LINK.getFieldName()));
		} catch (NullPointerException e) {
			return false;
		}
		
		if (epicIssue != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String getEpicName(Issue issue, CustomFieldManager customFieldManager) {
		Issue epicIssue = (Issue) issue.getCustomFieldValue(
				customFieldManager.getCustomFieldObject(
						CustomFieldHelper.EPIC_LINK.getFieldName()));
		
		if (IssueFunctions.hasParent(issue)) {
			return getEpicName(issue.getParentObject(), customFieldManager);
		} else if (epicIssue == null) {
			return null;
		} else {
			return epicIssue.getSummary();
		}
	}

}
