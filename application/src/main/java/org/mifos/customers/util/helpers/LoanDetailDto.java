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
package org.mifos.customers.util.helpers;

import org.mifos.framework.business.service.DataTransferObject;
import org.mifos.framework.util.helpers.Money;

/**
 *
 */
public class LoanDetailDto implements DataTransferObject {
    private final String globalAccountNum;
    private final String prdOfferingName;
    private final Short accountStateId;
    private final String accountStateName;
    private final Money outstandingBalance;
    private final Money totalAmountDue;

    public LoanDetailDto(final String globalAccountNum, final String prdOfferingName, final Short accountStateId,
            final String accountStateName, final Money outstandingBalance, final Money totalAmountDue) {
        this.globalAccountNum = globalAccountNum;
        this.prdOfferingName = prdOfferingName;
        this.accountStateId = accountStateId;
        this.accountStateName = accountStateName;
        this.outstandingBalance = outstandingBalance;
        this.totalAmountDue = totalAmountDue;
    }

    public String getGlobalAccountNum() {
        return this.globalAccountNum;
    }

    public String getPrdOfferingName() {
        return this.prdOfferingName;
    }

    public Short getAccountStateId() {
        return this.accountStateId;
    }

    public String getAccountStateName() {
        return this.accountStateName;
    }

    public Money getOutstandingBalance() {
        return this.outstandingBalance;
    }

    public Money getTotalAmountDue() {
        return this.totalAmountDue;
    }

}
