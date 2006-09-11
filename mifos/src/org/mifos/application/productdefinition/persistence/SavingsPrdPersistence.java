/**

 * SavingsPrdPersistence.java    version: 1.0

 

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
package org.mifos.application.productdefinition.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.meeting.business.RecurrenceTypeEntity;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

public class SavingsPrdPersistence extends Persistence {

	public SavingsOfferingBO getSavingsProduct(Short prdOfferingId)
			throws PersistenceException {
		return (SavingsOfferingBO) getPersistentObject(SavingsOfferingBO.class,
				prdOfferingId);
	}

	public List<SavingsBO> retrieveSavingsAccountsForPrd(Short prdOfferingId)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(ProductDefinitionConstants.PRDOFFERINGID,
				prdOfferingId);
		return executeNamedQuery(NamedQueryConstants.RETRIEVE_SAVINGS_ACCCOUNT,
				queryParameters);
	}

	public Short retrieveDormancyDays() throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("productTypeId", ProductType.SAVINGS.getValue());
		Object obj = execUniqueResultNamedQuery(
				NamedQueryConstants.GET_DORMANCY_DAYS, queryParameters);
		return obj != null ? (Short) obj : null;
	}

	public List<RecurrenceTypeEntity> getSavingsApplicableRecurrenceTypes()
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		return executeNamedQuery(
				NamedQueryConstants.SAVINGS_APPL_RECURRENCETYPES,
				queryParameters);
	}
}
