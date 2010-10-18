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

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
import org.mifos.application.servicefacade.*;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.TestObjectFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;

/**
 *
 */
public class CollectionSheetDaoHibernateIntegrationTest extends MifosIntegrationTestCase {

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

    @Before
    public void setUp() throws Exception {
        weeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();
        IntegrationTestObjectMother.saveMeeting(weeklyMeeting);

        weeklyPeriodicFeeForCenterOnly = new FeeBuilder().appliesToCenterOnly().withFeeAmount("100.0").withName(
                "Center Weekly Periodic Fee").withSameRecurrenceAs(weeklyMeeting).with(sampleBranchOffice())
                .build();
        IntegrationTestObjectMother.saveFee(weeklyPeriodicFeeForCenterOnly);

        center = new CenterBuilder().with(weeklyMeeting).withName("Center").with(sampleBranchOffice())
                .withLoanOfficer(testUser()).build();
        IntegrationTestObjectMother.createCenter((CenterBO)center, weeklyMeeting, weeklyPeriodicFeeForCenterOnly);

        weeklyPeriodicFeeForGroupOnly = new FeeBuilder().appliesToGroupsOnly().withFeeAmount("50.0").withName(
                "Group Weekly Periodic Fee").withSameRecurrenceAs(weeklyMeeting).with(sampleBranchOffice())
                .build();
        IntegrationTestObjectMother.saveFee(weeklyPeriodicFeeForGroupOnly);

        group = new GroupBuilder().withMeeting(weeklyMeeting).withName("Group").withOffice(sampleBranchOffice())
                .withLoanOfficer(testUser()).withFee(weeklyPeriodicFeeForGroupOnly).withParentCustomer(center).build();
        IntegrationTestObjectMother.createGroup(group, weeklyMeeting, weeklyPeriodicFeeForGroupOnly);

        weeklyPeriodicFeeForClientsOnly = new FeeBuilder().appliesToClientsOnly().withFeeAmount("10.0").withName(
                "Client Weekly Periodic Fee").withSameRecurrenceAs(weeklyMeeting).with(sampleBranchOffice())
                .build();
        IntegrationTestObjectMother.saveFee(weeklyPeriodicFeeForClientsOnly);

        client = new ClientBuilder().withMeeting(weeklyMeeting).withName("Client 1").withOffice(sampleBranchOffice())
                .withLoanOfficer(testUser()).withParentCustomer(group)
                .buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(client, weeklyMeeting);

        collectionSheetDao = new CollectionSheetDaoHibernate(savingsDao);
    }

    @After
    public void tearDown() throws Exception {
        savingsAccount = null;
        savingsAccount2 = null;
        IntegrationTestObjectMother.cleanSavingsProductAndAssociatedSavingsAccounts(savingsAccount, savingsAccount2);
        loan = null;
        client = null;
        center = null;
        weeklyMeeting = null;
        IntegrationTestObjectMother.cleanCustomerHierarchyWithMeeting(client, group, center, weeklyMeeting);
    }

    @Test
    public void testShouldRetrieveCustomerHierarchyWithACenterAsRootByBranchId() throws Exception {

        // setup
        final Integer customerId = center.getCustomerId();
        final LocalDate transactionDate = new LocalDate();

        // exercise test
        final List<CollectionSheetCustomerDto> customerHierarchy = collectionSheetDao.findCustomerHierarchy(customerId,
                transactionDate);

        // verification
        Assert.assertNotNull(customerHierarchy);
        Assert.assertFalse(customerHierarchy.isEmpty());
        Assert.assertNotNull(customerHierarchy.get(0));

        Assert.assertEquals(center.getCustomerId(), customerHierarchy.get(0).getCustomerId());
        Assert.assertThat(customerHierarchy.get(0).getParentCustomerId(), is(nullValue()));
        Assert.assertEquals(center.getDisplayName(), customerHierarchy.get(0).getName());
        Assert.assertEquals(center.getSearchId(), customerHierarchy.get(0).getSearchId());
        Assert.assertEquals(center.getLevel().getValue(), customerHierarchy.get(0).getLevelId());
        Assert.assertNull("center should have no attendance against them", customerHierarchy.get(0).getAttendanceId());

        Assert.assertNotNull(customerHierarchy.get(1));

        Assert.assertEquals(group.getCustomerId(), customerHierarchy.get(1).getCustomerId());
        Assert.assertThat(customerHierarchy.get(1).getParentCustomerId(), is(center.getCustomerId()));
        Assert.assertEquals(group.getDisplayName(), customerHierarchy.get(1).getName());
        Assert.assertEquals(group.getSearchId(), customerHierarchy.get(1).getSearchId());
        Assert.assertEquals(group.getLevel().getValue(), customerHierarchy.get(1).getLevelId());
        Assert.assertNull("group should have no attendance against them", customerHierarchy.get(1).getAttendanceId());
    }

