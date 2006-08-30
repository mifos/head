/**
 * 
 */
package org.mifos.application.accounts.loan.util.valueobjects;

import java.util.Date;

import org.mifos.framework.util.valueobjects.ValueObject;

public class WaiveOffHistory extends ValueObject {
	
	private Integer waiveOffId; 
	
	private Loan loan;
	
	private Date waiveOffDate;
	
	private String waiveOffType;

	/**
	 * @return Returns the loan.
	 */
	public Loan getLoan() {
		return loan;
	}

	/**
	 * @param loan The loan to set.
	 */
	public void setLoan(Loan loan) {
		this.loan = loan;
	}

	/**
	 * @return Returns the waiveOffDate.
	 */
	public Date getWaiveOffDate() {
		return waiveOffDate;
	}

	/**
	 * @param waiveOffDate The waiveOffDate to set.
	 */
	public void setWaiveOffDate(Date waiveOffDate) {
		this.waiveOffDate = waiveOffDate;
	}

	/**
	 * @return Returns the waiveOffType.
	 */
	public String getWaiveOffType() {
		return waiveOffType;
	}

	/**
	 * @param waiveOffType The waiveOffType to set.
	 */
	public void setWaiveOffType(String waiveOffType) {
		this.waiveOffType = waiveOffType;
	}

	/**
	 * @return Returns the waiveOffId.
	 */
	public Integer getWaiveOffId() {
		return waiveOffId;
	}

	/**
	 * @param waiveOffId The waiveOffId to set.
	 */
	public void setWaiveOffId(Integer waiveOffId) {
		this.waiveOffId = waiveOffId;
	}
	
	
	

}
