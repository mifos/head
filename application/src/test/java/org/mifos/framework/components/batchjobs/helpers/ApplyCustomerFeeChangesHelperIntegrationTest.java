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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeeChangeType;
import org.mifos.accounts.fees.util.helpers.FeeStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerAccountBOTestUtils;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerFeeScheduleEntity;
import org.mifos.customers.business.CustomerScheduleEntity;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ApplyCustomerFeeChangesHelperIntegrationTest extends MifosIntegrationTestCase {
    public ApplyCustomerFeeChangesHelperIntegrationTest() throws Exception {
        super();
    }

    CustomerBO center = null;

    CustomerBO group = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter(this.getClass().getSimpleName() + " center1", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter(this.getClass().getSimpleName() + " Group", CustomerStatus.GROUP_ACTIVE, center);
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            List<CustomerBO> customerList = new ArrayList<CustomerBO>();
            if (group != null) {
                customerList.add(group);
            }
            if (center != null) {
                customerList.add(center);
            }
            TestObjectFactory.cleanUp(customerList);
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        }
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testExecuteAmountUpdated() throws Exception {
        CustomerAccountBO customerAccount = center.getCustomerAccount();
        Set<AccountFeesEntity> accountFeeSet = customerAccount.getAccountFees();
        FeeBO trainingFee = TestObjectFactory.createPeriodicAmountFee("Training_Fee", FeeCategory.ALLCUSTOMERS, "10",
                RecurrenceType.WEEKLY, Short.valueOf("2"));
        AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(center.getCustomerAccount(), trainingFee,
                new Double("10.0"));
        accountFeeSet.add(accountPeriodicFee);
        CustomerScheduleEntity accountActionDate = (CustomerScheduleEntity) customerAccount.getAccountActionDate(Short
                .valueOf("1"));
        AccountFeesActionDetailEntity accountFeesaction = new CustomerFeeScheduleEntity(accountActionDate, trainingFee,
                accountPeriodicFee, new Money(getCurrency(), "10.0"));
        CustomerAccountBOTestUtils.setFeeAmountPaid((CustomerFeeScheduleEntity) accountFeesaction, new Money(
                getCurrency(), "0.0"));
        accountActionDate.addAccountFeesAction(accountFeesaction);
        TestObjectFactory.flushandCloseSession();

        trainingFee = (FeeBO) StaticHibernateUtil.getSessionTL().get(FeeBO.class, trainingFee.getFeeId());
        trainingFee.setUserContext(TestUtils.makeUser());
        ((AmountFeeBO) trainingFee).setFeeAmount(TestUtils.createMoney("5"));
        trainingFee.updateFeeChangeType(FeeChangeType.AMOUNT_UPDATED);
        trainingFee.save();
        TestObjectFactory.flushandCloseSession();
        ApplyCustomerFeeChangesTask task = new ApplyCustomerFeeChangesTask();
        ((ApplyCustomerFeeChangesHelper) task.getTaskHelper()).execute(System.currentTimeMillis());
        TestObjectFactory.flushandCloseSession();
        center = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, center.getCustomerId());

        CustomerScheduleEntity installment = (CustomerScheduleEntity) center.getCustomerAccount().getAccountActionDate(
                Short.valueOf("1"));

        AccountFeesActionDetailEntity accountFeesAction = installment.getAccountFeesAction(accountPeriodicFee
                .getAccountFeeId());
       Assert.assertEquals(TestUtils.createMoney(5.0), accountFeesAction.getFeeAmount());
        StaticHibernateUtil.closeSession();
        center = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, center.getCustomerId());
        group = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, group.getCustomerId());
    }

    public void testExecuteStatusUpdatedToInactive() throws Exception {
        CustomerAccountBO customerAccount = center.getCustomerAccount();
        CustomerScheduleEntity accountActionDate = (CustomerScheduleEntity) customerAccount.getAccountActionDate(Short
                .valueOf("1"));
        CustomerAccountBOTestUtils.setActionDate(accountActionDate, offSetDate(accountActionDate.getActionDate(),
                -1));
        TestObjectFactory.updateObject(center);
        TestObjectFactory.flushandCloseSession();
        center = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, center.getCustomerId());
        customerAccount = center.getCustomerAccount();
        Set<AccountFeesEntity> accountFeeSet = customerAccount.getAccountFees();
        FeeBO trainingFee = TestObjectFactory.createPeriodicAmountFee("Training_Fee", FeeCategory.ALLCUSTOMERS, "10",
                RecurrenceType.WEEKLY, Short.valueOf("2"));
        AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(center.getCustomerAccount(), trainingFee,
                new Double("10.0"));
        accountFeeSet.add(accountPeriodicFee);
        accountActionDate = (CustomerScheduleEntity) customerAccount.getAccountActionDate(Short.valueOf("2"));
        AccountFeesActionDetailEntity accountFeesaction = new CustomerFeeScheduleEntity(accountActionDate, trainingFee,
                accountPeriodicFee, new Money(getCurrency(), "10.0"));
        CustomerAccountBOTestUtils.setFeeAmountPaid((CustomerFeeScheduleEntity) accountFeesaction, new Money(
                getCurrency(), "0.0"));
        accountActionDate.addAccountFeesAction(accountFeesaction);
        TestObjectFactory.flushandCloseSession();

        trainingFee = (FeeBO) StaticHibernateUtil.getSessionTL().get(FeeBO.class, trainingFee.getFeeId());
        trainingFee.setUserContext(TestUtils.makeUserWithLocales());
        trainingFee.updateFeeChangeType(FeeChangeType.STATUS_UPDATED);
        trainingFee.updateStatus(FeeStatus.INACTIVE);

        trainingFee.update();
        TestObjectFactory.flushandCloseSession();
        ApplyCustomerFeeChangesTask applyCustomerFeeChangesTask = new ApplyCustomerFeeChangesTask();
        ((ApplyCustomerFeeChangesHelper) applyCustomerFeeChangesTask.getTaskHelper()).execute(System
                .currentTimeMillis());
        TestObjectFactory.flushandCloseSession();
        center = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, center.getCustomerId());

        AccountActionDateEntity installment = center.getCustomerAccount().getAccountActionDate(Short.valueOf("2"));

        AccountFeesActionDetailEntity accountFeesAction = ((CustomerScheduleEntity) installment)
                .getAccountFeesAction(accountPeriodicFee.getAccountFeeId());
        Assert.assertNull(accountFeesAction);
    }

    public void testExecuteStatusInactiveAndAmountUpdated() throws Exception {
        CustomerAccountBO customerAccount = center.getCustomerAccount();
        CustomerScheduleEntity accountActionDate = (CustomerScheduleEntity) customerAccount.getAccountActionDate(Short
                .valueOf("1"));
        CustomerAccountBOTestUtils.setActionDate(accountActionDate, offSetDate(accountActionDate.getActionDate(),
                -1));
        TestObjectFactory.updateObject(center);
        StaticHibernateUtil.closeSession();

        center = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, center.getCustomerId());
        customerAccount = center.getCustomerAccount();
        Set<AccountFeesEntity> accountFeeSet = customerAccount.getAccountFees();
        FeeBO trainingFee = TestObjectFactory.createPeriodicAmountFee("Training_Fee", FeeCategory.ALLCUSTOMERS, "10",
                RecurrenceType.WEEKLY, Short.valueOf("2"));
        AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(center.getCustomerAccount(), trainingFee,
                ((AmountFeeBO) trainingFee).getFeeAmount().getAmountDoubleValue());
        accountFeeSet.add(accountPeriodicFee);
        accountActionDate = (CustomerScheduleEntity) customerAccount.getAccountActionDate(Short.valueOf("2"));
        AccountFeesActionDetailEntity accountFeesaction = new CustomerFeeScheduleEntity(accountActionDate, trainingFee,
                accountPeriodicFee, new Money(getCurrency(), "10.0"));
        CustomerAccountBOTestUtils.setFeeAmountPaid((CustomerFeeScheduleEntity) accountFeesaction, new Money(
                getCurrency(), "0.0"));
        accountActionDate.addAccountFeesAction(accountFeesaction);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        trainingFee = (FeeBO) StaticHibernateUtil.getSessionTL().get(FeeBO.class, trainingFee.getFeeId());
        trainingFee.setUserContext(TestUtils.makeUserWithLocales());
        trainingFee.updateFeeChangeType(FeeChangeType.AMOUNT_AND_STATUS_UPDATED);
        ((AmountFeeBO) trainingFee).setFeeAmount(TestUtils.createMoney("5"));
        trainingFee.updateStatus(FeeStatus.INACTIVE);
        trainingFee.update();
        TestObjectFactory.flushandCloseSession();
        ApplyCustomerFeeChangesTask applyCustomerFeeChangesTask = new ApplyCustomerFeeChangesTask();
        ((ApplyCustomerFeeChangesHelper) applyCustomerFeeChangesTask.getTaskHelper()).execute(System
                .currentTimeMillis());
        TestObjectFactory.flushandCloseSession();
        center = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, center.getCustomerId());

        AccountActionDateEntity installment = center.getCustomerAccount().getAccountActionDate(Short.valueOf("2"));

        AccountFeesActionDetailEntity accountFeesAction = ((CustomerScheduleEntity) installment)
                .getAccountFeesAction(accountPeriodicFee.getAccountFeeId());
        Assert.assertNull(accountFeesAction);

        AccountFeesEntity accountFee = center.getCustomerAccount().getAccountFees(trainingFee.getFeeId());

        Assert.assertNotNull(accountFee);
       Assert.assertEquals(TestUtils.createMoney(5.0), accountFee.getAccountFeeAmount());
    }

    public void testExecuteStatusInactiveToActive() throws Exception {
        CustomerAccountBO customerAccount = center.getCustomerAccount();
        Set<AccountFeesEntity> accountFeeSet = customerAccount.getAccountFees();
        FeeBO trainingFee = TestObjectFactory.createPeriodicAmountFee("Training_Fee", FeeCategory.ALLCUSTOMERS, "10",
                RecurrenceType.WEEKLY, Short.valueOf("2"));
        AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(center.getCustomerAccount(), trainingFee,
                ((AmountFeeBO) trainingFee).getFeeAmount().getAmountDoubleValue());
        accountPeriodicFee.setFeeStatus(FeeStatus.INACTIVE);
        accountFeeSet.add(accountPeriodicFee);
        trainingFee.updateStatus(FeeStatus.INACTIVE);
        trainingFee.setUserContext(TestUtils.makeUserWithLocales());
        trainingFee.update();
        TestObjectFactory.flushandCloseSession();

        trainingFee = (FeeBO) StaticHibernateUtil.getSessionTL().get(FeeBO.class, trainingFee.getFeeId());
        trainingFee.setUserContext(TestUtils.makeUserWithLocales());
        trainingFee.updateFeeChangeType(FeeChangeType.STATUS_UPDATED);
        trainingFee.updateStatus(FeeStatus.ACTIVE);
        trainingFee.update();
        TestObjectFactory.flushandCloseSession();
        ApplyCustomerFeeChangesTask applyCustomerFeeChangesTask = new ApplyCustomerFeeChangesTask();
        ((ApplyCustomerFeeChangesHelper) applyCustomerFeeChangesTask.getTaskHelper()).execute(System
                .currentTimeMillis());
        TestObjectFactory.flushandCloseSession();
        center = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, center.getCustomerId());

        AccountActionDateEntity installment = center.getCustomerAccount().getAccountActionDate(Short.valueOf("1"));

        AccountFeesActionDetailEntity accountFeesAction = ((CustomerScheduleEntity) installment)
                .getAccountFeesAction(accountPeriodicFee.getAccountFeeId());
        Assert.assertNotNull(accountFeesAction);
       Assert.assertEquals(2, ((CustomerScheduleEntity) installment).getAccountFeesActionDetails().size());
    }

    public void testExecuteStatusInactiveToActiveAndAmountChanged() throws Exception {
        CustomerAccountBO customerAccount = center.getCustomerAccount();
        Set<AccountFeesEntity> accountFeeSet = customerAccount.getAccountFees();
        FeeBO trainingFee = TestObjectFactory.createPeriodicAmountFee("Training_Fee", FeeCategory.ALLCUSTOMERS, "10",
                RecurrenceType.WEEKLY, Short.valueOf("2"));
        AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(center.getCustomerAccount(), trainingFee,
                ((AmountFeeBO) trainingFee).getFeeAmount().getAmountDoubleValue());
        accountPeriodicFee.setFeeStatus(FeeStatus.INACTIVE);
        accountFeeSet.add(accountPeriodicFee);
        trainingFee.updateStatus(FeeStatus.INACTIVE);
        trainingFee.setUserContext(TestUtils.makeUserWithLocales());
        trainingFee.update();
        TestObjectFactory.flushandCloseSession();

        trainingFee = (FeeBO) StaticHibernateUtil.getSessionTL().get(FeeBO.class, trainingFee.getFeeId());
        trainingFee.setUserContext(TestUtils.makeUserWithLocales());
        trainingFee.updateFeeChangeType(FeeChangeType.AMOUNT_AND_STATUS_UPDATED);
        trainingFee.updateStatus(FeeStatus.ACTIVE);
        ((AmountFeeBO) trainingFee).setFeeAmount(TestUtils.createMoney("5"));
        trainingFee.update();
        TestObjectFactory.flushandCloseSession();
        new ApplyCustomerFeeChangesHelper(new ApplyCustomerFeeChangesTask()).execute(System.currentTimeMillis());
        TestObjectFactory.flushandCloseSession();
        center = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, center.getCustomerId());

        AccountActionDateEntity installment = center.getCustomerAccount().getAccountActionDate(Short.valueOf("1"));

        AccountFeesActionDetailEntity accountFeesAction = ((CustomerScheduleEntity) installment)
                .getAccountFeesAction(accountPeriodicFee.getAccountFeeId());
        Assert.assertNotNull(accountFeesAction);
       Assert.assertEquals(TestUtils.createMoney(5.0), accountFeesAction.getFeeAmount());
       Assert.assertEquals(2, ((CustomerScheduleEntity) installment).getAccountFeesActionDetails().size());
        AccountFeesEntity accountFee = center.getCustomerAccount().getAccountFees(trainingFee.getFeeId());
        Assert.assertNotNull(accountFee);
       Assert.assertEquals(TestUtils.createMoney(5.0), accountFee.getAccountFeeAmount());
    }

    public void testExecuteAmountUpdatedForMultipleAccount() throws Exception {
        CustomerAccountBO centerAccount = center.getCustomerAccount();
        CustomerAccountBO groupAccount = group.getCustomerAccount();
        Set<AccountFeesEntity> accountFeeSet = centerAccount.getAccountFees();
        Set<AccountFeesEntity> groupFeeSet = groupAccount.getAccountFees();

        FeeBO trainingFee = TestObjectFactory.createPeriodicAmountFee("Training_Fee", FeeCategory.ALLCUSTOMERS, "10",
                RecurrenceType.WEEKLY, Short.valueOf("2"));

        AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(center.getCustomerAccount(), trainingFee,
                ((AmountFeeBO) trainingFee).getFeeAmount().getAmountDoubleValue());

        AccountFeesEntity groupaccountPeriodicFee = new AccountFeesEntity(group.getCustomerAccount(), trainingFee,
                ((AmountFeeBO) trainingFee).getFeeAmount().getAmountDoubleValue());
        accountFeeSet.add(accountPeriodicFee);
        groupFeeSet.add(groupaccountPeriodicFee);
        CustomerScheduleEntity accountActionDate = (CustomerScheduleEntity) centerAccount.getAccountActionDate(Short
                .valueOf("1"));
        AccountFeesActionDetailEntity accountFeesaction = new CustomerFeeScheduleEntity(accountActionDate, trainingFee,
                accountPeriodicFee, new Money(getCurrency(), "10.0"));
        CustomerAccountBOTestUtils.setFeeAmountPaid((CustomerFeeScheduleEntity) accountFeesaction, new Money(
                getCurrency(), "0.0"));
        accountActionDate.addAccountFeesAction(accountFeesaction);
        CustomerScheduleEntity groupaccountActionDate = (CustomerScheduleEntity) groupAccount
                .getAccountActionDate(Short.valueOf("1"));
        AccountFeesActionDetailEntity groupaccountFeesaction = new CustomerFeeScheduleEntity(groupaccountActionDate,
                trainingFee, groupaccountPeriodicFee, new Money(getCurrency(), "10.0"));
        CustomerAccountBOTestUtils.setFeeAmountPaid((CustomerFeeScheduleEntity) groupaccountFeesaction,
                new Money(getCurrency(), "0.0"));
        groupaccountActionDate.addAccountFeesAction(groupaccountFeesaction);

        TestObjectFactory.flushandCloseSession();

        trainingFee = (FeeBO) StaticHibernateUtil.getSessionTL().get(FeeBO.class, trainingFee.getFeeId());
        trainingFee.setUserContext(TestUtils.makeUser());
        ((AmountFeeBO) trainingFee).setFeeAmount(TestUtils.createMoney("5"));
        trainingFee.updateFeeChangeType(FeeChangeType.AMOUNT_UPDATED);
        trainingFee.save();
        TestObjectFactory.flushandCloseSession();
        new ApplyCustomerFeeChangesHelper(new ApplyCustomerFeeChangesTask()).execute(System.currentTimeMillis());
        TestObjectFactory.flushandCloseSession();
        center = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, center.getCustomerId());
        group = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, group.getCustomerId());
        AccountActionDateEntity installment = center.getCustomerAccount().getAccountActionDate(Short.valueOf("1"));

        AccountFeesActionDetailEntity accountFeesAction = ((CustomerScheduleEntity) installment)
                .getAccountFeesAction(accountPeriodicFee.getAccountFeeId());
       Assert.assertEquals(TestUtils.createMoney(5.0), accountFeesAction.getFeeAmount());

        AccountActionDateEntity groupinstallment = group.getCustomerAccount().getAccountActionDate(Short.valueOf("1"));

        AccountFeesActionDetailEntity groupaccountFeesAction = ((CustomerScheduleEntity) groupinstallment)
                .getAccountFeesAction(groupaccountPeriodicFee.getAccountFeeId());
       Assert.assertEquals(TestUtils.createMoney(5.0), groupaccountFeesAction.getFeeAmount());
    }

    public void testBatchJobException() {
        List<String> error = new ArrayList<String>();
        error.add("error1");
        error.add("error2");
        BatchJobException batchJobException = new BatchJobException("error.invailddata", error);
       Assert.assertEquals("error.invailddata", batchJobException.getKey());
       Assert.assertEquals("error1,error2", batchJobException.getErrorMessage());
    }

    private java.sql.Date offSetDate(Date date, int noOfDays) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar = new GregorianCalendar(year, month, day + noOfDays);
        return new java.sql.Date(calendar.getTimeInMillis());
    }

}
