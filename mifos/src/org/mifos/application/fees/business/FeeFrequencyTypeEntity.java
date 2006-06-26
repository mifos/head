package org.mifos.application.fees.business;

import org.mifos.framework.business.PersistentObject;

public class FeeFrequencyTypeEntity extends PersistentObject {

	public FeeFrequencyTypeEntity(){}
	
	public Short feeFrequencyTypeId;

	public Short getFeeFrequencyTypeId() {
		return feeFrequencyTypeId;
	}
	
	public void setFeeFrequencyTypeId(Short feeFrequencyTypeId) {
		this.feeFrequencyTypeId = feeFrequencyTypeId;
	}
	
}
