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

package org.mifos.config.struts.actionform;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.master.business.CustomValueListElement;
import org.mifos.application.util.helpers.Methods;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.security.util.UserContext;

public class LookupOptionsActionForm extends BaseActionForm {
    private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CONFIGURATION_LOGGER);

    private String salutation;
    private String userTitle;
    private String maritalStatus;
    private String educationLevel;
    private String citizenship;
    private String handicapped;
    private String officerTitle;
    private String ethnicity;
    private String businessActivity;
    private String purposeOfLoan;
    private String collateralType;
    private String paymentType;

    private List<CustomValueListElement> salutations;
    private List<CustomValueListElement> userTitles;
    private List<CustomValueListElement> maritalStatuses;
    private List<CustomValueListElement> ethnicities;
    private List<CustomValueListElement> educationLevels;
    private List<CustomValueListElement> citizenships;
    private List<CustomValueListElement> businessActivities;
    private List<CustomValueListElement> purposesOfLoan;
    private List<CustomValueListElement> officerTitles;
    private List<CustomValueListElement> handicappeds;
    private List<CustomValueListElement> collateralTypes;
    private List<CustomValueListElement> paymentTypes;

    private String[] salutationList;
    private String[] userTitleList;
    private String[] maritalStatusList;
    private String[] ethnicityList;
    private String[] educationLevelList;
    private String[] citizenshipList;
    private String[] businessActivityList;
    private String[] purposeOfLoanList;
    private String[] officerTitleList;
    private String[] handicappedList;
    private String[] collateralTypeList;
    private String[] paymentTypeList;
    private String lookupValue;

    public String getLookupValue() {
        return lookupValue;
    }

    public void setLookupValue(String lookupValue) {
        this.lookupValue = lookupValue;
    }

    public String[] getSalutationList() {
        return salutationList;
    }

    public void setSalutationList(String[] salutationList) {
        this.salutationList = salutationList;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getUserTitle() {
        return userTitle;
    }

    public void setUserTitle(String userTitle) {
        this.userTitle = userTitle;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public String getHandicapped() {
        return handicapped;
    }

    public void setHandicapped(String handicapped) {
        this.handicapped = handicapped;
    }

    public String getOfficerTitle() {
        return officerTitle;
    }

    public void setOfficerTitle(String officerTitle) {
        this.officerTitle = officerTitle;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getPurposeOfLoan() {
        return purposeOfLoan;
    }

    public String getBusinessActivity() {
        return businessActivity;
    }

    public void setBusinessActivity(String businessActivity) {
        this.businessActivity = businessActivity;
    }

    public void setPurposeOfLoan(String purposeOfLoan) {
        this.purposeOfLoan = purposeOfLoan;
    }

    public String getCollateralType() {
        return collateralType;
    }

    public void setCollateralType(String collateralType) {
        this.collateralType = collateralType;
    }

    public List<CustomValueListElement> getSalutations() {
        return salutations;
    }

    public void setSalutations(List<CustomValueListElement> salutations) {
        this.salutations = salutations;
    }

    public List<CustomValueListElement> getUserTitles() {
        return userTitles;
    }

    public void setUserTitles(List<CustomValueListElement> userTitles) {
        this.userTitles = userTitles;
    }

    public List<CustomValueListElement> getMaritalStatuses() {
        return maritalStatuses;
    }

    public void setMaritalStatuses(List<CustomValueListElement> maritalStatuses) {
        this.maritalStatuses = maritalStatuses;
    }

    public List<CustomValueListElement> getEthnicities() {
        return ethnicities;
    }

    public void setEthnicities(List<CustomValueListElement> ethnicities) {
        this.ethnicities = ethnicities;
    }

    public List<CustomValueListElement> getBusinessActivities() {
        return businessActivities;
    }

    public void setBusinessActivities(List<CustomValueListElement> businessActivities) {
        this.businessActivities = businessActivities;
    }

    public List<CustomValueListElement> getPurposesOfLoan() {
        return purposesOfLoan;
    }

    public void setPurposesOfLoan(List<CustomValueListElement> purposesOfLoan) {
        this.purposesOfLoan = purposesOfLoan;
    }

    public List<CustomValueListElement> getEducationLevels() {
        return educationLevels;
    }

    public void setEducationLevels(List<CustomValueListElement> educationLevels) {
        this.educationLevels = educationLevels;
    }

    public List<CustomValueListElement> getCitizenships() {
        return citizenships;
    }

    public void setCitizenships(List<CustomValueListElement> citizenships) {
        this.citizenships = citizenships;
    }

    public List<CustomValueListElement> getHandicappeds() {
        return handicappeds;
    }

    public void setHandicappeds(List<CustomValueListElement> handicappeds) {
        this.handicappeds = handicappeds;
    }

    public List<CustomValueListElement> getOfficerTitles() {
        return officerTitles;
    }

    public void setOfficerTitles(List<CustomValueListElement> officerTitles) {
        this.officerTitles = officerTitles;
    }

    public List<CustomValueListElement> getCollateralTypes() {
        return collateralTypes;
    }

    public void setCollateralTypes(List<CustomValueListElement> collateralTypes) {
        this.collateralTypes = collateralTypes;
    }


    public String getPaymentType() {
        return this.paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public List<CustomValueListElement> getPaymentTypes() {
        return this.paymentTypes;
    }

    public void setPaymentTypes(List<CustomValueListElement> paymentTypes) {
        this.paymentTypes = paymentTypes;
    }

    public String[] getPaymentTypeList() {
        return this.paymentTypeList;
    }

    public void setPaymentTypeList(String[] paymentTypeList) {
        this.paymentTypeList = paymentTypeList;
    }

    public LookupOptionsActionForm() {
        super();

    }

    public void BuildLink() {
    }

    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
    }

    private boolean containsOneNonNullElement(String[] strings) {
        if (strings == null) {
            return false;
        } else {
            return strings.length == 1 && strings[0] != null && strings[0].length() > 0;
        }
    }

    private boolean itemIsSelectedInList(String listName) {
        if (listName.equals(ConfigurationConstants.CONFIG_SALUTATION)) {
            return (containsOneNonNullElement(getSalutationList()));
        } else if (listName.equals(ConfigurationConstants.CONFIG_PERSONNEL_TITLE)) {
            return (containsOneNonNullElement(getUserTitleList()));
        } else if (listName.equals(ConfigurationConstants.CONFIG_MARITAL_STATUS)) {
            return (containsOneNonNullElement(getMaritalStatusList()));
        } else if (listName.equals(ConfigurationConstants.CONFIG_ETHNICITY)) {
            return (containsOneNonNullElement(getEthnicityList()));
        } else if (listName.equals(ConfigurationConstants.CONFIG_EDUCATION_LEVEL)) {
            return (containsOneNonNullElement(getEducationLevelList()));
        } else if (listName.equals(ConfigurationConstants.CONFIG_CITIZENSHIP)) {
            return (containsOneNonNullElement(getCitizenshipList()));
        } else if (listName.equals(ConfigurationConstants.CONFIG_BUSINESS_ACTIVITY)) {
            return (containsOneNonNullElement(getBusinessActivityList()));
        } else if (listName.equals(ConfigurationConstants.CONFIG_LOAN_PURPOSE)) {
            return (containsOneNonNullElement(getPurposesOfLoanList()));
        } else if (listName.equals(ConfigurationConstants.CONFIG_COLLATERAL_TYPE)) {
            return (containsOneNonNullElement(getCollateralTypeList()));
        } else if (listName.equals(ConfigurationConstants.CONFIG_HANDICAPPED)) {
            return (containsOneNonNullElement(getHandicappedList()));
        } else if (listName.equals(ConfigurationConstants.CONFIG_OFFICER_TITLE)) {
            return (containsOneNonNullElement(getOfficerTitleList()));
        } else if (listName.equals(ConfigurationConstants.CONFIG_PAYMENT_TYPE)) {
            return (containsOneNonNullElement(getPaymentTypeList()));
        }
        throw new RuntimeException("Got unexpected constant: \"" + listName + "\"");
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
        request.setAttribute(ConfigurationConstants.CONFIG_PAYMENT_TYPE,
                ConfigurationConstants.CONFIG_PAYMENT_TYPE);
    }

    private void checkOneList(List<CustomValueListElement> list, ActionErrors errors, String entity) {

        for (CustomValueListElement element : list) {
            if (element.getLookUpValue() != null && element.getLookUpValue().equals(this.lookupValue)) {
                addError(errors, entity, "errors.duplicatevalue", new String[] { null });
                return;
            }

        }
    }

    private void checkForDuplicate(String entity, ActionErrors errors) {
        if (entity == null) {
            throw new RuntimeException("Null entity passed to checkForDuplicate.");
        }
        if (entity.equals(ConfigurationConstants.CONFIG_SALUTATION)) {
            checkOneList(salutations, errors, entity);
        } else if (entity.equals(ConfigurationConstants.CONFIG_CITIZENSHIP)) {
            checkOneList(citizenships, errors, entity);
        } else if (entity.equals(ConfigurationConstants.CONFIG_COLLATERAL_TYPE)) {
            checkOneList(collateralTypes, errors, entity);
        } else if (entity.equals(ConfigurationConstants.CONFIG_EDUCATION_LEVEL)) {
            checkOneList(educationLevels, errors, entity);
        } else if (entity.equals(ConfigurationConstants.CONFIG_ETHNICITY)) {
            checkOneList(ethnicities, errors, entity);
        } else if (entity.equals(ConfigurationConstants.CONFIG_HANDICAPPED)) {
            checkOneList(handicappeds, errors, entity);
        } else if (entity.equals(ConfigurationConstants.CONFIG_BUSINESS_ACTIVITY)) {
            checkOneList(businessActivities, errors, entity);
        } else if (entity.equals(ConfigurationConstants.CONFIG_LOAN_PURPOSE)) {
            checkOneList(purposesOfLoan, errors, entity);
        } else if (entity.equals(ConfigurationConstants.CONFIG_MARITAL_STATUS)) {
            checkOneList(maritalStatuses, errors, entity);
        } else if (entity.equals(ConfigurationConstants.CONFIG_OFFICER_TITLE)) {
            checkOneList(officerTitles, errors, entity);
        } else if (entity.equals(ConfigurationConstants.CONFIG_PERSONNEL_TITLE)) {
            checkOneList(userTitles, errors, entity);
        } else if (entity.equals(ConfigurationConstants.CONFIG_PAYMENT_TYPE)) {
            checkOneList(paymentTypes, errors, entity);
        } else {
            throw new RuntimeException("Unrecognized configuration entity \"" + entity + "\".");
        }
    }

    public void setOneList(String entity, List<CustomValueListElement> list) {
        if (entity == null) {
            throw new RuntimeException("Null entity passed to setOneList.");
        }
        if (entity.equals(ConfigurationConstants.CONFIG_SALUTATION)) {
            setSalutations(list);
        } else if (entity.equals(ConfigurationConstants.CONFIG_CITIZENSHIP)) {
            setCitizenships(list);
        } else if (entity.equals(ConfigurationConstants.CONFIG_COLLATERAL_TYPE)) {
            setCollateralTypes(list);
        } else if (entity.equals(ConfigurationConstants.CONFIG_EDUCATION_LEVEL)) {
            setEducationLevels(list);
        } else if (entity.equals(ConfigurationConstants.CONFIG_ETHNICITY)) {
            setEthnicities(list);
        } else if (entity.equals(ConfigurationConstants.CONFIG_HANDICAPPED)) {
            setHandicappeds(list);
        } else if (entity.equals(ConfigurationConstants.CONFIG_BUSINESS_ACTIVITY)) {
            setBusinessActivities(list);
        } else if (entity.equals(ConfigurationConstants.CONFIG_LOAN_PURPOSE)) {
            setPurposesOfLoan(list);
        } else if (entity.equals(ConfigurationConstants.CONFIG_MARITAL_STATUS)) {
            setMaritalStatuses(list);
        } else if (entity.equals(ConfigurationConstants.CONFIG_OFFICER_TITLE)) {
            setOfficerTitles(list);
        } else if (entity.equals(ConfigurationConstants.CONFIG_PERSONNEL_TITLE)) {
            setUserTitles(list);
        } else if (entity.equals(ConfigurationConstants.CONFIG_PAYMENT_TYPE)) {
            setPaymentTypes(list);
        } else {
            throw new RuntimeException("Unrecognized configuration entity \"" + entity + "\".");
        }
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        logger.debug("Inside validate method");

        String method = request.getParameter(Methods.method.toString());
        request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));

        ActionErrors errors = new ActionErrors();
        if (method.equals(Methods.update.toString())) {
            errors = super.validate(mapping, request);
            request.setAttribute(ConfigurationConstants.LOOKUP_TYPE, request
                    .getParameter(ConfigurationConstants.LOOKUP_TYPE));
            if (errors.isEmpty()) // check for duplicate
            {
                String entity = request.getParameter(ConfigurationConstants.ENTITY);
                checkForDuplicate(entity, errors);
            }
        } else if (method.equals(Methods.addEditLookupOption.toString())) {
            String entity = request.getParameter(ConfigurationConstants.ENTITY);
            String addOrEdit = request.getParameter(ConfigurationConstants.ADD_OR_EDIT);
            if (addOrEdit.equals("edit") && !itemIsSelectedInList(entity)) {
                String entityType = getEntityType(entity, request);
                addError(errors, entity, "errors.selectvalue", new String[] { entityType });
                setHiddenFields(request);

            }
        }

        if (!errors.isEmpty()) {
            request.setAttribute(Methods.method.toString(), method);
        }

        logger.debug("outside validate method");
        return errors;

    }

    public void clear() {
        this.salutation = null;
        this.userTitle = null;
        this.maritalStatus = null;
        this.educationLevel = null;
        this.citizenship = null;
        this.handicapped = null;
        this.officerTitle = null;
        this.ethnicity = null;
        this.businessActivity = null;
        this.purposeOfLoan = null;
        this.collateralType = null;
        this.salutationList = null;
        this.userTitleList = null;
        this.maritalStatusList = null;
        this.ethnicityList = null;
        this.educationLevelList = null;
        this.citizenshipList = null;
        this.businessActivityList = null;
        this.purposeOfLoanList = null;
        this.officerTitleList = null;
        this.handicappedList = null;
        this.collateralTypeList = null;
        this.salutations = null;
        this.userTitles = null;
        this.maritalStatuses = null;
        this.ethnicities = null;
        this.educationLevels = null;
        this.citizenships = null;
        this.businessActivities = null;
        this.purposesOfLoan = null;
        this.officerTitles = null;
        this.handicappeds = null;
        this.collateralTypes = null;
        this.lookupValue = null;
        this.paymentType = null;

    }

    public String[] getCitizenshipList() {
        return citizenshipList;
    }

    public void setCitizenshipList(String[] citizenshipList) {
        this.citizenshipList = citizenshipList;
    }

    public String[] getCollateralTypeList() {
        return collateralTypeList;
    }

    public void setCollateralTypeList(String[] collateralTypeList) {
        this.collateralTypeList = collateralTypeList;
    }

    public String[] getEducationLevelList() {
        return educationLevelList;
    }

    public void setEducationLevelList(String[] educationLevelList) {
        this.educationLevelList = educationLevelList;
    }

    public String[] getEthnicityList() {
        return ethnicityList;
    }

    public void setEthnicityList(String[] ethnicityList) {
        this.ethnicityList = ethnicityList;
    }

    public String[] getHandicappedList() {
        return handicappedList;
    }

    public void setHandicappedList(String[] handicappedList) {
        this.handicappedList = handicappedList;
    }

    public String[] getMaritalStatusList() {
        return maritalStatusList;
    }

    public void setMaritalStatusList(String[] maritalStatusList) {
        this.maritalStatusList = maritalStatusList;
    }

    public String[] getOfficerTitleList() {
        return officerTitleList;
    }

    public void setOfficerTitleList(String[] officerTitleList) {
        this.officerTitleList = officerTitleList;
    }

    public String[] getBusinessActivityList() {
        return businessActivityList;
    }

    public void setBusinessActivityList(String[] businessActivityList) {
        this.businessActivityList = businessActivityList;
    }

    public String[] getPurposesOfLoanList() {
        return purposeOfLoanList;
    }

    public void setPurposesOfLoanList(String[] purposeOfLoanList) {
        this.purposeOfLoanList = purposeOfLoanList;
    }

    public String[] getUserTitleList() {
        return userTitleList;
    }

    public void setUserTitleList(String[] userTitleList) {
        this.userTitleList = userTitleList;
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

    private String getEntityType(String entity, HttpServletRequest request) {
        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.CONFIGURATION_UI_RESOURCE_PROPERTYFILE,
                getUserLocale(request));
        String entityType = null;
        if (entity.equals(ConfigurationConstants.CONFIG_SALUTATION)) {
            entityType = resources.getString("configuration.salutation");
        } else if (entity.equals(ConfigurationConstants.CONFIG_CITIZENSHIP)) {
            entityType = resources.getString("configuration.citizenship");
        } else if (entity.equals(ConfigurationConstants.CONFIG_COLLATERAL_TYPE)) {
            entityType = resources.getString("configuration.collateraltype");
        } else if (entity.equals(ConfigurationConstants.CONFIG_PAYMENT_TYPE)) {
            entityType = resources.getString("configuration.paymentmodes");
        } else if (entity.equals(ConfigurationConstants.CONFIG_EDUCATION_LEVEL)) {
            entityType = resources.getString("configuration.educationlevel");
        } else if (entity.equals(ConfigurationConstants.CONFIG_ETHNICITY)) {
            entityType = resources.getString("configuration.ethnicity");
        } else if (entity.equals(ConfigurationConstants.CONFIG_HANDICAPPED)) {
            entityType = resources.getString("configuration.handicapped");
        } else if (entity.equals(ConfigurationConstants.CONFIG_BUSINESS_ACTIVITY)) {
            entityType = resources.getString("configuration.businessactivity");
        } else if (entity.equals(ConfigurationConstants.CONFIG_LOAN_PURPOSE)) {
            entityType = resources.getString("configuration.purposeofloan");
        } else if (entity.equals(ConfigurationConstants.CONFIG_MARITAL_STATUS)) {
            entityType = resources.getString("configuration.maritalstatus");
        } else if (entity.equals(ConfigurationConstants.CONFIG_OFFICER_TITLE)) {
            entityType = resources.getString("configuration.officertitle");
        } else if (entity.equals(ConfigurationConstants.CONFIG_PERSONNEL_TITLE)) {
            entityType = resources.getString("configuration.usertitle");
        } else {
            entityType = "";
        }
        return entityType;
    }

}
