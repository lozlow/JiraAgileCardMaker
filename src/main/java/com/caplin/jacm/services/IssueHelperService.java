package com.caplin.jacm.services;

import java.util.ArrayList;
import java.util.Collection;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.caplin.jacm.helpers.CustomFieldHelper;
import com.caplin.jacm.iface.IIssueHelperService;

public class IssueHelperService implements IIssueHelperService {
	
	private final IssueService issueService;
	private final CustomFieldManager customFieldManager;
	
	public IssueHelperService(IssueService issueService, CustomFieldManager customFieldManager) {
		this.issueService = issueService;
		this.customFieldManager = customFieldManager;
	}

	@Override
	public Issue getIssue(Long issueId) {
		User currentUser = ComponentAccessor.getJiraAuthenticationContext().getUser().getDirectoryUser();
		return issueService.getIssue(currentUser, issueId).getIssue();
	}
	
	@Override
	public boolean isEpic(Issue issue) {
		if (issue.getIssueTypeObject().getName().equals("Epic")) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean hasParent(Issue issue) {
		if (issue.getParentObject() != null) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public Issue getParent(Issue issue) {
    	Issue parent = issue.getParentObject();
    	return parent;
    }
	
	@Override
	public final boolean hasSubTasks(Issue issue) {
    	if (this.getSubTasks(issue).size() > 0) {
    		return true;
    	} else {
			return false;
    	}
	}
    
	@Override
	public final Collection<Issue> getSubTasks(Issue issue) {
    	Collection<Issue> issueList;
    	
    	if (this.isEpic(issue)) {
    		try {
    			//TODO Currently cannot work out a way to get issues in an Epic
	    		//issueList = new EpicLinkManagerImpl().getIssuesInEpic(issue);    			
    			throw new NullPointerException();
    		} catch (NullPointerException e) {
    			issueList = new ArrayList<Issue> ();
    		}
    	} else {
    		issueList = issue.getSubTaskObjects();
    	}
    	
		return issueList;
	}
	
	@Override
	public boolean hasEpic(Issue issue) {
		Issue epicIssue = null;
		try {
			epicIssue = (Issue) issue.getCustomFieldValue(
				this.customFieldManager.getCustomFieldObject(
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
	
	@Override
	public String getEpicName(Issue issue) {
		Issue epicIssue = (Issue) issue.getCustomFieldValue(
				this.customFieldManager.getCustomFieldObject(
						CustomFieldHelper.EPIC_LINK.getFieldName()));
		
		if (this.hasParent(issue)) {
			return this.getEpicName(issue.getParentObject());
		} else if (epicIssue == null) {
			return null;
		} else {
			return epicIssue.getSummary();
		}
	}

}