    @Test
    public void testShouldRetrieveCustomerHierarchyWithAGroupAsRootByBranchId() throws Exception {

        // setup
        final Integer customerId = group.getCustomerId();
        final LocalDate transactionDate = new LocalDate();

        // exercise test
        final List<CollectionSheetCustomerDto> customerHierarchy = collectionSheetDao.findCustomerHierarchy(customerId,
                transactionDate);

        // verification
        Assert.assertNotNull(customerHierarchy);
        Assert.assertFalse(customerHierarchy.isEmpty());
        Assert.assertNotNull(customerHierarchy.get(0));

        Assert.assertNotNull(customerHierarchy.get(0));

        Assert.assertEquals(group.getCustomerId(), customerHierarchy.get(0).getCustomerId());
        Assert.assertEquals(group.getDisplayName(), customerHierarchy.get(0).getName());
        Assert.assertEquals(group.getSearchId(), customerHierarchy.get(0).getSearchId());
        Assert.assertEquals(group.getLevel().getValue(), customerHierarchy.get(0).getLevelId());
        Assert.assertNull("group should have no attendance against them", customerHierarchy.get(0).getAttendanceId());

        Assert.assertNotNull(customerHierarchy.get(1));

        Assert.assertEquals(client.getCustomerId(), customerHierarchy.get(1).getCustomerId());
        Assert.assertEquals(client.getDisplayName(), customerHierarchy.get(1).getName());
        Assert.assertEquals(client.getSearchId(), customerHierarchy.get(1).getSearchId());
        Assert.assertEquals(client.getLevel().getValue(), customerHierarchy.get(1).getLevelId());
        Assert.assertNull("client should have no attendance against them", customerHierarchy.get(1).getAttendanceId());
    }

    @Test
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
        Assert.assertNotNull(allLoanRepayments);
        Assert.assertNotNull(allLoanRepayments.get(group.getCustomerId()));

