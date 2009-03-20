/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
 
package org.mifos.application.customer.util.helpers;

import junit.framework.TestCase;

import org.mifos.framework.TestUtils;

public class LoanCycleCounterTest extends TestCase {

	public void testEqualsObject() {
		LoanCycleCounter loanCycleCounter1 = new LoanCycleCounter();
		LoanCycleCounter loanCycleCounter2 = new LoanCycleCounter();
		LoanCycleCounter loanCycleCounter1b = new LoanCycleCounter();
		LoanCycleCounter loanCycleCounter4 = new LoanCycleCounter();
		
		loanCycleCounter1.setOfferingName("Loan1");
		loanCycleCounter2.setOfferingName("Loan2");
		loanCycleCounter1b.setOfferingName("Loan1");
		loanCycleCounter4.setOfferingName("Loan4");
		
		TestUtils.verifyBasicEqualsContract( 
				new LoanCycleCounter []{loanCycleCounter1,loanCycleCounter1b}, 
				new LoanCycleCounter [] {loanCycleCounter2,loanCycleCounter4} );
		
	}

}
