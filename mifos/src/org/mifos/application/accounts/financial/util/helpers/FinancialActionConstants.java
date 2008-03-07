/**

 * FinancialActionConstants.java    version: 1.0

 

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


public enum FinancialActionConstants {
	PRINCIPALPOSTING (1),
	INTERESTPOSTING (2),
	FEEPOSTING (3),
	MISCFEEPOSTING (4),
	PENALTYPOSTING (5),
	MISCPENALTYPOSTING (6),
	DISBURSAL (7),
	ROUNDING (8),
	MANDATORYDEPOSIT (9),
	VOLUNTORYDEPOSIT (10),
	MANDATORYWITHDRAWAL (11),
	VOLUNTORYWITHDRAWAL (12),
	
	/**
	 * Not currently used in the code. If this action is to be supported, it
	 * must also be added to the bean config file used by {@link FinancialRules}.
	 */
	REVERSAL_ADJUSTMENT (13),
	
	SAVINGS_INTERESTPOSTING (14),
	CUSTOMERACCOUNTMISCFEESPOSTING (16),
	MANDATORYDEPOSIT_ADJUSTMENT (18),
	VOLUNTORYDEPOSIT_ADJUSTMENT (19),
	MANDATORYWITHDRAWAL_ADJUSTMENT (20),
	VOLUNTORYWITHDRAWAL_ADJUSTMENT (21),
	WRITEOFF (22);
	
	short value;
	
	private FinancialActionConstants(int value) {
		this.value = (short)value;
	}

	public short getValue() {
		return value;
	}
	
	public static FinancialActionConstants getFinancialAction(short value){
		for (FinancialActionConstants financialActions : FinancialActionConstants.values()) {
			if (financialActions.getValue() == value) {
				return financialActions;
			}
		}
		throw new RuntimeException("no financial action for " + value);
	}
	
}
