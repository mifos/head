/**

 * CollectionSheetPersistence.java    version: 1.0

 

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
package org.mifos.application.collectionsheet.persistence;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.HibernateException;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetConstants;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;

/**
 * @author ashishsm
 *
 */
public class CollectionSheetPersistence extends Persistence{

	
	public CollectionSheetPersistence() {
		super();
		
	}
	
		
	/**
	 * @return - Returs a list of accountActionDate objects,where there objects are obtained by running a query.
	 * The query returns all rows where meeting date is the same as passed as parameter to the query
	 * and the status of the customer is either active or hold. Also 
	 * they should have atleast one active loan or Savings or Customer account
	 */
	public List<AccountActionDateEntity> getCustFromAccountActionsDate(Date date){
		List<AccountActionDateEntity> accountActionDate = null;
		HashMap queryParameters = new HashMap();
		queryParameters.put(CollectionSheetConstants.MEETING_DATE, date);
		accountActionDate = executeNamedQuery(NamedQueryConstants.CUSTOMERS_WITH_SPECIFIED_MEETING_DATE,queryParameters);
		return accountActionDate;
	}
	
	/**
	 * It gets list of account objects which are in the state 
	 * approved or disbursed to loan officer and have disbursal date same as the date passed.
	 * It retrieves the list using an named HQL query.
	 */
	public List<LoanBO> getLnAccntsWithDisbursalDate(Date date) {
		
		List<LoanBO> loans = null;
		HashMap queryParameters = new HashMap();
		queryParameters.put(CollectionSheetConstants.MEETING_DATE, date);
		try {
			
			loans = executeNamedQuery(NamedQueryConstants.CUSTOMERS_WITH_SPECIFIED_DISBURSAL_DATE,queryParameters);
			
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return loans;
	}


	public LoanBO getLoanAccount(Integer accountId) {
		
		return (LoanBO)HibernateUtil.getSessionTL().get(LoanBO.class, accountId);
	}


	public SavingsBO getSavingsAccount(Integer accountId) {
		return (SavingsBO)HibernateUtil.getSessionTL().get(SavingsBO.class, accountId);
	}	

}
