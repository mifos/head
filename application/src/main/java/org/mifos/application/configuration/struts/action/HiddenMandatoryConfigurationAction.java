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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.mifos.application.configuration.struts.actionform.HiddenMandatoryConfigurationActionForm;
import org.mifos.application.configuration.util.helpers.HiddenMandatoryFieldNamesConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.persistence.FieldConfigurationPersistence;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfig;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class HiddenMandatoryConfigurationAction extends BaseAction {

    private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CONFIGURATION_LOGGER);

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
    }

    @Override
    protected BusinessService getService() {
        return ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Configuration);
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("hiddenmandatoryconfigurationaction");
        security.allow("load", SecurityConstants.CAN_DEFINE_HIDDEN_MANDATORY_FIELDS);
        security.allow("update", SecurityConstants.VIEW);
        security.allow("cancel", SecurityConstants.VIEW);
        security.allow("validate", SecurityConstants.VIEW);
        return security;
    }

    @Override
    protected boolean isNewBizRequired(HttpServletRequest request) throws ServiceException {
        return false;
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("Inside load method");
        HiddenMandatoryConfigurationActionForm actionForm = (HiddenMandatoryConfigurationActionForm) form;
        actionForm.clear();
        List<FieldConfigurationEntity> confFieldList = new FieldConfigurationPersistence()
                .getAllConfigurationFieldList();
        populateActionForm(actionForm, confFieldList);
        logger.debug("Outside load method");
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("cancel method called");
        return mapping.findForward(ActionForwards.cancel_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("Inside validate method");
        ActionForwards actionForward = ActionForwards.load_failure;
        String method = (String) request.getAttribute("methodCalled");
        if (method != null) {
            if (method.equals(Methods.load.toString())) {
                actionForward = ActionForwards.load_failure;
            } else if (method.equals(Methods.update.toString())) {
                actionForward = ActionForwards.update_failure;
            }
        }
        logger.debug("outside validate method");
        return mapping.findForward(actionForward.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("Inside update method");
        HiddenMandatoryConfigurationActionForm actionForm = (HiddenMandatoryConfigurationActionForm) form;
        List<FieldConfigurationEntity> confFieldList = new FieldConfigurationPersistence()
                .getAllConfigurationFieldList();
        updateFieldConfiguration(actionForm, confFieldList);
        StaticHibernateUtil.commitTransaction();
        initializeFieldConfiguration(servlet);
        logger.debug("Outside update method");
        return mapping.findForward(ActionForwards.update_success.toString());
    }

    private void initializeFieldConfiguration(ActionServlet servlet) throws HibernateProcessException,
            ApplicationException {
        FieldConfig fieldConfig = FieldConfig.getInstance();
        fieldConfig.init();
        servlet.getServletContext().setAttribute(Constants.FIELD_CONFIGURATION,
                fieldConfig.getEntityMandatoryFieldMap());
    }

    private void updateFieldConfiguration(HiddenMandatoryConfigurationActionForm actionForm,
            List<FieldConfigurationEntity> confFieldList) throws Exception {
        if (confFieldList != null && confFieldList.size() > 0) {
            for (FieldConfigurationEntity fieldConfiguration : confFieldList) {
                if (fieldConfiguration.getEntityType() == EntityType.CLIENT
                        || fieldConfiguration.getEntityType() == EntityType.PERSONNEL) {
                    updateClientDetails(actionForm, fieldConfiguration);
                } else if (fieldConfiguration.getEntityType() == EntityType.GROUP) {
                    updateGrouptDetails(actionForm, fieldConfiguration);
                } else {
                    updateSystemFields(actionForm, fieldConfiguration);
                }
            }
        }
    }

    private void updateSystemFields(HiddenMandatoryConfigurationActionForm actionForm,
            FieldConfigurationEntity fieldConfiguration) throws Exception {
        if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.EXTERNAL_ID)) {
            fieldConfiguration.update(getShortValue(actionForm.getMandatorySystemExternalId()),
                    getShortValue(actionForm.getHideSystemExternalId()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS1)) {
            fieldConfiguration.update(getShortValue(actionForm.getMandatorySystemAddress1()), fieldConfiguration
                    .getHiddenFlag());
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS2)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideSystemAddress2()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS3)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideSystemAddress3()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.CITY)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideSystemCity()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.STATE)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideSystemState()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.COUNTRY)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideSystemCountry()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.POSTAL_CODE)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideSystemCountry()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.RECEIPT_ID)
                || fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.RECEIPT_DATE)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideSystemReceiptIdDate()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.PURPOSE_OF_LOAN)) {
            // TODO: move to a new method with other loan account field updates
            fieldConfiguration.update(getShortValue(actionForm.getMandatoryLoanAccountPurpose()), fieldConfiguration
                    .getHiddenFlag());
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.SOURCE_OF_FUND)) {
            fieldConfiguration.update(getShortValue(actionForm.getMandatoryLoanSourceOfFund()), fieldConfiguration
                    .getHiddenFlag());
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.COLLATERAL_TYPE)
                || fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.COLLATERAL_NOTES)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideSystemCollateralTypeNotes()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ASSIGN_CLIENTS)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideSystemAssignClientPostions()));
        }
    }

    private void updateGrouptDetails(HiddenMandatoryConfigurationActionForm actionForm,
            FieldConfigurationEntity fieldConfiguration) throws Exception {
        if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS1)) {
            fieldConfiguration.update(getShortValue(actionForm.getMandatorySystemAddress1()), fieldConfiguration
                    .getHiddenFlag());
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS2)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideSystemAddress2()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS3)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideSystemAddress3()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.TRAINED)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideGroupTrained()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.EXTERNAL_ID)) {
            fieldConfiguration.update(getShortValue(actionForm.getMandatorySystemExternalId()),
                    getShortValue(actionForm.getHideSystemExternalId()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.CITY)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideSystemCity()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.STATE)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideSystemState()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.COUNTRY)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideSystemCountry()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.POSTAL_CODE)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideSystemCountry()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.EXTERNAL_ID)) {
            fieldConfiguration.update(getShortValue(actionForm.getMandatorySystemExternalId()),
                    getShortValue(actionForm.getHideSystemExternalId()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ETHINICITY)) {
            fieldConfiguration.update(getShortValue(actionForm.getMandatorySystemEthnicity()), getShortValue(actionForm
                    .getHideSystemEthnicity()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.CITIZENSHIP)) {
            fieldConfiguration.update(getShortValue(actionForm.getMandatorySystemCitizenShip()),
                    getShortValue(actionForm.getHideSystemCitizenShip()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.HANDICAPPED)) {
            fieldConfiguration.update(getShortValue(actionForm.getMandatorySystemHandicapped()),
                    getShortValue(actionForm.getHideSystemHandicapped()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.EDUCATION_LEVEL)) {
            fieldConfiguration.update(getShortValue(actionForm.getMandatorySystemEducationLevel()),
                    getShortValue(actionForm.getHideSystemEducationLevel()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.PHOTO)) {
            fieldConfiguration.update(getShortValue(actionForm.getMandatorySystemPhoto()), getShortValue(actionForm
                    .getHideSystemPhoto()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.RECEIPT_ID)
                || fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.RECEIPT_DATE)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideSystemReceiptIdDate()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ASSIGN_CLIENTS)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideSystemAssignClientPostions()));
        }
    }

    private void updateClientDetails(HiddenMandatoryConfigurationActionForm actionForm,
            FieldConfigurationEntity fieldConfiguration) throws Exception {
        if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.MIDDLE_NAME)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideClientMiddleName()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.SECOND_LAST_NAME)) {
            fieldConfiguration.update(getShortValue(actionForm.getMandatoryClientSecondLastName()),
                    getShortValue(actionForm.getHideClientSecondLastName()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.GOVERNMENT_ID)) {
            fieldConfiguration.update(getShortValue(actionForm.getMandatoryClientGovtId()), getShortValue(actionForm
                    .getHideClientGovtId()));
        } else if (fieldConfiguration.getFieldName().equals(
                HiddenMandatoryFieldNamesConstants.POVERTY_STATUS)) {
            fieldConfiguration.update(getShortValue(actionForm.getMandatoryClientPovertyStatus()),
                    getShortValue(actionForm.getHideClientPovertyStatus()));
        } else if (fieldConfiguration.getFieldName().equals(
                HiddenMandatoryFieldNamesConstants.SPOUSE_FATHER_MIDDLE_NAME)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideClientSpouseFatherMiddleName()));
        } else if (fieldConfiguration.getFieldName().equals(
                HiddenMandatoryFieldNamesConstants.SPOUSE_FATHER_SECOND_LAST_NAME)) {
            fieldConfiguration.update(getShortValue(actionForm.getMandatoryClientSpouseFatherSecondLastName()),
                    getShortValue(actionForm.getHideClientSpouseFatherSecondLastName()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.PHONE_NUMBER)) {
            fieldConfiguration.update(getShortValue(actionForm.getMandatoryClientPhone()), getShortValue(actionForm
                    .getHideClientPhone()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.TRAINED)) {
            fieldConfiguration.update(getShortValue(actionForm.getMandatoryClientTrained()), getShortValue(actionForm
                    .getHideClientTrained()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.TRAINED_DATE)) {
            fieldConfiguration.update(getShortValue(actionForm.getMandatoryClientTrainedOn()), fieldConfiguration
                    .getHiddenFlag());
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.BUSINESS_ACTIVITIES)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideClientBusinessWorkActivities()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.EXTERNAL_ID)) {
            fieldConfiguration.update(getShortValue(actionForm.getMandatorySystemExternalId()),
                    getShortValue(actionForm.getHideSystemExternalId()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ETHINICITY)) {
            fieldConfiguration.update(getShortValue(actionForm.getMandatorySystemEthnicity()), getShortValue(actionForm
                    .getHideSystemEthnicity()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.CITIZENSHIP)) {
            fieldConfiguration.update(getShortValue(actionForm.getMandatorySystemCitizenShip()),
                    getShortValue(actionForm.getHideSystemCitizenShip()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.HANDICAPPED)) {
            fieldConfiguration.update(getShortValue(actionForm.getMandatorySystemHandicapped()),
                    getShortValue(actionForm.getHideSystemHandicapped()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.EDUCATION_LEVEL)) {
            fieldConfiguration.update(getShortValue(actionForm.getMandatorySystemEducationLevel()),
                    getShortValue(actionForm.getHideSystemEducationLevel()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.PHOTO)) {
            fieldConfiguration.update(getShortValue(actionForm.getMandatorySystemPhoto()), getShortValue(actionForm
                    .getHideSystemPhoto()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS1)) {
            fieldConfiguration.update(getShortValue(actionForm.getMandatorySystemAddress1()), fieldConfiguration
                    .getHiddenFlag());
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS2)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideSystemAddress2()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS3)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideSystemAddress3()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.CITY)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideSystemCity()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.STATE)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideSystemState()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.COUNTRY)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideSystemCountry()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.POSTAL_CODE)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideSystemCountry()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.RECEIPT_ID)
                || fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.RECEIPT_DATE)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideSystemReceiptIdDate()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ASSIGN_CLIENTS)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(actionForm
                    .getHideSystemAssignClientPostions()));
        }
    }

    private void populateActionForm(HiddenMandatoryConfigurationActionForm actionForm,
            List<FieldConfigurationEntity> confFieldList) {
        if (confFieldList != null && confFieldList.size() > 0) {
            for (FieldConfigurationEntity fieldConfiguration : confFieldList) {
                if (fieldConfiguration.getEntityType() == EntityType.CLIENT) {
                    populateClientDetails(actionForm, fieldConfiguration);
                } else if (fieldConfiguration.getEntityType() == EntityType.GROUP) {
                    populateGrouptDetails(actionForm, fieldConfiguration);
                } else {
                    populateSystemFields(actionForm, fieldConfiguration);
                }
            }
        }
    }

    private void populateClientDetails(HiddenMandatoryConfigurationActionForm actionForm,
            FieldConfigurationEntity fieldConfiguration) {
        if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.MIDDLE_NAME)) {
            actionForm.setHideClientMiddleName(getStringValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.SECOND_LAST_NAME)) {
            actionForm.setHideClientSecondLastName(getStringValue(fieldConfiguration.getHiddenFlag()));
            actionForm.setMandatoryClientSecondLastName(getStringValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.GOVERNMENT_ID)) {
            actionForm.setHideClientGovtId(getStringValue(fieldConfiguration.getHiddenFlag()));
            actionForm.setMandatoryClientGovtId(getStringValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.POVERTY_STATUS)) {
            actionForm.setHideClientPovertyStatus(getStringValue(fieldConfiguration.getHiddenFlag()));
            actionForm.setMandatoryClientPovertyStatus(getStringValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(
                HiddenMandatoryFieldNamesConstants.SPOUSE_FATHER_MIDDLE_NAME)) {
            actionForm.setHideClientSpouseFatherMiddleName(getStringValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(
                HiddenMandatoryFieldNamesConstants.SPOUSE_FATHER_SECOND_LAST_NAME)) {
            actionForm.setHideClientSpouseFatherSecondLastName(getStringValue(fieldConfiguration.getHiddenFlag()));
            actionForm.setMandatoryClientSpouseFatherSecondLastName(getStringValue(fieldConfiguration
                    .getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.PHONE_NUMBER)) {
            actionForm.setHideClientPhone(getStringValue(fieldConfiguration.getHiddenFlag()));
            actionForm.setMandatoryClientPhone(getStringValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.TRAINED)) {
            actionForm.setHideClientTrained(getStringValue(fieldConfiguration.getHiddenFlag()));
            actionForm.setMandatoryClientTrained(getStringValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.TRAINED_DATE)) {
            actionForm.setMandatoryClientTrainedOn(getStringValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.BUSINESS_ACTIVITIES)) {
            actionForm.setHideClientBusinessWorkActivities(getStringValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ETHINICITY)) {
            actionForm.setHideSystemEthnicity(getStringValue(fieldConfiguration.getHiddenFlag()));
            actionForm.setMandatorySystemEthnicity(getStringValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.CITIZENSHIP)) {
            actionForm.setHideSystemCitizenShip(getStringValue(fieldConfiguration.getHiddenFlag()));
            actionForm.setMandatorySystemCitizenShip(getStringValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.HANDICAPPED)) {
            actionForm.setHideSystemHandicapped(getStringValue(fieldConfiguration.getHiddenFlag()));
            actionForm.setMandatorySystemHandicapped(getStringValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.EDUCATION_LEVEL)) {
            actionForm.setHideSystemEducationLevel(getStringValue(fieldConfiguration.getHiddenFlag()));
            actionForm.setMandatorySystemEducationLevel(getStringValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.PHOTO)) {
            actionForm.setHideSystemPhoto(getStringValue(fieldConfiguration.getHiddenFlag()));
            actionForm.setMandatorySystemPhoto(getStringValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS1)) {
            actionForm.setMandatorySystemAddress1(getStringValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS2)) {
            actionForm.setHideSystemAddress2(getStringValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS3)) {
            actionForm.setHideSystemAddress3(getStringValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.CITY)) {
            actionForm.setHideSystemCity(getStringValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.STATE)) {
            actionForm.setHideSystemState(getStringValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.COUNTRY)) {
            actionForm.setHideSystemCountry(getStringValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.POSTAL_CODE)) {
            actionForm.setHideSystemPostalCode(getStringValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ASSIGN_CLIENTS)) {
            actionForm.setHideSystemAssignClientPostions(getStringValue(fieldConfiguration.getHiddenFlag()));
        }
    }

    private void populateGrouptDetails(HiddenMandatoryConfigurationActionForm actionForm,
            FieldConfigurationEntity fieldConfiguration) {
        if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.TRAINED)) {
            actionForm.setHideGroupTrained(getStringValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS1)) {
            actionForm.setMandatorySystemAddress1(getStringValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS2)) {
            actionForm.setHideSystemAddress2(getStringValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS3)) {
            actionForm.setHideSystemAddress3(getStringValue(fieldConfiguration.getHiddenFlag()));
        }

    }

    private void populateSystemFields(HiddenMandatoryConfigurationActionForm actionForm,
            FieldConfigurationEntity fieldConfiguration) {
        if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.EXTERNAL_ID)) {
            actionForm.setHideSystemExternalId(getStringValue(fieldConfiguration.getHiddenFlag()));
            actionForm.setMandatorySystemExternalId(getStringValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.RECEIPT_ID)) {
            actionForm.setHideSystemReceiptIdDate(getStringValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.PURPOSE_OF_LOAN)) {
            actionForm.setMandatoryLoanAccountPurpose(getStringValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.SOURCE_OF_FUND)) {
            actionForm.setMandatoryLoanSourceOfFund(getStringValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.COLLATERAL_TYPE)) {
            actionForm.setHideSystemCollateralTypeNotes(getStringValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS1)) {
            actionForm.setMandatorySystemAddress1(getStringValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS2)) {
            actionForm.setHideSystemAddress2(getStringValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS3)) {
            actionForm.setHideSystemAddress3(getStringValue(fieldConfiguration.getHiddenFlag()));
        }
    }
}
