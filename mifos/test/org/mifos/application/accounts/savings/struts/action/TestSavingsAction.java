package org.mifos.application.accounts.savings.struts.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountStateMachines;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsRecentActivityView;
import org.mifos.application.accounts.savings.business.SavingsTransactionHistoryView;
import org.mifos.application.accounts.savings.business.service.SavingsBusinessService;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.SavingsPaymentData;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.PrdOfferingView;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestSavingsAction extends MifosMockStrutsTestCase {

	private String flowKey;

	private UserContext userContext;

	private CustomerBO group;

	private CustomerBO center;

	private SavingsBO savings;

	private SavingsBO savings3;

	private SavingsOfferingBO savingsOffering;

	private SavingsOfferingBO savingsOffering1;

	private SavingsOfferingBO savingsOffering2;

	private SavingsOfferingBO savingsOffering3;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml").getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/accounts/savings/struts-config.xml")
				.getPath());
		userContext = TestObjectFactory.getContext();
		userContext.setPereferedLocale(new Locale("en", "US"));
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		request.getSession(false).setAttribute("ActivityContext",
				TestObjectFactory.getActivityContext());
		Flow flow = new Flow();
		flowKey = String.valueOf(System.currentTimeMillis());
		FlowManager flowManager = new FlowManager();
		flowManager.addFLow(flowKey, flow);
		request.getSession(false).setAttribute(Constants.FLOWMANAGER,
				flowManager);
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

	}

	@Override
	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(savings);
		TestObjectFactory.cleanUp(savings3);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		TestObjectFactory.removeObject(savingsOffering);
		TestObjectFactory.removeObject(savingsOffering1);
		TestObjectFactory.removeObject(savingsOffering2);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	private void createAndAddObjects() throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering("sav prd1", "prd1");
		SavingsBO savingsObj = new SavingsBO(userContext, savingsOffering,
				group, AccountState.SAVINGS_ACC_APPROVED, savingsOffering
						.getRecommendedAmount(), getCustomFieldView());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savingsObj,request);
		addRequestParameter("selectedPrdOfferingId", savingsOffering
				.getPrdOfferingId().toString());

	}

	private void createAndAddObjectsForCreate() throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering("sav prd1", "prd1");
		SessionUtils.setAttribute(SavingsConstants.CLIENT, group, request);
		SessionUtils.setAttribute(SavingsConstants.PRDOFFCERING,
				savingsOffering, request);
	}

	private void createAndAddObjects(AccountState state) throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering("sav prd1", "prd1");
		addRequestParameter("input", "preview");
		savings = createSavingsAccount("000X00000000013", savingsOffering,
				state.getValue());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
		addRequestParameter("selectedPrdOfferingId", savingsOffering
				.getPrdOfferingId().toString());

	}

	private List<CustomFieldView> getCustomFieldView() {
		List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();
		customFields.add(new CustomFieldView(new Short("8"), "13", null));
		return customFields;

	}

	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group_Active_test", Short
				.valueOf("9"), "1.1.1", center, new Date(System
				.currentTimeMillis()));
	}

	private SavingsOfferingBO createSavingsOffering(String prdOfferingName,
			String shortName) {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createSavingsOffering(prdOfferingName,
				shortName, Short.valueOf("2"), new Date(System
						.currentTimeMillis()), Short.valueOf("2"), 300.0, Short
						.valueOf("1"), 1.2, 200.0, 200.0, Short.valueOf("2"),
				Short.valueOf("1"), meetingIntCalc, meetingIntPost);
	}

	private SavingsBO createSavingsAccount(String globalAccountNum,
			SavingsOfferingBO savingsOffering, short accountStateId)
			throws Exception {
		return TestObjectFactory.createSavingsAccount(globalAccountNum, group,
				accountStateId, new Date(), savingsOffering, userContext);
	}

	public void testGetPrdOfferings() throws Exception {
		createInitialObjects();
		savingsOffering1 = createSavingsOffering("sav prd1", "prd1");
		savingsOffering2 = createSavingsOffering("sav prd2", "prd2");
		addRequestParameter("customerId", group.getCustomerId().toString());
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "getPrdOfferings");
		actionPerform();
		verifyForward("getPrdOfferings_success");
		List<PrdOfferingView> savingPrds = (List<PrdOfferingView>) SessionUtils
				.getAttribute(SavingsConstants.SAVINGS_PRD_OFFERINGS, request);
		assertEquals(Integer.valueOf("2").intValue(), savingPrds.size());
		CustomerBO client = (CustomerBO) SessionUtils.getAttribute(
				SavingsConstants.CLIENT, request);
		assertEquals(group.getCustomerId(), client.getCustomerId());

	}

	public void testSuccessfullLoad() throws Exception {

		createAndAddObjects();
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "load");
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(
				MasterConstants.INTEREST_CAL_TYPES, request));
		assertNotNull(SessionUtils.getAttribute(MasterConstants.SAVINGS_TYPE,
				request));
		assertNotNull(SessionUtils.getAttribute(
				MasterConstants.RECOMMENDED_AMOUNT_UNIT, request));
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.CUSTOM_FIELDS,
				request));
		assertEquals(SessionUtils.getAttribute(SavingsConstants.PRDOFFCERING,
				request), savingsOffering);

	}

	public void testScuccessfulReLoad() throws Exception {
		createAndAddObjects();
		savingsOffering1 = createSavingsOffering("sav prd_1", "pr_1");
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "reLoad");
		addRequestParameter("selectedPrdOfferingId", savingsOffering1
				.getPrdOfferingId().toString());
		actionPerform();
		verifyForward("load_success");

		assertEquals(SessionUtils.getAttribute(SavingsConstants.PRDOFFCERING,
				request), savingsOffering1);

	}

	public void testSuccessfulPreview() throws Exception {
		createAndAddObjectsForCreate();
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "preview");
		actionPerform();
		verifyForward("preview_success");

	}

	public void testSuccessfulEditPreview() throws Exception {
		createAndAddObjectsForCreate();
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "editPreview");
		actionPerform();
		verifyForward("editPreview_success");

	}
	
	public void testSuccessfulPrevious() throws Exception {
		createAndAddObjects();
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "previous");
		actionPerform();
		verifyForward("previous_success");

	}

	public void testSuccessfulCreateWithCustomFields() throws Exception {
		createAndAddObjectsForCreate();
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("selectedPrdOfferingId", savingsOffering
				.getPrdOfferingId().toString());

		actionPerform();
		verifyForward("load_success");
		addRequestParameter("selectedPrdOfferingId", savingsOffering
				.getPrdOfferingId().toString());
		savingsOffering = null;
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(SavingsConstants.CUSTOM_FIELDS, request);
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "preview");
		int i = 0;
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			addRequestParameter("customField[" + i + "].fieldId",
					customFieldDef.getFieldId().toString());
			addRequestParameter("customField[" + i + "].fieldValue", "11");
			i++;
		}
		actionPerform();
		verifyForward("preview_success");
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("stateSelected", Short
				.toString(AccountStates.SAVINGS_ACC_PARTIALAPPLICATION));
		addRequestParameter("method", "create");
		actionPerform();
		verifyForward("create_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		String globalAccountNum = (String) request
				.getAttribute(SavingsConstants.GLOBALACCOUNTNUM);
		assertNotNull(globalAccountNum);
		savings = new SavingsPersistence().findBySystemId(globalAccountNum);
		assertEquals(customFieldDefs.size(), savings.getAccountCustomFields()
				.size());
	}

	public void testSuccessfulSave() throws Exception {

		createAndAddObjectsForCreate();
		addRequestParameter("recommendedAmount", "1000.0");
		addRequestParameter("stateSelected", "13");
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "create");
		actionPerform();
		verifyForward("create_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		savingsOffering = null;
		String globalAccountNum = (String) request
				.getAttribute(SavingsConstants.GLOBALACCOUNTNUM);
		assertNotNull(globalAccountNum);
		savings = new SavingsPersistence().findBySystemId(globalAccountNum);
		assertNotNull(savings);
		assertEquals(1000.0, savings.getRecommendedAmount()
				.getAmountDoubleValue());
		assertEquals(AccountState.SAVINGS_ACC_PARTIALAPPLICATION.getValue(),
				savings.getAccountState().getId());

	}

	public void testSuccessfulSaveInActiveState() throws Exception {
		createAndAddObjectsForCreate();
		addRequestParameter("recommendedAmount", "1000.0");
		addRequestParameter("stateSelected", "16");
		savingsOffering = null;
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "create");
		actionPerform();
		verifyForward("create_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		String globalAccountNum = (String) request
				.getAttribute(SavingsConstants.GLOBALACCOUNTNUM);
		assertNotNull(globalAccountNum);
		savings = new SavingsPersistence().findBySystemId(globalAccountNum);
		assertNotNull(savings);
		assertEquals(1000.0, savings.getRecommendedAmount()
				.getAmountDoubleValue());
		assertEquals(AccountState.SAVINGS_ACC_APPROVED.getValue(), savings
				.getAccountState().getId());

	}

	public void testSuccessfulGetBySystemId() throws Exception {

		createAndAddObjects(AccountState.SAVINGS_ACC_PARTIALAPPLICATION);
		addRequestParameter("globalAccountNum", "000X00000000013");
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "get");
		actionPerform();
		verifyForward("get_success");
		SavingsBO savingsObj = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		assertEquals(savings.getGlobalAccountNum(), savingsObj
				.getGlobalAccountNum());
		assertEquals(savingsOffering.getRecommendedAmount()
				.getAmountDoubleValue(), savingsObj.getRecommendedAmount()
				.getAmountDoubleValue());
		savingsOffering = null;
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertNotNull(SessionUtils.getAttribute(MasterConstants.SAVINGS_TYPE,
				request));
		assertNotNull(SessionUtils.getAttribute(
				MasterConstants.RECOMMENDED_AMOUNT_UNIT, request));
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.CUSTOM_FIELDS,
				request));
		assertNotNull(SessionUtils.getAttribute(
				SavingsConstants.RECENTY_ACTIVITY_DETAIL_PAGE, request));
		assertNotNull(SessionUtils
				.getAttribute(SavingsConstants.NOTES, request));

	}

	public void testSuccessfulEdit() throws Exception {
		createAndAddObjects(AccountState.SAVINGS_ACC_PARTIALAPPLICATION);
		savingsOffering = null;
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "edit");
		actionPerform();
		verifyForward("edit_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testSuccessfulEditPrevious() throws Exception {
		createAndAddObjects(AccountState.SAVINGS_ACC_PARTIALAPPLICATION);
		savingsOffering = null;
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "editPrevious");
		actionPerform();
		verifyForward("editPrevious_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testSuccessfulUpdate() throws Exception {
		createAndAddObjects(AccountState.SAVINGS_ACC_PARTIALAPPLICATION);
		savingsOffering = null;
		addRequestParameter("recommendedAmount", "600.0");
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "update");
		List<CustomFieldDefinitionEntity> customFieldDefs = new SavingsBusinessService()
				.retrieveCustomFieldsDefinition();
		int i = 0;
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			addRequestParameter("customField[" + i + "].fieldId",
					customFieldDef.getFieldId().toString());
			addRequestParameter("customField[" + i + "].fieldValue", "12");
			i++;
		}
		actionPerform();
		verifyForward("update_success");
		String globalAccountNum = (String) request
				.getAttribute(SavingsConstants.GLOBALACCOUNTNUM);
		assertNotNull(globalAccountNum);
		HibernateUtil.closeSession();
		savings = new SavingsPersistence().findBySystemId(globalAccountNum);
		assertNotNull(savings);
		assertEquals(600.0, savings.getRecommendedAmount()
				.getAmountDoubleValue());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testSuccessfulUpdateInActiveState() throws Exception {

		createAndAddObjects(AccountState.SAVINGS_ACC_APPROVED);
		savingsOffering = null;
		addRequestParameter("recommendedAmount", "600.0");

		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "update");
		actionPerform();
		verifyForward("update_success");
		String globalAccountNum = (String) request
				.getAttribute(SavingsConstants.GLOBALACCOUNTNUM);
		assertNotNull(globalAccountNum);
		HibernateUtil.closeSession();
		savings = new SavingsPersistence().findBySystemId(globalAccountNum);
		assertNotNull(savings);
		assertEquals(600.0, savings.getRecommendedAmount()
				.getAmountDoubleValue());
		verifyNoActionErrors();
		verifyNoActionMessages();

	}

	public void testSuccessfulGetRecentActivity() throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering("sav prd1", "prd1");
		savings = createSavingsAccount("000X00000000018", savingsOffering,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION);
		savingsOffering = null;
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
		try {
			SessionUtils.setAttribute(Constants.USER_CONTEXT_KEY,
					TestObjectFactory.getUserContext(), request.getSession());
		} catch (Exception e) {
			assertEquals(e.getMessage(), false);
		}
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "getRecentActivity");
		addRequestParameter("globalAccountNum", savings.getGlobalAccountNum());
		actionPerform();
		verifyForward("getRecentActivity_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals(((List<SavingsRecentActivityView>) SessionUtils
				.getAttribute(SavingsConstants.RECENTY_ACTIVITY_LIST , request)).size(),
				0);
	}

	public void testSuccessfulGetTransactionHistory() throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering("sav prd1", "prd1");
		savings = createSavingsAccount("000X00000000019", savingsOffering,
				AccountStates.SAVINGS_ACC_APPROVED);
		savingsOffering = null;
		
		Money enteredAmount = new Money(TestObjectFactory.getMFICurrency(), "100.0");
		PaymentData paymentData = new PaymentData(enteredAmount, savings
				.getPersonnel(), Short.valueOf("1"), new Date(System
				.currentTimeMillis()));
		paymentData.setCustomer(group);
		paymentData.setRecieptDate(new Date(System.currentTimeMillis()));
		paymentData.setRecieptNum("34244");
		AccountActionDateEntity accountActionDate = savings
				.getAccountActionDate(Short.valueOf("1"));

		SavingsPaymentData savingsPaymentData = new SavingsPaymentData(accountActionDate);
		paymentData.addAccountPaymentData(savingsPaymentData);
		
		savings.applyPayment(paymentData);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		savings = new SavingsPersistence().findById(savings.getAccountId());
		savings.setUserContext(userContext);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
				
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "getTransactionHistory");
		addRequestParameter("globalAccountNum", savings.getGlobalAccountNum());
		actionPerform();
		verifyForward("getTransactionHistory_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals(2, ((List<SavingsTransactionHistoryView>)SessionUtils.getAttribute(SavingsConstants.TRXN_HISTORY_LIST,request))
				.size());
		HibernateUtil.closeSession();	
		savings = new SavingsPersistence().findById(savings.getAccountId());
		group = savings.getCustomer();
		center = group.getParentCustomer();
	}

	public void testGetDepositDueDetails() throws Exception {

		createInitialObjects();
		savingsOffering = createSavingsOffering("sav prd1", "prd1");
		savings = createSavingsAccount("000X00000000020", savingsOffering,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION);
		savingsOffering = null;
		HibernateUtil.closeSession();
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
		SessionUtils.setAttribute(Constants.USER_CONTEXT_KEY, TestObjectFactory
				.getUserContext(), request.getSession());
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "getDepositDueDetails");
		addRequestParameter("globalAccountNum", savings.getGlobalAccountNum());
		actionPerform();
		verifyForward("depositduedetails_success");
		verifyNoActionErrors();
		verifyNoActionMessages();

		HibernateUtil.closeSession();
		savings = new SavingsBusinessService().findBySystemId(savings
				.getGlobalAccountNum());

	}

	public void testWaiveAmountDue() throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering("sav prd1", "prd1");
		savings = createSavingsAccount("000X00000000019", savingsOffering,
				AccountStates.SAVINGS_ACC_APPROVED);
		HibernateUtil.closeSession();
		savingsOffering = null;
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
		SessionUtils.setAttribute(Constants.USER_CONTEXT_KEY, TestObjectFactory
				.getUserContext(), request.getSession());
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "waiveAmountDue");
		addRequestParameter("globalAccountNum", savings.getGlobalAccountNum());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("waiveAmount_success");

		HibernateUtil.closeSession();
		savings = new SavingsBusinessService().findBySystemId(savings
				.getGlobalAccountNum());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testWaiveAmountOverDue() throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering("sav prd1", "prd1");
		savings = createSavingsAccount("000X00000000019", savingsOffering,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION);
		savingsOffering = null;
		HibernateUtil.closeSession();
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
		SessionUtils.setAttribute(Constants.USER_CONTEXT_KEY, TestObjectFactory
				.getUserContext(), request.getSession());
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "waiveAmountOverDue");
		addRequestParameter("globalAccountNum", savings.getGlobalAccountNum());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("waiveAmount_success");

		HibernateUtil.closeSession();
		savings = new SavingsBusinessService().findBySystemId(savings
				.getGlobalAccountNum());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testSuccessfulGetStatusHistory() throws Exception {
		SavingsTestHelper helper = new SavingsTestHelper();
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering("asfddsf", "213a");
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION, userContext);
		savingsOffering = null;
		AccountStateMachines.getInstance().initialize((short) 1, (short) 1,
				AccountTypes.SAVINGSACCOUNT, null);
		savings.changeStatus(AccountState.SAVINGS_ACC_PENDINGAPPROVAL
				.getValue(), null, "notes");
		assertEquals(AccountStates.SAVINGS_ACC_PENDINGAPPROVAL, savings
				.getAccountState().getId().shortValue());

		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
		try {
			SessionUtils.setAttribute(Constants.USER_CONTEXT_KEY,
					TestObjectFactory.getUserContext(), request.getSession());
		} catch (Exception e) {
			assertEquals(e.getMessage(), false);
		}
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "getStatusHistory");
		addRequestParameter("globalAccountNum", savings.getGlobalAccountNum());
		actionPerform();
		verifyForward("getStatusHistory_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals(2, ((List<SavingsTransactionHistoryView>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_CHANGE_HISTORY_LIST , request)).size());
	}
