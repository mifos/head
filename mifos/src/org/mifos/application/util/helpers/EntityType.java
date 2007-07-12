package org.mifos.application.util.helpers;


public enum EntityType {

	CLIENT(1),
	LOANPRODUCT(2),
	SAVINGSPRODUCT(3), 
	PRODUCT_CATEGORY(4),
	PRODUCT_CONFIGURATION(5),
	FEES(6),
	ACCOUNTS(7),
	ADMIN(8),
	CHECKLIST(9),
	CONFIGURATION(10),
	CUSTOMER(11),
	GROUP(12),
	LOGIN(13),
	MEETING(14),
	OFFICE(15), 
	PENALTY(16),
	PERSONNEL(17), 
	PROGRAM(18),
	ROLE_AND_PERMISSION(19),
	CENTER(20), 
	SAVINGS(21),
	LOAN(22),
	BULK_ENTRY(23)
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
