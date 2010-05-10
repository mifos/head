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

package org.mifos.application.holiday.util.helpers;

import junit.framework.Assert;

import org.junit.Test;

public class RepaymentRuleTypesTest {

    @Test
    public void testfromInt() {
        Assert.assertEquals(RepaymentRuleTypes.fromInt(1), RepaymentRuleTypes.SAME_DAY);
        Assert.assertEquals(RepaymentRuleTypes.fromInt(2), RepaymentRuleTypes.NEXT_MEETING_OR_REPAYMENT);
        Assert.assertEquals(RepaymentRuleTypes.fromInt(3), RepaymentRuleTypes.NEXT_WORKING_DAY);
        Assert.assertEquals(RepaymentRuleTypes.fromInt(4), RepaymentRuleTypes.REPAYMENT_MORATORIUM);
    }

    @Test
    public void testfromIntFailure() {
        try {
            RepaymentRuleTypes.fromInt(0);
            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("No " + RepaymentRuleTypes.class.getSimpleName() + " defined for id=0", e.getMessage());
        }

        try {
            RepaymentRuleTypes.fromInt(5);
            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("No " + RepaymentRuleTypes.class.getSimpleName() + " defined for id=5", e.getMessage());
        }
    }

    @Test
    public void testGetValue() {
        Assert.assertEquals(Short.valueOf("1"), RepaymentRuleTypes.SAME_DAY.getValue());
        Assert.assertEquals(Short.valueOf("2"), RepaymentRuleTypes.NEXT_MEETING_OR_REPAYMENT.getValue());
        Assert.assertEquals(Short.valueOf("3"), RepaymentRuleTypes.NEXT_WORKING_DAY.getValue());
        Assert.assertEquals(Short.valueOf("4"), RepaymentRuleTypes.REPAYMENT_MORATORIUM.getValue());
    }
}
