package org.mifos.application.office.business;

import org.mifos.application.master.MessageLookup;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.framework.business.View;

/*
 * Feb 2008 i18n work in progress.
 * looks like we need to make officeName, officeNameKey and
 * levelName, levelNameKey and then go through MessageLookup to resolve them
 */
public class OfficeView extends View {

	private Short officeId;

	private String officeName;

	private Short levelId;

	private String levelNameKey;

	private Integer versionNo;

	public OfficeView(Short officeId, String officeName, Short levelId,
			Integer versionNo) {
		this.officeId = officeId;
		this.officeName = officeName;
		this.levelId = levelId;
		this.versionNo = versionNo;
	}
	
	public OfficeView(Short officeId, String officeName,
			OfficeLevel level, String levelNameKey, Integer versionNo) {
		this(officeId, officeName, level.getValue(), levelNameKey, versionNo);
	}

	public OfficeView(Short officeId, String officeName, Short levelId,
			String levelNameKey, Integer versionNo) {
		this.officeId = officeId;
		this.officeName = officeName;
		this.levelId = levelId;
		this.levelNameKey = levelNameKey;
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
		return MessageLookup.getInstance().lookup(levelNameKey);
	}

	public OfficeView(Short levelId, String levelNameKey) {
		this.levelId = levelId;
		this.levelNameKey = levelNameKey;
	}

	public String getDisplayName() {
		return getLevelName() + "(" + officeName + ")";
	}
}
