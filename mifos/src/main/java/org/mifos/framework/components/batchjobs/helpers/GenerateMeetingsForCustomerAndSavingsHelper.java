package org.mifos.framework.components.batchjobs.helpers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.customer.business.CustomerAccountBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.config.GeneralConfig;

public class GenerateMeetingsForCustomerAndSavingsHelper extends TaskHelper {

	public GenerateMeetingsForCustomerAndSavingsHelper(MifosTask mifosTask) {
		super(mifosTask);
	}

	@Override
	public void execute(long timeInMillis) throws BatchJobException {
		System.out.println("GenerateMeetingsForCustomerAndSavings starts ");
		long taskStartTime = System.currentTimeMillis();
		AccountPersistence accountPersistence = new AccountPersistence();
		List<Integer> customerAccountIds;
		int accountCount = 0;
		
		try {
			long time1 = System.currentTimeMillis();
			customerAccountIds = accountPersistence
					.getActiveCustomerAndSavingsAccounts();
			accountCount = customerAccountIds.size();
			long duration = System.currentTimeMillis() - time1;
			System.out.println("Time to execute the query " + duration + " . Got " + accountCount + " accounts.");
			if (accountCount == 0)
			{
				return;
			}
		} catch (PersistenceException e) {
			throw new BatchJobException(e);
		}
		
		List<String> errorList = new ArrayList<String>();
		int i = 0;
		int batchSize = GeneralConfig.getBatchSizeForBatchJobs();
		int recordCommittingSize = GeneralConfig.getRecordCommittingSizeForBatchJobs();
		long startTime = System.currentTimeMillis();
		Integer currentAccountId = null;
		int updatedRecordCount = 0;
		try 
		{
			HibernateUtil.getSessionTL();
			HibernateUtil.startTransaction();
			for (Integer accountId : customerAccountIds) 
			{
				i++;
				currentAccountId = accountId;
				AccountBO accountBO = accountPersistence.getAccount(accountId);
				if (isScheduleToBeGenerated(accountBO.getLastInstallmentId(),
						accountBO.getDetailsOfNextInstallment())) 
				{
					if (accountBO instanceof CustomerAccountBO) {
						((CustomerAccountBO) accountBO)
								.generateNextSetOfMeetingDates();
						updatedRecordCount++;
					}
					else if (accountBO instanceof SavingsBO) {
						((SavingsBO) accountBO).generateNextSetOfMeetingDates(new CustomerPersistence());
						updatedRecordCount++;
					}
				}
				if (updatedRecordCount > 0)
				{
					if (updatedRecordCount % batchSize == 0)
					{
						HibernateUtil.flushAndClearSession();
					}
					if (updatedRecordCount % recordCommittingSize == 0)
					{
						HibernateUtil.commitTransaction();
						HibernateUtil.getSessionTL();
						HibernateUtil.startTransaction();
					}
					if (updatedRecordCount % 1000 == 0)
					{
						long time = System.currentTimeMillis();
						System.out.println("out of " + i + " accounts processed, " + updatedRecordCount +
								" accounts updated. The last 1000 updated in " + (time - startTime) + 
								" milliseconds. There are " + (accountCount -i) + " more accounts to be processed.");
						startTime = time;
						
					}
				}
				
				
			}
			HibernateUtil.commitTransaction();
			
				
		} catch (Exception e) 
			{
			    System.out.println("account " + currentAccountId.intValue() + " exception " + e.getMessage());
				getLogger().debug("GenerateMeetingsForCustomerAndSavingsHelper " + e.getMessage());
				HibernateUtil.rollbackTransaction();
				if (currentAccountId != null)
				{
					errorList.add(currentAccountId.toString());
				}
				errorList.add(currentAccountId.toString());
				getLogger().error(
					"Unable to generate schedules for account with ID " + 
					currentAccountId, false, null, e);
			} finally {
				HibernateUtil.closeSession();
			}
		
		if (errorList.size() > 0)
			throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
		System.out.println("GenerateMeetingsForCustomerAndSavings ends successfully. Ran in " +  (System.currentTimeMillis()-taskStartTime));
		
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
	
	@Override
	public boolean isTaskAllowedToRun() {
		return true;
	}

}
