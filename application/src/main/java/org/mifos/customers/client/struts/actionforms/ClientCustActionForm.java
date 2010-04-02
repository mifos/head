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

package org.mifos.customers.client.struts.actionforms;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.ClientRules;
import org.mifos.customers.center.util.helpers.ValidateMethods;
import org.mifos.customers.client.business.ClientDetailView;
import org.mifos.customers.client.business.ClientFamilyDetailView;
import org.mifos.customers.client.business.ClientNameDetailView;
import org.mifos.customers.client.business.FamilyDetailDTO;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.customers.struts.actionforms.CustomerActionForm;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.SavingsDetailDto;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigurationConstant;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigurationHelper;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.security.util.UserContext;

public class ClientCustActionForm extends CustomerActionForm {

    private MeetingBO parentCustomerMeeting;

    private String groupFlag;
    private List<FamilyDetailDTO> familyDetailBean = new ArrayList<FamilyDetailDTO>();
    private ClientDetailView clientDetailView;
    private ClientNameDetailView clientName;
    private ClientNameDetailView spouseName;

    private String parentGroupId;
    private String centerDisplayName;
    private String groupDisplayName;
    private String governmentId;
    private String dateOfBirthDD;
    private String dateOfBirthMM;
    private String dateOfBirthYY;
    private String nextOrPreview;
    private String editFamily;
    private String gotErrorInPage;
    private String deleteThisRow;
    private FormFile picture;
    private InputStream customerPicture;
    private int age;
    private final List<Short> selectedOfferings;
    private List<ClientNameDetailView> familyNames;
    private List<ClientFamilyDetailView> familyDetails;
    private int[] relativePrimaryKey = new int[ClientRules.getMaximumNumberOfFamilyMembers()];

    // family details
    private List<Short> familyRelationship;
    private List<String> familyFirstName;
    private List<String> familyMiddleName;
    private List<String> familyLastName;
    private List<String> familyDateOfBirthDD;
    private List<String> familyDateOfBirthMM;
    private List<String> familyDateOfBirthYY;
    private List<Short> familyGender;
    private List<Short> familyLivingStatus;
    private List<Integer> familyPrimaryKey;
    private List<String> familyDateOfBirth;

    private String loanOfficerName;

    public ClientCustActionForm() {
        super();
        selectedOfferings = new ArrayList<Short>(ClientConstants.MAX_OFFERINGS_SIZE);
        for (int i = 0; i < ClientConstants.MAX_OFFERINGS_SIZE; i++) {
            selectedOfferings.add(null);
        }

        initializeFamilyMember();
        addFamilyMember();
    }

    public List<ClientFamilyDetailView> getFamilyDetails() {
        return this.familyDetails;
    }

    public void setFamilyDetails(List<ClientFamilyDetailView> familyDetails) {
        this.familyDetails = familyDetails;
    }

    public void setFamilyNames(List<ClientNameDetailView> familyNames) {
        this.familyNames = familyNames;
    }

    public int[] getRelativePrimaryKey() {
        return this.relativePrimaryKey;
    }

    public void setRelativePrimaryKey(int[] relativePrimaryKey) {
        this.relativePrimaryKey = relativePrimaryKey;
    }

    public String getDeleteThisRow() {
        return this.deleteThisRow;
    }

    public void setDeleteThisRow(String deleteThisRow) {
        this.deleteThisRow = deleteThisRow;
    }

    public String getGotErrorInPage() {
        return this.gotErrorInPage;
    }

    public void setGotErrorInPage(String gotErrorInPage) {
        this.gotErrorInPage = gotErrorInPage;
    }

    public String getEditFamily() {
        return this.editFamily;
    }

    public void setEditFamily(String editFamily) {
        this.editFamily = editFamily;
    }

    public List<FamilyDetailDTO> getFamilyDetailBean() {
        return this.familyDetailBean;
    }

    public void setFamilyDetailBean(List<FamilyDetailDTO> familyDetailBean) {
        this.familyDetailBean = familyDetailBean;
    }

