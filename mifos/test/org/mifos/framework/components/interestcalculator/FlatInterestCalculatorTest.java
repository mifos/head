package org.mifos.framework.components.interestcalculator;

import java.util.Date;

import junit.framework.TestCase;

import org.mifos.framework.components.logger.TestLogger;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyTest;

public class FlatInterestCalculatorTest extends TestCase {
	
	private static final long ONE_DAY = 1000L * 60L * 60L * 24L;

	public void testBasics() throws Exception {
		InterestCalculatorIfc calculator = 
			new FlatInterestCalculator(new TestLogger());
		long SAMPLE_DATE = 1222333444L;
		InterestInputs inputs = new InterestInputs(
			new Money(MoneyTest.RUPEE, "153"), 6, 
			52, InterestCalculatorConstansts.WEEK_INSTALLMENT,
			
			// These would only matter for MONTH_INSTALLMENT
			new Date(SAMPLE_DATE), new Date(SAMPLE_DATE + 1400 * ONE_DAY)
			);
		Money interest = calculator.getInterest(inputs);
		assertEquals(new Money(MoneyTest.RUPEE, "9.2"), interest);
	}

}
