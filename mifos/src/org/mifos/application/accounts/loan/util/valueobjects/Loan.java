package org.mifos.application.accounts.loan.util.valueobjects;




import java.sql.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.util.valueobjects.Account;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.fund.util.valueobjects.Fund;
import org.mifos.application.master.util.valueobjects.Currency;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.productdefinition.util.valueobjects.LoanOffering;
import org.mifos.framework.util.helpers.Money;

public class Loan extends Account{
	
	
	
	public Loan(){
		super.setResultName("loan");
		// this is being initialized here because this field is not there in the action form.
		// it comes from prdOffering.
		this.loanPenaltySet = new HashSet<LoanPenalty>();
//		 this is being initialized here because this field is not there in the action form.
		this.loanSummary = new LoanSummary();
		loanActivitySet=new HashSet<LoanActivity>();
		
	}
	/** The value of the simple loanAmount property. */
	private Money loanAmount;
	
	private Date disbursementDate;
	
	
	private Short gracePeriodPenalty;
	
	
	private Short intrestAtDisbursement;
	
	
	private Integer businessActivityId;
	
	
	private Short collateralTypeId;
	
	
	private LoanPerfHistory perfHistory;
	
	
	private List loanInstallments;
	
	
	private Short status;
	
	private Customer customer;
	
	
	
	/** The composite primary key value. */
	private Account account;
	
	/** The value of the currency association. */
	private Currency currency;
	
	/** The value of the fund association. */
	private Fund fund;
	
	/** The value of the gracePeriodType association. */
	private Short gracePeriodTypeId;
	
	/** The value of the interestTypes association. */
	private Short interestTypeId;
	
	/** The value of the meeting association. */
	private Meeting loanMeeting;
	
	/** The value of the loanPenaltySet one-to-many association. */
	private java.util.Set<LoanPenalty> loanPenaltySet;
	
	/** The value of the simple gracePeriodDuration property. */
	private java.lang.Short gracePeriodDuration;
	
	/** The value of the simple groupFlag property. */
	private java.lang.Short groupFlag;
	
	private String collateralNote;
	
	
	/** The value of the simple loanBalance property. */
	private java.lang.Double loanBalance;
	
	/** The value of the simple interestRateAmount property. */
	private java.lang.Double interestRateAmount;
	
	/** The value of the simple noOfInstallments property. */
	private java.lang.Short noOfInstallments;
	
	/** The value of the simple notes property. */
	private java.lang.String notes;
	
	/**
	 * this is the one-to-one association with loan summary
	 */
	private LoanSummary loanSummary;
	
	/**
	 * This indicates the state selected by the user for saving the loan account.
	 */
	private String stateSelected;
				  
	private Short selectedPrdOfferingId;
	
	/** The value of the prdOffering association. */
    private LoanOffering loanOffering;

	
	private Set<WaiveOffHistory> waiveOffHistorySet;
	
	private Set<LoanActivity> loanActivitySet=null;
	private LoanPerformanceHistory loanPerformanceHistory;
	
	/**
	 * @param prdOffering The prdOffering to set.
	 */
	public void setLoanOffering(LoanOffering loanOffering) {
		this.loanOffering = loanOffering;
	}
	/**
	 * @return Returns the account}.
	 */
	public Account getAccount() {
		return account;
	}
	
	/**
	 * @param account The account to set.
	 */
	public void setAccount(Account account) {
		this.account = account;
	}
	
	/**
	 * @return Returns the currency}.
	 */
	public Currency getCurrency() {
		return currency;
	}
	
	/**
	 * @param currency The currency to set.
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	
	/**
	 * @return Returns the customer}.
	 */
	public Customer getCustomer() {
		return customer;
	}
	
	/**
	 * @param customer The customer to set.
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	/**
	 * @return Returns the disbursementDate}.
	 */
	public Date getDisbursementDate() {
		return disbursementDate;
	}
	
	/**
	 * @param disbursementDate The disbursementDate to set.
	 */
	public void setDisbursementDate(Date disbursementDate) {
		this.disbursementDate = disbursementDate;
	}
	
	/**
	 * @return Returns the fund}.
	 */
	public Fund getFund() {
		return fund;
	}
	
	/**
	 * @param fund The fund to set.
	 */
	public void setFund(Fund fund) {
		this.fund = fund;
	}
	
	/**
	 * @return Returns the groupFlag}.
	 */
	public java.lang.Short getGroupFlag() {
		return groupFlag;
	}
	
	/**
	 * @param groupFlag The groupFlag to set.
	 */
	public void setGroupFlag(java.lang.Short groupFlag) {
		this.groupFlag = groupFlag;
	}
	
