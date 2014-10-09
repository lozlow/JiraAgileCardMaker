package com.caplin.jacm.jira.webwork;

import java.util.HashMap;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.velocity.VelocityManager;
import com.caplin.jacm.IssueFormatter;
import com.caplin.jacm.factories.IssueFormatterFactory;
import com.caplin.jacm.services.IssueHelperService;

@SuppressWarnings("deprecation")
public class JACMAction extends JiraWebActionSupport
{
	private static final long serialVersionUID = 1L;
	private StringBuffer html;
	private String issueKey;
	private final IssueHelperService issueHelperService;
	private final IssueFormatterFactory issueFormatterFactory;
	
    public JACMAction(IssueHelperService issueHelperService, IssueFormatterFactory issueFormatterFactory) {
    	this.html = new StringBuffer();
    	this.issueHelperService = issueHelperService;
    	this.issueFormatterFactory = issueFormatterFactory;
    }
    
    @Override
    public String execute() throws Exception {
		VelocityManager velocityManager = ComponentManager.getInstance().getVelocityManager();
    	long issueNumber = Long.valueOf(getHttpRequest().getParameter("id"));
		Issue issue = this.issueHelperService.getIssue(issueNumber);
		
		if (issue != null) {
			IssueFormatter issueHelper = this.issueFormatterFactory.createIssueFormatter(issue);

			this.issueKey = issue.getKey();
	    	this.html.append(velocityManager.getBody("templates/jacm-search-view-plugin/", "agile-card-header-view.vm", new HashMap<String, Object> ()));
	    	this.html.append(velocityManager.getBody("templates/jacm-search-view-plugin/", "agile-card-single-view.vm", issueHelper.toMap()));
	    	
	    	return SUCCESS;
		} else {
			return ERROR;
		}
    }
    
    public String getHtml() {
    	return this.html.toString();
    }
    
    public String getIssueKey() {
    	return this.issueKey;
    }
    
}
