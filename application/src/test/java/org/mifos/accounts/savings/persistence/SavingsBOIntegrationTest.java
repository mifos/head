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

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.hibernate.Session;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountActionEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountCustomFieldEntity;
import org.mifos.accounts.business.AccountNotesEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.business.AccountStateFlagEntity;
import org.mifos.accounts.business.AccountStateMachines;
import org.mifos.accounts.business.AccountTestUtils;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.accounts.savings.business.SavingsActivityEntity;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.accounts.util.helpers.SavingsPaymentData;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryInstallmentDto;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.config.AccountingRules;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.config.business.Configuration;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.screen.SavingsRecentActivityDto;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;

public class SavingsBOIntegrationTest extends MifosIntegrationTestCase {

    private UserContext userContext;
    private CustomerBO group;
    private CustomerBO center;
    private CustomerBO client1;
    @SuppressWarnings("unused")
    private CustomerBO client2;
    private SavingsBO savings;
    private SavingsOfferingBO savingsOffering;
    private final SavingsTestHelper helper = new SavingsTestHelper();
    @Autowired
    private LegacyAccountDao legacyAccountDao;
    private MifosCurrency currency = null;
    private PersonnelBO createdBy = null;

    @Autowired
    private SavingsDao savingsDao;

    private Money getRoundedMoney(final Double value) {
        return MoneyUtils.currencyRound(new Money(TestUtils.RUPEE, value.toString()));
    }

    @Before
    public void setUp() throws Exception {
        enableCustomWorkingDays();
        userContext = TestUtils.makeUser();
        createdBy = legacyPersonnelDao.getPersonnel(userContext.getId());
        currency = Configuration.getInstance().getSystemConfig().getCurrency();
    }

