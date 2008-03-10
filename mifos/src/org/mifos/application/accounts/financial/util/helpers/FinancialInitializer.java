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

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.financial.business.COABO;
import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.exceptions.FinancialExceptionConstants;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.config.ChartOfAccountsConfig;
import org.mifos.config.GLAccount;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class FinancialInitializer {
	public static void initialize() throws FinancialException {
		try {
			HibernateUtil.getSessionTL();
			HibernateUtil.startTransaction();
			initalizeFinancialAction();
			loadCOA();
			HibernateUtil.commitTransaction();
			
			// necessary or cacheCOA() doesn't work correctly. Is that because
			// the commitTransaction() isn't flushing the session?
			HibernateUtil.closeSession();
			
			cacheCOA();
		}
		catch (Exception e) {
			HibernateUtil.rollbackTransaction();
			throw new FinancialException(
					FinancialExceptionConstants.ACTIONNOTFOUND, e);
		}
	}

	/**
	 * Reads chart of accounts from a configuration file and inserts into the
	 * database.
	 */
	public static void loadCOA() throws FinancialException {
		Session session = HibernateUtil.getSessionTL();
		if (ChartOfAccountsConfig.isLoaded(session))
			return;

		ChartOfAccountsConfig coa;
		try {
			coa = ChartOfAccountsConfig.load(ChartOfAccountsConfig.getCoaUri());
		}
		catch (ConfigurationException e) {
			throw new FinancialException(e);
		}

		AccountPersistence ap = new AccountPersistence();
		for (GLAccount glAccount : coa.getGLAccounts()) {
			// TODO: don't add accounts that already exist
			// -- add unique constraint(s) to coa table?
			ap.addGeneralLedgerAccount(glAccount.name, glAccount.glCode,
					glAccount.parentGlCode, glAccount.categoryType);
		}
	}

	/**
	 * Reads chart of accounts from the database and caches in memory.
	 */
	public static void cacheCOA() throws FinancialException {
		if (ChartOfAccountsCache.isInitialized())
			return;
		Session session = HibernateUtil.getSessionTL();
		Query query = session.getNamedQuery(NamedQueryConstants.GET_ALL_COA);
		List<COABO> coaBoList = query.list();
		for (COABO coabo : coaBoList) {
			ChartOfAccountsCache.add(hibernateInitalize(coabo));
		}
	}

	public static void initalizeFinancialAction()
			throws FinancialException {
		Session session = HibernateUtil.getSessionTL();
		try {
			Query queryFinancialAction = session
					.getNamedQuery(FinancialQueryConstants.GET_ALL_FINANCIAL_ACTION);
			List<FinancialActionBO> listFinancialAction =
				queryFinancialAction.list();
			for (FinancialActionBO fabo : listFinancialAction)
				FinancialActionCache.addToCache(fabo);
		}
		catch (Exception e) {
			throw new FinancialException(
					FinancialExceptionConstants.FINANCIALACTION_INITFAILED, e);
		}

	}

	private static COABO hibernateInitalize(COABO coa) {

		Hibernate.initialize(coa);
		Hibernate.initialize(coa.getCOAHead());
		Hibernate.initialize(coa.getAssociatedGlcode());
		Hibernate.initialize(coa.getSubCategory());

		return coa;
	}

}
