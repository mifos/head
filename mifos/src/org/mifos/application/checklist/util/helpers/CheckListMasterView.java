package org.mifos.application.checklist.util.helpers;

import java.io.Serializable;

public class CheckListMasterView implements Serializable {

	private boolean isCustomer;

	private String masterTypeName;

	private Short masterTypeId;

	public CheckListMasterView(Short id, String name) {
		this.masterTypeId = id;
		this.masterTypeName = name;
	}

	public Short getMasterTypeId() {
		return masterTypeId;
	}

	public String getMasterTypeName() {
		return masterTypeName;
	}

	public boolean getIsCustomer() {
		return isCustomer;
	}

	public void setIsCustomer(boolean isCustomer) {
		this.isCustomer = isCustomer;
	}
}
