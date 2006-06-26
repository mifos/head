package org.mifos.application.accounts.loan.util.valueobjects;

import java.sql.Date;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author krishankg
 *
 */
public class LoanDisbursment extends ValueObject{

	private Integer accountId;
	
	private Date disbursmentDate;
	
	private Integer receiptId;
	
	private Date receiptDate;
	
	private Short disbursmentModeOfPayment;
	
	private Short paymentModeOfPayment;
	
	private Double loanAmount;
	
	private Double interestAmount;

	/**
	 * Constructor
	 */
	public LoanDisbursment() {
		super();
		this.setResultName("LoanDisbursment");
	}
	
	/**
	 * @return Returns the disbursmentDate.
	 */
	public Date getDisbursmentDate() {
		return disbursmentDate;
	}

	/**
	 * @param disbursmentDate The disbursmentDate to set.
	 */
	public void setDisbursmentDate(Date disbursmentDate) {
		this.disbursmentDate = disbursmentDate;
	}

	/**
	 * @return Returns the disbursmentModeOfPayment.
	 */
	public Short getDisbursmentModeOfPayment() {
		return disbursmentModeOfPayment;
	}

	/**
	 * @param disbursmentModeOfPayment The disbursmentModeOfPayment to set.
	 */
	public void setDisbursmentModeOfPayment(Short disbursmentModeOfPayment) {
		this.disbursmentModeOfPayment = disbursmentModeOfPayment;
	}

	/**
	 * @return Returns the paymentModeOfPayment.
	 */
	public Short getPaymentModeOfPayment() {
		return paymentModeOfPayment;
	}

	/**
	 * @param paymentModeOfPayment The paymentModeOfPayment to set.
	 */
	public void setPaymentModeOfPayment(Short paymentModeOfPayment) {
		this.paymentModeOfPayment = paymentModeOfPayment;
	}

	/**
	 * @return Returns the receiptDate.
	 */
	public Date getReceiptDate() {
		return receiptDate;
	}

	/**
	 * @param receiptDate The receiptDate to set.
	 */
	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}

	/**
	 * @return Returns the receiptId.
	 */
	public Integer getReceiptId() {
		return receiptId;
	}

	/**
	 * @param receiptId The receiptId to set.
	 */
	public void setReceiptId(Integer receiptId) {
		this.receiptId = receiptId;
	}

	/**
	 * @return Returns the interestAmount.
	 */
	public Double getInterestAmount() {
		return interestAmount;
	}

	/**
	 * @param interestAmount The interestAmount to set.
	 */
	public void setInterestAmount(Double interestAmount) {
		this.interestAmount = interestAmount;
	}

	/**
	 * @return Returns the loanAmount.
	 */
	public Double getLoanAmount() {
		return loanAmount;
	}

	/**
	 * @param loanAmount The loanAmount to set.
	 */
	public void setLoanAmount(Double loanAmount) {
		this.loanAmount = loanAmount;
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

		
	
	
}
