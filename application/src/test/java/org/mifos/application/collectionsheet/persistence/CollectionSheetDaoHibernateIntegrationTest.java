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

package org.mifos.application.collectionsheet.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsProductBuilder;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.accounts.savings.persistence.SavingsDao;
import org.mifos.accounts.savings.persistence.SavingsDaoHibernate;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.CollectionSheetCustomerAccountCollectionDto;
import org.mifos.application.servicefacade.CollectionSheetCustomerDto;
import org.mifos.application.servicefacade.CollectionSheetCustomerLoanDto;
import org.mifos.application.servicefacade.CollectionSheetCustomerSavingsAccountDto;
import org.mifos.application.servicefacade.CollectionSheetLoanFeeDto;
import org.mifos.application.servicefacade.CustomerHierarchyParams;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.TestObjectFactory;

/**
 *
 */
public class CollectionSheetDaoHibernateIntegrationTest extends MifosIntegrationTestCase {

    public CollectionSheetDaoHibernateIntegrationTest() throws Exception {
        super();
    }

    // class under test
    private CollectionSheetDao collectionSheetDao;

    // collaborators
    private final GenericDao genericDao = new GenericDaoHibernate();
    private final SavingsDao savingsDao = new SavingsDaoHibernate(genericDao);

    private MeetingBO weeklyMeeting;
    private AmountFeeBO weeklyPeriodicFeeForCenterOnly;
    private AmountFeeBO weeklyPeriodicFeeForGroupOnly;
    private AmountFeeBO weeklyPeriodicFeeForClientsOnly;
    private CustomerBO center;
    private GroupBO group;
    private ClientBO client;
    private SavingsBO savingsAccount;
    private SavingsOfferingBO savingsProduct;
    private SavingsOfferingBO savingsProduct2;
    private SavingsBO savingsAccount2;
    private LoanBO loan;
    private LoanOfferingBO loanOffering;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        weeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();
        IntegrationTestObjectMother.saveMeeting(weeklyMeeting);

        weeklyPeriodicFeeForCenterOnly = new FeeBuilder().appliesToCenterOnly().withFeeAmount("100.0").withName(
                "Center Weekly Periodic Fee").withSameRecurrenceAs(weeklyMeeting).withOffice(sampleBranchOffice())
                .build();
        IntegrationTestObjectMother.saveFee(weeklyPeriodicFeeForCenterOnly);

        center = new CenterBuilder().withMeeting(weeklyMeeting).withName("Center").withOffice(sampleBranchOffice())
                .withLoanOfficer(testUser()).withFee(weeklyPeriodicFeeForCenterOnly).build();
        IntegrationTestObjectMother.createCenter((CenterBO)center, weeklyMeeting, weeklyPeriodicFeeForCenterOnly);

        weeklyPeriodicFeeForGroupOnly = new FeeBuilder().appliesToGroupsOnly().withFeeAmount("50.0").withName(
                "Group Weekly Periodic Fee").withSameRecurrenceAs(weeklyMeeting).withOffice(sampleBranchOffice())
                .build();
        IntegrationTestObjectMother.saveFee(weeklyPeriodicFeeForGroupOnly);

        group = new GroupBuilder().withMeeting(weeklyMeeting).withName("Group").withOffice(sampleBranchOffice())
                .withLoanOfficer(testUser()).withFee(weeklyPeriodicFeeForGroupOnly).withParentCustomer(center).build();
        IntegrationTestObjectMother.createGroup(group, weeklyMeeting, weeklyPeriodicFeeForGroupOnly);

        weeklyPeriodicFeeForClientsOnly = new FeeBuilder().appliesToClientsOnly().withFeeAmount("10.0").withName(
                "Client Weekly Periodic Fee").withSameRecurrenceAs(weeklyMeeting).withOffice(sampleBranchOffice())
                .build();
        IntegrationTestObjectMother.saveFee(weeklyPeriodicFeeForClientsOnly);

        client = new ClientBuilder().withMeeting(weeklyMeeting).withName("Client 1").withOffice(sampleBranchOffice())
                .withLoanOfficer(testUser()).withFee(weeklyPeriodicFeeForClientsOnly).withParentCustomer(group)
                .buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(client, weeklyMeeting);

