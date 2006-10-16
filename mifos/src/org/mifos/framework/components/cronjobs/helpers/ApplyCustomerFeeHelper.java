package org.mifos.framework.components.cronjobs.helpers;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.customer.business.CustomerAccountBO;
import org.mifos.framework.components.cronjobs.MifosTask;
import org.mifos.framework.components.cronjobs.SchedulerConstants;
import org.mifos.framework.components.cronjobs.TaskHelper;
import org.mifos.framework.components.cronjobs.exceptions.CronJobException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class ApplyCustomerFeeHelper extends TaskHelper {

	public ApplyCustomerFeeHelper(MifosTask mifosTask) {
		super(mifosTask);
	}

	@Override
	public void execute(long timeInMills) throws CronJobException {
		List<String> errorList = new ArrayList<String>();
		List<Integer> accountIds;
		AccountPersistence accountPersistence = new AccountPersistence();
		try {
			accountIds = accountPersistence
					.getAccountsWithYesterdaysInstallment();
		} catch (Exception e) {
			throw new CronJobException(e);
		}
		for (Integer accountId : accountIds) {
			try {
				CustomerAccountBO customerAccountBO = (CustomerAccountBO) accountPersistence
						.getAccount(accountId);
				customerAccountBO.applyPeriodicFees();
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
