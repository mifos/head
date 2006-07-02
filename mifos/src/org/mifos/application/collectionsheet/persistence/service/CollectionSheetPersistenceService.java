/**

 * CollectionSheetPersistenceService.java    version: 1.0

 

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
package org.mifos.application.collectionsheet.persistence.service;

import java.sql.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.collectionsheet.business.CollectionSheetBO;
import org.mifos.application.collectionsheet.persistence.CollectionSheetPersistence;
import org.mifos.framework.persistence.service.PersistenceService;

/**
 * @author ashishsm
 *
 */
public class CollectionSheetPersistenceService extends PersistenceService{
	
	CollectionSheetPersistence collectionSheetPersistence = new CollectionSheetPersistence();
	
	
	

	/**
	 * @return - Returs a list of accountActionDate objects,where these objects are obtained by running a query.
	 * The query returns all rows where meeting date is the same as passed as parameter to the query
	 * and the status of the customer is either active or hold. Also 
	 * they should have atleast one active loan or Savings or Customer account
	 */
	public List<AccountActionDateEntity> getCustFromAccountActionsDate(Date date){
		
		return collectionSheetPersistence.getCustFromAccountActionsDate(date);
	}

	/**
	 * It gets list of account objects which are in the state 
	 * approved or disbursed to loan officer and have disbursal date same as the date passed.
	 * 
	 */
	public List<LoanBO> getLnAccntsWithDisbursalDate(Date date) {
		return collectionSheetPersistence.getLnAccntsWithDisbursalDate(date);
		
	}

	public void create(CollectionSheetBO collectionSheet) {
		collectionSheetPersistence.createOrUpdate(collectionSheet);
		
	}

	public void update(CollectionSheetBO collectionSheet) {
		collectionSheetPersistence.createOrUpdate(collectionSheet);
		
	}

	public LoanBO getLoanAccount(Integer accountId) {
		
		return collectionSheetPersistence.getLoanAccount(accountId);
	}

	public SavingsBO getSavingsAccount(Integer accountId) {
		
		return collectionSheetPersistence.getSavingsAccount(accountId);
		
	}
	
}
