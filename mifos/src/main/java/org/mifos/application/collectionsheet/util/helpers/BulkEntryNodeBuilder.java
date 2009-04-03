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

import org.mifos.application.collectionsheet.business.CollectionSheetEntryAccountFeeActionView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryInstallmentView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryView;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.client.business.service.ClientAttendanceDto;

public class BulkEntryNodeBuilder {

    public static CollectionSheetEntryView buildBulkEntry(List<CustomerBO> allCustomers,
            CustomerView parentCustomerView, Date transactionDate,
            List<CollectionSheetEntryInstallmentView> bulkEntryLoanViews,
            List<CollectionSheetEntryInstallmentView> bulkEntryCustomerViews,
            List<CollectionSheetEntryAccountFeeActionView> bulkEntryLoanFeeViews,
            List<CollectionSheetEntryAccountFeeActionView> bulkEntryCustomerFeeViews,
            List<ClientAttendanceDto> collectionSheetEntryClientAttendanceViews) {
        CustomerBO parentCustomer = getCustomer(parentCustomerView.getCustomerId(), allCustomers);
        return buildBulkEntry(allCustomers, parentCustomer, parentCustomerView, transactionDate, bulkEntryLoanViews,
                bulkEntryCustomerViews, bulkEntryLoanFeeViews, bulkEntryCustomerFeeViews,
                collectionSheetEntryClientAttendanceViews);
    }

    public static void buildBulkEntrySavingsAccounts(CollectionSheetEntryView parentNode, Date transactionDate,
            List<CollectionSheetEntryInstallmentView> bulkEntryAccountActionViews) {
        List<CollectionSheetEntryView> immediateChildren = parentNode.getCollectionSheetEntryChildren();
        if (immediateChildren != null && immediateChildren.size() != 0) {
            for (CollectionSheetEntryView childCustomer : immediateChildren) {
                buildBulkEntrySavingsAccounts(childCustomer, transactionDate, bulkEntryAccountActionViews);
            }
        }
        parentNode.populateSavingsAccountActions(parentNode.getCustomerDetail().getCustomerId(), transactionDate,
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
        parentNode.populateClientAttendance(parentNode.getCustomerDetail().getCustomerId(), transactionDate,
                collectionSheetEntryClientAttendanceViews);

    }

    private static CollectionSheetEntryView buildBulkEntry(List<CustomerBO> allCustomers, CustomerBO parentCustomer,
            CustomerView parentCustomerView, Date transactionDate,
            List<CollectionSheetEntryInstallmentView> bulkEntryLoanViews,
            List<CollectionSheetEntryInstallmentView> bulkEntryCustomerViews,
            List<CollectionSheetEntryAccountFeeActionView> bulkEntryLoanFeeViews,
            List<CollectionSheetEntryAccountFeeActionView> bulkEntryCustomerFeeViews,
            List<ClientAttendanceDto> clientAttendance) {
        CollectionSheetEntryView parentNode = new CollectionSheetEntryView(parentCustomerView);
        List<CustomerBO> immediateChildren = getImmediateCustomers(parentNode.getCustomerDetail().getCustomerId(),
                allCustomers);
        if (immediateChildren != null && immediateChildren.size() != 0) {
            for (CustomerBO childCustomer : immediateChildren) {
                CustomerView customerView = getCustomerView(childCustomer);
                parentNode.addChildNode(buildBulkEntry(allCustomers, childCustomer, customerView, transactionDate,
                        bulkEntryLoanViews, bulkEntryCustomerViews, bulkEntryLoanFeeViews, bulkEntryCustomerFeeViews,
                        clientAttendance));
            }
        }
        parentNode.populateLoanAccountsInformation(parentCustomer, transactionDate, bulkEntryLoanViews,
                bulkEntryLoanFeeViews);
        parentNode.populateSavingsAccountsInformation(parentCustomer);
        parentNode
                .populateCustomerAccountInformation(parentCustomer, bulkEntryCustomerViews, bulkEntryCustomerFeeViews);
        return parentNode;
    }

    private static CustomerBO getCustomer(Integer customerId, List<CustomerBO> customers) {
        for (CustomerBO customer : customers) {
            if (customerId.equals(customer.getCustomerId()))
                return customer;
        }
        return null;
    }

    private static List<CustomerBO> getImmediateCustomers(Integer parentCustomerId, List<CustomerBO> allCustomers) {
        List<CustomerBO> immediateChildren = new ArrayList<CustomerBO>();
        for (CustomerBO child : allCustomers) {
            if (child.getParentCustomer() != null && parentCustomerId.equals(child.getParentCustomer().getCustomerId()))
                immediateChildren.add(child);
        }
        return immediateChildren;
    }

    private static CustomerView getCustomerView(CustomerBO customer) {
        return new CustomerView(customer.getCustomerId(), customer.getDisplayName(), customer.getParentCustomer()
                .getCustomerId(), customer.getCustomerLevel().getId());
    }

}
