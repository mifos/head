package org.mifos.application.productdefinition.business;


import static org.mifos.framework.util.helpers.NumberUtils.isBetween;

import org.mifos.framework.business.PersistentObject;

public abstract class AmountRange extends PersistentObject {
	private Double minLoanAmount;
	private Double maxLoanAmount;

	public AmountRange(Double minLoanAmount, Double maxLoanAmount) {
		super();
		this.minLoanAmount = minLoanAmount;
		this.maxLoanAmount = maxLoanAmount;
	}

	public Double getMaxLoanAmount() {
		return maxLoanAmount;
	}

	public void setMaxLoanAmount(Double maxLoanAmount) {
		this.maxLoanAmount = maxLoanAmount;
	}

	public Double getMinLoanAmount() {
		return minLoanAmount;
	}

	public void setMinLoanAmount(Double minLoanAmount) {
		this.minLoanAmount = minLoanAmount;
	}

	public boolean isInRange(Double amount) {
		return isBetween(minLoanAmount, maxLoanAmount, amount);
	}
}
