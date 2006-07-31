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

import org.mifos.framework.exceptions.PropertyNotFoundException;

public enum AccountState {

	LOANACC_PARTIALAPPLICATION((short) 1), LOANACC_PENDINGAPPROVAL((short) 2), LOANACC_APPROVED(
			(short) 3), LOANACC_DBTOLOANOFFICER((short) 4), LOANACC_ACTIVEINGOODSTANDING(
			(short) 5), LOANACC_OBLIGATIONSMET((short) 6), LOANACC_WRITTENOFF(
			(short) 7), LOANACC_RESCHEDULED((short) 8), LOANACC_BADSTANDING(
			(short) 9), LOANACC_CANCEL((short) 10), CUSTOMERACCOUNT_ACTIVE(
			(short) 11), CUSTOMERACCOUNT_INACTIVE((short) 12), SAVINGS_ACC_PARTIALAPPLICATION(
			(short) 13), SAVINGS_ACC_PENDINGAPPROVAL((short) 14), SAVINGS_ACC_CANCEL(
			(short) 15), SAVINGS_ACC_APPROVED((short) 16), SAVINGS_ACC_CLOSED(
			(short) 17), SAVINGS_ACC_INACTIVE((short) 18);

	Short value;

	AccountState(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}
	
	public static AccountState getStatus(Short value)throws PropertyNotFoundException{
		for(AccountState status : AccountState.values())
			if(status.getValue().equals(value))
				return status;
		throw new PropertyNotFoundException("AccountState");
	}
}
