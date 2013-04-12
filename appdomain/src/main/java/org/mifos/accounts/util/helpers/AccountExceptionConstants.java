/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.accounts.util.helpers;

/**
 * This public interface stores keys for accounts related exceptions.
 */
public interface AccountExceptionConstants {
    String IDGenerationException = "exception.accounts.ApplicationException.IDGenerationException";
    String PASSWORD_USED_EXCEPTION = "error.passwordAlreadyUsedException";
    String FINDBYGLOBALACCNTEXCEPTION = "exception.accounts.ApplicationException.FindByGlobalAccntException";
    String ZEROAMNTADJUSTMENT = "exception.accounts.ApplicationException.ZeroAmntAdjustmnet";
    String CANNOTADJUST = "exception.accounts.ApplicationException.CannotAdjust";
    String CREATEEXCEPTION = "exception.accounts.create";
    String CREATEEXCEPTIONPRDINACTIVE = "exception.accounts.create.prd.inactive";
    String CREATEEXCEPTIONCUSTOMERINACTIVE = "exception.accounts.create.customer.inactive";
    String APPLY_CAHRGE_NO_CUSTOMER_MEETING_EXCEPTION = "error.applycharge.nocustomermeeting";
    String CHANGEINLOANMEETING = "exception.accounts.changeInLoanMeeting";
    String CANT_APPLY_FEE_EXCEPTION = "exception.accounts.cantapplyfee";
    String CANT_APPLY_CHARGE_EXCEPTION = "exception.accounts.cantapplycharge";

    String CANT_REMOVE_FEE_EXCEPTION = "exception.accounts.cantremovefee";
    String CANT_REMOVE_PENALTY_EXCEPTION = "exception.accounts.cantremovepenalty";
}
