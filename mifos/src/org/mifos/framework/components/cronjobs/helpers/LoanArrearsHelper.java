/**

 * LoanArrearsHelper.java    version: 1.0

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */
package org.mifos.framework.components.cronjobs.helpers;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.persistance.LoanPersistance;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.productdefinition.persistence.LoanPrdPersistence;
import org.mifos.framework.components.cronjobs.MifosTask;
import org.mifos.framework.components.cronjobs.SchedulerConstants;
import org.mifos.framework.components.cronjobs.TaskHelper;
import org.mifos.framework.components.cronjobs.exceptions.CronJobException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class LoanArrearsHelper extends TaskHelper {

	public LoanArrearsHelper(MifosTask mifosTask) {
		super(mifosTask);
	}

	@Override
	public void execute(long timeInMillis) throws CronJobException {
		AccountPersistence accountPersistence = new AccountPersistence();
		List<String> errorList = new ArrayList<String>();
		List<Integer> listAccountIds = null;
		try {
			Short latenessDays = new LoanPrdPersistence()
					.retrieveLatenessForPrd();
			listAccountIds = new LoanPersistance()
					.getLoanAccountsInArrears(latenessDays);
		} catch (Exception e) {
			throw new CronJobException(e);
		}
		for (Integer accountId : listAccountIds) {
			try {
				LoanBO loanBO = (LoanBO) accountPersistence
						.getAccount(accountId);
				loanBO.handleArrears();
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

	@Override
	public boolean isTaskAllowedToRun() {
		return true;
	}

}
