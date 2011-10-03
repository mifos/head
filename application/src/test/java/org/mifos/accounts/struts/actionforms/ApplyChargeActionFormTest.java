/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.accounts.struts.actionforms;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.apache.struts.action.ActionErrors;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.TestUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

@RunWith(MockitoJUnitRunner.class)
public class ApplyChargeActionFormTest {

    // for constructing the ChargeType member
    private static final String FEE_ID = "-1";
    private static final String IS_RATE_TYPE = "1";
    private static final String IS_NOT_RATE_TYPE = "0";
    private static final String CHARGE_TYPE_SEPARATOR = ":";

    private String constructChargeType(String feeId, String isRateType) {
        return feeId + CHARGE_TYPE_SEPARATOR + isRateType;
    }

    private Locale locale = TestUtils.ukLocale();

    private ApplyChargeActionForm form;

    private ActionErrors errors;

    @Mock
    private HttpServletRequest request;

    @Before
    public void setUp() {
        form = new ApplyChargeActionForm();
        errors = new ActionErrors();
    }

    /**
     * Test method for <b>ApplyChargeActionForm#validateAmount(ActionErrors, Locale)</b>.
     */
    @Ignore
    @Test
    public void testValidateValidRate() {
        form.setCharge("1.12345");
        form.setChargeType(constructChargeType(FEE_ID, IS_RATE_TYPE));
        form.validateAmount(errors, locale);
        assertEquals(0,errors.size());
    }

    @Test
    public void testValidateInvalidRateWithTooMuchPrecision() {
        form.setCharge("1.123456");
        form.setChargeType(constructChargeType(FEE_ID, IS_RATE_TYPE));
        form.validateAmount(errors, locale);
        assertEquals(1,errors.size());
    }

    @Test
    public void testValidateValidAmount() {
        form.setCharge("1.1");
        form.setChargeType(constructChargeType(FEE_ID, IS_NOT_RATE_TYPE));
        form.validateAmount(errors, locale);
        assertEquals(0,errors.size());
    }

    @Test
    public void testValidateInvalidAmount() {
        form.setCharge("1.12345");
        form.setChargeType(constructChargeType(FEE_ID, IS_NOT_RATE_TYPE));
        form.validateAmount(errors, locale);
        assertEquals(1,errors.size());
    }

    @Test
    public void validateEmptyRateAmountShouldHaveErrors() {
        form.setChargeAmount("");
        form.setChargeType(constructChargeType(FEE_ID, IS_RATE_TYPE));
        form.validateRate(errors, request);
        assertEquals(1, errors.size());
    }

    @Test
    public void validateCharactersRateAmountShouldHaveErrors() {
        form.setChargeAmount("amount");
        form.setChargeType(constructChargeType(FEE_ID, IS_RATE_TYPE));
        form.validateRate(errors, request);
        assertEquals(1, errors.size());
    }

}
