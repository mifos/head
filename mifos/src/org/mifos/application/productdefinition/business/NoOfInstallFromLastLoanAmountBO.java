
package org.mifos.application.productdefinition.business;

import org.mifos.framework.business.PersistentObject;


public class NoOfInstallFromLastLoanAmountBO extends PersistentObject {

	private Short minNoOfInstall;
	private Short maxNoOfInstall;
	private Short defaultNoOfInstall;
	private Double startRange;
	private Double endRange;
	private final LoanOfferingBO loanOffering;
	private final Short noOfInstallFromLastLoanID;

	public NoOfInstallFromLastLoanAmountBO(Short minNoOfInstall,
			Short maxNoOfInstall, Short defaultNoOfInstall, Double startRange,
			Double endRange, LoanOfferingBO loanOffering) {
		this.minNoOfInstall = minNoOfInstall;
		this.maxNoOfInstall = maxNoOfInstall;
		this.defaultNoOfInstall = defaultNoOfInstall;
		this.startRange = startRange;
		this.endRange = endRange;
		this.loanOffering = loanOffering;
		this.noOfInstallFromLastLoanID = null;
	}

	public NoOfInstallFromLastLoanAmountBO() {
		this.minNoOfInstall = null;
		this.maxNoOfInstall = null;
		this.defaultNoOfInstall = null;
		this.startRange = null;
		this.endRange = null;
		this.loanOffering = null;
		this.noOfInstallFromLastLoanID = null;
	}

	public Short getDefaultNoOfInstall() {
		return defaultNoOfInstall;
	}

	public void setDefaultNoOfInstall(Short defaultNoOfInstall) {
		this.defaultNoOfInstall = defaultNoOfInstall;
	}

	public Double getEndRange() {
		return endRange;
	}

	public void setEndRange(Double endRange) {
		this.endRange = endRange;
	}

	public Short getMaxNoOfInstall() {
		return maxNoOfInstall;
	}

	public void setMaxNoOfInstall(Short maxNoOfInstall) {
		this.maxNoOfInstall = maxNoOfInstall;
	}

	public Short getMinNoOfInstall() {
		return minNoOfInstall;
	}

	public void setMinNoOfInstall(Short minNoOfInstall) {
		this.minNoOfInstall = minNoOfInstall;
	}

	public Double getStartRange() {
		return startRange;
	}

	public void setStartRange(Double startRange) {
		this.startRange = startRange;
	}
}
