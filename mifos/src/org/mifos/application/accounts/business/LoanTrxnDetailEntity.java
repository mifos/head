/**

 * LoanTrxnDetailEntity.java    version: 1.0

 

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

package org.mifos.application.accounts.business;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.mifos.application.accounts.loan.persistance.LoanPersistance;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.LoanPaymentData;
import org.mifos.application.master.persistence.service.MasterPersistenceService;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.service.PersonnelPersistenceService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.PersistenceServiceName;

public class LoanTrxnDetailEntity extends AccountTrxnEntity {
	public LoanTrxnDetailEntity() {
		feesTrxnDetails = new HashSet<FeesTrxnDetailEntity>();
		loanPersistance = new LoanPersistance();
	}

	private LoanPersistance loanPersistance;

	private AccountTrxnEntity accountTrxn;

	private Money principalAmount;

	private Money interestAmount;

	private Money penaltyAmount;

	private Money miscFeeAmount;

	private Money miscPenaltyAmount;

	private Set<FeesTrxnDetailEntity> feesTrxnDetails;

	public AccountTrxnEntity getAccountTrxn() {
		return accountTrxn;
	}

	public void setAccountTrxn(AccountTrxnEntity accountTrxn) {
		this.accountTrxn = accountTrxn;
	}

	public Set<FeesTrxnDetailEntity> getFeesTrxnDetails() {
		return feesTrxnDetails;
	}

	private void setFeesTrxnDetails(Set<FeesTrxnDetailEntity> feesTrxnDetails) {
		this.feesTrxnDetails = feesTrxnDetails;
	}

	public Money getInterestAmount() {
		return interestAmount;
	}

	public void setInterestAmount(Money interestAmount) {
		this.interestAmount = interestAmount;
	}

	public Money getPenaltyAmount() {
		return penaltyAmount;
	}

	public void setPenaltyAmount(Money penaltyAmount) {
		this.penaltyAmount = penaltyAmount;
	}

	public Money getPrincipalAmount() {
		return principalAmount;
	}

	public void setPrincipalAmount(Money principalAmount) {
		this.principalAmount = principalAmount;
	}

	public Money getMiscFeeAmount() {
		return miscFeeAmount;
	}

	public void setMiscFeeAmount(Money miscFeeAmount) {
		this.miscFeeAmount = miscFeeAmount;
	}

	public void addFeesTrxnDetail(FeesTrxnDetailEntity feesTrxn) {
		feesTrxn.setAccountTrxn(this);
		feesTrxnDetails.add(feesTrxn);
	}

	public Money getMiscPenaltyAmount() {
		return miscPenaltyAmount;
	}

	public void setMiscPenaltyAmount(Money miscPenaltyAmount) {
		this.miscPenaltyAmount = miscPenaltyAmount;
	}
	
	public void setLoanTrxnDetailsForWriteOff(AccountActionDateEntity accountAction,Short personnelId) throws ServiceException {
		PersonnelBO personnel = new PersonnelPersistenceService().getPersonnel(personnelId);
		setAmount(accountAction.getPrincipal());
		principalAmount = accountAction.getPrincipal();
		setComments("Loan Written Off");
		setDueDate(accountAction.getActionDate());
		setPersonnel(personnel);
		setCustomer(accountAction.getCustomer());
		setInstallmentId(accountAction.getInstallmentId());
		setActionDate(new Date(System.currentTimeMillis()));
		MasterPersistenceService masterPersistenceService = (MasterPersistenceService) ServiceFactory
		.getInstance().getPersistenceService(
				PersistenceServiceName.MasterDataService);
		setAccountActionEntity((AccountActionEntity) masterPersistenceService.findById(AccountActionEntity.class,
						AccountConstants.ACTION_WRITEOFF));
	}

	public Money setPaymentDetails(AccountActionDateEntity accountAction,
			LoanPaymentData loanPaymentDataView, Short personnelId,
			java.util.Date transactionDate) throws ServiceException {
		// TODO - Remove this from loanPersistence service as a new
		// AccountActionEntity has been added
		// in business
		/*
		 * AccountAction action = loanPersistance
		 * .getAccountAction(AccountConstants.ACTION_LOAN_REPAYMENT);
		 */
		MasterPersistenceService masterPersistenceService = (MasterPersistenceService) ServiceFactory
				.getInstance().getPersistenceService(
						PersistenceServiceName.MasterDataService);
		PersonnelBO personnel = new PersonnelPersistenceService()
				.getPersonnel(personnelId);

		setActionDate(transactionDate);
		setDueDate(accountAction.getActionDate());
		setPersonnel(personnel);
		setAccountActionEntity((AccountActionEntity) masterPersistenceService
				.findById(AccountActionEntity.class,
						AccountConstants.ACTION_LOAN_REPAYMENT));
		setCustomer(accountAction.getCustomer());
		setComments("Payment rcvd.");
		setTrxnCreatedDate(new Timestamp(System.currentTimeMillis()));

		setInstallmentId(loanPaymentDataView.getInstallmentId());
		interestAmount = loanPaymentDataView.getInterestPaid();
		penaltyAmount = loanPaymentDataView.getPenaltyPaid();
		principalAmount = loanPaymentDataView.getPrincipalPaid();
		miscFeeAmount = loanPaymentDataView.getMiscFeePaid();
		miscPenaltyAmount = loanPaymentDataView.getMiscPenaltyPaid();

		Money totalFees = new Money();
		for (AccountFeesActionDetailEntity accountFeesActionDetail : accountAction
				.getAccountFeesActionDetails()) {
			if (loanPaymentDataView.getFeesPaid().containsKey(
					accountFeesActionDetail.getFee().getFeeId())) {
				accountFeesActionDetail.makePayment(loanPaymentDataView
						.getFeesPaid().get(
								accountFeesActionDetail.getFee().getFeeId()));
				FeesTrxnDetailEntity feesTrxnDetailBO = new FeesTrxnDetailEntity();
				feesTrxnDetailBO.makePayment(accountFeesActionDetail);
				addFeesTrxnDetail(feesTrxnDetailBO);
				totalFees = totalFees.add(accountFeesActionDetail
						.getFeeAmountPaid());
			}
		}
		return totalFees;
	}

	public void setSumAmount(Money totalFees) {
		setAmount(totalFees.add(principalAmount).add(interestAmount).add(
				penaltyAmount).add(miscFeeAmount).add(miscPenaltyAmount));
	}

	public AccountTrxnEntity generateReverseTrxn(String adjustmentComment)
			throws ApplicationException, SystemException {
		MasterPersistenceService masterPersistenceService = (MasterPersistenceService) ServiceFactory
				.getInstance().getPersistenceService(
						PersistenceServiceName.MasterDataService);
		MifosLogManager
				.getLogger(LoggerConstants.ACCOUNTSLOGGER)
				.debug(
						"Inside generate reverse transaction method of loan trxn detail");
		LoanTrxnDetailEntity reverseAccntTrxn = new LoanTrxnDetailEntity();
		reverseAccntTrxn.setAccount(getAccount());
		reverseAccntTrxn.setAccountPayment(getAccountPayment());
		reverseAccntTrxn.setPersonnel(getPersonnel());
		reverseAccntTrxn
				.setAccountActionEntity((AccountActionEntity) masterPersistenceService
						.findById(AccountActionEntity.class,
								AccountConstants.ACTION_LOAN_ADJUSTMENT));
		reverseAccntTrxn.setAmount(getAmount().negate());
		// TODO : make the duedate null also change the database.
		reverseAccntTrxn.setDueDate(getDueDate());
		if (null == adjustmentComment)
			reverseAccntTrxn.setComments(getComments());
		else
			reverseAccntTrxn.setComments(adjustmentComment);

		reverseAccntTrxn.setActionDate(getActionDate());
		reverseAccntTrxn.setCustomer(getCustomer());
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"Just before setting created date");
		reverseAccntTrxn.setTrxnCreatedDate(new Timestamp(System
				.currentTimeMillis()));

		reverseAccntTrxn.setPrincipalAmount(getPrincipalAmount().negate());
		reverseAccntTrxn.setInterestAmount(getInterestAmount().negate());
		reverseAccntTrxn.setPenaltyAmount(getPenaltyAmount().negate());
		reverseAccntTrxn.setMiscFeeAmount(getMiscFeeAmount().negate());
		reverseAccntTrxn.setMiscPenaltyAmount(getMiscPenaltyAmount().negate());
		reverseAccntTrxn.setInstallmentId(getInstallmentId());
		reverseAccntTrxn.setRelatedTrxn(this);
		if (null != getFeesTrxnDetails() && getFeesTrxnDetails().size() > 0) {
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"Before generating reverse entries for fees");
			for (FeesTrxnDetailEntity feeTrxnDetail : getFeesTrxnDetails()) {
				reverseAccntTrxn.addFeesTrxnDetail(feeTrxnDetail
						.generateReverseTrxn());
			}
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"after generating reverse entries for fees");
		}

		return reverseAccntTrxn;
	}

	public Money getFeeAmount() {
		Money feeAmnt = new Money();
		if (null != feesTrxnDetails && feesTrxnDetails.size() > 0) {
			for (FeesTrxnDetailEntity feesTrxn : feesTrxnDetails) {
				feeAmnt = feeAmnt.add(feesTrxn.getFeeAmount());
			}
		}

		return feeAmnt;
	}

	public FeesTrxnDetailEntity getFeesTrxn(Integer accountFeeId) {
		if (null != feesTrxnDetails && feesTrxnDetails.size() > 0) {
			for (FeesTrxnDetailEntity feesTrxn : feesTrxnDetails) {
				if (feesTrxn.getAccountFees().getAccountFeeId().equals(
						accountFeeId)) {
					return feesTrxn;
				}
			}
		}
		return null;
	}

	public void setDesbursementDetails(Money pricipalAmount, Money totalAmount) {
		setPrincipalAmount(pricipalAmount);
		setAmount(totalAmount);

	}

}
