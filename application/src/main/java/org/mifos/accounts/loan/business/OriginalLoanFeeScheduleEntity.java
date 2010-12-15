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
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.framework.util.helpers.Money;

import java.io.Serializable;

public class OriginalLoanFeeScheduleEntity implements Comparable<OriginalLoanFeeScheduleEntity>, Serializable {
    private Integer accountFeesActionDetailId;
    private OriginalLoanScheduleEntity accountActionDate;

    private Short installmentId;

    private FeeBO fee;

    private AccountFeesEntity accountFee;

    private Money feeAmount;

    private Money feeAmountPaid;

    private Money feeAllocated;
    private int versionNo;

    public OriginalLoanFeeScheduleEntity() {
    }

    //Introduced to aid testing
    public OriginalLoanFeeScheduleEntity(OriginalLoanScheduleEntity originalLoanScheduleEntity, FeeBO fee, AccountFeesEntity accountFee, Money feeAmount, int versionNo) {
        this.accountActionDate = originalLoanScheduleEntity;
        if (accountActionDate != null) {
            this.installmentId = accountActionDate.getInstallmentId();
            this.feeAmountPaid = new Money(accountActionDate.getAccount().getCurrency());
        }
        else {
            this.installmentId = null;
        }
        this.fee = fee;
        this.accountFee = accountFee;
        this.feeAmount = feeAmount;
        this.versionNo = versionNo;
    }

    public OriginalLoanFeeScheduleEntity(AccountFeesActionDetailEntity accountFeesActionDetail, OriginalLoanScheduleEntity originalLoanScheduleEntity) {
        this.accountActionDate = originalLoanScheduleEntity;
        if (accountActionDate != null) {
            this.installmentId = accountActionDate.getInstallmentId();
            this.feeAmountPaid = new Money(accountActionDate.getAccount().getCurrency());
        }
        else {
            this.installmentId = null;
        }
        this.fee = accountFeesActionDetail.getFee();
        this.accountFee = accountFeesActionDetail.getAccountFee();
        this.feeAmount = accountFeesActionDetail.getFeeAmount();
        this.versionNo = ((LoanFeeScheduleEntity)accountFeesActionDetail).getVersionNo();
    }

    protected void setFeeAmount(Money feeAmount) {
        this.feeAmount = feeAmount;
    }

    protected void setFeeAmountPaid(Money feeAmountPaid) {
        this.feeAmountPaid = feeAmountPaid;
    }

    public void setVersionNo(int versionNo) {
        this.versionNo = versionNo;
    }

    public int getVersionNo() {
        return versionNo;
    }

    public OriginalLoanScheduleEntity getAccountActionDate() {
        return this.accountActionDate;
    }

    public AccountFeesEntity getAccountFee() {
        return this.accountFee;
    }

    public Integer getAccountFeesActionDetailId() {
        return this.accountFeesActionDetailId;
    }

    public FeeBO getFee() {
        return this.fee;
    }

    public Money getFeeAmount() {
        return this.feeAmount;
    }

    public Money getFeeAmountPaid() {
        return this.feeAmountPaid == null ? new Money(this.accountFee.getAccount().getCurrency()) : this.feeAmountPaid;
    }

    public Short getInstallmentId() {
        return this.installmentId;
    }

    public int compareTo(OriginalLoanFeeScheduleEntity obj) {
        return this.getFee().getFeeId().compareTo(obj.getFee().getFeeId());
    }

    public Money getFeeAllocated() {
        return this.feeAllocated;
    }

    public void setAccountFeesActionDetailId(Integer accountFeesActionDetailId) {
        this.accountFeesActionDetailId = accountFeesActionDetailId;
    }

    public void setInstallmentId(Short installmentId) {
        this.installmentId = installmentId;
    }

    public Integer getAccountFeeId() {
        return this.accountFee.getAccountFeeId();
    }

    public Money getFeeDue() {
        return this.feeAmount.subtract(getFeeAmountPaid());
    }
}
