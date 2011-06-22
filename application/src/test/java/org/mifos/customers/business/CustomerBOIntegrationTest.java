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

package org.mifos.customers.business;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanBOTestUtils;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.savings.SavingBOTestUtils;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.ClientPerformanceHistoryEntity;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.business.GroupPerformanceHistoryEntity;
import org.mifos.customers.group.business.GroupTestUtils;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.util.helpers.AuditInterceptor;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CustomerBOIntegrationTest extends MifosIntegrationTestCase {

    private AccountBO accountBO;
    private CenterBO center;
    private GroupBO group;
    private ClientBO client;
    private CustomerPersistence customerPersistence = new CustomerPersistence();
    private MeetingBO meeting;
    private SavingsTestHelper helper = new SavingsTestHelper();
    private SavingsOfferingBO savingsOffering;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        try {
            accountBO = null;
            client = null;
            group = null;
            center = null;
        } catch (Exception e) {
        }
        StaticHibernateUtil.flushAndClearSession();
    }

    @Test
    public void testHasActiveLoanAccounts() throws Exception {
        createInitialObjects();
        client.setUserContext(TestUtils.makeUser());
        ((AuditInterceptor) StaticHibernateUtil.getInterceptor()).createInitialValueMap(client);
        boolean res = client.hasActiveLoanAccounts();
        StaticHibernateUtil.flushAndClearSession();
        Assert.assertEquals(res, false);

    }

    @Test
    public void testCheckIfClientIsATitleHolder() throws Exception {
        createInitialObjects();
        client.setUserContext(TestUtils.makeUser());
        ((AuditInterceptor) StaticHibernateUtil.getInterceptor()).createInitialValueMap(client);

        try {
            client.checkIfClientIsATitleHolder();

        } catch (CustomerException expected) {
            Assert.assertEquals(CustomerConstants.CLIENT_IS_A_TITLE_HOLDER_EXCEPTION, expected.getKey());
        }

        StaticHibernateUtil.flushAndClearSession();


    }

    @Test
    public void testGroupPerfObject() throws PersistenceException {
        createInitialObjects();
        GroupPerformanceHistoryEntity groupPerformanceHistory = group.getGroupPerformanceHistory();
        GroupTestUtils.setLastGroupLoanAmount(groupPerformanceHistory, new Money(getCurrency(), "100"));
        TestObjectFactory.updateObject(group);
        StaticHibernateUtil.flushAndClearSession();
        group = (GroupBO) customerPersistence
                .findBySystemId(group.getGlobalCustNum(), group.getCustomerLevel().getId());
        Assert.assertEquals(group.getCustomerId(), group.getGroupPerformanceHistory().getGroup().getCustomerId());
        Assert.assertEquals(new Money(getCurrency(), "100"), group.getGroupPerformanceHistory()
                .getLastGroupLoanAmount());
        StaticHibernateUtil.flushAndClearSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
    }

    @Test
    public void testGroupPerformanceObject() throws Exception {
        GroupPerformanceHistoryEntity groupPerformanceHistory = new GroupPerformanceHistoryEntity(Integer.valueOf("1"),
                new Money(getCurrency(), "23"), new Money(getCurrency(), "24"), new Money(getCurrency(), "26"),
                new Money(getCurrency(), "25"), new Money(getCurrency(), "27"));
        Assert.assertEquals(new Money(getCurrency(), "23"), groupPerformanceHistory.getLastGroupLoanAmount());
        Assert.assertEquals(new Money(getCurrency(), "27"), groupPerformanceHistory.getPortfolioAtRisk());
        Assert.assertEquals(1, groupPerformanceHistory.getClientCount().intValue());

    }

    @Test
    public void testClientPerfObject() throws PersistenceException {
        createInitialObjects();
        ClientPerformanceHistoryEntity clientPerformanceHistory = client.getClientPerformanceHistory();
        clientPerformanceHistory.setNoOfActiveLoans(Integer.valueOf("1"));
        clientPerformanceHistory.setLastLoanAmount(new Money(getCurrency(), "100"));
        TestObjectFactory.updateObject(client);
        client = (ClientBO) customerPersistence.findBySystemId(client.getGlobalCustNum(), client.getCustomerLevel()
                .getId());
        Assert.assertEquals(client.getCustomerId(), client.getClientPerformanceHistory().getClient().getCustomerId());
        Assert.assertEquals(new Money(getCurrency(), "100"), client.getClientPerformanceHistory().getLastLoanAmount());
        Assert.assertEquals(new Money(getCurrency(), "0"), client.getClientPerformanceHistory()
                .getDelinquentPortfolioAmount());
    }

    @Test
    public void testGetBalanceForAccountsAtRisk() {
        createInitialObjects();
        accountBO = getLoanAccount(group, meeting);
        StaticHibernateUtil.flushSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
        Assert.assertEquals(new Money(getCurrency()), getBalanceForAccountsAtRisk());
        changeFirstInstallmentDate(accountBO, 31);
        Assert.assertEquals(new Money(getCurrency(), "300"), getBalanceForAccountsAtRisk());
        StaticHibernateUtil.flushSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
    }

    private Money getBalanceForAccountsAtRisk() {
        Money amount = new Money(getCurrency());
        for (AccountBO account : group.getAccounts()) {
            if (account.getType() == AccountTypes.LOAN_ACCOUNT && ((LoanBO) account).isAccountActive()) {
                LoanBO loan = (LoanBO) account;
                if (loan.hasPortfolioAtRisk()) {
                    amount = amount.add(loan.getRemainingPrincipalAmount());
                }
            }
        }
        return amount;
    }

    @Test
    public void testGetOutstandingLoanAmount() {
        createInitialObjects();
        accountBO = getLoanAccount(group, meeting);
        StaticHibernateUtil.flushSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        Assert.assertEquals(new Money(getCurrency(), "300.0"), group.getOutstandingLoanAmount(getCurrency()));
        StaticHibernateUtil.flushSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
    }

    @Test
    public void testGetActiveLoanCounts() {
        createInitialObjects();
        accountBO = getLoanAccount(group, meeting);
        StaticHibernateUtil.flushSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        Assert.assertEquals(1, group.getActiveLoanCounts().intValue());
        StaticHibernateUtil.flushSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
    }

    @Test
    public void testGetLoanAccountInUse() {
        createInitialObjects();
        accountBO = getLoanAccount(group, meeting);
        StaticHibernateUtil.flushSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        List<LoanBO> loans = group.getOpenLoanAccounts();
        Assert.assertEquals(1, loans.size());
        Assert.assertEquals(accountBO.getAccountId(), loans.get(0).getAccountId());
        StaticHibernateUtil.flushSession();

        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
    }

    @Test
    public void testGetSavingsAccountInUse() throws Exception {
        accountBO = getSavingsAccount("fsaf6", "ads6");
        StaticHibernateUtil.flushSession();
        client = TestObjectFactory.getClient(client.getCustomerId());
        List<SavingsBO> savings = client.getOpenSavingAccounts();
        Assert.assertEquals(1, savings.size());
        Assert.assertEquals(accountBO.getAccountId(), savings.get(0).getAccountId());
        StaticHibernateUtil.flushSession();

        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
    }

    @Test
    public void testHasAnyLoanAccountInUse() {
        createInitialObjects();
        accountBO = getLoanAccount(group, meeting);
        StaticHibernateUtil.flushSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        Assert.assertTrue(group.isAnyLoanAccountOpen());
        StaticHibernateUtil.flushSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
    }

    @Test
    public void testHasAnySavingsAccountInUse() throws Exception {
        accountBO = getSavingsAccount("fsaf5", "ads5");
        StaticHibernateUtil.flushSession();
        client = TestObjectFactory.getClient(client.getCustomerId());
        Assert.assertTrue(client.isAnySavingsAccountOpen());
        StaticHibernateUtil.flushSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
    }

    @Test
    public void testgetSavingsBalance() throws Exception {
        SavingsBO savings = getSavingsAccount("fsaf4", "ads4");
        SavingBOTestUtils.setBalance(savings, new Money(getCurrency(), "1000"));
        savings.update();
        StaticHibernateUtil.flushAndClearSession();
        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        Assert.assertEquals(new Money(getCurrency(), "1000.0"), savings.getSavingsBalance());
        Assert.assertEquals(new Money(getCurrency(), "1000.0"), client.getSavingsBalance(getCurrency()));
        StaticHibernateUtil.flushSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        savings = null;
    }

    @Test
    public void testApplicablePrdforCustomLevel() throws Exception {
        createInitialObjects();
        Assert.assertEquals(Short.valueOf("1"), client.getCustomerLevel().getProductApplicableType());
        Assert.assertEquals(Short.valueOf("3"), center.getCustomerLevel().getProductApplicableType());
    }

    @Test
    public void testCustomerPerformanceView() throws Exception {
        CustomerPerformanceHistoryDto customerPerformanceView = new CustomerPerformanceHistoryDto(Integer.valueOf("1"),
                Integer.valueOf("1"), "10");

        Assert.assertEquals(1, customerPerformanceView.getMeetingsAttended().intValue());
        Assert.assertEquals(1, customerPerformanceView.getMeetingsMissed().intValue());
        Assert.assertEquals("10", customerPerformanceView.getLastLoanAmount());

    }

    @Test
    public void testCustomerStatusFlagEntity() throws Exception {
        CustomerStatusFlagEntity customerStatusFlag = (CustomerStatusFlagEntity) TestObjectFactory.getObject(
                CustomerStatusFlagEntity.class, Short.valueOf("1"));
        Assert.assertEquals("Withdraw", customerStatusFlag.getFlagDescription());
        customerStatusFlag.setFlagDescription("Other");
        Assert.assertEquals("Other", customerStatusFlag.getFlagDescription());

    }

    private void changeFirstInstallmentDate(AccountBO accountBO, int numberOfDays) {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day - numberOfDays);
        for (AccountActionDateEntity accountActionDateEntity : accountBO.getAccountActionDates()) {
            LoanBOTestUtils.setActionDate(accountActionDateEntity,
                    new java.sql.Date(currentDateCalendar.getTimeInMillis()));
            break;
        }
    }

    private SavingsBO getSavingsAccount(String offeringName, String shortName) throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering(offeringName, shortName);
        return TestObjectFactory.createSavingsAccount("000100000000017", client, AccountStates.SAVINGS_ACC_APPROVED,
                new Date(System.currentTimeMillis()), savingsOffering);
    }

    private void createInitialObjects() {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter(this.getClass().getSimpleName() + " Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter(this.getClass().getSimpleName() + " Group",
                CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient(this.getClass().getSimpleName() + " Client",
                CustomerStatus.CLIENT_ACTIVE, group);
        StaticHibernateUtil.flushAndClearSession();
    }

    private AccountBO getLoanAccount(CustomerBO customer, MeetingBO meeting) {
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", customer, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);

    }

    @Override
    protected OfficeBO getBranchOffice() {
        return TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
    }
}