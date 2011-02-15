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

package org.mifos.customers.struts.actionforms;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.customers.group.struts.actionforms.GroupCustActionForm;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CustomerActionFormTest {

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private ActionErrors actionErrors;

    @Mock
    private QuestionnaireServiceFacade questionnaireServiceFacade;

    @Test
    public void testNullInCustomerId() throws Exception {
        CustomerActionForm form = new GroupCustActionForm();
        form.setCustomerId(null);
        assertNull(form.getCustomerId());

        try {
            form.getCustomerIdAsInt();
            fail();
        } catch (NullPointerException expected) {
        }
    }
}
