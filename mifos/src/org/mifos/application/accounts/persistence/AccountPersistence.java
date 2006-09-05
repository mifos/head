package org.mifos.application.accounts.persistence;

import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountException;

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
import org.mifos.application.accounts.util.helpers.ApplicableCharge;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.checklist.util.valueobjects.CheckListMaster;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.HibernateSearchException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryFactory;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.persistence.Persistence;

public class AccountPersistence extends Persistence {
	
	public AccountBO getAccount(Integer accountId) throws PersistenceException {
		AccountBO account =null;
		try{
			Session session = HibernateUtil.getSessionTL();
			account = (AccountBO) session.get(AccountBO.class, accountId);
		}catch(HibernateException ex){
			throw new PersistenceException(ex);
		}
		return account;
	}
	
	public void updateAccount(AccountBO account) throws PersistenceException {
		try{
			Session session = HibernateUtil.getSessionTL();
			Transaction transaction = HibernateUtil.startTransaction();
			session.update(account);
			transaction.commit();
		}catch(HibernateException ex){
			throw new PersistenceException(ex);
		}
	}
	
	public AccountBO loadBusinessObject(Integer accountId)throws PersistenceException{
		if(!isValid(accountId))
			throw new PersistenceException();
		Session session=null;
		AccountBO accountBO=null;
		try{
			session=HibernateUtil.getSessionTL();
			accountBO=(AccountBO)session.get(AccountBO.class,accountId);
		}catch(HibernateException ex){
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
		}catch(HibernateException e){
			throw new PersistenceException(e);
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
		}catch(HibernateException e){
			throw new PersistenceException(e);
		}
		return accountBO;
	}
	
	public AccountActionEntity getAccountAction(Short actionType) throws PersistenceException{
		try{
			Session session = HibernateUtil.getSessionTL();
			return (AccountActionEntity) session.get(AccountActionEntity.class,	actionType);
		}catch(HibernateException e){
			throw new PersistenceException(e);
		}
	}
	
	public AccountFeesEntity getAccountFeeEntity( Integer accountFeesEntityId)throws PersistenceException{
		Session session=null;
		AccountFeesEntity accountFeesEntity=null;
		try{
			session=HibernateUtil.getSessionTL();
			accountFeesEntity=(AccountFeesEntity)session.get(AccountFeesEntity.class,accountFeesEntityId);
		}catch(HibernateException ex){
			throw new PersistenceException(ex);
		}
		return accountFeesEntity;
	}
	
	public List<AccountActionDateEntity> retrieveCustomerAccountActionDetails(
			Integer accountId, Date transactionDate) throws PersistenceException {
		List<AccountActionDateEntity> queryResult =null;
		try{
			HashMap<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("ACCOUNT_ID", accountId);
			queryParameters.put("ACTION_DATE", transactionDate);
			queryParameters.put("PAYMENT_STATUS", PaymentStatus.UNPAID.getValue());
			queryResult = executeNamedQuery(
					NamedQueryConstants.CUSTOMER_ACCOUNT_ACTIONS_DATE,
					queryParameters);
		}catch(HibernateException e){
			throw new PersistenceException(e);
		}
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
		List queryResult=null;
		try{
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
			queryResult=executeNamedQuery(NamedQueryConstants.GET_TODAYS_UNPAID_INSTALLMENT_FOR_ACTIVE_CUSTOMERS,queryParameters);
		}catch(HibernateException e){
			throw new PersistenceException(e);
		}
		return queryResult;		
	}
	
	public QueryResult getAllAccountNotes(Integer accountId) throws PersistenceException{
		QueryResult notesResult=null;
		try{
			Session session=null;
			notesResult = QueryFactory.getQueryResult("NotesSearch");
			session = notesResult.getSession();
	 		Query query= session.getNamedQuery(NamedQueryConstants.GETALLACCOUNTNOTES);
	 		query.setInteger("accountId",accountId);
	 		notesResult.executeQuery(query);
	 	}catch(HibernateException he) {		
			throw new PersistenceException(he);
		}catch (HibernateProcessException e) {
			throw new PersistenceException(e);
		}catch (HibernateSearchException e) {
			throw new PersistenceException(e);
		}
      return notesResult;
	}
	
