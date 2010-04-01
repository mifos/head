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

import static org.mifos.framework.util.helpers.DateUtils.getCalendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.persistence.HolidayPersistence;
import org.mifos.application.holiday.util.helpers.HolidayUtils;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.util.helpers.YesNoFlag;
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

public class ApplyHolidayChangesHelper extends TaskHelper {

    List<HolidayBO> unappliedHolidays;
    AccountPersistence accountPersistence = new AccountPersistence();

    int outputIntervalForBatchJobs;
    int batchSize;
    int recordCommittingSize;
    long rollingStartTime;
    int accountCount;
    int currentRecordNumber;

    private enum ScheduleTypes {
        CUSTOMER, LOAN, SAVINGS;
    }

    public ApplyHolidayChangesHelper(MifosTask mifosTask) {
        super(mifosTask);
    }

    @Override
    public void execute(long timeInMillis) throws BatchJobException {

        long taskStartTime = new DateTimeService().getCurrentDateTime().getMillis();

        currentRecordNumber = 0;
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
            for (HolidayBO holiday : unappliedHolidays) {
                logMessage("Processing Holiday: " + holiday.getHolidayName());
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

            for (LoanScheduleEntity loanScheduleEntity : affectedDates) {

                Date adjustedDate = HolidayUtils.adjustDate(getCalendar(loanScheduleEntity.getActionDate()),
                        loadedMeeting).getTime();
                loanScheduleEntity.setActionDate(new java.sql.Date(adjustedDate.getTime()));
            }
        } catch (ServiceException e) {
            throw new RuntimeException("Account Id: " + accountId + " - " + e);
        } catch (MeetingException e) {
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

            for (SavingsScheduleEntity savingScheduleEntity : affectedDates) {

                Date adjustedDate = HolidayUtils.adjustDate(getCalendar(savingScheduleEntity.getActionDate()),
                        loadedMeeting).getTime();
                savingScheduleEntity.setActionDate(new java.sql.Date(adjustedDate.getTime()));
            }
        } catch (ServiceException e) {
            throw new RuntimeException("Account Id: " + accountId + " - " + e);
        } catch (MeetingException e) {
            throw new RuntimeException("Account Id: " + accountId + " - " + e);
        } catch (PersistenceException e) {
            throw new RuntimeException("Account Id: " + accountId + " - " + e);
        }
    }

    public void rescheduleCustomerDates(final Integer accountId, final Date fromDate, final Date thruDate)
            throws RuntimeException {
        try {
            CustomerAccountBO customerAccount = (CustomerAccountBO) new AccountBusinessService().getAccount(accountId);
            MeetingBO loadedMeeting = customerAccount.getCustomer().getCustomerMeeting().getMeeting();
            List<CustomerScheduleEntity> affectedDates = accountPersistence
                    .getCustomerSchedulesForAccountThatAreWithinDates(accountId, fromDate, thruDate);
            if (affectedDates == null || affectedDates.size() == 0) {
                throw new RuntimeException("No Customer Schedules were affected.  There should have been at least one.");
            }

            for (CustomerScheduleEntity customerSchedule : affectedDates) {

                Date adjustedDate = HolidayUtils.adjustDate(getCalendar(customerSchedule.getActionDate()),
                        loadedMeeting).getTime();
                customerSchedule.setActionDate(new java.sql.Date(adjustedDate.getTime()));
            }
        } catch (ServiceException e) {
            throw new RuntimeException("Account Id: " + accountId + " - " + e);
        } catch (MeetingException e) {
            throw new RuntimeException("Account Id: " + accountId + " - " + e);
        } catch (PersistenceException e) {
            throw new RuntimeException("Account Id: " + accountId + " - " + e);
        }
    }

    @Override
    public boolean isTaskAllowedToRun() {
        return true;
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
