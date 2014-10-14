package com.caplin.jacm.iface;

import com.atlassian.jira.issue.Issue;

public interface IIssueRegistryService {

	boolean isIssueRegistered(Issue issue);
	void clearRegisteredIssues();
	void registerIssue(Issue issue);
	
}
