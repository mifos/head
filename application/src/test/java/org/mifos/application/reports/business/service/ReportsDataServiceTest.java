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

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.framework.exceptions.ServiceException;

public class ReportsDataServiceTest extends TestCase {
    private LoanPrdBusinessService loanPrdBusinessServiceMock;

    private ReportsDataService reportsDataService;

    private Short localeId;

    private Short userId;

    private PersonnelBusinessService personnelBusinessServiceMock;

    private OfficeBusinessService officeBusinessServiceMock;

    private LoanPersistence loanPersistenceMock;

    private PersonnelBO personnelMock;

    private ServiceException expectedException;

    private Short branchId;

    private Short loanOfficerId;

    private Short loanProductId;

    @Override
    protected void setUp() throws Exception {

        loanPrdBusinessServiceMock = createMock(LoanPrdBusinessService.class);
        personnelBusinessServiceMock = createMock(PersonnelBusinessService.class);
        officeBusinessServiceMock = createMock(OfficeBusinessService.class);
        personnelMock = createMock(PersonnelBO.class);
        loanPersistenceMock = createMock(LoanPersistence.class);
        expectedException = new ServiceException("someServiceException");

        userId = 1;
        branchId = 2;
        localeId = 3;
        loanOfficerId = 3;
        loanProductId = 4;

        reportsDataService = new ReportsDataService();

        reportsDataService.setLoanPrdBusinessService(loanPrdBusinessServiceMock);
        reportsDataService.setPersonnelBusinessService(personnelBusinessServiceMock);
        reportsDataService.setOfficeBusinessService(officeBusinessServiceMock);
        reportsDataService.setPersonnel(personnelMock);
        reportsDataService.setLoanPersistence(loanPersistenceMock);
    }

    public void testInitialize() throws Exception {
        reportsDataService.setPersonnel(null);

        expect(personnelBusinessServiceMock.getPersonnel(userId.shortValue())).andReturn(personnelMock);
        replay(personnelBusinessServiceMock);
        reportsDataService.initialize(userId.intValue());
        assertSame(personnelMock, reportsDataService.getPersonnel());
        verify(personnelBusinessServiceMock);
    }

    public void testInitializeShouldComplainIfPersonnelBusinessServiceComplains() throws Exception {
        reportsDataService.setPersonnel(null);

        expect(personnelBusinessServiceMock.getPersonnel(userId.shortValue())).andThrow(expectedException);
        replay(personnelBusinessServiceMock);
        try {
            reportsDataService.initialize(userId.intValue());
            Assert.fail("exception expected");
        } catch (ServiceException e) {
            assertSame(expectedException, e);
        }
        Assert.assertNull(reportsDataService.getPersonnel());
        verify(personnelBusinessServiceMock);
    }

    public void testGetAllLoanProductsShouldDelegateToLoanPrdBusinessService() throws Exception {
        List<LoanOfferingBO> expectedLoanProducts = new ArrayList<LoanOfferingBO>();

        expect(personnelMock.getLocaleId()).andReturn(localeId);
        expect(loanPrdBusinessServiceMock.getAllLoanOfferings(localeId)).andReturn(expectedLoanProducts);
        replay(personnelBusinessServiceMock, personnelMock, loanPrdBusinessServiceMock);
        assertSame(expectedLoanProducts, reportsDataService.getAllLoanProducts());
        verify(personnelBusinessServiceMock, personnelMock, loanPrdBusinessServiceMock);
    }

    public void testGetAllLoanProductsShouldComplainIfLoanPrdBusinessServiceComplains() throws Exception {
        expect(personnelMock.getLocaleId()).andReturn(localeId);
        expect(loanPrdBusinessServiceMock.getAllLoanOfferings(localeId)).andThrow(expectedException);
        replay(personnelBusinessServiceMock, personnelMock, loanPrdBusinessServiceMock);
        try {
            reportsDataService.getAllLoanProducts();
            Assert.fail("exception expected");
        } catch (ServiceException e) {
            assertSame(expectedException, e);
        }
        verify(personnelBusinessServiceMock, personnelMock, loanPrdBusinessServiceMock);
    }

    public void testGetAllBranchShouldReturnActiveBranchesUnderUser() throws Exception {
        List<OfficeBO> expectedBranches = new ArrayList<OfficeBO>();

        expect(officeBusinessServiceMock.getActiveBranchesUnderUser(personnelMock)).andReturn(expectedBranches);
        replay(personnelBusinessServiceMock, personnelMock, officeBusinessServiceMock);
        assertSame(expectedBranches, reportsDataService.getActiveBranchesUnderUser());
        verify(personnelBusinessServiceMock, personnelMock, officeBusinessServiceMock);
    }

