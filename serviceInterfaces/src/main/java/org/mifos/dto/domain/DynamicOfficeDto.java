package org.mifos.dto.domain;


public class DynamicOfficeDto {
	
	public int officeId;
	public int customerId;
	public String displayName;
	public int officeLevelId;
	public int customerLevelId;
	public String globalOfficeNumber;
	public String globalCustomerNumber;
	
	public String getGlobalCustomerNumber() {
		return globalCustomerNumber;
	}
	public void setGlobalCustomerNumber(String globalCustomerNumber) {
		this.globalCustomerNumber = globalCustomerNumber;
	}
	public int getCustomerLevelId() {
		return customerLevelId;
	}
	public void setCustomerLevelId(int customerLevelId) {
		this.customerLevelId = customerLevelId;
	}
	public int getOfficeId() {
		return officeId;
	}
	public void setOfficeId(int officeId) {
		this.officeId = officeId;
	}
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public int getOfficeLevelId() {
		return officeLevelId;
	}
	public void setOfficeLevelId(int officeLevelId) {
		this.officeLevelId = officeLevelId;
	}
	public String getGlobalOfficeNumber() {
		return globalOfficeNumber;
	}
	public void setGlobalOfficeNumber(String globalOfficeNumber) {
		this.globalOfficeNumber = globalOfficeNumber;
	}
	
	

}
