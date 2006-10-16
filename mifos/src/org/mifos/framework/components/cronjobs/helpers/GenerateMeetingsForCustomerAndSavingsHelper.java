package org.mifos.framework.components.cronjobs.helpers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.customer.business.CustomerAccountBO;
import org.mifos.framework.components.cronjobs.MifosTask;
import org.mifos.framework.components.cronjobs.SchedulerConstants;
import org.mifos.framework.components.cronjobs.TaskHelper;
import org.mifos.framework.components.cronjobs.exceptions.CronJobException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;

public class GenerateMeetingsForCustomerAndSavingsHelper extends TaskHelper {

	public GenerateMeetingsForCustomerAndSavingsHelper(MifosTask mifosTask) {
		super(mifosTask);
	}

	@Override
	public void execute(long timeInMillis) throws CronJobException {
		AccountPersistence accountPersistence = new AccountPersistence();
		List<Integer> customerAccountIds;
		try {
			customerAccountIds = accountPersistence
					.getActiveCustomerAndSavingsAccounts();
		} catch (PersistenceException e) {
			throw new CronJobException(e);
		}
		List<String> errorList = new ArrayList<String>();
		for (Integer accountId : customerAccountIds) {
			try {
				HibernateUtil.getSessionTL();
				HibernateUtil.startTransaction();
				AccountBO accountBO = accountPersistence.getAccount(accountId);
				if (isScheduleToBeGenerated(accountBO.getLastInstallmentId(),
						accountBO.getDetailsOfNextInstallment())) {
					if (accountBO instanceof CustomerAccountBO)
						((CustomerAccountBO) accountBO)
								.generateNextSetOfMeetingDates();
					else if (accountBO instanceof SavingsBO)
						((SavingsBO) accountBO).generateNextSetOfMeetingDates();
				}
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

	private boolean isScheduleToBeGenerated(int installmentSize,
			AccountActionDateEntity nextInstallment) {
		Date currentDate = DateUtils.getCurrentDateWithoutTimeStamp();
		short nextInstallmentId = (short) installmentSize;
		if (nextInstallment != null) {
			if (nextInstallment.getActionDate().compareTo(currentDate) == 0) {
				nextInstallmentId = (short) (nextInstallment.getInstallmentId()
						.intValue() + 1);
			} else {
				nextInstallmentId = (short) (nextInstallment.getInstallmentId()
						.intValue());
			}
		}
		int totalInstallmentDatesToBeChanged = installmentSize
				- nextInstallmentId + 1;
		return totalInstallmentDatesToBeChanged <= 5;
	}
}
