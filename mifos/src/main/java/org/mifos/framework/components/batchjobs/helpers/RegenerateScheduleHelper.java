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
 
package org.mifos.framework.components.batchjobs.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class RegenerateScheduleHelper extends TaskHelper {

	public RegenerateScheduleHelper(MifosTask mifosTask) {
		super(mifosTask);
	}

	List<Integer> accountList;

	@Override
	public void execute(long timeInMills) throws BatchJobException {
		List<String> errorList = new ArrayList<String>();
		accountList = new ArrayList<Integer>();
		List<Integer> customerIds;
		try {
			customerIds = new CustomerPersistence()
					.getCustomersWithUpdatedMeetings();
		} catch (Exception e) {
			throw new BatchJobException(e);
		}
		if (customerIds != null && !customerIds.isEmpty())
			for (Integer customerId : customerIds) {
				try {
					handleChangeInMeetingSchedule(customerId);
					StaticHibernateUtil.commitTransaction();
				} catch (Exception e) {
					StaticHibernateUtil.rollbackTransaction();
					errorList.add(customerId.toString());
				} finally {
					StaticHibernateUtil.closeSession();
				}
			}
		if (errorList.size() > 0)
			throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
	}

	@Override
	public boolean isTaskAllowedToRun() {
		return true;
	}

	private void handleChangeInMeetingSchedule(Integer customerId)
			throws Exception {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		CustomerBO customer = customerPersistence.getCustomer(customerId);
		Set<AccountBO> accounts = customer.getAccounts();
		if (accounts != null && !accounts.isEmpty())
			for (AccountBO account : accounts) {
				if (!accountList.contains(account.getAccountId())) {
					account.handleChangeInMeetingSchedule();
					accountList.add(account.getAccountId());
				}
			}
		List<Integer> customerIds = customerPersistence.getChildrenForParent(
				customer.getSearchId(), customer.getOffice().getOfficeId());
		if (customerIds != null && !customerIds.isEmpty()) {
			for (Integer childCustomerId : customerIds) {
				handleChangeInMeetingSchedule(childCustomerId);
			}
		}
	}

}
