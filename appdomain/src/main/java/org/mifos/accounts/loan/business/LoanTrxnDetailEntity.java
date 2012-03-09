/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.business.AccountFlagMapping;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountPenaltiesEntity;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.business.FeesTrxnDetailEntity;
import org.mifos.accounts.business.PenaltiesTrxnDetailEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStateFlag;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.persistence.LegacyGenericDao;
import org.mifos.framework.util.helpers.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * LoanTrxnDetailEntity encapsulates a financial transaction
 * such as disbursing a loan or making a payment on a loan.
 * Possible types of transactions are described by AccountActionTypes.
 */
public class LoanTrxnDetailEntity extends AccountTrxnEntity {

    private static final Logger logger = LoggerFactory.getLogger(LoanTrxnDetailEntity.class);

    private final Money principalAmount;

    private final Money interestAmount;

    private final Money penaltyAmount;

    private final Money miscFeeAmount;

    private final Money miscPenaltyAmount;

    private final Set<FeesTrxnDetailEntity> feesTrxnDetails;
    
    private final Set<PenaltiesTrxnDetailEntity> penaltiesTrxnDetails;

    private CalculatedInterestOnPayment calculatedInterestOnPayment;

    public Set<FeesTrxnDetailEntity> getFeesTrxnDetails() {
        return feesTrxnDetails;
    }
    
