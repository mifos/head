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

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.HolidayServiceFacade;
import org.mifos.dto.domain.HolidayDetails;
import org.mifos.dto.domain.OfficeHoliday;
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
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/defineNewHoliday")
@SessionAttributes("formBean")
public class DefineNewHolidayController {

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";

    @Autowired
    private HolidayServiceFacade holidayServiceFacade;
    private HolidayAssembler holidayAssembler = new HolidayAssembler();

    protected DefineNewHolidayController() {
        // default contructor for spring autowiring
    }

    public DefineNewHolidayController(final HolidayServiceFacade adminServiceFacade) {
        this.holidayServiceFacade = adminServiceFacade;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new HolidayFormValidator());
    }

    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink("admin.viewholidays", "viewHolidays.ftl").withLink("organizationPreferences.definenewholiday.addHoliday", "defineNewHoliday.ftl").build();
    }

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("formBean")
    public HolidayFormBean showPopulatedForm() {
        return new HolidayFormBean();
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
                                    @Valid @ModelAttribute("formBean") HolidayFormBean formBean,
                                    BindingResult result,
                                    SessionStatus status) {

        ModelAndView mav = new ModelAndView(REDIRECT_TO_ADMIN_SCREEN);

        if (StringUtils.isNotBlank(cancel)) {
            status.setComplete();
        } else if (result.hasErrors()) {
            mav = new ModelAndView("defineNewHoliday");
        } else {

            HolidayDetails holidayDetail = holidayAssembler.translateHolidayFormBeanToDto(formBean);

            List<Short> branchIds = holidayAssembler.convertToIds(formBean.getSelectedOfficeIds());

            OfficeHoliday officeHoliday = holidayServiceFacade.retrieveHolidayDetailsForPreview(holidayDetail, branchIds);

            List<String> otherHolidays = holidayServiceFacade.retrieveOtherHolidayNamesWithTheSameDate(holidayDetail, branchIds);

            mav = new ModelAndView("previewHoliday");
            mav.addObject("formBean", formBean);
            mav.addObject("officeHoliday", officeHoliday);
            mav.addObject("otherHolidays", otherHolidays);
        }

        return mav;
    }

    public void setHolidayAssembler(HolidayAssembler holidayAssembler) {
        this.holidayAssembler = holidayAssembler;
    }
}