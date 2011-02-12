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
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.dto.domain.OfficeLevelDto;
import org.mifos.dto.domain.UpdateConfiguredOfficeLevelRequest;
import org.mifos.service.BusinessRuleException;
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
@RequestMapping("/viewOfficeHierarchy")
@SessionAttributes("formBean")
public class ViewOfficeHierarchyController {

    private static final String FORM_VIEW = "viewOfficeHierarchy";
    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    protected ViewOfficeHierarchyController() {
        // default contructor for spring autowiring
    }

    public ViewOfficeHierarchyController(final AdminServiceFacade adminServiceFacade) {
        this.adminServiceFacade = adminServiceFacade;
    }

    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink("admin.viewofficehierarchy", "viewOfficeHierarchy.ftl").build();
    }

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("formBean")
    public ViewOfficeHierarchyFormBean showPopulatedCheckboxForm() {
        OfficeLevelDto officeLevels = adminServiceFacade.retrieveOfficeLevelsWithConfiguration();
        ViewOfficeHierarchyFormBean formBean = new ViewOfficeHierarchyFormBean();
        formBean.setHeadOffice(officeLevels.isHeadOfficeEnabled());
        formBean.setRegionalOffice(officeLevels.isRegionalOfficeEnabled());
        formBean.setSubRegionalOffice(officeLevels.isSubRegionalOfficeEnabled());
        formBean.setAreaOffice(officeLevels.isAreaOfficeEnabled());
        formBean.setBranchOffice(officeLevels.isBranchOfficeEnabled());
        return formBean;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
                                    @ModelAttribute("formBean") ViewOfficeHierarchyFormBean formBean,
                                    BindingResult result,
                                    SessionStatus status) {
        String viewName = REDIRECT_TO_ADMIN_SCREEN;

        if (StringUtils.isNotBlank(cancel)) {
            viewName = REDIRECT_TO_ADMIN_SCREEN;
            status.setComplete();
        } else if (result.hasErrors()) {
            viewName = populateFormBeanForErrorView(formBean);
        } else {
            try {
                UpdateConfiguredOfficeLevelRequest updateRequest = new UpdateConfiguredOfficeLevelRequest(formBean
                        .isSubRegionalOffice(), formBean.isRegionalOffice(), formBean.isAreaOffice());
                adminServiceFacade.updateOfficeLevelHierarchies(updateRequest);
                status.setComplete();
            } catch (BusinessRuleException e) {
                result.reject(e.getMessageKey(), "The update to office levels was not successful.");
                viewName = populateFormBeanForErrorView(formBean);
            }
        }
        return viewName;
    }

    private String populateFormBeanForErrorView(ViewOfficeHierarchyFormBean formBean) {
        String viewName = FORM_VIEW;
        formBean.setHeadOffice(true);
        formBean.setBranchOffice(true);
        return viewName;
    }
}