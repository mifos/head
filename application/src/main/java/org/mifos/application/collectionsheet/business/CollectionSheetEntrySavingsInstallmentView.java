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

import org.mifos.framework.util.helpers.Money;

public class CollectionSheetEntrySavingsInstallmentView extends CollectionSheetEntryInstallmentView {

    private final Money deposit;
    private final Money depositPaid;

    public CollectionSheetEntrySavingsInstallmentView(final Integer accountId, final Integer customerId, final Short installmentId,
            final Integer actionDateId, final Date actionDate, final Money deposit, final Money depositPaid) {
        super(accountId, customerId, installmentId, actionDateId, actionDate);
        this.deposit = deposit;
        this.depositPaid = depositPaid;
    }

    public CollectionSheetEntrySavingsInstallmentView(final Integer accountId, final Integer customerId) {
        super(accountId, customerId, null, null, null);
        this.deposit = null;
        this.depositPaid = null;
    }

    public Money getDeposit() {
        return deposit;
    }

    public Money getDepositPaid() {
        return depositPaid;
    }

    public Money getTotalDepositDue() {
        return getDeposit().subtract(getDepositPaid());
    }

}
