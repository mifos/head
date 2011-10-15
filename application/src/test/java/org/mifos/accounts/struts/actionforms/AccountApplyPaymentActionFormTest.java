/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */
package org.mifos.accounts.struts.actionforms;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccountApplyPaymentActionFormTest {

    @Mock
    private ActionMapping actionMapping;
    @Mock
    private HttpServletRequest request;
    @Mock
    private ResourceBundle resourceBundle;

    private AccountApplyPaymentActionForm accountApplyPaymentActionForm;

    @Before
    public void setUp() throws Exception {
        accountApplyPaymentActionForm = new AccountApplyPaymentActionForm();
    }

    @Test
    public void paymentDateCannotBePriorToLastPaymentDate() throws Exception {
        accountApplyPaymentActionForm.setLastPaymentDate(new DateTime().withDate(2010, 10, 13).toDate());
        ActionErrors actionErrors = accountApplyPaymentActionForm.validatePaymentDate("12/10/2010", "accounts.date_of_trxn");
        Assert.assertEquals(1,actionErrors.size());
    }

    @Test
    public void paymentDateCanBeOnLastPaymentDate() throws Exception {
        accountApplyPaymentActionForm.setLastPaymentDate(new DateTime().withDate(2010, 10, 12).toDate());
        ActionErrors actionErrors = accountApplyPaymentActionForm.validatePaymentDate("12/10/2010", "accounts.date_of_trxn");
        Assert.assertNull(actionErrors);
    }

    @Test
    public void paymentDateCanBeAfterLastPaymentDate() throws Exception {
        accountApplyPaymentActionForm.setLastPaymentDate(new DateTime().withDate(2010, 10, 12).toDate());
        ActionErrors actionErrors = accountApplyPaymentActionForm.validatePaymentDate("13/10/2010", "accounts.date_of_trxn");
        Assert.assertNull(actionErrors);
    }
}
