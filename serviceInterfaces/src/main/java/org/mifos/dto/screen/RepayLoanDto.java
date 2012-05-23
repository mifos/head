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

import org.mifos.application.servicefacade.ListItem;

public class RepayLoanDto {

    private final String earlyRepaymentMoney;
    private final String waivedRepaymentMoney;
    private final boolean waiverInterest;
    private final List<ListItem<String>> savingsAccountsForTransfer;

    public RepayLoanDto(String earlyRepaymentMoney, String waivedRepaymentMoney, boolean waiverInterest, List<ListItem<String>> savingsAccountsForTransfer) {
        this.earlyRepaymentMoney = earlyRepaymentMoney;
        this.waivedRepaymentMoney = waivedRepaymentMoney;
        this.waiverInterest = waiverInterest;
        this.savingsAccountsForTransfer = savingsAccountsForTransfer;
    }

    public String getEarlyRepaymentMoney() {
        return earlyRepaymentMoney;
    }

    public String getWaivedRepaymentMoney() {
        return waivedRepaymentMoney;
    }

    public boolean shouldWaiverInterest() {
        return waiverInterest;
    }

    public List<ListItem<String>> getSavingsAccountsForTransfer() {
        return savingsAccountsForTransfer;
    }
}
