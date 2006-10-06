package org.mifos.application.accounts.loan.struts.action;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountNotesEntity;
import org.mifos.application.accounts.loan.business.LoanActivityEntity;
import org.mifos.application.accounts.loan.business.LoanActivityView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.LoanOfferingFeesEntity;
import org.mifos.application.productdefinition.util.helpers.PrdApplicableMaster;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.components.audit.util.helpers.AuditConstants;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidUserException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestLoanAccountAction extends MifosMockStrutsTestCase {

	private UserContext userContext;

	protected AccountBO accountBO = null;

	protected CustomerBO center = null;

	protected CustomerBO group = null;

	private CustomerBO client = null;

	private String flowKey;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
					.getPath());
			setConfigFile(ResourceLoader.getURI(
					"org/mifos/application/accounts/struts-config.xml")
					.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext",
				TestObjectFactory.getActivityContext());
		Flow flow = new Flow();
		flowKey = String.valueOf(System.currentTimeMillis());
		FlowManager flowManager = new FlowManager();
		flowManager.addFLow(flowKey, flow);
		request.getSession(false).setAttribute(Constants.FLOWMANAGER,
				flowManager);

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
	
	public void testLoadChangeLog(){
		accountBO = getLoanAccount();
		AuditLog auditLog = new AuditLog(accountBO.getAccountId(), EntityType.LOAN.getValue(), "Mifos", new java.sql.Date(System.currentTimeMillis()),
				Short.valueOf("3"));
		Set<AuditLogRecord> auditLogRecords = new HashSet<AuditLogRecord>();
		AuditLogRecord auditLogRecord = new AuditLogRecord("ColumnName_1",
				"test_1", "new_test_1", auditLog);
		auditLogRecords.add(auditLogRecord);
		auditLog.addAuditLogRecords(auditLogRecords);
		auditLog.save();
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("method", "loadChangeLog");
		addRequestParameter("entityType", "Loan");
		addRequestParameter("entityId", accountBO.getAccountId().toString());
		actionPerform();
		assertEquals(1,((List)request.getSession().getAttribute(AuditConstants.AUDITLOGRECORDS)).size());
		verifyForward("viewLoanChangeLog");
		TestObjectFactory.cleanUpChangeLog();
	}
	
	public void testCancelChangeLog(){
		accountBO = getLoanAccount();
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("method", "cancelChangeLog");
		addRequestParameter("entityType", "Loan");
		actionPerform();
		verifyForward("cancelLoanChangeLog");
	}

	public void testGetAllActivity() {
		try {
			request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
			Date startDate = new Date(System.currentTimeMillis());
			accountBO = getLoanAccount(Short.valueOf("3"), startDate, 1);
			LoanBO loan = (LoanBO) accountBO;
			setRequestPathInfo("/loanAccountAction.do");
			addRequestParameter("method", "getAllActivity");
			addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
			addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
					.getAttribute(Constants.CURRENTFLOWKEY));
			actionPerform();
			verifyForward("getAllActivity_success");
			assertEquals(1, ((List<LoanActivityView>) SessionUtils
					.getAttribute(LoanConstants.LOAN_ALL_ACTIVITY_VIEW, request
							.getSession())).size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testGetInstallmentDetails() {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(Short.valueOf("3"), startDate, 1);
		LoanBO loan = (LoanBO) accountBO;
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "getInstallmentDetails");
		addRequestParameter("accountId", String.valueOf(loan.getAccountId()));
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward("viewInstmentDetails_success");
	}

	public void testGet() throws Exception {
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(Short.valueOf("3"), startDate, 1);
		LoanBO loan = (LoanBO) accountBO;

		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "get");
		addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
		actionPerform();
		verifyForward("get_success");

		assertEquals(0, loan.getPerformanceHistory().getNoOfPayments()
				.intValue());
		assertEquals(((LoanBO) accountBO).getTotalAmountDue()
				.getAmountDoubleValue(), 212.0);
		modifyActionDateForFirstInstallment();
		assertEquals("Total no. of notes should be 5", 5, accountBO
				.getAccountNotes().size());
		assertEquals("Total no. of recent notes should be 3", 3,
				((List<AccountNotesEntity>) SessionUtils.getAttribute(
						LoanConstants.NOTES, request)).size());
	}

	public void testGetWithPayment() throws Exception {
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(Short.valueOf("3"), startDate, 1);
		disburseLoan(startDate);
		LoanBO loan = (LoanBO) accountBO;

		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "get");
		addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
		actionPerform();
		verifyForward("get_success");

		assertEquals("Total no. of notes should be 5", 5, accountBO
				.getAccountNotes().size());
		assertEquals("Total no. of recent notes should be 3", 3,
				((List<AccountNotesEntity>) SessionUtils.getAttribute(
						LoanConstants.NOTES, request)).size());

		assertEquals("Last payment action should be 'PAYMENT'",
				AccountActionTypes.DISBURSAL.getValue(), SessionUtils
						.getAttribute(AccountConstants.LAST_PAYMENT_ACTION,
								request));
		client = (CustomerBO) HibernateUtil.getSessionTL().get(
				CustomerBO.class, client.getCustomerId());
		group = (CustomerBO) HibernateUtil.getSessionTL().get(CustomerBO.class,
				group.getCustomerId());
		center = (CustomerBO) HibernateUtil.getSessionTL().get(
				CustomerBO.class, center.getCustomerId());
	}

	public void testGetLoanRepaymentSchedule() {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "getLoanRepaymentSchedule");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward(ActionForwards.getLoanRepaymentSchedule.toString());
	}

	public void testViewStatusHistory() {
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(Short.valueOf("3"), startDate, 1);
		LoanBO loan = (LoanBO) accountBO;

		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "get");
		addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
		actionPerform();

		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "viewStatusHistory");
		actionPerform();
		verifyForward(ActionForwards.viewStatusHistory.toString());
	}

	public void testGetPrdOfferingsWithoutCustomer() throws Exception {
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "getPrdOfferings");
		actionPerform();
		verifyActionErrors(new String[] { LoanConstants.CUSTOMERNOTSELECTEDERROR });
		verifyInputForward();
	}

	public void testGetPrdOfferings() throws Exception {

		createInitialObjects();
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "getPrdOfferings");
		addRequestParameter("customerId", group.getCustomerId().toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.getPrdOfferigs_success.toString());
		assertEquals("Group", ((CustomerBO) SessionUtils.getAttribute(
				LoanConstants.LOANACCOUNTOWNER, request)).getDisplayName());
	}

	public void testGetPrdOfferingsApplicableForCustomer() throws Exception {
		createInitialObjects();
		LoanOfferingBO loanOffering1 = getLoanOffering("fdfsdfsd", "ertg",
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		LoanOfferingBO loanOffering2 = getLoanOffering("rwrfdb", "1qsd",
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		LoanOfferingBO loanOffering3 = getLoanOffering("mksgfgfd", "9u78",
				PrdApplicableMaster.CLIENTS.getValue().toString(), 1, 1);

		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "getPrdOfferings");
		addRequestParameter("customerId", group.getCustomerId().toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.getPrdOfferigs_success.toString());

		assertEquals("Group", ((CustomerBO) SessionUtils.getAttribute(
				LoanConstants.LOANACCOUNTOWNER, request)).getDisplayName());
		assertEquals(2, ((List<LoanOfferingBO>) SessionUtils.getAttribute(
				LoanConstants.LOANPRDOFFERINGS, request)).size());

		TestObjectFactory.removeObject(loanOffering1);
		TestObjectFactory.removeObject(loanOffering2);
		TestObjectFactory.removeObject(loanOffering3);
	}

	public void testGetPrdOfferingsApplicableForCustomersWithMeeting()
			throws Exception {
		createInitialObjects();
		LoanOfferingBO loanOffering1 = getLoanOffering("vcxvxc", "a123",
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		LoanOfferingBO loanOffering2 = getLoanOffering("fgdsghdh", "4fdh",
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		LoanOfferingBO loanOffering3 = getLoanOffering("mgkkkj", "6tyu",
				PrdApplicableMaster.CLIENTS.getValue().toString(), 1, 1);
		LoanOfferingBO loanOffering4 = getLoanOffering("aq12sfdsf", "456j",
				PrdApplicableMaster.GROUPS.getValue().toString(), 2, 1);
		LoanOfferingBO loanOffering5 = getLoanOffering("bdfhgfh", "6yu7",
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 3);

		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "getPrdOfferings");
		addRequestParameter("customerId", group.getCustomerId().toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.getPrdOfferigs_success.toString());

		assertEquals("Group", ((CustomerBO) SessionUtils.getAttribute(
				LoanConstants.LOANACCOUNTOWNER, request)).getDisplayName());
		assertEquals(3, ((List<LoanOfferingBO>) SessionUtils.getAttribute(
				LoanConstants.LOANPRDOFFERINGS, request)).size());

		TestObjectFactory.removeObject(loanOffering1);
		TestObjectFactory.removeObject(loanOffering2);
		TestObjectFactory.removeObject(loanOffering3);
		TestObjectFactory.removeObject(loanOffering4);
		TestObjectFactory.removeObject(loanOffering5);
	}

	public void testLoadWithoutCustomerAndPrdOfferingId() throws Exception {
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "load");
		actionPerform();
		verifyActionErrors(new String[] {
				LoanConstants.CUSTOMERNOTSELECTEDERROR,
				LoanConstants.LOANOFFERINGNOTSELECTEDERROR });
		verifyInputForward();
	}

	public void testLoadWithoutCustomer() throws Exception {
		LoanOfferingBO loanOffering = getLoanOffering("fdfsdfsd", "ertg",
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId()
				.toString());
		actionPerform();
		verifyActionErrors(new String[] { LoanConstants.CUSTOMERNOTSELECTEDERROR });
		verifyInputForward();

		TestObjectFactory.removeObject(loanOffering);
	}

	public void testLoadWithoutPrdOfferingId() throws Exception {
		createInitialObjects();

		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", group.getCustomerId().toString());
		addRequestParameter("prdOfferingId", "");
		actionPerform();
		verifyActionErrors(new String[] { LoanConstants.LOANOFFERINGNOTSELECTEDERROR });
		verifyInputForward();
	}

	public void testLoad() throws Exception {
		createInitialObjects();
		LoanOfferingBO loanOffering = getLoanOffering("fdfsdfsd", "ertg",
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "getPrdOfferings");
		addRequestParameter("customerId", group.getCustomerId().toString());
		actionPerform();

		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("method", "load");
		addRequestParameter("customerId", group.getCustomerId().toString());
		addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId()
				.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());
		assertNotNull(SessionUtils.getAttribute(LoanConstants.LOANOFFERING,
				request));
		assertNotNull(SessionUtils.getAttribute(LoanConstants.LOANFUNDS,
				request));

		TestObjectFactory.removeObject(loanOffering);
	}

	public void testLoadForMasterData() throws Exception {
		createInitialObjects();
		LoanOfferingBO loanOffering = getLoanOffering("fdfsdfsd", "ertg",
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "getPrdOfferings");
		addRequestParameter("customerId", group.getCustomerId().toString());
		actionPerform();

		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("method", "load");
		addRequestParameter("customerId", group.getCustomerId().toString());
		addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId()
				.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());

		assertEquals(2, ((List) SessionUtils.getAttribute(
				MasterConstants.COLLATERAL_TYPES, request)).size());
		assertEquals(129, ((List) SessionUtils.getAttribute(
				MasterConstants.BUSINESS_ACTIVITIES, request)).size());

		TestObjectFactory.removeObject(loanOffering);
	}

	public void testLoadWithFee() throws Exception {
		createInitialObjects();
		LoanOfferingBO loanOffering = getLoanOffering("fdfsdfsd", "ertg",
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		List<FeeBO> fees = getFee();
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "getPrdOfferings");
		addRequestParameter("customerId", group.getCustomerId().toString());
		actionPerform();

		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("method", "load");
		addRequestParameter("customerId", group.getCustomerId().toString());
		addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId()
				.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());
		LoanAccountActionForm loanActionForm = (LoanAccountActionForm) request
				.getSession().getAttribute("loanAccountActionForm");
		assertEquals(2, ((List) SessionUtils.getAttribute(
				LoanConstants.ADDITIONAL_FEES_LIST, request)).size());
		assertEquals(loanOffering.getDefaultLoanAmount().toString(),
				loanActionForm.getLoanAmount());

		assertEquals(loanOffering.getDefNoInstallments().toString(),
				loanActionForm.getNoOfInstallments());
		assertEquals(loanOffering.getDefInterestRate().toString(),
				loanActionForm.getInterestRate());
		assertEquals(loanOffering.isIntDedDisbursement(), loanActionForm
				.isInterestDedAtDisbValue());
		assertEquals(loanOffering.getGracePeriodDuration().toString(),
				loanActionForm.getGracePeriodDuration());
		assertEquals(DateHelper
				.getCurrentDate(((UserContext) request.getSession()
						.getAttribute("UserContext")).getPereferedLocale()),
				loanActionForm.getDisbursementDate());

		TestObjectFactory.removeObject((LoanOfferingBO) TestObjectFactory
				.getObject(LoanOfferingBO.class, loanOffering
						.getPrdOfferingId()));
		for (FeeBO fee : fees) {
			TestObjectFactory.cleanUp((FeeBO) TestObjectFactory.getObject(
					FeeBO.class, fee.getFeeId()));
		}

		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
	}

	public void testLoadWithFeeForLoanOffering() throws Exception {
		createInitialObjects();
		List<FeeBO> fees = getFee();
		LoanOfferingBO loanOffering = getLoanOffering("fdfsdfsd", "ertg",
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		loanOffering.addPrdOfferingFee(new LoanOfferingFeesEntity(loanOffering,
				fees.get(0)));
		TestObjectFactory.updateObject(loanOffering);
		HibernateUtil.closeSession();

		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "getPrdOfferings");
		addRequestParameter("customerId", group.getCustomerId().toString());
		actionPerform();

		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("method", "load");
		addRequestParameter("customerId", group.getCustomerId().toString());
		addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId()
				.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());
		LoanAccountActionForm loanActionForm = (LoanAccountActionForm) request
				.getSession().getAttribute("loanAccountActionForm");
		assertEquals(1, ((List) SessionUtils.getAttribute(
				LoanConstants.ADDITIONAL_FEES_LIST, request)).size());
		assertEquals(1, loanActionForm.getDefaultFees().size());

		assertEquals(loanOffering.getDefaultLoanAmount().toString(),
				loanActionForm.getLoanAmount());

		assertEquals(loanOffering.getDefNoInstallments().toString(),
				loanActionForm.getNoOfInstallments());
		assertEquals(loanOffering.getDefInterestRate().toString(),
				loanActionForm.getInterestRate());
		assertEquals(loanOffering.isIntDedDisbursement(), loanActionForm
				.isInterestDedAtDisbValue());
		assertEquals(loanOffering.getGracePeriodDuration().toString(),
				loanActionForm.getGracePeriodDuration());
		assertEquals(DateHelper
				.getCurrentDate(((UserContext) request.getSession()
						.getAttribute("UserContext")).getPereferedLocale()),
				loanActionForm.getDisbursementDate());

		TestObjectFactory.removeObject((LoanOfferingBO) TestObjectFactory
				.getObject(LoanOfferingBO.class, loanOffering
						.getPrdOfferingId()));
		for (FeeBO fee : fees) {
			TestObjectFactory.cleanUp((FeeBO) TestObjectFactory.getObject(
					FeeBO.class, fee.getFeeId()));
		}

		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
	}

	public void testSchedulePreview() throws Exception {
		createInitialObjects();
		LoanOfferingBO loanOffering = getLoanOffering("fdfsdfsd", "ertg",
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		List<FeeBO> fees = getFee();
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "getPrdOfferings");
		addRequestParameter("customerId", group.getCustomerId().toString());
		actionPerform();

		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("method", "load");
		addRequestParameter("customerId", group.getCustomerId().toString());
		addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId()
				.toString());
		actionPerform();
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("loanAmount", loanOffering.getDefaultLoanAmount()
				.toString());
		addRequestParameter("interestRate", loanOffering.getDefInterestRate()
				.toString());
		addRequestParameter("noOfInstallments", loanOffering
				.getDefNoInstallments().toString());
		addRequestParameter("disbursementDate", DateHelper
				.getCurrentDate(((UserContext) request.getSession()
						.getAttribute("UserContext")).getPereferedLocale()));
		addRequestParameter("gracePeriodDuration", "1");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("method", "schedulePreview");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.schedulePreview_success.toString());

		LoanBO loan = (LoanBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		assertNotNull(loan);

		TestObjectFactory.removeObject((LoanOfferingBO) TestObjectFactory
				.getObject(LoanOfferingBO.class, loanOffering
						.getPrdOfferingId()));
		for (FeeBO fee : fees) {
			TestObjectFactory.cleanUp((FeeBO) TestObjectFactory.getObject(
					FeeBO.class, fee.getFeeId()));
		}

		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());

	}

	public void testSchedulePreviewWithoutData() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createInitialObjects();
		LoanOfferingBO loanOffering = getLoanOffering("fdfsdfsd", "ertg",
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		SessionUtils.setAttribute(LoanConstants.LOANOFFERING, loanOffering,
				request);
		SessionUtils.setAttribute(LoanConstants.LOANFUNDS,
				new ArrayList<FundBO>(), request);

		SessionUtils.setAttribute(LoanConstants.LOANACCOUNTOWNER, group,
				request);
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("method", "schedulePreview");

		actionPerform();

		verifyActionErrors(new String[] { "errors.defMinMax",
				"errors.defMinMax", "errors.defMinMax",
				"errors.validandmandatory", "errors.graceper" });
		verifyInputForward();

		TestObjectFactory.removeObject((LoanOfferingBO) TestObjectFactory
				.getObject(LoanOfferingBO.class, loanOffering
						.getPrdOfferingId()));
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
	}

	public void testSchedulePreviewWithDataWithNoGracePer() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createInitialObjects();
		LoanOfferingBO loanOffering = getLoanOffering("fdfsdfsd", "ertg",
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		SessionUtils.setAttribute(LoanConstants.LOANOFFERING, loanOffering,
				request);
		SessionUtils.setAttribute(LoanConstants.LOANFUNDS,
				new ArrayList<FundBO>(), request);
		SessionUtils.setAttribute(LoanConstants.LOANACCOUNTOWNER, group,
				request);
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("loanAmount", loanOffering.getDefaultLoanAmount()
				.toString());
		addRequestParameter("interestRate", loanOffering.getDefInterestRate()
				.toString());
		addRequestParameter("noOfInstallments", loanOffering
				.getDefNoInstallments().toString());
		addRequestParameter("disbursementDate", DateHelper
				.getCurrentDate(((UserContext) request.getSession()
						.getAttribute("UserContext")).getPereferedLocale()));

		addRequestParameter("method", "schedulePreview");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyActionErrors(new String[] { "errors.graceper" });
		verifyInputForward();

		TestObjectFactory.removeObject((LoanOfferingBO) TestObjectFactory
				.getObject(LoanOfferingBO.class, loanOffering
						.getPrdOfferingId()));
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
	}

	public void testSchedulePreviewWithData() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createInitialObjects();
		LoanOfferingBO loanOffering = getLoanOffering("fdfsdfsd", "ertg",
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		SessionUtils.setAttribute(LoanConstants.LOANOFFERING, loanOffering,
				request);
		SessionUtils.setAttribute(LoanConstants.LOANFUNDS,
				new ArrayList<FundBO>(), request);
		SessionUtils.setAttribute(LoanConstants.LOANACCOUNTOWNER, group,
				request);
		SessionUtils.setAttribute(MasterConstants.COLLATERAL_TYPES,
				new ArrayList<MasterDataEntity>(), request);
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("loanAmount", loanOffering.getDefaultLoanAmount()
				.toString());
		addRequestParameter("interestRate", loanOffering.getDefInterestRate()
				.toString());
		addRequestParameter("noOfInstallments", loanOffering
				.getDefNoInstallments().toString());
		addRequestParameter("disbursementDate", DateHelper
				.getCurrentDate(((UserContext) request.getSession()
						.getAttribute("UserContext")).getPereferedLocale()));
		addRequestParameter("gracePeriodDuration", "1");
		addRequestParameter("method", "schedulePreview");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.schedulePreview_success.toString());
		TestObjectFactory.removeObject((LoanOfferingBO) TestObjectFactory
				.getObject(LoanOfferingBO.class, loanOffering
						.getPrdOfferingId()));
	}

	public void testSchedulePreviewWithDataForIntDedAtDisb() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createInitialObjects();
		LoanOfferingBO loanOffering = getLoanOffering("fdfsdfsd", "ertg",
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		SessionUtils.setAttribute(LoanConstants.LOANOFFERING, loanOffering,
				request);
		SessionUtils.setAttribute(LoanConstants.LOANFUNDS,
				new ArrayList<FundBO>(), request);
		SessionUtils.setAttribute(LoanConstants.LOANACCOUNTOWNER, group,
				request);
		SessionUtils.setAttribute(MasterConstants.COLLATERAL_TYPES,
				new ArrayList<MasterDataEntity>(), request);
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("loanAmount", loanOffering.getDefaultLoanAmount()
				.toString());
		addRequestParameter("interestRate", loanOffering.getDefInterestRate()
				.toString());
		addRequestParameter("noOfInstallments", loanOffering
				.getDefNoInstallments().toString());
		addRequestParameter("disbursementDate", DateHelper
				.getCurrentDate(((UserContext) request.getSession()
						.getAttribute("UserContext")).getPereferedLocale()));
		addRequestParameter("intDedDisbursement", "1");
		addRequestParameter("method", "schedulePreview");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.schedulePreview_success.toString());
		TestObjectFactory.removeObject((LoanOfferingBO) TestObjectFactory
				.getObject(LoanOfferingBO.class, loanOffering
						.getPrdOfferingId()));
	}

	public void testCreate() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createInitialObjects();
		LoanOfferingBO loanOffering = getLoanOffering("fdfsdfsd", "ertg",
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		SessionUtils.setAttribute(LoanConstants.LOANOFFERING, loanOffering,
				request);
		SessionUtils.setAttribute(LoanConstants.LOANFUNDS,
				new ArrayList<FundBO>(), request);
		SessionUtils.setAttribute(LoanConstants.LOANACCOUNTOWNER, group,
				request);
		SessionUtils.setAttribute(MasterConstants.COLLATERAL_TYPES,
				new ArrayList<MasterDataEntity>(), request);
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("loanAmount", loanOffering.getDefaultLoanAmount()
				.toString());
		addRequestParameter("interestRate", loanOffering.getDefInterestRate()
				.toString());
		addRequestParameter("noOfInstallments", loanOffering
				.getDefNoInstallments().toString());
		addRequestParameter("disbursementDate", DateHelper
				.getCurrentDate(((UserContext) request.getSession()
						.getAttribute("UserContext")).getPereferedLocale()));
		addRequestParameter("gracePeriodDuration", "0");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("method", "schedulePreview");
		actionPerform();
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("method", "create");
		addRequestParameter("stateSelected", "1");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.create_success.toString());
		LoanAccountActionForm actionForm = (LoanAccountActionForm) request
				.getSession().getAttribute("loanAccountActionForm");
		LoanBO loan = (LoanBO) TestObjectFactory.getObject(LoanBO.class,
				new Integer(actionForm.getAccountId()).intValue());
		assertEquals(loanOffering.getDefaultLoanAmount().toString(), loan
				.getLoanAmount().toString());

		assertEquals(loanOffering.getDefInterestRate(), loan.getInterestRate());
		assertEquals(loanOffering.getDefNoInstallments(), loan
				.getNoOfInstallments());
		assertEquals(new java.sql.Date(DateUtils
				.getCurrentDateWithoutTimeStamp().getTime()).toString(), loan
				.getDisbursementDate().toString());
		assertEquals(Short.valueOf("0"), loan.getGracePeriodDuration());
		assertEquals(Short.valueOf("1"), loan.getAccountState().getId());
		assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		TestObjectFactory.cleanUp(loan);
	}

	public void testManage() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(Short.valueOf("3"), startDate, 1);
		LoanBO loan = (LoanBO) accountBO;
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);

		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("method", "manage");
		actionPerform();
		verifyForward(ActionForwards.manage_success.toString());
		assertNotNull(SessionUtils.getAttribute(LoanConstants.LOANOFFERING,
				request));
		assertNotNull(SessionUtils.getAttribute(
				MasterConstants.COLLATERAL_TYPES, request));
		assertNotNull(SessionUtils.getAttribute(
				MasterConstants.BUSINESS_ACTIVITIES, request));
	}

	public void testManageWithoutFlow() throws Exception {
		try {
			request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
			Date startDate = new Date(System.currentTimeMillis());
			accountBO = getLoanAccount(Short.valueOf("3"), startDate, 1);
			LoanBO loan = (LoanBO) accountBO;
			SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
			setRequestPathInfo("/loanAccountAction.do");
			addRequestParameter("method", "manage");
			actionPerform();
		} catch (PageExpiredException pe) {
			assertTrue(true);
			assertEquals(ExceptionConstants.PAGEEXPIREDEXCEPTION, pe.getKey());
		}

	}

	public void testManagePreview() throws ServiceException,
			InvalidUserException, SystemException, ApplicationException {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(Short.valueOf("3"), startDate, 1);
		((LoanBO) accountBO).setBusinessActivityId(1);
		accountBO.update();
		HibernateUtil.commitTransaction();
		LoanBO loan = (LoanBO) accountBO;
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);

		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("method", "manage");
		actionPerform();

		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("method", "managePreview");
		addRequestParameter("loanAmount", loan.getLoanOffering()
				.getDefaultLoanAmount().toString());
		addRequestParameter("interestRate", loan.getLoanOffering()
				.getDefInterestRate().toString());
		addRequestParameter("noOfInstallments", loan.getLoanOffering()
				.getDefNoInstallments().toString());
		addRequestParameter("disbursementDate", DateHelper
				.getCurrentDate(((UserContext) request.getSession()
						.getAttribute("UserContext")).getPereferedLocale()));
		addRequestParameter("gracePeriodDuration", "1");
		addRequestParameter("intDedDisbursement", "1");
		actionPerform();
		verifyForward(ActionForwards.managepreview_success.toString());

		assertNotNull(SessionUtils.getAttribute(
				MasterConstants.COLLATERAL_TYPE_NAME, request));
		assertNotNull(SessionUtils.getAttribute(
				MasterConstants.BUSINESS_ACTIVITIE_NAME, request));
	}

	public void testManagePrevious() {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("method", "managePrevious");
		actionPerform();
		verifyForward(ActionForwards.manageprevious_success.toString());
	}

	public void testCancel() {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "cancel");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward(ActionForwards.loan_detail_page.toString());
	}

	public void testUpdateSuccessWithRegeneratingNewRepaymentSchedule()
			throws Exception {

		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		Date startDate = new Date(System.currentTimeMillis());
		String newDate = offSetCurrentDate(14, userContext.getPereferedLocale());
		accountBO = getLoanAccount(Short.valueOf("3"), startDate, 1);
		((LoanBO) accountBO).setBusinessActivityId(1);
		accountBO.changeStatus(AccountState.LOANACC_APPROVED.getValue(), null,
				"status changed");
		accountBO.update();

		HibernateUtil.commitTransaction();
		LoanBO loan = (LoanBO) accountBO;
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("method", "manage");
		actionPerform();
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("method", "managePreview");
		addRequestParameter("loanAmount", loan.getLoanOffering()
				.getDefaultLoanAmount().toString());
		addRequestParameter("interestRate", loan.getLoanOffering()
				.getDefInterestRate().toString());
		addRequestParameter("noOfInstallments", loan.getLoanOffering()
				.getDefNoInstallments().toString());
		addRequestParameter("disbursementDate", newDate);
		addRequestParameter("gracePeriodDuration", "0");
		addRequestParameter("intDedDisbursement", "1");
		actionPerform();
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("method", "update");
		addRequestParameter("collateralNote", "test");
		actionPerform();
		verifyForward(ActionForwards.update_success.toString());
		loan = (LoanBO) TestObjectFactory.getObject(LoanBO.class, loan
				.getAccountId());
		assertEquals("test", loan.getCollateralNote());
		assertEquals(300.0, loan.getLoanAmount().getAmountDoubleValue());
		assertTrue(loan.isInterestDeductedAtDisbursement());
		assertEquals(0, loan.getGracePeriodDuration().intValue());
		assertEquals(newDate, DateHelper.getUserLocaleDate(TestObjectFactory
				.getContext().getPereferedLocale(), DateHelper
				.toDatabaseFormat(loan.getAccountActionDate(Short.valueOf("1"))
						.getActionDate())));

	}

	public void testUpdateSuccessWithoutRegeneratingNewRepaymentSchedule()
			throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		accountBO = getLoanAccount();
		LoanBO loan = (LoanBO) accountBO;
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
		String newDate = offSetCurrentDate(6, userContext.getPereferedLocale());
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("method", "update");
		addRequestParameter("loanAmount", loan.getLoanOffering()
				.getDefaultLoanAmount().toString());
		addRequestParameter("interestRate", loan.getLoanOffering()
				.getDefInterestRate().toString());
		addRequestParameter("noOfInstallments", loan.getLoanOffering()
				.getDefNoInstallments().toString());
		addRequestParameter("disbursementDate", newDate);
		addRequestParameter("businessActivityId", "1");
		addRequestParameter("intDedDisbursement", "1");
		addRequestParameter("gracePeriodDuration", "1");
		addRequestParameter("collateralNote", "test");
		actionPerform();
		verifyForward(ActionForwards.update_success.toString());
	}

	private void modifyActionDateForFirstInstallment() throws Exception {
		LoanScheduleEntity installment = (LoanScheduleEntity) accountBO
				.getAccountActionDate((short) 1);
		installment.setPrincipal(new Money("20.0"));
		installment.setPenalty(new Money("5.0"));
		installment.setInterest(new Money("10.0"));
		installment.setActionDate(offSetCurrentDate(1));
		accountBO = saveAndFetch(accountBO);
	}

	private java.sql.Date offSetCurrentDate(int noOfDays) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day - noOfDays);
		return new java.sql.Date(currentDateCalendar.getTimeInMillis());
	}

	private AccountBO saveAndFetch(AccountBO account) throws Exception {
		AccountPersistence accountPersistence = new AccountPersistence();
		TestObjectFactory.updateObject(account);
		return accountPersistence.getAccount(account.getAccountId());
	}

	private AccountNotesEntity createAccountNotes(String comment,
			AccountBO account) {
		AccountNotesEntity accountNotes = new AccountNotesEntity(
				new java.sql.Date(System.currentTimeMillis()), comment,
				TestObjectFactory.getPersonnel(userContext.getId()), account);
		return accountNotes;
	}

	private void addNotes() {
		accountBO.addAccountNotes(createAccountNotes("Notes1", accountBO));
		TestObjectFactory.updateObject(accountBO);
		accountBO.addAccountNotes(createAccountNotes("Notes2", accountBO));
		TestObjectFactory.updateObject(accountBO);
		accountBO.addAccountNotes(createAccountNotes("Notes3", accountBO));
		TestObjectFactory.updateObject(accountBO);
		accountBO.addAccountNotes(createAccountNotes("Notes4", accountBO));
		TestObjectFactory.updateObject(accountBO);
		accountBO.addAccountNotes(createAccountNotes("Notes5", accountBO));
		TestObjectFactory.updateObject(accountBO);
	}

	private void disburseLoan(Date startDate) throws Exception {
		((LoanBO) accountBO).disburseLoan("1234", startDate,
				Short.valueOf("1"), accountBO.getPersonnel(), startDate, Short
						.valueOf("1"));
		HibernateUtil.commitTransaction();
	}

	private AccountBO getLoanAccount(Short accountSate, Date startDate,
			int disbursalType) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client",
				ClientConstants.STATUS_ACTIVE, "1.4.1.1", group, new Date(
						System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		accountBO = TestObjectFactory.createLoanAccountWithDisbursement(
				"99999999999", group, accountSate, startDate, loanOffering,
				disbursalType);
		LoanActivityEntity loanActivity = new LoanActivityEntity(accountBO,
				TestObjectFactory.getPersonnel(userContext.getId()), "testing",
				new Money("100"), new Money("100"), new Money("100"),
				new Money("100"), new Money("100"), new Money("100"),
				new Money("100"), new Money("100"), startDate);
		((LoanBO) accountBO).addLoanActivity(loanActivity);
		addNotes();
		TestObjectFactory.updateObject(accountBO);
		return accountBO;
	}

	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
	}

	private LoanOfferingBO getLoanOffering(String name, String shortName,
			String prdApplicableTo, int meetingFrequency, int recurAfter) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(meetingFrequency, recurAfter, 4, 2));
		Date currentDate = new Date(System.currentTimeMillis());
		return TestObjectFactory.createLoanOffering(name, shortName, Short
				.valueOf(prdApplicableTo), currentDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
	}

	private List<FeeBO> getFee() {
		List<FeeBO> fees = new ArrayList<FeeBO>();
		FeeBO fee1 = TestObjectFactory.createOneTimeAmountFee(
				"One Time Amount Fee", FeeCategory.LOAN, "120.0",
				FeePayment.TIME_OF_DISBURSMENT);
		FeeBO fee3 = TestObjectFactory.createPeriodicAmountFee("Periodic Fee",
				FeeCategory.LOAN, "10.0", RecurrenceType.WEEKLY, (short) 1);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		fees.add(fee1);
		fees.add(fee3);
		return fees;
	}

	private String getPrefferedDate(Date date) {
		Calendar currentDateCalendar = new GregorianCalendar();
		currentDateCalendar.setTime(date);
		return (currentDateCalendar.get(Calendar.DATE) + "/"
				+ currentDateCalendar.get(Calendar.MONTH) + 1)
				+ "/" + currentDateCalendar.get(Calendar.YEAR);

	}

	private String offSetCurrentDate(int noOfDays, Locale locale) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day + noOfDays);
		java.sql.Date currentDate = new java.sql.Date(currentDateCalendar
				.getTimeInMillis());
		SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateInstance(
				DateFormat.SHORT, locale);
		String userfmt = DateHelper
				.convertToCurrentDateFormat(((SimpleDateFormat) sdf)
						.toPattern());
		return DateHelper.convertDbToUserFmt(currentDate.toString(), userfmt);
	}

	private Date incrementCurrentDate(int noOfDays) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day + noOfDays);
		return DateUtils.getDateWithoutTimeStamp(currentDateCalendar
				.getTimeInMillis());
	}

	private void createInitialCustomers() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client",
				ClientConstants.STATUS_ACTIVE, "1.4.1.1", group, new Date(
						System.currentTimeMillis()));
	}

	private AccountBO getLoanAccount() {
		createInitialCustomers();
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting());
		return TestObjectFactory.createLoanAccount("42423142341", client, Short
				.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
	}
}
