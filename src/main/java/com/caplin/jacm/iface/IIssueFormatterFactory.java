package com.caplin.jacm.iface;

import com.atlassian.jira.issue.Issue;
import com.caplin.jacm.IssueFormatter;

public interface IIssueFormatterFactory {

	IssueFormatter createIssueFormatter(Issue issue);
	
}
