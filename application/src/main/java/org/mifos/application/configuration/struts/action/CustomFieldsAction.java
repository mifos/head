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

package org.mifos.application.configuration.struts.action;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.business.AccountCustomFieldEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.service.SavingsBusinessService;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.business.service.ConfigurationBusinessService;
import org.mifos.application.configuration.persistence.ApplicationConfigurationPersistence;
import org.mifos.application.configuration.struts.actionform.CustomFieldsActionForm;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.configuration.util.helpers.CustomFieldsBackfiller;
import org.mifos.application.configuration.util.helpers.CustomFieldsListBoxData;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerCustomFieldEntity;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldCategory;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficeCustomFieldEntity;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelCustomFieldEntity;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.SecurityConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class CustomFieldsAction extends BaseAction {

    private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CONFIGURATION_LOGGER);

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("customFieldsAction");

        security.allow("load", SecurityConstants.VIEW);
        security.allow("viewCategory", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.VIEW);
        security.allow("create", SecurityConstants.VIEW);
        security.allow("cancel", SecurityConstants.VIEW);
        security.allow("loadDefineCustomFields", SecurityConstants.CAN_DEFINE_CUSTOM_FIELD);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("editPreview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.CAN_DEFINE_CUSTOM_FIELD);
        security.allow("cancelCreate", SecurityConstants.VIEW);
        security.allow("editField", SecurityConstants.CAN_DEFINE_CUSTOM_FIELD);
        security.allow("editPrevious", SecurityConstants.CAN_DEFINE_CUSTOM_FIELD);
        security.allow("cancelEdit", SecurityConstants.VIEW);
        return security;
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("Inside load method");
        CustomFieldsActionForm actionForm = (CustomFieldsActionForm) form;
        actionForm.clear();
        logger.debug("Outside load method");
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("start previous method of Define Custom Fields Action");
        return mapping.findForward(ActionForwards.previous_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editPrevious(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        CustomFieldsActionForm actionForm = (CustomFieldsActionForm) form;
        request.setAttribute("category", actionForm.getCategory());
        logger.debug("start previous method of Define Custom Fields Action");
        return mapping.findForward(ActionForwards.editprevious_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancelCreate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("start Cancel Create method of Define Custom Fields Action");
        return mapping.findForward(ActionForwards.cancelCreate_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancelEdit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        CustomFieldsActionForm actionForm = (CustomFieldsActionForm) form;
        request.setAttribute("category", actionForm.getCategory());
        String flow = request.getParameter(Constants.CURRENTFLOWKEY).toString();
        request.setAttribute(Constants.CURRENTFLOWKEY, flow);
        logger.debug("start Cancel Edit method of Define Custom Fields Action");
        return mapping.findForward(ActionForwards.cancelEdit_success.toString());
    }

    private void loadDataTypes(Locale locale, CustomFieldsActionForm actionForm, HttpServletRequest request)
            throws Exception {
        List<CustomFieldsListBoxData> dataTypes = new ArrayList<CustomFieldsListBoxData>();
        CustomFieldsListBoxData data = null;
        for (CustomFieldType dataType : CustomFieldType.values()) {
            data = new CustomFieldsListBoxData();
            data.setName(MessageLookup.getInstance().lookup(dataType, locale));
            data.setId(dataType.getValue());
            dataTypes.add(data);
        }
        SessionUtils.setCollectionAttribute(ConfigurationConstants.ALL_DATA_TYPES, dataTypes, request);

    }

    private void loadCategories(CustomFieldsActionForm actionForm, HttpServletRequest request) throws Exception {
        List<CustomFieldsListBoxData> allCategories = new ArrayList<CustomFieldsListBoxData>();
        CustomFieldsListBoxData data = null;
        for (CustomFieldCategory category : CustomFieldCategory.values()) {
            EntityType entityType = category.mapToEntityType();
            String entityName = MessageLookup.getInstance().lookupLabel(category.name(), getUserContext(request));
            data = new CustomFieldsListBoxData();
            data.setName(entityName);
            data.setId(entityType.getValue());
            allCategories.add(data);
        }
        SessionUtils.setCollectionAttribute(ConfigurationConstants.ALL_CATEGORIES, allCategories, request);

    }

    protected Locale getUserLocale(HttpServletRequest request) {
        Locale locale = null;
        HttpSession session = request.getSession();
        if (session != null) {
            UserContext userContext = (UserContext) session.getAttribute(LoginConstants.USERCONTEXT);

            if (null != userContext) {

                locale = userContext.getCurrentLocale();
            }
        }
        return locale;
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward loadDefineCustomFields(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("Inside load define fields method");
        CustomFieldsActionForm actionForm = (CustomFieldsActionForm) form;
        actionForm.clear();
        Locale locale = getUserLocale(request);
        loadDataTypes(locale, actionForm, request);
        loadCategories(actionForm, request);

        logger.debug("Outside load define fields method");
        return mapping.findForward(ActionForwards.loadDefineCustomFields_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward viewCategory(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("Inside viewCategory method");
        CustomFieldsActionForm actionForm = (CustomFieldsActionForm) form;
        String category = null;
        if (request.getParameter("category") != null)
            category = request.getParameter("category");
        else
            category = actionForm.getCategory();
        request.setAttribute("category", category);
        String categoryName = MessageLookup.getInstance().lookupLabel(category);
        request.setAttribute("categoryName", categoryName);

        logger.debug("Outside viewCategory method");
        return mapping.findForward(ActionForwards.viewCategory_success.toString());
    }

    private String changeDefaultValueDateToDBFormat(String defaultValue, Locale locale) throws InvalidDateException {
        SimpleDateFormat shortFormat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale);
        String userfmt = DateUtils.convertToCurrentDateFormat(shortFormat.toPattern());
        return DateUtils.convertUserToDbFmt(defaultValue, userfmt);
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        CustomFieldsActionForm actionForm = (CustomFieldsActionForm) form;
        Locale locale = getUserLocale(request);
        CustomFieldDefinitionEntity customField = (CustomFieldDefinitionEntity) SessionUtils.getAttribute(
                ConfigurationConstants.CURRENT_CUSTOM_FIELD, request);
        Short dataType = Short.parseShort(actionForm.getDataType());
        if (dataType.equals(CustomFieldType.DATE.getValue()))
            customField.setDefaultValue(changeDefaultValueDateToDBFormat(actionForm.getDefaultValue(), locale));
        else
            customField.setDefaultValue(actionForm.getDefaultValue());
        YesNoFlag flag = null;
        if (actionForm.isMandatoryField())
            flag = YesNoFlag.YES;
        else
            flag = YesNoFlag.NO;
        customField.setMandatoryFlag(flag.getValue());
        Short localeId = getUserContext(request).getLocaleId();
        String labelName = actionForm.getLabelName();
        customField.setLabel(labelName);

        ApplicationConfigurationPersistence persistence = new ApplicationConfigurationPersistence();
        persistence.updateCustomField(customField);
        // MifosConfiguration.getInstance().reload();
        request.setAttribute("category", actionForm.getCategory());
        MifosConfiguration.getInstance().updateLabelKey(customField.getLookUpEntity().getEntityType(), labelName,
                localeId);
        logger.debug("Inside update method");
        return mapping.findForward(ActionForwards.update_success.toString());
    }

    /*
     * For now the level id of the custom field is derived from the category. It
     * may be changed when business knows what exactly the level id is
     */
    private Short getLevelId(EntityType categoryType) {
        if (categoryType.equals(EntityType.CENTER))
            return CustomerLevel.CENTER.getValue();
        else if (categoryType.equals(EntityType.GROUP))
            return CustomerLevel.GROUP.getValue();
        else if (categoryType.equals(EntityType.CLIENT))
            return CustomerLevel.CLIENT.getValue();
        else
            return null;
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        CustomFieldsActionForm actionForm = (CustomFieldsActionForm) form;
        String categoryType = actionForm.getCategoryType();
        EntityType categoryTypeEntity = EntityType.fromInt(Integer.parseInt(categoryType));
        CustomFieldType fieldType = CustomFieldType.fromInt(Integer.parseInt(actionForm.getDataType()));
        String defaultValue = actionForm.getDefaultValue();
        if (fieldType.equals(CustomFieldType.DATE))
            defaultValue = changeDefaultValueDateToDBFormat(defaultValue, getUserLocale(request));

        String label = actionForm.getLabelName();
        Short levelId = getLevelId(categoryTypeEntity);
        YesNoFlag mandatory = null;
        if (actionForm.isMandatoryField())
            mandatory = YesNoFlag.YES;
        else
            mandatory = YesNoFlag.NO;
        Short localeId = getUserContext(request).getLocaleId();
        CustomFieldDefinitionEntity customField = new CustomFieldDefinitionEntity(label, levelId, fieldType,
                categoryTypeEntity, defaultValue, mandatory);
        ApplicationConfigurationPersistence persistence = new ApplicationConfigurationPersistence();
        persistence.addCustomField(customField);
        MifosConfiguration.getInstance().updateLabelKey(customField.getLookUpEntity().getEntityType(), label, localeId);

        // client, loan, group, etc. records extant before this custom field was
        // created still need to be associated with the custom field
        CustomFieldsBackfiller cfb = new CustomFieldsBackfiller();
        cfb.addCustomFieldsForExistingRecords(categoryTypeEntity, levelId, customField);

        logger.debug("Inside create method");
        return mapping.findForward(ActionForwards.create_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("cancel method called");
        return mapping.findForward(ActionForwards.cancel_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        CustomFieldsActionForm actionForm = (CustomFieldsActionForm) form;
        YesNoFlag flag = null;
        if (actionForm.isMandatoryField())
            flag = YesNoFlag.YES;
        else
            flag = YesNoFlag.NO;
        Locale locale = getUserLocale(request);
        String mandatoryStringValue = CustomFieldDefinitionEntity.getMandatoryStringValue(locale, flag.getValue());
        actionForm.setMandatoryStringValue(mandatoryStringValue);

        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editPreview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        CustomFieldsActionForm actionForm = (CustomFieldsActionForm) form;
        request.setAttribute("category", actionForm.getCategory());
        YesNoFlag flag = null;
        if (actionForm.isMandatoryField())
            flag = YesNoFlag.YES;
        else
            flag = YesNoFlag.NO;
        Locale locale = getUserLocale(request);
        String mandatoryStringValue = CustomFieldDefinitionEntity.getMandatoryStringValue(locale, flag.getValue());
        actionForm.setMandatoryStringValue(mandatoryStringValue);

        return mapping.findForward(ActionForwards.editPreview_success.toString());
    }

    @Override
    protected BusinessService getService() {
        return new ConfigurationBusinessService();
    }

    private String getEntityTypeName(Short entityTypeId, UserContext userContext) {
        String entityTypeName = null;
        for (CustomFieldCategory category : CustomFieldCategory.values()) {
            EntityType entityType = category.mapToEntityType();
            if (entityType.getValue().equals(entityTypeId)) {
                entityTypeName = MessageLookup.getInstance().lookupLabel(category.name(), userContext);
                break;
            }
        }
        return entityTypeName;
    }

    private String getDataType(Short dataTypeId, Locale locale) {
        String dataTypeName = null;
        for (CustomFieldType dataType : CustomFieldType.values()) {
            if (dataType.getValue().equals(dataTypeId)) {
                dataTypeName = MessageLookup.getInstance().lookup(dataType, locale);
                break;
            }

        }
        return dataTypeName;
    }

    private void setFormAttributes(CustomFieldsActionForm actionForm, Short editedCustomFieldId,
            HttpServletRequest request) throws Exception {
        UserContext userContext = getUserContext(request);
        MasterPersistence masterPersistence = new MasterPersistence();
        CustomFieldDefinitionEntity customField = masterPersistence
                .retrieveOneCustomFieldDefinition(editedCustomFieldId);
        actionForm.setCategoryType(customField.getEntityType().toString()); // entity
                                                                            // type
                                                                            // id

        String label = customField.getLabel();
        actionForm.setLabelName(label);
        String entityTypeName = getEntityTypeName(customField.getEntityType(), userContext);
        actionForm.setCategoryTypeName(entityTypeName);
        String customFieldCategory = CustomFieldCategory.fromInt(customField.getEntityType().intValue()).name();
        request.setAttribute("category", customFieldCategory);
        Locale locale = getUserLocale(request);
        String dataTypeName = getDataType(customField.getFieldType(), locale);
        Short fieldType = customField.getFieldType();
        String defaultValue = customField.getDefaultValue();
        if (fieldType.equals(CustomFieldType.DATE.getValue()) && !StringUtils.isNullOrEmpty(defaultValue))
            actionForm.setDefaultValue(DateUtils.getUserLocaleDate(locale, defaultValue));
        else
            actionForm.setDefaultValue(defaultValue);
        actionForm.setDataType(fieldType.toString());
        actionForm.setMandatoryField(customField.isMandatory());
        actionForm.setMandatoryStringValue(customField.getMandatoryStringValue(locale));
        List<CustomFieldsListBoxData> dataTypes = new ArrayList<CustomFieldsListBoxData>();
        CustomFieldsListBoxData dataType = new CustomFieldsListBoxData();
        dataType.setName(dataTypeName);
        dataType.setId(customField.getFieldType());
        dataTypes.add(dataType);
        SessionUtils.setCollectionAttribute(ConfigurationConstants.CURRENT_DATA_TYPE, dataTypes, request);
        List<CustomFieldsListBoxData> categories = new ArrayList<CustomFieldsListBoxData>();
        CustomFieldsListBoxData category = new CustomFieldsListBoxData();
        category.setName(entityTypeName);
        category.setId(customField.getEntityType());
        categories.add(category);
        SessionUtils.setCollectionAttribute(ConfigurationConstants.CURRENT_CATEGORY, categories, request);
        SessionUtils.setAttribute(ConfigurationConstants.CURRENT_CUSTOM_FIELD, customField, request);

    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editField(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("start manage method of Fund Action");
        CustomFieldsActionForm actionForm = (CustomFieldsActionForm) form;
        if (StringUtils.isNullOrEmpty(actionForm.getCustomFieldIdStr()))
            throw new Exception("Error! No custom field id is null.");
        Short editedCustomFieldId = Short.parseShort(actionForm.getCustomFieldIdStr());
        setFormAttributes(actionForm, editedCustomFieldId, request);
        String flow = request.getParameter(Constants.CURRENTFLOWKEY).toString();
        request.setAttribute(Constants.CURRENTFLOWKEY, flow);
        return mapping.findForward(ActionForwards.edit_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("Inside validate method");
        CustomFieldsActionForm actionForm = (CustomFieldsActionForm) form;
        ActionForwards actionForward = ActionForwards.update_failure;

        String method = (String) request.getAttribute("method");
        if (method != null) {
            if (method.equals(Methods.load.toString())) {
                actionForward = ActionForwards.load_failure;
            } else if (method.equals(Methods.update.toString())) {
                actionForward = ActionForwards.update_failure;
            } else if (method.equals(Methods.preview.toString())) {
                actionForward = ActionForwards.preview_failure;
            } else if (method.equals(Methods.editPreview.toString())) {
                request.setAttribute("category", actionForm.getCategory());
                actionForward = ActionForwards.editPreview_failure;
            }
        }

        logger.debug("outside validate method");
        return mapping.findForward(actionForward.toString());
    }

}
