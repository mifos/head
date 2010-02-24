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

package org.mifos.reports.branchreport.persistence;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.mifos.framework.util.helpers.MoneyUtils.createMoney;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.reports.branchreport.BranchReportBO;
import org.mifos.reports.branchreport.BranchReportBOFixture;
import org.mifos.reports.branchreport.BranchReportClientSummaryBO;
import org.mifos.reports.branchreport.BranchReportLoanArrearsAgingBO;
import org.mifos.reports.branchreport.BranchReportStaffSummaryBO;
import org.mifos.reports.branchreport.BranchReportStaffingLevelSummaryBO;
import org.mifos.reports.branchreport.LoanArrearsAgingPeriod;
import org.mifos.customers.personnel.business.service.PersonnelBusinessService;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.reports.business.service.BranchReportIntegrationTestCase;
import org.mifos.config.AccountingRules;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.CollectionUtils;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

public class BranchReportPersistenceIntegrationTest extends BranchReportIntegrationTestCase {

    public BranchReportPersistenceIntegrationTest() throws Exception {
        super();
    }

    private static final Short LOAN_OFFICER_ID_SHORT = Short.valueOf("3");
    private static final String TOTAL_STAFF_ROLENAME_STR = "Total Staff";
    private static final Short BRANCH_ID = Short.valueOf("1");
    private Session session;
    private Transaction transaction;
    private Date runDate;
    private BranchReportPersistence branchReportPersistence;
    private BranchReportBO branchReportWithLoanArrears;
    private BranchReportBO branchReportWithClientSummaries;
    private BranchReportBO branchReportWithStaffSummary;

    public void testGetBranchReportBatchForDateAndBranch() throws Exception {
        BranchReportBO branchReportBO = new BranchReportBO(BRANCH_ID, runDate);
        session.save(branchReportBO);
        List<BranchReportBO> retrievedBranchReports = branchReportPersistence.getBranchReport(BRANCH_ID, runDate);
        assertListSizeAndTrueCondition(1, retrievedBranchReports, PredicateUtils.equalPredicate(branchReportBO));
    }

    public void testRetrievesEmptyListIfNoBranchReportBatchForDateAndBranch() throws Exception {
        List<BranchReportBO> retrievedBranchReports = branchReportPersistence.getBranchReport(BRANCH_ID, runDate);
        assertListSizeAndTrueCondition(0, retrievedBranchReports, PredicateUtils.truePredicate());
    }

    public void testRetrievesEmptyListIfNoBranchReportsForGivenDate() throws Exception {
        List<BranchReportBO> retrievedBranchReports = branchReportPersistence.getBranchReport(runDate);
        assertListSizeAndTrueCondition(0, retrievedBranchReports, PredicateUtils.truePredicate());
    }

    public void testRetrievesBranchReportsForGivenDate() throws Exception {
        BranchReportBO branchReportBO = new BranchReportBO(BRANCH_ID, runDate);
        session.save(branchReportBO);
        List<BranchReportBO> retrievedBranchReports = branchReportPersistence.getBranchReport(runDate);
        assertListSizeAndTrueCondition(1, retrievedBranchReports, PredicateUtils.equalPredicate(branchReportBO));
    }

    public void testSaveBranchReportWithLoanArrearsAndRetrieveUsingBranchReport() throws Exception {
        session.save(branchReportWithLoanArrears);
        List<BranchReportBO> retrievedBranchReports = branchReportPersistence.getBranchReport(BRANCH_ID, runDate);
        assertListSizeAndTrueCondition(1, retrievedBranchReports, PredicateUtils
                .equalPredicate(branchReportWithLoanArrears));
    }

    public void testSaveBranchReportWithClientSummaryAndRetrieveUsingBranchReport() throws Exception {
        session.save(branchReportWithClientSummaries);
        List<BranchReportBO> retrievedBranchReports = branchReportPersistence.getBranchReport(BRANCH_ID, runDate);
        assertListSizeAndTrueCondition(1, retrievedBranchReports, PredicateUtils
                .equalPredicate(branchReportWithClientSummaries));
    }

    public void testReturnsEmptyListIfNoBranchReportClientSummaryBatchForDateAndBranch() throws Exception {
        retrieveAndAssertBranchReportClientSummaryForBranchAndDate(0, PredicateUtils.truePredicate());
    }

