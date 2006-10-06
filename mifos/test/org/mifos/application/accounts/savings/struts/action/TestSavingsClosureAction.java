package org.mifos.application.accounts.savings.struts.action;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.Hibernate;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.AccountNotesEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.savings.util.helpers.SavingsHelper;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
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
		userContext.setPereferedLocale(new Locale("en", "US"));
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession().setAttribute(Constants.USER_CONTEXT_KEY,
				userContext);
		request.getSession(false).setAttribute("ActivityContext",
				TestObjectFactory.getActivityContext());
		Flow flow = new Flow();
		flowKey = String.valueOf(System.currentTimeMillis());
		FlowManager flowManager = new FlowManager();
		flowManager.addFLow(flowKey, flow);
		request.getSession(false).setAttribute(Constants.FLOWMANAGER,
				flowManager);
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

	public void testSuccessfullLoad() throws Exception {
		createInitialObjects();
		createClients();
		savingsOffering = createSavingsOffering();
		savings = createSavingsAccount("000X00000000017", savingsOffering,
				group, AccountStates.SAVINGS_ACC_APPROVED);
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
		String currentDate = DateHelper.getCurrentDate(userContext
				.getPereferedLocale());
		addRequestParameter("receiptDate", currentDate);
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

	public void testSuccessfullCloseAccount() throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering("asfddsf", "213a");
		savings = helper.createSavingsAccount("000X00000000017",
				savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);
		savings.setActivationDate(helper.getDate("20/05/2006"));
		PersonnelBO createdBy = new PersonnelPersistence()
				.getPersonnel(userContext.getId());
		AccountPaymentEntity payment1 = helper.createAccountPaymentToPersist(
				savings,
				new Money(TestObjectFactory.getMFICurrency(), "1000.0"),
				new Money(TestObjectFactory.getMFICurrency(), "1000.0"), helper
						.getDate("30/05/2006"),
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings, createdBy,
				group);
		savings.addAccountPayment(payment1);
		savings.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		Money balanceAmount = new Money(TestObjectFactory.getMFICurrency(),
				"1500.0");
		AccountPaymentEntity payment2 = helper.createAccountPaymentToPersist(
				savings,
				new Money(TestObjectFactory.getMFICurrency(), "500.0"),
				balanceAmount, helper.getDate("15/06/2006"),
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings, createdBy,
				group);
		savings.addAccountPayment(payment2);
		savings.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		Money interestAmount = new Money(TestObjectFactory.getMFICurrency(),
				"40");
		savings.setInterestToBePosted(interestAmount);
		savings.setSavingsBalance(balanceAmount);
		savings.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		savings = new SavingsPersistence().findById(savings.getAccountId());
		savings.setUserContext(userContext);
		Money interestAtClosure = savings
				.calculateInterestForClosure(new SavingsHelper()
						.getCurrentDate());

		group = savings.getCustomer();
		center = group.getParentCustomer();
		savings.getSavingsOffering().getDescription();
		savings.getCustomer().getPersonnel();

		AccountPaymentEntity payment = new AccountPaymentEntity(savings,
				balanceAmount.add(interestAtClosure), null, null,
				new PaymentTypeEntity(Short.valueOf("1")));
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);

		for (AccountPaymentEntity acPayment : savings.getAccountPayments())
			acPayment.getAccountTrxns();
		for (AccountActionDateEntity actionDate : savings
				.getAccountActionDates())
			actionDate.getActionDate();
		for (AccountFeesEntity fee : savings.getAccountFees())
			fee.getAccountFeeAmount();
		for (AccountNotesEntity notes : savings.getAccountNotes())
			notes.getCommentDate();

		SessionUtils.setAttribute(SavingsConstants.ACCOUNT_PAYMENT, payment,
				request);
		addRequestParameter("notes", "this is the notes added");
		setRequestPathInfo("/savingsClosureAction.do");
		addRequestParameter("method", "close");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("close_success");
		savings = (SavingsBO) TestObjectFactory.getObject(SavingsBO.class,
				savings.getAccountId());
	}

	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group_Active_test", Short
				.valueOf("9"), "1.1.1", center, new Date(System
				.currentTimeMillis()));
	}

	private void createClients() {
		client1 = TestObjectFactory.createClient("client1",
				CustomerStatus.CLIENT_CLOSED.getValue(), "1.1.1.1", group,
				new Date(System.currentTimeMillis()));
		client2 = TestObjectFactory.createClient("client2",
				CustomerStatus.CLIENT_ACTIVE.getValue(), "1.1.1.2", group,
				new Date(System.currentTimeMillis()));
		client3 = TestObjectFactory.createClient("client2",
				CustomerStatus.CLIENT_PARTIAL.getValue(), "1.1.1.2", group,
				new Date(System.currentTimeMillis()));
		client4 = TestObjectFactory.createClient("client2",
				CustomerStatus.CLIENT_HOLD.getValue(), "1.1.1.2", group,
				new Date(System.currentTimeMillis()));
	}

	private SavingsOfferingBO createSavingsOffering() {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createSavingsOffering("SavingPrd1", Short
				.valueOf("2"), new Date(System.currentTimeMillis()), Short
				.valueOf("2"), 300.0, Short.valueOf("1"), 1.2, 200.0, 200.0,
				Short.valueOf("2"), Short.valueOf("1"), meetingIntCalc,
				meetingIntPost);
	}

	private SavingsBO createSavingsAccount(String globalAccountNum,
			SavingsOfferingBO savingsOffering, CustomerBO group,
			short accountStateId) throws Exception {
		return TestObjectFactory.createSavingsAccount(globalAccountNum, group,
				accountStateId, new Date(), savingsOffering, userContext);
	}
}
