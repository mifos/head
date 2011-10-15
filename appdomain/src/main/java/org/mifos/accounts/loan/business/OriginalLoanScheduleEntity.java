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

import java.io.Serializable;
import java.sql.Date;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.business.CustomerBO;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

@NamedQueries( {
        @NamedQuery(
                name = "originalLoanScheduleEntity.getScheduleForLoan",
                query = " from org.mifos.accounts.loan.business.OriginalLoanScheduleEntity originalLoanSchedule" +
                        " where originalLoanSchedule.account.id = :id"
        )
})
@Entity
@Table(name = "original_loan_schedule")
public class OriginalLoanScheduleEntity implements Comparable<OriginalLoanScheduleEntity>, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer actionDateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private AccountBO account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerBO customer;

    @Column(name = "installment_id")
    private Short installmentId;

    @Column(name = "action_date")
    private Date actionDate;

    @Column(name = "payment_status")
    private Short paymentStatus;

    @Column(name = "payment_date")
    private Date paymentDate;


    @Type(type = "org.mifos.framework.util.helpers.MoneyCompositeUserType")
    @Columns(columns = {
            @Column(name = "principal_currency_id"),
            @Column(name = "principal")
    })
    private Money principal;

    @Type(type = "org.mifos.framework.util.helpers.MoneyCompositeUserType")
    @Columns(columns = {
            @Column(name = "interest_currency_id"),
            @Column(name = "interest")
    })
    private Money interest;

    // TODO: Instance variable "penalty" appears to be unused. Verify and
    // remove.
    @Type(type = "org.mifos.framework.util.helpers.MoneyCompositeUserType")
    @Columns(columns = {
            @Column(name = "penalty_currency_id"),
            @Column(name = "penalty")
    })
    private Money penalty;

    @Transient
    private Money extraInterest;

    @Type(type = "org.mifos.framework.util.helpers.MoneyCompositeUserType")
    @Columns(columns = {
            @Column(name = "miscFees_currency_id"),
            @Column(name = "miscFees")
    })
    private Money miscFee;

    @Type(type = "org.mifos.framework.util.helpers.MoneyCompositeUserType")
    @Columns(columns = {
            @Column(name = "misc_penalty_currency_id"),
            @Column(name = "misc_penalty")
    })
    private Money miscPenalty;

    @Type(type = "org.mifos.framework.util.helpers.MoneyCompositeUserType")
    @Columns(columns = {
            @Column(name = "principal_paid_currency_id"),
            @Column(name = "principal_paid")
    })
    private Money principalPaid;

    @Type(type = "org.mifos.framework.util.helpers.MoneyCompositeUserType")
    @Columns(columns = {
            @Column(name = "interest_paid_currency_id"),
            @Column(name = "interest_paid")
    })
    private Money interestPaid;

    @Type(type = "org.mifos.framework.util.helpers.MoneyCompositeUserType")
    @Columns(columns = {
            @Column(name = "penalty_paid_currency_id"),
            @Column(name = "penalty_paid")
    })
    private Money penaltyPaid;

    @Transient
    private Money extraInterestPaid;

    @Type(type = "org.mifos.framework.util.helpers.MoneyCompositeUserType")
    @Columns(columns = {
            @Column(name = "misc_fees_paid_currency_id"),
            @Column(name = "misc_fees_paid")
    })
    private Money miscFeePaid;

    @Type(type = "org.mifos.framework.util.helpers.MoneyCompositeUserType")
    @Columns(columns = {
            @Column(name = "misc_penalty_paid_currency_id"),
            @Column(name = "misc_penalty_paid")
    })
    private Money miscPenaltyPaid;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinColumn(name = "id", updatable = true, insertable = true)
    private Set<OriginalLoanFeeScheduleEntity> accountFeesActionDetails = new HashSet<OriginalLoanFeeScheduleEntity>();

    @Column(name = "version_no")
    private int versionNo;

    @Transient
    private PaymentAllocation paymentAllocation;


    public OriginalLoanScheduleEntity() {
    }

    public OriginalLoanScheduleEntity(LoanScheduleEntity loanScheduleEntity) {
        this.actionDateId = null;
        this.account = loanScheduleEntity.getAccount();
        this.customer = loanScheduleEntity.getCustomer();
        this.installmentId = loanScheduleEntity.getInstallmentId();
        this.actionDate = loanScheduleEntity.getActionDate();
        Short paymentStatus = loanScheduleEntity.getPaymentStatus();
        if (paymentStatus != null) {
            this.paymentStatus = paymentStatus;
        }
        this.principal = loanScheduleEntity.getPrincipal();
        this.interest = loanScheduleEntity.getInterest();
        this.penalty = loanScheduleEntity.getPenalty();
        this.extraInterest = loanScheduleEntity.getExtraInterest();
        this.miscFee = loanScheduleEntity.getMiscFee();
        this.miscPenalty = loanScheduleEntity.getMiscPenalty();
        this.miscFeePaid = loanScheduleEntity.getMiscFeePaid();
        this.miscPenaltyPaid = loanScheduleEntity.getMiscPenaltyPaid();
        this.extraInterestPaid = loanScheduleEntity.getExtraInterestPaid();
        this.penaltyPaid = loanScheduleEntity.getPenaltyPaid();
        this.interestPaid = loanScheduleEntity.getInterestPaid();
        this.principalPaid = loanScheduleEntity.getPrincipalPaid();
        this.versionNo = loanScheduleEntity.getVersionNo();
        this.paymentAllocation = loanScheduleEntity.getPaymentAllocation();
        for (AccountFeesActionDetailEntity accountFeesActionDetail : loanScheduleEntity.getAccountFeesActionDetails()) {
            this.addAccountFeesAction(new OriginalLoanFeeScheduleEntity(accountFeesActionDetail, this));
        }
        reset(getCurrency());
    }

    private void reset(MifosCurrency currency) {
        this.penaltyPaid = new Money(currency);
        this.extraInterest = new Money(currency);
        this.miscFeePaid = new Money(currency);
        this.miscPenaltyPaid = new Money(currency);
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

    public Collection<OriginalLoanFeeScheduleEntity> getAccountFeesActionDetails() {
        return accountFeesActionDetails;
    }

    public void addAccountFeesAction(OriginalLoanFeeScheduleEntity accountFeesAction) {
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

    public Money getTotalDueWithFees() {
        return getTotalDue().add(getTotalFeesDue());
    }

    void removeAccountFeesActionDetailEntity(AccountFeesActionDetailEntity accountFeesActionDetailEntity) {
        accountFeesActionDetails.remove(accountFeesActionDetailEntity);
    }

    public Money getMiscFeeDue() {
        return getMiscFee().subtract(getMiscFeePaid());
    }

    public Money getTotalFeesDue() {
        Money totalFees = new Money(getCurrency());
        for (OriginalLoanFeeScheduleEntity obj : accountFeesActionDetails) {
            totalFees = totalFees.add(obj.getFeeDue());
        }
        return totalFees;
    }

    public Money getTotalScheduledFeeAmountWithMiscFee() {
        Money totalFees = new Money(getCurrency());
        for (OriginalLoanFeeScheduleEntity obj : accountFeesActionDetails) {
            totalFees = totalFees.add(obj.getFeeAmount());
        }
        totalFees = totalFees.add(getMiscFee());
        return totalFees;
    }

    public Money getTotalPaymentDue() {
        return getTotalDue().add(getTotalFeesDue());
    }


    public void setVersionNo(int versionNo) {
        this.versionNo = versionNo;
    }

    public int getVersionNo() {
        return versionNo;
    }

    public RepaymentScheduleInstallment toDto() {
        return new RepaymentScheduleInstallment(this.installmentId,
                this.actionDate, this.principal, this.interest,
                this.getTotalFeesDue(), this.miscFee, this.miscPenalty);
    }

    public Money getExtraInterest() {
        return extraInterest == null ? Money.zero(getCurrency()) : extraInterest;
    }

    public void setExtraInterest(Money extraInterest) {
        this.extraInterest = extraInterest;
    }

    public Money getExtraInterestPaid() {
        return extraInterestPaid == null ? Money.zero(getCurrency()) : extraInterestPaid;
    }

    public void setExtraInterestPaid(Money extraInterestPaid) {
        this.extraInterestPaid = extraInterestPaid;
    }

    public Money getExtraInterestDue() {
        return getExtraInterest().subtract(getExtraInterestPaid());
    }

    public Money getEffectiveInterestDue() {
        return getInterestDue().add(getExtraInterestDue());
    }

    private void initPaymentAllocation(MifosCurrency currency) {
        paymentAllocation = new PaymentAllocation(currency);
    }

    public PaymentAllocation getPaymentAllocation() {
        return paymentAllocation;
    }

    public Integer getActionDateId() {
        return actionDateId;
    }

    public void setActionDateId(Integer actionDateId) {
        this.actionDateId = actionDateId;
    }

    public AccountBO getAccount() {
        return account;
    }

    public CustomerBO getCustomer() {
        return customer;
    }

    public Short getInstallmentId() {
        return installmentId;
    }

    public Date getActionDate() {
        return actionDate;
    }

    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Short getPaymentStatus() {
        return paymentStatus;
    }

    public PaymentStatus getPaymentStatusAsEnum() {
        return PaymentStatus.fromInt(paymentStatus);
    }

    public void setPaymentStatus(PaymentStatus status) {
        this.paymentStatus = status.getValue();
    }

    public void setPaymentStatus(Short paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public int compareDate(java.util.Date date) {
        return this.getActionDate().compareTo(date);
    }

    public boolean isPaid() {
        return this.getPaymentStatusAsEnum() == PaymentStatus.PAID;
    }

    @Override
	public int compareTo(OriginalLoanScheduleEntity obj) {
        return this.getInstallmentId().compareTo(obj.getInstallmentId());
    }

    public MifosCurrency getCurrency() {
        return account.getCurrency();
    }

    public boolean isDueToday() {
        return DateUtils.getDateWithoutTimeStamp(this.getActionDate().getTime()).equals(
                DateUtils.getCurrentDateWithoutTimeStamp());
    }

    public void setAccount(AccountBO account) {
        this.account = account;
    }

    public boolean isBeforeOrOn(LocalDate date) {
        return new LocalDate(this.actionDate).isBefore(date) || new LocalDate(this.actionDate).isEqual(date);
    }

    public boolean isNotPaid() {
        return !this.isPaid();
    }

    public void setInstallmentId(Short installmentId) {
        this.installmentId = installmentId;
    }

}