    public Set<PenaltiesTrxnDetailEntity> getPenaltiesTrxnDetails() {
        return penaltiesTrxnDetails;
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
    
    public void addPenaltiesTrxnDetail(PenaltiesTrxnDetailEntity penaltiesTrxn) {
        penaltiesTrxnDetails.add(penaltiesTrxn);
    }

    public Money getMiscPenaltyAmount() {
        return miscPenaltyAmount;
    }

    protected LoanTrxnDetailEntity() {
        feesTrxnDetails = new HashSet<FeesTrxnDetailEntity>();
        penaltiesTrxnDetails = new HashSet<PenaltiesTrxnDetailEntity>();
        principalAmount = null;
        interestAmount = null;
        penaltyAmount = null;
        miscFeeAmount = null;
        miscPenaltyAmount = null;
    }

    public LoanTrxnDetailEntity(AccountPaymentEntity accountPayment, AccountActionTypes accountActionType,
            Short installmentId, Date dueDate, PersonnelBO personnel, Date actionDate, Money amount, String comments,
            AccountTrxnEntity relatedTrxn, Money principalAmount, Money interestAmount, Money penaltyAmount,
            Money miscFeeAmount, Money miscPenaltyAmount, List<AccountFeesEntity> accountFees, List<AccountPenaltiesEntity> accountPenalties) {
        super(accountPayment, accountActionType, installmentId, dueDate, personnel, null, actionDate, amount,
                comments, relatedTrxn);
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
        
        penaltiesTrxnDetails = new HashSet<PenaltiesTrxnDetailEntity>();
        if (accountPenalties != null && accountPenalties.size() > 0) {
            for (AccountPenaltiesEntity accountPenaltiesEntity : accountPenalties) {
                addPenaltiesTrxnDetail(new PenaltiesTrxnDetailEntity(this, accountPenaltiesEntity, accountPenaltiesEntity
                        .getAccountPenaltyAmount()));
            }
        }
    }

    public LoanTrxnDetailEntity(AccountPaymentEntity accountPaymentEntity, LoanScheduleEntity loanScheduleEntity,
                                PersonnelBO personnel, Date transactionDate, AccountActionTypes accountActionType,
                                String comments, LegacyGenericDao persistence) {
        super(accountPaymentEntity, accountActionType, loanScheduleEntity.getInstallmentId(), loanScheduleEntity
                .getActionDate(), personnel, null, transactionDate, loanScheduleEntity.getPaymentAllocation().getTotalPaid(), comments,
                null);
        PaymentAllocation paymentAllocation = loanScheduleEntity.getPaymentAllocation();
        interestAmount = paymentAllocation.getTotalInterestPaid();
        penaltyAmount = paymentAllocation.getPenaltyPaid();
        principalAmount = paymentAllocation.getPrincipalPaid();
        miscFeeAmount = paymentAllocation.getMiscFeePaid();
        miscPenaltyAmount = paymentAllocation.getMiscPenaltyPaid();
        feesTrxnDetails = new HashSet<FeesTrxnDetailEntity>();
        for (AccountFeesActionDetailEntity accountFeesActionDetail : loanScheduleEntity.getAccountFeesActionDetails()) {
            Integer feeId = accountFeesActionDetail.getAccountFeesActionDetailId();
            if (feeId == null) { // special workaround for MIFOS-4517
                feeId = accountFeesActionDetail.hashCode();
            }
            if(paymentAllocation.isFeeAllocated(feeId)) {
                Money feePaid = paymentAllocation.getFeePaid(feeId);
                addFeesTrxnDetail(new FeesTrxnDetailEntity(this, accountFeesActionDetail.getAccountFee(), feePaid));
            }
        }
        
        penaltiesTrxnDetails = new HashSet<PenaltiesTrxnDetailEntity>();
        for (LoanPenaltyScheduleEntity loanPenaltyScheduleEntity : loanScheduleEntity.getLoanPenaltyScheduleEntities()) {
            Integer penaltyId = loanPenaltyScheduleEntity.getLoanPenaltyScheduleEntityId();
            if (penaltyId == null) { // special workaround for MIFOS-4517
                penaltyId = loanPenaltyScheduleEntity.hashCode();
            }
            if(paymentAllocation.isPenaltyAllocated(penaltyId)) {
                Money penaltyPaid = paymentAllocation.getPenaltyPaid(penaltyId);
                addPenaltiesTrxnDetail(new PenaltiesTrxnDetailEntity(this, loanPenaltyScheduleEntity.getAccountPenalty(), penaltyPaid));
            }
        }
    }

    @Override
    protected AccountTrxnEntity generateReverseTrxn(PersonnelBO loggedInUser, String adjustmentComment)
            throws AccountException {
        logger.debug(
                "Inside generate reverse transaction method of loan trxn detail");
        String comment = null;
        if (null == adjustmentComment) {
            comment = getComments();
        } else {
            comment = adjustmentComment;
        }

        LoanTrxnDetailEntity reverseAccntTrxn;

        reverseAccntTrxn = new LoanTrxnDetailEntity(getAccountPayment(),
                getReverseTransctionActionType(), getInstallmentId(), getDueDate(), loggedInUser, getActionDate(), getAmount()
                .negate(), comment, this, getPrincipalAmount().negate(), getInterestAmount().negate(),
                getPenaltyAmount().negate(), getMiscFeeAmount().negate(), getMiscPenaltyAmount().negate(), null, null);
        reverseAccntTrxn.setCalculatedInterestOnPayment(this.getCalculatedInterestOnPayment());

        if (null != getFeesTrxnDetails() && getFeesTrxnDetails().size() > 0) {
            logger.debug(
                    "Before generating reverse entries for fees");
            for (FeesTrxnDetailEntity feeTrxnDetail : getFeesTrxnDetails()) {
                reverseAccntTrxn.addFeesTrxnDetail(feeTrxnDetail.generateReverseTrxn(reverseAccntTrxn));
            }
            logger.debug("after generating reverse entries for fees");
        }
        
        if (null != getPenaltiesTrxnDetails() && getPenaltiesTrxnDetails().size() > 0) {
            logger.debug("Before generating reverse entries for penalties");
            for (PenaltiesTrxnDetailEntity penaltyTrxnDetail : getPenaltiesTrxnDetails()) {
                reverseAccntTrxn.addPenaltiesTrxnDetail(penaltyTrxnDetail.generateReverseTrxn(reverseAccntTrxn));
            }
            logger.debug("after generating reverse entries for penalties");
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
    
    public PenaltiesTrxnDetailEntity getPenaltiesTrxn(Integer accountPenaltyId) {
        if (null != penaltiesTrxnDetails && penaltiesTrxnDetails.size() > 0) {
            for (PenaltiesTrxnDetailEntity penaltiesTrxn : penaltiesTrxnDetails) {
                if (penaltiesTrxn.getAccountPenalties().getLoanAccountPenaltyId().equals(accountPenaltyId)) {
                    return penaltiesTrxn;
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

    Money totalPenaltyPaid() {
        return getPenaltyAmount().add(getMiscPenaltyAmount());
    }

    Money totalAndMiscFeesPaid() {
        return getFeeAmount().add(getMiscFeeAmount());
    }

    public boolean isNotEmptyTransaction() {
        return getInstallmentId() != null && !getInstallmentId().equals(Short.valueOf("0"));
    }

    void adjustFees(AccountFeesActionDetailEntity accntFeesAction) {
        FeesTrxnDetailEntity feesTrxnDetailEntity = getFeesTrxn(accntFeesAction.getAccountFeeId());
        if (feesTrxnDetailEntity != null) {
            ((LoanFeeScheduleEntity) accntFeesAction).adjustFees(feesTrxnDetailEntity);
        }
    }
    
    void adjustPenalties(LoanPenaltyScheduleEntity loanPenaltyScheduleEntity) {
        PenaltiesTrxnDetailEntity penaltiesTrxnDetailEntity = getPenaltiesTrxn(loanPenaltyScheduleEntity.getAccountPenaltyId());
        if(penaltiesTrxnDetailEntity != null) {
            loanPenaltyScheduleEntity.adjustPenalties(penaltiesTrxnDetailEntity);
        }
    }

    public CalculatedInterestOnPayment getCalculatedInterestOnPayment() {
        return calculatedInterestOnPayment;
    }

    public void setCalculatedInterestOnPayment(CalculatedInterestOnPayment calculatedInterestOnPayment) {
        this.calculatedInterestOnPayment = calculatedInterestOnPayment;
    }

    public void computeAndSetCalculatedInterestOnPayment(Money originalInterest, Money extraInterestPaid, Money interestDueTillPaid) {
        CalculatedInterestOnPayment calculatedInterestOnPayment = new CalculatedInterestOnPayment();
        calculatedInterestOnPayment.setLoanTrxnDetailEntity(this);
        calculatedInterestOnPayment.setOriginalInterest(originalInterest);
        calculatedInterestOnPayment.setExtraInterestPaid(extraInterestPaid);
        calculatedInterestOnPayment.setInterestDueTillPaid(interestDueTillPaid);
        setCalculatedInterestOnPayment(calculatedInterestOnPayment);
    }
}
