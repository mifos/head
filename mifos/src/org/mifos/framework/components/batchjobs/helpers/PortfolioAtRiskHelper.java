package org.mifos.framework.components.batchjobs.helpers;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class PortfolioAtRiskHelper extends TaskHelper {

	public PortfolioAtRiskHelper(MifosTask mifosTask) {
		super(mifosTask);
	}

	@Override
	public void execute(long timeInMillis) throws BatchJobException {
		List<Integer> customerIds;
		List<String> errorList = new ArrayList<String>();
		CustomerPersistence customerPersistence = new CustomerPersistence();
		try {
			customerIds = new CustomerPersistence()
					.getCustomers(CustomerLevel.GROUP.getValue());
		} catch (Exception e) {
			throw new BatchJobException(e);
		}
		if (customerIds != null && !customerIds.isEmpty())
			for (Integer customerId : customerIds) {
				try {
					GroupBO group = (GroupBO) customerPersistence
							.getCustomer(customerId);
					group.getPerformanceHistory().generatePortfolioAtRisk();
					group.update();
					HibernateUtil.commitTransaction();
				} catch (Exception e) {
					HibernateUtil.rollbackTransaction();
					errorList.add(customerId.toString());
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
