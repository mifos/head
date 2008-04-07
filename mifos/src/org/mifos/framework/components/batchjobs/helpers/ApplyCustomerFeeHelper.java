package org.mifos.framework.components.batchjobs.helpers;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.customer.business.CustomerAccountBO;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class ApplyCustomerFeeHelper extends TaskHelper {

	public ApplyCustomerFeeHelper(MifosTask mifosTask) {
		super(mifosTask);
	}

	@Override
	public void execute(long timeInMills) throws BatchJobException {
		List<String> errorList = new ArrayList<String>();
		List<Integer> accountIds;
		AccountPersistence accountPersistence = new AccountPersistence();
		try {
			accountIds = accountPersistence
					.getAccountsWithYesterdaysInstallment();
		} catch (Exception e) {
			throw new BatchJobException(e);
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
			throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
	}

	@Override
	public boolean isTaskAllowedToRun() {
		return true;
	}

}
