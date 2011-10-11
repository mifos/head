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
package org.mifos.platform.rest.ui.controller;

import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.mifos.dto.screen.PersonnelInformationDto;
import org.mifos.security.MifosUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PersonnelRESTController {

    @Autowired
    private PersonnelServiceFacade personnelServiceFacade;

    @RequestMapping(value = "personnel/whoami", method = RequestMethod.GET)
    public final @ResponseBody
    PersonnelInformationDto getCurrentPersonnel() {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return personnelServiceFacade.getPersonnelInformationDto((long)user.getUserId(), "");
    }
}
