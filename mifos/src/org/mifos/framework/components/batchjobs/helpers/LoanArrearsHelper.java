/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
package org.mifos.framework.components.batchjobs.helpers;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.productdefinition.persistence.LoanPrdPersistence;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.config.GeneralConfig;

public class LoanArrearsHelper extends TaskHelper {
	

	public LoanArrearsHelper(MifosTask mifosTask) {
		super(mifosTask);
	}

	@Override
	public void execute(long timeInMillis) throws BatchJobException {
		long time1 = System.currentTimeMillis();
		System.out.println("LoanArrearsTask starts");
		AccountPersistence accountPersistence = new AccountPersistence();
		List<String> errorList = new ArrayList<String>();
		List<Integer> listAccountIds = null;
		int accountNumber = 0;
		try {
			Short latenessDays = new LoanPrdPersistence()
					.retrieveLatenessForPrd();
			long time3 = System.currentTimeMillis();
			listAccountIds = new LoanPersistence()
					.getLoanAccountsInArrearsInGoodStanding(latenessDays);
			long duration2 = System.currentTimeMillis() - time3;
			accountNumber = listAccountIds.size();
			System.out.println("LoanArrearsTask: getLoanAccountsInArrearsInGoodStanding ran in " + 
					duration2 + " milliseconds" + " got " + accountNumber + " accounts to update.");
		} catch (Exception e) {
			throw new BatchJobException(e);
		}
		LoanBO loanBO = null;
		int i=1;
		int batchSize = GeneralConfig.getBatchSizeForBatchJobs();
		int recordCommittingSize = GeneralConfig.getRecordCommittingSizeForBatchJobs();
		
		try
		{
			long startTime = System.currentTimeMillis();
			for (Integer accountId : listAccountIds) {
					loanBO = (LoanBO) accountPersistence
							.getAccount(accountId);
					assert (loanBO.getAccountState().getId().shortValue() ==
						AccountState.LOAN_ACTIVE_IN_GOOD_STANDING.getValue().shortValue());
					
						
					loanBO.handleArrears();
					if (i % batchSize == 0)
					{
						HibernateUtil.flushAndClearSession();
					}
					if (i % recordCommittingSize == 0)
					{
						HibernateUtil.commitTransaction();
					}
					if (i % 1000 == 0)
					{
						long time = System.currentTimeMillis();
						System.out.println("1000 accounts updated in " + (time - startTime) + 
								" milliseconds. There are " + (accountNumber -i) + " more accounts to be updated.");
						startTime = time;
						
					}
					i++;	
					
				
				}
				HibernateUtil.commitTransaction();
				
				
			} catch (Exception e) {
				getLogger().debug("LoanArrearsTask " + e.getMessage());
				HibernateUtil.rollbackTransaction();
				if (loanBO != null)
				{
					errorList.add(loanBO.getAccountId().toString());
				}
			} finally {
				
				HibernateUtil.closeSession();
			}
		long time2 = System.currentTimeMillis();
		long duration = time2 - time1;
		getLogger().info("Time to run LoanArrearsTask " + duration);
		System.out.println("LoanArrearsTask ran in " + duration + " milliseconds");
		if (errorList.size() > 0)
			throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
		getLogger().debug("LoanArrearsTask ran successfully");
		System.out.println("LoanArrearsTask ran successfully");
	}

	@Override
	public boolean isTaskAllowedToRun() {
		return true;
	}

}
