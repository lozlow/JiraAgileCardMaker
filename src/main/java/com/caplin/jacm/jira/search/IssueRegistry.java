package com.caplin.jacm.jira.search;

import java.util.HashSet;
import java.util.Set;

import com.atlassian.jira.issue.Issue;

/**
 * This should keep track of the issues published so that there aren't duplicates
 */
public class IssueRegistry {

	private Set<Issue> registeredIssues;
	
	public IssueRegistry() {
		this.registeredIssues = new HashSet<Issue> ();
	}

	public boolean isIssueRegistered(Issue issue) {
		return this.registeredIssues.contains(issue);
	}

	public void clearRegisteredIssues() {
		this.registeredIssues = new HashSet<Issue> ();
	}

	public void registerIssue(Issue issue) {
		this.registeredIssues.add(issue);
	}

}
