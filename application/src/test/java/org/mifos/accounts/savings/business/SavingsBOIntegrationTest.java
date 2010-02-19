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

package org.mifos.accounts.savings.business;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.MeetingType.SAVINGS_INTEREST_CALCULATION_TIME_PERIOD;
import static org.mifos.application.meeting.util.helpers.MeetingType.SAVINGS_INTEREST_POSTING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.DAILY;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.MONTHLY;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_DAY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_MONTH;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.joda.time.Days;
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
import org.mifos.accounts.financial.business.FinancialTransactionBO;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.productdefinition.business.InterestCalcTypeEntity;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsTypeEntity;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.accounts.savings.persistence.SavingsPersistence;
import org.mifos.accounts.savings.util.helpers.SavingsHelper;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.accounts.util.helpers.SavingsPaymentData;
import org.mifos.accounts.util.helpers.WaiveEnum;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryInstallmentView;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.customers.util.helpers.CustomerStatusFlag;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.config.AccountingRules;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class SavingsBOIntegrationTest extends MifosIntegrationTestCase {
    public SavingsBOIntegrationTest() throws Exception {
        super();
    }

    private UserContext userContext;

    private CustomerBO group;

    private CustomerBO center;

    private CustomerBO client1;

    private CustomerBO client2;

    private SavingsBO savings;

    private SavingsOfferingBO savingsOffering;

    private final SavingsTestHelper helper = new SavingsTestHelper();

    private final SavingsPersistence savingsPersistence = new SavingsPersistence();

    private final AccountPersistence accountPersistence = new AccountPersistence();

    private final CustomerPersistence customerPersistence = new CustomerPersistence();

    private final MifosCurrency currency = Configuration.getInstance().getSystemConfig().getCurrency();

    private PersonnelBO createdBy = null;
    
    private List<Days> workingDays = FiscalCalendarRules.getWorkingDaysAsJodaTimeDays();
    private List<Holiday> holidays = new ArrayList<Holiday>();
    
    private Money getRoundedMoney(final Money value) {
        return MoneyUtils.currencyRound(value);
    }
    
    private Money getRoundedMoney(final Double value) {
        return MoneyUtils.currencyRound(new Money(TestUtils.RUPEE, value.toString()));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userContext = TestUtils.makeUser();
        createdBy = new PersonnelPersistence().getPersonnel(userContext.getId());
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUp(savings);
            TestObjectFactory.cleanUp(client1);
            TestObjectFactory.cleanUp(client2);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        }
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testInterestAdjustmentOnLaterWithdrawal() throws Exception {
        createInitialObjects();
        savingsOffering = createSavingsOfferingForIntCalc("prd1", "cfgh", SavingsType.MANDATORY,
                InterestCalcType.MINIMUM_BALANCE, MONTHLY, EVERY_MONTH);
        savingsOffering.setInterestRate(8.0);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);
        savings.setActivationDate(helper.getDate("25/09/2008"));
        savings.setNextIntCalcDate(helper.getDate("30/09/2008"));

        Money depositMoney = new Money(currency, "2000.0");
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, depositMoney, depositMoney, helper
                .getDate("25/09/2008"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.setSavingsBalance(depositMoney);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        Money balanceAmount = new Money(currency, "2000.0");
       Assert.assertEquals(balanceAmount, savings.getSavingsBalance());
        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        // Min Bal from 25/09 - 30/9 = 2000
        // Interest 2000*.12*5/365 = 2.2
       Assert.assertEquals(TestUtils.createMoney(2.2), savings.getInterestToBePosted());
        savings.postInterest();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        Money recommendedAmnt = new Money(currency, "1000.0");
        balanceAmount = new Money(currency, "1002.2");

        AccountPaymentEntity payment2 = helper.createAccountPayment(savings, null, recommendedAmnt, helper
                .getDate("26/10/2008"), createdBy);
        payment2.addAccountTrxn(helper.createAccountTrxn(payment2, null, recommendedAmnt, balanceAmount, helper
                .getDate("26/10/2008"), null, null, AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(), savings,
                createdBy, group));
        AccountTestUtils.addAccountPayment(payment2, savings);
        savings.setSavingsBalance(balanceAmount);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
       Assert.assertEquals(balanceAmount, savings.getSavingsBalance());

        Money amountAdjustedTo = new Money(getCurrency(), "0");
        savings.setUserContext(userContext);
        try {
            savings.adjustLastUserAction(amountAdjustedTo, "correction entry");
        } catch (ApplicationException ae) {
           Assert.assertTrue(false);
        }
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
       Assert.assertEquals(new Money(currency, "2002.2"), savings.getSavingsBalance());
        balanceAmount = new Money(currency, "1502.2");
        recommendedAmnt = new Money(currency, "500.0");
        AccountPaymentEntity payment3 = helper.createAccountPayment(savings, null, recommendedAmnt, helper
                .getDate("26/10/2008"), createdBy);
        payment3.addAccountTrxn(helper.createAccountTrxn(payment3, null, recommendedAmnt, balanceAmount, helper
                .getDate("26/10/2008"), null, null, AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(), savings,
                createdBy, group));
        AccountTestUtils.addAccountPayment(payment3, savings);
        savings.setSavingsBalance(balanceAmount);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
       Assert.assertEquals(new Money(currency, "1502.2"), savings.getSavingsBalance());

        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        // Min Bal from 30/09 - 31/10 = 1502.2
        // Interest 1502.2*.8*31/365 = 10.3
       Assert.assertEquals(TestUtils.createMoney(10.3), savings.getInterestToBePosted());
        savings.postInterest();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        // Min Bal from 31/10 - 30/11 = 1512.5
        // Interest 1512.5*.8*30/365 = 10.0
       Assert.assertEquals(TestUtils.createMoney(10.0), savings.getInterestToBePosted());
        savings.postInterest();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        // Min Bal from 30/11 - 31/12 = 1522.5
        // Interest1522.5*.8*31/365 = 10.4
       Assert.assertEquals(TestUtils.createMoney(10.4), savings.getInterestToBePosted());
        savings.postInterest();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    /**
     * FIXME: notice the hardcoded dates in the year 2030, below. This is likely
     * a time bomb, but perhaps not as much of a problem as when we reach the
     * year 2038.
     */
    public void testInterestAdjustmentOnLaterDeposit() throws Exception {
        createInitialObjects();
        savingsOffering = createSavingsOfferingForIntCalc("prd1", "cfgh", SavingsType.MANDATORY,
                InterestCalcType.MINIMUM_BALANCE, MONTHLY, EVERY_MONTH);
        savingsOffering.setInterestRate(8.0);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);
        savings.setActivationDate(helper.getDate("25/09/2030"));
        savings.setNextIntCalcDate(helper.getDate("30/09/2030"));

        Money depositMoney = new Money(currency, "2000.0");
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, depositMoney, depositMoney, helper
                .getDate("25/09/2030"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.setSavingsBalance(depositMoney);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        Money balanceAmount = new Money(currency, "2000.0");
       Assert.assertEquals(balanceAmount, savings.getSavingsBalance());
        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        // Min Bal from 25/09 - 30/9 = 2000
        // Interest 2000*.12*5/365 = 2.2
       Assert.assertEquals(TestUtils.createMoney(2.2), savings.getInterestToBePosted());
        savings.postInterest();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        Money recommendedAmnt = new Money(currency, "234.0");
        Date paymentDate = helper.getDate("26/10/2030");
        AccountActionDateEntity actionDate1 = helper.createAccountActionDate(savings, Short.valueOf("1"), helper
                .getDate("26/10/2030"), paymentDate, savings.getCustomer(), recommendedAmnt, recommendedAmnt,
                PaymentStatus.PAID);
        AccountTestUtils.addAccountActionDate(actionDate1, savings);
        AccountPaymentEntity payment2 = helper.createAccountPayment(savings, depositMoney, paymentDate, createdBy);

        balanceAmount = new Money(currency, "2236.2");
        SavingsTrxnDetailEntity trxn1 = helper.createAccountTrxn(payment2, Short.valueOf("1"), recommendedAmnt,
                balanceAmount, paymentDate, helper.getDate("26/10/2030"), null, AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);
        payment2.addAccountTrxn(trxn1);
        AccountTestUtils.addAccountPayment(payment2, savings);
        savings.setSavingsBalance(balanceAmount);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());

        Money amountAdjustedTo = new Money(getCurrency(), "0");
        savings.setUserContext(userContext);
        try {
            savings.adjustLastUserAction(amountAdjustedTo, "correction entry");
        } catch (ApplicationException ae) {
           Assert.assertTrue(false);
        }
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
       Assert.assertEquals(new Money(currency, "2002.2"), savings.getSavingsBalance());

        recommendedAmnt = new Money(currency, "345.0");
        paymentDate = helper.getDate("26/10/2030");
        actionDate1 = helper.createAccountActionDate(savings, Short.valueOf("1"), helper.getDate("26/10/2030"),
                paymentDate, savings.getCustomer(), recommendedAmnt, recommendedAmnt, PaymentStatus.PAID);
        AccountTestUtils.addAccountActionDate(actionDate1, savings);
        AccountPaymentEntity payment3 = helper.createAccountPayment(savings, depositMoney, paymentDate, createdBy);
        balanceAmount = new Money(currency, "2347.2");
        SavingsTrxnDetailEntity trxn2 = helper.createAccountTrxn(payment2, Short.valueOf("1"), recommendedAmnt,
                balanceAmount, paymentDate, helper.getDate("26/10/2030"), null, AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);
        payment3.addAccountTrxn(trxn2);
        AccountTestUtils.addAccountPayment(payment3, savings);
        savings.setSavingsBalance(balanceAmount);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
       Assert.assertEquals(new Money(currency, "2347.2"), savings.getSavingsBalance());

        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        // Min Bal from 30/09 - 31/10 = 2000
        // Interest 2002.2*.8*31/365 = 13.6
       Assert.assertEquals(TestUtils.createMoney(13.7), savings.getInterestToBePosted());
        savings.postInterest();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        // Min Bal from 31/10 - 30/11 = 2360.79
        // Interest 2360.79*.8*30/365 = 15.6
       Assert.assertEquals(TestUtils.createMoney(15.6), savings.getInterestToBePosted());
        savings.postInterest();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        // Min Bal from 30/11 - 31/12 = 2392.46
        // Interest 2392.469*.8*31/365 = 16.2
       Assert.assertEquals(TestUtils.createMoney(16.2), savings.getInterestToBePosted());
        savings.postInterest();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        // Min Bal from 31/12 - 30/01 = 2408.72
        // Interest 2408.72*.8*30/365 = 16.3
       Assert.assertEquals(TestUtils.createMoney(16.3), savings.getInterestToBePosted());
        savings.postInterest();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testInterestAdjustmentOnFirstDeposit() throws Exception {
        createInitialObjects();
        savingsOffering = createSavingsOfferingForIntCalc("prd1", "cfgh", SavingsType.MANDATORY,
                InterestCalcType.MINIMUM_BALANCE, MONTHLY, EVERY_MONTH);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);
        savings.setActivationDate(helper.getDate("20/02/2006"));
        savings.setNextIntCalcDate(helper.getDate("01/03/2006"));

        Money depositMoney = new Money(currency, "2000.0");
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, depositMoney, depositMoney, helper
                .getDate("25/02/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.setSavingsBalance(depositMoney);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        Money balanceAmount = new Money(currency, "2000.0");
       Assert.assertEquals(balanceAmount, savings.getSavingsBalance());
        Money amountAdjustedTo = new Money(getCurrency(), "1000");
        savings.setUserContext(userContext);
        try {
            savings.adjustLastUserAction(amountAdjustedTo, "correction entry");
        } catch (ApplicationException ae) {
           Assert.assertTrue(false);
        }
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
       Assert.assertEquals(new Money(currency, "1000"), savings.getSavingsBalance());

        Money recommendedAmnt = new Money(currency, "1000.0");
        Date paymentDate = helper.getDate("25/02/2006");
        AccountActionDateEntity actionDate1 = helper.createAccountActionDate(savings, Short.valueOf("1"), helper
                .getDate("25/02/2006"), paymentDate, savings.getCustomer(), recommendedAmnt, recommendedAmnt,
                PaymentStatus.PAID);
        AccountTestUtils.addAccountActionDate(actionDate1, savings);
        AccountPaymentEntity payment2 = helper.createAccountPayment(savings, depositMoney, paymentDate, createdBy);
        balanceAmount = new Money(currency, "2000.0");
        SavingsTrxnDetailEntity trxn1 = helper.createAccountTrxn(payment2, Short.valueOf("1"), recommendedAmnt,
                balanceAmount, paymentDate, helper.getDate("25/02/2006"), null, AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);
        payment2.addAccountTrxn(trxn1);
        AccountTestUtils.addAccountPayment(payment2, savings);

        savings.setSavingsBalance(balanceAmount);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
       Assert.assertEquals(balanceAmount, savings.getSavingsBalance());
        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        // Min Bal from 25/02 - 01/3 = 2000
        // Interest 2000*.12*4/365 = 2.6
       Assert.assertEquals(TestUtils.createMoney(2.7), savings.getInterestToBePosted());
        savings.postInterest();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        // Min Bal from 01/03 - 31/3 = 2002.7
        // Interest 2002.7*.12*30/365 = 20.4
       Assert.assertEquals(TestUtils.createMoney(19.8), savings.getInterestToBePosted());
        savings.postInterest();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        // Min Bal from 31/03 - 30/4 = 2022.5
        // Interest 2022.5*.12*30/365 = 20.0
       Assert.assertEquals(TestUtils.createMoney(20.0), savings.getInterestToBePosted());
        savings.postInterest();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
       Assert.assertEquals(TestUtils.createMoney(20.9), savings.getInterestToBePosted());
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    // ///////////////////////////////////////////////////
    private SavingsOfferingBO createSavingsOffering(final String offeringName, final String shortName, final SavingsType savingsType,
            final InterestCalcType interestCalcType, final RecurrenceType freqeuncyIntCalc, final short recurAfterIntCalc,
            final double interestRate, final Date startDate, final Double minAmtToCalculateInterest, final Double maxAmtToWithdraw)
            throws Exception {
        MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(helper.getMeeting(freqeuncyIntCalc,
                recurAfterIntCalc, SAVINGS_INTEREST_CALCULATION_TIME_PERIOD));
        MeetingBO meetingIntPost = TestObjectFactory.createMeeting(helper.getMeeting(MONTHLY, EVERY_MONTH,
                SAVINGS_INTEREST_POSTING));
        return TestObjectFactory.createSavingsProduct(offeringName, shortName, ApplicableTo.GROUPS, startDate,
                PrdStatus.SAVINGS_ACTIVE, 300.0, RecommendedAmountUnit.PER_INDIVIDUAL, interestRate, maxAmtToWithdraw,
                minAmtToCalculateInterest, savingsType, interestCalcType, meetingIntCalc, meetingIntPost);
    }

    private SavingsOfferingBO createSavingsOfferingForWeekly(final String offeringName, final String shortName,
            final SavingsType savingsType, final InterestCalcType interestCalcType, final RecurrenceType freqeuncyIntCalc,
            final short recurAfterIntCalc, final double interestRate, final Date startDate, final Double minAmtToCalculateInterest,
            final Double maxAmtToWithdraw) throws Exception {
        MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(helper.getMeeting(freqeuncyIntCalc,
                recurAfterIntCalc, SAVINGS_INTEREST_CALCULATION_TIME_PERIOD));
        MeetingBO meetingIntPost = TestObjectFactory.createMeeting(helper.getMeeting(WEEKLY, EVERY_WEEK,
                SAVINGS_INTEREST_POSTING));
        return TestObjectFactory.createSavingsProduct(offeringName, shortName, ApplicableTo.GROUPS, startDate,
                PrdStatus.SAVINGS_ACTIVE, 300.0, RecommendedAmountUnit.PER_INDIVIDUAL, interestRate, maxAmtToWithdraw,
                minAmtToCalculateInterest, savingsType, interestCalcType, meetingIntCalc, meetingIntPost);
    }

    private SavingsOfferingBO createSavingsOfferingForDaily(final String offeringName, final String shortName,
            final SavingsType savingsType, final InterestCalcType interestCalcType, final RecurrenceType freqeuncyIntCalc,
            final short recurAfterIntCalc, final double interestRate, final Date startDate, final Double minAmtToCalculateInterest,
            final Double maxAmtToWithdraw) throws Exception {
        MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(helper.getMeeting(freqeuncyIntCalc,
                recurAfterIntCalc, SAVINGS_INTEREST_CALCULATION_TIME_PERIOD));
        MeetingBO meetingIntPost = TestObjectFactory.createMeeting(helper.getMeeting(DAILY, EVERY_DAY,
                SAVINGS_INTEREST_POSTING));
        return TestObjectFactory.createSavingsProduct(offeringName, shortName, ApplicableTo.GROUPS, startDate,
                PrdStatus.SAVINGS_ACTIVE, 300.0, RecommendedAmountUnit.PER_INDIVIDUAL, interestRate, maxAmtToWithdraw,
                minAmtToCalculateInterest, savingsType, interestCalcType, meetingIntCalc, meetingIntPost);
    }

    public void testCalcDateAndPostDatesAreSameForDaily() throws Exception {

        createInitialObjects();
        Date startDate = DateUtils.getCurrentDateWithoutTimeStamp();
        savingsOffering = createSavingsOfferingForDaily("prd2", "cfgg", SavingsType.VOLUNTARY,
                InterestCalcType.MINIMUM_BALANCE, DAILY, new Short("1"), 10.0, startDate, 200.0, 0.0);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);
        savings.setActivationDate(startDate);
        Short flagId = 1;
        savings.changeStatus(AccountState.LOAN_APPROVED, flagId, "approved");
       Assert.assertEquals(savings.getNextIntPostDate(), savings.getNextIntCalcDate());
        Money depositMoney = new Money(currency, "200.0");
        Money balanceAmt = new Money(currency, "200.0");
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, depositMoney, balanceAmt,
                startDate, AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.setSavingsBalance(balanceAmt);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        for (int i = 0; i < 12; i++) {
            savings.updateInterestAccrued();
            savings.postInterest();
           Assert.assertEquals(DateUtils.addDays(savings.getLastIntPostDate(), 1), savings.getNextIntPostDate());
           Assert.assertEquals(DateUtils.addDays(savings.getLastIntCalcDate(), 1), savings.getNextIntCalcDate());
           Assert.assertEquals(savings.getNextIntPostDate(), savings.getNextIntCalcDate());
        }

    }

    public void testCalcDateAndPostDatesAreSameForWeekly() throws Exception {

        createInitialObjects();
        Date startDate = DateUtils.getCurrentDateWithoutTimeStamp();
        savingsOffering = createSavingsOfferingForWeekly("prd2", "cfgg", SavingsType.VOLUNTARY,
                InterestCalcType.MINIMUM_BALANCE, WEEKLY, new Short("1"), 10.0, startDate, 200.0, 0.0);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);
        savings.setActivationDate(startDate);
        Short flagId = 1;
        savings.changeStatus(AccountState.LOAN_APPROVED, flagId, "approved");
       Assert.assertEquals(savings.getNextIntPostDate(), savings.getNextIntCalcDate());
        Money depositMoney = new Money(currency, "200.0");
        Money balanceAmt = new Money(currency, "200.0");
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, depositMoney, balanceAmt,
                startDate, AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.setSavingsBalance(balanceAmt);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        for (int i = 0; i < 12; i++) {
            savings.updateInterestAccrued();
            savings.postInterest();
           Assert.assertEquals(DateUtils.addDays(savings.getLastIntPostDate(), 7), savings.getNextIntPostDate());
           Assert.assertEquals(DateUtils.addDays(savings.getLastIntCalcDate(), 7), savings.getNextIntCalcDate());
           Assert.assertEquals(savings.getNextIntPostDate(), savings.getNextIntCalcDate());
        }

    }

    public void testCalcDateAndPostDatesAreSameForMonthly() throws Exception {

        createInitialObjects();
        Date startDate = DateUtils.getCurrentDateWithoutTimeStamp();
        savingsOffering = createSavingsOffering("prd4", "cfgl", SavingsType.VOLUNTARY,
                InterestCalcType.MINIMUM_BALANCE, MONTHLY, new Short("1"), 10.0, startDate, 200.0, 0.0);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);
        savings.setActivationDate(startDate);
        Short flagId = 1;
        savings.changeStatus(AccountState.LOAN_APPROVED, flagId, "approved");
       Assert.assertEquals(savings.getNextIntPostDate(), savings.getNextIntCalcDate());
        Money depositMoney = new Money(currency, "200.0");
        Money balanceAmt = new Money(currency, "200.0");
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, depositMoney, balanceAmt,
                startDate, AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.setSavingsBalance(balanceAmt);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        for (int i = 0; i < 12; i++) {
            savings.updateInterestAccrued();
            savings.postInterest();
           Assert.assertEquals(savings.getNextIntPostDate(), savings.getNextIntCalcDate());
        }

    }

    public void testCalculateInterest_IntCalcFreqMonthly_minbal() throws Exception {

        createInitialObjects();
        Short savedDigits = AccountingRules.getDigitsAfterDecimal();
        AccountingRules.setDigitsAfterDecimal(new Short("3"));
        Date startDate = DateUtils.getCurrentDateWithoutTimeStamp();
        savingsOffering = createSavingsOffering("prd1", "cfgh", SavingsType.VOLUNTARY,
                InterestCalcType.MINIMUM_BALANCE, MONTHLY, new Short("1"), 10.0, startDate, 200.0, 0.0);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);

        Date nextIntCalcDate = DateUtils.addDays(startDate, 30);
        savings.setNextIntCalcDate(nextIntCalcDate);
        savings.setActivationDate(startDate);
        Money depositMoney = new Money(currency, "200.0");
        Money balanceAmt = new Money(currency, "200.0");
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, depositMoney, balanceAmt,
                startDate, AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.setSavingsBalance(balanceAmt);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        savings.updateInterestAccrued();
       Assert.assertEquals(TestUtils.createMoney(1.644), savings.getInterestToBePosted());

        SavingsHelper sHelper = new SavingsHelper();
        Date nextIntCalcDate2 = sHelper.getNextScheduleDate(startDate, nextIntCalcDate, savingsOffering
                .getTimePerForInstcalc().getMeeting());
       Assert.assertEquals(nextIntCalcDate2, savings.getNextIntCalcDate());
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        savings.postInterest();

        Date postDate = sHelper.getNextScheduleDate(startDate, null, savingsOffering.getFreqOfPostIntcalc()
                .getMeeting());
       Assert.assertEquals(postDate, savings.getLastIntPostDate());
        postDate = sHelper
                .getNextScheduleDate(startDate, postDate, savingsOffering.getFreqOfPostIntcalc().getMeeting());

       Assert.assertEquals(postDate, savings.getNextIntPostDate());
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        savings.updateInterestAccrued();
        int days = sHelper.calculateDays(nextIntCalcDate, nextIntCalcDate2);
        double interest = (201.644 * 0.1 * days / 365);
       Assert.assertEquals(getRoundedMoney(interest), savings.getInterestToBePosted());
        nextIntCalcDate2 = sHelper.getNextScheduleDate(startDate, nextIntCalcDate2, savingsOffering
                .getTimePerForInstcalc().getMeeting());
       Assert.assertEquals(nextIntCalcDate2, savings.getNextIntCalcDate());

        AccountingRules.setDigitsAfterDecimal(savedDigits);
    }

    // //////////////////////////////////////////////////

    
    private void createInitialObjects() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
    }

    private SavingsBO createSavingsAccountPayment() throws Exception {
        AccountPaymentEntity payment;
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("2333dsf", "2132");
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);

        Money initialBal = new Money(currency, "5500");
        payment = helper.createAccountPayment(savings, null, new Money(currency, "5500.0"), new Date(), createdBy);
        payment.addAccountTrxn(helper.createAccountTrxn(payment, null, initialBal, initialBal, helper
                .getDate("04/01/2006"), null, null, AccountActionTypes.SAVINGS_ADJUSTMENT.getValue(), savings,
                createdBy, group));
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        return TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
    }

    public void testInterestAdjustment_LastDepositPaymentNullify_AfterMultiple_Interest_Calculation_WithMinBal()
            throws Exception {
        createInitialObjects();
        savingsOffering = createSavingsOfferingForIntCalc("prd1", "cfgh", SavingsType.MANDATORY,
                InterestCalcType.MINIMUM_BALANCE, MONTHLY, EVERY_MONTH);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);
        savings.setActivationDate(helper.getDate("20/02/2006"));
        savings.setNextIntCalcDate(helper.getDate("01/03/2006"));

        Money depositMoney = new Money(currency, "2000.0");
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, depositMoney, depositMoney, helper
                .getDate("25/02/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.setSavingsBalance(depositMoney);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        depositMoney = new Money(currency, "5000.0");
        Money balanceAmt = new Money(currency, "7000.0");
        AccountPaymentEntity payment2 = helper.createAccountPaymentToPersist(savings, depositMoney, balanceAmt, helper
                .getDate("27/02/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment2, savings);
        savings.setSavingsBalance(balanceAmt);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        // - deposited for two action dates
        Money recommendedAmnt = new Money(currency, "500.0");
        Date paymentDate = helper.getDate("15/03/2006");
        AccountActionDateEntity actionDate1 = helper.createAccountActionDate(savings, Short.valueOf("1"), helper
                .getDate("07/03/2006"), paymentDate, savings.getCustomer(), recommendedAmnt, recommendedAmnt,
                PaymentStatus.PAID);
        AccountActionDateEntity actionDate2 = helper.createAccountActionDate(savings, Short.valueOf("2"), helper
                .getDate("14/03/2006"), paymentDate, savings.getCustomer(), recommendedAmnt, recommendedAmnt,
                PaymentStatus.PAID);

        AccountTestUtils.addAccountActionDate(actionDate1, savings);
        AccountTestUtils.addAccountActionDate(actionDate2, savings);

        depositMoney = new Money(currency, "1200.0");

        // Adding 1 account payment of Rs 1200. With three transactions of (500
        // + 500 + 200).
        AccountPaymentEntity payment3 = helper.createAccountPayment(savings, depositMoney, paymentDate, createdBy);
        Money balanceAmount = new Money(currency, "7500.0");
        SavingsTrxnDetailEntity trxn1 = helper.createAccountTrxn(payment3, Short.valueOf("1"), recommendedAmnt,
                balanceAmount, paymentDate, helper.getDate("01/05/2006"), null, AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);

        balanceAmount = new Money(currency, "8000.0");
        SavingsTrxnDetailEntity trxn2 = helper.createAccountTrxn(payment3, Short.valueOf("2"), recommendedAmnt,
                balanceAmount, paymentDate, helper.getDate("08/05/2006"), null, AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);

        balanceAmount = new Money(currency, "8200.0");
        SavingsTrxnDetailEntity trxn3 = helper.createAccountTrxn(payment3, null, new Money(currency, "200.0"),
                balanceAmount, paymentDate, null, null, AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings,
                createdBy, group);

        payment3.addAccountTrxn(trxn1);
        payment3.addAccountTrxn(trxn2);
        payment3.addAccountTrxn(trxn3);
        AccountTestUtils.addAccountPayment(payment3, savings);

        savings.setSavingsBalance(balanceAmount);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
       Assert.assertEquals(balanceAmount, savings.getSavingsBalance());

        Money oldInterest = savings.calculateInterestForAdjustment(helper.getDate("15/03/2006"), null);
       Assert.assertEquals(TestUtils.createMoney(), oldInterest);

        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        // Min Bal from 25/02 - 01/3 = 2000
        // Interest 2000*.12*4/365 = 2.6
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(2.6,
        // savings.getInterestToBePosted());
       Assert.assertEquals(TestUtils.createMoney(2.7), savings.getInterestToBePosted());

        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());

        // principal = (7000* 31) /31 = 7000
        // Interest = 7000*.12*31/365 = 71.3
        oldInterest = savings.calculateInterestForAdjustment(helper.getDate("15/03/2006"), null);
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(71.3, oldInterest);
       Assert.assertEquals(getRoundedMoney(71.4), oldInterest);
        // 71.3 + 2.6 = 73.9
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(73.9,
        // savings.getInterestToBePosted());
       Assert.assertEquals(getRoundedMoney(71.8), savings.getInterestToBePosted());

        savings.setUserContext(userContext);
        // Nullifying last payment
        Money amountAdjustedTo = new Money(getCurrency());

        try {
            savings.adjustLastUserAction(amountAdjustedTo, "correction entry");
        } catch (ApplicationException ae) {
           Assert.assertTrue(false);
        }
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());

       Assert.assertEquals(new Money(currency, "7000"), savings.getSavingsBalance());
        // principal = (7000* 31) /31 = 7000
        // Interest = 7000*.12*31/365 = 71.3
        // 71.3 + 2.0 = 73.9
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(73.9,
        // savings.getInterestToBePosted());
       Assert.assertEquals(getRoundedMoney(71.8), savings.getInterestToBePosted());

        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testInterestAdjustment_LastWithdrawalPaymentNullify_AfterMultiple_Interest_Calculation_WithMinBal()
            throws Exception {
        createInitialObjects();
        savingsOffering = createSavingsOfferingForIntCalc("prd1", "cfgh", SavingsType.MANDATORY,
                InterestCalcType.MINIMUM_BALANCE, MONTHLY, EVERY_MONTH);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);
        savings.setActivationDate(helper.getDate("20/02/2006"));
        savings.setNextIntCalcDate(helper.getDate("28/02/2006"));

        Money depositMoney = new Money(currency, "2000.0");
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, depositMoney, depositMoney, helper
                .getDate("25/02/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.setSavingsBalance(depositMoney);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        depositMoney = new Money(currency, "5000.0");
        Money balanceAmt = new Money(currency, "7000.0");
        AccountPaymentEntity payment2 = helper.createAccountPaymentToPersist(savings, depositMoney, balanceAmt, helper
                .getDate("27/02/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment2, savings);
        savings.setSavingsBalance(balanceAmt);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        Money withdrawalMoney = new Money(currency, "2000.0");
        balanceAmt = new Money(currency, "5000.0");
        AccountPaymentEntity payment3 = helper.createAccountPaymentToPersist(savings, withdrawalMoney, balanceAmt,
                helper.getDate("15/03/2006"), AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(), savings, createdBy,
                group);
        AccountTestUtils.addAccountPayment(payment3, savings);
        savings.setSavingsBalance(balanceAmt);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());

        Money oldInterest = savings.calculateInterestForAdjustment(helper.getDate("15/03/2006"), null);
       Assert.assertEquals(TestUtils.createMoney(), oldInterest);

        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        // Min Bal from 25/02 - 28/2 = 2000
        // Interest 2000*.12*3/365 = 2.0
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(2.6,
        // savings.getInterestToBePosted());
       Assert.assertEquals(getRoundedMoney(2.0), savings.getInterestToBePosted());

        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());

        // principal = (5000* 31) /31 = 5000
        // Interest = 5000*.12*31/365 = 51.0
        oldInterest = savings.calculateInterestForAdjustment(helper.getDate("15/03/2006"), null);
        // * TODO: financial_calculation_rounding
       Assert.assertEquals(getRoundedMoney(51.0), oldInterest);
        // 51.0 + 2.0 = 53.0
        // * TODO: financial_calculation_rounding
       Assert.assertEquals(getRoundedMoney(53.0), savings.getInterestToBePosted());

        savings.setUserContext(userContext);
        // Nullifying last payment
        Money amountAdjustedTo = new Money(getCurrency());

        try {
            savings.adjustLastUserAction(amountAdjustedTo, "correction entry");
        } catch (ApplicationException ae) {
           Assert.assertTrue(false);
        }
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());

       Assert.assertEquals(new Money(currency, "7000"), savings.getSavingsBalance());
        // principal = (7000* 31) /31 = 7000
        // Interest = 7000*.12*31/365 = 71.3
        // 71.3 + 2.0 = 73.3
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(73.9,
        // savings.getInterestToBePosted());
       Assert.assertEquals(getRoundedMoney(73.4), savings.getInterestToBePosted());

        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testInterestAdjustment_LastPaymentBefore_InterestCalc_has_MultipleTrxns() throws Exception {
        createInitialObjects();
        savingsOffering = createSavingsOfferingForIntCalc("prd1", "cfgh", SavingsType.MANDATORY,
                InterestCalcType.AVERAGE_BALANCE, MONTHLY, EVERY_MONTH);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);
        savings.setActivationDate(helper.getDate("20/02/2006"));
        savings.setNextIntCalcDate(helper.getDate("28/02/2006"));

        Money depositMoney = new Money(currency, "2000.0");
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, depositMoney, depositMoney, helper
                .getDate("25/02/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.setSavingsBalance(depositMoney);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        depositMoney = new Money(currency, "5000.0");

        // Adding 1 account payment of Rs 5000. With three transactions of (2000
        // + 2000 + 1000).
        Date paymentDate = helper.getDate("27/02/2006");
        Money recommendedAmnt = new Money(currency, "2000");

        AccountPaymentEntity payment2 = helper.createAccountPayment(savings, depositMoney, paymentDate, createdBy);
        Money balanceAmount = new Money(currency, "4000.0");
        SavingsTrxnDetailEntity trxn1 = helper.createAccountTrxn(payment2, Short.valueOf("1"), recommendedAmnt,
                balanceAmount, paymentDate, null, null, AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings,
                createdBy, group);

        balanceAmount = new Money(currency, "6000.0");
        SavingsTrxnDetailEntity trxn2 = helper.createAccountTrxn(payment2, Short.valueOf("2"), recommendedAmnt,
                balanceAmount, paymentDate, null, null, AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings,
                createdBy, group);

        balanceAmount = new Money(currency, "7000.0");
        SavingsTrxnDetailEntity trxn3 = helper.createAccountTrxn(payment2, null, new Money(currency, "1000.0"),
                balanceAmount, paymentDate, null, null, AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings,
                createdBy, group);

        payment2.addAccountTrxn(trxn1);
        payment2.addAccountTrxn(trxn2);
        payment2.addAccountTrxn(trxn3);
        AccountTestUtils.addAccountPayment(payment2, savings);

        savings.setSavingsBalance(balanceAmount);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        depositMoney = new Money(currency, "1200.0");
        balanceAmount = new Money(currency, "8200.0");
        AccountPaymentEntity payment3 = helper.createAccountPaymentToPersist(savings, depositMoney, balanceAmount,
                helper.getDate("15/03/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment3, savings);
        savings.setSavingsBalance(balanceAmount);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
       Assert.assertEquals(balanceAmount, savings.getSavingsBalance());

        Money oldInterest = savings.calculateInterestForAdjustment(helper.getDate("15/03/2006"), null);
       Assert.assertEquals(TestUtils.createMoney(), oldInterest);

        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        // from 25/02 - 27/02 = 2000*2 + from 27/02 - 28/02 = 7000*1 = 11000
        // Therefore avg bal 11000/3
        // Interest 11000/3*.12*3/365 = 3.6
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(5.9,
        // savings.getInterestToBePosted());
       Assert.assertEquals(getRoundedMoney(3.7), savings.getInterestToBePosted());

        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());

        // principal = (7000* 14(days)+ 8200*17(days))/31 = 7658.1
        // Interest = 7658.1*.12*31/365 = 78.0
        oldInterest = savings.calculateInterestForAdjustment(helper.getDate("15/03/2006"), null);
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(78.0, oldInterest);
       Assert.assertEquals(getRoundedMoney(77.7), oldInterest);
        // 77.7 + 3.6 = 81.3
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(83.9,
        // savings.getInterestToBePosted());
       Assert.assertEquals(getRoundedMoney(81.4), savings.getInterestToBePosted());

        savings.setUserContext(userContext);
        Money amountAdjustedTo = new Money(currency, "2000");

        try {
            savings.adjustLastUserAction(amountAdjustedTo, "correction entry");
        } catch (ApplicationException ae) {
           Assert.assertTrue(false);
        }
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());

       Assert.assertEquals(new Money(currency, "9000"), savings.getSavingsBalance());
        // principal = (7000* 14(days)+ 9000*17(days))/31 = 8096.8
        // Interest = 8096.8*.12*31/365 = 82.5
        // Total Interest = 82.5 + 3.6 = 86.1
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(88.4,
        // savings.getInterestToBePosted());
       Assert.assertEquals(getRoundedMoney(85.6), savings.getInterestToBePosted());

        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testInterestAdjustment_LastDepositPaymentModified_AfterMultiple_Interest_Calculation_WithAvgBal()
            throws Exception {
        createInitialObjects();
        savingsOffering = createSavingsOfferingForIntCalc("prd1", "cfgh", SavingsType.MANDATORY,
                InterestCalcType.AVERAGE_BALANCE, MONTHLY, EVERY_MONTH);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);
        savings.setActivationDate(helper.getDate("20/02/2006"));
        savings.setNextIntCalcDate(helper.getDate("28/02/2006"));

        Money depositMoney = new Money(currency, "2000.0");
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, depositMoney, depositMoney, helper
                .getDate("25/02/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.setSavingsBalance(depositMoney);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        depositMoney = new Money(currency, "5000.0");
        Money balanceAmt = new Money(currency, "7000.0");
        AccountPaymentEntity payment2 = helper.createAccountPaymentToPersist(savings, depositMoney, balanceAmt, helper
                .getDate("27/02/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment2, savings);
        savings.setSavingsBalance(balanceAmt);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        // - deposited for two action dates
        Money recommendedAmnt = new Money(currency, "500.0");
        Date paymentDate = helper.getDate("15/03/2006");
        AccountActionDateEntity actionDate1 = helper.createAccountActionDate(savings, Short.valueOf("1"), helper
                .getDate("07/03/2006"), paymentDate, savings.getCustomer(), recommendedAmnt, recommendedAmnt,
                PaymentStatus.PAID);
        AccountActionDateEntity actionDate2 = helper.createAccountActionDate(savings, Short.valueOf("2"), helper
                .getDate("14/03/2006"), paymentDate, savings.getCustomer(), recommendedAmnt, recommendedAmnt,
                PaymentStatus.PAID);

        AccountTestUtils.addAccountActionDate(actionDate1, savings);
        AccountTestUtils.addAccountActionDate(actionDate2, savings);

        depositMoney = new Money(currency, "1200.0");

        // Adding 1 account payment of Rs 1200. With three transactions of (500
        // + 500 + 200).
        AccountPaymentEntity payment3 = helper.createAccountPayment(savings, depositMoney, paymentDate, createdBy);
        Money balanceAmount = new Money(currency, "7500.0");
        SavingsTrxnDetailEntity trxn1 = helper.createAccountTrxn(payment3, Short.valueOf("1"), recommendedAmnt,
                balanceAmount, paymentDate, helper.getDate("01/05/2006"), null, AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);

        balanceAmount = new Money(currency, "8000.0");
        SavingsTrxnDetailEntity trxn2 = helper.createAccountTrxn(payment3, Short.valueOf("2"), recommendedAmnt,
                balanceAmount, paymentDate, helper.getDate("08/05/2006"), null, AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);

        balanceAmount = new Money(currency, "8200.0");
        SavingsTrxnDetailEntity trxn3 = helper.createAccountTrxn(payment3, null, new Money(currency, "200.0"),
                balanceAmount, paymentDate, null, null, AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings,
                createdBy, group);

        payment3.addAccountTrxn(trxn1);
        payment3.addAccountTrxn(trxn2);
        payment3.addAccountTrxn(trxn3);
        AccountTestUtils.addAccountPayment(payment3, savings);

        savings.setSavingsBalance(balanceAmount);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
       Assert.assertEquals(balanceAmount, savings.getSavingsBalance());

        Money oldInterest = savings.calculateInterestForAdjustment(helper.getDate("15/03/2006"), null);
       Assert.assertEquals(TestUtils.createMoney(), oldInterest);

        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        // from 25/02 - 27/02 = 2000*2 + from 27/02 - 28/02 = 7000*1 = 11000
        // Therefore avg bal 11000/3
        // Interest 11000/3 *.12*3/365 = 3.6
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(5.9,
        // savings.getInterestToBePosted());
       Assert.assertEquals(getRoundedMoney(3.7), savings.getInterestToBePosted());

        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());

        // principal = (7000* 14(days)+ 8200*17(days))/31 = 7658.1
        // Interest = 7658.1*.12*31/365 = 78.0
        oldInterest = savings.calculateInterestForAdjustment(helper.getDate("15/03/2006"), null);
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(78.0, oldInterest);
       Assert.assertEquals(getRoundedMoney(77.7), oldInterest);
        // 77.7 + 3.6 = 81.3
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(83.9,
        // savings.getInterestToBePosted());
       Assert.assertEquals(getRoundedMoney(81.4), savings.getInterestToBePosted());

        savings.setUserContext(userContext);
        Money amountAdjustedTo = new Money(currency, "2000");

        try {
            savings.adjustLastUserAction(amountAdjustedTo, "correction entry");
        } catch (ApplicationException ae) {
           Assert.assertTrue(false);
        }
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());

       Assert.assertEquals(new Money(currency, "9000"), savings.getSavingsBalance());
        // principal = (7000* 14(days)+ 9000*17(days))/31 = 8096.8
        // Interest = 8096.8*.12*31/365 = 82.5
        // Total Interest = 82.5 + 3.6 = 86.1
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(88.4,
        // savings.getInterestToBePosted());
       Assert.assertEquals(getRoundedMoney(85.6), savings.getInterestToBePosted());

        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testInterestAdjustment_LastDepositPaymentNullify_AfterMultiple_Interest_Calculation_WithAvgBal()
            throws Exception {
        createInitialObjects();
        savingsOffering = createSavingsOfferingForIntCalc("prd1", "cfgh", SavingsType.MANDATORY,
                InterestCalcType.AVERAGE_BALANCE, MONTHLY, EVERY_MONTH);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);
        savings.setActivationDate(helper.getDate("20/02/2006"));
        savings.setNextIntCalcDate(helper.getDate("28/02/2006"));

        Money depositMoney = new Money(currency, "2000.0");
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, depositMoney, depositMoney, helper
                .getDate("25/02/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.setSavingsBalance(depositMoney);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        depositMoney = new Money(currency, "5000.0");
        Money balanceAmt = new Money(currency, "7000.0");
        AccountPaymentEntity payment2 = helper.createAccountPaymentToPersist(savings, depositMoney, balanceAmt, helper
                .getDate("27/02/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment2, savings);
        savings.setSavingsBalance(balanceAmt);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        // - deposited for two action dates
        Money recommendedAmnt = new Money(currency, "500.0");
        Date paymentDate = helper.getDate("15/03/2006");
        AccountActionDateEntity actionDate1 = helper.createAccountActionDate(savings, Short.valueOf("1"), helper
                .getDate("07/03/2006"), paymentDate, savings.getCustomer(), recommendedAmnt, recommendedAmnt,
                PaymentStatus.PAID);
        AccountActionDateEntity actionDate2 = helper.createAccountActionDate(savings, Short.valueOf("2"), helper
                .getDate("14/03/2006"), paymentDate, savings.getCustomer(), recommendedAmnt, recommendedAmnt,
                PaymentStatus.PAID);

        AccountTestUtils.addAccountActionDate(actionDate1, savings);
        AccountTestUtils.addAccountActionDate(actionDate2, savings);

        depositMoney = new Money(currency, "1200.0");

        // Adding 1 account payment of Rs 1200. With three transactions of (500
        // + 500 + 200).
        AccountPaymentEntity payment3 = helper.createAccountPayment(savings, depositMoney, paymentDate, createdBy);
        Money balanceAmount = new Money(currency, "7500.0");
        SavingsTrxnDetailEntity trxn1 = helper.createAccountTrxn(payment3, Short.valueOf("1"), recommendedAmnt,
                balanceAmount, paymentDate, helper.getDate("01/05/2006"), null, AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);

        balanceAmount = new Money(currency, "8000.0");
        SavingsTrxnDetailEntity trxn2 = helper.createAccountTrxn(payment3, Short.valueOf("2"), recommendedAmnt,
                balanceAmount, paymentDate, helper.getDate("08/05/2006"), null, AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);

        balanceAmount = new Money(currency, "8200.0");
        SavingsTrxnDetailEntity trxn3 = helper.createAccountTrxn(payment3, null, new Money(currency, "200.0"),
                balanceAmount, paymentDate, null, null, AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings,
                createdBy, group);

        payment3.addAccountTrxn(trxn1);
        payment3.addAccountTrxn(trxn2);
        payment3.addAccountTrxn(trxn3);
        AccountTestUtils.addAccountPayment(payment3, savings);

        savings.setSavingsBalance(balanceAmount);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
       Assert.assertEquals(balanceAmount, savings.getSavingsBalance());

        Money oldInterest = savings.calculateInterestForAdjustment(helper.getDate("15/03/2006"), null);
       Assert.assertEquals(TestUtils.createMoney(), oldInterest);

        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        // from 25/02 - 27/02 = 2000*2 + from 27/02 - 28/02 = 7000*1 = 11000
        // Therefore avg bal 11000/3
        // Interest (11000/3)*.12*3/365 = 3.6
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(5.9,
        // savings.getInterestToBePosted());
       Assert.assertEquals(getRoundedMoney(3.7), savings.getInterestToBePosted());

        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());

        // principal = (7000* 14(days)+ 8200*17(days))/31 = 7658.1
        // Interest = 7658.1*.12*31/365 = 78.0
        oldInterest = savings.calculateInterestForAdjustment(helper.getDate("15/03/2006"), null);
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(78.0, oldInterest);
       Assert.assertEquals(getRoundedMoney(77.7), oldInterest);
        // 77.7 + 3.6 = 81.3
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(83.9,
        // savings.getInterestToBePosted());
       Assert.assertEquals(getRoundedMoney(81.4), savings.getInterestToBePosted());

        savings.setUserContext(userContext);
        // Nullifying last payment
        Money amountAdjustedTo = new Money(getCurrency());

        try {
            savings.adjustLastUserAction(amountAdjustedTo, "correction entry");
        } catch (ApplicationException ae) {
           Assert.assertTrue(false);
        }
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());

       Assert.assertEquals(new Money(currency, "7000"), savings.getSavingsBalance());
        // principal = (7000* 31) /31 = 7000
        // Interest = 7000*.12*31/365 = 71.3
        // Total Interest = 71.3 + 3.6 = 74.9
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(77.2,
        // savings.getInterestToBePosted());
       Assert.assertEquals(getRoundedMoney(75.1), savings.getInterestToBePosted());

        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testInterestAdjustment_LastDepositPaymentModified_AfterMultiple_Interest_Calculation_WithMinBal()
            throws Exception {
        createInitialObjects();
        savingsOffering = createSavingsOfferingForIntCalc("prd1", "cfgh", SavingsType.VOLUNTARY,
                InterestCalcType.MINIMUM_BALANCE, MONTHLY, EVERY_MONTH);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);
        savings.setActivationDate(helper.getDate("20/02/2006"));
        savings.setNextIntCalcDate(helper.getDate("28/02/2006"));

        Money depositMoney = new Money(currency, "2000.0");
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, depositMoney, depositMoney, helper
                .getDate("25/02/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.setSavingsBalance(depositMoney);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        depositMoney = new Money(currency, "5000.0");
        Money balanceAmt = new Money(currency, "7000.0");
        AccountPaymentEntity payment2 = helper.createAccountPaymentToPersist(savings, depositMoney, balanceAmt, helper
                .getDate("27/02/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment2, savings);
        savings.setSavingsBalance(balanceAmt);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());

        Money oldInterest = savings.calculateInterestForAdjustment(helper.getDate("27/02/2006"), null);
       Assert.assertEquals(TestUtils.createMoney(), oldInterest);

        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        // Min Bal 25/02 - 28/02 = 2000
        // Interest 2000*.12*3/365 = 2.0
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(2.6,
        // savings.getInterestToBePosted());
       Assert.assertEquals(getRoundedMoney(2.0), savings.getInterestToBePosted());

        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());

        // Min Bal 25/02 - 28/02 = 7000
        // Interest 7000*.12*31/365 = 71.3
        // Total Interest 71.3 + 2.0 = 73.3
        oldInterest = savings.calculateInterestForAdjustment(helper.getDate("27/02/2006"), null);
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(73.9, oldInterest);
        //Assert.assertEquals(73.9, savings.getInterestToBePosted()
        // );
       Assert.assertEquals(getRoundedMoney(73.4), oldInterest);
       Assert.assertEquals(getRoundedMoney(73.4), savings.getInterestToBePosted());

        savings.setUserContext(userContext);
        Money amountAdjustedTo = new Money(currency, "4000");

        try {
            savings.adjustLastUserAction(amountAdjustedTo, "correction entry");
        } catch (ApplicationException ae) {
           Assert.assertTrue(false);
        }
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        payment = savings.getLastPmnt();
       Assert.assertEquals(new Money(currency, "6000"), savings.getSavingsBalance());

        // Min Bal 25/02 - 28/02 = 6000
        // Interest 6000*.12*31/365 = 61.2
        // Total Interest 61.2 + 2.0 = 63.2
        // * TODO: financial_calculation_rounding
       Assert.assertEquals(getRoundedMoney(63.2), savings.getInterestToBePosted());

        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

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

    private SavingsBO createSavingsWithAccountPayments() throws Exception {
        AccountPaymentEntity payment;
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("2333dsf", "2132");
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);

        Money initialBal = new Money(currency, "5500");
        payment = helper.createAccountPayment(savings, null, new Money(currency, "5500.0"), new Date(), createdBy);
        payment.addAccountTrxn(helper.createAccountTrxn(payment, null, initialBal, initialBal, helper
                .getDate("04/01/2006"), null, null, AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy,
                group));
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();

        payment = helper.createAccountPayment(savings, null, new Money(currency, "2500.0"), new Date(), createdBy);
        payment.addAccountTrxn(helper.createAccountTrxn(payment, null, new Money(currency, "2500"), new Money(currency,
                "7500"), helper.getDate("10/01/2006"), null, null, AccountActionTypes.SAVINGS_DEPOSIT.getValue(),
                savings, createdBy, group));
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();

        payment = helper.createAccountPayment(savings, null, new Money(currency, "3500.0"), new Date(), createdBy);
        payment.addAccountTrxn(helper.createAccountTrxn(payment, null, new Money(currency, "3500"), new Money(currency,
                "4000"), helper.getDate("16/02/2006"), null, null, AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(),
                savings, createdBy, group));
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();

        payment = helper.createAccountPayment(savings, null, new Money(currency, "1000.0"), new Date(), createdBy);
        payment.addAccountTrxn(helper.createAccountTrxn(payment, null, new Money(currency, "1000"), new Money(currency,
                "5000"), helper.getDate("05/03/2006"), null, null, AccountActionTypes.SAVINGS_DEPOSIT.getValue(),
                savings, createdBy, group));
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();

        payment = helper.createAccountPayment(savings, null, new Money(currency, "500.0"), new Date(), createdBy);
        payment.addAccountTrxn(helper.createAccountTrxn(payment, null, new Money(currency, "1200.0"), new Money(
                currency, "3800.0"), helper.getDate("05/03/2006"), null, null, AccountActionTypes.SAVINGS_WITHDRAWAL
                .getValue(), savings, createdBy, group));
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();

        payment = helper.createAccountPayment(savings, null, new Money(currency, "500.0"), new Date(), createdBy);
        payment.addAccountTrxn(helper.createAccountTrxn(payment, null, new Money(currency, "500.0"), new Money(
                currency, "4200.0"), helper.getDate("05/03/2006"), null, null, AccountActionTypes.SAVINGS_DEPOSIT
                .getValue(), savings, createdBy, group));
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();

        payment = helper.createAccountPayment(savings, null, new Money(currency, "200.0"), new Date(), createdBy);
        payment.addAccountTrxn(helper.createAccountTrxn(payment, null, new Money(currency, "200"), new Money(currency,
                "4000"), helper.getDate("05/04/2006"), null, null, AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(),
                savings, createdBy, group));
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();

        StaticHibernateUtil.closeSession();

        return TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
    }

    private List<CustomFieldView> getCustomFieldView() {
        List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();
        customFields.add(new CustomFieldView(new Short("8"), "13", CustomFieldType.NONE));
        return customFields;

    }

    private Date getTimeStampDate(final Date date) {
        return new Timestamp(date.getTime());
    }

    private void verifyFields() throws Exception {
       Assert.assertTrue(true);
       Assert.assertEquals(savings.getInterestRate(), savingsOffering.getInterestRate());
       Assert.assertEquals(savings.getMinAmntForInt(), savingsOffering.getMinAmntForInt()
                );
       Assert.assertEquals(savings.getMinAmntForInt(), savingsOffering.getMinAmntForInt());
       Assert.assertEquals(savings.getTimePerForInstcalc().getMeetingDetails().getRecurAfter(), savingsOffering
                .getTimePerForInstcalc().getMeeting().getMeetingDetails().getRecurAfter());
       Assert.assertEquals(1, savings.getAccountCustomFields().size());
        Iterator itr = savings.getAccountCustomFields().iterator();
        AccountCustomFieldEntity customFieldEntity = (AccountCustomFieldEntity) itr.next();
       Assert.assertEquals(Short.valueOf("8"), customFieldEntity.getFieldId());
       Assert.assertEquals("13", customFieldEntity.getFieldValue());
       Assert.assertEquals(AccountTypes.SAVINGS_ACCOUNT, savings.getType());
       Assert.assertEquals(group.getPersonnel().getPersonnelId(), savings.getPersonnel().getPersonnelId());
    }

    public void testInterestAdjustment_LastWithdrawalPaymentNullified_WithMinBal() throws Exception {
        createInitialObjects();
        savingsOffering = createSavingsOfferingForIntCalc("prd1", "cfgh", SavingsType.VOLUNTARY,
                InterestCalcType.MINIMUM_BALANCE, MONTHLY, EVERY_MONTH);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);
        savings.setActivationDate(helper.getDate("20/02/2006"));
        savings.setNextIntCalcDate(helper.getDate("28/02/2006"));
        // add deposit trxn
        Money depositMoney = new Money(currency, "2000.0");
        AccountPaymentEntity payment1 = helper.createAccountPaymentToPersist(savings, depositMoney, depositMoney,
                helper.getDate("20/02/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment1, savings);
        savings.setSavingsBalance(depositMoney);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        // add deposit trxn
        depositMoney = new Money(currency, "1000.0");
        Money balanceAmt = new Money(currency, "3000.0");
        AccountPaymentEntity payment2 = helper.createAccountPaymentToPersist(savings, depositMoney, balanceAmt, helper
                .getDate("24/02/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment2, savings);
        savings.setSavingsBalance(balanceAmt);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        // add withdrawal trxn
        Money withrawalAmt = new Money(currency, "500.0");
        balanceAmt = new Money(currency, "2500.0");
        AccountPaymentEntity payment3 = helper.createAccountPaymentToPersist(savings, withrawalAmt, balanceAmt, helper
                .getDate("26/02/2006"), AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment3, savings);
        savings.setSavingsBalance(balanceAmt);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());

        Money oldInterest = savings.calculateInterestForAdjustment(helper.getDate("25/02/2006"), null);
       Assert.assertEquals(TestUtils.createMoney(), oldInterest);

        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());

        // Min Bal 20/02 - 28/02 = 2000
        // Interest 2000*.12*8/365 = 5.3
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(5.9,
        // savings.getInterestToBePosted());
       Assert.assertEquals(getRoundedMoney(5.3), savings.getInterestToBePosted());

        oldInterest = savings.calculateInterestForAdjustment(helper.getDate("25/02/2006"), null);
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(5.9, oldInterest);
       Assert.assertEquals(getRoundedMoney(5.3), oldInterest);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        Money amountAdjustedTo = new Money(getCurrency());
        try {
            savings.adjustLastUserAction(amountAdjustedTo, "correction entry");
        } catch (ApplicationException ae) {
           Assert.assertTrue(false);
        }
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        // After Adjustmnet minimum balance has not been changed.
        // Min Bal 20/02 - 01/03 = 2000
        // Interest 2000*.12*8/365 = 5.3
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(5.9,
        // savings.getInterestToBePosted());
       Assert.assertEquals(getRoundedMoney(5.3), savings.getInterestToBePosted());

        group = savings.getCustomer();
        center = group.getParentCustomer();

    }

    public void testInterestAdjustment_LastWithdrawalPaymentNullified_WithAvgBal() throws Exception {
        createInitialObjects();
        savingsOffering = createSavingsOfferingForIntCalc("prd1", "cfgh", SavingsType.VOLUNTARY,
                InterestCalcType.AVERAGE_BALANCE, MONTHLY, EVERY_MONTH);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);
        savings.setActivationDate(helper.getDate("20/02/2006"));
        savings.setNextIntCalcDate(helper.getDate("28/02/2006"));
        // add deposit trxn
        Money depositMoney = new Money(currency, "2000.0");
        AccountPaymentEntity payment1 = helper.createAccountPaymentToPersist(savings, depositMoney, depositMoney,
                helper.getDate("20/02/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment1, savings);
        savings.setSavingsBalance(depositMoney);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        // add deposit trxn
        depositMoney = new Money(currency, "1000.0");
        Money balanceAmt = new Money(currency, "3000.0");
        AccountPaymentEntity payment2 = helper.createAccountPaymentToPersist(savings, depositMoney, balanceAmt, helper
                .getDate("24/02/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment2, savings);
        savings.setSavingsBalance(balanceAmt);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        // add withdrawal trxn
        Money withrawalAmt = new Money(currency, "500.0");
        balanceAmt = new Money(currency, "2500.0");
        AccountPaymentEntity payment3 = helper.createAccountPaymentToPersist(savings, withrawalAmt, balanceAmt, helper
                .getDate("26/02/2006"), AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment3, savings);
        savings.setSavingsBalance(balanceAmt);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());

        Money oldInterest = savings.calculateInterestForAdjustment(helper.getDate("25/02/2006"), null);
       Assert.assertEquals(TestUtils.createMoney(), oldInterest);

        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        // from 20/02 - 24/02 = 2000*4 + from 24/02 - 26/02 = 3000*2 + from
        // 26/02 - 28/02 = 2500*2,
        // Therefore Avg Bal (8000+6000+5000)/8 = 19000/8 = 2375
        // Interest = (21500 * .12 / 365 * 8) = 6.2

        // * TODO: financial_calculation_rounding
       Assert.assertEquals(getRoundedMoney(6.3), savings.getInterestToBePosted());

        oldInterest = savings.calculateInterestForAdjustment(helper.getDate("25/02/2006"), null);
        // * TODO: financial_calculation_rounding
       Assert.assertEquals(getRoundedMoney(6.3), oldInterest);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        Money amountAdjustedTo = new Money(getCurrency());
        try {
            savings.adjustLastUserAction(amountAdjustedTo, "correction entry");
        } catch (ApplicationException ae) {
           Assert.assertTrue(false);
        }
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        // from 20/02 - 24/02 = 2000*4 + from 24/02 - 28/02 = 3000*4,
        // Therefore Avg Bal (8000+12000)/9 = 20000/9
        // Interest = (20000 * .12 / 365) = 7.56
        // * TODO: financial_calculation_rounding
       Assert.assertEquals(getRoundedMoney(6.6), savings.getInterestToBePosted());

        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testInterestAdjustment_LastDepositPaymentNullified_WithAvgBal() throws Exception {
        createInitialObjects();
        savingsOffering = createSavingsOfferingForIntCalc("prd1", "cfgh", SavingsType.VOLUNTARY,
                InterestCalcType.AVERAGE_BALANCE, MONTHLY, EVERY_MONTH);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);
        savings.setActivationDate(helper.getDate("20/02/2006"));
        savings.setNextIntCalcDate(helper.getDate("28/02/2006"));

        Money depositMoney = new Money(currency, "2000.0");
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, depositMoney, depositMoney, helper
                .getDate("25/02/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.setSavingsBalance(depositMoney);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        depositMoney = new Money(currency, "5000.0");
        Money balanceAmt = new Money(currency, "7000.0");
        AccountPaymentEntity payment2 = helper.createAccountPaymentToPersist(savings, depositMoney, balanceAmt, helper
                .getDate("27/02/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment2, savings);
        savings.setSavingsBalance(balanceAmt);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());

        Money oldInterest = savings.calculateInterestForAdjustment(helper.getDate("25/02/2006"), null);
       Assert.assertEquals(TestUtils.createMoney(), oldInterest);

        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        // from 25/02 - 27/02 = 2000*2 + from 27/02 - 28/02 = 7000*1 = 11000
        // Therefore avg bal 11000/3 = 3666.667
        // Interest 3666.667*.12*3/365 = 3.616

        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(5.9,
        // savings.getInterestToBePosted());
       Assert.assertEquals(getRoundedMoney(3.7), savings.getInterestToBePosted());

        oldInterest = savings.calculateInterestForAdjustment(helper.getDate("25/02/2006"), null);
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(5.9, oldInterest);
       Assert.assertEquals(getRoundedMoney(3.7), oldInterest);

        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        Money amountAdjustedTo = new Money(getCurrency());

        try {
            savings.adjustLastUserAction(amountAdjustedTo, "correction entry");
        } catch (ApplicationException ae) {
           Assert.assertTrue(false);
        }
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        payment = savings.getLastPmnt();
       Assert.assertEquals(Integer.valueOf(2).intValue(), payment.getAccountTrxns().size());
       Assert.assertEquals(new Money(currency, "2000"), savings.getSavingsBalance());
        // After Adjustment
        // Avg Bal 25/02 - 28/02 = 2000 * 3/3 = 2000
        // Interest 2000*.12*3/365 = 2.0
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(2.6,
        // savings.getInterestToBePosted());
       Assert.assertEquals(getRoundedMoney(2.0), savings.getInterestToBePosted());

        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testInterestAdjustment_LastDepositPaymentNullified_WithMinBal() throws Exception {
        createInitialObjects();
        savingsOffering = createSavingsOfferingForIntCalc("prd1", "cfgh", SavingsType.VOLUNTARY,
                InterestCalcType.MINIMUM_BALANCE, MONTHLY, EVERY_MONTH);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);
        savings.setActivationDate(helper.getDate("20/02/2006"));
        savings.setNextIntCalcDate(helper.getDate("28/02/2006"));

        Money depositMoney = new Money(currency, "2000.0");
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, depositMoney, depositMoney, helper
                .getDate("25/02/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.setSavingsBalance(depositMoney);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());

        Money oldInterest = savings.calculateInterestForAdjustment(helper.getDate("25/02/2006"), null);
       Assert.assertEquals(TestUtils.createMoney(), oldInterest);

        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        // Min Bal 25/02 - 01/03 = 2000
        // Interest 2000*.12*3/365 = 2.0
        //Assert.assertEquals(2.6,
        // savings.getInterestToBePosted());
        // * TODO: financial_calculation_rounding
       Assert.assertEquals(TestUtils.createMoney(2.0), savings.getInterestToBePosted());

        oldInterest = savings.calculateInterestForAdjustment(helper.getDate("25/02/2006"), null);
        // TODO: financial_calculation_rounding
        //Assert.assertEquals(2.6, oldInterest);
       Assert.assertEquals(getRoundedMoney(2.0), oldInterest);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        Money amountAdjustedTo = new Money(getCurrency());

        try {
            savings.adjustLastUserAction(amountAdjustedTo, "correction entry");
        } catch (ApplicationException ae) {
           Assert.assertTrue(false);
        }
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        payment = savings.getLastPmnt();
       Assert.assertEquals(Integer.valueOf(2).intValue(), payment.getAccountTrxns().size());
       Assert.assertEquals(new Money(getCurrency()), savings.getSavingsBalance());

       Assert.assertEquals(getRoundedMoney(0.0), savings.getInterestToBePosted());

        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testIsTrxnDateValid_BeforeFirstMeeting() throws Exception {
        createInitialObjects();
        savingsOffering = TestObjectFactory.createSavingsProduct("dfasdasd1", "sad1",
                RecommendedAmountUnit.COMPLETE_GROUP);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        int i = -5;
        for (AccountActionDateEntity actionDate : savings.getAccountActionDates()) {
            ((SavingsScheduleEntity) actionDate).setActionDate(offSetCurrentDate(i--));
        }
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());

       Assert.assertTrue(savings.isTrxnDateValid(savings.getActivationDate()));
        Assert.assertFalse(savings.isTrxnDateValid(offSetCurrentDate(1)));

        group = TestObjectFactory.getGroup(group.getCustomerId());
    }

    public void testIsTrxnDateValid_AfterFirstMeeting() throws Exception {
        createInitialObjects();
        savingsOffering = TestObjectFactory.createSavingsProduct("dfasdasd1", "sad1",
                RecommendedAmountUnit.COMPLETE_GROUP);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        int i = -5;
        for (AccountActionDateEntity actionDate : savings.getAccountActionDates()) {
            ((SavingsScheduleEntity) actionDate).setActionDate(offSetCurrentDate(i--));
        }
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        java.util.Date trxnDate = offSetCurrentDate(-5);
        if (AccountingRules.isBackDatedTxnAllowed())
           Assert.assertTrue(savings.isTrxnDateValid(trxnDate));
        else
            Assert.assertFalse(savings.isTrxnDateValid(trxnDate));
        group = TestObjectFactory.getGroup(group.getCustomerId());
    }

    public void testSuccessfulSave() throws Exception {
        createInitialObjects();
        savingsOffering = TestObjectFactory.createSavingsProduct("dfasdasd1", "sad1",
                RecommendedAmountUnit.PER_INDIVIDUAL);
        savings = new SavingsBO(userContext, savingsOffering, group, AccountState.SAVINGS_PENDING_APPROVAL, new Money(
                getCurrency(), "100"), getCustomFieldView());
        savings.save();
        StaticHibernateUtil.getTransaction().commit();
        StaticHibernateUtil.closeSession();
        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        verifyFields();
       Assert.assertEquals(AccountState.SAVINGS_PENDING_APPROVAL.getValue(), savings.getAccountState().getId());
       Assert.assertEquals(TestUtils.createMoney(100.0), savings.getRecommendedAmount());
    }

    public void testSuccessfulSaveInApprovedState() throws Exception {
        center = helper.createCenter();
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center);
        client1 = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_CLOSED, group);
        client2 = TestObjectFactory.createClient("client2", CustomerStatus.CLIENT_ACTIVE, group);
        savingsOffering = TestObjectFactory.createSavingsProduct("dfasdasd2", "sad2",
                RecommendedAmountUnit.PER_INDIVIDUAL);
        savings = new SavingsBO(userContext, savingsOffering, group, AccountState.SAVINGS_ACTIVE, savingsOffering
                .getRecommendedAmount(), getCustomFieldView());
        savings.save();
        StaticHibernateUtil.getTransaction().commit();
        StaticHibernateUtil.closeSession();
        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        verifyFields();
       Assert.assertEquals(AccountState.SAVINGS_ACTIVE.getValue(), savings.getAccountState().getId());
       Assert.assertEquals(savingsOffering.getRecommendedAmount(), savings.getRecommendedAmount()
                );

    }

    public void testSuccessfulUpdate() throws Exception {
        createInitialObjects();
        savingsOffering = TestObjectFactory.createSavingsProduct("dfasdasd1", "sad1",
                RecommendedAmountUnit.PER_INDIVIDUAL);
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_PARTIALAPPLICATION, userContext);
        savings.update(new Money(currency, "700.0"), getCustomFieldView());
        StaticHibernateUtil.getTransaction().commit();
        StaticHibernateUtil.closeSession();
        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
       Assert.assertTrue(true);
       Assert.assertEquals(TestUtils.createMoney(700.0), savings.getRecommendedAmount());
    }

    public void testSuccessfulUpdateDepositSchedule() throws Exception {
        createInitialObjects();
        savingsOffering = TestObjectFactory.createSavingsProduct("dfasdasd1", "sad1",
                RecommendedAmountUnit.COMPLETE_GROUP);
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        savings.update(new Money(currency, "700.0"), getCustomFieldView());

        StaticHibernateUtil.getTransaction().commit();
        StaticHibernateUtil.closeSession();
        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
       Assert.assertTrue(true);
       Assert.assertEquals(TestUtils.createMoney(700.0), savings.getRecommendedAmount());
        Set<AccountActionDateEntity> actionDates = savings.getAccountActionDates();
        Assert.assertNotNull(actionDates);
        for (AccountActionDateEntity entity : actionDates) {
            if (entity.getActionDate().compareTo(new java.sql.Date(System.currentTimeMillis())) > 0)
               Assert.assertEquals(TestUtils.createMoney(700.0), ((SavingsScheduleEntity) entity).getDeposit());
        }
    }

    public void testGetMinimumBalance() throws Exception {
        savings = createSavingsWithAccountPayments();
        SavingsTrxnDetailEntity initialTrxn = new SavingsPersistence().retrieveFirstTransaction(savings.getAccountId());
        boolean initialBalance = false;
        Money minBal = savings.getMinimumBalance(getTimeStampDate(helper.getDate("05/01/2006")),
                getTimeStampDate(helper.getDate("10/05/2006")), initialTrxn, null, initialBalance);
       Assert.assertEquals(TestUtils.createMoney("4000"), minBal);
        TestObjectFactory.flushandCloseSession();
        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        group = TestObjectFactory.getGroup(savings.getCustomer().getCustomerId());
        center = TestObjectFactory.getCenter(group.getParentCustomer().getCustomerId());
    }

    // Following tranxs are used for average calculation Date Days Amount
    // (01/01/2006 - 10/01/06) 05 5500*5 + (10/01/2006 - 16/02/06) 37 7500*37 +
    // (16/02/2006 - 05/03/06) 17 4000*17 + (05/03/2006 - 05/04/06) 31 4200*31 +
    // (05/04/2006 - 10/04/06) 05 4000*5 = 523200 Avg = 523200/95 = 5507.3

    public void testGetAverageBalance() throws Exception {
        savings = createSavingsWithAccountPayments();
        SavingsTrxnDetailEntity initialTrxn = new SavingsPersistence().retrieveLastTransaction(savings.getAccountId(),
                helper.getDate("05/01/2006"));
        Money avgBalance = savings.getAverageBalance(getTimeStampDate(helper.getDate("05/01/2006")),
                getTimeStampDate(helper.getDate("10/04/2006")), initialTrxn, null);
       Assert.assertEquals(getRoundedMoney(5507.4), getRoundedMoney(avgBalance));
        TestObjectFactory.flushandCloseSession();
        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        group = TestObjectFactory.getGroup(savings.getCustomer().getCustomerId());
        center = TestObjectFactory.getCenter(group.getParentCustomer().getCustomerId());
    }

    public void testCalculateInterestForClosureAvgBal() throws Exception {
        createInitialObjects();
        savingsOffering = TestObjectFactory.createSavingsProduct("dfasdasd1", "sad1",
                RecommendedAmountUnit.PER_INDIVIDUAL);
        InterestCalcTypeEntity intType = new InterestCalcTypeEntity(InterestCalcType.AVERAGE_BALANCE);
        savingsOffering.setInterestCalcType(intType);
        savings = new SavingsBO(userContext, savingsOffering, group, AccountState.SAVINGS_ACTIVE, savingsOffering
                .getRecommendedAmount(), null);
        savings.save();
        StaticHibernateUtil.commitTransaction();
        savings.setActivationDate(helper.getDate("15/05/2006"));
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, new Money(currency, "1000.0"),
                new Money(currency, "1000.0"), helper.getDate("20/05/2006"), AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        savings.getSavingsOffering().setMinAmntForInt(new Money(getCurrency(), "0"));
        Money amount = savings.calculateInterestForClosure(helper.getDate("30/05/2006"));
       Assert.assertEquals(getRoundedMoney(6.6), amount);
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testCalculateInterestForClosureWithMinValueForIntCalc() throws Exception {
        createInitialObjects();
        savingsOffering = TestObjectFactory.createSavingsProduct("dfasdasd1", "sad1",
                RecommendedAmountUnit.PER_INDIVIDUAL);
        InterestCalcTypeEntity intType = new InterestCalcTypeEntity(InterestCalcType.AVERAGE_BALANCE);
        savingsOffering.setInterestCalcType(intType);
        savingsOffering.setMinAmntForInt(new Money(getCurrency(), "5000"));
        savings = new SavingsBO(userContext, savingsOffering, group, AccountState.SAVINGS_ACTIVE, savingsOffering
                .getRecommendedAmount(), null);
        savings.save();
        StaticHibernateUtil.commitTransaction();
        savings.setActivationDate(helper.getDate("15/05/2006"));
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, new Money(currency, "1000.0"),
                new Money(currency, "1000.0"), helper.getDate("20/05/2006"), AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        Money intAmount = savings.calculateInterestForClosure(helper.getDate("30/05/2006"));
        Assert.assertEquals(TestUtils.createMoney(), intAmount);
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testCalculateInterestForClosure() throws Exception {
        // using min balance for interest calculation
        // from 15/01/2006 to 10/03/2006 = 85 no of days
        // interest Rate 24% per anum
        // interest = minBal 1400 * 24%/(365*100)*85 = 78.2465753425
        // using default currency rounding (CEILING) and DIGITS_AFTER_DECIMAL
        // (1)
        // rounded to 78.3

        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = new SavingsBO(userContext, savingsOffering, group, AccountState.SAVINGS_ACTIVE, savingsOffering
                .getRecommendedAmount(), null);
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, new Money(currency, "700.0"),
                new Money(currency, "1700.0"), helper.getDate("15/01/2006"), AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.save();
        StaticHibernateUtil.commitTransaction();
        savings.setActivationDate(helper.getDate("05/01/2006"));
        payment = helper.createAccountPaymentToPersist(savings, new Money(currency, "1000.0"), new Money(currency,
                "2700.0"), helper.getDate("20/02/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings,
                createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        payment = helper.createAccountPaymentToPersist(savings, new Money(currency, "500.0"), new Money(currency,
                "2200.0"), helper.getDate("10/03/2006"), AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(), savings,
                createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        payment = helper.createAccountPaymentToPersist(savings, new Money(currency, "1200.0"), new Money(currency,
                "3400.0"), helper.getDate("15/03/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings,
                createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        payment = helper.createAccountPaymentToPersist(savings, new Money(currency, "2000.0"), new Money(currency,
                "1400.0"), helper.getDate("25/03/2006"), AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(), savings,
                createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();

        // remove workarounds like this when rounding rules are working
        // double intAmount = savings.calculateInterestForClosure(
        // helper.getDate("10/04/2006"));
        //Assert.assertEquals(Double.valueOf("78.2"), intAmount));

        Money roundedInterestForClosure = MoneyUtils.currencyRound(
                savings.calculateInterestForClosure(helper.getDate("10/04/2006")));
       Assert.assertEquals(getRoundedMoney(78.3), roundedInterestForClosure);
    }

    public void testIsMandatory() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
       Assert.assertEquals(Boolean.valueOf(false).booleanValue(), savings.isMandatory());
    }

    public void testIsDepositScheduleBeRegenerated() throws Exception {
        createInitialObjects();
        savingsOffering = TestObjectFactory.createSavingsProduct("dfasdasd1", "sad1",
                RecommendedAmountUnit.PER_INDIVIDUAL);
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
       Assert.assertEquals(Boolean.valueOf(false).booleanValue(), savings.isMandatory());
    }

    public void testGenerateAndUpdateDepositActionsForClient() throws Exception {
        center = helper.createCenter();
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center);
        savingsOffering = TestObjectFactory.createSavingsProduct("dfasdasd1", "sad1",
                RecommendedAmountUnit.PER_INDIVIDUAL);

        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group, AccountState.SAVINGS_ACTIVE,
                userContext);
        StaticHibernateUtil.closeSession();

        group = (GroupBO) StaticHibernateUtil.getSessionTL().get(GroupBO.class, group.getCustomerId());
        // This calls savings.generateAndUpdateDepositActionsForClient
        // which is what this test is all about testing.
        client1 = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        group = savings.getCustomer();
        center = group.getParentCustomer();
       Assert.assertEquals(10, savings.getAccountActionDates().size());
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        client1 = customerPersistence.getCustomer(client1.getCustomerId());
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testSuccessfulWithdraw() throws AccountException, Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client1 = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = TestObjectFactory.createSavingsAccount("43245434", client1, Short.valueOf("16"), new Date(System
                .currentTimeMillis()), savingsOffering);

        StaticHibernateUtil.commitTransaction();
        savings = (SavingsBO) accountPersistence.getAccount(savings.getAccountId());
        savings.setSavingsBalance(TestUtils.createMoney("100.0"));
        Money enteredAmount = new Money(currency, "100.0");
        PaymentData paymentData = PaymentData.createPaymentData(enteredAmount, savings.getPersonnel(), Short
                .valueOf("1"), new Date(System.currentTimeMillis()));
        paymentData.setCustomer(client1);
        paymentData.setReceiptDate(new Date(System.currentTimeMillis()));
        paymentData.setReceiptNum("34244");
        boolean persist = true;
        savings.withdraw(paymentData, persist);
       Assert.assertEquals(TestUtils.createMoney(), savings.getSavingsBalance());
       Assert.assertEquals(1, savings.getSavingsActivityDetails().size());
        savings.getAccountPayments().clear();
    }

    public void testSuccessfulApplyPayment() throws AccountException, Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client1 = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = TestObjectFactory.createSavingsAccount("43245434", client1, AccountStates.SAVINGS_ACC_INACTIVE,
                new Date(System.currentTimeMillis()), savingsOffering);

        StaticHibernateUtil.closeSession();
        savings = (SavingsBO) accountPersistence.getAccount(savings.getAccountId());
        savings.setSavingsBalance(new Money(getCurrency()));

        Money enteredAmount = new Money(currency, "100.0");
        PaymentData paymentData = PaymentData.createPaymentData(enteredAmount, savings.getPersonnel(), Short
                .valueOf("1"), new Date(System.currentTimeMillis()));
        paymentData.setCustomer(client1);
        paymentData.setReceiptDate(new Date(System.currentTimeMillis()));
        paymentData.setReceiptNum("34244");
        AccountActionDateEntity accountActionDate = savings.getAccountActionDate(Short.valueOf("1"));

        SavingsPaymentData savingsPaymentData = new SavingsPaymentData(accountActionDate);
        paymentData.addAccountPaymentData(savingsPaymentData);
        savings.applyPaymentWithPersist(paymentData);
       Assert.assertEquals(AccountStates.SAVINGS_ACC_APPROVED, savings.getAccountState().getId().shortValue());
       Assert.assertEquals(TestUtils.createMoney(100.0), savings.getSavingsBalance());
       Assert.assertEquals(1, savings.getSavingsActivityDetails().size());
        savings.getAccountPayments().clear();
        client1 = customerPersistence.getCustomer(client1.getCustomerId());
    }

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
        StaticHibernateUtil.closeSession();
        savings = (SavingsBO) accountPersistence.getAccount(savings.getAccountId());

        Money enteredAmount = new Money(currency, "100.0");
        PaymentData paymentData = PaymentData.createPaymentData(enteredAmount, savings.getPersonnel(), Short
                .valueOf("1"), new Date(System.currentTimeMillis()));
        paymentData.setCustomer(client1);
        paymentData.setReceiptDate(new Date(System.currentTimeMillis()));
        paymentData.setReceiptNum("34244");
        AccountActionDateEntity accountActionDate = savings.getAccountActionDate(Short.valueOf("1"));

        SavingsPaymentData savingsPaymentData = new SavingsPaymentData(accountActionDate);
        paymentData.addAccountPaymentData(savingsPaymentData);
        savings.applyPaymentWithPersist(paymentData);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = (SavingsBO) accountPersistence.getAccount(savings.getAccountId());
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
        client1 = customerPersistence.getCustomer(client1.getCustomerId());
    }

    public void testSuccessfulApplyPaymentWhenNoDepositDue() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = helper.createSavingsAccount("000X00000000013", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        savings.setUserContext(TestObjectFactory.getContext());
        savings.changeStatus(AccountState.SAVINGS_CANCELLED.getValue(), null, "");

        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        Money enteredAmount = new Money(currency, "100.0");
        PaymentData paymentData = PaymentData.createPaymentData(enteredAmount, savings.getPersonnel(), Short
                .valueOf("1"), new Date(System.currentTimeMillis()));
        paymentData.setCustomer(group);
        paymentData.setReceiptDate(new Date(System.currentTimeMillis()));
        paymentData.setReceiptNum("34244");
        paymentData.addAccountPaymentData(getSavingsPaymentdata(null));
        savings.applyPaymentWithPersist(paymentData);

        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());

       Assert.assertEquals(AccountStates.SAVINGS_ACC_APPROVED, savings.getAccountState().getId().shortValue());
       Assert.assertEquals(TestUtils.createMoney(100.0), savings.getSavingsBalance());
       Assert.assertEquals(1, savings.getSavingsActivityDetails().size());
    }

    private SavingsPaymentData getSavingsPaymentdata(final CollectionSheetEntryInstallmentView bulkEntryAccountActionView) {
        return new SavingsPaymentData(bulkEntryAccountActionView);
    }

    public void testMaxWithdrawAmount() throws AccountException, Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client1 = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = TestObjectFactory.createSavingsAccount("43245434", client1, Short.valueOf("16"), new Date(System
                .currentTimeMillis()), savingsOffering);

        StaticHibernateUtil.commitTransaction();
        savings = (SavingsBO) accountPersistence.getAccount(savings.getAccountId());
        savings.setSavingsBalance(TestUtils.createMoney("100.0"));
        Money enteredAmount = new Money(currency, "300.0");
        PaymentData paymentData = PaymentData.createPaymentData(enteredAmount, savings.getPersonnel(), Short
                .valueOf("1"), new Date(System.currentTimeMillis()));
        paymentData.setCustomer(client1);
        paymentData.setReceiptDate(new Date(System.currentTimeMillis()));
        paymentData.setReceiptNum("34244");
        try {
            boolean persist = true;
            savings.withdraw(paymentData, persist);
           Assert.assertTrue("No Exception is thrown. Even amount greater than max withdrawal allowed to be withdrawn", false);
        } catch (AccountException ae) {
           Assert.assertTrue("Exception is thrown. Amount greater than max withdrawal not allowed to be withdrawn", true);
        }
        savings.getAccountPayments().clear();
    }

    public void testSuccessfulFlagSave() throws Exception {
        Session session = StaticHibernateUtil.getSessionTL();
        StaticHibernateUtil.startTransaction();
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = helper.createSavingsAccount("000X00000000013", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        savings.setUserContext(TestObjectFactory.getContext());
        savings.changeStatus(AccountState.SAVINGS_CANCELLED.getValue(), null, "");

        savings.setUserContext(this.userContext);
        AccountStateEntity state = (AccountStateEntity) session.get(AccountStateEntity.class, (short) 15);
        for (AccountStateFlagEntity flag : state.getFlagSet())
            AccountTestUtils.addAccountFlag(flag, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        session = StaticHibernateUtil.getSessionTL();
        SavingsBO savingsNew = (SavingsBO) (session.get(SavingsBO.class, Integer.valueOf(savings.getAccountId())));
       Assert.assertEquals(savingsNew.getAccountFlags().size(), 3);
        session.evict(savingsNew);
        StaticHibernateUtil.closeSession();
    }

    public void testChangeStatusPermissionToPendingApprovalSucess() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_PARTIALAPPLICATION, userContext);
        AccountStateMachines.getInstance().initialize((short) 1, (short) 1, AccountTypes.SAVINGS_ACCOUNT, null);
        savings.changeStatus(AccountState.SAVINGS_PENDING_APPROVAL.getValue(), null, "notes");
       Assert.assertEquals(AccountStates.SAVINGS_ACC_PENDINGAPPROVAL, savings.getAccountState().getId().shortValue());

    }

    public void testChangeStatusPermissionToCancelBlacklistedSucess() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_PENDINGAPPROVAL, userContext);
        AccountStateMachines.getInstance().initialize((short) 1, (short) 1, AccountTypes.SAVINGS_ACCOUNT, null);
        // 6 is blacklisted

        savings.changeStatus(AccountState.SAVINGS_CANCELLED.getValue(), Short.valueOf("6"), "notes");
       Assert.assertEquals(AccountStates.SAVINGS_ACC_CANCEL, savings.getAccountState().getId().shortValue());

    }

    public void testIsAdjustPossibleOnLastTrxn_OnPartialAccount() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_PARTIALAPPLICATION, userContext);
        Money amountAdjustedTo = new Money(currency, "500.0");
        boolean isAdjustPossible = savings.isAdjustPossibleOnLastTrxn(amountAdjustedTo);
       Assert.assertEquals(savings.getAccountState().getId().shortValue(), AccountStates.SAVINGS_ACC_PARTIALAPPLICATION);
       Assert.assertEquals(Boolean.FALSE.booleanValue(), isAdjustPossible);
    }

    public void testIsAdjustPossibleOnLastTrxn_NoLastPayment() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        Money amountAdjustedTo = new Money(currency, "500.0");
        boolean isAdjustPossible = savings.isAdjustPossibleOnLastTrxn(amountAdjustedTo);
       Assert.assertEquals(savings.getAccountState().getId().shortValue(), AccountStates.SAVINGS_ACC_APPROVED);
        Assert.assertNull(savings.getLastPmnt());
       Assert.assertEquals(Boolean.FALSE.booleanValue(), isAdjustPossible);
    }

    public void testIsAdjustPossibleOnLastTrxn_LastPaymentIsInterestPosting() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, new Money(currency, "1000.0"),
                new Money(currency, "1000.0"), helper.getDate("20/05/2006"),
                AccountActionTypes.SAVINGS_INTEREST_POSTING.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        Money amountAdjustedTo = new Money(currency, "500.0");
        boolean isAdjustPossible = savings.isAdjustPossibleOnLastTrxn(amountAdjustedTo);
       Assert.assertEquals(savings.getAccountState().getId().shortValue(), AccountStates.SAVINGS_ACC_APPROVED);
        Assert.assertNotNull(savings.getLastPmnt());
       Assert.assertEquals(Boolean.FALSE.booleanValue(), isAdjustPossible);
    }

    public void testIsAdjustPossibleOnLastTrxn_AmountSame() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, new Money(currency, "1000.0"),
                new Money(currency, "1000.0"), helper.getDate("20/05/2006"), AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        Money amountAdjustedTo = new Money(currency, "1000.0");
        boolean isAdjustPossible = savings.isAdjustPossibleOnLastTrxn(amountAdjustedTo);
       Assert.assertEquals(savings.getAccountState().getId().shortValue(), AccountStates.SAVINGS_ACC_APPROVED);
        Assert.assertNotNull(savings.getLastPmnt());
       Assert.assertEquals(Boolean.FALSE.booleanValue(), isAdjustPossible);
    }

    public void testIsAdjustPossibleOnLastTrxn_MaxWithdrawalAmntIsLess() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, new Money(currency, "1000.0"),
                new Money(currency, "1000.0"), helper.getDate("20/05/2006"), AccountActionTypes.SAVINGS_WITHDRAWAL
                        .getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        savingsOffering.setMaxAmntWithdrawl(new Money(getCurrency(), "1200"));
        Money amountAdjustedTo = new Money(currency, "1500.0");
        boolean isAdjustPossible = savings.isAdjustPossibleOnLastTrxn(amountAdjustedTo);
       Assert.assertEquals(savings.getAccountState().getId().shortValue(), AccountStates.SAVINGS_ACC_APPROVED);
        Assert.assertNotNull(savings.getLastPmnt());
       Assert.assertEquals(Boolean.FALSE.booleanValue(), isAdjustPossible);
    }

    public void testIsAdjustPossibleOnLastTrxn_Balance_Negative() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, new Money(currency, "1000.0"),
                new Money(currency, "1000.0"), helper.getDate("20/05/2006"), AccountActionTypes.SAVINGS_WITHDRAWAL
                        .getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.setSavingsBalance(new Money(currency, "400.0"));
        savings.update();

        StaticHibernateUtil.commitTransaction();
        savingsOffering.setMaxAmntWithdrawl(new Money(getCurrency(), "2500"));
        Money amountAdjustedTo = new Money(currency, "2000.0");
        boolean isAdjustPossible = savings.isAdjustPossibleOnLastTrxn(amountAdjustedTo);
       Assert.assertEquals(savings.getAccountState().getId().shortValue(), AccountStates.SAVINGS_ACC_APPROVED);
        Assert.assertNotNull(savings.getLastPmnt());
       Assert.assertEquals(savings.getSavingsBalance(), new Money(currency, "400.0"));
       Assert.assertEquals(Boolean.FALSE.booleanValue(), isAdjustPossible);
    }

    // When IsAdjustPossibleOnLastTrxn returns false.

    public void testAdjustPmntFailure() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, new Money(currency, "1000.0"),
                new Money(currency, "1000.0"), helper.getDate("20/05/2006"), AccountActionTypes.SAVINGS_WITHDRAWAL
                        .getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.setSavingsBalance(new Money(currency, "400.0"));
        savings.update();
        StaticHibernateUtil.commitTransaction();
        savingsOffering.setMaxAmntWithdrawl(new Money(getCurrency(), "2500"));
        Money amountAdjustedTo = new Money(currency, "2000.0");
        try {
            savings.adjustLastUserAction(amountAdjustedTo, "correction entry");
        } catch (ApplicationException ae) {
           Assert.assertTrue(true);
           Assert.assertEquals("exception.accounts.ApplicationException.CannotAdjust", ae.getKey());
        }

       Assert.assertEquals(savings.getAccountState().getId().shortValue(), AccountStates.SAVINGS_ACC_APPROVED);
        Assert.assertNotNull(savings.getLastPmnt());
       Assert.assertEquals(savings.getSavingsBalance(), new Money(currency, "400.0"));
    }

    public void testAdjustPmnt_LastPaymentNullified() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, new Money(currency, "1000.0"),
                new Money(currency, "1000.0"), helper.getDate("20/05/2006"), AccountActionTypes.SAVINGS_WITHDRAWAL
                        .getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.setSavingsBalance(new Money(currency, "400.0"));
        savings.update();
        savingsOffering.setMaxAmntWithdrawl(new Money(getCurrency(), "2500"));
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        savings.getSavingsOffering().setMaxAmntWithdrawl(new Money(getCurrency(), "2500"));
        savings.setUserContext(userContext);
        Money amountAdjustedTo = new Money(getCurrency());
       Assert.assertEquals(Integer.valueOf(1).intValue(), payment.getAccountTrxns().size());
        try {
            savings.adjustLastUserAction(amountAdjustedTo, "correction entry");
        } catch (ApplicationException ae) {
           Assert.assertTrue(false);
        }
        payment = savings.getLastPmnt();
       Assert.assertEquals(Integer.valueOf(2).intValue(), payment.getAccountTrxns().size());
       Assert.assertEquals(savings.getSavingsBalance(), new Money(currency, "1400.0"));
       Assert.assertEquals(Integer.valueOf("1").intValue(), savings.getSavingsActivityDetails().size());
        for (SavingsActivityEntity activity : savings.getSavingsActivityDetails()) {
           Assert.assertEquals(new Money(currency, "1000.0"), activity.getAmount());
           Assert.assertEquals(new Money(currency, "1400.0"), activity.getBalanceAmount());
           Assert.assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(), DateUtils.getDateWithoutTimeStamp(activity
                    .getTrxnCreatedDate().getTime()));
        }
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testAdjustPmnt_LastPaymentDecreasedForWithdrawal() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savingsOffering.setMaxAmntWithdrawl(new Money(getCurrency(), "2500"));
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        Money withdrawalAmount = new Money(currency, "1000.0");
        Money balanceAmount = new Money(currency, "4500.0");
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, withdrawalAmount, balanceAmount,
                helper.getDate("20/05/2006"), AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(), savings, createdBy,
                group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.setSavingsBalance(balanceAmount);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);

       Assert.assertEquals(Integer.valueOf(1).intValue(), payment.getAccountTrxns().size());
        Money amountAdjustedTo = new Money(currency, "500.0");

        payment = savings.getLastPmnt();

        savings.adjustLastUserAction(amountAdjustedTo, "correction entry");
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());

       Assert.assertEquals(Integer.valueOf(2).intValue(), savings.getAccountPayments().size());
       Assert.assertEquals(Integer.valueOf(2).intValue(), payment.getAccountTrxns().size());
       Assert.assertEquals(new Money(getCurrency()), payment.getAmount());
       Assert.assertEquals(savings.getSavingsBalance(), new Money(currency, "5000.0"));
       Assert.assertEquals(amountAdjustedTo, savings.getLastPmnt().getAmount());

       Assert.assertEquals(Integer.valueOf("2").intValue(), savings.getSavingsActivityDetails().size());
        for (SavingsActivityEntity activity : savings.getSavingsActivityDetails()) {
            if (activity.getActivity().getId().equals(AccountActionTypes.SAVINGS_ADJUSTMENT.getValue())) {
               Assert.assertEquals(new Money(currency, "1000.0"), activity.getAmount());
               Assert.assertEquals(new Money(currency, "5500.0"), activity.getBalanceAmount());
               Assert.assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(), DateUtils.getDateWithoutTimeStamp(activity
                        .getTrxnCreatedDate().getTime()));
            } else {
               Assert.assertEquals(new Money(currency, "500.0"), activity.getAmount());
               Assert.assertEquals(new Money(currency, "5000.0"), activity.getBalanceAmount());
               Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(getDate("20/05/2006").getTime()), DateUtils
                        .getDateWithoutTimeStamp(activity.getTrxnCreatedDate().getTime()));
            }
        }
        Hibernate.initialize(savings.getAccountActionDates());
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testAdjustPmnt_LastPaymentIncreasedForWithdrawal() throws Exception {
        createInitialObjects();
        client1 = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savingsOffering.setMaxAmntWithdrawl(new Money(getCurrency(), "2500"));
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        Money withdrawalAmount = new Money(currency, "1000.0");
        Money balanceAmount = new Money(currency, "4500.0");
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, withdrawalAmount, balanceAmount,
                helper.getDate("20/05/2006"), AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(), savings, createdBy,
                client1);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.setSavingsBalance(balanceAmount);
        savings.update();
        savings.changeStatus(AccountState.SAVINGS_INACTIVE.getValue(), null, "status changed");
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);

       Assert.assertEquals(Integer.valueOf(1).intValue(), payment.getAccountTrxns().size());
        Money amountAdjustedTo = new Money(currency, "1900.0");

        payment = savings.getLastPmnt();

        savings.adjustLastUserAction(amountAdjustedTo, "correction entry");
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
       Assert.assertEquals(AccountState.SAVINGS_ACTIVE.getValue(), savings.getAccountState().getId());
       Assert.assertEquals(Integer.valueOf(2).intValue(), savings.getAccountPayments().size());
       Assert.assertEquals(Integer.valueOf(2).intValue(), payment.getAccountTrxns().size());
        Integer clientId = client1.getCustomerId();

        for (AccountPaymentEntity payment1 : savings.getAccountPayments()) {
            for (AccountTrxnEntity accountTrxn : payment1.getAccountTrxns()) {
                SavingsTrxnDetailEntity trxn = (SavingsTrxnDetailEntity) accountTrxn;
               Assert.assertEquals(clientId, trxn.getCustomer().getCustomerId());
            }
        }
       Assert.assertEquals(new Money(getCurrency()), payment.getAmount());
       Assert.assertEquals(new Money(currency, "3600.0"), savings.getSavingsBalance());
       Assert.assertEquals(amountAdjustedTo, savings.getLastPmnt().getAmount());

        Hibernate.initialize(savings.getAccountActionDates());
        group = savings.getCustomer();
        center = group.getParentCustomer();
        client1 = TestObjectFactory.getClient(client1.getCustomerId());
    }

    public void testAdjustPmnt_LastPaymentDepositVol_without_schedule() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savingsOffering.setMaxAmntWithdrawl(new Money(getCurrency(), "2500"));
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        Money depositAmount = new Money(currency, "1000.0");
        Money balanceAmount = new Money(currency, "4500.0");

        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, depositAmount, balanceAmount,
                helper.getDate("20/05/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.setSavingsBalance(balanceAmount);
        savings.update();
        savings.changeStatus(AccountState.SAVINGS_INACTIVE.getValue(), null, "changedInactive");
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);

       Assert.assertEquals(Integer.valueOf(1).intValue(), payment.getAccountTrxns().size());
        Money amountAdjustedTo = new Money(currency, "2000.0");

        payment = savings.getLastPmnt();

        savings.adjustLastUserAction(amountAdjustedTo, "correction entry");
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
       Assert.assertEquals(AccountState.SAVINGS_ACTIVE.getValue(), savings.getAccountState().getId());
       Assert.assertEquals(Integer.valueOf(2).intValue(), savings.getAccountPayments().size());
       Assert.assertEquals(Integer.valueOf(2).intValue(), payment.getAccountTrxns().size());
       Assert.assertEquals(new Money(getCurrency()), payment.getAmount());
       Assert.assertEquals(new Money(currency, "5500.0"), savings.getSavingsBalance());
       Assert.assertEquals(amountAdjustedTo, savings.getLastPmnt().getAmount());

        for (SavingsActivityEntity activity : savings.getSavingsActivityDetails()) {
            if (activity.getActivity().getId().equals(AccountActionTypes.SAVINGS_ADJUSTMENT.getValue())) {
               Assert.assertEquals(new Money(currency, "1000.0"), activity.getAmount());
               Assert.assertEquals(new Money(currency, "3500.0"), activity.getBalanceAmount());
               Assert.assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(), DateUtils.getDateWithoutTimeStamp(activity
                        .getTrxnCreatedDate().getTime()));
            } else {
               Assert.assertEquals(new Money(currency, "2000.0"), activity.getAmount());
               Assert.assertEquals(new Money(currency, "5500.0"), activity.getBalanceAmount());
               Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(getDate("20/05/2006").getTime()), DateUtils
                        .getDateWithoutTimeStamp(activity.getTrxnCreatedDate().getTime()));
            }
        }
        Hibernate.initialize(savings.getAccountActionDates());
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testAdjustPmnt_LastPaymentDepositMandatory_PaidAllDue() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        Money recommendedAmnt = new Money(currency, "500.0");
        Date paymentDate = helper.getDate("09/05/2006");
        AccountActionDateEntity actionDate1 = helper.createAccountActionDate(savings, Short.valueOf("1"), helper
                .getDate("01/05/2006"), paymentDate, savings.getCustomer(), recommendedAmnt, recommendedAmnt,
                PaymentStatus.PAID);
        AccountActionDateEntity actionDate2 = helper.createAccountActionDate(savings, Short.valueOf("2"), helper
                .getDate("08/05/2006"), paymentDate, savings.getCustomer(), recommendedAmnt, recommendedAmnt,
                PaymentStatus.PAID);
        AccountTestUtils.addAccountActionDate(actionDate1, savings);
        AccountTestUtils.addAccountActionDate(actionDate2, savings);

        Money depositAmount = new Money(currency, "1200.0");

        // Adding 1 account payment of Rs 1200. With three transactions of
        // (500
        // + 500 + 200).
        AccountPaymentEntity payment = helper.createAccountPayment(savings, depositAmount, paymentDate, createdBy);
        Money balanceAmount = new Money(currency, "4500.0");
        SavingsTrxnDetailEntity trxn1 = helper.createAccountTrxn(payment, Short.valueOf("1"), recommendedAmnt,
                balanceAmount, paymentDate, helper.getDate("01/05/2006"), null, AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);

        balanceAmount = new Money(currency, "5000.0");
        SavingsTrxnDetailEntity trxn2 = helper.createAccountTrxn(payment, Short.valueOf("2"), recommendedAmnt,
                balanceAmount, paymentDate, helper.getDate("08/05/2006"), null, AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);

        balanceAmount = new Money(currency, "5200.0");
        SavingsTrxnDetailEntity trxn3 = helper.createAccountTrxn(payment, null, new Money(currency, "200.0"),
                balanceAmount, paymentDate, null, null, AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings,
                createdBy, group);

        payment.addAccountTrxn(trxn1);
        payment.addAccountTrxn(trxn2);
        payment.addAccountTrxn(trxn3);
        AccountTestUtils.addAccountPayment(payment, savings);

        savings.setSavingsBalance(balanceAmount);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        payment = savings.getLastPmnt();
        for (AccountTrxnEntity accountTrxn : payment.getAccountTrxns()) {
            Hibernate.initialize(accountTrxn.getFinancialTransactions());
        }
       Assert.assertEquals(Integer.valueOf(3).intValue(), payment.getAccountTrxns().size());

        // Adjust last deposit of 1200 to 2000.
        Money amountAdjustedTo = new Money(currency, "2000.0");
        SavingsTypeEntity savingsType = new SavingsTypeEntity(SavingsType.MANDATORY);
        savings.setSavingsType(savingsType);
        savings.adjustLastUserAction(amountAdjustedTo, "correction entry");

        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        AccountPaymentEntity newPayment = savings.getLastPmnt();
       Assert.assertEquals(Integer.valueOf(2).intValue(), savings.getAccountPayments().size());
       Assert.assertEquals(Integer.valueOf(6).intValue(), payment.getAccountTrxns().size());
        int countFinancialTrxns = 0;
        for (AccountTrxnEntity accountTrxn : payment.getAccountTrxns()) {
            countFinancialTrxns += accountTrxn.getFinancialTransactions().size();
            if (accountTrxn.getAccountActionEntity().getId().equals(AccountActionTypes.SAVINGS_ADJUSTMENT.getValue()))
               Assert.assertEquals(userContext.getId(), accountTrxn.getPersonnel().getPersonnelId());
            for (FinancialTransactionBO finTrxn : accountTrxn.getFinancialTransactions()) {
               Assert.assertEquals("correction entry", finTrxn.getNotes());
               Assert.assertEquals(userContext.getId(), finTrxn.getPostedBy().getPersonnelId());
            }
        }
       Assert.assertEquals(Integer.valueOf(6).intValue(), countFinancialTrxns);
       Assert.assertEquals(payment.getAmount(), new Money(getCurrency()));

       Assert.assertEquals(new Money(currency, "6000.0"), savings.getSavingsBalance());
       Assert.assertEquals(newPayment.getAmount(), amountAdjustedTo);
       Assert.assertEquals(Integer.valueOf(3).intValue(), newPayment.getAccountTrxns().size());

        Hibernate.initialize(savings.getAccountActionDates());
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testAdjustPmnt_LastPaymentDepositMandatory_PaidSomeDueInstallments() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        Money recommendedAmnt = new Money(currency, "500.0");
        Date paymentDate = helper.getDate("08/05/2006");
        AccountActionDateEntity actionDate1 = helper.createAccountActionDate(savings, Short.valueOf("1"), helper
                .getDate("01/05/2006"), paymentDate, savings.getCustomer(), recommendedAmnt, recommendedAmnt,
                PaymentStatus.PAID);
        AccountActionDateEntity actionDate2 = helper.createAccountActionDate(savings, Short.valueOf("2"), helper
                .getDate("08/05/2006"), null, savings.getCustomer(), recommendedAmnt, null, PaymentStatus.UNPAID);

        AccountTestUtils.addAccountActionDate(actionDate1, savings);
        AccountTestUtils.addAccountActionDate(actionDate2, savings);

        Money depositAmount = new Money(currency, "500.0");

        // Adding 1 account payment of Rs 500. With one transactions of 500.
        AccountPaymentEntity payment = helper.createAccountPayment(savings, depositAmount, paymentDate, createdBy);
        Money balanceAmount = new Money(currency, "4500.0");

        SavingsTrxnDetailEntity trxn1 = helper.createAccountTrxn(payment, Short.valueOf("1"), recommendedAmnt,
                balanceAmount, paymentDate, helper.getDate("01/05/2006"), null, AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);

        payment.addAccountTrxn(trxn1);
        AccountTestUtils.addAccountPayment(payment, savings);

        savings.setSavingsBalance(balanceAmount);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        payment = savings.getLastPmnt();

       Assert.assertEquals(Integer.valueOf(1).intValue(), payment.getAccountTrxns().size());

        // Adjust last deposit of 500 to 1200.
        Money amountAdjustedTo = new Money(currency, "1200.0");
        SavingsTypeEntity savingsType = new SavingsTypeEntity(SavingsType.MANDATORY);
        savings.setSavingsType(savingsType);
        savings.adjustLastUserAction(amountAdjustedTo, "correction entry");

        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());

        AccountPaymentEntity newPayment = savings.getLastPmnt();
        for (AccountTrxnEntity accountTrxn : newPayment.getAccountTrxns()) {
            if (accountTrxn.getInstallmentId() != null && accountTrxn.getInstallmentId().shortValue() == 1)
               Assert.assertTrue(true);
        }

       Assert.assertEquals(Integer.valueOf(2).intValue(), savings.getAccountPayments().size());
       Assert.assertEquals(Integer.valueOf(2).intValue(), payment.getAccountTrxns().size());
       Assert.assertEquals(payment.getAmount(), new Money(getCurrency()));

       Assert.assertEquals(new Money(currency, "5200.0"), savings.getSavingsBalance());
       Assert.assertEquals(amountAdjustedTo, newPayment.getAmount());
       Assert.assertEquals(Integer.valueOf(3).intValue(), newPayment.getAccountTrxns().size());

        Hibernate.initialize(savings.getAccountActionDates());
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testAdjustPmnt_LastPaymentDepositMandatory_PaidPartialDueAmount() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("prd143", InterestCalcType.MINIMUM_BALANCE,
                SavingsType.MANDATORY);
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        Money recommendedAmnt = new Money(currency, "500.0");
        Money partialAmnt = new Money(currency, "200.0");
        Date paymentDate = helper.getDate("08/05/2006");
        AccountActionDateEntity actionDate1 = helper.createAccountActionDate(savings, Short.valueOf("1"), helper
                .getDate("01/05/2006"), paymentDate, savings.getCustomer(), recommendedAmnt, recommendedAmnt,
                PaymentStatus.PAID);
        AccountActionDateEntity actionDate2 = helper.createAccountActionDate(savings, Short.valueOf("2"), helper
                .getDate("08/05/2006"), paymentDate, savings.getCustomer(), recommendedAmnt, partialAmnt,
                PaymentStatus.UNPAID);

        AccountTestUtils.addAccountActionDate(actionDate1, savings);
        AccountTestUtils.addAccountActionDate(actionDate2, savings);

        Money depositAmount = new Money(currency, "700.0");

        // Adding 1 account payment of Rs 500. With one transactions of 500.
        AccountPaymentEntity payment = helper.createAccountPayment(savings, depositAmount, paymentDate, createdBy);
        Money balanceAmount = new Money(currency, "4500.0");
        SavingsTrxnDetailEntity trxn1 = helper.createAccountTrxn(payment, Short.valueOf("1"), recommendedAmnt,
                balanceAmount, paymentDate, helper.getDate("01/05/2006"), null, AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);

        balanceAmount = new Money(currency, "4700.0");
        SavingsTrxnDetailEntity trxn2 = helper.createAccountTrxn(payment, Short.valueOf("2"), partialAmnt,
                balanceAmount, paymentDate, helper.getDate("08/05/2006"), null, AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);

        payment.addAccountTrxn(trxn1);
        payment.addAccountTrxn(trxn2);
        AccountTestUtils.addAccountPayment(payment, savings);

        savings.setSavingsBalance(balanceAmount);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        payment = savings.getLastPmnt();

       Assert.assertEquals(Integer.valueOf(2).intValue(), payment.getAccountTrxns().size());
        // Adjust last deposit of 700 to 1000.
        Money amountAdjustedTo = new Money(currency, "1000.0");
        savings.adjustLastUserAction(amountAdjustedTo, "correction entry");

        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());

        for (AccountActionDateEntity action : savings.getAccountActionDates())
           Assert.assertEquals(recommendedAmnt, ((SavingsScheduleEntity) action).getDepositPaid());

        AccountPaymentEntity newPayment = savings.getLastPmnt();

       Assert.assertEquals(Integer.valueOf(2).intValue(), savings.getAccountPayments().size());
       Assert.assertEquals(Integer.valueOf(4).intValue(), payment.getAccountTrxns().size());
       Assert.assertEquals(payment.getAmount(), new Money(getCurrency()));

       Assert.assertEquals(new Money(currency, "5000.0"), savings.getSavingsBalance());
       Assert.assertEquals(amountAdjustedTo, newPayment.getAmount());
       Assert.assertEquals(Integer.valueOf(2).intValue(), newPayment.getAccountTrxns().size());

        Hibernate.initialize(savings.getAccountActionDates());
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testAdjustPmnt_LastPaymentDepositVol_PaidDueExactAmount() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        Money recommendedAmnt = new Money(currency, "500.0");
        Money partialAmnt = new Money(currency, "200.0");
        Date paymentDate = helper.getDate("06/05/2006");
        AccountActionDateEntity actionDate1 = helper.createAccountActionDate(savings, Short.valueOf("1"), helper
                .getDate("01/05/2006"), paymentDate, savings.getCustomer(), recommendedAmnt, partialAmnt,
                PaymentStatus.PAID);
        AccountActionDateEntity actionDate2 = helper.createAccountActionDate(savings, Short.valueOf("2"), helper
                .getDate("08/05/2006"), null, savings.getCustomer(), recommendedAmnt, null, PaymentStatus.UNPAID);

        AccountTestUtils.addAccountActionDate(actionDate1, savings);
        AccountTestUtils.addAccountActionDate(actionDate2, savings);

        Money depositAmount = new Money(currency, "200.0");

        // Adding 1 account payment of Rs 500. With one transactions of 500.
        AccountPaymentEntity payment = helper.createAccountPayment(savings, depositAmount, paymentDate, createdBy);
        Money balanceAmount = new Money(currency, "4200.0");
        SavingsTrxnDetailEntity trxn1 = helper.createAccountTrxn(payment, Short.valueOf("1"), partialAmnt,
                balanceAmount, paymentDate, helper.getDate("01/05/2006"), null, AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);

        payment.addAccountTrxn(trxn1);
        AccountTestUtils.addAccountPayment(payment, savings);

        savings.setSavingsBalance(balanceAmount);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        payment = savings.getLastPmnt();

       Assert.assertEquals(Integer.valueOf(1).intValue(), payment.getAccountTrxns().size());

        // Adjust last deposit of 700 to 1000.
        Money amountAdjustedTo = new Money(currency, "500.0");
        savings.adjustLastUserAction(amountAdjustedTo, "correction entry");

        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());

        AccountPaymentEntity newPayment = savings.getLastPmnt();

       Assert.assertEquals(Integer.valueOf(2).intValue(), savings.getAccountPayments().size());
       Assert.assertEquals(Integer.valueOf(2).intValue(), payment.getAccountTrxns().size());
       Assert.assertEquals(payment.getAmount(), new Money(getCurrency()));

       Assert.assertEquals(new Money(currency, "4500.0"), savings.getSavingsBalance());
       Assert.assertEquals(amountAdjustedTo, newPayment.getAmount());
       Assert.assertEquals(Integer.valueOf(1).intValue(), newPayment.getAccountTrxns().size());

        Hibernate.initialize(savings.getAccountActionDates());
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testAdjustPmnt_LastPaymentDepositVol_PaidDueExcessAmount() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        Money recommendedAmnt = new Money(currency, "500.0");
        Money partialAmnt = new Money(currency, "200.0");
        Date paymentDate = helper.getDate("06/05/2006");
        AccountActionDateEntity actionDate1 = helper.createAccountActionDate(savings, Short.valueOf("1"), helper
                .getDate("01/05/2006"), paymentDate, savings.getCustomer(), recommendedAmnt, partialAmnt,
                PaymentStatus.PAID);
        AccountActionDateEntity actionDate2 = helper.createAccountActionDate(savings, Short.valueOf("2"), helper
                .getDate("08/05/2006"), null, savings.getCustomer(), recommendedAmnt, null, PaymentStatus.UNPAID);

        AccountTestUtils.addAccountActionDate(actionDate1, savings);
        AccountTestUtils.addAccountActionDate(actionDate2, savings);

        Money depositAmount = new Money(currency, "200.0");

        // Adding 1 account payment of Rs 500. With one transactions of 500.
        AccountPaymentEntity payment = helper.createAccountPayment(savings, depositAmount, paymentDate, createdBy);
        Money balanceAmount = new Money(currency, "4200.0");
        SavingsTrxnDetailEntity trxn1 = helper.createAccountTrxn(payment, Short.valueOf("1"), partialAmnt,
                balanceAmount, paymentDate, helper.getDate("01/05/2006"), null, AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);

        payment.addAccountTrxn(trxn1);
        AccountTestUtils.addAccountPayment(payment, savings);

        savings.setSavingsBalance(balanceAmount);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        payment = savings.getLastPmnt();

       Assert.assertEquals(Integer.valueOf(1).intValue(), payment.getAccountTrxns().size());

        // Adjust last deposit of 700 to 1000.
        Money amountAdjustedTo = new Money(currency, "800.0");
        savings.adjustLastUserAction(amountAdjustedTo, "correction entry");

        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());

        AccountPaymentEntity newPayment = savings.getLastPmnt();

       Assert.assertEquals(Integer.valueOf(2).intValue(), savings.getAccountPayments().size());
       Assert.assertEquals(Integer.valueOf(2).intValue(), payment.getAccountTrxns().size());
       Assert.assertEquals(payment.getAmount(), new Money(getCurrency()));

       Assert.assertEquals(new Money(currency, "4800.0"), savings.getSavingsBalance());
       Assert.assertEquals(amountAdjustedTo, newPayment.getAmount());
       Assert.assertEquals(Integer.valueOf(2).intValue(), newPayment.getAccountTrxns().size());

        Hibernate.initialize(savings.getAccountActionDates());
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

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
        savings = TestObjectFactory.createSavingsAccount("43245434", client1, Short.valueOf("16"), new Date(System
                .currentTimeMillis()), savingsOffering);
        Session session = StaticHibernateUtil.getSessionTL();
        Transaction trxn = session.beginTransaction();
        SavingsScheduleEntity accountActionDate = (SavingsScheduleEntity) savings.getAccountActionDate((short) 1);
        accountActionDate.setDepositPaid(TestUtils.createMoney("100.0"));
        session.update(savings);
        trxn.commit();
        StaticHibernateUtil.closeSession();
        accountActionDate = (SavingsScheduleEntity) savings.getAccountActionDate((short) 3);
       Assert.assertEquals(getRoundedMoney(300.00), savings.getOverDueDepositAmount(accountActionDate.getActionDate()));

    }

    public void testGetOverDueDepositAmountForVoluntaryAccounts() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        SavingsOfferingBO savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client1 = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
        savings = TestObjectFactory.createSavingsAccount("43245434", client1, Short.valueOf("16"), new Date(System
                .currentTimeMillis()), savingsOffering);
        Session session = StaticHibernateUtil.getSessionTL();
        Transaction trxn = session.beginTransaction();
        SavingsScheduleEntity accountActionDate = (SavingsScheduleEntity) savings.getAccountActionDate((short) 1);
        accountActionDate.setDepositPaid(TestUtils.createMoney("100.0"));
        session.update(savings);
        trxn.commit();
        StaticHibernateUtil.closeSession();
       Assert.assertEquals(getRoundedMoney(0.00), savings.getOverDueDepositAmount(accountActionDate.getActionDate()));

    }

    public void testGetRecentAccountActivity() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);

        SavingsActivityEntity savingsActivity = new SavingsActivityEntity(savings.getPersonnel(),
                (AccountActionEntity) StaticHibernateUtil.getSessionTL().get(AccountActionEntity.class,
                        Short.valueOf("1")), new Money(getCurrency(), "100"), new Money(getCurrency(), "22"), new Date(), savings);

        savingsActivity.setCreatedBy(Short.valueOf("1"));
        savingsActivity.setCreatedDate(new Date(System.currentTimeMillis()));

        savings.addSavingsActivityDetails(savingsActivity);

        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);
       Assert.assertEquals(1, savings.getRecentAccountActivity(3).size());
        for (SavingsRecentActivityView view : savings.getRecentAccountActivity(3)) {
            Assert.assertNotNull(view.getActivity());
            // * TODO: financial_calculation_rounding getAmount returns too much
            // precision
           Assert.assertEquals(new Money(getCurrency(), "100.0"), new Money(getCurrency(), view.getAmount()));

           Assert.assertEquals(new Money(getCurrency(), "22.0"), new Money(getCurrency(), view.getRunningBalance()));
            Assert.assertNotNull(view.getAccountTrxnId());
           Assert.assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(), DateUtils.getDateWithoutTimeStamp(view
                    .getActionDate().getTime()));
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

    public void testGetTotalAmountInArrearsForCurrentDateMeeting() throws Exception {
        savings = getSavingsAccount();
       Assert.assertEquals(savings.getTotalAmountInArrears(), getRoundedMoney(0.0));
    }

    public void testGetTotalAmountInArrearsForSingleInstallmentDue() throws Exception {
        savings = getSavingsAccount();
        AccountActionDateEntity accountActionDateEntity = savings.getAccountActionDate((short) 1);
        ((SavingsScheduleEntity) accountActionDateEntity).setActionDate(offSetCurrentDate(1));
        savings = (SavingsBO) saveAndFetch(savings);
       Assert.assertEquals(TestUtils.createMoney(200.0), savings.getTotalAmountInArrears());
    }

    public void testGetTotalAmountInArrearsWithPartialPayment() throws Exception {
        savings = getSavingsAccount();
        SavingsScheduleEntity accountActionDateEntity = (SavingsScheduleEntity) savings.getAccountActionDate((short) 1);
        accountActionDateEntity.setDepositPaid(new Money(getCurrency(), "20.0"));
        accountActionDateEntity.setActionDate(offSetCurrentDate(1));

        savings = (SavingsBO) saveAndFetch(savings);
       Assert.assertEquals(TestUtils.createMoney(180.0), savings.getTotalAmountInArrears());
    }

    public void testGetTotalAmountInArrearsWithPaymentDone() throws Exception {
        savings = getSavingsAccount();

        AccountActionDateEntity accountActionDateEntity = savings.getAccountActionDate(Short.valueOf("1"));
        ((SavingsScheduleEntity) accountActionDateEntity).setActionDate(offSetCurrentDate(1));
        accountActionDateEntity.setPaymentStatus(PaymentStatus.PAID);
        savings = (SavingsBO) saveAndFetch(savings);
       Assert.assertEquals(savings.getTotalAmountInArrears(), TestUtils.createMoney());
    }

    public void testGetTotalAmountDueForTwoInstallmentsDue() throws Exception {
        savings = getSavingsAccount();

        AccountActionDateEntity accountActionDateEntity = savings.getAccountActionDate((short) 1);
        ((SavingsScheduleEntity) accountActionDateEntity).setActionDate(offSetCurrentDate(2));
        AccountActionDateEntity accountActionDateEntity2 = savings.getAccountActionDate((short) 2);
        ((SavingsScheduleEntity) accountActionDateEntity2).setActionDate(offSetCurrentDate(1));

        savings = (SavingsBO) saveAndFetch(savings);

       Assert.assertEquals(savings.getTotalAmountInArrears(), TestUtils.createMoney(400.0));
    }

    public void testGetTotalAmountDueForCurrentDateMeeting() throws Exception {
        savings = getSavingsAccount();
       Assert.assertEquals(savings.getTotalAmountDue(), TestUtils.createMoney(200.0));
    }

    public void testGetTotalAmountDueForSingleInstallment() throws Exception {
        savings = getSavingsAccount();

        AccountActionDateEntity accountActionDateEntity = savings.getAccountActionDate((short) 1);
        ((SavingsScheduleEntity) accountActionDateEntity).setActionDate(offSetCurrentDate(1));

        savings = (SavingsBO) saveAndFetch(savings);
       Assert.assertEquals(savings.getTotalAmountDue(), TestUtils.createMoney(400.0));
    }

    public void testGetTotalAmountDueWithPartialPayment() throws Exception {
        savings = getSavingsAccount();

        SavingsScheduleEntity accountActionDateEntity = (SavingsScheduleEntity) savings.getAccountActionDate((short) 1);

        accountActionDateEntity.setDepositPaid(new Money(getCurrency(), "20.0"));
        accountActionDateEntity.setActionDate(offSetCurrentDate(1));
        savings = (SavingsBO) saveAndFetch(savings);
       Assert.assertEquals(savings.getTotalAmountDue(), TestUtils.createMoney(380.0));
    }

    public void testGetTotalAmountDueWithPaymentDone() throws Exception {
        savings = getSavingsAccount();

        AccountActionDateEntity accountActionDateEntity = savings.getAccountActionDate((short) 1);
        ((SavingsScheduleEntity) accountActionDateEntity).setActionDate(offSetCurrentDate(1));
        accountActionDateEntity.setPaymentStatus(PaymentStatus.PAID);

        savings = (SavingsBO) saveAndFetch(savings);

       Assert.assertEquals(savings.getTotalAmountDue(), TestUtils.createMoney(200.0));
    }

    public void testGetTotalAmountDueForTwoInstallments() throws Exception {
        savings = getSavingsAccount();
        AccountActionDateEntity accountActionDateEntity = savings.getAccountActionDate((short) 1);
        ((SavingsScheduleEntity) accountActionDateEntity).setActionDate(offSetCurrentDate(2));
        AccountActionDateEntity accountActionDateEntity2 = savings.getAccountActionDate((short) 2);

        ((SavingsScheduleEntity) accountActionDateEntity2).setActionDate(offSetCurrentDate(1));

        savings = (SavingsBO) saveAndFetch(savings);

       Assert.assertEquals(savings.getTotalAmountDue(), TestUtils.createMoney(600.0));
    }

    public void testGetTotalAmountDueForActiveCustomers() throws Exception {
        savings = getSavingsAccountForCenter();
        client1.changeStatus(CustomerStatus.CLIENT_CLOSED, CustomerStatusFlag.CLIENT_CLOSED_TRANSFERRED,
                "Client closed");
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = new SavingsPersistence().findById(savings.getAccountId());
        for (AccountActionDateEntity actionDate : savings.getAccountActionDates()) {
            if (actionDate.getInstallmentId().shortValue() == 1)
                ((SavingsScheduleEntity) actionDate).setActionDate(offSetCurrentDate(2));
        }
        Money dueAmount = new Money(getCurrency(), "1000");
       Assert.assertEquals(dueAmount, savings.getTotalAmountDue());
        client1 = TestObjectFactory.getClient(client1.getCustomerId());
        client2 = TestObjectFactory.getClient(client2.getCustomerId());
    }

    public void testSuccessfulCloseCustomerAccount() throws Exception {
        savings = getSavingsAccountForCenter();
        client1.changeStatus(CustomerStatus.CLIENT_CLOSED, CustomerStatusFlag.CLIENT_CLOSED_TRANSFERRED,
                "Client closed");
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        for (AccountBO account : client1.getAccounts()) {
            Assert.assertNotNull(account.getClosedDate());
            if (account.isOfType(AccountTypes.CUSTOMER_ACCOUNT)) {
                Assert.assertTrue(account.getAccountState().isInState(AccountState.CUSTOMER_ACCOUNT_INACTIVE));
            }
        }
    }

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

    public void testGetTotalAmountDueForNextInstallmentForCurrentDateMeeting() throws Exception {
        savings = getSavingsAccount();
       Assert.assertEquals(savings.getTotalAmountDueForNextInstallment(), TestUtils.createMoney(200.0));
    }

    public void testGetTotalAmountDueForNextInstallmentWithPartialPayment() throws Exception {
        savings = getSavingsAccount();
        SavingsScheduleEntity accountActionDateEntity = (SavingsScheduleEntity) savings.getAccountActionDate((short) 1);
        accountActionDateEntity.setDepositPaid(new Money(getCurrency(), "20.0"));

        savings = (SavingsBO) saveAndFetch(savings);
       Assert.assertEquals(savings.getTotalAmountDueForNextInstallment(), getRoundedMoney(180.0));
    }

    public void testGetTotalAmountDueForNextInstallmentPaymentDone() throws Exception {
        savings = getSavingsAccount();

        AccountActionDateEntity accountActionDateEntity = savings.getAccountActionDate(Short.valueOf("1"));
        accountActionDateEntity.setPaymentStatus(PaymentStatus.PAID);
        savings = (SavingsBO) saveAndFetch(savings);
       Assert.assertEquals(savings.getTotalAmountDueForNextInstallment(), TestUtils.createMoney());
    }

    public void testGetDetailsOfInstallmentsInArrearsForCurrentDateMeeting() throws Exception {
        savings = getSavingsAccount();
       Assert.assertEquals(savings.getDetailsOfInstallmentsInArrears().size(), 0);
    }

    public void testGetDetailsOfInstallmentsInArrearsForSingleInstallmentDue() throws Exception {
        savings = getSavingsAccount();
        AccountActionDateEntity accountActionDateEntity = savings.getAccountActionDate((short) 1);
        ((SavingsScheduleEntity) accountActionDateEntity).setActionDate(offSetCurrentDate(1));
        savings = (SavingsBO) saveAndFetch(savings);
       Assert.assertEquals(savings.getDetailsOfInstallmentsInArrears().size(), 1);
    }

    public void testGetDetailsOfInstallmentsInArrearsWithPaymentDone() throws Exception {
        savings = getSavingsAccount();

        AccountActionDateEntity accountActionDateEntity = savings.getAccountActionDate(Short.valueOf("1"));
        ((SavingsScheduleEntity) accountActionDateEntity).setActionDate(offSetCurrentDate(1));
        accountActionDateEntity.setPaymentStatus(PaymentStatus.PAID);
        savings = (SavingsBO) saveAndFetch(savings);
       Assert.assertEquals(savings.getDetailsOfInstallmentsInArrears().size(), 0);
    }

    public void testGetDetailsOfInstallmentsInArrearsForTwoInstallmentsDue() throws Exception {
        savings = getSavingsAccount();

        AccountActionDateEntity accountActionDateEntity = savings.getAccountActionDate((short) 1);
        ((SavingsScheduleEntity) accountActionDateEntity).setActionDate(offSetCurrentDate(2));
        AccountActionDateEntity accountActionDateEntity2 = savings.getAccountActionDate((short) 2);
        ((SavingsScheduleEntity) accountActionDateEntity2).setActionDate(offSetCurrentDate(1));

        savings = (SavingsBO) saveAndFetch(savings);

       Assert.assertEquals(savings.getDetailsOfInstallmentsInArrears().size(), 2);
    }

    public void testWaiveAmountOverDueForSingleInstallmentDue() throws Exception {
        savings = getSavingsAccount();
        AccountActionDateEntity accountActionDateEntity = savings.getAccountActionDate((short) 1);
        ((SavingsScheduleEntity) accountActionDateEntity).setActionDate(offSetCurrentDate(1));
        savings = (SavingsBO) saveAndFetch(savings);
       Assert.assertEquals(savings.getTotalAmountInArrears(), TestUtils.createMoney(200.0));
        savings.waiveAmountOverDue();
        savings = (SavingsBO) saveAndFetch(savings);
       Assert.assertEquals(savings.getTotalAmountInArrears(), getRoundedMoney(0.0));
       Assert.assertEquals(savings.getSavingsActivityDetails().size(), 1);
    }

    public void testWaiveAmountOverDueWithPartialPayment() throws Exception {
        savings = getSavingsAccount();
        SavingsScheduleEntity accountActionDateEntity = (SavingsScheduleEntity) savings.getAccountActionDate((short) 1);
        accountActionDateEntity.setDepositPaid(new Money(getCurrency(), "20.0"));
        accountActionDateEntity.setActionDate(offSetCurrentDate(1));

        savings = (SavingsBO) saveAndFetch(savings);
       Assert.assertEquals(savings.getTotalAmountInArrears(), TestUtils.createMoney(180.0));

        savings.waiveAmountOverDue();
        savings = (SavingsBO) saveAndFetch(savings);
       Assert.assertEquals(savings.getTotalAmountInArrears(), getRoundedMoney(0.0));
       Assert.assertEquals(savings.getSavingsActivityDetails().size(), 1);
    }

    public void testWaiveAmountOverDueForTwoInstallmentsDue() throws Exception {
        savings = getSavingsAccount();

        SavingsScheduleEntity accountActionDateEntity = (SavingsScheduleEntity) savings.getAccountActionDate((short) 1);
        accountActionDateEntity.setActionDate(offSetCurrentDate(2));
        SavingsScheduleEntity accountActionDateEntity2 = (SavingsScheduleEntity) savings
                .getAccountActionDate((short) 2);
        accountActionDateEntity2.setActionDate(offSetCurrentDate(1));

        savings = (SavingsBO) saveAndFetch(savings);

       Assert.assertEquals(savings.getTotalAmountInArrears(), TestUtils.createMoney(400.0));

        savings.waiveAmountOverDue();
        savings = (SavingsBO) saveAndFetch(savings);
       Assert.assertEquals(savings.getTotalAmountInArrears(), getRoundedMoney(0.0));
       Assert.assertEquals(savings.getSavingsActivityDetails().size(), 1);
    }

    public void testWaiveAmountDueForCurrentDateMeeting() throws Exception {
        savings = getSavingsAccount();
       Assert.assertEquals(savings.getTotalAmountDueForNextInstallment(), TestUtils.createMoney(200.0));
        savings.waiveAmountDue(WaiveEnum.ALL);
        savings = (SavingsBO) saveAndFetch(savings);
       Assert.assertEquals(savings.getTotalAmountInArrears(), getRoundedMoney(0.0));
       Assert.assertEquals(savings.getSavingsActivityDetails().size(), 1);
    }

    public void testWaiveAmountDueWithPartialPayment() throws Exception {
        savings = getSavingsAccount();
        SavingsScheduleEntity accountActionDateEntity = (SavingsScheduleEntity) savings.getAccountActionDate((short) 1);
        accountActionDateEntity.setDepositPaid(new Money(getCurrency(), "20.0"));

        savings = (SavingsBO) saveAndFetch(savings);
       Assert.assertEquals(savings.getTotalAmountDueForNextInstallment(), TestUtils.createMoney(180.0));
        savings.waiveAmountDue(WaiveEnum.ALL);
        savings = (SavingsBO) saveAndFetch(savings);
       Assert.assertEquals(savings.getTotalAmountInArrears(), getRoundedMoney(0.0));
       Assert.assertEquals(savings.getSavingsActivityDetails().size(), 1);
    }

    private void createCustomerObjects() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
        client1 = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        client2 = TestObjectFactory.createClient("client2", CustomerStatus.CLIENT_ACTIVE, group);
    }

    public void testCalculateInterest_IntCalcFreqOneDay_minbal() throws Exception {
        createInitialObjects();
        savingsOffering = createSavingsOfferingForIntCalc("prd1", "cfgh", SavingsType.VOLUNTARY,
                InterestCalcType.MINIMUM_BALANCE, DAILY, EVERY_DAY);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);
        savings.setNextIntCalcDate(helper.getDate("06/03/2006"));
        savings.setActivationDate(helper.getDate("05/03/2006"));
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        savings.updateInterestAccrued();
       Assert.assertEquals(TestUtils.createMoney(), savings.getInterestToBePosted());
       Assert.assertEquals(helper.getDate("07/03/2006"), savings.getNextIntCalcDate());

        AccountPaymentEntity payment1 = helper.createAccountPaymentToPersist(savings, new Money(currency, "1000.0"),
                new Money(currency, "1000.0"), helper.getDate("06/03/2006"), AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);

        AccountTestUtils.addAccountPayment(payment1, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();

        AccountPaymentEntity payment2 = helper.createAccountPaymentToPersist(savings, new Money(currency, "2500.0"),
                new Money(currency, "3500.0"), helper.getDate("06/03/2006"), AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment2, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        savings.updateInterestAccrued();
       Assert.assertEquals(getRoundedMoney(1.2), savings.getInterestToBePosted());
       Assert.assertEquals(helper.getDate("08/03/2006"), savings.getNextIntCalcDate());
    }

    public void testCalculateInterest_IntCalcFreqOneDay_avgBal() throws Exception {
        createInitialObjects();
        savingsOffering = createSavingsOfferingForIntCalc("prd1", "cfgh", SavingsType.VOLUNTARY,
                InterestCalcType.AVERAGE_BALANCE, DAILY, EVERY_DAY);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);
        savings.setNextIntCalcDate(helper.getDate("06/03/2006"));
        savings.setActivationDate(helper.getDate("05/03/2006"));
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        savings.updateInterestAccrued();
       Assert.assertEquals(TestUtils.createMoney(), savings.getInterestToBePosted());
       Assert.assertEquals(helper.getDate("07/03/2006"), savings.getNextIntCalcDate());

        AccountPaymentEntity payment1 = helper.createAccountPaymentToPersist(savings, new Money(currency, "1000.0"),
                new Money(currency, "1000.0"), helper.getDate("06/03/2006"), AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);

        AccountTestUtils.addAccountPayment(payment1, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();

        AccountPaymentEntity payment2 = helper.createAccountPaymentToPersist(savings, new Money(currency, "2500.0"),
                new Money(currency, "3500.0"), helper.getDate("06/03/2006"), AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment2, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        savings.updateInterestAccrued();
       Assert.assertEquals(getRoundedMoney(1.2), savings.getInterestToBePosted());
       Assert.assertEquals(helper.getDate("08/03/2006"), savings.getNextIntCalcDate());
    }

    public void testCalculateInterest_IntCalcFreqTenDays_minbal() throws Exception {
        final short TEN_DAYS = 10;
        createInitialObjects();
        savingsOffering = createSavingsOfferingForIntCalc("prd1", "cfgh", SavingsType.VOLUNTARY,
                InterestCalcType.MINIMUM_BALANCE, DAILY, TEN_DAYS);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);
        savings.setNextIntCalcDate(helper.getDate("12/03/2006"));
        savings.setActivationDate(helper.getDate("05/03/2006"));
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        savings.updateInterestAccrued();
       Assert.assertEquals(TestUtils.createMoney(), savings.getInterestToBePosted());
       Assert.assertEquals(helper.getDate("22/03/2006"), savings.getNextIntCalcDate());

        AccountPaymentEntity payment1 = helper.createAccountPaymentToPersist(savings, new Money(currency, "1000.0"),
                new Money(currency, "1000.0"), helper.getDate("11/03/2006"), AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);

        AccountTestUtils.addAccountPayment(payment1, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();

        AccountPaymentEntity payment2 = helper.createAccountPaymentToPersist(savings, new Money(currency, "500.0"),
                new Money(currency, "500.0"), helper.getDate("15/03/2006"), AccountActionTypes.SAVINGS_WITHDRAWAL
                        .getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment2, savings);
        savings.update();

        AccountPaymentEntity payment3 = helper.createAccountPaymentToPersist(savings, new Money(currency, "2500.0"),
                new Money(currency, "3000.0"), helper.getDate("19/03/2006"), AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment3, savings);
        savings.update();

        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        savings.updateInterestAccrued();
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(1.6,
        // savings.getInterestToBePosted());
       Assert.assertEquals(getRoundedMoney(1.7), savings.getInterestToBePosted());
       Assert.assertEquals(helper.getDate("01/04/2006"), savings.getNextIntCalcDate());
    }

    public void testCalculateInterest_IntCalcFreqTenDays_avg() throws Exception {
        final short TEN_DAYS = 10;
        createInitialObjects();
        savingsOffering = createSavingsOfferingForIntCalc("prd1", "cfgh", SavingsType.VOLUNTARY,
                InterestCalcType.AVERAGE_BALANCE, DAILY, TEN_DAYS);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);
        savings.setNextIntCalcDate(helper.getDate("12/03/2006"));
        savings.setActivationDate(helper.getDate("05/03/2006"));
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        savings.updateInterestAccrued();
       Assert.assertEquals(TestUtils.createMoney(), savings.getInterestToBePosted());
       Assert.assertEquals(helper.getDate("22/03/2006"), savings.getNextIntCalcDate());

        AccountPaymentEntity payment1 = helper.createAccountPaymentToPersist(savings, new Money(currency, "1000.0"),
                new Money(currency, "1000.0"), helper.getDate("11/03/2006"), AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);

        AccountTestUtils.addAccountPayment(payment1, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();

        AccountPaymentEntity payment2 = helper.createAccountPaymentToPersist(savings, new Money(currency, "500.0"),
                new Money(currency, "500.0"), helper.getDate("15/03/2006"), AccountActionTypes.SAVINGS_WITHDRAWAL
                        .getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment2, savings);
        savings.update();

        AccountPaymentEntity payment3 = helper.createAccountPaymentToPersist(savings, new Money(currency, "2500.0"),
                new Money(currency, "3000.0"), helper.getDate("19/03/2006"), AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment3, savings);
        savings.update();

        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        savings.updateInterestAccrued();
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(4.6,
        // savings.getInterestToBePosted());
       Assert.assertEquals(getRoundedMoney(4.7), savings.getInterestToBePosted());
       Assert.assertEquals(helper.getDate("01/04/2006"), savings.getNextIntCalcDate());
    }

    private SavingsOfferingBO createSavingsOfferingForIntCalc(final String offeringName, final String shortName,
            final SavingsType savingsType, final InterestCalcType interestCalcType, final RecurrenceType freqeuncyIntCalc,
            final short recurAfterIntCalc) throws Exception {
        MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(helper.getMeeting(freqeuncyIntCalc,
                recurAfterIntCalc, SAVINGS_INTEREST_CALCULATION_TIME_PERIOD));
        MeetingBO meetingIntPost = TestObjectFactory.createMeeting(helper.getMeeting(MONTHLY, EVERY_MONTH,
                SAVINGS_INTEREST_POSTING));
        return TestObjectFactory.createSavingsProduct(offeringName, shortName, ApplicableTo.GROUPS, new Date(System
                .currentTimeMillis()), PrdStatus.SAVINGS_ACTIVE, 300.0, RecommendedAmountUnit.PER_INDIVIDUAL, 12.0,
                200.0, 200.0, savingsType, interestCalcType, meetingIntCalc, meetingIntPost);
    }

    private SavingsBO getSavingsAccount() throws Exception {
        createCustomerObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        return TestObjectFactory.createSavingsAccount("000100000000017", client1, AccountStates.SAVINGS_ACC_APPROVED,
                new Date(System.currentTimeMillis()), savingsOffering);
    }

    private AccountBO saveAndFetch(final AccountBO account) throws Exception {
        TestObjectFactory.updateObject(account);
        return accountPersistence.getAccount(account.getAccountId());
    }

    private java.sql.Date offSetCurrentDate(final int noOfDays) {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day - noOfDays);
        return new java.sql.Date(currentDateCalendar.getTimeInMillis());
    }

    public void testInterestAdjustment_LastWithdrawalPaymentModified_WithMinBal() throws Exception {
        createInitialObjects();
        savingsOffering = createSavingsOfferingForIntCalc("prd1", "cfgh", SavingsType.VOLUNTARY,
                InterestCalcType.MINIMUM_BALANCE, MONTHLY, EVERY_MONTH);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);
        savings.setActivationDate(helper.getDate("20/02/2006"));
        savings.setNextIntCalcDate(helper.getDate("28/02/2006"));
        // add deposit trxn
        Money depositMoney = new Money(currency, "2000.0");
        AccountPaymentEntity payment1 = helper.createAccountPaymentToPersist(savings, depositMoney, depositMoney,
                helper.getDate("20/02/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment1, savings);
        savings.setSavingsBalance(depositMoney);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        // add withdrawal trxn
        Money withrawalAmt = new Money(currency, "500.0");
        Money balanceAmt = new Money(currency, "1500.0");
        AccountPaymentEntity payment2 = helper.createAccountPaymentToPersist(savings, withrawalAmt, balanceAmt, helper
                .getDate("25/02/2006"), AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment2, savings);
        savings.setSavingsBalance(balanceAmt);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());

        Money oldInterest = savings.calculateInterestForAdjustment(helper.getDate("25/02/2006"), null);
       Assert.assertEquals(TestUtils.createMoney(), oldInterest);

        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        // MinBal from 20/02 - 28/02 = 1500
        // Interest 1500*.12*8/365 = 3.9
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(4.4,
        // savings.getInterestToBePosted());
       Assert.assertEquals(getRoundedMoney(4.0), savings.getInterestToBePosted());

        oldInterest = savings.calculateInterestForAdjustment(helper.getDate("25/02/2006"), null);
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(4.4, oldInterest);
       Assert.assertEquals(getRoundedMoney(4.0), oldInterest);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        Money amountAdjustedTo = new Money(currency, "1000");
        savings.getSavingsOffering().setMaxAmntWithdrawl(new Money(currency, "2000"));
        try {
            savings.adjustLastUserAction(amountAdjustedTo, "correction entry");
        } catch (ApplicationException ae) {
           Assert.assertTrue(false);
        }
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        // MinBal from 20/02 - 28/02 = 1000
        // Interest 1000*.12*8/365 = 3.0
        // * TODO: financial_calculation_rounding
       Assert.assertEquals(getRoundedMoney(2.7), savings.getInterestToBePosted());

        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testInterestAdjustment_LastDepositPaymentModified_WithAvgBal() throws Exception {
        createInitialObjects();
        savingsOffering = createSavingsOfferingForIntCalc("prd1", "cfgh", SavingsType.VOLUNTARY,
                InterestCalcType.AVERAGE_BALANCE, MONTHLY, EVERY_MONTH);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);
        savings.setActivationDate(helper.getDate("20/02/2006"));
        savings.setNextIntCalcDate(helper.getDate("28/02/2006"));

        Money depositMoney = new Money(currency, "2000.0");
        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, depositMoney, depositMoney, helper
                .getDate("25/02/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);
        savings.setSavingsBalance(depositMoney);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        depositMoney = new Money(currency, "5000.0");
        Money balanceAmt = new Money(currency, "7000.0");
        AccountPaymentEntity payment2 = helper.createAccountPaymentToPersist(savings, depositMoney, balanceAmt, helper
                .getDate("27/02/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment2, savings);
        savings.setSavingsBalance(balanceAmt);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());

        Money oldInterest = savings.calculateInterestForAdjustment(helper.getDate("27/02/2006"), null);
       Assert.assertEquals(TestUtils.createMoney(), oldInterest);

        savings.updateInterestAccrued();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        // from 25/02 - 27/02 = 2000*2 + from 27/02 - 28/02 = 7000*1 = 11000
        // Therefore avg bal 11000/3 = 3666.667
        // Interest 3666.667*.12*3/365 = 5.91
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(5.9,
        // savings.getInterestToBePosted());
       Assert.assertEquals(getRoundedMoney(3.7), savings.getInterestToBePosted());

        oldInterest = savings.calculateInterestForAdjustment(helper.getDate("27/02/2006"), null);
        // * TODO: financial_calculation_rounding
        //Assert.assertEquals(5.9, oldInterest);
       Assert.assertEquals(getRoundedMoney(3.7), oldInterest);

        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        Money amountAdjustedTo = new Money(currency, "4000");

        try {
            savings.adjustLastUserAction(amountAdjustedTo, "correction entry");
        } catch (ApplicationException ae) {
           Assert.assertTrue(false);
        }
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
        payment = savings.getLastPmnt();
       Assert.assertEquals(new Money(currency, "6000"), savings.getSavingsBalance());
        // from 25/02 - 27/02 = 2000*2 + from 27/02 - 28//02 = 6000*1 = 10000
        // Therefore avg bal 10000/3 = 3333.333
        // Interest 3333.333*.12*3/365 = 3.288
        // * TODO: financial_calculation_rounding
       Assert.assertEquals(getRoundedMoney(3.3), savings.getInterestToBePosted());

        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testRegenerateFutureInstallments() throws Exception {
        savings = getSavingAccount();
        TestObjectFactory.flushandCloseSession();
        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        AccountActionDateEntity accountActionDateEntity = savings.getDetailsOfNextInstallment();
        MeetingBO meeting = savings.getCustomer().getCustomerMeeting().getMeeting();
        meeting.getMeetingDetails().setRecurAfter(Short.valueOf("2"));
        meeting.setMeetingStartDate(accountActionDateEntity.getActionDate());

        List<java.util.Date> meetingDates = meeting.getAllDates(DateUtils.getLastDayOfNextYear());
        meetingDates.remove(0);
        savings.regenerateFutureInstallments((short) (accountActionDateEntity.getInstallmentId().intValue() + 1), workingDays, holidays);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        TestObjectFactory.flushandCloseSession();
        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        client1 = TestObjectFactory.getCustomer(client1.getCustomerId());
        client2 = TestObjectFactory.getCustomer(client2.getCustomerId());
        for (AccountActionDateEntity actionDateEntity : savings.getAccountActionDates()) {
            if (actionDateEntity.getInstallmentId().equals(Short.valueOf("2")))
               Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(0).getTime()), DateUtils
                        .getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
            else if (actionDateEntity.getInstallmentId().equals(Short.valueOf("3")))
               Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(1).getTime()), DateUtils
                        .getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
        }
    }

    public void testRegenerateFutureInstallmentsWithCancelState() throws Exception {

        savings = getSavingAccount();
        TestObjectFactory.flushandCloseSession();
        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        java.sql.Date intallment2ndDate = null;
        java.sql.Date intallment3ndDate = null;
        for (AccountActionDateEntity actionDateEntity : savings.getAccountActionDates()) {
            if (actionDateEntity.getInstallmentId().equals(Short.valueOf("2")))
                intallment2ndDate = actionDateEntity.getActionDate();
            else if (actionDateEntity.getInstallmentId().equals(Short.valueOf("3")))
                intallment3ndDate = actionDateEntity.getActionDate();
        }
        AccountActionDateEntity accountActionDateEntity = savings.getDetailsOfNextInstallment();
        MeetingBO meeting = savings.getCustomer().getCustomerMeeting().getMeeting();
        meeting.getMeetingDetails().setRecurAfter(Short.valueOf("2"));
        meeting.setMeetingStartDate(accountActionDateEntity.getActionDate());
        List<java.util.Date> meetingDates = meeting.getAllDates(DateUtils.getLastDayOfNextYear());
        meetingDates.remove(0);
        savings.setUserContext(TestObjectFactory.getContext());
        savings.changeStatus(AccountState.SAVINGS_CANCELLED.getValue(), null, "");
        savings.regenerateFutureInstallments((short) (accountActionDateEntity.getInstallmentId().intValue() + 1),  workingDays, holidays);
        StaticHibernateUtil.commitTransaction();
        TestObjectFactory.flushandCloseSession();
        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        client1 = TestObjectFactory.getCustomer(client1.getCustomerId());
        client2 = TestObjectFactory.getCustomer(client2.getCustomerId());
        for (AccountActionDateEntity actionDateEntity : savings.getAccountActionDates()) {
            if (actionDateEntity.getInstallmentId().equals(Short.valueOf("2")))
               Assert.assertEquals(intallment2ndDate, DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate()
                        .getTime()));
            else if (actionDateEntity.getInstallmentId().equals(Short.valueOf("3")))
               Assert.assertEquals(intallment3ndDate, DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate()
                        .getTime()));
        }
    }

    private SavingsBO getSavingAccount() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center);
        client1 = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        client2 = TestObjectFactory.createClient("client2", CustomerStatus.CLIENT_ACTIVE, group);
        MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
                EVERY_WEEK, CUSTOMER_MEETING));
        MeetingBO meetingIntPost = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
                EVERY_WEEK, CUSTOMER_MEETING));
        savingsOffering = TestObjectFactory.createSavingsProduct("SavingPrd1", ApplicableTo.GROUPS, new Date(System
                .currentTimeMillis()), PrdStatus.LOAN_ACTIVE, 300.0, RecommendedAmountUnit.PER_INDIVIDUAL, 24.0, 200.0,
                200.0, SavingsType.VOLUNTARY, InterestCalcType.MINIMUM_BALANCE, meetingIntCalc, meetingIntPost);
        SavingsBO savings = new SavingsBO(userContext, savingsOffering, group, AccountState.SAVINGS_ACTIVE,
                savingsOffering.getRecommendedAmount(), getCustomFieldView());
        savings.save();
        StaticHibernateUtil.getTransaction().commit();
        return savings;
    }

    public void testGetTotalPaymentDueForVol() throws Exception {
        createObjectToCheckForTotalInstallmentDue(SavingsType.VOLUNTARY);
        savings = savingsPersistence.findById(savings.getAccountId());
        Money recommendedAmnt = new Money(currency, "500.0");
        Money paymentDue = savings.getTotalPaymentDue(group.getCustomerId());
       Assert.assertEquals(recommendedAmnt, paymentDue);
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testGetTotalInstallmentDueForVol() throws Exception {
        createObjectToCheckForTotalInstallmentDue(SavingsType.VOLUNTARY);
        savings = savingsPersistence.findById(savings.getAccountId());
        List<AccountActionDateEntity> dueInstallment = savings.getTotalInstallmentsDue(group.getCustomerId());
       Assert.assertEquals(1, dueInstallment.size());
       Assert.assertEquals(Short.valueOf("2"), dueInstallment.get(0).getInstallmentId());
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testGetTotalPaymentDueForMan() throws Exception {
        createObjectToCheckForTotalInstallmentDue(SavingsType.MANDATORY);
        savings = savingsPersistence.findById(savings.getAccountId());
        Money amount = new Money(currency, "1000.0");
        Money paymentDue = savings.getTotalPaymentDue(group.getCustomerId());
       Assert.assertEquals(amount, paymentDue);
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testGetTotalInstallmentDueForMan() throws Exception {
        createObjectToCheckForTotalInstallmentDue(SavingsType.MANDATORY);
        savings = savingsPersistence.findById(savings.getAccountId());
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
        AccountActionDateEntity actionDate1 = helper.createAccountActionDate(savings, Short.valueOf("1"), cal2
                .getTime(), paymentDate, savings.getCustomer(), recommendedAmnt, new Money(getCurrency()), PaymentStatus.UNPAID);
        AccountActionDateEntity actionDate2 = helper.createAccountActionDate(savings, Short.valueOf("2"), new Date(),
                paymentDate, savings.getCustomer(), recommendedAmnt, new Money(getCurrency()), PaymentStatus.UNPAID);
        AccountTestUtils.addAccountActionDate(actionDate1, savings);
        AccountTestUtils.addAccountActionDate(actionDate2, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
    }

    public void testSuccessfulCloseAccount() throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        savings.setActivationDate(helper.getDate("20/05/2006"));

        AccountPaymentEntity payment1 = helper.createAccountPaymentToPersist(savings, new Money(currency, "1000.0"),
                new Money(currency, "1000.0"), helper.getDate("30/05/2006"), AccountActionTypes.SAVINGS_DEPOSIT
                        .getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment1, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        Money balanceAmount = new Money(currency, "1500.0");
        AccountPaymentEntity payment2 = helper.createAccountPaymentToPersist(savings, new Money(currency, "500.0"),
                balanceAmount, helper.getDate("15/06/2006"), AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings,
                createdBy, group);
        AccountTestUtils.addAccountPayment(payment2, savings);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        Money interestAmount = new Money(currency, "40");
        savings.setInterestToBePosted(interestAmount);
        savings.setSavingsBalance(balanceAmount);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        Money interestAtClosure = savings.calculateInterestForClosure(new SavingsHelper().getCurrentDate());
        AccountPaymentEntity payment = new AccountPaymentEntity(savings, balanceAmount.add(interestAtClosure), null,
                null, new PaymentTypeEntity(Short.valueOf("1")), new Date(System.currentTimeMillis()));
        AccountNotesEntity notes = new AccountNotesEntity(new java.sql.Date(System.currentTimeMillis()),
                "closing account", TestObjectFactory.getPersonnel(userContext.getId()), savings);
        savings.closeAccount(payment, notes, group);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = savingsPersistence.findById(savings.getAccountId());
       Assert.assertEquals(2, savings.getSavingsActivityDetails().size());

        for (SavingsActivityEntity activity : savings.getSavingsActivityDetails()) {
           Assert.assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(), DateUtils.getDateWithoutTimeStamp(activity
                    .getTrxnCreatedDate().getTime()));
            if (activity.getActivity().getId().equals(AccountActionTypes.SAVINGS_WITHDRAWAL.getValue())) {
                // * TODO: financial_calculation_rounding
               Assert.assertEquals(getRoundedMoney(balanceAmount.add(interestAtClosure)), getRoundedMoney(activity
                        .getAmount()));
               Assert.assertEquals(getRoundedMoney(balanceAmount.add(interestAtClosure)), getRoundedMoney(savings
                        .getSavingsPerformance().getTotalWithdrawals()));
            } else if (activity.getActivity().getId().equals(AccountActionTypes.SAVINGS_INTEREST_POSTING.getValue()))
               Assert.assertEquals(getRoundedMoney(interestAtClosure), getRoundedMoney(activity.getAmount()));
        }
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    private SavingsBO getSavingsAccountForCenter() throws Exception {
        createInitialObjects();
        client1 = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        client2 = TestObjectFactory.createClient("client2", CustomerStatus.CLIENT_ACTIVE, group);
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        return helper.createSavingsAccount(savingsOffering, center, AccountState.SAVINGS_ACTIVE, userContext);
    }

    public void testGetRecentAccountNotes() throws Exception {
        savings = getSavingsAccountForCenter();
        addNotes("note1");
        addNotes("note2");
        addNotes("note3");
        addNotes("note4");
        addNotes("note5");
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        List<AccountNotesEntity> notes = savings.getRecentAccountNotes();

       Assert.assertEquals("Size of recent notes is 3", 3, notes.size());
        for (AccountNotesEntity accountNotesEntity : notes) {
           Assert.assertEquals("Last note added is note5", "note5", accountNotesEntity.getComment());
            break;
        }

    }

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

        List<Days> workingDays = FiscalCalendarRules.getWorkingDaysAsJodaTimeDays();
        List<Holiday> holidays = new ArrayList<Holiday>();

        savingsBO.generateNextSetOfMeetingDates(workingDays, holidays);

        TestObjectFactory.updateObject(savingsBO);
        TestObjectFactory.updateObject(center);
        TestObjectFactory.updateObject(group);
        TestObjectFactory.updateObject(savingsBO);
        TestObjectFactory.flushandCloseSession();
        center = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, center.getCustomerId());
        group = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, group.getCustomerId());
        savingsBO = (SavingsBO) StaticHibernateUtil.getSessionTL().get(SavingsBO.class, savingsBO.getAccountId());

        MeetingBO meetingBO = center.getCustomerMeeting().getMeeting();
        meetingBO.setMeetingStartDate(lastYearLastInstallment.getActionDate());
        List<Date> meetingDates = meetingBO.getAllDates((short) 10);
        meetingDates.remove(0);
        Date FirstSavingInstallmetDate = savingsBO.getAccountActionDate(installmetId.shortValue()).getActionDate();
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(meetingDates.get(0));
        Calendar calendar3 = Calendar.getInstance();
        calendar3.setTime(FirstSavingInstallmetDate);
        Assert.assertEquals(0, new GregorianCalendar(calendar3.get(Calendar.YEAR), calendar3.get(Calendar.MONTH),
                calendar3.get(Calendar.DATE), 0, 0, 0).compareTo(new GregorianCalendar(calendar2.get(Calendar.YEAR),
                calendar2.get(Calendar.MONTH), calendar2.get(Calendar.DATE), 0, 0, 0)));
        TestObjectFactory.cleanUp(savingsBO);
    }
    
    private void addNotes(final String comment) throws Exception {
        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
        PersonnelBO personnelBO = new PersonnelPersistence().getPersonnel(userContext.getId());
        AccountNotesEntity accountNotesEntity = new AccountNotesEntity(currentDate, comment, personnelBO, savings);
        savings.addAccountNotes(accountNotesEntity);
        savings.update();
        StaticHibernateUtil.commitTransaction();
    }
}