    public void testGetAllBranchShouldComplainIfOfficecBusinessServiceComplains() throws Exception {
        expect(officeBusinessServiceMock.getActiveBranchesUnderUser(personnelMock)).andThrow(expectedException);
        replay(personnelBusinessServiceMock, personnelMock, officeBusinessServiceMock);
        try {
            reportsDataService.getActiveBranchesUnderUser();
            Assert.fail("exception expected");
        } catch (ServiceException e) {
            assertSame(expectedException, e);
        }
        verify(personnelBusinessServiceMock, personnelMock, officeBusinessServiceMock);
    }

    public void testGetActiveLoanOfficersShouldReturnHimselfIfUserIsALoanOfficer() throws Exception {
        List<PersonnelBO> expectedLoanOfficer = new ArrayList<PersonnelBO>();
        expectedLoanOfficer.add(personnelMock);

        expect(personnelMock.isLoanOfficer()).andReturn(true);
        replay(personnelBusinessServiceMock, personnelMock);
       Assert.assertEquals(expectedLoanOfficer, reportsDataService.getActiveLoanOfficers(branchId.intValue()));
        verify(personnelBusinessServiceMock, personnelMock);
    }

    public void testGetActiveLoanOfficersShouldReturnAListOfActiveLoanOfficersUnderTheBranchIfUserIsNotALoanOfficer()
            throws Exception {
        List<PersonnelBO> expectedLoanOfficer = new ArrayList<PersonnelBO>();

        expect(personnelMock.isLoanOfficer()).andReturn(false);
        expect(personnelBusinessServiceMock.getActiveLoanOfficersUnderOffice(branchId)).andReturn(expectedLoanOfficer);
        replay(personnelBusinessServiceMock, personnelMock);
       Assert.assertEquals(expectedLoanOfficer, reportsDataService.getActiveLoanOfficers(branchId.intValue()));
        verify(personnelBusinessServiceMock, personnelMock);
    }

    public void testGetLoanAccountsInActiveBadStandingShouldReturnListOfLoanAccountsInActiveBadStanding()
            throws Exception {
        List<LoanBO> exceptedLoanList = new ArrayList<LoanBO>();

        expect(loanPersistenceMock.getLoanAccountsInActiveBadStanding(branchId, loanOfficerId, loanProductId))
                .andReturn(exceptedLoanList);
        replay(loanPersistenceMock);
        assertSame(exceptedLoanList, reportsDataService.getLoanAccountsInActiveBadStanding(branchId.intValue(),
                loanOfficerId.intValue(), loanProductId.intValue()));
        verify(loanPersistenceMock);
    }

    public void testGetTotalOutstandingPrincipalOfLoanAccountsInActiveGoodStandingShouldReturnSumOfOutstandingPrincipalBalanceForloansInActiveBadStanding()
            throws Exception {
        BigDecimal exceptedSum = new BigDecimal(0);

        expect(
                loanPersistenceMock.getTotalOutstandingPrincipalOfLoanAccountsInActiveGoodStanding(branchId,
                        loanOfficerId, loanProductId)).andReturn(exceptedSum);
        replay(loanPersistenceMock);
        assertSame(exceptedSum, reportsDataService.getTotalOutstandingPrincipalOfLoanAccountsInActiveGoodStanding(
                branchId.intValue(), loanOfficerId.intValue(), loanProductId.intValue()));
        verify(loanPersistenceMock);
    }

    public void testGetActiveLoansBothInGoodAndBadStandingByLoanOfficerShouldReturnListOfAllActiveLoans()
            throws Exception {
        List<LoanBO> exceptedLoans = new ArrayList<LoanBO>();

        expect(
                loanPersistenceMock.getActiveLoansBothInGoodAndBadStandingByLoanOfficer(branchId, loanOfficerId,
                        loanProductId)).andReturn(exceptedLoans);
        replay(loanPersistenceMock);
        assertSame(exceptedLoans, reportsDataService.getActiveLoansBothInGoodAndBadStandingByLoanOfficer(branchId
                .intValue(), loanOfficerId.intValue(), loanProductId.intValue()));
        verify(loanPersistenceMock);
    }
}
