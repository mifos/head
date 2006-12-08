package org.mifos.application.customer.group.struts.action;

import java.net.URISyntaxException;
import java.util.List;

import org.mifos.application.customer.business.CustomerHierarchyEntity;
import org.mifos.application.customer.business.CustomerMovementEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.util.helpers.CenterSearchInput;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
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
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class GroupTransferActionTest extends MifosMockStrutsTestCase{
	private CenterBO center;
	private GroupBO group;
	private CenterBO center1;
	private GroupBO group1;
	private ClientBO client;
	private OfficeBO office;
	private Short officeId = 3;
	private Short personnelId = 3;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
					.getPath());
			setConfigFile(ResourceLoader.getURI(
					"org/mifos/application/customer/group/struts-config.xml")
					.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		UserContext userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
		FlowManager flowManager = new FlowManager();
		request.getSession(false).setAttribute(Constants.FLOWMANAGER,
				flowManager);		
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
		loadOffices();
		verifyForward(ActionForwards.loadBranches_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	public void testSuccessfulPreview_transferToBranch() throws Exception {
		loadOffices();
		HibernateUtil.closeSession();
		setRequestPathInfo("/groupTransferAction.do");
		addRequestParameter("method", "previewBranchTransfer");
		addRequestParameter("officeId", group.getOffice().getOfficeId().toString());
		addRequestParameter("officeName", group.getOffice().getOfficeName());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		
		verifyForward(ActionForwards.previewBranchTransfer_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	public void testFailure_transferToBranch() throws Exception {
		loadOffices();	
		HibernateUtil.closeSession();
		setRequestPathInfo("/groupTransferAction.do");
		addRequestParameter("method", "previewBranchTransfer");
		addRequestParameter("officeId", group.getOffice().getOfficeId().toString());
		addRequestParameter("officeName", group.getOffice().getOfficeName());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		
		setRequestPathInfo("/groupTransferAction.do");
		addRequestParameter("method", "transferToBranch");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyActionErrors(new String[]{CustomerConstants.ERRORS_SAME_BRANCH_TRANSFER});
		verifyForward(ActionForwards.transferToBranch_failure.toString());
		group = (GroupBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());		 
	}
	
	public void testSuccessful_transferToBranch() throws Exception {
		TestObjectFactory.cleanUpChangeLog();
		office = createOffice();
		loadOffices();		
		HibernateUtil.closeSession();
		setRequestPathInfo("/groupTransferAction.do");
		addRequestParameter("method", "previewBranchTransfer");
		addRequestParameter("officeId", office.getOfficeId().toString());
		addRequestParameter("officeName", office.getOfficeName());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
	
		setRequestPathInfo("/groupTransferAction.do");
		addRequestParameter("method", "transferToBranch");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
	
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.update_success.toString());
		
		group = (GroupBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		assertEquals(office.getOfficeId(), group.getOffice().getOfficeId());
		assertEquals(CustomerStatus.GROUP_HOLD, group.getStatus());
		
		CustomerMovementEntity customerMovement = group.getActiveCustomerMovement();
		assertNotNull(customerMovement);
		assertEquals(office.getOfficeId(), customerMovement.getOffice().getOfficeId());
		office = group.getOffice();
		
		List<AuditLog> auditLogList=TestObjectFactory.getChangeLog(EntityType.GROUP.getValue(),group.getCustomerId());
		assertEquals(1,auditLogList.size());
		assertEquals(EntityType.GROUP.getValue(),auditLogList.get(0).getEntityType());
		assertEquals(3,auditLogList.get(0).getAuditLogRecords().size());
		for(AuditLogRecord auditLogRecord :  auditLogList.get(0).getAuditLogRecords()){
			if(auditLogRecord.getFieldName().equalsIgnoreCase("Branch Office Name")){
				assertEquals("TestBranchOffice",auditLogRecord.getOldValue());
				assertEquals("customer_office",auditLogRecord.getNewValue());
			}
		}
		TestObjectFactory.cleanUpChangeLog();
	}
	
	public void testLoad_updateParent() throws Exception {
		loadParents();
		verifyForward(ActionForwards.loadParents_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		CenterSearchInput centerSearchInput = (CenterSearchInput)SessionUtils.getAttribute(GroupConstants.CENTER_SEARCH_INPUT,request.getSession());
		assertNotNull(centerSearchInput);
		assertEquals(TestObjectFactory.getUserContext().getBranchId(),centerSearchInput.getOfficeId());
	}
	
	public void testSuccessfulPreview_transferToCenter() throws Exception {
		loadParents();
		HibernateUtil.closeSession();
		
		setRequestPathInfo("/groupTransferAction.do");
		addRequestParameter("method", "previewParentTransfer");
		addRequestParameter("centerSystemId", center.getGlobalCustNum());
		addRequestParameter("centerName", center.getDisplayName());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		
		verifyForward(ActionForwards.previewParentTransfer_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	public void testFailure_transferToCenter() throws Exception {
		loadParents();
		HibernateUtil.closeSession();
		
		setRequestPathInfo("/groupTransferAction.do");
		addRequestParameter("method", "previewParentTransfer");
		addRequestParameter("centerSystemId", center.getGlobalCustNum());
		addRequestParameter("centerName", center.getDisplayName());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		
		setRequestPathInfo("/groupTransferAction.do");
		addRequestParameter("method", "transferToCenter");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyActionErrors(new String[]{CustomerConstants.ERRORS_SAME_PARENT_TRANSFER});
		verifyForward(ActionForwards.transferToCenter_failure.toString());
		
		group = (GroupBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		center = (CenterBO)TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		center1 = (CenterBO)TestObjectFactory.getObject(CenterBO.class,center1.getCustomerId());
		office = TestObjectFactory.getOffice(office.getOfficeId());
	}
	
	public void testSuccessful_transferToCenter() throws Exception {
		TestObjectFactory.cleanUpChangeLog();
		loadParents();
		HibernateUtil.closeSession();
		
		setRequestPathInfo("/groupTransferAction.do");
		addRequestParameter("method", "previewParentTransfer");
		addRequestParameter("centerSystemId", center1.getGlobalCustNum());
		addRequestParameter("centerName", center1.getDisplayName());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		
		setRequestPathInfo("/groupTransferAction.do");
		addRequestParameter("method", "transferToCenter");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		HibernateUtil.closeSession();
		
		group = (GroupBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		center = (CenterBO)TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		center1 = (CenterBO)TestObjectFactory.getObject(CenterBO.class,center1.getCustomerId());
		office = TestObjectFactory.getOffice(office.getOfficeId());
		
		assertEquals(center1.getCustomerId(), group.getParentCustomer().getCustomerId());
		
		CustomerHierarchyEntity customerHierarchy = group.getActiveCustomerHierarchy();
		assertEquals(center1.getCustomerId(), customerHierarchy.getParentCustomer().getCustomerId());
		assertEquals(group.getCustomerId(), customerHierarchy.getCustomer().getCustomerId());
		List<AuditLog> auditLogList=TestObjectFactory.getChangeLog(EntityType.GROUP.getValue(),new Integer(group.getCustomerId().toString()));
		assertEquals(1,auditLogList.size());
		assertEquals(EntityType.GROUP.getValue(),auditLogList.get(0).getEntityType());
		for(AuditLogRecord auditLogRecord :  auditLogList.get(0).getAuditLogRecords()){
			if(auditLogRecord.getFieldName().equalsIgnoreCase("Kendra Name")){
				matchValues(auditLogRecord,"Center","MyCenter");
			}
		}
		TestObjectFactory.cleanUpChangeLog();

	}
	
	public void testCancel() throws Exception {
		loadOffices();
		HibernateUtil.closeSession();
		setRequestPathInfo("/groupTransferAction.do");
		addRequestParameter("method", "cancel");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward(ActionForwards.cancel_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	private void loadParents() throws Exception{
		center = createCenter("Center", officeId);
		office = createOffice();
		center1 = createCenter("MyCenter", office.getOfficeId());
		group = createGroup("group", center);
		startFlowForGroup();
		setRequestPathInfo("/groupTransferAction.do");
		addRequestParameter("method", "loadParents");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();	
	}
	
	private void loadOffices() {
		group = createGroupUnderBranch(CustomerStatus.GROUP_ACTIVE, officeId);
		startFlowForGroup();
		setRequestPathInfo("/groupTransferAction.do");
		addRequestParameter("method", "loadBranches");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
	}
	
	private void startFlowForGroup(){
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("globalCustNum", group.getGlobalCustNum());
		addRequestParameter("method", "get");
		actionPerform();
	}
	
	private GroupBO createGroupUnderBranch(CustomerStatus groupStatus, Short officeId){
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createGroupUnderBranch("group1",groupStatus, officeId, meeting, personnelId);
	}
	
	private CenterBO createCenter(String name, Short officeId){
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		return  TestObjectFactory.createCenter(name, meeting, officeId, personnelId);
	}
	
	private OfficeBO createOffice()throws Exception{
		return TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, TestObjectFactory.getOffice(Short.valueOf("1")), "customer_office", "cust");
	}
	
	private GroupBO createGroup(String name, CenterBO center){
		return TestObjectFactory.createGroupUnderCenter(name, CustomerStatus.GROUP_ACTIVE, center);
	}
	
	private void createCustomers(){
		center = createCenter("center1", officeId);
		group = createGroup("group", center);
	}
}
