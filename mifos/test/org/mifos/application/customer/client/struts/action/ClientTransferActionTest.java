package org.mifos.application.customer.client.struts.action;

import java.net.URISyntaxException;
import java.sql.Date;
import java.util.GregorianCalendar;

import org.mifos.application.customer.business.CustomerHierarchyEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.customer.util.valueobjects.CustomerSearchInput;
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
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ClientTransferActionTest extends MifosMockStrutsTestCase{
	private CenterBO center;
	private GroupBO group;
	private CenterBO center1;
	private GroupBO group1;
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
		TestObjectFactory.cleanUp(group1);
		TestObjectFactory.cleanUp(center);
		TestObjectFactory.cleanUp(center1);
		TestObjectFactory.cleanUp(office);
		HibernateUtil.closeSession();
		super.tearDown();
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
	
	public void testLoad_updateParent() throws Exception {
		setRequestPathInfo("/clientTransferAction.do");
		addRequestParameter("method", "loadParents");
		actionPerform();
		verifyForward(ActionForwards.loadParents_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		CustomerSearchInput clientSearchInput = (CustomerSearchInput)SessionUtils.getAttribute(CustomerConstants.CUSTOMER_SEARCH_INPUT,request.getSession());
		assertNotNull(clientSearchInput);
		assertEquals(TestObjectFactory.getUserContext().getBranchId(),clientSearchInput.getOfficeId());
	}
	
	public void testPreview_transferToParent() throws Exception {
		createObjectsForTransferringClientInGroup();
		setRequestPathInfo("/clientTransferAction.do");
		addRequestParameter("method", "previewParentTransfer");
		addRequestParameter("parentGroupId", client.getParentCustomer().getCustomerId().toString());
		addRequestParameter("parentGroupName", client.getParentCustomer().getDisplayName());
		actionPerform();
		verifyForward(ActionForwards.previewParentTransfer_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	public void testFailure_transferToParent() throws Exception {
	
		createObjectsForTransferringClientInGroup();
		request.getSession().setAttribute(Constants.BUSINESS_KEY, client);
		setRequestPathInfo("/clientTransferAction.do");
		addRequestParameter("method", "updateParent");
		addRequestParameter("parentGroupId", client.getParentCustomer().getCustomerId().toString());
		addRequestParameter("parentGroupName", client.getParentCustomer().getDisplayName());
		actionPerform();
		verifyForward(ActionForwards.updateParent_failure.toString());
		group = (GroupBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		group1 = (GroupBO)TestObjectFactory.getObject(GroupBO.class,group1.getCustomerId());
		client = (ClientBO)TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
	}
	
	public void testSuccessful_transferToParent() throws Exception {
		createObjectsForTransferringClientInGroup();
		request.getSession().setAttribute(Constants.BUSINESS_KEY, client);
		setRequestPathInfo("/clientTransferAction.do");
		addRequestParameter("method", "updateParent");
		addRequestParameter("parentGroupId", group1.getCustomerId().toString());
		addRequestParameter("parentGroupName", group1.getDisplayName());
		actionPerform();
		verifyForward(ActionForwards.update_success.toString());
		client = (ClientBO)TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
		group = (GroupBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		group1 = (GroupBO)TestObjectFactory.getObject(GroupBO.class,group1.getCustomerId());
		center = (CenterBO)TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		assertEquals(group1.getCustomerId(),client.getParentCustomer().getCustomerId());
		assertEquals(0, group.getMaxChildCount().intValue());
		assertEquals(1, group1.getMaxChildCount().intValue());
		assertEquals(center1.getSearchId()+".1.1", client.getSearchId());
		CustomerHierarchyEntity currentHierarchy = client.getActiveCustomerHierarchy();
		assertEquals(group1.getCustomerId(),currentHierarchy.getParentCustomer().getCustomerId());
	}
	
	private void createObjectsForClientTransfer()throws Exception{
		office = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, TestObjectFactory.getOffice(Short.valueOf("1")), "customer_office", "cust");
		client = TestObjectFactory.createClient("client_to_transfer",getMeeting(),CustomerStatus.CLIENT_ACTIVE.getValue(), new java.util.Date());
		HibernateUtil.closeSession();
	}
	
	private void createObjectsForTransferringClientInGroup()throws Exception{
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meeting1 = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", CustomerStatus.CENTER_ACTIVE.getValue(),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", CustomerStatus.GROUP_ACTIVE.getValue(),
				center.getSearchId()+".1", center, new Date(System.currentTimeMillis()));
		center1 = TestObjectFactory.createCenter("Center1", CustomerStatus.CENTER_ACTIVE.getValue(),
				"1.1", meeting1, new Date(System.currentTimeMillis()));
		group1 = TestObjectFactory.createGroup("Group2", CustomerStatus.GROUP_ACTIVE.getValue(),
				center1.getSearchId()+".1", center1, new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client11", CustomerStatus.CLIENT_ACTIVE.getValue(),
				group.getSearchId()+".1", group, new Date(System.currentTimeMillis()));
		HibernateUtil.closeSession();
	}
	
	private MeetingBO getMeeting() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		meeting.setMeetingStartDate(new GregorianCalendar());
		return meeting;
	}
}
