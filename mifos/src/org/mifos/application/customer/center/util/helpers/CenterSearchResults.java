package org.mifos.application.customer.center.util.helpers;

import org.mifos.framework.business.View;

public class CenterSearchResults extends View {

	private short parentOfficeId;

	private String parentOfficeName;

	private String centerSystemId;

	private String centerName;

	public String getCenterSystemId() {
		return centerSystemId;
	}

	public void setCenterSystemId(String centerId) {
		this.centerSystemId = centerId;
	}

	public String getCenterName() {
		return centerName;
	}

	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}

	public short getParentOfficeId() {
		return parentOfficeId;
	}

	public void setParentOfficeId(short parentOfficeId) {
		this.parentOfficeId = parentOfficeId;
	}

	public String getParentOfficeName() {
		return parentOfficeName;
	}

	public void setParentOfficeName(String parentOfficeName) {
		this.parentOfficeName = parentOfficeName;
	}
}
