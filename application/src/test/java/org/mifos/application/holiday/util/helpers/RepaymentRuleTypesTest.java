package org.mifos.application.holiday.util.helpers;

import junit.framework.Assert;

import org.junit.Test;

public class RepaymentRuleTypesTest {

    /**
     * This test will make sure that order of enums in RepaymentRuleTypes is maintained otherwise this test will break.
     *
     * If you change the current order of enums SAME_DAY((short) 1), NEXT_MEETING_OR_REPAYMENT((short) 2),
     * NEXT_WORKING_DAY((short) 3), REPAYMENT_MORATORIUM((short) 4);
     *
     * TO
     *
     * NEXT_MEETING_OR_REPAYMENT((short) 2), SAME_DAY((short) 1), NEXT_WORKING_DAY((short) 3),
     * REPAYMENT_MORATORIUM((short) 4);
     *
     * This test will break, Reason: ordinal value is decided by the order of enums
     *
     * we should use the value(id) mapped to the enums to avoid conditions in future where this could result as an bug
     */
    @Test
    public void testfromOrd() {
        Assert.assertEquals(RepaymentRuleTypes.fromOrd(1), RepaymentRuleTypes.SAME_DAY);
        Assert.assertEquals(RepaymentRuleTypes.fromOrd(2), RepaymentRuleTypes.NEXT_MEETING_OR_REPAYMENT);
        Assert.assertEquals(RepaymentRuleTypes.fromOrd(3), RepaymentRuleTypes.NEXT_WORKING_DAY);
        Assert.assertEquals(RepaymentRuleTypes.fromOrd(4), RepaymentRuleTypes.REPAYMENT_MORATORIUM);
    }

}
