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
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.holiday.persistence.HolidayPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.config.GeneralConfig;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerScheduleEntity;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.schedule.ScheduledDateGeneration;
import org.mifos.schedule.ScheduledEvent;
import org.mifos.schedule.ScheduledEventFactory;
import org.mifos.schedule.internal.HolidayAndWorkingDaysScheduledDateGeneration;

public class ApplyHolidayChangesHelper extends TaskHelper {

    private List<HolidayBO> unappliedHolidays;
    private AccountPersistence accountPersistence = new AccountPersistence();

    private int outputIntervalForBatchJobs;
    private int batchSize;
    private int recordCommittingSize;
    private long rollingStartTime;
    private int accountCount;
    private int currentRecordNumber;
    private ScheduledDateGeneration dateGeneration;

    private enum ScheduleTypes {
        CUSTOMER, LOAN, SAVINGS;
    }

    public ApplyHolidayChangesHelper(MifosTask mifosTask) {
        super(mifosTask);
    }

    @Override
    public void execute(long timeInMillis) throws BatchJobException {

        long taskStartTime = new DateTimeService().getCurrentDateTime().getMillis();

        outputIntervalForBatchJobs = GeneralConfig.getOutputIntervalForBatchJobs();
        batchSize = GeneralConfig.getBatchSizeForBatchJobs();
        // int recordCommittingSize = GeneralConfig.getRecordCommittingSizeForBatchJobs();
        // jpw - hardcoded recordCommittingSize to 500 because only accounts that need more schedules are returned
        recordCommittingSize = 500;

        unappliedHolidays = new ArrayList<HolidayBO>();
        try {
            unappliedHolidays = new HolidayPersistence().getUnAppliedHolidays();
        } catch (Exception e) {
            throw new BatchJobException(e);
        }

        List<String> errorList = new ArrayList<String>();
        if (unappliedHolidays != null && !unappliedHolidays.isEmpty()) {

            createDateGeneration();
            for (HolidayBO holiday : unappliedHolidays) {
                logMessage("Processing Holiday: " + holiday.getHolidayName() + " From: "
                        + DateUtils.getLocalDateFromDate(holiday.getHolidayFromDate()) + " To: "
                        + DateUtils.getLocalDateFromDate(holiday.getHolidayThruDate()));
                try {
                    handleHolidayApplication(holiday);
                    String endHolidayMessage = "Completed Processing for Holiday: " + holiday.getHolidayName()
                            + "  Time Taken: "
                            + (new DateTimeService().getCurrentDateTime().getMillis() - taskStartTime + " ms");
                    logMessage(endHolidayMessage);
                } catch (Exception e) {
                    StaticHibernateUtil.rollbackTransaction();
                    errorList.add(e.toString() + " - " + holiday.toString());
                    e.printStackTrace();
                    throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
                } finally {
                    StaticHibernateUtil.closeSession();
                }
            }

            String finalMessage = "ApplyHolidayChanges task completed in "
                    + (new DateTimeService().getCurrentDateTime().getMillis() - taskStartTime) + " ms";
            logMessage(finalMessage);
        }

    }

    private void handleHolidayApplication(HolidayBO holiday) throws Exception {

        List<Integer> accountIds = null;

        // HolidayUtils.rescheduleLoanRepaymentDates(holiday);
        accountIds = accountPersistence.getListOfAccountIdsHavingLoanSchedulesWithinDates(holiday.getHolidayFromDate(),
                holiday.getHolidayThruDate());
        rescheduleDates(accountIds, ScheduleTypes.LOAN, holiday.getHolidayFromDate(), holiday.getHolidayThruDate());

        // HolidayUtils.rescheduleSavingDates(holiday);
        accountIds = accountPersistence.getListOfAccountIdsHavingSavingsSchedulesWithinDates(holiday
                .getHolidayFromDate(), holiday.getHolidayThruDate());
        rescheduleDates(accountIds, ScheduleTypes.SAVINGS, holiday.getHolidayFromDate(), holiday.getHolidayThruDate());

        // HolidayUtils.rescheduleCustomerDates(holiday);
        accountIds = accountPersistence.getListOfAccountIdsHavingCustomerSchedulesWithinDates(holiday
                .getHolidayFromDate(), holiday.getHolidayThruDate());
        rescheduleDates(accountIds, ScheduleTypes.CUSTOMER, holiday.getHolidayFromDate(), holiday.getHolidayThruDate());

        // Change flag for this holiday in the database
        holiday.setHolidayChangesAppliedFlag(YesNoFlag.YES.getValue());
        new HolidayPersistence().createOrUpdate(holiday);
        StaticHibernateUtil.commitTransaction();
    }

