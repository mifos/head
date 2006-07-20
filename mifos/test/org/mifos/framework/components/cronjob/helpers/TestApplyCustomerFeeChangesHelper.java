package org.mifos.framework.components.cronjob.helpers;

import java.util.Date;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.CustomerAccountBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.fees.business.FeeUpdateTypeEntity;
import org.mifos.application.fees.business.FeesBO;
import org.mifos.application.fees.persistence.service.FeePersistenceService;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.fees.util.helpers.FeesConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.cronjobs.helpers.ApplyCustomerFeeChangesHelper;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestApplyCustomerFeeChangesHelper extends MifosTestCase {
	CustomerBO center = null;
	CustomerBO group = null;
	@Override
	protected void setUp() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		Date startDate = new Date(System.currentTimeMillis());
		center = TestObjectFactory.createCenter("center1", Short.valueOf("13"), "1.4", meeting,startDate );
		group = TestObjectFactory.createGroup("Group",GroupConstants.ACTIVE,"1.1.1",center,startDate);

	}

	@Override
	protected void tearDown() throws Exception {
	TestObjectFactory.cleanUp(group);
	TestObjectFactory.cleanUp(center);
	}
 
	public void testExecuteAmountUpdated() throws Exception{
		
		CustomerAccountBO customerAccount = center.getCustomerAccount();
		Set<AccountFeesEntity> accountFeeSet = customerAccount.getAccountFees();
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity();
		accountPeriodicFee.setAccount(center.getCustomerAccount());
		accountPeriodicFee.setAccountFeeAmount(new Money("10.0"));
		accountPeriodicFee.setFeeAmount(new Money("10.0"));
		FeesBO trainingFee = TestObjectFactory.createPeriodicFees("Training_Fee", 10.0, 1,
				2, 1);
		accountPeriodicFee.setFees(trainingFee);
		accountFeeSet.add(accountPeriodicFee);
		AccountActionDateEntity accountActionDate= customerAccount.getAccountActionDate(Short.valueOf("1"));
		AccountFeesActionDetailEntity accountFeesaction = new AccountFeesActionDetailEntity();
		accountFeesaction.setAccountFee(accountPeriodicFee);
		accountFeesaction.setAccountActionDate(accountActionDate);
		accountFeesaction.setFee(trainingFee);
		accountFeesaction.setFeeAmount(new Money("10.0"));
		accountFeesaction.setFeeAmountPaid(new Money("0.0"));
		accountFeesaction.setInstallmentId(Short.valueOf("1"));
		accountActionDate.addAccountFeesAction(accountFeesaction);
		TestObjectFactory.flushandCloseSession();
		
		trainingFee = (FeesBO)HibernateUtil.getSessionTL().get(FeesBO.class,trainingFee.getFeeId());
		trainingFee.setUserContext(TestObjectFactory.getUserContext());
		trainingFee.setAmount("5");
		trainingFee.setUpdateFlag(Short.valueOf("1"));
		trainingFee.setFeeUpdateType(new FeePersistenceService().getUpdateTypeEntity(Short.valueOf("1")));
		trainingFee.save(false);
		TestObjectFactory.flushandCloseSession();
		new ApplyCustomerFeeChangesHelper().execute(System.currentTimeMillis());
		TestObjectFactory.flushandCloseSession();
		center = (CustomerBO)HibernateUtil.getSessionTL().get(CustomerBO.class,center.getCustomerId());
		
		AccountActionDateEntity installment=center.getCustomerAccount().getAccountActionDate(Short.valueOf("1"));
		
		AccountFeesActionDetailEntity accountFeesAction = installment.getAccountFeesAction(accountPeriodicFee.getAccountFeeId());
		assertEquals(5.0 ,accountFeesAction.getFeeAmount().getAmountDoubleValue());
		
		
	}
	
	public void testExecuteStatusUpdatedToInactive() throws Exception{
		
		CustomerAccountBO customerAccount = center.getCustomerAccount();
		Set<AccountFeesEntity> accountFeeSet = customerAccount.getAccountFees();
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity();
		accountPeriodicFee.setAccount(center.getCustomerAccount());
		accountPeriodicFee.setAccountFeeAmount(new Money("10.0"));
		accountPeriodicFee.setFeeAmount(new Money("10.0"));
		FeesBO trainingFee = TestObjectFactory.createPeriodicFees("Training_Fee", 10.0, 1,
				2, 1);
		accountPeriodicFee.setFees(trainingFee);
		accountFeeSet.add(accountPeriodicFee);
		AccountActionDateEntity accountActionDate= customerAccount.getAccountActionDate(Short.valueOf("1"));
		AccountFeesActionDetailEntity accountFeesaction = new AccountFeesActionDetailEntity();
		accountFeesaction.setAccountFee(accountPeriodicFee);
		accountFeesaction.setAccountActionDate(accountActionDate);
		accountFeesaction.setFee(trainingFee);
		accountFeesaction.setFeeAmount(new Money("10.0"));
		accountFeesaction.setFeeAmountPaid(new Money("0.0"));
		accountFeesaction.setInstallmentId(Short.valueOf("1"));
		accountActionDate.addAccountFeesAction(accountFeesaction);
		TestObjectFactory.flushandCloseSession();
		
		trainingFee = (FeesBO)HibernateUtil.getSessionTL().get(FeesBO.class,trainingFee.getFeeId());
		trainingFee.setUserContext(TestObjectFactory.getUserContext());
		trainingFee.setUpdateFlag(Short.valueOf("2"));
		trainingFee.modifyStatus(FeeStatus.INACTIVE);
		trainingFee.setFeeUpdateType(new FeePersistenceService().getUpdateTypeEntity(Short.valueOf("1")));
		trainingFee.update();
		TestObjectFactory.flushandCloseSession();
		new ApplyCustomerFeeChangesHelper().execute(System.currentTimeMillis());
		TestObjectFactory.flushandCloseSession();
		center = (CustomerBO)HibernateUtil.getSessionTL().get(CustomerBO.class,center.getCustomerId());
		
		AccountActionDateEntity installment=center.getCustomerAccount().getAccountActionDate(Short.valueOf("1"));
		
		
		AccountFeesActionDetailEntity accountFeesAction = installment.getAccountFeesAction(accountPeriodicFee.getAccountFeeId());
		assertNull(accountFeesAction);
		assertEquals(1,installment.getAccountFeesActionDetails().size());
		
	}
	public void testExecuteStatusInactiveAndAmountUpdated() throws Exception{
		CustomerAccountBO customerAccount = center.getCustomerAccount();
		Set<AccountFeesEntity> accountFeeSet = customerAccount.getAccountFees();
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity();
		accountPeriodicFee.setAccount(center.getCustomerAccount());
		accountPeriodicFee.setAccountFeeAmount(new Money("10.0"));
		accountPeriodicFee.setFeeAmount(new Money("10.0"));
		FeesBO trainingFee = TestObjectFactory.createPeriodicFees("Training_Fee", 10.0, 1,
				2, 1);
		accountPeriodicFee.setFees(trainingFee);
		accountFeeSet.add(accountPeriodicFee);
		AccountActionDateEntity accountActionDate= customerAccount.getAccountActionDate(Short.valueOf("1"));
		AccountFeesActionDetailEntity accountFeesaction = new AccountFeesActionDetailEntity();
		accountFeesaction.setAccountFee(accountPeriodicFee);
		accountFeesaction.setAccountActionDate(accountActionDate);
		accountFeesaction.setFee(trainingFee);
		accountFeesaction.setFeeAmount(new Money("10.0"));
		accountFeesaction.setFeeAmountPaid(new Money("0.0"));
		accountFeesaction.setInstallmentId(Short.valueOf("1"));
		accountActionDate.addAccountFeesAction(accountFeesaction);
		TestObjectFactory.flushandCloseSession();
		
		trainingFee = (FeesBO)HibernateUtil.getSessionTL().get(FeesBO.class,trainingFee.getFeeId());
		trainingFee.setUserContext(TestObjectFactory.getUserContext());
		trainingFee.setUpdateFlag(Short.valueOf("3"));
		trainingFee.setAmount("5");
		trainingFee.setFeeAmount(new Money("5.0"));
		trainingFee.modifyStatus(FeeStatus.INACTIVE);
		trainingFee.setFeeUpdateType(new FeePersistenceService().getUpdateTypeEntity(Short.valueOf("1")));
		trainingFee.update();
		TestObjectFactory.flushandCloseSession();
		new ApplyCustomerFeeChangesHelper().execute(System.currentTimeMillis());
		TestObjectFactory.flushandCloseSession();
		center = (CustomerBO)HibernateUtil.getSessionTL().get(CustomerBO.class,center.getCustomerId());
		
		AccountActionDateEntity installment=center.getCustomerAccount().getAccountActionDate(Short.valueOf("1"));
		
		
		AccountFeesActionDetailEntity accountFeesAction = installment.getAccountFeesAction(accountPeriodicFee.getAccountFeeId());
		assertNull(accountFeesAction);
		assertEquals(1,installment.getAccountFeesActionDetails().size());
		
		AccountFeesEntity accountFee=  center.getCustomerAccount().getAccountFees(trainingFee.getFeeId());
		
		assertNotNull(accountFee);
		assertEquals(5.0,accountFee.getAccountFeeAmount().getAmountDoubleValue());
	}
	
	
	public void testExecuteStatusInactiveToActive() throws Exception{
		try{
			CustomerAccountBO customerAccount = center.getCustomerAccount();
			Set<AccountFeesEntity> accountFeeSet = customerAccount.getAccountFees();
			AccountFeesEntity accountPeriodicFee = new AccountFeesEntity();
			accountPeriodicFee.setAccount(center.getCustomerAccount());
			accountPeriodicFee.setAccountFeeAmount(new Money("10.0"));
			accountPeriodicFee.setFeeAmount(new Money("10.0"));
			FeesBO trainingFee = TestObjectFactory.createPeriodicFees("Training_Fee", 10.0, 1,
					2, 1);
			accountPeriodicFee.setFees(trainingFee);
			accountPeriodicFee.setFeeStatus(Short.valueOf("2"));
			accountFeeSet.add(accountPeriodicFee);
			trainingFee.modifyStatus(FeeStatus.INACTIVE);
			trainingFee.setUserContext(TestObjectFactory.getUserContext());
			trainingFee.update();
			TestObjectFactory.flushandCloseSession();
			
			trainingFee = (FeesBO)HibernateUtil.getSessionTL().get(FeesBO.class,trainingFee.getFeeId());
			trainingFee.setUserContext(TestObjectFactory.getUserContext());
			trainingFee.setUpdateFlag(Short.valueOf("2"));
			trainingFee.modifyStatus(FeeStatus.ACTIVE);
			trainingFee.setFeeUpdateType(new FeePersistenceService().getUpdateTypeEntity(Short.valueOf("1")));
			trainingFee.update();
			TestObjectFactory.flushandCloseSession();
			new ApplyCustomerFeeChangesHelper().execute(System.currentTimeMillis());
			TestObjectFactory.flushandCloseSession();
			center = (CustomerBO)HibernateUtil.getSessionTL().get(CustomerBO.class,center.getCustomerId());
			
			AccountActionDateEntity installment=center.getCustomerAccount().getAccountActionDate(Short.valueOf("1"));
			
			
			AccountFeesActionDetailEntity accountFeesAction = installment.getAccountFeesAction(accountPeriodicFee.getAccountFeeId());
			assertNotNull(accountFeesAction);
			assertEquals(2,installment.getAccountFeesActionDetails().size());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
	}
	public void testExecuteStatusInactiveToActiveAndAmountChanged() throws Exception{
		
		CustomerAccountBO customerAccount = center.getCustomerAccount();
		Set<AccountFeesEntity> accountFeeSet = customerAccount.getAccountFees();
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity();
		accountPeriodicFee.setAccount(center.getCustomerAccount());
		accountPeriodicFee.setAccountFeeAmount(new Money("10.0"));
		accountPeriodicFee.setFeeAmount(new Money("10.0"));
		FeesBO trainingFee = TestObjectFactory.createPeriodicFees("Training_Fee", 10.0, 1,
				2, 1);
		accountPeriodicFee.setFees(trainingFee);
		accountPeriodicFee.setFeeStatus(Short.valueOf("2"));
		accountFeeSet.add(accountPeriodicFee);
		trainingFee.modifyStatus(FeeStatus.INACTIVE);
		trainingFee.setUserContext(TestObjectFactory.getUserContext());
		trainingFee.update();
		TestObjectFactory.flushandCloseSession();
		
		trainingFee = (FeesBO)HibernateUtil.getSessionTL().get(FeesBO.class,trainingFee.getFeeId());
		trainingFee.setUserContext(TestObjectFactory.getUserContext());
		trainingFee.setUpdateFlag(Short.valueOf("3"));
		trainingFee.modifyStatus(FeeStatus.ACTIVE);
		trainingFee.setAmount("5");
		trainingFee.setFeeAmount(new Money("5.0"));
		trainingFee.setFeeUpdateType(new FeePersistenceService().getUpdateTypeEntity(Short.valueOf("1")));
		trainingFee.update();
		TestObjectFactory.flushandCloseSession();
		new ApplyCustomerFeeChangesHelper().execute(System.currentTimeMillis());
		TestObjectFactory.flushandCloseSession();
		center = (CustomerBO)HibernateUtil.getSessionTL().get(CustomerBO.class,center.getCustomerId());
		
		AccountActionDateEntity installment=center.getCustomerAccount().getAccountActionDate(Short.valueOf("1"));
		
		
		AccountFeesActionDetailEntity accountFeesAction = installment.getAccountFeesAction(accountPeriodicFee.getAccountFeeId());
		assertNotNull(accountFeesAction);
		assertEquals(5.0,accountFeesAction.getFeeAmount().getAmountDoubleValue());
		assertEquals(2,installment.getAccountFeesActionDetails().size());
		AccountFeesEntity accountFee=  center.getCustomerAccount().getAccountFees(trainingFee.getFeeId());
		assertNotNull(accountFee);
		assertEquals(5.0,accountFee.getAccountFeeAmount().getAmountDoubleValue());

		
	}
	public void testExecuteAmountUpdatedForMultipleAccount() throws Exception{
		
		CustomerAccountBO centerAccount = center.getCustomerAccount();
		CustomerAccountBO groupAccount = group.getCustomerAccount();
		Set<AccountFeesEntity> accountFeeSet = centerAccount.getAccountFees();
		Set<AccountFeesEntity> groupFeeSet = groupAccount.getAccountFees();
		
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity();
		accountPeriodicFee.setAccount(center.getCustomerAccount());
		accountPeriodicFee.setAccountFeeAmount(new Money("10.0"));
		accountPeriodicFee.setFeeAmount(new Money("10.0"));
		
		AccountFeesEntity groupaccountPeriodicFee = new AccountFeesEntity();
		groupaccountPeriodicFee.setAccount(group.getCustomerAccount());
		groupaccountPeriodicFee.setAccountFeeAmount(new Money("10.0"));
		groupaccountPeriodicFee.setFeeAmount(new Money("10.0"));
		
		
		FeesBO trainingFee = TestObjectFactory.createPeriodicFees("Training_Fee", 10.0, 1,
				2, 1);
		accountPeriodicFee.setFees(trainingFee);
		
		groupaccountPeriodicFee.setFees(trainingFee);
		
		accountFeeSet.add(accountPeriodicFee);
		groupFeeSet.add(groupaccountPeriodicFee);
		
		AccountActionDateEntity accountActionDate= centerAccount.getAccountActionDate(Short.valueOf("1"));
		AccountFeesActionDetailEntity accountFeesaction = new AccountFeesActionDetailEntity();
		accountFeesaction.setAccountFee(accountPeriodicFee);
		accountFeesaction.setAccountActionDate(accountActionDate);
		accountFeesaction.setFee(trainingFee);
		accountFeesaction.setFeeAmount(new Money("10.0"));
		accountFeesaction.setFeeAmountPaid(new Money("0.0"));
		accountFeesaction.setInstallmentId(Short.valueOf("1"));
		accountActionDate.addAccountFeesAction(accountFeesaction);
		
		
		AccountActionDateEntity groupaccountActionDate= groupAccount.getAccountActionDate(Short.valueOf("1"));
		AccountFeesActionDetailEntity groupaccountFeesaction = new AccountFeesActionDetailEntity();
		groupaccountFeesaction.setAccountFee(groupaccountPeriodicFee);
		groupaccountFeesaction.setAccountActionDate(groupaccountActionDate);
		groupaccountFeesaction.setFee(trainingFee);
		groupaccountFeesaction.setFeeAmount(new Money("10.0"));
		groupaccountFeesaction.setFeeAmountPaid(new Money("0.0"));
		groupaccountFeesaction.setInstallmentId(Short.valueOf("1"));
		groupaccountActionDate.addAccountFeesAction(groupaccountFeesaction);
		
		
		
		TestObjectFactory.flushandCloseSession();
		
		trainingFee = (FeesBO)HibernateUtil.getSessionTL().get(FeesBO.class,trainingFee.getFeeId());
		trainingFee.setUserContext(TestObjectFactory.getUserContext());
		trainingFee.setAmount("5");
		trainingFee.setUpdateFlag(Short.valueOf("1"));
		trainingFee.setFeeUpdateType(new FeePersistenceService().getUpdateTypeEntity(Short.valueOf("1")));
		trainingFee.save(false);
		TestObjectFactory.flushandCloseSession();
		new ApplyCustomerFeeChangesHelper().execute(System.currentTimeMillis());
		TestObjectFactory.flushandCloseSession();
		center = (CustomerBO)HibernateUtil.getSessionTL().get(CustomerBO.class,center.getCustomerId());
		group = (CustomerBO)HibernateUtil.getSessionTL().get(CustomerBO.class,group.getCustomerId());
		AccountActionDateEntity installment=center.getCustomerAccount().getAccountActionDate(Short.valueOf("1"));
		
		AccountFeesActionDetailEntity accountFeesAction = installment.getAccountFeesAction(accountPeriodicFee.getAccountFeeId());
		assertEquals(5.0 ,accountFeesAction.getFeeAmount().getAmountDoubleValue());
		
		AccountActionDateEntity groupinstallment=group.getCustomerAccount().getAccountActionDate(Short.valueOf("1"));
		
		AccountFeesActionDetailEntity groupaccountFeesAction = groupinstallment.getAccountFeesAction(groupaccountPeriodicFee.getAccountFeeId());
		assertEquals(5.0 ,groupaccountFeesAction.getFeeAmount().getAmountDoubleValue());
		
		
	}
		
}
