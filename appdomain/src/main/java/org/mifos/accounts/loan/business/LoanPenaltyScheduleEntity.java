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

import java.util.Date;

import org.mifos.accounts.business.AccountPenaltiesEntity;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.penalties.business.PenaltyBO;
import org.mifos.framework.business.AbstractEntity;
import org.mifos.framework.util.helpers.Money;

public class LoanPenaltyScheduleEntity extends AbstractEntity implements Comparable<LoanPenaltyScheduleEntity> {
    private Integer loanPenaltyScheduleEntityId;
    private final LoanScheduleEntity loanSchedule;
    private Short installmentId;
    private final PenaltyBO penalty;
    private final AccountPenaltiesEntity accountPenalty;
    private Money penaltyAmount;
    private Money penaltyAmountPaid;
    private Money penaltyAllocated;
    private Date lastApplied;
    private int versionNo;
    
    public LoanPenaltyScheduleEntity() {
        this(null, null, null, null, null);
    }
    
    public LoanPenaltyScheduleEntity(final LoanScheduleEntity loanSchedule, final PenaltyBO penalty,
            final AccountPenaltiesEntity accountPenalty, final Money penaltyAmount, final Date lastApplied) {
        this.loanSchedule = loanSchedule;
        
        if(loanSchedule != null) {
            this.installmentId = loanSchedule.getInstallmentId();
            this.penaltyAmountPaid = new Money(penaltyAmount.getCurrency());
        } else {
            this.installmentId = null;
        }
        
        this.penalty = penalty;
        this.accountPenalty = accountPenalty;
        this.penaltyAmount = penaltyAmount;
        this.lastApplied = lastApplied;
    }
    
    public LoanScheduleEntity getLoanSchedule() {
        return loanSchedule;
    }
    
    public AccountPenaltiesEntity getAccountPenalty() {
        return accountPenalty;
    }

    public Integer getLoanPenaltyScheduleEntityId() {
        return loanPenaltyScheduleEntityId;
    }

    public PenaltyBO getPenalty() {
        return penalty;
    }

    public Money getPenaltyAmount() {
        return penaltyAmount == null ? new Money(accountPenalty.getAccount().getCurrency()) : penaltyAmount;
    }
    
    protected void setPenaltyAmount(Money penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }

    public Money getPenaltyAmountPaid() {
        return penaltyAmountPaid == null ? new Money(accountPenalty.getAccount().getCurrency()) : penaltyAmountPaid;
    }

    public void setPenaltyAmountPaid(Money penaltyAmountPaid) {
        this.penaltyAmountPaid = penaltyAmountPaid;
    }
    
    public Short getInstallmentId() {
        return installmentId;
    }
    
    protected void makePayment(Money penaltyPaid) {
        this.penaltyAmountPaid = getPenaltyAmountPaid().add(penaltyPaid);
    }
    
    public Money getPenaltyDue() {
        return getPenaltyAmount().subtract(getPenaltyAmountPaid());
    }

    protected void makeRepaymentEnteries(String payFullOrPartial) {
        if (payFullOrPartial.equals(LoanConstants.PAY_FEES_PENALTY_INTEREST)||payFullOrPartial.equals(LoanConstants.PAY_FEES_PENALTY)) {
            setPenaltyAmountPaid(getPenaltyAmountPaid().add(getPenaltyDue()));
        }
    }

    protected Money waiveCharges() {
        Money chargeWaived = new Money(accountPenalty.getAccount().getCurrency());
        chargeWaived = chargeWaived.add(getPenaltyDue());
        setPenaltyAmount(getPenaltyAmountPaid());
        return chargeWaived;
    }

    @Override
    public int compareTo(final LoanPenaltyScheduleEntity obj) {
        return this.getPenalty().getPenaltyId().compareTo(obj.getPenalty().getPenaltyId());
    }

    public Money payPenalty(Money amount) {
        penaltyAllocated = min(amount, getPenaltyDue());
        penaltyAmountPaid = penaltyAmountPaid.add(penaltyAllocated);
        return amount.subtract(penaltyAllocated);
    }

    public Money getPenaltyAllocated() {
        return penaltyAllocated == null ? new Money(accountPenalty.getAccount().getCurrency()) : penaltyAllocated;
    }
    
    public void setLoanPenaltyScheduleEntityId(Integer loanPenaltyScheduleEntityId) {
        this.loanPenaltyScheduleEntityId = loanPenaltyScheduleEntityId;
    }

    public void setInstallmentId(Short installmentId) {
        this.installmentId = installmentId;
    }

    public Integer getAccountPenaltyId() {
        return accountPenalty.getLoanAccountPenaltyId();
    }
    
    public void setVersionNo(int versionNo) {
        this.versionNo = versionNo;
    }

    public int getVersionNo() {
        return versionNo;
    }

    public Date getLastApplied() {
        return lastApplied;
    }

    public void setLastApplied(final Date lastApplied) {
        this.lastApplied = lastApplied;
    }
    
}

