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

import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.dto.domain.AccountStatusesLabelDto;
import org.mifos.dto.domain.ConfigurableLookupLabelDto;
import org.mifos.dto.domain.GracePeriodDto;
import org.mifos.dto.domain.OfficeLevelDto;
import org.mifos.dto.screen.ConfigureApplicationLabelsDto;
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
@RequestMapping("/defineLabels")
@SessionAttributes("formBean")
public class ConfigureApplicationLabelsController {

    private static final String FORM_VIEW = "defineLabels";
    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    protected ConfigureApplicationLabelsController() {
        // default contructor for spring autowiring
    }

    public ConfigureApplicationLabelsController(final AdminServiceFacade adminServiceFacade) {
        this.adminServiceFacade = adminServiceFacade;
    }

    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink(FORM_VIEW, "defineLabels.ftl").build();
    }

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("formBean")
    public ConfigureApplicationLabelsFormBean showPopulatedForm() {
        ConfigureApplicationLabelsDto appLabels = adminServiceFacade.retrieveConfigurableLabels();
        ConfigureApplicationLabelsFormBean formBean = new ConfigureApplicationLabelsFormBean();

        populateOfficeHierarchyLabels(formBean, appLabels.getOfficeLevels());
        populateClientLabels(formBean, appLabels.getLookupLabels());
        populateProductTypeLabels(formBean, appLabels.getLookupLabels());
        populatePersonalInfoLabels(formBean, appLabels.getLookupLabels());
        populateAddressLabels(formBean, appLabels.getLookupLabels());
        populateAccountStatusesLabels(formBean, appLabels.getAccountStatusLabels());
        populateGracePeriodLabels(formBean, appLabels.getGracePeriodDto());
        populateMiscLabels(formBean, appLabels.getLookupLabels());
        return formBean;
    }

    private void populateAccountStatusesLabels(ConfigureApplicationLabelsFormBean formBean, AccountStatusesLabelDto statusLabels) {
        formBean.setPartialApplication(statusLabels.getPartialApplication());
        formBean.setPendingApproval(statusLabels.getPendingApproval());
        formBean.setApproved(statusLabels.getApproved());
        formBean.setCancel(statusLabels.getCancel());
        formBean.setClosed(statusLabels.getClosed());
        formBean.setOnhold(statusLabels.getOnhold());
        formBean.setActive(statusLabels.getActive());
        formBean.setInActive(statusLabels.getInActive());
        formBean.setActiveInBadStanding(statusLabels.getActiveInBadStanding());
        formBean.setActiveInGoodStanding(statusLabels.getActiveInGoodStanding());
        formBean.setClosedObligationMet(statusLabels.getClosedObligationMet());
        formBean.setClosedRescheduled(statusLabels.getClosedRescheduled());
        formBean.setClosedWrittenOff(statusLabels.getClosedWrittenOff());
    }

    private void populateClientLabels(ConfigureApplicationLabelsFormBean formBean, ConfigurableLookupLabelDto lookupLabels) {
        formBean.setClient(lookupLabels.getClient());
        formBean.setGroup(lookupLabels.getGroup());
        formBean.setCenter(lookupLabels.getCenter());
    }

    private void populateProductTypeLabels(ConfigureApplicationLabelsFormBean formBean, ConfigurableLookupLabelDto lookupLabels) {
        formBean.setLoans(lookupLabels.getLoans());
        formBean.setSavings(lookupLabels.getSavings());
    }

    private void populatePersonalInfoLabels(ConfigureApplicationLabelsFormBean formBean, ConfigurableLookupLabelDto lookupLabels) {
        formBean.setState(lookupLabels.getState());
        formBean.setPostalCode(lookupLabels.getPostalCode());
        formBean.setEthnicity(lookupLabels.getEthnicity());
        formBean.setCitizenship(lookupLabels.getCitizenship());
        formBean.setHandicapped(lookupLabels.getHandicapped());
        formBean.setGovtId(lookupLabels.getGovtId());
    }

    private void populateAddressLabels(ConfigureApplicationLabelsFormBean formBean, ConfigurableLookupLabelDto lookupLabels) {
        formBean.setAddress1(lookupLabels.getAddress1());
        formBean.setAddress2(lookupLabels.getAddress2());
        formBean.setAddress3(lookupLabels.getAddress3());
    }

    private void populateMiscLabels(ConfigureApplicationLabelsFormBean formBean, ConfigurableLookupLabelDto lookupLabels) {
        formBean.setInterest(lookupLabels.getInterest());
        formBean.setExternalId(lookupLabels.getExternalId());
        formBean.setBulkEntry(lookupLabels.getBulkEntry());
    }

    private void populateGracePeriodLabels(ConfigureApplicationLabelsFormBean formBean, GracePeriodDto gracePeriodDto) {
        formBean.setNone(gracePeriodDto.getNone());
        formBean.setGraceOnAllRepayments(gracePeriodDto.getGraceOnAllRepayments());
        formBean.setPrincipalOnlyGrace(gracePeriodDto.getPrincipalOnlyGrace());
    }

    private void populateOfficeHierarchyLabels(ConfigureApplicationLabelsFormBean formBean, OfficeLevelDto officeLevels) {
        formBean.setHeadOffice(officeLevels.getHeadOfficeNameKey());
        formBean.setRegionalOffice(officeLevels.getRegionalOfficeNameKey());
        formBean.setSubRegionalOffice(officeLevels.getSubRegionalOfficeNameKey());
        formBean.setAreaOffice(officeLevels.getAreaOfficeNameKey());
        formBean.setBranchOffice(officeLevels.getBranchOfficeNameKey());
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
                                    @ModelAttribute("formBean") ConfigureApplicationLabelsFormBean formBean,
                                    BindingResult result,
                                    SessionStatus status) {
        String viewName = REDIRECT_TO_ADMIN_SCREEN;

        if (StringUtils.isNotBlank(cancel)) {
            viewName = REDIRECT_TO_ADMIN_SCREEN;
            status.setComplete();
        } else if (result.hasErrors()) {
            viewName = FORM_VIEW;
        } else {
            try {
//                UpdateConfiguredOfficeLevelRequest updateRequest = new UpdateConfiguredOfficeLevelRequest(formBean
//                        .isSubRegionalOffice(), formBean.isRegionalOffice(), formBean.isAreaOffice());
//                adminServiceFacade.updateOfficeLevelHierarchies(updateRequest);
                status.setComplete();
            } catch (BusinessRuleException e) {
                result.reject(e.getMessageKey(), "The update to office levels was not successful.");
                viewName = FORM_VIEW;
            }
        }
        return viewName;
    }
}