    private void rescheduleDates(final List<Integer> accountIds, final ScheduleTypes scheduleType, final Date fromDate,
            final Date thruDate) throws Exception {

        if (!(scheduleType.equals(ScheduleTypes.LOAN) || scheduleType.equals(ScheduleTypes.SAVINGS) || scheduleType
                .equals(ScheduleTypes.CUSTOMER))) {
            throw new RuntimeException("Invalid ScheduleType: " + scheduleType.toString());
        }

        long startTime = new DateTimeService().getCurrentDateTime().getMillis();
        rollingStartTime = startTime;
        currentRecordNumber = 0;
        accountCount = accountIds.size();
        logMessage("No. of " + scheduleType.toString() + " Accounts to Process: " + accountCount);

        StaticHibernateUtil.getSessionTL();
        StaticHibernateUtil.startTransaction();
        for (Integer accountId : accountIds) {
            currentRecordNumber++;

            if (scheduleType.equals(ScheduleTypes.LOAN)) {
                rescheduleLoanRepaymentDates(accountId, fromDate, thruDate);
            }
            if (scheduleType.equals(ScheduleTypes.SAVINGS)) {
                rescheduleSavingDates(accountId, fromDate, thruDate);
            }
            if (scheduleType.equals(ScheduleTypes.CUSTOMER)) {
                rescheduleCustomerDates(accountId, fromDate, thruDate);
            }

            houseKeeping();

        }
        StaticHibernateUtil.commitTransaction();
        long time = new DateTimeService().getCurrentDateTime().getMillis();
        String message = "" + currentRecordNumber + " updated, " + (accountCount - currentRecordNumber)
                + " remaining, batch time: " + (time - rollingStartTime) + " ms";
        logMessage(message);

        String finalMessage = scheduleType.toString() + " Accounts Processed in: "
                + (new DateTimeService().getCurrentDateTime().getMillis() - startTime) + " ms";
        logMessage(finalMessage);

    }

    private void rescheduleLoanRepaymentDates(final Integer accountId, final Date fromDate, final Date thruDate)
            throws RuntimeException {
        try {
            LoanBO loan = new LoanBusinessService().getAccount(accountId);
            MeetingBO loadedMeeting = loan.getLoanMeeting();

            List<LoanScheduleEntity> affectedDates = accountPersistence.getLoanSchedulesForAccountThatAreWithinDates(
                    accountId, fromDate, thruDate);
            if (affectedDates == null || affectedDates.size() == 0) {
                throw new RuntimeException("No Loan Schedules were affected.  There should have been at least one.");
            }

            ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(loadedMeeting);
            for (LoanScheduleEntity loanScheduleEntity : affectedDates) {

                DateTime previousScheduledDateThatShouldFallInHoliday = new DateTime(loanScheduleEntity.getActionDate())
                        .toDateMidnight().toDateTime();
                List<DateTime> installmentDates = dateGeneration.generateScheduledDates(1,
                        previousScheduledDateThatShouldFallInHoliday, scheduledEvent);
                loanScheduleEntity.setActionDate(new java.sql.Date(installmentDates.get(0).toDate().getTime()));
            }
        } catch (ServiceException e) {
            throw new RuntimeException("Account Id: " + accountId + " - " + e);
        } catch (PersistenceException e) {
            throw new RuntimeException("Account Id: " + accountId + " - " + e);
        }
    }

