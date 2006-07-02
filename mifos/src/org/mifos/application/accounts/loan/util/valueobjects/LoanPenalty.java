/**

 * LoanPenalty.java    version: xxx

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */

package org.mifos.application.accounts.loan.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author ashishsm
 *
 */
public class LoanPenalty extends ValueObject {

	/**
	 * 
	 */
	public LoanPenalty() {
		super();
		
	}
	
	
/** The composite primary key value. */
    private java.lang.Integer loanPenaltyId;

    /** The value of the loanAccount association. */
    private Loan loan;

    /** The value of the penalty association. */
    private Short penaltyId;

    /** The value of the simple startDate property. */
    private java.sql.Date startDate;

    /** The value of the simple endDate property. */
    private java.sql.Date endDate;
    
    private String penaltyType;
    
    private Double penaltyRate;

	/**
	 * @return Returns the endDate}.
	 */
	public java.sql.Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate The endDate to set.
	 */
	public void setEndDate(java.sql.Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return Returns the loan}.
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
	 * @return Returns the loanPenaltyId}.
	 */
	public java.lang.Integer getLoanPenaltyId() {
		return loanPenaltyId;
	}

	/**
	 * @param loanPenaltyId The loanPenaltyId to set.
	 */
	public void setLoanPenaltyId(java.lang.Integer loanPenaltyId) {
		this.loanPenaltyId = loanPenaltyId;
	}

	/**
	 * @return Returns the penaltyId}.
	 */
	public Short getPenaltyId() {
		return penaltyId;
	}

	/**
	 * @param penaltyId The penaltyId to set.
	 */
	public void setPenaltyId(Short penaltyId) {
		this.penaltyId = penaltyId;
	}

	/**
	 * @return Returns the startDate}.
	 */
	public java.sql.Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate The startDate to set.
	 */
	public void setStartDate(java.sql.Date startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * This method implements equals based on the business logic.
	 * the business logic is account id and penalty id.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj){
		LoanPenalty loanPenalty = null;
		if(null != obj){
			loanPenalty = (LoanPenalty)obj;
		}
		
		if(this.penaltyId.equals(loanPenalty.getPenaltyId()) && (this.loan.getAccountId().equals(loanPenalty.getLoan().getAccountId()))){
			return true;
		}
		return false;
	}

	/**
	 * @return Returns the penaltyRate}.
	 */
	public Double getPenaltyRate() {
		return penaltyRate;
	}

	/**
	 * @param penaltyRate The penaltyRate to set.
	 */
	public void setPenaltyRate(Double penaltyRate) {
		this.penaltyRate = penaltyRate;
	}

	/**
	 * @return Returns the penaltyType}.
	 */
	public String getPenaltyType() {
		return penaltyType;
	}

	/**
	 * @param penaltyType The penaltyType to set.
	 */
	public void setPenaltyType(String penaltyType) {
		this.penaltyType = penaltyType;
	}

}
