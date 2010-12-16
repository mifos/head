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

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.questionnaire.struts.DefaultQuestionnaireServiceFacadeLocator;
import org.mifos.application.questionnaire.struts.QuestionnaireFlowAdapter;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficeCustomFieldEntity;
import org.mifos.customers.office.business.service.OfficeBusinessService;
import org.mifos.customers.office.exceptions.OfficeException;
import org.mifos.customers.office.struts.OfficeUpdateRequest;
import org.mifos.customers.office.struts.actionforms.OffActionForm;
import org.mifos.customers.office.util.helpers.OfficeConstants;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.office.util.helpers.OfficeStatus;
import org.mifos.customers.office.util.helpers.OperationMode;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.OfficeDetailsDto;
import org.mifos.dto.domain.OfficeDto;
import org.mifos.dto.screen.ListElement;
import org.mifos.dto.screen.OfficeFormDto;
import org.mifos.dto.screen.OfficeHierarchyByLevelDto;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.authorization.HierarchyManager;
import org.mifos.security.util.UserContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.mifos.accounts.loan.util.helpers.LoanConstants.METHODCALLED;

public class OffAction extends BaseAction {

    @Override
    protected BusinessService getService() throws ServiceException {
        return new OfficeBusinessService();
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        OffActionForm actionForm = (OffActionForm) form;
        actionForm.clear();
        String officeLevel = request.getParameter("officeLevel");
        Short officeLevelId = null;
        if (StringUtils.isNotBlank(officeLevel)) {
            officeLevelId = Short.valueOf(officeLevel);
        }

        OfficeFormDto officeFormDto = this.officeServiceFacade.retrieveOfficeFormInformation(officeLevelId);

        // loadParents
        if (StringUtils.isNotBlank(officeLevel)) {
            actionForm.setOfficeLevel(officeLevel);

            OfficeDto office = (OfficeDto) SessionUtils.getAttribute(OfficeConstants.OFFICE_DTO, request);

            List<OfficeDto> validParentOffices = new ArrayList<OfficeDto>();
            if (actionForm.getInput() != null && actionForm.getInput().equals("edit") && office != null) {
                for (OfficeDto view : officeFormDto.getParents()) {
                    if (!view.getId().equals(office.getOfficeId())) {
                        validParentOffices.add(view);
                    }
                }
            } else {
                validParentOffices.addAll(officeFormDto.getParents());
            }
            SessionUtils.setCollectionAttribute(OfficeConstants.PARENTS, validParentOffices, request);
        }

        actionForm.setCustomFields(new ArrayList<CustomFieldDto>());

        SessionUtils.setCollectionAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, new ArrayList<CustomFieldDto>(), request);
        SessionUtils.setCollectionAttribute(OfficeConstants.OFFICELEVELLIST, ((OfficeBusinessService) getService()).getConfiguredLevels(getUserContext(request).getLocaleId()), request);
        SessionUtils.setAttribute(CustomerConstants.URL_MAP, null, request.getSession(false));
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
    public ActionForward preview(ActionMapping mapping, ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        OffActionForm officeActionForm = (OffActionForm) form;
        return createGroupQuestionnaire.fetchAppliedQuestions(mapping, officeActionForm, request, ActionForwards.preview_success);
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
        OfficeDto officeDto = new OfficeDto(getShortValue(offActionForm.getOfficeLevel()), getShortValue(offActionForm.getParentOfficeId()),
                offActionForm.getCustomFields(), offActionForm.getOfficeName(), offActionForm.getShortName(),
                Address.toDto(offActionForm.getAddress()));
        UserContext userContext = getUserContext(request);
        ListElement element = officeServiceFacade.createOffice(userContext.getId(), userContext.getPreferredLocale(), OperationMode.REMOTE_SERVER.getValue(), officeDto);
        offActionForm.setOfficeId(element.getId().toString());
        offActionForm.setGlobalOfficeNum(element.getName());
        createGroupQuestionnaire.saveResponses(request, offActionForm, element.getId());
        return mapping.findForward(ActionForwards.create_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward getAllOffices(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        OfficeHierarchyByLevelDto officeHierarchyStructure = this.officeServiceFacade.retrieveAllOffices();

        SessionUtils.setCollectionAttribute(OfficeConstants.GET_HEADOFFICE, officeHierarchyStructure.getHeadOffices(), request);
        SessionUtils.setCollectionAttribute(OfficeConstants.GET_REGIONALOFFICE, officeHierarchyStructure.getRegionalOffices(), request);
        SessionUtils.setCollectionAttribute(OfficeConstants.GET_SUBREGIONALOFFICE, officeHierarchyStructure.getDivisionalOffices(), request);
        SessionUtils.setCollectionAttribute(OfficeConstants.GET_AREAOFFICE, officeHierarchyStructure.getAreaOffices(), request);
        SessionUtils.setCollectionAttribute(OfficeConstants.GET_BRANCHOFFICE, officeHierarchyStructure.getBranchOffices(), request);

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
        if (StringUtils.isBlank(actionForm.getOfficeId())) {
            throw new OfficeException(OfficeConstants.KEYGETFAILED);
        }

        OfficeDto officeDto = this.officeServiceFacade.retrieveOfficeById(Short.valueOf(actionForm.getOfficeId()));
        actionForm.clear();
        loadCustomFieldDefinitions(request);
        actionForm.populate(officeDto);
        SessionUtils.setAttribute(OfficeConstants.OFFICE_DTO, officeDto, request);
        setCurrentPageUrl(request, officeDto);
        return mapping.findForward(ActionForwards.get_success.toString());
    }
    private void setCurrentPageUrl(HttpServletRequest request, OfficeDto officeDto) throws PageExpiredException, UnsupportedEncodingException {
        SessionUtils.removeThenSetAttribute("currentPageUrl", constructCurrentPageUrl(officeDto), request);
    }

    private String constructCurrentPageUrl(OfficeDto officeDto) throws UnsupportedEncodingException {
        String url = String.format("offAction.do?officeId=%h",
                officeDto.getId());
        return URLEncoder.encode(url, "UTF-8");
    }




    @TransactionDemarcate(joinToken = true)
    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        OffActionForm offActionForm = (OffActionForm) form;

        OfficeDto sessionOffice = (OfficeDto) SessionUtils.getAttribute(OfficeConstants.OFFICE_DTO, request);

        OfficeBO office = ((OfficeBusinessService) getService()).getOffice(sessionOffice.getOfficeId());

        checkVersionMismatch(sessionOffice.getVersionNum(), office.getVersionNo());

        SessionUtils.setCollectionAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, new ArrayList<CustomFieldDefinitionEntity>(), request);
        loadofficeLevels(request);
        loadParents(request, offActionForm);
        offActionForm.setCustomFields(new ArrayList<CustomFieldDto>());
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
        OfficeDto sessionOffice = (OfficeDto) SessionUtils.getAttribute(OfficeConstants.OFFICE_DTO, request);

        UserContext userContext = getUserContext(request);
        Short officeId = sessionOffice.getOfficeId();
        Integer versionNum = sessionOffice.getVersionNum();
        OfficeUpdateRequest officeUpdateRequest = officeUpdateRequestFrom(offActionForm);

        boolean isParentOfficeChanged = this.legacyOfficeServiceFacade.updateOffice(userContext, officeId, versionNum, officeUpdateRequest);

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
            OfficeDto office = (OfficeDto) SessionUtils.getAttribute(OfficeConstants.OFFICE_DTO, request);

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
        SessionUtils.setCollectionAttribute(OfficeConstants.OFFICESTATUSLIST, ((OfficeBusinessService) getService()).getStatusList(), request);

    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward captureQuestionResponses(
            final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        request.setAttribute(METHODCALLED, "captureQuestionResponses");
        ActionErrors errors = createGroupQuestionnaire.validateResponses(request, (OffActionForm) form);
        if (errors != null && !errors.isEmpty()) {
            addErrors(request, errors);
            return mapping.findForward(ActionForwards.captureQuestionResponses.toString());
        }
        return createGroupQuestionnaire.rejoinFlow(mapping);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editQuestionResponses(
            final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        request.setAttribute(METHODCALLED, "editQuestionResponses");
        return createGroupQuestionnaire.editResponses(mapping, request, (OffActionForm) form);
    }

    private QuestionnaireFlowAdapter createGroupQuestionnaire = new QuestionnaireFlowAdapter("Create", "Office",
            ActionForwards.preview_success, "custSearchAction.do?method=loadMainSearch", new DefaultQuestionnaireServiceFacadeLocator());
}