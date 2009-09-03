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

package org.mifos.application.reports.business.service;

import static org.apache.commons.collections.CollectionUtils.exists;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.mifos.application.branchreport.BranchReportBOFixture.createBranchReportClientSummaryBO;
import static org.mifos.application.branchreport.BranchReportBOFixture.createBranchReportWithStaffSummary;
import static org.mifos.application.branchreport.BranchReportClientSummaryBO.ACTIVE_BORROWERS_COUNT;
import static org.mifos.application.branchreport.BranchReportClientSummaryBO.ACTIVE_CLIENTS_COUNT;
import static org.mifos.application.branchreport.BranchReportClientSummaryBO.CENTER_COUNT;
import static org.mifos.framework.util.AssertionUtils.assertSameCollections;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.collections.PredicateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.branchreport.BranchReportBO;
import org.mifos.application.branchreport.BranchReportBOFixture;
import org.mifos.application.branchreport.BranchReportClientSummaryBO;
import org.mifos.application.branchreport.BranchReportLoanArrearsAgingBO;
import org.mifos.application.branchreport.BranchReportStaffSummaryBO;
import org.mifos.application.branchreport.BranchReportStaffingLevelSummaryBO;
import org.mifos.application.branchreport.LoanArrearsAgingPeriod;
import org.mifos.application.branchreport.persistence.BranchReportPersistence;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficecFixture;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.reports.business.dto.BranchReportHeaderDTO;
import org.mifos.application.reports.util.helpers.ReportUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;

public class BranchReportServiceIntegrationTest extends BranchReportIntegrationTestCase {

    public BranchReportServiceIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private BranchReportClientSummaryBO activeClientsCountSummary;;
    private BranchReportClientSummaryBO centerCountClientSummary;
    private BranchReportClientSummaryBO activeBorrowersCountSummary;

    private Session session;
    private Transaction transaction;
    private OfficeBusinessService officeBusinessServiceMock;
    private IBranchReportService branchReportService;
    private BranchReportBO branchReport;
    private BranchReportLoanArrearsAgingBO loanArrearReportForFirstWeek;
    private BranchReportLoanArrearsAgingBO loanArrearReportForSecondWeek;
    private BranchReportLoanArrearsAgingBO loanArrearReportForThirdWeek;

    public void testReturnsClientSummaryForGivenBranchAndRunDate() throws Exception {
        session.save(branchReport);
        List<BranchReportClientSummaryBO> retrievedClientSummaries = branchReportService.getClientSummaryInfo(
                BRANCH_ID, RUN_DATE_STR);
        Assert.assertNotNull(retrievedClientSummaries);
       Assert.assertEquals(3, retrievedClientSummaries.size());

       Assert.assertTrue(exists(retrievedClientSummaries, PredicateUtils.equalPredicate(centerCountClientSummary)));

       Assert.assertTrue(exists(retrievedClientSummaries, PredicateUtils.equalPredicate(activeClientsCountSummary)));

       Assert.assertTrue(exists(retrievedClientSummaries, PredicateUtils.equalPredicate(activeBorrowersCountSummary)));
    }

    public void testReturnsLoanArrearsAgingInfo() throws Exception {
        session.save(branchReport);
        List<BranchReportLoanArrearsAgingBO> retrievedLoanArrearsAgingInfo = branchReportService
                .getLoanArrearsAgingInfo(BRANCH_ID, RUN_DATE_STR);
       Assert.assertEquals(3, retrievedLoanArrearsAgingInfo.size());
       Assert.assertTrue(exists(retrievedLoanArrearsAgingInfo, PredicateUtils.equalPredicate(loanArrearReportForFirstWeek)));
       Assert.assertTrue(exists(retrievedLoanArrearsAgingInfo, PredicateUtils.equalPredicate(loanArrearReportForSecondWeek)));
       Assert.assertTrue(exists(retrievedLoanArrearsAgingInfo, PredicateUtils.equalPredicate(loanArrearReportForThirdWeek)));
    }