	/**
	 * @return Returns the interestRateAmount}.
	 */
	public java.lang.Double getInterestRateAmount() {
		return interestRateAmount;
	}
	
	/**
	 * @param interestRateAmount The interestRateAmount to set.
	 */
	public void setInterestRateAmount(java.lang.Double interestRateAmount) {
		this.interestRateAmount = interestRateAmount;
	}
	
	
	/**
	 * @return Returns the loanBalance}.
	 */
	public java.lang.Double getLoanBalance() {
		return loanBalance;
	}
	
	/**
	 * @param loanBalance The loanBalance to set.
	 */
	public void setLoanBalance(java.lang.Double loanBalance) {
		this.loanBalance = loanBalance;
	}
	
	/**
	 * @return Returns the loanInstallments}.
	 */
	public List getLoanInstallments() {
		return loanInstallments;
	}
	
	/**
	 * @param loanInstallments The loanInstallments to set.
	 */
	public void setLoanInstallments(List loanInstallments) {
		this.loanInstallments = loanInstallments;
	}
	
	/**
	 * @return Returns the loanPenaltySet}.
	 */
	public java.util.Set<LoanPenalty> getLoanPenaltySet() {
		return loanPenaltySet;
	}
	
	/**
	 * @param loanPenaltySet The loanPenaltySet to set.
	 */
	public void setLoanPenaltySet(java.util.Set<LoanPenalty> loanPenaltySet) {
		this.loanPenaltySet = loanPenaltySet;
	}
	
	/**
	 * @return Returns the noOfInstallments}.
	 */
	public java.lang.Short getNoOfInstallments() {
		return noOfInstallments;
	}
	
	/**
	 * @param noOfInstallments The noOfInstallments to set.
	 */
	public void setNoOfInstallments(java.lang.Short noOfInstallments) {
		this.noOfInstallments = noOfInstallments;
	}
	
	/**
	 * @return Returns the notes}.
	 */
	public java.lang.String getNotes() {
		return notes;
	}
	
	/**
	 * @param notes The notes to set.
	 */
	public void setNotes(java.lang.String notes) {
		this.notes = notes;
	}
	
	/**
	 * @return Returns the perfHistory}.
	 */
	public LoanPerfHistory getPerfHistory() {
		return perfHistory;
	}
	
	/**
	 * @param perfHistory The perfHistory to set.
	 */
	public void setPerfHistory(LoanPerfHistory perfHistory) {
		this.perfHistory = perfHistory;
	}
	
	
	
	/**
	 * @return Returns the status}.
	 */
	public Short getStatus() {
		return status;
	}
	
	/**
	 * @param status The status to set.
	 */
	public void setStatus(Short status) {
		this.status = status;
	}
	
	/**
	 * This method has been overridden because in the jsp we would want to access fields specific to loan offering
	 * and not just prdOffering so I need to have that object type in the jsp.
	 * @see org.mifos.application.accounts.util.valueobjects.Account#getPrdOffering()
	 */
	public LoanOffering getLoanOffering(){
		return loanOffering;
	}

	/**
	 * @return Returns the collateralNote}.
	 */
	public String getCollateralNote() {
		return collateralNote;
	}

	/**
	 * @param collateralNote The collateralNote to set.
	 */
	public void setCollateralNote(String collateralNote) {
		this.collateralNote = collateralNote;
	}
	
	/**
	 * @return Returns the loanAmount}.
	 */
	public Money getLoanAmount() {
		return loanAmount;
	}

	/**
	 * @param loanAmount The loanAmount to set.
	 */
	public void setLoanAmount(Money loanAmount) {
		this.loanAmount = loanAmount;
	}

	/**
	 * @return Returns the gracePeriodTypeId}.
	 */
	public Short getGracePeriodTypeId() {
		return gracePeriodTypeId;
	}

	/**
	 * @param gracePeriodTypeId The gracePeriodTypeId to set.
	 */
	public void setGracePeriodTypeId(Short gracePeriodTypeId) {
		this.gracePeriodTypeId = gracePeriodTypeId;
	}

	/**
	 * @return Returns the interestTypeId}.
	 */
	public Short getInterestTypeId() {
		return interestTypeId;
	}

	/**
	 * @param interestTypeId The interestTypeId to set.
	 */
	public void setInterestTypeId(Short interestTypeId) {
		this.interestTypeId = interestTypeId;
	}

	/**
	 * @return Returns the gracePeriodDuration}.
	 */
	public java.lang.Short getGracePeriodDuration() {
		return gracePeriodDuration;
	}

