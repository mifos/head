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

package org.mifos.dto.domain;

import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.dto.screen.ListElement;

public class SavingsAccountClosureDto {

    private final LocalDate closureDate;
    private final String balance;
    private final String interestAmountAtClosure;
    private final List<ListElement> depositPaymentTypes;

    public SavingsAccountClosureDto(LocalDate closureDate, String balance, String interestAmountAtClosure, List<ListElement> depositPaymentTypes) {
        this.closureDate = closureDate;
        this.balance = balance;
        this.interestAmountAtClosure = interestAmountAtClosure;
        this.depositPaymentTypes = depositPaymentTypes;
    }

    public String getBalance() {
        return this.balance;
    }

    public LocalDate getClosureDate() {
        return this.closureDate;
    }

    public String getInterestAmountAtClosure() {
        return this.interestAmountAtClosure;
    }

    public List<ListElement> getDepositPaymentTypes() {
        return this.depositPaymentTypes;
    }
}