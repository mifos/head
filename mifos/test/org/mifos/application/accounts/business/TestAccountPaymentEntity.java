package org.mifos.application.accounts.business;

import java.sql.Date;
import java.util.List;

import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerAccountBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerScheduleEntity;
import org.mifos.application.customer.business.CustomerTrxnDetailEntity;
import org.mifos.application.customer.business.TestCustomerAccountBO;
import org.mifos.application.customer.business.TestCustomerTrxnDetailEntity;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.UserContext;
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

	public static void addAccountPayment(AccountPaymentEntity payment,AccountBO account) {
		account.addAccountPayment(payment);
	}
	
	public static List<AccountTrxnEntity> reversalAdjustment(
			String adjustmentComment, AccountPaymentEntity lastPayment)
			throws Exception {
		return lastPayment.reversalAdjustment(new PersonnelPersistence().getPersonnel(TestObjectFactory.getContext().getId()),adjustmentComment);

	}
	
	public void testReversalAdjustment() throws Exception {
		userContext = TestObjectFactory.getContext();
		createInitialObjects();
		accountBO=client.getCustomerAccount();
		Date currentDate = new Date(System.currentTimeMillis());
		CustomerAccountBO customerAccountBO = (CustomerAccountBO) accountBO;
		customerAccountBO.setUserContext(userContext);
		
		CustomerScheduleEntity accountAction = (CustomerScheduleEntity)customerAccountBO.getAccountActionDate(Short.valueOf("1"));
		TestCustomerAccountBO.setMiscFeePaid(accountAction,TestObjectFactory.getMoneyForMFICurrency(100));
		TestCustomerAccountBO.setMiscPenaltyPaid(accountAction,TestObjectFactory.getMoneyForMFICurrency(100));
		TestCustomerAccountBO.setPaymentDate(accountAction,currentDate);
		accountAction.setPaymentStatus(PaymentStatus.PAID);
		
		MasterPersistence masterPersistenceService = new MasterPersistence();
		
		AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(customerAccountBO,TestObjectFactory.getMoneyForMFICurrency(100),"1111",currentDate,new PaymentTypeEntity(Short.valueOf("1")), new Date(System.currentTimeMillis()));
		
		CustomerTrxnDetailEntity accountTrxnEntity = new CustomerTrxnDetailEntity(accountPaymentEntity,
			(AccountActionEntity) masterPersistenceService
				.getPersistentObject(AccountActionEntity.class,AccountActionTypes.PAYMENT.getValue()),Short.valueOf("1"),
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
			//TODO: is there anything to assert on here?
			//totalFees = accountFeesActionDetailEntity.getFeeAmountPaid();
		}
		accountPaymentEntity.addAccountTrxn(accountTrxnEntity);
		customerAccountBO.addAccountPayment(accountPaymentEntity);
		
		TestObjectFactory.updateObject(customerAccountBO);
		TestObjectFactory.flushandCloseSession();
		customerAccountBO= TestObjectFactory.getObject(CustomerAccountBO.class,customerAccountBO.getAccountId());
		client = customerAccountBO.getCustomer(); 
		
		PersonnelBO loggedInUser = new PersonnelPersistence().getPersonnel(userContext.getId());
		List<AccountTrxnEntity> reversedTrxns = customerAccountBO.getLastPmnt().reversalAdjustment(loggedInUser, "adjustment");
		for (AccountTrxnEntity accntTrxn : reversedTrxns) {
			CustomerTrxnDetailEntity custTrxn = (CustomerTrxnDetailEntity)accntTrxn;
			CustomerScheduleEntity accntActionDate =(CustomerScheduleEntity) customerAccountBO.getAccountActionDate(custTrxn.getInstallmentId());
			assertEquals(custTrxn.getMiscFeeAmount().getAmountDoubleValue(),accntActionDate.getMiscFeePaid().negate().getAmountDoubleValue());
			assertEquals(custTrxn.getMiscPenaltyAmount().getAmountDoubleValue(),accntActionDate.getMiscPenaltyPaid().negate().getAmountDoubleValue());
			assertEquals(loggedInUser.getPersonnelId(), custTrxn.getPersonnel().getPersonnelId());
		}
		
	}
	
	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		// TODO: Is CLIENT_ACTIVE really right?  Shouldn't it be GROUP_ACTIVE?
		group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.CENTER_ACTIVE, center);
		client = TestObjectFactory.createClient("Client_Active_test",
				CustomerStatus.CLIENT_ACTIVE, group);
	}

}
