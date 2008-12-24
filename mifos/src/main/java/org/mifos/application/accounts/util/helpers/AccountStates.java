/**

 * AccountStates.java    version: xxx



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

import org.mifos.framework.security.util.ActivityMapper;

/**
 * For most purposes this is obsolete and should be replaced by
 * {@link AccountState} (can replace it completely once we figure
 * out how to handle the switch statements, for example in
 * {@link ActivityMapper#getActivityIdForNewStateId(short, short)}).
 */
public interface AccountStates {

	public final short CUSTOMERACCOUNT_ACTIVE = 11;
	public final short CUSTOMERACCOUNT_INACTIVE = 12;

	// Constants for loan account states
	public final short LOANACC_PARTIALAPPLICATION = 1;
	public final short LOANACC_PENDINGAPPROVAL = 2;
	public final short LOANACC_APPROVED = 3;
	public final short LOANACC_DBTOLOANOFFICER = 4;
	public final short LOANACC_ACTIVEINGOODSTANDING = 5;
	public final short LOANACC_OBLIGATIONSMET = 6;
	public final short LOANACC_WRITTENOFF = 7;
	public final short LOANACC_RESCHEDULED = 8;
	public final short LOANACC_BADSTANDING = 9;
	public final short LOANACC_CANCEL = 10;

	//	Constants for savings account states
	public final short SAVINGS_ACC_PARTIALAPPLICATION = 13;
	public final short SAVINGS_ACC_PENDINGAPPROVAL = 14;
	public final short SAVINGS_ACC_CANCEL = 15;
	public final short SAVINGS_ACC_APPROVED = 16;
	public final short SAVINGS_ACC_CLOSED = 17;
	public final short SAVINGS_ACC_INACTIVE = 18;

	public final String TRANSITION_CONFIG_FILE_PATH_SAVINGS="org/mifos/framework/util/resources/stateMachine/StateMachine_saving.xml";
	public final String TRANSITION_CONFIG_FILE_PATH_LOAN="org/mifos/framework/util/resources/stateMachine/StateMachine_loan.xml";
}
