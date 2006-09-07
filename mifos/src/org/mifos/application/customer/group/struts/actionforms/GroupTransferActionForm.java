package org.mifos.application.customer.group.struts.actionforms;

import org.mifos.framework.struts.actionforms.BaseActionForm;

public class GroupTransferActionForm extends BaseActionForm{
	private String officeId;
	private String officeName;
	private String centerSystemId;
	private String centerName;
	
	public String getCenterSystemId() {
		return centerSystemId;
	}
	
	public void setCenterSystemId(String centerSystemId) {
		this.centerSystemId = centerSystemId;
	}
	
	public String getCenterName() {
		return centerName;
	}
	
	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}
	
	public String getOfficeId() {
		return officeId;
	}
	
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}
	
	public String getOfficeName() {
		return officeName;
	}
	
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	
	public Short getOfficeIdValue() {
		return getShortValue(officeId);
	}
}
