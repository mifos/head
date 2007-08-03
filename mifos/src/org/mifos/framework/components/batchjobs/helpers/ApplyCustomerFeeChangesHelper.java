package org.mifos.framework.components.batchjobs.helpers;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.customer.business.CustomerAccountBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.fees.util.helpers.FeeChangeType;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;

public class ApplyCustomerFeeChangesHelper extends TaskHelper {

	public ApplyCustomerFeeChangesHelper(MifosTask mifosTask) {
		super(mifosTask);
	}

	@Override
	public void execute(long timeInMillis) throws BatchJobException {
		List<String> errorList = new ArrayList<String>();
		List<FeeBO> fees;
		try {
			fees = new FeePersistence().getUpdatedFeesForCustomer();
		} catch (Exception e) {
			throw new BatchJobException(e);
		}
		if (fees != null && fees.size() > 0) {
			for (FeeBO fee : fees) {
				try {
					if (!fee.getFeeChangeType().equals(
							FeeChangeType.NOT_UPDATED)) {
						List<AccountBO> accounts = new CustomerPersistence()
								.getCustomerAccountsForFee(fee.getFeeId());
						if (accounts != null && accounts.size() > 0) {
							for (AccountBO account : accounts) {
								updateAccountFee(account, fee);
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
			throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
	}

	private void updateAccountFee(AccountBO account, FeeBO feesBO)
			throws BatchJobException {
		CustomerAccountBO customerAccount = (CustomerAccountBO) account;
		customerAccount.updateFee(
				account.getAccountFees(feesBO.getFeeId()), feesBO);
	}
	
	@Override
	public boolean isTaskAllowedToRun() {
		return true;
	}
}