        List<CollectionSheetCustomerLoanDto> loansAgainstGroup = allLoanRepayments.get(group.getCustomerId());
        Assert.assertThat(loansAgainstGroup.size(), is(1));
        Assert.assertThat(loansAgainstGroup.get(0).getAccountId(), is(loan.getAccountId()));
        Assert.assertThat(loansAgainstGroup.get(0).getAccountStateId(), is(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING
                .getValue()));
        Assert.assertThat(loansAgainstGroup.get(0).getProductShortName(), is(loanOffering.getPrdOfferingShortName()));
        Assert.assertThat(loansAgainstGroup.get(0).getTotalRepaymentDue(), is(Double.valueOf("112.00")));
    }

    @Test
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
        Assert.assertNotNull(allOutstandingFeesByCustomerThenAccountId);
        Assert.assertNotNull(allOutstandingFeesByCustomerThenAccountId.get(group.getCustomerId()));
        final Map<Integer, List<CollectionSheetLoanFeeDto>> loanFeesByAccountId = allOutstandingFeesByCustomerThenAccountId
                .get(group.getCustomerId());

        Assert.assertThat(loanFeesByAccountId.size(), is(1));

        final List<CollectionSheetLoanFeeDto> loanFeesAgainstGroupAccountLoan = loanFeesByAccountId.get(loan
                .getAccountId());
        Assert.assertThat(loanFeesAgainstGroupAccountLoan.size(), is(1));

        Assert.assertThat(loanFeesAgainstGroupAccountLoan.get(0).getCustomerId(), is(group.getCustomerId()));
        Assert.assertThat(loanFeesAgainstGroupAccountLoan.get(0).getAccountId(), is(loan.getAccountId()));
        Assert.assertThat(loanFeesAgainstGroupAccountLoan.get(0).getFeeAmountDue(), is(new BigDecimal("100.0000")));
        Assert.assertThat(loanFeesAgainstGroupAccountLoan.get(0).getFeeAmountPaid(), is(new BigDecimal("0.0000")));
        Assert.assertThat(loanFeesAgainstGroupAccountLoan.get(0).getTotalFeeAmountDue(), is(Double.valueOf("100.0")));
    }

    @Test
    public void testShouldFindAccountCollectionFeesForCustomerAccounts() {

        // setup
        final Short branchId = center.getOffice().getOfficeId();
        final String searchId = center.getSearchId() + ".%";
        final LocalDate transactionDate = new LocalDate();

        // exercise test
        Map<Integer, List<CollectionSheetCustomerAccountCollectionDto>> allCustomerAccountCollectionFees = collectionSheetDao
                .findAccountCollectionsOnCustomerAccount(branchId, searchId, transactionDate, center.getCustomerId());

        // verification
        Assert.assertNotNull(allCustomerAccountCollectionFees);
        final List<CollectionSheetCustomerAccountCollectionDto> accountCollections = allCustomerAccountCollectionFees
                .get(group.getCustomerId());

        Assert.assertNotNull(accountCollections);
        Assert.assertThat(accountCollections.size(), is(1));
        Assert.assertThat(accountCollections.get(0).getAccountId(), is(group.getCustomerAccount().getAccountId()));
        Assert.assertThat(accountCollections.get(0).getCustomerId(), is(group.getCustomerId()));
        Assert.assertThat(accountCollections.get(0).getMiscFeesDue(), is(new BigDecimal("0.0000")));
        Assert.assertThat(accountCollections.get(0).getMiscFeesPaid(), is(new BigDecimal("0.0000")));
        Assert.assertThat(accountCollections.get(0).getAccountCollectionPayment(), is(Double.valueOf("0.0")));
    }

    @Test
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
        Assert.assertNotNull(allOutstandingFeesByCustomerId);
        final List<CollectionSheetCustomerAccountCollectionDto> accountCollectionFees = allOutstandingFeesByCustomerId
                .get(group.getCustomerId());

        Assert.assertNotNull(accountCollectionFees);
        Assert.assertThat(accountCollectionFees.size(), is(1));
        Assert.assertThat(accountCollectionFees.get(0).getAccountId(), is(group.getCustomerAccount().getAccountId()));
        Assert.assertThat(accountCollectionFees.get(0).getCustomerId(), is(group.getCustomerId()));
        Assert.assertThat(accountCollectionFees.get(0).getMiscFeesDue(), is(new BigDecimal("0")));
        Assert.assertThat(accountCollectionFees.get(0).getMiscFeesPaid(), is(new BigDecimal("0")));
        Assert.assertThat(accountCollectionFees.get(0).getAccountCollectionPayment(), is(Double.valueOf("0.0")));
    }

    @Test
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
        Assert.assertNotNull(allLoanDisbursements);
        final List<CollectionSheetCustomerLoanDto> loanDisbursements = allLoanDisbursements.get(group.getCustomerId());
        Assert.assertThat(loanDisbursements.size(), is(1));
        Assert.assertThat(loanDisbursements.get(0).getAccountId(), is(loan.getAccountId()));
        Assert.assertThat(loanDisbursements.get(0).getAccountStateId(), is(AccountState.LOAN_APPROVED.getValue()));
        Assert.assertThat(loanDisbursements.get(0).getCustomerId(), is(group.getCustomerId()));
        Assert.assertThat(loanDisbursements.get(0).getPayInterestAtDisbursement(), is(Constants.NO));
        Assert.assertThat(loanDisbursements.get(0).getTotalDisbursement(), is(Double.valueOf("300.0")));
        Assert.assertThat(loanDisbursements.get(0).getAmountDueAtDisbursement(), is(Double.valueOf("30.0")));
    }

    @Test
    public void testShouldFindSavingsDepositsforCustomerHierarchy() {

        // setup
        savingsProduct = new SavingsProductBuilder().mandatory().appliesToCentersOnly().withShortName("SP1")
                .buildForIntegrationTests();
        savingsAccount = new SavingsAccountBuilder().mandatory().withSavingsProduct(savingsProduct)
                .withCustomer(center).build();
        IntegrationTestObjectMother.saveSavingsProductAndAssociatedSavingsAccounts(savingsProduct, savingsAccount);

        savingsProduct2 = new SavingsProductBuilder().withName("product2").withShortName("SP2").mandatory().appliesToCentersOnly()
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
        Assert.assertThat(allSavingsDeposits.size(), is(2));
    }

}
