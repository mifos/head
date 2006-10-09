package org.mifos.application.customer.struts.action;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.customer.business.CustomerHistoricalDataEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CustHistoricalDataActionTest extends MifosMockStrutsTestCase {

	private ClientBO client;

	private GroupBO group;

	private CenterBO center;

	private MeetingBO meeting;
	
	private String flowKey;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
				.getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/customer/struts-config.xml")
				.getPath());
		UserContext userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		
		request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
		
		Flow flow = new Flow();
		flowKey = String.valueOf(System.currentTimeMillis());
		FlowManager flowManager = new FlowManager();
		flowManager.addFLow(flowKey, flow);
		request.getSession(false).setAttribute(Constants.FLOWMANAGER,
				flowManager);
	}

	@Override
	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGetWhenCustHistoricalDataIsNull() {
		createInitialObjects();
		setRequestPathInfo("/custHistoricalDataAction.do");
		addRequestParameter("method", "get");
		addRequestParameter("globalCustNum", client.getGlobalCustNum());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward(ActionForwards.get_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals(new java.sql.Date(DateUtils
				.getCurrentDateWithoutTimeStamp().getTime()).toString(), request.getSession().getAttribute(CustomerConstants.MFIJOININGDATE).toString());
	}

	public void testGetWhenCustHistoricalDataIsNotNull()
			throws Exception {
		createInitialObjects();
		Integer oldLoanCycleNo = client.getPerformanceHistory().getLoanCycleNumber();
		CustomerHistoricalDataEntity customerHistoricalDataEntity = new CustomerHistoricalDataEntity(
				client);
		customerHistoricalDataEntity.setMfiJoiningDate(offSetCurrentDate(10));
		Date mfiDate = new Date(customerHistoricalDataEntity.getMfiJoiningDate().getTime());
		client.updateHistoricalData(customerHistoricalDataEntity,0);
		client.update();
		HibernateUtil.commitTransaction();
		assertEquals(oldLoanCycleNo,client.getPerformanceHistory().getLoanCycleNumber());
		assertEquals(mfiDate,new Date(client.getMfiJoiningDate().getTime()));
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request
				.getSession());
		setRequestPathInfo("/custHistoricalDataAction.do");
		addRequestParameter("method", "get");
		addRequestParameter("globalCustNum", client.getGlobalCustNum());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward(ActionForwards.get_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals(new java.sql.Date(DateUtils
				.getDateWithoutTimeStamp(mfiDate.getTime()).getTime()).toString(), request.getSession().getAttribute(CustomerConstants.MFIJOININGDATE).toString());
	}

	public void testLoadWhenCustHistoricalDataIsNull() {
		createInitialObjects();
		
		setRequestPathInfo("/custHistoricalDataAction.do");
		addRequestParameter("method", "get");
		addRequestParameter("globalCustNum", client.getGlobalCustNum());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward(ActionForwards.get_success.toString());

		setRequestPathInfo("/custHistoricalDataAction.do");
		addRequestParameter("method", "load");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward(ActionForwards.load_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testPreview() {
		createInitialObjects();
		setRequestPathInfo("/custHistoricalDataAction.do");
		addRequestParameter("method", "preview");
		actionPerform();
		verifyForward(ActionForwards.preview_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testPrevious() {
		createInitialObjects();
		setRequestPathInfo("/custHistoricalDataAction.do");
		addRequestParameter("method", "previous");
		actionPerform();
		verifyForward(ActionForwards.previous_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testCancel() {
		createInitialObjects();
		setRequestPathInfo("/custHistoricalDataAction.do");
		addRequestParameter("method", "cancel");
		addRequestParameter("type", "Client");
		actionPerform();
		verifyForward(ActionForwards.client_detail_page.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testUpdateWhenCustHistoricalDataIsNull() {
		createInitialObjects();
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request
				.getSession());
		setRequestPathInfo("/custHistoricalDataAction.do");
		addRequestParameter("method", "update");
		addRequestParameter("productName", "Test");
		addRequestParameter("loanAmount", "100");
		addRequestParameter("totalAmountPaid", "50");
		addRequestParameter("interestPaid", "10");
		addRequestParameter("missedPaymentsCount", "1");
		addRequestParameter("totalPaymentsCount", "2");
		addRequestParameter("commentNotes", "Test notes");
		addRequestParameter("loanCycleNumber", "1");
		addRequestParameter("type", "Client");
		addRequestParameter("mfiJoiningDate", DateHelper
				.getCurrentDate(((UserContext) request.getSession()
						.getAttribute("UserContext")).getPereferedLocale()));
		actionPerform();
		verifyForward(ActionForwards.update_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		assertEquals("Test", client.getHistoricalData().getProductName());
		assertEquals("Test notes", client.getHistoricalData().getNotes());
		assertEquals(new Money("100"), client.getHistoricalData()
				.getLoanAmount());
		assertEquals(new Money("50"), client.getHistoricalData()
				.getTotalAmountPaid());
		assertEquals(new Money("10"), client.getHistoricalData()
				.getInterestPaid());
		assertEquals(1, client.getHistoricalData().getMissedPaymentsCount()
				.intValue());
		assertEquals(2, client.getHistoricalData().getTotalPaymentsCount()
				.intValue());
		assertEquals(1, client.getHistoricalData().getLoanCycleNumber()
				.intValue());
		
		List<AuditLog> auditLogList=TestObjectFactory.getChangeLog(EntityType.CLIENT.getValue(),client.getCustomerId());
		assertEquals(1,auditLogList.size());
		assertEquals(EntityType.CLIENT.getValue(),auditLogList.get(0).getEntityType());
		assertEquals(client.getCustomerId(),auditLogList.get(0).getEntityId());
		
		for(AuditLogRecord auditLogRecord :  auditLogList.get(0).getAuditLogRecords()){
			if(auditLogRecord.getFieldName().equalsIgnoreCase("Product name")){
				matchValues(auditLogRecord,"-", "Test");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Number of missed payments")){
				matchValues(auditLogRecord,"-", "1");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Total number of payments")){
				matchValues(auditLogRecord,"-", "2");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Amount of loan")){
				matchValues(auditLogRecord,"-", "100.0");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Loan Cycle per Product")){
				matchValues(auditLogRecord,"-", "1");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Notes")){
				matchValues(auditLogRecord,"-", "Test notes");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Service Charge Paid")){
				matchValues(auditLogRecord,"-", "10.0");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Total amount paid")){
				matchValues(auditLogRecord,"-", "50.0");
			}
		}
				
		TestObjectFactory.cleanUpChangeLog();
	}

	public void testUpdateWhenCustHistoricalDataIsNotNull()
			throws Exception {
		createInitialObjects();
		Integer oldLoanCycleNo = client.getPerformanceHistory().getLoanCycleNumber();
		CustomerHistoricalDataEntity customerHistoricalDataEntity = new CustomerHistoricalDataEntity(
				client);
		Integer oldHistoricalLoanCycleNo = customerHistoricalDataEntity.getLoanCycleNumber();
		customerHistoricalDataEntity.setLoanCycleNumber(5);
		customerHistoricalDataEntity.setProductName("prd1");
		customerHistoricalDataEntity.setLoanAmount(new Money("100"));
		customerHistoricalDataEntity.setTotalAmountPaid(new Money("50"));
		customerHistoricalDataEntity.setMissedPaymentsCount(1);
		customerHistoricalDataEntity.setNotes("comment1");
		customerHistoricalDataEntity.setTotalPaymentsCount(1);
		customerHistoricalDataEntity.setInterestPaid(new Money("10"));
		
		client.updateHistoricalData(customerHistoricalDataEntity,oldHistoricalLoanCycleNo);
		client.update();
		HibernateUtil.commitTransaction();
		assertEquals(oldLoanCycleNo+5,client.getPerformanceHistory().getLoanCycleNumber().intValue());

		SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request
				.getSession());
		setRequestPathInfo("/custHistoricalDataAction.do");
		addRequestParameter("method", "update");
		addRequestParameter("productName", "Test");
		addRequestParameter("loanAmount", "200");
		addRequestParameter("totalAmountPaid", "150");
		addRequestParameter("interestPaid", "50");
		addRequestParameter("missedPaymentsCount", "2");
		addRequestParameter("totalPaymentsCount", "3");
		addRequestParameter("commentNotes", "Test notes");
		addRequestParameter("loanCycleNumber", "2");
		addRequestParameter("type", "Client");
		addRequestParameter("mfiJoiningDate", DateHelper
				.getCurrentDate(((UserContext) request.getSession()
						.getAttribute("UserContext")).getPereferedLocale()));
		actionPerform();
		verifyForward(ActionForwards.update_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		assertEquals("Test", client.getHistoricalData().getProductName());
		assertEquals("Test notes", client.getHistoricalData().getNotes());
		assertEquals(new Money("200"), client.getHistoricalData()
				.getLoanAmount());
		assertEquals(new Money("150"), client.getHistoricalData()
				.getTotalAmountPaid());
		assertEquals(new Money("50"), client.getHistoricalData()
				.getInterestPaid());
		assertEquals(2, client.getHistoricalData().getMissedPaymentsCount()
				.intValue());
		assertEquals(3, client.getHistoricalData().getTotalPaymentsCount()
				.intValue());
		assertEquals(2, client.getHistoricalData().getLoanCycleNumber()
				.intValue());

		List<AuditLog> auditLogList=TestObjectFactory.getChangeLog(EntityType.CLIENT.getValue(),client.getCustomerId());
		assertEquals(1,auditLogList.size());
		assertEquals(EntityType.CLIENT.getValue(),auditLogList.get(0).getEntityType());
		assertEquals(client.getCustomerId(),auditLogList.get(0).getEntityId());
		
		for(AuditLogRecord auditLogRecord :  auditLogList.get(0).getAuditLogRecords()){
			if(auditLogRecord.getFieldName().equalsIgnoreCase("Product name")){
				matchValues(auditLogRecord,"prd1", "Test");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Number of missed payments")){
				matchValues(auditLogRecord,"1", "2");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Total number of payments")){
				matchValues(auditLogRecord,"1", "3");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Amount of loan")){
				matchValues(auditLogRecord,"100.0", "200.0");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Loan Cycle per Product")){
				matchValues(auditLogRecord,"5", "2");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Notes")){
				matchValues(auditLogRecord,"comment1", "Test notes");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Service Charge Paid")){
				matchValues(auditLogRecord,"10.0", "50.0");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Total amount paid")){
				matchValues(auditLogRecord,"50.0", "150.0");
			}
		}
	}
	
	//TODO
	
	public void testGetHistoricalDataWhenCustHistoricalDataIsNull() throws Exception {
		createInitialObjects();
		setRequestPathInfo("/custHistoricalDataAction.do");
		addRequestParameter("method", "getHistoricalData");
		addRequestParameter("globalCustNum", group.getGlobalCustNum());
		getRequest().getSession().setAttribute("security_param", "Group");
		actionPerform();
		verifyForward(ActionForwards.getHistoricalData_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals(new java.sql.Date(DateUtils
				.getCurrentDateWithoutTimeStamp().getTime()).toString(), SessionUtils.getAttribute(CustomerConstants.MFIJOININGDATE,request).toString());
	}

	public void testGetHistoricalDataWhenCustHistoricalDataIsNotNull()
			throws Exception {
		createInitialObjects();
		CustomerHistoricalDataEntity customerHistoricalDataEntity = new CustomerHistoricalDataEntity(
				group);
		customerHistoricalDataEntity.setMfiJoiningDate(offSetCurrentDate(10));
		Date mfiDate = new Date(customerHistoricalDataEntity.getMfiJoiningDate().getTime());
		group.updateHistoricalData(customerHistoricalDataEntity,0);
		group.update();
		HibernateUtil.commitTransaction();
		assertEquals(mfiDate,new Date(group.getMfiJoiningDate().getTime()));
		setRequestPathInfo("/custHistoricalDataAction.do");
		addRequestParameter("method", "getHistoricalData");
		addRequestParameter("globalCustNum", group.getGlobalCustNum());
		getRequest().getSession().setAttribute("security_param", "Group");
		actionPerform();
		verifyForward(ActionForwards.getHistoricalData_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals(new java.sql.Date(DateUtils
				.getDateWithoutTimeStamp(mfiDate.getTime()).getTime()).toString(), SessionUtils.getAttribute(CustomerConstants.MFIJOININGDATE,request).toString());
	}

	public void testLoadHistoricalDataWhenCustHistoricalDataIsNull() {
		createInitialObjects();
		setRequestPathInfo("/custHistoricalDataAction.do");
		addRequestParameter("method", "getHistoricalData");
		addRequestParameter("globalCustNum", group.getGlobalCustNum());
		getRequest().getSession().setAttribute("security_param", "Group");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		flowKey = (String)request.getAttribute(Constants.CURRENTFLOWKEY);
		verifyForward(ActionForwards.getHistoricalData_success.toString());
		
		setRequestPathInfo("/custHistoricalDataAction.do");
		addRequestParameter("method", "loadHistoricalData");
		getRequest().getSession().setAttribute("security_param", "Group");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.loadHistoricalData_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testPreviewHistoricalData() {
		createInitialObjects();
		setRequestPathInfo("/custHistoricalDataAction.do");
		addRequestParameter("method", "previewHistoricalData");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.previewHistoricalData_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testPreviousHistoricalData() {
		createInitialObjects();
		setRequestPathInfo("/custHistoricalDataAction.do");
		addRequestParameter("method", "previousHistoricalData");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.previousHistoricalData_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testCancelHistoricalData() {
		createInitialObjects();
		setRequestPathInfo("/custHistoricalDataAction.do");
		addRequestParameter("method", "cancelHistoricalData");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("type", "Group");
		actionPerform();
		verifyForward(ActionForwards.group_detail_page.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testUpdateHistoricalDataWhenCustHistoricalDataIsNull() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createInitialObjects();
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, group, request);
		setRequestPathInfo("/custHistoricalDataAction.do");
		addRequestParameter("method", "updateHistoricalData");
		addRequestParameter("productName", "Test");
		addRequestParameter("loanAmount", "100");
		addRequestParameter("totalAmountPaid", "50");
		addRequestParameter("interestPaid", "10");
		addRequestParameter("missedPaymentsCount", "1");
		addRequestParameter("totalPaymentsCount", "2");
		addRequestParameter("commentNotes", "Test notes");
		addRequestParameter("loanCycleNumber", "1");
		addRequestParameter("type", "Group");
		addRequestParameter("mfiJoiningDate", DateHelper
				.getCurrentDate(((UserContext) request.getSession()
						.getAttribute("UserContext")).getPereferedLocale()));
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.updateHistoricalData_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertEquals("Test", group.getHistoricalData().getProductName());
		assertEquals("Test notes", group.getHistoricalData().getNotes());
		assertEquals(new Money("100"), group.getHistoricalData()
				.getLoanAmount());
		assertEquals(new Money("50"), group.getHistoricalData()
				.getTotalAmountPaid());
		assertEquals(new Money("10"), group.getHistoricalData()
				.getInterestPaid());
		assertEquals(1, group.getHistoricalData().getMissedPaymentsCount()
				.intValue());
		assertEquals(2, group.getHistoricalData().getTotalPaymentsCount()
				.intValue());
		assertEquals(1, group.getHistoricalData().getLoanCycleNumber()
				.intValue());
	}

	public void testUpdateHistoricalDataWhenCustHistoricalDataIsNotNull()
			throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createInitialObjects();
		CustomerHistoricalDataEntity customerHistoricalDataEntity = new CustomerHistoricalDataEntity(
				group);
		group.updateHistoricalData(customerHistoricalDataEntity,0);
		group.update();
		HibernateUtil.commitTransaction();

		SessionUtils.setAttribute(Constants.BUSINESS_KEY, group, request);
		setRequestPathInfo("/custHistoricalDataAction.do");
		addRequestParameter("method", "updateHistoricalData");
		addRequestParameter("productName", "Test");
		addRequestParameter("loanAmount", "200");
		addRequestParameter("totalAmountPaid", "150");
		addRequestParameter("interestPaid", "50");
		addRequestParameter("missedPaymentsCount", "2");
		addRequestParameter("totalPaymentsCount", "3");
		addRequestParameter("commentNotes", "Test notes");
		addRequestParameter("loanCycleNumber", "2");
		addRequestParameter("type", "Group");
		addRequestParameter("mfiJoiningDate", DateHelper
				.getCurrentDate(((UserContext) request.getSession()
						.getAttribute("UserContext")).getPereferedLocale()));
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.updateHistoricalData_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertEquals("Test", group.getHistoricalData().getProductName());
		assertEquals("Test notes", group.getHistoricalData().getNotes());
		assertEquals(new Money("200"), group.getHistoricalData()
				.getLoanAmount());
		assertEquals(new Money("150"), group.getHistoricalData()
				.getTotalAmountPaid());
		assertEquals(new Money("50"), group.getHistoricalData()
				.getInterestPaid());
		assertEquals(2, group.getHistoricalData().getMissedPaymentsCount()
				.intValue());
		assertEquals(3, group.getHistoricalData().getTotalPaymentsCount()
				.intValue());
		assertEquals(2, group.getHistoricalData().getLoanCycleNumber()
				.intValue());
	}

	private void createInitialObjects() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.4", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", GroupConstants.ACTIVE,
				"1.4.1", center, new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client",
				ClientConstants.STATUS_ACTIVE, "1.4.1.1", group, new Date(
						System.currentTimeMillis()));
	}

	private java.sql.Date offSetCurrentDate(int noOfDays) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day - noOfDays);
		return new java.sql.Date(currentDateCalendar.getTimeInMillis());
	}
}
