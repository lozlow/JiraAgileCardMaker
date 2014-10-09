package com.caplin.jacm.iface;

import java.util.List;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;

public interface IIssueHelperService {

	Issue getIssue(Long issueId);
	boolean isEpic(Issue issue);
	boolean hasParent(Issue issue);
	Issue getParent(Issue issue);
	List<String> getSubTasks(Issue issue);
	boolean hasEpic(Issue issue);
	String getEpicName(Issue issue);
	CustomFieldManager getCustomFieldManager();
	
}
