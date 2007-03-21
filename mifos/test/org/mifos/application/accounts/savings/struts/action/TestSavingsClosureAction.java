package org.mifos.application.accounts.savings.struts.action;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.Hibernate;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.TestAccountPaymentEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.TestSavingsBO;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestSavingsClosureAction extends MifosMockStrutsTestCase {
	private UserContext userContext;

	private CustomerBO group;

	private CustomerBO center;

	private SavingsBO savings;

	private SavingsBO newSavings;

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
		userContext.setPreferredLocale(new Locale("en", "US"));
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession().setAttribute(Constants.USER_CONTEXT_KEY,
				userContext);
		request.getSession(false).setAttribute("ActivityContext",
				TestObjectFactory.getActivityContext());
		flowKey = createFlow(request, SavingsClosureAction.class);
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
	}

	@Override
	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(savings);
		TestObjectFactory.cleanUp(newSavings);
		TestObjectFactory.cleanUp(client1);
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(client3);
		TestObjectFactory.cleanUp(client4);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testSuccessfullLoad_Client() throws Exception {
		createInitialObjects();
		createClients();
		savingsOffering = TestObjectFactory.createSavingsOffering(
			"Offering1", "s1", SavingsType.MANDATORY, ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
		savings = createSavingsAccount("000X00000000017", savingsOffering,
				client1, AccountState.SAVINGS_ACC_APPROVED);
		HibernateUtil.closeSession();
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
		setRequestPathInfo("/savingsClosureAction.do");
		addRequestParameter("method", "load");
		actionPerform();
		verifyForward("load_success");
		savings = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY,request);
		Hibernate.initialize(savings.getAccountPayments());
		Hibernate.initialize(savings.getAccountFees());
		Hibernate.initialize(savings.getAccountActionDates());
		assertNotNull(SessionUtils.getAttribute(
				MasterConstants.PAYMENT_TYPE,request));
		List<CustomerBO> clientList = (List<CustomerBO>)SessionUtils.getAttribute(SavingsConstants.CLIENT_LIST,request);
		assertNull(clientList);

		group = new CustomerPersistence().getCustomer(group
				.getCustomerId());
		center = new CustomerPersistence().getCustomer(center
				.getCustomerId());
		client1 = new CustomerPersistence()
				.getCustomer(client1.getCustomerId());
		client2 = new CustomerPersistence()
				.getCustomer(client2.getCustomerId());
		client3 = new CustomerPersistence()
				.getCustomer(client3.getCustomerId());
		client4 = new CustomerPersistence()
				.getCustomer(client4.getCustomerId());
	}
	
	public void testSuccessfullLoad() throws Exception {
		createInitialObjects();
		createClients();
		savingsOffering = createSavingsOffering();
		savings = createSavingsAccount("000X00000000017", savingsOffering,
				group, AccountState.SAVINGS_ACC_APPROVED);
		HibernateUtil.closeSession();
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
		setRequestPathInfo("/savingsClosureAction.do");
		addRequestParameter("method", "load");
		actionPerform();
		verifyForward("load_success");
		savings = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY,request);
		Hibernate.initialize(savings.getAccountPayments());
		Hibernate.initialize(savings.getAccountFees());
		Hibernate.initialize(savings.getAccountActionDates());
		assertNotNull(SessionUtils.getAttribute(
				MasterConstants.PAYMENT_TYPE,request));
		List<CustomerBO> clientList = (List<CustomerBO>)SessionUtils.getAttribute(SavingsConstants.CLIENT_LIST,request);
		assertNotNull(clientList);
		assertEquals(2, clientList.size());
		
		group = savings.getCustomer();
		center = group.getParentCustomer();
		client1 = new CustomerPersistence()
				.getCustomer(client1.getCustomerId());
		client2 = new CustomerPersistence()
				.getCustomer(client2.getCustomerId());
		client3 = new CustomerPersistence()
				.getCustomer(client3.getCustomerId());
		client4 = new CustomerPersistence()
				.getCustomer(client4.getCustomerId());
	}

	public void testSuccessfullPreview()throws Exception {
		AccountPaymentEntity payment = new AccountPaymentEntity(null,
				new Money(Configuration.getInstance().getSystemConfig()
						.getCurrency(), "500"), null, null, null);
		SessionUtils.setAttribute(SavingsConstants.ACCOUNT_PAYMENT, payment,
				request);
		addRequestParameter("receiptId", "101");
		addRequestParameter("receiptDate", DateHelper.makeDateAsSentFromBrowser());
		addRequestParameter("paymentTypeId", "1");
		addRequestParameter("customerId", "1");
		addRequestParameter("notes", "notes");
		setRequestPathInfo("/savingsClosureAction.do");
		addRequestParameter("method", "preview");
		actionPerform();
		verifyNoActionErrors();
		verifyForward("preview_success");
	}
	
	public void testPreviewDateValidation()throws Exception {
		AccountPaymentEntity payment = new AccountPaymentEntity(null,
				new Money(Configuration.getInstance().getSystemConfig()
						.getCurrency(), "500"), null, null, null);
		SessionUtils.setAttribute(SavingsConstants.ACCOUNT_PAYMENT, payment,
				request);
		addRequestParameter("receiptId", "101");
		String badDate = "3/20/2005"; // an invalid date
		addRequestParameter("receiptDate", badDate);
		addRequestParameter("paymentTypeId", "1");
		addRequestParameter("customerId", "1");
		addRequestParameter("notes", "notes");
		setRequestPathInfo("/savingsClosureAction.do");
		addRequestParameter("method", "preview");
		actionPerform();
		verifyActionErrors(new String[] {AccountConstants.ERROR_INVALIDDATE});
	}

	public void testSuccessfullPreview_withoutReceipt()throws Exception {
		AccountPaymentEntity payment = new AccountPaymentEntity(null,
				new Money(Configuration.getInstance().getSystemConfig()
						.getCurrency(), "500"), null, null, null);
		SessionUtils.setAttribute(SavingsConstants.ACCOUNT_PAYMENT, payment,
				request);
		addRequestParameter("receiptId", "");
		addRequestParameter("receiptDate", "");
		addRequestParameter("paymentTypeId", "1");
		addRequestParameter("customerId", "1");
		addRequestParameter("notes", "notes");
		setRequestPathInfo("/savingsClosureAction.do");
		addRequestParameter("method", "preview");
		actionPerform();
		verifyForward("preview_success");
	}
	
	public void testSuccessfullPrevious() {
		setRequestPathInfo("/savingsClosureAction.do");
		addRequestParameter("method", "previous");
		actionPerform();
		verifyForward("previous_success");
	}

	public void testSuccessfullCancel() throws Exception {
		setRequestPathInfo("/savingsClosureAction.do");
		addRequestParameter("method", "cancel");
		actionPerform();
		verifyForward("close_success");
	}
	
	public void testSuccessfullCloseAccount() throws Exception {
		createInitialObjects();
		createClients();
		savingsOffering = helper.createSavingsOffering("asfddsf", "213a");
		savings = helper.createSavingsAccount("000X00000000017",
				savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);
		TestSavingsBO.setActivationDate(savings,helper.getDate("20/05/2006"));
		PersonnelBO createdBy = new PersonnelPersistence()
				.getPersonnel(userContext.getId());
		AccountPaymentEntity payment1 = helper.createAccountPaymentToPersist(
				savings,
				new Money(TestObjectFactory.getMFICurrency(), "1000.0"),
				new Money(TestObjectFactory.getMFICurrency(), "1000.0"), helper
						.getDate("30/05/2006"),
				AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy,
				group);
		TestAccountPaymentEntity.addAccountPayment(payment1,savings);
		savings.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		Money balanceAmount = new Money(TestObjectFactory.getMFICurrency(),
				"1500.0");
		AccountPaymentEntity payment2 = helper.createAccountPaymentToPersist(
				savings,
				new Money(TestObjectFactory.getMFICurrency(), "500.0"),
				balanceAmount, helper.getDate("15/06/2006"),
				AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy,
				group);
		TestAccountPaymentEntity.addAccountPayment(payment2,savings);
		savings.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		Money interestAmount = new Money(TestObjectFactory.getMFICurrency(),
				"40");
		TestSavingsBO.setInterestToBePosted(savings,interestAmount);
		TestSavingsBO.setBalance(savings,balanceAmount);
		savings.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		savings = new SavingsPersistence().findById(savings.getAccountId());
		savings.setUserContext(userContext);

		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
		setRequestPathInfo("/savingsClosureAction.do");
		addRequestParameter("method", "load");
		actionPerform();
		verifyForward("load_success");

		addRequestParameter("receiptId", "101");
		addRequestParameter("receiptDate", DateHelper.makeDateAsSentFromBrowser());
		addRequestParameter("paymentTypeId", "1");
		addRequestParameter("customerId", "1");
		addRequestParameter("notes", "closing account");
		setRequestPathInfo("/savingsClosureAction.do");
		addRequestParameter("method", "preview");
		actionPerform();

		setRequestPathInfo("/savingsClosureAction.do");
		addRequestParameter("method", "close");
		actionPerform();
		
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("close_success");
		savings = TestObjectFactory.getObject(SavingsBO.class,
				savings.getAccountId());
		
		assertEquals(new Money(), savings.getSavingsBalance());
		assertEquals(AccountState.SAVINGS_ACC_CLOSED.getValue(), savings.getAccountState().getId());
	}

	
	private void createInitialObjects() {
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

	private SavingsOfferingBO createSavingsOffering() {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getTypicalMeeting());
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getTypicalMeeting());
		return TestObjectFactory.createSavingsOffering("SavingPrd1", ApplicableTo.GROUPS, new Date(System.currentTimeMillis()), Short
						.valueOf("2"), 300.0, Short.valueOf("1"), 1.2, 200.0, 200.0, Short.valueOf("2"), Short.valueOf("1"), meetingIntCalc, meetingIntPost);
	}

	private SavingsBO createSavingsAccount(String globalAccountNum,
			SavingsOfferingBO savingsOffering, CustomerBO group,
			AccountState state) throws Exception {
		return TestObjectFactory.createSavingsAccount(globalAccountNum, group,
				state, new Date(), savingsOffering, userContext);
	}
}
