package org.mifos.application.office.business;

import org.mifos.framework.business.View;

public class OfficeView extends View {

	private Short officeId;

	private String officeName;

	private Short levelId;

	private String levelName;

	private Integer versionNo;

	public OfficeView(Short officeId, String officeName, Short levelId,
			Integer versionNo) {
		this.officeId = officeId;
		this.officeName = officeName;
		this.levelId = levelId;
		this.versionNo = versionNo;
	}

	public OfficeView(Short officeId, String officeName, Short levelId,
			String levelName, Integer versionNo) {
		this.officeId = officeId;
		this.officeName = officeName;
		this.levelId = levelId;
		this.levelName = levelName;
		this.versionNo = versionNo;
	}

	public OfficeView(Short officeId, String officeName, Integer versionNo) {
		this.officeId = officeId;
		this.officeName = officeName;
		this.versionNo = versionNo;
	}

	public Short getLevelId() {
		return levelId;
	}

	public Short getOfficeId() {
		return officeId;
	}


	public String getOfficeName() {
		return officeName;
	}

	public Integer getVersionNo() {
		return versionNo;
	}

	public String getLevelName() {
		return levelName;
	}

	public OfficeView(Short levelId, String levelName) {
		this.levelId = levelId;
		this.levelName = levelName;
	}

	public String getDisplayName() {
		return levelName + "(" + officeName + ")";
	}
}
