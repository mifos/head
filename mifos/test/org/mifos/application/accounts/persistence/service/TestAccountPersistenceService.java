/**
 * 
 */
package org.mifos.application.accounts.persistence.service;

import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.TestAccount;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.CustomerAccountBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.fees.business.FeesBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

/**
 * @author krishankg
 * 
 */
public class TestAccountPersistenceService extends TestAccount {

	AccountPersistanceService accountPersistenceService = new AccountPersistanceService();

	public void testSuccessloadBusinessObjectService() {
		AccountBO accountObject = null;
		try {
			accountObject = accountPersistenceService
					.loadBusinessObject(accountBO.getAccountId());
			assertEquals(accountBO.getAccountId(), accountObject.getAccountId());
		} catch (PersistenceException e) {
			assertTrue(false);
		}

	}

	public void testSuccessGetNextInstallmentListService() {
		List<Short> installmentIdList = null;
		try {
			installmentIdList = accountPersistenceService
					.getNextInstallmentList(accountBO.getAccountId());
			assertEquals(6, installmentIdList.size());
		} catch (PersistenceException e) {
			assertTrue(false);
		}
	}

	public void testSuccessSave() {
		try {
			accountPersistenceService.save(accountBO);
			assertTrue(true);
		} catch (PersistenceException e) {
			assert (false);
		}
	}

	public void testGetAccountAction() throws Exception {
		AccountActionEntity accountaction = accountPersistenceService
				.getAccountAction(AccountConstants.ACTION_SAVINGS_INTEREST_POSTING);
		assertNotNull(accountaction);
	}

	public void testRetrieveCustomerAccountActionDetails() {
		assertNotNull(center.getCustomerAccount());
		List<AccountActionDateEntity> actionDates = accountPersistenceService
				.retrieveCustomerAccountActionDetails(center
						.getCustomerAccount().getAccountId(),
						new java.sql.Date(System.currentTimeMillis()));
		assertEquals("The size of the due insallments is ", actionDates.size(),
				1);
	}

	public void testOptionalAccountStates() throws Exception {
		assertEquals(Integer.valueOf(1).intValue(), accountPersistenceService
				.getAccountStates(Short.valueOf("0")).size());
	}

	public void testAccountStatesInUse() throws Exception {
		assertEquals(Integer.valueOf(17).intValue(), accountPersistenceService
				.getAccountStates(Short.valueOf("1")).size());
	}

	public void testGetCustomerAccountsForFee() {

		FeesBO periodicFee = TestObjectFactory.createPeriodicFees(
				"ClientPeridoicFee", 5.0, 1, 1, 4);
		AccountFeesEntity accountFee = new AccountFeesEntity();
		accountFee.setFeeAmount(periodicFee.getFeeAmount());
		accountFee.setAccountFeeAmount(periodicFee.getFeeAmount());
		accountFee.setFees(periodicFee);
		CustomerAccountBO customerAccount = center.getCustomerAccount();
		customerAccount.addAccountFees(accountFee);
		TestObjectFactory.updateObject(customerAccount);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		// check for the account fee
		List accountList = accountPersistenceService
				.getCustomerAccountsForFee(periodicFee.getFeeId());
		assertNotNull(accountList);
		assertEquals(1, accountList.size());
		// get all objects again
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		group = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());

	}

	public void testGetActiveCustomerAndSavingsAccounts() throws Exception{

		SavingsBO savingsBO =  TestObjectFactory.createSavingsAccount("12345678910", group,
				new Short("16"), new Date(), createSavingsOffering("qqqqq"), TestObjectFactory.getUserContext());
		List<AccountBO> customerAccounts = accountPersistenceService.getActiveCustomerAndSavingsAccounts();
		assertNotNull(customerAccounts);
		assertEquals(3,customerAccounts.size());
		TestObjectFactory.cleanUp(savingsBO);

	}
	public void testGetLastInstallment(){
		
		Short maxInstallmentid =null;
		for (AccountActionDateEntity installment : center.getCustomerAccount().getAccountActionDates()) {
			
			if ( maxInstallmentid==null)
			 maxInstallmentid = installment.getInstallmentId();
			else {
				
				if(maxInstallmentid.shortValue()< installment.getInstallmentId().shortValue())maxInstallmentid=installment.getInstallmentId();
				
			}
		}
		assertEquals(maxInstallmentid.shortValue(), accountPersistenceService.getLastInstallment(center.getCustomerAccount().getAccountId()).getInstallmentId().shortValue());
		
		
	}
	private SavingsOfferingBO createSavingsOffering(String offeringName) {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createSavingsOffering(offeringName, Short
				.valueOf("2"), new Date(System.currentTimeMillis()), Short
				.valueOf("2"), 300.0, Short.valueOf("1"), 1.2, 200.0, 200.0,
				Short.valueOf("2"), Short.valueOf("1"), meetingIntCalc,
				meetingIntPost);
	}
}
