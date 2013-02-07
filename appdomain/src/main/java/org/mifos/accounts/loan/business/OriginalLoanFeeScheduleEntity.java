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
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.framework.util.helpers.Money;

@Entity
@Table(name = "original_loan_fee_schedule")
public class OriginalLoanFeeScheduleEntity implements Comparable<OriginalLoanFeeScheduleEntity>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "account_fees_detail_id")
    private Integer accountFeesActionDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private OriginalLoanScheduleEntity accountActionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_id")
    private FeeBO fee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_fee_id")
    private AccountFeesEntity accountFee;

    @Column(name = "installment_id")
    private Short installmentId;

    @Type(type = "org.mifos.framework.util.helpers.MoneyCompositeUserType")
    @Columns(columns = {
            @Column(name = "amount_currency_id"),
            @Column(name = "amount")
    })
    private Money feeAmount;

    @Type(type = "org.mifos.framework.util.helpers.MoneyCompositeUserType")
    @Columns(columns = {
            @Column(name = "amount_paid_currency_id"),
            @Column(name = "amount_paid")
    })
    private Money feeAmountPaid;

    @Column(name = "version_no")
    private int versionNo;

    public OriginalLoanFeeScheduleEntity() {
    }

    public OriginalLoanFeeScheduleEntity(AccountFeesActionDetailEntity accountFeesActionDetail, OriginalLoanScheduleEntity originalLoanScheduleEntity) {
        this.accountActionDate = originalLoanScheduleEntity;
        if (accountActionDate != null) {
            this.installmentId = accountActionDate.getInstallmentId();
            this.feeAmountPaid = new Money(accountActionDate.getAccount().getCurrency());
        } else {
            this.installmentId = null;
        }
        this.fee = accountFeesActionDetail.getFee();
        this.accountFee = accountFeesActionDetail.getAccountFee();
        this.feeAmount = accountFeesActionDetail.getFeeAmount();
        this.versionNo = ((LoanFeeScheduleEntity) accountFeesActionDetail).getVersionNo();
    }

    protected void setFeeAmount(Money feeAmount) {
        this.feeAmount = feeAmount;
    }
    
    public void updateFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = new Money(accountFee.getAccount().getCurrency(), feeAmount);
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

    @Override
	public int compareTo(OriginalLoanFeeScheduleEntity obj) {
        return this.getFee().getFeeId().compareTo(obj.getFee().getFeeId());
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
