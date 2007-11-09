
package org.mifos.application.productdefinition.business;

import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.business.PersistentObject;


public class LoanAmountSameForAllLoanBO extends PersistentObject {

	private Double minLoanAmount;
	private Double maxLoanAmount;
	private Double defaultLoanAmount;
	private final LoanOfferingBO loanOffering;
	private final Short loanAmountSameForAllLoanID;


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


	public LoanAmountSameForAllLoanBO(Double minLoanAmount,
			Double maxLoanAmount, Double defaultLoanAmount,
			LoanOfferingBO loanOffering) {
		this.minLoanAmount = minLoanAmount;
		this.maxLoanAmount = maxLoanAmount;
		this.defaultLoanAmount = defaultLoanAmount;
		this.loanOffering = loanOffering;
		this.loanAmountSameForAllLoanID = null;
	}

	public LoanAmountSameForAllLoanBO() {
		this.minLoanAmount = null;
		this.maxLoanAmount = null;
		this.defaultLoanAmount = null;
		this.loanOffering = null;
		this.loanAmountSameForAllLoanID = null;
	}

	public Short getLoanAmountSameForAllLoanID() {
		return loanAmountSameForAllLoanID;
	}

	public LoanOfferingBO getLoanOffering() {
		return loanOffering;
	}


}
