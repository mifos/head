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

import org.mifos.application.bulkentry.business.BulkEntryView;
import org.mifos.application.customer.business.CustomerView;

public class BulkEntryNodeBuilder {

	/**
	 * This function builds each bulk entry node. Each node has a customer view
	 * object associated with it which has information like customer id,
	 * customer name, search id, parent id etc. The node also has information of
	 * the accounts for which payment is due as well the attendance of the
	 * customer at the last meeting. This node is build from the list of
	 * children that are retrieved for the parent customer.
	 * 
	 */
	public static BulkEntryView buildBulkEntryNode(
			List<CustomerView> allChildNodes, CustomerView parentCustomer,
			Date transactionDate) {

		BulkEntryView parentNode = new BulkEntryView(parentCustomer);
		List<CustomerView> immediateChildren = getImmediateChildren(parentNode
				.getCustomerDetail().getCustomerId(), allChildNodes);
		if (immediateChildren != null && immediateChildren.size() != 0) {
			for (CustomerView childCustomer : immediateChildren) {
				parentNode.addChildNode(buildBulkEntryNode(allChildNodes,
						childCustomer, transactionDate));
			}
		}
		parentNode.populate(transactionDate);
		return parentNode;
	}

	/**
	 * This function retrieves the list of immediate children for a parent. It
	 * checks the parent ids and obtains those customers whose parent id is that
	 * of the customer id being passed.
	 */
	private static List<CustomerView> getImmediateChildren(Integer customerId,
			List<CustomerView> allChildNodes) {

		List<CustomerView> immediateChildren = new ArrayList<CustomerView>();
		for (CustomerView child : allChildNodes) {
			if (customerId.equals(child.getParentCustomerId())) {
				immediateChildren.add(child);
			}
		}
		return immediateChildren;
	}

	public static void buildBulkEntrySavingsAccounts(BulkEntryView parentNode,
			Date transactionDate) {
		List<BulkEntryView> immediateChildren = parentNode
				.getBulkEntryChildren();
		if (immediateChildren != null && immediateChildren.size() != 0) {
			for (BulkEntryView childCustomer : immediateChildren) {
				buildBulkEntrySavingsAccounts(childCustomer, transactionDate);
			}
		}
		parentNode.populateSavingsAccountActions(parentNode.getCustomerDetail()
				.getCustomerId(), transactionDate);

	}
}
