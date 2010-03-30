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
import java.util.Set;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.persistence.HolidayPersistence;
import org.mifos.application.holiday.util.helpers.HolidayUtils;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.GeneralConfig;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;

public class ApplyHolidayChangesHelper extends TaskHelper {

    List<HolidayBO> unappliedHolidays;

    public ApplyHolidayChangesHelper(MifosTask mifosTask) {
        super(mifosTask);
    }

    @Override
    public void execute(long timeInMillis) throws BatchJobException {

        long taskStartTime = new DateTimeService().getCurrentDateTime().getMillis();

        List<String> errorList = new ArrayList<String>();

        unappliedHolidays = new ArrayList<HolidayBO>();
        try {
            unappliedHolidays = new HolidayPersistence().getUnAppliedHolidays();
        } catch (Exception e) {
            throw new BatchJobException(e);
        }
        if (unappliedHolidays != null && !unappliedHolidays.isEmpty()) {
            for (HolidayBO holiday : unappliedHolidays) {
                logMessage("Processing Holiday: " + holiday.getHolidayName());
                try {
                    handleHolidayApplication(holiday);
                    StaticHibernateUtil.commitTransaction();
                } catch (Exception e) {
                    StaticHibernateUtil.rollbackTransaction();
                    errorList.add(holiday.toString());
                } finally {
                    StaticHibernateUtil.closeSession();
                }
                String endHolidayMessage = "Completed Processing for Holiday: " + holiday.getHolidayName() + "  Took: "
                        + (new DateTimeService().getCurrentDateTime().getMillis() - taskStartTime);
                logMessage(endHolidayMessage);
            }
        }
        if (errorList.size() > 0) {
            throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
        }

        String finalMessage = "ApplyHolidayChanges ran in "
                + (new DateTimeService().getCurrentDateTime().getMillis() - taskStartTime);
        logMessage(finalMessage);

    }

    private void handleHolidayApplication(HolidayBO holiday) throws Exception {
        // HolidayUtils.rescheduleLoanRepaymentDates(holiday);

        rescheduleLoanRepaymentDates(holiday);

        //
        HolidayUtils.rescheduleSavingDates(holiday);
        StaticHibernateUtil.commitTransaction();

        HolidayUtils.rescheduleCustomerDates(holiday);
        StaticHibernateUtil.commitTransaction();

        // Change flag for this holiday in the database
        holiday.setHolidayChangesAppliedFlag(YesNoFlag.YES.getValue());
        new HolidayPersistence().createOrUpdate(holiday);
        StaticHibernateUtil.commitTransaction();
    }

    private void rescheduleLoanRepaymentDates(HolidayBO holiday) throws Exception {

        List<Integer> accountIds = new AccountPersistence().getListOfAccountIdsWithLoanSchedulesWithinDates(holiday
                .getHolidayFromDate(), holiday.getHolidayThruDate());

        long loanStartTime = new DateTimeService().getCurrentDateTime().getMillis();
        long rollingStartTime = loanStartTime;

        int accountCount = accountIds.size();
        logMessage("No. of Loan Accounts to Process: " + accountCount);

        int currentRecordNumber = 0;
        int outputIntervalForBatchJobs = GeneralConfig.getOutputIntervalForBatchJobs();
        int batchSize = GeneralConfig.getBatchSizeForBatchJobs();
        // int recordCommittingSize = GeneralConfig.getRecordCommittingSizeForBatchJobs();
        // jpw - hardcoded recordCommittingSize to 500 because now only accounts that need more schedules are returned
        int recordCommittingSize = 500;

        StaticHibernateUtil.getSessionTL();
        StaticHibernateUtil.startTransaction();
        for (Integer accountId : accountIds) {
            currentRecordNumber++;

            rescheduleLoanRepaymentDates(accountId, holiday.getHolidayFromDate(), holiday.getHolidayThruDate());

            if (currentRecordNumber % batchSize == 0) {
                StaticHibernateUtil.flushAndClearSession();
            }
            if (currentRecordNumber % recordCommittingSize == 0) {
                StaticHibernateUtil.commitTransaction();
                StaticHibernateUtil.getSessionTL();
                StaticHibernateUtil.startTransaction();
            }

            if (currentRecordNumber % outputIntervalForBatchJobs == 0) {
                long time = System.currentTimeMillis();
                String message = "" + currentRecordNumber + " updated, " + (accountCount - currentRecordNumber)
                        + " remaining, batch time: " + (time - rollingStartTime) + " ms";
                logMessage(message);
                rollingStartTime = time;
            }
        }
        StaticHibernateUtil.commitTransaction();
        long time = System.currentTimeMillis();
        String message = "" + currentRecordNumber + " updated, " + (accountCount - currentRecordNumber)
                + " remaining, batch time: " + (time - rollingStartTime) + " ms";
        logMessage(message);

        String finalMessage = "Loan Accounts Processed in: "
                + (new DateTimeService().getCurrentDateTime().getMillis() - rollingStartTime);
        logMessage(finalMessage);

    }

    private void rescheduleLoanRepaymentDates(final Integer accountId, final Date fromDate, final Date thruDate)
            throws RuntimeException {
        try {
            LoanBO loan = new LoanBusinessService().getAccount(accountId);
            MeetingBO loadedMeeting = loan.getLoanMeeting();
            for (AccountActionDateEntity accountActionDate : getAffectedActionDates(loan.getAccountActionDates(),
                    fromDate, thruDate)) {
                LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDate;

                Date adjustedDate = HolidayUtils.adjustDate(getCalendar(loanScheduleEntity.getActionDate()),
                        loadedMeeting).getTime();
                loanScheduleEntity.setActionDate(new java.sql.Date(adjustedDate.getTime()));
            }
        } catch (ServiceException e) {
            throw new RuntimeException("Account Id: " + accountId + " - " + e);
        } catch (MeetingException e) {
            throw new RuntimeException("Account Id: " + accountId + " - " + e);
        }
    }

    private List<AccountActionDateEntity> getAffectedActionDates(final Set<AccountActionDateEntity> accountActionDates,
            final Date fromDate, final Date thruDate) {

        List<AccountActionDateEntity> affectedAccountActionDates = new ArrayList<AccountActionDateEntity>();
        for (AccountActionDateEntity accountActionDate : accountActionDates) {

            if (accountActionDate.getActionDate().compareTo(fromDate) >= 0
                    && accountActionDate.getActionDate().compareTo(thruDate) <= 0) {
                affectedAccountActionDates.add(accountActionDate);
            }

        }
        if (affectedAccountActionDates.size() == 0) {
            throw new RuntimeException("No schedules were affected.  There should have been at least one.");
        }
        return affectedAccountActionDates;
    }

    @Override
    public boolean isTaskAllowedToRun() {
        return true;
    }

    private void logMessage(String finalMessage) {
        System.out.println(finalMessage);
        getLogger().info(finalMessage);
    }

}
