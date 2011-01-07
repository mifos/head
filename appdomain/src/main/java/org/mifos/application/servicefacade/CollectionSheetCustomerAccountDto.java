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
package org.mifos.application.servicefacade;

public class CollectionSheetCustomerAccountDto {

    private final Integer accountId;
    private final Short currencyId;
    private final Double totalCustomerAccountCollectionFee;

    public CollectionSheetCustomerAccountDto(final Integer accountId, final Short currencyId,
            final Double totalCustomerAccountCollectionFee) {
        this.accountId = accountId;
        this.currencyId = currencyId;
        this.totalCustomerAccountCollectionFee = totalCustomerAccountCollectionFee;
    }

    public Integer getAccountId() {
        return this.accountId;
    }

    public Double getTotalCustomerAccountCollectionFee() {
        return this.totalCustomerAccountCollectionFee;
    }

    public Short getCurrencyId() {
        return this.currencyId;
    }

}
