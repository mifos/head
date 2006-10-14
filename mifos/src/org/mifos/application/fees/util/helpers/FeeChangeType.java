package org.mifos.application.fees.util.helpers;

import org.mifos.framework.exceptions.PropertyNotFoundException;

public enum FeeChangeType {

	NOT_UPDATED ((short)0), 
	AMOUNT_UPDATED((short) 1), 
	STATUS_UPDATED((short) 2), 
	AMOUNT_AND_STATUS_UPDATED((short) 3);

	Short value;

	FeeChangeType(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}
	
	public static FeeChangeType getFeeChangeType(Short value)throws PropertyNotFoundException{
		for (FeeChangeType changeType : FeeChangeType.values()) 
			if (changeType.getValue().equals(value))
				return changeType;
		throw new PropertyNotFoundException("FeeChangeType");
	}

}
