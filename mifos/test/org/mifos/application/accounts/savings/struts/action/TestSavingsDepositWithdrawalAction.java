package org.mifos.application.accounts.savings.struts.action;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.TestSavingsBO;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestSavingsDepositWithdrawalAction extends MifosMockStrutsTestCase{
	private UserContext userContext;
	private CustomerBO group;
	private CustomerBO center;
	private SavingsBO savings;
	private SavingsOfferingBO savingsOffering;
	private CustomerBO client1;
	private CustomerBO client2;
	private CustomerBO client3;
	private CustomerBO client4;
	private SavingsTestHelper helper = new SavingsTestHelper();
	private String flowKey;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
					.getPath());
			setConfigFile(ResourceLoader.getURI(
					"org/mifos/application/accounts/savings/struts-config.xml")
					.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		userContext = TestObjectFactory.getContext();
		userContext.setPereferedLocale(new Locale("en", "US"));
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession().setAttribute(Constants.USER_CONTEXT_KEY, userContext);
		request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
		flowKey = createFlow(request, SavingsDepositWithdrawalAction.class);
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
	}
	
	@Override
	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(savings);
		TestObjectFactory.cleanUp(client1);
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(client3);
		TestObjectFactory.cleanUp(client4);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	public void testSuccessfullLoad_ForClientAccount() throws Exception {
		createCenterAndGroup();
		createClients();
		savingsOffering = TestObjectFactory.createSavingsOffering("Offering1", "s1", SavingsType.MANDATORY, ApplicableTo.CLIENTS);
		savings = helper.createSavingsAccount("000X00000000017", savingsOffering, client1, AccountStates.SAVINGS_ACC_APPROVED, userContext);
		HibernateUtil.closeSession();
		
		savings = new SavingsPersistence().findById(savings.getAccountId());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
		setRequestPathInfo("/savingsDepositWithdrawalAction.do");
		addRequestParameter("method", "load");
		actionPerform();
		verifyForward("load_success");

		List<AccountActionEntity> trxnTypes = (List<AccountActionEntity>)SessionUtils.getAttribute(AccountConstants.TRXN_TYPES,request);
		assertNotNull(trxnTypes);
		assertEquals(2, trxnTypes.size());
		
		List<CustomerBO> clientList = (List<CustomerBO>)SessionUtils.getAttribute(SavingsConstants.CLIENT_LIST,request);
		assertNull(clientList);
		
		Boolean isBackDatedAllowed = (Boolean)SessionUtils.getAttribute(SavingsConstants.IS_BACKDATED_TRXN_ALLOWED,request);
		assertNotNull(isBackDatedAllowed);
		assertNotNull(SessionUtils.getAttribute(MasterConstants.PAYMENT_TYPE,request));
		group = new CustomerPersistence().getCustomer(group
				.getCustomerId());
		center = new CustomerPersistence().getCustomer(center
				.getCustomerId());
		client1 = new CustomerPersistence().getCustomer(client1
				.getCustomerId());
		client2 = new CustomerPersistence().getCustomer(client2
				.getCustomerId());
		client3 = new CustomerPersistence().getCustomer(client3
				.getCustomerId());
		client4 = new CustomerPersistence().getCustomer(client4
				.getCustomerId());
	}
	
	public void testSuccessfullLoad() throws Exception {
		createCenterAndGroup();
		createClients();
		savingsOffering = helper.createSavingsOffering("asfddsf","213a");
		savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED, userContext);
		HibernateUtil.closeSession();
		
		savings = new SavingsPersistence().findById(savings.getAccountId());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
		setRequestPathInfo("/savingsDepositWithdrawalAction.do");
		addRequestParameter("method", "load");
		actionPerform();
		verifyForward("load_success");

		List<AccountActionEntity> trxnTypes = (List<AccountActionEntity>)SessionUtils.getAttribute(AccountConstants.TRXN_TYPES,request);
		assertNotNull(trxnTypes);
		assertEquals(2, trxnTypes.size());
		
		List<CustomerBO> clientList = (List<CustomerBO>)SessionUtils.getAttribute(SavingsConstants.CLIENT_LIST,request);
		assertNotNull(clientList);
		assertEquals(2, clientList.size());
		Boolean isBackDatedAllowed = (Boolean)SessionUtils.getAttribute(SavingsConstants.IS_BACKDATED_TRXN_ALLOWED,request);
		assertNotNull(isBackDatedAllowed);
		assertNotNull(SessionUtils.getAttribute(MasterConstants.PAYMENT_TYPE,request));
		group = savings.getCustomer();
		center = group.getParentCustomer();
		client1 = new CustomerPersistence().getCustomer(client1
				.getCustomerId());
		client2 = new CustomerPersistence().getCustomer(client2
				.getCustomerId());
		client3 = new CustomerPersistence().getCustomer(client3
				.getCustomerId());
		client4 = new CustomerPersistence().getCustomer(client4
				.getCustomerId());
	}
	
	public void testSuccessfullReLoad() throws Exception {
		createCenterAndGroup();
		savingsOffering = helper.createSavingsOffering("asfddsf","213a");
		savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED, userContext);
		HibernateUtil.closeSession();
		
		savings = new SavingsPersistence().findById(savings.getAccountId());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
		setRequestPathInfo("/savingsDepositWithdrawalAction.do");
		addRequestParameter("method", "reLoad");
		addRequestParameter("trxnTypeId", String.valueOf(AccountActionTypes.SAVINGS_WITHDRAWAL.getValue()));
		actionPerform();
		verifyForward("load_success");
	}
	
	public void testSuccessfullReLoad_Deposit() throws Exception {
		createCenterAndGroup();
		savingsOffering = helper.createSavingsOffering("asfddsf","213a");
		savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED, userContext);
		HibernateUtil.closeSession();
		
		savings = new SavingsPersistence().findById(savings.getAccountId());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
		setRequestPathInfo("/savingsDepositWithdrawalAction.do");
		addRequestParameter("method", "reLoad");
		addRequestParameter("trxnTypeId", String.valueOf(AccountActionTypes.SAVINGS_DEPOSIT.getValue()));
		actionPerform();
		verifyForward("load_success");
	}
	
	public void testFailurePreview() throws Exception {
		createCenterAndGroup();
		savingsOffering = helper.createSavingsOffering("asfddsf","213a");
		savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED, userContext);
		HibernateUtil.closeSession();
		savings = new SavingsPersistence().findById(savings.getAccountId());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
		setRequestPathInfo("/savingsDepositWithdrawalAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("amount", "");
		addRequestParameter("customerId", "");
		addRequestParameter("trxnDate", "");
		addRequestParameter("paymentTypeId", "1");
		addRequestParameter("trxnTypeId", String.valueOf(AccountActionTypes.SAVINGS_DEPOSIT.getValue()));
		actionPerform();
		assertEquals(3,getErrrorSize());
		assertEquals(3,getErrrorSize(AccountConstants.ERROR_MANDATORY));
	}
	
	public void testSuccessfulPreview() throws Exception {
		createCenterAndGroup();
		savingsOffering = helper.createSavingsOffering("asfddsf","213a");
		savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED, userContext);
		HibernateUtil.closeSession();
		savings = new SavingsPersistence().findById(savings.getAccountId());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
		setRequestPathInfo("/savingsDepositWithdrawalAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("amount", "200");
		addRequestParameter("customerId", group.getCustomerId().toString());
		addRequestParameter("trxnDate", DateHelper.getCurrentDate(userContext.getPereferedLocale()));
		addRequestParameter("paymentTypeId", "1");
		addRequestParameter("trxnTypeId", String.valueOf(AccountActionTypes.SAVINGS_DEPOSIT.getValue()));
		actionPerform();
		verifyForward(ActionForwards.preview_success.toString());
	}
	
	public void testSuccessfulMakePayment_Deposit() throws Exception {
		createCenterAndGroup();
		savingsOffering = helper.createSavingsOffering("asfddsf","213a");
		savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED, userContext);
		HibernateUtil.closeSession();
		savings = new SavingsPersistence().findById(savings.getAccountId());
				
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
		setRequestPathInfo("/savingsDepositWithdrawalAction.do");
		addRequestParameter("method", "load");
		actionPerform();
		
		setRequestPathInfo("/savingsDepositWithdrawalAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("amount", "200");
		addRequestParameter("customerId", group.getCustomerId().toString());
		addRequestParameter("trxnDate", DateHelper.getCurrentDate(userContext.getPereferedLocale()));
		addRequestParameter("paymentTypeId", "1");
		addRequestParameter("trxnTypeId", String.valueOf(AccountActionTypes.SAVINGS_DEPOSIT.getValue()));
		actionPerform();
		
		setRequestPathInfo("/savingsDepositWithdrawalAction.do");
		addRequestParameter("method", "makePayment");
		actionPerform();
		verifyForward(ActionForwards.account_details_page.toString());
		HibernateUtil.closeSession();
		savings = new SavingsPersistence().findById(savings.getAccountId());
		assertEquals(new Money("200"),savings.getSavingsBalance());
	}
	
	public void testSuccessfulMakePayment_Withdrawal() throws Exception {
		createCenterAndGroup();
		savingsOffering = helper.createSavingsOffering("asfddsf","213a");
		savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED, userContext);
		HibernateUtil.closeSession();
		savings = new SavingsPersistence().findById(savings.getAccountId());
		TestSavingsBO.setBalance(savings,new Money("500"));
		
		savings.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savings = new SavingsPersistence().findById(savings.getAccountId());
		
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
		
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
		setRequestPathInfo("/savingsDepositWithdrawalAction.do");
		addRequestParameter("method", "load");
		actionPerform();
		
		setRequestPathInfo("/savingsDepositWithdrawalAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("amount", "30");
		addRequestParameter("customerId", group.getCustomerId().toString());
		addRequestParameter("trxnDate", DateHelper.getCurrentDate(userContext.getPereferedLocale()));
		addRequestParameter("paymentTypeId", "1");
		addRequestParameter("trxnTypeId", String.valueOf(AccountActionTypes.SAVINGS_WITHDRAWAL.getValue()));
		actionPerform();
		
		setRequestPathInfo("/savingsDepositWithdrawalAction.do");
		addRequestParameter("method", "makePayment");
		actionPerform();
		verifyForward(ActionForwards.account_details_page.toString());
		HibernateUtil.closeSession();
		savings = new SavingsPersistence().findById(savings.getAccountId());
		assertEquals(new Money("470"),savings.getSavingsBalance());
	}
	
	public void testSuccessfullPrevious() throws Exception {
		setRequestPathInfo("/savingsDepositWithdrawalAction.do");
		addRequestParameter("method", "previous");
		actionPerform();
		verifyForward("previous_success");
	}

	public void testSuccessfullCancel() throws Exception {
		setRequestPathInfo("/savingsDepositWithdrawalAction.do");
		addRequestParameter("method", "cancel");
		actionPerform();
		verifyForward(ActionForwards.account_details_page.toString());
	}
	
	private void createCenterAndGroup() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
	}

	private void createClients() {
		client1 = TestObjectFactory.createClient("client1",
				CustomerStatus.CLIENT_CLOSED, group);
		client2 = TestObjectFactory.createClient("client2",
				CustomerStatus.CLIENT_ACTIVE, group);
		client3 = TestObjectFactory.createClient("client3",
				CustomerStatus.CLIENT_PARTIAL, group);
		client4 = TestObjectFactory.createClient("client4",
				CustomerStatus.CLIENT_HOLD, group);
	}
}
