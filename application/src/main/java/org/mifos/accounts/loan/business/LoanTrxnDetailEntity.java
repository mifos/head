/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.accounts.loan.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.accounts.business.AccountActionEntity;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.business.AccountFlagMapping;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.business.FeesTrxnDetailEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStateFlag;
import org.mifos.accounts.util.helpers.LoanPaymentData;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;
import org.mifos.framework.util.helpers.Money;

/*
 * LoanTrxnDetailEntity encapsulates a financial transaction
 * such as disbursing a loan or making a payment on a loan.
 * Possible types of transactions are described by AccountActionTypes.
 */
public class LoanTrxnDetailEntity extends AccountTrxnEntity {

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
        feesTrxnDetails.add(feesTrxn);
    }

    public Money getMiscPenaltyAmount() {
        return miscPenaltyAmount;
    }

    protected LoanTrxnDetailEntity() {
        feesTrxnDetails = new HashSet<FeesTrxnDetailEntity>();
        principalAmount = null;
        interestAmount = null;
        penaltyAmount = null;
        miscFeeAmount = null;
        miscPenaltyAmount = null;
    }

    public LoanTrxnDetailEntity(AccountPaymentEntity accountPayment, AccountActionTypes accountActionType,
            Short installmentId, Date dueDate, PersonnelBO personnel, Date actionDate, Money amount, String comments,
            AccountTrxnEntity relatedTrxn, Money principalAmount, Money interestAmount, Money penaltyAmount,
            Money miscFeeAmount, Money miscPenaltyAmount, List<AccountFeesEntity> accountFees,
            Persistence persistence) {
        super(accountPayment, accountActionType, installmentId, dueDate, personnel, null, actionDate, amount,
                comments, relatedTrxn, persistence);
        this.principalAmount = principalAmount;
        this.interestAmount = interestAmount;
        this.penaltyAmount = penaltyAmount;
        this.miscFeeAmount = miscFeeAmount;
        this.miscPenaltyAmount = miscPenaltyAmount;
        feesTrxnDetails = new HashSet<FeesTrxnDetailEntity>();
        if (accountFees != null && accountFees.size() > 0) {
            for (AccountFeesEntity accountFeesEntity : accountFees) {
                addFeesTrxnDetail(new FeesTrxnDetailEntity(this, accountFeesEntity, accountFeesEntity
                        .getAccountFeeAmount()));
            }
        }
    }

    public LoanTrxnDetailEntity(AccountPaymentEntity accountPaymentEntity, LoanPaymentData loanPaymentData,
            PersonnelBO personnel, java.util.Date transactionDate, AccountActionTypes accountActionType,
            Money amount, String comments, Persistence persistence) {

        super(accountPaymentEntity, accountActionType, loanPaymentData.getInstallmentId(), loanPaymentData
                .getAccountActionDate().getActionDate(), personnel, null, transactionDate, amount, comments,
                null, persistence);
        interestAmount = loanPaymentData.getInterestPaid();
        penaltyAmount = loanPaymentData.getPenaltyPaid();
        principalAmount = loanPaymentData.getPrincipalPaid();
        miscFeeAmount = loanPaymentData.getMiscFeePaid();
        miscPenaltyAmount = loanPaymentData.getMiscPenaltyPaid();
        feesTrxnDetails = new HashSet<FeesTrxnDetailEntity>();
        LoanScheduleEntity loanSchedule = (LoanScheduleEntity) loanPaymentData.getAccountActionDate();
        for (AccountFeesActionDetailEntity accountFeesActionDetail : loanSchedule.getAccountFeesActionDetails()) {
            if (loanPaymentData.getFeesPaid().containsKey(accountFeesActionDetail.getFee().getFeeId())) {
                addFeesTrxnDetail(new FeesTrxnDetailEntity(this, accountFeesActionDetail.getAccountFee(),
                        loanPaymentData.getFeesPaid().get(accountFeesActionDetail.getFee().getFeeId())));
            }
        }
    }

    @Override
    protected AccountTrxnEntity generateReverseTrxn(PersonnelBO loggedInUser, String adjustmentComment)
            throws AccountException {
        MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
                "Inside generate reverse transaction method of loan trxn detail");
        String comment = null;
        if (null == adjustmentComment)
            comment = getComments();
        else
            comment = adjustmentComment;

        LoanTrxnDetailEntity reverseAccntTrxn;

        reverseAccntTrxn = new LoanTrxnDetailEntity(getAccountPayment(),
                getReverseTransctionActionType(), getInstallmentId(), getDueDate(), loggedInUser, getActionDate(), getAmount()
                .negate(), comment, this, getPrincipalAmount().negate(), getInterestAmount().negate(),
                getPenaltyAmount().negate(), getMiscFeeAmount().negate(), getMiscPenaltyAmount().negate(), null,
                new MasterPersistence());


        if (null != getFeesTrxnDetails() && getFeesTrxnDetails().size() > 0) {
            MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
                    "Before generating reverse entries for fees");
            for (FeesTrxnDetailEntity feeTrxnDetail : getFeesTrxnDetails()) {
                reverseAccntTrxn.addFeesTrxnDetail(feeTrxnDetail.generateReverseTrxn(reverseAccntTrxn));
            }
            MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER)
                    .debug("after generating reverse entries for fees");
        }

        return reverseAccntTrxn;
    }

    public Money getFeeAmount() {
        Money feeAmnt = new Money(getAccount().getCurrency());
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
                if (feesTrxn.getAccountFees().getAccountFeeId().equals(accountFeeId)) {
                    return feesTrxn;
                }
            }
        }
        return null;
    }

    private boolean isAccountCancelled() {
        if (getAccount().getAccountState().getId().equals(AccountState.LOAN_CANCELLED.getValue())) {
            Set<AccountFlagMapping> accountFlags = getAccount().getAccountFlags();
            if (accountFlags != null && accountFlags.size() > 0) {
                for (AccountFlagMapping accountFlagMapping : accountFlags) {
                    if (accountFlagMapping.getFlag().getId().equals(AccountStateFlag.LOAN_REVERSAL.getValue())) {
                        return true;
                    }

                }
            }
        }
        return false;
    }

    private AccountActionTypes getReverseTransctionActionType() {
        if (getAccountActionEntity().getId().equals(AccountActionTypes.DISBURSAL.getValue())) {
            return AccountActionTypes.LOAN_DISBURSAL_AMOUNT_REVERSAL;
        } else if (isAccountCancelled()) {
            return AccountActionTypes.LOAN_REVERSAL;
        } else {
            return AccountActionTypes.LOAN_ADJUSTMENT;
        }
    }
}
