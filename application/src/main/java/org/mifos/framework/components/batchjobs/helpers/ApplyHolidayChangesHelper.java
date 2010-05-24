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

import org.joda.time.Days;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.holiday.persistence.HolidayDaoHibernate;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.configuration.BatchJobConfigurationService;
import org.mifos.framework.components.batchjobs.configuration.StandardBatchJobConfigurationService;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.DateTimeService;

public class ApplyHolidayChangesHelper extends TaskHelper {

    // injectable external dependencies
    private AccountPersistence accountPersistence;
    private BatchJobConfigurationService batchJobConfigurationService;
    private HibernateUtil hibernateUtil;
    private HolidayDao holidayDao;
    private FiscalCalendarRules fiscalCalendarRules;
    private LoanBusinessService loanBusinessService;
    private AccountBusinessService accountBusinessService;

    private int outputIntervalForBatchJobs;
    private int batchSize;
    private int recordCommittingSize;
    private long rollingStartTime;
    private long taskStartTime;
    private int accountCount;
    private int currentRecordNumber;
    private List<Holiday> thisAndNextYearsHolidays;
    private List<Days> workingDays;
    private List<String> errorList;
    private List<Holiday> unappliedHolidays;

    public ApplyHolidayChangesHelper(MifosTask mifosTask) {
        super(mifosTask);
    }

    public BatchJobConfigurationService getBatchJobConfigurationService() {
        if (batchJobConfigurationService == null) {
            return new StandardBatchJobConfigurationService();
        }
        return this.batchJobConfigurationService;
    }

    public void setBatchJobConfigurationService(BatchJobConfigurationService batchJobConfigurationService) {
        this.batchJobConfigurationService = batchJobConfigurationService;
    }

    public HibernateUtil getHibernateUtil() {
        if (hibernateUtil == null) {
            return new HibernateUtil();
        }
        return this.hibernateUtil;
    }

    public void setHibernateUtil(HibernateUtil hibernateUtil) {
        this.hibernateUtil = hibernateUtil;
    }

    public AccountPersistence getAccountPersistence() {
        if (accountPersistence == null) {
            return new AccountPersistence();
        }
        return this.accountPersistence;
    }

    public void setAccountPersistence(AccountPersistence accountPersistence) {
        this.accountPersistence = accountPersistence;
    }

    public HolidayDao getHolidayDao() {
        if (holidayDao == null) {
            return new HolidayDaoHibernate(new GenericDaoHibernate());
        }
        return this.holidayDao;
    }

    public void setHolidayDao(HolidayDao holidayDao) {
        this.holidayDao = holidayDao;
    }

    public FiscalCalendarRules getFiscalCalendarRules() {
        if (fiscalCalendarRules == null) {
            fiscalCalendarRules = new FiscalCalendarRules();
        }
        return this.fiscalCalendarRules;
    }

    public void setFiscalCalendarRules(FiscalCalendarRules fiscalCalendarRules) {
        this.fiscalCalendarRules = fiscalCalendarRules;
    }

    public LoanBusinessService getLoanBusinessService() {
        if (loanBusinessService == null) {
            return new LoanBusinessService();
        }
        return this.loanBusinessService;
    }

    public void setLoanBusinessService(LoanBusinessService loanBusinessService) {
        this.loanBusinessService = loanBusinessService;
    }

    public AccountBusinessService getAccountBusinessService() {
        if (accountBusinessService == null) {
            return new AccountBusinessService();
        }
        return this.accountBusinessService;
    }

    public void setAccountBusinessService(AccountBusinessService accountBusinessService) {
        this.accountBusinessService = accountBusinessService;
    }

    @Override
    public void execute(long timeInMillis) throws BatchJobException {

        initializeTaskGlobalParameters(timeInMillis);

        if (unappliedHolidays != null && !unappliedHolidays.isEmpty()) {
            try {
                rescheduleDatesStartingFromUnappliedHolidays();
            } catch (Exception e) {
                getHibernateUtil().rollbackTransaction();
                errorList.add("Failed to apply holiday changes: " + e.toString());
                e.printStackTrace();
                throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
            } finally {
                getHibernateUtil().closeSession();
            }
            String finalMessage = "ApplyHolidayChanges task completed in "
                + (new DateTimeService().getCurrentDateTime().getMillis() - taskStartTime) + " ms";
            logMessage(finalMessage);
        }
    }

    private void rescheduleDatesStartingFromUnappliedHolidays () throws ServiceException, PersistenceException {

        reschedule (new LoanAccountBatch());
        reschedule (new SavingsAccountBatch());
        reschedule (new CustomerAccountBatch());

        markHolidaysAsApplied();
    }

