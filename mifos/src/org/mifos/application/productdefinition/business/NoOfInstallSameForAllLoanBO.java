
package org.mifos.application.productdefinition.business;

import org.mifos.framework.business.PersistentObject;

public class NoOfInstallSameForAllLoanBO extends PersistentObject {

	private Short minNoOfInstall;
	private Short maxNoOfInstall;
	private Short defaultNoOfInstall;
	private final LoanOfferingBO loanOffering;
	private final Short noOfInstallSameForAllLoanID;

	public NoOfInstallSameForAllLoanBO(Short minNoOfInstall,
			Short maxNoOfInstall, Short defaultNoOfInstall,
			LoanOfferingBO loanOffering) {
		this.minNoOfInstall = minNoOfInstall;
		this.maxNoOfInstall = maxNoOfInstall;
		this.defaultNoOfInstall = defaultNoOfInstall;
		this.loanOffering = loanOffering;
		this.noOfInstallSameForAllLoanID = null;
	}

	public NoOfInstallSameForAllLoanBO() {
		this.minNoOfInstall = null;
		this.maxNoOfInstall = null;
		this.defaultNoOfInstall = null;
		this.loanOffering = null;
		this.noOfInstallSameForAllLoanID = null;
	}

	public Short getDefaultNoOfInstall() {
		return defaultNoOfInstall;
	}

	public void setDefaultNoOfInstall(Short defaultNoOfInstall) {
		this.defaultNoOfInstall = defaultNoOfInstall;
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

}
