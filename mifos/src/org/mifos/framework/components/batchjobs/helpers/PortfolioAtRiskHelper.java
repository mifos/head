package org.mifos.framework.components.batchjobs.helpers;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.customer.business.CustomerPerformanceHistory;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryEntity;
import org.mifos.application.customer.group.persistence.GroupPersistence;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.config.GeneralConfig;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.components.batchjobs.persistence.TaskPersistence;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class PortfolioAtRiskHelper extends TaskHelper {

	public PortfolioAtRiskHelper(MifosTask mifosTask) {
		super(mifosTask);
	}

	@Override
	public void execute(long timeInMillis) throws BatchJobException {
		TaskPersistence p = new TaskPersistence();
		try
		{
			if (p.hasLoanArrearsTaskRunSuccessfully() == false)
			{
				String message = "PortfolioAtRisk Task can't run because it requires the LoanArrearsTask to run successfully first.";
				MifosLogManager.getLogger(LoggerConstants.BATCH_JOBS).error(message);
				System.out.println(message);
				return;
			}
		}
		catch (PersistenceException ex)
		{
			throw new  BatchJobException(ex);
		}
		System.out.println("PortfolioAtRiskTask starts");
		long time1 = System.currentTimeMillis();
		List<Integer> customerIds = null;
		List<String> errorList = new ArrayList<String>();
		
		try {
			customerIds = new CustomerPersistence().getCustomers(CustomerLevel.GROUP.getValue());
			
		} catch (Exception e) {
			throw new BatchJobException(e);
		}
		
		if (customerIds != null && !customerIds.isEmpty())
		{
			
			int groupCount = customerIds.size();
			System.out.println("PortfolioAtRisk: got " + groupCount + " groups to process.");
			long startTime = System.currentTimeMillis();
			int i=1;
			Integer groupId = null;
			GroupPersistence groupPersistence = new GroupPersistence();
			try
			{
				for (Integer customerId : customerIds) {
					    groupId = customerId;
						GroupBO group = groupPersistence.getGroup(customerId);
						GroupPerformanceHistoryEntity groupPerf = group.getGroupPerformanceHistory();
						// TODO: HACK done because sometimes the customer performance history for GroupBO is sometimes null???
						if (null == groupPerf) {						
							groupPerf = (GroupPerformanceHistoryEntity) HibernateUtil.getSessionTL().createQuery("from org.mifos.application.customer.group.business.GroupPerformanceHistoryEntity e where e.group.customerId = " + group.getCustomerId()).uniqueResult();
							group.setGroupPerformanceHistory(groupPerf);
						}
						group.getGroupPerformanceHistory().generatePortfolioAtRiskForTask();
						group.update();
						HibernateUtil.commitTransaction();
						if (i % 500 == 0)
						{
							long time = System.currentTimeMillis();
							System.out.println("500 groups updated in " + (time - startTime) + 
									" milliseconds. There are " + (groupCount -i) + " more groups to be updated.");
							startTime = time;
							
						}
						i++;
				}
						
				
				
			} catch (Exception e) {
					e.printStackTrace();
					MifosLogManager
					.getLogger(LoggerConstants.BATCH_JOBS)
					.error("PortfolioAtRiskHelper execute failed with exception " + e.getClass().getName() + ": " + 
							e.getMessage() + " at group " + groupId.toString());
					
					HibernateUtil.rollbackTransaction();
					errorList.add(groupId.toString());
				} finally {
					HibernateUtil.closeSession();
				}
			}
		
		long time2 = System.currentTimeMillis();
		System.out.println("PortfolioAtRiskTask ran in " + (time2 - time1) + " milliseconds");

		if (errorList.size() > 0)
			throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
		System.out.println("PortfolioAtRiskTask ran successfully");
	}

	@Override
	public boolean isTaskAllowedToRun() {
		return true;
	}

}
