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

package org.mifos.accounts.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.util.helpers.FeeStatus;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.business.AbstractEntity;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

/**
 *
 */
public class AccountFeesEntity extends AbstractEntity {

    private final Integer accountFeeId;

    private AccountBO account;

    private final FeeBO fees;

    private Money accountFeeAmount;

    private Double feeAmount;

    private Short feeStatus;

    private Date statusChangeDate;

    private Date lastAppliedDate;

    private int versionNo;

    /**
     * default constructor for hibernate usage
     */
    protected AccountFeesEntity() {
        super();
        accountFeeId = null;
        account = null;
        fees = null;

    }

    public AccountFeesEntity(final AccountBO account, final FeeBO fee, final Double feeAmount) {
        accountFeeId = null;
        this.account = account;
        this.fees = fee;
        this.feeAmount = feeAmount;

        MifosCurrency currency = Money.getDefaultCurrency();
        if (account != null) {
            currency = account.getCurrency();
        }

        this.accountFeeAmount = new Money(currency, String.valueOf(feeAmount));
    }

    public AccountFeesEntity(final AccountBO account, final FeeBO fees, final Double feeAmount, final Short feeStatus, final Date statusChangeDate,
            final Date lastAppliedDate) {
        accountFeeId = null;
        this.account = account;
        this.fees = fees;
        this.accountFeeAmount = new Money(account.getCurrency(), String.valueOf(feeAmount));
        this.feeAmount = feeAmount;
        this.feeStatus = feeStatus;
        this.statusChangeDate = statusChangeDate;
        this.lastAppliedDate = lastAppliedDate;
    }

    public AccountBO getAccount() {
        return account;
    }

    public Integer getAccountFeeId() {
        return accountFeeId;
    }

    public Money getAccountFeeAmount() {
        return accountFeeAmount;
    }

    public void setAccountFeeAmount(final Money accountFeeAmount) {
        this.accountFeeAmount = accountFeeAmount;
    }

    public Double getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(final Double feeAmount) {
        this.feeAmount = feeAmount;
    }

    public FeeBO getFees() {
        return fees;
    }

    public Short getFeeStatus() {
        return feeStatus;
    }

    public FeeStatus getFeeStatusAsEnum() {
        return FeeStatus.getFeeStatus(feeStatus);
    }

    /**
     * For hibernate.
     */
    void setFeeStatus(final Short feeStatus) {
        this.feeStatus = feeStatus;
    }

    /**
     * Low-level setting function. Methods like
     * {@link #changeFeesStatus(FeeStatus, Date)} and others to be created better
     * express what kinds of actions take place.
     */
    public void setFeeStatus(final FeeStatus status) {
        this.feeStatus = status.getValue();
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

    public void changeFeesStatus(final FeeStatus status, final Date changeDate) {
        this.setFeeStatus(status);
        this.setStatusChangeDate(changeDate);
    }

    public boolean isTimeOfDisbursement() {
        return getFees().isTimeOfDisbursement();
    }

    public boolean isPeriodic() {
        return getFees().isPeriodic();
    }

    public boolean isOneTime() {
        return getFees().isOneTime();
    }

    public boolean isActive() {
        if (feeStatus == null || feeStatus.equals(FeeStatus.ACTIVE.getValue())) {
            return true;
        }
        return false;
    }

    /**
     * Count the number of fee installments due after this periodic fee's last-applied date, up to and
     * including the given date. The fees' installment dates are calculated using the customer's meeting
     * schedule (weekly, monthly) but the periodic fee's recurrence rate (every, every second, etc), and
     * starting with the fee's last applied date.
     */
    public Integer getApplicableDatesCount(final List<DateTime> installmentDates) {

        List<DateTime> applicableDates = new ArrayList<DateTime>();

        if (getLastAppliedDate() != null) {

            for (DateTime installmentDate : installmentDates) {
                if (DateUtils.getDateWithoutTimeStamp(installmentDate.toDate().getTime()).compareTo(
                        DateUtils.getDateWithoutTimeStamp(getLastAppliedDate().getTime())) > 0) {
                    applicableDates.add(installmentDate);
                }
            }
        }

        return applicableDates.size();
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

    public static boolean isPeriodicFeeDuplicated(List<AccountFeesEntity> accountFees, FeeBO fee) {

        int feeOccurrenceCount = 0;

        if (fee.isPeriodic()) {
            for (AccountFeesEntity accountFee : accountFees) {
                if (accountFee.getFees().getFeeName().equals(fee.getFeeName())) {
                    feeOccurrenceCount++;
                }
            }
        }

        return feeOccurrenceCount > 1;
    }
}