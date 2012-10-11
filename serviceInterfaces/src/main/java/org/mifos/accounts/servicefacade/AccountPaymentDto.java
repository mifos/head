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

package org.mifos.accounts.servicefacade;

import java.util.Date;
import java.util.List;

import org.mifos.application.servicefacade.ListItem;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.SavingsDetailDto;
import org.mifos.dto.domain.UserReferenceDto;


/**
 * Holds information required for making an account payment.
 *
 */
public class AccountPaymentDto {
    private final AccountTypeDto accountType;
    private final int version;
    private final List<ListItem<Short>> paymentTypeList;
    private final String totalPaymentDue;
    private final UserReferenceDto userMakingPayment;
    private final Date lastPaymentDate;
    private final List<SavingsDetailDto> savingsAccountsFroTransfer;
    private final CustomerDto customerDto;

    public AccountPaymentDto(AccountTypeDto accountType, int version, List<ListItem<Short>> paymentTypeList,
            String totalPaymentDue, UserReferenceDto userMakingPayment, Date lastPaymentDate,
            List<SavingsDetailDto> savingsAccountsForTransfer, CustomerDto customerDto) {
        this.accountType = accountType;
        this.version = version;
        this.paymentTypeList = paymentTypeList;
        this.totalPaymentDue = totalPaymentDue;
        this.userMakingPayment = userMakingPayment;
        this.lastPaymentDate = new Date(lastPaymentDate.getTime());
        this.savingsAccountsFroTransfer = savingsAccountsForTransfer;
        this.customerDto = customerDto;
    }

    public AccountTypeDto getAccountType() {
        return this.accountType;
    }

    public int getVersion() {
        return this.version;
    }

    public List<ListItem<Short>> getPaymentTypeList() {
        return this.paymentTypeList;
    }

    public String getTotalPaymentDue() {
        return this.totalPaymentDue;
    }

    public UserReferenceDto getUserMakingPayment() {
        return this.userMakingPayment;
    }

    public Date getLastPaymentDate() {
        return new Date(lastPaymentDate.getTime());
    }

    public List<SavingsDetailDto> getSavingsAccountsFroTransfer() {
        return savingsAccountsFroTransfer;
    }

    public CustomerDto getCustomerDto() {
        return customerDto;
    }
}