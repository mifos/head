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

package org.mifos.application.collectionsheet.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.ClientDetailView;
import org.mifos.application.customer.client.business.ClientNameDetailView;
import org.mifos.application.customer.client.business.NameType;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.persistence.GroupPersistence;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.servicefacade.CollectionSheetCustomerDto;
import org.mifos.application.servicefacade.CollectionSheetCustomerAccountCollectionDto;
import org.mifos.application.servicefacade.CollectionSheetCustomerLoanDto;
import org.mifos.application.servicefacade.CollectionSheetCustomerSavingDto;
import org.mifos.application.servicefacade.CollectionSheetLoanFeeDto;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.TestObjectFactory;

/**
 *
 */
public class CollectionSheetDaoHibernateIntegrationTest extends MifosIntegrationTestCase {

    public CollectionSheetDaoHibernateIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    // class under test
    private CollectionSheetDao collectionSheetDao;
    
    // collaborators
    private CustomerBO center;
    private GroupBO group;
    private ClientBO client;
    private MeetingBO meeting;
    private LoanBO loan;
    private LoanOfferingBO loanOffering;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestObjectFactory.cleanUp(loan);
        TestObjectFactory.cleanUp(client);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
        TestObjectFactory.cleanUp(meeting);
        
        
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));

        center = new CenterBO(TestUtils.makeUserWithLocales(), "Center", null, null, TestObjectFactory.getFees(), null,
                null, sampleBranchOffice(), meeting, testUser(), new CustomerPersistence());

        group = new GroupBO(TestUtils.makeUserWithLocales(), "Group", CustomerStatus.GROUP_ACTIVE, null, false, null,
                null, TestObjectFactory.getCustomFields(), TestObjectFactory.getFees(), testUser(), center,
                new GroupPersistence(), new OfficePersistence());

        final int sampleSalutationValue = 1;
        ClientDetailView clientDetailView = new ClientDetailView(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                .valueOf("1"), Short.valueOf("41"));
        ClientNameDetailView clientNameDetailView = new ClientNameDetailView(NameType.MAYBE_CLIENT, sampleSalutationValue,
                "Client", "middle", "Client", "secondLast");
        ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(NameType.SPOUSE, sampleSalutationValue,
                "Client", "middle", "Client", "secondLast");
        
        client = new ClientBO(TestUtils.makeUserWithLocales(), "Client", CustomerStatus.CLIENT_ACTIVE, null, null,
                null, null, TestObjectFactory.getFees(), null, testUser(), sampleBranchOffice(), group, new DateTime()
                        .toDate(), null, null, null, YesNoFlag.YES.getValue(), clientNameDetailView,
                spouseNameDetailView, clientDetailView, null);
        
        IntegrationTestObjectMother.saveCustomerHierarchy(center, group, client);
        
        collectionSheetDao = new CollectionSheetDaoHibernate();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        try {
            TestObjectFactory.cleanUp(loan);
            TestObjectFactory.cleanUp(client);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);
            TestObjectFactory.cleanUp(meeting);
        } catch (Exception e) {

        } finally {
            StaticHibernateUtil.closeSession();
        }
    }
    
    public void testShouldRetrieveCustomerHierarchyWithACenterAsRootByBranchId() throws Exception {
        
        // setup
        final Integer customerId = center.getCustomerId();
        final Date transactionDate = new Date();

        // exercise test
        final List<CollectionSheetCustomerDto> customerHierarchy = collectionSheetDao.findCustomerHierarchy(customerId,
                transactionDate);
        
        // verification
        assertNotNull(customerHierarchy);
        assertFalse(customerHierarchy.isEmpty());
        assertNotNull(customerHierarchy.get(0));

        assertEquals(center.getCustomerId(), customerHierarchy.get(0).getCustomerId());
        assertEquals(center.getDisplayName(), customerHierarchy.get(0).getName());
        assertEquals(center.getSearchId(), customerHierarchy.get(0).getSearchId());
        assertEquals(center.getLevel().getValue(), customerHierarchy.get(0).getLevelId());
        assertNull("center should have no attendance against them", customerHierarchy.get(0).getAttendanceId());

        assertNotNull(customerHierarchy.get(1));

        assertEquals(group.getCustomerId(), customerHierarchy.get(1).getCustomerId());
        assertEquals(group.getDisplayName(), customerHierarchy.get(1).getName());
        assertEquals(group.getSearchId(), customerHierarchy.get(1).getSearchId());
        assertEquals(group.getLevel().getValue(), customerHierarchy.get(1).getLevelId());
        assertNull("group should have no attendance against them", customerHierarchy.get(1).getAttendanceId());
    }
    
    public void testShouldRetrieveCustomerHierarchyWithAGroupAsRootByBranchId() throws Exception {

        // setup
        final Integer customerId = group.getCustomerId();
        final Date transactionDate = new Date();

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
    
    // TODO - write another test for loans on clients and set up all types of
    // fees, one-time, periodic, penalties, miscFee etc
    public void testShouldFindAllLoanRepaymentInCenterHierarchy() {
        
        // setup
        Date startDate = new Date(System.currentTimeMillis());
        loanOffering = TestObjectFactory.createLoanOffering("Loancfgb", "dhsq", ApplicableTo.GROUPS, startDate,
                PrdStatus.LOAN_ACTIVE, 300.0, 1.2, (short) 3, InterestType.FLAT, meeting);

        loan = TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);

        final Short branchId = center.getOffice().getOfficeId();
        final String searchId = center.getSearchId() + ".%";
        final java.util.Date transactionDate = new DateTime().toDateMidnight().toDate();

        // exercise test
        final Map<Integer, List<CollectionSheetCustomerLoanDto>> allLoanRepayments = collectionSheetDao
                .findAllLoanRepaymentsForCustomerHierarchy(branchId, searchId, transactionDate);

        // verification
        assertNotNull(allLoanRepayments);
        assertNotNull(allLoanRepayments.get(group.getCustomerId()));
        
        List<CollectionSheetCustomerLoanDto> loansAgainstGroup = allLoanRepayments.get(group.getCustomerId());
        assertThat(loansAgainstGroup.size(), is(1));
        assertThat(loansAgainstGroup.get(0).getAccountId(), is(loan.getAccountId()));
        assertThat(loansAgainstGroup.get(0).getProductShortName(), is(loanOffering.getPrdOfferingShortName()));
        assertThat(loansAgainstGroup.get(0).getTotalRepaymentDue(), is(Double.valueOf("112.00")));
    }
    
    public void testShouldFindOutstandingFeesForLoansInCenterHierarchy() {

        // setup
        Date startDate = new Date(System.currentTimeMillis());
        loanOffering = TestObjectFactory.createLoanOffering("Loancfgb", "dhsq", ApplicableTo.GROUPS, startDate,
                PrdStatus.LOAN_ACTIVE, 300.0, 1.2, (short) 3, InterestType.FLAT, meeting);

        loan = TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);

        final Short branchId = center.getOffice().getOfficeId();
        final String searchId = center.getSearchId() + ".%";
        final java.util.Date transactionDate = new DateTime().toDateMidnight().toDate();

        // exercise test
        Map<Integer, Map<Integer, List<CollectionSheetLoanFeeDto>>> allOutstandingFeesByCustomerThenAccountId = collectionSheetDao
                .findOutstandingFeesForLoansOnCustomerHierarchy(branchId, searchId, transactionDate);

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
        assertThat(loanFeesAgainstGroupAccountLoan.get(0).getFeeAmountDue(), is(new BigDecimal("100.000")));
        assertThat(loanFeesAgainstGroupAccountLoan.get(0).getFeeAmountPaid(), is(new BigDecimal("0.000")));
        assertThat(loanFeesAgainstGroupAccountLoan.get(0).getTotalFeeAmountDue(), is(Double.valueOf("100.0")));
    }
    
    public void testShouldFindAccountCollectionFeesForCustomerAccountsXXX() {

        // setup
        final Short branchId = center.getOffice().getOfficeId();
        final String searchId = center.getSearchId() + ".%";
        final java.util.Date transactionDate = new DateTime().toDateMidnight().toDate();

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
        assertThat(accountCollections.get(0).getMiscFeesDue(), is(new BigDecimal("0.000")));
        assertThat(accountCollections.get(0).getMiscFeesPaid(), is(new BigDecimal("0.000")));
        assertThat(accountCollections.get(0).getAccountCollectionPayment(), is(Double.valueOf("0.0")));
    }
    
    public void testShouldFindOutstandingFeesForCustomerAccountsXXX() {

        // setup
        final Short branchId = center.getOffice().getOfficeId();
        final String searchId = center.getSearchId() + ".%";
        final java.util.Date transactionDate = new DateTime().toDateMidnight().toDate();

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
                PrdStatus.LOAN_ACTIVE, 300.0, 1.2, (short) 3, InterestType.FLAT, meeting);
        loan = TestObjectFactory.createLoanAccountWithDisbursement("42423142341", group, AccountState.LOAN_APPROVED,
                startDate, loanOffering, 1);
        
        final Short branchId = center.getOffice().getOfficeId();
        final String searchId = center.getSearchId() + ".%";
        final java.util.Date transactionDate = new DateTime().toDateMidnight().toDate();

        // exercise test
        final Map<Integer, List<CollectionSheetCustomerLoanDto>> allLoanDisbursements = collectionSheetDao
                .findLoanDisbursementsForCustomerHierarchy(branchId, searchId, transactionDate);

        // verification
        assertNotNull(allLoanDisbursements);
        final List<CollectionSheetCustomerLoanDto> loanDisbursements = allLoanDisbursements.get(group.getCustomerId());
        assertThat(loanDisbursements.size(), is(1));
        assertThat(loanDisbursements.get(0).getAccountId(), is(loan.getAccountId()));
        assertThat(loanDisbursements.get(0).getCustomerId(), is(group.getCustomerId()));
        assertThat(loanDisbursements.get(0).getPayInterestAtDisbursement(), is(Constants.NO));
        assertThat(loanDisbursements.get(0).getTotalDisbursement(), is(Double.valueOf("330.0")));
    }
    
    public void testShouldFindSavingAccountsDeposits() {

        // setup
        final Short branchId = center.getOffice().getOfficeId();
        final String searchId = center.getSearchId() + ".%";
        final java.util.Date transactionDate = new DateTime().toDateMidnight().toDate();

        // exercise test
        Map<Integer, List<CollectionSheetCustomerSavingDto>> allSavingAccountsByCustomerId = collectionSheetDao
                .findSavingsDepositsforCustomerHierarchy(branchId, searchId, transactionDate);

        // verification
        assertNotNull(allSavingAccountsByCustomerId);
        // TODO add saving account for test
        // assertNotNull(allSavingAccountsByCustomerId.get(group.getCustomerId()));
    }
}
