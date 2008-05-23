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

public class LoanArrearsHelper extends TaskHelper {

	public LoanArrearsHelper(MifosTask mifosTask) {
		super(mifosTask);
	}

	@Override
	public void execute(long timeInMillis) throws BatchJobException {
		AccountPersistence accountPersistence = new AccountPersistence();
		List<String> errorList = new ArrayList<String>();
		List<Integer> listAccountIds = null;
		try {
			Short latenessDays = new LoanPrdPersistence()
					.retrieveLatenessForPrd();
			listAccountIds = new LoanPersistence()
					.getLoanAccountsInArrearsInGoodStanding(latenessDays);
		} catch (Exception e) {
			throw new BatchJobException(e);
		}
		for (Integer accountId : listAccountIds) {
			try {
				LoanBO loanBO = (LoanBO) accountPersistence
						.getAccount(accountId);
				if (loanBO.getAccountState().equals(
						AccountState.LOAN_PENDING_APPROVAL)){
					throw new Exception("Loan was pending approval");
				}

				if(loanBO.getAccountState().isInState(AccountState.LOAN_APPROVED)){
					//Do nothing
					//"LOAN_APPROVED" should not move to "LOAN ACTIVE IN BAD STANDING"
					//fixes issue #1848
				} else {
					loanBO.handleArrears();
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
			throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
	}

	@Override
	public boolean isTaskAllowedToRun() {
		return true;
	}

}
