package org.mifos.application.accounts.loan.struts.action;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountNotesEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.loan.business.LoanActivityEntity;
import org.mifos.application.accounts.loan.business.LoanActivityView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fund.util.valueobjects.Fund;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingFrequency;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.util.helpers.PrdApplicableMaster;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleException;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidUserException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
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

	protected void setUp() throws Exception {
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
					.getPath());
			setConfigFile(ResourceLoader.getURI(
					"org/mifos/framework/util/helpers/struts-config.xml")
					.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		userContext = new UserContext();
		userContext.setId(new Short("1"));
		userContext.setLocaleId(new Short("1"));
		Set<Short> set = new HashSet<Short>();
		set.add(Short.valueOf("1"));
		userContext.setRoles(set);
		userContext.setLevelId(Short.valueOf("2"));
		userContext.setName("mifos");
		userContext.setPereferedLocale(new Locale("en", "US"));
		userContext.setBranchId(new Short("1"));
		userContext.setBranchGlobalNum("0001");
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		
	}

	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGetAllActivity() {
		try {
			Date startDate = new Date(System.currentTimeMillis());
			accountBO = getLoanAccount(Short.valueOf("3"), startDate, 1);
			LoanBO loan = (LoanBO) accountBO;
			setRequestPathInfo("/loanAccountAction.do");
			addRequestParameter("method", "getAllActivity");
			addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
			actionPerform();
			verifyForward("getAllActivity_success");
			assertEquals(1,((List<LoanActivityView>)SessionUtils.getAttribute(LoanConstants.LOAN_ALL_ACTIVITY_VIEW,request.getSession())).size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testGetInstallmentDetails() {
			Date startDate = new Date(System.currentTimeMillis());
			accountBO = getLoanAccount(Short.valueOf("3"), startDate, 1);
			LoanBO loan = (LoanBO) accountBO;
			setRequestPathInfo("/loanAccountAction.do");
			addRequestParameter("method", "getInstallmentDetails");
			addRequestParameter("accountId", String
					.valueOf(loan.getAccountId()));
			actionPerform();
			verifyForward("viewInstmentDetails_success");
	}
	
	public void testGet() throws Exception{
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(Short.valueOf("3"), startDate, 1);
		LoanBO loan = (LoanBO) accountBO;
		
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "get");
		addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
		actionPerform();
		verifyForward("get_success");
		
		assertEquals(0,loan.getPerformanceHistory().getNoOfPayments().intValue());
		assertEquals(((LoanBO) accountBO).getTotalAmountDue().getAmountDoubleValue(), 212.0);
		modifyActionDateForFirstInstallment();
		assertEquals("Total no. of notes should be 5",5,accountBO.getAccountNotes().size());
		assertEquals("Total no. of recent notes should be 3",3,((List<AccountNotesEntity>)SessionUtils.getAttribute(LoanConstants.NOTES,request.getSession())).size());
	}
	
	public void testGetWithPayment() throws AccountException, SystemException, NumberFormatException, RepaymentScheduleException, FinancialException{
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(Short.valueOf("3"), startDate, 1);
		disburseLoan(startDate);
		LoanBO loan = (LoanBO) accountBO;
		
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "get");
		addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
		actionPerform();
		verifyForward("get_success");
		
		assertEquals("Total no. of notes should be 5",5,accountBO.getAccountNotes().size());
		assertEquals("Total no. of recent notes should be 3",3,((List<AccountNotesEntity>)SessionUtils.getAttribute(LoanConstants.NOTES,request.getSession())).size());
		
		assertEquals("Last payment action should be 'PAYMENT'",AccountActionTypes.DISBURSAL.getValue(),SessionUtils.getAttribute(AccountConstants.LAST_PAYMENT_ACTION,request.getSession()));
		client = (CustomerBO) HibernateUtil.getSessionTL().get(CustomerBO.class,client.getCustomerId());
		group = (CustomerBO) HibernateUtil.getSessionTL().get(CustomerBO.class,group.getCustomerId());
		center = (CustomerBO) HibernateUtil.getSessionTL().get(CustomerBO.class,center.getCustomerId());
	}
	
	public void testGetLoanRepaymentSchedule() {		
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "getLoanRepaymentSchedule");
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
	
	public void testGetPrdOfferingsWithoutCustomer() {
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "getPrdOfferings");
		actionPerform();
		verifyActionErrors(new String[]{LoanConstants.CUSTOMERNOTSELECTEDERROR});
		verifyInputForward();
	}
	
	public void testGetPrdOfferings() {

		createInitialObjects();

		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "getPrdOfferings");
		addRequestParameter("customerId", group.getCustomerId().toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.getPrdOfferigs_success.toString());
		
		assertEquals("Group", ((CustomerBO) request.getSession()
				.getAttribute(LoanConstants.LOANACCOUNTOWNER)).getDisplayName());
	}

	public void testGetPrdOfferingsApplicableForCustomer() {
		createInitialObjects();
		LoanOfferingBO loanOffering1 = getLoanOffering(
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		LoanOfferingBO loanOffering2 = getLoanOffering(
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		LoanOfferingBO loanOffering3 = getLoanOffering(
				PrdApplicableMaster.CLIENTS.getValue().toString(), 1, 1);

		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "getPrdOfferings");
		addRequestParameter("customerId", group.getCustomerId().toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.getPrdOfferigs_success.toString());

		assertEquals("Group", ((CustomerBO) request.getSession()
				.getAttribute(LoanConstants.LOANACCOUNTOWNER)).getDisplayName());
		assertEquals(2, ((List<LoanOfferingBO>) request.getSession()
				.getAttribute(LoanConstants.LOANPRDOFFERINGS)).size());

		TestObjectFactory.removeObject(loanOffering1);
		TestObjectFactory.removeObject(loanOffering2);
		TestObjectFactory.removeObject(loanOffering3);
	}

	public void testGetPrdOfferingsApplicableForCustomersWithMeeting() {
		createInitialObjects();
		LoanOfferingBO loanOffering1 = getLoanOffering(
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		LoanOfferingBO loanOffering2 = getLoanOffering(
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		LoanOfferingBO loanOffering3 = getLoanOffering(
				PrdApplicableMaster.CLIENTS.getValue().toString(), 1, 1);
		LoanOfferingBO loanOffering4 = getLoanOffering(
				PrdApplicableMaster.GROUPS.getValue().toString(), 2, 1);
		LoanOfferingBO loanOffering5 = getLoanOffering(
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 3);

		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "getPrdOfferings");
		addRequestParameter("customerId", group.getCustomerId().toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.getPrdOfferigs_success.toString());

		assertEquals("Group", ((CustomerBO) request.getSession()
				.getAttribute(LoanConstants.LOANACCOUNTOWNER)).getDisplayName());
		assertEquals(3, ((List<LoanOfferingBO>) request.getSession()
				.getAttribute(LoanConstants.LOANPRDOFFERINGS)).size());

		TestObjectFactory.removeObject(loanOffering1);
		TestObjectFactory.removeObject(loanOffering2);
		TestObjectFactory.removeObject(loanOffering3);
		TestObjectFactory.removeObject(loanOffering4);
		TestObjectFactory.removeObject(loanOffering5);
	}
	
	public void testLoadWithoutCustomerAndPrdOfferingId() {
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "load");
		actionPerform();
		verifyActionErrors(new String[] {
				LoanConstants.CUSTOMERNOTSELECTEDERROR,
				LoanConstants.LOANOFFERINGNOTSELECTEDERROR });
		verifyInputForward();
	}
	
	public void testLoadWithoutCustomer() {
		LoanOfferingBO loanOffering = getLoanOffering(
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId().toString());
		actionPerform();
		verifyActionErrors(new String[] {
				LoanConstants.CUSTOMERNOTSELECTEDERROR});
		verifyInputForward();
		
		TestObjectFactory.removeObject(loanOffering);
	}
	
	public void testLoadWithoutPrdOfferingId() {
		createInitialObjects();
		
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", group.getCustomerId().toString());
		addRequestParameter("prdOfferingId", "");
		actionPerform();
		verifyActionErrors(new String[] {
				LoanConstants.LOANOFFERINGNOTSELECTEDERROR});
		verifyInputForward();
	}
	
	public void testLoad() {
		createInitialObjects();
		LoanOfferingBO loanOffering = getLoanOffering(
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "getPrdOfferings");
		addRequestParameter("customerId", group.getCustomerId().toString());
		actionPerform();

		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", group.getCustomerId().toString());
		addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId()
				.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());
		assertNotNull(request.getSession().getAttribute(
				LoanConstants.LOANOFFERING));
		assertNotNull(request.getSession()
				.getAttribute(LoanConstants.LOANFUNDS));

		TestObjectFactory.removeObject(loanOffering);
	}
	
	public void testLoadForMasterData() {
		createInitialObjects();
		LoanOfferingBO loanOffering = getLoanOffering(
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "getPrdOfferings");
		addRequestParameter("customerId", group.getCustomerId().toString());
		actionPerform();
		
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", group.getCustomerId().toString());
		addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId().toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());
		
		assertEquals(2, ((List) request.getSession()
				.getAttribute(MasterConstants.COLLATERAL_TYPES)).size());
		assertEquals(129, ((List) request.getSession()
				.getAttribute(MasterConstants.BUSINESS_ACTIVITIES)).size());
		
		TestObjectFactory.removeObject(loanOffering);
	}
	
	public void testLoadWithFee() {
		createInitialObjects();
		LoanOfferingBO loanOffering = getLoanOffering(
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		List<FeeBO> fees = getFee();
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "getPrdOfferings");
		addRequestParameter("customerId", group.getCustomerId().toString());
		actionPerform();

		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", group.getCustomerId().toString());
		addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId()
				.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());

		HttpSession session = request.getSession();
		LoanAccountActionForm loanActionForm = (LoanAccountActionForm) session
				.getAttribute("loanAccountActionForm");
		assertEquals(2,( (List)session.getAttribute(LoanConstants.ADDITIONAL_FEES_LIST)).size());
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
		assertEquals(DateHelper.getCurrentDate(((UserContext) session
				.getAttribute("UserContext")).getPereferedLocale()),
				loanActionForm.getDisbursementDate());

		TestObjectFactory.removeObject((LoanOfferingBO) TestObjectFactory
				.getObject(LoanOfferingBO.class, loanOffering
						.getPrdOfferingId()));
		for (FeeBO fee : fees) {
			TestObjectFactory.cleanUp((FeeBO) TestObjectFactory.getObject(
					FeeBO.class, fee.getFeeId()));
		}
		
		group = (GroupBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
	}
	
	public void testSchedulePreviewWithoutData() {
		createInitialObjects();
		LoanOfferingBO loanOffering = getLoanOffering(
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		SessionUtils.setAttribute(LoanConstants.LOANOFFERING, loanOffering,
				request.getSession());
		SessionUtils.setAttribute(LoanConstants.LOANFUNDS,
				new ArrayList<Fund>(), request.getSession());
		SessionUtils.setAttribute(LoanConstants.LOANACCOUNTOWNER, group,
				request.getSession());
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "schedulePreview");
		actionPerform();

		verifyActionErrors(new String[] { "errors.defMinMax",
				"errors.defMinMax", "errors.defMinMax",
				"errors.validandmandatory", "errors.graceper" });
		verifyInputForward();
		
		TestObjectFactory.removeObject((LoanOfferingBO) TestObjectFactory
				.getObject(LoanOfferingBO.class, loanOffering
						.getPrdOfferingId()));
	}
	
	public void testSchedulePreviewWithDataWithNoGracePer() {
		createInitialObjects();
		LoanOfferingBO loanOffering = getLoanOffering(
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		SessionUtils.setAttribute(LoanConstants.LOANOFFERING, loanOffering,
				request.getSession());
		SessionUtils.setAttribute(LoanConstants.LOANFUNDS,
				new ArrayList<Fund>(), request.getSession());
		SessionUtils.setAttribute(LoanConstants.LOANACCOUNTOWNER, group,
				request.getSession());
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
		actionPerform();
		verifyActionErrors(new String[] { "errors.graceper" });
		verifyInputForward();

		TestObjectFactory.removeObject((LoanOfferingBO) TestObjectFactory
				.getObject(LoanOfferingBO.class, loanOffering
						.getPrdOfferingId()));
	}
	
	public void testSchedulePreviewWithData() {
		createInitialObjects();
		LoanOfferingBO loanOffering = getLoanOffering(
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		SessionUtils.setAttribute(LoanConstants.LOANOFFERING, loanOffering,
				request.getSession());
		SessionUtils.setAttribute(LoanConstants.LOANFUNDS,
				new ArrayList<Fund>(), request.getSession());
		SessionUtils.setAttribute(LoanConstants.LOANACCOUNTOWNER, group,
				request.getSession());
		SessionUtils.setAttribute(MasterConstants.COLLATERAL_TYPES,
				new ArrayList<MasterDataEntity>(), request.getSession());
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
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.schedulePreview_success.toString());
		TestObjectFactory.removeObject((LoanOfferingBO) TestObjectFactory
				.getObject(LoanOfferingBO.class, loanOffering
						.getPrdOfferingId()));
	}
	
	public void testSchedulePreviewWithDataForIntDedAtDisb() {
		createInitialObjects();
		LoanOfferingBO loanOffering = getLoanOffering(
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		SessionUtils.setAttribute(LoanConstants.LOANOFFERING, loanOffering,
				request.getSession());
		SessionUtils.setAttribute(LoanConstants.LOANFUNDS,
				new ArrayList<Fund>(), request.getSession());
		SessionUtils.setAttribute(LoanConstants.LOANACCOUNTOWNER, group,
				request.getSession());
		SessionUtils.setAttribute(MasterConstants.COLLATERAL_TYPES,
				new ArrayList<MasterDataEntity>(), request.getSession());
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
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.schedulePreview_success.toString());
		TestObjectFactory.removeObject((LoanOfferingBO) TestObjectFactory
				.getObject(LoanOfferingBO.class, loanOffering
						.getPrdOfferingId()));
	}
	
	public void testCreate() {
		createInitialObjects();
		LoanOfferingBO loanOffering = getLoanOffering(
				PrdApplicableMaster.GROUPS.getValue().toString(), 1, 1);
		SessionUtils.setAttribute(LoanConstants.LOANOFFERING, loanOffering,
				request.getSession());
		SessionUtils.setAttribute(LoanConstants.LOANFUNDS,
				new ArrayList<Fund>(), request.getSession());
		SessionUtils.setAttribute(LoanConstants.LOANACCOUNTOWNER, group,
				request.getSession());
		SessionUtils.setAttribute(MasterConstants.COLLATERAL_TYPES,
				new ArrayList<MasterDataEntity>(), request.getSession());
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
		addRequestParameter("method", "schedulePreview");
		actionPerform();

		addRequestParameter("method", "create");
		addRequestParameter("stateSelected", "1");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.create_success.toString());

		LoanBO loan = (LoanBO) request.getSession().getAttribute(
				Constants.BUSINESS_KEY);
		loan = (LoanBO) TestObjectFactory.getObject(LoanBO.class, loan
				.getAccountId());
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
		TestObjectFactory.cleanUp(loan);
	}
	
	public void testManage() {
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(Short.valueOf("3"), startDate, 1);
		LoanBO loan = (LoanBO) accountBO;
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request.getSession());
		
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "manage");
		actionPerform();
		verifyForward(ActionForwards.manage_success.toString());
	}
	
	public void testManagePreview() throws ServiceException, InvalidUserException, SystemException, ApplicationException {	
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(Short.valueOf("3"), startDate, 1);
		LoanBO loan = (LoanBO) accountBO;
		Date newDate = incrementCurrentDate(14);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request.getSession());
		SessionUtils.setAttribute(LoanConstants.LOANOFFERING, ((LoanBusinessService) ServiceFactory
				.getInstance().getBusinessService(BusinessServiceName.Loan)).getLoanOffering(loan.getLoanOffering().getPrdOfferingId(), TestObjectFactory.getUserContext().getLocaleId()),request.getSession());
		SessionUtils.setAttribute(MasterConstants.COLLATERAL_TYPES,
				new ArrayList<MasterDataEntity>(), request.getSession());
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "managePreview");
		addRequestParameter("loanAmount", loan.getLoanOffering().getDefaultLoanAmount()
				.toString());
		addRequestParameter("interestRate", loan.getLoanOffering().getDefInterestRate()
				.toString());
		addRequestParameter("noOfInstallments", loan.getLoanOffering().getDefNoInstallments().toString());
		addRequestParameter("disbursementDate", DateHelper
				.getCurrentDate(((UserContext) request.getSession()
						.getAttribute("UserContext")).getPereferedLocale()));
		addRequestParameter("gracePeriodDuration", "1");
		addRequestParameter("intDedDisbursement", "1");
		actionPerform();
		verifyForward(ActionForwards.managepreview_success.toString());
	}
	
	public void testManagePrevious() {		
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "managePrevious");
		actionPerform();
		verifyForward(ActionForwards.manageprevious_success.toString());
	}
	
	public void testCancel() {		
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "cancel");
		actionPerform();
		verifyForward(ActionForwards.loan_detail_page.toString());
	}
	
	public void testUpdateSuccessWithRegeneratingNewRepaymentSchedule() throws NumberFormatException, InvalidUserException, SystemException, ApplicationException {
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount();
		accountBO.setAccountState(new AccountStateEntity(AccountState.LOANACC_APPROVED));
		LoanBO loan = (LoanBO) accountBO;
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request.getSession());
		SessionUtils.setAttribute(MasterConstants.COLLATERAL_TYPES,
				new ArrayList<MasterDataEntity>(), request.getSession());
		Date newDate = incrementCurrentDate(14);
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "update");
		addRequestParameter("loanAmount", loan.getLoanOffering().getDefaultLoanAmount()
				.toString());
		addRequestParameter("interestRate", loan.getLoanOffering().getDefInterestRate()
				.toString());
		addRequestParameter("noOfInstallments", loan.getLoanOffering().getDefNoInstallments().toString());
		addRequestParameter("disbursementDate",getPrefferedDate(newDate));
		addRequestParameter("businessActivityId", "1");
		addRequestParameter("intDedDisbursement", "1");
		addRequestParameter("gracePeriodDuration", "1");
		addRequestParameter("collateralNote", "test");
		actionPerform();
		verifyForward(ActionForwards.update_success.toString());
		
		loan = (LoanBO)TestObjectFactory.getObject(LoanBO.class,loan.getAccountId());
		assertEquals("test",loan.getCollateralNote());
		assertEquals(300.0,loan.getLoanAmount().getAmountDoubleValue());
		assertEquals(newDate,loan.getAccountActionDate(Short.valueOf("1")).getActionDate());
	}
	
	public void testUpdateSuccessWithoutRegeneratingNewRepaymentSchedule() throws NumberFormatException, InvalidUserException, SystemException, ApplicationException {
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount();
		LoanBO loan = (LoanBO) accountBO;
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request.getSession());
		Date newDate = incrementCurrentDate(6);
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "update");
				addRequestParameter("loanAmount", loan.getLoanOffering().getDefaultLoanAmount()
				.toString());
		addRequestParameter("interestRate", loan.getLoanOffering().getDefInterestRate()
				.toString());
		addRequestParameter("noOfInstallments", loan.getLoanOffering().getDefNoInstallments().toString());
		addRequestParameter("disbursementDate",getPrefferedDate(newDate));
		addRequestParameter("businessActivityId", "1");
		addRequestParameter("intDedDisbursement", "1");
		addRequestParameter("gracePeriodDuration", "1");
		addRequestParameter("collateralNote", "test");
		actionPerform();
		verifyForward(ActionForwards.update_success.toString());
	}
	
	private void modifyActionDateForFirstInstallment() throws Exception {
		LoanScheduleEntity installment = (LoanScheduleEntity)accountBO.getAccountActionDate((short) 1);
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
		accountPersistence.updateAccount(account);
		return accountPersistence.getAccount(account.getAccountId());
	}
	
	private AccountNotesEntity createAccountNotes(String comment){
		AccountNotesEntity accountNotes = new AccountNotesEntity();
		accountNotes.setCommentDate(new java.sql.Date(System
				.currentTimeMillis()));
		accountNotes.setPersonnel(TestObjectFactory.getPersonnel(userContext.getId()));
		accountNotes.setComment(comment);
		return accountNotes;
	}
	
	private void addNotes() {
		accountBO.addAccountNotes(createAccountNotes("Notes1"));
		TestObjectFactory.updateObject(accountBO);
		accountBO.addAccountNotes(createAccountNotes("Notes2"));
		TestObjectFactory.updateObject(accountBO);
		accountBO.addAccountNotes(createAccountNotes("Notes3"));
		TestObjectFactory.updateObject(accountBO);
		accountBO.addAccountNotes(createAccountNotes("Notes4"));
		TestObjectFactory.updateObject(accountBO);
		accountBO.addAccountNotes(createAccountNotes("Notes5"));
		TestObjectFactory.updateObject(accountBO);
	}
	
	private void applyPaymentandRetrieveAccount() throws AccountException,	SystemException {
		Date startDate = new Date(System.currentTimeMillis());
		PaymentData paymentData = new PaymentData(new Money(Configuration
				.getInstance().getSystemConfig().getCurrency(), "100.0"),
				accountBO.getPersonnel(), Short.valueOf("1"), startDate);
		paymentData.setRecieptDate(startDate);
		paymentData.setRecieptNum("5435345");
		accountBO.applyPayment(paymentData);
		HibernateUtil.commitTransaction();
	}
	
	private void disburseLoan(Date startDate) throws NumberFormatException, AccountException, RepaymentScheduleException, FinancialException, SystemException {
		((LoanBO) accountBO).disburseLoan("1234", startDate,Short.valueOf("1"), accountBO.getPersonnel(), startDate, Short.valueOf("1"));
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
		LoanActivityEntity loanActivity = new LoanActivityEntity(accountBO,TestObjectFactory.getPersonnel(userContext.getId()),"testing",new Money("100"),new Money("100"),new Money("100"),new Money("100"),new Money("100"),new Money("100"),new Money("100"),new Money("100"));
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
	
	private LoanOfferingBO getLoanOffering(String prdApplicableTo,
			int meetingFrequency, int recurAfter) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(meetingFrequency, recurAfter, 4, 2));
		Date currentDate = new Date(System.currentTimeMillis());
		return TestObjectFactory.createLoanOffering("Loan", Short
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
				FeeCategory.LOAN, "10.0", MeetingFrequency.WEEKLY, (short) 1);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		fees.add(fee1);
		fees.add(fee3);
		return fees;
	}
	
	private String getPrefferedDate(Date date){
		Calendar currentDateCalendar = new GregorianCalendar();
		currentDateCalendar.setTime(date);
		return (currentDateCalendar.get(Calendar.MONTH)+1)+"/"+currentDateCalendar.get(Calendar.DATE)+"/"+currentDateCalendar.get(Calendar.YEAR);
		
	}
	
	private Date incrementCurrentDate(int noOfDays) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day + noOfDays);
		return DateUtils.getDateWithoutTimeStamp(currentDateCalendar.getTimeInMillis());
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
