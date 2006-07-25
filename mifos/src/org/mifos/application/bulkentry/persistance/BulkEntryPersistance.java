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

import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.bulkentry.business.BulkEntryAccountActionView;
import org.mifos.application.bulkentry.business.BulkEntryAccountFeeActionView;
import org.mifos.framework.persistence.Persistence;

public class BulkEntryPersistance extends Persistence {

	public List<BulkEntryAccountActionView> getBulkEntryActionView(
			Date meetingDate, String searchString, Short officeId) {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("MEETING_DATE", meetingDate);
		queryParameters.put("PAYMENT_STATUS", AccountConstants.PAYMENT_UNPAID);
		queryParameters.put("SEARCH_STRING", searchString + '%');
		queryParameters.put("OFFICE_ID", officeId);
		List<BulkEntryAccountActionView> queryResult = executeNamedQuery(
				"account.getAllInstallmentsForAllAcounts", queryParameters);
		return queryResult;

	}

	public List<BulkEntryAccountFeeActionView> getBulkEntryFeeActionView(
			Date meetingDate, String searchString, Short officeId) {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("MEETING_DATE", meetingDate);
		queryParameters.put("PAYMENT_STATUS", AccountConstants.PAYMENT_UNPAID);
		queryParameters.put("SEARCH_STRING", searchString + '%');
		queryParameters.put("OFFICE_ID", officeId);
		List<BulkEntryAccountFeeActionView> queryResult = executeNamedQuery(
				"account.getAllAccountFeeForAllInstallmentsForAllAcounts",
				queryParameters);
		return queryResult;

	}

	public List<AccountFeesActionDetailEntity> getFeesActionDetails(
			Integer actionId) {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("ACCOUNT_ACTION_ID", actionId);
		List<AccountFeesActionDetailEntity> queryResult = executeNamedQuery(
				"acccount.getAccountFeeActionDetails", queryParameters);
		return queryResult;

	}
}
