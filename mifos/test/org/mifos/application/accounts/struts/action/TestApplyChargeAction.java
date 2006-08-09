package org.mifos.application.accounts.struts.action;

import java.net.URISyntaxException;
import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.ApplicableCharge;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestApplyChargeAction extends MifosMockStrutsTestCase {

	private AccountBO accountBO;

	private UserContext userContext;
	
	private CustomerBO client;

	private CustomerBO group;

	private CustomerBO center;
	
	private MeetingBO meeting;

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

	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testLoad() {
		createInitialObjects();
		accountBO = getLoanAccount(client,meeting);
		setRequestPathInfo("/applyChargeAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("accountId", accountBO.getAccountId().toString());
		actionPerform();
		verifyForward("load_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		assertNotNull(SessionUtils.getAttribute(AccountConstants.APPLICABLE_CHARGE_LIST,request.getSession()));
		assertEquals("Size of the list should be 2",2,((List<ApplicableCharge>)SessionUtils.getAttribute(AccountConstants.APPLICABLE_CHARGE_LIST,request.getSession())).size());
	}
	
	public void testCancel() throws ServiceException, SystemException, ApplicationException {
		createInitialObjects();
		accountBO = getLoanAccount(client,meeting);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,
				getAccountBusinessService().getAccount(accountBO.getAccountId()), request
						.getSession());
		setRequestPathInfo("/applyChargeAction.do");
		addRequestParameter("method", "cancel");
		actionPerform();
		verifyForward("loanDetails_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	public void testUpdateSuccess() {
		createInitialObjects();
		accountBO = getLoanAccount(client,meeting);
		setRequestPathInfo("/applyChargeAction.do");
		addRequestParameter("method", "update");
		addRequestParameter("chargeType","-1");
		addRequestParameter("charge", "18");
		addRequestParameter("accountId", accountBO.getAccountId().toString());
		actionPerform();
		verifyForward("loanDetails_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		
	}
	
	private void createInitialObjects() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short
				.valueOf("13"), "1.4", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", GroupConstants.ACTIVE, "1.4.1", center, new Date(System
				.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client",ClientConstants.STATUS_ACTIVE,"1.4.1.1",group,new Date(System
				.currentTimeMillis()));
	}
	
	private LoanBO getLoanAccount(CustomerBO customer, MeetingBO meeting) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short
						.valueOf("1"), 300.0, 1.2, Short.valueOf("3"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", customer, Short
				.valueOf("5"), startDate, loanOffering);

	}
	
	private AccountBusinessService getAccountBusinessService()
			throws ServiceException {
		return (AccountBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Accounts);
	}

}
