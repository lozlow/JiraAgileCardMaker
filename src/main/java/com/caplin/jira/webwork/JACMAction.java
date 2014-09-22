package com.caplin.jira.webwork;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.velocity.VelocityManager;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.caplin.IssueHelper;

public class JACMAction extends JiraWebActionSupport
{
    private static final Logger log = LoggerFactory.getLogger(JACMAction.class);
	private final IssueService issueService;
	private final PageBuilderService pageBuilderService;
	private final CustomFieldManager customFieldManager;

	private String html;
	private String issueKey;
	
    public JACMAction(PageBuilderService pageBuilderService, IssueService issueService, CustomFieldManager customFieldManager) {
    	this.pageBuilderService = pageBuilderService;
    	this.issueService = issueService;
    	this.customFieldManager = customFieldManager;
    }
    
    @Override
    public String execute() throws Exception {
    	VelocityManager velocityManager = ComponentManager.getInstance().getVelocityManager();
    	User currentUser = ComponentAccessor.getJiraAuthenticationContext().getUser().getDirectoryUser();
		long issueNumber = Long.valueOf(getHttpRequest().getParameter("id"));
		Issue issue = issueService.getIssue(currentUser, issueNumber).getIssue();
		
		IssueHelper issueHelper = new IssueHelper(this.customFieldManager, issue);
		
		this.issueKey = issue.getKey();
    	this.html = velocityManager.getBody("templates/jacm-search-view-plugin/", "agile-card-header-view.vm", new HashMap<String, Object> ());
    	this.html += velocityManager.getBody("templates/jacm-search-view-plugin/", "agile-card-single-view.vm", issueHelper.toMap());
    	
    	return SUCCESS; //returns SUCCESS
    }
    
    public String getHtml() {
    	return this.html;
    }
    
    public String getIssueKey() {
    	return this.issueKey;
    }
    
}
