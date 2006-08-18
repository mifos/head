package org.mifos.application.office.struts.actionforms;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.struts.actionforms.BaseActionForm;

public class OffActionForm extends BaseActionForm {
	private String officeId;

	private String officeName;

	private String shortName;

	private String officeLevel;

	private String parentOfficeId;

	private String officeStatus;

	private Address address;

	private List<CustomFieldView> customFields;

	public OffActionForm() {
		this.customFields = new ArrayList<CustomFieldView>();
		this.address = new Address();

	}

	public List<CustomFieldView> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(List<CustomFieldView> customFields) {
		this.customFields = customFields;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getOfficeLevel() {
		return officeLevel;
	}

	public void setOfficeLevel(String officeLevel) {
		this.officeLevel = officeLevel;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public String getOfficeStatus() {
		return officeStatus;
	}

	public void setOfficeStatus(String officeStatus) {
		this.officeStatus = officeStatus;
	}

	public String getParentOfficeId() {
		return parentOfficeId;
	}

	public void setParentOfficeId(String parentOfficeId) {
		this.parentOfficeId = parentOfficeId;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public CustomFieldView getCustomField(int i){
		while(i>=customFields.size()){
			customFields.add(new CustomFieldView());
		}
		return (CustomFieldView)(customFields.get(i));
	}
	public void clear(){
		this.officeId="";
		this.officeLevel="";
		this.officeName="";
		this.officeStatus="";
		this.parentOfficeId="";
		this.customFields = new ArrayList<CustomFieldView>();
		this.address = new Address();

	}
}
