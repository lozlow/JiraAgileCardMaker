package com.caplin.jacm.jira.webwork;

import java.util.Collections;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.velocity.VelocityManager;
import com.atlassian.webresource.api.assembler.PageBuilderService;
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
	private final VelocityManager velocityManager;
	private final PageBuilderService pageBuilderService;
	
    public JACMAction(IssueHelperService issueHelperService, IssueFormatterFactory issueFormatterFactory,
    		VelocityManager velocityManager, PageBuilderService pageBuilderService) {
    	this.html = new StringBuffer();
    	this.issueHelperService = issueHelperService;
    	this.issueFormatterFactory = issueFormatterFactory;
    	this.velocityManager = velocityManager;
    	this.pageBuilderService = pageBuilderService;
    }
    
    @Override
    public String execute() throws Exception {    	
    	long issueNumber = Long.valueOf(getHttpRequest().getParameter("id"));
		Issue issue = this.issueHelperService.getIssue(issueNumber);
		
		if (issue != null) {
			IssueFormatter issueHelper = this.issueFormatterFactory.createIssueFormatter(issue);

			this.issueKey = issue.getKey();
	    	this.html.append(velocityManager.getBody("templates/jacm-search-view-plugin/",
	    			"agile-card-header-view.vm", Collections.<String, Object> emptyMap()));
	    	this.html.append(velocityManager.getBody("templates/jacm-search-view-plugin/",
	    			"agile-card-single-view.vm", issueHelper.toMap()));
	    	
	    	if (issueHelperService.hasSubTasks(issue)) {
            	for (Issue subTask: issueHelperService.getSubTasks(issue)) {
            		this.html.append(velocityManager.getBody("templates/jacm-search-view-plugin/",
            				"agile-card-single-view.vm", issueFormatterFactory.createIssueFormatter(subTask).toMap()));
            	}
            }
	    	
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
