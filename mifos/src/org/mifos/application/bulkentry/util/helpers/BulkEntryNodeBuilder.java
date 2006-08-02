/**

 * BulkEntryNodeBuilder.java    version: 1.0

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied.  

 *

 */

package org.mifos.application.bulkentry.util.helpers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.mifos.application.bulkentry.business.BulkEntryAccountActionView;
import org.mifos.application.bulkentry.business.BulkEntryAccountFeeActionView;
import org.mifos.application.bulkentry.business.BulkEntryView;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerView;

public class BulkEntryNodeBuilder {

	public static BulkEntryView buildBulkEntry(List<CustomerBO> allCustomers,
			CustomerView parentCustomerView, Date transactionDate,
			List<BulkEntryAccountActionView> bulkEntryAccountActionViews,
			List<BulkEntryAccountFeeActionView> bulkEntryAccountFeeActionViews) {
		CustomerBO parentCustomer = getCustomer(parentCustomerView
				.getCustomerId(), allCustomers);
		return buildBulkEntry(allCustomers, parentCustomer, parentCustomerView,
				transactionDate, bulkEntryAccountActionViews,
				bulkEntryAccountFeeActionViews);
	}

	public static void buildBulkEntrySavingsAccounts(BulkEntryView parentNode,
			Date transactionDate,
			List<BulkEntryAccountActionView> bulkEntryAccountActionViews) {
		List<BulkEntryView> immediateChildren = parentNode
				.getBulkEntryChildren();
		if (immediateChildren != null && immediateChildren.size() != 0) {
			for (BulkEntryView childCustomer : immediateChildren) {
				buildBulkEntrySavingsAccounts(childCustomer, transactionDate,
						bulkEntryAccountActionViews);
			}
		}
		parentNode.populateSavingsAccountActions(parentNode.getCustomerDetail()
				.getCustomerId(), transactionDate, bulkEntryAccountActionViews);

	}

	private static BulkEntryView buildBulkEntry(List<CustomerBO> allCustomers,
			CustomerBO parentCustomer, CustomerView parentCustomerView,
			Date transactionDate,
			List<BulkEntryAccountActionView> bulkEntryAccountActionViews,
			List<BulkEntryAccountFeeActionView> bulkEntryAccountFeeActionViews) {
		BulkEntryView parentNode = new BulkEntryView(parentCustomerView);
		List<CustomerBO> immediateChildren = getImmediateCustomers(parentNode
				.getCustomerDetail().getCustomerId(), allCustomers);
		if (immediateChildren != null && immediateChildren.size() != 0) {
			for (CustomerBO childCustomer : immediateChildren) {
				CustomerView customerView = getCustomerView(childCustomer);
				parentNode.addChildNode(buildBulkEntry(allCustomers,
						childCustomer, customerView, transactionDate,
						bulkEntryAccountActionViews,
						bulkEntryAccountFeeActionViews));
			}
		}
		parentNode.populateLoanAccountsInformation(parentCustomer,
				transactionDate, bulkEntryAccountActionViews,
				bulkEntryAccountFeeActionViews);
		parentNode.populateSavingsAccountsInformation(parentCustomer);
		parentNode.populateCustomerAccountInformation(parentCustomer,
				bulkEntryAccountActionViews, bulkEntryAccountFeeActionViews);
		return parentNode;
	}

	private static CustomerBO getCustomer(Integer customerId,
			List<CustomerBO> customers) {
		for (CustomerBO customer : customers) {
			if (customerId.equals(customer.getCustomerId()))
				return customer;
		}
		return null;
	}

	private static List<CustomerBO> getImmediateCustomers(
			Integer parentCustomerId, List<CustomerBO> allCustomers) {
		List<CustomerBO> immediateChildren = new ArrayList<CustomerBO>();
		for (CustomerBO child : allCustomers) {
			if (child.getParentCustomer() != null
					&& parentCustomerId.equals(child.getParentCustomer()
							.getCustomerId()))
				immediateChildren.add(child);
		}
		return immediateChildren;
	}

	private static CustomerView getCustomerView(CustomerBO customer) {
		return new CustomerView(customer.getCustomerId(), customer
				.getDisplayName(),
				customer.getParentCustomer().getCustomerId(), customer
						.getCustomerLevel().getId());
	}

}
