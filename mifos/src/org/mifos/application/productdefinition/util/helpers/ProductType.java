package org.mifos.application.productdefinition.util.helpers;

import org.mifos.framework.exceptions.PropertyNotFoundException;

public enum ProductType {
	LOAN((short) 1), SAVINGS((short) 2);

	Short value;

	ProductType(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

	public static ProductType getProductType(Short value)
			throws PropertyNotFoundException {
		for (ProductType productType : ProductType.values())
			if (productType.getValue().equals(value))
				return productType;
		throw new PropertyNotFoundException("ProductType");
	}
}
