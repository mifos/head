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
package org.mifos.application.servicefacade;

import java.util.List;

import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.customer.client.business.ClientBO;

/**
 *
 */
public class ErrorAndCollectionSheetDataDto {

    private final CollectionSheetEntryDecomposedView decomposedViews;
    private final CollectionSheetErrorsView errors;
    private final List<SavingsBO> savingsAccounts;
    private final List<ClientBO> clients;

    public ErrorAndCollectionSheetDataDto(CollectionSheetEntryDecomposedView decomposedViews,
            CollectionSheetErrorsView errors, List<SavingsBO> savingsAccounts, List<ClientBO> clients) {
                this.decomposedViews = decomposedViews;
        this.errors = errors;
        this.savingsAccounts = savingsAccounts;
        this.clients = clients;
    }

    public CollectionSheetEntryDecomposedView getDecomposedViews() {
        return this.decomposedViews;
    }

    public CollectionSheetErrorsView getErrors() {
        return this.errors;
    }

    public List<SavingsBO> getSavingsAccounts() {
        return this.savingsAccounts;
    }

    public List<ClientBO> getClients() {
        return this.clients;
    }
}
