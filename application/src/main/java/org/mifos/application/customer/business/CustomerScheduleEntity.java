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

package org.mifos.application.customer.business;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.CustomerAccountPaymentData;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.framework.util.helpers.Money;

public class CustomerScheduleEntity extends AccountActionDateEntity {

    private Money miscFee;

    private Money miscFeePaid;

    private Money miscPenalty;

    private Money miscPenaltyPaid;

    private Set<AccountFeesActionDetailEntity> accountFeesActionDetails;

    protected CustomerScheduleEntity() {
        super(null, null, null, null, null);
    }

    public CustomerScheduleEntity(AccountBO account, CustomerBO customer, Short installmentId, Date actionDate,
            PaymentStatus paymentStatus) {
        super(account, customer, installmentId, actionDate, paymentStatus);
        accountFeesActionDetails = new HashSet<AccountFeesActionDetailEntity>();
        miscFee = new Money(account.getCurrency());
        miscFeePaid = new Money(account.getCurrency());
        miscPenalty = new Money(account.getCurrency());
        miscPenaltyPaid = new Money(account.getCurrency());
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

    void setMiscPenaltyPaid(Money miscPenaltyPaid) {
        this.miscPenaltyPaid = miscPenaltyPaid;
    }

    public Money getMiscFeeDue() {
        return getMiscFee().subtract(getMiscFeePaid());
    }

    public Money getMiscPenaltyDue() {
        return getMiscPenalty().subtract(getMiscPenaltyPaid());
    }

    public Money getTotalFeeDue() {
        Money totalFees = new Money(getAccount().getCurrency());
        for (AccountFeesActionDetailEntity obj : accountFeesActionDetails) {
            totalFees = totalFees.add(obj.getFeeDue());
        }
        return totalFees;
    }

    public Money getTotalFeeDueWithMiscFee() {
        Money totalFees = new Money(getAccount().getCurrency());
        totalFees = totalFees.add(getTotalFeeDue()).add(getMiscFeeDue());
        return totalFees;
    }

    public Money getTotalFees() {
        return getMiscFee().add(getTotalFeeDue());
    }

    public Money getTotalDueWithFees() {
        return getMiscPenaltyDue().add(getTotalFeeDueWithMiscFee());
    }

    void applyPeriodicFees(Short feeId, Money totalAmount) {
        AccountFeesEntity accountFeesEntity = account.getAccountFees(feeId);
        AccountFeesActionDetailEntity accountFeesActionDetailEntity = new CustomerFeeScheduleEntity(this,
                accountFeesEntity.getFees(), accountFeesEntity, totalAmount);
        addAccountFeesAction(accountFeesActionDetailEntity);
    }

    void setPaymentDetails(CustomerAccountPaymentData customerAccountPaymentData, Date paymentDate) {
        this.miscFeePaid = customerAccountPaymentData.getMiscFeePaid();
        this.miscPenaltyPaid = customerAccountPaymentData.getMiscPenaltyPaid();
        this.paymentStatus = customerAccountPaymentData.getPaymentStatus();
        this.paymentDate = paymentDate;
    }

    Money waiveCharges() {
        Money chargeWaived = new Money(getAccount().getCurrency());
        chargeWaived = chargeWaived.add(getMiscFee()).add(getMiscPenalty());
        setMiscFee(new Money(getAccount().getCurrency()));
        setMiscPenalty(new Money(getAccount().getCurrency()));
        for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : getAccountFeesActionDetails()) {
            chargeWaived = chargeWaived.add(((CustomerFeeScheduleEntity) accountFeesActionDetailEntity).waiveCharges());
        }
        return chargeWaived;
    }

    Money waiveFeeCharges() {
        Money chargeWaived = new Money(getAccount().getCurrency());
        chargeWaived = chargeWaived.add(getMiscFee());
        setMiscFee(new Money(getAccount().getCurrency()));
        for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : getAccountFeesActionDetails()) {
            chargeWaived = chargeWaived.add(((CustomerFeeScheduleEntity) accountFeesActionDetailEntity).waiveCharges());
        }
        return chargeWaived;
    }

    void removeAccountFeesActionDetailEntity(AccountFeesActionDetailEntity accountFeesActionDetailEntity) {
        accountFeesActionDetails.remove(accountFeesActionDetailEntity);
    }

    public AccountFeesActionDetailEntity getAccountFeesAction(Integer accountFeeId) {
        for (AccountFeesActionDetailEntity accountFeesAction : getAccountFeesActionDetails()) {
            if (accountFeesAction.getAccountFee().getAccountFeeId().equals(accountFeeId)) {
                return accountFeesAction;
            }
        }
        return null;
    }

    Money removeFees(Short feeId) {
        Money feeAmount = null;
        AccountFeesActionDetailEntity objectToRemove = null;
        Set<AccountFeesActionDetailEntity> accountFeesActionDetailSet = this.getAccountFeesActionDetails();
        for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountFeesActionDetailSet) {
            if (accountFeesActionDetailEntity.getFee().getFeeId().equals(feeId)) {
                objectToRemove = accountFeesActionDetailEntity;
                break;
            }
        }
        if (objectToRemove != null) {
            feeAmount = objectToRemove.getFeeAmount();
            this.removeAccountFeesActionDetailEntity(objectToRemove);
        }

        return feeAmount;
    }

    void applyMiscCharge(Short chargeType, Money charge) {
        if (chargeType.equals(Short.valueOf(AccountConstants.MISC_FEES)))
            setMiscFee(getMiscFee().add(charge));
        else if (chargeType.equals(Short.valueOf(AccountConstants.MISC_PENALTY)))
            setMiscPenalty(getMiscPenalty().add(charge));
    }

}
