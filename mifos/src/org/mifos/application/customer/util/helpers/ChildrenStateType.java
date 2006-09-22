package org.mifos.application.customer.util.helpers;

public enum ChildrenStateType {
	
	ALL (Short.valueOf("1")), OTHER_THAN_CLOSED(Short.valueOf("2")), 
	OTHER_THAN_CANCELLED_AND_CLOSED(Short.valueOf("3")), ACTIVE_AND_ONHOLD (Short.valueOf("1"));
	
	Short value;

	ChildrenStateType(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}	
}
