package org.mifos.application.productdefinition.business;

import org.mifos.framework.business.View;

public class AccountProcessFlowView extends View {

	private String clientsSubmitApproval;

	private String groupsSubmitApproval;

	private String loansSubmitApproval;

	private String loanDisbursLO;

	private String savingsSubmitApproval;

	private String insuranceSubmitApproval;

	public AccountProcessFlowView() {
		super();
	}

	public String getClientsSubmitApproval() {
		return clientsSubmitApproval;
	}

	public void setClientsSubmitApproval(String clientsSubmitApproval) {
		this.clientsSubmitApproval = clientsSubmitApproval;
	}

	public String getGroupsSubmitApproval() {
		return groupsSubmitApproval;
	}

	public void setGroupsSubmitApproval(String groupsSubmitApproval) {
		this.groupsSubmitApproval = groupsSubmitApproval;
	}

	public String getInsuranceSubmitApproval() {
		return insuranceSubmitApproval;
	}

	public void setInsuranceSubmitApproval(String insuranceSubmitApproval) {
		this.insuranceSubmitApproval = insuranceSubmitApproval;
	}

	public String getLoanDisbursLO() {
		return loanDisbursLO;
	}

	public void setLoanDisbursLO(String loanDisbursLO) {
		this.loanDisbursLO = loanDisbursLO;
	}

	public String getLoansSubmitApproval() {
		return loansSubmitApproval;
	}

	public void setLoansSubmitApproval(String loansSubmitApproval) {
		this.loansSubmitApproval = loansSubmitApproval;
	}

	public String getSavingsSubmitApproval() {
		return savingsSubmitApproval;
	}

	public void setSavingsSubmitApproval(String savingsSubmitApproval) {
		this.savingsSubmitApproval = savingsSubmitApproval;
	}

}
