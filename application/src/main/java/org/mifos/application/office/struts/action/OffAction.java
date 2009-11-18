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

package org.mifos.application.office.struts.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficeCustomFieldEntity;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.application.office.struts.actionforms.OffActionForm;
import org.mifos.application.office.util.helpers.OfficeConstants;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.helpers.OfficeStatus;
import org.mifos.application.office.util.helpers.OperationMode;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class OffAction extends BaseAction {

    @Override
    protected BusinessService getService() throws ServiceException {
        return ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Office);
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        OffActionForm actionForm = (OffActionForm) form;
        actionForm.clear();
        loadParents(request, actionForm);
        loadCreateCustomFields(actionForm, request);
        loadofficeLevels(request);
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadParent(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        OffActionForm offActionForm = (OffActionForm) form;
        loadParents(request, offActionForm);
        if (offActionForm.getInput() != null && offActionForm.getInput().equals("edit"))
            return mapping.findForward(ActionForwards.edit_success.toString());
        else
            return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        return mapping.findForward(ActionForwards.previous_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
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
    public ActionForward getAllOffices(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

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
    public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute("methodCalled");
        return mapping.findForward(method + "_failure");
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        OffActionForm actionForm = (OffActionForm) form;
        OfficeBO officeBO = null;
        if (StringUtils.isBlank(actionForm.getOfficeId()))
            throw new OfficeException(OfficeConstants.KEYGETFAILED);
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
            HttpServletResponse response) throws Exception {
        OffActionForm offActionForm = (OffActionForm) form;
        OfficeBO sessionOffice = (OfficeBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        OfficeBO office = ((OfficeBusinessService) getService()).getOffice(sessionOffice.getOfficeId());
        checkVersionMismatch(sessionOffice.getVersionNo(), office.getVersionNo());
        office.setVersionNo(sessionOffice.getVersionNo());
        office.setUserContext(getUserContext(request));
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, office, request);
        loadofficeLevels(request);
        loadParents(request, offActionForm);
        loadEditCustomFields(request, offActionForm);
        loadOfficeStatus(request);
        return mapping.findForward(ActionForwards.edit_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editpreview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.editpreview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editprevious(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.editprevious_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        OffActionForm offActionForm = (OffActionForm) form;
        ActionForward forward = null;
        OfficeBO sessionOffice = (OfficeBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        OfficeBO office = ((OfficeBusinessService) getService()).getOffice(Short.valueOf(sessionOffice.getOfficeId()));
        checkVersionMismatch(sessionOffice.getVersionNo(), office.getVersionNo());
        office.setVersionNo(sessionOffice.getVersionNo());
        office.setUserContext(getUserContext(request));
        OfficeStatus newStatus = null;
        if (getShortValue(offActionForm.getOfficeStatus()) != null)
            newStatus = OfficeStatus.getOfficeStatus(getShortValue(offActionForm.getOfficeStatus()));
        OfficeLevel newlevel = OfficeLevel.getOfficeLevel(getShortValue(offActionForm.getOfficeLevel()));
        OfficeBO parentOffice = null;
        if (getShortValue(offActionForm.getParentOfficeId()) != null)
            parentOffice = ((OfficeBusinessService) getService()).getOffice(getShortValue(offActionForm
                    .getParentOfficeId()));

        if (parentOffice != null && !office.getParentOffice().getOfficeId().equals(parentOffice.getOfficeId()))
            forward = mapping.findForward(ActionForwards.update_cache_success.toString());
        else
            forward = mapping.findForward(ActionForwards.update_success.toString());

        office.update(offActionForm.getOfficeName(), offActionForm.getShortName(), newStatus, newlevel, parentOffice,
                offActionForm.getAddress(), offActionForm.getCustomFields());
        return forward;
    }

    public ActionForward updateCache(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        HierarchyManager.getInstance().init();
        return mapping.findForward(ActionForwards.update_success.toString());
    }

    private void loadEditCustomFields(HttpServletRequest request, OffActionForm offActionForm) throws Exception {
        // get the office
        OfficeBO office = (OfficeBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        List<CustomFieldView> customfields = new ArrayList<CustomFieldView>();
        if (office.getCustomFields() != null && office.getCustomFields().size() > 0)
            for (OfficeCustomFieldEntity customField : office.getCustomFields()) {
                customfields.add(new CustomFieldView(customField.getFieldId(), customField.getFieldValue(),
                        CustomFieldType.NONE));
            }
        offActionForm.setCustomFields(customfields);
    }

    private List<OfficeBO> getOffice(List<OfficeBO> officeList, OfficeLevel officeLevel) throws Exception {
        if (officeList != null) {
            List<OfficeBO> newOfficeList = new ArrayList<OfficeBO>();
            for (OfficeBO officeBO : officeList) {
                if (officeBO.getOfficeLevel().equals(officeLevel)) {
                    newOfficeList.add(officeBO);
                }
            }
            if (newOfficeList.isEmpty())
                return null;
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

            List<OfficeView> parents = ((OfficeBusinessService) getService()).getActiveParents(Level, getUserContext(
                    request).getLocaleId());
            OfficeBO office = (OfficeBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);

            if (form.getInput() != null && form.getInput().equals("edit") && office != null)

                for (int i = 0; i < parents.size(); i++) {
                    OfficeView view = parents.get(i);
                    if (view.getOfficeId().equals(office.getOfficeId())) {
                        parents.remove(view);
                    }
                }
            SessionUtils.setCollectionAttribute(OfficeConstants.PARENTS, parents, request);
        }
    }

    private void loadCreateCustomFields(OffActionForm actionForm, HttpServletRequest request) throws Exception {
        loadCustomFieldDefinitions(request);
        // Set Default values for custom fields
        List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
                .getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
        List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();

        for (CustomFieldDefinitionEntity fieldDef : customFieldDefs) {
            if (StringUtils.isNotBlank(fieldDef.getDefaultValue())
                    && fieldDef.getFieldType().equals(CustomFieldType.DATE.getValue())) {
                customFields.add(new CustomFieldView(fieldDef.getFieldId(), DateUtils.getUserLocaleDate(getUserContext(
                        request).getPreferredLocale(), fieldDef.getDefaultValue()), fieldDef.getFieldType()));
            } else {
                customFields.add(new CustomFieldView(fieldDef.getFieldId(), fieldDef.getDefaultValue(), fieldDef
                        .getFieldType()));
            }
        }
        actionForm.setCustomFields(customFields);
    }

    private void loadCustomFieldDefinitions(HttpServletRequest request) throws Exception {
        MasterDataService masterDataService = (MasterDataService) ServiceFactory.getInstance().getBusinessService(
                BusinessServiceName.MasterDataService);
        List<CustomFieldDefinitionEntity> customFieldDefs = masterDataService
                .retrieveCustomFieldsDefinition(EntityType.OFFICE);
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
