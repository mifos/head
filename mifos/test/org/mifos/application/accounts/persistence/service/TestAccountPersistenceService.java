/**
 * 
 */
package org.mifos.application.accounts.persistence.service;

import java.util.List;

import org.mifos.application.accounts.TestAccount;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.framework.exceptions.PersistenceException;
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
}
