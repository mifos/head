package org.mifos.application.productdefinition.util.helpers;

import org.mifos.framework.exceptions.PropertyNotFoundException;

public enum RecommendedAmountUnit {
	PERINDIVIDUAL((short) 1), COMPLETEGROUP((short) 2);

	Short value;

	RecommendedAmountUnit(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

	public static RecommendedAmountUnit getRecommendedAmountUnit(Short value)
			throws PropertyNotFoundException {
		for (RecommendedAmountUnit recommendedAmountUnit : RecommendedAmountUnit
				.values())
			if (recommendedAmountUnit.getValue().equals(value))
				return recommendedAmountUnit;
		throw new PropertyNotFoundException("RecommendedAmountUnit");
	}
}
