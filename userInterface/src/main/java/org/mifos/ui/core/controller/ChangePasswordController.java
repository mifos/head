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

package org.mifos.ui.core.controller;

import javax.validation.Valid;

import org.mifos.application.servicefacade.NewLoginServiceFacade;
import org.mifos.dto.domain.ChangePasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@RequestMapping("/changePassword")
@SessionAttributes("formBean")
public class ChangePasswordController {

    private static final String REDIRECT_AND_LOGOUT = "redirect:/j_spring_security_logout";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String CANCEL_PARAM_VALUE = "Cancel";
    private static final String HOME_PAGE = "redirect:/custSearchAction.do?method=getHomePage";

    @Autowired
    private NewLoginServiceFacade loginServiceFacade;

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("formBean")
    public ChangePasswordFormBean showFirstTimeUserChangePasswordForm(@RequestParam(value = "username", required = true) String username) {

        ChangePasswordFormBean formBean = new ChangePasswordFormBean();
        formBean.setUsername(username);
        return formBean;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        LazyBindingErrorProcessor errorProcessor = new LazyBindingErrorProcessor();
        binder.setValidator(new ChangePasswordValidator());
        binder.setBindingErrorProcessor(errorProcessor);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
                                    @ModelAttribute("formBean") @Valid ChangePasswordFormBean formBean,
                                    BindingResult result,
                                    SessionStatus status) {

        String viewName = HOME_PAGE;

        if (CANCEL_PARAM_VALUE.equals(cancel)) {
            viewName = REDIRECT_AND_LOGOUT;
            status.setComplete();
        } else if (result.hasErrors()) {
            viewName = "changePassword";
        } else {
            ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(formBean.getUsername(), formBean.getOldPassword(), formBean.getNewPassword());
            loginServiceFacade.changePassword(changePasswordRequest);
            status.setComplete();
        }

        return viewName;
    }
}