package org.mifos.framework.components.cronjobs.helpers;

import java.util.List;

import org.hibernate.Session;
import org.mifos.application.accounts.business.CustomerAccountBO;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.cronjobs.TaskHelper;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.PersistenceServiceName;

public class ApplyCustomerFeeHelper extends TaskHelper {
		
	public void execute(long timeInMills){		
		try{
			AccountPersistanceService accountPersistanceService = (AccountPersistanceService) ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.Account);
			List<Integer> accountIds = accountPersistanceService.getAccountsWithTodaysInstallment();
			for(Integer accountId : accountIds){
				try{
					HibernateUtil.startTransaction();
					Session session = HibernateUtil.getSessionTL();
					CustomerAccountBO customerAccountBO = (CustomerAccountBO) session.get(CustomerAccountBO.class,accountId);
					customerAccountBO.applyPeriodicFees(timeInMills);
					HibernateUtil.commitTransaction();
				}			
				catch(Exception e){
					HibernateUtil.rollbackTransaction();
				}finally{
					HibernateUtil.closeSession();
				}
			}			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean isTaskAllowedToRun() {
		return true;
	}

		
}
