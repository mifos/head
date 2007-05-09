package org.mifos.application.productdefinition.util.helpers;


public enum ProductType {
	LOAN((short) 1), SAVINGS((short) 2);

	Short value;

	ProductType(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

	public static ProductType getProductType(int value) {
		for (ProductType productType : ProductType.values()) {
			if (productType.getValue() == value) {
				return productType;
			}
		}
		throw new RuntimeException("no product type " + value);
	}
}
