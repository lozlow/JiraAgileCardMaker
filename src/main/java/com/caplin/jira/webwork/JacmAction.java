package com.caplin.jira.webwork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.atlassian.jira.web.action.JiraWebActionSupport;

public class JacmAction extends JiraWebActionSupport
{
    private static final Logger log = LoggerFactory.getLogger(JacmAction.class);

    @Override
    public String execute() throws Exception {
        return super.execute(); //returns SUCCESS
    }
}
