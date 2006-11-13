/**

 * TestBulkEntryAction.java    version: 1.0

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */

package org.mifos.application.bulkentry.struts.action;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountView;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsAccountView;
import org.mifos.application.bulkentry.business.BulkEntryBO;
import org.mifos.application.bulkentry.business.BulkEntryView;
import org.mifos.application.bulkentry.struts.actionforms.BulkEntryActionForm;
import org.mifos.application.bulkentry.util.helpers.BulkEntryConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.util.helpers.CustomerAccountView;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.business.PaymentTypeView;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestBulkEntryAction extends MifosMockStrutsTestCase {
	UserContext userContext;

	CustomerBO center;

	CustomerBO group;

	ClientBO client;

	AccountBO account;

	LoanBO groupAccount;

	LoanBO clientAccount;

	private SavingsBO centerSavingsAccount;

	private SavingsBO groupSavingsAccount;

	private SavingsBO clientSavingsAccount;

	private String flowKey;

	@Override
	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(centerSavingsAccount);
		TestObjectFactory.cleanUp(groupSavingsAccount);
		TestObjectFactory.cleanUp(clientSavingsAccount);
		TestObjectFactory.cleanUp(groupAccount);
		TestObjectFactory.cleanUp(clientAccount);
		TestObjectFactory.cleanUp(account);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
					.getPath());
			setConfigFile(ResourceLoader.getURI(
					"org/mifos/application/bulkentry/struts-config.xml")
					.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		userContext = new UserContext();
		userContext.setId(new Short("1"));
		userContext.setLocaleId(new Short("1"));
		Locale locale = new Locale("en", "US");
		userContext.setPereferedLocale(locale);
		Set<Short> set = new HashSet<Short>();
		set.add(Short.valueOf("1"));
		userContext.setRoles(set);
		userContext.setLevelId(Short.valueOf("2"));
		userContext.setName("mifos");
		userContext.setPereferedLocale(new Locale("en", "US"));
		userContext.setBranchId(new Short("1"));
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
		flowKey = createFlow(request, BulkEntryAction.class);
	}

	public void testSuccessfulCreate() throws Exception {
		BulkEntryBO bulkEntry = getSuccessfulBulkEntry();
		Calendar meetinDateCalendar = new GregorianCalendar();
		int year = meetinDateCalendar.get(Calendar.YEAR);
		int month = meetinDateCalendar.get(Calendar.MONTH);
		int day = meetinDateCalendar.get(Calendar.DAY_OF_MONTH);
		meetinDateCalendar = new GregorianCalendar(year, month, day);
		
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(BulkEntryConstants.BULKENTRY, bulkEntry,
				request);
		setRequestPathInfo("/bulkentryaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("transactionDate", day + "/" + (month + 1) + "/"
				+ year);
		actionPerform();

		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/bulkentryaction.do");
		addRequestParameter("method", "create");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("attendenceSelected[0]", "2");
		addRequestParameter("transactionDate", day + "/" + (month + 1) + "/"
				+ year);

		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("create_success");
		assertNotNull(request.getAttribute(BulkEntryConstants.CENTER));
		assertEquals(request.getAttribute(BulkEntryConstants.CENTER), center
				.getDisplayName());

		groupAccount = (LoanBO) TestObjectFactory.getObject(LoanBO.class,
				groupAccount.getAccountId());
		clientAccount = (LoanBO) TestObjectFactory.getObject(LoanBO.class,
				clientAccount.getAccountId());
		centerSavingsAccount = (SavingsBO) TestObjectFactory.getObject(
				SavingsBO.class, centerSavingsAccount.getAccountId());
		clientSavingsAccount = (SavingsBO) TestObjectFactory.getObject(
				SavingsBO.class, clientSavingsAccount.getAccountId());
		groupSavingsAccount = (SavingsBO) TestObjectFactory.getObject(
				SavingsBO.class, groupSavingsAccount.getAccountId());
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		group = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());

		assertEquals(client.getClientAttendances().size(), 1);
		assertEquals("2", client.getClientAttendanceForMeeting(
				new java.sql.Date(meetinDateCalendar.getTimeInMillis()))
				.getAttendance().toString());
	}

	public void testFailureCreate() throws Exception {
		BulkEntryBO bulkEntry = getFailureBulkEntry();
		Calendar meetinDateCalendar = new GregorianCalendar();
		int year = meetinDateCalendar.get(Calendar.YEAR);
		int month = meetinDateCalendar.get(Calendar.MONTH);
		int day = meetinDateCalendar.get(Calendar.DAY_OF_MONTH);
		meetinDateCalendar = new GregorianCalendar(year, month, day);
		
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(BulkEntryConstants.BULKENTRY, bulkEntry,
				request);
		setRequestPathInfo("/bulkentryaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("transactionDate", day + "/" + (month + 1) + "/"
				+ year);
		TestObjectFactory.simulateInvalidConnection();
		actionPerform();

		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/bulkentryaction.do");
		addRequestParameter("method", "create");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("attendenceSelected[0]", "2");
		addRequestParameter("transactionDate", day + "/" + (month + 1) + "/"
				+ year);

		
		actionPerform();
		HibernateUtil.closeSession();

		groupAccount = (LoanBO) TestObjectFactory.getObject(LoanBO.class,
				groupAccount.getAccountId());
		clientAccount = (LoanBO) TestObjectFactory.getObject(LoanBO.class,
				clientAccount.getAccountId());
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		group = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());

		verifyActionErrors(new String[] { "errors.update" });
	}

	public void testSuccessfulPreview() throws Exception {
		BulkEntryBO bulkEntry = getSuccessfulBulkEntry();
		Calendar meetinDateCalendar = new GregorianCalendar();
		int year = meetinDateCalendar.get(Calendar.YEAR);
		int month = meetinDateCalendar.get(Calendar.MONTH);
		int day = meetinDateCalendar.get(Calendar.DAY_OF_MONTH);
		meetinDateCalendar = new GregorianCalendar(year, month, day);
		
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(BulkEntryConstants.BULKENTRY, bulkEntry,
				request);
		setRequestPathInfo("/bulkentryaction.do");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("method", "preview");
		addRequestParameter("attendenceSelected[0]", "1");
		addRequestParameter("enteredAmount[0][0]", "212.0");
		addRequestParameter("enteredAmount[1][1]", "212.0");
		addRequestParameter("enteredAmount[0][1]", "212.0");
		addRequestParameter("enteredAmount[1][0]", "212.0");
		addRequestParameter("withDrawalAmountEntered[2][2]", "100.0");
		addRequestParameter("depositAmountEntered[2][2]", "100.0");
		addRequestParameter("withDrawalAmountEntered[0][0]", "100.0");
		addRequestParameter("depositAmountEntered[0][0]", "100.0");
		addRequestParameter("transactionDate", day + "/" + (month + 1) + "/"
				+ year);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("preview_success");
		HibernateUtil.closeSession();

		groupAccount = (LoanBO) TestObjectFactory.getObject(LoanBO.class,
				groupAccount.getAccountId());
		clientAccount = (LoanBO) TestObjectFactory.getObject(LoanBO.class,
				clientAccount.getAccountId());
		centerSavingsAccount = (SavingsBO) TestObjectFactory.getObject(
				SavingsBO.class, centerSavingsAccount.getAccountId());
		clientSavingsAccount = (SavingsBO) TestObjectFactory.getObject(
				SavingsBO.class, clientSavingsAccount.getAccountId());
		groupSavingsAccount = (SavingsBO) TestObjectFactory.getObject(
				SavingsBO.class, groupSavingsAccount.getAccountId());
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		group = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());

	}

	public void testFailurePreview() throws Exception {
		BulkEntryBO bulkEntry = getFailureBulkEntry();
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(BulkEntryConstants.BULKENTRY, bulkEntry,
				request);
		setRequestPathInfo("/bulkentryaction.do");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("method", "preview");
		addRequestParameter("customerAccountAmountEntered[0][6]", "");
		addRequestParameter("customerAccountAmountEntered[1][6]", "abc");
		actionPerform();

		verifyActionErrors(new String[] { "errors.invalidamount",
				"errors.invalidamount" });

	}

	public void testLoad() throws PageExpiredException {
		setRequestPathInfo("/bulkentryaction.do");
		addRequestParameter("method", "load");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward("load_success");
		assertEquals("The value for isBackDated Trxn Allowed", SessionUtils
				.getAttribute(BulkEntryConstants.ISBACKDATEDTRXNALLOWED,
						request), Constants.NO);
		assertEquals("The value for isCenter Heirarchy Exists", SessionUtils
				.getAttribute(BulkEntryConstants.ISCENTERHEIRARCHYEXISTS,
						request), Constants.YES);
	}

	public void testLoadForNonLoanOfficerInBranch() throws PageExpiredException {
		userContext.setBranchId(Short.valueOf("3"));
		userContext.setId(Short.valueOf("2"));
		userContext.setLevelId(Short.valueOf("2"));
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		setRequestPathInfo("/bulkentryaction.do");
		addRequestParameter("method", "load");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward("load_success");
		assertEquals("The value for isBackDated Trxn Allowed", SessionUtils
				.getAttribute(BulkEntryConstants.ISBACKDATEDTRXNALLOWED,
						request), Constants.NO);
	}

	public void testLoadPersonnel() throws PageExpiredException {
		setRequestPathInfo("/bulkentryaction.do");
		addRequestParameter("method", "loadLoanOfficers");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward("load_success");
		List<PersonnelView> loanOfficerList = (List<PersonnelView>) SessionUtils
				.getAttribute(CustomerConstants.LOAN_OFFICER_LIST, request);
		assertEquals(1, loanOfficerList.size());
	}

	public void testLoadCustomers() throws PageExpiredException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active", meeting);
		setRequestPathInfo("/bulkentryaction.do");
		addRequestParameter("method", "loadCustomerList");
		addRequestParameter("officeId", "3");
		addRequestParameter("loanOfficerId", "1");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward("load_success");
		List<CustomerView> parentCustomerList = (List<CustomerView>) SessionUtils
				.getAttribute(BulkEntryConstants.CUSTOMERSLIST, request);
		assertEquals(1, parentCustomerList.size());
		assertEquals("The value for isCenter Heirarchy Exists", SessionUtils
				.getAttribute(BulkEntryConstants.ISCENTERHEIRARCHYEXISTS,
						request), Constants.YES);
	}

	public void testGetLastMeetingDateForCustomer() throws PageExpiredException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active", meeting);
		setRequestPathInfo("/bulkentryaction.do");
		addRequestParameter("method", "getLastMeetingDateForCustomer");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("officeId", "3");
		addRequestParameter("loanOfficerId", "1");
		addRequestParameter("customerId", String.valueOf(center.getCustomerId()
				.intValue()));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("load_success");
		if (Configuration.getInstance().getAccountConfig(
				Short.valueOf(center.getOffice().getOfficeId()))
				.isBackDatedTxnAllowed()) {
			assertEquals("The value for isBackDated Trxn Allowed", SessionUtils
					.getAttribute(BulkEntryConstants.ISBACKDATEDTRXNALLOWED,
							request), Constants.YES);
			assertEquals(new java.sql.Date(DateUtils.getDateWithoutTimeStamp(
					getMeetingDates(meeting).getTime()).getTime()).toString(),
					SessionUtils.getAttribute("LastMeetingDate", request)
							.toString());
			assertEquals(DateHelper.getUserLocaleDate(getUserLocale(request),
					new java.sql.Date(DateUtils.getDateWithoutTimeStamp(
							getMeetingDates(meeting).getTime()).getTime())
							.toString()), ((BulkEntryActionForm) request
					.getSession().getAttribute(
							BulkEntryConstants.BULKENTRYACTIONFORM))
					.getTransactionDate());
		} else {
			assertEquals("The value for isBackDated Trxn Allowed", SessionUtils
					.getAttribute(BulkEntryConstants.ISBACKDATEDTRXNALLOWED,
							request), Constants.NO);
			assertEquals(new java.sql.Date(DateUtils.getDateWithoutTimeStamp(
					getMeetingDates(meeting).getTime()).getTime()).toString(),
					SessionUtils.getAttribute("LastMeetingDate", request)
							.toString());
			assertEquals(DateHelper.getUserLocaleDate(getUserLocale(request),
					new java.sql.Date(DateUtils
							.getCurrentDateWithoutTimeStamp().getTime())
							.toString()), ((BulkEntryActionForm) request
					.getSession().getAttribute(
							BulkEntryConstants.BULKENTRYACTIONFORM))
					.getTransactionDate());
		}
	}

	public void testSuccessfulGet() throws Exception {
		MasterDataService masterService = (MasterDataService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.MasterDataService);
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		Date startDate = new Date(System.currentTimeMillis());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client", 
				CustomerStatus.CLIENT_ACTIVE,
				group);
		account = getLoanAccount(group, meeting);
		SavingsOfferingBO savingsOffering1 = createSavingsOffering(
				"SavingPrd1", "ased");
		SavingsOfferingBO savingsOffering2 = createSavingsOffering(
				"SavingPrd2", "cvdf");
		SavingsOfferingBO savingsOffering3 = createSavingsOffering(
				"SavingPrd3", "zxsd");

		centerSavingsAccount = TestObjectFactory.createSavingsAccount(
				"43244334", center, Short.valueOf("16"), startDate,
				savingsOffering1);
		groupSavingsAccount = TestObjectFactory.createSavingsAccount(
				"43234434", group, Short.valueOf("16"), startDate,
				savingsOffering2);
		clientSavingsAccount = TestObjectFactory.createSavingsAccount(
				"43245434", client, Short.valueOf("16"), startDate,
				savingsOffering3);
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils
		.setAttribute(BulkEntryConstants.PAYMENT_TYPES_LIST,
				masterService.retrieveMasterEntities(
						PaymentTypeEntity.class, userContext
								.getLocaleId()), request);
		SessionUtils.setAttribute(BulkEntryConstants.ISCENTERHEIRARCHYEXISTS,
				Constants.YES, request);

		setMasterListInSession(center.getCustomerId());
		setRequestPathInfo("/bulkentryaction.do");
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
		SessionUtils.setAttribute("LastMeetingDate", new java.sql.Date(
				meetinDateCalendar.getTimeInMillis()), request);
		addRequestParameter("transactionDate", (month + 1) + "/" + day + "/"
				+ year);
		addRequestParameter("receiptId", "1");
		addRequestParameter("receiptDate", "03/20/2006");
		addRequestParameter("customerId", String.valueOf(center.getCustomerId()
				.intValue()));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("get_success");
	}

	public void testFailureGet() throws Exception {
		BulkEntryBO bulkEntry = getSuccessfulBulkEntry();
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(BulkEntryConstants.BULKENTRY, bulkEntry,
				request);
		SessionUtils.setAttribute(BulkEntryConstants.ISCENTERHEIRARCHYEXISTS,
				Constants.YES, request);
		setRequestPathInfo("/bulkentryaction.do");
		addRequestParameter("method", "get");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyActionErrors(new String[] { "errors.mandatoryenter",
				"errors.mandatoryselect", "errors.mandatoryselect",
				"errors.mandatoryselect", "errors.mandatoryselect" });
	}

	public void testFailurePreviewForEmptyAmount() throws Exception {
		BulkEntryBO bulkEntry = getFailureBulkEntry();
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(BulkEntryConstants.BULKENTRY, bulkEntry,
				request);
		setRequestPathInfo("/bulkentryaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("customerAccountAmountEntered[0][6]", "");
		addRequestParameter("customerAccountAmountEntered[1][6]", "");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyActionErrors(new String[] { "errors.invalidamount",
				"errors.invalidamount" });
	}

	public void testFailurePreviewForCharAmount() throws Exception {
		BulkEntryBO bulkEntry = getFailureBulkEntry();
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(BulkEntryConstants.BULKENTRY, bulkEntry,
				request);
		setRequestPathInfo("/bulkentryaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("customerAccountAmountEntered[0][6]", "abc");
		addRequestParameter("customerAccountAmountEntered[1][6]", "abc");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyActionErrors(new String[] { "errors.invalidamount",
				"errors.invalidamount" });
	}

	public void testValidateForLoadMethod() {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);

		setRequestPathInfo("/bulkentryaction.do");
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

		setRequestPathInfo("/bulkentryaction.do");
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

		setRequestPathInfo("/bulkentryaction.do");
		addRequestParameter("method", "validate");
		addRequestParameter("input", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.preview_success.toString());

	}

	private BulkEntryBO getSuccessfulBulkEntry() throws Exception {

		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		Date startDate = new Date(System.currentTimeMillis());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client", 
				CustomerStatus.CLIENT_ACTIVE,
				group);
		LoanOfferingBO loanOffering1 = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		LoanOfferingBO loanOffering2 = TestObjectFactory.createLoanOffering(
				"Loan2345", "313f", Short.valueOf("2"), new Date(System
						.currentTimeMillis()), Short.valueOf("1"), 300.0, 1.2,
				Short.valueOf("3"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				meeting);
		groupAccount = TestObjectFactory.createLoanAccount("42423142341",
				group, Short.valueOf("5"),
				new Date(System.currentTimeMillis()), loanOffering1);
		clientAccount = getLoanAccount(Short.valueOf("3"), startDate, 1,
				loanOffering2);
		SavingsOfferingBO savingsOffering1 = createSavingsOffering(
				"SavingPrd1", "ased");
		SavingsOfferingBO savingsOffering2 = createSavingsOffering(
				"SavingPrd2", "cvdf");
		SavingsOfferingBO savingsOffering3 = createSavingsOffering(
				"SavingPrd3", "zxsd");

		centerSavingsAccount = TestObjectFactory.createSavingsAccount(
				"43244334", center, Short.valueOf("16"), startDate,
				savingsOffering1);
		groupSavingsAccount = TestObjectFactory.createSavingsAccount(
				"43234434", group, Short.valueOf("16"), startDate,
				savingsOffering2);
		clientSavingsAccount = TestObjectFactory.createSavingsAccount(
				"43245434", client, Short.valueOf("16"), startDate,
				savingsOffering3);

		BulkEntryBO bulkEntry = new BulkEntryBO();

		BulkEntryView bulkEntryParent = new BulkEntryView(
				getCusomerView(center));
		SavingsAccountView centerSavingsAccountView = getSavingsAccountView(centerSavingsAccount);
		centerSavingsAccountView.setDepositAmountEntered("100");
		centerSavingsAccountView.setWithDrawalAmountEntered("10");
		bulkEntryParent.addSavingsAccountDetail(centerSavingsAccountView);
		bulkEntryParent
				.setCustomerAccountDetails(getCustomerAccountView(center));

		BulkEntryView bulkEntryChild = new BulkEntryView(getCusomerView(group));
		LoanAccountView groupLoanAccountView = getLoanAccountView(groupAccount);
		SavingsAccountView groupSavingsAccountView = getSavingsAccountView(groupSavingsAccount);
		groupSavingsAccountView.setDepositAmountEntered("100");
		groupSavingsAccountView.setWithDrawalAmountEntered("10");
		bulkEntryChild.addLoanAccountDetails(groupLoanAccountView);
		bulkEntryChild.addSavingsAccountDetail(groupSavingsAccountView);
		bulkEntryChild.setCustomerAccountDetails(getCustomerAccountView(group));

		BulkEntryView bulkEntrySubChild = new BulkEntryView(
				getCusomerView(client));
		LoanAccountView clientLoanAccountView = getLoanAccountView(clientAccount);
		clientLoanAccountView.setAmountPaidAtDisbursement(0.0);
		SavingsAccountView clientSavingsAccountView = getSavingsAccountView(clientSavingsAccount);
		clientSavingsAccountView.setDepositAmountEntered("100");
		clientSavingsAccountView.setWithDrawalAmountEntered("10");
		bulkEntrySubChild.addLoanAccountDetails(clientLoanAccountView);
		bulkEntrySubChild.setAttendence(new Short("2"));
		bulkEntrySubChild.addSavingsAccountDetail(clientSavingsAccountView);
		bulkEntrySubChild
				.setCustomerAccountDetails(getCustomerAccountView(client));

		bulkEntryChild.addChildNode(bulkEntrySubChild);
		bulkEntryParent.addChildNode(bulkEntryChild);

		bulkEntryChild.getLoanAccountDetails().get(0).setPrdOfferingId(
				groupLoanAccountView.getPrdOfferingId());
		bulkEntryChild.getLoanAccountDetails().get(0).setEnteredAmount("100.0");
		bulkEntrySubChild.getLoanAccountDetails().get(0)
				.setDisBursementAmountEntered(
						clientAccount.getLoanAmount().toString());
		bulkEntrySubChild.getLoanAccountDetails().get(0).setPrdOfferingId(
				clientLoanAccountView.getPrdOfferingId());
		List<PrdOfferingBO> loanProducts = new ArrayList<PrdOfferingBO>();
		loanProducts.add(loanOffering1);
		loanProducts.add(loanOffering2);
		List<PrdOfferingBO> savingsProducts = new ArrayList<PrdOfferingBO>();
		savingsProducts.add(savingsOffering1);
		savingsProducts.add(savingsOffering2);
		savingsProducts.add(savingsOffering3);
		bulkEntry.setLoanProducts(loanProducts);
		bulkEntry.setSavingsProducts(savingsProducts);
		bulkEntry.setTotalCustomers(3);
		bulkEntry.setBulkEntryParent(bulkEntryParent);
		bulkEntry.setReceiptDate(new java.sql.Date(System.currentTimeMillis()));
		bulkEntry.setReceiptId("324343242");
		bulkEntry.setLoanOfficer(getPersonnelView(center.getPersonnel()));
		bulkEntry.setPaymentType(getPaymentTypeView());
		bulkEntry.setTransactionDate(new java.sql.Date(System
				.currentTimeMillis()));

		return bulkEntry;
	}

	private BulkEntryBO getFailureBulkEntry() throws Exception {

		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient(
				"Client", CustomerStatus.CLIENT_ACTIVE,
				group);
		LoanOfferingBO loanOffering1 = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		LoanOfferingBO loanOffering2 = TestObjectFactory.createLoanOffering(
				"Loan2345", "313f", Short.valueOf("2"), new Date(System
						.currentTimeMillis()), Short.valueOf("1"), 300.0, 1.2,
				Short.valueOf("3"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				meeting);
		groupAccount = TestObjectFactory.createLoanAccount("42423142341",
				group, Short.valueOf("5"),
				new Date(System.currentTimeMillis()), loanOffering1);
		clientAccount = TestObjectFactory.createLoanAccount("3243", client,
				Short.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering2);
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		SavingsOfferingBO savingsOffering = TestObjectFactory
				.createSavingsOffering("SavingPrd123c", "ased", Short
						.valueOf("2"), new Date(System.currentTimeMillis()),
						Short.valueOf("2"), 300.0, Short.valueOf("1"), 1.2,
						200.0, 200.0, Short.valueOf("2"), Short.valueOf("1"),
						meetingIntCalc, meetingIntPost);
		SavingsOfferingBO savingsOffering1 = TestObjectFactory
				.createSavingsOffering("SavingPrd1we", "vbgr", Short
						.valueOf("2"), new Date(System.currentTimeMillis()),
						Short.valueOf("2"), 300.0, Short.valueOf("1"), 1.2,
						200.0, 200.0, Short.valueOf("2"), Short.valueOf("1"),
						meetingIntCalc, meetingIntPost);
		centerSavingsAccount = TestObjectFactory.createSavingsAccount("432434",
				center, Short.valueOf("16"), new Date(System
						.currentTimeMillis()), savingsOffering);
		clientSavingsAccount = TestObjectFactory.createSavingsAccount("432434",
				client, Short.valueOf("16"), new Date(System
						.currentTimeMillis()), savingsOffering1);

		// java.sql.Date transactionDate = new Date(System.currentTimeMillis());

		BulkEntryBO bulkEntry = new BulkEntryBO();

		BulkEntryView bulkEntryParent = new BulkEntryView(
				getCusomerView(center));
		bulkEntryParent
				.addSavingsAccountDetail(getSavingsAccountView(centerSavingsAccount));
		bulkEntryParent
				.setCustomerAccountDetails(getCustomerAccountView(center));

		BulkEntryView bulkEntryChild = new BulkEntryView(getCusomerView(group));
		LoanAccountView groupLoanAccountView = getLoanAccountView(groupAccount);
		bulkEntryChild.addLoanAccountDetails(groupLoanAccountView);
		bulkEntryChild.setCustomerAccountDetails(getCustomerAccountView(group));
		BulkEntryView bulkEntrySubChild = new BulkEntryView(
				getCusomerView(client));
		LoanAccountView clientLoanAccountView = getLoanAccountView(clientAccount);
		bulkEntrySubChild.addLoanAccountDetails(clientLoanAccountView);
		bulkEntrySubChild
				.addSavingsAccountDetail(getSavingsAccountView(clientSavingsAccount));
		bulkEntrySubChild
				.setCustomerAccountDetails(getCustomerAccountView(client));

		bulkEntryChild.addChildNode(bulkEntrySubChild);
		bulkEntryParent.addChildNode(bulkEntryChild);
		bulkEntryChild.getLoanAccountDetails().get(0).setEnteredAmount("100.0");
		bulkEntryChild.getLoanAccountDetails().get(0).setPrdOfferingId(
				groupLoanAccountView.getPrdOfferingId());
		bulkEntrySubChild.getLoanAccountDetails().get(0).setEnteredAmount(
				"100.0");
		bulkEntrySubChild.getLoanAccountDetails().get(0).setPrdOfferingId(
				clientLoanAccountView.getPrdOfferingId());
		List<PrdOfferingBO> loanProducts = new ArrayList<PrdOfferingBO>();
		loanProducts.add(loanOffering1);
		loanProducts.add(loanOffering2);
		List<PrdOfferingBO> savingsProducts = new ArrayList<PrdOfferingBO>();
		savingsProducts.add(savingsOffering);
		bulkEntry.setLoanProducts(loanProducts);
		bulkEntry.setSavingsProducts(savingsProducts);
		bulkEntry.setTotalCustomers(3);
		bulkEntry.setBulkEntryParent(bulkEntryParent);
		bulkEntry.setReceiptDate(new java.sql.Date(System.currentTimeMillis()));
		bulkEntry.setReceiptId("324343242");
		bulkEntry.setLoanOfficer(getPersonnelView(center.getPersonnel()));
		bulkEntry.setPaymentType(getPaymentTypeView());
		bulkEntry.setTransactionDate(new java.sql.Date(System
				.currentTimeMillis()));

		return bulkEntry;
	}

	private LoanAccountView getLoanAccountView(LoanBO account) {
		LoanAccountView accountView = TestObjectFactory
				.getLoanAccountView(account);
		List<AccountActionDateEntity> actionDates = new ArrayList<AccountActionDateEntity>();
		actionDates.add(account.getAccountActionDate((short) 1));
		accountView.addTrxnDetails(TestObjectFactory
				.getBulkEntryAccountActionViews(actionDates));

		return accountView;
	}

	private SavingsAccountView getSavingsAccountView(SavingsBO account) {
		SavingsAccountView accountView = new SavingsAccountView(account
				.getAccountId(), account.getAccountType().getAccountTypeId(),
				account.getSavingsOffering());
		accountView.addAccountTrxnDetail(TestObjectFactory
				.getBulkEntryAccountActionView(account
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
		if (null != customer.getParentCustomer())
			customerView.setParentCustomerId(customer.getParentCustomer()
					.getCustomerId());
		customerView.setPersonnelId(customer.getPersonnel().getPersonnelId());
		customerView.setStatusId(customer.getCustomerStatus().getId());
		return customerView;
	}

	private PersonnelView getPersonnelView(PersonnelBO personnel) {
		PersonnelView personnelView = new PersonnelView(personnel
				.getPersonnelId(), personnel.getDisplayName());
		return personnelView;
	}

	private PaymentTypeView getPaymentTypeView() {
		PaymentTypeView paymentTypeView = new PaymentTypeView();
		paymentTypeView.setPaymentTypeId(Short.valueOf("1"));
		return paymentTypeView;
	}

	private CustomerAccountView getCustomerAccountView(CustomerBO customer) {
		CustomerAccountView customerAccountView = new CustomerAccountView(
				customer.getCustomerAccount().getAccountId());

		List<AccountActionDateEntity> accountAction = new ArrayList<AccountActionDateEntity>();
		accountAction.add(customer.getCustomerAccount().getAccountActionDate(
				Short.valueOf("1")));
		customerAccountView.setAccountActionDates(TestObjectFactory
				.getBulkEntryAccountActionViews(accountAction));
		customerAccountView.setCustomerAccountAmountEntered("100.0");
		customerAccountView.setValidCustomerAccountAmountEntered(true);
		return customerAccountView;
	}

	private AccountBO getLoanAccount(CustomerBO group, MeetingBO meeting) {

		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", group, Short
				.valueOf("5"), startDate, loanOffering);
	}

	private static java.util.Date getMeetingDates(MeetingBO meeting) {
		List<java.util.Date> dates = new ArrayList<java.util.Date>();
		try {
			dates = meeting.getAllDates(new java.util.Date(System
					.currentTimeMillis()));
		} catch (MeetingException e) {
			e.printStackTrace();
		}
		return dates.get(dates.size() - 1);
	}

	private SavingsOfferingBO createSavingsOffering(String offeringName,
			String shortName) {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createSavingsOffering(offeringName, shortName,
				Short.valueOf("2"), new Date(System.currentTimeMillis()), Short
						.valueOf("2"), 300.0, Short.valueOf("1"), 1.2, 200.0,
				200.0, Short.valueOf("2"), Short.valueOf("1"), meetingIntCalc,
				meetingIntPost);
	}

	private void setMasterListInSession(Integer customerId)
			throws PageExpiredException {
		OfficeView office = new OfficeView(Short.valueOf("3"), "Branch",
				OfficeConstants.BRANCHOFFICE, Integer.valueOf("0"));
		List<OfficeView> branchOfficesList = new ArrayList<OfficeView>();
		branchOfficesList.add(office);
		SessionUtils.setAttribute(OfficeConstants.OFFICESBRANCHOFFICESLIST,
				branchOfficesList, request);

		PersonnelView personnel = new PersonnelView(Short.valueOf("3"), "John");
		List<PersonnelView> personnelList = new ArrayList<PersonnelView>();
		personnelList.add(personnel);
		SessionUtils.setAttribute(CustomerConstants.LOAN_OFFICER_LIST,
				personnelList, request);

		CustomerView parentCustomer = new CustomerView(customerId,
				"Center_Active", Short
						.valueOf(CustomerConstants.CENTER_LEVEL_ID), "1.1");
		List<CustomerView> customerList = new ArrayList<CustomerView>();
		customerList.add(parentCustomer);
		SessionUtils.setAttribute(BulkEntryConstants.CUSTOMERSLIST,
				customerList, request);
	}

	private Locale getUserLocale(HttpServletRequest request) {
		Locale locale = null;
		HttpSession session = request.getSession();
		if (session != null) {
			UserContext userContext = (UserContext) session
					.getAttribute(LoginConstants.USERCONTEXT);
			if (null != userContext) {
				locale = userContext.getPereferedLocale();
				if (null == locale) {
					locale = userContext.getMfiLocale();
				}
			}
		}
		return locale;
	}

	private LoanBO getLoanAccount(Short accountSate, Date startDate,
			int disbursalType, LoanOfferingBO loanOfferingBO) {
		return TestObjectFactory.createLoanAccountWithDisbursement(
				"99999999999", group, accountSate, startDate, loanOfferingBO,
				disbursalType);

	}

}
