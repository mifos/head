package org.mifos.dto.domain;

public class OfficeHierarchy {
	
	private String officeId;
	private String officeLevel;
	
	public OfficeHierarchy(String officeId, String officeLevel){
		this.officeId = officeId;
		this.officeLevel = officeLevel;
	}
	
	public String getOfficeId() {
		return officeId;
	}
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}
	public String getOfficeLevel() {
		return officeLevel;
	}
	public void setOfficeLevel(String officeLevel) {
		this.officeLevel = officeLevel;
	}

}
