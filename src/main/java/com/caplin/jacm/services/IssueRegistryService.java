package com.caplin.jacm.services;

import java.util.HashSet;
import java.util.Set;

import com.atlassian.jira.issue.Issue;
import com.caplin.jacm.iface.IIssueRegistryService;

/**
 * This should keep track of the issues published so that there aren't duplicates
 */
public class IssueRegistryService implements IIssueRegistryService {

	private Set<Issue> registeredIssues;
	
	public IssueRegistryService() {
		this.registeredIssues = new HashSet<Issue> ();
	}

	@Override
	public boolean isIssueRegistered(Issue issue) {
		return this.registeredIssues.contains(issue);
	}

	@Override
	public void clearRegisteredIssues() {
		this.registeredIssues = new HashSet<Issue> ();
	}

	@Override
	public void registerIssue(Issue issue) {
		this.registeredIssues.add(issue);
	}

}
