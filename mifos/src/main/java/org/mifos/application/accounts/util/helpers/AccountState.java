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

import org.mifos.config.LocalizedTextLookup;

/*
 * AccountState corresponds to {@link AccountStateEntity} instances.
 */
public enum AccountState implements LocalizedTextLookup {

	// AccountState Entity Values
	LOAN_PARTIAL_APPLICATION(1,"AccountState-PartialApplication"), 
	LOAN_PENDING_APPROVAL(2,"AccountState-ApplicationPendingApproval"), 
	LOAN_APPROVED(3,"AccountState-ApplicationApproved"), 
	LOAN_DISBURSED_TO_LOAN_OFFICER(4,"AccountState-DisbursedToLo"), 
	LOAN_ACTIVE_IN_GOOD_STANDING(5,"AccountState-ActiveInGoodStanding"), 
	LOAN_CLOSED_OBLIGATIONS_MET(6,"AccountState-ClosedObligationMet"), 
	LOAN_CLOSED_WRITTEN_OFF(7,"AccountState-ClosedWrittenOff"), 
	LOAN_CLOSED_RESCHEDULED(8,"AccountState-ClosedRescheduled"), 
	LOAN_ACTIVE_IN_BAD_STANDING(9,"AccountState-ActiveInBadStanding"), 
	LOAN_CANCELLED(10,"AccountState-Cancel"), 
	// CustomerStatus Entity Values
	CUSTOMER_ACCOUNT_ACTIVE(11,"CustomerStatus-CustomerAccountActive"), 
	CUSTOMER_ACCOUNT_INACTIVE(12,"CustomerStatus-CustomerAccountInactive"), 
	// SavingsStatus Entity Values
	SAVINGS_PARTIAL_APPLICATION(13,"SavingsStatus-PartialApplication"), 
	SAVINGS_PENDING_APPROVAL(14,"SavingsStatus-ApplicationPendingApproval"), 
	SAVINGS_CANCELLED(15,"SavingsStatus-Cancelled"), 
	SAVINGS_ACTIVE(16,"SavingsStatus-Active"), 
	SAVINGS_CLOSED(17,"SavingsStatus-Closed"), 
	SAVINGS_INACTIVE(18,"SavingsStatus-Inactive");

	Short value;
	String messageKey;

	private AccountState(int value, String key) {
		this.value = (short)value;
		this.messageKey = key;
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
	
	public boolean isActiveLoanAccountState() {
		return equals(LOAN_ACTIVE_IN_GOOD_STANDING) || equals(LOAN_ACTIVE_IN_BAD_STANDING);
	}
	
	public String getPropertiesKey() {
		return messageKey;
	}	
}
