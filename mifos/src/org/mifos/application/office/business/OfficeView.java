package org.mifos.application.office.business;

import org.mifos.framework.business.View;

public class OfficeView extends View{
	private Short officeId;
	private String officeName;
	private Short levelId;
	private String levelName;
	private Integer versionNo;
	
	public Short getLevelId() {
		return levelId;
	}
	public void setLevelId(Short levelId) {
		this.levelId = levelId;
	}
	public Short getOfficeId() {
		return officeId;
	}
	public void setOfficeId(Short officeId) {
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
	public OfficeView(Short officeId,String officeName,Short levelId,Integer versionNo){
		this.officeId=officeId;
		this.officeName=officeName;
		this.levelId=levelId;
		this.versionNo=versionNo;
	}
	public OfficeView(Short officeId, String officeName, Short levelId,String levelName, Integer versionNo) {
		this.officeId = officeId;
		this.officeName = officeName;
		this.levelId = levelId;
		this.levelName = levelName;
		this.versionNo = versionNo;
	}
	public String getLevelName() {
		return levelName;
	}
	public OfficeView(Short levelId, String levelName) {
		this.levelId = levelId;
		this.levelName = levelName;
	}
	public String getDisplayName (){
		return levelName + "("+officeName+")";
	}
}
