package com.caplin.jacm.jira.search;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueFactory;
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
import com.caplin.jacm.IssueFormatter;
import com.caplin.jacm.factories.IssueFormatterFactory;
import com.caplin.jacm.services.IssueHelperService;
import com.caplin.jacm.services.IssueRegistryService;

@SuppressWarnings("deprecation")
public class JACMView extends AbstractSearchRequestView {
	
	private final JiraAuthenticationContext authenticationContext;
    private final SearchProviderFactory searchProviderFactory;
    private final IssueFactory issueFactory;
    private final SearchProvider searchProvider;
    private final VelocityParamFactory velocityParams;
	private final IssueFormatterFactory issueFormatterFactory;
	private final IssueHelperService issueHelperService;
	private final IssueRegistryService issueRegisterService;
 
    public JACMView(JiraAuthenticationContext authenticationContext, SearchProviderFactory searchProviderFactory,
            IssueFactory issueFactory, SearchProvider searchProvider, VelocityParamFactory velocityParams,
            IssueFormatterFactory issueFormatterFactory, IssueHelperService issueHelperService,
            IssueRegistryService issueRegisterService) {
        this.authenticationContext = authenticationContext;
        this.searchProviderFactory = searchProviderFactory;
        this.issueFactory = issueFactory;
        this.searchProvider = searchProvider;
        this.velocityParams = velocityParams;
        this.issueFormatterFactory = issueFormatterFactory;
        this.issueHelperService = issueHelperService;
        this.issueRegisterService = issueRegisterService;
    }
	
    @Override
    public void writeSearchResults(SearchRequest searchRequest, SearchRequestParams searchRequestParams, Writer writer) throws SearchException {
        final Map<String, Object> defaultParams = this.velocityParams.getDefaultVelocityParams(authenticationContext);
        final Map<String, Object> headerParams = new HashMap<String, Object> (defaultParams);
        this.issueRegisterService.clearRegisteredIssues();
        
        try {
            writer.write(descriptor.getHtml("header", headerParams));
 
            final Searcher searcher = searchProviderFactory.getSearcher(SearchProviderFactory.ISSUE_INDEX);
 
            final DocumentHitCollector hitCollector = new IssueWriterHitCollector((IndexSearcher) searcher, writer, issueFactory) {
            	
            	protected void writeIssue(Issue issue, Writer writer) throws IOException {
            		
                    JACMView.this.writeIssue(issue, writer);
                    
                    if (issueHelperService.hasSubTasks(issue)) {
                    	for (Issue subTask: issueHelperService.getSubTasks(issue)) {
                    		JACMView.this.writeIssue(subTask, writer);
                    	}
                    }
                }
            	
            };
            
            searchProvider.searchAndSort(searchRequest.getQuery(), authenticationContext.getUser(), hitCollector, searchRequestParams.getPagerFilter());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SearchException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void writeIssue(Issue issue, Writer writer) throws IOException {
    	if (issueRegisterService.isIssueRegistered(issue) == false) {
    		IssueFormatter issueHelper = issueFormatterFactory.createIssueFormatter(issue);
            writer.write(descriptor.getHtml("single-view", issueHelper.toMap()));
            
            issueRegisterService.registerIssue(issue);
    	}
    }
    
}
