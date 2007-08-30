/**

 * FinancialInitializer.java    version: 1.0

 

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

package org.mifos.application.accounts.financial.util.helpers;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.financial.business.COABO;
import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.exceptions.FinancialExceptionConstants;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class FinancialInitializer {
	public static void initialize() throws FinancialException {
		Session session = null;

		try {
			session = HibernateUtil.openSession();
			initalizeFinancialAction(session);
			initializeCOA(session);
		}
		catch (Exception e) {
			throw new FinancialException(
					FinancialExceptionConstants.ACTIONNOTFOUND, e);
		} finally {
			try {
				HibernateUtil.closeSession(session);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static void initializeCOA(Session session) throws FinancialException {
		if (ChartOfAccountsCache.isInitialized()) return;
		
		Query query = session.getNamedQuery(NamedQueryConstants.GET_ALL_COA);
		List<COABO> coaList = query.list();
		for (COABO coa: coaList){
			ChartOfAccountsCache.add(hibernateInitalize(coa));
		}
	}

	public static void initalizeFinancialAction(Session session)
			throws FinancialException {
		try {
			Query queryFinancialAction = session
					.getNamedQuery("GETALLFINANCIALACTION");
			List<FinancialActionBO> listFinancialAction = queryFinancialAction
					.list();
			Iterator<FinancialActionBO> iterFinancialAction = listFinancialAction
					.iterator();
			while (iterFinancialAction.hasNext()) {
				FinancialActionCache.addToCache(iterFinancialAction.next());
			}
		} catch (Exception e) {
			throw new FinancialException(
					FinancialExceptionConstants.FINANCIALACTION_INITFAILED, e);
		}

	}

	private static COABO hibernateInitalize(
			COABO coa) {

		Hibernate.initialize(coa);
		Hibernate.initialize(coa.getCOAHead());
		Hibernate.initialize(coa.getAssociatedGlcode());
		Hibernate.initialize(coa.getSubCategory());

		return coa;
	}

}
