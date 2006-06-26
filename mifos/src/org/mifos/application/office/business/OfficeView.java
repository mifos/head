package org.mifos.application.office.business;

import org.mifos.framework.business.View;

public class OfficeView extends View{
	private Short officeId;
	private String officeName;
	private Short levelId;
	private Integer versionNo;
	
	/**
	 * @return Returns the levelId.
	 */
	public Short getLevelId() {
		return levelId;
	}

	/**
	 * @param levelId The levelId to set.
	 */
	public void setLevelId(Short levelId) {
		this.levelId = levelId;
	}

	/**
	 * @return Returns the officeId.
	 */
	public Short getOfficeId() {
		return officeId;
	}

	/**
	 * @param officeId The officeId to set.
	 */
	public void setOfficeId(Short officeId) {
		this.officeId = officeId;
	}

	/**
	 * @return Returns the officeName.
	 */
	public String getOfficeName() {
		return officeName;
	}

	/**
	 * @param officeName The officeName to set.
	 */
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	/**
	 * @return Returns the versionNo.
	 */
	public Integer getVersionNo() {
		return versionNo;
	}

	/**
	 * @param versionNo The versionNo to set.
	 */
	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}

	public OfficeView(Short officeId,String officeName,Short levelId,Integer versionNo){
		this.officeId=officeId;
		this.officeName=officeName;
		this.levelId=levelId;
		this.versionNo=versionNo;
	}
	
}
