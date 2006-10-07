package org.mifos.application.checklist.util.helpers;

public class CheckListMasterView {

	private boolean isCustomer;

	private String masterTypeName;

	private Short masterTypeId;

	public CheckListMasterView() {

	}

	public CheckListMasterView(Short id, String name) {
		this.masterTypeId = id;
		this.masterTypeName = name;
	}

	public Short getMasterTypeId() {
		return masterTypeId;
	}

	public void setMasterTypeId(Short masterTypeId) {
		this.masterTypeId = masterTypeId;
	}

	public String getMasterTypeName() {
		return masterTypeName;
	}

	public void setMasterTypeName(String masterTypeName) {
		this.masterTypeName = masterTypeName;
	}

	public boolean getIsCustomer() {
		return isCustomer;
	}

	public void setIsCustomer(boolean isCustomer) {
		this.isCustomer = isCustomer;
	}



}
