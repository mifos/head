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
package org.mifos.accounts.struts.action;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionMapping;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.servicefacade.AccountPaymentDto;
import org.mifos.accounts.servicefacade.AccountServiceFacade;
import org.mifos.accounts.struts.actionforms.AccountApplyPaymentActionForm;
import org.mifos.dto.domain.UserReferenceDto;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.security.util.UserContext;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccountApplyPaymentActionTest {
    @Mock
    private ActionMapping actionMapping;
    @Mock
    private AccountApplyPaymentActionForm form;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private AccountServiceFacade accountServiceFacade;
    @Mock
    private UserContext userContext;

    private AccountApplyPaymentAction accountApplyPaymentAction;

    @Test
    public void loadShouldSetLastPaymentDateOnForm() throws Exception {
        when(accountServiceFacade.getAccountPaymentInformation(Matchers.<Integer>any(), Matchers.<String>any(),
                Matchers.<Short>any(), Matchers.<UserReferenceDto>any(), Matchers.<Date>any())).thenReturn(new AccountPaymentDto(null, 0, null, null, null, new Date(1234)));
        when(form.getAccountId()).thenReturn("1");
        accountApplyPaymentAction = new AccountApplyPaymentAction(accountServiceFacade) {
            @Override
            protected UserContext getUserContext(HttpServletRequest request) {
                return userContext;
            }

            @Override
            void setValuesInSession(HttpServletRequest request, AccountApplyPaymentActionForm actionForm, AccountPaymentDto accountPaymentDto) throws PageExpiredException {
            }
        };
        accountApplyPaymentAction.load(actionMapping, form, request, response);
        verify(form).setLastPaymentDate(new Date(1234));
    }
}
