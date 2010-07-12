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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.dto.screen.OfficeLevelDto;
import org.mifos.dto.screen.OfficeLevels;
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

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/admin.ftl";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String CANCEL_PARAM_VALUE = "Cancel";

    private final BreadCrumbsLinks adminCrumb = new BreadCrumbsLinks();
    private final BreadCrumbsLinks childCrumb = new BreadCrumbsLinks();
    private final List<BreadCrumbsLinks> breadcrumbs = new LinkedList<BreadCrumbsLinks> ();

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    protected ViewOfficeHierarchyController() {
        // default contructor for spring autowiring
    }

    public ViewOfficeHierarchyController(final AdminServiceFacade adminServiceFacade) {
        this.adminServiceFacade = adminServiceFacade;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        adminCrumb.setLink("admin.ftl");
        childCrumb.setLink("viewOfficeHierarchy.ftl");
        adminCrumb.setMessage("admin");
        childCrumb.setMessage("viewOfficeHierarchy");
        breadcrumbs.add(adminCrumb);
        breadcrumbs.add(childCrumb);
        return breadcrumbs;
    }

    @ModelAttribute("formBean")
    public ViewOfficeHierarchyFormBean showPopulatedForm() {
        List<OfficeLevelDto> officeLevels = adminServiceFacade.retrieveOfficeLevelsWithConfiguration();
        ViewOfficeHierarchyFormBean formBean = new ViewOfficeHierarchyFormBean();
        setConfiguredDataInForm(formBean, officeLevels);
        return formBean;
    }

    @RequestMapping(method = RequestMethod.POST)
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
            List<OfficeLevelDto> dtos = new ArrayList<OfficeLevelDto>();
            updateConfiguredData(formBean, dtos);
            adminServiceFacade.updateOfficeLevelHierarchies(dtos);
            status.setComplete();
        }

        return viewName;
    }

    private void setConfiguredDataInForm(ViewOfficeHierarchyFormBean formBean,
            List<OfficeLevelDto> officeLevels) {
        formBean.setHeadOffice(true);
        formBean.setBranchOffice(true);
        for (OfficeLevelDto dto : officeLevels) {
            if (dto.getLevelId().equals(OfficeLevels.REGIONALOFFICE)) {
                formBean.setRegionalOffice(true);
            } else if (dto.getLevelId().equals(OfficeLevels.SUBREGIONALOFFICE)) {
                formBean.setSubRegionalOffice(true);
            } else if (dto.getLevelId().equals(OfficeLevels.AREAOFFICE)) {
                formBean.setAreaOffice(true);
            }
        }
    }

    private void updateConfiguredData(ViewOfficeHierarchyFormBean formBean,
            List<OfficeLevelDto> officeLevels) {

        officeLevels.add(new OfficeLevelDto(OfficeLevels.HEADOFFICE.getValue(), formBean.getHeadOffice()));
        officeLevels.add(new OfficeLevelDto(OfficeLevels.BRANCHOFFICE.getValue(), formBean.getBranchOffice()));
        officeLevels.add(new OfficeLevelDto(OfficeLevels.REGIONALOFFICE.getValue(), formBean.getBranchOffice()));
        officeLevels.add(new OfficeLevelDto(OfficeLevels.SUBREGIONALOFFICE.getValue(), formBean.getBranchOffice()));
        officeLevels.add(new OfficeLevelDto(OfficeLevels.AREAOFFICE.getValue(), formBean.getBranchOffice()));
   }
}