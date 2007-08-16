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

import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.BANKACCOUNTONE;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.BANKBALANCES;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.CLIENTSDEPOSITS;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.DIRECTEXPENDITURE;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.FEES;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.INCOMEMICROCREDIT;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.INTERESTINCOMELOANS;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.LOANSADVANCES;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.LOANTOCLIENTS;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.MANDATORYSAVINGS;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.PENALTY;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.ROUNDINGGL;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.SAVINGSMANDATORY;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.WRITEOFFS;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.CUSTOMERACCOUNTMISCFEESPOSTING;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.DISBURSAL;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.FEEPOSTING;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.INTERESTPOSTING;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.MANDATORYDEPOSIT;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.MANDATORYDEPOSIT_ADJUSTMENT;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.MANDATORYWITHDRAWAL;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.MANDATORYWITHDRAWAL_ADJUSTMENT;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.MISCFEEPOSTING;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.MISCPENALTYPOSTING;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.PENALTYPOSTING;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.PRINCIPALPOSTING;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.ROUNDING;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.SAVINGS_INTERESTPOSTING;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.VOLUNTORYDEPOSIT;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.VOLUNTORYDEPOSIT_ADJUSTMENT;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.VOLUNTORYWITHDRAWAL;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.VOLUNTORYWITHDRAWAL_ADJUSTMENT;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.WRITEOFF;
import static org.mifos.application.accounts.financial.util.helpers.FinancialConstants.CREDIT;
import static org.mifos.application.accounts.financial.util.helpers.FinancialConstants.DEBIT;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.persistence.AccountPersistence;

public class DynamicFinancialRules {
	private Map<FinancialActionConstants, Short> actionToCategoryDebit = new HashMap<FinancialActionConstants, Short>();
	private Map<FinancialActionConstants, Short> actionToCategoryCredit = new HashMap<FinancialActionConstants, Short>();
	
	private static AccountPersistence accountPersistence = new AccountPersistence();

	public void init() {
		addMapping(DEBIT, PRINCIPALPOSTING, 		BANKACCOUNTONE);
		addMapping(DEBIT, INTERESTPOSTING, 			BANKACCOUNTONE);
		addMapping(DEBIT, FEEPOSTING, 				BANKACCOUNTONE);
		addMapping(DEBIT, PENALTYPOSTING, 			BANKBALANCES);
		addMapping(DEBIT, ROUNDING, 				ROUNDINGGL);
		addMapping(DEBIT, MANDATORYDEPOSIT, 		BANKACCOUNTONE);
		addMapping(DEBIT, VOLUNTORYDEPOSIT, 		BANKACCOUNTONE);
		addMapping(DEBIT, MANDATORYWITHDRAWAL,		MANDATORYSAVINGS);
		addMapping(DEBIT, VOLUNTORYWITHDRAWAL, 		CLIENTSDEPOSITS);
		addMapping(DEBIT, SAVINGS_INTERESTPOSTING,	DIRECTEXPENDITURE);
		addMapping(DEBIT, DISBURSAL,	 			LOANTOCLIENTS);
		addMapping(DEBIT, MISCFEEPOSTING, 			BANKACCOUNTONE);
		addMapping(DEBIT, MISCPENALTYPOSTING, 		BANKACCOUNTONE);
		addMapping(DEBIT, CUSTOMERACCOUNTMISCFEESPOSTING, 	BANKACCOUNTONE);
		addMapping(DEBIT, MANDATORYDEPOSIT_ADJUSTMENT, 		MANDATORYSAVINGS);
		addMapping(DEBIT, VOLUNTORYDEPOSIT_ADJUSTMENT, 		CLIENTSDEPOSITS);
		addMapping(DEBIT, MANDATORYWITHDRAWAL_ADJUSTMENT, 	BANKACCOUNTONE);
		addMapping(DEBIT, VOLUNTORYWITHDRAWAL_ADJUSTMENT, 	BANKACCOUNTONE);
		addMapping(DEBIT, WRITEOFF,				 			WRITEOFFS);

		addMapping(CREDIT, PRINCIPALPOSTING, 			LOANSADVANCES);
		addMapping(CREDIT, INTERESTPOSTING, 			INTERESTINCOMELOANS);
		addMapping(CREDIT, FEEPOSTING, 					INCOMEMICROCREDIT);
		addMapping(CREDIT, PENALTYPOSTING, 				PENALTY);
		addMapping(CREDIT, ROUNDING, 					ROUNDINGGL);
		addMapping(CREDIT, MANDATORYDEPOSIT, 			MANDATORYSAVINGS);
		addMapping(CREDIT, VOLUNTORYDEPOSIT, 			CLIENTSDEPOSITS);
		addMapping(CREDIT, MANDATORYWITHDRAWAL, 		BANKACCOUNTONE);
		addMapping(CREDIT, VOLUNTORYWITHDRAWAL, 	 	BANKACCOUNTONE);
		addMapping(CREDIT, SAVINGS_INTERESTPOSTING, 	SAVINGSMANDATORY);
		addMapping(CREDIT, DISBURSAL, 					BANKACCOUNTONE);
		addMapping(CREDIT, MISCFEEPOSTING, 				FEES);
		addMapping(CREDIT, MISCPENALTYPOSTING, 			PENALTY);
		addMapping(CREDIT, CUSTOMERACCOUNTMISCFEESPOSTING,	FEES);
		addMapping(CREDIT, MANDATORYDEPOSIT_ADJUSTMENT, 	BANKACCOUNTONE);
		addMapping(CREDIT, VOLUNTORYDEPOSIT_ADJUSTMENT, 	BANKACCOUNTONE);
		addMapping(CREDIT, MANDATORYWITHDRAWAL_ADJUSTMENT,	MANDATORYSAVINGS);
		addMapping(CREDIT, VOLUNTORYWITHDRAWAL_ADJUSTMENT,	CLIENTSDEPOSITS);
		addMapping(CREDIT, WRITEOFF, 						LOANTOCLIENTS);
	}

