package org.mifos.application.customer.util.helpers;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestUtils;

public class LoanCycleCounterTest extends MifosTestCase {

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
