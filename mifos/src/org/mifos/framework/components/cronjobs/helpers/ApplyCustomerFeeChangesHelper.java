package org.mifos.framework.components.cronjobs.helpers;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.customer.business.CustomerAccountBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.fees.util.helpers.FeeChangeType;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.components.cronjobs.MifosTask;
import org.mifos.framework.components.cronjobs.SchedulerConstants;
import org.mifos.framework.components.cronjobs.TaskHelper;
import org.mifos.framework.components.cronjobs.exceptions.CronJobException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;

public class ApplyCustomerFeeChangesHelper extends TaskHelper {

	public ApplyCustomerFeeChangesHelper(MifosTask mifosTask) {
		super(mifosTask);
	}

	@Override
	public void execute(long timeInMillis) throws CronJobException {
		List<String> errorList = new ArrayList<String>();
		List<FeeBO> fees;
		try {
			fees = new FeePersistence().getUpdatedFeesForCustomer();
		} catch (Exception e) {
			throw new CronJobException(e);
		}
		AccountPersistence accountPersistence = new AccountPersistence();
		if (fees != null && fees.size() > 0) {
			for (FeeBO fee : fees) {
				try {
					if (!fee.getFeeChangeType().equals(
							FeeChangeType.NOT_UPDATED)) {
						List<Integer> accounts = new CustomerPersistence()
								.getCustomerAccountsForFee(fee.getFeeId());
						if (accounts != null && accounts.size() > 0) {
							for (Integer accountId : accounts) {
								updateAccountFee(accountId, fee,
										accountPersistence);
							}
						}
					}
					fee.updateFeeChangeType(FeeChangeType.NOT_UPDATED);
					UserContext userContext = new UserContext();
					userContext.setId(PersonnelConstants.SYSTEM_USER);
					fee.setUserContext(userContext);
					fee.save();
					HibernateUtil.commitTransaction();
				} catch (Exception e) {
					HibernateUtil.rollbackTransaction();
					errorList.add(fee.getFeeName());
				}
			}
		}
		if (errorList.size() > 0)
			throw new CronJobException(SchedulerConstants.FAILURE, errorList);
	}

	private void updateAccountFee(Integer accountId, FeeBO feesBO,
			AccountPersistence accountPersistence) throws CronJobException {
		try {
			CustomerAccountBO account = (CustomerAccountBO) accountPersistence
					.getAccount(accountId);
			account
					.updateFee(account.getAccountFees(feesBO.getFeeId()),
							feesBO);
		} catch (PersistenceException e) {
			throw new CronJobException(e);
		}
	}
}
