package org.mifos.framework.components.cronjobs.helpers;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.mifos.application.accounts.business.CustomerAccountBO;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.framework.components.cronjobs.TaskHelper;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;

public class ApplyCustomerFeeHelper extends TaskHelper {
		
	public void execute(long timeInMills){				
		try{
			AccountPersistence accountPersistence = new AccountPersistence();
			List<Integer> accountIds = accountPersistence.getAccountsWithTodaysInstallment();
			for(Integer accountId : accountIds){
				try{			
					Session session = HibernateUtil.getSessionTL();
					CustomerAccountBO customerAccountBO = (CustomerAccountBO) session.get(CustomerAccountBO.class,accountId);
					Date date = DateUtils.getDateWithoutTimeStamp(timeInMills);
					customerAccountBO.applyPeriodicFees(date);					
					HibernateUtil.commitTransaction();
				}			
				catch(Exception e){
					e.printStackTrace();
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