	public List<Integer> getCustomerAccountsForFee(Short feeId) throws PersistenceException {
		List queryResult=null;
		try{
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("FEEID",feeId);
		queryResult=executeNamedQuery(NamedQueryConstants.GET_CUSTOMER_ACCOUNTS_FOR_FEE,queryParameters);
		}catch(HibernateException e){
			throw new PersistenceException(e);
		}
		return queryResult;

	}
	public List<AccountBO> getActiveCustomerAndSavingsAccounts() throws PersistenceException{
		List queryResult=null;
		try{
			queryResult=executeNamedQuery(NamedQueryConstants.GET_ACTIVE_CUSTOMER__AND_SAVINGS_ACCOUNTS,null);
		}catch(HibernateException he){
			throw new PersistenceException(he);
		}
		return queryResult;
	}
	
	
	public AccountBO getCustomerAccountWithAccountActionsInitialized(
			Integer accountId) throws PersistenceException {
		Object[] obj1 = null;
		try{
			Session session = HibernateUtil.getSessionTL();
			Query query = session
					.getNamedQuery("accounts.retrieveCustomerAccountWithAccountActions");
			query.setInteger("accountId", accountId);
			List obj = query.list();
			obj1 = (Object[]) obj.get(0);
		}catch(HibernateException he){
			throw new PersistenceException(he);
		}
		return (AccountBO) obj1[0];
	}

	public AccountBO getSavingsAccountWithAccountActionsInitialized(
			Integer accountId) throws PersistenceException {
		Object[] obj1 = null;
		try{
			Session session = HibernateUtil.getSessionTL();
			Query query = session
					.getNamedQuery("accounts.retrieveSavingsAccountWithAccountActions");
			query.setInteger("accountId", accountId);
			List obj = query.list();
			obj1 = (Object[]) obj.get(0);
		}catch(HibernateException he){
			throw new PersistenceException(he);
		}
		return (AccountBO) obj1[0];
	}

	public AccountBO getLoanAccountWithAccountActionsInitialized(
			Integer accountId) throws PersistenceException {
		Object[] obj1 = null;
		try{
			Session session = HibernateUtil.getSessionTL();
			Query query = session
					.getNamedQuery("accounts.retrieveLoanAccountWithAccountActions");
			query.setInteger("accountId", accountId);
			List obj = query.list();
			obj1 = (Object[]) obj.get(0);
		}catch(HibernateException he){
			throw new PersistenceException(he);
		}
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

	public List<CheckListMaster> getStatusChecklist(Short accountStatusId, Short accountTypeId) throws PersistenceException {
		List queryResult = null;
		try{
			HashMap<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("accountTypeId", accountTypeId);
			queryParameters.put("accountStatus", accountStatusId);
			queryParameters.put("checklistStatus", 1);
			queryResult = executeNamedQuery(
					NamedQueryConstants.STATUSCHECKLIST, queryParameters);
		}catch(HibernateException he){
			throw new PersistenceException(he);
		}
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
	
	public List<FeeBO> getAllAppllicableFees(Integer accountId,Short categoryType) throws PersistenceException{
		List queryResult=null;
		try{
			HashMap<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("accountId", accountId);
			queryParameters.put("feeFrequencyTypeId", FeeFrequencyType.PERIODIC.getValue());
			queryParameters.put("active", FeeStatus.ACTIVE.getValue());
			if(categoryType.equals(FeeCategory.LOAN.getValue())){
				queryParameters.put("category",FeeCategory.LOAN.getValue());
				queryResult = executeNamedQuery(
						NamedQueryConstants.GET_ALL_APPLICABLE_LOAN_FEE, queryParameters);
			}else{
				queryParameters.put("category1", FeeCategory.ALLCUSTOMERS.getValue());
				queryParameters.put("category2",categoryType);
				queryResult = executeNamedQuery(
						NamedQueryConstants.GET_ALL_APPLICABLE_CUSTOMER_FEE, queryParameters);
			}
		}catch(HibernateException he){
			throw new PersistenceException(he);
		}
		return queryResult;
	}
	
	private boolean isValid(Integer id){
		if(id != null)
			return true;
		return false;
	}

}
