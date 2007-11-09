
package org.mifos.application.productdefinition.business;

import org.mifos.framework.business.PersistentObject;


public class LoanAmountFromLoanCycleBO extends PersistentObject {

	private Double minLoanAmount;
	private Double maxLoanAmount;
	private Double defaultLoanAmount;
	private Short rangeIndex;
	private final LoanOfferingBO loanOffering;
	private final Short loanAmountFromLoanCycleID;

	public Double getDefaultLoanAmount() {
		return defaultLoanAmount;
	}

	public void setDefaultLoanAmount(Double defaultLoanAmount) {
		this.defaultLoanAmount = defaultLoanAmount;
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


	public LoanAmountFromLoanCycleBO(Double minLoanAmount,
			Double maxLoanAmount, Double defaultLoanAmount, Short rangeIndex,
			LoanOfferingBO loanOffering) {
		this.minLoanAmount = minLoanAmount;
		this.maxLoanAmount = maxLoanAmount;
		this.defaultLoanAmount = defaultLoanAmount;
		this.loanOffering = loanOffering;
		this.rangeIndex = rangeIndex;
		this.loanAmountFromLoanCycleID = null;
	}

	public LoanAmountFromLoanCycleBO() {
		this.minLoanAmount = null;
		this.maxLoanAmount = null;
		this.defaultLoanAmount = null;
		this.loanOffering = null;
		this.loanAmountFromLoanCycleID = null;
		this.rangeIndex = null;
	}

	public Short getRangeIndex() {
		return rangeIndex;
	}

	public void setRangeIndex(Short rangeIndex) {
		this.rangeIndex = rangeIndex;
	}


}
