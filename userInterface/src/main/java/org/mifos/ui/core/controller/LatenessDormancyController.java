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

package org.mifos.ui.core.controller;

import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.dto.screen.ProductConfigurationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@RequestMapping("/editLatenessDormancy")
@SessionAttributes("formBean")
public class LatenessDormancyController {

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String CANCEL_PARAM_VALUE = "Cancel";

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    protected LatenessDormancyController() {
        // default contructor for spring autowiring
    }

    public LatenessDormancyController(final AdminServiceFacade adminServiceFacade) {
        this.adminServiceFacade = adminServiceFacade;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("formBean")
    public LatenessDormancyFormBean showPopulatedForm() {

        ProductConfigurationDto productConfiguration = adminServiceFacade.retrieveProductConfiguration();

        LatenessDormancyFormBean formBean = new LatenessDormancyFormBean();
        formBean.setLatenessDays(productConfiguration.getLatenessDays());
        formBean.setDormancyDays(productConfiguration.getDormancyDays());

        return formBean;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
                                    @ModelAttribute("formBean") LatenessDormancyFormBean formBean,
                                    BindingResult result,
                                    SessionStatus status) {

        String viewName = REDIRECT_TO_ADMIN_SCREEN;

        if (CANCEL_PARAM_VALUE.equals(cancel)) {
            viewName = REDIRECT_TO_ADMIN_SCREEN;
            status.setComplete();
        } else if (result.hasErrors()) {
            viewName = "editLatenessDormancy";
        } else {
            ProductConfigurationDto productConfigurationDto = new ProductConfigurationDto(formBean.getLatenessDays(), formBean.getDormancyDays());
            this.adminServiceFacade.updateProductConfiguration(productConfigurationDto);
            status.setComplete();
        }

        return viewName;
    }
}