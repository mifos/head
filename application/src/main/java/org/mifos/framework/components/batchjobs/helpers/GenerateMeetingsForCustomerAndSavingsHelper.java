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

import org.joda.time.Days;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.config.GeneralConfig;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;

public class GenerateMeetingsForCustomerAndSavingsHelper extends TaskHelper {

    private final HolidayDao holidayDao = DependencyInjectedServiceLocator.locateHolidayDao();
    
    public GenerateMeetingsForCustomerAndSavingsHelper(final MifosTask mifosTask) {
        super(mifosTask);
    }

    @Override
    public void execute(final long timeInMillis) throws BatchJobException {
        long taskStartTime = new DateTimeService().getCurrentDateTime().getMillis();
        AccountPersistence accountPersistence = new AccountPersistence();
        List<Integer> customerAndSavingsAccountIds;
        int accountCount = 0;

        try {
            long time1 = new DateTimeService().getCurrentDateTime().getMillis();
            customerAndSavingsAccountIds = accountPersistence
                    .getActiveCustomerAndSavingsAccountIdsForGenerateMeetingTask();
            accountCount = customerAndSavingsAccountIds.size();
            long duration = new DateTimeService().getCurrentDateTime().getMillis() - time1;
            getLogger().info("Time to execute the query " + duration + " . Got " + accountCount + " accounts.");
            if (accountCount == 0) {
                return;
            }
        } catch (PersistenceException e) {
            throw new BatchJobException(e);
        }

        List<String> errorList = new ArrayList<String>();
        int currentRecordNumber = 0;
        int outputIntervalForBatchJobs = GeneralConfig.getOutputIntervalForBatchJobs();
        int batchSize = GeneralConfig.getBatchSizeForBatchJobs();
        // int recordCommittingSize = GeneralConfig.getRecordCommittingSizeForBatchJobs();
        // jpw - hardcoded recordCommittingSize to 100 because now only accounts that need more schedules are returned
        int recordCommittingSize = 100;
        getLogger().info(
                "Using parameters:" + "\n  OutputIntervalForBatchJobs: " + outputIntervalForBatchJobs
                        + "\n  BatchSizeForBatchJobs: " + batchSize + "\n  RecordCommittingSizeForBatchJobs: "
                        + recordCommittingSize);
        String initial_message = "" + accountCount + " accounts to process, results output every "
                + outputIntervalForBatchJobs + " accounts";
        getLogger().info(initial_message);

        long startTime = new DateTimeService().getCurrentDateTime().getMillis();
        Integer currentAccountId = null;
        int updatedRecordCount = 0;

        List<Holiday> orderedUpcomingHolidays = holidayDao.findAllHolidaysThisYearAndNext();
        
        List<Days> workingDays = FiscalCalendarRules.getWorkingDaysAsJodaTimeDays();
        
        try {
            StaticHibernateUtil.getSessionTL();
            StaticHibernateUtil.startTransaction();
            for (Integer accountId : customerAndSavingsAccountIds) {
                currentRecordNumber++;
                currentAccountId = accountId;
                AccountBO accountBO = accountPersistence.getAccount(accountId);
                
                if (accountBO instanceof CustomerAccountBO) {
                    ((CustomerAccountBO) accountBO).generateNextSetOfMeetingDates(workingDays, orderedUpcomingHolidays);
                    updatedRecordCount++;
                } else if (accountBO instanceof SavingsBO) {
                    ((SavingsBO) accountBO).generateNextSetOfMeetingDates(workingDays, orderedUpcomingHolidays);
                    updatedRecordCount++;
                }
                
                if (currentRecordNumber % batchSize == 0) {
                    StaticHibernateUtil.flushAndClearSession();
                    getLogger().debug("completed HibernateUtil.flushAndClearSession()");
                }
                if (updatedRecordCount > 0) {
                    if (updatedRecordCount % recordCommittingSize == 0) {
                        StaticHibernateUtil.commitTransaction();
                        StaticHibernateUtil.getSessionTL();
                        StaticHibernateUtil.startTransaction();
                    }
                }
                if (currentRecordNumber % outputIntervalForBatchJobs == 0) {
                    long time = System.currentTimeMillis();
                    String message = "" + currentRecordNumber + " processed, " + (accountCount - currentRecordNumber)
                            + " remaining, " + updatedRecordCount + " updated, batch time: " + (time - startTime)
                            + " ms";
                    System.out.println(message);
                    getLogger().info(message);
                    startTime = time;
                }
            }
            StaticHibernateUtil.commitTransaction();

        } catch (Exception e) {
            getLogger().info("account " + currentAccountId.intValue() + " exception " + e.getMessage());
            StaticHibernateUtil.rollbackTransaction();
            if (currentAccountId != null) {
                errorList.add(currentAccountId.toString());
            }
            errorList.add(currentAccountId.toString());
            getLogger().error("Unable to generate schedules for account with ID " + currentAccountId, e);
        } finally {
            StaticHibernateUtil.closeSession();
        }

        if (errorList.size() > 0) {
            throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
        }
        getLogger().info(
                "GenerateMeetingsForCustomerAndSavings ran in "
                        + (new DateTimeService().getCurrentDateTime().getMillis() - taskStartTime));

    }

    @Override
    public boolean isTaskAllowedToRun() {
        return true;
    }

}
