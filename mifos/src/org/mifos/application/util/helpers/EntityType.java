package org.mifos.application.util.helpers;


public enum EntityType {

	PERSONNEL(17), 
	CENTER(20), 
	CLIENT(1),
	OFFICE(15), 
	GROUP(12),
	LOAN(22),
	CUSTOMER(11),
	SAVINGS(21),
	LOANPRODUCT(2),
	SAVINGSPRODUCT(3),
	;

	Short value;

	EntityType(int value) {
		this.value = (short)value;
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

	public static EntityType fromInt(int type) {
		for (EntityType candidate : EntityType.values()) {
			if (candidate.getValue() == type) {
				return candidate;
			}
		}
		throw new RuntimeException("no entity type " + type);
	}
	
}
