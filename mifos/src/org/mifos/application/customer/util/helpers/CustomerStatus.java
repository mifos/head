package org.mifos.application.customer.util.helpers;

import org.mifos.application.customer.group.util.helpers.GroupConstants;



public enum CustomerStatus {
	CLIENT_PARTIAL (Short.valueOf("1")), 
	CLIENT_PENDING (Short.valueOf("2")), 
	CLIENT_ACTIVE (Short.valueOf("3")), 
	CLIENT_HOLD (Short.valueOf("4")), 
	CLIENT_CANCELLED (Short.valueOf("5")), 
	CLIENT_CLOSED (Short.valueOf("6")), 

	GROUP_PARTIAL (GroupConstants.PARTIAL_APPLICATION), 
	GROUP_PENDING (GroupConstants.PENDING_APPROVAL), 
	GROUP_ACTIVE (GroupConstants.ACTIVE),
	GROUP_HOLD (GroupConstants.HOLD), 
	GROUP_CANCELLED (GroupConstants.CANCELLED),
	GROUP_CLOSED (GroupConstants.CLOSED),

	CENTER_ACTIVE(Short.valueOf("13")), 
	CENTER_INACTIVE(Short.valueOf("14"));

	private Short value;

	private CustomerStatus(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}
	
	public static CustomerStatus fromInt(int value){
		for (CustomerStatus status : CustomerStatus.values()) {
			if (status.getValue() == value) {
				return status;
			}
		}
		throw new RuntimeException("no customer status " + value);
	}

}
