package org.mifos.application.office.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

public class OfficeMaster extends ValueObject{
	private short officeId;
	private String officeName;
	private short levelId;
	private Integer versionNo;
	
	public OfficeMaster(short officeId,String officeName,Short levelId,Integer versionNo){
		this.officeId=officeId;
		this.officeName=officeName;
		this.levelId=levelId;
		this.versionNo=versionNo;
	}
	public short getOfficeId() {
		return officeId;
	}
	public void setOfficeId(short officeId) {
		this.officeId = officeId;
	}
	public String getOfficeName() {
		return officeName;
	}
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	public Integer getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}
	public short getLevelId() {
		return levelId;
	}
	public void setLevelId(short levelId) {
		this.levelId = levelId;
	}
}
