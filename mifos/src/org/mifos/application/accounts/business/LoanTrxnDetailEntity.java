/**

 * LoanTrxnDetailEntity.java    version: 1.0

 

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

package org.mifos.application.accounts.business;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.loan.persistance.LoanPersistance;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.LoanPaymentData;
import org.mifos.application.master.persistence.service.MasterPersistenceService;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.PersistenceServiceName;

public class LoanTrxnDetailEntity extends AccountTrxnEntity {

	protected LoanTrxnDetailEntity() {
		feesTrxnDetails = new HashSet<FeesTrxnDetailEntity>();
		principalAmount = null;
		interestAmount = null;
		penaltyAmount = null;
		miscFeeAmount = null;
		miscPenaltyAmount = null;
	}

	private LoanPersistance loanPersistance;

	private final Money principalAmount;

	private final Money interestAmount;

	private final Money penaltyAmount;

	private final Money miscFeeAmount;

	private final Money miscPenaltyAmount;

	private final Set<FeesTrxnDetailEntity> feesTrxnDetails;

	public Set<FeesTrxnDetailEntity> getFeesTrxnDetails() {
		return feesTrxnDetails;
	}

	public Money getInterestAmount() {
		return interestAmount;
	}

	public Money getPenaltyAmount() {
		return penaltyAmount;
	}

	public Money getPrincipalAmount() {
		return principalAmount;
	}

	public Money getMiscFeeAmount() {
		return miscFeeAmount;
	}

	public void addFeesTrxnDetail(FeesTrxnDetailEntity feesTrxn) {
		feesTrxn.setAccountTrxn(this);
		feesTrxnDetails.add(feesTrxn);
	}

	public Money getMiscPenaltyAmount() {
		return miscPenaltyAmount;
	}

	public LoanTrxnDetailEntity(AccountPaymentEntity accountPayment,
			AccountActionEntity accountActionEntity, Short installmentId,
			Date dueDate, PersonnelBO personnel, Date actionDate, Money amount,
			String comments, AccountTrxnEntity relatedTrxn,
			Money principalAmount, Money interestAmount, Money penaltyAmount,
			Money miscFeeAmount, Money miscPenaltyAmount) {
		super(accountPayment, accountActionEntity, installmentId, dueDate,
				personnel, actionDate, amount, comments, relatedTrxn);
		loanPersistance = new LoanPersistance();
		this.principalAmount = principalAmount;
		this.interestAmount = interestAmount;
		this.penaltyAmount = penaltyAmount;
		this.miscFeeAmount = miscFeeAmount;
		this.miscPenaltyAmount = miscPenaltyAmount;
		feesTrxnDetails = new HashSet<FeesTrxnDetailEntity>();
	}

	public LoanTrxnDetailEntity(AccountPaymentEntity accountPayment,
			AccountActionEntity accountActionEntity,
			AccountActionDateEntity accountActionDateEntity,
			PersonnelBO personnel, String comments) {
		super(accountPayment, accountActionEntity, accountActionDateEntity
				.getInstallmentId(), accountActionDateEntity.getActionDate(),
				personnel, new Date(System.currentTimeMillis()),
				((LoanScheduleEntity) accountActionDateEntity).getPrincipal(),
				comments);
		loanPersistance = new LoanPersistance();
		this.principalAmount = ((LoanScheduleEntity) accountActionDateEntity)
				.getPrincipal();
		this.interestAmount = new Money();
		this.penaltyAmount = new Money();
		this.miscFeeAmount = new Money();
		this.miscPenaltyAmount = new Money();
		feesTrxnDetails = new HashSet<FeesTrxnDetailEntity>();
	}

	public LoanTrxnDetailEntity(AccountPaymentEntity accountPaymentEntity,
			Date recieptDate, AccountActionEntity accountActionEntity,
			PersonnelBO personnel, String comments, Short installmentId,
			Money totalAmount) {
		super(accountPaymentEntity, accountActionEntity, installmentId,
				recieptDate, personnel, recieptDate, totalAmount, comments);
		interestAmount = new Money();
		penaltyAmount = new Money();
		principalAmount = totalAmount;
		miscFeeAmount = new Money();
		miscPenaltyAmount = new Money();
		feesTrxnDetails = new HashSet<FeesTrxnDetailEntity>();
	}

	public LoanTrxnDetailEntity(AccountPaymentEntity accountPaymentEntity,
			Date recieptDate, AccountActionEntity accountActionEntity,
			PersonnelBO personnel, String comments, Short installmentId,
			Money totalFeeAmount, Set<AccountFeesEntity> accountFees) {
		super(accountPaymentEntity, accountActionEntity, installmentId,
				recieptDate, personnel, recieptDate, totalFeeAmount, comments);
		interestAmount = new Money();
		penaltyAmount = new Money();
		principalAmount = new Money();
		miscFeeAmount = new Money();
		miscPenaltyAmount = new Money();
		feesTrxnDetails = new HashSet<FeesTrxnDetailEntity>();
		for (AccountFeesEntity accountFeesEntity : accountFees) {
			if (accountFeesEntity.isTimeOfDisbursement()) {
				FeesTrxnDetailEntity feesTrxnDetailEntity = new FeesTrxnDetailEntity();
				feesTrxnDetailEntity.setFeeDetails(accountFeesEntity,
						accountFeesEntity.getAccountFeeAmount());
				addFeesTrxnDetail(feesTrxnDetailEntity);
			}
		}
	}

	public LoanTrxnDetailEntity(AccountPaymentEntity accountPaymentEntity,
			LoanPaymentData loanPaymentDataView, PersonnelBO personnel,
			java.util.Date transactionDate,
			AccountActionEntity accountActionEntity, Money amount,
			String comments) {

		super(accountPaymentEntity, accountActionEntity, loanPaymentDataView
				.getInstallmentId(), loanPaymentDataView.getAccountActionDate()
				.getActionDate(), personnel, transactionDate, amount, comments);
		interestAmount = loanPaymentDataView.getInterestPaid();
		penaltyAmount = loanPaymentDataView.getPenaltyPaid();
		principalAmount = loanPaymentDataView.getPrincipalPaid();
		miscFeeAmount = loanPaymentDataView.getMiscFeePaid();
		miscPenaltyAmount = loanPaymentDataView.getMiscPenaltyPaid();
		feesTrxnDetails = new HashSet<FeesTrxnDetailEntity>();
		LoanScheduleEntity loanSchedule = (LoanScheduleEntity) loanPaymentDataView
				.getAccountActionDate();
		for (AccountFeesActionDetailEntity accountFeesActionDetail : loanSchedule
				.getAccountFeesActionDetails()) {
			if (loanPaymentDataView.getFeesPaid().containsKey(
					accountFeesActionDetail.getFee().getFeeId())) {
				Money feeAmount = loanPaymentDataView.getFeesPaid().get(
						accountFeesActionDetail.getFee().getFeeId());
				accountFeesActionDetail.makePayment(feeAmount);
				FeesTrxnDetailEntity feesTrxnDetailBO = new FeesTrxnDetailEntity();
				feesTrxnDetailBO.setFeeDetails(accountFeesActionDetail
						.getAccountFee(), feeAmount);
				addFeesTrxnDetail(feesTrxnDetailBO);
			}
		}
	}

	public AccountTrxnEntity generateReverseTrxn(String adjustmentComment){
		MasterPersistenceService masterPersistenceService = new MasterPersistenceService();
		MifosLogManager
				.getLogger(LoggerConstants.ACCOUNTSLOGGER)
				.debug(
						"Inside generate reverse transaction method of loan trxn detail");
		String comment = null;
		if (null == adjustmentComment)
			comment = getComments();
		else
			comment = adjustmentComment;

		LoanTrxnDetailEntity reverseAccntTrxn = new LoanTrxnDetailEntity(
				getAccountPayment(),
				(AccountActionEntity) masterPersistenceService.findById(
						AccountActionEntity.class,
						AccountConstants.ACTION_LOAN_ADJUSTMENT),
				getInstallmentId(), getDueDate(), getPersonnel(),
				getActionDate(), getAmount().negate(), comment, this,
				getPrincipalAmount().negate(), getInterestAmount().negate(),
				getPenaltyAmount().negate(), getMiscFeeAmount().negate(),
				getMiscPenaltyAmount().negate());

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

}
