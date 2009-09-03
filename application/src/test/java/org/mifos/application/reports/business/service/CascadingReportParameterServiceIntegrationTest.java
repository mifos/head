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
import static org.mifos.application.reports.ui.SelectionItem.ALL_LOAN_OFFICER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.NA_BRANCH_OFFICE_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.NA_CENTER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.NA_LOAN_OFFICER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_BRANCH_OFFICE_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_LOAN_OFFICER_SELECTION_ITEM;
import static org.mifos.framework.util.CollectionUtils.asList;
import static org.mifos.framework.util.helpers.NumberUtils.convertShortToInteger;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelFixture;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.reports.ui.DateSelectionItem;
import org.mifos.application.reports.ui.SelectionItem;
import org.mifos.application.reports.ui.SelectionItemFixture;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.DateUtils;

public class CascadingReportParameterServiceIntegrationTest extends AbstractCollectionSheetIntegrationTestCase {
    public CascadingReportParameterServiceIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private PersonnelBusinessService personnelBusinessServiceMock;
    private IReportsParameterService reportsParameterServiceMock;
    private CustomerBusinessService customerBusinessServiceMock;
    private CascadingReportParameterService cascadingReportParameterService;

    private List<SelectionItem> retrieveBranchOffices(List<SelectionItem> branchOffices, PersonnelBO personnel)
            throws ServiceException, PersistenceException {
        expect(personnelBusinessServiceMock.getPersonnel(PERSONNEL_ANY_SHORT_ID)).andReturn(personnel);
        replay(reportsParameterServiceMock);
        replay(personnelBusinessServiceMock);
        List<SelectionItem> returnedBranchOffices = cascadingReportParameterService.getBranchOffices(PERSONNEL_ANY_ID);
        verify(reportsParameterServiceMock);
        verify(personnelBusinessServiceMock);
        return returnedBranchOffices;
    }

    public void testGetBranchesCallsOfficeBusinessServiceAndChecksSelect() throws Exception {
        PersonnelBO personnel = PersonnelFixture.createPersonnel(PERSONNEL_ANY_SHORT_ID);
        expect(reportsParameterServiceMock.getActiveBranchesUnderUser(personnel.getOfficeSearchId())).andReturn(
                branchOfficesSelectionItems);
        List<SelectionItem> returnedBranchOffices = retrieveBranchOffices(branchOfficesSelectionItems, personnel);
       Assert.assertEquals(MAX_COUNT + 1, returnedBranchOffices.size());
       Assert.assertEquals(SELECT_BRANCH_OFFICE_SELECTION_ITEM, returnedBranchOffices.get(0));
    }

    public void testGetNotApplicableBranchOfficeIfNoneExists() throws Exception {
        List<SelectionItem> returnedBranchOffices = retrieveBranchOffices(new ArrayList<SelectionItem>(), null);
       Assert.assertEquals(1, returnedBranchOffices.size());
       Assert.assertEquals(NA_BRANCH_OFFICE_SELECTION_ITEM, returnedBranchOffices.get(0));
    }

    public void testGetActiveLoanOfficersWithOptionForAllLoanOfficer() throws Exception {
        expect(personnelBusinessServiceMock.getPersonnel(PERSONNEL_ANY_SHORT_ID)).andReturn(ANY_PERSONNEL);
        expect(reportsParameterServiceMock.getActiveLoanOfficersUnderOffice(ANY_ID)).andReturn(
                loanOfficersSelectionItems);
        replay(personnelBusinessServiceMock);
        replay(reportsParameterServiceMock);
        List<SelectionItem> activeLoanOfficers = cascadingReportParameterService.getActiveLoanOfficers(
                PERSONNEL_ANY_ID, ANY_ID);
        verify(personnelBusinessServiceMock);
        verify(reportsParameterServiceMock);
       Assert.assertEquals(MAX_COUNT + 1, activeLoanOfficers.size());
       Assert.assertEquals(ALL_LOAN_OFFICER_SELECTION_ITEM, activeLoanOfficers.get(0));
    }

