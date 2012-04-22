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

package org.mifos.config.struts.action;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomValueDto;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.LegacyMasterDao;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.config.struts.actionform.LookupOptionsActionForm;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.config.util.helpers.LookupOptionData;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.activity.DynamicLookUpValueCreationTypes;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LookupOptionsAction extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(LookupOptionsAction.class);

    private void setLookupType(String configurationEntity, HttpServletRequest request) {

        if (configurationEntity.equals(ConfigurationConstants.CONFIG_SALUTATION)) {
            request.setAttribute(ConfigurationConstants.LOOKUP_TYPE, getLocalizedMessage("configuration.salutation"));
            request.setAttribute(ConfigurationConstants.ENTITY, ConfigurationConstants.CONFIG_SALUTATION);
        } else if (configurationEntity.equals(ConfigurationConstants.CONFIG_PERSONNEL_TITLE)) {
            request.setAttribute(ConfigurationConstants.LOOKUP_TYPE, getLocalizedMessage("configuration.usertitle"));
            request.setAttribute(ConfigurationConstants.ENTITY, ConfigurationConstants.CONFIG_PERSONNEL_TITLE);
        } else if (configurationEntity.equals(ConfigurationConstants.CONFIG_MARITAL_STATUS)) {
            request.setAttribute(ConfigurationConstants.LOOKUP_TYPE, getLocalizedMessage("configuration.maritalstatus"));
            request.setAttribute(ConfigurationConstants.ENTITY, ConfigurationConstants.CONFIG_MARITAL_STATUS);
        } else if (configurationEntity.equals(ConfigurationConstants.CONFIG_ETHNICITY)) {
            String label = ApplicationContextProvider.getBean(MessageLookup.class).lookupLabel(ConfigurationConstants.ETHNICITY);
            request.setAttribute(ConfigurationConstants.LOOKUP_TYPE, label);
            request.setAttribute(ConfigurationConstants.ENTITY, ConfigurationConstants.CONFIG_ETHNICITY);
        } else if (configurationEntity.equals(ConfigurationConstants.CONFIG_EDUCATION_LEVEL)) {
            request.setAttribute(ConfigurationConstants.LOOKUP_TYPE, getLocalizedMessage("configuration.educationlevel"));
            request.setAttribute(ConfigurationConstants.ENTITY, ConfigurationConstants.CONFIG_EDUCATION_LEVEL);
        } else if (configurationEntity.equals(ConfigurationConstants.CONFIG_CITIZENSHIP)) {
            String label = ApplicationContextProvider.getBean(MessageLookup.class).lookupLabel(ConfigurationConstants.CITIZENSHIP);
            request.setAttribute(ConfigurationConstants.LOOKUP_TYPE, label);
            request.setAttribute(ConfigurationConstants.ENTITY, ConfigurationConstants.CONFIG_CITIZENSHIP);
        } else if (configurationEntity.equals(ConfigurationConstants.CONFIG_BUSINESS_ACTIVITY)) {
            request.setAttribute(ConfigurationConstants.LOOKUP_TYPE, getLocalizedMessage("configuration.businessactivity"));
            request.setAttribute(ConfigurationConstants.ENTITY, ConfigurationConstants.CONFIG_BUSINESS_ACTIVITY);
        } else if (configurationEntity.equals(ConfigurationConstants.CONFIG_LOAN_PURPOSE)) {
            request.setAttribute(ConfigurationConstants.LOOKUP_TYPE, getLocalizedMessage("configuration.purposeofloan"));
            request.setAttribute(ConfigurationConstants.ENTITY, ConfigurationConstants.CONFIG_LOAN_PURPOSE);
        } else if (configurationEntity.equals(ConfigurationConstants.CONFIG_COLLATERAL_TYPE)) {
            request.setAttribute(ConfigurationConstants.LOOKUP_TYPE, getLocalizedMessage("configuration.collateraltype"));
            request.setAttribute(ConfigurationConstants.ENTITY, ConfigurationConstants.CONFIG_COLLATERAL_TYPE);
        } else if (configurationEntity.equals(ConfigurationConstants.CONFIG_HANDICAPPED)) {
            String label = ApplicationContextProvider.getBean(MessageLookup.class).lookupLabel(ConfigurationConstants.HANDICAPPED);
            request.setAttribute(ConfigurationConstants.LOOKUP_TYPE, label);
            request.setAttribute(ConfigurationConstants.ENTITY, ConfigurationConstants.CONFIG_HANDICAPPED);
        } else if (configurationEntity.equals(ConfigurationConstants.CONFIG_OFFICER_TITLE)) {
            request.setAttribute(ConfigurationConstants.LOOKUP_TYPE, getLocalizedMessage("configuration.officertitle"));
            request.setAttribute(ConfigurationConstants.ENTITY, ConfigurationConstants.CONFIG_OFFICER_TITLE);
        } else if (configurationEntity.equals(ConfigurationConstants.CONFIG_PAYMENT_TYPE)) {
            request.setAttribute(ConfigurationConstants.LOOKUP_TYPE, getLocalizedMessage("configuration.paymentmodes"));
            request.setAttribute(ConfigurationConstants.ENTITY, ConfigurationConstants.CONFIG_PAYMENT_TYPE);
        }
    }

    /**
     * @return boolean -- Return true if we found the expected data to use,
     *         otherwise return false
     */
    private boolean setLookupOptionData(String configurationEntity, LookupOptionData data, HttpServletRequest request,
            String addOrEdit, LookupOptionsActionForm lookupOptionsActionForm) throws Exception {
        assert (configurationEntity.equals(ConfigurationConstants.CONFIG_SALUTATION)
                || configurationEntity.equals(ConfigurationConstants.CONFIG_PERSONNEL_TITLE)
                || configurationEntity.equals(ConfigurationConstants.CONFIG_MARITAL_STATUS)
                || configurationEntity.equals(ConfigurationConstants.CONFIG_ETHNICITY)
                || configurationEntity.equals(ConfigurationConstants.CONFIG_EDUCATION_LEVEL)
                || configurationEntity.equals(ConfigurationConstants.CONFIG_CITIZENSHIP)
                || configurationEntity.equals(ConfigurationConstants.CONFIG_BUSINESS_ACTIVITY)
                || configurationEntity.equals(ConfigurationConstants.CONFIG_LOAN_PURPOSE)
                || configurationEntity.equals(ConfigurationConstants.CONFIG_COLLATERAL_TYPE)
                || configurationEntity.equals(ConfigurationConstants.CONFIG_HANDICAPPED)
                || configurationEntity.equals(ConfigurationConstants.CONFIG_OFFICER_TITLE)
                || configurationEntity.equals(ConfigurationConstants.CONFIG_PAYMENT_TYPE)
                );
        setLookupType(configurationEntity, request);
        data.setValueListId(Short.parseShort(SessionUtils.getAttribute(configurationEntity, request).toString()));
        if (addOrEdit.equals("add")) {
            data.setLookupValue("");
            data.setLookupId(0);
            lookupOptionsActionForm.setLookupValue("");
            return true;
        }
        String selectedValue1 = null;

        if (configurationEntity.equals(ConfigurationConstants.CONFIG_SALUTATION)) {
            assert (lookupOptionsActionForm.getSalutationList().length == 1);
            selectedValue1 = lookupOptionsActionForm.getSalutationList()[0];
        } else if (configurationEntity.equals(ConfigurationConstants.CONFIG_PERSONNEL_TITLE)) {
            assert (lookupOptionsActionForm.getUserTitleList().length == 1);
            selectedValue1 = lookupOptionsActionForm.getUserTitleList()[0];
        } else if (configurationEntity.equals(ConfigurationConstants.CONFIG_MARITAL_STATUS)) {
            assert (lookupOptionsActionForm.getMaritalStatusList().length == 1);
            selectedValue1 = lookupOptionsActionForm.getMaritalStatusList()[0];
        } else if (configurationEntity.equals(ConfigurationConstants.CONFIG_ETHNICITY)) {
            assert (lookupOptionsActionForm.getEthnicityList().length == 1);
            selectedValue1 = lookupOptionsActionForm.getEthnicityList()[0];
        } else if (configurationEntity.equals(ConfigurationConstants.CONFIG_EDUCATION_LEVEL)) {
            assert (lookupOptionsActionForm.getEducationLevelList().length == 1);
            selectedValue1 = lookupOptionsActionForm.getEducationLevelList()[0];
        } else if (configurationEntity.equals(ConfigurationConstants.CONFIG_CITIZENSHIP)) {
            assert (lookupOptionsActionForm.getCitizenshipList().length == 1);
            selectedValue1 = lookupOptionsActionForm.getCitizenshipList()[0];
        } else if (configurationEntity.equals(ConfigurationConstants.CONFIG_BUSINESS_ACTIVITY)) {
            assert (lookupOptionsActionForm.getBusinessActivityList().length == 1);
            selectedValue1 = lookupOptionsActionForm.getBusinessActivityList()[0];
        } else if (configurationEntity.equals(ConfigurationConstants.CONFIG_LOAN_PURPOSE)) {
            assert (lookupOptionsActionForm.getPurposesOfLoanList().length == 1);
            selectedValue1 = lookupOptionsActionForm.getPurposesOfLoanList()[0];
        } else if (configurationEntity.equals(ConfigurationConstants.CONFIG_COLLATERAL_TYPE)) {
            assert (lookupOptionsActionForm.getCollateralTypeList().length == 1);
            selectedValue1 = lookupOptionsActionForm.getCollateralTypeList()[0];
        } else if (configurationEntity.equals(ConfigurationConstants.CONFIG_HANDICAPPED)) {
            assert (lookupOptionsActionForm.getHandicappedList().length == 1);
            selectedValue1 = lookupOptionsActionForm.getHandicappedList()[0];
        } else if (configurationEntity.equals(ConfigurationConstants.CONFIG_OFFICER_TITLE)) {
            assert (lookupOptionsActionForm.getOfficerTitleList().length == 1);
            selectedValue1 = lookupOptionsActionForm.getOfficerTitleList()[0];
        } else if (configurationEntity.equals(ConfigurationConstants.CONFIG_PAYMENT_TYPE)) {
            assert (lookupOptionsActionForm.getPaymentTypeList().length == 1);
            selectedValue1 = lookupOptionsActionForm.getPaymentTypeList()[0];
        }
        // edit
        String selectedValue = selectedValue1;
        if (selectedValue == null) {
            return false;
        }
        String[] spliteStrList = selectedValue.split(";");
        assert (spliteStrList.length == 2);
        data.setLookupValue(spliteStrList[1]);
        lookupOptionsActionForm.setLookupValue(spliteStrList[1]);
        data.setLookupId(Integer.parseInt(spliteStrList[0]));
        return true;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward addEditLookupOption(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        LookupOptionsActionForm lookupOptionsActionForm = (LookupOptionsActionForm) form;

        String entity = request.getParameter(ConfigurationConstants.ENTITY);
        String addOrEdit = request.getParameter(ConfigurationConstants.ADD_OR_EDIT);
        LookupOptionData data = new LookupOptionData();

        boolean setLookupData = setLookupOptionData(entity, data, request, addOrEdit, lookupOptionsActionForm);
        if (setLookupData) {
            SessionUtils.setAttribute(ConfigurationConstants.LOOKUP_OPTION_DATA, data, request);
            return mapping.findForward(ActionForwards.addEditLookupOption_success.toString());
        }

        return mapping.findForward(ActionForwards.addEditLookupOption_failure.toString());
    }

    private void populateConfigurationListBox(String configurationEntity, LegacyMasterDao legacyMasterDao,
            Short localeId, HttpServletRequest request, LookupOptionsActionForm lookupOptionsActionForm,
            String configurationEntityConst) throws Exception {
        CustomValueDto valueList = legacyMasterDao.getLookUpEntity(configurationEntity);
        Short valueListId = valueList.getEntityId();
        // save this value and will be retrieved when update the data to db
        SessionUtils.setAttribute(configurationEntityConst, valueListId, request);
        if (configurationEntity.equals(MasterConstants.SALUTATION)) {
            lookupOptionsActionForm.setSalutations(valueList.getCustomValueListElements());
        } else if (configurationEntity.equals(MasterConstants.PERSONNEL_TITLE)) {
            lookupOptionsActionForm.setUserTitles(valueList.getCustomValueListElements());
        } else if (configurationEntity.equals(MasterConstants.MARITAL_STATUS)) {
            lookupOptionsActionForm.setMaritalStatuses(valueList.getCustomValueListElements());
        } else if (configurationEntity.equals(MasterConstants.ETHNICITY)) {
            lookupOptionsActionForm.setEthnicities(valueList.getCustomValueListElements());
        } else if (configurationEntity.equals(MasterConstants.EDUCATION_LEVEL)) {
            lookupOptionsActionForm.setEducationLevels(valueList.getCustomValueListElements());
        } else if (configurationEntity.equals(MasterConstants.CITIZENSHIP)) {
            lookupOptionsActionForm.setCitizenships(valueList.getCustomValueListElements());
        } else if (configurationEntity.equals(MasterConstants.BUSINESS_ACTIVITIES)) {
            lookupOptionsActionForm.setBusinessActivities(valueList.getCustomValueListElements());
        } else if (configurationEntity.equals(MasterConstants.LOAN_PURPOSES)) {
            lookupOptionsActionForm.setPurposesOfLoan(valueList.getCustomValueListElements());
        } else if (configurationEntity.equals(MasterConstants.COLLATERAL_TYPES)) {
            lookupOptionsActionForm.setCollateralTypes(valueList.getCustomValueListElements());
        } else if (configurationEntity.equals(MasterConstants.HANDICAPPED)) {
            lookupOptionsActionForm.setHandicappeds(valueList.getCustomValueListElements());
        } else if (configurationEntity.equals(MasterConstants.OFFICER_TITLES)) {
            lookupOptionsActionForm.setOfficerTitles(valueList.getCustomValueListElements());
        } else if (configurationEntity.equals(MasterConstants.PAYMENT_TYPE)) {
            lookupOptionsActionForm.setPaymentTypes(valueList.getCustomValueListElements());
        } else {
            throw new Exception("Invalid configuration type in LookupOptionAction. Type is " + configurationEntity);
        }
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

    private void setHiddenFields(HttpServletRequest request) {

        request.setAttribute(ConfigurationConstants.CONFIG_SALUTATION, ConfigurationConstants.CONFIG_SALUTATION);
        request
                .setAttribute(ConfigurationConstants.CONFIG_MARITAL_STATUS,
                        ConfigurationConstants.CONFIG_MARITAL_STATUS);
        request.setAttribute(ConfigurationConstants.CONFIG_PERSONNEL_TITLE,
                ConfigurationConstants.CONFIG_PERSONNEL_TITLE);
        request.setAttribute(ConfigurationConstants.CONFIG_EDUCATION_LEVEL,
                ConfigurationConstants.CONFIG_EDUCATION_LEVEL);
        request.setAttribute(ConfigurationConstants.CONFIG_CITIZENSHIP, ConfigurationConstants.CONFIG_CITIZENSHIP);
        request.setAttribute(ConfigurationConstants.CONFIG_HANDICAPPED, ConfigurationConstants.CONFIG_HANDICAPPED);
        request.setAttribute(ConfigurationConstants.CONFIG_OFFICER_TITLE, ConfigurationConstants.CONFIG_OFFICER_TITLE);
        request.setAttribute(ConfigurationConstants.CONFIG_BUSINESS_ACTIVITY,
                ConfigurationConstants.CONFIG_BUSINESS_ACTIVITY);
        request.setAttribute(ConfigurationConstants.CONFIG_LOAN_PURPOSE, ConfigurationConstants.CONFIG_LOAN_PURPOSE);
        request.setAttribute(ConfigurationConstants.CONFIG_COLLATERAL_TYPE,
                ConfigurationConstants.CONFIG_COLLATERAL_TYPE);
        request.setAttribute(ConfigurationConstants.CONFIG_ETHNICITY, ConfigurationConstants.CONFIG_ETHNICITY);
        request.setAttribute(ConfigurationConstants.CONFIG_PAYMENT_TYPE, ConfigurationConstants.CONFIG_PAYMENT_TYPE);

    }

    private void setSpecialLables(UserContext userContext, LookupOptionsActionForm lookupOptionsActionForm) {
        lookupOptionsActionForm.setCitizenship(ApplicationContextProvider.getBean(MessageLookup.class).lookupLabel(
                ConfigurationConstants.CITIZENSHIP));
        lookupOptionsActionForm.setHandicapped(ApplicationContextProvider.getBean(MessageLookup.class).lookupLabel(
                ConfigurationConstants.HANDICAPPED));
        lookupOptionsActionForm.setEthnicity(ApplicationContextProvider.getBean(MessageLookup.class).lookupLabel(ConfigurationConstants.ETHNICITY));
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("Inside load method");
        LookupOptionsActionForm lookupOptionsActionForm = (LookupOptionsActionForm) form;
        lookupOptionsActionForm.clear();
        setHiddenFields(request);

        Short localeId = getUserContext(request).getLocaleId();
        setSpecialLables(getUserContext(request), lookupOptionsActionForm);
        populateConfigurationListBox(MasterConstants.SALUTATION, legacyMasterDao, localeId, request,
                lookupOptionsActionForm, ConfigurationConstants.CONFIG_SALUTATION);
        populateConfigurationListBox(MasterConstants.PERSONNEL_TITLE, legacyMasterDao, localeId, request,
                lookupOptionsActionForm, ConfigurationConstants.CONFIG_PERSONNEL_TITLE);
        populateConfigurationListBox(MasterConstants.MARITAL_STATUS, legacyMasterDao, localeId, request,
                lookupOptionsActionForm, ConfigurationConstants.CONFIG_MARITAL_STATUS);
        populateConfigurationListBox(MasterConstants.ETHNICITY, legacyMasterDao, localeId, request,
                lookupOptionsActionForm, ConfigurationConstants.CONFIG_ETHNICITY);
        populateConfigurationListBox(MasterConstants.EDUCATION_LEVEL, legacyMasterDao, localeId, request,
                lookupOptionsActionForm, ConfigurationConstants.CONFIG_EDUCATION_LEVEL);
        populateConfigurationListBox(MasterConstants.CITIZENSHIP, legacyMasterDao, localeId, request,
                lookupOptionsActionForm, ConfigurationConstants.CONFIG_CITIZENSHIP);
        populateConfigurationListBox(MasterConstants.BUSINESS_ACTIVITIES, legacyMasterDao, localeId, request,
                lookupOptionsActionForm, ConfigurationConstants.CONFIG_BUSINESS_ACTIVITY);
        populateConfigurationListBox(MasterConstants.LOAN_PURPOSES, legacyMasterDao, localeId, request,
                lookupOptionsActionForm, ConfigurationConstants.CONFIG_LOAN_PURPOSE);
        populateConfigurationListBox(MasterConstants.COLLATERAL_TYPES, legacyMasterDao, localeId, request,
                lookupOptionsActionForm, ConfigurationConstants.CONFIG_COLLATERAL_TYPE);
        populateConfigurationListBox(MasterConstants.HANDICAPPED, legacyMasterDao, localeId, request,
                lookupOptionsActionForm, ConfigurationConstants.CONFIG_HANDICAPPED);
        populateConfigurationListBox(MasterConstants.OFFICER_TITLES, legacyMasterDao, localeId, request,
                lookupOptionsActionForm, ConfigurationConstants.CONFIG_OFFICER_TITLE);
        populateConfigurationListBox(MasterConstants.PAYMENT_TYPE, legacyMasterDao, localeId, request,
                lookupOptionsActionForm, ConfigurationConstants.CONFIG_PAYMENT_TYPE);

        logger.debug("Outside load method");
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("cancel method called");
        return mapping.findForward(ActionForwards.cancel_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward addEditLookupOption_cancel(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("addEditLookupOption_cancel method called");
        return mapping.findForward(ActionForwards.addEditLookupOption_cancel_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("Inside validate method");
        ActionForwards actionForward = ActionForwards.addEditLookupOption_failure;

        String method = (String) request.getAttribute("method");
        if (method != null) {
            if (method.equals(Methods.load.toString())) {
                actionForward = ActionForwards.load_failure;
            } else if (method.equals(Methods.update.toString())) {
                String entity = request.getParameter(ConfigurationConstants.ENTITY);
                setLookupType(entity, request);

                actionForward = ActionForwards.update_failure;
            }
        }

        logger.debug("outside validate method");
        return mapping.findForward(actionForward.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("Inside update method");
        // setHiddenFields(request);
        LookupOptionsActionForm lookupOptionsActionForm = (LookupOptionsActionForm) form;
        LookupOptionData data = (LookupOptionData) SessionUtils.getAttribute(ConfigurationConstants.LOOKUP_OPTION_DATA,request);
        data.setLookupValue(lookupOptionsActionForm.getLookupValue());
        String entity = request.getParameter(ConfigurationConstants.ENTITY);
        if (data.getLookupId() > 0) {
            legacyMasterDao.updateValueListElementForLocale(data.getLookupId(), data.getLookupValue());
        } else {
            LookUpValueEntity newLookupValue =  legacyMasterDao.addValueListElementForLocale(
                    DynamicLookUpValueCreationTypes.LookUpOption, data.getValueListId(),
                    data.getLookupValue());

            /*
             * Add a special case for payment types since we not only need to create a new
             * lookup value but also a new PaymentTypeEntity when adding an entry
             */
            if (entity.equals(ConfigurationConstants.CONFIG_PAYMENT_TYPE)) {
                PaymentTypeEntity newPaymentType = new PaymentTypeEntity(newLookupValue);
                legacyMasterDao.createOrUpdate(newPaymentType);
                StaticHibernateUtil.commitTransaction();
            }

        }

        return mapping.findForward(ActionForwards.update_success.toString());
    }
}