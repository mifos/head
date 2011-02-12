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

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink("admin.definemandatory/hiddenfields",
                "defineMandatoryHiddenFields.ftl").build();
    }

    @ModelAttribute("fields")
    public DefineMandatoryHiddenFieldsFormBean getDetails() {

        MandatoryHiddenFieldsDto dto = this.adminServiceFacade.retrieveHiddenMandatoryFields();
        return assmebleFormBean(dto);
    }

    private DefineMandatoryHiddenFieldsFormBean assmebleFormBean(MandatoryHiddenFieldsDto dto) {

        DefineMandatoryHiddenFieldsFormBean formBean = new DefineMandatoryHiddenFieldsFormBean();

        formBean.setFamilyDetailsRequired(dto.isFamilyDetailsRequired());
        formBean.setHideClientBusinessWorkActivities(dto.isHideClientBusinessWorkActivities());
        formBean.setHideClientGovtId(dto.isHideClientGovtId());
        formBean.setHideClientMiddleName(dto.isHideClientMiddleName());
        formBean.setHideClientPhone(dto.isHideClientPhone());
        formBean.setHideClientPovertyStatus(dto.isHideClientPovertyStatus());
        formBean.setHideClientSecondLastName(dto.isHideClientSecondLastName());
        formBean.setHideClientSpouseFatherInformation(dto.isHideClientSpouseFatherInformation());
        formBean.setHideClientSpouseFatherMiddleName(dto.isHideClientSpouseFatherMiddleName());
        formBean.setHideClientSpouseFatherSecondLastName(dto.isHideClientSpouseFatherSecondLastName());
        formBean.setHideClientTrained(dto.isHideClientTrained());
        formBean.setHideGroupTrained(dto.isHideGroupTrained());
        formBean.setHideSystemAddress2(dto.isHideSystemAddress2());
        formBean.setHideSystemAddress3(dto.isHideSystemAddress3());
        formBean.setHideSystemAssignClientPostions(dto.isHideSystemAssignClientPostions());
        formBean.setHideSystemCitizenShip(dto.isHideSystemCitizenShip());
        formBean.setHideSystemCity(dto.isHideSystemCity());
        formBean.setHideSystemCollateralTypeNotes(dto.isHideSystemCollateralTypeNotes());
        formBean.setHideSystemCountry(dto.isHideSystemCountry());
        formBean.setHideSystemEducationLevel(dto.isHideSystemEducationLevel());
        formBean.setHideSystemEthnicity(dto.isHideSystemEthnicity());
        formBean.setHideSystemExternalId(dto.isHideSystemExternalId());
        formBean.setHideSystemHandicapped(dto.isHideSystemHandicapped());
        formBean.setHideSystemPhoto(dto.isHideSystemPhoto());
        formBean.setHideSystemPostalCode(dto.isHideSystemPostalCode());
        formBean.setHideSystemReceiptIdDate(dto.isHideSystemReceiptIdDate());
        formBean.setHideSystemState(dto.isHideSystemState());

        formBean.setMandatoryClientBusinessWorkActivities(dto.isMandatoryClientBusinessWorkActivities());
        formBean.setMandatoryClientGovtId(dto.isMandatoryClientGovtId());
        formBean.setMandatoryClientMiddleName(dto.isMandatoryClientMiddleName());
        formBean.setMandatoryClientPhone(dto.isMandatoryClientPhone());
        formBean.setMandatoryClientPovertyStatus(dto.isMandatoryClientPovertyStatus());
        formBean.setMandatoryMaritalStatus(dto.isMandatoryMaritalStatus());
        formBean.setMandatoryClientFamilyDetails(dto.isMandatoryClientFamilyDetails());

        formBean.setMandatoryClientSecondLastName(dto.isMandatoryClientSecondLastName());
        formBean.setMandatoryClientSpouseFatherInformation(dto.isMandatoryClientSpouseFatherInformation());
        formBean.setMandatoryClientSpouseFatherSecondLastName(dto.isMandatoryClientSpouseFatherSecondLastName());
        formBean.setMandatoryClientTrained(dto.isMandatoryClientTrained());
        formBean.setMandatoryClientTrainedOn(dto.isMandatoryClientTrainedOn());
        formBean.setMandatoryNumberOfChildren(dto.isMandatoryNumberOfChildren());

        formBean.setMandatorySystemAddress1(dto.isMandatorySystemAddress1());
        formBean.setMandatorySystemCitizenShip(dto.isMandatorySystemCitizenShip());
        formBean.setMandatorySystemEducationLevel(dto.isMandatorySystemEducationLevel());
        formBean.setMandatorySystemEthnicity(dto.isMandatorySystemEthnicity());
        formBean.setMandatorySystemExternalId(dto.isMandatorySystemExternalId());
        formBean.setMandatorySystemHandicapped(dto.isMandatorySystemHandicapped());
        formBean.setMandatorySystemPhoto(dto.isMandatorySystemPhoto());

        formBean.setMandatoryLoanAccountPurpose(dto.isMandatoryLoanAccountPurpose());
        formBean.setMandatoryLoanSourceOfFund(dto.isMandatoryLoanSourceOfFund());

        return formBean;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
            DefineMandatoryHiddenFieldsFormBean bean, BindingResult result, SessionStatus status) {

        String viewName = REDIRECT_TO_ADMIN_SCREEN;

        if (StringUtils.isNotBlank(cancel)) {
            viewName = REDIRECT_TO_ADMIN_SCREEN;
            status.setComplete();
        } else if (result.hasErrors()) {
            viewName = "defineMandatoryHiddenFields";
        } else {

            MandatoryHiddenFieldsDto dto = assembleDto(bean);
            adminServiceFacade.updateHiddenMandatoryFields(dto);
            status.setComplete();
        }

        return viewName;
    }

    private MandatoryHiddenFieldsDto assembleDto(DefineMandatoryHiddenFieldsFormBean bean) {

        MandatoryHiddenFieldsDto dto = new MandatoryHiddenFieldsDto();

        dto.setFamilyDetailsRequired(bean.isFamilyDetailsRequired());

        dto.setHideClientBusinessWorkActivities(bean.isHideClientBusinessWorkActivities());
        dto.setHideClientGovtId(bean.isHideClientGovtId());
        dto.setHideClientMiddleName(bean.isHideClientMiddleName());
        dto.setHideClientPhone(bean.isHideClientPhone());
        dto.setHideClientPovertyStatus(bean.isHideClientPovertyStatus());
        dto.setHideClientSecondLastName(bean.isHideClientSecondLastName());
        dto.setHideClientSpouseFatherInformation(bean.isHideClientSpouseFatherInformation());
        dto.setHideClientSpouseFatherMiddleName(bean.isHideClientSpouseFatherMiddleName());
        dto.setHideClientSpouseFatherSecondLastName(bean.isHideClientSpouseFatherSecondLastName());
        dto.setHideClientTrained(bean.isHideClientTrained());

        dto.setHideGroupTrained(bean.isHideGroupTrained());

        dto.setHideSystemAddress2(bean.isHideSystemAddress2());
        dto.setHideSystemAddress3(bean.isHideSystemAddress3());
        dto.setHideSystemAssignClientPostions(bean.isHideSystemAssignClientPostions());
        dto.setHideSystemCitizenShip(bean.isHideSystemCitizenShip());
        dto.setHideSystemCity(bean.isHideSystemCity());
        dto.setHideSystemCollateralTypeNotes(bean.isHideSystemCollateralTypeNotes());
        dto.setHideSystemCountry(bean.isHideSystemCountry());
        dto.setHideSystemEducationLevel(bean.isHideSystemEducationLevel());
        dto.setHideSystemEthnicity(bean.isHideSystemEthnicity());
        dto.setHideSystemExternalId(bean.isHideSystemExternalId());
        dto.setHideSystemHandicapped(bean.isHideSystemHandicapped());
        dto.setHideSystemPhoto(bean.isHideSystemPhoto());
        dto.setHideSystemPostalCode(bean.isHideSystemPostalCode());
        dto.setHideSystemReceiptIdDate(bean.isHideSystemReceiptIdDate());
        dto.setHideSystemState(bean.isHideSystemState());

        dto.setMandatoryClientBusinessWorkActivities(bean.isMandatoryClientBusinessWorkActivities());
        dto.setMandatoryClientGovtId(bean.isMandatoryClientGovtId());
        dto.setMandatoryClientMiddleName(bean.isMandatoryClientMiddleName());
        dto.setMandatoryClientPhone(bean.isMandatoryClientPhone());
        dto.setMandatoryClientPovertyStatus(bean.isMandatoryClientPovertyStatus());
        dto.setMandatoryMaritalStatus(bean.isMandatoryMaritalStatus());
        dto.setMandatoryClientFamilyDetails(bean.isMandatoryClientFamilyDetails());

        dto.setMandatoryClientSecondLastName(bean.isMandatoryClientSecondLastName());
        dto.setMandatoryClientSpouseFatherInformation(bean.isMandatoryClientSpouseFatherInformation());
        dto.setMandatoryClientSpouseFatherSecondLastName(bean.isMandatoryClientSpouseFatherSecondLastName());
        dto.setMandatoryClientTrained(bean.isMandatoryClientTrained());
        dto.setMandatoryClientTrainedOn(bean.isMandatoryClientTrainedOn());
        dto.setMandatoryNumberOfChildren(bean.isMandatoryNumberOfChildren());

        dto.setMandatorySystemAddress1(bean.isMandatorySystemAddress1());
        dto.setMandatorySystemCitizenShip(bean.isMandatorySystemCitizenShip());
        dto.setMandatorySystemEducationLevel(bean.isMandatorySystemEducationLevel());
        dto.setMandatorySystemEthnicity(bean.isMandatorySystemEthnicity());
        dto.setMandatorySystemExternalId(bean.isMandatorySystemExternalId());
        dto.setMandatorySystemHandicapped(bean.isMandatorySystemHandicapped());
        dto.setMandatorySystemPhoto(bean.isMandatorySystemPhoto());

        dto.setMandatoryLoanAccountPurpose(bean.isMandatoryLoanAccountPurpose());
        dto.setMandatoryLoanSourceOfFund(bean.isMandatoryLoanSourceOfFund());

        return dto;
    }
}