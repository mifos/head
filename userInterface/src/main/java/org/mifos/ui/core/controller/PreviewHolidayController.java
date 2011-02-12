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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.HolidayServiceFacade;
import org.mifos.dto.domain.HolidayDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/previewHoliday")
@SessionAttributes("formBean")
public class PreviewHolidayController {

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String EDIT_PARAM = "EDIT";

    @Autowired
    private HolidayServiceFacade holidayServiceFacade;
    private HolidayAssembler holidayAssembler = new HolidayAssembler();

    protected PreviewHolidayController() {
        // default contructor for spring autowiring
    }

    public PreviewHolidayController(final HolidayServiceFacade adminServiceFacade) {
        this.holidayServiceFacade = adminServiceFacade;
    }

    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink("admin.viewholidays", "viewHolidays.ftl").build();
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = EDIT_PARAM, required = false) String edit,
                                    @RequestParam(value = CANCEL_PARAM, required = false) String cancel,
                                    @ModelAttribute("formBean") HolidayFormBean formBean,
                                    BindingResult result,
                                    SessionStatus status) {

        String viewName = REDIRECT_TO_ADMIN_SCREEN;

        ModelAndView modelAndView = new ModelAndView();
        if (StringUtils.isNotBlank(edit)) {
            viewName = "defineNewHoliday";
            modelAndView.setViewName(viewName);
            modelAndView.addObject("formBean", formBean);
        } else if (StringUtils.isNotBlank(cancel)) {
            modelAndView.setViewName("redirect:viewHolidays.ftl");
            status.setComplete();
        } else if (result.hasErrors()) {
            modelAndView.setViewName("previewHoliday");
        } else {

            HolidayDetails holidayDetail = holidayAssembler.translateHolidayFormBeanToDto(formBean);
            List<Short> branchIds = holidayAssembler.convertToIds(formBean.getSelectedOfficeIds());

            this.holidayServiceFacade.createHoliday(holidayDetail, branchIds);

            viewName = REDIRECT_TO_ADMIN_SCREEN;
            modelAndView.setViewName(viewName);
            status.setComplete();
        }
        return modelAndView;
    }

    public void setHolidayAssembler(HolidayAssembler holidayAssembler) {
        this.holidayAssembler = holidayAssembler;
    }
}