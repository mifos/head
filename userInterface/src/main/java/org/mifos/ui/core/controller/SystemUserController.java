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
import java.util.List;

import org.mifos.application.admin.servicefacade.OfficeServiceFacade;
import org.mifos.dto.domain.OfficeDto;
import org.mifos.dto.domain.OfficeHierarchyDto;
import org.mifos.dto.screen.ListElement;
import org.mifos.dto.screen.OnlyBranchOfficeHierarchyDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class SystemUserController {

    @Autowired
    private OfficeServiceFacade officeServiceFacade;

    protected SystemUserController() {
        // default contructor for spring autowiring
    }

    public SystemUserController(final OfficeServiceFacade serviceFacade) {
        this.officeServiceFacade = serviceFacade;
    }

    public OfficeHierarchyFormBean showBranchHierarchy() {

        OnlyBranchOfficeHierarchyDto hierarchy = this.officeServiceFacade.retrieveBranchOnlyOfficeHierarchy();

        List<BranchOnlyHierarchyBean> branchOnlyHierarchyList = new ArrayList<BranchOnlyHierarchyBean>();

        for (OfficeHierarchyDto office : hierarchy.getBranchOnlyOfficeHierarchy()) {

            BranchOnlyHierarchyBean branchOnlyHierarchyBean = new BranchOnlyHierarchyBean();
            branchOnlyHierarchyBean.setId(office.getOfficeId().intValue());
            branchOnlyHierarchyBean.setName(office.getOfficeName());

            List<ListElement> branches = new ArrayList<ListElement>();

            for (OfficeHierarchyDto child : office.getChildren()) {
                branches.add(new ListElement(child.getOfficeId().intValue(), child.getOfficeName()));
            }

            branchOnlyHierarchyBean.setChildren(branches);
            branchOnlyHierarchyList.add(branchOnlyHierarchyBean);
        }

        List<OfficeDto> nonBranchOffices = this.officeServiceFacade.retrieveAllNonBranchOfficesApplicableToLoggedInUser();

        List<ListElement> nonBranches = new ArrayList<ListElement>();

        for (OfficeDto office : nonBranchOffices) {
            nonBranches.add(new ListElement(office.getId().intValue(), office.getName()));
        }

        OfficeHierarchyFormBean bean = new OfficeHierarchyFormBean();
        bean.setNonBranches(nonBranches);
        bean.setBranchOnlyOfficeHierarchy(branchOnlyHierarchyList);

        return bean;
    }
}