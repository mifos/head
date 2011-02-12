/*
 * Copyright Grameen Foundation USA
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

package org.mifos.ui.core.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.dto.screen.ProductConfigurationDto;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.support.SessionStatus;

@SuppressWarnings("PMD")
@RunWith(MockitoJUnitRunner.class)
public class LatenessDormancyControllerTest {

    private static final String FORM_VIEW = "editLatenessDormancy";

    private static final String ADMIN_VIEW = "redirect:/AdminAction.do?method=load";

    private LatenessDormancyController latenessDormancyController;

    @Mock
    private AdminServiceFacade adminServiceFacade;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private SessionStatus sessionStatus;

    @Before
    public void setupAndInject() {
        latenessDormancyController = new LatenessDormancyController(adminServiceFacade);
    }

    @Test
    public void shouldReturnedPopulatedForm() throws Exception {

        // setup
        LatenessDormancyFormBean expectedFormBean = new LatenessDormancyFormBean();
        expectedFormBean.setLatenessDays(1);
        expectedFormBean.setDormancyDays(1);

        // stubbing
        when(adminServiceFacade.retrieveProductConfiguration()).thenReturn(new ProductConfigurationDto(1,1));

        // exercise test
        LatenessDormancyFormBean returnedFormBean = latenessDormancyController.showPopulatedForm();

        // verification
        Assert.assertThat(returnedFormBean, is(expectedFormBean));
        Mockito.verify(adminServiceFacade).retrieveProductConfiguration();
    }

    @Test
    public void cancelShouldRedirectToAdminView() throws Exception {

        // setup
        LatenessDormancyFormBean expectedFormBean = new LatenessDormancyFormBean();
        expectedFormBean.setLatenessDays(1);
        expectedFormBean.setDormancyDays(1);

        // exercise test
        String returnedView = latenessDormancyController.processFormSubmit("Cancel", expectedFormBean, bindingResult, sessionStatus);

        // verification
        Assert.assertThat(returnedView, is(ADMIN_VIEW));
        Mockito.verify(sessionStatus).setComplete();
    }

    @Test
    public void bindingErrorsShouldCauseReturnToFormView() throws Exception {

        // setup
        LatenessDormancyFormBean expectedFormBean = new LatenessDormancyFormBean();
        expectedFormBean.setLatenessDays(1);
        expectedFormBean.setDormancyDays(1);

        // stubbing
        when(bindingResult.hasErrors()).thenReturn(true);

        // exercise test
        String returnedView = latenessDormancyController.processFormSubmit("", expectedFormBean, bindingResult, sessionStatus);

        // verification
        Assert.assertThat(returnedView, is(FORM_VIEW));
        Mockito.verify(sessionStatus, times(0)).setComplete();
    }

    @Test
    public void productConfigurationIsUpdatedOnFormSubmission() throws Exception {

        // setup
        LatenessDormancyFormBean expectedFormBean = new LatenessDormancyFormBean();
        expectedFormBean.setLatenessDays(1);
        expectedFormBean.setDormancyDays(1);

        // exercise test
        String returnedView = latenessDormancyController.processFormSubmit("", expectedFormBean, bindingResult, sessionStatus);

        // verification
        Assert.assertThat(returnedView, is(ADMIN_VIEW));
        Mockito.verify(adminServiceFacade).updateProductConfiguration((ProductConfigurationDto)anyObject());
        Mockito.verify(sessionStatus).setComplete();
    }
}