    private void createInitialObjects() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE,
                center);
    }

    private SavingsBO createSavingsAccountPayment() throws Exception {
        AccountPaymentEntity payment;
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("2333dsf", "2132");
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);

        Money initialBal = new Money(currency, "5500");
        payment = helper.createAccountPayment(savings, null, new Money(currency, "5500.0"), new Date(), createdBy);

        SavingsTrxnDetailEntity adjustment = SavingsTrxnDetailEntity.savingsAdjustment(payment, group, initialBal, initialBal, createdBy,
                helper.getDate("04/01/2006"), helper.getDate("04/01/2006"), new DateTime().toDate(), "", null);
        payment.addAccountTrxn(adjustment);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.update();


        return TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
    }

    @Test
    public void testSavingsTrxnDetailsWithZeroAmt() throws Exception {
        savings = createSavingsAccountPayment();
        for (AccountPaymentEntity accountPaymentEntity : savings.getAccountPayments()) {
            for (AccountTrxnEntity accountTrxnEntity : accountPaymentEntity.getAccountTrxns()) {
                SavingsTrxnDetailEntity trxnEntity = (SavingsTrxnDetailEntity) accountTrxnEntity;
                Assert.assertEquals(new Money(getCurrency(), "5500"), trxnEntity.getBalance());
                Assert.assertEquals(new Money(getCurrency()), trxnEntity.getDepositAmount());
                Assert.assertEquals(new Money(getCurrency()), trxnEntity.getInterestAmount());
                Assert.assertEquals(new Money(getCurrency()), trxnEntity.getWithdrawlAmount());
                break;
            }
        }
    }

    private List<CustomFieldDto> getCustomFieldView() {
        List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();
        customFields.add(new CustomFieldDto(new Short("8"), "13", null));
        return customFields;

    }

    private void verifyFields() throws Exception {
        Assert.assertTrue(true);
        Assert.assertEquals(savings.getInterestRate(), savingsOffering.getInterestRate());
        Assert.assertEquals(savings.getMinAmntForInt(), savingsOffering.getMinAmntForInt());
        Assert.assertEquals(savings.getMinAmntForInt(), savingsOffering.getMinAmntForInt());
        Assert.assertEquals(savings.getInterestCalculationMeeting().getMeetingDetails().getRecurAfter(), savingsOffering
                .getTimePerForInstcalc().getMeeting().getMeetingDetails().getRecurAfter());
        Assert.assertEquals(1, savings.getAccountCustomFields().size());
        Iterator<AccountCustomFieldEntity> itr = savings.getAccountCustomFields().iterator();
        AccountCustomFieldEntity customFieldEntity = itr.next();
        Assert.assertEquals(Short.valueOf("8"), customFieldEntity.getFieldId());
        Assert.assertEquals("13", customFieldEntity.getFieldValue());
        Assert.assertEquals(AccountTypes.SAVINGS_ACCOUNT, savings.getType());
        Assert.assertEquals(group.getPersonnel().getPersonnelId(), savings.getPersonnel().getPersonnelId());
    }

    @Test
    public void testIsTrxnDateValid_AfterFirstMeeting() throws Exception {
        createInitialObjects();
        savingsOffering = TestObjectFactory.createSavingsProduct("dfasdasd1", "sad1",
                RecommendedAmountUnit.COMPLETE_GROUP);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);


        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        int i = -5;
        for (AccountActionDateEntity actionDate : savings.getAccountActionDates()) {
            ((SavingsScheduleEntity) actionDate).setActionDate(offSetCurrentDate(i--));
        }
        savings.update();


        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        java.util.Date trxnDate = offSetCurrentDate(-5);
        Date meetingDate = new CustomerPersistence().getLastMeetingDateForCustomer(savings.getCustomer().getCustomerId());
        boolean repaymentIndependentOfMeetingEnabled = new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();
        if (AccountingRules.isBackDatedTxnAllowed()) {
            Assert.assertTrue(savings.isTrxnDateValid(trxnDate, meetingDate, repaymentIndependentOfMeetingEnabled));
        } else {
            Assert.assertFalse(savings.isTrxnDateValid(trxnDate, meetingDate, repaymentIndependentOfMeetingEnabled));
        }
        group = TestObjectFactory.getGroup(group.getCustomerId());
    }

    @Test
    public void testSuccessfulSave() throws Exception {
        createInitialObjects();
        savingsOffering = TestObjectFactory.createSavingsProduct("dfasdasd1", "sad1",
                RecommendedAmountUnit.PER_INDIVIDUAL);
        savings = new SavingsBO(userContext, savingsOffering, group, AccountState.SAVINGS_PENDING_APPROVAL, new Money(
                getCurrency(), "100"), getCustomFieldView());
        savings.save();
        StaticHibernateUtil.flushSession();;

        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        verifyFields();
        Assert.assertEquals(AccountState.SAVINGS_PENDING_APPROVAL.getValue(), savings.getAccountState().getId());
        Assert.assertEquals(TestUtils.createMoney(100.0), savings.getRecommendedAmount());
    }

    @Test
    public void testSuccessfulSaveInApprovedState() throws Exception {
        center = helper.createCenter();
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center);
        client1 = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_CLOSED, group);
        client2 = TestObjectFactory.createClient("client2", CustomerStatus.CLIENT_ACTIVE, group);
        savingsOffering = TestObjectFactory.createSavingsProduct("dfasdasd2", "sad2",
                RecommendedAmountUnit.PER_INDIVIDUAL);
        savings = new SavingsBO(userContext, savingsOffering, group, AccountState.SAVINGS_ACTIVE,
                savingsOffering.getRecommendedAmount(), getCustomFieldView());
        savings.save();
        StaticHibernateUtil.flushSession();;

        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        verifyFields();
        Assert.assertEquals(AccountState.SAVINGS_ACTIVE.getValue(), savings.getAccountState().getId());
        Assert.assertEquals(savingsOffering.getRecommendedAmount(), savings.getRecommendedAmount());

    }

    @Test
    public void testIsMandatory() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        Assert.assertEquals(Boolean.valueOf(false).booleanValue(), savings.isMandatory());
    }

    @Test
    public void testIsDepositScheduleBeRegenerated() throws Exception {
        createInitialObjects();
        savingsOffering = TestObjectFactory.createSavingsProduct("dfasdasd1", "sad1",
                RecommendedAmountUnit.PER_INDIVIDUAL);
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        Assert.assertEquals(Boolean.valueOf(false).booleanValue(), savings.isMandatory());
    }

    @Test
    public void testGenerateAndUpdateDepositActionsForClient() throws Exception {
        center = helper.createCenter();
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center);
        savingsOffering = TestObjectFactory.createSavingsProduct("dfasdasd1", "sad1",
                RecommendedAmountUnit.PER_INDIVIDUAL);

        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group, AccountState.SAVINGS_ACTIVE,
                userContext);

        group = (GroupBO) StaticHibernateUtil.getSessionTL().get(GroupBO.class, group.getCustomerId());
        // This calls savings.generateAndUpdateDepositActionsForClient
        // which is what this test is all about testing.
        client1 = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);

        savings = savingsDao.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        group = savings.getCustomer();
        center = group.getParentCustomer();
        Assert.assertEquals(10, savings.getAccountActionDates().size());

        savings = savingsDao.findById(savings.getAccountId());
        client1 = IntegrationTestObjectMother.findCustomerById(client1.getCustomerId());
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    @Test
    public void testSuccessfulWithdraw() throws AccountException, Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client1 = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = TestObjectFactory.createSavingsAccount("43245434", client1, Short.valueOf("16"),
                new Date(System.currentTimeMillis()), savingsOffering);


        savings = (SavingsBO) legacyAccountDao.getAccount(savings.getAccountId());
        savings.setSavingsBalance(TestUtils.createMoney("100.0"));
        Money enteredAmount = new Money(currency, "100.0");
        PaymentData paymentData = PaymentData.createPaymentData(enteredAmount, savings.getPersonnel(),
                Short.valueOf("1"), new Date(System.currentTimeMillis()));
        paymentData.setCustomer(client1);
        paymentData.setReceiptDate(new Date(System.currentTimeMillis()));
        paymentData.setReceiptNum("34244");
        boolean persist = true;
        savings.withdraw(paymentData, persist);
        Assert.assertEquals(TestUtils.createMoney(), savings.getSavingsBalance());
        Assert.assertEquals(1, savings.getSavingsActivityDetails().size());
        savings.getAccountPayments().clear();
    }

    @Test
    public void testSuccessfulApplyPayment() throws AccountException, Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client1 = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = TestObjectFactory.createSavingsAccount("43245434", client1, AccountStates.SAVINGS_ACC_INACTIVE,
                new Date(System.currentTimeMillis()), savingsOffering);

        savings = (SavingsBO) legacyAccountDao.getAccount(savings.getAccountId());
        savings.setSavingsBalance(new Money(getCurrency()));

        Money enteredAmount = new Money(currency, "100.0");
        PaymentData paymentData = PaymentData.createPaymentData(enteredAmount, savings.getPersonnel(),
                Short.valueOf("1"), new Date(System.currentTimeMillis()));
        paymentData.setCustomer(client1);
        paymentData.setReceiptDate(new Date(System.currentTimeMillis()));
        paymentData.setReceiptNum("34244");
        AccountActionDateEntity accountActionDate = savings.getAccountActionDate(Short.valueOf("1"));

        SavingsPaymentData savingsPaymentData = new SavingsPaymentData(accountActionDate);
        paymentData.addAccountPaymentData(savingsPaymentData);
        IntegrationTestObjectMother.applyAccountPayment(savings, paymentData);

        Assert.assertEquals(AccountStates.SAVINGS_ACC_APPROVED, savings.getAccountState().getId().shortValue());
        Assert.assertEquals(TestUtils.createMoney(100.0), savings.getSavingsBalance());
        Assert.assertEquals(1, savings.getSavingsActivityDetails().size());
        savings.getAccountPayments().clear();
        client1 = IntegrationTestObjectMother.findCustomerById(client1.getCustomerId());
    }

    @Test
    public void testSuccessfulDepositForCenterAccount() throws AccountException, Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client1 = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = TestObjectFactory.createSavingsAccount("43245434", center, AccountStates.SAVINGS_ACC_APPROVED,
                new Date(System.currentTimeMillis()), savingsOffering);
        savings.setSavingsBalance(new Money(getCurrency()));

        savings = (SavingsBO) legacyAccountDao.getAccount(savings.getAccountId());

        Money enteredAmount = new Money(currency, "100.0");
        PaymentData paymentData = PaymentData.createPaymentData(enteredAmount, savings.getPersonnel(),
                Short.valueOf("1"), new Date(System.currentTimeMillis()));
        paymentData.setCustomer(client1);
        paymentData.setReceiptDate(new Date(System.currentTimeMillis()));
        paymentData.setReceiptNum("34244");
        AccountActionDateEntity accountActionDate = savings.getAccountActionDate(Short.valueOf("1"));

        SavingsPaymentData savingsPaymentData = new SavingsPaymentData(accountActionDate);
        paymentData.addAccountPaymentData(savingsPaymentData);
        IntegrationTestObjectMother.applyAccountPayment(savings, paymentData);

        savings = (SavingsBO) legacyAccountDao.getAccount(savings.getAccountId());
        Assert.assertEquals(AccountStates.SAVINGS_ACC_APPROVED, savings.getAccountState().getId().shortValue());
        Assert.assertEquals(TestUtils.createMoney(100.0), savings.getSavingsBalance());
        Assert.assertEquals(1, savings.getSavingsActivityDetails().size());
        List<AccountPaymentEntity> payments = savings.getAccountPayments();
        Assert.assertEquals(1, payments.size());
        for (AccountPaymentEntity payment : payments) {
            Assert.assertEquals(1, payment.getAccountTrxns().size());
            for (AccountTrxnEntity accountTrxn : payment.getAccountTrxns()) {
                SavingsTrxnDetailEntity trxn = (SavingsTrxnDetailEntity) accountTrxn;
                Assert.assertEquals(enteredAmount, trxn.getBalance());
                Assert.assertEquals(client1.getCustomerId(), trxn.getCustomer().getCustomerId());
            }
        }
        client1 = IntegrationTestObjectMother.findCustomerById(client1.getCustomerId());
    }

    @Test
    public void testSuccessfulApplyPaymentWhenNoDepositDue() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = helper.createSavingsAccount("000X00000000013", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        savings.setUserContext(TestObjectFactory.getContext());
        PersonnelBO loggedInUser = IntegrationTestObjectMother.testUser();
        savings.changeStatus(AccountState.SAVINGS_CANCELLED, null, "", loggedInUser);

        StaticHibernateUtil.getSessionTL().clear();

        savings = savingsDao.findById(savings.getAccountId());
        Money enteredAmount = new Money(currency, "100.0");
        PaymentData paymentData = PaymentData.createPaymentData(enteredAmount, savings.getPersonnel(),
                Short.valueOf("1"), new Date(System.currentTimeMillis()));
        paymentData.setCustomer(group);
        paymentData.setReceiptDate(new Date(System.currentTimeMillis()));
        paymentData.setReceiptNum("34244");
        paymentData.addAccountPaymentData(getSavingsPaymentdata(null));
        IntegrationTestObjectMother.applyAccountPayment(savings, paymentData);

        savings = savingsDao.findById(savings.getAccountId());

        Assert.assertEquals(AccountStates.SAVINGS_ACC_APPROVED, savings.getAccountState().getId().shortValue());
        Assert.assertEquals(TestUtils.createMoney(100.0), savings.getSavingsBalance());
        Assert.assertEquals(1, savings.getSavingsActivityDetails().size());
    }

    private SavingsPaymentData getSavingsPaymentdata(final CollectionSheetEntryInstallmentDto bulkEntryAccountActionView) {
        return new SavingsPaymentData(bulkEntryAccountActionView);
    }

    @Test
    public void testMaxWithdrawAmount() throws AccountException, Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client1 = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = TestObjectFactory.createSavingsAccount("43245434", client1, Short.valueOf("16"),
                new Date(System.currentTimeMillis()), savingsOffering);


        savings = (SavingsBO) legacyAccountDao.getAccount(savings.getAccountId());
        savings.setSavingsBalance(TestUtils.createMoney("100.0"));
        Money enteredAmount = new Money(currency, "300.0");
        PaymentData paymentData = PaymentData.createPaymentData(enteredAmount, savings.getPersonnel(),
                Short.valueOf("1"), new Date(System.currentTimeMillis()));
        paymentData.setCustomer(client1);
        paymentData.setReceiptDate(new Date(System.currentTimeMillis()));
        paymentData.setReceiptNum("34244");
        try {
            boolean persist = true;
            savings.withdraw(paymentData, persist);
            Assert.assertTrue(
                    "No Exception is thrown. Even amount greater than max withdrawal allowed to be withdrawn", false);
        } catch (AccountException ae) {
            Assert.assertTrue("Exception is thrown. Amount greater than max withdrawal not allowed to be withdrawn",
                    true);
        }
        savings.getAccountPayments().clear();
    }

    @Test
    public void testSuccessfulFlagSave() throws Exception {
        Session session = StaticHibernateUtil.getSessionTL();
        StaticHibernateUtil.startTransaction();
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = helper.createSavingsAccount("000X00000000013", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        savings.setUserContext(TestObjectFactory.getContext());
        PersonnelBO loggedInUser = IntegrationTestObjectMother.testUser();
        savings.changeStatus(AccountState.SAVINGS_CANCELLED, null, "", loggedInUser);

        savings.setUserContext(this.userContext);
        AccountStateEntity state = (AccountStateEntity) session.get(AccountStateEntity.class, (short) 15);
        for (AccountStateFlagEntity flag : state.getFlagSet()) {
            AccountTestUtils.addAccountFlag(flag, savings);
        }
        savings.update();


        session = StaticHibernateUtil.getSessionTL();
        SavingsBO savingsNew = (SavingsBO) (session.get(SavingsBO.class, Integer.valueOf(savings.getAccountId())));
        Assert.assertEquals(savingsNew.getAccountFlags().size(), 3);
        session.evict(savingsNew);

    }

    @Test
    public void testChangeStatusPermissionToPendingApprovalSucess() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_PARTIALAPPLICATION, userContext);
        AccountStateMachines.getInstance().initialize(AccountTypes.SAVINGS_ACCOUNT, null);
        PersonnelBO loggedInUser = IntegrationTestObjectMother.testUser();
        savings.changeStatus(AccountState.SAVINGS_PENDING_APPROVAL, null, "notes", loggedInUser);
        Assert.assertEquals(AccountStates.SAVINGS_ACC_PENDINGAPPROVAL, savings.getAccountState().getId().shortValue());

    }

    @Test
    public void testChangeStatusPermissionToCancelBlacklistedSucess() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_PENDINGAPPROVAL, userContext);
        AccountStateMachines.getInstance().initialize(AccountTypes.SAVINGS_ACCOUNT, null);
        // 6 is blacklisted

        PersonnelBO loggedInUser = IntegrationTestObjectMother.testUser();
        savings.changeStatus(AccountState.SAVINGS_CANCELLED, Short.valueOf("6"), "notes", loggedInUser);
        Assert.assertEquals(AccountStates.SAVINGS_ACC_CANCEL, savings.getAccountState().getId().shortValue());

    }

    @Test
    public void testGetOverDueDepositAmountForMandatoryAccounts() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
                EVERY_WEEK, CUSTOMER_MEETING));
        MeetingBO meetingIntPost = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
                EVERY_WEEK, CUSTOMER_MEETING));
        SavingsOfferingBO savingsOffering = TestObjectFactory.createSavingsProduct("SavingPrd1", ApplicableTo.GROUPS,
                new Date(System.currentTimeMillis()), PrdStatus.SAVINGS_ACTIVE, 300.0,
                RecommendedAmountUnit.PER_INDIVIDUAL, 1.2, 200.0, 200.0, SavingsType.MANDATORY,
                InterestCalcType.MINIMUM_BALANCE, meetingIntCalc, meetingIntPost);
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client1 = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
        savings = TestObjectFactory.createSavingsAccount("43245434", client1, Short.valueOf("16"),
                new Date(System.currentTimeMillis()), savingsOffering);
        Session session = StaticHibernateUtil.getSessionTL();
        StaticHibernateUtil.startTransaction();
        SavingsScheduleEntity accountActionDate = (SavingsScheduleEntity) savings.getAccountActionDate((short) 1);
        accountActionDate.setDepositPaid(TestUtils.createMoney("100.0"));
        session.update(savings);
        StaticHibernateUtil.flushSession();

        accountActionDate = (SavingsScheduleEntity) savings.getAccountActionDate((short) 3);
        Assert.assertEquals(getRoundedMoney(300.00), savings.getOverDueDepositAmount(accountActionDate.getActionDate()));

    }

    @Test
    public void testGetOverDueDepositAmountForVoluntaryAccounts() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        SavingsOfferingBO savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client1 = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
        savings = TestObjectFactory.createSavingsAccount("43245434", client1, Short.valueOf("16"),
                new Date(System.currentTimeMillis()), savingsOffering);
        Session session = StaticHibernateUtil.getSessionTL();
        StaticHibernateUtil.startTransaction();
        SavingsScheduleEntity accountActionDate = (SavingsScheduleEntity) savings.getAccountActionDate((short) 1);
        accountActionDate.setDepositPaid(TestUtils.createMoney("100.0"));
        session.update(savings);
        StaticHibernateUtil.flushSession();

        Assert.assertEquals(getRoundedMoney(0.00), savings.getOverDueDepositAmount(accountActionDate.getActionDate()));

    }

    @Test
    public void testGetRecentAccountActivity() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);

        SavingsActivityEntity savingsActivity = new SavingsActivityEntity(savings.getPersonnel(),
                (AccountActionEntity) StaticHibernateUtil.getSessionTL().get(AccountActionEntity.class,
                        Short.valueOf("1")), new Money(getCurrency(), "100"), new Money(getCurrency(), "22"),
                new Date(), savings);

        savings.addSavingsActivityDetails(savingsActivity);

        savings.update();
        StaticHibernateUtil.flushAndClearSession();

        savings = savingsDao.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        Assert.assertEquals(1, savings.getRecentAccountActivity(3).size());
        for (SavingsRecentActivityDto view : savings.getRecentAccountActivity(3)) {
            Assert.assertNotNull(view.getActivity());
            Assert.assertEquals(new Money(getCurrency(), "100.0"), new Money(getCurrency(), view.getAmount()));

            Assert.assertEquals(new Money(getCurrency(), "22.0"), new Money(getCurrency(), view.getRunningBalance()));
            Assert.assertNotNull(view.getAccountTrxnId());
            Assert.assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(),
                    DateUtils.getDateWithoutTimeStamp(view.getActionDate().getTime()));
            Assert.assertNull(view.getLocale());
            break;
        }
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    // For the MifosTestCases for amount in arrears, we are creating a savings
    // account with deposit due of 200.0 for each installment. So all the values
    // and asserts are based on that.
    //
    // So, if 1 installments is not paid, then amount in arrears will be 200.0
    // and the total amount due will be 400.0 (which includes amount in arrears
    // +
    // amount to be paid for next installment)

    @Test
    public void testGetTotalAmountInArrearsForCurrentDateMeeting() throws Exception {
        savings = getSavingsAccount();
        Assert.assertEquals(savings.getTotalAmountInArrears(), getRoundedMoney(0.0));
    }

    @Test
    public void testGetTotalAmountInArrearsForSingleInstallmentDue() throws Exception {
        savings = getSavingsAccount();
        AccountActionDateEntity accountActionDateEntity = savings.getAccountActionDate((short) 1);
        ((SavingsScheduleEntity) accountActionDateEntity).setActionDate(offSetCurrentDate(1));
        savings = (SavingsBO) saveAndFetch(savings);
        Assert.assertEquals(TestUtils.createMoney(200.0), savings.getTotalAmountInArrears());
    }

    @Test
    public void testGetTotalAmountInArrearsWithPartialPayment() throws Exception {
        savings = getSavingsAccount();
        SavingsScheduleEntity accountActionDateEntity = (SavingsScheduleEntity) savings.getAccountActionDate((short) 1);
        accountActionDateEntity.setDepositPaid(new Money(getCurrency(), "20.0"));
        accountActionDateEntity.setActionDate(offSetCurrentDate(1));

        savings = (SavingsBO) saveAndFetch(savings);
        Assert.assertEquals(TestUtils.createMoney(180.0), savings.getTotalAmountInArrears());
    }

    @Test
    public void testGetTotalAmountInArrearsWithPaymentDone() throws Exception {
        savings = getSavingsAccount();

        AccountActionDateEntity accountActionDateEntity = savings.getAccountActionDate(Short.valueOf("1"));
        ((SavingsScheduleEntity) accountActionDateEntity).setActionDate(offSetCurrentDate(1));
        accountActionDateEntity.setPaymentStatus(PaymentStatus.PAID);
        savings = (SavingsBO) saveAndFetch(savings);
        Assert.assertEquals(savings.getTotalAmountInArrears(), TestUtils.createMoney());
    }

    @Test
    public void testGetTotalAmountDueForTwoInstallmentsDue() throws Exception {
        savings = getSavingsAccount();

        AccountActionDateEntity accountActionDateEntity = savings.getAccountActionDate((short) 1);
        ((SavingsScheduleEntity) accountActionDateEntity).setActionDate(offSetCurrentDate(2));
        AccountActionDateEntity accountActionDateEntity2 = savings.getAccountActionDate((short) 2);
        ((SavingsScheduleEntity) accountActionDateEntity2).setActionDate(offSetCurrentDate(1));

        savings = (SavingsBO) saveAndFetch(savings);

        Assert.assertEquals(savings.getTotalAmountInArrears(), TestUtils.createMoney(400.0));
    }

    @Test
    public void testGetTotalAmountDueForCurrentDateMeeting() throws Exception {
        savings = getSavingsAccount();
        Assert.assertEquals(savings.getTotalAmountDue(), TestUtils.createMoney(200.0));
    }

    @Test
    public void testGetTotalAmountDueForSingleInstallment() throws Exception {
        savings = getSavingsAccount();

        AccountActionDateEntity accountActionDateEntity = savings.getAccountActionDate((short) 1);
        ((SavingsScheduleEntity) accountActionDateEntity).setActionDate(offSetCurrentDate(1));

        savings = (SavingsBO) saveAndFetch(savings);
        Assert.assertEquals(savings.getTotalAmountDue(), TestUtils.createMoney(400.0));
    }

    @Test
    public void testGetTotalAmountDueWithPartialPayment() throws Exception {
        savings = getSavingsAccount();

        SavingsScheduleEntity accountActionDateEntity = (SavingsScheduleEntity) savings.getAccountActionDate((short) 1);

        accountActionDateEntity.setDepositPaid(new Money(getCurrency(), "20.0"));
        accountActionDateEntity.setActionDate(offSetCurrentDate(1));
        savings = (SavingsBO) saveAndFetch(savings);
        Assert.assertEquals(savings.getTotalAmountDue(), TestUtils.createMoney(380.0));
    }

    @Test
    public void testGetTotalAmountDueWithPaymentDone() throws Exception {
        savings = getSavingsAccount();

        AccountActionDateEntity accountActionDateEntity = savings.getAccountActionDate((short) 1);
        ((SavingsScheduleEntity) accountActionDateEntity).setActionDate(offSetCurrentDate(1));
        accountActionDateEntity.setPaymentStatus(PaymentStatus.PAID);

        savings = (SavingsBO) saveAndFetch(savings);

        Assert.assertEquals(savings.getTotalAmountDue(), TestUtils.createMoney(200.0));
    }

    @Test
    public void testGetTotalAmountDueForTwoInstallments() throws Exception {
        savings = getSavingsAccount();
        AccountActionDateEntity accountActionDateEntity = savings.getAccountActionDate((short) 1);
        ((SavingsScheduleEntity) accountActionDateEntity).setActionDate(offSetCurrentDate(2));
        AccountActionDateEntity accountActionDateEntity2 = savings.getAccountActionDate((short) 2);

        ((SavingsScheduleEntity) accountActionDateEntity2).setActionDate(offSetCurrentDate(1));

        savings = (SavingsBO) saveAndFetch(savings);

        Assert.assertEquals(savings.getTotalAmountDue(), TestUtils.createMoney(600.0));
    }

    @Test
    public void testGetTotalAmountDueForCenter() throws Exception {
        savings = getSavingsAccountForCenter();
        Money dueAmount = new Money(getCurrency());
        for (AccountActionDateEntity actionDate : savings.getAccountActionDates()) {
            dueAmount = dueAmount.add(((SavingsScheduleEntity) actionDate).getDeposit());
            break;
        }
        dueAmount = dueAmount.add(dueAmount);
        Assert.assertEquals(dueAmount, savings.getTotalAmountDue());
    }

    @Test
    public void testGetTotalAmountDueForNextInstallmentForCurrentDateMeeting() throws Exception {
        savings = getSavingsAccount();
        Assert.assertEquals(savings.getTotalAmountDueForNextInstallment(), TestUtils.createMoney(200.0));
    }

    @Test
    public void testGetTotalAmountDueForNextInstallmentWithPartialPayment() throws Exception {
        savings = getSavingsAccount();
        SavingsScheduleEntity accountActionDateEntity = (SavingsScheduleEntity) savings.getAccountActionDate((short) 1);
        accountActionDateEntity.setDepositPaid(new Money(getCurrency(), "20.0"));

        savings = (SavingsBO) saveAndFetch(savings);
        Assert.assertEquals(savings.getTotalAmountDueForNextInstallment(), getRoundedMoney(180.0));
    }

    @Test
    public void testGetTotalAmountDueForNextInstallmentPaymentDone() throws Exception {
        savings = getSavingsAccount();

        AccountActionDateEntity accountActionDateEntity = savings.getAccountActionDate(Short.valueOf("1"));
        accountActionDateEntity.setPaymentStatus(PaymentStatus.PAID);
        savings = (SavingsBO) saveAndFetch(savings);
        Assert.assertEquals(savings.getTotalAmountDueForNextInstallment(), TestUtils.createMoney());
    }

    @Test
    public void testGetDetailsOfInstallmentsInArrearsForCurrentDateMeeting() throws Exception {
        savings = getSavingsAccount();
        Assert.assertEquals(savings.getDetailsOfInstallmentsInArrears().size(), 0);
    }

    @Test
    public void testGetDetailsOfInstallmentsInArrearsForSingleInstallmentDue() throws Exception {
        savings = getSavingsAccount();
        AccountActionDateEntity accountActionDateEntity = savings.getAccountActionDate((short) 1);
        ((SavingsScheduleEntity) accountActionDateEntity).setActionDate(offSetCurrentDate(1));
        savings = (SavingsBO) saveAndFetch(savings);
        Assert.assertEquals(savings.getDetailsOfInstallmentsInArrears().size(), 1);
    }

    @Test
    public void testGetDetailsOfInstallmentsInArrearsWithPaymentDone() throws Exception {
        savings = getSavingsAccount();

        AccountActionDateEntity accountActionDateEntity = savings.getAccountActionDate(Short.valueOf("1"));
        ((SavingsScheduleEntity) accountActionDateEntity).setActionDate(offSetCurrentDate(1));
        accountActionDateEntity.setPaymentStatus(PaymentStatus.PAID);
        savings = (SavingsBO) saveAndFetch(savings);
        Assert.assertEquals(savings.getDetailsOfInstallmentsInArrears().size(), 0);
    }

    @Test
    public void testGetDetailsOfInstallmentsInArrearsForTwoInstallmentsDue() throws Exception {
        savings = getSavingsAccount();

        AccountActionDateEntity accountActionDateEntity = savings.getAccountActionDate((short) 1);
        ((SavingsScheduleEntity) accountActionDateEntity).setActionDate(offSetCurrentDate(2));
        AccountActionDateEntity accountActionDateEntity2 = savings.getAccountActionDate((short) 2);
        ((SavingsScheduleEntity) accountActionDateEntity2).setActionDate(offSetCurrentDate(1));

        savings = (SavingsBO) saveAndFetch(savings);

        Assert.assertEquals(savings.getDetailsOfInstallmentsInArrears().size(), 2);
    }

    private void createCustomerObjects() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE,
                center);
        client1 = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        client2 = TestObjectFactory.createClient("client2", CustomerStatus.CLIENT_ACTIVE, group);
    }

    private SavingsBO getSavingsAccount() throws Exception {
        createCustomerObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        return TestObjectFactory.createSavingsAccount("000100000000017", client1, AccountStates.SAVINGS_ACC_APPROVED,
                new Date(System.currentTimeMillis()), savingsOffering);
    }

    private AccountBO saveAndFetch(final AccountBO account) throws Exception {
        TestObjectFactory.updateObject(account);
        return legacyAccountDao.getAccount(account.getAccountId());
    }

    private java.sql.Date offSetCurrentDate(final int noOfDays) {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day - noOfDays);
        return new java.sql.Date(currentDateCalendar.getTimeInMillis());
    }

    @Test
    public void testGetTotalPaymentDueForVol() throws Exception {
        createObjectToCheckForTotalInstallmentDue(SavingsType.VOLUNTARY);
        savings = savingsDao.findById(savings.getAccountId());
        Money recommendedAmnt = new Money(currency, "500.0");
        Money paymentDue = savings.getTotalPaymentDue(group.getCustomerId());
        Assert.assertEquals(recommendedAmnt, paymentDue);
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    @Test
    public void testGetTotalInstallmentDueForVol() throws Exception {
        createObjectToCheckForTotalInstallmentDue(SavingsType.VOLUNTARY);
        StaticHibernateUtil.flushAndClearSession();
        savings = savingsDao.findById(savings.getAccountId());
        List<AccountActionDateEntity> dueInstallment = savings.getTotalInstallmentsDue(group.getCustomerId());
        Assert.assertEquals(1, dueInstallment.size());
        Assert.assertEquals(Short.valueOf("2"), dueInstallment.get(0).getInstallmentId());
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    @Test
    public void testGetTotalPaymentDueForMan() throws Exception {
        createObjectToCheckForTotalInstallmentDue(SavingsType.MANDATORY);
        StaticHibernateUtil.flushAndClearSession();
        savings = savingsDao.findById(savings.getAccountId());
        Money amount = new Money(currency, "1000.0");
        Money paymentDue = savings.getTotalPaymentDue(group.getCustomerId());
        Assert.assertEquals(amount, paymentDue);
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    @Test
    public void testGetTotalInstallmentDueForMan() throws Exception {
        createObjectToCheckForTotalInstallmentDue(SavingsType.MANDATORY);
        StaticHibernateUtil.flushAndClearSession();
        savings = savingsDao.findById(savings.getAccountId());
        List<AccountActionDateEntity> dueInstallment = savings.getTotalInstallmentsDue(group.getCustomerId());
        Assert.assertEquals(2, dueInstallment.size());
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    private void createObjectToCheckForTotalInstallmentDue(final SavingsType savingsType) throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("prd1df", InterestCalcType.MINIMUM_BALANCE, savingsType);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_PENDING_APPROVAL,
                userContext);
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal2.setLenient(true);
        cal2.set(Calendar.DAY_OF_MONTH, cal1.get(Calendar.DAY_OF_MONTH) - 1);

        Date paymentDate = helper.getDate("09/05/2006");
        Money recommendedAmnt = new Money(currency, "500.0");
        AccountActionDateEntity actionDate1 = helper.createAccountActionDate(savings, Short.valueOf("1"),
                cal2.getTime(), paymentDate, savings.getCustomer(), recommendedAmnt, new Money(getCurrency()),
                PaymentStatus.UNPAID);
        AccountActionDateEntity actionDate2 = helper.createAccountActionDate(savings, Short.valueOf("2"), new Date(),
                paymentDate, savings.getCustomer(), recommendedAmnt, new Money(getCurrency()), PaymentStatus.UNPAID);
        AccountTestUtils.addAccountActionDate(actionDate1, savings);
        AccountTestUtils.addAccountActionDate(actionDate2, savings);
        savings.update();


    }

    private SavingsBO getSavingsAccountForCenter() throws Exception {
        createInitialObjects();
        client1 = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        client2 = TestObjectFactory.createClient("client2", CustomerStatus.CLIENT_ACTIVE, group);
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        return helper.createSavingsAccount(savingsOffering, center, AccountState.SAVINGS_ACTIVE, userContext);
    }

    @Test
    public void testGetRecentAccountNotes() throws Exception {
        savings = getSavingsAccountForCenter();
        addNotes("note1");
        addNotes("note2");
        addNotes("note3");
        addNotes("note4");
        addNotes("note5");
        StaticHibernateUtil.flushAndClearSession();

        savings = savingsDao.findById(savings.getAccountId());
        List<AccountNotesEntity> notes = savings.getRecentAccountNotes();

        Assert.assertEquals("Size of recent notes is 3", 3, notes.size());
        for (AccountNotesEntity accountNotesEntity : notes) {
            Assert.assertEquals("Last note added is note5", "note5", accountNotesEntity.getComment());
            break;
        }

    }

    @Test
    public void testGenerateMeetingForNextYear() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));

        center = TestObjectFactory.createWeeklyFeeCenter("center1", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);

        SavingsTestHelper SavingsTestHelper = new SavingsTestHelper();

        SavingsOfferingBO savingsOfferingBO = SavingsTestHelper.createSavingsOffering("dfasdasd1", "sad1");
        savingsOfferingBO.setRecommendedAmntUnit(RecommendedAmountUnit.COMPLETE_GROUP);
        SavingsBO savingsBO = SavingsTestHelper.createSavingsAccount(savingsOfferingBO, group,
                AccountState.SAVINGS_ACTIVE, TestUtils.makeUser());

        Short LastInstallmentId = savingsBO.getLastInstallmentId();
        AccountActionDateEntity lastYearLastInstallment = savingsBO.getAccountActionDate(LastInstallmentId);

        Integer installmetId = lastYearLastInstallment.getInstallmentId().intValue() + (short) 1;

        List<Days> workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
        List<Holiday> holidays = new ArrayList<Holiday>();

        savingsBO.generateNextSetOfMeetingDates(workingDays, holidays);

        TestObjectFactory.updateObject(savingsBO);
        TestObjectFactory.updateObject(center);
        TestObjectFactory.updateObject(group);
        TestObjectFactory.updateObject(savingsBO);

        center = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, center.getCustomerId());
        group = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, group.getCustomerId());
        savingsBO = (SavingsBO) StaticHibernateUtil.getSessionTL().get(SavingsBO.class, savingsBO.getAccountId());

        MeetingBO meetingBO = center.getCustomerMeeting().getMeeting();
        meetingBO.setMeetingStartDate(lastYearLastInstallment.getActionDate());
        List<Date> meetingDates = TestObjectFactory.getMeetingDates(group.getOfficeId(), meetingBO, 10);
        meetingDates.remove(0);
        Date FirstSavingInstallmetDate = savingsBO.getAccountActionDate(installmetId.shortValue()).getActionDate();
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(meetingDates.get(0));
        Calendar calendar3 = Calendar.getInstance();
        calendar3.setTime(FirstSavingInstallmetDate);
        Assert.assertEquals(0, new GregorianCalendar(calendar3.get(Calendar.YEAR), calendar3.get(Calendar.MONTH),
                calendar3.get(Calendar.DATE), 0, 0, 0).compareTo(new GregorianCalendar(calendar2.get(Calendar.YEAR),
                calendar2.get(Calendar.MONTH), calendar2.get(Calendar.DATE), 0, 0, 0)));
    }

    private void addNotes(final String comment) throws Exception {
        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
        PersonnelBO personnelBO = legacyPersonnelDao.getPersonnel(userContext.getId());
        AccountNotesEntity accountNotesEntity = new AccountNotesEntity(currentDate, comment, personnelBO, savings);
        savings.addAccountNotes(accountNotesEntity);
        savings.update();
        StaticHibernateUtil.flushAndClearSession();
    }
}
