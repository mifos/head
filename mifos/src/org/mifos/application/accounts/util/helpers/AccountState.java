/**

 * AccountState.java    version: 1.0

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue), NW), Suite 400), Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License), Version 2.0 (the "License")), you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing), software
 * distributed under the License is distributed on an "AS IS" BASIS),
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND), either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */
package org.mifos.application.accounts.util.helpers;


public enum AccountState {

	LOANACC_PARTIALAPPLICATION(1), 
	LOANACC_PENDINGAPPROVAL(2), 
	LOANACC_APPROVED(3), 
	LOANACC_DBTOLOANOFFICER(4), 
	LOANACC_ACTIVEINGOODSTANDING(5), 
	LOANACC_OBLIGATIONSMET(6), 
	LOANACC_WRITTENOFF(7), 
	LOANACC_RESCHEDULED(8), 
	LOANACC_BADSTANDING(9), 
	LOANACC_CANCEL(10), 
	CUSTOMERACCOUNT_ACTIVE(11), 
	CUSTOMERACCOUNT_INACTIVE(12), 
	SAVINGS_ACC_PARTIALAPPLICATION(13), 
	SAVINGS_ACC_PENDINGAPPROVAL(14), 
	SAVINGS_ACC_CANCEL(15), 
	SAVINGS_ACC_APPROVED(16), 
	SAVINGS_ACC_CLOSED(17), 
	SAVINGS_ACC_INACTIVE(18);

	Short value;

	private AccountState(int value) {
		this.value = (short)value;
	}

	public Short getValue() {
		return value;
	}
	
	public static AccountState fromShort(Short value){
		for (AccountState candidate : AccountState.values()) {
			if (candidate.getValue().equals(value)) {
				return candidate;
			}
		}
		/* Do we really want null rather than an exception?  I suspect
		   IllegalArgumentException would make more sense. */
		return null;
	}
}
