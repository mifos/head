package org.mifos.application.office.util.helpers;

import org.mifos.framework.business.View;

public class OfficeView extends View {

	private short officeId;

	private String officeName;

	private short levelId;

	private Short versionNo;

	public OfficeView(short officeId, String officeName, Short levelId,
			Short versionNo) {
		this.officeId = officeId;
		this.officeName = officeName;
		this.levelId = levelId;
		this.versionNo = versionNo;
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

	public Short getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(Short versionNo) {
		this.versionNo = versionNo;
	}

	public short getLevelId() {
		return levelId;
	}

	public void setLevelId(short levelId) {
		this.levelId = levelId;
	}
}
