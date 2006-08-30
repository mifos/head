/**
 * 
 */
package org.mifos.application.accounts.loan.util.valueobjects;

public class InstallmentDetails {
	
	private Integer accountId;
	
	private Short nextInstallmentId;
	
	private Double nextInstallmentPrincipal=0.0;
	
	private Double nextInstallmentInterest=0.0;
	
	private Double nextInstallmentFees=0.0;
	
	private Double nextInstallmentPenalty=0.0;
	
	private Double totalNextInstallment=0.0; 
	
	private Double pastDuePrincipal=0.0;
	
	private Double pastDueInterest=0.0;
	
	private Double pastDueFees=0.0;
	
	private Double pastDuePenalty=0.0;
	
	private Double totalPastDue=0.0;
	
	private Short[] pastInstallmentIds;
	
	private Double total;

	/**
	 * @return Returns the nextInstallmentFees.
	 */
	public Double getNextInstallmentFees() {
		return nextInstallmentFees;
	}

	/**
	 * @param nextInstallmentFees The nextInstallmentFees to set.
	 */
	public void setNextInstallmentFees(Double nextInstallmentFees) {
		this.nextInstallmentFees = nextInstallmentFees;
	}

	/**
	 * @return Returns the nextInstallmentInterest.
	 */
	public Double getNextInstallmentInterest() {
		return nextInstallmentInterest;
	}

	/**
	 * @param nextInstallmentInterest The nextInstallmentInterest to set.
	 */
	public void setNextInstallmentInterest(Double nextInstallmentInterest) {
		this.nextInstallmentInterest = nextInstallmentInterest;
	}

	/**
	 * @return Returns the nextInstallmentPenalty.
	 */
	public Double getNextInstallmentPenalty() {
		return nextInstallmentPenalty;
	}

	/**
	 * @param nextInstallmentPenalty The nextInstallmentPenalty to set.
	 */
	public void setNextInstallmentPenalty(Double nextInstallmentPenalty) {
		this.nextInstallmentPenalty = nextInstallmentPenalty;
	}

	/**
	 * @return Returns the nextInstallmentPrincipal.
	 */
	public Double getNextInstallmentPrincipal() {
		return nextInstallmentPrincipal;
	}

	/**
	 * @param nextInstallmentPrincipal The nextInstallmentPrincipal to set.
	 */
	public void setNextInstallmentPrincipal(Double nextInstallmentPrincipal) {
		this.nextInstallmentPrincipal = nextInstallmentPrincipal;
	}

	/**
	 * @return Returns the pastDueFees.
	 */
	public Double getPastDueFees() {
		return pastDueFees;
	}

	/**
	 * @param pastDueFees The pastDueFees to set.
	 */
	public void setPastDueFees(Double pastDueFees) {
		this.pastDueFees = pastDueFees;
	}

	/**
	 * @return Returns the pastDueInterest.
	 */
	public Double getPastDueInterest() {
		return pastDueInterest;
	}

	/**
	 * @param pastDueInterest The pastDueInterest to set.
	 */
	public void setPastDueInterest(Double pastDueInterest) {
		this.pastDueInterest = pastDueInterest;
	}

	/**
	 * @return Returns the pastDuePenalty.
	 */
	public Double getPastDuePenalty() {
		return pastDuePenalty;
	}

	/**
	 * @param pastDuePenalty The pastDuePenalty to set.
	 */
	public void setPastDuePenalty(Double pastDuePenalty) {
		this.pastDuePenalty = pastDuePenalty;
	}

	/**
	 * @return Returns the pastDuePrincipal.
	 */
	public Double getPastDuePrincipal() {
		return pastDuePrincipal;
	}

	/**
	 * @param pastDuePrincipal The pastDuePrincipal to set.
	 */
	public void setPastDuePrincipal(Double pastDuePrincipal) {
		this.pastDuePrincipal = pastDuePrincipal;
	}

	/**
	 * @return Returns the totalNextInstallment.
	 */
	public Double getTotalNextInstallment() {
		return getNextInstallmentPrincipal()+getNextInstallmentInterest()+getNextInstallmentFees()+getNextInstallmentPenalty();
	}


	/**
	 * @return Returns the totalPastDue.
	 */
	public Double getTotalPastDue() {
		return getPastDuePrincipal()+getPastDueInterest()+getPastDuePenalty()+getPastDueFees();    
	}

	/**
	 * @return Returns the accountId.
	 */
	public Integer getAccountId() {
		return accountId;
	}

	/**
	 * @param accountId The accountId to set.
	 */
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	/**
	 * @return Returns the nextInstallmentId.
	 */
	public Short getNextInstallmentId() {
		return nextInstallmentId;
	}

	/**
	 * @param nextInstallmentId The nextInstallmentId to set.
	 */
	public void setNextInstallmentId(Short nextInstallmentId) {
		this.nextInstallmentId = nextInstallmentId;
	}

	/**
	 * @return Returns the pastInstallmentIds.
	 */
	public Short[] getPastInstallmentIds() {
		return pastInstallmentIds;
	}

	/**
	 * @param pastInstallmentIds The pastInstallmentIds to set.
	 */
	public void setPastInstallmentIds(Short[] pastInstallmentIds) {
		this.pastInstallmentIds = pastInstallmentIds;
	}

	/**
	 * @return Returns the total.
	 */
	public Double getTotal() {
		return getTotalNextInstallment()+getTotalPastDue();
	}

	

}
