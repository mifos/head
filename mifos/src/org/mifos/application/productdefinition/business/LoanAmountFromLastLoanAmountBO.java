package org.mifos.application.productdefinition.business;

import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.business.PersistentObject;


public class LoanAmountFromLastLoanAmountBO extends PersistentObject {

	private Double minLoanAmount;
	private Double maxLoanAmount;
	private Double defaultLoanAmount;
	private Double endRange;
	private Double startRange;
	private final LoanOfferingBO loanOffering;
	private final Short loanAmountFromLastLoanID;


	public Double getDefaultLoanAmount() {
		return defaultLoanAmount;
	}

	public Double getMaxLoanAmount() {
		return maxLoanAmount;
	}


	public Double getMinLoanAmount() {
		return minLoanAmount;
	}


	public LoanAmountFromLastLoanAmountBO(Double minLoanAmount,
			Double maxLoanAmount, Double defaultLoanAmount, Double startRange,
			Double endRange, LoanOfferingBO loanOffering) {
		this.minLoanAmount = minLoanAmount;
		this.maxLoanAmount = maxLoanAmount;
		this.defaultLoanAmount = defaultLoanAmount;
		this.endRange = endRange;
		this.startRange = startRange;
		this.loanOffering = loanOffering;
		this.loanAmountFromLastLoanID = null;
	}

	public LoanAmountFromLastLoanAmountBO() {
		this.minLoanAmount = null;
		this.maxLoanAmount = null;
		this.defaultLoanAmount = null;
		this.endRange = null;
		this.startRange = null;
		this.loanOffering = null;
		this.loanAmountFromLastLoanID = null;
	}

	public Double getEndRange() {
		return endRange;
	}

	public Double getStartRange() {
		return startRange;
	}


	public Short getLoanAmountFromLastLoanID() {
		return loanAmountFromLastLoanID;
	}

	public LoanOfferingBO getLoanOffering() {
		return loanOffering;
	}

	public void setDefaultLoanAmount(Double defaultLoanAmount) {
		this.defaultLoanAmount = defaultLoanAmount;
	}

	public void setEndRange(Double endRange) {
		this.endRange = endRange;
	}

	public void setMaxLoanAmount(Double maxLoanAmount) {
		this.maxLoanAmount = maxLoanAmount;
	}

	public void setMinLoanAmount(Double minLoanAmount) {
		this.minLoanAmount = minLoanAmount;
	}

	public void setStartRange(Double startRange) {
		this.startRange = startRange;
	}

}
