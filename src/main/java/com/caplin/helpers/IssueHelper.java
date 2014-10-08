package com.caplin.helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.caplin.functions.Utility;
import com.caplin.functions.IssueFunctions;

public class IssueHelper {

	private static final int NUM_SUBTASKS_TO_SHOW = 7;
	
	private Issue issue = null;
	private final CustomFieldManager customFieldManager;
	private List<String> subTasks;

	public IssueHelper(CustomFieldManager customFieldManager, Issue issue) {
		this.customFieldManager = customFieldManager;
		this.issue = issue;
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
			return (Double) issue.getCustomFieldValue(this.customFieldManager
				.getCustomFieldObject(CustomFieldHelper.ESTIMATED_STORY_POINTS
						.getFieldName()));
		} catch (NullPointerException e) {
			return 0.0;
		}
	}

	public Double getActualStoryPoints() {
		try {
			return (Double) issue.getCustomFieldValue(this.customFieldManager
					.getCustomFieldObject(CustomFieldHelper.ACTUAL_STORY_POINTS
							.getFieldName()));
		} catch (NullPointerException e) {
			return 0.0;
		}
	}

	public String getSummary() {
		return this.issue.getSummary();
	}

	public String getEpic() {
		if (IssueFunctions.hasEpic(this.issue, this.customFieldManager)) {
			
			return IssueFunctions.getEpicName(this.issue, this.customFieldManager);
			
		} else if (IssueFunctions.hasParent(this.issue)) {
			
			if (IssueFunctions.hasEpic(IssueFunctions.getParent(this.issue), this.customFieldManager)) {
				return IssueFunctions.getEpicName(IssueFunctions.getParent(this.issue), this.customFieldManager);
			} else {
				return "";
			}
			
		} else {
			
			return "";
			
		}
	}
	
	public List<String> getSubtasks() {
		if (this.subTasks == null) {
			this.subTasks = IssueFunctions.getSubTasks(this.issue);
		}
		return this.subTasks;
	}

	public List<String> getSubtasksTruncated() {
		return this.getSubtasks().subList(0, Math.min(this.getNumSubtasks(), IssueHelper.NUM_SUBTASKS_TO_SHOW));
	}

	public int getNumSubtasks() {
		return this.getSubtasks().size();
	}
	
	private String getRestSubtasks() {
		if (this.getNumSubtasks() > IssueHelper.NUM_SUBTASKS_TO_SHOW) {
			String retString;
			int delta = this.getNumSubtasks() - IssueHelper.NUM_SUBTASKS_TO_SHOW;

			if (delta == 1) {
				retString = "... and " + delta + " other";
			} else {
				retString = "... and " + delta + " others";
			}
						
			return retString;
		}
		
		return null;
	}

	public String getParent() {
		if (IssueFunctions.hasParent(this.issue)) {
			Issue parent = IssueFunctions.getParent(this.issue);
			return parent.getKey() + ": " + parent.getSummary();
		} else {
			return "";
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
		issueMap.put("estimate", Utility.emptyStrOnFalsey(this.getEstimatedStoryPoints()));
		issueMap.put("actual", Utility.emptyStrOnFalsey(this.getActualStoryPoints()));
		issueMap.put("summary", this.getSummary());
		issueMap.put("epic", this.getEpic());
		issueMap.put("subtasks", this.getSubtasksTruncated());
		issueMap.put("numsubtasks", this.getNumSubtasks());
		issueMap.put("restsubtasks", Utility.emptyStrOnFalsey(this.getRestSubtasks()));
		issueMap.put("parent", this.getParent());
		issueMap.put("priority", this.getPriority());

		return issueMap;
	}

	

}
