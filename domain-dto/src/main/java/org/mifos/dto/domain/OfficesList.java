package org.mifos.dto.domain;

public class OfficesList {

	public int officeId;
	public String displayName;
	public int officeLevelId;
	public String globalOfficeNumber;

	public OfficesList(int officeId, String displayName, int officeLevelId,
			String globalOfficeNumber) {
		super();
		this.officeId = officeId;
		this.displayName = displayName;
		this.officeLevelId = officeLevelId;
		this.globalOfficeNumber = globalOfficeNumber;
	}

	public int getOfficeId() {
		return officeId;
	}
	public void setOfficeId(int officeId) {
		this.officeId = officeId;
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
