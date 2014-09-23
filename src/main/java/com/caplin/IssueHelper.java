package com.caplin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.caplin.functional.Functions;
import com.caplin.functional.IssueFns;
import com.caplin.iface.IIssueHelper;

public class IssueHelper implements IIssueHelper {

	private Issue issue = null;
	private final CustomFieldManager customFieldManager;

	public IssueHelper(CustomFieldManager customFieldManager, Issue issue) {
		this.customFieldManager = customFieldManager;
		this.issue = issue;
	}

	@Override
	public String getIssueType() {
		return this.issue.getIssueTypeObject().getName();
	}

	@Override
	public String getProjectKey() {
		return this.issue.getProjectObject().getKey();
	}

	@Override
	public Long getProjectIssueNumber() {
		int startLoc = this.issue.getKey().indexOf("-");
		return Long.valueOf(this.issue.getKey().substring(startLoc + 1));
	}

	@Override
	public Float getEstimatedStoryPoints() {
		try {
			return (Float) issue.getCustomFieldValue(this.customFieldManager
				.getCustomFieldObject(CustomFieldHelper.ESTIMATED_STORY_POINTS
						.getFieldName()));
		} catch (NullPointerException e) {
			return 0.0f;
		}
	}

	@Override
	public Float getActualStoryPoints() {
		try {
			return (Float) issue.getCustomFieldValue(this.customFieldManager
					.getCustomFieldObject(CustomFieldHelper.ACTUAL_STORY_POINTS
							.getFieldName()));
		} catch (NullPointerException e) {
			return 0.0f;
		}
	}

	@Override
	public String getSummary() {
		return this.issue.getSummary();
	}

	@Override
	public String getEpic() {
		if (IssueFns.hasEpic(this.issue, this.customFieldManager)) {
			
			return IssueFns.getEpicName(this.issue, this.customFieldManager);
			
		} else if (IssueFns.hasParent(this.issue)) {
			
			if (IssueFns.hasEpic(IssueFns.getParent(this.issue), this.customFieldManager)) {
				return IssueFns.getEpicName(IssueFns.getParent(this.issue), this.customFieldManager);
			} else {
				return "";
			}
			
		} else {
			
			return "";
			
		}
	}

	@Override
	public List<String> getSubtasks() {
		return IssueFns.getSubTasks(this.issue);
	}

	@Override
	public int getNumSubtasks() {
		return IssueFns.getSubTasks(this.issue).size();
	}

	@Override
	public String getParent() {
		if (IssueFns.hasParent(this.issue)) {
			Issue parent = IssueFns.getParent(this.issue);
			return parent.getKey() + ": " + parent.getSummary();
		} else {
			return "";
		}
	}

	@Override
	public PriorityHelper getPriority() {
		return PriorityHelper.getPriorityFromString(this.issue
				.getPriorityObject().getName());
	}

	public final Map<String, Object> toMap() {

		Map<String, Object> issueMap = new HashMap<String, Object>();

		issueMap.put("issueType", this.getIssueType());
		issueMap.put("projectKey", this.getProjectKey());
		issueMap.put("projectIssueNumber", this.getProjectIssueNumber());
		issueMap.put("estimate", Functions.emptyStrOnFalsey(this.getEstimatedStoryPoints()));
		issueMap.put("actual", Functions.emptyStrOnFalsey(this.getActualStoryPoints()));
		issueMap.put("summary", this.getSummary());
		issueMap.put("epic", this.getEpic());
		issueMap.put("subtasks", this.getSubtasks());
		issueMap.put("numsubtasks", this.getNumSubtasks());
		issueMap.put("parent", this.getParent());
		issueMap.put("priority", this.getPriority());

		return issueMap;
	}

}
