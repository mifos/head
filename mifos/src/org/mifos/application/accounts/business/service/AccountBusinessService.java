package org.mifos.application.accounts.business.service;

import java.util.List;

import org.hibernate.StaleObjectStateException;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.TransactionHistoryView;
import org.mifos.application.accounts.exceptions.AccountExceptionConstants;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.TrxnObjectBuilder;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.PersistenceServiceName;



public class AccountBusinessService extends BusinessService {

	AccountPersistanceService accountPersistanceService=null;
	
	public AccountBusinessService() {
	}

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		
		return null;
	}
	
	public void removeFees(Integer accountId,Short feeId,Short personnelId) throws SystemException,ApplicationException{
		AccountBO accountBO=null;
		try{
			accountPersistanceService=(AccountPersistanceService)ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.Account);
			accountBO=accountPersistanceService.loadBusinessObject(accountId);
			accountBO.removeFees(feeId,personnelId);
			accountPersistanceService.save(accountBO);
		}catch (ServiceException e) {
			throw new ApplicationException(e);
		}catch(StaleObjectStateException sose){
			throw new ApplicationException(AccountConstants.VERSIONNOMATCHINGPROBLEM);
		}
	}
	
	public AccountBO findBySystemId(String accountGlobalNum)throws SystemException,ApplicationException {
		AccountBO accountBO = null;
		try {
			accountPersistanceService=(AccountPersistanceService)ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.Account);
			accountBO =  accountPersistanceService.findBySystemId(accountGlobalNum);
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
		accountPersistanceService=(AccountPersistanceService)ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.Account);
		return  accountPersistanceService.getAccount(accountId);
	}

}
