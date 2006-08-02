package org.mifos.application.accounts.persistence.service;

import java.sql.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountStateFlagEntity;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.checklist.util.valueobjects.CheckListMaster;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.HibernateSearchException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.persistence.service.PersistenceService;

public class AccountPersistanceService extends PersistenceService {
	AccountPersistence accountPersistence =  new AccountPersistence();

	public AccountBO getAccount(Integer accountId) {
		return accountPersistence.getAccount(accountId);
	}
	
	public AccountBO updateAccount(AccountBO account) {
		return accountPersistence.updateAccount(account);
	}
	
	public AccountBO update(AccountBO account) {
		return (AccountBO)accountPersistence.createOrUpdate(account);
	}
	
	public List<Short> getNextInstallmentList(Integer accountId) throws PersistenceException{
		return accountPersistence.getNextInstallmentList(accountId);
	}
		
	public AccountBO loadBusinessObject(Integer accountId) throws PersistenceException{
		return accountPersistence.loadBusinessObject(accountId);
	}
	
	public void save(AccountBO accountBO)  throws PersistenceException{
		accountPersistence.createOrUpdate(accountBO);
	}
	
	public void delete(Object object)throws HibernateException {
		accountPersistence.delete(object);
	}
	
	/**
	 * This method returns the running number which is indicated by the max account id in the accounts table  
	 */
	public Integer getAccountRunningNumber()throws PersistenceException{
		 return accountPersistence.getAccountRunningNumber();
	}

	public AccountBO findBySystemId(String accountGlobalNum)throws PersistenceException {
		return accountPersistence.findBySystemId(accountGlobalNum);
	}
	
	public AccountActionEntity getAccountAction(Short actionType)throws PersistenceException{
		return accountPersistence.getAccountAction(actionType);
	}
	
	public AccountFeesEntity getAccountFeeEntity( Integer accountFeesEntityId)throws PersistenceException{
		return accountPersistence.getAccountFeeEntity(accountFeesEntityId);
	}
	
	public List<AccountActionDateEntity> retrieveCustomerAccountActionDetails(
			Integer accountId, Date transactionDate) {
		return accountPersistence.retrieveCustomerAccountActionDetails(accountId,transactionDate);
	}
	
	public List<AccountStateEntity> getAccountStates(Short optionalFlag)throws PersistenceException{
		return accountPersistence.getAccountStates(optionalFlag);
	}
	public List<Integer> getAccountsWithTodaysInstallment() throws PersistenceException{
		return accountPersistence.getAccountsWithTodaysInstallment();
	}
	public QueryResult getAllAccountNotes(Integer accountId) throws PersistenceException, HibernateSearchException, HibernateProcessException {
		return accountPersistence.getAllAccountNotes(accountId);
	}
	public List<Integer> getCustomerAccountsForFee(Short feeId){
		return accountPersistence.getCustomerAccountsForFee(feeId);

	}
	public List<AccountBO> getActiveCustomerAndSavingsAccounts(){
		return accountPersistence.getActiveCustomerAndSavingsAccounts();

	}
	public AccountActionDateEntity getLastInstallment(Integer accountId ){
		
		return accountPersistence.getLastInstallment(accountId );

	}
	
	public List<AccountStateEntity> retrieveAllAccountStateList(Short prdTypeId) throws PersistenceException{
		return accountPersistence.retrieveAllAccountStateList(prdTypeId);
	}
	
	public List<CheckListMaster> getStatusChecklist(Short accountStatusId, Short accountTypeId) {
		return accountPersistence.getStatusChecklist(accountStatusId,accountTypeId);
	}
	
	public AccountStateFlagEntity getAccountStateFlag(Short flagId) throws PersistenceException {
		return accountPersistence.getAccountStateFlag(flagId);
	}
}