    /*
     * This is used to construct Name detail view and family detail view for each family member, Please note i have
     * added display name into family detail view so that i can iterate over only one collection when i do preview of
     * this page.
     */
    public void constructFamilyDetails() {
        this.familyDetails = new ArrayList<ClientFamilyDetailView>();
        this.familyNames = new ArrayList<ClientNameDetailView>();
        for (int row = 0; row < familyFirstName.size(); row++) {
            ClientNameDetailView familyNames = new ClientNameDetailView();
            familyNames.setFirstName(getFamilyFirstName(row));
            familyNames.setMiddleName(getFamilyMiddleName(row));
            familyNames.setLastName(getFamilyLastName(row));
            familyNames.setNameType(getFamilyRelationship(row));
            familyNames.setDisplayName(new StringBuilder(getFamilyFirstName(row) + getFamilyLastName(row)));
            ClientFamilyDetailView familyDetails = null;

            try {
                if (getFamilyDateOfBirth(row) != null) {
                    familyDetails = new ClientFamilyDetailView(getFamilyRelationship(row), getFamilyGender(row),
                            getFamilyLivingStatus(row), DateUtils.getDateAsSentFromBrowser(getFamilyDateOfBirth(row)));
                    familyDetails.setDisplayName(familyNames.getDisplayName());
                } else {
                    familyDetails = new ClientFamilyDetailView(getFamilyRelationship(row), getFamilyGender(row),
                            getFamilyLivingStatus(row), null);
                    familyDetails.setDisplayName(familyNames.getDisplayName());
                }

            } catch (InvalidDateException e) {
                // FIXME - empty catch!
            }
            this.familyNames.add(familyNames);
            this.familyDetails.add(familyDetails);
        }
    }

    public String removeSpaces(String s) {
        StringTokenizer st = new StringTokenizer(s, " ", false);
        String t = "";
        while (st.hasMoreElements()) {
            t += st.nextElement();
        }
        return t;
    }

    public List<ClientNameDetailView> getFamilyNames() {
        return this.familyNames;
    }

    public String getGroupFlag() {
        return groupFlag;
    }

    public Short getGroupFlagValue() {
        return getShortValue(groupFlag);
    }

    public void setGroupFlag(String groupFlag) {
        this.groupFlag = groupFlag;
    }

    public ClientDetailView getClientDetailView() {
        return clientDetailView;
    }

    public void setClientDetailView(ClientDetailView clientDetailView) {
        this.clientDetailView = clientDetailView;
    }

    public ClientNameDetailView getClientName() {
        return clientName;
    }

    public void setClientName(ClientNameDetailView clientName) {
        this.clientName = clientName;
    }

    public ClientNameDetailView getSpouseName() {
        return spouseName;
    }

    public void setSpouseName(ClientNameDetailView spouseName) {
        this.spouseName = spouseName;
    }

    public String getParentGroupId() {
        return parentGroupId;
    }

    public void setParentGroupId(String parentGroupId) {
        this.parentGroupId = parentGroupId;
    }

    public String getGovernmentId() {
        return governmentId;
    }

    public void setGovernmentId(String governmentId) {
        this.governmentId = governmentId;
    }

    public FormFile getPicture() {
        return picture;
    }

    public String getNextOrPreview() {
        return nextOrPreview;
    }

    public void setNextOrPreview(String nextOrPreview) {
        this.nextOrPreview = nextOrPreview;
    }

    public void setPicture(FormFile picture) {
        this.picture = picture;
        try {
            customerPicture = picture.getInputStream();
        } catch (IOException ioe) {

        }
    }

    public InputStream getCustomerPicture() {
        return customerPicture;
    }

    public int getAge() {
        return age;

    }

    public void setAge(int age) {
        this.age = age;

    }

    public List<Short> getSelectedOfferings() {
        return selectedOfferings;
    }

    public Short getSavingsOffering(int i) {
        return (i < ClientConstants.MAX_OFFERINGS_SIZE) ? selectedOfferings.get(i) : null;
    }

    public void setSavingsOffering(int i, Short value) {
        if (i < ClientConstants.MAX_OFFERINGS_SIZE) {
            selectedOfferings.set(i, value);
        }
    }

    @Override
    protected ActionErrors validateFields(HttpServletRequest request, String method) throws ApplicationException {
        ActionErrors errors = new ActionErrors();
        UserContext userContext = (UserContext) request.getSession().getAttribute(LoginConstants.USERCONTEXT);
        Locale locale = userContext.getPreferredLocale();
        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.CUSTOMER_UI_RESOURCE_PROPERTYFILE, locale);
        if ((method.equals(Methods.previewPersonalInfo.toString()) || method.equals(Methods.next.toString()) || method
                .equals(Methods.previewEditPersonalInfo.toString()))
                && (ClientConstants.INPUT_PERSONAL_INFO.equals(input) || ClientConstants.INPUT_EDIT_PERSONAL_INFO
                        .equals(input))) {
            validateClientNames(errors, resources);
            validateDateOfBirth(errors, resources);
            validateGender(errors, resources);
            if (!ClientRules.isFamilyDetailsRequired()) {
                validateSpouseNames(errors, resources);
            }
            checkForMandatoryFields(EntityType.CLIENT.getValue(), errors, request);
            validateCustomFieldsForCustomers(request, errors);
            validatePicture(request, errors);
        }
        if (method.equals(Methods.preview.toString()) && ClientConstants.INPUT_MFI_INFO.equals(input)) {
            validateFormedByPersonnel(errors);
            validateConfigurableMandatoryFields(request, errors, EntityType.CLIENT);
            validateTrained(request, errors);
            validateFees(request, errors);
            validateSelectedOfferings(errors, request);
        }

