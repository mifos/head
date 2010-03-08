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

package org.mifos.application.collectionsheet.business;

import java.util.Date;
import java.util.List;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.helpers.Money;

public class CollectionSheetEntryCustomerAccountInstallmentView extends CollectionSheetEntryInstallmentView {

    private final Money miscFee;

    private final Money miscFeePaid;

    private final Money miscPenalty;

    private final Money miscPenaltyPaid;

    private List<CollectionSheetEntryAccountFeeActionView> collectionSheetEntryAccountFeeActions;

    private final MifosCurrency currency;

    public CollectionSheetEntryCustomerAccountInstallmentView(Integer accountId, Integer customerId,
            Short installmentId, Integer actionDateId, Date actionDate, Money miscFee, Money miscFeePaid,
            Money miscPenalty, Money miscPenaltyPaid, MifosCurrency currency) {
        super(accountId, customerId, installmentId, actionDateId, actionDate);
        this.miscFee = miscFee;
        this.miscFeePaid = miscFeePaid;
        this.miscPenalty = miscPenalty;
        this.miscPenaltyPaid = miscPenaltyPaid;
        this.currency = currency;
    }

    public CollectionSheetEntryCustomerAccountInstallmentView(Integer accountId, Integer customerId, MifosCurrency currency) {
        super(accountId, customerId, null, null, null);
        this.miscFee = null;
        this.miscFeePaid = null;
        this.miscPenalty = null;
        this.miscPenaltyPaid = null;
        this.currency = currency;
    }

    public Money getMiscFee() {
        return miscFee;
    }

    public Money getMiscFeePaid() {
        return miscFeePaid;
    }

    public Money getMiscPenalty() {
        return miscPenalty;
    }

    public Money getMiscPenaltyPaid() {
        return miscPenaltyPaid;
    }

    public List<CollectionSheetEntryAccountFeeActionView> getCollectionSheetEntryAccountFeeActions() {
        return collectionSheetEntryAccountFeeActions;
    }

    public void setCollectionSheetEntryAccountFeeActions(
            List<CollectionSheetEntryAccountFeeActionView> collectionSheetEntryAccountFeeActions) {
        this.collectionSheetEntryAccountFeeActions = collectionSheetEntryAccountFeeActions;
    }

    public Money getMiscFeeDue() {
        return getMiscFee().subtract(getMiscFeePaid());
    }

    public Money getMiscPenaltyDue() {
        return getMiscPenalty().subtract(getMiscPenaltyPaid());
    }

    public Money getTotalFeeDue() {
        Money totalFees = new Money(currency);
        if (collectionSheetEntryAccountFeeActions != null)
            for (CollectionSheetEntryAccountFeeActionView obj : collectionSheetEntryAccountFeeActions) {
                totalFees = totalFees.add(obj.getFeeDue());
            }

        return totalFees;
    }

    public Money getTotalFees() {
        return getMiscFee().add(getTotalFeeDue());
    }

    public Money getTotalDueWithFees() {
        return getMiscPenaltyDue().add(getTotalFees());
    }

}
