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

package org.mifos.dto.screen;

import java.util.List;

import org.joda.time.LocalDate;

public class DepositWithdrawalReferenceDto {

    private final List<ListElement> transactionTypes;
    private final List<ListElement> depositPaymentTypes;
    private final List<ListElement> withdrawalPaymentTypes;
    private final List<ListElement> clients;
    private final boolean backDatedTransactionsAllowed;
    private final LocalDate transactionDate;
    private final String depositDue;
    private final String withdrawalDue;

    public DepositWithdrawalReferenceDto(List<ListElement> transactionTypes, List<ListElement> depositPaymentTypes, List<ListElement> withdrawalPaymentTypes, List<ListElement> clients, boolean backDatedTransactionsAllowed, LocalDate transactionDate, String depositDue, String withdrawalDue) {
        this.transactionTypes = transactionTypes;
        this.depositPaymentTypes = depositPaymentTypes;
        this.withdrawalPaymentTypes = withdrawalPaymentTypes;
        this.clients = clients;
        this.backDatedTransactionsAllowed = backDatedTransactionsAllowed;
        this.transactionDate = transactionDate;
        this.depositDue = depositDue;
        this.withdrawalDue = withdrawalDue;
    }

    public boolean isBackDatedTransactionsAllowed() {
        return this.backDatedTransactionsAllowed;
    }

    public LocalDate getTransactionDate() {
        return this.transactionDate;
    }

    public List<ListElement> getTransactionTypes() {
        return this.transactionTypes;
    }

    public List<ListElement> getClients() {
        return this.clients;
    }

    public List<ListElement> getDepositPaymentTypes() {
        return this.depositPaymentTypes;
    }

    public List<ListElement> getWithdrawalPaymentTypes() {
        return this.withdrawalPaymentTypes;
    }

    public String getDepositDue() {
        return this.depositDue;
    }

    public String getWithdrawalDue() {
        return this.withdrawalDue;
    }
}