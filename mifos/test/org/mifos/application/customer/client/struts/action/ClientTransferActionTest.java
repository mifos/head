package org.mifos.application.customer.client.struts.action;

import java.util.List;

import org.mifos.application.customer.business.CustomerHierarchyEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerSearchInputView;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ClientTransferActionTest extends MifosMockStrutsTestCase{
	private CenterBO center;
	private GroupBO group;
	private CenterBO center1;
	private GroupBO group1;
	private ClientBO client;
	private OfficeBO office;
	private String flowKey;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UserContext userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());		
		flowKey = createFlow(request, ClientTransferAction.class);
	}
	
	@Override
	protected void tearDown() throws Exception {
		try {
			TestObjectFactory.cleanUp(client);
			TestObjectFactory.cleanUp(group);
			TestObjectFactory.cleanUp(group1);
			TestObjectFactory.cleanUp(center);
			TestObjectFactory.cleanUp(center1);
			TestObjectFactory.cleanUp(office);
		} catch (Exception e) {
			// TODO Whoops, cleanup didnt work, reset db
			TestDatabase.resetMySQLDatabase();
		}
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testLoad_transferToBranch() throws Exception {
		setRequestPathInfo("/clientTransferAction.do");
		addRequestParameter("method", "loadBranches");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
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
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.previewBranchTransfer_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	public void testFailure_transferToBranch() throws Exception {
		createObjectsForClientTransfer();	
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, client,request);
		setRequestPathInfo("/clientTransferAction.do");
		addRequestParameter("method", "transferToBranch");
		addRequestParameter("officeId", client.getOffice().getOfficeId().toString());
		addRequestParameter("officeName", client.getOffice().getOfficeName());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyActionErrors(new String[]{CustomerConstants.ERRORS_SAME_BRANCH_TRANSFER});
		verifyForward(ActionForwards.transferToBranch_failure.toString());		
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
	}
	
	public void testSuccessful_transferToBranch() throws Exception {
		createObjectsForClientTransfer();
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, client,request);
		setRequestPathInfo("/clientTransferAction.do");
		addRequestParameter("method", "transferToBranch");
		addRequestParameter("officeId", office.getOfficeId().toString());
		addRequestParameter("officeName", office.getOfficeName());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.update_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		client = TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
		assertEquals(client.getOffice().getOfficeId(), office.getOfficeId());
		assertEquals(CustomerStatus.CLIENT_HOLD, client.getStatus());
		office = client.getOffice();
	}
	
	public void testCancel() throws Exception {
		setRequestPathInfo("/clientTransferAction.do");
		addRequestParameter("method", "cancel");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.cancel_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	public void testLoad_updateParent() throws Exception {
		setRequestPathInfo("/clientTransferAction.do");
		addRequestParameter("method", "loadParents");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.loadParents_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		CustomerSearchInputView clientSearchInput = (CustomerSearchInputView)
			SessionUtils.getAttribute(CustomerConstants.CUSTOMER_SEARCH_INPUT,
				request.getSession());
		assertNotNull(clientSearchInput);
		assertEquals(TestObjectFactory.HEAD_OFFICE,
			clientSearchInput.getOfficeId());
	}
	
	public void testPreview_transferToParent() throws Exception {
		createObjectsForTransferringClientInGroup();
		setRequestPathInfo("/clientTransferAction.do");
		addRequestParameter("method", "previewParentTransfer");
		addRequestParameter("parentGroupId", client.getParentCustomer().getCustomerId().toString());
		addRequestParameter("parentGroupName", client.getParentCustomer().getDisplayName());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.previewParentTransfer_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	public void testFailure_transferToParent() throws Exception {
	
		createObjectsForTransferringClientInGroup();
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, client,request);
		setRequestPathInfo("/clientTransferAction.do");
		addRequestParameter("method", "updateParent");
		addRequestParameter("parentGroupId", client.getParentCustomer().getCustomerId().toString());
		addRequestParameter("parentGroupName", client.getParentCustomer().getDisplayName());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.updateParent_failure.toString());
		group = TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		group1 = TestObjectFactory.getObject(GroupBO.class,group1.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
	}
	
	public void testSuccessful_transferToParent() throws Exception {
		createObjectsForTransferringClientInGroup();
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, client,request);
		setRequestPathInfo("/clientTransferAction.do");
		addRequestParameter("method", "updateParent");
		addRequestParameter("parentGroupId", group1.getCustomerId().toString());
		addRequestParameter("parentGroupName", group1.getDisplayName());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.update_success.toString());
		client = TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		group1 = TestObjectFactory.getObject(GroupBO.class,group1.getCustomerId());
		center = TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		assertEquals(group1.getCustomerId(),client.getParentCustomer().getCustomerId());
		assertEquals(0, group.getMaxChildCount().intValue());
		assertEquals(1, group1.getMaxChildCount().intValue());
		assertEquals(center1.getSearchId()+".1.1", client.getSearchId());
		CustomerHierarchyEntity currentHierarchy = client.getActiveCustomerHierarchy();
		assertEquals(group1.getCustomerId(),currentHierarchy.getParentCustomer().getCustomerId());
	}
	
	public void testSuccessful_transferToParent_AuditLog() throws Exception {
		createObjectsForTransferringClientInGroup();
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, client,request);
		setRequestPathInfo("/clientTransferAction.do");
		addRequestParameter("method", "updateParent");
		addRequestParameter("parentGroupId", group1.getCustomerId().toString());
		addRequestParameter("parentGroupName", group1.getDisplayName());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.update_success.toString());
		client = TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		group1 = TestObjectFactory.getObject(GroupBO.class,group1.getCustomerId());
		center = TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		assertEquals(group1.getCustomerId(),client.getParentCustomer().getCustomerId());
		assertEquals(0, group.getMaxChildCount().intValue());
		assertEquals(1, group1.getMaxChildCount().intValue());
		assertEquals(center1.getSearchId()+".1.1", client.getSearchId());
		CustomerHierarchyEntity currentHierarchy = client.getActiveCustomerHierarchy();
		assertEquals(group1.getCustomerId(),currentHierarchy.getParentCustomer().getCustomerId());
		
		List<AuditLog> auditLogList=TestObjectFactory.getChangeLog(
				EntityType.CLIENT,client.getCustomerId());
		assertEquals(1,auditLogList.size());
		assertEquals(EntityType.CLIENT.getValue(),auditLogList.get(0).getEntityType());
		assertEquals(client.getCustomerId(),auditLogList.get(0).getEntityId());
		assertEquals(1,auditLogList.get(0).getAuditLogRecords().size());
		for(AuditLogRecord auditLogRecord :  auditLogList.get(0).getAuditLogRecords()){
			if(auditLogRecord.getFieldName().equalsIgnoreCase("Group Name"))
				matchValues(auditLogRecord,"Group", "Group2");
		}
		TestObjectFactory.cleanUpChangeLog();
	}
	
	public void testSuccessful_transferToBranch_AuditLog() throws Exception {
		createObjectsForClientTransfer();
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, client,request);
		setRequestPathInfo("/clientTransferAction.do");
		addRequestParameter("method", "transferToBranch");
		addRequestParameter("officeId", office.getOfficeId().toString());
		addRequestParameter("officeName", office.getOfficeName());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.update_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		client = TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
		assertEquals(client.getOffice().getOfficeId(), office.getOfficeId());
		assertEquals(CustomerStatus.CLIENT_HOLD, client.getStatus());
		office = client.getOffice();
		List<AuditLog> auditLogList=TestObjectFactory.getChangeLog(
				EntityType.CLIENT,client.getCustomerId());
		assertEquals(1,auditLogList.size());
		assertEquals(EntityType.CLIENT.getValue(),auditLogList.get(0).getEntityType());
		assertEquals(client.getCustomerId(),auditLogList.get(0).getEntityId());
		
		assertEquals(3,auditLogList.get(0).getAuditLogRecords().size());
		
		for(AuditLogRecord auditLogRecord :  auditLogList.get(0).getAuditLogRecords()){
			if(auditLogRecord.getFieldName().equalsIgnoreCase("Loan Officer Assigned"))
				matchValues(auditLogRecord,"mifos", "-");
			else if(auditLogRecord.getFieldName().equalsIgnoreCase("Status"))
				matchValues(auditLogRecord,"Active", "On Hold");
			else if(auditLogRecord.getFieldName().equalsIgnoreCase("Branch Office Name"))
				matchValues(auditLogRecord,"TestBranchOffice", "customer_office");
		}
		TestObjectFactory.cleanUpChangeLog();
	}
	
	private void createObjectsForClientTransfer()throws Exception{
		office = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE), "customer_office", "cust");
		client = TestObjectFactory.createClient("client_to_transfer",
			getMeeting(), CustomerStatus.CLIENT_ACTIVE);
		HibernateUtil.closeSession();
	}
	
	private void createObjectsForTransferringClientInGroup()throws Exception{
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		MeetingBO meeting1 = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", 
				CustomerStatus.GROUP_ACTIVE, center);
		center1 = TestObjectFactory.createCenter("Center1", 
				meeting1);
		group1 = TestObjectFactory.createGroupUnderCenter("Group2", 
				CustomerStatus.GROUP_ACTIVE, center1);
		client = TestObjectFactory.createClient("Client11", 
				CustomerStatus.CLIENT_ACTIVE,
				group);
		HibernateUtil.closeSession();
	}
	
	private MeetingBO getMeeting() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		return meeting;
	}
}
