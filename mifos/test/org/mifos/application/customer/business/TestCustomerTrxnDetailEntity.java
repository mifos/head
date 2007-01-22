package org.mifos.application.customer.business;

import java.sql.Date;

import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.accounts.business.TestAccountPaymentEntity;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.service.MasterPersistenceService;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.PersistenceServiceName;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCustomerTrxnDetailEntity extends MifosTestCase {

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
	
	public static void addFeesTrxnDetail(CustomerTrxnDetailEntity accountTrxnEntity,FeesTrxnDetailEntity feeTrxn) {
		accountTrxnEntity.addFeesTrxnDetail(feeTrxn);
	}
	
	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingForToday(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		// TODO: Is CLIENT_ACTIVE right or should this be GROUP_ACTIVE?
		group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", 
				CustomerStatus.CLIENT_ACTIVE, 
				center);
		client = TestObjectFactory.createClient("Client_Active_test",
				CustomerStatus.CLIENT_ACTIVE, group);
	}

	public void testGenerateReverseTrxn() throws Exception {
		userContext = TestObjectFactory.getUserContext();
		createInitialObjects();
		accountBO=client.getCustomerAccount();
		Date currentDate = new Date(System.currentTimeMillis());
		CustomerAccountBO customerAccountBO = (CustomerAccountBO) accountBO;
		customerAccountBO.setUserContext(userContext);
		
		CustomerScheduleEntity accountAction =(CustomerScheduleEntity) customerAccountBO.getAccountActionDate(Short.valueOf("1"));
		accountAction.setMiscFeePaid(TestObjectFactory.getMoneyForMFICurrency(100));
		accountAction.setMiscPenaltyPaid(TestObjectFactory.getMoneyForMFICurrency(100));
		accountAction.setPaymentDate(currentDate);
		accountAction.setPaymentStatus(PaymentStatus.PAID.getValue());
		
		MasterPersistenceService masterPersistenceService = (MasterPersistenceService) ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.MasterDataService);
		
		AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(accountBO,TestObjectFactory.getMoneyForMFICurrency(100),"1111",currentDate,new PaymentTypeEntity(Short.valueOf("1")));
		
		CustomerTrxnDetailEntity accountTrxnEntity = new CustomerTrxnDetailEntity(
				accountPaymentEntity,
				(AccountActionEntity) masterPersistenceService
				.findById(AccountActionEntity.class,AccountConstants.ACTION_PAYMENT), Short.valueOf("1"),
				accountAction.getActionDate(), TestObjectFactory.getPersonnel(userContext.getId()),
				currentDate, TestObjectFactory.getMoneyForMFICurrency(200), 
				"payment done", null,
				TestObjectFactory.getMoneyForMFICurrency(100), TestObjectFactory.getMoneyForMFICurrency(100));
		
		
		for(AccountFeesActionDetailEntity accountFeesActionDetailEntity:accountAction.getAccountFeesActionDetails()) {
			TestCustomerAccountBO.setFeeAmountPaid((CustomerFeeScheduleEntity) accountFeesActionDetailEntity,TestObjectFactory.getMoneyForMFICurrency(100));
			FeesTrxnDetailEntity feeTrxn = new FeesTrxnDetailEntity(
					accountTrxnEntity, accountFeesActionDetailEntity.getAccountFee(),
					accountFeesActionDetailEntity.getFeeAmount());
			accountTrxnEntity.addFeesTrxnDetail(feeTrxn);
		}
		accountPaymentEntity.addAcountTrxn(accountTrxnEntity);
		TestAccountPaymentEntity.addAccountPayment(accountPaymentEntity,customerAccountBO);
		
		TestObjectFactory.updateObject(customerAccountBO);
		TestObjectFactory.flushandCloseSession();
		customerAccountBO= TestObjectFactory.getObject(CustomerAccountBO.class,customerAccountBO.getAccountId());
		client = customerAccountBO.getCustomer();
		PersonnelBO loggedInUser = new PersonnelPersistence().getPersonnel(userContext.getId());
		for(AccountTrxnEntity accntTrxn : customerAccountBO.getLastPmnt().getAccountTrxns()){
			AccountTrxnEntity reverseAccntTrxn = ((CustomerTrxnDetailEntity)accntTrxn).generateReverseTrxn(loggedInUser, "adjustment");
			assertEquals(reverseAccntTrxn.getAmount(),accntTrxn.getAmount().negate());
			assertEquals(loggedInUser.getPersonnelId(), reverseAccntTrxn.getPersonnel().getPersonnelId());
		}
		
	}

}
