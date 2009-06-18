/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.framework.util.helpers;

import static org.mifos.framework.TestUtils.EURO;
import junit.framework.TestCase;

import org.mifos.application.accounts.loan.util.helpers.EMIInstallment;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.SystemException;
import org.testng.annotations.Test;

@Test(groups={"unit", "fastTestsSuite"},  dependsOnGroups={"productMixTestSuite"})
public class MethodInvokerTest extends TestCase {

    EMIInstallment installment;

    @Override
    protected void setUp() throws Exception {
        MifosLogManager.configureLogging();
        installment = new EMIInstallment(new Money(EURO, "0"), new Money(EURO, "0"));
    }

    public void testInvokeFailure() throws Exception {
        try {
            MethodInvoker.invoke(this, "testSomethingElse", null, null, null);
            fail();
        } catch (SystemException se) {
            assertEquals("exception.framework.SystemException.MethodInvocationException", se.getKey());
        }
    }

    public void testInvoke() throws Exception {
        Money interest = (Money) MethodInvoker.invoke(installment, "getInterest", new Object[] {});
        assertEquals(Double.valueOf("0.0"), interest.getAmountDoubleValue());
    }

    public void testInvokeWithNoExceptionFailure() throws Exception {
        Object object = MethodInvoker.invokeWithNoException(this, "testSomethingElse", null, null, null);
        assertNull(object);
    }

    public void testInvokeWithNoException() throws Exception {
        Money interest = (Money) MethodInvoker.invokeWithNoException(installment, "getInterest", new Object[] {});
        assertEquals(Double.valueOf("0.0"), interest.getAmountDoubleValue());
    }

}
