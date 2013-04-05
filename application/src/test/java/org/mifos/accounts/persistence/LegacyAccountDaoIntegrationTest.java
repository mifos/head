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

package org.mifos.accounts.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mifos.accounts.AccountIntegrationTestCase;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountActionEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.financial.business.COABO;
import org.mifos.accounts.financial.business.COAHierarchyEntity;
import org.mifos.accounts.financial.business.GLCategoryType;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.holiday.persistence.HolidayDaoHibernate;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.LegacyMasterDao;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.servicefacade.TestCollectionSheetRetrieveSavingsAccountsUtils;
import org.mifos.customers.business.CustomerScheduleEntity;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.office.persistence.OfficeDaoHibernate;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.domain.builders.HolidayBuilder;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.TestGeneralLedgerCode;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class LegacyAccountDaoIntegrationTest extends AccountIntegrationTestCase {

    @Autowired
    LegacyMasterDao legacyMasterDao;

    @Autowired
    private LegacyAccountDao legacyAccountDao;

    public static final int LOAN_CUSTOMFIELDS_NUMBER = 1;
    private static final String ASSETS_GL_ACCOUNT_CODE = "10000";
    private static final String DIRECT_EXPENDITURE_GL_ACCOUNT_CODE = "41000";

    @Test
    public void testAddDuplicateGlAccounts() {
        String name = "New Account Name";
        String name2 = "New Account Name 2";
        String glCode = "999999";
        String parentGlCode = ASSETS_GL_ACCOUNT_CODE;

        try {
            legacyAccountDao.addGeneralLedgerAccount(name, glCode, parentGlCode, null);
            legacyAccountDao.addGeneralLedgerAccount(name2, glCode, parentGlCode, null);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("An account already exists with glcode"));
        }
    }

    @Test
    public void testAddGlAccount() {
        String name = "New Account Name";
        String glCode = "999999";
        String parentGlCode = ASSETS_GL_ACCOUNT_CODE;

        COABO coa = legacyAccountDao.addGeneralLedgerAccount(name, glCode, parentGlCode, null);
        Assert.assertEquals(coa.getAccountId(), legacyAccountDao.getAccountIdFromGlCode(glCode));
    }

    /**
     * The Chart of Accounts hierarchy is created when TestCaseInitializer is instantiated in parent class static
     * initializer. Verify it worked as planned.
     */
    @Test
    public void testAddCoaHierarchy() {
        short id = TestGeneralLedgerCode.COST_OF_FUNDS;
        COAHierarchyEntity h = (COAHierarchyEntity) StaticHibernateUtil.getSessionTL().load(COAHierarchyEntity.class,
                id);
        Assert.assertEquals(DIRECT_EXPENDITURE_GL_ACCOUNT_CODE, h.getParentAccount().getCoa().getAssociatedGlcode()
                .getGlcode());
    }

    /**
     * The top-level "ASSETS" general ledger account should always be the first one inserted. This will hopefully be
     * reliable enough for testing purposes.
     */
    @Test
    public void testGetAccountIdForGLCode() {
        Assert.assertEquals(new Short((short) 1), TestGeneralLedgerCode.ASSETS);
    }

    @Test
    public void testTopLevelAccountPersisted() throws Exception {
        COABO incomeCategory = legacyAccountDao.getCategory(GLCategoryType.INCOME);
        Assert.assertEquals(GLCategoryType.INCOME, incomeCategory.getCategoryType());
    }

    @Test
    public void testDumpChartOfAccounts() throws Exception {
        String expected_chart = "<configuration>" + "  <ChartOfAccounts>"
                + "    <GLAssetsAccount code=\"10000\" name=\"ASSETS\">"
                + "      <GLAccount code=\"11000\" name=\"Cash and bank balances\">"
                + "        <GLAccount code=\"11100\" name=\"Petty Cash Accounts\">"
                + "          <GLAccount code=\"11101\" name=\"Cash 1\"/>"
                + "          <GLAccount code=\"11102\" name=\"Cash 2\"/>" + "        </GLAccount>"
                + "        <GLAccount code=\"11200\" name=\"Bank Balances\">"
                + "          <GLAccount code=\"11201\" name=\"Bank Account 1\"/>"
                + "          <GLAccount code=\"11202\" name=\"Bank Account 2\"/>" + "        </GLAccount>"
                + "        <GLAccount code=\"11300\" name=\"Transfers\">"
                + "          <GLAccount code=\"11301\" name=\"Inter Office Transfers\"/>" + "        </GLAccount>"
                + "      </GLAccount>" + "      <GLAccount code=\"13000\" name=\"Loan Portfolio\">"
                + "        <GLAccount code=\"13100\" name=\"Loans and Advances\">"
                + "          <GLAccount code=\"13101\" name=\"Loans to clients\"/>"
                + "          <GLAccount code=\"1501\" name=\"IGLoan\"/>"
                + "          <GLAccount code=\"1502\" name=\"ManagedICICI-IGLoan\"/>"
                + "          <GLAccount code=\"1503\" name=\"SPLoan\"/>"
                + "          <GLAccount code=\"1504\" name=\"ManagedICICI-SPLoan\"/>"
                + "          <GLAccount code=\"1505\" name=\"WFLoan\"/>"
                + "          <GLAccount code=\"1506\" name=\"Managed WFLoan\"/>"
                + "          <GLAccount code=\"1507\" name=\"Emergency Loans\"/>"
                + "          <GLAccount code=\"1508\" name=\"Special Loans\"/>"
                + "          <GLAccount code=\"1509\" name=\"Micro Enterprises Loans\"/>" + "        </GLAccount>"
                + "        <GLAccount code=\"13200\" name=\"Loan Loss Provisions\">"
                + "          <GLAccount code=\"13201\" name=\"Write-offs\"/>" + "        </GLAccount>"
                + "      </GLAccount>" + "    </GLAssetsAccount>"
                + "    <GLLiabilitiesAccount code=\"20000\" name=\"LIABILITIES\">"
                + "      <GLAccount code=\"22000\" name=\"Interest Payable\">"
                + "        <GLAccount code=\"22100\" name=\"Interest payable on clients savings\">"
                + "          <GLAccount code=\"22101\" name=\"Interest on mandatory savings\"/>"
                + "        </GLAccount>" + "      </GLAccount>"
                + "      <GLAccount code=\"23000\" name=\"Clients Deposits\">"
                + "        <GLAccount code=\"23100\" name=\"Clients Deposits\">"
                + "          <GLAccount code=\"23101\" name=\"Savings accounts\"/>"
                + "          <GLAccount code=\"4601\" name=\"Emergency Fund\"/>"
                + "          <GLAccount code=\"4602\" name=\"Margin Money-1\"/>"
                + "          <GLAccount code=\"4603\" name=\"Margin Money-2\"/>"
                + "          <GLAccount code=\"4606\" name=\"Village Development Fund\"/>" + "        </GLAccount>"
                + "      </GLAccount>" + "      <GLAccount code=\"24000\" name=\"Mandatory Savings\">"
                + "        <GLAccount code=\"24100\" name=\"Mandatory Savings\">"
                + "          <GLAccount code=\"24101\" name=\"Mandatory Savings Accounts\"/>" + "        </GLAccount>"
                + "      </GLAccount>" + "    </GLLiabilitiesAccount>"
                + "    <GLIncomeAccount code=\"30000\" name=\"INCOME\">"
                + "      <GLAccount code=\"31000\" name=\"Direct Income\">"
                + "        <GLAccount code=\"31100\" name=\"Interest income from loans\">"
                + "          <GLAccount code=\"31101\" name=\"Interest on loans\"/>"
                + "          <GLAccount code=\"31102\" name=\"Penalty\"/>"
                + "          <GLAccount code=\"5001\" name=\"Interest\"/>" + "        </GLAccount>"
                + "        <GLAccount code=\"31300\" name=\"Income from micro credit &amp; lending activities\">"
                + "          <GLAccount code=\"31301\" name=\"Fees\"/>"
                + "          <GLAccount code=\"5201\" name=\"Processing Fees\"/>"
                + "          <GLAccount code=\"5202\" name=\"Annual Subscription Fee\"/>"
                + "          <GLAccount code=\"5203\" name=\"Emergency Loan Documentation Fee\"/>"
                + "          <GLAccount code=\"5204\" name=\"Sale of Publication\"/>"
                + "          <GLAccount code=\"5205\" name=\"Fines &amp; Penalties\"/>"
                + "          <GLAccount code=\"6201\" name=\"Miscelleneous Income\"/>" + "        </GLAccount>"
                + "      </GLAccount>" + "      <GLAccount code=\"31401\" name=\"Income from 999 Account\"/>"
                + "    </GLIncomeAccount>" + "    <GLExpenditureAccount code=\"40000\" name=\"EXPENDITURE\">"
                + "      <GLAccount code=\"41000\" name=\"Direct Expenditure\">"
                + "        <GLAccount code=\"41100\" name=\"Cost of Funds\">"
                + "          <GLAccount code=\"41101\" name=\"Interest on clients voluntary savings\"/>"
                + "          <GLAccount code=\"41102\" name=\"Interest on clients mandatory savings\"/>"
                + "        </GLAccount>" + "      </GLAccount>" + "    </GLExpenditureAccount>"
                + "  </ChartOfAccounts>" + "</configuration>";
        String chart = legacyAccountDao.dumpChartOfAccounts();

        // save old values so they can be restored when we clean up before
        // leaving this test method
        boolean ignoreWhitespace = XMLUnit.getIgnoreWhitespace();
        XMLUnit.setIgnoreWhitespace(true);

        XMLAssert.assertXMLEqual(expected_chart, chart);

        XMLUnit.setIgnoreWhitespace(ignoreWhitespace);
    }

    @Test
    public void testSuccessGetNextInstallmentList() {
        List<AccountActionDateEntity> installmentIdList = groupLoan.getApplicableIdsForFutureInstallments();
        Assert.assertEquals(5, installmentIdList.size());
    }

    @Test
    public void testSuccessLoadBusinessObject() throws Exception {
        AccountBO readAccount = legacyAccountDao.getAccount(groupLoan.getAccountId());
        Assert.assertEquals(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, readAccount.getState());
    }

    @Test
    public void testFailureLoadBusinessObject() {
        try {
            legacyAccountDao.getAccount(null);
            Assert.fail();
        } catch (PersistenceException expected) {
        }
    }

    @Test
    public void testGetAccountAction() throws Exception {
        AccountActionEntity accountAction = legacyMasterDao.getPersistentObject(
                AccountActionEntity.class, AccountActionTypes.SAVINGS_INTEREST_POSTING.getValue());
        Assert.assertNotNull(accountAction);
    }

    @Test
    public void testOptionalAccountStates() throws Exception {
        Assert.assertEquals(1, legacyAccountDao.getAccountStates(Short.valueOf("0")).size());
    }

    @Test
    public void testAccountStatesInUse() throws Exception {
        Assert.assertEquals(17, legacyAccountDao.getAccountStates(Short.valueOf("1")).size());
    }

    @Test
    public void testSearchAccount() throws Exception {
        savingsBO = createSavingsAccount();

        QueryResult queryResult = null;

        queryResult = legacyAccountDao.search(savingsBO.getGlobalAccountNum(), (short) 3);
        Assert.assertNotNull(queryResult);
        Assert.assertEquals(1, queryResult.getSize());
        Assert.assertEquals(1, queryResult.get(0, 10).size());
    }

    @Test
    public void testSearchCustomerAccount() throws Exception {
        QueryResult queryResult = null;
        queryResult = legacyAccountDao.search(center.getCustomerAccount().getGlobalAccountNum(), (short) 3);
        Assert.assertNull(queryResult);
    }

    @Test
    public void testGetListOfAccountIdsHavingLoanSchedulesWithinAHoliday() throws Exception {

        Set<HolidayBO> holidays;
        DateTime fromDate = new DateMidnight().toDateTime().plusDays(1);
        DateTime thruDate = new DateMidnight().toDateTime().plusDays(30);

        GenericDao genericDao = new GenericDaoHibernate();
        OfficeDao officeDao = new OfficeDaoHibernate(genericDao);
        HolidayDao holidayDao = new HolidayDaoHibernate(genericDao);

        OfficeBO sampleBranch = officeDao.findOfficeById(this.getBranchOffice().getOfficeId());
        holidays = new HashSet<HolidayBO>();
        holiday = new HolidayBuilder().withName("Welcome Holiday").from(fromDate).to(thruDate).build();
        holidayDao.save(holiday);
        holidays.add((HolidayBO) holiday);
        sampleBranch.setHolidays(holidays);
        new OfficePersistence().createOrUpdate(sampleBranch);
        StaticHibernateUtil.flushSession();


        List<Object[]> AccountIds = legacyAccountDao.getListOfAccountIdsHavingLoanSchedulesWithinAHoliday(
                holiday);

        Assert.assertNotNull(AccountIds);
        assertThat(AccountIds.size(), is(2));
    }

    @Test
    public void testGetListOfAccountIdsHavingCustomerSchedulesWithinAHoliday() throws Exception {

        Set<HolidayBO> holidays;
        DateTime fromDate = new DateMidnight().toDateTime().plusDays(1);
        DateTime thruDate = new DateMidnight().toDateTime().plusDays(30);

        GenericDao genericDao = new GenericDaoHibernate();
        OfficeDao officeDao = new OfficeDaoHibernate(genericDao);
        HolidayDao holidayDao = new HolidayDaoHibernate(genericDao);

        OfficeBO sampleBranch = officeDao.findOfficeById(this.getBranchOffice().getOfficeId());
        holidays = new HashSet<HolidayBO>();
        holiday = new HolidayBuilder().withName("Welcome Holiday").from(fromDate).to(thruDate).build();
        holidayDao.save(holiday);
        holidays.add((HolidayBO) holiday);
        sampleBranch.setHolidays(holidays);
        new OfficePersistence().createOrUpdate(sampleBranch);
        StaticHibernateUtil.flushSession();


        List<Object[]> AccountIds = legacyAccountDao.getListOfAccountIdsHavingCustomerSchedulesWithinAHoliday(
                holiday);

        Assert.assertNotNull(AccountIds);
        assertThat(AccountIds.size(), is(3));
    }

    @Test
    public void testGetLoanSchedulesForAccountThatAreWithinDates() throws Exception {

        DateTime fromDate = new DateMidnight().toDateTime().plusDays(1);
        DateTime thruDate = new DateMidnight().toDateTime().plusDays(30);
        List<LoanScheduleEntity> affectedDates = legacyAccountDao.getLoanSchedulesForAccountThatAreWithinDates(
                groupLoan.getAccountId(), fromDate, thruDate);

        Assert.assertNotNull(affectedDates);
        Assert.assertEquals(4, affectedDates.size());
    }

    @Test
    public void testGetCustomerSchedulesForAccountThatAreWithinDates() throws Exception {

        DateTime fromDate = new DateMidnight().toDateTime().plusDays(1);
        DateTime thruDate = new DateMidnight().toDateTime().plusDays(23);
        List<CustomerScheduleEntity> affectedDates = legacyAccountDao
                .getCustomerSchedulesForAccountThatAreWithinDates(center.getCustomerAccount().getAccountId(), fromDate,
                        thruDate);

        Assert.assertNotNull(affectedDates);
        Assert.assertEquals(3, affectedDates.size());
    }

    @Test
    public void testGetSavingsSchedulesForAccountThatAreWithinDates() throws Exception {

        savingsBO = new TestCollectionSheetRetrieveSavingsAccountsUtils().createSavingsAccount(group, "clm", "3.0",
                false, false);
        DateTime fromDate = new DateMidnight().toDateTime().plusDays(1);
        DateTime thruDate = new DateMidnight().toDateTime().plusDays(37);
        List<SavingsScheduleEntity> affectedDates = legacyAccountDao.getSavingsSchedulesForAccountThatAreWithinDates(
                savingsBO.getAccountId(), fromDate, thruDate);

        Assert.assertNotNull(affectedDates);
        Assert.assertEquals(5, affectedDates.size());

    }

    @Test
    public void testGetActiveCustomerAndSavingsAccountIdsForGenerateMeetingTaskShouldReturnNothing() throws Exception {
        // Superclass creates a center, a group, and a client that start meeting today
        // They should have 10 current or future meeting dates. Savings account should also have 10 deposit installments
        // so should not be retrieved.

        savingsBO = new SavingsTestHelper().createSavingsAccount(createSavingsOffering("qqqqq"), group,
                AccountState.SAVINGS_ACTIVE, TestUtils.makeUser());
        List<Integer> accountIds = legacyAccountDao.getActiveCustomerAndSavingsAccountIdsForGenerateMeetingTask();
        assertThat(accountIds.size(), is(0));

    }


    @Test
    public void testGetActiveCustomerAndSavingsAccountIdsForGenerateMeetingTaskShouldReturnThreeCustomerAccounts()
            throws Exception {

        // Superclass creates a center, a group, and a client that start meeting today
        // They should have 10 current or future meeting dates
        // Set time ahead 7 weeks to force regenerating customer schedules.

        new DateTimeService().setCurrentDateTime(new DateTime().plusWeeks(7));
        List<Integer> accountIds = legacyAccountDao.getActiveCustomerAndSavingsAccountIdsForGenerateMeetingTask();
        assertThat(accountIds.size(), is(3));
        assertThat(accountIds.contains(center.getCustomerAccount().getAccountId()), is(true));
        assertThat(accountIds.contains(group.getCustomerAccount().getAccountId()), is(true));
        assertThat(accountIds.contains(client.getCustomerAccount().getAccountId()), is(true));
    }

    @Test
    public void testGetActiveCustomerAndSavingsAccountIdsForGenerateMeetingTaskShouldReturnThreeCustomerAccountsAndOneSavingsAccount()
            throws Exception {

        // Superclass creates a center, a group, and a client that start meeting today
        // They should have 10 current or future meeting dates
        // Set time ahead 7 weeks to force regenerating customer schedules.

        savingsBO = new SavingsTestHelper().createSavingsAccount(createSavingsOffering("qqqqq"), group,
                AccountState.SAVINGS_ACTIVE, TestUtils.makeUser());

        new DateTimeService().setCurrentDateTime(new DateTime().plusWeeks(7));
        List<Integer> accountIds = legacyAccountDao.getActiveCustomerAndSavingsAccountIdsForGenerateMeetingTask();
        assertThat(accountIds.size(), is(4));
        assertThat(accountIds.contains(center.getCustomerAccount().getAccountId()), is(true));
        assertThat(accountIds.contains(group.getCustomerAccount().getAccountId()), is(true));
        assertThat(accountIds.contains(client.getCustomerAccount().getAccountId()), is(true));
        assertThat(accountIds.contains(savingsBO.getAccountId()), is(true));
    }

    @Test
    public void testFindingAccountPaymentShouldReturnOnePayment() throws Exception {
        savingsBO = createSavingsAccount();

        AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(savingsBO, TestUtils.createMoney(100),
                "1111", new Date(System.currentTimeMillis()), new PaymentTypeEntity(Short.valueOf("1")), new Date(System.currentTimeMillis()));
        List<AccountPaymentEntity> payments = new ArrayList<AccountPaymentEntity>();
        payments.add(accountPaymentEntity);
        savingsBO.setAccountPayments(payments);
        legacyAccountDao.createOrUpdate(savingsBO);
        StaticHibernateUtil.commitTransaction();

        List<AccountPaymentEntity> result = legacyAccountDao.findAccountPaymentsByReceiptNumber("1111");
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("1111", result.get(0).getReceiptNumber());
    }

    @Test
    public void testFindingAccountPaymentShouldReturnZeroPayments() throws Exception {
        savingsBO = createSavingsAccount();

        AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(savingsBO, TestUtils.createMoney(100),
                "2222", new Date(System.currentTimeMillis()), new PaymentTypeEntity(Short.valueOf("1")), new Date(System.currentTimeMillis()));
        List<AccountPaymentEntity> payments = new ArrayList<AccountPaymentEntity>();
        payments.add(accountPaymentEntity);
        savingsBO.setAccountPayments(payments);
        legacyAccountDao.createOrUpdate(savingsBO);
        StaticHibernateUtil.commitTransaction();

        List<AccountPaymentEntity> result = legacyAccountDao.findAccountPaymentsByReceiptNumber("1111");
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    /**
     * ******************
     * Helper methods
     * ******************
     */

    private SavingsBO createSavingsAccount() throws Exception {
        return TestObjectFactory.createSavingsAccount("12345678910", group, AccountState.SAVINGS_ACTIVE, new Date(),
                createSavingsOffering("qqqqq"), TestUtils.makeUser());
    }

    private SavingsOfferingBO createSavingsOffering(String offeringName) {
        Date startDate = new Date(System.currentTimeMillis());

        MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(RecurrenceType.WEEKLY,
                TestObjectFactory.EVERY_WEEK, MeetingType.CUSTOMER_MEETING));
        MeetingBO meetingIntPost = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(RecurrenceType.WEEKLY,
                TestObjectFactory.EVERY_WEEK, MeetingType.CUSTOMER_MEETING));
        return TestObjectFactory.createSavingsProduct(offeringName, ApplicableTo.GROUPS, startDate,
                PrdStatus.SAVINGS_ACTIVE, 300.0, RecommendedAmountUnit.PER_INDIVIDUAL, 1.2, 200.0, 200.0,
                SavingsType.VOLUNTARY, InterestCalcType.MINIMUM_BALANCE, meetingIntCalc, meetingIntPost);
    }

}
