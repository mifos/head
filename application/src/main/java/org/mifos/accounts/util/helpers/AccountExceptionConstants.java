/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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
 * This interface stores keys for accounts related exceptions.
 */
public interface AccountExceptionConstants {
    public final String IDGenerationException = "exception.accounts.ApplicationException.IDGenerationException";
    public final String FINDBYGLOBALACCNTEXCEPTION = "exception.accounts.ApplicationException.FindByGlobalAccntException";
    public final String ZEROAMNTADJUSTMENT = "exception.accounts.ApplicationException.ZeroAmntAdjustmnet";
    public final String CANNOTADJUST = "exception.accounts.ApplicationException.CannotAdjust";
    public final String CREATEEXCEPTION = "exception.accounts.create";
    public final String CREATEEXCEPTIONPRDINACTIVE = "exception.accounts.create.prd.inactive";
    public final String CREATEEXCEPTIONCUSTOMERINACTIVE = "exception.accounts.create.customer.inactive";
    public final String APPLY_CAHRGE_NO_CUSTOMER_MEETING_EXCEPTION = "error.applycharge.nocustomermeeting";
    public final String CHANGEINLOANMEETING = "exception.accounts.changeInLoanMeeting";
    public final String CANT_APPLY_FEE_EXCEPTION = "exception.accounts.cantapplyfee";
    public final String CANT_APPLY_CHARGE_EXCEPTION = "exception.accounts.cantapplycharge";
}
