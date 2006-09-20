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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.bulkentry.business.BulkEntryAccountFeeActionView;
import org.mifos.application.bulkentry.business.BulkEntryInstallmentView;
import org.mifos.application.bulkentry.persistance.BulkEntryPersistance;
import org.mifos.application.bulkentry.util.helpers.BulkEntryCache;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.persistence.service.PersistenceService;
import org.mifos.framework.util.helpers.DateUtils;

public class BulkEntryPersistanceService extends PersistenceService {

	private BulkEntryCache bulkEntryCache = new BulkEntryCache();

	public List<BulkEntryInstallmentView> getBulkEntryActionView(
			Date meetingDate, String searchString, Short officeId,
			AccountTypes accountType) throws PersistenceException {
		return new BulkEntryPersistance().getBulkEntryActionView(meetingDate,
				searchString, officeId, accountType);

	}

	public List<BulkEntryAccountFeeActionView> getBulkEntryFeeActionView(
			Date meetingDate, String searchString, Short officeId,
			AccountTypes accountType) throws PersistenceException {
		return new BulkEntryPersistance().getBulkEntryFeeActionView(
				meetingDate, searchString, officeId, accountType);

	}

	public AccountBO getCustomerAccountWithAccountActionsInitialized(
			Integer accountId) throws PersistenceException {
		return new AccountPersistence()
				.getCustomerAccountWithAccountActionsInitialized(accountId);
	}

	public AccountBO getSavingsAccountWithAccountActionsInitialized(
			Integer accountId) throws PersistenceException {
		if (!bulkEntryCache.isAccountPresent(accountId)) {
			AccountBO account;
			account = new AccountPersistence()
					.getSavingsAccountWithAccountActionsInitialized(accountId);
			bulkEntryCache.addAccount(accountId, account);
			Set<AccountActionDateEntity> accountActionDates = account
					.getAccountActionDates();
			for (Iterator<AccountActionDateEntity> iter = accountActionDates
					.iterator(); iter.hasNext();) {
				AccountActionDateEntity actionDate = iter.next();
				actionDate.getCustomer().getCustomerId();
				if (actionDate.getPaymentStatus().equals(
						PaymentStatus.PAID.getValue())
						|| actionDate.compareDate(DateUtils
								.getCurrentDateWithoutTimeStamp()) > 0)
					iter.remove();
			}
		}
		SavingsBO savings = (SavingsBO) bulkEntryCache.getAccount(accountId);
		savings.setAccountPayments(null);
		savings.setSavingsActivityDetails(null);
		return savings;
	}

	public AccountBO getLoanAccountWithAccountActionsInitialized(
			Integer accountId) throws PersistenceException {
		return new AccountPersistence()
				.getLoanAccountWithAccountActionsInitialized(accountId);
	}

	public CustomerBO getCustomer(Integer customerId) throws PersistenceException{
		if (!bulkEntryCache.isCustomerPresent(customerId)) {
			CustomerBO customer = new CustomerPersistence()
					.getCustomer(customerId);
			bulkEntryCache.addCustomer(customerId, customer);
		}
		return bulkEntryCache.getCustomer(customerId);
	}

	public PersonnelBO getPersonnel(Short personnelId)throws ServiceException {
		
		try{
		if (!bulkEntryCache.isPersonnelPresent(personnelId)) {
			PersonnelBO personnel = new PersonnelPersistence()
					.getPersonnel(personnelId);
			bulkEntryCache.addPersonnel(personnelId, personnel);
		}
		return bulkEntryCache.getPersonnel(personnelId);
		}catch (PersistenceException e) {
			throw new ServiceException(e);
		}
		

	}

}
