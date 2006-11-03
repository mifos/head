package org.mifos.application.customer.struts.action;

import java.sql.Date;

import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCustomerAccountAction extends MifosMockStrutsTestCase {

	private ClientBO client;

	private GroupBO group;

	private CenterBO center;

	private MeetingBO meeting;
	
	private String flowKey;
	
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
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		flowKey = createFlow(request, CustomerAccountAction.class);
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
	}

	@Override
	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testLoadClientChargesDetails_client() {
		initialization("Client");
		addRequestParameter("globalCustNum", client.getGlobalCustNum());
		getRequest().getSession().setAttribute("security_param", "Client");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.client_detail_page.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testLoadClientChargesDetails_group() {
		initialization("Group");
		addRequestParameter("globalCustNum", group.getGlobalCustNum());
		getRequest().getSession().setAttribute("security_param", "Group");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.group_detail_page.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testLoadClientChargesDetails_center() {
		initialization("Center");
		addRequestParameter("globalCustNum", center.getGlobalCustNum());
		getRequest().getSession().setAttribute("security_param", "Center");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.center_detail_page.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	private void initialization(String customer) {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));

		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.4", meeting, new Date(System.currentTimeMillis()));
		if (!(customer == "Center"))
			group = TestObjectFactory.createGroup("Group",
					GroupConstants.ACTIVE, "1.4.1", center, new Date(System
							.currentTimeMillis()));
		if (!(customer == "Center" || customer == "Group"))
			client = TestObjectFactory.createClient("Client",
					CustomerStatus.CLIENT_ACTIVE, "1.4.1.1", group, new Date(
							System.currentTimeMillis()));
		setPath();
	}

	private void setPath() {
		setRequestPathInfo("/customerAccountAction.do");
		addRequestParameter("method", "load");
	}

}
