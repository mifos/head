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

import java.util.List;

import org.mifos.accounts.loan.util.helpers.LoanAccountsProductDto;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryDto;
import org.mifos.customers.util.helpers.CustomerAccountDto;
import org.mifos.framework.business.service.DataTransferObject;

/**
 *
 */
public class CollectionSheetEntryDecomposedDto implements DataTransferObject  {

    private final List<LoanAccountsProductDto> loanAccountViews;
    private final List<CustomerAccountDto> customerAccountDtos;
    private final List<CollectionSheetEntryDto> parentCollectionSheetEntryViews;

    public CollectionSheetEntryDecomposedDto(List<LoanAccountsProductDto> loanAccountViews,
            List<CustomerAccountDto> customerAccountDtos,
            List<CollectionSheetEntryDto> parentCollectionSheetEntryViews) {
                this.loanAccountViews = loanAccountViews;
        this.customerAccountDtos = customerAccountDtos;
        this.parentCollectionSheetEntryViews = parentCollectionSheetEntryViews;
    }

    public List<LoanAccountsProductDto> getLoanAccountViews() {
        return this.loanAccountViews;
    }

    public List<CustomerAccountDto> getCustomerAccountViews() {
        return this.customerAccountDtos;
    }

    public List<CollectionSheetEntryDto> getParentCollectionSheetEntryViews() {
        return this.parentCollectionSheetEntryViews;
    }
}