    public void testWhenBranchOfficeIdIsNull() throws Exception {
        expect(personnelBusinessServiceMock.getPersonnel(PERSONNEL_ANY_SHORT_ID)).andReturn(null);
        replay(personnelBusinessServiceMock);
        List<SelectionItem> activeLoanOfficers = cascadingReportParameterService.getActiveLoanOfficers(
                PERSONNEL_ANY_ID, null);
        verify(personnelBusinessServiceMock);
       Assert.assertEquals(1, activeLoanOfficers.size());
       Assert.assertEquals(SELECT_LOAN_OFFICER_SELECTION_ITEM, activeLoanOfficers.get(0));
    }

    public void testRetrieveLoanOfficersReturnNAWhenUserIdDoesNotExist() throws Exception {
        expect(personnelBusinessServiceMock.getPersonnel(ANY_SHORT_ID)).andReturn(null);
        replay(personnelBusinessServiceMock);
        List<SelectionItem> loanOfficers = cascadingReportParameterService.getActiveLoanOfficers(ANY_ID, ANY_ID);
        verify(personnelBusinessServiceMock);
       Assert.assertEquals(1, loanOfficers.size());
        assertSame(NA_LOAN_OFFICER_SELECTION_ITEM, loanOfficers.get(0));
    }

    public void testOnlySelfLoanOfficerPopulatedIfUserIsLoanOfficer() throws Exception {
        expect(personnelBusinessServiceMock.getPersonnel(PERSONNEL_ANY_SHORT_ID)).andReturn(LOAN_OFFICER);
        replay(personnelBusinessServiceMock);
        List<SelectionItem> activeLoanOfficers = cascadingReportParameterService.getActiveLoanOfficers(
                PERSONNEL_ANY_ID, BRANCH_ID);
       Assert.assertEquals(2, activeLoanOfficers.size());
        assertSame(SELECT_LOAN_OFFICER_SELECTION_ITEM, activeLoanOfficers.get(0));
       Assert.assertEquals(new SelectionItem(convertShortToInteger(LOAN_OFFICER.getPersonnelId()), LOAN_OFFICER
                .getDisplayName()), activeLoanOfficers.get(1));
        verify(personnelBusinessServiceMock);
    }

    public void testLoanOfficerNAWhenNoneExistsForABranch() throws Exception {
        setExpectationForAnyUserWhenRetrievingLoanOfficer();
        expect(reportsParameterServiceMock.getActiveLoanOfficersUnderOffice(ANY_ID)).andReturn(
                new ArrayList<SelectionItem>());
        replay(personnelBusinessServiceMock);
        replay(reportsParameterServiceMock);
        retrieveAndAssertLoanOfficer(convertShortToInteger(anyOffice.getOfficeId()), NA_LOAN_OFFICER_SELECTION_ITEM);
        verify(personnelBusinessServiceMock);
        verify(reportsParameterServiceMock);
    }

    private void retrieveAndAssertLoanOfficer(Integer officeId, SelectionItem expectedLoanOfficer)
            throws ServiceException, PersistenceException {
        List<SelectionItem> activeLoanOfficers = cascadingReportParameterService.getActiveLoanOfficers(
                PERSONNEL_ANY_ID, officeId);
        verify(personnelBusinessServiceMock);
       Assert.assertEquals(1, activeLoanOfficers.size());
       Assert.assertEquals(expectedLoanOfficer, activeLoanOfficers.get(0));
    }

    public void testCenterIsSelectLoanOfficerIsSelect() throws Exception {
        retrieveAndAssertCenterBO(SELECT_LOAN_OFFICER_SELECTION_ITEM.getId(),
                SelectionItem.SELECT_CENTER_SELECTION_ITEM);
    }

    public void testCenterForLoanOfficerIsNotApplicable() throws Exception {
        retrieveAndAssertCenterBO(NA_LOAN_OFFICER_SELECTION_ITEM.getId(), SelectionItem.NA_CENTER_SELECTION_ITEM);
    }

    public void testCenterWhenLoanOfficerIdNull() throws Exception {
        retrieveAndAssertCenterBO(null, SelectionItem.SELECT_CENTER_SELECTION_ITEM);
    }

