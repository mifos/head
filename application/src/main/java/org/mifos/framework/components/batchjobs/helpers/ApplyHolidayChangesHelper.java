/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.holiday.persistence.HolidayDaoHibernate;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.configuration.BatchJobConfigurationService;
import org.mifos.framework.components.batchjobs.configuration.StandardBatchJobConfigurationService;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.schedule.ScheduledDateGeneration;
import org.mifos.schedule.ScheduledEvent;
import org.mifos.schedule.ScheduledEventFactory;
import org.mifos.schedule.internal.HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration;

public class ApplyHolidayChangesHelper extends TaskHelper {

    // injectable external dependencies
    private LegacyAccountDao legacyAccountDao;
    private BatchJobConfigurationService batchJobConfigurationService;
    private HolidayDao holidayDao;
    private FiscalCalendarRules fiscalCalendarRules;
    private Map<Short, ScheduledDateGeneration> officeScheduledDateGenerationMap;

    private int outputIntervalForBatchJobs;
    private int batchSize;
    private int recordCommittingSize;
    private long rollingStartTime;
    private int accountCount;
    private int currentRecordNumber;
    private List<Days> workingDays;
    private List<String> errorList;
    private List<Holiday> unappliedHolidays;

    public ApplyHolidayChangesHelper() {
        super();
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

    public LegacyAccountDao getlegacyAccountDao() {
        if (legacyAccountDao == null) {
            return ApplicationContextProvider.getBean(LegacyAccountDao.class);
        }
        return this.legacyAccountDao;
    }

    public void setlegacyAccountDao(LegacyAccountDao legacyAccountDao) {
        this.legacyAccountDao = legacyAccountDao;
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

    @Override
    public void execute(long timeInMillis) throws BatchJobException {

        long taskStartTime = new DateTimeService().getCurrentDateTime().getMillis();

        initializeTaskGlobalParameters();

        if (unappliedHolidays != null && !unappliedHolidays.isEmpty()) {

            for (Holiday holiday : unappliedHolidays) {
                logMessage("Processing Holiday: " + holiday.getName() + " From: "
                        + DateUtils.getLocalDateFromDate(holiday.getFromDate().toDate()) + " To: "
                        + DateUtils.getLocalDateFromDate(holiday.getThruDate().toDate()));

                try {
                    rescheduleDatesStartingFromUnappliedHoliday(holiday);
                } catch (Exception e) {
                    StaticHibernateUtil.rollbackTransaction();
                    errorList.add("Failed to apply holiday changes: " + e.toString());
                    e.printStackTrace();
                    throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
                }
            }
        }
        StaticHibernateUtil.closeSession();
        String finalMessage = "ApplyHolidayChanges task completed in "
                + (new DateTimeService().getCurrentDateTime().getMillis() - taskStartTime) + " ms";
        logMessage(finalMessage);
    }

    private void rescheduleDatesStartingFromUnappliedHoliday(Holiday holiday) throws PersistenceException {

        long holidayStartTime = new DateTimeService().getCurrentDateTime().getMillis();

        reschedule(holiday, new SavingsAccountBatch());
        reschedule(holiday, new CustomerAccountBatch());
        reschedule(holiday, new LoanAccountBatch());

        applyHoliday(holiday);

        String endHolidayMessage = "Completed Processing for Holiday: " + holiday.getName() + "  Time Taken: "
                + (new DateTimeService().getCurrentDateTime().getMillis() - holidayStartTime) + " ms";
        logMessage(endHolidayMessage);
    }

    private void reschedule(Holiday holiday, AccountBatch accountBatch) throws PersistenceException {

        long rescheduleStartTime = new DateTimeService().getCurrentDateTime().getMillis();

        List<Object[]> accountIdsArray = accountBatch.getAccountIdsWithDatesIn(holiday);
        accountCount = accountIdsArray.size();
        logMessage("No. of " + accountBatch.getAccountTypeName() + " Accounts to Process: " + accountCount
                + " : Query took: " + (new DateTimeService().getCurrentDateTime().getMillis() - rescheduleStartTime)
                + " ms");

        rollingStartTime = new DateTimeService().getCurrentDateTime().getMillis();
        currentRecordNumber = 0;

        StaticHibernateUtil.getSessionTL();
        StaticHibernateUtil.startTransaction();

        for (Object[] accountIds : accountIdsArray) {
            Integer accountId = (Integer) accountIds[0];
            Short officeId = (Short) accountIds[1];
            Integer meetingId = (Integer) accountIds[2];

            currentRecordNumber++;

            ScheduledDateGeneration officeScheduledDateGeneration = getScheduledDateGeneration(officeId);

            DateTime amendedThruDate = holiday.getThruDate();
            // Moratoria affect all installments on or after the fromDate.
            // Normal holidays only affect installments between the fromDate and thruDate
            if (holiday.getRepaymentRuleType().getValue().equals(RepaymentRuleTypes.REPAYMENT_MORATORIUM.getValue())) {
                amendedThruDate = holiday.getThruDate().plusYears(10);
            }
            List<AccountActionDateEntity> futureAffectedInstallments = accountBatch.getAffectedInstallments(accountId,
                    holiday.getFromDate(), amendedThruDate);

            MeetingBO meeting = (MeetingBO) StaticHibernateUtil.getSessionTL().get(MeetingBO.class, meetingId);

            rescheduleDatesForNewHolidays(officeScheduledDateGeneration, futureAffectedInstallments, meeting);

            houseKeeping();

        }

        StaticHibernateUtil.commitTransaction();
        long rescheduleEndTime = new DateTimeService().getCurrentDateTime().getMillis();
        String message = "" + currentRecordNumber + " updated, " + (accountCount - currentRecordNumber)
                + " remaining, batch time: " + (rescheduleEndTime - rollingStartTime) + " ms";
        logMessage(message);

        String finalMessage = accountBatch.getAccountTypeName() + " accounts Processed in: "
                + (rescheduleEndTime - rescheduleStartTime) + " ms";
        logMessage(finalMessage);
    }

    /**
     * Shift schedule dates to account for new holidays, starting with the first future or present installment presented
     */
    private void rescheduleDatesForNewHolidays(final ScheduledDateGeneration scheduledDateGeneration,
            List<AccountActionDateEntity> futureAffectedInstallments, final MeetingBO meeting) {

        List<DateTime> installmentDates = getDatesToReplaceScheduledDates(scheduledDateGeneration,
                futureAffectedInstallments, meeting);

        int counter = 0;
        for (AccountActionDateEntity actionDate : futureAffectedInstallments) {

            actionDate.setActionDate(new java.sql.Date(installmentDates.get(counter).toDate().getTime()));
            counter++;
        }

    }

    private List<DateTime> getDatesToReplaceScheduledDates(final ScheduledDateGeneration dateGeneration,
            List<AccountActionDateEntity> installments, final MeetingBO meeting) {

        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(meeting);
        int numberOfDatesToGenerate = installments.size() + 1;

        DateTime dayBeforeFirstDateToGenerate = new DateTime(installments.get(0).getActionDate()).minusDays(1);
        return dateGeneration.generateScheduledDates(numberOfDatesToGenerate, dayBeforeFirstDateToGenerate,
                scheduledEvent, false);
    }

    private ScheduledDateGeneration getScheduledDateGeneration(Short officeId) {

        ScheduledDateGeneration scheduledDateGeneration = officeScheduledDateGenerationMap.get(officeId);

        if (scheduledDateGeneration != null) {
            return scheduledDateGeneration;
        }

        List<Holiday> futureHolidays = getHolidayDao().findCurrentAndFutureOfficeHolidaysEarliestFirst(officeId);
        scheduledDateGeneration = new HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration(workingDays,
                futureHolidays);
        officeScheduledDateGenerationMap.put(officeId, scheduledDateGeneration);

        return scheduledDateGeneration;
    }

    private void applyHoliday(Holiday holiday) throws PersistenceException {
        StaticHibernateUtil.getSessionTL();
        StaticHibernateUtil.startTransaction();
        Holiday updateHoliday = getHolidayDao().findHolidayById(holiday.getId());
        updateHoliday.markAsApplied();
        getHolidayDao().save(updateHoliday);
        StaticHibernateUtil.commitTransaction();
    }

    private void initializeTaskGlobalParameters() {

        outputIntervalForBatchJobs = getBatchJobConfigurationService().getOutputIntervalForBatchJobs();
        batchSize = getBatchJobConfigurationService().getBatchSizeForBatchJobs();

        recordCommittingSize = 500;
        errorList = new ArrayList<String>();
        workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
        unappliedHolidays = getHolidayDao().getUnAppliedHolidays();
        officeScheduledDateGenerationMap = new HashMap<Short, ScheduledDateGeneration>();
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
        getLogger().info(finalMessage);
    }

    private interface AccountBatch {
        List<Object[]> getAccountIdsWithDatesIn(Holiday holiday) throws PersistenceException;

        List<AccountActionDateEntity> getAffectedInstallments(Integer accountId, DateTime fromDate, DateTime thruDate)
                throws PersistenceException;

        String getAccountTypeName();

    }

    private abstract class AbstractAccountBatch implements AccountBatch {

        @Override
		public List<Object[]> getAccountIdsWithDatesIn(Holiday holiday) throws PersistenceException {
            return getAccountIdsHavingSchedulesWithinHoliday(holiday);
        }

        @Override
		public List<AccountActionDateEntity> getAffectedInstallments(Integer accountId, DateTime fromDate,
                DateTime thruDate) throws PersistenceException {
            return getAffectedInstallmentsForAccountType(accountId, fromDate, thruDate);
        }

        @Override
		public String getAccountTypeName() {
            return "Abstract Account Type";
        }

        protected abstract List<Object[]> getAccountIdsHavingSchedulesWithinHoliday(Holiday holiday)
                throws PersistenceException;

        protected abstract List<AccountActionDateEntity> getAffectedInstallmentsForAccountType(Integer accountId,
                DateTime fromDate, DateTime thruDate) throws PersistenceException;

    }

    private class LoanAccountBatch extends AbstractAccountBatch {

        @Override
        public List<Object[]> getAccountIdsHavingSchedulesWithinHoliday(Holiday holiday) throws PersistenceException {
            return getlegacyAccountDao().getListOfAccountIdsHavingLoanSchedulesWithinAHoliday(holiday);
        }

        @Override
        public List<AccountActionDateEntity> getAffectedInstallmentsForAccountType(Integer accountId,
                DateTime fromDate, DateTime thruDate) throws PersistenceException {

            List<AccountActionDateEntity> affectedInstallmentsGeneric = new ArrayList<AccountActionDateEntity>();
            affectedInstallmentsGeneric.addAll(getlegacyAccountDao().getLoanSchedulesForAccountThatAreWithinDates(
                    accountId, fromDate, thruDate));
            return affectedInstallmentsGeneric;
        }

        @Override
        public String getAccountTypeName() {
            return "Loan";
        }
    }

    private class SavingsAccountBatch extends AbstractAccountBatch {

        @Override
        public List<Object[]> getAccountIdsHavingSchedulesWithinHoliday(Holiday holiday) throws PersistenceException {

            return getlegacyAccountDao().getListOfAccountIdsHavingSavingsSchedulesWithinAHoliday(holiday);
        }

        @Override
        public List<AccountActionDateEntity> getAffectedInstallmentsForAccountType(Integer accountId,
                DateTime fromDate, DateTime thruDate) throws PersistenceException {

            List<AccountActionDateEntity> affectedInstallmentsGeneric = new ArrayList<AccountActionDateEntity>();
            affectedInstallmentsGeneric.addAll(getlegacyAccountDao().getSavingsSchedulesForAccountThatAreWithinDates(
                    accountId, fromDate, thruDate));
            return affectedInstallmentsGeneric;
        }

        @Override
        public String getAccountTypeName() {
            return "Savings";
        }
    }

    private class CustomerAccountBatch extends AbstractAccountBatch {

        @Override
        public List<Object[]> getAccountIdsHavingSchedulesWithinHoliday(Holiday holiday) throws PersistenceException {
            return getlegacyAccountDao().getListOfAccountIdsHavingCustomerSchedulesWithinAHoliday(holiday);
        }

        @Override
        public List<AccountActionDateEntity> getAffectedInstallmentsForAccountType(Integer accountId,
                DateTime fromDate, DateTime thruDate) throws PersistenceException {

            List<AccountActionDateEntity> affectedInstallmentsGeneric = new ArrayList<AccountActionDateEntity>();
            affectedInstallmentsGeneric.addAll(getlegacyAccountDao()
                    .getCustomerSchedulesForAccountThatAreWithinDates(accountId, fromDate, thruDate));
            return affectedInstallmentsGeneric;
        }

        @Override
        public String getAccountTypeName() {
            return "Customer";
        }
    }

}
