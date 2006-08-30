/**
 * 
 */
package org.mifos.application.productdefinition.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

public class AccountProcessFlow extends ValueObject {

	private static final long serialVersionUID = 6542679008754641L;

	public AccountProcessFlow() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	private String clientsSubmitApproval=null;
	private String groupsSubmitApproval=null;
	private String loansSubmitApproval=null;
	private String loanDisbursLO=null;
	private String savingsSubmitApproval=null;
	private String insuranceSubmitApproval=null;

	/**
	 * @return Returns the clientsSubmitApproval.
	 */
	public String getClientsSubmitApproval() {
		return clientsSubmitApproval;
	}
	/**
	 * @param clientsSubmitApproval The clientsSubmitApproval to set.
	 */
	public void setClientsSubmitApproval(String clientsSubmitApproval) {
		this.clientsSubmitApproval = clientsSubmitApproval;
	}
	/**
	 * @return Returns the groupsSubmitApproval.
	 */
	public String getGroupsSubmitApproval() {
		return groupsSubmitApproval;
	}
	/**
	 * @param groupsSubmitApproval The groupsSubmitApproval to set.
	 */
	public void setGroupsSubmitApproval(String groupsSubmitApproval) {
		this.groupsSubmitApproval = groupsSubmitApproval;
	}
	/**
	 * @return Returns the insuranceSubmitApproval.
	 */
	public String getInsuranceSubmitApproval() {
		return insuranceSubmitApproval;
	}
	/**
	 * @param insuranceSubmitApproval The insuranceSubmitApproval to set.
	 */
	public void setInsuranceSubmitApproval(String insuranceSubmitApproval) {
		this.insuranceSubmitApproval = insuranceSubmitApproval;
	}
	/**
	 * @return Returns the loanDisbursLO.
	 */
	public String getLoanDisbursLO() {
		return loanDisbursLO;
	}
	/**
	 * @param loanDisbursLO The loanDisbursLO to set.
	 */
	public void setLoanDisbursLO(String loanDisbursLO) {
		this.loanDisbursLO = loanDisbursLO;
	}
	/**
	 * @return Returns the loansSubmitApproval.
	 */
	public String getLoansSubmitApproval() {
		return loansSubmitApproval;
	}
	/**
	 * @param loansSubmitApproval The loansSubmitApproval to set.
	 */
	public void setLoansSubmitApproval(String loansSubmitApproval) {
		this.loansSubmitApproval = loansSubmitApproval;
	}
	/**
	 * @return Returns the savingsSubmitApproval.
	 */
	public String getSavingsSubmitApproval() {
		return savingsSubmitApproval;
	}
	/**
	 * @param savingsSubmitApproval The savingsSubmitApproval to set.
	 */
	public void setSavingsSubmitApproval(String savingsSubmitApproval) {
		this.savingsSubmitApproval = savingsSubmitApproval;
	}
	
	

}
