package org.mifos.framework.components.cronjobs.helpers;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.CustomerAccountBO;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.framework.components.cronjobs.TaskHelper;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;

public class GenerateMeetingsForCustomerAndSavingsHelper extends TaskHelper {

	@Override
	public void execute(long timeInMillis) {
		try {
			if( !isYearEnd(timeInMillis)) return ;
			AccountPersistence accountPersistence = new AccountPersistence();
			List<AccountBO> customerAccounts = accountPersistence
					.getActiveCustomerAndSavingsAccounts();
			for (AccountBO accountBO : customerAccounts) {
				HibernateUtil.startTransaction();
				if (accountBO instanceof CustomerAccountBO)
					 ((CustomerAccountBO)accountBO).generateMeetingsForNextYear();
				else if (accountBO instanceof SavingsBO)
					((SavingsBO)accountBO).generateMeetingsForNextYear();

				HibernateUtil.commitTransaction();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private boolean isYearEnd( long timeInMillis){
		
		Calendar currentDate =Calendar.getInstance();
		currentDate.setTimeInMillis(timeInMillis);
		Calendar lastDayOfyear = Calendar.getInstance();
		lastDayOfyear.setTime(DateUtils.getLastDayOfYear());
		
		 return new GregorianCalendar(
				 currentDate.get(Calendar.YEAR),currentDate.get(Calendar.MONTH),
				 currentDate.get(Calendar.DATE),0,0,0).compareTo(
			   	new GregorianCalendar(
			   	lastDayOfyear.get(Calendar.YEAR),lastDayOfyear.get(Calendar.MONTH),
				lastDayOfyear.get(Calendar.DATE),0,0,0))
				==0 ?true:false;
	}
}
