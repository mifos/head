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

import static org.mifos.framework.util.helpers.NumberUtils.min;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountPenaltiesEntity;
import org.mifos.accounts.loan.persistance.LegacyLoanDao;
import org.mifos.accounts.loan.schedule.domain.Installment;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.penalties.business.PenaltyBO;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.OverDueAmounts;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyUtils;
import org.mifos.platform.util.CollectionUtils;

public class LoanScheduleEntity extends AccountActionDateEntity {

    private Money principal;
    private Money principalPaid;
    private Money interest;
    private Money interestPaid;

    private Money penalty;
    private Money penaltyPaid;

    private Money extraInterest;
    private Money extraInterestPaid;

    private Money miscFee;
    private Money miscFeePaid;

    private Money miscPenalty;
    private Money miscPenaltyPaid;

    private Set<AccountFeesActionDetailEntity> accountFeesActionDetails = new HashSet<AccountFeesActionDetailEntity>();
    private Set<LoanPenaltyScheduleEntity> loanPenaltiesSchedule = new HashSet<LoanPenaltyScheduleEntity>();

    private int versionNo;

    private PaymentAllocation paymentAllocation;

    protected LoanScheduleEntity() {
        super(null, null, null, null, null);
    }

    public LoanScheduleEntity(AccountBO account, CustomerBO customer, Short installmentId, java.sql.Date actionDate,
            PaymentStatus paymentStatus, Money principal, Money interest) {
        super(account, customer, installmentId, actionDate, paymentStatus);
        this.principal = principal;
        this.interest = interest;
        zeroRemainingFields(principal.getCurrency());
    }

    private void zeroRemainingFields(MifosCurrency currency) {
        this.penalty = new Money(currency);
        this.extraInterest = new Money(currency);
        this.miscFee = new Money(currency);
        this.miscPenalty = new Money(currency);
        this.principalPaid = new Money(currency);
        this.interestPaid = new Money(currency);
        this.penaltyPaid = new Money(currency);
        this.extraInterestPaid = new Money(currency);
        this.miscFeePaid = new Money(currency);
        this.miscPenaltyPaid = new Money(currency);
    }

    @Override
    public MifosCurrency getCurrency() {
        MifosCurrency currency = null;
        if (this.account != null) {
            currency = super.getCurrency();
        } else {
            currency = this.principal.getCurrency();
        }
        return currency;
    }

    public Money getInterest() {
        return interest;
    }

    public void setInterest(Money interest) {
        this.interest = interest;
    }

    public Money getInterestPaid() {
        return interestPaid;
    }

    public void setInterestPaid(Money interestPaid) {
        this.interestPaid = interestPaid;
    }

    public void setPenalty(Money penalty) {
        this.penalty = penalty;
    }

    public Money getPenaltyPaid() {
        return penaltyPaid;
    }
    
    public Money getTotalPenaltyPaid() {
        return penaltyPaid.add(miscPenaltyPaid);
    }

    public void setPenaltyPaid(Money penaltyPaid) {
        this.penaltyPaid = penaltyPaid;
    }

    public Money getPrincipal() {
        return principal;
    }

    public void setPrincipal(Money principal) {
        this.principal = principal;
    }

    public Money getPrincipalPaid() {
        return principalPaid;
    }

    public void setPrincipalPaid(Money principalPaid) {
        this.principalPaid = principalPaid;
    }

    public Money getPrincipalDue() {
        return principal.subtract(principalPaid);
    }

    public Money getInterestDue() {
        return interest.subtract(interestPaid);
    }

    public Money getPenalty() {
        return penalty;
    }

    public Set<AccountFeesActionDetailEntity> getAccountFeesActionDetails() {
        return accountFeesActionDetails;
    }

    public void addAccountFeesAction(AccountFeesActionDetailEntity accountFeesAction) {
        accountFeesActionDetails.add(accountFeesAction);
    }
    
    public Set<LoanPenaltyScheduleEntity> getLoanPenaltyScheduleEntities() {
        return loanPenaltiesSchedule;
    }

    public void addLoanPenaltySchedule(LoanPenaltyScheduleEntity loanPenaltySchedule) {
        loanPenaltiesSchedule.add(loanPenaltySchedule);
    }

    public Money getMiscFee() {
        return miscFee;
    }

    public void setMiscFee(Money miscFee) {
        this.miscFee = miscFee;
    }

    public Money getMiscFeePaid() {
        return miscFeePaid;
    }

    public void setMiscFeePaid(Money miscFeePaid) {
        this.miscFeePaid = miscFeePaid;
    }

    public Money getMiscPenalty() {
        return miscPenalty;
    }