        if (method.equals(Methods.previewEditMfiInfo.toString())) {
            checkForMandatoryFields(EntityType.CLIENT.getValue(), errors, request);
            validateTrained(request, errors);
        }
        if (method.equals(Methods.updateMfiInfo.toString())) {
            validateTrained(request, errors);
        }
        if (method.equals(Methods.familyInfoNext.toString())) {
            validateFamilyDateOfBirths(errors);
            validateFamilyNames(errors);
            validateFamilyOneSpouseOrFather(errors);
            validateFamilyGender(errors);
            validateFamilyRelationship(errors);
            validateFamilyLivingStatus(errors);

        }
        if (method.equals(Methods.prevFamilyInfoNext.toString())) {
            validateFamilyDateOfBirths(errors);
            validateFamilyNames(errors);
            validateFamilyOneSpouseOrFather(errors);
            validateFamilyGender(errors);
            validateFamilyRelationship(errors);
            validateFamilyLivingStatus(errors);
        }
        if (method.equals(Methods.addFamilyRow.toString())) {
            validateNumberOfFamilyMembers(errors);
        }
        if (method.equals(Methods.editAddFamilyRow.toString())) {
            validateNumberOfFamilyMembers(errors);
        }
        if (method.equals(Methods.previewEditFamilyInfo.toString())) {
            validateFamilyDateOfBirths(errors);
            validateFamilyNames(errors);
            validateFamilyOneSpouseOrFather(errors);
            validateFamilyGender(errors);
            validateFamilyRelationship(errors);
            validateFamilyLivingStatus(errors);
        }
        return errors;
    }

    private void validatePicture(HttpServletRequest request, ActionErrors errors) throws PageExpiredException {
        if (picture != null) {
            String fileName = picture.getFileName();
            if (picture.getFileSize() > ClientConstants.PICTURE_ALLOWED_SIZE) {
                errors.add(ClientConstants.PICTURE_SIZE_EXCEPTION, new ActionMessage(
                        ClientConstants.PICTURE_SIZE_EXCEPTION));
            }
            if (!ValidateMethods.isNullOrBlank(fileName)) {
                String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
                if (!(fileExtension.equalsIgnoreCase("jpeg") || fileExtension.equalsIgnoreCase("jpg"))) {
                    errors.add(ClientConstants.PICTURE_EXCEPTION, new ActionMessage(ClientConstants.PICTURE_EXCEPTION));
                }
            }
            if (picture.getFileSize() == 0 || picture.getFileSize() < 0) {
                SessionUtils.setAttribute("noPicture", "Yes", request);
            } else {
                SessionUtils.setAttribute("noPicture", "No", request);
            }
        }
    }

    private void validateGender(ActionErrors errors, ResourceBundle resources) {
        if (clientDetailView.getGender() == null) {
            errors.add(CustomerConstants.GENDER, new ActionMessage(CustomerConstants.ERRORS_MANDATORY, resources
                    .getString("Customer.Gender")));
        }
    }

    private void validateClientNames(ActionErrors errors, ResourceBundle resources) {
        if (clientName.getSalutation() == null) {
            errors.add(CustomerConstants.SALUTATION, new ActionMessage(CustomerConstants.ERRORS_MANDATORY, resources
                    .getString("Customer.Salutation")));
        }
        if (StringUtils.isBlank(clientName.getFirstName())) {
            errors.add(CustomerConstants.FIRST_NAME, new ActionMessage(CustomerConstants.ERRORS_MANDATORY, resources
                    .getString("Customer.FirstName")));
        }
        if (StringUtils.isBlank(clientName.getLastName())) {
            errors.add(CustomerConstants.LAST_NAME, new ActionMessage(CustomerConstants.ERRORS_MANDATORY, resources
                    .getString("Customer.LastName")));
        }
    }

    private void validateSpouseNames(ActionErrors errors, ResourceBundle resources) {
        if (spouseName.getNameType() == null) {
            errors.add(CustomerConstants.SPOUSE_TYPE, new ActionMessage(CustomerConstants.ERRORS_MANDATORY, resources
                    .getString("Customer.SpouseType")));
        }
        if (StringUtils.isBlank(spouseName.getFirstName())) {
            errors.add(CustomerConstants.SPOUSE_FIRST_NAME, new ActionMessage(CustomerConstants.ERRORS_MANDATORY,
                    resources.getString("Customer.SpouseFirstName")));
        }
        if (StringUtils.isBlank(spouseName.getLastName())) {
            errors.add(CustomerConstants.SPOUSE_LAST_NAME, new ActionMessage(CustomerConstants.ERRORS_MANDATORY,
                    resources.getString("Customer.SpouseLastName")));
        }
    }

    void validateDateOfBirth(ActionErrors errors) {
        validateDateOfBirth(errors, null);
    }

    void validateDateOfBirth(ActionErrors errors, ResourceBundle resources) {
        if (StringUtils.isBlank(getDateOfBirth())) {
            if (resources == null) {
                errors.add(CustomerConstants.DOB, new ActionMessage(CustomerConstants.ERRORS_MANDATORY,
                        CustomerConstants.DOB));
            } else {
                errors.add(CustomerConstants.DOB, new ActionMessage(CustomerConstants.ERRORS_MANDATORY, resources
                        .getString("Customer.DateOfBirth")));
            }
        } else if (!isValid(getDateOfBirthDD()) || !isValid(getDateOfBirthMM()) || !isValid(getDateOfBirthYY())) {
            errors.add(ClientConstants.INVALID_DOB_EXCEPTION, new ActionMessage(ClientConstants.INVALID_DOB_EXCEPTION));
        }

        else {
            try {
                Date date = DateUtils.getDateAsSentFromBrowser(getDateOfBirth());
                setAge(DateUtils.DateDiffInYears(DateUtils.getDateAsSentFromBrowser(getDateOfBirth())));
                if (DateUtils.whichDirection(date) > 0) {
                    errors.add(ClientConstants.FUTURE_DOB_EXCEPTION, new ActionMessage(
                            ClientConstants.FUTURE_DOB_EXCEPTION));
                } else if (ClientRules.isAgeCheckEnabled()) {
                    if (getAge() > ClientRules.getMaximumAgeForNewClient()
                            || getAge() < ClientRules.getMinimumAgeForNewClient()) {
                        errors.add(ClientConstants.INVALID_AGE, new ActionMessage(ClientConstants.INVALID_AGE,
                                ClientRules.getMinimumAgeForNewClient(), ClientRules.getMaximumAgeForNewClient()));
                    }
                }
            } catch (InvalidDateException e) {
                errors.add(ClientConstants.INVALID_DOB_EXCEPTION, new ActionMessage(
                        ClientConstants.INVALID_DOB_EXCEPTION));
            }

        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void checkForMandatoryFields(Short entityId, ActionErrors errors, HttpServletRequest request) {
        Map<Short, List<FieldConfigurationEntity>> entityMandatoryFieldMap = (Map<Short, List<FieldConfigurationEntity>>) request
                .getSession().getServletContext().getAttribute(Constants.FIELD_CONFIGURATION);

        List<FieldConfigurationEntity> mandatoryfieldList = entityMandatoryFieldMap.get(entityId);
        for (FieldConfigurationEntity fieldConfigurationEntity : mandatoryfieldList) {
            String propertyName = request.getParameter(fieldConfigurationEntity.getLabel());
            UserContext userContext = ((UserContext) request.getSession().getAttribute(LoginConstants.USERCONTEXT));

            if (propertyName != null && !propertyName.equals("") && !propertyName.equalsIgnoreCase("picture")) {
                String propertyValue = request.getParameter(propertyName);
                if (propertyValue == null || propertyValue.equals("")) {
                    errors.add(fieldConfigurationEntity.getLabel(), new ActionMessage(
                            FieldConfigurationConstant.EXCEPTION_MANDATORY, FieldConfigurationHelper
                                    .getLocalSpecificFieldNames(fieldConfigurationEntity.getLabel(), userContext)));
                }
            } else if (propertyName != null && !propertyName.equals("") && propertyName.equalsIgnoreCase("picture")) {
                try {
                    if (getCustomerPicture() == null || getCustomerPicture().read() == -1) {
                        errors.add(fieldConfigurationEntity.getLabel(), new ActionMessage(
                                FieldConfigurationConstant.EXCEPTION_MANDATORY, FieldConfigurationHelper
                                        .getLocalSpecificFieldNames(fieldConfigurationEntity.getLabel(), userContext)));
                    }
                    getCustomerPicture().reset();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void validateSelectedOfferings(ActionErrors errors, HttpServletRequest request) {
        boolean duplicateFound = false;
        for (int i = 0; i < selectedOfferings.size() - 1; i++) {
            for (int j = i + 1; j < selectedOfferings.size(); j++) {
                if (selectedOfferings.get(i) != null && selectedOfferings.get(j) != null
                        && selectedOfferings.get(i).equals(selectedOfferings.get(j))) {
                    String selectedOffering = "";
                    try {
                        List<SavingsDetailDto> offeringsList = (List<SavingsDetailDto>) SessionUtils.getAttribute(
                                ClientConstants.SAVINGS_OFFERING_LIST, request);
                        for (SavingsDetailDto savingsOffering : offeringsList) {
                            if (selectedOfferings.get(i).equals(savingsOffering.getPrdOfferingId())) {
                                selectedOffering = savingsOffering.getPrdOfferingName();
                            }
                            break;
                        }
                    } catch (PageExpiredException pee) {
                    }
                    errors.add(ClientConstants.ERRORS_DUPLICATE_OFFERING_SELECTED, new ActionMessage(
                            ClientConstants.ERRORS_DUPLICATE_OFFERING_SELECTED, selectedOffering));
                    duplicateFound = true;
                    break;
                }
            }
            if (duplicateFound) {
                break;
            }
        }
    }

    public void validateNumberOfFamilyMembers(ActionErrors errors) {
        if (familyFirstName.size() == ClientRules.getMaximumNumberOfFamilyMembers()) {
            errors
                    .add(ClientConstants.INVALID_NUMBER_OF_FAMILY_MEMBERS, new ActionMessage(
                            ClientConstants.INVALID_NUMBER_OF_FAMILY_MEMBERS, 1, ClientRules
                                    .getMaximumNumberOfFamilyMembers()));
        }
    }

    public void validateFamilyNames(ActionErrors errors) {

        for (int row = 0; row < familyFirstName.size(); row++) {
            if (StringUtils.isBlank(familyFirstName.get(row))) {
                errors.add(ClientConstants.INVALID_FAMILY_FIRST_NAME, new ActionMessage(
                        ClientConstants.INVALID_FAMILY_FIRST_NAME));
                break;
            }
            if (StringUtils.isBlank(familyLastName.get(row))) {
                errors.add(ClientConstants.INVALID_FAMILY_LAST_NAME, new ActionMessage(
                        ClientConstants.INVALID_FAMILY_LAST_NAME));
                break;
            }
        }
    }

    public void validateFamilyDateOfBirths(ActionErrors errors) {
        // Setting date of births for family members before validating it.
        setFamilyDateOfBirth();
        for (int row = 0; row < familyDateOfBirth.size(); row++) {
            if (StringUtils.isBlank(familyDateOfBirth.get(row))) {
                errors.add(CustomerConstants.DOB, new ActionMessage(CustomerConstants.ERRORS_MANDATORY,
                        CustomerConstants.DOB));
                break;
            } else if (!isValid(familyDateOfBirthDD.get(row)) || !isValid(familyDateOfBirthMM.get(row))
                    || !isValid(familyDateOfBirthYY.get(row))) {
                errors.add(ClientConstants.INVALID_FAMILY_DOB_EXCPETION, new ActionMessage(
                        ClientConstants.INVALID_FAMILY_DOB_EXCPETION, row + 1));
                break;
            } else {
                Date date;
                try {
                    date = DateUtils.getDateAsSentFromBrowser(familyDateOfBirth.get(row));
                    if (DateUtils.whichDirection(date) > 0) {
                        errors.add(ClientConstants.FUTURE_DOB_EXCEPTION, new ActionMessage(
                                ClientConstants.FUTURE_DOB_EXCEPTION));
                        break;
                    }
                } catch (InvalidDateException e) {
                    errors.add(ClientConstants.INVALID_FAMILY_DOB_EXCPETION, new ActionMessage(
                            ClientConstants.INVALID_FAMILY_DOB_EXCPETION, row + 1));
                }
            }
        }
    }

    public void validateFamilyGender(ActionErrors errors) {
        for (int row = 0; row < familyGender.size(); row++) {
            if (familyGender.get(row) == null) {
                errors.add(ClientConstants.INVALID_FAMILY_GENDER, new ActionMessage(
                        ClientConstants.INVALID_FAMILY_GENDER));
                break;
            }
        }
    }

    public void validateFamilyRelationship(ActionErrors errors) {
        for (int row = 0; row < familyRelationship.size(); row++) {
            if (familyRelationship.get(row) == null) {
                errors.add(ClientConstants.INVALID_FAMILY_RELATIONSHIP, new ActionMessage(
                        ClientConstants.INVALID_FAMILY_RELATIONSHIP));
                break;
            }
        }
    }

    public void validateFamilyLivingStatus(ActionErrors errors) {
        for (int row = 0; row < familyLivingStatus.size(); row++) {
            if (familyLivingStatus.get(row) == null) {
                errors.add(ClientConstants.INVALID_FAMILY_LIVING_STATUS, new ActionMessage(
                        ClientConstants.INVALID_FAMILY_LIVING_STATUS));
                break;
            }
        }
    }

    public void validateFamilyOneSpouseOrFather(ActionErrors errors) {
        int spouseCount = 0;
        int fatherCount = 0;
        for (int row = 0; row < familyRelationship.size(); row++) {
            if (familyRelationship.get(row) != null) {
                if (familyRelationship.get(row) == 2) {
                    fatherCount++;
                }
                if (familyRelationship.get(row) == 1) {
                    spouseCount++;
                }
            }
        }
        if (spouseCount > 1) {
            errors.add(ClientConstants.INVALID_NUMBER_OF_SPOUSES, new ActionMessage(
                    ClientConstants.INVALID_NUMBER_OF_SPOUSES));
        }
        if (fatherCount > 1) {
            errors.add(ClientConstants.INVALID_NUMBER_OF_FATHERS, new ActionMessage(
                    ClientConstants.INVALID_NUMBER_OF_FATHERS));
        }
    }

    @Override
    protected MeetingBO getCustomerMeeting(HttpServletRequest request) throws ApplicationException {

        if (groupFlag.equals(ClientConstants.YES) && this.parentCustomerMeeting != null) {
            return parentCustomerMeeting;
        }

        return (MeetingBO) SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING, request);
    }

    public String getDateOfBirth() {
        if (StringUtils.isNotBlank(dateOfBirthDD) && StringUtils.isNotBlank(dateOfBirthMM)
                && StringUtils.isNotBlank(dateOfBirthYY)) {
            String dateSeparator = new LocalizationConverter().getDateSeparatorForCurrentLocale();
            return dateOfBirthDD + dateSeparator + dateOfBirthMM + dateSeparator + dateOfBirthYY;
        }

        return null;
    }

    public String getDateOfBirth(String dateOfBirthDD, String dateOfBirthMM, String dateOfBirthYY) {
        if (StringUtils.isNotBlank(dateOfBirthDD) && StringUtils.isNotBlank(dateOfBirthMM)
                && StringUtils.isNotBlank(dateOfBirthYY)) {
            String dateSeparator = new LocalizationConverter().getDateSeparatorForCurrentLocale();
            return dateOfBirthDD + dateSeparator + dateOfBirthMM + dateSeparator + dateOfBirthYY;
        }

        return null;
    }

    public void setDateOfBirth(String receiptDate) throws InvalidDateException {
        if (StringUtils.isBlank(receiptDate)) {
            dateOfBirthDD = null;
            dateOfBirthMM = null;
            dateOfBirthYY = null;
        } else {
            Calendar cal = new GregorianCalendar();
            java.sql.Date date = DateUtils.getDateAsSentFromBrowser(receiptDate);
            cal.setTime(date);
            dateOfBirthDD = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
            dateOfBirthMM = Integer.toString(cal.get(Calendar.MONTH) + 1);
            dateOfBirthYY = Integer.toString(cal.get(Calendar.YEAR));
        }
    }

    public String getDateOfBirthDD() {
        return dateOfBirthDD;
    }

    public void setDateOfBirthDD(String dateOfBirthDD) {
        this.dateOfBirthDD = dateOfBirthDD;
    }

    public String getDateOfBirthMM() {
        return dateOfBirthMM;
    }

    public void setDateOfBirthMM(String dateOfBirthMM) {
        this.dateOfBirthMM = dateOfBirthMM;
    }

    public String getDateOfBirthYY() {
        return dateOfBirthYY;
    }

    public void setDateOfBirthYY(String dateOfBirthYY) {
        this.dateOfBirthYY = dateOfBirthYY;
    }

    private boolean isValid(Object input) {
        try {
            String inputString = (String) input;
            Integer.parseInt(inputString);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // FirstName
    public List<String> getFamilyFirstName() {
        return this.familyFirstName;
    }

    public void setFamilyFirstName(List<String> familyFirstName) {
        this.familyFirstName = familyFirstName;
    }

    public String getFamilyFirstName(int forMember) {
        return familyFirstName.get(forMember);
    }

    public void setFamilyFirstName(int forMember, String value) {
        if (forMember < familyFirstName.size()) {
            familyFirstName.set(forMember, value);
        }
    }

    // RelationShip
    public List<Short> getFamilyRelationship() {
        return this.familyRelationship;
    }

    public void setFamilyRelationship(List<Short> familyRelationship) {
        this.familyRelationship = familyRelationship;
    }

    public Short getFamilyRelationship(int forMember) {
        return familyRelationship.get(forMember);
    }

    public void setFamilyRelationship(int forMember, Short value) {
        if (forMember < familyRelationship.size()) {
            familyRelationship.set(forMember, value);
        }
    }

    // MiddleName
    public List<String> getFamilyMiddleName() {
        return this.familyMiddleName;
    }

    public void setFamilyMiddleName(List<String> familyMiddleName) {
        this.familyMiddleName = familyMiddleName;
    }

    public String getFamilyMiddleName(int forMember) {
        return familyMiddleName.get(forMember);
    }

    public void setFamilyMiddleName(int forMember, String value) {
        if (forMember < familyMiddleName.size()) {
            familyMiddleName.set(forMember, value);
        }
    }

    // LastName
    public List<String> getFamilyLastName() {
        return this.familyLastName;
    }

    public void setFamilyLastName(List<String> familyLastName) {
        this.familyLastName = familyLastName;
    }

    public String getFamilyLastName(int forMember) {
        return familyLastName.get(forMember);
    }

    public void setFamilyLastName(int forMember, String value) {
        if (forMember < familyLastName.size()) {
            familyLastName.set(forMember, value);
        }
    }

    // DOB-DD
    public List<String> getFamilyDateOfBirthDD() {
        return this.familyDateOfBirthDD;
    }

    public void setFamilyDateOfBirthDD(List<String> familyDateOfBirthDD) {
        this.familyDateOfBirthDD = familyDateOfBirthDD;
    }

    public String getFamilyDateOfBirthDD(int forMember) {
        return familyDateOfBirthDD.get(forMember);
    }

    public void setFamilyDateOfBirthDD(int forMember, String value) {
        if (forMember < familyDateOfBirthDD.size()) {
            familyDateOfBirthDD.set(forMember, value);
        }
    }

    // DOB-MM
    public List<String> getFamilyDateOfBirthMM() {
        return this.familyDateOfBirthMM;
    }

    public void setFamilyDateOfBirthMM(List<String> familyDateOfBirthMM) {
        this.familyDateOfBirthMM = familyDateOfBirthMM;
    }

    public String getFamilyDateOfBirthMM(int forMember) {
        return familyDateOfBirthMM.get(forMember);
    }

    public void setFamilyDateOfBirthMM(int forMember, String value) {
        if (forMember < familyDateOfBirthMM.size()) {
            familyDateOfBirthMM.set(forMember, value);
        }
    }

    // DOB-YY
    public List<String> getFamilyDateOfBirthYY() {
        return this.familyDateOfBirthYY;
    }

    public void setFamilyDateOfBirthYY(List<String> familyDateOfBirthYY) {
        this.familyDateOfBirthYY = familyDateOfBirthYY;
    }

    public String getFamilyDateOfBirthYY(int forMember) {
        return familyDateOfBirthYY.get(forMember);
    }

    public void setFamilyDateOfBirthYY(int forMember, String value) {
        if (forMember < familyDateOfBirthYY.size()) {
            familyDateOfBirthYY.set(forMember, value);
        }
    }

    // Gender
    public List<Short> getFamilyGender() {
        return this.familyGender;
    }

    public void setFamilyGender(List<Short> familyGender) {
        this.familyGender = familyGender;
    }

    public Short getFamilyGender(int forMember) {
        return familyGender.get(forMember);
    }

    public void setFamilyGender(int forMember, Short value) {
        if (forMember < familyGender.size()) {
            familyGender.set(forMember, value);
        }
    }

    // Living Status
    public List<Short> getFamilyLivingStatus() {
        return this.familyLivingStatus;
    }

    public void setFamilyLivingStatus(List<Short> familyLivingStatus) {
        this.familyLivingStatus = familyLivingStatus;
    }

    public Short getFamilyLivingStatus(int forMember) {
        return familyLivingStatus.get(forMember);
    }

    public void setFamilyLivingStatus(int forMember, Short value) {
        if (forMember < familyLivingStatus.size()) {
            familyLivingStatus.set(forMember, value);
        }
    }

    public List<Integer> getFamilyPrimaryKey() {
        return this.familyPrimaryKey;
    }

    public void setFamilyPrimaryKey(List<Integer> familyPrimaryKey) {
        this.familyPrimaryKey = familyPrimaryKey;
    }

    public Integer getFamilyPrimaryKey(int forMember) {
        return familyPrimaryKey.get(forMember);
    }

    public void setFamilyPrimaryKey(int forMember, Integer value) {
        familyPrimaryKey.set(forMember, value);
    }

    public void initializeFamilyMember() {
        // relationship
        familyRelationship = new ArrayList<Short>();

        // fistName
        familyFirstName = new ArrayList<String>();

        // middleName
        familyMiddleName = new ArrayList<String>();

        // lastName
        familyLastName = new ArrayList<String>();

        // DOB-DD
        familyDateOfBirthDD = new ArrayList<String>();

        // DOB-MM
        familyDateOfBirthMM = new ArrayList<String>();

        // DOB-YY
        familyDateOfBirthYY = new ArrayList<String>();

        // Gender
        familyGender = new ArrayList<Short>();

        // LivingStatus
        familyLivingStatus = new ArrayList<Short>();

        // Primary keys
        familyPrimaryKey = new ArrayList<Integer>();
    }

    public void addFamilyMember() {
        familyPrimaryKey.add(null);
        familyRelationship.add(null);
        familyFirstName.add(null);
        familyMiddleName.add(null);
        familyLastName.add(null);
        familyDateOfBirthDD.add(null);
        familyDateOfBirthMM.add(null);
        familyDateOfBirthYY.add(null);
        familyGender.add(null);
        familyLivingStatus.add(null);
    }

    public void removeFamilyMember(int member) {
        familyPrimaryKey.remove(member);
        familyRelationship.remove(member);
        familyFirstName.remove(member);
        familyMiddleName.remove(member);
        familyLastName.remove(member);
        familyDateOfBirthDD.remove(member);
        familyDateOfBirthMM.remove(member);
        familyDateOfBirthYY.remove(member);
        familyGender.remove(member);
        familyLivingStatus.remove(member);
    }

    public int getFamilySize() {
        return familyFirstName.size() - 1;
    }

    public List<String> getFamilyDateOfBirth() {
        return this.familyDateOfBirth;
    }

    public String getFamilyDateOfBirth(int forMember) {
        return this.familyDateOfBirth.get(forMember);
    }

    /*
     * I set date of Birth so that it can be used by the construct family details function
     */
    public void setFamilyDateOfBirth() {
        this.familyDateOfBirth = new ArrayList<String>();
        for (int forMember = 0; forMember < familyDateOfBirthDD.size(); forMember++) {
            this.familyDateOfBirth.add(getDateOfBirth(getFamilyDateOfBirthDD(forMember),
                    getFamilyDateOfBirthMM(forMember), getFamilyDateOfBirthYY(forMember)));
        }
    }

    public boolean isGroupFlagSet() {
        return YesNoFlag.NO.getValue().equals(this.getGroupFlagValue());
    }

    public String getCenterDisplayName() {
        return this.centerDisplayName;
    }

    public void setCenterDisplayName(String centerDisplayName) {
        this.centerDisplayName = centerDisplayName;
    }

    public String getGroupDisplayName() {
        return this.groupDisplayName;
    }

    public void setGroupDisplayName(String groupDisplayName) {
        this.groupDisplayName = groupDisplayName;
    }

    public void clearMostButNotAllFieldsOnActionForm() {

        setDefaultFees(new ArrayList<FeeView>());
        setAdditionalFees(new ArrayList<FeeView>());
        setCustomFields(new ArrayList<CustomFieldView>());
        setFamilyNames(new ArrayList<ClientNameDetailView>());
        setFamilyDetails(new ArrayList<ClientFamilyDetailView>());
        setFamilyRelationship(new ArrayList<Short>());
        setFamilyFirstName(new ArrayList<String>());
        setFamilyMiddleName(new ArrayList<String>());
        setFamilyLastName(new ArrayList<String>());
        setFamilyDateOfBirthDD(new ArrayList<String>());
        setFamilyDateOfBirthMM(new ArrayList<String>());
        setFamilyDateOfBirthYY(new ArrayList<String>());
        setFamilyGender(new ArrayList<Short>());
        setFamilyLivingStatus(new ArrayList<Short>());
        initializeFamilyMember();
        addFamilyMember();
        setAddress(new Address());
        setDisplayName(null);
        setDateOfBirthDD(null);
        setDateOfBirthMM(null);
        setDateOfBirthYY(null);
        setGovernmentId(null);
        setMfiJoiningDate(null);
        setGlobalCustNum(null);
        setCustomerId(null);
        setExternalId(null);
        setLoanOfficerId(null);
        setLoanOfficerName("");
        setFormedByPersonnel(null);
        setTrained(null);
        setTrainedDate(null);
        setClientName(new ClientNameDetailView());
        setSpouseName(new ClientNameDetailView());
        setClientDetailView(new ClientDetailView());
        setNextOrPreview("next");

        for (int i = 0; i < getSelectedOfferings().size(); i++) {
            getSelectedOfferings().set(i, null);
        }
    }

    public void setLoanOfficerName(String loanOfficerName) {
        this.loanOfficerName = loanOfficerName;
    }

    public String getLoanOfficerName() {
        return this.loanOfficerName;
    }

    public MeetingBO getParentCustomerMeeting() {
        return this.parentCustomerMeeting;
    }

    public void setParentCustomerMeeting(MeetingBO parentCustomerMeeting) {
        this.parentCustomerMeeting = parentCustomerMeeting;
    }
}