    public void testGetLoanArrearsReportReturnsEmptyListIfReportNotFound() throws Exception {
        retrieveAndAssertLoanArrearsReportForBranchAndDate(0, PredicateUtils.truePredicate());
    }

    public void testGetLoanArrearsReportForDateAndBranch() throws Exception {
        session.save(branchReportWithLoanArrears);
        retrieveAndAssertLoanArrearsReportForBranchAndDate(1, PredicateUtils.equalPredicate(CollectionUtils
                .first(branchReportWithLoanArrears.getLoanArrearsAging())));
    }

    public void testRetrieveStaffSummaryForDateAndBranchReturnsEmptyListIfNoDataPresent() throws Exception {
        retrieveAndAssertBranchReportStaffSummaryForBranchAndDate(0, PredicateUtils.truePredicate());
    }

    public void testRetrieveStaffSummaryForDateAndBranch() throws Exception {
        session.save(branchReportWithStaffSummary);
        retrieveAndAssertBranchReportStaffSummaryForBranchAndDate(1, PredicateUtils.equalPredicate(CollectionUtils
                .first(branchReportWithStaffSummary.getStaffSummaries())));
    }

    // TODO TW Add test data and have better test
    public void testExtractStaffSummaryActiveBorrowersCountReturnsEmptyListIfNoDataPresent() throws Exception {
        List<BranchReportStaffSummaryBO> staffSummary = branchReportPersistence.extractBranchReportStaffSummary(Short
                .valueOf("2"), Integer.valueOf(1), DEFAULT_CURRENCY);
       Assert.assertTrue(staffSummary.isEmpty());
    }

    public void testPopulateCustomersFormedByLoanOfficerReturnsIfSummaryListIsEmpty() throws Exception {
        HashMap staffSummariesMock = createMock(HashMap.class);
        expect(staffSummariesMock.isEmpty()).andReturn(true);
        replay(staffSummariesMock);
        branchReportPersistence.populateTotalClientsEnrolledByPersonnel(staffSummariesMock);
        verify(staffSummariesMock);
    }

    public void testPopulateCustomersFormedByLoanOfficerReadsSummaries() throws Exception {
        HashSet<Short> personnelIds = new HashSet<Short>();
        personnelIds.add(LOAN_OFFICER_ID_SHORT);
        HashMap staffSummariesMock = createMock(HashMap.class);
        expect(staffSummariesMock.isEmpty()).andReturn(false);
        expect(staffSummariesMock.keySet()).andReturn(personnelIds);
        replay(staffSummariesMock);
        branchReportPersistence.populateTotalClientsEnrolledByPersonnel(staffSummariesMock);
        verify(staffSummariesMock);
    }

    private Map<Short, BranchReportStaffSummaryBO> createStaffSummariesMap() {
        Set<BranchReportStaffSummaryBO> staffSummaries = branchReportWithStaffSummary.getStaffSummaries();
        HashMap<Short, BranchReportStaffSummaryBO> map = new HashMap<Short, BranchReportStaffSummaryBO>();
        for (BranchReportStaffSummaryBO summaryBO : staffSummaries) {
            map.put(summaryBO.getPersonnelId(), summaryBO);
        }
        return map;
    }

    public void testPopulateCustomersFormedBySetsTotalClientsEnrolledBy() throws Exception {
        Map<Short, BranchReportStaffSummaryBO> staffSummaries = createStaffSummariesMap();
        branchReportPersistence.populateTotalClientsEnrolledByPersonnel(staffSummaries);
        Assert.assertNull(org.apache.commons.collections.CollectionUtils.find(staffSummaries.values(), new Predicate() {
            public boolean evaluate(Object object) {
                return !Integer.valueOf(0).equals(((BranchReportStaffSummaryBO) object).getTotalClientsEnrolled());
            }
        }));
    }

    public void testPopulateCustomersFormedThisMonthSetsCustomersEnrolledThisMonth() throws Exception {
        HashMap staffSummariesMock = createMock(HashMap.class);
        expect(staffSummariesMock.isEmpty()).andReturn(true);
        replay(staffSummariesMock);
        branchReportPersistence.populateClientsEnrolledByPersonnelThisMonth(staffSummariesMock);
        verify(staffSummariesMock);
    }

