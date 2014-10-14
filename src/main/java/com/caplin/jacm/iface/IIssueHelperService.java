package com.caplin.jacm.iface;

import java.util.Collection;
import com.atlassian.jira.issue.Issue;

public interface IIssueHelperService {

	Issue getIssue(Long issueId);
	boolean isEpic(Issue issue);
	boolean hasParent(Issue issue);
	Issue getParent(Issue issue);
	boolean hasSubTasks(Issue issue);
	Collection<Issue> getSubTasks(Issue issue);
	boolean hasEpic(Issue issue);
	String getEpicName(Issue issue);
	
}
