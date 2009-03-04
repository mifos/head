package org.mifos.framework.components.batchjobs.helpers;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerPerformanceHistory;
import org.mifos.application.customer.group.BasicGroupInfo;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryEntity;
import org.mifos.application.customer.group.persistence.GroupPersistence;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.components.batchjobs.persistence.TaskPersistence;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Money;

public class PortfolioAtRiskHelper extends TaskHelper {

	public PortfolioAtRiskHelper(MifosTask mifosTask) {
		super(mifosTask);
	}

	
	@Override
	public void execute(long timeInMillis) throws BatchJobException {

		System.out.println("PortfolioAtRiskTask starts");
		long time1 = new DateTimeService().getCurrentDateTime().getMillis();
		List<BasicGroupInfo> groupInfos = null;
		List<String> errorList = new ArrayList<String>();
		
		try {
			groupInfos = new CustomerPersistence().getAllBasicGroupInfo();
			
		} catch (Exception e) {
			throw new BatchJobException(e);
		}
		
		if (groupInfos != null && !groupInfos.isEmpty())
		{
			
			int groupCount = groupInfos.size();
			System.out.println("PortfolioAtRisk: got " + groupCount + " groups to process.");
			long startTime = new DateTimeService().getCurrentDateTime().getMillis();
			int i=1;
			Integer groupId = null;
			GroupPersistence groupPersistence = new GroupPersistence();
			try
			{
				for (BasicGroupInfo groupInfo : groupInfos) {
					    groupId = groupInfo.getGroupId();
					    String searchStr = groupInfo.getSearchId() + ".%";
						double portfolioAtRisk = 
							PortfolioAtRiskCalculation.generatePortfolioAtRiskForTask(groupId, groupInfo.getBranchId(), searchStr);
						//System.out.println("Group " + groupInfo.getGroupName() + " groupId " + groupId + " branchId " + groupInfo.getBranchId() 
						//		+ " searchID "+ groupInfo.getSearchId() + " portfolio " + portfolioAtRisk);
								
						// update group perf history and group table for the field updated_by and updated_date
						if (portfolioAtRisk > -1)
						{
							groupPersistence.updateGroupInfoAndGroupPerformanceHistoryForPortfolioAtRisk(portfolioAtRisk, groupId);
						}
						if (i % 500 == 0)
						{
							long time = new DateTimeService().getCurrentDateTime().getMillis();
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
					if (groupId != null)
						errorList.add(groupId.toString());
				} finally {
					HibernateUtil.closeSession();
				}
			}
		
		long time2 = new DateTimeService().getCurrentDateTime().getMillis();
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
