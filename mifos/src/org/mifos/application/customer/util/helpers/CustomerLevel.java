package org.mifos.application.customer.util.helpers;


public enum CustomerLevel {

	CLIENT(Short.valueOf("1")), GROUP(Short.valueOf("2")), CENTER(Short.valueOf("3"));

	Short value;

	CustomerLevel(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}
	
	public static CustomerLevel getLevel(Short value){
		for(CustomerLevel level : CustomerLevel.values())
			if(level.getValue().equals(value))
				return level;
		return null;
	}
}
