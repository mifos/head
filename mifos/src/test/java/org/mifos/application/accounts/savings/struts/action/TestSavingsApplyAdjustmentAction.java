package org.mifos.application.accounts.savings.struts.action;

import java.util.Date;
import java.util.Locale;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.TestAccountPaymentEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.TestSavingsBO;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.SavingsPaymentData;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestSavingsApplyAdjustmentAction extends MifosMockStrutsTestCase {
	public TestSavingsApplyAdjustmentAction() throws SystemException, ApplicationException {
        super();
    }

    private UserContext userContext;

	private CustomerBO group;

	private CustomerBO center;

	private SavingsBO savings;

	private SavingsOfferingBO savingsOffering;

	private SavingsTestHelper helper = new SavingsTestHelper();
	
	private String flowKey;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userContext = TestObjectFactory.getContext();
		userContext.setPreferredLocale(new Locale("en", "GB"));
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession().setAttribute(Constants.USER_CONTEXT_KEY,
				userContext);
		request.getSession(false).setAttribute("ActivityContext",
				TestObjectFactory.getActivityContext());
		flowKey = createFlow(request, SavingsApplyAdjustmentAction.class);
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
	}

	@Override
	public void tearDown() throws Exception {
		try {
			if (savings != null) {
				savings = (SavingsBO)HibernateUtil.getSessionTL().get(SavingsBO.class, savings.getAccountId());
			}
			TestObjectFactory.cleanUp(savings);
			savings = null;
			if (group != null) {
				group = (GroupBO)HibernateUtil.getSessionTL().get(GroupBO.class, group.getCustomerId());
			}
			TestObjectFactory.cleanUp(group);
			group = null;
			if (center != null) {
				center = (CenterBO)HibernateUtil.getSessionTL().get(CenterBO.class, center.getCustomerId());
			}
			TestObjectFactory.cleanUp(center);
			center = null;
		} catch (Exception e) {
			// TODO Whoops, cleanup didnt work, reset db
			TestDatabase.resetMySQLDatabase();
		}
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testSuccessfullLoad_WithValidLastPaymentDeposit()
			throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering();
		savings = createSavingsAccount("000X00000000017", savingsOffering, group, 
				AccountState.SAVINGS_ACTIVE);
		PersonnelBO createdBy = new PersonnelPersistence()
				.getPersonnel(userContext.getId());
		Money depositAmount = new Money(Configuration.getInstance()
				.getSystemConfig().getCurrency(), "1000");
		AccountPaymentEntity payment = helper.createAccountPaymentToPersist(
				savings, depositAmount, depositAmount, helper
						.getDate("20/05/2006"),
				AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy,
				group);
		TestAccountPaymentEntity.addAccountPayment(payment,savings);
		TestSavingsBO.setBalance(savings,depositAmount);
		savings.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
		setRequestPathInfo("/savingsApplyAdjustmentAction.do");
		addRequestParameter("method", "load");
		actionPerform();
		verifyForward("load_success");
		verifyNoActionMessages();
		verifyNoActionErrors();
		savings = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY,request);
		assertNotNull(savings.getLastPmnt());
		assertNotNull(SessionUtils.getAttribute(
				SavingsConstants.ACCOUNT_ACTION,request ));
		AccountActionEntity accountAction = (AccountActionEntity) 
			SessionUtils.getAttribute(SavingsConstants.ACCOUNT_ACTION,request);
		assertEquals(AccountActionTypes.SAVINGS_DEPOSIT,
				accountAction.asEnum());
		assertEquals(Short.valueOf("1"),  SessionUtils
				.getAttribute(SavingsConstants.IS_LAST_PAYMENT_VALID,request));
	}

	public void testSuccessfullLoad_WithValidLastPaymentWithdrawal()
			throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering();
		savings = createSavingsAccount("000X00000000017", savingsOffering, group, AccountState.SAVINGS_ACTIVE);
		PersonnelBO createdBy = new PersonnelPersistence()
				.getPersonnel(userContext.getId());
		Money withdrawalAmount = new Money(Configuration.getInstance()
				.getSystemConfig().getCurrency(), "1000.0");
		Money balance = new Money(Configuration.getInstance().getSystemConfig()
				.getCurrency(), "2000.0");
		AccountPaymentEntity payment = helper.createAccountPaymentToPersist(
				savings, withdrawalAmount, balance, helper
						.getDate("20/05/2006"),
				AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(), savings, createdBy,
				group);
		TestAccountPaymentEntity.addAccountPayment(payment,savings);
		TestSavingsBO.setBalance(savings,balance);
		savings.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
		setRequestPathInfo("/savingsApplyAdjustmentAction.do");
		addRequestParameter("method", "load");
		actionPerform();
		verifyForward("load_success");
		verifyNoActionMessages();
		verifyNoActionErrors();
		savings = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY,request);
		assertNotNull(savings.getLastPmnt());
		assertNotNull(SessionUtils.getAttribute(
				SavingsConstants.ACCOUNT_ACTION,request));
		AccountActionEntity accountAction = (AccountActionEntity) SessionUtils.getAttribute(SavingsConstants.ACCOUNT_ACTION,request);
		assertEquals(AccountActionTypes.SAVINGS_WITHDRAWAL, 
				accountAction.asEnum());
		assertEquals(Short.valueOf("1"), SessionUtils
				.getAttribute(SavingsConstants.IS_LAST_PAYMENT_VALID,request));
	}

	public void testSuccessfullLoad_WithoutValidLastPayment() throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering();
		savings = createSavingsAccount("000X00000000017", savingsOffering, group, 
				AccountState.SAVINGS_ACTIVE);
		HibernateUtil.closeSession();
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
		setRequestPathInfo("/savingsApplyAdjustmentAction.do");
		addRequestParameter("method", "load");
		actionPerform();
		verifyForward("load_success");
		verifyNoActionMessages();
		verifyNoActionErrors();
		savings = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY,request);
		assertNull(savings.getLastPmnt());
		assertNull(SessionUtils.getAttribute(
				SavingsConstants.ACCOUNT_ACTION,request));
		assertNull(SessionUtils.getAttribute(
				SavingsConstants.CLIENT_NAME,request));
		assertEquals(Short.valueOf("0"),  SessionUtils
				.getAttribute(SavingsConstants.IS_LAST_PAYMENT_VALID,request));
	}

	public void testSuccessfullPreviewSuccess() throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering();
		savings = createSavingsAccountWithPayment("000X00000000017", savingsOffering,
				group, AccountState.SAVINGS_ACTIVE);
		
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
		HibernateUtil.closeSession();
		setRequestPathInfo("/savingsApplyAdjustmentAction.do");
		addRequestParameter("method", "load");
		actionPerform();
		verifyForward("load_success");
		
		setRequestPathInfo("/savingsApplyAdjustmentAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("note", "adjustmentComment");
		addRequestParameter("lastPaymentAmount", "100");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionMessages();
		verifyNoActionErrors();
		HibernateUtil.closeSession();
//		savings = new SavingsPersistence().findById(savings.getAccountId());
	}

	public void testSuccessfullPreviewFailure_NoLastPayment() throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering();
		savings = createSavingsAccount("000X00000000017", savingsOffering, group, AccountState.SAVINGS_ACTIVE);
		HibernateUtil.closeSession();
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
		setRequestPathInfo("/savingsApplyAdjustmentAction.do");
		addRequestParameter("method", "preview");
		actionPerform();
		verifyActionErrors(new String[] { SavingsConstants.INVALID_LAST_PAYMENT });
	}

	public void testSuccessfullPreviewFailure_LongNotes() throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering();
		savings = createSavingsAccountWithPayment("000X00000000017", savingsOffering,
				group, AccountState.SAVINGS_ACTIVE);
		
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
		HibernateUtil.closeSession();
		setRequestPathInfo("/savingsApplyAdjustmentAction.do");
		addRequestParameter("method", "load");
		actionPerform();
		verifyForward("load_success");
		
		setRequestPathInfo("/savingsApplyAdjustmentAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("lastPaymentAmount", "");
		addRequestParameter("note", "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz" +
				"abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz" +
				"abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz" +
				"abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz" +
				"abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz" +
				"abcdefghijklmnopqrstuvwxyz");
		actionPerform();
		assertEquals(2, getErrorSize());
		assertEquals(1, getErrorSize(AccountConstants.MAX_NOTE_LENGTH));
		assertEquals(1, getErrorSize(SavingsConstants.INVALID_ADJUSTMENT_AMOUNT));
		HibernateUtil.closeSession();
//		savings = new SavingsPersistence().findById(savings.getAccountId());
	}
	
	public void testSuccessfullPrevious() {
		setRequestPathInfo("/savingsApplyAdjustmentAction.do");
		addRequestParameter("method", "previous");
		actionPerform();
		verifyForward("previous_success");
	}

	public void testSuccessfullCancel()throws Exception {
		setRequestPathInfo("/savingsApplyAdjustmentAction.do");
		addRequestParameter("method", "cancel");
		actionPerform();
		verifyForward("account_detail_page");
		assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testSuccessfullAdjustUserPayment_AmountNullified()
			throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering();
		savings = createSavingsAccount("000X00000000017", savingsOffering, group, 
				AccountState.SAVINGS_ACTIVE);
		PersonnelBO createdBy = new PersonnelPersistence()
				.getPersonnel(userContext.getId());
		Money depositAmount = new Money(Configuration.getInstance()
				.getSystemConfig().getCurrency(), "1000.0");
		AccountPaymentEntity payment = helper.createAccountPaymentToPersist(
				savings, depositAmount, depositAmount, helper
						.getDate("20/05/2006"),
				AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy,
				group);
		TestAccountPaymentEntity.addAccountPayment(payment,savings);
		TestSavingsBO.setBalance(savings,depositAmount);
		savings.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savings = new SavingsPersistence().findById(savings.getAccountId());
		assertEquals(Integer.valueOf(1).intValue(), savings.getLastPmnt()
				.getAccountTrxns().size());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
		HibernateUtil.closeSession();
		setRequestPathInfo("/savingsApplyAdjustmentAction.do");
		addRequestParameter("method", "adjustLastUserAction");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("account_detail_page");
		HibernateUtil.closeSession();
		savings = new SavingsPersistence().findById(savings.getAccountId());
		assertEquals(Integer.valueOf(2).intValue(), savings.getLastPmnt()
				.getAccountTrxns().size());
	}

	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
	}

	private SavingsOfferingBO createSavingsOffering() {
		Date currentDate = new Date(System.currentTimeMillis());
		return TestObjectFactory.createSavingsProduct(
			"SavingPrd1", "S", currentDate);
	}

	private SavingsBO createSavingsAccountWithPayment(String globalAccountNum,
			SavingsOfferingBO savingsOffering, CustomerBO group,
			AccountState state) throws Exception {
		SavingsBO savings =  TestObjectFactory.createSavingsAccount(
				globalAccountNum, group,
				state, new Date(), savingsOffering, userContext);
		PaymentData paymentData = PaymentData.createPaymentData(new Money("100"), savings
				.getPersonnel(), Short.valueOf("1"), new Date(System
				.currentTimeMillis()));
		paymentData.setCustomer(group);
		paymentData.setRecieptDate(new Date(System.currentTimeMillis()));
		paymentData.setRecieptNum("34244");
		AccountActionDateEntity accountActionDate=null;
		paymentData.addAccountPaymentData(new SavingsPaymentData(accountActionDate));
		savings.applyPaymentWithPersist(paymentData);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		return new SavingsPersistence().findById(savings.getAccountId());
	}
	
	private SavingsBO createSavingsAccount(String globalAccountNum, SavingsOfferingBO savingsOffering, CustomerBO group, AccountState state) throws Exception {
		return TestObjectFactory.createSavingsAccount(globalAccountNum, group,
				state, new Date(), savingsOffering, userContext);
	}
	
}