    public void setMiscPenalty(Money miscPenalty) {
        this.miscPenalty = miscPenalty;
    }

    public Money getMiscPenaltyPaid() {
        return miscPenaltyPaid;
    }

    public Money getMiscPenaltyDue() {
        return miscPenalty.subtract(miscPenaltyPaid);
    }

    public void setMiscPenaltyPaid(Money miscPenaltyPaid) {
        this.miscPenaltyPaid = miscPenaltyPaid;
    }

    public Money getPenaltyDue() {
        return (penalty.add(miscPenalty)).subtract(penaltyPaid.add(miscPenaltyPaid));
    }

    public Money getTotalDue() {
        return principal.subtract(principalPaid).add(getEffectiveInterestDue()).add(getPenaltyDue()).add(getMiscFeeDue());
    }

    public Money getTotalDueWithoutPrincipal() {
        return getInterestDue().add(getPenaltyDue()).add(getMiscFeeDue());
    }

    public Money getTotalPenalty() {
        return penalty.add(miscPenalty);
    }

    public Money getTotalDueWithFees() {
        return getTotalDue().add(getTotalFeesDue());
    }

    public Money getTotalScheduleAmountWithFees() {
        return principal.add(
                interest.add(penalty).add(getTotalScheduledFeeAmountWithMiscFee()).add(miscPenalty));
    }

    public OverDueAmounts getDueAmnts() {
        OverDueAmounts overDueAmounts = new OverDueAmounts();
        overDueAmounts.setFeesOverdue(getTotalFeesDue().add(getMiscFeeDue()));
        overDueAmounts.setInterestOverdue(getInterestDue());
        overDueAmounts.setPenaltyOverdue(getPenaltyDue());
        overDueAmounts.setPrincipalOverDue(getPrincipalDue());
        overDueAmounts.setTotalPrincipalPaid(getPrincipalPaid());
        return overDueAmounts;
    }

    void makeEarlyRepaymentEntries(String payFullOrPartial, Money interestDue, Date paymentDate) {
        setPrincipalPaid(getPrincipalPaid().add(getPrincipalDue()));
        setExtraInterestPaid(getExtraInterestPaid().add(getExtraInterestDue()));
        if (payFullOrPartial.equals(LoanConstants.PAY_FEES_PENALTY_INTEREST)) {
            setInterestPaid(getInterestPaid().add(interestDue));
            setPenaltyPaid(getPenaltyPaid().add(getPenaltyDue()));
            if (getMiscFeePaid().isLessThan(getMiscFee())) {
            setMiscFeePaid(getMiscFeePaid().add(getMiscFee()));
            }
            if (getMiscPenaltyPaid().isLessThan(getMiscPenalty())) {
            setMiscPenaltyPaid(getMiscPenaltyPaid().add(getMiscPenalty()));
            }
        } else if (payFullOrPartial.equals(LoanConstants.PAY_FEES_PENALTY)) {
            setPenaltyPaid(getPenaltyPaid().add(getPenaltyDue()));
            if (getMiscFeePaid().isLessThan(getMiscFee())) {
            setMiscFeePaid(getMiscFeePaid().add(getMiscFee()));
            }
            if (getMiscPenaltyPaid().isLessThan(getMiscPenalty())) {
            setMiscPenaltyPaid(getMiscPenaltyPaid().add(getMiscPenalty()));
            }
        }
        makeRepaymentEntries(payFullOrPartial, paymentDate);
    }

    private void makeRepaymentEntries(String payFullOrPartial, Date paymentDate) {
        setPaymentStatus(PaymentStatus.PAID);
        setPaymentDate(new java.sql.Date(paymentDate.getTime()));
        Set<AccountFeesActionDetailEntity> accountFeesActionDetailSet = this.getAccountFeesActionDetails();
        for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountFeesActionDetailSet) {
            ((LoanFeeScheduleEntity) accountFeesActionDetailEntity).makeRepaymentEnteries(payFullOrPartial);
        }
        