    public void testPopulateCustomersEnrolledByLoanOfficerThisMonthReadsSummaries() throws Exception {
        HashSet<Short> personnelIds = new HashSet<Short>();
        personnelIds.add(LOAN_OFFICER_ID_SHORT);
        HashMap staffSummariesMock = createMock(HashMap.class);
        expect(staffSummariesMock.isEmpty()).andReturn(false);
        expect(staffSummariesMock.keySet()).andReturn(personnelIds);
        replay(staffSummariesMock);
        branchReportPersistence.populateClientsEnrolledByPersonnelThisMonth(staffSummariesMock);
        verify(staffSummariesMock);
    }

    public void testPopulateLoanArrearsAmountReturnsIfSummaryListisEmpty() throws Exception {
        HashMap staffSummariesMock = createMock(HashMap.class);
        expect(staffSummariesMock.isEmpty()).andReturn(true);
        replay(staffSummariesMock);
        branchReportPersistence.populateLoanArrearsAmountForPersonnel(staffSummariesMock, DEFAULT_CURRENCY);
        verify(staffSummariesMock);
    }

    public void testPopulateLoanArrearsAmountReadsSummaries() throws Exception {
        HashSet<Short> personnelIds = new HashSet<Short>();
        personnelIds.add(LOAN_OFFICER_ID_SHORT);
        HashMap staffSummariesMock = createMock(HashMap.class);
        expect(staffSummariesMock.isEmpty()).andReturn(false);
        expect(staffSummariesMock.keySet()).andReturn(personnelIds);
        replay(staffSummariesMock);
        branchReportPersistence.populateLoanArrearsAmountForPersonnel(staffSummariesMock, DEFAULT_CURRENCY);
        verify(staffSummariesMock);
    }

    public void testPopulateLoanArrearsAmountSetsLoanArrearsAmount() throws Exception {
        Map<Short, BranchReportStaffSummaryBO> staffSummaries = createStaffSummariesMap();
        branchReportPersistence.populateLoanArrearsAmountForPersonnel(staffSummaries, DEFAULT_CURRENCY);
        Assert.assertNull(org.apache.commons.collections.CollectionUtils.find(staffSummaries.values(), new Predicate() {
            public boolean evaluate(Object object) {
                return !BigDecimal.ZERO.setScale(AccountingRules.getDigitsAfterDecimal()).equals(
                        ((BranchReportStaffSummaryBO) object).getLoanArrearsAmount());
            }
        }));
    }

    public void testRetrieveLoanArrearsAging() throws Exception {
        BranchReportLoanArrearsAgingBO loanArrears = branchReportPersistence.extractLoanArrearsAgingInfoInPeriod(
                LoanArrearsAgingPeriod.FIVE_TO_EIGHT_WEEK, Short.valueOf("2"), DEFAULT_CURRENCY);
        Assert.assertNotNull(loanArrears);
    }

    public void testSaveLoanArrearsBOWithLargeValueForAmountOutstanding() throws Exception {
        BranchReportLoanArrearsAgingBO branchReportLoanArrearsAgingBO = new BranchReportLoanArrearsAgingBO(
                LoanArrearsAgingPeriod.FIVE_TO_EIGHT_WEEK, Integer.valueOf(1), Integer.valueOf(2),
                createMoney(TestUtils.RUPEE, 15724323.10), createMoney(TestUtils.RUPEE,1283439.70), 
                createMoney(TestUtils.RUPEE, 459625.70));
        BranchReportBO branchReport = BranchReportBOFixture.createBranchReport(null, Short.valueOf("2"), DateUtils
                .currentDate());
        branchReport.addLoanArrearsAging(branchReportLoanArrearsAgingBO);
        try {
            session.save(branchReport);
            transaction.commit();
            transaction = session.beginTransaction();
            deleteBranchReport(branchReport.getBranchReportId());
        } catch (Exception e) {
            e.printStackTrace(System.out);
            Assert.fail("Should not throw error when saving: " + e);
        }
    }

    public void testExtractingLoanArrears() throws Exception {
        BranchReportLoanArrearsAgingBO result = branchReportPersistence.extractLoanArrearsAgingInfoInPeriod(
                LoanArrearsAgingPeriod.ONE_WEEK, Short.valueOf("2"), DEFAULT_CURRENCY);
        Assert.assertNotNull(result);
    }

