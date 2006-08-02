package org.mifos.application.accounts.persistence;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountStateFlagEntity;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.checklist.util.valueobjects.CheckListMaster;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.HibernateSearchException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryFactory;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.persistence.Persistence;

public class AccountPersistence extends Persistence {
	
	public AccountBO getAccount(Integer accountId) {
		Session session = HibernateUtil.getSessionTL();
		AccountBO account = (AccountBO) session.get(AccountBO.class, accountId);
		return account;
	}
	
	public AccountBO updateAccount(AccountBO account) {
		Session session = HibernateUtil.getSessionTL();
		Transaction transaction = HibernateUtil.startTransaction();
		session.update(account);
		transaction.commit();
		return account;
	}
	
	public List<Short> getNextInstallmentList(Integer accountId) throws PersistenceException{
		Session session=null;
		List<Short> installmentIds=null;
		try{
			session=HibernateUtil.getSessionTL();
			Query query = session.getNamedQuery(NamedQueryConstants.ACCOUNT_GETNEXTINSTALLMENTIDS);
			query.setInteger("accountId",accountId);
			query.setShort("paymentStatus",PaymentStatus.UNPAID.getValue());
			installmentIds=query.list();
		}catch(Exception ex){
			throw new PersistenceException(ex);
		}
		return installmentIds;
	}
		
	public AccountBO loadBusinessObject(Integer accountId)throws PersistenceException{
		Session session=null;
		AccountBO accountBO=null;
		try{
			session=HibernateUtil.getSessionTL();
			accountBO=(AccountBO)session.get(AccountBO.class,accountId);
		}catch(Exception ex){
			throw new PersistenceException(ex);
		}
		return accountBO;
	}
	
	public Integer getAccountRunningNumber()throws PersistenceException{
		try{
		 List queryResult = executeNamedQuery(NamedQueryConstants.GET_MAX_ACCOUNT_ID, null);
		 Integer accountRunningNumber = new Integer(0);
		 if(null != queryResult && null != queryResult.get(0)){
				 // it breaks after the first iteration because the query should return only one row.
				 accountRunningNumber = new Integer(queryResult.get(0).toString());
		 }
		 return accountRunningNumber + 1;
		}catch(HibernateException he){
			throw new PersistenceException(he);
		}
	 }

	public AccountBO findBySystemId(String accountGlobalNum)throws PersistenceException {
		Map<String,String> queryParameters = new HashMap<String,String>();
		AccountBO accountBO = null;
		queryParameters.put("globalAccountNumber", accountGlobalNum);
		try{
			List<AccountBO> queryResult = executeNamedQuery(NamedQueryConstants.FIND_ACCOUNT_BY_SYSTEM_ID, queryParameters);
			if(null != queryResult && queryResult.size() > 0){
				accountBO = queryResult.get(0);
			}
		}catch(HibernateException he){
			throw new PersistenceException(he);
		}
		
		return accountBO;
	}
	
	public AccountActionEntity getAccountAction(Short actionType) throws PersistenceException{
		try{
			Session session = HibernateUtil.getSessionTL();
			return (AccountActionEntity) session.get(AccountActionEntity.class,	actionType);
		}catch(HibernateException he){
			throw new PersistenceException(he);
		}
	}
	
	public AccountFeesEntity getAccountFeeEntity( Integer accountFeesEntityId)throws PersistenceException{
		Session session=null;
		AccountFeesEntity accountFeesEntity=null;
		try{
			session=HibernateUtil.getSessionTL();
			accountFeesEntity=(AccountFeesEntity)session.get(AccountFeesEntity.class,accountFeesEntityId);
		}catch(Exception ex){
			throw new PersistenceException(ex);
		}
		return accountFeesEntity;
	}
	
	public List<AccountActionDateEntity> retrieveCustomerAccountActionDetails(
			Integer accountId, Date transactionDate) {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("ACCOUNT_ID", accountId);
		queryParameters.put("ACTION_DATE", transactionDate);
		queryParameters.put("PAYMENT_STATUS", PaymentStatus.UNPAID.getValue());
		List<AccountActionDateEntity> queryResult = executeNamedQuery(
				NamedQueryConstants.CUSTOMER_ACCOUNT_ACTIONS_DATE,
				queryParameters);
		return queryResult;
	}
	
	public List<AccountStateEntity> getAccountStates(Short optionalFlag)throws PersistenceException{
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("OPTIONAL_FLAG", optionalFlag);
		List<AccountStateEntity> queryResult=null;
		try{
			queryResult = executeNamedQuery(NamedQueryConstants.GET_ACCOUNT_STATES, queryParameters);
		}catch(HibernateException he){
			throw new PersistenceException(he);
		}
		return queryResult;
	}
	
