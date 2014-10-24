package com.caplin.jacm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.caplin.jacm.functions.Utility;
import com.caplin.jacm.helpers.CustomFieldHelper;
import com.caplin.jacm.helpers.PriorityHelper;
import com.caplin.jacm.services.IssueHelperService;

public class IssueFormatter {

	private static final int NUM_SUBTASKS_TO_SHOW = 7;
	private static final int NUM_SUMMARY_CHARS_TO_SHOW_NO_SUBTASKS = 370;
	private static final int NUM_SUMMARY_CHARS_TO_SHOW_WHEN_SUBTASKS = 255;
	private static final String TRUNCATE_STR = "...";
	
	private final Issue issue;
	private List<String> subTasks;
	private final IssueHelperService issueHelperService;
	private final CustomFieldManager customFieldManager;

	public IssueFormatter(Issue issue, IssueHelperService issueHelperService, CustomFieldManager customFieldManager) {
		this.issue = issue;
		this.issueHelperService = issueHelperService;
		this.customFieldManager = customFieldManager;
	}

	private String getIssueType() {
		return this.issue.getIssueTypeObject().getName();
	}

	private String getProjectKey() {
		return this.issue.getProjectObject().getKey();
	}

	private Long getProjectIssueNumber() {
		int startLoc = this.issue.getKey().indexOf("-");
		return Long.valueOf(this.issue.getKey().substring(startLoc + 1));
	}

	private Double getEstimatedStoryPoints() {
		try {
			return (Double) issue.getCustomFieldValue(
					this.customFieldManager.getCustomFieldObject(
							CustomFieldHelper.ESTIMATED_STORY_POINTS.getFieldName()));
		} catch (NullPointerException e) {
			return null;
		}
	}

	private Double getActualStoryPoints() {
		try {
			return (Double) issue.getCustomFieldValue(
					this.customFieldManager.getCustomFieldObject(
							CustomFieldHelper.ACTUAL_STORY_POINTS.getFieldName()));
		} catch (NullPointerException e) {
			return null;
		}
	}

	private String getSummary() {
		int maxLen;
		if (this.issueHelperService.hasSubTasks(this.issue)) {
			maxLen = IssueFormatter.NUM_SUMMARY_CHARS_TO_SHOW_WHEN_SUBTASKS;
		} else {
			maxLen = IssueFormatter.NUM_SUMMARY_CHARS_TO_SHOW_NO_SUBTASKS;
		}
		maxLen = maxLen - this.getParent().length() - IssueFormatter.TRUNCATE_STR.length();
		return this.truncateStringToLength(this.issue.getSummary(), maxLen);
	}
	
	private String truncateStringToLength(String str, int toLen) {
		String truncatedStr;
		if (str.length() <= toLen) {
			return str;
		} else {
			truncatedStr = str.substring(0, toLen - 1);
			int lastSpaceLoc = truncatedStr.lastIndexOf(" ");
			truncatedStr.substring(0, lastSpaceLoc);
			
			truncatedStr = truncatedStr + IssueFormatter.TRUNCATE_STR;
		}
		return truncatedStr;
	}

	private String getEpic() {
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
	
	private List<String> getSubtasks() {
		if (this.subTasks == null) {
			this.subTasks = new ArrayList<String> ();
			
			for (Issue subTask: this.issueHelperService.getSubTasks(this.issue)) {
				this.subTasks.add(subTask.getKey());
			}
		}
		return this.subTasks;
	}

	private List<String> getSubtasksTruncated() {
		return this.getSubtasks().subList(0, Math.min(this.getNumSubtasks(), IssueFormatter.NUM_SUBTASKS_TO_SHOW));
	}

	private int getNumSubtasks() {
		return this.getSubtasks().size();
	}
	
	private String getRestSubtasks() {
		if (this.getNumSubtasks() > IssueFormatter.NUM_SUBTASKS_TO_SHOW) {
			int delta = this.getNumSubtasks() - IssueFormatter.NUM_SUBTASKS_TO_SHOW;

			String retString = "... and " + delta + ((delta == 1) ? "other" : "others");

			return retString;
		} else {
			return null;
		}
	}

	private String getParent() {
		if (this.issueHelperService.hasParent(this.issue)) {
			Issue parent = this.issueHelperService.getParent(this.issue);
			return parent.getKey() + ": " + parent.getSummary();
		} else {
			return "";
		}
	}

	private PriorityHelper getPriority() {
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
		issueMap.put("parent", this.getParent());
		issueMap.put("priority", this.getPriority());

		return issueMap;
	}

	

}
