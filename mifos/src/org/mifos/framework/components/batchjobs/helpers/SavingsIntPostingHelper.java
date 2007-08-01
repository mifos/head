package org.mifos.framework.components.batchjobs.helpers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class SavingsIntPostingHelper extends TaskHelper {

	public SavingsIntPostingHelper(MifosTask mifosTask) {
		super(mifosTask);
	}

	@Override
	public void execute(long timeInMillis) throws BatchJobException {
		Calendar cal = Calendar.getInstance(Configuration.getInstance()
				.getSystemConfig().getMifosTimeZone());
		cal.setTimeInMillis(timeInMillis);
		List<String> errorList = new ArrayList<String>();
		SavingsPersistence persistence = new SavingsPersistence();
		List<Integer> accountList;
		try {
			accountList = persistence.retreiveAccountsPendingForIntPosting(cal
					.getTime());
		} catch (Exception e) {
			throw new BatchJobException(e);
		}
		for (Integer accountId : accountList) {
			try {
				SavingsBO savings = persistence.findById(accountId);
				savings.postInterest();
				HibernateUtil.commitTransaction();
			} catch (Exception e) {
				HibernateUtil.rollbackTransaction();
				errorList.add(accountId.toString());
			} finally {
				HibernateUtil.closeSession();
			}
		}
		if (errorList.size() > 0)
			throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
	}
}
