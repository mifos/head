package org.mifos.application.customer.client.struts.action;

import java.net.URISyntaxException;
import java.util.GregorianCalendar;

import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigImplementer;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigItf;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ClientTransferActionTest extends MifosMockStrutsTestCase{
	private CenterBO center;
	private GroupBO group;
	private ClientBO client;
	private OfficeBO office;
	
	@Override
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
		UserContext userContext = TestObjectFactory.getUserContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
		EntityMasterData.getInstance().init();
		FieldConfigItf fieldConfigItf=FieldConfigImplementer.getInstance();
		fieldConfigItf.init();		
		FieldConfigImplementer.getInstance();
		getActionServlet().getServletContext().setAttribute(Constants.FIELD_CONFIGURATION,fieldConfigItf.getEntityMandatoryFieldMap());
	}
	
	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		TestObjectFactory.cleanUp(office);
	}

	public void testLoad_transferToBranch() throws Exception {
		setRequestPathInfo("/clientTransferAction.do");
		addRequestParameter("method", "loadBranches");
		actionPerform();
		verifyForward(ActionForwards.loadBranches_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	public void testSuccessfulPreview_transferToBranch() throws Exception {
		createObjectsForClientTransfer();
		setRequestPathInfo("/clientTransferAction.do");
		addRequestParameter("method", "previewBranchTransfer");
		addRequestParameter("officeId", client.getOffice().getOfficeId().toString());
		addRequestParameter("officeName", client.getOffice().getOfficeName());
		actionPerform();
		verifyForward(ActionForwards.previewBranchTransfer_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	public void testFailure_transferToBranch() throws Exception {
		createObjectsForClientTransfer();		
		request.getSession().setAttribute(Constants.BUSINESS_KEY, client);
		setRequestPathInfo("/clientTransferAction.do");
		addRequestParameter("method", "transferToBranch");
		addRequestParameter("officeId", client.getOffice().getOfficeId().toString());
		addRequestParameter("officeName", client.getOffice().getOfficeName());
		actionPerform();
		verifyForward(ActionForwards.transferToBranch_failure.toString());		
	}
	
	public void testSuccessful_transferToBranch() throws Exception {
		createObjectsForClientTransfer();
		request.getSession().setAttribute(Constants.BUSINESS_KEY, client);
		setRequestPathInfo("/clientTransferAction.do");
		addRequestParameter("method", "transferToBranch");
		addRequestParameter("officeId", office.getOfficeId().toString());
		addRequestParameter("officeName", office.getOfficeName());
		actionPerform();
		verifyForward(ActionForwards.update_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		client = (ClientBO)TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
		assertEquals(client.getOffice().getOfficeId(), office.getOfficeId());
		assertEquals(CustomerStatus.CLIENT_HOLD, client.getStatus());
		office = client.getOffice();
	}
	
	public void testCancel() throws Exception {
		setRequestPathInfo("/clientTransferAction.do");
		addRequestParameter("method", "cancel");
		actionPerform();
		verifyForward(ActionForwards.cancel_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	private void createObjectsForClientTransfer()throws Exception{
		office = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, TestObjectFactory.getOffice(Short.valueOf("1")), "customer_office", "cust");
		client = TestObjectFactory.createClient("client_to_transfer",getMeeting(),CustomerStatus.CLIENT_ACTIVE.getValue(), new java.util.Date());
		HibernateUtil.closeSession();
	}
	
	private MeetingBO getMeeting() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		meeting.setMeetingStartDate(new GregorianCalendar());
		return meeting;
	}
}
