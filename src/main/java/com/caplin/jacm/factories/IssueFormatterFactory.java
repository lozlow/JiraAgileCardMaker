package com.caplin.jacm.factories;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.caplin.jacm.IssueFormatter;
import com.caplin.jacm.iface.IIssueFormatterFactory;
import com.caplin.jacm.services.IssueHelperService;

public class IssueFormatterFactory implements IIssueFormatterFactory {

	private final IssueHelperService issueHelperService;
	private final CustomFieldManager customFieldManager;

	public IssueFormatterFactory(IssueHelperService issueHelperService, CustomFieldManager customFieldManager) {
		this.issueHelperService = issueHelperService;
		this.customFieldManager = customFieldManager;
	}
	
	@Override
	public IssueFormatter createIssueFormatter(Issue issue) {
		return new IssueFormatter(issue, this.issueHelperService, this.customFieldManager);
	}

}
