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

package org.mifos.accounts.struts.actionforms;

import static org.junit.Assert.*;

import java.util.Locale;

import org.apache.struts.action.ActionErrors;
import org.junit.Test;
import org.mifos.accounts.fees.util.helpers.RateAmountFlag;
import org.mifos.framework.TestUtils;

public class ApplyChargeActionFormTest {
    // for constructing the ChargeType member
    private static final String FEE_ID = "-1";
    private static final String IS_RATE_TYPE = "1";
    private static final String IS_NOT_RATE_TYPE = "0";
    private static final String CHARGE_TYPE_SEPARATOR = ":";
    
    private String constructChargeType(String feeId, String isRateType) {
        return feeId + CHARGE_TYPE_SEPARATOR + isRateType;
    }
    
    /**
     * Test method for <b>ApplyChargeActionForm#validateAmount(ActionErrors, Locale)</b>.
     */
    @Test
    public void testValidateValidRate() {
        ApplyChargeActionForm form = new ApplyChargeActionForm();
        form.setCharge("1.12345");
        form.setChargeType(constructChargeType(FEE_ID, IS_RATE_TYPE));
        ActionErrors errors = new ActionErrors();
        Locale locale = TestUtils.ukLocale();
        form.validateAmount(errors, locale);
        assertEquals(0,errors.size());
    }

    @Test
    public void testValidateInvalidRateWithTooMuchPrecision() {
        ApplyChargeActionForm form = new ApplyChargeActionForm();
        form.setCharge("1.123456");
        form.setChargeType(constructChargeType(FEE_ID, IS_RATE_TYPE));
        ActionErrors errors = new ActionErrors();
        Locale locale = TestUtils.ukLocale();
        form.validateAmount(errors, locale);
        assertEquals(1,errors.size());
    }
  
    @Test
    public void testValidateValidAmount() {
        ApplyChargeActionForm form = new ApplyChargeActionForm();
        form.setCharge("1.1");
        form.setChargeType(constructChargeType(FEE_ID, IS_NOT_RATE_TYPE));
        ActionErrors errors = new ActionErrors();
        Locale locale = TestUtils.ukLocale();
        form.validateAmount(errors, locale);
        assertEquals(0,errors.size());
    }
    
    @Test
    public void testValidateInvalidAmount() {
        ApplyChargeActionForm form = new ApplyChargeActionForm();
        form.setCharge("1.12345");
        form.setChargeType(constructChargeType(FEE_ID, IS_NOT_RATE_TYPE));
        ActionErrors errors = new ActionErrors();
        Locale locale = TestUtils.ukLocale();
        form.validateAmount(errors, locale);
        assertEquals(1,errors.size());
    }
        
}