        collectionSheetDao = new CollectionSheetDaoHibernate(savingsDao);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        IntegrationTestObjectMother.cleanSavingsProductAndAssociatedSavingsAccounts(savingsAccount, savingsAccount2);
        TestObjectFactory.cleanUp(loan);
        IntegrationTestObjectMother.cleanCustomerHierarchyWithMeetingAndFees(client, group, center, weeklyMeeting);
    }

    public void testShouldRetrieveCustomerHierarchyWithACenterAsRootByBranchId() throws Exception {

        // setup
        final Integer customerId = center.getCustomerId();
        final LocalDate transactionDate = new LocalDate();

        // exercise test
        final List<CollectionSheetCustomerDto> customerHierarchy = collectionSheetDao.findCustomerHierarchy(customerId,
                transactionDate);

        // verification
        assertNotNull(customerHierarchy);
        assertFalse(customerHierarchy.isEmpty());
        assertNotNull(customerHierarchy.get(0));

        assertEquals(center.getCustomerId(), customerHierarchy.get(0).getCustomerId());
        assertThat(customerHierarchy.get(0).getParentCustomerId(), is(nullValue()));
        assertEquals(center.getDisplayName(), customerHierarchy.get(0).getName());
        assertEquals(center.getSearchId(), customerHierarchy.get(0).getSearchId());
        assertEquals(center.getLevel().getValue(), customerHierarchy.get(0).getLevelId());
        assertNull("center should have no attendance against them", customerHierarchy.get(0).getAttendanceId());

        assertNotNull(customerHierarchy.get(1));

        assertEquals(group.getCustomerId(), customerHierarchy.get(1).getCustomerId());
        assertThat(customerHierarchy.get(1).getParentCustomerId(), is(center.getCustomerId()));
        assertEquals(group.getDisplayName(), customerHierarchy.get(1).getName());
        assertEquals(group.getSearchId(), customerHierarchy.get(1).getSearchId());
        assertEquals(group.getLevel().getValue(), customerHierarchy.get(1).getLevelId());
        assertNull("group should have no attendance against them", customerHierarchy.get(1).getAttendanceId());
    }

    public void testShouldRetrieveCustomerHierarchyWithAGroupAsRootByBranchId() throws Exception {

        // setup
        final Integer customerId = group.getCustomerId();
        final LocalDate transactionDate = new LocalDate();

        // exercise test
        final List<CollectionSheetCustomerDto> customerHierarchy = collectionSheetDao.findCustomerHierarchy(customerId,
                transactionDate);

        // verification
        assertNotNull(customerHierarchy);
        assertFalse(customerHierarchy.isEmpty());
        assertNotNull(customerHierarchy.get(0));

        assertNotNull(customerHierarchy.get(0));

        assertEquals(group.getCustomerId(), customerHierarchy.get(0).getCustomerId());
        assertEquals(group.getDisplayName(), customerHierarchy.get(0).getName());
        assertEquals(group.getSearchId(), customerHierarchy.get(0).getSearchId());
        assertEquals(group.getLevel().getValue(), customerHierarchy.get(0).getLevelId());
        assertNull("group should have no attendance against them", customerHierarchy.get(0).getAttendanceId());

        assertNotNull(customerHierarchy.get(1));

        assertEquals(client.getCustomerId(), customerHierarchy.get(1).getCustomerId());
        assertEquals(client.getDisplayName(), customerHierarchy.get(1).getName());
        assertEquals(client.getSearchId(), customerHierarchy.get(1).getSearchId());
        assertEquals(client.getLevel().getValue(), customerHierarchy.get(1).getLevelId());
        assertNull("client should have no attendance against them", customerHierarchy.get(1).getAttendanceId());
    }

    // TODO - keithw - write another test for loans on clients and set up all
    // types of
    // fees, one-time, periodic, penalties, miscFee etc
    public void testShouldFindAllLoanRepaymentInCenterHierarchy() {

        // setup
        Date startDate = new Date(System.currentTimeMillis());
        loanOffering = TestObjectFactory.createLoanOffering("Loancfgb", "dhsq", ApplicableTo.GROUPS, startDate,
                PrdStatus.LOAN_ACTIVE, 300.0, 1.2, (short) 3, InterestType.FLAT, weeklyMeeting);

        loan = TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);

        final Integer customerAtTopOfHierarchyId = center.getCustomerId();
        final Short branchId = center.getOffice().getOfficeId();
        final String searchId = center.getSearchId() + ".%";
        final LocalDate transactionDate = new LocalDate();
        // exercise test

        final Map<Integer, List<CollectionSheetCustomerLoanDto>> allLoanRepayments = collectionSheetDao
                .findAllLoanRepaymentsForCustomerHierarchy(branchId, searchId, transactionDate,
                        customerAtTopOfHierarchyId);

        // verification
        assertNotNull(allLoanRepayments);
        assertNotNull(allLoanRepayments.get(group.getCustomerId()));

        List<CollectionSheetCustomerLoanDto> loansAgainstGroup = allLoanRepayments.get(group.getCustomerId());
        assertThat(loansAgainstGroup.size(), is(1));
        assertThat(loansAgainstGroup.get(0).getAccountId(), is(loan.getAccountId()));
        assertThat(loansAgainstGroup.get(0).getAccountStateId(), is(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING
                .getValue()));
        assertThat(loansAgainstGroup.get(0).getProductShortName(), is(loanOffering.getPrdOfferingShortName()));
        assertThat(loansAgainstGroup.get(0).getTotalRepaymentDue(), is(Double.valueOf("112.00")));
    }

    public void testShouldFindOutstandingFeesForLoansInCenterHierarchy() {

        // setup
        Date startDate = new Date(System.currentTimeMillis());
        loanOffering = TestObjectFactory.createLoanOffering("Loancfgb", "dhsq", ApplicableTo.GROUPS, startDate,
                PrdStatus.LOAN_ACTIVE, 300.0, 1.2, (short) 3, InterestType.FLAT, weeklyMeeting);

        loan = TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);

        final Integer customerAtTopOfHierarchyId = center.getCustomerId();
        final Short branchId = center.getOffice().getOfficeId();
        final String searchId = center.getSearchId() + ".%";
        final LocalDate transactionDate = new LocalDate();

        // exercise test
        Map<Integer, Map<Integer, List<CollectionSheetLoanFeeDto>>> allOutstandingFeesByCustomerThenAccountId = collectionSheetDao
                .findOutstandingFeesForLoansOnCustomerHierarchy(branchId, searchId, transactionDate,
                        customerAtTopOfHierarchyId);

        // verification
        assertNotNull(allOutstandingFeesByCustomerThenAccountId);
        assertNotNull(allOutstandingFeesByCustomerThenAccountId.get(group.getCustomerId()));
        final Map<Integer, List<CollectionSheetLoanFeeDto>> loanFeesByAccountId = allOutstandingFeesByCustomerThenAccountId
                .get(group.getCustomerId());

        assertThat(loanFeesByAccountId.size(), is(1));

        final List<CollectionSheetLoanFeeDto> loanFeesAgainstGroupAccountLoan = loanFeesByAccountId.get(loan
                .getAccountId());
        assertThat(loanFeesAgainstGroupAccountLoan.size(), is(1));

        assertThat(loanFeesAgainstGroupAccountLoan.get(0).getCustomerId(), is(group.getCustomerId()));
        assertThat(loanFeesAgainstGroupAccountLoan.get(0).getAccountId(), is(loan.getAccountId()));
        assertThat(loanFeesAgainstGroupAccountLoan.get(0).getFeeAmountDue(), is(new BigDecimal("100.0000")));
        assertThat(loanFeesAgainstGroupAccountLoan.get(0).getFeeAmountPaid(), is(new BigDecimal("0.0000")));
        assertThat(loanFeesAgainstGroupAccountLoan.get(0).getTotalFeeAmountDue(), is(Double.valueOf("100.0")));
    }

    public void testShouldFindAccountCollectionFeesForCustomerAccounts() {

        // setup
        final Short branchId = center.getOffice().getOfficeId();
        final String searchId = center.getSearchId() + ".%";
        final LocalDate transactionDate = new LocalDate();

        // exercise test
        Map<Integer, List<CollectionSheetCustomerAccountCollectionDto>> allCustomerAccountCollectionFees = collectionSheetDao
                .findAccountCollectionsOnCustomerAccount(branchId, searchId, transactionDate, center.getCustomerId());

        // verification
        assertNotNull(allCustomerAccountCollectionFees);
        final List<CollectionSheetCustomerAccountCollectionDto> accountCollections = allCustomerAccountCollectionFees
                .get(group.getCustomerId());

        assertNotNull(accountCollections);
        assertThat(accountCollections.size(), is(1));
        assertThat(accountCollections.get(0).getAccountId(), is(group.getCustomerAccount().getAccountId()));
        assertThat(accountCollections.get(0).getCustomerId(), is(group.getCustomerId()));
        assertThat(accountCollections.get(0).getMiscFeesDue(), is(new BigDecimal("0.0000")));
        assertThat(accountCollections.get(0).getMiscFeesPaid(), is(new BigDecimal("0.0000")));
        assertThat(accountCollections.get(0).getAccountCollectionPayment(), is(Double.valueOf("0.0")));
    }

    public void testShouldFindOutstandingFeesForCustomerAccountsXXX() {

        // setup
        final Short branchId = center.getOffice().getOfficeId();
        final String searchId = center.getSearchId() + ".%";
        final LocalDate transactionDate = new LocalDate();

        // exercise test
        Map<Integer, List<CollectionSheetCustomerAccountCollectionDto>> allOutstandingFeesByCustomerId = collectionSheetDao
                .findOutstandingFeesForCustomerAccountOnCustomerHierarchy(branchId, searchId, transactionDate, center
                        .getCustomerId());

        // verification
        assertNotNull(allOutstandingFeesByCustomerId);
        final List<CollectionSheetCustomerAccountCollectionDto> accountCollectionFees = allOutstandingFeesByCustomerId
                .get(group.getCustomerId());

        assertNotNull(accountCollectionFees);
        assertThat(accountCollectionFees.size(), is(1));
        assertThat(accountCollectionFees.get(0).getAccountId(), is(group.getCustomerAccount().getAccountId()));
        assertThat(accountCollectionFees.get(0).getCustomerId(), is(group.getCustomerId()));
        assertThat(accountCollectionFees.get(0).getMiscFeesDue(), is(new BigDecimal("0")));
        assertThat(accountCollectionFees.get(0).getMiscFeesPaid(), is(new BigDecimal("0")));
        assertThat(accountCollectionFees.get(0).getAccountCollectionPayment(), is(Double.valueOf("0.0")));
    }

    public void testShouldFindLoanAccountsInDisbursementState() {

        // setup
        Date startDate = new Date(System.currentTimeMillis());
        loanOffering = TestObjectFactory.createLoanOffering("Loancfgb", "dhsq", ApplicableTo.GROUPS, startDate,
                PrdStatus.LOAN_ACTIVE, 300.0, 1.2, (short) 3, InterestType.FLAT, weeklyMeeting);

        loan = TestObjectFactory.createLoanAccountWithDisbursement("42423142341", group, AccountState.LOAN_APPROVED,
                startDate, loanOffering, 1);

        final Integer customerAtTopOfHierarchyId = center.getCustomerId();
        final Short branchId = center.getOffice().getOfficeId();
        final String searchId = center.getSearchId() + ".%";
        final LocalDate transactionDate = new LocalDate();

        // exercise test
        final Map<Integer, List<CollectionSheetCustomerLoanDto>> allLoanDisbursements = collectionSheetDao
                .findLoanDisbursementsForCustomerHierarchy(branchId, searchId, transactionDate,
                        customerAtTopOfHierarchyId);

        // verification
        assertNotNull(allLoanDisbursements);
        final List<CollectionSheetCustomerLoanDto> loanDisbursements = allLoanDisbursements.get(group.getCustomerId());
        assertThat(loanDisbursements.size(), is(1));
        assertThat(loanDisbursements.get(0).getAccountId(), is(loan.getAccountId()));
        assertThat(loanDisbursements.get(0).getAccountStateId(), is(AccountState.LOAN_APPROVED.getValue()));
        assertThat(loanDisbursements.get(0).getCustomerId(), is(group.getCustomerId()));
        assertThat(loanDisbursements.get(0).getPayInterestAtDisbursement(), is(Constants.NO));
        assertThat(loanDisbursements.get(0).getTotalDisbursement(), is(Double.valueOf("300.0")));
        assertThat(loanDisbursements.get(0).getAmountDueAtDisbursement(), is(Double.valueOf("30.0")));
    }

    public void testShouldFindSavingsDepositsforCustomerHierarchy() {

        // setup
        savingsProduct = new SavingsProductBuilder().mandatory().appliesToCentersOnly().buildForIntegrationTests();
        savingsAccount = new SavingsAccountBuilder().mandatory().withSavingsProduct(savingsProduct)
                .withCustomer(center).build();
        IntegrationTestObjectMother.saveSavingsProductAndAssociatedSavingsAccounts(savingsProduct, savingsAccount);

        savingsProduct2 = new SavingsProductBuilder().withName("product2").mandatory().appliesToCentersOnly()
                .buildForIntegrationTests();
        savingsAccount2 = new SavingsAccountBuilder().mandatory().withSavingsProduct(savingsProduct2).withCustomer(
                center).build();
        IntegrationTestObjectMother.saveSavingsProductAndAssociatedSavingsAccounts(savingsProduct2, savingsAccount2);

        final Integer customerAtTopOfHierarchyId = center.getCustomerId();
        final Short branchId = center.getOffice().getOfficeId();
        final String searchId = center.getSearchId() + ".%";
        final LocalDate transactionDate = new LocalDate();

        final CustomerHierarchyParams customerHierarchyParams = new CustomerHierarchyParams(customerAtTopOfHierarchyId,
                branchId, searchId, transactionDate);

        // exercise test
        final List<CollectionSheetCustomerSavingsAccountDto> allSavingsDeposits = collectionSheetDao
                .findAllSavingAccountsForCustomerHierarchy(customerHierarchyParams);

        // verification
        assertThat(allSavingsDeposits.size(), is(2));
    }

}
