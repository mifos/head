/**
 * 
 */
package org.mifos.application.accounts.persistence;

import java.util.List;

import org.mifos.application.accounts.TestAccount;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.CustomerAccountBO;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.accounts.util.valueobjects.CustomerAccount;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.fees.business.FeesBO;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

/**
 * @author krishankg
 *
 */
public class TestAccountPersistence extends TestAccount {			
	
	private AccountPersistence accountPersistence=new AccountPersistence();
	
	public void testSuccessGetNextInstallmentList(){
		List<Short> installmentIdList=null;
		try {
			installmentIdList = accountPersistence.getNextInstallmentList(accountBO.getAccountId());
			assertTrue(true);
		} catch (PersistenceException e) {
			assertFalse(false);
		}
		
	}
	
	
	public void testSuccessLoadBusinessObject(){
		AccountBO accountObject=null;
		try {
			accountObject = accountPersistence.loadBusinessObject(accountBO.getAccountId());
			assertTrue(true);
		} catch (PersistenceException e) {
			assertTrue(false);
		}
		
	}
	
	
	public void testFailureGetNextInstallmentList(){
		try {
			accountPersistence.getNextInstallmentList(null);
			assertTrue(false);
		} catch (PersistenceException e) {
			assertTrue(true);
		}
	}
	
	
	public void testFailureLoadBusinessObject(){
		try {
			accountPersistence.loadBusinessObject(null);
			assertTrue(false);
		} catch (PersistenceException e) {
			assertTrue(true);
		}
	}
	
	public void testGetAccountAction()throws Exception{
		AccountActionEntity accountaction = accountPersistence.getAccountAction(AccountConstants.ACTION_SAVINGS_INTEREST_POSTING);
		assertNotNull(accountaction);
	}
	
	public void testRetrieveCustomerAccountActionDetails() {
		assertNotNull(center.getCustomerAccount());
		List<AccountActionDateEntity> actionDates = accountPersistence.retrieveCustomerAccountActionDetails(center.getCustomerAccount().getAccountId(),new java.sql.Date(System.currentTimeMillis()));
		assertEquals("The size of the due insallments is ", actionDates.size(),1);
	}
	
	public void testOptionalAccountStates() throws Exception{
		assertEquals(Integer.valueOf(1).intValue(),accountPersistence.getAccountStates(Short.valueOf("0")).size());
	}
	
	public void testAccountStatesInUse() throws Exception{
		assertEquals(Integer.valueOf(17).intValue(),accountPersistence.getAccountStates(Short.valueOf("1")).size());
	}
	
	public void testGetAccountsWithTodaysInstallment() throws PersistenceException {				
		assertEquals(2,accountPersistence.getAccountsWithTodaysInstallment().size());		
	}

	public void testGetCustomerAccountsForFee(){
		
		FeesBO periodicFee = TestObjectFactory.createPeriodicFees(
				"ClientPeridoicFee", 5.0, 1, 1, 4);
		AccountFeesEntity accountFee = new AccountFeesEntity();
		accountFee.setFeeAmount(periodicFee.getFeeAmount());
		accountFee.setAccountFeeAmount(periodicFee.getFeeAmount());
		accountFee.setFees(periodicFee);
		CustomerAccountBO customerAccount = center.getCustomerAccount();
		customerAccount.addAccountFees(accountFee);
		TestObjectFactory.updateObject( customerAccount);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		//check for the account fee 
		List accountList =accountPersistence.getCustomerAccountsForFee(periodicFee.getFeeId());
		assertNotNull(accountList);
		assertEquals(1,accountList.size());
		//get all objects again
		accountBO =(AccountBO) TestObjectFactory.getObject(AccountBO.class,accountBO.getAccountId());
		group = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,group.getCustomerId());
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,center.getCustomerId());
		

	}
	
	public void testGetActiveCustomerAccounts(){
		
		List<CustomerAccountBO> customerAccounts = accountPersistence.getActiveCustomerAccounts();
		assertNotNull(customerAccounts);
		assertEquals(2,customerAccounts.size());
	}
}
