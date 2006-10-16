package org.mifos.application.accounts.business;

import java.sql.Date;
import java.util.List;

import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerAccountBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerScheduleEntity;
import org.mifos.application.customer.business.CustomerTrxnDetailEntity;
import org.mifos.application.customer.business.TestCustomerAccountBO;
import org.mifos.application.customer.business.TestCustomerTrxnDetailEntity;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.service.MasterPersistenceService;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.PersistenceServiceName;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestAccountPaymentEntity extends MifosTestCase {

	private AccountBO accountBO = null;
	private CustomerBO center=null;
	private CustomerBO group=null;
	private CustomerBO client=null;
	private UserContext userContext;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}


	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);

		HibernateUtil.closeSession();
	}

	public static void addAccountPayment(AccountPaymentEntity payment,AccountBO account) {
		account.addAccountPayment(payment);
	}
	
	public static List<AccountTrxnEntity> reversalAdjustment(
			String adjustmentComment, AccountPaymentEntity lastPayment)
			throws AccountException {
		return lastPayment.reversalAdjustment(adjustmentComment);

	}
	
	public void testReversalAdjustment() throws Exception {
		userContext = TestObjectFactory.getUserContext();
		createInitialObjects();
		accountBO=client.getCustomerAccount();
		Date currentDate = new Date(System.currentTimeMillis());
		CustomerAccountBO customerAccountBO = (CustomerAccountBO) accountBO;
		customerAccountBO.setUserContext(userContext);
		
		CustomerScheduleEntity accountAction = (CustomerScheduleEntity)customerAccountBO.getAccountActionDate(Short.valueOf("1"));
		TestCustomerAccountBO.setMiscFeePaid(accountAction,TestObjectFactory.getMoneyForMFICurrency(100));
		TestCustomerAccountBO.setMiscPenaltyPaid(accountAction,TestObjectFactory.getMoneyForMFICurrency(100));
		TestCustomerAccountBO.setPaymentDate(accountAction,currentDate);
		TestCustomerAccountBO.setPaymentStatus(accountAction,PaymentStatus.PAID.getValue());
		
		MasterPersistenceService masterPersistenceService = (MasterPersistenceService) ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.MasterDataService);
		
		AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(customerAccountBO,TestObjectFactory.getMoneyForMFICurrency(100),"1111",currentDate,new PaymentTypeEntity(Short.valueOf("1")));
		
		Money totalFees = new Money();
		CustomerTrxnDetailEntity accountTrxnEntity = new CustomerTrxnDetailEntity(accountPaymentEntity,
			(AccountActionEntity) masterPersistenceService
				.findById(AccountActionEntity.class,AccountConstants.ACTION_PAYMENT),Short.valueOf("1"),
			accountAction.getActionDate(), TestObjectFactory.getPersonnel(userContext.getId()),
			currentDate, TestObjectFactory.getMoneyForMFICurrency(200), 
			"payment done", null,
			TestObjectFactory.getMoneyForMFICurrency(100), TestObjectFactory.getMoneyForMFICurrency(100));
		
		
		for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountAction
				.getAccountFeesActionDetails()) {
			accountFeesActionDetailEntity.setFeeAmountPaid(TestObjectFactory
					.getMoneyForMFICurrency(100));
			FeesTrxnDetailEntity feeTrxn = new FeesTrxnDetailEntity(
					accountTrxnEntity, accountFeesActionDetailEntity
							.getAccountFee(), accountFeesActionDetailEntity
							.getFeeAmount());
			TestCustomerTrxnDetailEntity.addFeesTrxnDetail(accountTrxnEntity,feeTrxn);
			totalFees = accountFeesActionDetailEntity.getFeeAmountPaid();
		}
		accountPaymentEntity.addAcountTrxn(accountTrxnEntity);
		customerAccountBO.addAccountPayment(accountPaymentEntity);
		
		TestObjectFactory.updateObject(customerAccountBO);
		TestObjectFactory.flushandCloseSession();
		customerAccountBO= (CustomerAccountBO)TestObjectFactory.getObject(CustomerAccountBO.class,customerAccountBO.getAccountId());
		client = customerAccountBO.getCustomer(); 
		
		List<AccountTrxnEntity> reversedTrxns = customerAccountBO.getLastPmnt().reversalAdjustment("adjustment");
		for (AccountTrxnEntity accntTrxn : reversedTrxns) {
			CustomerTrxnDetailEntity custTrxn = (CustomerTrxnDetailEntity)accntTrxn;
			CustomerScheduleEntity accntActionDate =(CustomerScheduleEntity) customerAccountBO.getAccountActionDate(custTrxn.getInstallmentId());
			assertEquals(custTrxn.getMiscFeeAmount().getAmountDoubleValue(),accntActionDate.getMiscFeePaid().negate().getAmountDoubleValue());
			assertEquals(custTrxn.getMiscPenaltyAmount().getAmountDoubleValue(),accntActionDate.getMiscPenaltyPaid().negate().getAmountDoubleValue());
		}
		
	}
	
	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group_Active_test", Short
				.valueOf("3"), "1.1.1", center, new Date(System
				.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client_Active_test",Short
				.valueOf("3"), "1.1.1", group, new Date(System
				.currentTimeMillis()));
	}

}