    private void reschedule (AccountBatch accountBatch) throws PersistenceException, ServiceException {

        rollingStartTime = taskStartTime;
        currentRecordNumber = 0;

        List<Integer> accountIds = accountBatch.getAccountIdsWithDatesIn(unappliedHolidays);
        accountCount = accountIds.size();
        logMessage("No. of loan Accounts to Process: " + accountCount);

        getHibernateUtil().getSessionTL();
        getHibernateUtil().startTransaction();

        for (Integer accountId : accountIds) {
            currentRecordNumber++;
            AccountBO account = accountBatch.getAccount(accountId);
            account.rescheduleDatesForNewHolidays (workingDays, thisAndNextYearsHolidays, unappliedHolidays);
            houseKeeping();

        }
        getHibernateUtil().commitTransaction();
        long time = new DateTimeService().getCurrentDateTime().getMillis();
        String message = "" + currentRecordNumber + " updated, " + (accountCount - currentRecordNumber)
                + " remaining, batch time: " + (time - rollingStartTime) + " ms";
        logMessage(message);

        String finalMessage = "Loan accounts Processed in: "
                + (new DateTimeService().getCurrentDateTime().getMillis() - taskStartTime) + " ms";
        logMessage(finalMessage);
    }

    private void initializeTaskGlobalParameters(long startTime) {

        taskStartTime = startTime;
        outputIntervalForBatchJobs = getBatchJobConfigurationService().getOutputIntervalForBatchJobs();
        batchSize = getBatchJobConfigurationService().getBatchSizeForBatchJobs();
        // Overriding default recordCommittingSize of 1000 because only accounts that need more schedules are returned
        recordCommittingSize = 500;
        errorList = new ArrayList<String>();
        initializeHolidaysAndWorkingDays();
        unappliedHolidays = getUnappliedHolidays();
    }

    private void initializeHolidaysAndWorkingDays() {
        workingDays = getFiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
        thisAndNextYearsHolidays = getHolidayDao().findAllHolidaysThisYearAndNext();
    }

    @Override
    public boolean isTaskAllowedToRun() {
        return true;
    }

    private List<Holiday> getUnappliedHolidays() {

        return getHolidayDao().getUnAppliedHolidays();
    }

    private void markHolidaysAsApplied() {
        for (Holiday holiday : unappliedHolidays) {
            holiday.markAsApplied();
        }
    }

    private void houseKeeping() {

        if (currentRecordNumber % batchSize == 0) {
            getHibernateUtil().flushAndClearSession();
        }
        if (currentRecordNumber % recordCommittingSize == 0) {
            getHibernateUtil().commitTransaction();
            getHibernateUtil().getSessionTL();
            getHibernateUtil().startTransaction();
        }

        if (currentRecordNumber % outputIntervalForBatchJobs == 0) {
            long time = new DateTimeService().getCurrentDateTime().getMillis();
            String message = "" + currentRecordNumber + " updated, " + (accountCount - currentRecordNumber)
                    + " remaining, batch time: " + (time - rollingStartTime) + " ms";
            logMessage(message);
            rollingStartTime = time;
        }
    }

    private void logMessage(String finalMessage) {
        System.out.println(finalMessage);
        getLogger().info(finalMessage);
    }

    private interface AccountBatch {
        List<Integer> getAccountIdsWithDatesIn(List<Holiday> holidays) throws PersistenceException;
        AccountBO getAccount(Integer accountId) throws PersistenceException, ServiceException;
    }

    private abstract class AbstractAccountBatch implements AccountBatch {

        public List<Integer> getAccountIdsWithDatesIn(List<Holiday> holidays) throws PersistenceException {
            List<Integer> accountIds = new ArrayList<Integer>();
            for (Holiday holiday : holidays) {
                accountIds.addAll(getAccountIdsHavingSchedulesWithinHoliday(holiday));
            }
            return accountIds;
        }

        public abstract AccountBO getAccount(Integer accountId) throws PersistenceException, ServiceException;

        protected abstract List<Integer> getAccountIdsHavingSchedulesWithinHoliday (Holiday holiday)
                    throws PersistenceException;

    }

    private class LoanAccountBatch extends AbstractAccountBatch {

        @Override
        public List<Integer> getAccountIdsHavingSchedulesWithinHoliday(Holiday holiday) throws PersistenceException{
            return getAccountPersistence().getListOfAccountIdsHavingLoanSchedulesWithinDates
                                        (holiday.getFromDate(), holiday.getThruDate());
            }

        @Override
        public AccountBO getAccount(Integer accountId) throws ServiceException {
            return getAccountBusinessService().getAccount(accountId);
        }
    }

    private class SavingsAccountBatch extends AbstractAccountBatch {

        @Override
        public List<Integer> getAccountIdsHavingSchedulesWithinHoliday(Holiday holiday) throws PersistenceException{

            return getAccountPersistence().getListOfAccountIdsHavingSavingsSchedulesWithinDates
            (holiday.getFromDate(), holiday.getThruDate());
        }

        @Override
        public AccountBO getAccount(Integer accountId) throws ServiceException {
            return getAccountBusinessService().getAccount(accountId);
        }

    }

    private class CustomerAccountBatch extends AbstractAccountBatch {

        @Override
        public List<Integer> getAccountIdsHavingSchedulesWithinHoliday(Holiday holiday) throws PersistenceException{
            return getAccountPersistence().getListOfAccountIdsHavingCustomerSchedulesWithinDates
            (holiday.getFromDate(), holiday.getThruDate());
        }

        @Override
        public AccountBO getAccount(Integer accountId) throws ServiceException {
            return getAccountBusinessService().getAccount(accountId);
        }

    }
}
