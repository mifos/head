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

package org.mifos.application.customer.client.struts.actionforms;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.testng.annotations.Test;

@Test(groups={"unit", "fastTestsSuite"},  dependsOnGroups={"productMixTestSuite"})
public class ClientCustActionFormTest extends TestCase {

    public void testGoodDate() throws Exception {
        ClientCustActionForm form = new ClientCustActionForm();
        ActionErrors errors = new ActionErrors();
        form.setDateOfBirthDD("6");
        form.setDateOfBirthMM("12");
        form.setDateOfBirthYY("1950");
        form.validateDateOfBirth(null, errors);
       Assert.assertEquals(0, errors.size());
    }

    public void testFutureDate() throws Exception {
        ClientCustActionForm form = new ClientCustActionForm();
        ActionErrors errors = new ActionErrors();
        form.setDateOfBirthDD("6");
        form.setDateOfBirthMM("12");
        form.setDateOfBirthYY("2108");
        form.validateDateOfBirth(null, errors);
       Assert.assertEquals(1, errors.size());
        ActionMessage message = (ActionMessage) errors.get().next();
       Assert.assertEquals(ClientConstants.FUTURE_DOB_EXCEPTION, message.getKey());
    }

    public void testInvalidDate() throws Exception {
        ClientCustActionForm form = new ClientCustActionForm();
        ActionErrors errors = new ActionErrors();
        form.setDateOfBirthDD("2");
        form.setDateOfBirthMM("20");
        form.setDateOfBirthYY("1980");
        form.validateDateOfBirth(null, errors);
       Assert.assertEquals(1, errors.size());
        ActionMessage message = (ActionMessage) errors.get().next();
       Assert.assertEquals(ClientConstants.INVALID_DOB_EXCEPTION, message.getKey());

    }

}
