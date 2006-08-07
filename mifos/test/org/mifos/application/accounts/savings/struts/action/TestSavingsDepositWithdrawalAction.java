package org.mifos.application.accounts.savings.struts.action;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestSavingsDepositWithdrawalAction extends MifosMockStrutsTestCase{
	private UserContext userContext;
	private CustomerBO group;
	private CustomerBO center;
	private SavingsBO savings;
	private SavingsOfferingBO savingsOffering;
	private CustomerBO client1;
	private CustomerBO client2;
	private SavingsTestHelper helper = new SavingsTestHelper();
	
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
		request.getSession().setAttribute(Constants.USER_CONTEXT_KEY,
				userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
		request.getSession().setAttribute(Constants.USER_CONTEXT_KEY,
				userContext);

	}
	
	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(savings);
		TestObjectFactory.cleanUp(client1);
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
	}
	
	public void testSuccessfullLoad() throws Exception {
		createCenterAndGroup();
		createClients();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED, userContext);
		HibernateUtil.closeSession();
		
		savings = new SavingsPersistence().findById(savings.getAccountId());
		request.getSession().setAttribute(Constants.BUSINESS_KEY, savings);
		setRequestPathInfo("/savingsDepositWithdrawalAction.do");
		addRequestParameter("method", "load");
		actionPerform();
		verifyForward("load_success");

		List<AccountActionEntity> trxnTypes = (List<AccountActionEntity>)request.getSession().getAttribute(AccountConstants.TRXN_TYPES);
		assertNotNull(trxnTypes);
		assertEquals(2, trxnTypes.size());
		
		List<CustomerBO> clientList = (List<CustomerBO>)request.getSession().getAttribute(SavingsConstants.CLIENT_LIST);
		assertNotNull(clientList);
		Boolean isBackDatedAllowed = (Boolean)request.getSession().getAttribute(SavingsConstants.IS_BACKDATED_TRXN_ALLOWED);
		assertNotNull(isBackDatedAllowed);
		group = savings.getCustomer();
		center = group.getParentCustomer();
		client1 = new CustomerPersistence().getCustomer(client1
				.getCustomerId());
		client2 = new CustomerPersistence().getCustomer(client2
				.getCustomerId());
	}
	
	public void testSuccessfullReLoad() throws Exception {
		createCenterAndGroup();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED, userContext);
		HibernateUtil.closeSession();
		
		savings = new SavingsPersistence().findById(savings.getAccountId());
		request.getSession().setAttribute(Constants.BUSINESS_KEY, savings);
		setRequestPathInfo("/savingsDepositWithdrawalAction.do");
		addRequestParameter("method", "reLoad");
		addRequestParameter("trxnTypeId", String.valueOf(AccountConstants.ACTION_SAVINGS_WITHDRAWAL));
		actionPerform();
		verifyForward("load_success");
		
		assertNotNull(request.getSession().getAttribute(MasterConstants.PAYMENT_TYPE));
	}
	
	public void testSuccessfullPrevious() throws Exception {
		setRequestPathInfo("/savingsDepositWithdrawalAction.do");
		addRequestParameter("method", "previous");
		actionPerform();
		verifyForward("previous_success");
	}

	private void createCenterAndGroup() {
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
				ClientConstants.STATUS_CLOSED, "1.1.1.1", group, new Date(
						System.currentTimeMillis()));
		client2 = TestObjectFactory.createClient("client2",
				ClientConstants.STATUS_ACTIVE, "1.1.1.2", group, new Date(
						System.currentTimeMillis()));
	}
}
