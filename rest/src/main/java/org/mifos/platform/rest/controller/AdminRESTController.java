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
package org.mifos.platform.rest.controller;

import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.dto.domain.AcceptedPaymentTypeDto;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.mifos.application.admin.servicefacade.SystemInformationDto;
import org.mifos.application.admin.servicefacade.SystemInformationServiceFacade;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AdminRESTController {

    @Autowired
    private SystemInformationServiceFacade systemInformationServiceFacade;

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    @RequestMapping(value = "admin/system/id-information", method = RequestMethod.GET)
    public @ResponseBody
    SystemInformationDto getSystemInformation(HttpServletRequest request) {
      return systemInformationServiceFacade.getSystemInformation(request.getSession().getServletContext(), request.getLocale());
    }

    @RequestMapping(value = "admin/payment-types/state-accepted", method = RequestMethod.GET)
    public @ResponseBody
    AcceptedPaymentTypeDto getAcceptedPaymentTypes(HttpServletRequest request) {
      return adminServiceFacade.retrieveAcceptedPaymentTypes();
    }
}