    public void testCenterForExistingLoanOfficer() throws Exception {
        expect(
                reportsParameterServiceMock.getActiveCentersUnderUser(BRANCH_ID, convertShortToInteger(anyPersonnel
                        .getPersonnelId()))).andReturn(centerSelectionItems);
        expect(personnelBusinessServiceMock.getPersonnel(ANY_SHORT_ID)).andReturn(anyPersonnel);
        replay(reportsParameterServiceMock);
        replay(personnelBusinessServiceMock);
        List<SelectionItem> activeCenters = cascadingReportParameterService.getActiveCentersForLoanOfficer(ANY_ID,
                BRANCH_ID);
       Assert.assertEquals(MAX_COUNT + 1, activeCenters.size());
       Assert.assertEquals(SelectionItem.ALL_CENTER_SELECTION_ITEM, activeCenters.get(0));
        verify(reportsParameterServiceMock);
        verify(personnelBusinessServiceMock);
    }

    public void testCenterForAllLoanOfficers() throws Exception {
        ArrayList<SelectionItem> personnels = new ArrayList<SelectionItem>();
        SelectionItem otherPersonnel = SelectionItemFixture.createSelectionItem(new Short("2"));
        personnels.add(otherPersonnel);
        personnels.add(SelectionItemFixture.createSelectionItem(anyPersonnel.getPersonnelId()));
        expect(reportsParameterServiceMock.getActiveLoanOfficersUnderOffice(BRANCH_ID)).andReturn(personnels);
        ArrayList<SelectionItem> anyCustomers = new ArrayList<SelectionItem>();
        SelectionItem anyOneCustomer = SelectionItemFixture.createSelectionItem(1);
        anyCustomers.add(anyOneCustomer);
        anyCustomers.add(SelectionItemFixture.createSelectionItem(2));
        ArrayList<SelectionItem> otherCustomers = new ArrayList<SelectionItem>();
        SelectionItem otherOneCustomer = SelectionItemFixture.createSelectionItem(3);
        otherCustomers.add(otherOneCustomer);
        otherCustomers.add(SelectionItemFixture.createSelectionItem(4));
        expect(
                reportsParameterServiceMock.getActiveCentersUnderUser(BRANCH_ID, convertShortToInteger(anyPersonnel
                        .getPersonnelId()))).andReturn(anyCustomers);
        expect(reportsParameterServiceMock.getActiveCentersUnderUser(BRANCH_ID, otherPersonnel.getId())).andReturn(
                otherCustomers);
        replay(reportsParameterServiceMock);
        replay(personnelBusinessServiceMock);
        List<SelectionItem> activeCenters = cascadingReportParameterService.getActiveCentersForLoanOfficer(
                ALL_LOAN_OFFICER_SELECTION_ITEM.getId(), BRANCH_ID);
       Assert.assertEquals(5, activeCenters.size());
       Assert.assertEquals(SelectionItem.ALL_CENTER_SELECTION_ITEM, activeCenters.get(0));
       Assert.assertTrue(activeCenters.contains(anyOneCustomer));
       Assert.assertTrue(activeCenters.contains(otherOneCustomer));
        verify(reportsParameterServiceMock);
        verify(personnelBusinessServiceMock);
    }

    private void retrieveAndAssertCenterBO(Integer loanOfficerId, SelectionItem expectedCenter)
            throws ServiceException, PersistenceException {
        List<SelectionItem> centersForLoanOfficer = cascadingReportParameterService.getActiveCentersForLoanOfficer(
                loanOfficerId, null);
       Assert.assertEquals(1, centersForLoanOfficer.size());
       Assert.assertEquals(expectedCenter, centersForLoanOfficer.get(0));
    }

    public void testGetMeetingDatesForCenterWhenSpecifiedBranchOfficerAndCenter() throws Exception {
        expect(reportsParameterServiceMock.getMeetingDates(BRANCH_ID, LOAN_OFFICER_ID, CENTER_ID, DateUtils.sqlToday()))
                .andReturn(new ArrayList<DateSelectionItem>());
        replay(reportsParameterServiceMock);
        cascadingReportParameterService.getMeetingDatesForCollectionSheet(BRANCH_ID, LOAN_OFFICER_ID, CENTER_ID);
        verify(reportsParameterServiceMock);
    }