    public void testServiceReturnsHeaderInformation() throws Exception {
        session.save(branchReport);
        OfficeBO office = OfficecFixture.createOffice(BRANCH_ID_SHORT);
        expect(officeBusinessServiceMock.getOffice(BRANCH_ID_SHORT)).andReturn(office);
        replay(officeBusinessServiceMock);
        BranchReportHeaderDTO returnedHeaderDTO = branchReportService.getBranchReportHeaderDTO(BRANCH_ID, RUN_DATE_STR);
        verify(officeBusinessServiceMock);
       Assert.assertEquals(new BranchReportHeaderDTO(office, null, ReportUtils.parseReportDate(RUN_DATE_STR)),
                returnedHeaderDTO);
    }

    public void testRemovesSpecifiedBranchReport() throws Exception {
        session.save(branchReport);
        branchReportService.removeBranchReport(branchReport);

        List<BranchReportClientSummaryBO> clientSummaryInfo = branchReportService.getClientSummaryInfo(BRANCH_ID,
                RUN_DATE_STR);
        Assert.assertNotNull(clientSummaryInfo);
       Assert.assertEquals(0, clientSummaryInfo.size());

        List<BranchReportLoanArrearsAgingBO> loanArrearsAgingInfo = branchReportService.getLoanArrearsAgingInfo(
                BRANCH_ID, RUN_DATE_STR);
        Assert.assertNotNull(loanArrearsAgingInfo);
       Assert.assertEquals(0, loanArrearsAgingInfo.size());
    }

    public void testServiceReturnsFalseIfBranchReportDataNotPresent() throws Exception {
        Assert.assertFalse(branchReportService.isReportDataPresentForRundateAndBranchId("2", "01/01/2008"));
    }

    public void testServiceReturnsTrueIfBranchReportDataPresent() throws Exception {
        session.save(branchReport);
       Assert.assertTrue(branchReportService.isReportDataPresentForRundateAndBranchId(BRANCH_ID.toString(), RUN_DATE_STR));
    }

    public void testServiceReturnsFalseIfBranchReportDataNotPresentForGivenDate() throws Exception {
        Assert.assertFalse(branchReportService.isReportDataPresentForRundate(DateUtils.getDate(2008, Calendar.JANUARY, 1)));
    }

    public void testServiceReturnsTrueIfBranchReportDataPresentForGivenDate() throws Exception {
        session.save(branchReport);
       Assert.assertTrue(branchReportService.isReportDataPresentForRundate(RUN_DATE));
    }

    public void testGetStaffSummaryReportReturnsStaffSummaryForBranchAndDate() throws Exception {
        BranchReportBO branchReportWithStaffSummary = createBranchReportWithStaffSummary(BRANCH_ID_SHORT, RUN_DATE);
        BranchReportBO otherBranchReportWithStaffSummary = createBranchReportWithStaffSummary(BRANCH_ID_SHORT,
                FIRST_JAN_2008);
        session.save(branchReportWithStaffSummary);
        session.save(otherBranchReportWithStaffSummary);
        List<BranchReportStaffSummaryBO> retrievedStaffSummary = branchReportService.getStaffSummary(BRANCH_ID,
                RUN_DATE_STR);
       Assert.assertEquals(1, retrievedStaffSummary.size());
        assertSameCollections(branchReportWithStaffSummary.getStaffSummaries(), retrievedStaffSummary);
    }

    // TODO TW Add test data and have better test
    public void testExtractLoanArrearsAgingInPeriod() throws Exception {
        BranchReportLoanArrearsAgingBO loanArrearsAgingInfoInPeriod = branchReportService
                .extractLoanArrearsAgingInfoInPeriod(BRANCH_ID_SHORT, LoanArrearsAgingPeriod.ONE_WEEK, DEFAULT_CURRENCY);
        Assert.assertNotNull(loanArrearsAgingInfoInPeriod);
        // TODO TW more assertions based on test data
    }

