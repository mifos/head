package org.mifos.platform.cashflow;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class CashFlowHelperTest {

    @Test
    public void cashFlowBoundariesWithSameStartAndEndDates() {
        CashFlowHelper cashFlowHelper = new CashFlowHelper();
        Map<String, Integer> cashFlowBoundaries = cashFlowHelper.getCashFlowBoundaries(new DateTime(2009, 12, 31, 1, 1,
                1, 1), new DateTime(2009, 12, 31, 1, 1, 1, 1));
        Assert.assertEquals(cashFlowBoundaries.get(CashFlowConstants.START_MONTH), Integer.valueOf(11));
        Assert.assertEquals(cashFlowBoundaries.get(CashFlowConstants.START_YEAR), Integer.valueOf(2009));
        Assert.assertEquals(cashFlowBoundaries.get(CashFlowConstants.NO_OF_MONTHS), Integer.valueOf(3));
    }

    @Test
    public void cashFlowBoundariesWithExtremeDaysOfMonths() {
        CashFlowHelper cashFlowHelper = new CashFlowHelper();
        Map<String, Integer> cashFlowBoundaries = cashFlowHelper.getCashFlowBoundaries(new DateTime(2009, 12, 1, 1, 1,
                1, 1), new DateTime(2009, 12, 31, 1, 1, 1, 1));
        Assert.assertEquals(cashFlowBoundaries.get(CashFlowConstants.START_MONTH), Integer.valueOf(11));
        Assert.assertEquals(cashFlowBoundaries.get(CashFlowConstants.START_YEAR), Integer.valueOf(2009));
        Assert.assertEquals(cashFlowBoundaries.get(CashFlowConstants.NO_OF_MONTHS), Integer.valueOf(3));
    }

    @Test
    public void cashFlowBoundariesForDatesSpanningMultipleYears() {
        CashFlowHelper cashFlowHelper = new CashFlowHelper();
        Map<String, Integer> cashFlowBoundaries = cashFlowHelper.getCashFlowBoundaries(new DateTime(2009, 12, 1, 1, 1,
                1, 1), new DateTime(2010, 1, 1, 1, 1, 1, 1));
        Assert.assertEquals(cashFlowBoundaries.get(CashFlowConstants.START_MONTH), Integer.valueOf(11));
        Assert.assertEquals(cashFlowBoundaries.get(CashFlowConstants.START_YEAR), Integer.valueOf(2009));
        Assert.assertEquals(cashFlowBoundaries.get(CashFlowConstants.NO_OF_MONTHS), Integer.valueOf(4));
    }

    @Test
    public void cashFlowBoundariesForDatesSpanningMultipleYearsAndExtremeDaysOfMonths() {
        CashFlowHelper cashFlowHelper = new CashFlowHelper();
        Map<String, Integer> cashFlowBoundaries = cashFlowHelper.getCashFlowBoundaries(new DateTime(2009, 12, 1, 1, 1,
                1, 1), new DateTime(2010, 1, 31, 1, 1, 1, 1));
        Assert.assertEquals(cashFlowBoundaries.get(CashFlowConstants.START_MONTH), Integer.valueOf(11));
        Assert.assertEquals(cashFlowBoundaries.get(CashFlowConstants.START_YEAR), Integer.valueOf(2009));
        Assert.assertEquals(cashFlowBoundaries.get(CashFlowConstants.NO_OF_MONTHS), Integer.valueOf(4));
    }

    @Test
    public void cashFlowBoundariesForDaysInMiddle() {
        CashFlowHelper cashFlowHelper = new CashFlowHelper();
        Map<String, Integer> cashFlowBoundaries = cashFlowHelper.getCashFlowBoundaries(new DateTime(2009, 12, 31, 1, 1,
                1, 1), new DateTime(2010, 1, 7, 1, 1, 1, 1));
        Assert.assertEquals(cashFlowBoundaries.get(CashFlowConstants.START_MONTH), Integer.valueOf(11));
        Assert.assertEquals(cashFlowBoundaries.get(CashFlowConstants.START_YEAR), Integer.valueOf(2009));
        Assert.assertEquals(cashFlowBoundaries.get(CashFlowConstants.NO_OF_MONTHS), Integer.valueOf(4));
    }
}
