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

package org.mifos.accounts.util.helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryAccountFeeActionView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryCustomerAccountInstallmentView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryInstallmentView;
import org.mifos.customers.business.CustomerScheduleEntity;
import org.mifos.framework.util.helpers.Money;

/**
 * @deprecated This class shouldnt be needed. It should be possible to make
 *             payments using a monetary amount. The model then should have the
 *             capability of paying off all associated fees and penalties where
 *             possible as determined by the amount paid.
 */
@Deprecated
public class CustomerAccountPaymentData extends AccountPaymentData {

    private Money miscFeePaid;

    private Money miscPenaltyPaid;

    private Map<Short, Money> feesPaid;

    public Map<Short, Money> getFeesPaid() {
        return feesPaid;
    }

    private void setFeesPaid(final Map<Short, Money> feesPaid) {
        this.feesPaid = feesPaid;
    }

    public Money getMiscFeePaid() {
        return miscFeePaid;
    }

    private void setMiscFeePaid(final Money miscFeePaid) {
        this.miscFeePaid = miscFeePaid;
    }

    public Money getMiscPenaltyPaid() {
        return miscPenaltyPaid;
    }

    private void setMiscPenaltyPaid(final Money miscPenaltyPaid) {
        this.miscPenaltyPaid = miscPenaltyPaid;
    }

    public CustomerAccountPaymentData(final AccountActionDateEntity accountAction) {
        super(accountAction);
        CustomerScheduleEntity customerSchedule = (CustomerScheduleEntity) accountAction;
        Map<Short, Money> feesPaid = new HashMap<Short, Money>();
        setMiscFeePaid(customerSchedule.getMiscFee());
        setMiscPenaltyPaid(customerSchedule.getMiscPenalty());
        for (AccountFeesActionDetailEntity accountFees : customerSchedule.getAccountFeesActionDetails()) {
            feesPaid.put(accountFees.getFee().getFeeId(), accountFees.getFeeAmount());
        }
        setFeesPaid(feesPaid);
        setPaymentStatus(PaymentStatus.PAID.getValue());
    }

    public CustomerAccountPaymentData(final CollectionSheetEntryInstallmentView bulkEntryAccountAction) {
        super(bulkEntryAccountAction);
        CollectionSheetEntryCustomerAccountInstallmentView installmentView = (CollectionSheetEntryCustomerAccountInstallmentView) bulkEntryAccountAction;
        Map<Short, Money> feesPaid = new HashMap<Short, Money>();
        setMiscFeePaid(installmentView.getMiscFee());
        setMiscPenaltyPaid(installmentView.getMiscPenalty());
        List<CollectionSheetEntryAccountFeeActionView> collectionSheetEntryAccountFeeActionViews = installmentView
                .getCollectionSheetEntryAccountFeeActions();
        if (collectionSheetEntryAccountFeeActionViews != null && collectionSheetEntryAccountFeeActionViews.size() > 0) {
            for (CollectionSheetEntryAccountFeeActionView accountFeesActionDetailEntity : collectionSheetEntryAccountFeeActionViews) {

                if (accountFeesActionDetailEntity.getFeeAmount() != null
                        && accountFeesActionDetailEntity.getFeeAmount().isNonZero()) {
                    feesPaid.put(accountFeesActionDetailEntity.getFeeId(), accountFeesActionDetailEntity
                            .getFeeAmount());
                }
            }
        }
        setFeesPaid(feesPaid);
    }

    public Money getTotalPaidAmnt() {
        return getMiscFeePaid().add(getMiscPenaltyPaid());
    }
}
