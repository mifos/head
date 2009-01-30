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

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.bulkentry.business.CollectionSheetEntryAccountFeeActionView;
import org.mifos.application.bulkentry.business.CollectionSheetEntryClientAttendanceView;
import org.mifos.application.bulkentry.business.CollectionSheetEntryInstallmentView;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

public class BulkEntryPersistence extends Persistence {

	private static MifosLogger logger = 
		MifosLogManager.getLogger(LoggerConstants.BULKENTRYLOGGER);

	public List<CollectionSheetEntryInstallmentView> getBulkEntryActionView(
			Date meetingDate, String searchString, Short officeId,
			AccountTypes accountType) throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("MEETING_DATE", meetingDate);
		queryParameters.put("PAYMENT_STATUS", PaymentStatus.UNPAID.getValue());
		queryParameters.put("SEARCH_STRING", searchString + ".%");
		queryParameters.put("OFFICE_ID", officeId);
		if (accountType.equals(AccountTypes.LOAN_ACCOUNT)) {
			return executeNamedQuery(
					NamedQueryConstants.ALL_LOAN_SCHEDULE_DETAILS,
					queryParameters);
		} else if (accountType.equals(AccountTypes.SAVINGS_ACCOUNT)) {
			return executeNamedQuery(
					NamedQueryConstants.ALL_SAVINGS_SCHEDULE_DETAILS,
					queryParameters);
		} else if (accountType.equals(AccountTypes.CUSTOMER_ACCOUNT)) {
			List<CollectionSheetEntryInstallmentView> result = getBulkEntryActionViewForCustomerAccountWithSearchId(
					meetingDate, searchString, officeId);
			result.addAll(executeNamedQuery(
					NamedQueryConstants.ALL_CUSTOMER_SCHEDULE_DETAILS,
					queryParameters));
			return result;
		}
		return null;

	}

	public List<CollectionSheetEntryAccountFeeActionView> getBulkEntryFeeActionView(
			Date meetingDate, String searchString, Short officeId,
			AccountTypes accountType) throws PersistenceException {
		List<CollectionSheetEntryAccountFeeActionView> queryResult = null;
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("MEETING_DATE", meetingDate);
		queryParameters.put("PAYMENT_STATUS", PaymentStatus.UNPAID.getValue());
		queryParameters.put("SEARCH_STRING", searchString + ".%");
		queryParameters.put("OFFICE_ID", officeId);
		if (accountType.equals(AccountTypes.LOAN_ACCOUNT)) {
			queryResult = executeNamedQuery(
					NamedQueryConstants.ALL_LOAN_FEE_SCHEDULE_DETAILS,
					queryParameters);
		} else if (accountType.equals(AccountTypes.CUSTOMER_ACCOUNT)) {
			queryResult = getBulkEntryFeeActionViewForCustomerAccountWithSearchId(
					meetingDate, searchString, officeId);
			queryResult.addAll(executeNamedQuery(
					NamedQueryConstants.ALL_CUSTOMER_FEE_SCHEDULE_DETAILS,
					queryParameters));
		}
		initializeFees(queryResult);
		return queryResult;

	}

	public List<CollectionSheetEntryInstallmentView> getBulkEntryActionViewForCustomerAccountWithSearchId(
			Date meetingDate, String searchString, Short officeId) throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("MEETING_DATE", meetingDate);
		queryParameters.put("PAYMENT_STATUS", PaymentStatus.UNPAID.getValue());
		queryParameters.put("SEARCH_STRING", searchString);
		queryParameters.put("OFFICE_ID", officeId);
		return executeNamedQuery(NamedQueryConstants.CUSTOMER_SCHEDULE_DETAILS,
				queryParameters);

	}

	public List<CollectionSheetEntryAccountFeeActionView> getBulkEntryFeeActionViewForCustomerAccountWithSearchId(
			Date meetingDate, String searchString, Short officeId) throws PersistenceException {
		List<CollectionSheetEntryAccountFeeActionView> queryResult = null;
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("MEETING_DATE", meetingDate);
		queryParameters.put("PAYMENT_STATUS", PaymentStatus.UNPAID.getValue());
		queryParameters.put("SEARCH_STRING", searchString);
		queryParameters.put("OFFICE_ID", officeId);
		queryResult = executeNamedQuery(
				NamedQueryConstants.CUSTOMER_FEE_SCHEDULE_DETAILS,
				queryParameters);
		initializeFees(queryResult);
		return queryResult;

	}
    
    
    public List<CollectionSheetEntryClientAttendanceView> 
        getBulkEntryClientAttendanceActionView(Date meetingDate, Short officeId) 
    throws PersistenceException {
        
        logger.debug("persistence "+ meetingDate);
        logger.debug("persistence "+ officeId);
        
        List<CollectionSheetEntryClientAttendanceView> queryResult = null;
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("MEETING_DATE", meetingDate);
        queryParameters.put("OFFICE_ID", officeId);
        
       queryResult = executeNamedQuery(
                    "Customer.getAttedanceForOffice",
                    queryParameters);
        
        
        return queryResult;

    }

	private void initializeFees(
			List<CollectionSheetEntryAccountFeeActionView> actionViewList) {
		for (CollectionSheetEntryAccountFeeActionView actionView : actionViewList) {
			initialize(actionView.getFee());
		}
	}

}
