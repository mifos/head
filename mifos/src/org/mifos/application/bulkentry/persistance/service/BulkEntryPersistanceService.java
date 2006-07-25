/**

 * BulkEntryPersistanceService.java    version: 1.0

 

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

package org.mifos.application.bulkentry.persistance.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.bulkentry.business.BulkEntryAccountActionView;
import org.mifos.application.bulkentry.business.BulkEntryAccountFeeActionView;
import org.mifos.application.bulkentry.persistance.BulkEntryPersistance;
import org.mifos.framework.persistence.service.PersistenceService;
import org.mifos.framework.util.helpers.DateUtils;

public class BulkEntryPersistanceService extends PersistenceService {

	private Map<Integer, AccountBO> accounts = new HashMap<Integer, AccountBO>();

	public List<BulkEntryAccountActionView> getBulkEntryActionView(
			Date meetingDate, String searchString, Short officeId) {
		return new BulkEntryPersistance().getBulkEntryActionView(meetingDate,
				searchString, officeId);

	}

	public List<BulkEntryAccountFeeActionView> getBulkEntryFeeActionView(
			Date meetingDate, String searchString, Short officeId) {
		return new BulkEntryPersistance().getBulkEntryFeeActionView(
				meetingDate, searchString, officeId);

	}

	public List<AccountFeesActionDetailEntity> getFeesActionDetails(
			Integer actionId) {
		return new BulkEntryPersistance().getFeesActionDetails(actionId);

	}

	public AccountBO getCustomerAccountWithAccountActionsInitialized(
			Integer accountId) {
		return new AccountPersistence()
				.getCustomerAccountWithAccountActionsInitialized(accountId);
	}

	public AccountBO getSavingsAccountWithAccountActionsInitialized(
			Integer accountId) {
		if (!accounts.containsKey(accountId)) {
			AccountBO account = new AccountPersistence()
					.getSavingsAccountWithAccountActionsInitialized(accountId);
			accounts.put(accountId, account);
			Set<AccountActionDateEntity> accountActionDates = account
					.getAccountActionDates();
			for (Iterator<AccountActionDateEntity> iter = accountActionDates
					.iterator(); iter.hasNext();) {
				AccountActionDateEntity actionDate = iter.next();
				if (actionDate.getPaymentStatus().equals(
						AccountConstants.PAYMENT_PAID)
						|| actionDate.compareDate(DateUtils
								.getCurrentDateWithoutTimeStamp()) > 0)
					iter.remove();
			}
		}
		SavingsBO savings = (SavingsBO) accounts.get(accountId);
		savings.setAccountPayments(null);
		savings.setSavingsActivityDetails(null);
		return savings;
	}

	public AccountBO getLoanAccountWithAccountActionsInitialized(
			Integer accountId) {
		return new AccountPersistence()
				.getLoanAccountWithAccountActionsInitialized(accountId);
	}

}
