package org.mifos.framework.components.configuration.business;

import org.mifos.application.office.business.OfficeBO;
import org.mifos.framework.persistence.Persistence;

public class ConfigEntity extends Persistence {

	private Short systemConfigId;
	private OfficeBO office;
	private String scheduleMeetingOnHoliday; 
	private Short daysForCalDefinition; 
	private String nameSequence; 
	private Short backDatedTrxnAllowed; 
	private Short centerHierarchyExist; 
	private Short groupCanApplyLoans; 
	private Short clientCanExistOutsideGroup; 
	private Short noOfInterestDays;

	public ConfigEntity(){}

	
	public Short getBackDatedTrxnAllowed() {
		return backDatedTrxnAllowed;
	}	
	
	public void setBackDatedTrxnAllowed(Short backDatedTrxnAllowed) {
		this.backDatedTrxnAllowed = backDatedTrxnAllowed;
	}
	
	public Short getCenterHierarchyExist() {
		return centerHierarchyExist;
	}
	
	public void setCenterHierarchyExist(Short centerHierarchyExist) {
		this.centerHierarchyExist = centerHierarchyExist;
	}
	
	public Short getClientCanExistOutsideGroup() {
		return clientCanExistOutsideGroup;
	}
	public void setClientCanExistOutsideGroup(Short clientCanExistOutsideGroup) {
		this.clientCanExistOutsideGroup = clientCanExistOutsideGroup;
	}
	
	public Short getDaysForCalDefinition() {
		return daysForCalDefinition;
	}
	
	public void setDaysForCalDefinition(Short daysForCalDefinition) {
		this.daysForCalDefinition = daysForCalDefinition;
	}
	
	public Short getGroupCanApplyLoans() {
		return groupCanApplyLoans;
	}
	
	public void setGroupCanApplyLoans(Short groupCanApplyLoans) {
		this.groupCanApplyLoans = groupCanApplyLoans;
	}
	
	public String getNameSequence() {
		return nameSequence;
	}
	
	public void setNameSequence(String nameSequence) {
		this.nameSequence = nameSequence;
	}
	
	public Short getNoOfInterestDays() {
		return noOfInterestDays;
	}
	
	public void setNoOfInterestDays(Short noOfInterestDays) {
		this.noOfInterestDays = noOfInterestDays;
	}
	
	public OfficeBO getOffice() {
		return office;
	}
	
	public void setOffice(OfficeBO office) {
		this.office = office;
	}
	
	public String getScheduleMeetingOnHoliday() {
		return scheduleMeetingOnHoliday;
	}
	
	public void setScheduleMeetingOnHoliday(String scheduleMeetingOnHoliday) {
		this.scheduleMeetingOnHoliday = scheduleMeetingOnHoliday;
	}
	
	public Short getSystemConfigId() {
		return systemConfigId;
	}
	
	public void setSystemConfigId(Short systemConfigId) {
		this.systemConfigId = systemConfigId;
	}
	
}
