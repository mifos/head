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

package org.mifos.application.cashconfirmationreport.persistence;

import static org.mifos.framework.util.helpers.MoneyUtils.zero;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.cashconfirmationreport.BranchCashConfirmationCenterRecoveryBO;
import org.mifos.application.cashconfirmationreport.BranchCashConfirmationDisbursementBO;
import org.mifos.application.cashconfirmationreport.BranchCashConfirmationInfoBO;
import org.mifos.application.cashconfirmationreport.BranchCashConfirmationIssueBO;
import org.mifos.application.cashconfirmationreport.BranchCashConfirmationReportBO;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.accounts.productdefinition.persistence.PrdOfferingPersistence;
import org.mifos.application.reports.business.service.BranchReportIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.AssertionUtils;
import org.mifos.framework.util.CollectionUtils;
import org.mifos.framework.util.helpers.DateUtils;

public class BranchCashConfirmationReportPersistenceIntegrationTest extends BranchReportIntegrationTestCase {
    public BranchCashConfirmationReportPersistenceIntegrationTest() throws Exception {
        super();
    }

    private static final int DIGITS_AFTER_DECIMAL_FOR_AMOUNT_IN_DB = 3;
    private static final List<Short> DISBURSEMENT_PRODUCT_OFFERING_IDS = Arrays.asList(new Short[] { 1, 2 });
    private Session session;
    private Transaction transaction;
    private BranchCashConfirmationReportPersistence persistence;
    private BranchCashConfirmationReportBO reportBO;
    private MifosCurrency currency;
    private BranchCashConfirmationReportBO firstJanReportBO;

    public void testRetrievesTheReportFromDB() throws Exception {
        session.save(firstJanReportBO);
        List<BranchCashConfirmationReportBO> fetchedReports = persistence
                .getBranchCashConfirmationReportsForDate(FIRST_JAN_2008);
        Assert.assertNotNull(fetchedReports);
       Assert.assertEquals(1, fetchedReports.size());
       Assert.assertEquals(firstJanReportBO, fetchedReports.get(0));
    }

    public void testReturnsEmptyListIfReportForGivenDateAndBranchDoesNotExist() throws Exception {
        List<BranchCashConfirmationReportBO> report = persistence.getBranchCashConfirmationReportsForDateAndBranch(
                Short.valueOf("3"), FIRST_JAN_2008);
        Assert.assertNotNull(report);
       Assert.assertTrue(report.isEmpty());
    }

    public void testReturnsReportListIfReportForGivenDateAndBranchExist() throws Exception {
        session.save(firstJanReportBO);
        List<BranchCashConfirmationReportBO> report = persistence.getBranchCashConfirmationReportsForDateAndBranch(
                BRANCH_ID_SHORT, FIRST_JAN_2008);
        Assert.assertNotNull(report);
       Assert.assertEquals(1, report.size());
    }

    public void testRemovesTheReportFromDB() throws Exception {
        BranchCashConfirmationReportBO newReport = new BranchCashConfirmationReportBO(Short.valueOf("1"), RUN_DATE);
        session.save(newReport);
        persistence.delete(newReport);
        List<BranchCashConfirmationReportBO> fetchedReports = persistence
                .getBranchCashConfirmationReportsForDate(RUN_DATE);
        Assert.assertNotNull(fetchedReports);
       Assert.assertTrue(fetchedReports.isEmpty());
    }

    public void testRetrieveIssueReportForGivenDateAndBranch() throws Exception {
        BranchCashConfirmationInfoBO issueBO = new BranchCashConfirmationIssueBO("SOME PRODUCT", zero());
        reportBO.addCenterIssue(issueBO);
        BranchCashConfirmationInfoBO anotherIssue = new BranchCashConfirmationIssueBO("SOMEMORE", zero());
        reportBO.addCenterIssue(anotherIssue);
        session.save(reportBO);
        List<BranchCashConfirmationInfoBO> retrievedIssues = persistence.getCenterIssues(BRANCH_ID_SHORT, RUN_DATE);
        Assert.assertNotNull(retrievedIssues);
       Assert.assertEquals(2, retrievedIssues.size());
       Assert.assertTrue(retrievedIssues.contains(issueBO));
       Assert.assertTrue(retrievedIssues.contains(anotherIssue));
    }

