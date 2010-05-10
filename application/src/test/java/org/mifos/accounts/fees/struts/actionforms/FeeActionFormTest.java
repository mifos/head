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

package org.mifos.accounts.fees.struts.actionforms;

import java.util.Locale;

import junit.framework.Assert;

import org.apache.struts.action.ActionErrors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.fees.servicefacade.FeeDto;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class FeeActionFormTest {

    Locale locale = TestUtils.ukLocale();
    FeeActionForm form;

    @Before
    public void setUp() {
        form = new FeeActionForm();
    }

    @After
    public void tearDown() {
        form = null;
    }

    @Test
    public void testIsAmountValidWithNonNumberString() {
        form.setAmount("aaa");
        Assert.assertFalse(isAmountValid(form));
    }

    @Test
    public void testIsAmountValidWithValidString() {
        form.setAmount("2.5");
        Assert.assertTrue(isAmountValid(form));
    }

    @Test
    public void testIsAmountValidWithTooMuchPrecision() {
        form.setAmount("2.12345");
        Assert.assertFalse(isAmountValid(form));
    }

    @Test
    public void testIsAmountValidWithZero() {
        form.setAmount("0.0");
        Assert.assertFalse(isAmountValid(form));
    }

    @Test
    public void testIsAmountValidWithTooLargeANumber() {
        form.setAmount("123456789111111.5");
        Assert.assertFalse(isAmountValid(form));
    }

    @Test
    public void testUpdateWithRateFee() throws Exception {
        FeeDto fee = TestObjectFactory.getRateBasedFee("100", "StatusID", 12.34, "12");
        form.updateWithFee(fee);
        Assert.assertEquals("StatusID", form.getFeeStatus());
        Assert.assertEquals("12.34", form.getRate());
        Assert.assertEquals("12", form.getFeeFormula());
        Assert.assertNull(form.getAmount());
    }

    @Test
    public void testUpdateWithAmountFee() throws Exception {
        FeeDto fee = TestObjectFactory.getAmountBasedFee("100", "StatusID", "12.34");
        form.updateWithFee(fee);
        Assert.assertEquals("StatusID", form.getFeeStatus());
        Assert.assertEquals("12.3", form.getAmount());
        Assert.assertNull(form.getRate());
        Assert.assertNull(form.getFeeFormula());
    }

    private boolean isAmountValid(FeeActionForm form) {
        ActionErrors errors = new ActionErrors();
        form.validateAmount(errors, locale);
        return errors.size() == 0;
    }
}
