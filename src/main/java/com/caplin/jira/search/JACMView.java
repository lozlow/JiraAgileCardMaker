package com.caplin.jira.search;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;

import com.atlassian.greenhopper.manager.issuelink.EpicLinkManagerImpl;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueFactory;
import com.atlassian.jira.issue.fields.DefaultCustomFieldFactory;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.jira.issue.managers.DefaultCustomFieldManager;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.issue.search.SearchProviderFactory;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.statistics.util.DocumentHitCollector;
import com.atlassian.jira.issue.views.util.IssueWriterHitCollector;
import com.atlassian.jira.plugin.searchrequestview.AbstractSearchRequestView;
import com.atlassian.jira.plugin.searchrequestview.SearchRequestParams;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.VelocityParamFactory;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.caplin.PriorityHelper;

public class JACMView extends AbstractSearchRequestView {
	
	private final JiraAuthenticationContext authenticationContext;
    private final SearchProviderFactory searchProviderFactory;
    private final IssueFactory issueFactory;
    private final SearchProvider searchProvider;
    private final VelocityParamFactory velocityParams;
    private final EpicLinkManagerImpl epicManager;
 
    public JACMView(JiraAuthenticationContext authenticationContext, SearchProviderFactory searchProviderFactory,
            IssueFactory issueFactory, SearchProvider searchProvider, VelocityParamFactory velocityParams) {
        this.authenticationContext = authenticationContext;
        this.searchProviderFactory = searchProviderFactory;
        this.issueFactory = issueFactory;
        this.searchProvider = searchProvider;
        this.velocityParams = velocityParams;
        this.epicManager = new EpicLinkManagerImpl();
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
            		
                    

//                    issueParams.put("estimate", issue.getCustomFieldValue(fieldManager.getCustomFieldObject("customfield_10243")));
//                    if (issueParams.get("issueType").equals("Epic")) {
//                    	issueParams.put("", "");
//                    }
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
        issueMap.put("estimate", issue.getEstimate());
        issueMap.put("actual", issue.getTimeSpent());
        issueMap.put("summary", issue.getSummary());
        issueMap.put("epic", emptyStrOnNull(this.getEpicNameForIssue(issue)));
        issueMap.put("subtasks", this.listToString(this.getSubTasksForIssue(issue)));
        issueMap.put("numsubtasks", this.getSubTasksForIssue(issue).size());
        issueMap.put("parent", emptyStrOnNull(this.getParentIssueNameForIssue(issue)));
        issueMap.put("priority", PriorityHelper.getPriorityFromString(issue.getPriorityObject().getName()));
        
		return issueMap;
    }
    
    private String getParentIssueNameForIssue(Issue issue) {
    	if (issue.getParentObject() == null) {
    		return null;
    	} else {
    		return issue.getParentObject().getSummary();
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

    private String emptyStrOnNull(String str) {
    	if (str == null) {
    		return "";
    	} else {
    		return str;
    	}
    }
    
	private final List<String> getSubTasksForIssue(Issue issue) {
    	final List<String> taskList = new ArrayList<String> ();
		issue.getSubTaskObjects().forEach(new Consumer<Issue> () {

			@Override
			public void accept(Issue issue) {
				taskList.add(issue.getKey());
			}
		});
		
		return taskList;
	}

	private String getEpicNameForIssue(Issue issue) {
		Issue epicIssue = null;
		try {
			epicIssue = epicManager.getEpic(issue).get();
		} catch (NullPointerException e) {
			return null;
		}
		
        return epicIssue.getSummary();
    }
    
}
