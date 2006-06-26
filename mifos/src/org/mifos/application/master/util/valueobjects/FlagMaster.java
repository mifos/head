package org.mifos.application.master.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

public class FlagMaster extends ValueObject {

	private java.lang.Short statusId;

	private java.lang.Short flagId;

	private String flagName;

	public FlagMaster(Short statusId, Short flagId, String flagName) {
		this.statusId = statusId;
		this.flagId = flagId;
		this.flagName = flagName;

	}

	public java.lang.Short getStatusId() {
		return statusId;
	}

	public java.lang.Short getFlagId() {
		return flagId;
	}

	public String getFlagName() {
		return flagName;
	}

}
