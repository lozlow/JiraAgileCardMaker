package com.caplin.jira.search;

import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.ofbiz.core.entity.GenericValue;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.greenhopper.manager.issuelink.EpicLinkManagerImpl;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueFactory;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.renderer.IssueRenderContext;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.label.Label;
import com.atlassian.jira.issue.priority.Priority;
import com.atlassian.jira.issue.resolution.Resolution;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.issue.search.SearchProviderFactory;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.statistics.util.DocumentHitCollector;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.issue.views.util.IssueWriterHitCollector;
import com.atlassian.jira.plugin.searchrequestview.AbstractSearchRequestView;
import com.atlassian.jira.plugin.searchrequestview.SearchRequestParams;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.VelocityParamFactory;
import com.caplin.CustomFieldHelper;
import com.caplin.PriorityHelper;

public class JACMView extends AbstractSearchRequestView {
	
	private final JiraAuthenticationContext authenticationContext;
    private final SearchProviderFactory searchProviderFactory;
    private final IssueFactory issueFactory;
    private final SearchProvider searchProvider;
    private final VelocityParamFactory velocityParams;
	private final CustomFieldManager customFieldManager;
 
    public JACMView(JiraAuthenticationContext authenticationContext, SearchProviderFactory searchProviderFactory,
            IssueFactory issueFactory, SearchProvider searchProvider, VelocityParamFactory velocityParams,
            CustomFieldManager customFieldManager) {
        this.authenticationContext = authenticationContext;
        this.searchProviderFactory = searchProviderFactory;
        this.issueFactory = issueFactory;
        this.searchProvider = searchProvider;
        this.velocityParams = velocityParams;
        this.customFieldManager = customFieldManager;
    }
	
    @SuppressWarnings("deprecation")
	@Override
    public void writeSearchResults(SearchRequest searchRequest, SearchRequestParams searchRequestParams, Writer writer) throws SearchException {
        final Map<String, Object> defaultParams = this.velocityParams.getDefaultVelocityParams(authenticationContext);
        
        final Map<String, Object> headerParams = new HashMap<String, Object> (defaultParams);
        
        try {
            writer.write(descriptor.getHtml("header", headerParams));
 
            final Searcher searcher = searchProviderFactory.getSearcher(SearchProviderFactory.ISSUE_INDEX);
 
            final DocumentHitCollector hitCollector = new IssueWriterHitCollector((IndexSearcher) searcher, writer, issueFactory) {
            	
            	protected void writeIssue(Issue issue, Writer writer) throws IOException {
                    writer.write(descriptor.getHtml("single-view", issueToMap(issue)));
                }
            	
            };
            
            searchProvider.searchAndSort(searchRequest.getQuery(), authenticationContext.getUser(), hitCollector, searchRequestParams.getPagerFilter());

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SearchException e) {
            throw new RuntimeException(e);
        }
    }
    
    private final Map<String, Object> issueToMap(Issue issue) {
    	
    	Map<String, Object> issueMap = new HashMap<String, Object> ();
    	
    	issueMap.put("issueType", issue.getIssueTypeObject().getName());
    	
    	issueMap.put("projectKey", issue.getProjectObject().getKey());
    	
        issueMap.put("projectIssueNumber", issue.getNumber());
        
//        issueMap.put("estimate", emptyStrOnFalsey(
//        		issue.getCustomFieldValue(
//        				this.customFieldManager.getCustomFieldObject(
//        						CustomFieldHelper.ESTIMATED_STORY_POINTS.getFieldName()))));
//        
//        issueMap.put("actual", emptyStrOnFalsey(
//        		issue.getTimeSpent()));
        
        issueMap.put("estimate", "");
        
        issueMap.put("actual", "");
        
        issueMap.put("summary", issue.getSummary());
        
        issueMap.put("epic", emptyStrOnFalsey(
        		this.getEpicNameForIssue(issue)));
        
        issueMap.put("subtasks", this.listToString(
        		this.getSubTasksForIssue(issue)));
        
        issueMap.put("numsubtasks", this.getSubTasksForIssue(issue).size());
        
        issueMap.put("parent", emptyStrOnFalsey(
        		this.getParentIssueNameForIssue(issue)));
        
        issueMap.put("priority", PriorityHelper.getPriorityFromString(
        		issue.getPriorityObject().getName()));
        
		return issueMap;
    }
    
    private String getParentIssueNameForIssue(Issue issue) {
    	Issue parent = issue.getParentObject();
    	if (parent == null) {
    		return null;
    	} else {
    		return parent.getKey() + ": " + parent.getSummary();
    	}
    }
    
    private String listToString(List<String> subTasksForIssue) {
		final StringBuilder sb = new StringBuilder();
		subTasksForIssue.forEach(new Consumer<String> () {

			@Override
			public void accept(String task) {
				sb.append(task);
				sb.append("\n");
			}
			
		});
		
		return sb.toString();
	}
    
    private Object emptyStrOnFalsey(Object obj) {
    	if (obj == null) {
    		return "";
    	} else {
    		return obj;
    	}
    }
    
    private String emptyStrOnFalsey(Long lng) {
    	if (lng == null || lng == 0) {
    		return "";
    	} else {
    		return String.valueOf(lng);
    	}
    }
    
	private final List<String> getSubTasksForIssue(Issue issue) {
    	final List<String> taskList = new ArrayList<String> ();
    	Collection<Issue> issueList;
    	
    	if (isEpic(issue)) {
    		try {
	    		issueList = new EpicLinkManagerImpl().getIssuesInEpic(issue);
    		} catch (NullPointerException e) {
    			issueList = new ArrayList<Issue> ();
    		}
    	} else {
    		issueList = issue.getSubTaskObjects();
    	}
    	
    	issueList.forEach(new Consumer<Issue> () {

			@Override
			public void accept(Issue issue) {
				taskList.add(issue.getKey());
			}
		});
		
		return taskList;
	}
	
	private boolean isEpic(Issue issue) {
		if (issue.getIssueTypeObject().getName().equals("Epic")) {
			return true;
		} else {
			return false;
		}
	}

	private boolean hasParent(Issue issue) {
		if (issue.getParentObject() != null) {
			return true;
		} else {
			return false;
		}
	}
	
	private String getEpicNameForIssue(Issue issue) {
		Issue epicIssue = (Issue) issue.getCustomFieldValue(this.customFieldManager.getCustomFieldObject(CustomFieldHelper.EPIC_LINK.getFieldName()));
		
		if (hasParent(issue)) {
			return getEpicNameForIssue(issue.getParentObject());
		} else if (epicIssue == null) {
			return null;
		} else {
			return epicIssue.getSummary();
		}
    }
    
}
