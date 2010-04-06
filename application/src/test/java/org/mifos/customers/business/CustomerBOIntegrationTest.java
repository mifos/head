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

package org.mifos.customers.business;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.Assert;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanBOTestUtils;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.savings.business.SavingBOTestUtils;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
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
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CustomerBOIntegrationTest extends MifosIntegrationTestCase {
    public CustomerBOIntegrationTest() throws Exception {
        super();
    }

    private AccountBO accountBO;
    private CenterBO center;
    private GroupBO group;
    private ClientBO client;
    private CustomerPersistence customerPersistence = new CustomerPersistence();
    private MeetingBO meeting;
    private SavingsTestHelper helper = new SavingsTestHelper();
    private SavingsOfferingBO savingsOffering;
    private PersonnelBO loanOfficer;
    private OfficeBO createdBranchOffice;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUp(accountBO);
            TestObjectFactory.cleanUp(client);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);

            TestObjectFactory.cleanUp(loanOfficer);
            TestObjectFactory.cleanUp(createdBranchOffice);
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        }
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testRemoveGroupMemberShip() throws Exception {
        createInitialObjects();
        client.setUserContext(TestUtils.makeUser());
        StaticHibernateUtil.getInterceptor().createInitialValueMap(client);
        createPersonnel(PersonnelLevel.LOAN_OFFICER);
        client.removeGroupMemberShip(loanOfficer, "comment");
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        loanOfficer = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class,
                loanOfficer.getPersonnelId());
        Assert.assertEquals(loanOfficer.getPersonnelId(), client.getPersonnel().getPersonnelId());
        group = TestObjectFactory.getGroup(group.getCustomerId());

        TestObjectFactory.cleanUpChangeLog();
    }

    public void testHasActiveLoanAccounts() throws Exception {
        createInitialObjects();
        client.setUserContext(TestUtils.makeUser());
        StaticHibernateUtil.getInterceptor().createInitialValueMap(client);
        boolean res = client.hasActiveLoanAccounts();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        Assert.assertEquals(res, false);
        TestObjectFactory.cleanUpChangeLog();
    }

    public void testCheckIfClientIsATitleHolder() throws Exception {
        createInitialObjects();
        client.setUserContext(TestUtils.makeUser());
        StaticHibernateUtil.getInterceptor().createInitialValueMap(client);

        try {
            client.checkIfClientIsATitleHolder();

        } catch (CustomerException expected) {
            Assert.assertEquals(CustomerConstants.CLIENT_IS_A_TITLE_HOLDER_EXCEPTION, expected.getKey());
        }

        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        TestObjectFactory.cleanUpChangeLog();
    }

    public void testGroupPerfObject() throws PersistenceException {
        createInitialObjects();
        GroupPerformanceHistoryEntity groupPerformanceHistory = group.getGroupPerformanceHistory();
        GroupTestUtils.setLastGroupLoanAmount(groupPerformanceHistory, new Money(getCurrency(), "100"));
        TestObjectFactory.updateObject(group);
        StaticHibernateUtil.closeSession();
        group = (GroupBO) customerPersistence
                .findBySystemId(group.getGlobalCustNum(), group.getCustomerLevel().getId());
        Assert.assertEquals(group.getCustomerId(), group.getGroupPerformanceHistory().getGroup().getCustomerId());
        Assert.assertEquals(new Money(getCurrency(), "100"), group.getGroupPerformanceHistory()
                .getLastGroupLoanAmount());
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
    }

    public void testGroupPerformanceObject() throws Exception {
        GroupPerformanceHistoryEntity groupPerformanceHistory = new GroupPerformanceHistoryEntity(Integer.valueOf("1"),
                new Money(getCurrency(), "23"), new Money(getCurrency(), "24"), new Money(getCurrency(), "26"),
                new Money(getCurrency(), "25"), new Money(getCurrency(), "27"));
        Assert.assertEquals(new Money(getCurrency(), "23"), groupPerformanceHistory.getLastGroupLoanAmount());
        Assert.assertEquals(new Money(getCurrency(), "27"), groupPerformanceHistory.getPortfolioAtRisk());
        Assert.assertEquals(1, groupPerformanceHistory.getClientCount().intValue());

    }

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

    public void testGetBalanceForAccountsAtRisk() {
        createInitialObjects();
        accountBO = getLoanAccount(group, meeting);
        TestObjectFactory.flushandCloseSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
        Assert.assertEquals(new Money(getCurrency()), getBalanceForAccountsAtRisk());
        changeFirstInstallmentDate(accountBO, 31);
        Assert.assertEquals(new Money(getCurrency(), "300"), getBalanceForAccountsAtRisk());
        TestObjectFactory.flushandCloseSession();
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

    public void testGetOutstandingLoanAmount() {
        createInitialObjects();
        accountBO = getLoanAccount(group, meeting);
        TestObjectFactory.flushandCloseSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        Assert.assertEquals(new Money(getCurrency(), "300.0"), group.getOutstandingLoanAmount(getCurrency()));
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
    }

    public void testGetActiveLoanCounts() {
        createInitialObjects();
        accountBO = getLoanAccount(group, meeting);
        TestObjectFactory.flushandCloseSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        Assert.assertEquals(1, group.getActiveLoanCounts().intValue());
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
    }

    public void testGetOpenIndividualLoanAccounts() {
        createInitialObjects();
        accountBO = getIndividualLoanAccount(group, meeting);
        TestObjectFactory.flushandCloseSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        List<LoanBO> loans = group.getOpenIndividualLoanAccounts();
        Assert.assertEquals(1, loans.size());

        TestObjectFactory.flushandCloseSession();

        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
    }

    public void testGetLoanAccountInUse() {
        createInitialObjects();
        accountBO = getLoanAccount(group, meeting);
        TestObjectFactory.flushandCloseSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        List<LoanBO> loans = group.getOpenLoanAccounts();
        Assert.assertEquals(1, loans.size());
        Assert.assertEquals(accountBO.getAccountId(), loans.get(0).getAccountId());
        TestObjectFactory.flushandCloseSession();

        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
    }

    public void testGetSavingsAccountInUse() throws Exception {
        accountBO = getSavingsAccount("fsaf6", "ads6");
        TestObjectFactory.flushandCloseSession();
        client = TestObjectFactory.getClient(client.getCustomerId());
        List<SavingsBO> savings = client.getOpenSavingAccounts();
        Assert.assertEquals(1, savings.size());
        Assert.assertEquals(accountBO.getAccountId(), savings.get(0).getAccountId());
        TestObjectFactory.flushandCloseSession();

        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
    }

    public void testHasAnyLoanAccountInUse() {
        createInitialObjects();
        accountBO = getLoanAccount(group, meeting);
        TestObjectFactory.flushandCloseSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        Assert.assertTrue(group.isAnyLoanAccountOpen());
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
    }

    public void testHasAnySavingsAccountInUse() throws Exception {
        accountBO = getSavingsAccount("fsaf5", "ads5");
        TestObjectFactory.flushandCloseSession();
        client = TestObjectFactory.getClient(client.getCustomerId());
        Assert.assertTrue(client.isAnySavingsAccountOpen());
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
    }

    public void testgetSavingsBalance() throws Exception {
        SavingsBO savings = getSavingsAccount("fsaf4", "ads4");
        SavingBOTestUtils.setBalance(savings, new Money(getCurrency(), "1000"));
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        Assert.assertEquals(new Money(getCurrency(), "1000.0"), savings.getSavingsBalance());
        Assert.assertEquals(new Money(getCurrency(), "1000.0"), client.getSavingsBalance(getCurrency()));
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        TestObjectFactory.cleanUp(savings);
    }

    public void testApplicablePrdforCustomLevel() throws Exception {
        createInitialObjects();
        Assert.assertEquals(Short.valueOf("1"), client.getCustomerLevel().getProductApplicableType());
        Assert.assertEquals(Short.valueOf("3"), center.getCustomerLevel().getProductApplicableType());
    }

    public void testCustomerPerformanceView() throws Exception {
        CustomerPerformanceHistoryView customerPerformanceView = new CustomerPerformanceHistoryView(Integer
                .valueOf("1"), Integer.valueOf("1"), "10");

        Assert.assertEquals(1, customerPerformanceView.getMeetingsAttended().intValue());
        Assert.assertEquals(1, customerPerformanceView.getMeetingsMissed().intValue());
        Assert.assertEquals("10", customerPerformanceView.getLastLoanAmount());

    }

    public void testCustomerPositionView() throws Exception {
        CustomerPositionView customerPositionView = new CustomerPositionView(Integer.valueOf("1"), Short.valueOf("2"));

        Assert.assertEquals(1, customerPositionView.getCustomerId().intValue());
        Assert.assertEquals(2, customerPositionView.getPositionId().shortValue());

    }

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
            LoanBOTestUtils.setActionDate(accountActionDateEntity, new java.sql.Date(currentDateCalendar
                    .getTimeInMillis()));
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
    }

    private AccountBO getLoanAccount(CustomerBO customer, MeetingBO meeting) {
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", customer, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);

    }

    private AccountBO getIndividualLoanAccount(CustomerBO customer, MeetingBO meeting) {
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        return TestObjectFactory.createIndividualLoanAccount("42423142341", customer,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate, loanOffering);

    }

    private void createPersonnel(PersonnelLevel personnelLevel) throws Exception {
        List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
        customFieldView.add(new CustomFieldView(Short.valueOf("9"), "123456", CustomFieldType.NUMERIC));
        Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd");
        Name name = new Name("XYZ", null, null, "Last Name");
        java.util.Date date = new java.util.Date();
        loanOfficer = new PersonnelBO(personnelLevel, getBranchOffice(), Integer.valueOf("1"), Short.valueOf("1"),
                "ABCD", "XYZ", "xyz@yahoo.com", null, customFieldView, name, "111111", date, Integer.valueOf("1"),
                Integer.valueOf("1"), date, date, address, Short.valueOf("1"));
        loanOfficer.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        loanOfficer = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class,
                loanOfficer.getPersonnelId());

    }

    @Override
    protected OfficeBO getBranchOffice() {
        return TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
    }
}