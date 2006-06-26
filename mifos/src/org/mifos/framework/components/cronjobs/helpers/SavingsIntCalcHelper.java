package org.mifos.framework.components.cronjobs.helpers;

import java.util.Calendar;
import java.util.List;

import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.cronjobs.TaskHelper;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class SavingsIntCalcHelper extends TaskHelper{
	
	public void execute(long timeInMillis) {
		try{
			Calendar cal = Calendar.getInstance(Configuration.getInstance().getSystemConfig().getMifosTimeZone());
			cal.setTimeInMillis(timeInMillis);
			SavingsPersistence persistence = new SavingsPersistence();
			List<Integer> accountList=persistence.retreiveAccountsPendingForIntCalc(cal.getTime());
			HibernateUtil.closeSession();
			for(Integer accountId: accountList){
				try{
					SavingsBO savings = persistence.findById(accountId);
					savings.updateInterestAccrued();
					HibernateUtil.commitTransaction();
				}catch(Exception e){
					HibernateUtil.rollbackTransaction();
				}finally {
					HibernateUtil.closeSession();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			HibernateUtil.closeSession();
		}
	}
	
	public boolean isTaskAllowedToRun() {
		return true;
	}
}
