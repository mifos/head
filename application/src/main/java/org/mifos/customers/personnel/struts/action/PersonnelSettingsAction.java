/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

import java.sql.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.service.PersonnelBusinessService;
import org.mifos.customers.personnel.struts.actionforms.PersonnelSettingsActionForm;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.config.Localization;
import org.mifos.application.master.business.SupportedLocalesEntity;

public class PersonnelSettingsAction extends BaseAction {
    @Override
    protected BusinessService getService() {
        return ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Personnel);
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("yourSettings");
        security.allow("get", SecurityConstants.VIEW);
        security.allow("manage", SecurityConstants.PERSONNEL_EDIT_SELF_INFO);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.PERSONNEL_EDIT_SELF_INFO);
        security.allow("loadChangePassword", SecurityConstants.PERSONNEL_EDIT_SELF_INFO);
        return security;
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        PersonnelBO personnel = getPersonnel(getStringValue(getUserContext(request).getId()));
        SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, personnel, request);
        setDetailsData(request, getUserContext(request).getLocaleId(), personnel.getPersonnelDetails().getGender(),
                personnel.getPersonnelDetails().getMaritalStatus(),
                personnel.getPreferredLocale().getLanguage().getLookUpValue().getLookUpId());
        loadMasterData(request, getUserContext(request).getLocaleId());
        setPersonnelAge(request, (Date) personnel.getPersonnelDetails().getDob());
        return mapping.findForward(ActionForwards.get_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward manage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        PersonnelBO personnel = (PersonnelBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        setFormAttributes((PersonnelSettingsActionForm) form, personnel);
        return mapping.findForward(ActionForwards.manage_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        PersonnelSettingsActionForm personnelactionForm = (PersonnelSettingsActionForm) form;
        Integer prefeeredLocaleId = null;
        if (personnelactionForm.getPreferredLocaleValue() != null)
            prefeeredLocaleId = personnelactionForm.getPreferredLocaleValue().intValue();
        setDetailsData(request, getUserContext(request).getLocaleId(), personnelactionForm.getGenderValue(),
                personnelactionForm.getMaritalStatusValue(), prefeeredLocaleId);
        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.previous_success.toString());
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        PersonnelSettingsActionForm personnelSettingsActionForm = (PersonnelSettingsActionForm) form;
        PersonnelBO personnel = (PersonnelBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        PersonnelBO personnelInit = ((PersonnelBusinessService) getService()).getPersonnel(personnel.getPersonnelId());
        checkVersionMismatch(personnel.getVersionNo(), personnelInit.getVersionNo());
        personnelInit.setVersionNo(personnel.getVersionNo());
        personnelInit.setUserContext(getUserContext(request));
        setInitialObjectForAuditLogging(personnelInit);
        personnelInit.update(personnelSettingsActionForm.getEmailId(), personnelSettingsActionForm.getName(),
                personnelSettingsActionForm.getMaritalStatusValue(), personnelSettingsActionForm.getGenderValue(),
                personnelSettingsActionForm.getAddress(), getLocaleId(personnelSettingsActionForm
                        .getPreferredLocaleValue()), getUserContext(request).getId());
        return mapping.findForward(ActionForwards.updateSettings_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.cancel_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadChangePassword(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.loadChangePassword_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse httpservletresponse) throws Exception {
        String method = (String) request.getAttribute("methodCalled");
        if (method.equalsIgnoreCase(Methods.preview.toString())) {
            return mapping.findForward(ActionForwards.editPersonalInfo_failure.toString());
        }
        if (method.equalsIgnoreCase(Methods.update.toString())) {
            return mapping.findForward(ActionForwards.previewPersonalInfo_success.toString());
        }
        return null;
    }

    private PersonnelBO getPersonnel(String personnelId) throws Exception {
        return (((PersonnelBusinessService) getService()).getPersonnel(getShortValue(personnelId)));
    }

    private void setPersonnelAge(HttpServletRequest request, Date date) throws PageExpiredException {
        SessionUtils.removeAttribute(PersonnelConstants.PERSONNEL_AGE, request);
        int age = DateUtils.DateDiffInYears(date);
        if (age < 0) {
            age = 0;
        }
        SessionUtils.setAttribute(PersonnelConstants.PERSONNEL_AGE, age, request);
    }

    private String getNameForBusinessActivityEntity(Integer entityId, Short localeId) throws Exception {
        if (entityId != null) {
            return ((MasterDataService) ServiceFactory.getInstance().getBusinessService(
                    BusinessServiceName.MasterDataService)).retrieveMasterEntities(entityId, localeId);
        }
        return "";
    }

    private void setFormAttributes(PersonnelSettingsActionForm form, PersonnelBO personnelBO) throws Exception {
        form.setFirstName(personnelBO.getPersonnelDetails().getName().getFirstName());
        form.setMiddleName(personnelBO.getPersonnelDetails().getName().getMiddleName());
        form.setSecondLastName(personnelBO.getPersonnelDetails().getName().getSecondLastName());
        form.setLastName(personnelBO.getPersonnelDetails().getName().getLastName());
        form.setGender(getStringValue(personnelBO.getPersonnelDetails().getGender()));
        form.setUserName(personnelBO.getUserName());
        form.setEmailId(personnelBO.getEmailId());
        form.setGovernmentIdNumber(personnelBO.getPersonnelDetails().getGovernmentIdNumber());
        form.setAddress(personnelBO.getPersonnelDetails().getAddress());
        form.setDob(personnelBO.getPersonnelDetails().getDob().toString());
        form.setPreferredLocale(getStringValue(personnelBO.getPreferredLocale().getLanguage().getLookUpValue().getLookUpId()));
        form.setMaritalStatus(getStringValue(personnelBO.getPersonnelDetails().getMaritalStatus()));
    }

    private void loadMasterData(HttpServletRequest request, Short localeId) throws Exception {
        MasterPersistence masterPersistence = new MasterPersistence();
        SessionUtils.setCollectionAttribute(PersonnelConstants.GENDER_LIST, masterPersistence.retrieveMasterEntities(
                MasterConstants.GENDER, localeId), request);

        SessionUtils.setCollectionAttribute(PersonnelConstants.MARITAL_STATUS_LIST, masterPersistence
                .retrieveMasterEntities(MasterConstants.MARITAL_STATUS, localeId), request);

        SessionUtils.setCollectionAttribute(PersonnelConstants.LANGUAGE_LIST, masterPersistence.retrieveMasterEntities(
                MasterConstants.LANGUAGE, localeId), request);
    }

    private void setDetailsData(HttpServletRequest request, Short localeId, Integer gender, Integer maritalStatus,
            Integer preferredLocale) throws Exception {
        SessionUtils.setAttribute(PersonnelConstants.GENDER, getNameForBusinessActivityEntity(gender, localeId),
                request);
        SessionUtils.setAttribute(PersonnelConstants.MARITALSTATUS, getNameForBusinessActivityEntity(maritalStatus,
                localeId), request);
        SessionUtils.setAttribute(MasterConstants.LANGUAGE,
                getNameForBusinessActivityEntity(preferredLocale, localeId), request);
    }

    private Short getLocaleId(Short lookUpId) throws ServiceException {
        if (lookUpId != null) {
            for (SupportedLocalesEntity locale : ((PersonnelBusinessService) getService()).getAllLocales()) {
                if (locale.getLanguage().getLookUpValue().getLookUpId() == lookUpId.intValue()) {
                    return locale.getLocaleId();
                }
            }
        }

        return Localization.getInstance().getLocaleId();
    }
}
