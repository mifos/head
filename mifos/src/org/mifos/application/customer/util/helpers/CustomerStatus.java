package org.mifos.application.customer.util.helpers;



public enum CustomerStatus {
	CLIENT_PARTIAL (Short.valueOf("1")), CLIENT_PENDING (Short.valueOf("2")), 
	CLIENT_ACTIVE (Short.valueOf("3")), 
	CLIENT_HOLD (Short.valueOf("4")), CLIENT_CANCELLED (Short.valueOf("5")), 
	CLIENT_CLOSED (Short.valueOf("6")), 
	GROUP_PARTIAL (Short.valueOf("7")), GROUP_PENDING (Short.valueOf("8")), 
	GROUP_ACTIVE (Short.valueOf("9")),
	GROUP_HOLD (Short.valueOf("10")), GROUP_CANCELLED (Short.valueOf("11")),
	GROUP_CLOSED (Short.valueOf("12")),
	CENTER_ACTIVE(Short.valueOf("13")), CENTER_INACTIVE(Short.valueOf("14"));

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
