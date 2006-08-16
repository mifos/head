package org.mifos.framework.components.interestcalculator;

import java.util.Date;

import junit.framework.TestCase;

import org.mifos.framework.components.logger.TestLogger;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyTest;

public class FlatInterestCalculatorTest extends TestCase {
	
	private static final long SAMPLE_DATE = 1222333444L;
	private static final long ONE_DAY = 1000L * 60L * 60L * 24L;

	public void testBasics() throws Exception {
		check(new Money(MoneyTest.RUPEE, "9.2"),
			new InterestInputs(
				new Money(MoneyTest.RUPEE, "153"), 6, 
				52, InterestCalculatorConstants.WEEK_INSTALLMENT,
				
				// These would only matter for MONTH_INSTALLMENT
				new Date(SAMPLE_DATE), new Date(SAMPLE_DATE + 1400 * ONE_DAY)
			));
	}

	public void testMonth() throws Exception {
		check(new Money(MoneyTest.RUPEE, "600.0"),
			new InterestInputs(
				new Money(MoneyTest.RUPEE, "99999"), 6, 
				1, InterestCalculatorConstants.MONTH_INSTALLMENT,
				
				// I think the interesting case here is in which
				// the month has 31 days.
				// I guess the start date should be at the start
				// of a month if we really want realism.
				new Date(SAMPLE_DATE), new Date(SAMPLE_DATE + 31 * ONE_DAY)
			));
	}

	private void check(Money expected, InterestInputs inputs) throws InterestCalculationException {
		InterestCalculatorIfc calculator = 
			new FlatInterestCalculator(new TestLogger());
		Money interest = calculator.getInterest(inputs);
		assertEquals(expected, interest);
	}

}
