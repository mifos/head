package org.mifos.framework.components.cronjobs.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.components.cronjobs.MifosTask;
import org.mifos.framework.components.cronjobs.SchedulerConstants;
import org.mifos.framework.components.cronjobs.TaskHelper;
import org.mifos.framework.components.cronjobs.exceptions.CronJobException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class RegenerateScheduleHelper extends TaskHelper {

	public RegenerateScheduleHelper(MifosTask mifosTask) {
		super(mifosTask);
	}

	List<Integer> accountList;

	@Override
	public void execute(long timeInMills) throws CronJobException {
		List<String> errorList = new ArrayList<String>();
		accountList = new ArrayList<Integer>();
		List<Integer> customerIds;
		try {
			customerIds = new CustomerPersistence()
					.getCustomersWithUpdatedMeetings();
		} catch (Exception e) {
			throw new CronJobException(e);
		}
		if (customerIds != null && !customerIds.isEmpty())
			for (Integer customerId : customerIds) {
				try {
					handleChangeInMeetingSchedule(customerId);
					HibernateUtil.commitTransaction();
				} catch (Exception e) {
					HibernateUtil.rollbackTransaction();
					errorList.add(customerId.toString());
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

	private void handleChangeInMeetingSchedule(Integer customerId)
			throws Exception {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		CustomerBO customer = customerPersistence.getCustomer(customerId);
		Set<AccountBO> accounts = customer.getAccounts();
		if (accounts != null && !accounts.isEmpty())
			for (AccountBO account : accounts) {
				if (!accountList.contains(account.getAccountId())) {
					account.handleChangeInMeetingSchedule();
					accountList.add(account.getAccountId());
				}
			}
		List<Integer> customerIds = customerPersistence.getChildrenForParent(
				customer.getSearchId(), customer.getOffice().getOfficeId());
		if (customerIds != null && !customerIds.isEmpty()) {
			for (Integer childCustomerId : customerIds) {
				handleChangeInMeetingSchedule(childCustomerId);
			}
		}
	}

}
