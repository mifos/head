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
import org.mifos.dto.domain.OfficeLevelDto;
import org.mifos.dto.domain.UpdateConfiguredOfficeLevelRequest;
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
@RequestMapping("/viewOfficeHierarchy")
@SessionAttributes("formBean")
public class ViewOfficeHierarchyController {

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String CANCEL_PARAM_VALUE = "Cancel";

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    protected ViewOfficeHierarchyController() {
        // default contructor for spring autowiring
    }

    public ViewOfficeHierarchyController(final AdminServiceFacade adminServiceFacade) {
        this.adminServiceFacade = adminServiceFacade;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showBreadCrumbs() {

        OfficeLevelDto officeLevels = adminServiceFacade.retrieveOfficeLevelsWithConfiguration();
        ViewOfficeHierarchyFormBean formBean = new ViewOfficeHierarchyFormBean();
        formBean.setHeadOffice(officeLevels.isHeadOfficeEnabled());
        formBean.setRegionalOffice(officeLevels.isRegionalOfficeEnabled());
        formBean.setSubRegionalOffice(officeLevels.isSubRegionalOfficeEnabled());
        formBean.setAreaOffice(officeLevels.isAreaOfficeEnabled());
        formBean.setBranchOffice(officeLevels.isBranchOfficeEnabled());

        ModelAndView mav = new ModelAndView("viewOfficeHierarchy");
        mav.addObject("breadcrumbs", new AdminBreadcrumbBuilder().withLink("viewOfficeHierarchy", "viewOfficeHierarchy.ftl").build());
        mav.addObject("formBean", formBean);
        return mav;
    }

    @RequestMapping(method = RequestMethod.POST)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public String processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
                                    @ModelAttribute("formBean") ViewOfficeHierarchyFormBean formBean,
                                    BindingResult result,
                                    SessionStatus status) throws Exception {

        String viewName = REDIRECT_TO_ADMIN_SCREEN;

        if (CANCEL_PARAM_VALUE.equals(cancel)) {
            viewName = REDIRECT_TO_ADMIN_SCREEN;
            status.setComplete();
        } else if (result.hasErrors()) {
            viewName = "viewOfficeHierarchy";
        } else {
            UpdateConfiguredOfficeLevelRequest updateRequest = new UpdateConfiguredOfficeLevelRequest(formBean.isSubRegionalOffice(), formBean.isRegionalOffice(), formBean.isAreaOffice());
            adminServiceFacade.updateOfficeLevelHierarchies(updateRequest);
            status.setComplete();
        }
        return viewName;
    }
}