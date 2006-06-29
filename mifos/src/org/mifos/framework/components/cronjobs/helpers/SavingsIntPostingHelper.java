package org.mifos.framework.components.cronjobs.helpers;

import java.util.Calendar;
import java.util.List;

import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.cronjobs.TaskHelper;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class SavingsIntPostingHelper extends TaskHelper{
	public void execute(long timeInMillis) {
		try{
			Calendar cal = Calendar.getInstance(Configuration.getInstance().getSystemConfig().getMifosTimeZone());
			cal.setTimeInMillis(timeInMillis);
			SavingsPersistence persistence = new SavingsPersistence();
			List<Integer> accountList=persistence.retreiveAccountsPendingForIntPosting(cal.getTime());
			HibernateUtil.closeSession();
			System.out.println("----------size: "+ accountList.size());
			for(Integer accountId: accountList){
				try{
					SavingsBO savings = persistence.findById(accountId);
					System.out.println("--------before: "+ savings.getInterestToBePosted());
					savings.postInterest();
					System.out.println("--------after: "+ savings.getInterestToBePosted());
					HibernateUtil.commitTransaction();
					HibernateUtil.closeSession();
				}catch(Exception e){
					HibernateUtil.rollbackTransaction();
				}finally {
					HibernateUtil.closeSession();
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}finally {
			HibernateUtil.closeSession();
		}
	}
	public boolean isTaskAllowedToRun() {
		return true;
	}
}
