/**

 * LoanArrearsHelper.java    version: 1.0

 

 * Copyright © 2005-2006 Grameen Foundation USA

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


import java.util.List;

import org.hibernate.HibernateException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.persistance.LoanPersistance;
import org.mifos.application.productdefinition.persistence.LoansPrdPersistence;
import org.mifos.framework.components.cronjobs.TaskHelper;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class LoanArrearsHelper extends TaskHelper{

	@Override
	public void execute(long timeInMillis) {
		// TODO Auto-generated method stub
		
		Short latenessDays = null;
		LoanPersistance loanPersistence = new LoanPersistance();
		LoansPrdPersistence loansPrdPersistence = new LoansPrdPersistence();

		try {
			
			latenessDays = loansPrdPersistence.retrieveLatenessForPrd();

			List<LoanBO> listAccounts = loanPersistence
					.getLoanAccountsInArrears(latenessDays);
			

			for (LoanBO loanBO : listAccounts) {
				try {
					loanBO.handleArrears();
					HibernateUtil.commitTransaction();
				} catch (HibernateException ex) {
					HibernateUtil.getTransaction().rollback();
					MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER)
							.error(
									"Loan Account Arrears: Changes to Glob_Act_Num "
											+ loanBO.getGlobalAccountNum()
											+ "could not be committed.");
				}finally{
					HibernateUtil.closeSession();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			try {
				HibernateUtil.closeSession();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public boolean isTaskAllowedToRun() {
		return true;
	}




}