    public void testExtractStaffingSummaryLevels() throws Exception {
        List<BranchReportStaffingLevelSummaryBO> staffingLevels = branchReportPersistence
                .extractBranchReportStaffingLevelSummary(LOAN_OFFICER_ID_SHORT);
       Assert.assertEquals(2, staffingLevels.size());
        Assert.assertNull("Should not extract roles with zero personnel count", org.apache.commons.collections.CollectionUtils
                .find(staffingLevels, new Predicate() {
                    public boolean evaluate(Object arg0) {
                        BranchReportStaffingLevelSummaryBO summary = (BranchReportStaffingLevelSummaryBO) arg0;
                        return !TOTAL_STAFF_ROLENAME_STR.equals(summary.getRolename())
                                && Integer.valueOf(0).equals((summary).getPersonnelCount());
                    }
                }));
        for (BranchReportStaffingLevelSummaryBO summaryBO : staffingLevels) {
            if (TOTAL_STAFF_ROLENAME_STR.equals(summaryBO.getRolename())) {
               Assert.assertEquals(Integer.valueOf(2), summaryBO.getPersonnelCount());
            }
        }
    }

    public void testExtractStaffSummaryGetsOnlyLoanOfficers() throws Exception {
        List<BranchReportStaffSummaryBO> staffSummaries = branchReportPersistence.extractBranchReportStaffSummary(
                BRANCH_ID, Integer.valueOf(1), DEFAULT_CURRENCY);
        for (BranchReportStaffSummaryBO summaryBO : staffSummaries) {
            PersonnelLevel retrievedPersonnelLevel = new PersonnelBusinessService().getPersonnel(
                    summaryBO.getPersonnelId()).getLevelEnum();
           Assert.assertEquals(PersonnelLevel.LOAN_OFFICER, retrievedPersonnelLevel);
        }
    }

    private void retrieveAndAssertLoanArrearsReportForBranchAndDate(int resultCount, Predicate mustBeTrue)
            throws PersistenceException {
        List<BranchReportLoanArrearsAgingBO> retrievedLoanArrearsReports = branchReportPersistence
                .getLoanArrearsAgingReport(BRANCH_ID, runDate);
        assertListSizeAndTrueCondition(resultCount, retrievedLoanArrearsReports, mustBeTrue);
    }

    private void retrieveAndAssertBranchReportClientSummaryForBranchAndDate(int resultCount, Predicate predicate)
            throws PersistenceException {
        List<BranchReportClientSummaryBO> retrievedBranchReports = branchReportPersistence
                .getBranchReportClientSummary(BRANCH_ID, runDate);
        assertListSizeAndTrueCondition(resultCount, retrievedBranchReports, predicate);
    }

    private void retrieveAndAssertBranchReportStaffSummaryForBranchAndDate(int resultCount, Predicate predicate)
            throws PersistenceException {
        List<BranchReportStaffSummaryBO> retrievedBranchReports = branchReportPersistence.getBranchReportStaffSummary(
                BRANCH_ID, runDate);
        assertListSizeAndTrueCondition(resultCount, retrievedBranchReports, predicate);
    }

    private void deleteBranchReport(Integer reportId) throws PersistenceException {
        branchReportPersistence.delete(branchReportPersistence.getPersistentObject(BranchReportBO.class, reportId));
        transaction.commit();
        transaction = session.beginTransaction();
    }

    private void assertListSizeAndTrueCondition(int resultCount, List retrievedReports, Predicate mustBeTrue) {
       Assert.assertEquals(resultCount, retrievedReports.size());
       Assert.assertTrue(mustBeTrue.evaluate(CollectionUtils.first(retrievedReports)));
    }

    @Override
    protected void tearDown() throws Exception {
        transaction.rollback();
        super.tearDown();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        branchReportPersistence = new BranchReportPersistence();
        runDate = DateUtils.getDate(2008, Calendar.JANUARY, 1);
        branchReportWithLoanArrears = new BranchReportBO(BRANCH_ID, runDate);
        branchReportWithLoanArrears.addLoanArrearsAging(new BranchReportLoanArrearsAgingBO(
                LoanArrearsAgingPeriod.ONE_WEEK));

        branchReportWithClientSummaries = new BranchReportBO(BRANCH_ID, runDate);
        branchReportWithClientSummaries.addClientSummary(BranchReportBOFixture
                .createBranchReportClientSummaryBO(BranchReportClientSummaryBO.CENTER_COUNT));

        branchReportWithStaffSummary = new BranchReportBO(BRANCH_ID, runDate);
        branchReportWithStaffSummary.addStaffSummary(BranchReportBOFixture.createBranchReportStaffSummaryBO());

        session = StaticHibernateUtil.getSessionTL();
        transaction = session.beginTransaction();
    }
}
