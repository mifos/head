/**
 
 * AccountFees.java    version: xxx
 
 
 
 * Copyright © 2005-2006 Grameen Foundation USA
 
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

package org.mifos.application.accounts.util.valueobjects;


import java.util.Date;

import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author ashishsm
 *
 */
public class AccountFees extends ValueObject{
	
	/**
	 * @return Returns the feeAmount.
	 */
	public Double getFeeAmount() {
		return feeAmount;
	}
	/**
	 * @param feeAmount The feeAmount to set.
	 */
	public void setFeeAmount(Double feeAmount) {
		this.feeAmount = feeAmount;
	}
	/**
	 * 
	 */
	public AccountFees() {
		super();
		setResultName("accountFees");
	}
	
	private java.lang.Integer accountFeeId;
	private Account account;
	private Fees fees;
	private java.lang.Integer accountId;
	private Integer versionNo;
	private Short inheritedFlag ;
	private Money accountFeeAmount;
	private Double feeAmount;
	private Short feeStatus;
	private Date  statusChangeDate;

	/**
	 * This field is not persisted in the database , this is purely used for UI purposes.
	 * It determines if the fee should be removed as it is populated when the user selects 
	 * check to remove in the UI.
	 */
	private Short checkToRemove;
	
	/**
	 * This field is only supposed to be used for equals method otherwise if we use
	 * fees object for our equals method we need to populate fees object with global fee num
	 * as global fee num is the business key in fees object. 
	 */
	private Short feeId;
	
	/**
	 * @return Returns the fees}.
	 */
	public Fees getFees() {
		return fees;
	}
	/**
	 * @param fees The fees to set.
	 */
	public void setFees(Fees fees) {
		this.fees = fees;
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
	 * @return Returns the accountFeeId}.
	 */
	public java.lang.Integer getAccountFeeId() {
		
		return this.accountFeeId;
	}
	/**
	 * @param accountFeeId The accountFeeId to set.
	 */
	public void setAccountFeeId(java.lang.Integer accountFeeId) {
		this.accountFeeId = accountFeeId;
	}
	/**
	 * @return Returns the accountId}.
	 */
	public java.lang.Integer getAccountId() {
		return accountId;
	}
	/**
	 * @param accountId The accountId to set.
	 */
	public void setAccountId(java.lang.Integer accountId) {
		this.accountId = accountId;
	}
	
	/**
	 * This method has been overridden to perform comparision while creating set of accountFees objects.
	 * The business key here is accountId and feeId 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj){
		AccountFees accountFees = (AccountFees)obj;
		
		//If account id is not null business key comprises of both accountid and fee id
		// else business key is only fee id
		// account id can be null under condition when account is not yet created
		// and hence account id can not be assigned.
		// for e.g. when a user creates a customer , the account is not created and hence account id is null
		// but it needs to create a set of account fees to be set inside account object so that these rows can be inserted.
		if(null != this.accountId){
			if(this.accountId.equals(accountFees.getAccountId()) && this.feeId.equals(accountFees.getFeeId())){
				return true;
			}else{
				return false;
			}
		}else{
			if(this.feeId.equals(accountFees.getFeeId())){
				return true;
			}else{
				return false;
			}
		}
	}
	
	
	/**
	 * @return Returns the versionNo}.
	 */
	public Integer getVersionNo() {
		return versionNo;
	}
	/**
	 * @param versionNo The versionNo to set.
	 */
	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}
	/**
	 * @return Returns the feeId}.
	 */
	public Short getFeeId() {
		return feeId;
	}
	/**
	 * @param feeId The feeId to set.
	 */
	public void setFeeId(Short feeId) {
		this.feeId = feeId;
	}
	/**
	 * @return Returns the inheritedFlag}.
	 */
	public Short getInheritedFlag() {
		return inheritedFlag;
	}
	/**
	 * @param inheritedFlag The inheritedFlag to set.
	 */
	public void setInheritedFlag(Short inheritedFlag) {
		this.inheritedFlag = inheritedFlag;
	}
	/**
	 * @return Returns the accountFeeAmount}.
	 */
	public Money getAccountFeeAmount() {
		return accountFeeAmount;
	}
	/**
	 * @param accountFeeAmount The accountFeeAmount to set.
	 */
	public void setAccountFeeAmount(Money accountFeeAmount) {
		this.accountFeeAmount = accountFeeAmount;
	}
	/**
	 * @return Returns the checkToRemove}.
	 */
	public Short getCheckToRemove() {
		return checkToRemove;
	}
	/**
	 * This is set when the user selects the check box in the UI.
	 * This value is not persisted in the database.
	 * @param checkToRemove The checkToRemove to set.
	 */
	public void setCheckToRemove(Short checkToRemove) {
		this.checkToRemove = checkToRemove;
	}
	/**
	 * @return Returns the feeStatus.
	 */
	public Short getFeeStatus() {
		return feeStatus;
	}
	/**
	 * @param feeStatus The feeStatus to set.
	 */
	public void setFeeStatus(Short feeStatus) {
		this.feeStatus = feeStatus;
	}
	/**
	 * @return Returns the statusChangeDate.
	 */
	public Date getStatusChangeDate() {
		return statusChangeDate;
	}
	/**
	 * @param statusChangeDate The statusChangeDate to set.
	 */
	public void setStatusChangeDate(Date statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}
}