    public void testRetrievesCenterRecoveryReport() throws Exception {
        BranchCashConfirmationCenterRecoveryBO recoveryReport = new BranchCashConfirmationCenterRecoveryBO("PRDOFF1",
                zero(), zero(), zero());
        reportBO.addCenterRecovery(recoveryReport);
        session.save(reportBO);
        List<BranchCashConfirmationCenterRecoveryBO> retrievedRecoveryReport = persistence.getCenterRecoveries(
                BRANCH_ID_SHORT, RUN_DATE);
        Assert.assertNotNull(retrievedRecoveryReport);
       Assert.assertEquals(1, retrievedRecoveryReport.size());
       Assert.assertEquals(recoveryReport, retrievedRecoveryReport.get(0));
    }

    public void testExtractDisbursementReturnsEmptyListIfNoneExists() throws Exception {
        List<Object[]> disbursements = persistence.extractDisbursements(currency, AccountTypes.LOAN_ACCOUNT,
                DISBURSEMENT_PRODUCT_OFFERING_IDS, DateUtils.currentDate());
        Assert.assertNotNull(disbursements);
       Assert.assertTrue("disbursements should be empty", disbursements.isEmpty());
    }

    // TODO TW Test depends on inserting data into db breaking existing tests
    public void xtestExtractDisbursementsForGivenProductsAndDate() throws Exception {
        List<Object[]> disbursements = persistence.extractDisbursements(currency, AccountTypes.LOAN_ACCOUNT,
                DISBURSEMENT_PRODUCT_OFFERING_IDS, FIRST_JAN_2008);
        Assert.assertNotNull(disbursements);
        Assert.assertFalse("disbursments should not be empty", disbursements.isEmpty());
        AssertionUtils
                .assertSameCollections(Arrays.asList(new Object[] { Short.valueOf("3"), "TESTPRDOFFERING1",
                        BigDecimal.ZERO.setScale(DIGITS_AFTER_DECIMAL_FOR_AMOUNT_IN_DB) }), Arrays.asList(disbursements
                        .get(0)));
    }

    public void testRetrievesDisbursementsReturnsEmptyListIfNoneExists() throws Exception {
        List<BranchCashConfirmationDisbursementBO> disbursements = persistence.getDisbursements(BRANCH_ID_SHORT,
                RUN_DATE);
        Assert.assertNotNull("retrieved disbursements should not be null", disbursements);
       Assert.assertTrue("retrieved disbursements should be empty", disbursements.isEmpty());
    }

    public void testRetrievesDisbursementsForGivenDateAndBranch() throws Exception {
        BranchCashConfirmationDisbursementBO disbursement = new BranchCashConfirmationDisbursementBO("SOME PRODUCT",
                zero());
        BranchCashConfirmationDisbursementBO anotherDisbursement = new BranchCashConfirmationDisbursementBO(
                "ANOTHER PRODUCT", zero());
        reportBO.addDisbursement(disbursement);
        reportBO.addDisbursement(anotherDisbursement);
        session.save(reportBO);
        List<BranchCashConfirmationDisbursementBO> retrievedDisbursements = persistence.getDisbursements(
                BRANCH_ID_SHORT, RUN_DATE);
        Assert.assertNotNull("retrieved disbursements should not be null", retrievedDisbursements);
        Assert.assertFalse("retrieved disbursements should not be empty", retrievedDisbursements.isEmpty());
        AssertionUtils.assertSameCollections(CollectionUtils.asList(disbursement, anotherDisbursement),
                retrievedDisbursements);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        session = StaticHibernateUtil.getSessionTL();
        transaction = session.beginTransaction();
        persistence = new BranchCashConfirmationReportPersistence(new PrdOfferingPersistence());
        reportBO = new BranchCashConfirmationReportBO(BRANCH_ID_SHORT, RUN_DATE);
        firstJanReportBO = new BranchCashConfirmationReportBO(BRANCH_ID_SHORT, FIRST_JAN_2008);
        currency = TestUtils.RUPEE;
    }

    @Override
    protected void tearDown() throws Exception {
        transaction.rollback();
        super.tearDown();
    }
}
