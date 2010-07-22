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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.dto.domain.AcceptedPaymentTypeDto;
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
@RequestMapping("/defineAcceptedPaymentTypes.ftl")
@SessionAttributes("formBean")
public class AcceptedPaymentTypesController {

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    protected AcceptedPaymentTypesController() {
        // default contructor for spring autowiring
    }

    public AcceptedPaymentTypesController(final AdminServiceFacade adminServiceFacade) {
        this.adminServiceFacade = adminServiceFacade;
    }

    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink("defineAcceptedPaymentTypes", "defineAcceptedPaymentTypes.ftl").build();
    }

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("formBean")
    public AcceptedPaymentTypesBean showPopulatedForm() {
        AcceptedPaymentTypeDto acceptedPaymentTypeDto = adminServiceFacade.retrieveAcceptedPaymentTypes();

        // FIXME - keithw - for this form bean to bind to 'spring freemarker macro' controls I think that the data should
        // be represented as maps and you can then use @spring.formMultiSelect path, options, attributes/>
        // SEE - http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/view.html#view-velocity-forms
        // NOTE - you dont' have to worry about populating bean from DTO info
        return new AcceptedPaymentTypesBean();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
                                    @ModelAttribute("formBean") AcceptedPaymentTypesBean formBean,
                                    BindingResult result,
                                    SessionStatus status) {
        String viewName = REDIRECT_TO_ADMIN_SCREEN;

        if (StringUtils.isNotBlank(cancel)) {
            viewName = REDIRECT_TO_ADMIN_SCREEN;
            status.setComplete();
        } else if (result.hasErrors()) {
            viewName = "defineAcceptedPaymentTypes";
        } else {
            status.setComplete();
        }
        return viewName;
    }
}