    public void testGetMeetingDatesForCeneterWhenSpecifiedBranchAllOfficerAndCenterParameters() throws Exception {
        expect(
                reportsParameterServiceMock.getMeetingDates(BRANCH_ID, ALL_LOAN_OFFICER_ID, CENTER_ID, DateUtils
                        .sqlToday())).andReturn(new ArrayList<DateSelectionItem>());
        expect(customerBusinessServiceMock.getCustomer(CENTER_ID)).andReturn(center);
        replay(customerBusinessServiceMock);
        replay(reportsParameterServiceMock);
        cascadingReportParameterService.getMeetingDatesForCollectionSheet(BRANCH_ID, ALL_LOAN_OFFICER_ID, CENTER_ID);
        verify(customerBusinessServiceMock);
        verify(reportsParameterServiceMock);
    }

    public void testGetMeetingDatesForCenterWhenNACenter() throws Exception {
        List<org.mifos.application.reports.ui.DateSelectionItem> meetingDates = cascadingReportParameterService
                .getMeetingDatesForCollectionSheet(BRANCH_ID, NA_LOAN_OFFICER_SELECTION_ITEM.getId(),
                        NA_CENTER_SELECTION_ITEM.getId());
       Assert.assertEquals(1, meetingDates.size());
    }

    public void testForALoanOfficerUserOnlyBranchOfficeRelatedPopulated() throws Exception {
        expect(personnelBusinessServiceMock.getPersonnel(LOAN_OFFICER_SHORT_ID)).andReturn(LOAN_OFFICER);
        expect(reportsParameterServiceMock.getActiveBranchesUnderUser(LOAN_OFFICER.getOfficeSearchId())).andReturn(
                asList(OFFICE_SELECTION_ITEM));
        replay(personnelBusinessServiceMock);
        replay(reportsParameterServiceMock);
        List<SelectionItem> offices = cascadingReportParameterService.getBranchOffices(LOAN_OFFICER_ID);
        verify(personnelBusinessServiceMock);
        verify(reportsParameterServiceMock);
       Assert.assertEquals(2, offices.size());
        assertSame(SELECT_BRANCH_OFFICE_SELECTION_ITEM, offices.get(0));
        assertSame(OFFICE_SELECTION_ITEM, offices.get(1));
    }

    public void testGetNotApplicableLoanOfficerIfBranchNotApplicable() throws Exception {
        expect(personnelBusinessServiceMock.getPersonnel(PERSONNEL_ANY_SHORT_ID)).andReturn(null);
        replay(personnelBusinessServiceMock);
        retrieveAndAssertLoanOfficer(NA_BRANCH_OFFICE_SELECTION_ITEM.getId(), NA_LOAN_OFFICER_SELECTION_ITEM);
    }

    private void setExpectationForAnyUserWhenRetrievingLoanOfficer() throws ServiceException {
        expect(personnelBusinessServiceMock.getPersonnel(PERSONNEL_ANY_SHORT_ID)).andReturn(ANY_PERSONNEL);
    }

    public void testGetSelectLoanOfficerIfBranchIdIsSelect() throws Exception {
        expect(personnelBusinessServiceMock.getPersonnel(PERSONNEL_ANY_SHORT_ID)).andReturn(null);
        replay(personnelBusinessServiceMock);
        retrieveAndAssertLoanOfficer(SELECT_BRANCH_OFFICE_SELECTION_ITEM.getId(), SELECT_LOAN_OFFICER_SELECTION_ITEM);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        reportsParameterServiceMock = createMock(ReportsParameterService.class);
        personnelBusinessServiceMock = createMock(PersonnelBusinessService.class);
        customerBusinessServiceMock = createMock(CustomerBusinessService.class);
        cascadingReportParameterService = new CascadingReportParameterService(reportsParameterServiceMock,
                personnelBusinessServiceMock, customerBusinessServiceMock);
    }
}
