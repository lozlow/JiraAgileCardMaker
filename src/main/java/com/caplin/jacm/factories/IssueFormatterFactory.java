package com.caplin.jacm.factories;

import com.atlassian.jira.issue.Issue;
import com.caplin.jacm.IssueFormatter;
import com.caplin.jacm.iface.IIssueFormatterFactory;
import com.caplin.jacm.services.IssueHelperService;

public class IssueFormatterFactory implements IIssueFormatterFactory {

	private final IssueHelperService issueHelperService;

	public IssueFormatterFactory(IssueHelperService issueHelperService) {
		this.issueHelperService = issueHelperService;
	}
	
	@Override
	public IssueFormatter createIssueFormatter(Issue issue) {
		return new IssueFormatter(issue, issueHelperService);
	}

}
