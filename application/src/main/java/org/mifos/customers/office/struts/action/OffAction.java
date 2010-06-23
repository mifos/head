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

package org.mifos.customers.office.struts.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldDto;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficeCustomFieldEntity;
import org.mifos.customers.office.business.OfficeDetailsDto;
import org.mifos.customers.office.business.service.OfficeBusinessService;
import org.mifos.customers.office.exceptions.OfficeException;
import org.mifos.customers.office.struts.OfficeUpdateRequest;
import org.mifos.customers.office.struts.actionforms.OffActionForm;
import org.mifos.customers.office.util.helpers.OfficeConstants;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.office.util.helpers.OfficeStatus;
import org.mifos.customers.office.util.helpers.OperationMode;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.authorization.HierarchyManager;
import org.mifos.security.util.UserContext;

public class OffAction extends BaseAction {

    @Override
    protected BusinessService getService() throws ServiceException {
        return new OfficeBusinessService();
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(@SuppressWarnings("unused") String method) {
        return true;
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        OffActionForm actionForm = (OffActionForm) form;
        actionForm.clear();
        loadParents(request, actionForm);
        loadCreateCustomFields(actionForm, request);
        loadofficeLevels(request);
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadParent(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        OffActionForm offActionForm = (OffActionForm) form;
        loadParents(request, offActionForm);
        if (offActionForm.getInput() != null && offActionForm.getInput().equals("edit")) {
            return mapping.findForward(ActionForwards.edit_success.toString());
        }

        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        return mapping.findForward(ActionForwards.previous_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        OffActionForm offActionForm = (OffActionForm) form;
        OfficeLevel level = OfficeLevel.getOfficeLevel(getShortValue(offActionForm.getOfficeLevel()));
        OfficeBO parentOffice = ((OfficeBusinessService) getService()).getOffice(getShortValue(offActionForm
                .getParentOfficeId()));

        OfficeBO officeBO = new OfficeBO(getUserContext(request), level, parentOffice, offActionForm.getCustomFields(),
                offActionForm.getOfficeName(), offActionForm.getShortName(), offActionForm.getAddress(),
                OperationMode.REMOTE_SERVER);
        StaticHibernateUtil.flushAndCloseSession();
        officeBO.save();

        offActionForm.setOfficeId(officeBO.getOfficeId().toString());
        offActionForm.setGlobalOfficeNum(officeBO.getGlobalOfficeNum());

        return mapping.findForward(ActionForwards.create_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward getAllOffices(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request
                .getSession());
        List<OfficeBO> officeList = getOffices(userContext, ((OfficeBusinessService) getService())
                .getOfficesTillBranchOffice());
        SessionUtils.setCollectionAttribute(OfficeConstants.GET_HEADOFFICE, getOffice(officeList,
                OfficeLevel.HEADOFFICE), request);
        SessionUtils.setCollectionAttribute(OfficeConstants.GET_REGIONALOFFICE, getOffice(officeList,
                OfficeLevel.REGIONALOFFICE), request);
        SessionUtils.setCollectionAttribute(OfficeConstants.GET_SUBREGIONALOFFICE, getOffice(officeList,
                OfficeLevel.SUBREGIONALOFFICE), request);
        SessionUtils.setCollectionAttribute(OfficeConstants.GET_AREAOFFICE, getOffice(officeList,
                OfficeLevel.AREAOFFICE), request);
        SessionUtils.setCollectionAttribute(OfficeConstants.GET_BRANCHOFFICE, getOffices(userContext,
                ((OfficeBusinessService) getService()).getBranchOffices()), request);
        loadofficeLevels(request);
        return mapping.findForward(ActionForwards.search_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute("methodCalled");
        return mapping.findForward(method + "_failure");
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        OffActionForm actionForm = (OffActionForm) form;
        OfficeBO officeBO = null;
        if (StringUtils.isBlank(actionForm.getOfficeId())) {
            throw new OfficeException(OfficeConstants.KEYGETFAILED);
        }
        officeBO = ((OfficeBusinessService) getService()).getOffice(Short.valueOf(actionForm.getOfficeId()));
        actionForm.clear();
        loadCustomFieldDefinitions(request);
        officeBO.getStatus().setLocaleId(getUserContext(request).getLocaleId());
        officeBO.getLevel().setLocaleId(getUserContext(request).getLocaleId());
        actionForm.populate(officeBO);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, officeBO, request);
        return mapping.findForward(ActionForwards.get_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        OffActionForm offActionForm = (OffActionForm) form;

        OfficeBO sessionOffice = (OfficeBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);

        OfficeBO office = ((OfficeBusinessService) getService()).getOffice(sessionOffice.getOfficeId());

        checkVersionMismatch(sessionOffice.getVersionNo(), office.getVersionNo());
        office.setVersionNo(sessionOffice.getVersionNo());
        office.setUserContext(getUserContext(request));

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, office, request);

        loadCustomFieldDefinitions(request);
        loadofficeLevels(request);
        loadParents(request, offActionForm);
        offActionForm.setCustomFields(createCustomFieldViews(office.getCustomFields(), request));
        loadOfficeStatus(request);

        return mapping.findForward(ActionForwards.edit_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editpreview(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.editpreview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editprevious(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.editprevious_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        OffActionForm offActionForm = (OffActionForm) form;
        ActionForward forward = null;
        OfficeBO sessionOffice = (OfficeBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);

        UserContext userContext = getUserContext(request);
        Short officeId = sessionOffice.getOfficeId();
        Integer versionNum = sessionOffice.getVersionNo();
        OfficeUpdateRequest officeUpdateRequest = officeUpdateRequestFrom(offActionForm);

        boolean isParentOfficeChanged = this.officeServiceFacade.updateOffice(userContext, officeId, versionNum, officeUpdateRequest);

        if (isParentOfficeChanged) {
            forward = mapping.findForward(ActionForwards.update_cache_success.toString());
        } else {
            forward = mapping.findForward(ActionForwards.update_success.toString());
        }

        return forward;
    }

    private OfficeUpdateRequest officeUpdateRequestFrom(OffActionForm offActionForm) throws OfficeException {
        OfficeStatus newStatus = null;
        if (getShortValue(offActionForm.getOfficeStatus()) != null) {
            newStatus = OfficeStatus.getOfficeStatus(getShortValue(offActionForm.getOfficeStatus()));
        }
        OfficeLevel newlevel = OfficeLevel.getOfficeLevel(getShortValue(offActionForm.getOfficeLevel()));

        Short parentOfficeId = getShortValue(offActionForm.getParentOfficeId());
        return new OfficeUpdateRequest(offActionForm.getOfficeName(), offActionForm.getShortName(), newStatus, newlevel, parentOfficeId, offActionForm.getAddress(), offActionForm.getCustomFields());
    }

    public ActionForward updateCache(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        HierarchyManager.getInstance().init();
        return mapping.findForward(ActionForwards.update_success.toString());
    }

    @SuppressWarnings({"unchecked"})
    private List<CustomFieldDto> createCustomFieldViews(final Set<OfficeCustomFieldEntity> customFieldEntities,
                                                        final HttpServletRequest request) throws PageExpiredException {
        List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();

        List<CustomFieldDefinitionEntity> customFieldDefs = getCustomFieldDefinitionsFromSession(request);
        Locale locale = getUserContext(request).getPreferredLocale();
        if (customFieldEntities != null) {
            for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
                for (OfficeCustomFieldEntity customFieldEntity : customFieldEntities) {
                    if (customFieldDef.getFieldId().equals(customFieldEntity.getFieldId())) {
                        if (customFieldDef.getFieldType().equals(CustomFieldType.DATE.getValue())) {
                            customFields.add(new CustomFieldDto(customFieldEntity.getFieldId(), DateUtils
                                    .getUserLocaleDate(locale, customFieldEntity.getFieldValue()), customFieldDef
                                    .getFieldType()));
                        } else {
                            customFields.add(new CustomFieldDto(customFieldEntity.getFieldId(), customFieldEntity
                                    .getFieldValue(), customFieldDef.getFieldType()));
                        }
                    }
                }
            }
        }
        return customFields;
    }

    private List<OfficeBO> getOffice(List<OfficeBO> officeList, OfficeLevel officeLevel) throws Exception {
        if (officeList != null) {
            List<OfficeBO> newOfficeList = new ArrayList<OfficeBO>();
            for (OfficeBO officeBO : officeList) {
                if (officeBO.getOfficeLevel().equals(officeLevel)) {
                    newOfficeList.add(officeBO);
                }
            }
            if (newOfficeList.isEmpty()) {
                return null;
            }
            return newOfficeList;
        }
        return null;
    }

    private List<OfficeBO> getOffices(UserContext userContext, List<OfficeBO> officeList) throws Exception {
        if (officeList != null) {
            for (OfficeBO officeBO : officeList) {
                officeBO.getLevel().setLocaleId(userContext.getLocaleId());
                officeBO.getStatus().setLocaleId(userContext.getLocaleId());
            }
        }
        return officeList;
    }

    private void loadParents(HttpServletRequest request, OffActionForm form) throws Exception {
        String officeLevel = request.getParameter("officeLevel");
        if (StringUtils.isNotBlank(officeLevel)) {
            form.setOfficeLevel(officeLevel);
            OfficeLevel Level = OfficeLevel.getOfficeLevel(Short.valueOf(officeLevel));

            List<OfficeDetailsDto> parents = ((OfficeBusinessService) getService()).getActiveParents(Level, getUserContext(
                    request).getLocaleId());
            OfficeBO office = (OfficeBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);

            if (form.getInput() != null && form.getInput().equals("edit") && office != null) {
                for (int i = 0; i < parents.size(); i++) {
                    OfficeDetailsDto view = parents.get(i);
                    if (view.getOfficeId().equals(office.getOfficeId())) {
                        parents.remove(view);
                    }
                }
            }
            SessionUtils.setCollectionAttribute(OfficeConstants.PARENTS, parents, request);
        }
    }

    private void loadCreateCustomFields(OffActionForm actionForm, HttpServletRequest request) throws Exception {
        loadCustomFieldDefinitions(request);
        // Set Default values for custom fields
        List<CustomFieldDefinitionEntity> customFieldDefs = getCustomFieldDefinitionsFromSession(request);
        List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();

        for (CustomFieldDefinitionEntity fieldDef : customFieldDefs) {
            if (StringUtils.isNotBlank(fieldDef.getDefaultValue())
                    && fieldDef.getFieldType().equals(CustomFieldType.DATE.getValue())) {
                customFields.add(new CustomFieldDto(fieldDef.getFieldId(), DateUtils.getUserLocaleDate(getUserContext(
                        request).getPreferredLocale(), fieldDef.getDefaultValue()), fieldDef.getFieldType()));
            } else {
                customFields.add(new CustomFieldDto(fieldDef.getFieldId(), fieldDef.getDefaultValue(), fieldDef
                        .getFieldType()));
            }
        }
        actionForm.setCustomFields(customFields);
    }

    @SuppressWarnings("unchecked")
    private List<CustomFieldDefinitionEntity> getCustomFieldDefinitionsFromSession(HttpServletRequest request)
            throws PageExpiredException {
        return (List<CustomFieldDefinitionEntity>) SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
    }

    private void loadCustomFieldDefinitions(HttpServletRequest request) throws Exception {
        MasterDataService masterDataService = new MasterDataService();
        List<CustomFieldDefinitionEntity> customFieldDefs = masterDataService.retrieveCustomFieldsDefinition(EntityType.OFFICE);
        SessionUtils.setCollectionAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, customFieldDefs, request);
    }

    private void loadofficeLevels(HttpServletRequest request) throws Exception {
        SessionUtils.setCollectionAttribute(OfficeConstants.OFFICELEVELLIST, ((OfficeBusinessService) getService())
                .getConfiguredLevels(getUserContext(request).getLocaleId()), request);
    }

    private void loadOfficeStatus(HttpServletRequest request) throws Exception {
        SessionUtils.setCollectionAttribute(OfficeConstants.OFFICESTATUSLIST, ((OfficeBusinessService) getService())
                .getStatusList(getUserContext(request).getLocaleId()), request);

    }
}