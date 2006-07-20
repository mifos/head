/**
 * 
 */
package org.mifos.application.accounts.persistence.service;

import java.util.List;

import org.mifos.application.accounts.TestAccount;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.CustomerAccountBO;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.fees.business.FeesBO;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;
/**
 * @author krishankg
 *
 */
public class TestAccountPersistenceService  extends TestAccount{
	
	AccountPersistanceService accountPersistenceService=new AccountPersistanceService();
		
	public void testSuccessloadBusinessObjectService(){
		AccountBO accountObject=null;
		try {
			accountObject=accountPersistenceService.loadBusinessObject(accountBO.getAccountId());
			assertEquals(accountBO.getAccountId(),accountObject.getAccountId());
		} catch (PersistenceException e) {
			assertTrue(false);
		}

	}
	
		
	public void testSuccessGetNextInstallmentListService(){
		List<Short> installmentIdList=null;
		try {
			installmentIdList = accountPersistenceService.getNextInstallmentList(accountBO.getAccountId());
			assertEquals(6,installmentIdList.size());
		}catch (PersistenceException e) {
			assertTrue(false);
		}
	}
	
	
	public void testSuccessSave(){
		try {
			accountPersistenceService.save(accountBO);
			assertTrue(true);
		} catch (PersistenceException e) {
			assert(false);
		}
	}
	
	public void testGetAccountAction()throws Exception{
		AccountActionEntity accountaction = accountPersistenceService.getAccountAction(AccountConstants.ACTION_SAVINGS_INTEREST_POSTING);
		assertNotNull(accountaction);
	}
	
	public void testRetrieveCustomerAccountActionDetails() {
		assertNotNull(center.getCustomerAccount());
		List<AccountActionDateEntity> actionDates = accountPersistenceService.retrieveCustomerAccountActionDetails(center.getCustomerAccount().getAccountId(),new java.sql.Date(System.currentTimeMillis()));
		assertEquals("The size of the due insallments is ", actionDates.size(),1);
	}
	
	public void testOptionalAccountStates() throws Exception{
		assertEquals(Integer.valueOf(1).intValue(),accountPersistenceService.getAccountStates(Short.valueOf("0")).size());
	}
	
	public void testAccountStatesInUse() throws Exception{
		assertEquals(Integer.valueOf(17).intValue(),accountPersistenceService.getAccountStates(Short.valueOf("1")).size());
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
		List accountList =accountPersistenceService.getCustomerAccountsForFee(periodicFee.getFeeId());
		assertNotNull(accountList);
		assertEquals(1,accountList.size());
		//get all objects again
		accountBO =(AccountBO) TestObjectFactory.getObject(AccountBO.class,accountBO.getAccountId());
		group = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,group.getCustomerId());
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,center.getCustomerId());
		

	}

public void testGetActiveCustomerAccounts(){
	
	List<CustomerAccountBO> customerAccounts = accountPersistenceService.getActiveCustomerAccounts();
	assertNotNull(customerAccounts);
	assertEquals(2,customerAccounts.size());
}
}
