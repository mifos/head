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

package org.mifos.framework.components.batchjobs.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.joda.time.Days;
import org.mifos.accounts.business.AccountBO;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class RegenerateScheduleHelper extends TaskHelper {

    private final HolidayDao holidayDao = DependencyInjectedServiceLocator.locateHolidayDao();
    private final CustomerDao customerDao = DependencyInjectedServiceLocator.locateCustomerDao();

    public RegenerateScheduleHelper(final MifosTask mifosTask) {
        super(mifosTask);
    }

    private List<Integer> accountList = new ArrayList<Integer>();

    @Override
    public void execute(@SuppressWarnings("unused") final long timeInMills) throws BatchJobException {

        accountList = new ArrayList<Integer>();
        List<String> errorList = new ArrayList<String>();

        List<Days> workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();

        List<Integer> customerIds = customerDao.retrieveCustomerIdsOfCustomersWithUpdatedMeetings();
        for (Integer customerId : customerIds) {
            try {
                CustomerBO customer = customerDao.findCustomerById(customerId);
                List<Holiday> orderedUpcomingHolidays = holidayDao.findAllHolidaysThisYearAndNext(customer
                        .getOfficeId());

                StaticHibernateUtil.startTransaction();

                handleChangeInMeetingSchedule(customer, workingDays, orderedUpcomingHolidays);

                StaticHibernateUtil.commitTransaction();
            } catch (Exception e) {
                StaticHibernateUtil.rollbackTransaction();
                errorList.add(customerId.toString());
            } finally {
                StaticHibernateUtil.closeSession();
            }
        }

        if (errorList.size() > 0) {
            throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
        }
    }

    @Override
    public boolean isTaskAllowedToRun() {
        return true;
    }

    private void handleChangeInMeetingSchedule(CustomerBO customer, final List<Days> workingDays, final List<Holiday> orderedUpcomingHolidays) throws Exception {
        Set<AccountBO> accounts = customer.getAccounts();
        for (AccountBO account : accounts) {
            if (!accountList.contains(account.getAccountId())) {
                account.handleChangeInMeetingSchedule(workingDays, orderedUpcomingHolidays);
                customerDao.save(account);
                accountList.add(account.getAccountId());
            }
        }

        List<Integer> customerIds = customerDao.retrieveCustomerIdsOfChildrenForParent(customer.getSearchId(), customer.getOffice().getOfficeId());
        for (Integer childCustomerId : customerIds) {
            CustomerBO child = customerDao.findCustomerById(childCustomerId);
            handleChangeInMeetingSchedule(child, workingDays, orderedUpcomingHolidays);
        }
    }
}