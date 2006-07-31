package org.mifos.application.customer.util.helpers;


public enum CustomerStatus {
	CENTER_ACTIVE((short) 13), CENTER_INACTIVE((short) 14);

	Short value;

	CustomerStatus(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}
	
	public static CustomerStatus getStatus(Short value){
		for(CustomerStatus status : CustomerStatus.values())
			if(status.getValue().equals(value))
				return status;
		return null;
	}
}
