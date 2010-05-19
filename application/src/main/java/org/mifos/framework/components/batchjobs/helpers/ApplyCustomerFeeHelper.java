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
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

/*
 * Current understanding of this class is that generates the next
 * fee(s) due for client accounts.  It checks for
 * customer accounts that had a meeting yesterday and for each of
 * then generates a new set of periodic fees due for the next
 * "installment" (the next time the fee is scheduled to be due).
 *
 *  It appears that if this batch job does not run on a given day
 *  that then, there will be no fees generated for clients who
 *  had their meetings yesterday.  It does not make up for any
 *  days when it didn't run for some reason-- but it should.
 */
public class ApplyCustomerFeeHelper extends TaskHelper {

    private AccountPersistence accountPersistence;

    public AccountPersistence getAccountPersistence() {
        if (null == accountPersistence) {
            accountPersistence = new AccountPersistence();
        }
        return accountPersistence;
    }

    public void setAccountPersistence(AccountPersistence accountPersistence) {
        this.accountPersistence = accountPersistence;
    }

    public ApplyCustomerFeeHelper(MifosTask mifosTask) {
        super(mifosTask);
    }

    @Override
    public void execute(@SuppressWarnings("unused") long timeInMills) throws BatchJobException {
        List<String> errorList = new ArrayList<String>();
        Set<Integer> accountIds;
        AccountPersistence accountPersistence = getAccountPersistence();
        try {
            accountIds = accountPersistence.getAccountsWithYesterdaysInstallment();
        } catch (Exception e) {
            throw new BatchJobException(e);
        }

        for (Integer accountId : accountIds) {
            try {
                CustomerAccountBO customerAccount = (CustomerAccountBO) accountPersistence.getAccount(accountId);

                List<Days> workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();

                HolidayDao holidayDao = DependencyInjectedServiceLocator.locateHolidayDao();
                List<Holiday> holidays = holidayDao.findAllHolidaysThisYearAndNext();

                customerAccount.applyPeriodicFeesToNextSetOfMeetingDates();

                StaticHibernateUtil.startTransaction();
                CustomerDao customerDao = DependencyInjectedServiceLocator.locateCustomerDao();
                customerDao.save(customerAccount);
                StaticHibernateUtil.commitTransaction();
            } catch (Exception e) {
                StaticHibernateUtil.rollbackTransaction();
                errorList.add(accountId.toString());
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

}
