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

package org.mifos.customers.personnel.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.config.Localization;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.struts.actionforms.PersonnelSettingsActionForm;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.UserDetailDto;
import org.mifos.dto.screen.UserSettingsDto;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.MifosUser;
import org.mifos.ui.core.controller.util.helpers.SitePreferenceHelper;
import org.springframework.security.core.context.SecurityContextHolder;

public class PersonnelSettingsAction extends BaseAction {

    @TransactionDemarcate(saveToken = true)
    public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        Short userId = getUserContext(request).getId();

        PersonnelBO personnel = this.personnelDao.findPersonnelById(userId);
        SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, personnel, request);

        UserSettingsDto userSettings = this.personnelServiceFacade.retrieveUserSettings();

        SessionUtils.setAttribute(PersonnelConstants.GENDER, userSettings.getGender(), request);
        SessionUtils.setAttribute(PersonnelConstants.MARITALSTATUS, userSettings.getMartialStatus(), request);
        SessionUtils.setAttribute(MasterConstants.LANGUAGE, userSettings.getLanguage(), request);
        SessionUtils.setAttribute(PersonnelConstants.SITE_TYPE_PREFERRED, userSettings.getSitePreference(), request);

        SessionUtils.setCollectionAttribute(PersonnelConstants.GENDER_LIST, userSettings.getGenders(), request);
        SessionUtils.setCollectionAttribute(PersonnelConstants.MARITAL_STATUS_LIST, userSettings.getMartialStatuses(), request);
        SessionUtils.setCollectionAttribute(PersonnelConstants.LANGUAGE_LIST, userSettings.getLanguages(), request);
        SessionUtils.setCollectionAttribute(PersonnelConstants.SITE_TYPES_LIST, userSettings.getSitePreferenceTypes(), request);
        
        SessionUtils.removeAttribute(PersonnelConstants.PERSONNEL_AGE, request);
        SessionUtils.setAttribute(PersonnelConstants.PERSONNEL_AGE, userSettings.getAge(), request);
        return mapping.findForward(ActionForwards.get_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward manage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        PersonnelBO personnel = (PersonnelBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);

        // FIXME - provide all details through userDetailDto
        UserDetailDto userInfo = this.personnelServiceFacade.retrieveUserInformation(personnel.getPersonnelId());

        PersonnelSettingsActionForm form1 = (PersonnelSettingsActionForm) form;
        form1.setFirstName(userInfo.getFirstName());
        form1.setMiddleName(personnel.getPersonnelDetails().getName().getMiddleName());
        form1.setSecondLastName(personnel.getPersonnelDetails().getName().getSecondLastName());
        form1.setLastName(userInfo.getLastName());
        form1.setGender(getStringValue(personnel.getPersonnelDetails().getGender()));
        form1.setUserName(personnel.getUserName());
        form1.setEmailId(personnel.getEmailId());
        form1.setGovernmentIdNumber(personnel.getPersonnelDetails().getGovernmentIdNumber());
        form1.setAddress(personnel.getPersonnelDetails().getAddress());
        form1.setDob(personnel.getPersonnelDetails().getDob().toString());
        form1.setPreferredLocale(Localization.getInstance().getDisplayName(personnel.getPreferredLocale()));
        form1.setPreferredSiteTypeId(personnel.getSitePreference());
        form1.setMaritalStatus(getStringValue(personnel.getPersonnelDetails().getMaritalStatus()));

        return mapping.findForward(ActionForwards.manage_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        PersonnelSettingsActionForm personnelactionForm = (PersonnelSettingsActionForm) form;
        Integer prefeeredLocaleId = null;
        if (personnelactionForm.getPreferredLocaleValue() != null) {
            prefeeredLocaleId = personnelactionForm.getPreferredLocaleValue().intValue();
        }

        UserSettingsDto userSettings = this.personnelServiceFacade.retrieveUserSettings(personnelactionForm.getGenderValue(),
                                                                                        personnelactionForm.getMaritalStatusValue(),
                                                                                        prefeeredLocaleId, personnelactionForm.getPreferredSiteTypeId());

        SessionUtils.setAttribute(PersonnelConstants.GENDER, userSettings.getGender(), request);
        SessionUtils.setAttribute(PersonnelConstants.MARITALSTATUS, userSettings.getMartialStatus(), request);
        SessionUtils.setAttribute(MasterConstants.LANGUAGE, userSettings.getLanguage(), request);
        SessionUtils.setAttribute(PersonnelConstants.SITE_TYPE_PREFERRED, userSettings.getSitePreference(), request);
        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.previous_success.toString());
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        PersonnelSettingsActionForm personnelSettingsActionForm = (PersonnelSettingsActionForm) form;

        PersonnelBO personnel = (PersonnelBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);

        AddressDto address = null;
        if (personnelSettingsActionForm.getAddress() != null) {
            address = Address.toDto(personnelSettingsActionForm.getAddress());
        }

        this.personnelServiceFacade.updateUserSettings(personnel.getPersonnelId(), personnelSettingsActionForm.getEmailId(), personnelSettingsActionForm.getName(),
                personnelSettingsActionForm.getMaritalStatusValue(), personnelSettingsActionForm.getGenderValue(),
                address, personnelSettingsActionForm.getPreferredLocaleValue(), personnelSettingsActionForm.getPreferredSiteTypeId());

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setPreferredLocaleId(personnelSettingsActionForm.getPreferredLocaleValue());
        
        (new SitePreferenceHelper()).setSitePreferenceCookie(personnelServiceFacade.retrieveSitePreference(user.getUserId()), response);
        
        return mapping.findForward(ActionForwards.updateSettings_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.cancel_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadChangePassword(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.loadChangePassword_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse httpservletresponse) throws Exception {
        String method = (String) request.getAttribute("methodCalled");
        if (method.equalsIgnoreCase(Methods.preview.toString())) {
            return mapping.findForward(ActionForwards.editPersonalInfo_failure.toString());
        }
        if (method.equalsIgnoreCase(Methods.update.toString())) {
            return mapping.findForward(ActionForwards.previewPersonalInfo_success.toString());
        }
        return null;
    }
}