	/**
	 * @param gracePeriodDuration The gracePeriodDuration to set.
	 */
	public void setGracePeriodDuration(java.lang.Short gracePeriodDuration) {
		this.gracePeriodDuration = gracePeriodDuration;
	}

	/**
	 * @return Returns the businessActivityId}.
	 */
	public Integer getBusinessActivityId() {
		return businessActivityId;
	}

	/**
	 * @param businessActivityId The businessActivityId to set.
	 */
	public void setBusinessActivityId(Integer businessActivityId) {
		this.businessActivityId = businessActivityId;
	}

	/**
	 * @return Returns the stateSelected}.
	 */
	public String getStateSelected() {
		return stateSelected;
	}

	/**
	 * @param stateSelected The stateSelected to set.
	 */
	public void setStateSelected(String stateSelected) {
		this.stateSelected = stateSelected;
	}

	/**
	 * @return Returns the collateralTypeId}.
	 */
	public Short getCollateralTypeId() {
		return collateralTypeId;
	}

	/**
	 * @param collateralTypeId The collateralTypeId to set.
	 */
	public void setCollateralTypeId(Short collateralTypeId) {
		this.collateralTypeId = collateralTypeId;
	}

	/**
	 * @return Returns the intrestAtDisbursement}.
	 */
	public Short getIntrestAtDisbursement() {
		return intrestAtDisbursement;
	}

	/**
	 * @param intrestAtDisbursement The intrestAtDisbursement to set.
	 */
	public void setIntrestAtDisbursement(Short intrestAtDisbursement) {
		this.intrestAtDisbursement = intrestAtDisbursement;
	}

	/**
	 * @return Returns the gracePeriodPenalty}.
	 */
	public Short getGracePeriodPenalty() {
		return gracePeriodPenalty;
	}

	/**
	 * @param gracePeriodPenalty The gracePeriodPenalty to set.
	 */
	public void setGracePeriodPenalty(Short gracePeriodPenalty) {
		this.gracePeriodPenalty = gracePeriodPenalty;
	}

	/**
	 * @return Returns the loanMeeting}.
	 */
	public Meeting getLoanMeeting() {
		return loanMeeting;
	}

	/**
	 * @param loanMeeting The loanMeeting to set.
	 */
	public void setLoanMeeting(Meeting loanMeeting) {
		this.loanMeeting = loanMeeting;
	}

	/**
	 * @return Returns the loanSummary}.
	 */
	public LoanSummary getLoanSummary() {
		return loanSummary;
	}

	/**
	 * @param loanSummary The loanSummary to set.
	 */
	public void setLoanSummary(LoanSummary loanSummary) {
		if(loanSummary != null)
			   loanSummary.setLoan(this);	
			
			this.loanSummary = loanSummary;
	}

	/**
	 * @return
	 */
	public Short getSelectedPrdOfferingId() {
		
		return this.selectedPrdOfferingId;
	}

	/**
	 * @param selectedPrdOfferingId The selectedPrdOfferingId to set.
	 */
	public void setSelectedPrdOfferingId(Short selectedPrdOfferingId) {
		this.selectedPrdOfferingId = selectedPrdOfferingId;
	}


	/**
	 * @return Returns the waiveOffHistorySet.
	 */
	public Set<WaiveOffHistory> getWaiveOffHistorySet() {
		return waiveOffHistorySet;
	}

	public void addWaiveHistory(WaiveOffHistory waiveHistory){
		waiveHistory.setLoan(this);
		this.waiveOffHistorySet.add(waiveHistory);
		setWaiveOffHistorySet(waiveOffHistorySet);
	}
	
	/**
	 * @param waiveOffHistorySet The waiveOffHistorySet to set.
	 */
	public void setWaiveOffHistorySet(Set<WaiveOffHistory> waiveOffHistorySet) {
		if(null!=waiveOffHistorySet){
			for(Iterator itr=waiveOffHistorySet.iterator();itr.hasNext();){
				WaiveOffHistory waiveOffHistory=(WaiveOffHistory)itr.next();
				waiveOffHistory.setLoan(this); 
			}
		}
		this.waiveOffHistorySet = waiveOffHistorySet;
	}
	public Set<LoanActivity> getLoanActivitySet() {
		return loanActivitySet;
	}
	public void setLoanActivitySet(Set<LoanActivity> loanActivitySet) {
		this.loanActivitySet = loanActivitySet;
	}
	public LoanPerformanceHistory getLoanPerformanceHistory() {
		return loanPerformanceHistory;
	}
	public void setLoanPerformanceHistory(
			LoanPerformanceHistory loanPerformanceHistory) {
		this.loanPerformanceHistory = loanPerformanceHistory;
	}

	
	
	
}