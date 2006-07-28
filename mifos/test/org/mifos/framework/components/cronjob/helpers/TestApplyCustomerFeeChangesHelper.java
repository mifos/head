package org.mifos.framework.components.cronjob.helpers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.CustomerAccountBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.persistence.service.FeePersistenceService;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeChangeType;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingFrequency;
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
		List<CustomerBO> customerList = new ArrayList<CustomerBO>();
		if(group!=null)
			customerList.add(group);
		if(center!=null)
			customerList.add(center);
		TestObjectFactory.cleanUp(customerList);
	}
 
	public void testExecuteAmountUpdated() throws Exception{
		
		CustomerAccountBO customerAccount = center.getCustomerAccount();
		Set<AccountFeesEntity> accountFeeSet = customerAccount.getAccountFees();
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity();
		accountPeriodicFee.setAccount(center.getCustomerAccount());
		accountPeriodicFee.setAccountFeeAmount(new Money("10.0"));
		accountPeriodicFee.setFeeAmount(new Money("10.0"));
		FeeBO trainingFee = TestObjectFactory.createPeriodicAmountFee("Training_Fee", FeeCategory.ALLCUSTOMERS,"10", MeetingFrequency.WEEKLY, Short.valueOf("2"));
		
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
		
		trainingFee = (FeeBO)HibernateUtil.getSessionTL().get(FeeBO.class,trainingFee.getFeeId());
		trainingFee.setUserContext(TestObjectFactory.getUserContext());
		((AmountFeeBO)trainingFee).setFeeAmount(TestObjectFactory.getMoneyForMFICurrency("5"));
		trainingFee.updateFeeChangeType(FeeChangeType.AMOUNT_UPDATED);
		trainingFee.save();
		TestObjectFactory.flushandCloseSession();
		new ApplyCustomerFeeChangesHelper().execute(System.currentTimeMillis());
		TestObjectFactory.flushandCloseSession();
		center = (CustomerBO)HibernateUtil.getSessionTL().get(CustomerBO.class,center.getCustomerId());
		
		AccountActionDateEntity installment=center.getCustomerAccount().getAccountActionDate(Short.valueOf("1"));
		
		AccountFeesActionDetailEntity accountFeesAction = installment.getAccountFeesAction(accountPeriodicFee.getAccountFeeId());
		assertEquals(5.0 ,accountFeesAction.getFeeAmount().getAmountDoubleValue());
		HibernateUtil.closeSession();
		center = (CustomerBO)HibernateUtil.getSessionTL().get(CustomerBO.class,center.getCustomerId());
		group = (CustomerBO)HibernateUtil.getSessionTL().get(CustomerBO.class,group.getCustomerId());
	}
	
	public void testExecuteStatusUpdatedToInactive() throws Exception{
		
		CustomerAccountBO customerAccount = center.getCustomerAccount();
		Set<AccountFeesEntity> accountFeeSet = customerAccount.getAccountFees();
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity();
		accountPeriodicFee.setAccount(center.getCustomerAccount());
		accountPeriodicFee.setAccountFeeAmount(new Money("10.0"));
		accountPeriodicFee.setFeeAmount(new Money("10.0"));
		FeeBO trainingFee = TestObjectFactory.createPeriodicAmountFee("Training_Fee", FeeCategory.ALLCUSTOMERS,"10", MeetingFrequency.WEEKLY, Short.valueOf("2"));
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
		
		trainingFee = (FeeBO)HibernateUtil.getSessionTL().get(FeeBO.class,trainingFee.getFeeId());
		trainingFee.setUserContext(TestObjectFactory.getUserContext());
		trainingFee.updateFeeChangeType(FeeChangeType.STATUS_UPDATED);
		trainingFee.updateStatus(FeeStatus.INACTIVE);
		
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
		FeeBO trainingFee = TestObjectFactory.createPeriodicAmountFee("Training_Fee", FeeCategory.ALLCUSTOMERS,"10", MeetingFrequency.WEEKLY, Short.valueOf("2"));
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
		
		trainingFee = (FeeBO)HibernateUtil.getSessionTL().get(FeeBO.class,trainingFee.getFeeId());
		trainingFee.setUserContext(TestObjectFactory.getUserContext());
		trainingFee.updateFeeChangeType(FeeChangeType.AMOUNT_AND_STATUS_UPDATED);
		((AmountFeeBO)trainingFee).setFeeAmount(TestObjectFactory.getMoneyForMFICurrency("5"));
		trainingFee.updateStatus(FeeStatus.INACTIVE);
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
		CustomerAccountBO customerAccount = center.getCustomerAccount();
		Set<AccountFeesEntity> accountFeeSet = customerAccount.getAccountFees();
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity();
		accountPeriodicFee.setAccount(center.getCustomerAccount());
		accountPeriodicFee.setAccountFeeAmount(new Money("10.0"));
		accountPeriodicFee.setFeeAmount(new Money("10.0"));
		FeeBO trainingFee = TestObjectFactory.createPeriodicAmountFee("Training_Fee", FeeCategory.ALLCUSTOMERS,"10", MeetingFrequency.WEEKLY, Short.valueOf("2"));
		accountPeriodicFee.setFees(trainingFee);
		accountPeriodicFee.setFeeStatus(Short.valueOf("2"));
		accountFeeSet.add(accountPeriodicFee);
		trainingFee.updateStatus(FeeStatus.INACTIVE);
		trainingFee.setUserContext(TestObjectFactory.getUserContext());
		trainingFee.update();
		TestObjectFactory.flushandCloseSession();
		
		trainingFee = (FeeBO)HibernateUtil.getSessionTL().get(FeeBO.class,trainingFee.getFeeId());
		trainingFee.setUserContext(TestObjectFactory.getUserContext());
		trainingFee.updateFeeChangeType(FeeChangeType.STATUS_UPDATED);
		trainingFee.updateStatus(FeeStatus.ACTIVE);
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
	
	public void testExecuteStatusInactiveToActiveAndAmountChanged() throws Exception{
		
		CustomerAccountBO customerAccount = center.getCustomerAccount();
		Set<AccountFeesEntity> accountFeeSet = customerAccount.getAccountFees();
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity();
		accountPeriodicFee.setAccount(center.getCustomerAccount());
		accountPeriodicFee.setAccountFeeAmount(new Money("10.0"));
		accountPeriodicFee.setFeeAmount(new Money("10.0"));
		FeeBO trainingFee = TestObjectFactory.createPeriodicAmountFee("Training_Fee", FeeCategory.ALLCUSTOMERS,"10", MeetingFrequency.WEEKLY, Short.valueOf("2"));
		accountPeriodicFee.setFees(trainingFee);
		accountPeriodicFee.setFeeStatus(Short.valueOf("2"));
		accountFeeSet.add(accountPeriodicFee);
		trainingFee.updateStatus(FeeStatus.INACTIVE);
		trainingFee.setUserContext(TestObjectFactory.getUserContext());
		trainingFee.update();
		TestObjectFactory.flushandCloseSession();
		
		trainingFee = (FeeBO)HibernateUtil.getSessionTL().get(FeeBO.class,trainingFee.getFeeId());
		trainingFee.setUserContext(TestObjectFactory.getUserContext());
		trainingFee.updateFeeChangeType(FeeChangeType.AMOUNT_AND_STATUS_UPDATED);
		trainingFee.updateStatus(FeeStatus.ACTIVE);
		((AmountFeeBO)trainingFee).setFeeAmount(TestObjectFactory.getMoneyForMFICurrency("5"));
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
		
		FeeBO trainingFee = TestObjectFactory.createPeriodicAmountFee("Training_Fee", FeeCategory.ALLCUSTOMERS,"10", MeetingFrequency.WEEKLY, Short.valueOf("2"));
		
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
		
		trainingFee = (FeeBO)HibernateUtil.getSessionTL().get(FeeBO.class,trainingFee.getFeeId());
		trainingFee.setUserContext(TestObjectFactory.getUserContext());
		((AmountFeeBO)trainingFee).setFeeAmount(TestObjectFactory.getMoneyForMFICurrency("5"));
		trainingFee.updateFeeChangeType(FeeChangeType.AMOUNT_UPDATED);
		trainingFee.save();
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
		
		List<FeeBO> feeList = new ArrayList<FeeBO>();
		for(AccountFeesEntity accountFees: groupAccount.getAccountFees()){
			feeList.add(accountFees.getFees());
			accountFees.setFees(null);
		}
		
	}		
}
