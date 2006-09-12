package org.mifos.application.accounts.savings.struts.action;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.mifos.application.accounts.business.AccountCustomFieldEntity;
import org.mifos.application.accounts.business.AccountStateMachines;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsRecentActivityView;
import org.mifos.application.accounts.savings.business.SavingsTransactionHistoryView;
import org.mifos.application.accounts.savings.business.service.SavingsBusinessService;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.PrdOfferingView;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestSavingsAction extends MifosMockStrutsTestCase {
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
		try {
			setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
					.getPath());
			setConfigFile(ResourceLoader.getURI(
					"org/mifos/application/accounts/savings/struts-config.xml")
					.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		userContext = TestObjectFactory.getContext();
		userContext.setPereferedLocale(new Locale("en", "US"));
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
	}

	@Override
	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(savings);
		TestObjectFactory.cleanUp(savings3);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		if (savingsOffering != null)
			TestObjectFactory.removeObject(savingsOffering1);
		if (savingsOffering1 != null)
			TestObjectFactory.removeObject(savingsOffering1);
		if (savingsOffering2 != null)
			TestObjectFactory.removeObject(savingsOffering2);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGetPrdOfferings() {
		createInitialObjects();
		savingsOffering1 = createSavingsOffering("sav prd1","prd1");
		savingsOffering2 = createSavingsOffering("sav prd2","prd2");

		addRequestParameter("customerId", group.getCustomerId().toString());
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "getPrdOfferings");
		actionPerform();
		verifyForward("getPrdOfferings_success");
		List<PrdOfferingView> savingPrds = (List<PrdOfferingView>) request
				.getSession().getAttribute(
						SavingsConstants.SAVINGS_PRD_OFFERINGS);
		assertEquals(Integer.valueOf("2").intValue(), savingPrds.size());
	}

	public void testSuccessfullLoad() {
		createInitialObjects();
		savingsOffering = createSavingsOffering("sav prd1","prd1");
		addRequestParameter("selectedPrdOfferingId", savingsOffering
				.getPrdOfferingId().toString());

		SavingsBO savingsObj = new SavingsBO(userContext);
		savingsObj.setCustomer(group);
		request.getSession().setAttribute(Constants.BUSINESS_KEY, savingsObj);

		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "load");
		actionPerform();
		verifyForward("load_success");

		savingsObj = (SavingsBO) request.getSession().getAttribute(
				Constants.BUSINESS_KEY);

		assertNotNull(request.getSession().getAttribute(
				MasterConstants.INTEREST_CAL_TYPES));
		assertNotNull(request.getSession().getAttribute(
				MasterConstants.SAVINGS_TYPE));
		assertNotNull(request.getSession().getAttribute(
				MasterConstants.RECOMMENDED_AMOUNT_UNIT));
		assertNotNull(request.getSession().getAttribute(
				SavingsConstants.CUSTOM_FIELDS));

		assertEquals(savingsObj.getCustomer(), group);
		assertEquals(savingsObj.getSavingsOffering(), savingsOffering);
		TestObjectFactory.removeObject(savingsOffering);
	}

	public void testScuccessfulReLoad() {
		createInitialObjects();
		savingsOffering = createSavingsOffering("sav prd1","prd1");
		addRequestParameter("selectedPrdOfferingId", savingsOffering
				.getPrdOfferingId().toString());

		SavingsBO savingsObj = new SavingsBO(userContext);
		savingsObj.setCustomer(group);
		request.getSession().setAttribute(Constants.BUSINESS_KEY, savingsObj);

		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "reLoad");
		actionPerform();
		verifyForward("load_success");

		savingsObj = (SavingsBO) request.getSession().getAttribute(
				Constants.BUSINESS_KEY);
		savingsObj.setCustomer(group);
		assertEquals(savingsObj.getCustomer(), group);
		assertEquals(savingsObj.getSavingsOffering(), savingsOffering);
		TestObjectFactory.removeObject(savingsOffering);
	}

	public void testSuccessfulPreview() {
		createInitialObjects();
		savingsOffering = createSavingsOffering("sav prd1","prd1");
		SavingsBO savingsObj = new SavingsBO(userContext);
		savingsObj.setCustomer(group);
		savingsObj.setSavingsOffering(savingsOffering);
		savingsObj.setSavingsType(savingsOffering.getSavingsType());
		addRequestParameter("selectedPrdOfferingId", savingsOffering
				.getPrdOfferingId().toString());
		request.getSession().setAttribute(Constants.BUSINESS_KEY, savingsObj);
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "preview");
		actionPerform();
		verifyForward("preview_success");
		TestObjectFactory.removeObject(savingsOffering);
	}

	public void testSuccessfulPrevious() {
		createInitialObjects();
		savingsOffering = createSavingsOffering("sav prd1","prd1");
		SavingsBO savingsObj = new SavingsBO(userContext);
		savingsObj.setCustomer(group);
		savingsObj.setSavingsOffering(savingsOffering);
		request.getSession().setAttribute(Constants.BUSINESS_KEY, savingsObj);

		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "previous");
		actionPerform();
		verifyForward("previous_success");
		TestObjectFactory.removeObject(savingsOffering);
	}

	public void testSuccessfulCreateWithCustomFields() {
		createInitialObjects();
		savingsOffering = createSavingsOffering("sav prd1","prd1");
		addRequestParameter("selectedPrdOfferingId", savingsOffering
				.getPrdOfferingId().toString());
		savings = new SavingsBO(userContext);
		savings.setCustomer(group);
		savings.setSavingsType(savingsOffering.getSavingsType());
		request.getSession().setAttribute(Constants.BUSINESS_KEY, savings);
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "load");
		actionPerform();
		verifyForward("load_success");
		addRequestParameter("selectedPrdOfferingId", savingsOffering
				.getPrdOfferingId().toString());
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(SavingsConstants.CUSTOM_FIELDS, request.getSession());
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "preview");
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "11");
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
		assertEquals(customFieldDefs.size(), savings.getAccountCustomFields().size());
	}

	
	public void testSuccessfulSave() {

		createInitialObjects();
		savingsOffering = createSavingsOffering("sav prd1","prd1");
		// addRequestParameter("selectedPrdOfferingId",
		// savingsOffering.getPrdOfferingId().toString());
		addRequestParameter("stateSelected", Short
				.toString(AccountStates.SAVINGS_ACC_PARTIALAPPLICATION));

		savings = new SavingsBO(userContext);
		savings.setCustomer(group);
		savings.setSavingsOffering(savingsOffering);
		savings.setRecommendedAmount(savingsOffering.getRecommendedAmount());
		savings.setSavingsType(savingsOffering.getSavingsType());
		savings
				.setRecommendedAmntUnit(savingsOffering
						.getRecommendedAmntUnit());

		Set<AccountCustomFieldEntity> customFields = new HashSet<AccountCustomFieldEntity>();
		AccountCustomFieldEntity field = new AccountCustomFieldEntity();
		field.setFieldId(new Short("1"));
		field.setFieldValue("14");
		field.setAccount(savings);
		customFields.add(field);

		savings.setAccountCustomFields(customFields);

		request.getSession().setAttribute(Constants.BUSINESS_KEY, savings);

		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "create");
		actionPerform();
		verifyForward("create_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		
	}

	public void testSuccessfulSaveInActiveState() {

		createInitialObjects();
		savingsOffering = createSavingsOffering("sav prd1","prd1");
		// addRequestParameter("selectedPrdOfferingId",
		// savingsOffering.getPrdOfferingId().toString());
		addRequestParameter("stateSelected", Short
				.toString(AccountStates.SAVINGS_ACC_APPROVED));
		addRequestParameter("input", "preview");

		savings = new SavingsBO(userContext);
		savings.setCustomer(group);
		savings.setSavingsOffering(savingsOffering);
		savings.setRecommendedAmount(savingsOffering.getRecommendedAmount());
		savings.setSavingsType(savingsOffering.getSavingsType());
		savings
				.setRecommendedAmntUnit(savingsOffering
						.getRecommendedAmntUnit());

		Set<AccountCustomFieldEntity> customFields = new HashSet<AccountCustomFieldEntity>();
		AccountCustomFieldEntity field = new AccountCustomFieldEntity();
		field.setFieldId(new Short("1"));
		field.setFieldValue("14");
		field.setAccount(savings);
		customFields.add(field);
		savings.setAccountCustomFields(customFields);

		request.getSession().setAttribute(Constants.BUSINESS_KEY, savings);

		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "create");
		actionPerform();
		verifyForward("create_success");
		verifyNoActionErrors();
		verifyNoActionMessages();

	}

	public void testSuccessfulGetBySystemId() {
		createInitialObjects();
		savingsOffering = createSavingsOffering("sav prd1","prd1");
		addRequestParameter("input", "preview");
		savings = createSavingsAccount("000X00000000013", savingsOffering,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION);
		addRequestParameter("globalAccountNum", "000X00000000013");
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "get");
		actionPerform();
		verifyForward("get_success");
		SavingsBO savingsObj = (SavingsBO) request.getSession().getAttribute(
				Constants.BUSINESS_KEY);
		assertEquals(savings.getGlobalAccountNum(), savingsObj
				.getGlobalAccountNum());
		assertEquals(savingsOffering.getRecommendedAmount()
				.getAmountDoubleValue(), savingsObj.getRecommendedAmount()
				.getAmountDoubleValue());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testSuccessfulEdit() {
		createInitialObjects();
		savingsOffering = createSavingsOffering("sav prd1","prd1");
		savings = createSavingsAccount("000X00000000014", savingsOffering,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION);
		request.getSession().setAttribute(Constants.BUSINESS_KEY, savings);
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "edit");
		actionPerform();
		verifyForward("edit_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testSuccessfulEditPrevious() {
		createInitialObjects();
		savingsOffering = createSavingsOffering("sav prd1","prd1");
		savings = createSavingsAccount("000X00000000016", savingsOffering,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION);
		request.getSession().setAttribute(Constants.BUSINESS_KEY, savings);
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "editPrevious");
		actionPerform();
		verifyForward("editPrevious_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testSuccessfulUpdate() {
		createInitialObjects();
		savingsOffering = createSavingsOffering("sav prd1","prd1");
		savings = createSavingsAccount("000X00000000017", savingsOffering,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION);
		savings.setCustomer(group);
		savings.setSavingsOffering(savingsOffering);
		request.getSession().setAttribute(Constants.BUSINESS_KEY, savings);
		savings.setRecommendedAmount(new Money(TestObjectFactory
				.getMFICurrency(), "600.0"));
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "update");
		actionPerform();
		verifyForward("update_success");
		assertEquals(new Double(600).doubleValue(), savings
				.getRecommendedAmount().getAmountDoubleValue());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testSuccessfulUpdateInActiveState() {

		createInitialObjects();
		savingsOffering = createSavingsOffering("sav prd1","prd1");
		savings = createSavingsAccount("000X00000000017", savingsOffering,
				AccountStates.SAVINGS_ACC_APPROVED);
		savings.setCustomer(group);
		savings.setSavingsOffering(savingsOffering);
		savings.setRecommendedAmount(new Money(TestObjectFactory
				.getMFICurrency(), "600.0"));
		request.getSession().setAttribute(SavingsConstants.RECOMMENDED_AMOUNT,
				savings.getRecommendedAmount());
		request.getSession().setAttribute(Constants.BUSINESS_KEY, savings);
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "update");
		actionPerform();
		verifyForward("update_success");
		assertEquals(new Double(600).doubleValue(), savings
				.getRecommendedAmount().getAmountDoubleValue());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testMultipleCreatesInBetweenUpdate() throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering("sav prd1","prd1");
		addRequestParameter("stateSelected", Short
				.toString(AccountStates.SAVINGS_ACC_PARTIALAPPLICATION));
		savings = new SavingsBO(userContext);
		savings.setCustomer(group);
		savings.setSavingsOffering(savingsOffering);
		savings.setRecommendedAmount(savingsOffering.getRecommendedAmount());
		savings.setSavingsType(savingsOffering.getSavingsType());
		savings
				.setRecommendedAmntUnit(savingsOffering
						.getRecommendedAmntUnit());
		request.getSession().setAttribute(Constants.BUSINESS_KEY, savings);

		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "create");
		actionPerform();
		verifyForward("create_success");

		// retrieve saved object
		addRequestParameter("globalAccountNum", savings.getGlobalAccountNum());
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "get");
		actionPerform();
		verifyForward("get_success");

		// update retrieved object
		SavingsBO savingsObj = (SavingsBO) request.getSession().getAttribute(
				Constants.BUSINESS_KEY);
		savingsObj.setRecommendedAmount(new Money(TestObjectFactory
				.getMFICurrency(), "400"));
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "update");
		actionPerform();
		verifyForward("update_success");

		// create another savings object
		savingsOffering3 = createSavingsOffering("sav prd2","prd2");
		addRequestParameter("stateSelected", Short
				.toString(AccountStates.SAVINGS_ACC_PARTIALAPPLICATION));
		savings3 = new SavingsBO(userContext);
		savings3.setCustomer(group);
		savings3.setSavingsOffering(savingsOffering3);
		savings3.setRecommendedAmount(savingsOffering3.getRecommendedAmount());
		savings3.setSavingsType(savingsOffering3.getSavingsType());
		savings3.setRecommendedAmntUnit(savingsOffering3
				.getRecommendedAmntUnit());

		request.getSession().setAttribute(Constants.BUSINESS_KEY, savings3);

		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "create");
		actionPerform();
		verifyForward("create_success");
	}

	public void testSuccessfulGetRecentActivity() {
		createInitialObjects();
		savingsOffering = createSavingsOffering("sav prd1","prd1");
		savings = createSavingsAccount("000X00000000018", savingsOffering,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION);
		request.getSession().setAttribute(Constants.BUSINESS_KEY, savings);
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
		assertEquals(((List<SavingsRecentActivityView>) request.getSession()
				.getAttribute(SavingsConstants.RECENTY_ACTIVITY_LIST)).size(),
				0);
	}

	public void testSuccessfulGetTransactionHistory() {
		createInitialObjects();
		savingsOffering = createSavingsOffering("sav prd1","prd1");
		savings = createSavingsAccount("000X00000000019", savingsOffering,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION);
		request.getSession().setAttribute(Constants.BUSINESS_KEY, savings);
		try {
			SessionUtils.setAttribute(Constants.USER_CONTEXT_KEY,
					TestObjectFactory.getUserContext(), request.getSession());
		} catch (Exception e) {
			assertEquals(e.getMessage(), false);
		}
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "getTransactionHistory");
		addRequestParameter("globalAccountNum", savings.getGlobalAccountNum());
		actionPerform();
		verifyForward("getTransactionHistory_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals(((List<SavingsTransactionHistoryView>) request
				.getSession().getAttribute(SavingsConstants.TRXN_HISTORY_LIST))
				.size(), 0);
	}

	public void testGetDepositDueDetails() throws Exception {

		createInitialObjects();
		savingsOffering = createSavingsOffering("sav prd1","prd1");
		savings = createSavingsAccount("000X00000000020", savingsOffering,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION);
		HibernateUtil.closeSession();
		request.getSession().setAttribute(Constants.BUSINESS_KEY, savings);
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
		savingsOffering = createSavingsOffering("sav prd1","prd1");
		savings = createSavingsAccount("000X00000000019", savingsOffering,
				AccountStates.SAVINGS_ACC_APPROVED);
		HibernateUtil.closeSession();
		request.getSession().setAttribute(Constants.BUSINESS_KEY, savings);
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
	}

	public void testWaiveAmountOverDue() throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering("sav prd1","prd1");
		savings = createSavingsAccount("000X00000000019", savingsOffering,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION);
		HibernateUtil.closeSession();
		request.getSession().setAttribute(Constants.BUSINESS_KEY, savings);
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
	}
	
	public void testSuccessfulGetStatusHistory() throws Exception{
		SavingsTestHelper helper = new SavingsTestHelper();
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering("asfddsf","213a");
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION, userContext);
		AccountStateMachines.getInstance().initialize((short) 1, (short) 1,AccountTypes.SAVINGSACCOUNT,null);
		savings.changeStatus(AccountState.SAVINGS_ACC_PENDINGAPPROVAL.getValue(), null, "notes");
		assertEquals(AccountStates.SAVINGS_ACC_PENDINGAPPROVAL, savings
				.getAccountState().getId().shortValue());
		
		request.getSession().setAttribute(Constants.BUSINESS_KEY, savings);
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
		assertEquals(2,((List<SavingsTransactionHistoryView>) request
				.getSession().getAttribute(SavingsConstants.STATUS_CHANGE_HISTORY_LIST))
				.size());
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

	private SavingsOfferingBO createSavingsOffering(String prdOfferingName,String shortName) {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createSavingsOffering(prdOfferingName, shortName,Short
				.valueOf("2"), new Date(System.currentTimeMillis()), Short
				.valueOf("2"), 300.0, Short.valueOf("1"), 1.2, 200.0, 200.0,
				Short.valueOf("2"), Short.valueOf("1"), meetingIntCalc,
				meetingIntPost);
	}

	private SavingsBO createSavingsAccount(String globalAccountNum,
			SavingsOfferingBO savingsOffering, short accountStateId) {
		return TestObjectFactory.createSavingsAccount(globalAccountNum, group,
				accountStateId, new Date(), savingsOffering, userContext);
	}
}
