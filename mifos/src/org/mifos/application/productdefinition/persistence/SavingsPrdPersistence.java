/**

 * SavingsPrdPersistence.java    version: 1.0

 

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
package org.mifos.application.productdefinition.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;

public class SavingsPrdPersistence extends Persistence {

	public SavingsOfferingBO getSavingsProduct(Short prdOfferingId)
			throws PersistenceException {
		try {
			Session session = HibernateUtil.getSessionTL();
			SavingsOfferingBO savingsOffering = (SavingsOfferingBO) session
					.get(SavingsOfferingBO.class, prdOfferingId);
			return savingsOffering;
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
	}

	public List<SavingsBO> retrieveSavingsAccountsForPrd(Short prdOfferingId)
			throws PersistenceException {
		try {
			HashMap<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("prdOfferingId", prdOfferingId);
			return executeNamedQuery(
					NamedQueryConstants.RETRIEVE_SAVINGS_ACCCOUNT,
					queryParameters);
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
	}

	public Short retrieveDormancyDays() throws PersistenceException {
		try {
			Map<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("productTypeId", Short
					.valueOf(AccountTypes.SAVINGSACCOUNT));
			List<Short> queryResult = executeNamedQuery(
					NamedQueryConstants.GET_DORMANCY_DAYS, queryParameters);
			if (null != queryResult && null != queryResult.get(0))
				return queryResult.get(0);
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
		return null;
	}
}
