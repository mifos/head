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

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_SECOND_WEEK;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.hibernate.Query;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.customer.business.CustomerAccountBOTestUtils;
import org.mifos.application.customer.business.CustomerActivityEntity;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerScheduleEntity;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.business.Task;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CustomerFeeHelperIntegrationTest extends MifosIntegrationTestCase {

    public CustomerFeeHelperIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private CustomerBO center;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        TestObjectFactory.cleanUp(center);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testExecute() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("center1_Active_test", meeting);

        for (AccountActionDateEntity accountActionDateEntity : center.getCustomerAccount().getAccountActionDates()) {
            CustomerAccountBOTestUtils.setActionDate(accountActionDateEntity, offSetDate(accountActionDateEntity
                    .getActionDate(), 1));
        }

        meeting = center.getCustomerMeeting().getMeeting();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(offSetDate(new Date(System.currentTimeMillis()), 1));
        meeting.setMeetingStartDate(calendar.getTime());
        meeting.getMeetingDetails().getMeetingRecurrence().setWeekDay(
                new WeekDaysEntity(WeekDay
                        .getWeekDay(Short.valueOf(String.valueOf(calendar.get(Calendar.DAY_OF_WEEK))))));

        Set<AccountFeesEntity> accountFeeSet = center.getCustomerAccount().getAccountFees();
        FeeBO trainingFee = TestObjectFactory.createPeriodicAmountFee("Training_Fee", FeeCategory.ALLCUSTOMERS, "100",
                RecurrenceType.WEEKLY, Short.valueOf("2"));
        AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(center.getCustomerAccount(), trainingFee,
                ((AmountFeeBO) trainingFee).getFeeAmount().getAmountDoubleValue());
        accountFeeSet.add(accountPeriodicFee);
        Date lastAppliedFeeDate = offSetDate(new Date(System.currentTimeMillis()), 1);
       Assert.assertEquals(2, accountFeeSet.size());
        for (Iterator iter = accountFeeSet.iterator(); iter.hasNext();) {
            AccountFeesEntity accountFeesEntity = (AccountFeesEntity) iter.next();
            accountFeesEntity.setLastAppliedDate(offSetDate(new Date(System.currentTimeMillis()), 1));
        }
        TestObjectFactory.updateObject(center);
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        ApplyCustomerFeeTask applyCustomerFeeTask = new ApplyCustomerFeeTask();
        ApplyCustomerFeeHelper customerFeeHelper = (ApplyCustomerFeeHelper) applyCustomerFeeTask.getTaskHelper();
        customerFeeHelper.execute(System.currentTimeMillis());
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());

        Set<AccountFeesEntity> periodicFeeSet = center.getCustomerAccount().getAccountFees();
        for (AccountFeesEntity periodicFees : periodicFeeSet) {
            if (periodicFees.getFees().getFeeName().equalsIgnoreCase("Training_Fee"))
               Assert.assertEquals(lastAppliedFeeDate, DateUtils.getDateWithoutTimeStamp(periodicFees.getLastAppliedDate()
                        .getTime()));
            else {
               Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(offSetDate(lastAppliedFeeDate, -7).getTime()), DateUtils
                        .getDateWithoutTimeStamp(periodicFees.getLastAppliedDate().getTime()));
            }
        }
    }

    public void testExecuteToApplyPeriodicFee() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
                EVERY_SECOND_WEEK, CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("center1_Active_test", meeting);
        for (AccountActionDateEntity accountActionDateEntity : center.getCustomerAccount().getAccountActionDates()) {
            CustomerAccountBOTestUtils.setActionDate(accountActionDateEntity, offSetDate(accountActionDateEntity
                    .getActionDate(), 1));
        }
        meeting = center.getCustomerMeeting().getMeeting();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(offSetDate(new Date(System.currentTimeMillis()), 1));
        meeting.setMeetingStartDate(calendar.getTime());
        meeting.getMeetingDetails().getMeetingRecurrence().setWeekDay(
                new WeekDaysEntity(WeekDay
                        .getWeekDay(Short.valueOf(String.valueOf(calendar.get(Calendar.DAY_OF_WEEK))))));

        Set<AccountFeesEntity> accountFeeSet = center.getCustomerAccount().getAccountFees();
        FeeBO trainingFee = TestObjectFactory.createPeriodicAmountFee("Training_Fee", FeeCategory.LOAN, "100",
                RecurrenceType.WEEKLY, Short.valueOf("1"));
        AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(center.getCustomerAccount(), trainingFee,
                ((AmountFeeBO) trainingFee).getFeeAmount().getAmountDoubleValue());
        accountPeriodicFee.setLastAppliedDate(offSetDate(new Date(System.currentTimeMillis()), 1));
        accountFeeSet.add(accountPeriodicFee);

       Assert.assertEquals(2, accountFeeSet.size());
        TestObjectFactory.updateObject(center);
        TestObjectFactory.flushandCloseSession();

        center = TestObjectFactory.getCenter(center.getCustomerId());
        ApplyCustomerFeeHelper customerFeeHelper = new ApplyCustomerFeeHelper(new ApplyCustomerFeeTask());
        customerFeeHelper.execute(System.currentTimeMillis());
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        Date lastAppliedFeeDate = null;
        for (AccountActionDateEntity accountActionDateEntity : center.getCustomerAccount().getAccountActionDates()) {
            CustomerScheduleEntity customerScheduleEntity = (CustomerScheduleEntity) accountActionDateEntity;
            if (customerScheduleEntity.getInstallmentId().equals(Short.valueOf("2"))) {
                lastAppliedFeeDate = customerScheduleEntity.getActionDate();
               Assert.assertEquals(2, customerScheduleEntity.getAccountFeesActionDetails().size());
                for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : customerScheduleEntity
                        .getAccountFeesActionDetails()) {
                    if (accountFeesActionDetailEntity.getFee().getFeeName().equalsIgnoreCase("Training_Fee")) {
                       Assert.assertEquals(new Money(getCurrency(), "200.0"), accountFeesActionDetailEntity.getFeeAmount());
                    } else if (accountFeesActionDetailEntity.getFee().getFeeName().equalsIgnoreCase("Maintenance Fee")) {
                       Assert.assertEquals(new Money(getCurrency(), "200.0"), accountFeesActionDetailEntity.getFeeAmount());
                    }
                }
            }
        }
        for (CustomerActivityEntity customerActivityEntity : center.getCustomerAccount().getCustomerActivitDetails()) {
           Assert.assertEquals(new Money(getCurrency(), "200.0"), customerActivityEntity.getAmount());
        }
        Set<AccountFeesEntity> periodicFeeSet = center.getCustomerAccount().getAccountFees();
        for (AccountFeesEntity periodicFees : periodicFeeSet) {
            if (periodicFees.getFees().getFeeName().equalsIgnoreCase("Training_Fee"))
               Assert.assertEquals(lastAppliedFeeDate, DateUtils.getDateWithoutTimeStamp(periodicFees.getLastAppliedDate()
                        .getTime()));
        }
    }

    public void testExecuteTask() throws PersistenceException, BatchJobException {
        ApplyCustomerFeeTask applyCustomerFeeTask = new ApplyCustomerFeeTask();
        applyCustomerFeeTask.name = "ApplyCustomerFeeTask";
        ApplyCustomerFeeHelper applyCustomerFeeHelper = (ApplyCustomerFeeHelper) applyCustomerFeeTask.getTaskHelper();
        applyCustomerFeeHelper.executeTask();

        Query query = StaticHibernateUtil.getSessionTL().createQuery("from " + Task.class.getName());
        List<Task> tasks = query.list();
        Assert.assertNotNull(tasks);
       Assert.assertEquals(1, tasks.size());
        for (Task task : tasks) {
           Assert.assertEquals(TaskStatus.COMPLETE.getValue().shortValue(), task.getStatus());
           Assert.assertEquals("ApplyCustomerFeeTask", task.getTask());
           Assert.assertEquals(SchedulerConstants.FINISHED_SUCCESSFULLY, task.getDescription());
            TestObjectFactory.removeObject(task);
        }
    }

    public void testExecuteTaskAndForceException() throws PersistenceException, BatchJobException {
        ApplyCustomerFeeTask applyCustomerFeeTask = new ApplyCustomerFeeTask();
        applyCustomerFeeTask.name = "ApplyCustomerFeeTask";
        ApplyCustomerFeeHelper applyCustomerFeeHelper = (ApplyCustomerFeeHelper) applyCustomerFeeTask.getTaskHelper();
        AccountPersistence accountPersistenceMock = createMock(AccountPersistence.class);
        expect(accountPersistenceMock.getAccountsWithYesterdaysInstallment()).andThrow(
                new PersistenceException("mock exception"));
        replay(accountPersistenceMock);
        applyCustomerFeeHelper.setAccountPersistence(accountPersistenceMock);

        applyCustomerFeeHelper.executeTask();

        Query query = StaticHibernateUtil.getSessionTL().createQuery("from " + Task.class.getName());
        List<Task> tasks = query.list();
        Assert.assertNotNull(tasks);
       Assert.assertEquals(1, tasks.size());
        for (Task task : tasks) {
           Assert.assertEquals(TaskStatus.FAILED.getValue().shortValue(), task.getStatus());
           Assert.assertEquals("ApplyCustomerFeeTask", task.getTask());
            TestObjectFactory.removeObject(task);
        }
    }

    public void testExecuteFailure() {
        ApplyCustomerFeeTask applyCustomerFeeTask = new ApplyCustomerFeeTask();
        applyCustomerFeeTask.name = "ApplyCustomerFeeTask";
        ApplyCustomerFeeHelper applyCustomerFeeHelper = new ApplyCustomerFeeHelper(applyCustomerFeeTask);
        TestObjectFactory.simulateInvalidConnection();
        applyCustomerFeeHelper.executeTask();
        StaticHibernateUtil.closeSession();

        Query query = StaticHibernateUtil.getSessionTL().createQuery("from " + Task.class.getName());
        List<Task> tasks = query.list();
       Assert.assertEquals(0, tasks.size());

    }

    private java.sql.Date offSetDate(Date date, int noOfDays) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar = new GregorianCalendar(year, month, day - noOfDays);
        return new java.sql.Date(calendar.getTimeInMillis());
    }

}
