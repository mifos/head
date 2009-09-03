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

package org.mifos.framework.components.batchjobs.helpers;

import static org.mifos.framework.util.AssertionUtils.assertNotEmpty;
import static org.mifos.framework.util.AssertionUtils.assertSameCollections;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.branchreport.BranchReportBO;
import org.mifos.application.branchreport.BranchReportClientSummaryBO;
import org.mifos.application.branchreport.BranchReportLoanArrearsAgingBO;
import org.mifos.application.branchreport.BranchReportLoanArrearsProfileBO;
import org.mifos.application.branchreport.BranchReportLoanDetailsBO;
import org.mifos.application.branchreport.BranchReportStaffSummaryBO;
import org.mifos.application.branchreport.BranchReportStaffingLevelSummaryBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.application.reports.business.service.BranchReportIntegrationTestCase;
import org.mifos.application.reports.business.service.BranchReportService;
import org.mifos.application.reports.business.service.IBranchReportService;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class BranchReportHelperIntegrationTest extends BranchReportIntegrationTestCase {

    public BranchReportHelperIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private BranchReportHelper branchReportHelper;
    private Session session;
    private Transaction transaction;

    public void testBatchCreatesRowForBranchReportBatchBO() throws PersistenceException, ServiceException,
            BatchJobException {
        branchReportHelper = new BranchReportHelper(new BranchReportTask());
        OfficeBO office = new OfficeBusinessService().getOffice(BRANCH_ID_SHORT);
        BranchReportBO generatedBranchReport = branchReportHelper.createBranchReport(session, office, RUN_DATE);
        IBranchReportService branchReportService = new BranchReportService();
        BranchReportBO retrievedBranchReports = branchReportService.getBranchReport(BRANCH_ID_SHORT, RUN_DATE);

        assertBranchReport(generatedBranchReport, retrievedBranchReports);
        assertClientSummaryReport(generatedBranchReport.getClientSummaries(), branchReportService);
        assertLoanArrearsAging(generatedBranchReport.getLoanArrearsAging(), branchReportService);
        assertStaffSummary(generatedBranchReport.getStaffSummaries(), branchReportService);
        assertStaffingLevelSummary(generatedBranchReport.getStaffingLevelSummaries(), branchReportService);
        assertLoanDetails(generatedBranchReport.getLoanDetails(), branchReportService);
        assertLoanArrearsProfile(generatedBranchReport.getLoanArrearsProfile(), branchReportService);
    }

    private void assertLoanArrearsProfile(Set<BranchReportLoanArrearsProfileBO> loanArrearsProfile,
            IBranchReportService branchReportService) throws ServiceException {
        List<BranchReportLoanArrearsProfileBO> retrievedLoanArrearsProfile = branchReportService.getLoanArrearsProfile(
                BRANCH_ID, RUN_DATE_STR);
        assertSameCollections(loanArrearsProfile, retrievedLoanArrearsProfile);
    }

    private void assertLoanDetails(Set<BranchReportLoanDetailsBO> loanDetails, IBranchReportService branchReportService)
            throws ServiceException {
        List<BranchReportLoanDetailsBO> retrievedLoanDetails = branchReportService.getLoanDetails(BRANCH_ID,
                RUN_DATE_STR);
        assertSameCollections(loanDetails, retrievedLoanDetails);
    }

    private void assertStaffingLevelSummary(Set<BranchReportStaffingLevelSummaryBO> staffingLevelSummaries,
            IBranchReportService branchReportService) throws ServiceException {
        List<BranchReportStaffingLevelSummaryBO> retrievedStaffingLevelSummaries = branchReportService
                .getStaffingLevelSummary(BRANCH_ID, RUN_DATE_STR);
        assertSameCollections(staffingLevelSummaries, retrievedStaffingLevelSummaries);
    }

    private boolean compareBranchReports(BranchReportBO generatedBranchReport, BranchReportBO retrievedBranchReports) {
        boolean result = false;
       Assert.assertEquals(generatedBranchReport.getBranchId(), retrievedBranchReports.getBranchId());
       Assert.assertEquals(generatedBranchReport.getBranchReportId(), retrievedBranchReports.getBranchReportId());
       Assert.assertEquals(generatedBranchReport.getClientSummaries().size(), retrievedBranchReports.getClientSummaries()
                .size());
       Assert.assertEquals(generatedBranchReport.getLoanArrearsAging().size(), retrievedBranchReports.getLoanArrearsAging()
                .size());
       Assert.assertEquals(generatedBranchReport.getLoanArrearsProfile().size(), retrievedBranchReports
                .getLoanArrearsProfile().size());
       Assert.assertEquals(generatedBranchReport.getLoanDetails().size(), retrievedBranchReports.getLoanDetails().size());
       Assert.assertEquals(generatedBranchReport.getStaffingLevelSummaries().size(), retrievedBranchReports
                .getStaffingLevelSummaries().size());
       Assert.assertEquals(generatedBranchReport.getStaffSummaries().size(), retrievedBranchReports.getStaffSummaries()
                .size());
        result = true;
        return result;
    }

    private void assertBranchReport(BranchReportBO generatedBranchReport, BranchReportBO retrievedBranchReports)
            throws ServiceException {
        Assert.assertNotNull(retrievedBranchReports);
        // now that generatedBranchReport is flushed the generatedBranchReport
        // and retrievedBranchReports
        // are not equal any more, we have to compare on the contents. It is
        // replaced with compareBranchReport
        //Assert.assertEquals(generatedBranchReport, retrievedBranchReports);
       Assert.assertTrue(compareBranchReports(generatedBranchReport, retrievedBranchReports));
    }

    private void assertStaffSummary(Set<BranchReportStaffSummaryBO> generatedStaffSummaries,
            IBranchReportService branchReportService) throws ServiceException {
        List<BranchReportStaffSummaryBO> retrievedStaffSummaries = branchReportService.getStaffSummary(BRANCH_ID,
                RUN_DATE_STR);
        // TODO TW insert test data for loan officer staff summary report
        // assertNotEmpty(generatedStaffSummaries);
        assertSameCollections(generatedStaffSummaries, retrievedStaffSummaries);
    }

    private boolean find(BranchReportLoanArrearsAgingBO loanArrearsAging,
            List<BranchReportLoanArrearsAgingBO> retrievedLoanArrearsAgingReports) {
        boolean result = false;
        for (BranchReportLoanArrearsAgingBO bo : retrievedLoanArrearsAgingReports) {
            if (bo.getArrearsAgingId().equals(loanArrearsAging.getArrearsAgingId())) {
               Assert.assertEquals(bo.getPeriodDescription(), loanArrearsAging.getPeriodDescription());
               Assert.assertEquals(bo.getAgingPeriod(), loanArrearsAging.getAgingPeriod());
               Assert.assertEquals(bo.getAmountAging(), loanArrearsAging.getAmountAging());
               Assert.assertEquals(bo.getAmountOutstandingAging(), loanArrearsAging.getAmountOutstandingAging());
                result = true;
                return result;
            }
        }
        return result;
    }

    private void assertLoanArrearsAging(Set<BranchReportLoanArrearsAgingBO> generatedLoanArrearsAgingReport,
            IBranchReportService branchReportService) throws ServiceException {
        List<BranchReportLoanArrearsAgingBO> retrievedLoanArrearsAgingReports = branchReportService
                .getLoanArrearsAgingInfo(BRANCH_ID, RUN_DATE_STR);
        assertNotEmpty(generatedLoanArrearsAgingReport);
        Iterator iterator = generatedLoanArrearsAgingReport.iterator();
        while (iterator.hasNext()) {
            BranchReportLoanArrearsAgingBO loanArrearsAging = (BranchReportLoanArrearsAgingBO) iterator.next();
           Assert.assertTrue(find(loanArrearsAging, retrievedLoanArrearsAgingReports));
        }

    }

    private void assertClientSummaryReport(Set<BranchReportClientSummaryBO> generatedClientSummaryReport,
            IBranchReportService branchReportService) throws ServiceException {
        List<BranchReportClientSummaryBO> retrievedBranchReportClientSummaries = branchReportService
                .getClientSummaryInfo(BRANCH_ID, RUN_DATE_STR);
        assertNotEmpty(generatedClientSummaryReport);
        assertSameCollections(generatedClientSummaryReport, retrievedBranchReportClientSummaries);
    }

    @Override
    protected void tearDown() throws Exception {
        transaction.rollback();
        super.tearDown();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        session = StaticHibernateUtil.getSessionTL();
        transaction = session.beginTransaction();
    }
}
