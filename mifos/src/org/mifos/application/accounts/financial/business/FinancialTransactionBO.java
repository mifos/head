/**

 * FinancialTransactionBO.java    version: 1.0

 

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

package org.mifos.application.accounts.financial.business;

import java.util.Date;

import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.service.PersonnelPersistenceService;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.PersistenceServiceName;

public class FinancialTransactionBO extends BusinessObject {
	
	protected FinancialTransactionBO (){}
	
	public FinancialTransactionBO(FinancialActionBO financialAction,
			Money postedMoney,GLCodeEntity glcode,Date actionDate,PersonnelBO personnel,Short debitCreditFlag) {
		this.financialAction=financialAction;
		this.postedAmount=postedMoney;
		this.balanceAmount=postedMoney;
		this.glcode=glcode;
		this.actionDate=actionDate;
		this.postedDate=new Date(System.currentTimeMillis());
		this.postedBy=personnel;
		this.debitCreditFlag=debitCreditFlag;
	}
	
	
	
	private Integer trxnId;
	private AccountTrxnEntity accountTrxn;
	private FinancialTransactionBO relatedFinancialTrxn;
	private Money postedMoney;
	private Money balanceMoney;
	private FinancialActionBO financialAction;
	private GLCodeEntity glcode;
	private Date actionDate;
	private Date postedDate;
	private PersonnelBO postedBy;
	private Short accountingUpdated;
	private Money postedAmount;
	private Money balanceAmount;
	private String notes;
	private Short debitCreditFlag;



	public boolean isDebitEntry(){
		return this.debitCreditFlag.equals(FinancialConstants.DEBIT);
	}

	public boolean isCreditEntry(){
		return this.debitCreditFlag.equals(FinancialConstants.CREDIT);
	}	

	private void setDebitCreditFlag(Short debitCreditFlag) {
		this.debitCreditFlag = debitCreditFlag;
	}
	
	public Short getDebitCreditFlag(){
		return debitCreditFlag;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Short getAccountingUpdated() {
		return accountingUpdated;
	}

	private void setAccountingUpdated(Short accountingUpdated) {
		this.accountingUpdated = accountingUpdated;
	}

	public Date getActionDate() {
		return actionDate;
	}

	private void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	public FinancialActionBO getFinancialAction() {
		return financialAction;
	}

	private void setFinancialAction(FinancialActionBO financialAction) {
		this.financialAction = financialAction;
	}

	public GLCodeEntity getGlcode() {
		return glcode;
	}

	private void setGlcode(GLCodeEntity glcode) {
		this.glcode = glcode;
	}

	public Date getPostedDate() {
		return postedDate;
	}

	private void setPostedDate(Date postedDate) {
		this.postedDate = postedDate;
	}

	public FinancialTransactionBO getRelatedFinancialTrxn() {
		return relatedFinancialTrxn;
	}

	private void setRelatedFinancialTrxn(
			FinancialTransactionBO relatedFinancialTrxn) {
		this.relatedFinancialTrxn = relatedFinancialTrxn;
	}

	public Money getBalanceAmount() {
		return balanceAmount;
	}

	private void setBalanceAmount(Money balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public Money getPostedAmount() {
		return postedAmount;
	}

	private void setPostedAmount(Money postedAmount) {
		this.postedAmount = postedAmount;
	}

	private Integer getTrxnId() {
		return trxnId;
	}

	private void setTrxnId(Integer trxnId) {
		this.trxnId = trxnId;
	}

	private Money buildMoney(MifosCurrency currency, double amount) {
		return new Money(currency, amount);
	}

	public AccountTrxnEntity getAccountTrxn() {
		return accountTrxn;
	}

	public void setAccountTrxn(AccountTrxnEntity accountTrxn) {
		this.accountTrxn = accountTrxn;
	}

	public PersonnelBO getPostedBy() {
		return postedBy;
	}

	private void setPostedBy(PersonnelBO postedBy) {
		this.postedBy = postedBy;
	}

	public FinancialTransactionBO generateReverseTrxn() throws SystemException,ApplicationException {
		FinancialTransactionBO financialTrxn = new FinancialTransactionBO();
		//FinancialActionBO financialAction = new FinancialActionBO();
		//financialAction.setFinancialId(FinancialActionConstants.REVERSAL_ADJUSTMENT);
		financialTrxn.setPostedAmount(getPostedAmount().negate());
		financialTrxn.setFinancialAction(getFinancialAction());
		financialTrxn.setBalanceAmount(getBalanceAmount());
		financialTrxn.setFinancialAction(getFinancialAction());
		financialTrxn.setGlcode(getGlcode());
		financialTrxn.setActionDate(getActionDate());
		financialTrxn.setPostedDate(DateHelper.getSQLDate(DateHelper.getCurrentDate(getUserContext().getMfiLocale())));
		PersonnelPersistenceService persistenceService = (PersonnelPersistenceService)ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.Personnel);
		PersonnelBO personnel = persistenceService.getPersonnel(getAccountTrxn().getAccount().getUserContext().getId());
		financialTrxn.setPostedBy(personnel);
		//TODO replace this with a constant.
		financialTrxn.setAccountingUpdated(Short.valueOf("1"));
		if(isDebitEntry()){
			financialTrxn.setDebitCreditFlag(FinancialConstants.CREDIT);
		}else if(isCreditEntry()){
			financialTrxn.setDebitCreditFlag(FinancialConstants.DEBIT);
		}
		return financialTrxn;
	}



}
