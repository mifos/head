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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

@SuppressWarnings("PMD")
@RunWith(MockitoJUnitRunner.class)
public class DefineProductCategoryPreviewControllerTest {

    private static final String ADMIN_VIEW = "redirect:/AdminAction.do?method=load";

    private DefineProductCategoryPreviewController productCategoryPreviewController;

    @Mock
    private AdminServiceFacade adminServiceFacade;

    @Mock
    private BindingResult bindingResult;

    @Before
    public void setupAndInject() {
        productCategoryPreviewController = new DefineProductCategoryPreviewController(adminServiceFacade);
    }

    @Test
    public void cancelShouldRedirectToAdminView() throws Exception {

        // setup
        ProductCategoryFormBean expectedFormBean = new ProductCategoryFormBean();

        // exercise test
        ModelAndView returnedView = productCategoryPreviewController.processFormSubmit("Cancel", "", expectedFormBean, bindingResult);

        // verification
        Assert.assertThat(returnedView.getViewName(), is(ADMIN_VIEW));
    }
}