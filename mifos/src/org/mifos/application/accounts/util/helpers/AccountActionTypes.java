/**

 * AccountActionTypes.java    version: xxx

 

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
package org.mifos.application.accounts.util.helpers;

public enum AccountActionTypes {
	LOAN_REPAYMENT((short) 1), LOAN_PENALTY((short) 2), LOAN_PENALTY_MISC(
			(short) 3), FEE_REPAYMENT((short) 4), FEE_REPAYMENT_MISC((short) 5), SAVINGS_DEPOSIT(
			(short) 6), SAVINGS_WITHDRAWAL((short) 7), PAYMENT((short) 8), LOAN_ADJUSTMENT(
			(short) 9), DISBURSAL((short) 10), SAVINGS_INTEREST_POSTING(
			(short) 11), CUSTOMER_ACCOUNT_REPAYMENT((short) 12), CUSTOMER_ADJUSTMENT(
			(short) 13), SAVINGS_ADJUSTMENT((short) 14), WRITEOFF((short) 15), WAIVEOFFDUE(
			(short) 16), WAIVEOFFOVERDUE((short) 17);

	Short value;

	AccountActionTypes(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}
}
