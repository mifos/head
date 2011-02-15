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

import java.util.List;
import java.util.Map;

import org.mifos.application.admin.servicefacade.HolidayServiceFacade;
import org.mifos.dto.domain.OfficeHoliday;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HolidayController {

    @Autowired
    private HolidayServiceFacade holidayServiceFacade;

    protected HolidayController(){
        //spring autowiring
    }

    public HolidayController(final HolidayServiceFacade holidayServiceFacade){
        this.holidayServiceFacade = holidayServiceFacade;
    }

    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink("organizationPreferences.viewholidays.viewHolidaysOrganizationWide", "viewHolidays.ftl").build();
    }

    @RequestMapping("viewHolidays.ftl")
    @ModelAttribute("holidaysMap")
    public Map<String, List<OfficeHoliday>> showAllHolidaysByYear() {
        return holidayServiceFacade.holidaysByYear();
    }
}