	public List<Integer> getAccountsWithTodaysInstallment() throws PersistenceException{				
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		Date currentDate = new Date(System.currentTimeMillis());
		queryParameters.put("CUSTOMER_TYPE_ID",CustomerConstants.CUSTOMER_TYPE_ID);
		queryParameters.put("ACTIVE_CENTER_STATE",CustomerConstants.CENTER_ACTIVE_STATE);
		queryParameters.put("ACTIVE_GROUP_STATE",CustomerConstants.GROUP_ACTIVE_STATE);
		queryParameters.put("ACTIVE_CLIENT_STATE",CustomerConstants.CLIENT_APPROVED);
		queryParameters.put("ONHOLD_CLIENT_STATE",CustomerConstants.CLIENT_ONHOLD);
		queryParameters.put("ONHOLD_GROUP_STATE",GroupConstants.HOLD);
		queryParameters.put("CURRENT_DATE",currentDate);
		queryParameters.put("PAYMENT_UNPAID",PaymentStatus.UNPAID.getValue());		
		return executeNamedQuery(NamedQueryConstants.GET_TODAYS_UNPAID_INSTALLMENT_FOR_ACTIVE_CUSTOMERS,queryParameters);			
	}
	
	public QueryResult getAllAccountNotes(Integer accountId) throws PersistenceException, HibernateSearchException, HibernateProcessException {
		QueryResult notesResult=null;
		try{
			Session session=null;
			 notesResult = QueryFactory.getQueryResult("NotesSearch");
			 session = notesResult.getSession();
	 		Query query= session.getNamedQuery(NamedQueryConstants.GETALLACCOUNTNOTES);
	 		query.setInteger("accountId",accountId);
	 		notesResult.executeQuery(query);
	 	}
		catch(HibernateProcessException  hpe) {		
			throw hpe;
		}
      return notesResult;
	}
	
	public List<Integer> getCustomerAccountsForFee(Short feeId){
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("FEEID",feeId);
		return executeNamedQuery(NamedQueryConstants.GET_CUSTOMER_ACCOUNTS_FOR_FEE,queryParameters);

	}
	public List<AccountBO> getActiveCustomerAndSavingsAccounts(){
		return executeNamedQuery(NamedQueryConstants.GET_ACTIVE_CUSTOMER__AND_SAVINGS_ACCOUNTS,null);

	}
	public AccountActionDateEntity getLastInstallment(Integer accountId ){
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("accountId",accountId);
		
		List intallments  = executeNamedQuery(NamedQueryConstants.GET_LASTINSTALLMENT,queryParameters);
		if (intallments!=null&&intallments.size()>0)return (AccountActionDateEntity)intallments.get(0);
		else
			return null;

	}
	
	public AccountBO getCustomerAccountWithAccountActionsInitialized(
			Integer accountId) {
		Session session = HibernateUtil.getSessionTL();
		Query query = session
				.getNamedQuery("accounts.retrieveCustomerAccountWithAccountActions");
		query.setInteger("accountId", accountId);
		List obj = query.list();
		Object[] obj1 = (Object[]) obj.get(0);
		return (AccountBO) obj1[0];
	}

	public AccountBO getSavingsAccountWithAccountActionsInitialized(
			Integer accountId) {
		Session session = HibernateUtil.getSessionTL();
		Query query = session
				.getNamedQuery("accounts.retrieveSavingsAccountWithAccountActions");
		query.setInteger("accountId", accountId);
		List obj = query.list();
		Object[] obj1 = (Object[]) obj.get(0);
		return (AccountBO) obj1[0];
	}

	public AccountBO getLoanAccountWithAccountActionsInitialized(
			Integer accountId) {
		Session session = HibernateUtil.getSessionTL();
		Query query = session
				.getNamedQuery("accounts.retrieveLoanAccountWithAccountActions");
		query.setInteger("accountId", accountId);
		List obj = query.list();
		Object[] obj1 = (Object[]) obj.get(0);
		return (AccountBO) obj1[0];
	}
	
	public List<AccountStateEntity> retrieveAllAccountStateList(Short prdTypeId) throws PersistenceException {
		try {
			HashMap<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("prdTypeId", prdTypeId);
			List<AccountStateEntity> queryResult = executeNamedQuery(
					NamedQueryConstants.RETRIEVEALLACCOUNTSTATES,
					queryParameters);
			return queryResult;
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}

	}

	public List<CheckListMaster> getStatusChecklist(Short accountStatusId, Short accountTypeId) {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("accountTypeId", accountTypeId);
		queryParameters.put("accountStatus", accountStatusId);
		queryParameters.put("checklistStatus", 1);
		List queryResult = executeNamedQuery(
				NamedQueryConstants.STATUSCHECKLIST, queryParameters);
		return queryResult;
	}

	public AccountStateFlagEntity getAccountStateFlag(Short flagId) throws PersistenceException {
		AccountStateFlagEntity accountStateFlagEntity;
		try {
			Session session = HibernateUtil.getSessionTL();
			accountStateFlagEntity = (AccountStateFlagEntity) session.get(AccountStateFlagEntity.class, flagId);
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
		return accountStateFlagEntity;
	}
	
}
