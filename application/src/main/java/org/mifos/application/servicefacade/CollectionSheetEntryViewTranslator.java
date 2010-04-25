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

import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.loan.util.helpers.LoanAccountsProductDto;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryDto;
import org.mifos.customers.util.helpers.CustomerAccountView;
import org.mifos.customers.util.helpers.CustomerLevel;

/**
 *
 */
public class CollectionSheetEntryViewTranslator {

    public CollectionSheetEntryDecomposedView toDecomposedView(final CollectionSheetEntryDto collectionSheetEntryDto) {

        final List<LoanAccountsProductDto> loanAccountViews = new ArrayList<LoanAccountsProductDto>();
        final List<CustomerAccountView> customerAccountViews = new ArrayList<CustomerAccountView>();
        final List<CollectionSheetEntryDto> parentCollectionSheetEntryViews = new ArrayList<CollectionSheetEntryDto>();

        recursivelyCreateListOfViews(collectionSheetEntryDto, loanAccountViews, customerAccountViews,
                parentCollectionSheetEntryViews);

        return new CollectionSheetEntryDecomposedView(loanAccountViews, customerAccountViews,
                parentCollectionSheetEntryViews);
    }

    private void recursivelyCreateListOfViews(CollectionSheetEntryDto parent,
            List<LoanAccountsProductDto> loanAccountViewsForCenter,
            List<CustomerAccountView> customerAccountViews, List<CollectionSheetEntryDto> parentCollectionSheetEntryViews) {

        final List<CollectionSheetEntryDto> children = parent.getCollectionSheetEntryChildren();
        final Short levelId = parent.getCustomerDetail().getCustomerLevelId();

        if (null != children) {
            for (CollectionSheetEntryDto child : children) {
                recursivelyCreateListOfViews(child, loanAccountViewsForCenter, customerAccountViews, parentCollectionSheetEntryViews);
            }
        }

        parentCollectionSheetEntryViews.add(parent);
        if (!levelId.equals(CustomerLevel.CENTER.getValue())) {
            loanAccountViewsForCenter.addAll(parent.getLoanAccountDetails());
        }
        customerAccountViews.add(parent.getCustomerAccountDetails());
    }

}
