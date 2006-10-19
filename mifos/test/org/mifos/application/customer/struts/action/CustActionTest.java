package org.mifos.application.customer.struts.action;

import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStateFlag;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CustActionTest extends MifosMockStrutsTestCase {

	private CenterBO center;

	private GroupBO group;

	private ClientBO client;

	private MeetingBO meeting;

	private String flowKey;

	private SavingsTestHelper helper = new SavingsTestHelper();

	private SavingsOfferingBO savingsOffering;

	private LoanBO loan1;

	private SavingsBO savings1;
	
	private LoanBO loan2;

	private SavingsBO savings2;
	
	private LoanBO loan3;

	private SavingsBO savings3;
	
	private UserContext userContext;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
				.getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/customer/struts-config.xml")
					.getPath());
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		
		request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
		Flow flow = new Flow();
		flowKey = String.valueOf(System.currentTimeMillis());
		FlowManager flowManager = new FlowManager();
		flowManager.addFLow(flowKey, flow,CustAction.class.getName());
		request.getSession(false).setAttribute(Constants.FLOWMANAGER,
				flowManager);
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(loan1);
		TestObjectFactory.cleanUp(savings1);
		TestObjectFactory.cleanUp(loan2);
		TestObjectFactory.cleanUp(savings2);
		TestObjectFactory.cleanUp(loan3);
		TestObjectFactory.cleanUp(savings3);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		userContext = null;
		super.tearDown();
	}
	public void testGetClosedAccounts() throws Exception{
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createCustomers();
		createAccounts();
		setRequestPathInfo("/custAction.do");
		addRequestParameter("method", "getClosedAccounts");
		addRequestParameter("customerId", group.getCustomerId().toString());
		addRequestParameter("globalCustNum", group.getGlobalCustNum());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.getAllClosedAccounts.toString());
		
		assertEquals("Size of closed savings accounts should be 1 for group",1,((List<AccountBO>)SessionUtils.getAttribute(AccountConstants.CLOSEDSAVINGSACCOUNTSLIST,request)).size());
		assertEquals("Size of closed loan accounts should be 1 for group",1,((List<AccountBO>)SessionUtils.getAttribute(AccountConstants.CLOSEDLOANACCOUNTSLIST,request)).size());
	}

	public void testGetBackToGroupDetailsPage() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createCustomers();
		setRequestPathInfo("/custAction.do");
		addRequestParameter("method", "getBackToDetailsPage");
		addRequestParameter("input", "group");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.group_detail_page.toString());
	}
	
	public void testGetBackToCenterDetailsPage() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createCustomers();
		setRequestPathInfo("/custAction.do");
		addRequestParameter("method", "getBackToDetailsPage");
		addRequestParameter("input", "center");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.center_detail_page.toString());
	}
	
	public void testGetBackToClientDetailsPage() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createCustomers();
		setRequestPathInfo("/custAction.do");
		addRequestParameter("method", "getBackToDetailsPage");
		addRequestParameter("input", "client");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.client_detail_page.toString());
	}

	private void createCustomers() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center",
				CustomerStatus.CENTER_ACTIVE.getValue(), "1.4", meeting,
				new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("group",
				CustomerStatus.GROUP_ACTIVE.getValue(), center.getSearchId()
						+ ".1", center, new Date());
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE.getValue(), "1.1.1", group,
				new Date(System.currentTimeMillis()));
	}

	private LoanBO getLoanAccount(CustomerBO customerBO,String offeringName,String shortName ) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				offeringName,shortName, Short.valueOf("2"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", customerBO, AccountState.LOANACC_APPROVED.getValue(), startDate, loanOffering);

	}

	private SavingsBO getSavingsAccount(CustomerBO customerBO,String offeringName,String shortName) throws Exception {
		savingsOffering = helper.createSavingsOffering(offeringName,shortName);
		return TestObjectFactory.createSavingsAccount("000100000000017", customerBO,
				AccountState.SAVINGS_ACC_PARTIALAPPLICATION.getValue(), new Date(System
						.currentTimeMillis()), savingsOffering);
	}
	
	private void createAccounts() throws Exception  {
		savings1 = getSavingsAccount(group,"fsaf6","ads6");
		savings1.changeStatus(AccountState.SAVINGS_ACC_CANCEL.getValue(),AccountStateFlag.SAVINGS_BLACKLISTED.getValue(),"status changed for savings");
		savings1.update();
		loan1 = getLoanAccount(group,"fdsfsdf","2cvs");
		loan1.update();
		loan1.changeStatus(AccountState.LOANACC_CANCEL.getValue(),AccountStateFlag.LOAN_OTHER.getValue(),"status changed for loan");
		HibernateUtil.commitTransaction();
		savings2 = getSavingsAccount(group,"fsaf65","ads5");
		loan2 = getLoanAccount(client,"rtwetrtwert","5rre");
		savings3 = getSavingsAccount(center,"fsaf26","ads2");
		loan3 = getLoanAccount(client,"fsdsdfqwq234","13er");
	}
}
