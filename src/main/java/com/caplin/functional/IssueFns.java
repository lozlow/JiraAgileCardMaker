package com.caplin.functional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import com.atlassian.greenhopper.manager.issuelink.EpicLinkManagerImpl;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.caplin.CustomFieldHelper;

public class IssueFns {
	
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
    	
    	if (IssueFns.isEpic(issue)) {
    		try {
	    		issueList = new EpicLinkManagerImpl().getIssuesInEpic(issue);
    		} catch (NullPointerException e) {
    			issueList = new ArrayList<Issue> ();
    		}
    	} else {
    		issueList = issue.getSubTaskObjects();
    	}
    	
    	issueList.forEach(new Consumer<Issue> () {

			@Override
			public void accept(Issue issue) {
				taskList.add(issue.getKey());
			}
		});
		
		return taskList;
	}
	
	public static boolean hasEpic(Issue issue, CustomFieldManager customFieldManager) {
		Issue epicIssue = (Issue) issue.getCustomFieldValue(
				customFieldManager.getCustomFieldObject(
						CustomFieldHelper.EPIC_LINK.getFieldName()));
		
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
		
		if (IssueFns.hasParent(issue)) {
			return getEpicName(issue.getParentObject(), customFieldManager);
		} else if (epicIssue == null) {
			return null;
		} else {
			return epicIssue.getSummary();
		}
	}

}
