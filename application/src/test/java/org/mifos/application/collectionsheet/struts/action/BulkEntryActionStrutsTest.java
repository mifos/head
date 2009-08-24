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

package org.mifos.application.collectionsheet.struts.action;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountView;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountsProductView;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsAccountView;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryGridDto;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryView;
import org.mifos.application.collectionsheet.struts.actionforms.BulkEntryActionForm;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetEntryConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.client.business.AttendanceType;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.service.ClientAttendanceDto;
import org.mifos.application.customer.util.helpers.CustomerAccountView;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.master.business.CustomValueListElement;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.util.helpers.OfficeConstants;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestCalcType;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.application.servicefacade.CollectionSheetEntryFormDto;
import org.mifos.application.servicefacade.ListItem;
import org.mifos.application.servicefacade.ProductDto;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.config.AccountingRules;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class BulkEntryActionStrutsTest extends MifosMockStrutsTestCase {

    public BulkEntryActionStrutsTest() throws SystemException, ApplicationException {
        super();
    }

    /*
     * Setting this to true fixes the printing of stack traces to standard out,
     * but seems to cause failures (MySQL threw a
     * "Deadlock found when trying to get lock; try restarting transaction"
     * exception) only if BulkEntryBusinessServiceIntegrationTest is run
     * previously as part of the same suite.
     * 
     * This is presumably a second problem which was always there but was masked
     * by the first one.
     */
    private static final boolean SUPPLY_ENTERED_AMOUNT_PARAMETERS = false;
    private UserContext userContext;
    private CustomerBO center;
    private CustomerBO group;
    private ClientBO client;
    private AccountBO account;
    private LoanBO groupAccount;
    private LoanBO clientAccount;
    private SavingsBO centerSavingsAccount;
    private SavingsBO groupSavingsAccount;
    private SavingsBO clientSavingsAccount;
    private String flowKey;

    @Override
    public void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUp(centerSavingsAccount);
            TestObjectFactory.cleanUp(groupSavingsAccount);
            TestObjectFactory.cleanUp(clientSavingsAccount);
            TestObjectFactory.cleanUp(groupAccount);
            TestObjectFactory.cleanUp(clientAccount);
            TestObjectFactory.cleanUp(account);
            TestObjectFactory.cleanUp(client);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);

        } catch (Exception e) {
            // Throwing here may mask earlier failures.
            e.printStackTrace();
        }
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestDatabase.resetMySQLDatabase();
        userContext = TestUtils.makeUser();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        ActivityContext ac = new ActivityContext((short) 0, userContext.getBranchId().shortValue(), userContext.getId()
                .shortValue());
        request.getSession(false).setAttribute("ActivityContext", ac);
        flowKey = createFlow(request, CollectionSheetEntryAction.class);
    }

    public void testSuccessfulCreate() throws Exception {
        TestDatabase.resetMySQLDatabase();
        CollectionSheetEntryGridDto bulkEntry = getSuccessfulBulkEntry();
        Calendar meetingDateCalendar = new GregorianCalendar();
        int year = meetingDateCalendar.get(Calendar.YEAR);
        int month = meetingDateCalendar.get(Calendar.MONTH);
        int day = meetingDateCalendar.get(Calendar.DAY_OF_MONTH);
        meetingDateCalendar = new GregorianCalendar(year, month, day);

        Date meetingDate = new Date(meetingDateCalendar.getTimeInMillis());
        HashMap<Integer, ClientAttendanceDto> clientAttendance = new HashMap<Integer, ClientAttendanceDto>();
        clientAttendance.put(1, getClientAttendanceDto(1, meetingDate));
        clientAttendance.put(2, getClientAttendanceDto(2, meetingDate));
        clientAttendance.put(3, getClientAttendanceDto(3, meetingDate));

        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setMapAttribute(CollectionSheetEntryConstants.CLIENT_ATTENDANCE, clientAttendance, request);
        addRequestParameter("attendanceSelected[0]", "2");

        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(CollectionSheetEntryConstants.BULKENTRY, bulkEntry, request);
        setRequestPathInfo("/collectionsheetaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestDateParameter("transactionDate", day + "/" + (month + 1) + "/" + year);

        if (SUPPLY_ENTERED_AMOUNT_PARAMETERS) {
            addParametersForEnteredAmount();
            addParametersForDisbursalEnteredAmount();
        }

        performNoErrors();

        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/collectionsheetaction.do");
        addRequestParameter("method", "create");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("attendanceSelected[0]", "2");
        addRequestDateParameter("transactionDate", day + "/" + (month + 1) + "/" + year);
        addRequestParameter("customerId", "1");

        performNoErrors();
        verifyForward("create_success");
        assertNotNull(request.getAttribute(CollectionSheetEntryConstants.CENTER));
        assertEquals(request.getAttribute(CollectionSheetEntryConstants.CENTER), center.getDisplayName());

        groupAccount = TestObjectFactory.getObject(LoanBO.class, groupAccount.getAccountId());
        clientAccount = TestObjectFactory.getObject(LoanBO.class, clientAccount.getAccountId());
        centerSavingsAccount = TestObjectFactory.getObject(SavingsBO.class, centerSavingsAccount.getAccountId());
        clientSavingsAccount = TestObjectFactory.getObject(SavingsBO.class, clientSavingsAccount.getAccountId());
        groupSavingsAccount = TestObjectFactory.getObject(SavingsBO.class, groupSavingsAccount.getAccountId());
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());

        assertEquals(1, client.getClientAttendances().size());
        assertEquals(AttendanceType.ABSENT, client.getClientAttendanceForMeeting(
                new java.sql.Date(meetingDateCalendar.getTimeInMillis())).getAttendanceAsEnum());
    }

    public void testSuccessfulPreview() throws Exception {
        CollectionSheetEntryGridDto bulkEntry = getSuccessfulBulkEntry();
        Calendar meetinDateCalendar = new GregorianCalendar();
        int year = meetinDateCalendar.get(Calendar.YEAR);
        int month = meetinDateCalendar.get(Calendar.MONTH);
        int day = meetinDateCalendar.get(Calendar.DAY_OF_MONTH);
        meetinDateCalendar = new GregorianCalendar(year, month, day);

        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(CollectionSheetEntryConstants.BULKENTRY, bulkEntry, request);
        setRequestPathInfo("/collectionsheetaction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("method", "preview");
        addRequestParameter("attendanceSelected[0]", "1");
        addRequestParameter("enteredAmount[0][0]", "212.0");
        addRequestParameter("enteredAmount[1][1]", "212.0");
        addRequestParameter("enteredAmount[0][1]", "212.0");
        addRequestParameter("enteredAmount[1][0]", "212.0");
        addRequestParameter("withDrawalAmountEntered[2][2]", "100.0");
        addRequestParameter("depositAmountEntered[2][2]", "100.0");
        addRequestParameter("withDrawalAmountEntered[0][0]", "100.0");
        addRequestParameter("depositAmountEntered[0][0]", "100.0");
        addRequestDateParameter("transactionDate", day + "/" + (month + 1) + "/" + year);
        performNoErrors();
        verifyForward("preview_success");

        groupAccount = TestObjectFactory.getObject(LoanBO.class, groupAccount.getAccountId());
        clientAccount = TestObjectFactory.getObject(LoanBO.class, clientAccount.getAccountId());
        centerSavingsAccount = TestObjectFactory.getObject(SavingsBO.class, centerSavingsAccount.getAccountId());
        clientSavingsAccount = TestObjectFactory.getObject(SavingsBO.class, clientSavingsAccount.getAccountId());
        groupSavingsAccount = TestObjectFactory.getObject(SavingsBO.class, groupSavingsAccount.getAccountId());
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());

    }

    public void testFailurePreview() throws Exception {
        CollectionSheetEntryGridDto bulkEntry = getFailureBulkEntry();
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(CollectionSheetEntryConstants.BULKENTRY, bulkEntry, request);
        setRequestPathInfo("/collectionsheetaction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("method", "preview");
        addRequestParameter("customerAccountAmountEntered[0][6]", "");
        addRequestParameter("customerAccountAmountEntered[1][6]", "abc");
        actionPerform();

        verifyActionErrors(new String[] { "errors.invalidamount", "errors.invalidamount" });

    }

    public void testLoad() throws PageExpiredException {
        setRequestPathInfo("/collectionsheetaction.do");
        addRequestParameter("method", "load");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward("load_success");
        assertEquals("The value for isBackDated Trxn Allowed", SessionUtils.getAttribute(
                CollectionSheetEntryConstants.ISBACKDATEDTRXNALLOWED, request), Constants.NO);
        assertEquals("The value for isCenter Hierarchy Exists", SessionUtils.getAttribute(
                CollectionSheetEntryConstants.ISCENTERHIERARCHYEXISTS, request), Constants.YES);
    }

    @SuppressWarnings("unchecked")
    public void testLoadPersonnel() throws PageExpiredException {
        setRequestPathInfo("/collectionsheetaction.do");
        addRequestParameter("method", "loadLoanOfficers");
        addRequestParameter("officeId", "3");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(CollectionSheetEntryConstants.COLLECTION_SHEET_ENTRY_FORM_DTO,
                createDefaultCollectionSheetDto(), request);
        
        actionPerform();
        verifyForward("load_success");
        List<PersonnelView> loanOfficerList = (List<PersonnelView>) SessionUtils.getAttribute(
                CustomerConstants.LOAN_OFFICER_LIST, request);
        assertEquals(1, loanOfficerList.size());
    }

    @SuppressWarnings("unchecked")
    public void testLoadCustomers() throws PageExpiredException {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("Center_Active", meeting);
        
        setRequestPathInfo("/collectionsheetaction.do");
        addRequestParameter("method", "loadCustomerList");
        addRequestParameter("officeId", "3");
        addRequestParameter("loanOfficerId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(CollectionSheetEntryConstants.COLLECTION_SHEET_ENTRY_FORM_DTO,
                createDefaultCollectionSheetDto(), request);
        
        actionPerform();
        verifyForward("load_success");
        List<CustomerView> parentCustomerList = (List<CustomerView>) SessionUtils.getAttribute(
                CollectionSheetEntryConstants.CUSTOMERSLIST, request);
        
        assertEquals(1, parentCustomerList.size());
        assertEquals("The value for isCenter Hierarchy Exists", SessionUtils.getAttribute(
                CollectionSheetEntryConstants.ISCENTERHIERARCHYEXISTS, request), Constants.YES);
    }

    public void testGetLastMeetingDateForCustomer() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("Center_Active", meeting);
        setRequestPathInfo("/collectionsheetaction.do");
        addRequestParameter("method", "getLastMeetingDateForCustomer");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("officeId", "3");
        addRequestParameter("loanOfficerId", "1");
        addRequestParameter("customerId", String.valueOf(center.getCustomerId().intValue()));
        
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(CollectionSheetEntryConstants.COLLECTION_SHEET_ENTRY_FORM_DTO,
                createDefaultCollectionSheetDto(), request);
        
        
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward("load_success");
        
        if (AccountingRules.isBackDatedTxnAllowed()) {
            assertEquals("The value for isBackDated Trxn Allowed", SessionUtils.getAttribute(
                    CollectionSheetEntryConstants.ISBACKDATEDTRXNALLOWED, request), Constants.YES);
            assertEquals(new java.sql.Date(DateUtils.getDateWithoutTimeStamp(getMeetingDates(meeting).getTime())
                    .getTime()).toString(), SessionUtils.getAttribute("LastMeetingDate", request).toString());
            assertEquals(new java.util.Date(DateUtils.getDateWithoutTimeStamp(getMeetingDates(meeting).getTime())
                    .getTime()), DateUtils.getDate(((BulkEntryActionForm) request.getSession().getAttribute(
                    CollectionSheetEntryConstants.BULKENTRYACTIONFORM)).getTransactionDate()));
        } else {
            assertEquals("The value for isBackDated Trxn Allowed", SessionUtils.getAttribute(
                    CollectionSheetEntryConstants.ISBACKDATEDTRXNALLOWED, request), Constants.NO);
            assertEquals(new java.sql.Date(DateUtils.getDateWithoutTimeStamp(getMeetingDates(meeting).getTime())
                    .getTime()).toString(), SessionUtils.getAttribute("LastMeetingDate", request).toString());
            assertEquals(DateUtils.getUserLocaleDate(getUserLocale(request), new java.sql.Date(DateUtils
                    .getCurrentDateWithoutTimeStamp().getTime()).toString()), ((BulkEntryActionForm) request
                    .getSession().getAttribute(CollectionSheetEntryConstants.BULKENTRYACTIONFORM)).getTransactionDate());
        }
    }

    @SuppressWarnings("unchecked")
    public void testSuccessfulGet() throws Exception {
        
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        Date startDate = new Date(System.currentTimeMillis());
        center = TestObjectFactory.createCenter("Center", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
        account = getLoanAccount(group, meeting);
        Date currentDate = new Date(System.currentTimeMillis());
        SavingsOfferingBO savingsOffering1 = TestObjectFactory.createSavingsProduct("SavingPrd1", "ased", currentDate);
        SavingsOfferingBO savingsOffering2 = TestObjectFactory.createSavingsProduct("SavingPrd2", "cvdf", currentDate);
        SavingsOfferingBO savingsOffering3 = TestObjectFactory.createSavingsProduct("SavingPrd3", "zxsd", currentDate);

        centerSavingsAccount = TestObjectFactory.createSavingsAccount("43244334", center, Short.valueOf("16"),
                startDate, savingsOffering1);
        groupSavingsAccount = TestObjectFactory.createSavingsAccount("43234434", group, Short.valueOf("16"), startDate,
                savingsOffering2);
        clientSavingsAccount = TestObjectFactory.createSavingsAccount("43245434", client, Short.valueOf("16"),
                startDate, savingsOffering3);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        CustomerView customerView = new CustomerView();
        customerView.setCustomerId(center.getCustomerId());
        customerView.setCustomerSearchId(center.getSearchId());
        customerView.setCustomerLevelId(center.getCustomerLevel().getId());

        OfficeView officeView = new OfficeView(Short.valueOf("3"), "", Integer.valueOf(-1));
        PersonnelView personnelView = new PersonnelView(Short.valueOf("3"), "");
        
        SessionUtils.setAttribute(CollectionSheetEntryConstants.COLLECTION_SHEET_ENTRY_FORM_DTO,
                createCollectionSheetDto(customerView, officeView, personnelView), request);
        
        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.PAYMENT_TYPES_LIST,
                Arrays
                .asList(getPaymentTypeView()), request);
        SessionUtils.setAttribute(CollectionSheetEntryConstants.ISCENTERHIERARCHYEXISTS, Constants.YES, request);

        setMasterListInSession(center.getCustomerId());
        setRequestPathInfo("/collectionsheetaction.do");
        addRequestParameter("method", "get");
        addRequestParameter("officeId", "3");
        addRequestParameter("loanOfficerId", "3");
        addRequestParameter("paymentId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        Calendar meetinDateCalendar = new GregorianCalendar();
        meetinDateCalendar.setTime(getMeetingDates(meeting));
        int year = meetinDateCalendar.get(Calendar.YEAR);
        int month = meetinDateCalendar.get(Calendar.MONTH);
        int day = meetinDateCalendar.get(Calendar.DAY_OF_MONTH);
        meetinDateCalendar = new GregorianCalendar(year, month, day);
        SessionUtils.setAttribute("LastMeetingDate", new java.sql.Date(meetinDateCalendar.getTimeInMillis()), request);
        addRequestDateParameter("transactionDate", day + "/" + (month + 1) + "/" + year);
        addRequestParameter("receiptId", "1");
        addRequestDateParameter("receiptDate", "20/03/2006");
        addRequestParameter("customerId", String.valueOf(center.getCustomerId().intValue()));
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward("get_success");
    }

    public void testFailureGet() throws Exception {
        CollectionSheetEntryGridDto bulkEntry = getSuccessfulBulkEntry();
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(CollectionSheetEntryConstants.BULKENTRY, bulkEntry, request);
        SessionUtils.setAttribute(CollectionSheetEntryConstants.ISCENTERHIERARCHYEXISTS, Constants.YES, request);
        SessionUtils.setAttribute("LastMeetingDate", bulkEntry.getTransactionDate(), request);
        setRequestPathInfo("/collectionsheetaction.do");
        addRequestParameter("method", "get");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyActionErrors(new String[] { "errors.mandatoryenter", "errors.mandatoryselect", "errors.mandatoryselect",
                "errors.mandatoryselect", "errors.mandatoryselect" });
    }

    public void testFailurePreviewForEmptyAmount() throws Exception {
        CollectionSheetEntryGridDto bulkEntry = getFailureBulkEntry();
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(CollectionSheetEntryConstants.BULKENTRY, bulkEntry, request);
        setRequestPathInfo("/collectionsheetaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("customerAccountAmountEntered[0][6]", "");
        addRequestParameter("customerAccountAmountEntered[1][6]", "");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyActionErrors(new String[] { "errors.invalidamount", "errors.invalidamount" });
    }

    public void testFailurePreviewForCharAmount() throws Exception {
        CollectionSheetEntryGridDto bulkEntry = getFailureBulkEntry();
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(CollectionSheetEntryConstants.BULKENTRY, bulkEntry, request);
        setRequestPathInfo("/collectionsheetaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("customerAccountAmountEntered[0][6]", "abc");
        addRequestParameter("customerAccountAmountEntered[1][6]", "abc");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyActionErrors(new String[] { "errors.invalidamount", "errors.invalidamount" });
    }

    public void testValidateForLoadMethod() {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);

        setRequestPathInfo("/collectionsheetaction.do");
        addRequestParameter("method", "validate");
        addRequestParameter("input", "load");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.load_success.toString());

    }

    public void testValidateForGetMethod() {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);

        setRequestPathInfo("/collectionsheetaction.do");
        addRequestParameter("method", "validate");
        addRequestParameter("input", "get");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.get_success.toString());

    }

    public void testValidateForPreviewMethod() {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);

        setRequestPathInfo("/collectionsheetaction.do");
        addRequestParameter("method", "validate");
        addRequestParameter("input", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.preview_success.toString());

    }

    private ClientAttendanceDto getClientAttendanceDto(Integer clientId, Date meetingDate) {
        ClientAttendanceDto clientAttendanceDto = new ClientAttendanceDto(clientId, meetingDate, AttendanceType.ABSENT
                .getValue());
        return clientAttendanceDto;
    }

    private void addParametersForEnteredAmount() {
        for (int i = 0; i < 4; ++i) {
            addRequestParameter("enteredAmount[" + i + "][0]", "300.0");
            addRequestParameter("enteredAmount[" + i + "][1]", "300.0");
        }
    }

    private void addParametersForDisbursalEnteredAmount() {
        for (int i = 0; i < 4; ++i) {
            addRequestParameter("enteredAmount[" + i + "][5]", "300.0");
            addRequestParameter("enteredAmount[" + i + "][6]", "300.0");
        }
    }

    private CollectionSheetEntryGridDto getSuccessfulBulkEntry() throws Exception {

        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        Date startDate = new Date(System.currentTimeMillis());
        center = TestObjectFactory.createCenter("Center", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
        LoanOfferingBO loanOffering1 = TestObjectFactory.createLoanOffering(startDate, meeting);
        LoanOfferingBO loanOffering2 = TestObjectFactory.createLoanOffering("Loan2345", "313f", ApplicableTo.GROUPS,
                startDate, PrdStatus.LOAN_ACTIVE, 300.0, 1.2, 3, InterestType.FLAT, meeting);
        groupAccount = TestObjectFactory.createLoanAccount("42423142341", group,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate, loanOffering1);
        clientAccount = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1, loanOffering2);
        Date currentDate = new Date(System.currentTimeMillis());
        SavingsOfferingBO savingsOffering1 = TestObjectFactory.createSavingsProduct("SavingPrd1", "ased", currentDate);
        SavingsOfferingBO savingsOffering2 = TestObjectFactory.createSavingsProduct("SavingPrd2", "cvdf", currentDate);
        SavingsOfferingBO savingsOffering3 = TestObjectFactory.createSavingsProduct("SavingPrd3", "zxsd", currentDate);

        centerSavingsAccount = TestObjectFactory.createSavingsAccount("43244334", center, Short.valueOf("16"),
                startDate, savingsOffering1);
        groupSavingsAccount = TestObjectFactory.createSavingsAccount("43234434", group, Short.valueOf("16"), startDate,
                savingsOffering2);
        clientSavingsAccount = TestObjectFactory.createSavingsAccount("43245434", client, Short.valueOf("16"),
                startDate, savingsOffering3);

        CollectionSheetEntryView bulkEntryParent = new CollectionSheetEntryView(getCusomerView(center));
        SavingsAccountView centerSavingsAccountView = getSavingsAccountView(centerSavingsAccount);
        centerSavingsAccountView.setDepositAmountEntered("100");
        centerSavingsAccountView.setWithDrawalAmountEntered("10");
        bulkEntryParent.addSavingsAccountDetail(centerSavingsAccountView);
        bulkEntryParent.setCustomerAccountDetails(getCustomerAccountView(center));

        CollectionSheetEntryView bulkEntryChild = new CollectionSheetEntryView(getCusomerView(group));
        LoanAccountView groupLoanAccountView = getLoanAccountView(groupAccount);
        SavingsAccountView groupSavingsAccountView = getSavingsAccountView(groupSavingsAccount);
        groupSavingsAccountView.setDepositAmountEntered("100");
        groupSavingsAccountView.setWithDrawalAmountEntered("10");
        bulkEntryChild.addLoanAccountDetails(groupLoanAccountView);
        bulkEntryChild.addSavingsAccountDetail(groupSavingsAccountView);
        bulkEntryChild.setCustomerAccountDetails(getCustomerAccountView(group));

        CollectionSheetEntryView bulkEntrySubChild = new CollectionSheetEntryView(getCusomerView(client));
        LoanAccountView clientLoanAccountView = getLoanAccountView(clientAccount);
        clientLoanAccountView.setAmountPaidAtDisbursement(0.0);
        SavingsAccountView clientSavingsAccountView = getSavingsAccountView(clientSavingsAccount);
        clientSavingsAccountView.setDepositAmountEntered("100");
        clientSavingsAccountView.setWithDrawalAmountEntered("10");
        bulkEntrySubChild.addLoanAccountDetails(clientLoanAccountView);
        bulkEntrySubChild.setAttendence(new Short("2"));
        bulkEntrySubChild.addSavingsAccountDetail(clientSavingsAccountView);
        bulkEntrySubChild.setCustomerAccountDetails(getCustomerAccountView(client));

        bulkEntryChild.addChildNode(bulkEntrySubChild);
        bulkEntryParent.addChildNode(bulkEntryChild);

        LoanAccountsProductView childView = bulkEntryChild.getLoanAccountDetails().get(0);
        childView.setPrdOfferingId(groupLoanAccountView.getPrdOfferingId());
        childView.setEnteredAmount("100.0");
        LoanAccountsProductView subchildView = bulkEntrySubChild.getLoanAccountDetails().get(0);
        subchildView.setDisBursementAmountEntered(clientAccount.getLoanAmount().toString());
        subchildView.setPrdOfferingId(clientLoanAccountView.getPrdOfferingId());
        
        
        ProductDto loanOfferingDto = new ProductDto(loanOffering1.getPrdOfferingId(), loanOffering1
                .getPrdOfferingShortName());
        ProductDto loanOfferingDto2 = new ProductDto(loanOffering2.getPrdOfferingId(), loanOffering2
                .getPrdOfferingShortName());

        List<ProductDto> loanProducts = Arrays.asList(loanOfferingDto, loanOfferingDto2);

        ProductDto savingsOfferingDto = new ProductDto(savingsOffering1.getPrdOfferingId(), savingsOffering1
                .getPrdOfferingShortName());
        ProductDto savingsOfferingDto2 = new ProductDto(savingsOffering2.getPrdOfferingId(), savingsOffering2
                .getPrdOfferingShortName());
        ProductDto savingsOfferingDto3 = new ProductDto(savingsOffering3.getPrdOfferingId(), savingsOffering3
                .getPrdOfferingShortName());
        List<ProductDto> savingsProducts = Arrays.asList(savingsOfferingDto, savingsOfferingDto2, savingsOfferingDto3);
        
        final PersonnelView loanOfficer = getPersonnelView(center.getPersonnel());
        final OfficeView officeView = null;
        final HashMap<Integer, ClientAttendanceDto> clientAttendance = new HashMap<Integer, ClientAttendanceDto>();
        final List<CustomValueListElement> attendanceTypesList = new ArrayList<CustomValueListElement>();

        bulkEntryParent.setCountOfCustomers(3);
        final CollectionSheetEntryGridDto bulkEntry = new CollectionSheetEntryGridDto(bulkEntryParent, loanOfficer,
                officeView, getPaymentTypeView(), startDate, "324343242", startDate, loanProducts, savingsProducts,
                clientAttendance, attendanceTypesList);

        return bulkEntry;
    }

    private CollectionSheetEntryGridDto getFailureBulkEntry() throws Exception {
        Date startDate = new Date(System.currentTimeMillis());

        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("Center", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
        LoanOfferingBO loanOffering1 = TestObjectFactory.createLoanOffering(startDate, meeting);
        LoanOfferingBO loanOffering2 = TestObjectFactory.createLoanOffering("Loan2345", "313f", startDate, meeting);
        groupAccount = TestObjectFactory.createLoanAccount("42423142341", group,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate, loanOffering1);
        clientAccount = TestObjectFactory.createLoanAccount("3243", client, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering2);
        MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
                EVERY_WEEK, CUSTOMER_MEETING));
        MeetingBO meetingIntPost = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
                EVERY_WEEK, CUSTOMER_MEETING));
        SavingsOfferingBO savingsOffering = TestObjectFactory.createSavingsProduct("SavingPrd123c", "ased",
                ApplicableTo.GROUPS, startDate, PrdStatus.SAVINGS_ACTIVE, 300.0, RecommendedAmountUnit.PER_INDIVIDUAL,
                1.2, 200.0, 200.0, SavingsType.VOLUNTARY, InterestCalcType.MINIMUM_BALANCE, meetingIntCalc,
                meetingIntPost);
        SavingsOfferingBO savingsOffering1 = TestObjectFactory.createSavingsProduct("SavingPrd1we", "vbgr",
                ApplicableTo.GROUPS, startDate, PrdStatus.SAVINGS_ACTIVE, 300.0, RecommendedAmountUnit.PER_INDIVIDUAL,
                1.2, 200.0, 200.0, SavingsType.VOLUNTARY, InterestCalcType.MINIMUM_BALANCE, meetingIntCalc,
                meetingIntPost);
        centerSavingsAccount = TestObjectFactory.createSavingsAccount("432434", center, Short.valueOf("16"), startDate,
                savingsOffering);
        clientSavingsAccount = TestObjectFactory.createSavingsAccount("432434", client, Short.valueOf("16"), startDate,
                savingsOffering1);

        CollectionSheetEntryView bulkEntryParent = new CollectionSheetEntryView(getCusomerView(center));
        bulkEntryParent.addSavingsAccountDetail(getSavingsAccountView(centerSavingsAccount));
        bulkEntryParent.setCustomerAccountDetails(getCustomerAccountView(center));

        CollectionSheetEntryView bulkEntryChild = new CollectionSheetEntryView(getCusomerView(group));
        LoanAccountView groupLoanAccountView = getLoanAccountView(groupAccount);
        bulkEntryChild.addLoanAccountDetails(groupLoanAccountView);
        bulkEntryChild.setCustomerAccountDetails(getCustomerAccountView(group));
        CollectionSheetEntryView bulkEntrySubChild = new CollectionSheetEntryView(getCusomerView(client));
        LoanAccountView clientLoanAccountView = getLoanAccountView(clientAccount);
        bulkEntrySubChild.addLoanAccountDetails(clientLoanAccountView);
        bulkEntrySubChild.addSavingsAccountDetail(getSavingsAccountView(clientSavingsAccount));
        bulkEntrySubChild.setCustomerAccountDetails(getCustomerAccountView(client));

        bulkEntryChild.addChildNode(bulkEntrySubChild);
        bulkEntryParent.addChildNode(bulkEntryChild);
        bulkEntryChild.getLoanAccountDetails().get(0).setEnteredAmount("100.0");
        bulkEntryChild.getLoanAccountDetails().get(0).setPrdOfferingId(groupLoanAccountView.getPrdOfferingId());
        bulkEntrySubChild.getLoanAccountDetails().get(0).setEnteredAmount("100.0");
        bulkEntrySubChild.getLoanAccountDetails().get(0).setPrdOfferingId(clientLoanAccountView.getPrdOfferingId());
        
        ProductDto loanOfferingDto = new ProductDto(loanOffering1.getPrdOfferingId(), loanOffering1
                .getPrdOfferingShortName());
        ProductDto loanOfferingDto2 = new ProductDto(loanOffering2.getPrdOfferingId(), loanOffering2
                .getPrdOfferingShortName());
        
        List<ProductDto> loanProducts = Arrays.asList(loanOfferingDto, loanOfferingDto2);
        
        ProductDto savingsOfferingDto = new ProductDto(savingsOffering.getPrdOfferingId(), savingsOffering
                .getPrdOfferingShortName());
        List<ProductDto> savingsProducts = Arrays.asList(savingsOfferingDto);
        
        final PersonnelView loanOfficer = getPersonnelView(center.getPersonnel());
        final OfficeView officeView = null;
        final HashMap<Integer, ClientAttendanceDto> clientAttendance = new HashMap<Integer, ClientAttendanceDto>();
        final List<CustomValueListElement> attendanceTypesList = new ArrayList<CustomValueListElement>();

        bulkEntryParent.setCountOfCustomers(3);
        final CollectionSheetEntryGridDto bulkEntry = new CollectionSheetEntryGridDto(bulkEntryParent, loanOfficer,
                officeView, getPaymentTypeView(), startDate, "324343242", startDate,
                loanProducts, savingsProducts, clientAttendance, attendanceTypesList);

        return bulkEntry;
    }
    
    private CollectionSheetEntryFormDto createCollectionSheetDto(CustomerView customerView, OfficeView officeView,
            PersonnelView personnelView) {
        
        List<ListItem<Short>> paymentTypesDtoList = new ArrayList<ListItem<Short>>();

        List<OfficeView> activeBranches = Arrays.asList(officeView);
        List<CustomerView> customerList = Arrays.asList(customerView);
        List<PersonnelView> loanOfficerList = Arrays.asList(personnelView);
        final Short reloadFormAutomatically = Constants.YES;
        final Short backDatedTransactionAllowed = Constants.NO;
        final Short centerHierarchyExists = Constants.YES;
        final Date meetingDate = new Date();
        
        return new CollectionSheetEntryFormDto(activeBranches, paymentTypesDtoList, loanOfficerList, customerList,
                reloadFormAutomatically, centerHierarchyExists, backDatedTransactionAllowed, meetingDate);
    }
    
    private CollectionSheetEntryFormDto createDefaultCollectionSheetDto() {

        List<OfficeView> activeBranches = new ArrayList<OfficeView>();
        List<ListItem<Short>> paymentTypesDtoList = new ArrayList<ListItem<Short>>();
        List<CustomerView> customerList = new ArrayList<CustomerView>();
        List<PersonnelView> loanOfficerList = new ArrayList<PersonnelView>();
        final Short reloadFormAutomatically = Constants.YES;
        final Short backDatedTransactionAllowed = Constants.NO;
        final Short centerHierarchyExists = Constants.YES;
        final Date meetingDate = new Date();

        return new CollectionSheetEntryFormDto(activeBranches, paymentTypesDtoList, loanOfficerList, customerList,
                reloadFormAutomatically, centerHierarchyExists, backDatedTransactionAllowed, meetingDate);
    }

    private LoanAccountView getLoanAccountView(LoanBO account) {
        LoanAccountView accountView = TestObjectFactory.getLoanAccountView(account);
        List<AccountActionDateEntity> actionDates = new ArrayList<AccountActionDateEntity>();
        actionDates.add(account.getAccountActionDate((short) 1));
        accountView.addTrxnDetails(TestObjectFactory.getBulkEntryAccountActionViews(actionDates));

        return accountView;
    }

    private SavingsAccountView getSavingsAccountView(SavingsBO account) {
        final Integer customerId = null;
        final String savingOfferingShortName = account.getSavingsOffering().getPrdOfferingShortName();
        final Short savingOfferingId = account.getSavingsOffering().getPrdOfferingId();
        final Short savingsTypeId = account.getSavingsOffering().getSavingsType().getId();
        Short reccomendedAmountUnitId = null;
        if (account.getSavingsOffering().getRecommendedAmntUnit() != null) {
            reccomendedAmountUnitId = account.getSavingsOffering().getRecommendedAmntUnit().getId();
        }

        SavingsAccountView accountView = new SavingsAccountView(account.getAccountId(), customerId,
                savingOfferingShortName, savingOfferingId, savingsTypeId, reccomendedAmountUnitId);
        accountView.addAccountTrxnDetail(TestObjectFactory.getBulkEntryAccountActionView(account
                .getAccountActionDate((short) 1)));

        return accountView;
    }

    private CustomerView getCusomerView(CustomerBO customer) {
        CustomerView customerView = new CustomerView();
        customerView.setCustomerId(customer.getCustomerId());
        customerView.setCustomerLevelId(customer.getCustomerLevel().getId());
        customerView.setCustomerSearchId(customer.getSearchId());
        customerView.setDisplayName(customer.getDisplayName());
        customerView.setGlobalCustNum(customer.getGlobalCustNum());
        customerView.setOfficeId(customer.getOffice().getOfficeId());
        if (null != customer.getParentCustomer()) {
            customerView.setParentCustomerId(customer.getParentCustomer().getCustomerId());
        }
        customerView.setPersonnelId(customer.getPersonnel().getPersonnelId());
        customerView.setStatusId(customer.getCustomerStatus().getId());
        return customerView;
    }

    private PersonnelView getPersonnelView(PersonnelBO personnel) {
        PersonnelView personnelView = new PersonnelView(personnel.getPersonnelId(), personnel.getDisplayName());
        return personnelView;
    }

    private ListItem<Short> getPaymentTypeView() {
        ListItem<Short> paymentTypeView = new ListItem<Short>(Short.valueOf("1"), "displayValue");
        return paymentTypeView;
    }

    private CustomerAccountView getCustomerAccountView(CustomerBO customer) {
        CustomerAccountView customerAccountView = new CustomerAccountView(customer.getCustomerAccount().getAccountId());

        List<AccountActionDateEntity> accountAction = new ArrayList<AccountActionDateEntity>();
        accountAction.add(customer.getCustomerAccount().getAccountActionDate(Short.valueOf("1")));
        customerAccountView.setAccountActionDates(TestObjectFactory.getBulkEntryAccountActionViews(accountAction));
        customerAccountView.setCustomerAccountAmountEntered("100.0");
        customerAccountView.setValidCustomerAccountAmountEntered(true);
        return customerAccountView;
    }

    private AccountBO getLoanAccount(CustomerBO group, MeetingBO meeting) {
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
    }

    private static java.util.Date getMeetingDates(MeetingBO meeting) throws MeetingException {
        java.util.Date currentDate = new java.util.Date(System.currentTimeMillis());
        List<java.util.Date> dates = meeting.getAllDates(currentDate);
        return dates.get(dates.size() - 1);
    }

    private void setMasterListInSession(Integer customerId) throws PageExpiredException {
        OfficeView office = new OfficeView(Short.valueOf("3"), "Branch", OfficeConstants.BRANCHOFFICE, Integer
                .valueOf("0"));
        List<OfficeView> branchOfficesList = new ArrayList<OfficeView>();
        branchOfficesList.add(office);
        SessionUtils.setCollectionAttribute(OfficeConstants.OFFICESBRANCHOFFICESLIST, branchOfficesList, request);

        PersonnelView personnel = new PersonnelView(Short.valueOf("3"), "John");
        List<PersonnelView> personnelList = new ArrayList<PersonnelView>();
        personnelList.add(personnel);
        SessionUtils.setCollectionAttribute(CustomerConstants.LOAN_OFFICER_LIST, personnelList, request);

        CustomerView parentCustomer = new CustomerView(customerId, "Center_Active", Short.valueOf(CustomerLevel.CENTER
                .getValue()), "1.1");
        List<CustomerView> customerList = new ArrayList<CustomerView>();
        customerList.add(parentCustomer);
        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.CUSTOMERSLIST, customerList, request);
    }

    private Locale getUserLocale(HttpServletRequest request) {
        Locale locale = null;
        HttpSession session = request.getSession();
        if (session != null) {
            UserContext userContext = (UserContext) session.getAttribute(LoginConstants.USERCONTEXT);
            if (null != userContext) {
                locale = userContext.getCurrentLocale();

            }
        }
        return locale;
    }

    private LoanBO getLoanAccount(AccountState state, Date startDate, int disbursalType, LoanOfferingBO loanOfferingBO) {
        return TestObjectFactory.createLoanAccountWithDisbursement("99999999999", group, state, startDate,
                loanOfferingBO, disbursalType);

    }

}