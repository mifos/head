package org.mifos.application.branchreport;

import static org.junit.Assert.assertEquals;
import static org.mifos.application.branchreport.LoanArrearsAgingPeriod.FIVE_TO_EIGHT_WEEK;
import static org.mifos.application.branchreport.LoanArrearsAgingPeriod.FOUR_WEEK;
import static org.mifos.application.branchreport.LoanArrearsAgingPeriod.MORE_THAN_TWELVE;
import static org.mifos.application.branchreport.LoanArrearsAgingPeriod.NINE_TO_TWELVE_WEEK;
import static org.mifos.application.branchreport.LoanArrearsAgingPeriod.ONE_WEEK;
import static org.mifos.application.branchreport.LoanArrearsAgingPeriod.THREE_WEEK;
import static org.mifos.application.branchreport.LoanArrearsAgingPeriod.TWO_WEEK;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.junit.runners.Suite;
import org.mifos.framework.util.TestLocalizationConverter;

public class LoanArrearsAgingPeriodTest {
	
	private static final int TOTAL_WEEK_DAYS = 7;

	@Test
	public void testLoanArrearsAgingPeriodValues() throws Exception {
		assertEquals(1, ONE_WEEK.getMinDays());
		assertEquals(TOTAL_WEEK_DAYS, ONE_WEEK.getMaxDays());
		assertEquals(TOTAL_WEEK_DAYS+1, ONE_WEEK.getNotLessThanDays());
		
		assertEquals(TOTAL_WEEK_DAYS+1, TWO_WEEK.getMinDays());
		assertEquals(TOTAL_WEEK_DAYS*2, TWO_WEEK.getMaxDays());
		assertEquals(TOTAL_WEEK_DAYS*2+1, TWO_WEEK.getNotLessThanDays());
		
		assertEquals(TOTAL_WEEK_DAYS*2+1, THREE_WEEK.getMinDays());
		assertEquals(TOTAL_WEEK_DAYS*3, THREE_WEEK.getMaxDays());
		assertEquals(TOTAL_WEEK_DAYS*3+1, THREE_WEEK.getNotLessThanDays());		
		
		assertEquals(TOTAL_WEEK_DAYS*3+1, FOUR_WEEK.getMinDays());
		assertEquals(TOTAL_WEEK_DAYS*4, FOUR_WEEK.getMaxDays());
		assertEquals(TOTAL_WEEK_DAYS*4+1, FOUR_WEEK.getNotLessThanDays());		

		assertEquals(TOTAL_WEEK_DAYS*4+1, FIVE_TO_EIGHT_WEEK.getMinDays());
		assertEquals(TOTAL_WEEK_DAYS*8, FIVE_TO_EIGHT_WEEK.getMaxDays());
		assertEquals(TOTAL_WEEK_DAYS*8+1, FIVE_TO_EIGHT_WEEK.getNotLessThanDays());		

		assertEquals(TOTAL_WEEK_DAYS*8+1, NINE_TO_TWELVE_WEEK.getMinDays());
		assertEquals(TOTAL_WEEK_DAYS*12, NINE_TO_TWELVE_WEEK.getMaxDays());
		assertEquals(TOTAL_WEEK_DAYS*12+1, NINE_TO_TWELVE_WEEK.getNotLessThanDays());		

		assertEquals(TOTAL_WEEK_DAYS*12+1, MORE_THAN_TWELVE.getMinDays());
		assertEquals(Integer.MAX_VALUE-1, MORE_THAN_TWELVE.getMaxDays());
		assertEquals(Integer.MAX_VALUE, MORE_THAN_TWELVE.getNotLessThanDays());		
	}
	
	@Test
	public void testNumberOfArrearsPeriods() throws Exception {
		assertEquals(7, LoanArrearsAgingPeriod.values().length);
	}
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(LoanArrearsAgingPeriodTest.class);
	}	
	
}
