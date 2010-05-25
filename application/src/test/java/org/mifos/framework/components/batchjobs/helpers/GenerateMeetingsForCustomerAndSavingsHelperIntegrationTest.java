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

import junit.framework.Assert;

import org.mifos.accounts.business.AccountTestUtils;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.config.ConfigurationManager;
import org.mifos.config.GeneralConfig;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestGeneralLedgerCode;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.util.UserContext;

public class GenerateMeetingsForCustomerAndSavingsHelperIntegrationTest extends MifosIntegrationTestCase {

    public GenerateMeetingsForCustomerAndSavingsHelperIntegrationTest() throws Exception {
        super();
    }

    private CustomerBO group;
    private CustomerBO center;
    private CustomerBO client1;
    private CustomerBO client2;
    private SavingsBO savings;
    private SavingsOfferingBO savingsOffering;

    private UserContext userContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userContext = TestObjectFactory.getContext();
    }

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.cleanUp(savings);
        TestObjectFactory.cleanUp(client1);
        TestObjectFactory.cleanUp(client2);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testExecuteForCustomerAccount() throws Exception {
        StaticHibernateUtil.startTransaction();
        createCenter();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        StaticHibernateUtil.startTransaction();
        center = TestObjectFactory.getCustomer(center.getCustomerId());

        int noOfInstallments = center.getCustomerAccount().getAccountActionDates().size();
        new GenerateMeetingsForCustomerAndSavingsTask().getTaskHelper().execute(System.currentTimeMillis());

        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        center = TestObjectFactory.getCustomer(center.getCustomerId());
        Assert.assertEquals(noOfInstallments + 10, center.getCustomerAccount().getAccountActionDates().size());
    }

    public void testExecuteForSavingsAccount() throws Exception {
        int configuredValue = GeneralConfig.getOutputIntervalForBatchJobs();
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        int outputInterval = 1;

        try {
            // force output for every account
            configMgr.setProperty(GeneralConfig.OutputIntervalForBatchJobs, outputInterval);

            savings = getSavingsAccountForCenter();
            int noOfInstallments = savings.getAccountActionDates().size();

            AccountTestUtils.changeInstallmentDatesToPreviousDate(savings);
            TestObjectFactory.flushandCloseSession();
            savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
            new GenerateMeetingsForCustomerAndSavingsTask().getTaskHelper().execute(System.currentTimeMillis());
            savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
            Assert.assertEquals(noOfInstallments + 20, savings.getAccountActionDates().size());
        } finally {
            // restore original output interval value
            configMgr.setProperty(GeneralConfig.OutputIntervalForBatchJobs, configuredValue);
        }
    }

    public void testExecuteForCustomerAndSavingsAccount() throws Exception {
        // jpw - this test is similar to testExecuteForSavingsAccount
        // Re-using much of it to test that customer and savings accounts are processed as have made separate queries to
        // return the two different types of accounts.

        int configuredValue = GeneralConfig.getOutputIntervalForBatchJobs();
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        int outputInterval = 1;

        try {
            // force output for every account
            configMgr.setProperty(GeneralConfig.OutputIntervalForBatchJobs, outputInterval);

            savings = getSavingsAccountForCenter();
            int noOfInstallments = savings.getAccountActionDates().size();
            AccountTestUtils.changeInstallmentDatesToPreviousDate(savings);

            CustomerAccountBO centerCustomerAccount = center.getCustomerAccount();
            Integer centerCustomerAccountInstallments = centerCustomerAccount.getAccountActionDates().size();
            AccountTestUtils.changeInstallmentDatesToPreviousDate(centerCustomerAccount);
            CustomerAccountBO groupCustomerAccount = group.getCustomerAccount();
            Integer groupCustomerAccountInstallments = groupCustomerAccount.getAccountActionDates().size();
            AccountTestUtils.changeInstallmentDatesToPreviousDate(groupCustomerAccount);
            CustomerAccountBO client1CustomerAccount = client1.getCustomerAccount();
            Integer client1CustomerAccountInstallments = client1CustomerAccount.getAccountActionDates().size();
            AccountTestUtils.changeInstallmentDatesToPreviousDate(client1CustomerAccount);
            CustomerAccountBO client2CustomerAccount = client2.getCustomerAccount();
            Integer client2CustomerAccountInstallments = client2CustomerAccount.getAccountActionDates().size();
            AccountTestUtils.changeInstallmentDatesToPreviousDate(client2CustomerAccount);

            TestObjectFactory.flushandCloseSession();
            savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
            new GenerateMeetingsForCustomerAndSavingsTask().getTaskHelper().execute(System.currentTimeMillis());
            savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
            centerCustomerAccount = TestObjectFactory.getObject(CustomerAccountBO.class, centerCustomerAccount.getAccountId());
            groupCustomerAccount = TestObjectFactory.getObject(CustomerAccountBO.class, groupCustomerAccount.getAccountId());
            client1CustomerAccount = TestObjectFactory.getObject(CustomerAccountBO.class, client1CustomerAccount.getAccountId());
            client2CustomerAccount = TestObjectFactory.getObject(CustomerAccountBO.class, client2CustomerAccount.getAccountId());

            Assert.assertEquals(noOfInstallments + 20, savings.getAccountActionDates().size());
            Assert.assertEquals(centerCustomerAccountInstallments + 10, centerCustomerAccount.getAccountActionDates().size());
            Assert.assertEquals(groupCustomerAccountInstallments + 10, groupCustomerAccount.getAccountActionDates().size());
            Assert.assertEquals(client1CustomerAccountInstallments + 10, client1CustomerAccount.getAccountActionDates().size());
            Assert.assertEquals(client2CustomerAccountInstallments + 10, client2CustomerAccount.getAccountActionDates().size());
        } finally {
            // restore original output interval value
            configMgr.setProperty(GeneralConfig.OutputIntervalForBatchJobs, configuredValue);
        }
    }

    public void testExecuteForSavingsAccountForGroup() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE,
                center);
        SavingsTestHelper helper = new SavingsTestHelper();
        savingsOffering = createSavingsOffering("dfasdasd1", "sad1", InterestCalcType.MINIMUM_BALANCE,
                SavingsType.VOLUNTARY, TestGeneralLedgerCode.ASSETS, TestGeneralLedgerCode.CASH_AND_BANK_BALANCES,
                RecommendedAmountUnit.COMPLETE_GROUP);
        savings = helper.createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, userContext);
        Date meetingStartDate = savings.getCustomer().getCustomerMeeting().getMeeting().getStartDate();
        int noOfInstallments = savings.getAccountActionDates().size();
        AccountTestUtils.changeInstallmentDatesToPreviousDateExceptLastInstallment(savings, 7);
        TestObjectFactory.flushandCloseSession();
        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        new GenerateMeetingsForCustomerAndSavingsTask().getTaskHelper().execute(System.currentTimeMillis());
        StaticHibernateUtil.closeSession();
        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        Assert.assertEquals(noOfInstallments + 10, savings.getAccountActionDates().size());
        Assert.assertEquals(new java.sql.Date(DateUtils.getDateWithoutTimeStamp(meetingStartDate.getTime()).getTime())
                .toString(), group.getCustomerMeeting().getMeeting().getStartDate().toString());
    }

    private void createCenter() throws CustomerException {
        List<FeeDto> feeDto = new ArrayList<FeeDto>();
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("Center_Active_test", meeting, feeDto);
        // give batch jobs something useful to do
        // TODO: move this method to a shared util class?
        AccountTestUtils.changeInstallmentDatesToPreviousDate(center.getCustomerAccount());
        center.update();
    }

    private void createInitialObjects() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE,
                center);
    }

    private SavingsBO getSavingsAccountForCenter() throws Exception {
        createInitialObjects();
        client1 = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        client2 = TestObjectFactory.createClient("client2", CustomerStatus.CLIENT_ACTIVE, group);
        SavingsTestHelper helper = new SavingsTestHelper();
        savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
        return helper.createSavingsAccount(savingsOffering, center, AccountState.SAVINGS_ACTIVE, userContext);
    }

    private SavingsOfferingBO createSavingsOffering(String offeringName, String shortName,
            InterestCalcType interestCalcType, SavingsType savingsType, Short depGLCode, Short intGLCode,
            RecommendedAmountUnit recommendedAmountUnit) {
        MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        MeetingBO meetingIntPost = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        return TestObjectFactory.createSavingsProduct(offeringName, shortName, ApplicableTo.GROUPS, new Date(System
                .currentTimeMillis()), PrdStatus.SAVINGS_ACTIVE, 300.0, recommendedAmountUnit, 24.0, 200.0, 200.0,
                savingsType, interestCalcType, meetingIntCalc, meetingIntPost, depGLCode, intGLCode);
    }

}