	public void initByName() {
		String BANKACCOUNTONE_NAME = "Bank Account 1";
		String BANKBALANCES_NAME = "Bank Balances";
		String ROUNDINGGL_NAME = "Income from 999 Account";
		String MANDATORYSAVINGS_NAME = "Mandatory Savings 1";
		String CLIENTSDEPOSITS_NAME = "Clients Deposits 1";
		String DIRECTEXPENDITURE_NAME = "Direct Expenditure";
		String LOANTOCLIENTS_NAME = "Loans to clients";
		String WRITEOFFS_NAME = "Write-offs";
		String LOANSADVANCES_NAME = "Loans and Advances";
		String INTERESTINCOMELOANS_NAME = "Interest income from loans";
		String INCOMEMICROCREDIT_NAME = "Income from micro credit & lending activities";
		String PENALTY_NAME = "Penalty";
		String SAVINGSMANDATORY_NAME = "Mandatory Savings 2";
		String FEES_NAME = "Fees";
		
		
		addMapping(DEBIT, PRINCIPALPOSTING, 		BANKACCOUNTONE_NAME);
		addMapping(DEBIT, INTERESTPOSTING, 			BANKACCOUNTONE_NAME);
		addMapping(DEBIT, FEEPOSTING, 				BANKACCOUNTONE_NAME);
		addMapping(DEBIT, PENALTYPOSTING, 			BANKBALANCES_NAME);
		addMapping(DEBIT, ROUNDING, 				ROUNDINGGL_NAME);
		addMapping(DEBIT, MANDATORYDEPOSIT, 		BANKACCOUNTONE_NAME);
		addMapping(DEBIT, VOLUNTORYDEPOSIT, 		BANKACCOUNTONE_NAME);
		addMapping(DEBIT, MANDATORYWITHDRAWAL,		MANDATORYSAVINGS_NAME);
		addMapping(DEBIT, VOLUNTORYWITHDRAWAL, 		CLIENTSDEPOSITS_NAME);
		addMapping(DEBIT, SAVINGS_INTERESTPOSTING,	DIRECTEXPENDITURE_NAME);
		addMapping(DEBIT, DISBURSAL,	 			LOANTOCLIENTS_NAME);
		addMapping(DEBIT, MISCFEEPOSTING, 			BANKACCOUNTONE_NAME);
		addMapping(DEBIT, MISCPENALTYPOSTING, 		BANKACCOUNTONE_NAME);
		addMapping(DEBIT, CUSTOMERACCOUNTMISCFEESPOSTING, 	BANKACCOUNTONE_NAME);
		addMapping(DEBIT, MANDATORYDEPOSIT_ADJUSTMENT, 		MANDATORYSAVINGS_NAME);
		addMapping(DEBIT, VOLUNTORYDEPOSIT_ADJUSTMENT, 		CLIENTSDEPOSITS_NAME);
		addMapping(DEBIT, MANDATORYWITHDRAWAL_ADJUSTMENT, 	BANKACCOUNTONE_NAME);
		addMapping(DEBIT, VOLUNTORYWITHDRAWAL_ADJUSTMENT, 	BANKACCOUNTONE_NAME);
		addMapping(DEBIT, WRITEOFF,				 			WRITEOFFS_NAME);

		addMapping(CREDIT, PRINCIPALPOSTING, 			LOANSADVANCES_NAME);
		addMapping(CREDIT, INTERESTPOSTING, 			INTERESTINCOMELOANS_NAME);
		addMapping(CREDIT, FEEPOSTING, 					INCOMEMICROCREDIT_NAME);
		addMapping(CREDIT, PENALTYPOSTING, 				PENALTY_NAME);
		addMapping(CREDIT, ROUNDING, 					ROUNDINGGL_NAME);
		addMapping(CREDIT, MANDATORYDEPOSIT, 			MANDATORYSAVINGS_NAME);
		addMapping(CREDIT, VOLUNTORYDEPOSIT, 			CLIENTSDEPOSITS_NAME);
		addMapping(CREDIT, MANDATORYWITHDRAWAL, 		BANKACCOUNTONE_NAME);
		addMapping(CREDIT, VOLUNTORYWITHDRAWAL, 	 	BANKACCOUNTONE_NAME);
		addMapping(CREDIT, SAVINGS_INTERESTPOSTING, 	SAVINGSMANDATORY_NAME);
		addMapping(CREDIT, DISBURSAL, 					BANKACCOUNTONE_NAME);
		addMapping(CREDIT, MISCFEEPOSTING, 				FEES_NAME);
		addMapping(CREDIT, MISCPENALTYPOSTING, 			PENALTY_NAME);
		addMapping(CREDIT, CUSTOMERACCOUNTMISCFEESPOSTING,	FEES_NAME);
		addMapping(CREDIT, MANDATORYDEPOSIT_ADJUSTMENT, 	BANKACCOUNTONE_NAME);
		addMapping(CREDIT, VOLUNTORYDEPOSIT_ADJUSTMENT, 	BANKACCOUNTONE_NAME);
		addMapping(CREDIT, MANDATORYWITHDRAWAL_ADJUSTMENT,	MANDATORYSAVINGS_NAME);
		addMapping(CREDIT, VOLUNTORYWITHDRAWAL_ADJUSTMENT,	CLIENTSDEPOSITS_NAME);
		addMapping(CREDIT, WRITEOFF, 						LOANTOCLIENTS_NAME);
	}

