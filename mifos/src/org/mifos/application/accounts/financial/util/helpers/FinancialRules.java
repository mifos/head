/**

 * FinancialRules.java    version: 1.0

 

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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.framework.spring.SpringUtil;

/**
 * A Spring bean useful in resolving financial actions with their associated GL
 * (general ledger) codes. GL codes are unique to GL accounts.
 * <p>
 * Example use case: principal is added to a new loan (a credit) and GL accounts
 * appropriate for this action must be fetched. <a
 * href="http://www.mifos.org/knowledge/functional-specs/accounting-in-mifos">More
 * use cases</a>.
 * <p>
 * Spring must be initialized prior to using this class. This is currently
 * performed via {@link SpringUtil#initializeSpring()}.
 */
public class FinancialRules {
	private static AccountPersistence accountPersistence = new AccountPersistence();
	private Map<FinancialActionConstants, Short> actionToDebitAccount = new HashMap<FinancialActionConstants, Short>();
	private Map<FinancialActionConstants, Short> actionToCreditAccount = new HashMap<FinancialActionConstants, Short>();

	private static FinancialRules financialRules = new FinancialRules();

	public static final FinancialRules getInstance() {
		return financialRules;
	}

	public short getCategoryAssociatedToAction(short financialActionId,
			FinancialConstants type) throws FinancialException {
		FinancialActionConstants financialAction = FinancialActionConstants
				.getFinancialAction(financialActionId);
		return getCategoryAssociatedToAction(financialAction, type);
	}

	public short getCategoryAssociatedToAction(
			FinancialActionConstants financialAction, FinancialConstants type)
			throws FinancialException {
		if (type.equals(FinancialConstants.DEBIT)) {
			return actionToDebitAccount.get(financialAction);
		}
		else if (type.equals(FinancialConstants.CREDIT)) {
			return actionToCreditAccount.get(financialAction);
		}
		else {
			throw new IllegalArgumentException(
					"Unrecognized FinancialConstants type: " + type
							+ ". Only DEBIT and CREDIT are allowed.");
		}
	}

	/**
	 * Spring looks for this mutator while initializing the
	 * <code>actionToGLCodeDebit</code> property. Note that this property does
	 * <em>not</em> correspond to an attribute on this bean. The actual member
	 * variable mutated by this method is {@link #actionToDebitAccount}.
	 */
	public void setActionToGLCodeDebit(
			HashMap<FinancialActionConstants, String> actionToGLCodeDebit) {
		for (Entry<FinancialActionConstants, String> entry : actionToGLCodeDebit
				.entrySet()) {
			actionToDebitAccount.put(entry.getKey(), accountPersistence
					.getAccountIdFromGlCodeDuringInitialization(entry
							.getValue()));
		}
	}

	/**
	 * Spring looks for this mutator while initializing the
	 * <code>actionToGLCodeCredit</code> property. Note that this property
	 * does <em>not</em> correspond to an attribute on this bean. The actual
	 * member variable mutated by this method is {@link #actionToCreditAccount}.
	 */
	public void setActionToGLCodeCredit(
			HashMap<FinancialActionConstants, String> actionToGLCodeCredit) {
		for (Entry<FinancialActionConstants, String> entry : actionToGLCodeCredit
				.entrySet()) {
			actionToCreditAccount.put(entry.getKey(), accountPersistence
					.getAccountIdFromGlCodeDuringInitialization(entry
							.getValue()));
		}
	}

	// TODO: these methods will replace CategoryConstants
	// (or does COABO#getCOAHead() already get top-level categories?)
	public short getAssetsCategory() {
		throw new RuntimeException("not implemented");
	}

	public short getLiabilitiesCategory() {
		throw new RuntimeException("not implemented");
	}

	public short getIncomeCategory() {
		throw new RuntimeException("not implemented");
	}

	public short getExpensesCategory() {
		throw new RuntimeException("not implemented");
	}
}
