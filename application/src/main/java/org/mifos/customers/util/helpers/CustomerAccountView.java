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

package org.mifos.customers.util.helpers;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.collectionsheet.business.CollectionSheetEntryCustomerAccountInstallmentView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryInstallmentView;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.business.View;
import org.mifos.framework.util.helpers.Money;

public class CustomerAccountView extends View {

    private Integer accountId;

    private String customerAccountAmountEntered;

    private List<CollectionSheetEntryInstallmentView> accountActionDates;

    private boolean isValidCustomerAccountAmountEntered;

    private final Integer customerId;

    private final MifosCurrency currency;

    public CustomerAccountView(Integer accountId, MifosCurrency currency) {
        this.accountId = accountId;
        this.customerId = null;
        customerAccountAmountEntered = "0.0";
        accountActionDates = new ArrayList<CollectionSheetEntryInstallmentView>();
        isValidCustomerAccountAmountEntered = true;
        this.currency = currency;
    }

    public CustomerAccountView(final Integer accountId, final Integer customerId, MifosCurrency currency) {
        this.accountId = accountId;
        this.customerId = customerId;
        customerAccountAmountEntered = "0.0";
        accountActionDates = new ArrayList<CollectionSheetEntryInstallmentView>();
        isValidCustomerAccountAmountEntered = true;
        this.currency = currency;
    }

    public List<CollectionSheetEntryInstallmentView> getAccountActionDates() {
        return accountActionDates;
    }

    public void setAccountActionDates(List<CollectionSheetEntryInstallmentView> accountActionDates) {
        this.accountActionDates = accountActionDates;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getCustomerAccountAmountEntered() {
        return customerAccountAmountEntered;
    }

    public void setCustomerAccountAmountEntered(String customerAccountAmountEntered) {
        this.customerAccountAmountEntered = customerAccountAmountEntered;
    }

    public boolean isValidCustomerAccountAmountEntered() {
        return isValidCustomerAccountAmountEntered;
    }

    public void setValidCustomerAccountAmountEntered(boolean isValidCustomerAccountAmountEntered) {
        this.isValidCustomerAccountAmountEntered = isValidCustomerAccountAmountEntered;
    }

    public Money getTotalAmountDue() {
        Money totalAmount = new Money(currency);
        if (accountActionDates != null && accountActionDates.size() > 0) {
            for (CollectionSheetEntryInstallmentView accountAction : accountActionDates) {
                totalAmount = totalAmount.add(((CollectionSheetEntryCustomerAccountInstallmentView) accountAction)
                        .getTotalDueWithFees());
            }
        }
        return totalAmount;
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

}
