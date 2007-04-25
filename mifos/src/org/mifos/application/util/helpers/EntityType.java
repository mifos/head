package org.mifos.application.util.helpers;


public enum EntityType {

	PERSONNEL(Short.valueOf("17")), 
	CENTER(Short.valueOf("20")), 
	CLIENT(Short.valueOf("1")),
	OFFICE(Short.valueOf("15")), 
	GROUP(Short.valueOf("12")),
	LOAN(Short.valueOf("22")),
	CUSTOMER(Short.valueOf("11")),
	SAVINGS(Short.valueOf("21")),
	LOANPRODUCT(Short.valueOf("2")),
	SAVINGSPRODUCT(Short.valueOf("3")),
	
	ACCOUNT_ACTION((short)69),
	;

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

	public static EntityType fromInt(int type) {
		for (EntityType candidate : EntityType.values()) {
			if (candidate.getValue() == type) {
				return candidate;
			}
		}
		throw new RuntimeException("no entity type " + type);
	}
	
}