    private void rescheduleSavingDates(final Integer accountId, final Date fromDate, final Date thruDate)
            throws RuntimeException {
        try {
            SavingsBO saving = (SavingsBO) new AccountBusinessService().getAccount(accountId);
            MeetingBO loadedMeeting = saving.getCustomer().getCustomerMeeting().getMeeting();
            List<SavingsScheduleEntity> affectedDates = accountPersistence
                    .getSavingsSchedulesForAccountThatAreWithinDates(accountId, fromDate, thruDate);
            if (affectedDates == null || affectedDates.size() == 0) {
                throw new RuntimeException("No Savings Schedules were affected.  There should have been at least one.");
            }

            ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(loadedMeeting);
            for (SavingsScheduleEntity savingScheduleEntity : affectedDates) {

                DateTime previousScheduledDateThatShouldFallInHoliday = new DateTime(savingScheduleEntity
                        .getActionDate()).toDateMidnight().toDateTime();
                List<DateTime> installmentDates = dateGeneration.generateScheduledDates(1,
                        previousScheduledDateThatShouldFallInHoliday, scheduledEvent);
                savingScheduleEntity.setActionDate(new java.sql.Date(installmentDates.get(0).toDate().getTime()));
            }
        } catch (ServiceException e) {
            throw new RuntimeException("Account Id: " + accountId + " - " + e);
        } catch (PersistenceException e) {
            throw new RuntimeException("Account Id: " + accountId + " - " + e);
        }
    }

    private void rescheduleCustomerDates(final Integer accountId, final Date fromDate, final Date thruDate)
            throws RuntimeException {
        try {
            CustomerAccountBO customerAccount = (CustomerAccountBO) new AccountBusinessService().getAccount(accountId);
            MeetingBO loadedMeeting = customerAccount.getCustomer().getCustomerMeeting().getMeeting();
            List<CustomerScheduleEntity> affectedDates = accountPersistence
                    .getCustomerSchedulesForAccountThatAreWithinDates(accountId, fromDate, thruDate);
            if (affectedDates == null || affectedDates.size() == 0) {
                throw new RuntimeException("No Customer Schedules were affected.  There should have been at least one.");
            }

            ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(loadedMeeting);
            for (CustomerScheduleEntity customerSchedule : affectedDates) {

                DateTime previousScheduledDateThatShouldFallInHoliday = new DateTime(customerSchedule.getActionDate())
                        .toDateMidnight().toDateTime();
                List<DateTime> installmentDates = dateGeneration.generateScheduledDates(1,
                        previousScheduledDateThatShouldFallInHoliday, scheduledEvent);
                // Date adjustedDate = HolidayUtils.adjustDate(getCalendar(customerSchedule.getActionDate()),
                // loadedMeeting).getTime();
                customerSchedule.setActionDate(new java.sql.Date(installmentDates.get(0).toDate().getTime()));
            }
        } catch (ServiceException e) {
            throw new RuntimeException("Account Id: " + accountId + " - " + e);
        } catch (PersistenceException e) {
            throw new RuntimeException("Account Id: " + accountId + " - " + e);
        }
    }

    @Override
    public boolean isTaskAllowedToRun() {
        return true;
    }

    private void createDateGeneration() {

        List<Days> workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
        HolidayDao holidayDao = DependencyInjectedServiceLocator.locateHolidayDao();
        List<Holiday> thisAndNextYearsHolidays = holidayDao.findAllHolidaysThisYearAndNext();
        dateGeneration = new HolidayAndWorkingDaysScheduledDateGeneration(workingDays, thisAndNextYearsHolidays);
    }

    private void houseKeeping() {

        if (currentRecordNumber % batchSize == 0) {
            StaticHibernateUtil.flushAndClearSession();
        }
        if (currentRecordNumber % recordCommittingSize == 0) {
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.getSessionTL();
            StaticHibernateUtil.startTransaction();
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

}
