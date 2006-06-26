package org.mifos.application.accounts.savings.struts.action;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Hibernate;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.AccountNotesEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.persistence.service.SavingsPersistenceService;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.savings.util.helpers.SavingsHelper;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.persistence.service.CustomerPersistenceService;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

import servletunit.struts.MockStrutsTestCase;

public class TestSavingsClosureAction extends MockStrutsTestCase{
	private UserContext userContext ;
	private CustomerBO group;
	private CustomerBO center;
	private SavingsBO savings;
	private SavingsBO newSavings;
	private SavingsOfferingBO savingsOffering;
	private CustomerBO client1;
	private CustomerBO client2;

	public TestSavingsClosureAction() {
	}

	public TestSavingsClosureAction(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI(
					"WEB-INF/web.xml").getPath());
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
		request.getSession().setAttribute(Constants.USER_CONTEXT_KEY, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
		request.getSession().setAttribute(Constants.USER_CONTEXT_KEY, userContext);

	}

	public void tearDown()throws Exception{
		TestObjectFactory.cleanUp(savings);
		savings=null;
		TestObjectFactory.cleanUp(newSavings);
		newSavings=null;
		TestObjectFactory.cleanUp(client1);
		client1=null;
		TestObjectFactory.cleanUp(client2);
		client2=null;
		TestObjectFactory.cleanUp(group);
		group=null;
		TestObjectFactory.cleanUp(center);
		center=null;
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testSuccessfullLoad()throws Exception{
		createInitialObjects();
		createClients();
		savingsOffering = createSavingsOffering();
		savings = createSavingsAccount("000X00000000017",savingsOffering,group,AccountStates.SAVINGS_ACC_APPROVED);
		HibernateUtil.closeSession();
		request.getSession().setAttribute(Constants.BUSINESS_KEY,savings);
		setRequestPathInfo("/savingsClosureAction.do");
		addRequestParameter("method","load");
        actionPerform();
		verifyForward("load_success");
		savings = (SavingsBO)request.getSession().getAttribute(Constants.BUSINESS_KEY);
		Hibernate.initialize(savings.getAccountPayments());
		Hibernate.initialize(savings.getAccountFees());
		Hibernate.initialize(savings.getAccountActionDates());
		assertNotNull(request.getSession().getAttribute(MasterConstants.PAYMENT_TYPE));
		assertNotNull(request.getSession().getAttribute(SavingsConstants.CLIENT_LIST));
		group=savings.getCustomer();
		center=group.getParentCustomer();
		client1=new CustomerPersistenceService().getCustomer(client1.getCustomerId());
		client2=new CustomerPersistenceService().getCustomer(client2.getCustomerId());
	}

	public void testSuccessfullPreview(){
		AccountPaymentEntity payment = new AccountPaymentEntity();
		payment.setPaymentDate(new Date(new SavingsHelper().getCurrentDate().getTime()));
		payment.setAmount(new Money(Configuration.getInstance().getSystemConfig().getCurrency(),"500"));
		SessionUtils.setAttribute(SavingsConstants.ACCOUNT_PAYMENT,payment, request.getSession());
		addRequestParameter("receiptId","101");
		addRequestParameter("receiptDate","12/05/2005");
		addRequestParameter("paymentTypeId","1");
		addRequestParameter("customerId","1");
		setRequestPathInfo("/savingsClosureAction.do");
		addRequestParameter("method","preview");
		actionPerform();
		verifyForward("preview_success");
	}

	public void testSuccessfullPrevious(){
		setRequestPathInfo("/savingsClosureAction.do");
		addRequestParameter("method","previous");
		actionPerform();
		verifyForward("previous_success");
	}

	public void testSuccessfullCloseAccount()throws Exception{
		createInitialObjects();
		savingsOffering = createSavingsOffering();
		group.getPersonnel().getPersonnelStatus();
		savings = createSavingsAccount("000X00000000017",savingsOffering,group,AccountStates.SAVINGS_ACC_APPROVED);
		HibernateUtil.closeSession();
		savings = new SavingsPersistenceService().findById(savings.getAccountId());
		group= savings.getCustomer();
		center=group.getParentCustomer();
		savings.getSavingsOffering().getDescription();
		savings.getCustomer().getPersonnel();

		AccountPaymentEntity payment = new AccountPaymentEntity();
		savings.setInterestToBePosted(new Money(Configuration.getInstance().getSystemConfig().getCurrency(),"40"));

		savings.setUserContext(userContext);
		request.getSession().setAttribute(Constants.BUSINESS_KEY,savings);

		for(AccountPaymentEntity acPayment : savings.getAccountPayments())
			acPayment.getAccountTrxns();
		for(AccountActionDateEntity actionDate : savings.getAccountActionDates())
			actionDate.getActionDate();
		for(AccountFeesEntity fee: savings.getAccountFees())
			fee.getAccountFeeAmount();
		for(AccountNotesEntity notes: savings.getAccountNotes())
			notes.getCommentDate();
		payment.setPaymentDate(new Date(new SavingsHelper().getCurrentDate().getTime()));
		payment.setAmount(new Money(Configuration.getInstance().getSystemConfig().getCurrency(),"500"));

		PaymentTypeEntity paymentType = new PaymentTypeEntity();
		paymentType.setId(Short.valueOf("1"));
		payment.setPaymentType(paymentType);

		SessionUtils.setAttribute(SavingsConstants.ACCOUNT_PAYMENT,payment, request.getSession());
		addRequestParameter("notes","this is the notes added");
		setRequestPathInfo("/savingsClosureAction.do");
		addRequestParameter("method","close");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("close_success");
	}

	private void createInitialObjects(){
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short.valueOf("13"), "1.1", meeting,new Date(System.currentTimeMillis()));
		group=TestObjectFactory.createGroup("Group_Active_test",Short.valueOf("9"),"1.1.1",center,new Date(System.currentTimeMillis()));
	}
	private void createClients(){
		client1=TestObjectFactory.createClient("client1",ClientConstants.STATUS_CLOSED,"1.1.1.1",group,new Date(System.currentTimeMillis()));
		client2=TestObjectFactory.createClient("client2",ClientConstants.STATUS_ACTIVE,"1.1.1.2",group,new Date(System.currentTimeMillis()));
	}

	private SavingsOfferingBO createSavingsOffering(){
		MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createSavingsOffering("SavingPrd1",Short.valueOf("2"),new Date(System.currentTimeMillis()),
				Short.valueOf("2"),300.0,Short.valueOf("1"),1.2,200.0,200.0,Short.valueOf("2"),Short.valueOf("1"),meetingIntCalc,meetingIntPost);
	}

	private SavingsBO createSavingsAccount(String globalAccountNum,SavingsOfferingBO savingsOffering, CustomerBO group, short accountStateId){
		return TestObjectFactory.createSavingsAccount(globalAccountNum, group, accountStateId ,new Date(),savingsOffering,userContext);
	}
}
