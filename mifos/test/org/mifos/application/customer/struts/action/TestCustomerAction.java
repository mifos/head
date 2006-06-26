package org.mifos.application.customer.struts.action;

import java.net.URISyntaxException;
import java.sql.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.HibernateStartUp;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.TestObjectFactory;

import servletunit.struts.MockStrutsTestCase;

public class TestCustomerAction extends MockStrutsTestCase {
	
	private UserContext userContext;
	
	private CustomerBO client;

	private CustomerBO group;

	private CustomerBO center;

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
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testForwardWaiveChargeDue(){
		createInitialObjects();
		setRequestPathInfo("/customerAction.do");
		addRequestParameter("method", "waiveChargeDue");
		addRequestParameter("type","Client");
		AccountBO accountBO=client.getCustomerAccount();
		addRequestParameter("accountId",accountBO.getAccountId().toString());
		getRequest().getSession().putValue("security_param","Client");
		actionPerform();
		verifyForward("waiveChargesDue_Success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	public void testForwardWaiveChargeOverDue(){
		createInitialObjects();
		setRequestPathInfo("/customerAction.do");
		addRequestParameter("method", "waiveChargeOverDue");
		addRequestParameter("type","Client");
		AccountBO accountBO=client.getCustomerAccount();
		addRequestParameter("accountId",accountBO.getAccountId().toString());
		getRequest().getSession().putValue("security_param","Client");
		actionPerform();
		verifyForward("waiveChargesOverDue_Success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	public void testGetAllActivity(){
		createInitialObjects();
		setRequestPathInfo("/customerAction.do");
		addRequestParameter("method", "getAllActivity");
		addRequestParameter("type","Client");
		addRequestParameter("globalCustNum",client.getGlobalCustNum());
		getRequest().getSession().putValue("security_param","Client");
		actionPerform();
		verifyForward("viewClientActivity");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	
	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short
				.valueOf("13"), "1.1.1", center, new Date(System
				.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client",Short
				.valueOf("3"), "1.1.1", group, new Date(System
				.currentTimeMillis()));
	}
	
}
