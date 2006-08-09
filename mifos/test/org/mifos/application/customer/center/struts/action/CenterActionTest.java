package org.mifos.application.customer.center.struts.action;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.struts.actionforms.CenterCustActionForm;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingFrequency;
import org.mifos.application.meeting.util.helpers.MeetingType;
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

public class CenterActionTest extends MifosMockStrutsTestCase{
	private CenterBO center;
	
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
		TestObjectFactory.cleanUp(center);
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
		try{
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "create");
		actionPerform();
		verifyNoActionErrors();
		}catch(Exception e){
			e.printStackTrace();
		}
		verifyForward(ActionForwards.create_success.toString());
		
		CenterCustActionForm actionForm = (CenterCustActionForm)request.getSession().getAttribute("centerCustActionForm");
		center = (CenterBO)TestObjectFactory.getObject(CenterBO.class, new Integer(actionForm.getCustomerId()).intValue());
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
}
