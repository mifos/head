/**

 * CollectionSheetPersistence.java    version: 1.0

 

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
package org.mifos.application.collectionsheet.persistence;

import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetConstants;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

public class CollectionSheetPersistence extends Persistence {
	
	public CollectionSheetPersistence() {
		super();
	}
		
	/**
	 * The query returns all rows where meeting date is the same as 
	 * the date parameter
	 * and the status of the customer is either active or hold. Also 
	 * they should have at least one active loan or Savings or Customer account
	 */
	public List<AccountActionDateEntity> getCustFromAccountActionsDate(Date date) 
	throws PersistenceException {
		Map<String, Object> queryParameters = Collections.singletonMap(
			CollectionSheetConstants.MEETING_DATE, (Object)date);
		List<AccountActionDateEntity> accountActionDate = 
			executeNamedQuery(
				NamedQueryConstants.CUSTOMERS_WITH_SPECIFIED_MEETING_DATE,
				queryParameters);
		accountActionDate.addAll(executeNamedQuery(
			"CollectionSheetCustomer.loansWithSpecifiedMeetingDate",
			queryParameters));
		accountActionDate.addAll(executeNamedQuery(
			"CollectionSheetCustomer.savingssWithSpecifiedMeetingDate",
			queryParameters));
		return accountActionDate;
	}
	
	/**
	 * Get list of account objects which are in the state 
	 * approved or disbursed to loan officer and have disbursal date 
	 * same as the date passed.
	 */
	public List<LoanBO> getLnAccntsWithDisbursalDate(Date date) 
	throws PersistenceException {
		return executeNamedQuery(
			NamedQueryConstants.CUSTOMERS_WITH_SPECIFIED_DISBURSAL_DATE,
			Collections.singletonMap(CollectionSheetConstants.MEETING_DATE, date));
	}
}
