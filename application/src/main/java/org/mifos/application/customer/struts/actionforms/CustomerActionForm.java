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

package org.mifos.application.customer.struts.actionforms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.customer.business.CustomerPositionView;
import org.mifos.application.customer.center.util.helpers.ValidateMethods;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.SessionUtils;

/**
 * What's the difference between this and {@link CustActionForm} ?
 */
public abstract class CustomerActionForm extends BaseActionForm {

    protected String input;

    private String searchString;

    private String customerId;

    private String globalCustNum;

    private String displayName;

    private Address address;

    private String officeId;

    private String officeName;

    private String loanOfficerId;

    private String externalId;

    private String status;

    private String mfiJoiningDate;

    private String formedByPersonnel;

    private String trained;

    private String trainedDate;

    private List<FeeView> defaultFees;

    private List<FeeView> additionalFees;

    private String selectedFeeAmntList;

    private List<CustomFieldView> customFields;

    private List<CustomerPositionView> customerPositions;

    public CustomerActionForm() {
        address = new Address();
        defaultFees = new ArrayList<FeeView>();
        additionalFees = new ArrayList<FeeView>();
        customFields = new ArrayList<CustomFieldView>();
    }

    public List<FeeView> getAdditionalFees() {
        return additionalFees;
    }

    public void setAdditionalFees(List<FeeView> additionalFees) {
        this.additionalFees = additionalFees;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getCustomerId() {
        return customerId;
    }

    public int getCustomerIdAsInt() {
        if (customerId == null) {
            throw new NullPointerException("customerId is not set");
        }
        return Integer.parseInt(customerId);
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getGlobalCustNum() {
        return globalCustNum;
    }

    public void setGlobalCustNum(String globalCustNum) {
        this.globalCustNum = globalCustNum;
    }

    public List<CustomFieldView> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(List<CustomFieldView> customFields) {
        this.customFields = customFields;
    }

    public List<FeeView> getDefaultFees() {
        return defaultFees;
    }

    public void setDefaultFees(List<FeeView> defaultFees) {
        this.defaultFees = defaultFees;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getLoanOfficerId() {
        return loanOfficerId;
    }

    public void setLoanOfficerId(String loanOfficerId) {
        this.loanOfficerId = loanOfficerId;
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getStatus() {
        return status;
    }

    public String getMfiJoiningDate() {
        return mfiJoiningDate;
    }

    public void setMfiJoiningDate(String mfiJoiningDate) {
        this.mfiJoiningDate = mfiJoiningDate;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSelectedFeeAmntList() {
        return selectedFeeAmntList;
    }

    public void setSelectedFeeAmntList(String selectedFeeAmntList) {
        this.selectedFeeAmntList = selectedFeeAmntList;
    }

    public Short getOfficeIdValue() {
        return getShortValue(officeId);
    }

    public Short getLoanOfficerIdValue() {
        return getShortValue(loanOfficerId);
    }

    public CustomerStatus getStatusValue() {
        return StringUtils.isNotBlank(status) ? CustomerStatus.fromInt(Short.valueOf(status)) : null;
    }

    public Short getFormedByPersonnelValue() {
        return getShortValue(formedByPersonnel);
    }

    public String getTrained() {
        return trained;
    }

    public Short getTrainedValue() {
        return getShortValue(trained);
    }

    public void setTrained(String trained) {
        this.trained = trained;
    }

    public String getTrainedDate() {
        return trainedDate;
    }

    public void setTrainedDate(String trainedDate) {
        this.trainedDate = trainedDate;
    }

    public CustomFieldView getCustomField(int i) {
        while (i >= customFields.size()) {
            customFields.add(new CustomFieldView());
        }
        return customFields.get(i);
    }

    public String getFormedByPersonnel() {
        return formedByPersonnel;
    }

    public void setFormedByPersonnel(String formedByPersonnel) {
        this.formedByPersonnel = formedByPersonnel;
    }

    public FeeView getDefaultFee(int i) {
        while (i >= defaultFees.size()) {
            defaultFees.add(new FeeView());
        }
        return defaultFees.get(i);
    }

    public List<FeeView> getFeesToApply() {
        List<FeeView> feesToApply = new ArrayList<FeeView>();
        for (FeeView fee : getAdditionalFees())
            if (fee.getFeeIdValue() != null)
                feesToApply.add(fee);
        for (FeeView fee : getDefaultFees())
            if (!fee.isRemoved())
                feesToApply.add(fee);
        return feesToApply;
    }

    public FeeView getSelectedFee(int index) {
        while (index >= additionalFees.size())
            additionalFees.add(new FeeView());
        return additionalFees.get(index);
    }

    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        if (request.getParameter("displayName") != null) {
            for (int i = 0; i < defaultFees.size(); i++) {
                // if an already checked fee is unchecked then the value set to
                // 0
                if (request.getParameter("defaultFee[" + i + "].feeRemoved") == null) {
                    defaultFees.get(i).setFeeRemoved(YesNoFlag.NO.getValue());
                }
            }
        }
        super.reset(mapping, request);
    }

    public List<CustomerPositionView> getCustomerPositions() {
        return customerPositions;
    }

    public void setCustomerPositions(List<CustomerPositionView> customerPositions) {
        this.customerPositions = customerPositions;
    }

    public CustomerPositionView getCustomerPosition(int index) {
        while (index >= customerPositions.size()) {
            customerPositions.add(new CustomerPositionView());
        }
        return customerPositions.get(index);
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        String method = request.getParameter("method");
        if (null == request.getAttribute(Constants.CURRENTFLOWKEY))
            request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
        ActionErrors errors = null;
        try {
            errors = validateFields(request, method);
        } catch (ApplicationException ae) {
            errors = new ActionErrors();
            errors.add(ae.getKey(), new ActionMessage(ae.getKey(), ae.getValues()));
        }
        if (null != errors && !errors.isEmpty()) {
            request.setAttribute(Globals.ERROR_KEY, errors);
            request.setAttribute("methodCalled", method);

        }
        errors.add(super.validate(mapping, request));
        return errors;
    }

    protected abstract ActionErrors validateFields(HttpServletRequest request, String method)
            throws ApplicationException;

    protected void validateName(ActionErrors errors) {
        if (StringUtils.isBlank(getDisplayName()))
            errors.add(CustomerConstants.NAME, new ActionMessage(CustomerConstants.ERRORS_SPECIFY_NAME));
    }

    protected void validateLO(ActionErrors errors) {
        if (StringUtils.isBlank(getLoanOfficerId()))
            errors.add(CustomerConstants.LOAN_OFFICER, new ActionMessage(CustomerConstants.ERRORS_SELECT_LOAN_OFFICER));
    }

    protected void validateFormedByPersonnel(ActionErrors errors) {
        if (StringUtils.isBlank(getFormedByPersonnel()))
            errors.add(CustomerConstants.FORMED_BY_LOANOFFICER, new ActionMessage(
                    CustomerConstants.FORMEDBY_LOANOFFICER_BLANK_EXCEPTION));
    }

    protected void validateMeeting(HttpServletRequest request, ActionErrors errors) throws ApplicationException {
        MeetingBO meeting = getCustomerMeeting(request);
        if (meeting == null)
            errors.add(CustomerConstants.MEETING, new ActionMessage(CustomerConstants.ERRORS_SPECIFY_MEETING));
    }

    protected void validateConfigurableMandatoryFields(HttpServletRequest request, ActionErrors errors,
            EntityType entityType) {
        checkForMandatoryFields(entityType.getValue(), errors, request);
    }

    protected void validateCustomFields(HttpServletRequest request, ActionErrors errors) {
        try {
            List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
                    .getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
            for (CustomFieldView customField : customFields) {
                boolean isErrorFound = false;
                for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
                    if (customField.getFieldId().equals(customFieldDef.getFieldId()) && customFieldDef.isMandatory()) {
                        if (StringUtils.isBlank(customField.getFieldValue())) {
                            errors.add(CustomerConstants.CUSTOM_FIELD, new ActionMessage(
                                    CustomerConstants.ERRORS_SPECIFY_CUSTOM_FIELD_VALUE));
                            isErrorFound = true;
                            break;
                        }
                    }
                }
                if (isErrorFound)
                    break;
            }
        } catch (PageExpiredException pee) {
            errors.add(ExceptionConstants.PAGEEXPIREDEXCEPTION, new ActionMessage(
                    ExceptionConstants.PAGEEXPIREDEXCEPTION));
        }
    }

    protected abstract MeetingBO getCustomerMeeting(HttpServletRequest request) throws ApplicationException;

    protected void validateFees(HttpServletRequest request, ActionErrors errors) throws ApplicationException {
        validateForFeeAssignedWithoutMeeting(request, errors);
        validateForFeeRecurrence(request, errors);
        validateForFeeAmount(errors);
        validateForDuplicatePeriodicFee(request, errors);
    }

    protected void validateForFeeRecurrence(HttpServletRequest request, ActionErrors errors)
            throws ApplicationException {
        MeetingBO meeting = getCustomerMeeting(request);
        if (meeting != null) {
            List<FeeView> feeList = getDefaultFees();
            for (FeeView fee : feeList) {
                if (!fee.isRemoved() && fee.isPeriodic() && !isFrequencyMatches(fee, meeting)) {
                    errors.add(CustomerConstants.ERRORS_FEE_FREQUENCY_MISMATCH, new ActionMessage(
                            CustomerConstants.ERRORS_FEE_FREQUENCY_MISMATCH));
                    return;
                }
            }
            List<FeeView> additionalFeeList = (List<FeeView>) SessionUtils.getAttribute(
                    CustomerConstants.ADDITIONAL_FEES_LIST, request);
            for (FeeView selectedFee : getAdditionalFees()) {
                for (FeeView fee : additionalFeeList) {
                    if (selectedFee.getFeeIdValue() != null && selectedFee.getFeeId().equals(fee.getFeeId())) {
                        if (fee.isPeriodic() && !isFrequencyMatches(fee, meeting)) {
                            errors.add(CustomerConstants.ERRORS_FEE_FREQUENCY_MISMATCH, new ActionMessage(
                                    CustomerConstants.ERRORS_FEE_FREQUENCY_MISMATCH));
                            return;
                        }
                    }
                }
            }
        }
    }

    private boolean isFrequencyMatches(FeeView fee, MeetingBO meeting) {
        return (fee.getFrequencyType().equals(RecurrenceType.MONTHLY) && meeting.isMonthly())
                || (fee.getFrequencyType().equals(RecurrenceType.WEEKLY) && meeting.isWeekly());

    }

    private void validateForFeeAssignedWithoutMeeting(HttpServletRequest request, ActionErrors errors)
            throws ApplicationException {
        for (int i = 0; i < defaultFees.size(); i++) {
            // if an already checked fee is unchecked then the value set to 0
            if (request.getParameter("defaultFee[" + i + "].feeRemoved") == null) {
                defaultFees.get(i).setFeeRemoved(YesNoFlag.NO.getValue());
            }
        }
        MeetingBO meeting = getCustomerMeeting(request);
        if (meeting == null && getFeesToApply().size() > 0) {
            errors.add(CustomerConstants.MEETING_REQUIRED_EXCEPTION, new ActionMessage(
                    CustomerConstants.MEETING_REQUIRED_EXCEPTION));
        }
    }

    protected void validateForFeeAmount(ActionErrors errors) {
        List<FeeView> feeList = getFeesToApply();
        for (FeeView fee : feeList) {
            if (StringUtils.isBlank(fee.getAmount()))
                errors.add(CustomerConstants.FEE, new ActionMessage(CustomerConstants.ERRORS_SPECIFY_FEE_AMOUNT));
        }
    }

    protected void validateForDuplicatePeriodicFee(HttpServletRequest request, ActionErrors errors)
            throws ApplicationException {
        List<FeeView> additionalFeeList = (List<FeeView>) SessionUtils.getAttribute(
                CustomerConstants.ADDITIONAL_FEES_LIST, request);
        for (FeeView selectedFee : getAdditionalFees()) {
            int count = 0;
            for (FeeView duplicateSelectedfee : getAdditionalFees()) {
                if (selectedFee.getFeeIdValue() != null
                        && selectedFee.getFeeId().equals(duplicateSelectedfee.getFeeId())) {
                    if (isSelectedFeePeriodic(selectedFee, additionalFeeList))
                        count++;
                }
            }
            if (count > 1) {
                errors.add(CustomerConstants.FEE, new ActionMessage(CustomerConstants.ERRORS_DUPLICATE_PERIODIC_FEE));
                break;
            }
        }
    }

    protected void validateTrained(HttpServletRequest request, ActionErrors errors) {
        if (request.getParameter("trained") == null) {
            trained = null;
        } else if (isCustomerTrained()) {
            if (ValidateMethods.isNullOrBlank(getTrainedDate())) {
                errors.add(CustomerConstants.TRAINED_DATE_MANDATORY, new ActionMessage(
                        CustomerConstants.TRAINED_DATE_MANDATORY));
            }

            else { // if marked trained and a date is supplied
                if (!DateUtils.isValidDate(getTrainedDate())) {
                    errors.add(CustomerConstants.INVALID_TRAINED_DATE, new ActionMessage(
                            CustomerConstants.INVALID_TRAINED_DATE));
                }
            }

        }
        // if training date is entered and trained is not selected, throw an
        // error
        if (!ValidateMethods.isNullOrBlank(getTrainedDate()) && ValidateMethods.isNullOrBlank(trained)) {
            errors.add(CustomerConstants.TRAINED_CHECKED, new ActionMessage(CustomerConstants.TRAINED_CHECKED));
        }

    }

    private boolean isSelectedFeePeriodic(FeeView selectedFee, List<FeeView> additionalFeeList) {
        for (FeeView fee : additionalFeeList)
            if (fee.getFeeId().equals(selectedFee.getFeeId()))
                return fee.isPeriodic();
        return false;
    }

    public boolean isCustomerTrained() {
        return StringUtils.isNotBlank(trained) && Short.valueOf(trained).equals(YesNoFlag.YES.getValue());
    }

    public Date getTrainedDateValue(Locale locale) throws InvalidDateException {
        return getDateFromString(getTrainedDate(), locale);
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

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
}
