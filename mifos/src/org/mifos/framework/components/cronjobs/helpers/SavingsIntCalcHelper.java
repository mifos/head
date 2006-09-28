package org.mifos.framework.components.cronjobs.helpers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.cronjobs.MifosTask;
import org.mifos.framework.components.cronjobs.SchedulerConstants;
import org.mifos.framework.components.cronjobs.TaskHelper;
import org.mifos.framework.components.cronjobs.exceptions.CronJobException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class SavingsIntCalcHelper extends TaskHelper {

	public SavingsIntCalcHelper(MifosTask mifosTask) {
		super(mifosTask);
	}

	@Override
	public void execute(long timeInMillis) throws CronJobException {
		Calendar cal = Calendar.getInstance(Configuration.getInstance()
				.getSystemConfig().getMifosTimeZone());
		cal.setTimeInMillis(timeInMillis);
		List<String> errorList = new ArrayList<String>();
		SavingsPersistence persistence = new SavingsPersistence();
		List<Integer> accountList;
		try {
			accountList = persistence.retreiveAccountsPendingForIntCalc(cal
					.getTime());
		} catch (Exception e) {
			throw new CronJobException(e);
		}
		for (Integer accountId : accountList) {
			try {
				SavingsBO savings = persistence.findById(accountId);
				savings.updateInterestAccrued();
				HibernateUtil.commitTransaction();
			} catch (Exception e) {
				HibernateUtil.rollbackTransaction();
				errorList.add(accountId.toString());
			} finally {
				HibernateUtil.closeSession();
			}
		}
		if (errorList.size() > 0)
			throw new CronJobException(SchedulerConstants.FAILURE, errorList);
	}

	@Override
	public boolean isTaskAllowedToRun() {
		return true;
	}
}
