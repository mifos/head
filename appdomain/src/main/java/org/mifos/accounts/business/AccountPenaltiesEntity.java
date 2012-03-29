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

package org.mifos.accounts.business;

import java.util.Date;
import java.util.List;

import org.mifos.accounts.penalties.business.AmountPenaltyBO;
import org.mifos.accounts.penalties.business.PenaltyBO;
import org.mifos.accounts.penalties.util.helpers.PenaltyPeriod;
import org.mifos.accounts.penalties.util.helpers.PenaltyStatus;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.business.AbstractEntity;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Money;

public class AccountPenaltiesEntity extends AbstractEntity {
    private final Integer loanAccountPenaltyId;
    private AccountBO account;
    private final PenaltyBO penalty;
    private Money accountPenaltyAmount;
    private Double penaltyAmount;
    private Integer calculativeCount = 0;
    private Short penaltyStatus;
    private Date createdDate;
    private Date statusChangeDate;
    private Date lastAppliedDate;
    private int versionNo;
    
    protected AccountPenaltiesEntity() {
        super();
        loanAccountPenaltyId = null;
        account = null;
        penalty = null;
        createdDate = null;
    }
    
    public AccountPenaltiesEntity(final AccountBO account, final PenaltyBO penalty, final Double penaltyAmount) {
        loanAccountPenaltyId = null;
        this.account = account;
        this.penalty = penalty;
        this.penaltyAmount = penaltyAmount;
        this.createdDate = new DateTimeService().getCurrentJavaSqlDate();
        
        MifosCurrency currency = account == null ? Money.getDefaultCurrency() : account.getCurrency();
        this.accountPenaltyAmount = new Money(currency, String.valueOf(penaltyAmount));
    }

    public AccountPenaltiesEntity(final AccountBO account, final PenaltyBO penalty, final Double penaltyAmount,
            final Short penaltyStatus, final Date statusChangeDate, final Date lastAppliedDate) {
        loanAccountPenaltyId = null;
        this.account = account;
        this.penalty = penalty;
        this.accountPenaltyAmount = new Money(account.getCurrency(), String.valueOf(penaltyAmount));
        this.penaltyAmount = penaltyAmount;
        this.penaltyStatus = penaltyStatus;
        this.statusChangeDate = statusChangeDate;
        this.lastAppliedDate = lastAppliedDate;
        this.createdDate = new DateTimeService().getCurrentJavaSqlDate();
    }

    public AccountBO getAccount() {
        return account;
    }
    
    public Integer getLoanAccountPenaltyId() {
        return loanAccountPenaltyId;
    }

    public Money getAccountPenaltyAmount() {
        return accountPenaltyAmount;
    }

    public void setAccountPenaltyAmount(final Money accountPenaltyAmount) {
        this.accountPenaltyAmount = accountPenaltyAmount;
    }

    public Double getPenaltyAmount() {
        return penaltyAmount;
    }

    public void setPenaltyAmount(final Double penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }
    
    public Integer getCalculativeCount() {
        return calculativeCount;
    }

    public void incrementCalculativeCount() {
        ++calculativeCount;
    }
    
    protected void setCalculativeCount(Integer calculativeCount) {
        this.calculativeCount = calculativeCount;
    }

    public PenaltyBO getPenalty() {
        return penalty;
    }

    public Short getPenaltyStatus() {
        return penaltyStatus;
    }
    
    public PenaltyStatus getPenaltyStatusAsEnum() {
        return PenaltyStatus.getPenaltyStatus(penaltyStatus);
    }
    
    public boolean isAmountPenalty() {
        return penalty instanceof AmountPenaltyBO;
    }
    
    /**
     * For hibernate.
     */
    void setPenaltyStatus(final Short penaltyStatus) {
        this.penaltyStatus = penaltyStatus;
    }
    
    /**
     * Low-level setting function. Methods like
     * {@link #changePenaltyStatus(PenaltyStatus, Date)} and others to be created better
     * express what kinds of actions take place.
     */
    public void setPenaltyStatus(final PenaltyStatus status) {
        this.penaltyStatus = status.getValue();
    }
    
    public Date getStatusChangeDate() {
        return statusChangeDate;
    }
    
    public void setStatusChangeDate(final Date statusChangeDate) {
        this.statusChangeDate = statusChangeDate;
    }

    public Date getLastAppliedDate() {
        return lastAppliedDate;
    }

    public void setLastAppliedDate(final Date lastAppliedDate) {
        this.lastAppliedDate = lastAppliedDate;
    }
    
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final Date createdDate) {
        this.createdDate = createdDate;
    }

    public void changePenaltyStatus(final PenaltyStatus status, final Date changeDate) {
        this.setPenaltyStatus(status);
        this.setStatusChangeDate(changeDate);
    }
    
    public boolean isOneTime() {
        return getPenalty().isOneTime();
    }
    
    public boolean isDailyTime() {
        return getPenalty().isDailyTime();
    }
    
    public boolean isWeeklyTime() {
        return getPenalty().isWeeklyTime();
    }
    
    public boolean isMonthlyTime() {
        return getPenalty().isMonthlyTime();
    }
    
    public boolean hasPeriodType() {
        return getPenalty().getPeriodType().getPenaltyPeriod() != PenaltyPeriod.NONE;
    }
    
    public boolean isActive() {
        return penaltyStatus == null || penaltyStatus.equals(PenaltyStatus.ACTIVE.getValue());
    }
    
    public void setAccount(AccountBO account) {
        this.account = account;
    }

    public void setVersionNo(int versionNo) {
        this.versionNo = versionNo;
    }

    public int getVersionNo() {
        return versionNo;
    }
    
    public static boolean isPeriodicPenaltyDuplicated(List<AccountPenaltiesEntity> accountPenalties, PenaltyBO penalty) {
        int penaltyOccurrenceCount = 0;

        if (!penalty.isOneTime()) {
            for (AccountPenaltiesEntity accountPenalty : accountPenalties) {
                if (accountPenalty.getPenalty().getPenaltyName().equals(penalty.getPenaltyName())) {
                    penaltyOccurrenceCount++;
                }
            }
        }

        return penaltyOccurrenceCount > 1;
    }

}
