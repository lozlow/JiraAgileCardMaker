package com.caplin.jira.search;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueFactory;
import com.atlassian.jira.issue.fields.DefaultCustomFieldFactory;
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

public class JACMView extends AbstractSearchRequestView {
	
	private final JiraAuthenticationContext authenticationContext;
    private final SearchProviderFactory searchProviderFactory;
    private final IssueFactory issueFactory;
    private final SearchProvider searchProvider;
    private final VelocityParamFactory velocityParams;
 
    public JACMView(JiraAuthenticationContext authenticationContext, SearchProviderFactory searchProviderFactory,
            IssueFactory issueFactory, SearchProvider searchProvider, VelocityParamFactory velocityParams) {
        this.authenticationContext = authenticationContext;
        this.searchProviderFactory = searchProviderFactory;
        this.issueFactory = issueFactory;
        this.searchProvider = searchProvider;
        this.velocityParams = velocityParams;
    }
	
    @Override
    public void writeSearchResults(SearchRequest searchRequest, SearchRequestParams searchRequestParams, Writer writer) throws SearchException {
        final Map<String, Object> defaultParams = this.velocityParams.getDefaultVelocityParams(authenticationContext);
        
        //Need to put the filtername into the velocity context.  This may be null if this is an anoymous filter.
        final Map<String, Object> headerParams = new HashMap<String, Object> (defaultParams);
        //headerParams.put("filtername", searchRequest.getName());
        headerParams.put("filtername", "hello");
        try {
            writer.write(descriptor.getHtml("header", headerParams));
 
            //now lets write the search results.  This basically iterates over each issue in the search results and writes
            //it to the writer using the format defined by this plugin.  To ensure that this doesn't result in huge
            //memory consumption only one issue should be loaded into memory at a time.  This can be guaranteed by using a
            //Hitcollector.
            final Searcher searcher = searchProviderFactory.getSearcher(SearchProviderFactory.ISSUE_INDEX);
            final Map<String, Object> issueParams = new HashMap<String, Object> ();
            //This hit collector is responsible for writing out each issue as it is encountered in the search results.
            //It will be called for each search result by the underlying Lucene search code.
            final DocumentHitCollector hitCollector = new IssueWriterHitCollector((IndexSearcher) searcher, writer, issueFactory) {
                protected void writeIssue(Issue issue, Writer writer) throws IOException {
                	issueParams.put("issueType", issue.getIssueTypeObject().getName());
                    issueParams.put("projectKey", issue.getProjectObject().getKey());
                    issueParams.put("projectIssueNumber", issue.getNumber());
                    issueParams.put("estimate", issue.getEstimate());
                    issueParams.put("actual", issue.getTimeSpent());
                    issueParams.put("summary", issue.getSummary());
//                    issueParams.put("estimate", issue.getCustomFieldValue(fieldManager.getCustomFieldObject("customfield_10243")));
//                    if (issueParams.get("issueType").equals("Epic")) {
//                    	issueParams.put("", "");
//                    }
                    writer.write(descriptor.getHtml("single-view", issueParams));
                }
            };
            searchProvider.searchAndSort(searchRequest.getQuery(), authenticationContext.getUser(), hitCollector, searchRequestParams.getPagerFilter());

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SearchException e) {
            throw new RuntimeException(e);
        }
    }
}
