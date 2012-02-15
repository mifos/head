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

import org.joda.time.DateTime;
import org.mifos.application.admin.servicefacade.MonthClosingServiceFacade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


@Controller
@RequestMapping("/monthClosing")
@SessionAttributes("monthClosingFormBean")
public class MonthClosingController {

    @Autowired
    private MonthClosingServiceFacade monthClosingServiceFacade;

    protected MonthClosingController() {
        // default contructor for spring autowiring
    }

    protected MonthClosingController(final MonthClosingServiceFacade monthClosingServiceFacade) {
        this.monthClosingServiceFacade = monthClosingServiceFacade;
    }

    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink("admin.monthClosing", "monthClosing.ftl").build();
    }

    @ModelAttribute("monthClosingFormBean")
    public MonthClosingFormBean showPopulatedForm() {
        MonthClosingFormBean formBean = new MonthClosingFormBean();
        Date monthClosingDateToSet = monthClosingServiceFacade.getMonthClosingDate();
        if (monthClosingDateToSet == null) {
            monthClosingDateToSet = new DateTime().toDate();
        }
        formBean.setDate(new DateTime(monthClosingDateToSet));
        formBean.setLocale(Locale.getDefault());
        return formBean;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView handleRequestInternal() {
        Map<String, Object> model = new HashMap<String, Object>();
        String monthClosingDateString = "-";
        if (monthClosingServiceFacade.getMonthClosingDate() != null) {
            monthClosingDateString = org.joda.time.format.DateTimeFormat.forStyle("S-").
                    withLocale(Locale.getDefault()).print(new DateTime(monthClosingServiceFacade.getMonthClosingDate()));
        }
        model.put("currentDate", monthClosingDateString);
        Map<String, Object> status = new HashMap<String, Object>();
        List<String> errorMessages = new ArrayList<String>();
        status.put("errorMessages", errorMessages);

        ModelAndView modelAndView = new ModelAndView("monthClosing", "model", model);
        modelAndView.addObject("status", status);

        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@ModelAttribute("monthClosingFormBean") @Valid MonthClosingFormBean formBean,
                                          BindingResult result,
                                          SessionStatus status) {
        if (!result.hasErrors()) {
            Date monthClosingDate = null;
            if (formBean.getDate() != null) {
                monthClosingDate = formBean.getDate().toDate();
            }
            monthClosingServiceFacade.setMonthClosingDate(monthClosingDate);
            status.setComplete();
        }

        return handleRequestInternal();
    }
}
