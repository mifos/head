/**
 * InterestCalculatorConstansts.java version:1.0
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

package org.mifos.framework.components.interestcalculator;

/**
 *
 *  This class holds the constants for interest calculation
 */

public interface InterestCalculatorConstansts
{


	public static final String WEEK_INSTALLMENT = "Week";
	public static final String MONTH_INSTALLMENT = "Month";

	public static final int FLAT_INTEREST = 1;
	public static final int DECLINING_INTEREST = 2;
	public static final int COMPOUND_INTEREST = 3;

	public static int INTEREST_DAYS_360 = 360;
	public static int INTEREST_DAYS_365 = 365;

	public static final int INTEREST_DAYS=365;
	public static final int DAYS_IN_WEEK=7;
	public static final int DAYS_IN_MONTH=30;

	public static final String NOT_SUPPORTED_DURATION_TYPE = "errors.not_supported_durationtype";
	public static final String NOT_SUPPORTED_INTEREST_DAYS = "errors.not_supported_interestdays";
	
	//for savings
	public static final String DAYS="days";


}