    public void testResultsForStaffingLevelAreSorted() throws PersistenceException, ServiceException {
        BranchReportPersistence branchReportPersistenceMock = createMock(BranchReportPersistence.class);
        branchReportService = new BranchReportService(officeBusinessServiceMock, new PersonnelBusinessService(),
                branchReportPersistenceMock);
        ArrayList<BranchReportStaffingLevelSummaryBO> staffingLevelResult = new ArrayList<BranchReportStaffingLevelSummaryBO>();
        BranchReportStaffingLevelSummaryBO totalStaffSummaryBO = BranchReportBOFixture.createStaffingLevelBO(Integer
                .valueOf(-1));
        staffingLevelResult.add(totalStaffSummaryBO);
        staffingLevelResult.add(BranchReportBOFixture.createStaffingLevelBO(Integer.valueOf(2)));
        staffingLevelResult.add(BranchReportBOFixture.createStaffingLevelBO(Integer.valueOf(1)));
        expect(branchReportPersistenceMock.getBranchReportStaffingLevelSummary(BRANCH_ID_SHORT, RUN_DATE)).andReturn(
                staffingLevelResult);
        replay(branchReportPersistenceMock);
        List<BranchReportStaffingLevelSummaryBO> retrievedStaffingLevel = branchReportService.getStaffingLevelSummary(
                BRANCH_ID, RUN_DATE_STR);
        verify(branchReportPersistenceMock);
        BranchReportStaffingLevelSummaryBO lastBO = null;
        for (BranchReportStaffingLevelSummaryBO summaryBO : retrievedStaffingLevel) {
            if (lastBO != null)
               Assert.assertEquals(1, summaryBO.compareTo(lastBO));
            lastBO = summaryBO;
        }
       Assert.assertEquals(0, totalStaffSummaryBO.compareTo(lastBO));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        branchReport = new BranchReportBO(BRANCH_ID_SHORT, RUN_DATE);
        populateClientSummary();
        populateLoanArrearSummary();

        session = StaticHibernateUtil.getSessionTL();
        transaction = session.beginTransaction();
        officeBusinessServiceMock = createMock(OfficeBusinessService.class);
        branchReportService = new BranchReportService(officeBusinessServiceMock, new PersonnelBusinessService(),
                new BranchReportPersistence());
    }

    private void populateClientSummary() {
        activeClientsCountSummary = createBranchReportClientSummaryBO(ACTIVE_CLIENTS_COUNT);
        centerCountClientSummary = createBranchReportClientSummaryBO(CENTER_COUNT);
        activeBorrowersCountSummary = createBranchReportClientSummaryBO(ACTIVE_BORROWERS_COUNT);
        branchReport.addClientSummary(centerCountClientSummary);
        branchReport.addClientSummary(activeClientsCountSummary);
        branchReport.addClientSummary(activeBorrowersCountSummary);
    }

    private void populateLoanArrearSummary() {
        loanArrearReportForFirstWeek = new BranchReportLoanArrearsAgingBO(LoanArrearsAgingPeriod.ONE_WEEK);
        loanArrearReportForSecondWeek = new BranchReportLoanArrearsAgingBO(LoanArrearsAgingPeriod.TWO_WEEK);
        loanArrearReportForThirdWeek = new BranchReportLoanArrearsAgingBO(LoanArrearsAgingPeriod.THREE_WEEK);
        branchReport.addLoanArrearsAging(loanArrearReportForFirstWeek);
        branchReport.addLoanArrearsAging(loanArrearReportForSecondWeek);
        branchReport.addLoanArrearsAging(loanArrearReportForThirdWeek);
    }

    @Override
    protected void tearDown() throws Exception {
        transaction.rollback();
        super.tearDown();
    }
}
