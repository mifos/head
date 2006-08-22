package org.mifos.application.productdefinition.util.helpers;

public enum PrdCategoryStatus{

	ACTIVE ((short)1), INACTIVE ((short)0);
 
 	Short value;

 	PrdCategoryStatus(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}
}
