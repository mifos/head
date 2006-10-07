package org.mifos.application.util.helpers;

import org.mifos.application.customer.util.helpers.CustomerStatus;

public enum EntityType {

	PERSONNEL(Short.valueOf("17")), 
	CENTER(Short.valueOf("20")), 
	CLIENT(Short.valueOf("1")),
	OFFICE(Short.valueOf("15")), GROUP(Short.valueOf("12")),
	LOAN(Short.valueOf("22")),
	CUSTOMER(Short.valueOf("11")),
	SAVING(Short.valueOf("21"));

	Short value;

	EntityType(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}
	
	public static Short getEntityValue(String entity){
		for(EntityType entityType : EntityType.values())
			if(entityType.name().equals(entity))
				return entityType.getValue();
		return null;
	}
	
}
