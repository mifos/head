/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.loan.schedule.domain.Installment;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.util.helpers.*;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Money;
import org.mifos.platform.util.CollectionUtils;

import java.util.*;

import static org.mifos.framework.util.helpers.NumberUtils.min;

public class LoanScheduleEntity extends AccountActionDateEntity {
    private Money principal;

    private Money interest;

    // TODO: Instance variable "penalty" appears to be unused. Verify and
    // remove.
    private Money penalty;

    private Money extraInterest;

    private Money miscFee;

    private Money miscPenalty;

    private Money principalPaid;

    private Money interestPaid;

    private Money penaltyPaid;

    private Money extraInterestPaid;

    private Money miscFeePaid;

    private Money miscPenaltyPaid;

    private Set<AccountFeesActionDetailEntity> accountFeesActionDetails = new HashSet<AccountFeesActionDetailEntity>();

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
        reset(account.getCurrency());
    }

    private void reset(MifosCurrency currency) {
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

    public Money getInterest() {
        return interest;
    }

    public void setInterest(Money interest) {
        this.interest = interest;
    }

    public Money getInterestPaid() {
        return interestPaid;
    }

    void setInterestPaid(Money interestPaid) {
        this.interestPaid = interestPaid;
    }

    void setPenalty(Money penalty) {
        this.penalty = penalty;
    }

    public Money getPenaltyPaid() {
        return penaltyPaid;
    }

    void setPenaltyPaid(Money penaltyPaid) {
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

    void setPrincipalPaid(Money principalPaid) {
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

    public Money getMiscFee() {
        return miscFee;
    }

    void setMiscFee(Money miscFee) {
        this.miscFee = miscFee;
    }

    public Money getMiscFeePaid() {
        return miscFeePaid;
    }

    void setMiscFeePaid(Money miscFeePaid) {
        this.miscFeePaid = miscFeePaid;
    }

    public Money getMiscPenalty() {
        return miscPenalty;
    }

    void setMiscPenalty(Money miscPenalty) {
        this.miscPenalty = miscPenalty;
    }

    public Money getMiscPenaltyPaid() {
        return miscPenaltyPaid;
    }

    public Money getMiscPenaltyDue() {
        return miscPenalty.subtract(miscPenaltyPaid);
    }

    void setMiscPenaltyPaid(Money miscPenaltyPaid) {
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

    void makeEarlyRepaymentEnteries(String payFullOrPartial) {
        if (payFullOrPartial.equals(LoanConstants.PAY_FEES_PENALTY_INTEREST)) {
            setPrincipalPaid(getPrincipalPaid().add(getPrincipalDue()));
            setInterestPaid(getInterestPaid().add(getInterestDue()));
            setPenaltyPaid(getPenaltyPaid().add(getPenaltyDue()));
            setMiscFeePaid(getMiscFeePaid().add(getMiscFee()));
            setMiscPenaltyPaid(getMiscPenaltyPaid().add(getMiscPenalty()));
            makeRepaymentEntries(payFullOrPartial);
        } else if (payFullOrPartial.equals(LoanConstants.PAY_FEES_PENALTY)) {
            setPrincipalPaid(getPrincipalPaid().add(getPrincipalDue()));
            setPenaltyPaid(getPenaltyPaid().add(getPenaltyDue()));
            setMiscFeePaid(getMiscFeePaid().add(getMiscFee()));
            setMiscPenaltyPaid(getMiscPenaltyPaid().add(getMiscPenalty()));
            makeRepaymentEntries(payFullOrPartial);
        } else {
            setPrincipalPaid(getPrincipalPaid().add(getPrincipalDue()));
            makeRepaymentEntries(payFullOrPartial);
        }
    }

    private void makeRepaymentEntries(String payFullOrPartial) {
        setPaymentStatus(PaymentStatus.PAID);
        setPaymentDate(new DateTimeService().getCurrentJavaSqlDate());
        Set<AccountFeesActionDetailEntity> accountFeesActionDetailSet = this.getAccountFeesActionDetails();
        for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountFeesActionDetailSet) {
            ((LoanFeeScheduleEntity) accountFeesActionDetailEntity).makeRepaymentEnteries(payFullOrPartial);
        }
    }

    private void updatePaymentDetails(Money principal, Money interest, Money penalty, Money miscPenalty, Money miscFee) {
        principalPaid = principalPaid.add(principal);
        interestPaid = interestPaid.add(interest);
        penaltyPaid = penaltyPaid.add(penalty);
        miscPenaltyPaid = miscPenaltyPaid.add(miscPenalty);
        miscFeePaid = miscFeePaid.add(miscFee);
    }

    public void updatePaymentDetails(LoanTrxnDetailEntity loanReverseTrxn) {
        updatePaymentDetails(loanReverseTrxn.getPrincipalAmount(), loanReverseTrxn.getInterestAmount(),
                loanReverseTrxn.getPenaltyAmount(), loanReverseTrxn.getMiscPenaltyAmount(),
                loanReverseTrxn.getMiscFeeAmount());
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

    void removeAccountFeesActionDetailEntity(AccountFeesActionDetailEntity accountFeesActionDetailEntity) {
        accountFeesActionDetails.remove(accountFeesActionDetailEntity);
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


    public RepaymentScheduleInstallment toDto(Locale userLocale) {
        return new RepaymentScheduleInstallment(this.installmentId,
                this.actionDate, this.principal, this.interest,
                this.getTotalFeesDue(), this.miscFee, this.miscPenalty, userLocale);
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

    private Money payMiscPenalty(final Money amount, boolean updateMiscPenaltyPaid) {
        Money payable = min(amount, getMiscPenaltyDue());
        allocateMiscPenalty(payable, updateMiscPenaltyPaid);
        return amount.subtract(payable);
    }

    private void allocateMiscPenalty(Money payable, boolean updateMiscPenaltyPaid) {
        paymentAllocation.allocateForMiscPenalty(payable);
        miscPenaltyPaid = updateMiscPenaltyPaid ? miscPenaltyPaid.add(payable) : payable;
    }

    private Money payPenalty(final Money amount, boolean updatePenaltyPaid) {
        Money payable = min(amount, (getPenalty().subtract(getPenaltyPaid())));
        allocatePenalty(payable, updatePenaltyPaid);
        return amount.subtract(payable);
    }

    private void allocatePenalty(Money payable, boolean updatePenaltyPaid) {
        paymentAllocation.allocateForPenalty(payable);
        penaltyPaid = updatePenaltyPaid ? penaltyPaid.add(payable) : payable;
    }

    private Money payMiscFees(final Money amount, boolean updateMiscFeePaid) {
        Money payable = min(amount, getMiscFeeDue());
        allocateMiscFees(payable, updateMiscFeePaid);
        return amount.subtract(payable);
    }

    private void allocateMiscFees(Money payable, boolean updateMiscFeePaid) {
        paymentAllocation.allocateForMiscFees(payable);
        miscFeePaid = updateMiscFeePaid ? miscFeePaid.add(payable) : payable;
    }

    private void allocateExtraInterest(Money payable) {
        paymentAllocation.allocateForExtraInterest(payable);
        extraInterestPaid = payable;
    }

    private Money payFees(final Money amount) {
        Money balance = amount;
        for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : getAccountFeesActionDetails()) {
            balance = accountFeesActionDetailEntity.payFee(balance);
            Integer feeId = accountFeesActionDetailEntity.getAccountFeesActionDetailId();
            Money feeAllocated = accountFeesActionDetailEntity.getFeeAllocated();
            paymentAllocation.allocateForFee(feeId, feeAllocated);
        }
        return balance;
    }

    private Money payInterest(final Money amount, boolean updateInterestPaid) {
        Money payable = min(amount, getInterestDue());
        allocateInterest(payable, updateInterestPaid);
        return amount.subtract(payable);
    }

    private void allocateInterest(Money payable, boolean updateInterestPaid) {
        paymentAllocation.allocateForInterest(payable);
        interestPaid = updateInterestPaid ? interestPaid.add(payable) : payable;
    }

    private Money payPrincipal(final Money amount, boolean updatePrincipalPaid) {
        Money payable = min(amount, getPrincipalDue());
        allocatePrincipal(payable, updatePrincipalPaid);
        return amount.subtract(payable);
    }

    private void allocatePrincipal(Money payable, boolean updatePrincipalPaid) {
        paymentAllocation.allocateForPrincipal(payable);
        principalPaid = updatePrincipalPaid ? principalPaid.add(payable) : payable;
    }

    public Money payComponents(Money paymentAmount, Date paymentDate) {
        initPaymentAllocation(paymentAmount.getCurrency());
        Money balanceAmount = paymentAmount;
        balanceAmount = payMiscPenalty(balanceAmount, true);
        balanceAmount = payPenalty(balanceAmount, true);
        balanceAmount = payMiscFees(balanceAmount, true);
        balanceAmount = payFees(balanceAmount);
        balanceAmount = payInterest(balanceAmount, true);
        balanceAmount = payPrincipal(balanceAmount, true);
        recordPayment(paymentDate);
        return balanceAmount;
    }

    public void payComponents(Installment installment, MifosCurrency currency, Date paymentDate) {
        initPaymentAllocation(currency);
        allocatePrincipal(new Money(currency, installment.getPrincipalPaid()), false);
        allocateInterest(new Money(currency, installment.getInterestPaid()), false);
        allocateExtraInterest(new Money(currency, installment.getExtraInterestPaid()));
        payFees(new Money(currency, installment.getFeesPaid()));
        allocateMiscFees(new Money(currency, installment.getMiscFeesPaid()), false);
        allocatePenalty(new Money(currency, installment.getPenaltyPaid()), false);
        allocateMiscPenalty(new Money(currency, installment.getMiscPenaltyPaid()), false);
        setInterest(new Money(currency, installment.getApplicableInterest()));
        recordPayment(paymentDate);
    }

    private void initPaymentAllocation(MifosCurrency currency) {
        paymentAllocation = new PaymentAllocation(currency);
    }

    public PaymentAllocation getPaymentAllocation() {
        return paymentAllocation;
    }

    void recordForAdjustment() {
        setPaymentStatus(PaymentStatus.UNPAID);
        setPaymentDate(null);
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

    public void updateLoanSummaryAndPerformanceHistory(AccountPaymentEntity accountPayment, PersonnelBO personnel, Date transactionDate) {

        accountPayment.addAccountTrxn(new LoanTrxnDetailEntity(accountPayment, this, personnel, transactionDate,
                AccountActionTypes.LOAN_REPAYMENT, AccountConstants.PAYMENT_RCVD, ((LoanBO)account).getLoanPersistence()));

        ((LoanBO)account).getLoanSummary().updatePaymentDetails(getPaymentAllocation());

        if (isPaid()) {
            ((LoanBO)account).getPerformanceHistory().incrementPayments();
        }
    }

    public Money applyPayment(AccountPaymentEntity accountPaymentEntity, Money balance, PersonnelBO personnel, Date transactionDate) {
        if (isNotPaid() && balance.isGreaterThanZero()) {
            balance = payComponents(balance, transactionDate);
            updateLoanSummaryAndPerformanceHistory(accountPaymentEntity, personnel, transactionDate);
        }
        return balance;
    }

    boolean hasFees() {
        return CollectionUtils.isNotEmpty(accountFeesActionDetails);
    }
}