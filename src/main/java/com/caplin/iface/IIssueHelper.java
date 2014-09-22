package com.caplin.iface;

import java.util.Map;

import com.caplin.PriorityHelper;
import com.caplin.functional.Functions;

public interface IIssueHelper {

	public String getIssueType();
	public String getProjectKey();
	public Long getProjectIssueNumber();
	public Float getEstimatedStoryPoints();
	public Float getActualStoryPoints();
	public String getSummary();
	public String getEpic();
	public String getParent();
	public String getSubtasks();
	public int getNumSubtasks();
	public PriorityHelper getPriority();
	public Map<String, Object> toMap();
	
}