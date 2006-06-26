/**

 * FinancialActionConstants.java    version: 1.0

 

 * Copyright © 2005-2006 Grameen Foundation USA

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

public interface FinancialActionConstants {
	public static final short PRINCIPALPOSTING = 1;

	public static final short INTERESTPOSTING = 2;

	public static final short FEEPOSTING = 3;

	public static final short MISCFEEPOSTING = 4;

	public static final short PENALTYPOSTING = 5;

	public static final short MISCPENALTYPOSTING = 6;

	public static final short DISBURSAL = 7;
	
	public static final short ROUNDING = 8;
	
	public static final short MANDATORYDEPOSIT = 9;
	
	public static final short VOLUNTORYDEPOSIT = 10;
	
	public static final short MANDATORYWITHDRAWAL = 11;
	
	public static final short VOLUNTORYWITHDRAWAL = 12;
	
	public static final short REVERSAL_ADJUSTMENT = 13;
	
	public static final short SAVINGS_INTERESTPOSTING = 14;
	
	public static final short CUSTOMERACCOUNTMISCFEESPOSTING = 16;
	
	public static final short MANDATORYDEPOSIT_ADJUSTMENT = 18;
	
	public static final short VOLUNTORYDEPOSIT_ADJUSTMENT = 19;
	
	public static final short MANDATORYWITHDRAWAL_ADJUSTMENT = 20;
	
	public static final short VOLUNTORYWITHDRAWAL_ADJUSTMENT = 21;
	
	public static final short WRITEOFF = 22;
}
