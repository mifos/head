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

import java.util.List;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.dto.domain.MandatoryHiddenFieldsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@RequestMapping("defineMandatoryHiddenFields.ftl")
public class DefineMandatoryHiddenFieldsController {

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String CANCEL_PARAM_VALUE = "Cancel";

    @Autowired
    AdminServiceFacade adminServiceFacade;

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink("definemandatory/hiddenfields", "defineMandatoryHiddenFields.ftl")
                .build();
    }

    @ModelAttribute("fields")
    public DefineMandatoryHiddenFieldsFormBean getDetails() {
        DefineMandatoryHiddenFieldsFormBean formBean = new DefineMandatoryHiddenFieldsFormBean();
        formBean.setFamilyDetailsRequired(adminServiceFacade.retrieveHiddenMandatoryFields().isFamilyDetailsRequired());
        formBean.setHideClientBusinessWorkActivities(adminServiceFacade.retrieveHiddenMandatoryFields()
                .isHideClientBusinessWorkActivities());
        formBean.setHideClientGovtId(adminServiceFacade.retrieveHiddenMandatoryFields().isHideClientGovtId());
        formBean.setHideClientMiddleName(adminServiceFacade.retrieveHiddenMandatoryFields().isHideClientMiddleName());
        formBean.setHideClientPhone(adminServiceFacade.retrieveHiddenMandatoryFields().isHideClientPhone());
        formBean.setHideClientPovertyStatus(adminServiceFacade.retrieveHiddenMandatoryFields()
                .isHideClientPovertyStatus());
        formBean.setHideClientSecondLastName(adminServiceFacade.retrieveHiddenMandatoryFields()
                .isHideClientSecondLastName());
        formBean.setHideClientSpouseFatherInformation(adminServiceFacade.retrieveHiddenMandatoryFields()
                .isHideClientSpouseFatherInformation());
        formBean.setHideClientSpouseFatherMiddleName(adminServiceFacade.retrieveHiddenMandatoryFields()
                .isHideClientSpouseFatherMiddleName());
        formBean.setHideClientSpouseFatherSecondLastName(adminServiceFacade.retrieveHiddenMandatoryFields()
                .isHideClientSpouseFatherSecondLastName());
        formBean.setHideClientTrained(adminServiceFacade.retrieveHiddenMandatoryFields().isHideClientTrained());
        formBean.setHideGroupTrained(adminServiceFacade.retrieveHiddenMandatoryFields().isHideGroupTrained());
        formBean.setHideSystemAddress2(adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemAddress2());
        formBean.setHideSystemAddress3(adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemAddress3());
        formBean.setHideSystemAssignClientPostions(adminServiceFacade.retrieveHiddenMandatoryFields()
                .isHideSystemAssignClientPostions());
        formBean.setHideSystemCitizenShip(adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemCitizenShip());
        formBean.setHideSystemCity(adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemCity());
        formBean.setHideSystemCollateralTypeNotes(adminServiceFacade.retrieveHiddenMandatoryFields()
                .isHideSystemCollateralTypeNotes());
        formBean.setHideSystemCountry(adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemCountry());
        formBean.setHideSystemEducationLevel(adminServiceFacade.retrieveHiddenMandatoryFields()
                .isHideSystemEducationLevel());
        formBean.setHideSystemEthnicity(adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemEthnicity());
        formBean.setHideSystemExternalId(adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemExternalId());
        formBean.setHideSystemHandicapped(adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemHandicapped());
        formBean.setHideSystemPhoto(adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemPhoto());
        formBean.setHideSystemPostalCode(adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemPostalCode());
        formBean.setHideSystemReceiptIdDate(adminServiceFacade.retrieveHiddenMandatoryFields()
                .isHideSystemReceiptIdDate());
        formBean.setHideSystemState(adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemState());

        formBean.setMandatoryClientBusinessWorkActivities(adminServiceFacade.retrieveHiddenMandatoryFields()
                .isMandatoryClientBusinessWorkActivities());
        formBean.setMandatoryClientGovtId(adminServiceFacade.retrieveHiddenMandatoryFields().isMandatoryClientGovtId());
        formBean.setMandatoryClientMiddleName(adminServiceFacade.retrieveHiddenMandatoryFields()
                .isMandatoryClientMiddleName());
        formBean.setMandatoryClientPhone(adminServiceFacade.retrieveHiddenMandatoryFields().isMandatoryClientPhone());
        formBean.setMandatoryClientPovertyStatus(adminServiceFacade.retrieveHiddenMandatoryFields()
                .isMandatoryClientPovertyStatus());
        formBean.setMandatoryClientSecondLastName(adminServiceFacade.retrieveHiddenMandatoryFields()
                .isMandatoryClientSecondLastName());
        formBean.setMandatoryClientSpouseFatherInformation(adminServiceFacade.retrieveHiddenMandatoryFields()
                .isMandatoryClientSpouseFatherInformation());
        formBean.setMandatoryClientSpouseFatherSecondLastName(adminServiceFacade.retrieveHiddenMandatoryFields()
                .isMandatoryClientSpouseFatherSecondLastName());
        formBean.setMandatoryClientTrained(adminServiceFacade.retrieveHiddenMandatoryFields()
                .isMandatoryClientTrained());
        formBean.setMandatorySystemCitizenShip(adminServiceFacade.retrieveHiddenMandatoryFields()
                .isMandatorySystemCitizenShip());
        formBean.setMandatorySystemEducationLevel(adminServiceFacade.retrieveHiddenMandatoryFields()
                .isMandatorySystemEducationLevel());
        formBean.setMandatorySystemEthnicity(adminServiceFacade.retrieveHiddenMandatoryFields()
                .isMandatorySystemEthnicity());
        formBean.setMandatorySystemExternalId(adminServiceFacade.retrieveHiddenMandatoryFields()
                .isMandatorySystemExternalId());
        formBean.setMandatorySystemHandicapped(adminServiceFacade.retrieveHiddenMandatoryFields()
                .isMandatorySystemHandicapped());
        formBean.setMandatorySystemPhoto(adminServiceFacade.retrieveHiddenMandatoryFields().isMandatorySystemPhoto());
        return formBean;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
            MandatoryHiddenFieldsDto bean, BindingResult result, SessionStatus status) {

        String viewName = REDIRECT_TO_ADMIN_SCREEN;

        if (CANCEL_PARAM_VALUE.equals(cancel)) {
            viewName = REDIRECT_TO_ADMIN_SCREEN;
            status.setComplete();
        } else if (result.hasErrors()) {
            viewName = "defineMandatoryHiddenFields";
        } else {
            adminServiceFacade.updateHiddenMandatoryFields(bean);
            status.setComplete();
        }

        return viewName;
    }

}
