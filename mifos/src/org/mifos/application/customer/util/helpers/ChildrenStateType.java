package org.mifos.application.customer.util.helpers;

public enum ChildrenStateType {
	
	ALL (1), 
	OTHER_THAN_CLOSED(2), 
	OTHER_THAN_CANCELLED_AND_CLOSED(3), 
	ACTIVE_AND_ONHOLD (4);
	
	Short value;

	private ChildrenStateType(int value) {
		this.value = (short)value;
	}

	public Short getValue() {
		return value;
	}	
}
