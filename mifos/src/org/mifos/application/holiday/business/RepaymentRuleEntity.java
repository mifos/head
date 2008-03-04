package org.mifos.application.holiday.business;

import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.framework.business.BusinessObject;

public class RepaymentRuleEntity extends BusinessObject {

	private LookUpValueEntity lookUpObject;
	
	private String lookUpValueKey;

	private Short id;

	protected RepaymentRuleEntity() {
		lookUpValueKey = null;
	       lookUpObject = null;
	}

	public RepaymentRuleEntity(Short id, String lookUpValueKey) {
		this.id = id;
	    this.lookUpValueKey = lookUpValueKey;
	}
	
	public Short getId() {
		return this.id;
	}

	public LookUpValueEntity getLookUpObject() {
		return this.lookUpObject;
	}

	public String getLookUpValue() {
		return MessageLookup.getInstance().lookup(lookUpValueKey);
	}
	
	private void setId(Short Id) {
		this.id = Id;		
	}

	private void setLookUpValueKey(String lookUpValueKey) {
		this.lookUpValueKey = lookUpValueKey;
	}
	
	private void setLookUpValue(LookUpValueEntity lookUpObject) {
		this.lookUpObject = lookUpObject;
	}
}
