package org.mifos.application.personnel.business;

import org.mifos.framework.business.View;

public class PersonnelView extends View {
	public PersonnelView(Short personnelId, String displayName) {
		this.personnelId = personnelId;
		this.displayName = displayName;
	}

	private Short personnelId;

	private String displayName;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Short getPersonnelId() {
		return personnelId;
	}

	public void setPersonnelId(Short personnelId) {
		this.personnelId = personnelId;
	}

}