	public void initByGlCode() {
		String BANKACCOUNTONE_NAME = "11201";
		String BANKBALANCES_NAME = "11200";
		String ROUNDINGGL_NAME = "31401";
		String MANDATORYSAVINGS_NAME = "24000";
		String CLIENTSDEPOSITS_NAME = "23000";
		String DIRECTEXPENDITURE_NAME = "41000";
		String LOANTOCLIENTS_NAME = "13101";
		String WRITEOFFS_NAME = "13201";
		String LOANSADVANCES_NAME = "13100";
		String INTERESTINCOMELOANS_NAME = "31100";
		String INCOMEMICROCREDIT_NAME = "31300";
		String PENALTY_NAME = "31102";
		String SAVINGSMANDATORY_NAME = "24100";
		String FEES_NAME = "31301";
		
		
		addMapping(DEBIT, PRINCIPALPOSTING, 		BANKACCOUNTONE_NAME);
		addMapping(DEBIT, INTERESTPOSTING, 			BANKACCOUNTONE_NAME);
		addMapping(DEBIT, FEEPOSTING, 				BANKACCOUNTONE_NAME);
		addMapping(DEBIT, PENALTYPOSTING, 			BANKBALANCES_NAME);
		addMapping(DEBIT, ROUNDING, 				ROUNDINGGL_NAME);
		addMapping(DEBIT, MANDATORYDEPOSIT, 		BANKACCOUNTONE_NAME);
		addMapping(DEBIT, VOLUNTORYDEPOSIT, 		BANKACCOUNTONE_NAME);
		addMapping(DEBIT, MANDATORYWITHDRAWAL,		MANDATORYSAVINGS_NAME);
		addMapping(DEBIT, VOLUNTORYWITHDRAWAL, 		CLIENTSDEPOSITS_NAME);
		addMapping(DEBIT, SAVINGS_INTERESTPOSTING,	DIRECTEXPENDITURE_NAME);
		addMapping(DEBIT, DISBURSAL,	 			LOANTOCLIENTS_NAME);
		addMapping(DEBIT, MISCFEEPOSTING, 			BANKACCOUNTONE_NAME);
		addMapping(DEBIT, MISCPENALTYPOSTING, 		BANKACCOUNTONE_NAME);
		addMapping(DEBIT, CUSTOMERACCOUNTMISCFEESPOSTING, 	BANKACCOUNTONE_NAME);
		addMapping(DEBIT, MANDATORYDEPOSIT_ADJUSTMENT, 		MANDATORYSAVINGS_NAME);
		addMapping(DEBIT, VOLUNTORYDEPOSIT_ADJUSTMENT, 		CLIENTSDEPOSITS_NAME);
		addMapping(DEBIT, MANDATORYWITHDRAWAL_ADJUSTMENT, 	BANKACCOUNTONE_NAME);
		addMapping(DEBIT, VOLUNTORYWITHDRAWAL_ADJUSTMENT, 	BANKACCOUNTONE_NAME);
		addMapping(DEBIT, WRITEOFF,				 			WRITEOFFS_NAME);

		addMapping(CREDIT, PRINCIPALPOSTING, 			LOANSADVANCES_NAME);
		addMapping(CREDIT, INTERESTPOSTING, 			INTERESTINCOMELOANS_NAME);
		addMapping(CREDIT, FEEPOSTING, 					INCOMEMICROCREDIT_NAME);
		addMapping(CREDIT, PENALTYPOSTING, 				PENALTY_NAME);
		addMapping(CREDIT, ROUNDING, 					ROUNDINGGL_NAME);
		addMapping(CREDIT, MANDATORYDEPOSIT, 			MANDATORYSAVINGS_NAME);
		addMapping(CREDIT, VOLUNTORYDEPOSIT, 			CLIENTSDEPOSITS_NAME);
		addMapping(CREDIT, MANDATORYWITHDRAWAL, 		BANKACCOUNTONE_NAME);
		addMapping(CREDIT, VOLUNTORYWITHDRAWAL, 	 	BANKACCOUNTONE_NAME);
		addMapping(CREDIT, SAVINGS_INTERESTPOSTING, 	SAVINGSMANDATORY_NAME);
		addMapping(CREDIT, DISBURSAL, 					BANKACCOUNTONE_NAME);
		addMapping(CREDIT, MISCFEEPOSTING, 				FEES_NAME);
		addMapping(CREDIT, MISCPENALTYPOSTING, 			PENALTY_NAME);
		addMapping(CREDIT, CUSTOMERACCOUNTMISCFEESPOSTING,	FEES_NAME);
		addMapping(CREDIT, MANDATORYDEPOSIT_ADJUSTMENT, 	BANKACCOUNTONE_NAME);
		addMapping(CREDIT, VOLUNTORYDEPOSIT_ADJUSTMENT, 	BANKACCOUNTONE_NAME);
		addMapping(CREDIT, MANDATORYWITHDRAWAL_ADJUSTMENT,	MANDATORYSAVINGS_NAME);
		addMapping(CREDIT, VOLUNTORYWITHDRAWAL_ADJUSTMENT,	CLIENTSDEPOSITS_NAME);
		addMapping(CREDIT, WRITEOFF, 						LOANTOCLIENTS_NAME);
	}
	
