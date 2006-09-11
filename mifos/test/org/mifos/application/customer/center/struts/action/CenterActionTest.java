package org.mifos.application.customer.center.struts.action;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.PositionEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.struts.actionforms.CenterCustActionForm;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingFrequency;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigImplementer;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigItf;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CenterActionTest extends MifosMockStrutsTestCase{
	private CenterBO center;
	private GroupBO group;
	private ClientBO client;
	private String flowKey;
	private SavingsTestHelper helper = new SavingsTestHelper();

	private SavingsOfferingBO savingsOffering;
	private SavingsBO savingsBO;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
					.getPath());
			setConfigFile(ResourceLoader.getURI(
					"org/mifos/application/customer/center/struts-config.xml")
					.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		UserContext userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext",TestObjectFactory.getActivityContext());
		Flow flow = new Flow();
		flowKey = String.valueOf(System.currentTimeMillis());
		FlowManager flowManager = new FlowManager();
		flowManager.addFLow(flowKey, flow);
		request.getSession(false).setAttribute(Constants.FLOWMANAGER,
				flowManager);
		EntityMasterData.getInstance().init();
		FieldConfigItf fieldConfigItf=FieldConfigImplementer.getInstance();
		fieldConfigItf.init();		
		FieldConfigImplementer.getInstance();
		getActionServlet().getServletContext().setAttribute(Constants.FIELD_CONFIGURATION,fieldConfigItf.getEntityMandatoryFieldMap());
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(savingsBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testLoad() throws Exception {
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.LOAN_OFFICER_LIST, request.getSession()));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST,request.getSession()));
		
		CenterCustActionForm actionForm = (CenterCustActionForm)request.getSession().getAttribute("centerCustActionForm");
		String currentDate = DateHelper.getCurrentDate(TestObjectFactory.getUserContext().getPereferedLocale());
		assertEquals(currentDate,actionForm.getMfiJoiningDate());
	}
	
	public void testFailurePreviewWithAllValuesNull() throws Exception {
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("officeId", "3");
		actionPerform();
		assertEquals("Center Name", 1, getErrrorSize(CustomerConstants.NAME));				
		assertEquals("Loan Officer", 1, getErrrorSize(CustomerConstants.LOAN_OFFICER));
		assertEquals("Meeting", 1, getErrrorSize(CustomerConstants.MEETING));
		verifyInputForward();
	}
	
	public void testFailurePreviewWithNameNotNull()  throws Exception{
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("officeId", "3");
		addRequestParameter("displayName", "center");
		actionPerform();
		assertEquals("Center Name", 0, getErrrorSize(CustomerConstants.NAME));				
		assertEquals("Loan Officer", 1, getErrrorSize(CustomerConstants.LOAN_OFFICER));
		assertEquals("Meeting", 1, getErrrorSize(CustomerConstants.MEETING));
		verifyInputForward();
	}
	
	public void testFailurePreviewWithLoanOfficerNotNull() throws Exception{
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("officeId", "3");
		addRequestParameter("displayName", "center");
		addRequestParameter("loanOfficerId", "1");
		actionPerform();
		assertEquals("Center Name", 0, getErrrorSize(CustomerConstants.NAME));				
		assertEquals("Loan Officer", 0, getErrrorSize(CustomerConstants.LOAN_OFFICER));
		assertEquals("Meeting", 1, getErrrorSize(CustomerConstants.MEETING));
		verifyInputForward();
	}
	
	public void testFailurePreviewWithMeetingNotNull() throws Exception{
		SessionUtils.setAttribute(CenterConstants.CENTER_MEETING,new MeetingBO(MeetingFrequency.MONTHLY, Short.valueOf("2"),MeetingType.CUSTOMERMEETING), request.getSession());
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("officeId", "3");
		addRequestParameter("displayName", "center");
		addRequestParameter("loanOfficerId", "1");
		actionPerform();
		verifyNoActionErrors();
	}
	
	public void testFailurePreview_WithoutMandatoryCustomField_IfAny() throws Exception{
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		actionPerform();
		
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request.getSession());
		boolean isCustomFieldMandatory = false;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			if(customFieldDef.isMandatory()){
				isCustomFieldMandatory = true;
				break;
			}
		}
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("officeId", "3");
		addRequestParameter("displayName", "center");
		addRequestParameter("loanOfficerId", "1");
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "");
			i++;
		}
		actionPerform();
		
		if(isCustomFieldMandatory)
			assertEquals("CustomField", 1, getErrrorSize(CustomerConstants.CUSTOM_FIELD));	
		else
			assertEquals("CustomField", 0, getErrrorSize(CustomerConstants.CUSTOM_FIELD));	
	}
	
	public void testFailurePreview_WithDuplicateFee() throws Exception{
		List<FeeView> feesToRemove = getFees();
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		actionPerform();
		List<FeeView> feeList = (List<FeeView>)SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, request.getSession());
		FeeView fee = feeList.get(0);
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "preview");		
		addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
		addRequestParameter("selectedFee[0].amount", "100");
		addRequestParameter("selectedFee[1].feeId", fee.getFeeId());
		addRequestParameter("selectedFee[1].amount", "150");
		actionPerform();		
		assertEquals("Fee", 1, getErrrorSize(CustomerConstants.FEE));
		removeFees(feesToRemove);
	}
	
	public void testFailurePreview_WithFee_WithoutFeeAmount() throws Exception{
		List<FeeView> feesToRemove = getFees();
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		actionPerform();
		List<FeeView> feeList = (List<FeeView>)SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, request.getSession());
		FeeView fee = feeList.get(0);
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "preview");		
		addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
		addRequestParameter("selectedFee[0].amount", "");
		actionPerform();
		assertEquals("Fee", 1, getErrrorSize(CustomerConstants.FEE));
		removeFees(feesToRemove);
	}
	
	public void testSuccessfulPreview() throws Exception {
		List<FeeView> feesToRemove = getFees();
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		actionPerform();
		SessionUtils.setAttribute(CenterConstants.CENTER_MEETING,new MeetingBO(MeetingFrequency.MONTHLY, Short.valueOf("2"),MeetingType.CUSTOMERMEETING), request.getSession());
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request.getSession());
		List<FeeView> feeList = (List<FeeView>)SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, request.getSession());
		FeeView fee = feeList.get(0);
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "preview");	
		addRequestParameter("displayName", "center");
		addRequestParameter("loanOfficerId", "1");
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "11");
			i++;
		}
		addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
		addRequestParameter("selectedFee[0].amount", fee.getAmount());
		actionPerform();
		
		assertEquals(0, getErrrorSize());
		
		verifyForward(ActionForwards.preview_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		removeFees(feesToRemove);
	}
	
	public void testSuccessfulPrevious() throws Exception {
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "previous");
		actionPerform();
		verifyForward(ActionForwards.previous_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	public void testSuccessfulCreate() throws Exception {
		getFees();
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		actionPerform();
		SessionUtils.setAttribute(CenterConstants.CENTER_MEETING, getMeeting(), request.getSession());
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request.getSession());
		List<FeeView> feeList = (List<FeeView>)SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, request.getSession());
		FeeView fee = feeList.get(0);
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "preview");	
		addRequestParameter("displayName", "center");
		addRequestParameter("loanOfficerId", "1");
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "11");
			i++;
		}
		addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
		addRequestParameter("selectedFee[0].amount", fee.getAmount());
		actionPerform();
		verifyForward(ActionForwards.preview_success.toString());
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "create");
		actionPerform();
		verifyNoActionErrors();		
		verifyForward(ActionForwards.create_success.toString());
		
		CenterCustActionForm actionForm = (CenterCustActionForm)request.getSession().getAttribute("centerCustActionForm");
		center = (CenterBO)TestObjectFactory.getObject(CenterBO.class, new Integer(actionForm.getCustomerId()).intValue());
	}
	
	public void testManage() throws Exception {		
		createAndSetCenterInSession();
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("officeId", "3");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.manage_success.toString());
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.LOAN_OFFICER_LIST, request.getSession()));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST,request.getSession()));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.CLIENT_LIST,request.getSession()));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.POSITIONS,request.getSession()));
		
		CenterCustActionForm actionForm = (CenterCustActionForm)request.getSession().getAttribute("centerCustActionForm");
		assertEquals(center.getPersonnel().getPersonnelId(), actionForm.getLoanOfficerIdValue());
	}
	
	public void testFailureEditPreviewWithLoanOfficerNull() throws Exception {
		createAndSetCenterInSession();
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("officeId", "3");
		actionPerform();
		
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter("loanOfficerId", "");
		actionPerform();
		assertEquals("Loan Officer", 1, getErrrorSize(CustomerConstants.LOAN_OFFICER));
		verifyInputForward();
	}
	
	public void testFailureEditPreviewWith_MandatoryCustomFieldNull() 
	throws Exception {
		createAndSetCenterInSession();
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("officeId", "3");
		actionPerform();
		
		List<CustomFieldDefinitionEntity> customFieldDefs = 
			(List<CustomFieldDefinitionEntity>) SessionUtils.getAttribute(
				CustomerConstants.CUSTOM_FIELDS_LIST, request.getSession());
		boolean isCustomFieldMandatory = false;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			if(customFieldDef.isMandatory()){
				isCustomFieldMandatory = true;
				break;
			}
		}
		
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter("loanOfficerId", "");
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", 
				customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "");
			i++;
		}
		actionPerform();
		if(isCustomFieldMandatory)
			assertEquals("CustomField", 1, 
				getErrrorSize(CustomerConstants.CUSTOM_FIELD));	
		else
			assertEquals("CustomField", 0, 
				getErrrorSize(CustomerConstants.CUSTOM_FIELD));	
		verifyInputForward();
	}
	
	
	public void testSuccessfulEditPreview() throws Exception {
		createAndSetCenterInSession();
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("officeId", "3");
		actionPerform();
		
		List<CustomFieldDefinitionEntity> customFieldDefs = 
			(List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(
				CustomerConstants.CUSTOM_FIELDS_LIST, request.getSession());
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "editPreview");	
		addRequestParameter("displayName", "center");
		addRequestParameter("loanOfficerId", "1");
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", 
				customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "11");
			i++;
		}
		actionPerform();
		
		assertEquals(0, getErrrorSize());
		
		verifyForward(ActionForwards.editpreview_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	public void testSuccessfulUpdate() throws Exception {
		center = new CenterBO(
			TestObjectFactory.getUserContext(),"center", 
			new Address(), null, null, null, null, Short.valueOf("3"), 
			getMeeting(), Short.valueOf("1") );
		center.save();
		HibernateUtil.closeSession();
		center = (CenterBO)TestObjectFactory.getObject(
			CenterBO.class, new Integer(center.getCustomerId()).intValue());
		SessionUtils.setAttribute(
			Constants.BUSINESS_KEY, center, request.getSession());
		
		createGroupAndClient();
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("officeId", "3");
		actionPerform();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request.getSession());
		List<PositionEntity> positions = (List<PositionEntity>)SessionUtils.getAttribute(CustomerConstants.POSITIONS,request.getSession());
		
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "editPreview");	
		addRequestParameter("displayName", "center");
		addRequestParameter("loanOfficerId", "1");
		addRequestParameter("externalId", "12");
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "11");
			addRequestParameter("customField["+ i +"].fieldType", customFieldDef.getFieldType().toString());
			i++;
		}
		i = 0;
		for(PositionEntity position: positions){
			addRequestParameter("customerPosition["+ i +"].positionId", position.getId().toString());
			addRequestParameter("customerPosition["+ i +"].customerId", "" );
			i++;
		}
		actionPerform();
		assertEquals(0, getErrrorSize());
		
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "update");
		actionPerform();
		verifyForward(ActionForwards.update_success.toString());
		assertEquals(positions.size(), center.getCustomerPositions().size());
		assertEquals("12", center.getExternalId());
		center = (CenterBO)TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());
		group = (GroupBO)TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		client = (ClientBO)TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
		
	}
	
	
	private void createAndSetCenterInSession(){
		String name = "manage_center";
		center = TestObjectFactory.createCenter(
			name, CustomerStatus.CENTER_ACTIVE.getValue(), "", getMeeting(), 
			new Date());
		HibernateUtil.closeSession();
		center = (CenterBO)TestObjectFactory.getObject(
			CenterBO.class, new Integer(center.getCustomerId()).intValue());
		SessionUtils.setAttribute(
			Constants.BUSINESS_KEY, center, request.getSession());
	}
	

	
	public void testSuccessfulEditPrevious() throws Exception {
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "editPrevious");
		actionPerform();
		verifyForward(ActionForwards.editprevious_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	private MeetingBO getMeeting(){
		MeetingBO meeting = new MeetingBO(MeetingFrequency.MONTHLY, Short.valueOf("2"),MeetingType.CUSTOMERMEETING, Short.valueOf("2"), Short.valueOf("2"));
		meeting.setMeetingStartDate(Calendar.getInstance());
		return meeting;
	}
	
	private List<FeeView> getFees() {
		List<FeeView> fees = new ArrayList<FeeView>();
		AmountFeeBO fee1 = (AmountFeeBO) TestObjectFactory
				.createPeriodicAmountFee("PeriodicAmountFee",
						FeeCategory.CENTER, "200", MeetingFrequency.WEEKLY,
						Short.valueOf("2"));
		fees.add(new FeeView(fee1));
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		return fees;
	}
	
	private void removeFees(List<FeeView> feesToRemove){
		for(FeeView fee :feesToRemove){
			TestObjectFactory.cleanUp(new FeePersistence().getFee(fee.getFeeIdValue()));
		}
	}
	
	private void createGroupAndClient(){
		group = TestObjectFactory.createGroup("group", CustomerStatus.GROUP_ACTIVE.getValue(), center.getSearchId()+".1", center, new Date());
		client = TestObjectFactory.createClient("client",CustomerStatus.CLIENT_ACTIVE.getValue(), group.getSearchId()+".1", group, new Date());
	}
	
	public void testGet() throws Exception{
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short
				.valueOf("13"), "1.4", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", GroupConstants.ACTIVE, center.getSearchId()+".1", center, new Date(System
				.currentTimeMillis()));
		savingsBO = getSavingsAccount("fsaf6","ads6",center);
		HibernateUtil.closeSession();
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "get");
		addRequestParameter("globalCustNum", center.getGlobalCustNum());
		actionPerform();
		verifyForward(ActionForwards.get_success.toString());
		CustomerBO centerBO =(CenterBO) request.getSession().getAttribute(Constants.BUSINESS_KEY);
		assertNotNull(center);
		assertEquals(center.getCustomerId(),centerBO.getCustomerId());
		List children =(List) request.getSession().getAttribute(CenterConstants.GROUP_LIST);
		assertNotNull(children);
		assertEquals(1,children.size());
		assertEquals("Size of the active accounts should be 1",1,((List<SavingsBO>)SessionUtils.getAttribute(ClientConstants.CUSTOMERSAVINGSACCOUNTSINUSE,request.getSession())).size());
		HibernateUtil.closeSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		savingsBO = (SavingsBO) TestObjectFactory.getObject(SavingsBO.class,savingsBO.getAccountId());
	}
	
	private SavingsBO getSavingsAccount(String offeringName,String shortName,CustomerBO customer) {
		savingsOffering = helper.createSavingsOffering(offeringName,shortName);
		return TestObjectFactory.createSavingsAccount("000100000000017", customer,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION, new Date(System
						.currentTimeMillis()), savingsOffering);
	}
}
