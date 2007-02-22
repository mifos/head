package org.mifos.application.accounts.struts.action;

import java.net.URISyntaxException;
import java.sql.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.ApplicableCharge;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
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
		request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
		flowKey = createFlow(request, ApplyChargeAction.class);
	}

	@Override
	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testLoad() throws Exception{
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createInitialObjects();
		accountBO = getLoanAccount(client,meeting);
		setRequestPathInfo("/applyChargeAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("accountId", accountBO.getAccountId().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward("load_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		assertNotNull(SessionUtils.getAttribute(AccountConstants.APPLICABLE_CHARGE_LIST,request));
		assertEquals("Size of the list should be 2",2,((List<ApplicableCharge>)SessionUtils.getAttribute(AccountConstants.APPLICABLE_CHARGE_LIST,request)).size());
	}
	
	public void testCancel() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createInitialObjects();
		accountBO = getLoanAccount(client,meeting);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,
				getAccountBusinessService().getAccount(accountBO.getAccountId()), request
						);
		setRequestPathInfo("/applyChargeAction.do");
		addRequestParameter("method", "cancel");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward("loanDetails_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	public void testUpdateSuccess() {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createInitialObjects();
		accountBO = getLoanAccount(client,meeting);
		setRequestPathInfo("/applyChargeAction.do");
		addRequestParameter("method", "update");
		addRequestParameter("chargeType","-1");
		addRequestParameter("charge", "18");
		addRequestParameter("accountId", accountBO.getAccountId().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward("loanDetails_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		
	}
	
	public void testUpdateFailureWith_Rate_GreaterThan999() {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createInitialObjects();
		accountBO = getLoanAccount(client,meeting);
		setRequestPathInfo("/applyChargeAction.do");
		addRequestParameter("method", "update");
		addRequestParameter("chargeType","-1");
		addRequestParameter("chargeAmount","999999");
		addRequestParameter("selectedChargeFormula","%LoanAmount");
		addRequestParameter("charge", "18");
		addRequestParameter("accountId", accountBO.getAccountId().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		assertEquals("Rate", 1,
				getErrrorSize(AccountConstants.RATE));
		
	}
	
	public void testValidate() throws Exception {
		setRequestPathInfo("/applyChargeAction.do");
		addRequestParameter("method", "validate");
		request.setAttribute("methodCalled", "update");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.update_failure.toString());
	}
	
	private void createInitialObjects() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client",CustomerStatus.CLIENT_ACTIVE,group);
	}
	
	private LoanBO getLoanAccount(CustomerBO customer, MeetingBO meeting) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		return TestObjectFactory.createLoanAccount("42423142341", customer, 
				AccountState.LOANACC_ACTIVEINGOODSTANDING, startDate, loanOffering);

	}
	
	private AccountBusinessService getAccountBusinessService()
			throws ServiceException {
		return (AccountBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Accounts);
	}

}