        Set<LoanPenaltyScheduleEntity> loanPenaltyScheduleSet = this.getLoanPenaltyScheduleEntities();
        for (LoanPenaltyScheduleEntity loanPenaltyScheduleEntity : loanPenaltyScheduleSet) {
            loanPenaltyScheduleEntity.makeRepaymentEnteries(payFullOrPartial);
        }
    }

    public void updatePaymentDetailsForAdjustment(LoanTrxnDetailEntity loanReverseTrxn) {
        CalculatedInterestOnPayment interestOnPayment = loanReverseTrxn.getCalculatedInterestOnPayment();
        Money overdueInterestPaid = calculateExtraInterestPaid(interestOnPayment);
        principalPaid = principalPaid.add(loanReverseTrxn.getPrincipalAmount());
        interest = calculateAdjustedInterest(interestOnPayment, overdueInterestPaid, loanReverseTrxn);
        interestPaid = interestPaid.add(loanReverseTrxn.getInterestAmount()).add(overdueInterestPaid);
        penaltyPaid = penaltyPaid.add(loanReverseTrxn.getPenaltyAmount());
        miscPenaltyPaid = miscPenaltyPaid.add(loanReverseTrxn.getMiscPenaltyAmount());
        miscFeePaid = miscFeePaid.add(loanReverseTrxn.getMiscFeeAmount());
        extraInterestPaid = extraInterestPaid.subtract(overdueInterestPaid);
    }

    private Money calculateExtraInterestPaid(CalculatedInterestOnPayment interestOnPayment) {
        return interestOnPayment == null ? Money.zero(getCurrency()) : interestOnPayment.getExtraInterestPaid();
    }

    private Money calculateAdjustedInterest(CalculatedInterestOnPayment interestOnPayment, Money overdueInterestPaid,
                                            LoanTrxnDetailEntity loanReverseTrxn) {
        if (interestOnPayment != null && ((LoanBO)account).isDecliningBalanceInterestRecalculation()) {
            return interestOnPayment.getOriginalInterest().subtract(loanReverseTrxn.getInterestAmount()).subtract(overdueInterestPaid.
                    add(interestOnPayment.getInterestDueTillPaid()));
        }
        return interest;
    }

    Money waiveFeeCharges() {
        Money chargeWaived = new Money(getCurrency());
        chargeWaived = chargeWaived.add(getMiscFeeDue());
        setMiscFee(getMiscFeePaid());
        for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : getAccountFeesActionDetails()) {
            chargeWaived = chargeWaived.add(((LoanFeeScheduleEntity) accountFeesActionDetailEntity).waiveCharges());
        }
        return chargeWaived;
    }

    // watch out, this relies on passing in the same reference as the object held in the collection
    void removeAccountFeesActionDetailEntity(AccountFeesActionDetailEntity accountFeesActionDetailEntity) {
        accountFeesActionDetails.remove(accountFeesActionDetailEntity);
    }
    
    void removeLoanPenaltySchedule(LoanPenaltyScheduleEntity loanPenaltySchedule) {
        loanPenaltiesSchedule.remove(loanPenaltySchedule);
    }

    public Money getMiscFeeDue() {
        return getMiscFee().subtract(getMiscFeePaid());
    }

    public Money getTotalFeesDue() {
        Money totalFees = new Money(getCurrency());
        for (AccountFeesActionDetailEntity obj : accountFeesActionDetails) {
            totalFees = totalFees.add(obj.getFeeDue());
        }
        return totalFees;
    }
    
    public Money getTotalFeeAmountPaidWithMiscFee() {
        Money totalFees = new Money(getCurrency());
        for (AccountFeesActionDetailEntity obj : accountFeesActionDetails) {
            totalFees = totalFees.add(obj.getFeeAmountPaid());
        }
        totalFees = totalFees.add(getMiscFeePaid());
        return totalFees;
    }
    
    public Money getTotalScheduledFeeAmountWithMiscFee() {
        Money totalFees = new Money(getCurrency());
        for (AccountFeesActionDetailEntity obj : accountFeesActionDetails) {
            totalFees = totalFees.add(obj.getFeeAmount());
        }
        totalFees = totalFees.add(getMiscFee());
        return totalFees;
    }

    public Money getTotalFeesDueWithMiscFee() {
        return miscFee.add(getTotalFeesDue());
    }

    public Money getTotalFees() {
        Money totalFees = new Money(getCurrency());
        for (AccountFeesActionDetailEntity obj : accountFeesActionDetails) {
            totalFees = totalFees.add(obj.getFeeAmount());
        }
        return totalFees;
    }

    public Money getTotalFeesPaid() {
        Money totalFees = new Money(getCurrency());
        for (AccountFeesActionDetailEntity obj : accountFeesActionDetails) {
            totalFees = totalFees.add(obj.getFeeAmountPaid());
        }
        return totalFees;
    }

    public Money getTotalFeeDueWithMiscFeeDue() {
        return getMiscFeeDue().add(getTotalFeesDue());
    }
    
    public Money getTotalPaymentDue() {
        return getTotalDue().add(getTotalFeesDue());
    }

    Money removeFees(Short feeId) {
        Money feeAmount = null;
        AccountFeesActionDetailEntity objectToRemove = null;
        Set<AccountFeesActionDetailEntity> accountFeesActionDetailSet = this.getAccountFeesActionDetails();
        for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountFeesActionDetailSet) {
            if (accountFeesActionDetailEntity.getFee().getFeeId().equals(feeId)
                    && (accountFeesActionDetailEntity.getFeeAmountPaid() == null || accountFeesActionDetailEntity
                            .getFeeAmountPaid().isZero())) {
                objectToRemove = accountFeesActionDetailEntity;
                feeAmount = objectToRemove.getFeeAmount();
                break;
            } else if (accountFeesActionDetailEntity.getFee().getFeeId().equals(feeId)
                    && accountFeesActionDetailEntity.getFeeAmountPaid() != null
                    && accountFeesActionDetailEntity.getFeeAmountPaid().isGreaterThanZero()) {
                feeAmount = accountFeesActionDetailEntity.getFeeAmount().subtract(
                        accountFeesActionDetailEntity.getFeeAmountPaid());
                ((LoanFeeScheduleEntity) accountFeesActionDetailEntity).setFeeAmount(accountFeesActionDetailEntity
                        .getFeeAmountPaid());
                break;
            }
        }
        if (objectToRemove != null) {
            this.removeAccountFeesActionDetailEntity(objectToRemove);
        }
        return feeAmount;
    }

    public AccountFeesActionDetailEntity getAccountFeesAction(Short feeId) {
        for (AccountFeesActionDetailEntity accountFeesAction : getAccountFeesActionDetails()) {
            if (accountFeesAction.getFee().getFeeId().equals(feeId)) {
                return accountFeesAction;
            }
        }
        return null;
    }

    Money waivePenaltyCharges() {
        Money chargeWaived = new Money(getCurrency());
        chargeWaived = chargeWaived.add(getMiscPenaltyDue());
        setMiscPenalty(getMiscPenaltyPaid());
        setPenalty(getPenaltyPaid());
        for (LoanPenaltyScheduleEntity loanPenaltyScheduleEntity : getLoanPenaltyScheduleEntities()) {
            chargeWaived = chargeWaived.add(loanPenaltyScheduleEntity.waiveCharges());
        }
        return chargeWaived;
    }

    void applyMiscCharge(Short chargeType, Money charge) {
        if (chargeType.equals(Short.valueOf(AccountConstants.MISC_FEES))) {
            setMiscFee(getMiscFee().add(charge));
        } else if (chargeType.equals(Short.valueOf(AccountConstants.MISC_PENALTY))) {
            setMiscPenalty(getMiscPenalty().add(charge));
        }
    }

    public boolean isPrincipalZero() {
        return principal.isZero();
    }

    public boolean isFeeAlreadyAttatched(Short feeId) {
        for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : this.getAccountFeesActionDetails()) {
            if (accountFeesActionDetailEntity.getFee().getFeeId().equals(feeId)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPaymentAppliedToAccountFees() {
        Money feesPaid = new Money(getCurrency(),"0.0");
        for (AccountFeesActionDetailEntity accountFeesActionDetail : getAccountFeesActionDetails()) {
            feesPaid = feesPaid.add(accountFeesActionDetail.getFeeAmountPaid());
        }
        return feesPaid.isNonZero();
    }

    public boolean isPaymentApplied() {
        return getPrincipalPaid().isNonZero() || getEffectiveInterestPaid().isNonZero() || getMiscFeePaid().isNonZero()
                || getMiscPenaltyPaid().isNonZero() || isPaymentAppliedToAccountFees();
    }

    public void setVersionNo(int versionNo) {
        this.versionNo = versionNo;
    }

    public int getVersionNo() {
        return versionNo;
    }

    public List<AccountFeesActionDetailEntity> getAccountFeesActionDetailsSortedByFeeId() {
        List<AccountFeesActionDetailEntity> sortedList = new ArrayList<AccountFeesActionDetailEntity>();
        sortedList.addAll(this.getAccountFeesActionDetails());
        Collections.sort(sortedList);
        return sortedList;
    }


    public RepaymentScheduleInstallment toDto() {
        return new RepaymentScheduleInstallment(this.installmentId,
                this.actionDate, this.principal, this.interest,
                this.getTotalFeesDue(), this.miscFee, this.miscPenalty);
    }

    public boolean isSameAs(AccountActionDateEntity accountActionDateEntity) {
        return getInstallmentId().equals(accountActionDateEntity.getInstallmentId());
    }

    public Money getExtraInterest() {
        return extraInterest == null ? Money.zero(getCurrency()) : new Money(getCurrency(), extraInterest.getAmount());
    }

    public void setExtraInterest(Money extraInterest) {
        this.extraInterest = extraInterest;
    }

    public Money getExtraInterestPaid() {
        return extraInterestPaid == null ? Money.zero(getCurrency()) : new Money(getCurrency(), extraInterestPaid.getAmount());
    }

    public void setExtraInterestPaid(Money extraInterestPaid) {
        this.extraInterestPaid = extraInterestPaid;
    }

    public Money getExtraInterestDue() {
        return getExtraInterest().subtract(getExtraInterestPaid());
    }

    public Money getEffectiveInterestPaid() {
        return interestPaid.add(getExtraInterestPaid());
    }

    public Money getEffectiveInterestDue() {
        return getInterestDue().add(getExtraInterestDue());
    }

    private Money payMiscPenalty(final Money amount) {
        Money payable = min(amount, getMiscPenaltyDue());
        allocateMiscPenalty(payable);
        return amount.subtract(payable);
    }

    private void allocateMiscPenalty(Money payable) {
        paymentAllocation.allocateForMiscPenalty(payable);
        miscPenaltyPaid = miscPenaltyPaid.add(payable);
    }

    private Money payPenalty(final Money amount) {
        Money payable = min(amount, (getPenalty().subtract(getPenaltyPaid())));
        Money balance = amount;
        
        allocatePenalty(payable);
        
        for (LoanPenaltyScheduleEntity loanPenaltyScheduleEntity : getLoanPenaltyScheduleEntities()) {
            balance = loanPenaltyScheduleEntity.payPenalty(balance);
            Integer penaltyId = loanPenaltyScheduleEntity.getLoanPenaltyScheduleEntityId();
            if (penaltyId == null) { // special workaround for MIFOS-4517
                penaltyId = loanPenaltyScheduleEntity.hashCode();
            }
            Money penaltyAllocated = loanPenaltyScheduleEntity.getPenaltyAllocated();
            paymentAllocation.allocateForPenalty(penaltyId, penaltyAllocated);
        }
        
        return amount.subtract(payable);
    }

    private void allocatePenalty(Money payable) {
        paymentAllocation.allocateForPenalty(payable);
        penaltyPaid = penaltyPaid.add(payable);
    }

    private Money payMiscFees(final Money amount) {
        Money payable = min(amount, getMiscFeeDue());
        allocateMiscFees(payable);
        return amount.subtract(payable);
    }

    private void allocateMiscFees(Money payable) {
        paymentAllocation.allocateForMiscFees(payable);
        miscFeePaid = miscFeePaid.add(payable);
    }

    private void allocateExtraInterest(Money payable) {
        paymentAllocation.allocateForExtraInterest(payable);
        extraInterestPaid = extraInterestPaid.add(payable);
    }

    private Money payFees(final Money amount) {
        Money balance = amount;
        for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : getAccountFeesActionDetails()) {
            balance = accountFeesActionDetailEntity.payFee(balance);
            Integer feeId = accountFeesActionDetailEntity.getAccountFeesActionDetailId();
            if (feeId == null) { // special workaround for MIFOS-4517
                feeId = accountFeesActionDetailEntity.hashCode();
            }
            Money feeAllocated = accountFeesActionDetailEntity.getFeeAllocated();
            paymentAllocation.allocateForFee(feeId, feeAllocated);
        }
        return balance;
    }

    private Money payInterest(final Money amount) {
        Money payable = min(amount, getInterestDue());
        allocateInterest(payable);
        return amount.subtract(payable);
    }

    private void allocateInterest(Money payable) {
        paymentAllocation.allocateForInterest(payable);
        interestPaid = interestPaid.add(payable);
    }

    private Money reducePrincipalBy(final Money amount) {
        Money reducedBy = min(amount, getPrincipalDue());
        this.principal = this.principal.subtract(reducedBy);
        return amount.subtract(reducedBy);
    }
    
    private Money payPrincipal(final Money amount) {
        Money payable = min(amount, getPrincipalDue());
        allocatePrincipal(payable);
        return amount.subtract(payable);
    }

    private void allocatePrincipal(Money payable) {
        paymentAllocation.allocateForPrincipal(payable);
        principalPaid = principalPaid.add(payable);
    }

    public Money payComponents(Money paymentAmount, Date paymentDate) {
        initPaymentAllocation(paymentAmount.getCurrency());
        Money balanceAmount = paymentAmount;
        balanceAmount = payMiscPenalty(balanceAmount);
        balanceAmount = payPenalty(balanceAmount);
        balanceAmount = payMiscFees(balanceAmount);
        balanceAmount = payFees(balanceAmount);
        balanceAmount = payInterest(balanceAmount);
        balanceAmount = payPrincipal(balanceAmount);
        recordPayment(paymentDate);
        return balanceAmount;
    }

    public Money payInterestComponent(Money paymentAmount, Date paymentDate) {
        initPaymentAllocation(paymentAmount.getCurrency());
        Money balanceAmount = paymentAmount;
        balanceAmount = payInterest(balanceAmount);
        recordPayment(paymentDate);
        return balanceAmount;
    }

    public void payComponents(Installment installment, MifosCurrency currency, Date paymentDate) {
        initPaymentAllocation(currency);
        allocatePrincipal(new Money(currency, installment.getCurrentPrincipalPaid()));
        allocateInterest(new Money(currency, installment.getCurrentInterestPaid()));
        allocateExtraInterest(new Money(currency, installment.getCurrentExtraInterestPaid()));
        payFees(new Money(currency, installment.getCurrentFeesPaid()));
        allocateMiscFees(new Money(currency, installment.getCurrentMiscFeesPaid()));
        allocatePenalty(new Money(currency, installment.getCurrentPenaltyPaid()));
        allocateMiscPenalty(new Money(currency, installment.getCurrentMiscPenaltyPaid()));
        updateInterest(installment, currency);
        setExtraInterest(new Money(currency, installment.getExtraInterest()));
        recordPayment(paymentDate);
    }

    private void updateInterest(Installment installment, MifosCurrency currency) {
        if (installment.hasEffectiveInterest()) {
            setInterest(new Money(currency, installment.getEffectiveInterest().add(interestPaid.getAmount())));
        } else {
            setInterest(new Money(currency, installment.getInterest()));
        }
    }

    private void initPaymentAllocation(MifosCurrency currency) {
        paymentAllocation = new PaymentAllocation(currency);
    }

    public PaymentAllocation getPaymentAllocation() {
        return paymentAllocation;
    }

    void recordForAdjustment() {
        setPaymentStatus(PaymentStatus.UNPAID);
        setPaymentDate(getLastPaymentDate());
    }

    private java.sql.Date getLastPaymentDate() {
        AccountPaymentEntity lastPaymentToBeAdjusted = getAccount().getLastPmntToBeAdjusted();
        return lastPaymentToBeAdjusted == null ? null :
                new java.sql.Date(lastPaymentToBeAdjusted.getPaymentDate().getTime());
    }

    void recordPayment(Date paymentDate) {
        setPaymentDate(new java.sql.Date(paymentDate.getTime()));
        setPaymentStatus(getTotalDueWithFees().isTinyAmount() ? PaymentStatus.PAID : PaymentStatus.UNPAID);
    }

    public double getPrincipalAsDouble() {
        return principal.getAmount().doubleValue();
    }

    public double getInterestAsDouble() {
        return interest.getAmount().doubleValue();
    }

    public double getPenaltyAsDouble() {
        return penalty.getAmount().doubleValue();
    }

    public double getMiscFeeAsDouble() {
        return miscFee.getAmount().doubleValue();
    }

    public double getMiscPenaltyAsDouble() {
        return miscPenalty.getAmount().doubleValue();
    }

    public double getTotalFeesAsDouble() {
        return getTotalFees().getAmount().doubleValue();
    }

    public double getPrincipalPaidAsDouble() {
        return principalPaid.getAmount().doubleValue();
    }

    public double getInterestPaidAsDouble() {
        return interestPaid.getAmount().doubleValue();
    }

    public double getPenaltyPaidAsDouble() {
        return penaltyPaid.getAmount().doubleValue();
    }

    public double getMiscFeePaidAsDouble() {
        return miscFeePaid.getAmount().doubleValue();
    }

    public double getMiscPenaltyPaidAsDouble() {
        return miscPenaltyPaid.getAmount().doubleValue();
    }

    public double getTotalFeesPaidAsDouble() {
        return getTotalFeesPaid().getAmount().doubleValue();
    }

    public double getPrincipalDueAsDouble() {
        return getPrincipalDue().getAmount().doubleValue();
    }

    public double getInterestDueAsDouble() {
        return getInterestDue().getAmount().doubleValue();
    }

    public double getPenaltyDueAsDouble() {
        return getPenaltyDue().getAmount().doubleValue();
    }

    public double getMiscFeesDueAsDouble() {
        return getMiscFeeDue().getAmount().doubleValue();
    }

    public double getMiscPenaltyDueAsDouble() {
        return getMiscPenaltyDue().getAmount().doubleValue();
    }

    public double getTotalFeesDueAsDouble() {
        return getTotalFeesDue().getAmount().doubleValue();
    }

    public LoanTrxnDetailEntity updateSummaryAndPerformanceHistory(AccountPaymentEntity accountPayment,
                                                                   PersonnelBO personnel, Date transactionDate) {

        LoanBO loanBO = (LoanBO) account;
        LegacyLoanDao legacyLoanDao = loanBO.getlegacyLoanDao();
        LoanTrxnDetailEntity loanTrxnDetailEntity = recordTransaction(accountPayment, personnel, transactionDate, legacyLoanDao);
        loanBO.recordSummaryAndPerfHistory(isPaid(), paymentAllocation);
        return loanTrxnDetailEntity;
    }

    private LoanTrxnDetailEntity recordTransaction(AccountPaymentEntity accountPayment, PersonnelBO personnel,
                                                   Date transactionDate, LegacyLoanDao legacyLoanDao) {
        // TODO: Avoid passing the persistence instance in the constructor for reference data lookup
        LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity(accountPayment, this, personnel, transactionDate,
                AccountActionTypes.LOAN_REPAYMENT, AccountConstants.PAYMENT_RCVD, legacyLoanDao);
        accountPayment.addAccountTrxn(loanTrxnDetailEntity);
        return loanTrxnDetailEntity;
    }

    public Money applyPayment(AccountPaymentEntity accountPaymentEntity, Money balance, PersonnelBO personnel, Date transactionDate) {
        if (isNotPaid() && balance.isGreaterThanZero()) {
            balance = payComponents(balance, transactionDate);
            updateSummaryAndPerformanceHistory(accountPaymentEntity, personnel, transactionDate);
        }
        return balance;
    }

    public Money applyPaymentToInterest(AccountPaymentEntity accountPaymentEntity, Money balance, PersonnelBO personnel, Date transactionDate) {
        if (isNotPaid() && balance.isGreaterThanZero()) {
            balance = payInterestComponent(balance, transactionDate);
            updateSummaryAndPerformanceHistory(accountPaymentEntity, personnel, transactionDate);
        }
        return balance;
    }
    
    public Money reducePrincipal(AccountPaymentEntity accountPaymentEntity,
			Money balance, PersonnelBO personnel, Date transactionDate) {
    	
    	if (isNotPaid() && balance.isGreaterThanZero() && getPrincipalDue().isGreaterThanZero()) {
    		initPaymentAllocation(balance.getCurrency());
    		balance = reducePrincipalBy(balance);
    		updateSummaryAndPerformanceHistory(accountPaymentEntity, personnel, transactionDate);
    	}
    	
		return balance;
	}
    
    public Money applyOverPayment(AccountPaymentEntity accountPaymentEntity, Money balance, PersonnelBO personnel, Date transactionDate) {
        if (isNotPaid() && balance.isGreaterThanZero()) {
            balance = payComponentsNewMechanism(balance, transactionDate);
            updateSummaryAndPerformanceHistory(accountPaymentEntity, personnel, transactionDate);
        }
        return balance;
    }
    
    public Money payComponentsNewMechanism(Money paymentAmount, Date paymentDate) {
        initPaymentAllocation(paymentAmount.getCurrency());
        Money balanceAmount = paymentAmount;
        
        balanceAmount = payPrincipal(balanceAmount);
        if (isPrincipalZero()) {
        	// the entire principal was paid in advance so no interest due on this installment.
        }
        
        balanceAmount = payMiscPenalty(balanceAmount);
        balanceAmount = payPenalty(balanceAmount);
        balanceAmount = payMiscFees(balanceAmount);
        balanceAmount = payFees(balanceAmount);
        balanceAmount = payInterest(balanceAmount);
        recordPayment(paymentDate);
        return balanceAmount;
    }

    boolean hasFees() {
        return CollectionUtils.isNotEmpty(accountFeesActionDetails);
    }
    
    boolean hasPenalties() {
        return CollectionUtils.isNotEmpty(loanPenaltiesSchedule);
    }

    public void setPaymentAllocation(PaymentAllocation paymentAllocation) {
        this.paymentAllocation = paymentAllocation;
    }

    double getExtraInterestPaidAsDouble() {
        return getExtraInterestPaid().getAmount().doubleValue();
    }

    public Money getEffectiveInterest() {
        return interest.add(getExtraInterest());
    }

	public void updatePrincipalPaidby(AccountPaymentEntity accountPayment, PersonnelBO personnel) {
		initPaymentAllocation(getCurrency());
		this.principalPaid = this.principalPaid.add(accountPayment.getAmount());
		this.principal = this.principal.add(accountPayment.getAmount());
		this.getPaymentAllocation().allocateForPrincipal(accountPayment.getAmount());
	}
	
	public LoanPenaltyScheduleEntity getPenaltyScheduleEntity(final Short penaltyId) {
	    List<LoanPenaltyScheduleEntity> list = new ArrayList<LoanPenaltyScheduleEntity>(getLoanPenaltyScheduleEntities());
        
	    LoanPenaltyScheduleEntity entity = null;
        for(LoanPenaltyScheduleEntity item : list) {
            if(item.getPenalty().getPenaltyId().equals(penaltyId)) {
                entity = item;
                break;
            }
        }
        
        return entity;
	}
	
	Money removePenalties(Short penaltyId) {
        Money penaltyAmount = null;
        LoanPenaltyScheduleEntity objectToRemove = null;
        Set<LoanPenaltyScheduleEntity> loanPenaltyScheduleEntitySet = this.getLoanPenaltyScheduleEntities();
        for (LoanPenaltyScheduleEntity loanPenaltyScheduleEntity : loanPenaltyScheduleEntitySet) {
            if (loanPenaltyScheduleEntity.getPenalty().getPenaltyId().equals(penaltyId)
                    && (loanPenaltyScheduleEntity.getPenaltyAmountPaid() == null || loanPenaltyScheduleEntity
                            .getPenaltyAmountPaid().isZero())) {
                objectToRemove = loanPenaltyScheduleEntity;
                penaltyAmount = objectToRemove.getPenaltyAmount();
                break;
            } else if (loanPenaltyScheduleEntity.getPenalty().getPenaltyId().equals(penaltyId)
                    && loanPenaltyScheduleEntity.getPenaltyAmountPaid() != null
                    && loanPenaltyScheduleEntity.getPenaltyAmountPaid().isGreaterThanZero()) {
                penaltyAmount = loanPenaltyScheduleEntity.getPenaltyAmount().subtract(
                        loanPenaltyScheduleEntity.getPenaltyAmountPaid());
                loanPenaltyScheduleEntity.setPenaltyAmount(loanPenaltyScheduleEntity.getPenaltyAmountPaid());
                setPenalty(getPenaltyPaid());
                break;
            }
        }
        if (objectToRemove != null) {
            this.removeLoanPenaltySchedule(objectToRemove);
            penalty = penalty.subtract(penaltyAmount);
        }
        return penaltyAmount;
    }
	
	public Money getTotalAmountOfInstallment(){
	    Money money = new Money(getCurrency());
	    money = money.add(getTotalFees())
	            .add(getTotalPenalty())
	            .add(getPrincipal())
	            .add(getInterest())
	            .add(getMiscFee());
	    
	    return money;
	}
	
	public Money getTotalPaidAmount(){
	    Money money = new Money(getCurrency());
	    money = money.add(getInterestPaid())
	            .add(getTotalFeesPaid())
	            .add(getTotalPenaltyPaid())
	            .add(getPrincipalPaid())
	            .add(getMiscFeePaid());
	    
	    return money;
	}
	
	public BigDecimal getPaidProportion() {
	    BigDecimal proportion = getTotalPaidAmount().divide(getTotalAmountOfInstallment());
	    
	    if(proportion.compareTo(BigDecimal.ZERO)<0) {
	        proportion = BigDecimal.ZERO;
	    } else if(proportion.compareTo(BigDecimal.ONE)>0) {
	        proportion = BigDecimal.ONE;
	    }
	    
        return proportion;
	}
	
	public BigDecimal getProportionPaidBy(BigDecimal amount){
	    return new BigDecimal(amount.doubleValue()/getTotalAmountOfInstallment().getAmount().doubleValue());
	}
	
	public Money getAmountToBePaidToGetExpectedProportion(BigDecimal expected) {
	    Money amount = getTotalAmountOfInstallment().multiply(expected);
	    amount = amount.subtract(getTotalPaidAmount());
	    
	    return amount;
	}
	
	public void removeAllFees() {        
	    while(getAccountFeesActionDetails().iterator().hasNext()) {
	        AccountFeesActionDetailEntity fee = getAccountFeesActionDetails().iterator().next();
	        removeAccountFeesActionDetailEntity(fee);
	    }
	}
 
	public void removeAllPenalties() {
	    while(getLoanPenaltyScheduleEntities().iterator().hasNext()) {
	        LoanPenaltyScheduleEntity penalty = getLoanPenaltyScheduleEntities().iterator().next();
	        removePenalties(penalty.getPenalty().getPenaltyId());
	    }
	}
}
