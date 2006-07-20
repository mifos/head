package org.mifos.application.accounts.business.service;

import java.util.List;

import org.hibernate.StaleObjectStateException;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountNotesEntity;
import org.mifos.application.accounts.business.TransactionHistoryView;
import org.mifos.application.accounts.dao.AccountNotesDAO;
import org.mifos.application.accounts.exceptions.AccountExceptionConstants;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.PersistenceServiceName;



public class AccountBusinessService extends BusinessService {

	AccountPersistanceService dbService;
	
	public AccountBusinessService() {
	}

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {		
		return null;
	}
	
	public void removeFees(Integer accountId,Short feeId,Short personnelId) throws SystemException,ApplicationException{
		AccountBO accountBO=null;
		try{
			accountBO=getDBService().loadBusinessObject(accountId);
			accountBO.removeFees(feeId,personnelId);
			getDBService().save(accountBO);
		}catch (ServiceException e) {
			throw new ApplicationException(e);
		}catch(StaleObjectStateException sose){
			throw new ApplicationException(AccountConstants.VERSIONNOMATCHINGPROBLEM);
		}
	}
	
	public AccountBO findBySystemId(String accountGlobalNum)throws SystemException,ApplicationException {
		AccountBO accountBO = null;
		try {
			accountBO =  getDBService().findBySystemId(accountGlobalNum);
		} catch (PersistenceException e) {
			
			throw new ApplicationException(AccountExceptionConstants.FINDBYGLOBALACCNTEXCEPTION,e,new Object[]{accountGlobalNum});
			
		}
		return accountBO;
	}

	public List<TransactionHistoryView> getTrxnHistory(AccountBO accountBO,UserContext uc)throws SystemException,ApplicationException{
	  accountBO.setUserContext(uc);
	  return accountBO.getTransactionHistoryView();
	}
  
	public AccountBO getAccount(Integer accountId)throws SystemException,ApplicationException {
		return  getDBService().getAccount(accountId);
	}
	
	public AccountActionEntity getAccountAction(Short actionType, Short localeId)throws SystemException{
		AccountActionEntity accountAction = getDBService().getAccountAction(actionType);
		accountAction.setLocaleId(localeId);
		return accountAction;
	}
	
	private AccountPersistanceService getDBService()throws ServiceException{
		if(dbService==null)
			dbService=(AccountPersistanceService) ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.Account);
		
		return dbService;
	}
	
	public QueryResult getAllAccountNotes(Integer accountId) throws ApplicationException, SystemException{
		return getDBService().getAllAccountNotes(accountId);
	}

}
