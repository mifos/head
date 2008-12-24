package org.mifos.application.personnel.business;

import org.mifos.framework.business.View;

public class PersonnelView extends View {

	private Short personnelId;

	private String displayName;

	public PersonnelView(Short personnelId, String displayName) {
		this.personnelId = personnelId;
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}


	public Short getPersonnelId() {
		return personnelId;
	}

}
