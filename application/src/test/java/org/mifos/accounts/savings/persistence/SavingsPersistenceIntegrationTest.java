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

package org.mifos.accounts.savings.persistence;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.business.AccountTestUtils;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.savings.SavingBOTestUtils;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.config.business.Configuration;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.checklist.business.AccountCheckListBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;

public class SavingsPersistenceIntegrationTest extends MifosIntegrationTestCase {

    private UserContext userContext;
    private SavingsPersistence savingsPersistence;
    @Autowired
    private LegacyAccountDao legacyAccountDao;
    private CustomerBO group;
    private CustomerBO center;
    private SavingsBO savings;
    private SavingsBO savings1;
    private SavingsBO savings2;
    private SavingsOfferingBO savingsOffering;
    private SavingsOfferingBO savingsOffering2;
    @SuppressWarnings("unused")
    private AccountCheckListBO accountCheckList;

    @Autowired
    private SavingsDao savingsDao;

    @Before
    public void setUp() throws Exception {
        savingsPersistence = new SavingsPersistence();
        userContext = TestUtils.makeUser();

    }

    @After
    public void tearDown() throws Exception {
        savings = null;
        if (savings1 != null) {
            savings1 = null;
        }
        if (savings2 != null) {
            savings2 = null;
            savingsOffering2 = null;
        }

        group = null;
        center = null;
        accountCheckList = null;
        TestObjectFactory.removeObject(savingsOffering2);
        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testFindById() throws Exception {
        createInitialObjects();
        Date currentDate = new Date(System.currentTimeMillis());
        savingsOffering = TestObjectFactory.createSavingsProduct("SavingPrd1", "xdsa", currentDate,
                RecommendedAmountUnit.COMPLETE_GROUP);
        savings = createSavingsAccount("FFFF", savingsOffering);
        SavingsBO savings1 = savingsDao.findById(savings.getAccountId());
        Assert.assertEquals(savingsOffering.getRecommendedAmount(), savings1.getRecommendedAmount());
    }

    @Test
    public void testGetAccountStatus() throws Exception {
        AccountStateEntity accountState = savingsPersistence.getAccountStatusObject(AccountStates.SAVINGS_ACC_CLOSED);
        Assert.assertNotNull(accountState);
        Assert.assertEquals(accountState.getId().shortValue(), AccountStates.SAVINGS_ACC_CLOSED);
    }

    @Test
    public void testRetrieveAllAccountStateList() throws NumberFormatException, PersistenceException {
        List<AccountStateEntity> accountStateEntityList = legacyAccountDao.retrieveAllAccountStateList(Short
                .valueOf("2"));
        Assert.assertNotNull(accountStateEntityList);
        Assert.assertEquals(6, accountStateEntityList.size());
    }

    @Test
    public void testRetrieveAllActiveAccountStateList() throws NumberFormatException, PersistenceException {
        List<AccountStateEntity> accountStateEntityList = legacyAccountDao.retrieveAllActiveAccountStateList(Short
                .valueOf("2"));
        Assert.assertNotNull(accountStateEntityList);
        Assert.assertEquals(6, accountStateEntityList.size());
    }

    @Test
    public void testGetStatusChecklist() throws Exception {
        accountCheckList = TestObjectFactory.createAccountChecklist(AccountTypes.SAVINGS_ACCOUNT.getValue(),
                AccountState.SAVINGS_PARTIAL_APPLICATION, Short.valueOf("1"));
        List<AccountCheckListBO> statusCheckList = legacyAccountDao.getStatusChecklist(Short.valueOf("13"), AccountTypes.SAVINGS_ACCOUNT
                .getValue());
        Assert.assertNotNull(statusCheckList);

        Assert.assertEquals(1, statusCheckList.size());
    }

    @Test
    public void testFindBySystemId() throws Exception {
        createInitialObjects();
        Date currentDate = new Date(System.currentTimeMillis());
        savingsOffering = TestObjectFactory.createSavingsProduct("SavingPrd1", "v1ws", currentDate,
                RecommendedAmountUnit.COMPLETE_GROUP);
        savings = createSavingsAccount("kkk", savingsOffering);
        SavingsBO savings1 = savingsDao.findBySystemId(savings.getGlobalAccountNum());
        Assert.assertEquals(savings.getAccountId(), savings1.getAccountId());
        Assert.assertEquals(savingsOffering.getRecommendedAmount(), savings1.getRecommendedAmount());
    }

    @Test
    public void testRetrieveLastTransaction() throws Exception {
        try {
            SavingsTestHelper helper = new SavingsTestHelper();
            createInitialObjects();
            PersonnelBO createdBy = legacyPersonnelDao.getPersonnel(userContext.getId());
            savingsOffering = helper.createSavingsOffering("effwe", "231");
            savings = new SavingsBO(userContext, savingsOffering, group, AccountState.SAVINGS_ACTIVE, savingsOffering
                    .getRecommendedAmount(), null);

            AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, new Money(Configuration
                    .getInstance().getSystemConfig().getCurrency(), "700.0"), new Money(Configuration.getInstance()
                    .getSystemConfig().getCurrency(), "1700.0"), helper.getDate("15/01/2006"),
                    AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
            AccountTestUtils.addAccountPayment(payment, savings);
            savings.save();
            StaticHibernateUtil.flushSession();

            payment = helper.createAccountPaymentToPersist(savings, new Money(Configuration.getInstance()
                    .getSystemConfig().getCurrency(), "1000.0"), new Money(Configuration.getInstance()
                    .getSystemConfig().getCurrency(), "2700.0"), helper.getDate("20/02/2006"),
                    AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
            AccountTestUtils.addAccountPayment(payment, savings);
            savings.update();
            StaticHibernateUtil.flushSession();

            savings = savingsDao.findById(savings.getAccountId());
            savings.setUserContext(userContext);
            payment = helper.createAccountPaymentToPersist(savings, new Money(Configuration.getInstance()
                    .getSystemConfig().getCurrency(), "500.0"), new Money(Configuration.getInstance().getSystemConfig()
                    .getCurrency(), "2200.0"), helper.getDate("10/03/2006"), AccountActionTypes.SAVINGS_WITHDRAWAL
                    .getValue(), savings, createdBy, group);
            AccountTestUtils.addAccountPayment(payment, savings);
            savings.update();
            StaticHibernateUtil.flushSession();

            savings = savingsDao.findById(savings.getAccountId());
            savings.setUserContext(userContext);
            payment = helper.createAccountPaymentToPersist(savings, new Money(Configuration.getInstance()
                    .getSystemConfig().getCurrency(), "1200.0"), new Money(Configuration.getInstance()
                    .getSystemConfig().getCurrency(), "3400.0"), helper.getDate("15/03/2006"),
                    AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
            AccountTestUtils.addAccountPayment(payment, savings);
            savings.update();
            StaticHibernateUtil.flushSession();

            savings = savingsDao.findById(savings.getAccountId());
            savings.setUserContext(userContext);
            payment = helper.createAccountPaymentToPersist(savings, new Money(Configuration.getInstance()
                    .getSystemConfig().getCurrency(), "2500.0"), new Money(Configuration.getInstance()
                    .getSystemConfig().getCurrency(), "900.0"), helper.getDate("25/03/2006"),
                    AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(), savings, createdBy, group);
            AccountTestUtils.addAccountPayment(payment, savings);
            savings.update();
            StaticHibernateUtil.flushSession();

            savings = savingsDao.findById(savings.getAccountId());
            savings.setUserContext(userContext);
            SavingsTrxnDetailEntity trxn = savingsPersistence.retrieveLastTransaction(savings.getAccountId(), helper
                    .getDate("12/03/2006"));
            Assert.assertEquals(TestUtils.createMoney("500"), trxn.getAmount());
            group = savings.getCustomer();
            center = group.getParentCustomer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetMissedDeposits() throws Exception {
        SavingsTestHelper helper = new SavingsTestHelper();
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        savingsOffering = helper.createSavingsOffering("SavingPrd1", "wsed", Short.valueOf("1"), Short.valueOf("1"));
        savings = TestObjectFactory.createSavingsAccount("43245434", group, Short.valueOf("16"), new Date(System
                .currentTimeMillis()), savingsOffering);

        AccountActionDateEntity accountActionDateEntity = savings.getAccountActionDate((short) 1);
        SavingBOTestUtils.setActionDate(accountActionDateEntity, offSetCurrentDate(7));

        savings.update();
        StaticHibernateUtil.flushSession();

        savings = savingsDao.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        StaticHibernateUtil.flushSession();
        Calendar currentDateCalendar = new GregorianCalendar();
        java.sql.Date currentDate = new java.sql.Date(currentDateCalendar.getTimeInMillis());

        Assert.assertEquals(savingsPersistence.getMissedDeposits(savings.getAccountId(), currentDate), 1);
    }

    @Test
    public void testGetMissedDepositsPaidAfterDueDate() throws Exception {
        SavingsTestHelper helper = new SavingsTestHelper();
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        savingsOffering = helper.createSavingsOffering("SavingPrd1", "cvfg", Short.valueOf("1"), Short.valueOf("1"));
        ;
        savings = TestObjectFactory.createSavingsAccount("43245434", group, Short.valueOf("16"), new Date(System
                .currentTimeMillis()), savingsOffering);

        AccountActionDateEntity accountActionDateEntity = savings.getAccountActionDate((short) 1);
        SavingBOTestUtils.setActionDate(accountActionDateEntity, offSetCurrentDate(7));
        accountActionDateEntity.setPaymentStatus(PaymentStatus.PAID);
        Calendar currentDateCalendar = new GregorianCalendar();
        java.sql.Date currentDate = new java.sql.Date(currentDateCalendar.getTimeInMillis());

        SavingBOTestUtils.setPaymentDate(accountActionDateEntity, currentDate);
        savings.update();
        StaticHibernateUtil.flushSession();
        savings = savingsDao.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        StaticHibernateUtil.flushSession();
        Assert.assertEquals(savingsPersistence.getMissedDepositsPaidAfterDueDate(savings.getAccountId()), 1);
    }

    private void createInitialObjects() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);

    }

    private SavingsBO createSavingsAccount(String globalAccountNum, SavingsOfferingBO savingsOffering)
            throws NumberFormatException, Exception {
        UserContext userContext = new UserContext();
        userContext.setId(PersonnelConstants.SYSTEM_USER);
        userContext.setBranchGlobalNum("1001");
        return TestObjectFactory.createSavingsAccount(globalAccountNum, group, AccountState.SAVINGS_PENDING_APPROVAL,
                new Date(), savingsOffering, userContext);
    }

    private java.sql.Date offSetCurrentDate(int noOfDays) {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day - noOfDays);
        return new java.sql.Date(currentDateCalendar.getTimeInMillis());
    }
}