public void testSuccessful_Update_AuditLog() throws Exception {
		createAndAddObjects(AccountState.SAVINGS_ACC_PARTIALAPPLICATION);
		savingsOffering = null;
		addRequestParameter("recommendedAmount", "600.0");
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "update");
		List<CustomFieldDefinitionEntity> customFieldDefs = new SavingsBusinessService()
				.retrieveCustomFieldsDefinition();
		int i = 0;
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			addRequestParameter("customField[" + i + "].fieldId",
					customFieldDef.getFieldId().toString());
			addRequestParameter("customField[" + i + "].fieldValue", "12");
			i++;
		}
		actionPerform();
		verifyForward("update_success");
		String globalAccountNum = (String) request
				.getAttribute(SavingsConstants.GLOBALACCOUNTNUM);
		assertNotNull(globalAccountNum);
		HibernateUtil.closeSession();
		savings = new SavingsPersistence().findBySystemId(globalAccountNum);
		assertNotNull(savings);
		assertEquals(600.0, savings.getRecommendedAmount()
				.getAmountDoubleValue());
		
		List<AuditLog> auditLogList=TestObjectFactory.getChangeLog(EntityType.SAVINGS.getValue(),savings.getAccountId());
		assertEquals(1,auditLogList.size());
		assertEquals(EntityType.SAVINGS.getValue(),auditLogList.get(0).getEntityType());
		assertEquals(savings.getAccountId(),auditLogList.get(0).getEntityId());
		
		assertEquals(2,auditLogList.get(0).getAuditLogRecords().size());
		
		for(AuditLogRecord auditLogRecord :  auditLogList.get(0).getAuditLogRecords()){
			if(auditLogRecord.getFieldName().equalsIgnoreCase("Recommended Amount"))
				matchValues(auditLogRecord,"300.0","600.0" );
			else if(auditLogRecord.getFieldName().equalsIgnoreCase("Additional Information")){
				matchValues(auditLogRecord,"External Savings Id-custom field value","External Savings Id-12" );
		}
		}
	}

}