	public short getCategoryAssociatedToAction(short financialActionId,
			Short type) throws FinancialException {
		FinancialActionConstants financialAction = FinancialActionConstants.getFinancialAction(financialActionId);
		return getCategoryAssociatedToAction(financialAction, type);
	}
	
	public void addMapping(Short type, FinancialActionConstants action,
			String glCode) {
		addMapping(type, action, accountPersistence.getAccountIdFromGlCode(glCode));
	}

	public void addMapping(Short type, FinancialActionConstants action,
			Short categoryId) {
		if (type.equals(FinancialConstants.CREDIT)) {
			actionToCategoryCredit.put(action, categoryId);
		} else if (type.equals(FinancialConstants.DEBIT)) {
			actionToCategoryDebit.put(action, categoryId);
		} else {
			throw new RuntimeException("Unrecognized FinancialConstants type: " + type);
		}
	}

	public short getCategoryAssociatedToAction(FinancialActionConstants financialAction,
				Short type) throws FinancialException {
		if (type.equals(DEBIT)) {
			return actionToCategoryDebit.get(financialAction);
		} else if (type.equals(CREDIT)) {
			return actionToCategoryCredit.get(financialAction);
		} else {
			throw new RuntimeException("Unrecognized FinancialConstants type: " + type);
		}
	}

	public void setActionToCategoryDebit(
			Map<FinancialActionConstants, Short> actionToCategoryDebit) {
		this.actionToCategoryDebit = actionToCategoryDebit;
	}

	public void setActionToCategoryCredit(
			Map<FinancialActionConstants, Short> actionToCategoryCredit) {
		this.actionToCategoryCredit = actionToCategoryCredit;
	}

	public void setActionToGLCodeDebit(
			HashMap<FinancialActionConstants, String> actionToGLCodeDebit) {
		for (Entry<FinancialActionConstants, String> entry : actionToGLCodeDebit.entrySet()) {
			actionToCategoryDebit.put(entry.getKey(), accountPersistence.getAccountIdFromGlCode(entry.getValue()));
		}
	}

	public void setActionToGLCodeCredit(
			HashMap<FinancialActionConstants, String> actionToGLCodeCredit) {
		for (Entry<FinancialActionConstants, String> entry : actionToGLCodeCredit.entrySet()) {
			actionToCategoryCredit.put(entry.getKey(), accountPersistence.getAccountIdFromGlCode(entry.getValue()));
		}
	}
}
