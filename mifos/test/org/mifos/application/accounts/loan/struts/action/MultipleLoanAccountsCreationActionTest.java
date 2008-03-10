package org.mifos.application.accounts.loan.struts.action;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.loan.util.helpers.LoanExceptionConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.MONTHLY;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_MONTH;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

public class MultipleLoanAccountsCreationActionTest extends
		MifosMockStrutsTestCase {

	private UserContext userContext;

	protected AccountBO accountBO = null;

	protected CustomerBO center = null;

	protected CustomerBO group = null;

	private CustomerBO client = null;

	private String flowKey;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext",
				TestObjectFactory.getActivityContext());
		flowKey = createFlow(request, MultipleLoanAccountsCreationAction.class);
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testLoad() throws Exception {
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "load");
		performNoErrors();
		verifyForward(ActionForwards.load_success.toString());
		assertNotNull(SessionUtils.getAttribute(
				LoanConstants.MULTIPLE_LOANS_OFFICES_LIST, request));
		assertNotNull(SessionUtils.getAttribute(
				LoanConstants.IS_CENTER_HEIRARCHY_EXISTS, request));
	}

	public void testGetLoanOfficersWithoutOffice() throws Exception {
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "getLoanOfficers");
		actionPerform();
		verifyActionErrors(new String[] { LoanConstants.MANDATORY_SELECT });
		verifyInputForward();
	}

	public void testGetLoanOfficers() throws Exception {
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "load");
		actionPerform();
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "getLoanOfficers");
		addRequestParameter("branchOfficeId", "1");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		performNoErrors();
		verifyForward(ActionForwards.load_success.toString());
		assertNotNull(SessionUtils.getAttribute(
				LoanConstants.MULTIPLE_LOANS_OFFICES_LIST, request));
		assertNotNull(SessionUtils.getAttribute(
				LoanConstants.IS_CENTER_HEIRARCHY_EXISTS, request));
		assertNotNull(SessionUtils.getAttribute(
				LoanConstants.MULTIPLE_LOANS_LOAN_OFFICERS_LIST, request));
	}

	public void testGetCentersWithoutOfficeAndLoanOfficer() throws Exception {
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "getCenters");
		actionPerform();
		verifyActionErrors(new String[] { LoanConstants.MANDATORY_SELECT,
				LoanConstants.MANDATORY_SELECT });
		verifyInputForward();
	}

	public void testGetCenters() throws Exception {
		createInitialCustomers();
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "load");
		actionPerform();
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "getLoanOfficers");
		addRequestParameter("branchOfficeId", "1");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "getCenters");
		addRequestParameter("branchOfficeId", "1");
		addRequestParameter("loanOfficerId", "1");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		performNoErrors();
		verifyForward(ActionForwards.load_success.toString());
		assertNotNull(SessionUtils.getAttribute(
				LoanConstants.MULTIPLE_LOANS_OFFICES_LIST, request));
		assertNotNull(SessionUtils.getAttribute(
				LoanConstants.IS_CENTER_HEIRARCHY_EXISTS, request));
		assertNotNull(SessionUtils.getAttribute(
				LoanConstants.MULTIPLE_LOANS_LOAN_OFFICERS_LIST, request));
		assertNotNull(SessionUtils.getAttribute(
				LoanConstants.MULTIPLE_LOANS_CENTERS_LIST, request));
	}

	public void testGetPrdOfferingsWithoutOfficeAndLoanOfficerAndCenter()
			throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "getPrdOfferings");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(LoanConstants.IS_CENTER_HEIRARCHY_EXISTS,
				Constants.YES, request);
		actionPerform();
		verifyActionErrors(new String[] { LoanConstants.MANDATORY_SELECT,
				LoanConstants.MANDATORY_SELECT, LoanConstants.MANDATORY_SELECT });
		verifyInputForward();
	}

	public void testGetPrdOfferings() throws Exception {
		createInitialCustomers();
		LoanOfferingBO loanOffering1 = getLoanOffering("Loan Offering123",
				"LOOF", ApplicableTo.CLIENTS, WEEKLY, EVERY_WEEK);
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "load");
		actionPerform();
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "getLoanOfficers");
		addRequestParameter("branchOfficeId", "1");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "getCenters");
		addRequestParameter("branchOfficeId", "1");
		addRequestParameter("loanOfficerId", "1");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "getPrdOfferings");
		addRequestParameter("loanOfficerId", "1");
		addRequestParameter("branchOfficeId", "1");
		addRequestParameter("centerId", center.getCustomerId().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		performNoErrors();
		verifyForward(ActionForwards.load_success.toString());
		assertNotNull(SessionUtils.getAttribute(
				LoanConstants.MULTIPLE_LOANS_OFFICES_LIST, request));
		assertNotNull(SessionUtils.getAttribute(
				LoanConstants.IS_CENTER_HEIRARCHY_EXISTS, request));
		assertNotNull(SessionUtils.getAttribute(
				LoanConstants.MULTIPLE_LOANS_LOAN_OFFICERS_LIST, request));
		assertNotNull(SessionUtils.getAttribute(
				LoanConstants.MULTIPLE_LOANS_CENTERS_LIST, request));
		assertNotNull(SessionUtils.getAttribute(LoanConstants.LOANPRDOFFERINGS,
				request));
		TestObjectFactory.removeObject(loanOffering1);
	}

	public void testGetPrdOfferingsApplicableForCustomer() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createInitialCustomers();
		LoanOfferingBO loanOffering1 = getLoanOffering("fdfsdfsd", "ertg",
				ApplicableTo.GROUPS, WEEKLY, EVERY_WEEK);
		LoanOfferingBO loanOffering2 = getLoanOffering("rwrfdb", "1qsd",
				ApplicableTo.GROUPS, WEEKLY, EVERY_WEEK);
		LoanOfferingBO loanOffering3 = getLoanOffering("mksgfgfd", "9u78",
				ApplicableTo.CLIENTS, WEEKLY, EVERY_WEEK);

		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "getPrdOfferings");
		addRequestParameter("loanOfficerId", "1");
		addRequestParameter("branchOfficeId", "1");
		addRequestParameter("centerId", center.getCustomerId().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(LoanConstants.IS_CENTER_HEIRARCHY_EXISTS,
				Constants.YES, request);
		performNoErrors();
		verifyForward(ActionForwards.load_success.toString());
		assertEquals(1, ((List<LoanOfferingBO>) SessionUtils.getAttribute(
				LoanConstants.LOANPRDOFFERINGS, request)).size());

		TestObjectFactory.removeObject(loanOffering1);
		TestObjectFactory.removeObject(loanOffering2);
		TestObjectFactory.removeObject(loanOffering3);
	}

	public void testGetPrdOfferingsApplicableForCustomersWithMeeting()
			throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createInitialCustomers();
		LoanOfferingBO loanOffering1 = getLoanOffering("vcxvxc", "a123",
				ApplicableTo.CLIENTS, WEEKLY, EVERY_WEEK);
		LoanOfferingBO loanOffering2 = getLoanOffering("fgdsghdh", "4fdh",
				ApplicableTo.CLIENTS, WEEKLY, EVERY_WEEK);
		LoanOfferingBO loanOffering3 = getLoanOffering("mgkkkj", "6tyu",
				ApplicableTo.GROUPS, WEEKLY, EVERY_WEEK);
		LoanOfferingBO loanOffering4 = getLoanOffering("aq12sfdsf", "456j",
				ApplicableTo.CLIENTS, MONTHLY, EVERY_MONTH);
		LoanOfferingBO loanOffering5 = getLoanOffering("bdfhgfh", "6yu7",
				ApplicableTo.CLIENTS, WEEKLY, (short)3);

		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "getPrdOfferings");
		addRequestParameter("branchOfficeId", "1");
		addRequestParameter("loanOfficerId", "1");
		addRequestParameter("centerId", center.getCustomerId().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(LoanConstants.IS_CENTER_HEIRARCHY_EXISTS,
				Constants.YES, request);
		/* Why two calls to actionPerform?  Are we trying to test the
		   case where the user clicks twice or is this just a mistake? */
		actionPerform();
		performNoErrors();
		verifyForward(ActionForwards.load_success.toString());
		assertEquals(3, ((List<LoanOfferingBO>) SessionUtils.getAttribute(
				LoanConstants.LOANPRDOFFERINGS, request)).size());

		TestObjectFactory.removeObject(loanOffering1);
		TestObjectFactory.removeObject(loanOffering2);
		TestObjectFactory.removeObject(loanOffering3);
		TestObjectFactory.removeObject(loanOffering4);
		TestObjectFactory.removeObject(loanOffering5);
	}

	public void testGetWithoutData() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "get");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(LoanConstants.IS_CENTER_HEIRARCHY_EXISTS,
				Constants.YES, request);
		actionPerform();
		verifyActionErrors(new String[] {
				LoanConstants.LOANOFFERINGNOTSELECTEDERROR,
				LoanConstants.MANDATORY_SELECT, LoanConstants.MANDATORY_SELECT,
				LoanConstants.MANDATORY_SELECT });
		verifyInputForward();
	}

	public void testGetWithoutClients() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		LoanOfferingBO loanOffering1 = getLoanOffering("vcxvxc", "a123",
				ApplicableTo.CLIENTS, WEEKLY, EVERY_WEEK);
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "get");
		addRequestParameter("branchOfficeId", center.getOffice().getOfficeId()
				.toString());
		SessionUtils.setAttribute(LoanConstants.IS_CENTER_HEIRARCHY_EXISTS,
				Constants.YES, request);
		addRequestParameter("loanOfficerId", center.getPersonnel()
				.getPersonnelId().toString());
		addRequestParameter("centerId", center.getCustomerId().toString());
		addRequestParameter("prdOfferingId", loanOffering1.getPrdOfferingId()
				.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyActionErrors(new String[] { LoanConstants.NOSEARCHRESULTS });
		verifyForwardPath("/pages/application/loan/jsp/CreateMultipleLoanAccounts.jsp");
		TestObjectFactory.removeObject(loanOffering1);
	}

	public void testGet() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createInitialCustomers();
		LoanOfferingBO loanOffering = getLoanOffering("vcxvxc", "a123",
				ApplicableTo.CLIENTS, WEEKLY, EVERY_WEEK);
		loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "get");
		addRequestParameter("branchOfficeId", center.getOffice().getOfficeId()
				.toString());
		addRequestParameter("loanOfficerId", center.getPersonnel()
				.getPersonnelId().toString());
		addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId()
				.toString());
		SessionUtils.setAttribute(LoanConstants.IS_CENTER_HEIRARCHY_EXISTS,
				Constants.YES, request);
		addRequestParameter("centerId", center.getCustomerId().toString());
		addRequestParameter("centerSearchId", center.getSearchId().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		performNoErrors();
		verifyForward(ActionForwards.get_success.toString());

		assertEquals(1, ((List) SessionUtils.getAttribute(
				LoanConstants.MULTIPLE_LOANS_CLIENTS_LIST, request)).size());
		// this retrieve the loan purposes so this is 129 if empty lookup name are removed
		assertEquals(131, ((List) SessionUtils.getAttribute(
				MasterConstants.BUSINESS_ACTIVITIES, request)).size());
		assertNotNull(SessionUtils.getAttribute(LoanConstants.LOANOFFERING,
				request));
		assertNotNull(SessionUtils.getAttribute(
				CustomerConstants.PENDING_APPROVAL_DEFINED, request));
		TestObjectFactory.removeObject(loanOffering);
	}

	public void testCreateWithouSelectingClient() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createInitialCustomers();
		LoanOfferingBO loanOffering = getLoanOffering("vcxvxc", "a123",
				ApplicableTo.CLIENTS, WEEKLY, EVERY_WEEK);
		loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "get");
		addRequestParameter("branchOfficeId", center.getOffice().getOfficeId()
				.toString());
		addRequestParameter("loanOfficerId", center.getPersonnel()
				.getPersonnelId().toString());
		addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId()
				.toString());
		SessionUtils.setAttribute(LoanConstants.IS_CENTER_HEIRARCHY_EXISTS,
				Constants.YES, request);
		addRequestParameter("centerId", center.getCustomerId().toString());
		addRequestParameter("centerSearchId", center.getSearchId().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("clientDetails[0].loanAmount", loanOffering
				.getDefaultLoanAmount().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("stateSelected", "1");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("method", "create");
		addRequestParameter("stateSelected", "1");
		actionPerform();
		verifyActionErrors(new String[] { LoanExceptionConstants.SELECT_ATLEAST_ONE_RECORD });
		verifyInputForward();
		TestObjectFactory.removeObject(loanOffering);
	}

	public void testCreate() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createInitialCustomers();
		LoanOfferingBO loanOffering = getLoanOffering("vcxvxc", "a123",
				ApplicableTo.CLIENTS, WEEKLY, EVERY_WEEK);
		loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "get");
		addRequestParameter("branchOfficeId", center.getOffice().getOfficeId()
				.toString());
		addRequestParameter("loanOfficerId", center.getPersonnel()
				.getPersonnelId().toString());
		addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId()
				.toString());
		SessionUtils.setAttribute(LoanConstants.IS_CENTER_HEIRARCHY_EXISTS,
				Constants.YES, request);
		addRequestParameter("centerId", center.getCustomerId().toString());
		addRequestParameter("centerSearchId", center.getSearchId().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("clientDetails[0].loanAmount", loanOffering
				.getDefaultLoanAmount().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("stateSelected", "1");
		addRequestParameter("clients[0]", client.getCustomerId().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("method", "create");
		addRequestParameter("stateSelected", "1");
		performNoErrors();
		verifyForward(ActionForwards.create_success.toString());

		List<String> accountNumbers = ((List<String>) request
				.getAttribute(LoanConstants.ACCOUNTS_LIST));
		assertEquals(1, accountNumbers.size());
		LoanBO loan = new LoanBusinessService().findBySystemId(accountNumbers
				.get(0));
		assertEquals(loanOffering.getDefaultLoanAmount().toString(), loan
				.getLoanAmount().toString());
		assertEquals(loanOffering.getDefInterestRate(), loan.getInterestRate());
		assertEquals(loanOffering.getDefNoInstallments(), loan
				.getNoOfInstallments());
		assertEquals(Short.valueOf("0"), loan.getGracePeriodDuration());
		assertEquals(Short.valueOf("1"), loan.getAccountState().getId());
		assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		TestObjectFactory.cleanUp(loan);
	}

	public void testCreateWithoutPermission() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		UserContext userContext = TestUtils.makeUser();
		userContext.setRoles(new HashSet());
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		createInitialCustomers();
		LoanOfferingBO loanOffering = getLoanOffering("fdfsdfsd", "ertg",
				ApplicableTo.GROUPS, WEEKLY, EVERY_WEEK);
		loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "get");
		addRequestParameter("branchOfficeId", center.getOffice().getOfficeId()
				.toString());
		addRequestParameter("loanOfficerId", center.getPersonnel()
				.getPersonnelId().toString());
		addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId()
				.toString());
		SessionUtils.setAttribute(LoanConstants.IS_CENTER_HEIRARCHY_EXISTS,
				Constants.YES, request);
		addRequestParameter("centerId", center.getCustomerId().toString());
		addRequestParameter("centerSearchId", center.getSearchId().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		addRequestParameter("clients[0]", client.getCustomerId().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "create");
		addRequestParameter("stateSelected", "1");
		actionPerform();
		verifyActionErrors(new String[] { SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED });
		verifyForward(ActionForwards.create_failure.toString());
		TestObjectFactory.removeObject(loanOffering);
	}

	public void testCancel() {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "cancel");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward(ActionForwards.cancel_success.toString());
	}

	public void testValidate() throws Exception {
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "validate");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.load_success.toString());
	}

	public void testValidateForPreview() throws Exception {
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "validate");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		request.setAttribute("methodCalled", Methods.load.toString());

		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.load_success.toString());
	}

	public void testVaildateForCreate() throws Exception {
		setRequestPathInfo("/multipleloansaction.do");
		addRequestParameter("method", "validate");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		request.setAttribute("methodCalled", Methods.create.toString());

		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.get_success.toString());
	}

	private LoanOfferingBO getLoanOffering(String name, String shortName,
			ApplicableTo applicableTo, RecurrenceType meetingFrequency, 
			short recurAfter) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeeting(meetingFrequency, recurAfter, CUSTOMER_MEETING, WeekDay.MONDAY));
		Date currentDate = new Date(System.currentTimeMillis());
		return TestObjectFactory.createLoanOffering(name, shortName, 
				applicableTo, currentDate, PrdStatus.LOAN_ACTIVE,
				300.0, 1.2, (short)3, 
				InterestType.FLAT, true, true, 
				meeting);
	}

	private void createInitialCustomers() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
	}
}
