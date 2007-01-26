package org.mifos.application.holiday.business;

import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.framework.business.BusinessObject;

public class RepaymentRuleEntity extends BusinessObject {

	private LookUpValueEntity lookUpObject;
	
	private String lookUpValue;

	private Short id;

	protected RepaymentRuleEntity() {
	       lookUpValue = null;
	       lookUpObject = null;
	}

	public RepaymentRuleEntity(Short id, String lookUpValue) {
		this.id = id;
	    this.lookUpValue = lookUpValue;
	}
	
	public Short getId() {
		return this.id;
	}

	public LookUpValueEntity getLookUpObject() {
		return this.lookUpObject;
	}

	public String getLookUpValue() {
		return this.lookUpValue;
	}
	
	private void setId(Short Id) {
		this.id = Id;		
	}

	private void setLookUpValue(String lookUpValue) {
		this.lookUpValue = lookUpValue;
	}
	
	private void setLookUpValue(LookUpValueEntity lookUpObject) {
		this.lookUpObject = lookUpObject;
	}
}
