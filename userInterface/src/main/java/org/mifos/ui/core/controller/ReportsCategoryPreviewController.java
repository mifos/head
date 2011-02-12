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

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.dto.domain.ReportCategoryDto;
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
@RequestMapping("/previewReportCategory")
@SessionAttributes("reportCategory")
public class ReportsCategoryPreviewController {

    private static final String REDIRECT_TO_LIST_OF_REPORT_CATEGORIES = "redirect:/viewReportsCategory.ftl";
    private static final String CANCEL_PARAM = "CANCEL";

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    protected ReportsCategoryPreviewController(){
        //for spring autowiring
    }
    public ReportsCategoryPreviewController(final AdminServiceFacade adminServicefacade){
        this.adminServiceFacade = adminServicefacade;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ReportCategoryFormBean showEmptyForm(@ModelAttribute("reportCategory") ReportCategoryFormBean reportCategory) {
        return reportCategory;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
            @RequestParam(value = "EDIT", required = false) String edit,
            @RequestParam(value = "editFormView", required = true) String editFormView,
            @ModelAttribute("reportCategory") @Valid ReportCategoryFormBean reportCategoryFormBean,
            BindingResult result, SessionStatus status) {

        ModelAndView modelAndView = new ModelAndView(REDIRECT_TO_LIST_OF_REPORT_CATEGORIES);
        if (StringUtils.isNotBlank(cancel)) {
            modelAndView.setViewName(REDIRECT_TO_LIST_OF_REPORT_CATEGORIES);
            status.setComplete();
        } else if (StringUtils.isNotBlank(edit)) {
            modelAndView.setViewName(editFormView);
        } else if (result.hasErrors()) {
            modelAndView.setViewName("defineReportCategory");
            modelAndView.addObject("reportCategory", reportCategoryFormBean);
        } else {

//            if (editFormView.equals("editReportCategory")) {
//                ReportCategoryDto reportCategory = new ReportCategoryDto(null, reportCategoryFormBean.getName());
//                adminServiceFacade.updateReportCategory(reportCategory);
//            } else {
                ReportCategoryDto reportCategory = new ReportCategoryDto(null, reportCategoryFormBean.getName());
                adminServiceFacade.createReportsCategory(reportCategory);
//            }
        }

        return modelAndView;
    }
}