/**

 * BulkEntryPersistance.java    version: 1.0

 

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

package org.mifos.application.bulkentry.persistance;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Hibernate;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.bulkentry.business.BulkEntryAccountFeeActionView;
import org.mifos.application.bulkentry.business.BulkEntryInstallmentView;
import org.mifos.framework.persistence.Persistence;

public class BulkEntryPersistance extends Persistence {

	public List<BulkEntryInstallmentView> getBulkEntryActionView(
			Date meetingDate, String searchString, Short officeId,
			AccountTypes accountType) {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("MEETING_DATE", meetingDate);
		queryParameters.put("PAYMENT_STATUS", PaymentStatus.UNPAID.getValue());
		queryParameters.put("SEARCH_STRING", searchString + '%');
		queryParameters.put("OFFICE_ID", officeId);
		if (accountType.equals(AccountTypes.LOANACCOUNT)) {
			return executeNamedQuery(
					NamedQueryConstants.ALL_LOAN_SCHEDULE_DETAILS,
					queryParameters);
		} else if (accountType.equals(AccountTypes.SAVINGSACCOUNT)) {
			return executeNamedQuery(
					NamedQueryConstants.ALL_SAVINGS_SCHEDULE_DETAILS,
					queryParameters);
		} else if (accountType.equals(AccountTypes.CUSTOMERACCOUNT)) {
			return executeNamedQuery(
					NamedQueryConstants.ALL_CUSTOMER_SCHEDULE_DETAILS,
					queryParameters);
		}
		return null;

	}

	public List<BulkEntryAccountFeeActionView> getBulkEntryFeeActionView(
			Date meetingDate, String searchString, Short officeId,
			AccountTypes accountType) {
		List<BulkEntryAccountFeeActionView> queryResult = null;
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("MEETING_DATE", meetingDate);
		queryParameters.put("PAYMENT_STATUS", PaymentStatus.UNPAID.getValue());
		queryParameters.put("SEARCH_STRING", searchString + '%');
		queryParameters.put("OFFICE_ID", officeId);
		if (accountType.equals(AccountTypes.LOANACCOUNT)) {
			queryResult = executeNamedQuery(
					NamedQueryConstants.ALL_LOAN_FEE_SCHEDULE_DETAILS,
					queryParameters);
		} else if (accountType.equals(AccountTypes.CUSTOMERACCOUNT)) {
			queryResult = executeNamedQuery(
					NamedQueryConstants.ALL_CUSTOMER_FEE_SCHEDULE_DETAILS,
					queryParameters);
		}
		initializeFees(queryResult);
		return queryResult;

	}

	private void initializeFees(
			List<BulkEntryAccountFeeActionView> actionViewList) {
		for (BulkEntryAccountFeeActionView actionView : actionViewList) {
			Hibernate.initialize(actionView.getFee());
		}
	}

}
