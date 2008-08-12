package org.mifos.application.customer.struts.action;

import java.sql.Date;

import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.accounts.business.TestAccountPaymentEntity;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerAccountBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerFeeScheduleEntity;
import org.mifos.application.customer.business.CustomerScheduleEntity;
import org.mifos.application.customer.business.CustomerTrxnDetailEntity;
import org.mifos.application.customer.business.TestCustomerAccountBO;
import org.mifos.application.customer.business.TestCustomerTrxnDetailEntity;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCustomerApplyAdjustmentAction extends MifosMockStrutsTestCase {
	private AccountBO accountBO = null;

	private UserContext userContext;

	private CustomerBO client;

	private CustomerBO group;

	private CustomerBO center;

	private String flowKey;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext",
				TestObjectFactory.getActivityContext());
		flowKey = createFlow(request, CustomerApplyAdjustmentAction.class);
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
	}

	@Override
	public void tearDown() throws Exception {
		try {
			TestObjectFactory.cleanUp(client);
			TestObjectFactory.cleanUp(group);
			TestObjectFactory.cleanUp(center);
		} catch (Exception e) {
			// TODO Whoops, cleanup didnt work, reset db
			TestDatabase.resetMySQLDatabase();
		}
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testLoadAdjustment() throws Exception {
		applyPayment();
		setRequestPathInfo("/custApplyAdjustment.do");
		addRequestParameter("method", "loadAdjustment");
		addRequestParameter("input", "ViewClientCharges");
		addRequestParameter("globalCustNum", client.getGlobalCustNum());
		addRequestParameter("prdOfferingName", "Client_Active_test_3");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("loadAdjustment_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testPreviewAdjustment() throws Exception {
		applyPayment();
		setRequestPathInfo("/custApplyAdjustment.do");
		addRequestParameter("method", "previewAdjustment");
		addRequestParameter("input", "ViewClientCharges");
		addRequestParameter("globalCustNum", client.getGlobalCustNum());
		addRequestParameter("prdOfferingName", "Client_Active_test_3");
		addRequestParameter("adjustcheckbox", "true");
		addRequestParameter("adjustmentNote", "abc");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("previewAdjustment_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testApplyAdjustment() throws Exception {
		applyPayment();
		setRequestPathInfo("/custApplyAdjustment.do");
		addRequestParameter("method", "applyAdjustment");
		addRequestParameter("globalCustNum", client.getGlobalCustNum());
		addRequestParameter("prdOfferingName", "Client_Active_test_3");
		addRequestParameter("input", "ViewClientCharges");
		addRequestParameter("adjustmentNote", "abcef");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("applyAdjustment_client_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testCancelAdjustment() throws Exception {
		setRequestPathInfo("/custApplyAdjustment.do");
		addRequestParameter("method", "cancelAdjustment");
		addRequestParameter("input", "ViewClientCharges");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("canceladj_client_success");
		assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testValidation_AdjustmentCheckbox() throws Exception {
		applyPayment();
		addRequestParameter("globalCustNum", "Client_Active_test_3");
		setRequestPathInfo("/custApplyAdjustment");
		addRequestParameter("method", "previewAdjustment");
		addRequestParameter("prdOfferingName", "Client_Active_test_3");
		addRequestParameter("input", "ViewClientCharges");
		addRequestParameter("adjustmentNote", "This is to test errors.");
		addRequestParameter("adjustcheckbox", "false");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyActionErrors(new String[] { "errors.mandatorycheckbox" });
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testValidation_AdjustmentNoteSize() throws Exception {
		applyPayment();
		addRequestParameter("globalCustNum", "Client_Active_test_3");
		setRequestPathInfo("/custApplyAdjustment");
		addRequestParameter("method", "previewAdjustment");
		addRequestParameter("prdOfferingName", "Client_Active_test_3");
		addRequestParameter("input", "ViewClientCharges");
		addRequestParameter(
				"adjustmentNote",
				"This is to test errors in case adjustment note size exceeds 200 characters.This is to test errors in case adjustment note size exceeds 200 characters.This is to test errors in case adjustment note size exceeds 200 characters.This is to test errors in case adjustment note size exceeds 200 characters.This is to test errors in case adjustment note size exceeds 200 characters.");
		addRequestParameter("adjustcheckbox", "true");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyActionErrors(new String[] { "errors.adjustmentNoteTooBig" });
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testValidation_AdjustmentNoteMandatory() throws Exception {
		applyPayment();
		addRequestParameter("globalCustNum", "Client_Active_test_3");
		setRequestPathInfo("/custApplyAdjustment");
		addRequestParameter("method", "previewAdjustment");
		addRequestParameter("prdOfferingName", "Client_Active_test_3");
		addRequestParameter("input", "ViewClientCharges");
		addRequestParameter("adjustcheckbox", "true");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyActionErrors(new String[] { "errors.mandatorytextarea" });
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client_Active_test_3", 
				CustomerStatus.CLIENT_ACTIVE, group);
	}

	private void applyPayment() throws Exception {
		createInitialObjects();
		accountBO = client.getCustomerAccount();
		Date currentDate = new Date(System.currentTimeMillis());
		CustomerAccountBO customerAccountBO = (CustomerAccountBO) accountBO;
		customerAccountBO.setUserContext(userContext);

		CustomerScheduleEntity accountAction = (CustomerScheduleEntity) customerAccountBO
				.getAccountActionDate(Short.valueOf("1"));
		TestCustomerAccountBO.setMiscFeePaid(accountAction,TestObjectFactory
				.getMoneyForMFICurrency(100));
		TestCustomerAccountBO.setMiscPenaltyPaid(accountAction,TestObjectFactory
				.getMoneyForMFICurrency(100));
		TestCustomerAccountBO.setPaymentDate(accountAction,currentDate);
		accountAction.setPaymentStatus(PaymentStatus.PAID);

		MasterPersistence masterPersistenceService =
			new MasterPersistence();

		AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(
				accountBO, TestObjectFactory.getMoneyForMFICurrency(100),
				"1111", currentDate, new PaymentTypeEntity(Short.valueOf("1")), new Date(System.currentTimeMillis()));

//		Money totalFees = new Money();
		CustomerTrxnDetailEntity accountTrxnEntity = new CustomerTrxnDetailEntity(
				accountPaymentEntity,
				(AccountActionEntity) masterPersistenceService.getPersistentObject(
						AccountActionEntity.class,
						AccountActionTypes.PAYMENT.getValue()), Short.valueOf("1"),
				accountAction.getActionDate(), TestObjectFactory
						.getPersonnel(userContext.getId()), currentDate,
				TestObjectFactory.getMoneyForMFICurrency(200), "payment done",
				null, TestObjectFactory.getMoneyForMFICurrency(100),
				TestObjectFactory.getMoneyForMFICurrency(100));

		for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountAction
				.getAccountFeesActionDetails()) {
			TestCustomerAccountBO.setFeeAmountPaid((CustomerFeeScheduleEntity) accountFeesActionDetailEntity,TestObjectFactory
					.getMoneyForMFICurrency(100));
			FeesTrxnDetailEntity feeTrxn = new FeesTrxnDetailEntity(
					accountTrxnEntity, accountFeesActionDetailEntity
							.getAccountFee(), accountFeesActionDetailEntity
							.getFeeAmount());
			TestCustomerTrxnDetailEntity.addFeesTrxnDetail(
					accountTrxnEntity,feeTrxn);
//			totalFees = accountFeesActionDetailEntity.getFeeAmountPaid();
		}
		accountPaymentEntity.addAccountTrxn(accountTrxnEntity);
		TestAccountPaymentEntity.addAccountPayment(accountPaymentEntity,customerAccountBO);

		TestObjectFactory.updateObject(customerAccountBO);
		TestObjectFactory.flushandCloseSession();
		customerAccountBO = TestObjectFactory.getObject(
				CustomerAccountBO.class, customerAccountBO.getAccountId());
		client = customerAccountBO.getCustomer();
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,client,request);
	}

}
