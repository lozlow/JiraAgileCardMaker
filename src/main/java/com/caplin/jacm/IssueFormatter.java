package com.caplin.jacm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.issue.Issue;
import com.caplin.jacm.functions.Utility;
import com.caplin.jacm.helpers.CustomFieldHelper;
import com.caplin.jacm.helpers.PriorityHelper;
import com.caplin.jacm.services.IssueHelperService;

public class IssueFormatter {

	private static final int NUM_SUBTASKS_TO_SHOW = 7;
	
	private final Issue issue;
	private List<String> subTasks;
	private final IssueHelperService issueHelperService;

	public IssueFormatter(Issue issue, IssueHelperService issueHelperService) {
		this.issue = issue;
		this.issueHelperService = issueHelperService;
	}

	public String getIssueType() {
		return this.issue.getIssueTypeObject().getName();
	}

	public String getProjectKey() {
		return this.issue.getProjectObject().getKey();
	}

	public Long getProjectIssueNumber() {
		int startLoc = this.issue.getKey().indexOf("-");
		return Long.valueOf(this.issue.getKey().substring(startLoc + 1));
	}

	public Double getEstimatedStoryPoints() {
		try {
			return (Double) issue.getCustomFieldValue(
					this.issueHelperService.getCustomFieldManager().getCustomFieldObject(
							CustomFieldHelper.ESTIMATED_STORY_POINTS.getFieldName()));
		} catch (NullPointerException e) {
			return null;
		}
	}

	public Double getActualStoryPoints() {
		try {
			return (Double) issue.getCustomFieldValue(
					this.issueHelperService.getCustomFieldManager().getCustomFieldObject(
							CustomFieldHelper.ACTUAL_STORY_POINTS.getFieldName()));
		} catch (NullPointerException e) {
			return null;
		}
	}

	public String getSummary() {
		return this.issue.getSummary();
	}

	public String getEpic() {
		if (this.issueHelperService.hasEpic(this.issue)) {
			return this.issueHelperService.getEpicName(this.issue);
			
		} else if (this.issueHelperService.hasParent(this.issue)) {
			Issue parent = this.issueHelperService.getParent(issue);
			
			if (this.issueHelperService.hasEpic(parent)) {
				return this.issueHelperService.getEpicName(this.issueHelperService.getParent(this.issue));
			} else {
				return "";
			}
			
		} else {
			return null;
		}
	}
	
	public List<String> getSubtasks() {
		if (this.subTasks == null) {
			this.subTasks = this.issueHelperService.getSubTasks(this.issue);
		}
		return this.subTasks;
	}

	public List<String> getSubtasksTruncated() {
		return this.getSubtasks().subList(0, Math.min(this.getNumSubtasks(), IssueFormatter.NUM_SUBTASKS_TO_SHOW));
	}

	public int getNumSubtasks() {
		return this.getSubtasks().size();
	}
	
	private String getRestSubtasks() {
		if (this.getNumSubtasks() > IssueFormatter.NUM_SUBTASKS_TO_SHOW) {
			String retString;
			int delta = this.getNumSubtasks() - IssueFormatter.NUM_SUBTASKS_TO_SHOW;

			if (delta == 1) {
				retString = "... and " + delta + " other";
			} else {
				retString = "... and " + delta + " others";
			}
						
			return retString;
		} else {
			return null;
		}
	}

	public String getParent() {
		if (this.issueHelperService.hasParent(this.issue)) {
			Issue parent = this.issueHelperService.getParent(this.issue);
			return parent.getKey() + ": " + parent.getSummary();
		} else {
			return null;
		}
	}

	public PriorityHelper getPriority() {
		return PriorityHelper.getPriorityFromString(this.issue
				.getPriorityObject().getName());
	}

	public final Map<String, Object> toMap() {
		Map<String, Object> issueMap = new HashMap<String, Object>();

		issueMap.put("issueType", this.getIssueType());
		issueMap.put("projectKey", this.getProjectKey());
		issueMap.put("projectIssueNumber", this.getProjectIssueNumber());
		issueMap.put("estimate", Utility.or(this.getEstimatedStoryPoints(), ""));
		issueMap.put("actual", Utility.or(this.getActualStoryPoints(), ""));
		issueMap.put("summary", this.getSummary());
		issueMap.put("epic", Utility.or(this.getEpic(), ""));
		issueMap.put("subtasks", this.getSubtasksTruncated());
		issueMap.put("numsubtasks", this.getNumSubtasks());
		issueMap.put("restsubtasks", Utility.or(this.getRestSubtasks(), ""));
		issueMap.put("parent", Utility.or(this.getParent(), ""));
		issueMap.put("priority", this.getPriority());

		return issueMap;
	}

	

}
