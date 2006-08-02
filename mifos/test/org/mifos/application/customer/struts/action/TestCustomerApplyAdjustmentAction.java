package org.mifos.application.customer.struts.action;

import java.net.URISyntaxException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.CustomerAccountBO;
import org.mifos.application.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerTrxnDetailEntity;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.service.MasterPersistenceService;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.PersistenceServiceName;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.TestObjectFactory;

import org.mifos.framework.MifosMockStrutsTestCase;

public class TestCustomerApplyAdjustmentAction extends MifosMockStrutsTestCase {
	private AccountBO accountBO = null;
	
	private UserContext userContext;
	
	private CustomerBO client;

	private CustomerBO group;

	private CustomerBO center;

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
		userContext = new UserContext();
		userContext.setId(new Short("1"));
		userContext.setLocaleId(new Short("1"));
		Set<Short> set = new HashSet<Short>();
		set.add(Short.valueOf("1"));
		userContext.setRoles(set);
		userContext.setLevelId(Short.valueOf("2"));
		userContext.setName("mifos");
		userContext.setPereferedLocale(new Locale("en", "US"));
		userContext.setBranchId(new Short("1"));
		userContext.setBranchGlobalNum("0001");
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);

	}

	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testLoadAdjustment() throws Exception {
		applyPayment();
		setRequestPathInfo("/custApplyAdjustment.do");
		addRequestParameter("method", "loadAdjustment");
		addRequestParameter("input","ViewClientCharges");
		addRequestParameter("globalCustNum",client.getGlobalCustNum());
		addRequestParameter("prdOfferingName","Client_Active_test_3");
		getRequest().getSession().setAttribute("security_param","Client");
		actionPerform();
		verifyForward("loadAdjustment_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testPreviewAdjustment() throws Exception {
		applyPayment();
		setRequestPathInfo("/custApplyAdjustment.do");
		addRequestParameter("method", "previewAdjustment");
		addRequestParameter("input","ViewClientCharges");
		addRequestParameter("globalCustNum",client.getGlobalCustNum());
		addRequestParameter("prdOfferingName","Client_Active_test_3");
		addRequestParameter("adjustcheckbox","true");
		addRequestParameter("adjustmentNote","abc");
		getRequest().getSession().setAttribute("security_param","Client");
		actionPerform();
		verifyForward("previewAdjustment_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testApplyAdjustment() throws Exception {
		applyPayment();
		setRequestPathInfo("/custApplyAdjustment.do");
		addRequestParameter("method", "applyAdjustment");
		addRequestParameter("globalCustNum",client.getGlobalCustNum());
		addRequestParameter("prdOfferingName","Client_Active_test_3");
		addRequestParameter("input","ViewClientCharges");
		addRequestParameter("adjustmentNote","abcef");
		getRequest().getSession().setAttribute("security_param","Client");
		actionPerform();
		verifyForward("applyAdjustment_client_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	public void testCancelAdjustment()throws Exception{
		setRequestPathInfo("/custApplyAdjustment.do");
		addRequestParameter("method", "cancelAdjustment");
		addRequestParameter("input","ViewClientCharges");
		getRequest().getSession().setAttribute("security_param","Client");
		actionPerform();
		verifyForward("canceladj_client_success");
	}
	
	public void testValidation_AdjustmentCheckbox()throws Exception{
		applyPayment();
		addRequestParameter("globalCustNum", "Client_Active_test_3");
		setRequestPathInfo("/applyAdjustment");
		addRequestParameter("method", "previewAdjustment");
		addRequestParameter("prdOfferingName","Client_Active_test_3");
		addRequestParameter("input","ViewClientCharges");
		addRequestParameter("adjustmentNote", "This is to test errors.");
		addRequestParameter("adjustcheckbox", "false");
		actionPerform();
		verifyActionErrors(new String[]{"errors.mandatorycheckbox"});
	}
	
	public void testValidation_AdjustmentNoteSize()throws Exception{
		applyPayment();
		addRequestParameter("globalCustNum", "Client_Active_test_3");
		setRequestPathInfo("/applyAdjustment");
		addRequestParameter("method", "previewAdjustment");
		addRequestParameter("prdOfferingName","Client_Active_test_3");
		addRequestParameter("input","ViewClientCharges");
		addRequestParameter("adjustmentNote", "This is to test errors in case adjustment note size exceeds 200 characters.This is to test errors in case adjustment note size exceeds 200 characters.This is to test errors in case adjustment note size exceeds 200 characters.This is to test errors in case adjustment note size exceeds 200 characters.This is to test errors in case adjustment note size exceeds 200 characters.");
		addRequestParameter("adjustcheckbox", "true");
		actionPerform();
		verifyActionErrors(new String[]{"errors.adjustmentNoteTooBig"});
	}
	
	public void testValidation_AdjustmentNoteMandatory()throws Exception{
		applyPayment();
		addRequestParameter("globalCustNum", "Client_Active_test_3");
		setRequestPathInfo("/applyAdjustment");
		addRequestParameter("method", "previewAdjustment");
		addRequestParameter("prdOfferingName","Client_Active_test_3");
		addRequestParameter("input","ViewClientCharges");
		addRequestParameter("adjustcheckbox", "true");
		actionPerform();
		verifyActionErrors(new String[]{"errors.mandatorytextarea"});
	}
	
	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group_Active_test", Short
				.valueOf("13"), "1.1.1", center, new Date(System
				.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client_Active_test_3",Short
				.valueOf("3"), "1.1.1", group, new Date(System
				.currentTimeMillis()));
	}
	
	private void applyPayment() throws Exception {
		createInitialObjects();
		accountBO=client.getCustomerAccount();
		Date currentDate = new Date(System.currentTimeMillis());
		CustomerAccountBO customerAccountBO = (CustomerAccountBO) accountBO;
		customerAccountBO.setUserContext(userContext);
		
		AccountActionDateEntity accountAction = customerAccountBO.getAccountActionDate(Short.valueOf("1"));
		accountAction.setMiscFeePaid(TestObjectFactory.getMoneyForMFICurrency(100));
		accountAction.setMiscPenaltyPaid(TestObjectFactory.getMoneyForMFICurrency(100));
		accountAction.setPaymentDate(currentDate);
		accountAction.setPaymentStatus(PaymentStatus.PAID.getValue());
		
		MasterPersistenceService masterPersistenceService = (MasterPersistenceService) ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.MasterDataService);
		
		AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(accountBO,TestObjectFactory.getMoneyForMFICurrency(100),"1111",currentDate,new PaymentTypeEntity(Short.valueOf("1")));
			
		Money totalFees = new Money();
		CustomerTrxnDetailEntity accountTrxnEntity = new CustomerTrxnDetailEntity(
				accountPaymentEntity,
				(AccountActionEntity) masterPersistenceService
				.findById(AccountActionEntity.class,AccountConstants.ACTION_PAYMENT), Short.valueOf("1"),
				accountAction.getActionDate(), TestObjectFactory.getPersonnel(userContext.getId()),
				currentDate, TestObjectFactory.getMoneyForMFICurrency(200), 
				"payment done", null,
				TestObjectFactory.getMoneyForMFICurrency(100), TestObjectFactory.getMoneyForMFICurrency(100));
		
		
		for(AccountFeesActionDetailEntity accountFeesActionDetailEntity:accountAction.getAccountFeesActionDetails()) {
			accountFeesActionDetailEntity.setFeeAmountPaid(TestObjectFactory.getMoneyForMFICurrency(100));
			FeesTrxnDetailEntity feeTrxn = new FeesTrxnDetailEntity();
			feeTrxn.makePayment(accountFeesActionDetailEntity);
			accountTrxnEntity.addFeesTrxnDetail(feeTrxn);
			totalFees = accountFeesActionDetailEntity.getFeeAmountPaid();
		}
		accountPaymentEntity.addAcountTrxn(accountTrxnEntity);
		customerAccountBO.addAccountPayment(accountPaymentEntity);
		
		TestObjectFactory.updateObject(customerAccountBO);
		TestObjectFactory.flushandCloseSession();
		customerAccountBO= (CustomerAccountBO)TestObjectFactory.getObject(CustomerAccountBO.class,customerAccountBO.getAccountId());
		client = customerAccountBO.getCustomer();	
		
	}
	

}
