package org.mifos.application.reports.business;

import java.util.HashSet;
import java.util.Set;

import org.mifos.framework.business.BusinessObject;

public class ReportsCategoryBO extends BusinessObject {
	
	public static short ANALYSIS = 6;
	
	public ReportsCategoryBO(){
		reportsSet = new HashSet<ReportsBO>();
	}
	
	private Short reportCategoryId;
	private String reportCategoryName;	
	private Set<ReportsBO> reportsSet;
	private Short activityId;
	
	public Short getReportCategoryId() {
		return reportCategoryId;
	}

	public void setReportCategoryId(Short reportCategoryId) {
		this.reportCategoryId = reportCategoryId;
	}

	public String getReportCategoryName() {
		return reportCategoryName;
	}

	public void setReportCategoryName(String reportCategoryName) {
		this.reportCategoryName = reportCategoryName;
	}
	
	public Set<ReportsBO> getReportsSet() {
		return reportsSet;
	}

	@SuppressWarnings("unused")
	private void setReportsSet(Set<ReportsBO> reportsSet) {
		this.reportsSet = reportsSet;
	}

	public void addReports(ReportsBO reportsBO){		
		this.reportsSet.add(reportsBO);
	}
	
	public void setActivityId(Short activityId){
		this.activityId = activityId;
	}
	
	public Short getActivityId(){
		return activityId;
	}

}
