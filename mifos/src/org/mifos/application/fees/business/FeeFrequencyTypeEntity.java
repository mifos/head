package org.mifos.application.fees.business;

import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.framework.business.PersistentObject;

public class FeeFrequencyTypeEntity extends PersistentObject {

	public FeeFrequencyTypeEntity() {
	}

	private Short feeFrequencyTypeId;

	public Short getFeeFrequencyTypeId() {
		return feeFrequencyTypeId;
	}

	public void setFeeFrequencyTypeId(Short feeFrequencyTypeId) {
		this.feeFrequencyTypeId = feeFrequencyTypeId;
	}

	public boolean isPeriodic() {
		return getFeeFrequencyTypeId().equals(
				FeeFrequencyType.PERIODIC.getValue());

	}

	public boolean isOneTime() {
		return getFeeFrequencyTypeId().equals(
				FeeFrequencyType.ONETIME.getValue());

	}
}
