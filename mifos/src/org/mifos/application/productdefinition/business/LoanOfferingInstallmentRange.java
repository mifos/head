package org.mifos.application.productdefinition.business;


public abstract class LoanOfferingInstallmentRange extends InstallmentRange {
	@SuppressWarnings("unused")
	private final LoanOfferingBO loanOffering;
	private Short defaultNoOfInstall;

	public LoanOfferingInstallmentRange(Short minNoOfInstall,
			Short maxNoOfInstall, Short defaultNoOfInstall,
			LoanOfferingBO loanOffering) {
		super(minNoOfInstall, maxNoOfInstall);
		this.defaultNoOfInstall = defaultNoOfInstall;
		this.loanOffering = loanOffering;
	}

	public Short getDefaultNoOfInstall() {
		return defaultNoOfInstall;
	}

	public void setDefaultNoOfInstall(Short defaultNoOfInstall) {
		this.defaultNoOfInstall = defaultNoOfInstall;
	}
}
