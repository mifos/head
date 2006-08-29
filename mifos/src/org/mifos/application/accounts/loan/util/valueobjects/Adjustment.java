/**
 * 
 */
package org.mifos.application.accounts.loan.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author krishankg
 *
 */
public class Adjustment extends ValueObject {
	
	private Integer accountId;
	
	private Integer paymentId;
	
	private Double amount;
	
	private String note;
	
	private Short personnelId;
	
	
	/**
	 * @return Returns the personnelId.
	 */
	public Short getPersonnelId() {
		return personnelId;
	}

	/**
	 * @param personnelId The personnelId to set.
	 */
	public void setPersonnelId(Short personnelId) {
		this.personnelId = personnelId;
	}

	/**
	 * Constructor
	 */
	public Adjustment() {
		super();
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
	 * @return Returns the amount.
	 */
	public Double getAmount() {
		return amount;
	}

	/**
	 * @param amount The amount to set.
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	/**
	 * @return Returns the paymentId.
	 */
	public Integer getPaymentId() {
		return paymentId;
	}

	/**
	 * @param paymentId The paymentId to set.
	 */
	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}

	/**
	 * @return Returns the note.
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @param note The note to set.
	 */
	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public String getResultName() {
		return "Adjustment";
	}

}
