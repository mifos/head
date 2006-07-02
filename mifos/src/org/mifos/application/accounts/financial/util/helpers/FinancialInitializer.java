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
import org.mifos.application.accounts.financial.business.COAIDMapperEntity;
import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.exceptions.FinancialExceptionConstants;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class FinancialInitializer {
	public static void initialize() throws FinancialException {
		Session session = null;

		try {

			session = HibernateUtil.getSession();
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

			}
		}

	}

	public static void initializeCOA(Session session) throws FinancialException {
		try {
			Query queryCOAIDMapper = session.getNamedQuery("GETALLCOA");
			List<COAIDMapperEntity> listIdMapper = queryCOAIDMapper.list();
			Iterator<COAIDMapperEntity> iterIdMapper = listIdMapper.iterator();
			while (iterIdMapper.hasNext()) {
				COACache.addToCache(hibernateInitalize(iterIdMapper.next()));
			}
		} catch (Exception e) {
			throw new FinancialException(
					FinancialExceptionConstants.COA_INITFAILED, e);
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

	private static COAIDMapperEntity hibernateInitalize(
			COAIDMapperEntity coaIDMapper) {

		Hibernate.initialize(coaIDMapper.getCoa());
		Hibernate.initialize(coaIDMapper.getCoa().getCOAHead());
		Hibernate.initialize(coaIDMapper.getCoa().getAssociatedGlcode());
		Hibernate.initialize(coaIDMapper.getCoa().getSubCategory());

		return coaIDMapper;
	}
}
