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

package org.mifos.application.collectionsheet.util.helpers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.mifos.application.accounts.loan.util.helpers.LoanAccountView;
import org.mifos.application.accounts.savings.util.helpers.SavingsAccountView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryAccountFeeActionView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryInstallmentView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryView;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.client.business.service.ClientAttendanceDto;
import org.mifos.application.customer.util.helpers.CustomerAccountView;

public class BulkEntryNodeBuilder {

    public static CollectionSheetEntryView buildBulkEntry(List<CustomerView> allCustomers,
            CustomerView parentCustomerView, Date transactionDate,
            List<CollectionSheetEntryInstallmentView> bulkEntryLoanViews,
            List<CollectionSheetEntryInstallmentView> bulkEntryCustomerViews,
            List<CollectionSheetEntryAccountFeeActionView> bulkEntryLoanFeeViews,
            List<CollectionSheetEntryAccountFeeActionView> bulkEntryCustomerFeeViews,
            List<ClientAttendanceDto> collectionSheetEntryClientAttendanceViews,
            List<CustomerAccountView> customerAccountList, List<LoanAccountView> loanAccountViewList,
            List<SavingsAccountView> savingsAccountViewList) {
        
        final CustomerView topCustomerOfHierarchy = allCustomers.get(0);
        
        return buildBulkEntryNode(allCustomers, topCustomerOfHierarchy, parentCustomerView, transactionDate,
                bulkEntryLoanViews,
                bulkEntryCustomerViews, bulkEntryLoanFeeViews, bulkEntryCustomerFeeViews,
                collectionSheetEntryClientAttendanceViews, customerAccountList, loanAccountViewList,
                savingsAccountViewList);
    }

    public static void buildBulkEntrySavingsAccounts(CollectionSheetEntryView parentNode, Date transactionDate,
            List<CollectionSheetEntryInstallmentView> bulkEntryAccountActionViews) {
        List<CollectionSheetEntryView> immediateChildren = parentNode.getCollectionSheetEntryChildren();
        if (immediateChildren != null && immediateChildren.size() != 0) {
            for (CollectionSheetEntryView childCustomer : immediateChildren) {
                buildBulkEntrySavingsAccounts(childCustomer, transactionDate, bulkEntryAccountActionViews);
            }
        }
        parentNode.populateSavingsAccountActions(parentNode.getCustomerDetail().getCustomerId(),
                bulkEntryAccountActionViews);

    }

    public static void buildBulkEntryClientAttendance(CollectionSheetEntryView parentNode, Date transactionDate,
            List<ClientAttendanceDto> collectionSheetEntryClientAttendanceViews) {
        List<CollectionSheetEntryView> immediateChildren = parentNode.getCollectionSheetEntryChildren();
        if (immediateChildren != null && immediateChildren.size() != 0) {
            for (CollectionSheetEntryView childCustomer : immediateChildren) {
                buildBulkEntryClientAttendance(childCustomer, transactionDate,
                        collectionSheetEntryClientAttendanceViews);

            }
        }
        parentNode.populateClientAttendance(parentNode.getCustomerDetail().getCustomerId(),
                collectionSheetEntryClientAttendanceViews);

    }

    private static CollectionSheetEntryView buildBulkEntryNode(List<CustomerView> allCustomers,
            CustomerView parentCustomer, CustomerView parentCustomerView, Date transactionDate,
            List<CollectionSheetEntryInstallmentView> bulkEntryLoanViews,
            List<CollectionSheetEntryInstallmentView> bulkEntryCustomerViews,
            List<CollectionSheetEntryAccountFeeActionView> bulkEntryLoanFeeViews,
            List<CollectionSheetEntryAccountFeeActionView> bulkEntryCustomerFeeViews,
            List<ClientAttendanceDto> clientAttendance, List<CustomerAccountView> customerAccountList,
            List<LoanAccountView> loanAccountViewList, List<SavingsAccountView> savingsAccountViewList) {

        CollectionSheetEntryView parentNode = new CollectionSheetEntryView(parentCustomerView);
        List<CustomerView> immediateChildren = getImmediateCustomers(parentNode.getCustomerDetail().getCustomerId(),
                allCustomers);

        if (immediateChildren != null && immediateChildren.size() != 0) {
            for (CustomerView customerView : immediateChildren) {
                parentNode.addChildNode(buildBulkEntryNode(allCustomers, customerView, customerView, transactionDate,
                        bulkEntryLoanViews, bulkEntryCustomerViews, bulkEntryLoanFeeViews, bulkEntryCustomerFeeViews,
                        clientAttendance, customerAccountList, loanAccountViewList, savingsAccountViewList));
            }
        }

        parentNode.populateLoanAccountsInformation(loanAccountViewList, bulkEntryLoanViews, bulkEntryLoanFeeViews);
        parentNode.populateSavingsAccountsInformation(savingsAccountViewList);

        parentNode.populateCustomerAccountInformation(customerAccountList, bulkEntryCustomerViews,
                bulkEntryCustomerFeeViews);
        return parentNode;
    }

    private static List<CustomerView> getImmediateCustomers(Integer parentCustomerId, List<CustomerView> allCustomers) {
        List<CustomerView> immediateChildren = new ArrayList<CustomerView>();
        for (CustomerView child : allCustomers) {
            if (child.getParentCustomerId() != null && parentCustomerId.equals(child.getParentCustomerId())) {
                immediateChildren.add(child);
            }
        }
        return immediateChildren;
    }
}
