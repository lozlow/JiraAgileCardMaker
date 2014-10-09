package com.caplin.jacm.jira.webwork;

import java.util.HashMap;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.velocity.VelocityManager;
import com.caplin.jacm.helpers.IssueHelper;

@SuppressWarnings("deprecation")
public class JACMAction extends JiraWebActionSupport
{
    private final IssueService issueService;
	private final CustomFieldManager customFieldManager;	
	private StringBuffer html;
	private String issueKey;
	
    public JACMAction(IssueService issueService, CustomFieldManager customFieldManager) {
    	this.issueService = issueService;
    	this.customFieldManager = customFieldManager;
    	this.html = new StringBuffer();
    }
    
    @Override
    public String execute() throws Exception {
		VelocityManager velocityManager = ComponentManager.getInstance().getVelocityManager();
    	User currentUser = ComponentAccessor.getJiraAuthenticationContext().getUser().getDirectoryUser();
		long issueNumber = Long.valueOf(getHttpRequest().getParameter("id"));
		Issue issue = issueService.getIssue(currentUser, issueNumber).getIssue();
		
		IssueHelper issueHelper = new IssueHelper(this.customFieldManager, issue);

		this.issueKey = issue.getKey();
    	this.html.append(velocityManager.getBody("templates/jacm-search-view-plugin/", "agile-card-header-view.vm", new HashMap<String, Object> ()));
    	this.html.append(velocityManager.getBody("templates/jacm-search-view-plugin/", "agile-card-single-view.vm", issueHelper.toMap()));
    	
    	return SUCCESS;
    }
    
    public String getHtml() {
    	return this.html.toString();
    }
    
    public String getIssueKey() {
    	return this.issueKey;
    }
    
}
