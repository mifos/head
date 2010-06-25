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

package org.mifos.accounts.loan.struts.actionforms;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fees.util.helpers.RateAmountFlag;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.struts.uihelpers.PaymentDataHtmlBean;
import org.mifos.accounts.loan.util.helpers.LoanAccountDetailsDto;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.loan.util.helpers.LoanExceptionConstants;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.productdefinition.business.AmountRange;
import org.mifos.accounts.productdefinition.business.InstallmentRange;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.PaymentDataTemplate;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldDto;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.service.CustomerBusinessService;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.util.helpers.CustomerDetailDto;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.DoubleConversionResult;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.security.util.UserContext;

public class LoanAccountActionForm extends BaseActionForm {

    public LoanAccountActionForm() {
        super();
        defaultFees = new ArrayList<FeeDto>();
        additionalFees = new ArrayList<FeeDto>();
        customFields = new ArrayList<CustomFieldDto>();
        clients = new ArrayList<String>();
        clientDetails = new ArrayList<LoanAccountDetailsDto>();
        configService = new ConfigurationBusinessService();
    }

    // For individual monitoring

    private List<String> clients;

    private List<LoanAccountDetailsDto> clientDetails;

    private String perspective;

    private String accountId;

    private String globalAccountNum;

    private String prdOfferingName;

    private String accountName;

    private String accountTypeId;

    private String customerId;

    private String prdOfferingId;

    private String loanAmount;

    private String interestRate;

    private String noOfInstallments;

    private String disbursementDate;

    private String intDedDisbursement;

    private String loanOfferingFund;

    private String gracePeriodDuration;

    private String externalId;

    private String businessActivityId;

    private String collateralTypeId;

    private String collateralNote;

    private List<FeeDto> defaultFees;

    private List<FeeDto> additionalFees;

    private String stateSelected;

    private String gracePeriod;

    private List<CustomFieldDto> customFields;

    private List<PaymentDataHtmlBean> paymentDataBeans = new ArrayList<PaymentDataHtmlBean>();

    // For Repayment day

    private String monthRank;

    private String weekRank;

    private String monthWeek;

    private String monthType;

    private String monthDay;

    private String dayRecurMonth;

    private String weekDay;

    private String recurMonth;

    private String recurWeek;

    private String frequency;

    private String firstRepaymentDay;

    private String recurrenceId;

    private AmountRange amountRange;

    private Double maxInterestRate;

    private Double minInterestRate;

    private InstallmentRange installmentRange;

    private String dayNumber;

    private ConfigurationBusinessService configService;

    private Date originalDisbursementDate;

    public Date getOriginalDisbursementDate() {
        return this.originalDisbursementDate;
    }

    public void setOriginalDisbursementDate(Date originalDisbursementDate) {
        this.originalDisbursementDate = originalDisbursementDate;
    }

    public String getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(String dayNumber) {
        this.dayNumber = dayNumber;
    }

    public String getMonthRank() {
        return monthRank;
    }

    public void setMonthRank(String monthRank) {
        this.monthRank = monthRank;
    }

    public String getMonthWeek() {
        return monthWeek;
    }

    public void setMonthWeek(String monthWeek) {
        this.monthWeek = monthWeek;
    }

    public String getMonthType() {
        return monthType;
    }

    public void setMonthType(String monthType) {
        this.monthType = monthType;
    }

    public String getMonthDay() {
        return monthDay;
    }

    public void setMonthDay(String monthDay) {
        this.monthDay = monthDay;
    }

    public String getDayRecurMonth() {
        return dayRecurMonth;
    }

    public void setDayRecurMonth(String dayRecurMonth) {
        this.dayRecurMonth = dayRecurMonth;
    }

    public String getRecurMonth() {
        return recurMonth;
    }

    public void setRecurMonth(String recurMonth) {
        this.recurMonth = recurMonth;
    }

    public String getRecurWeek() {
        return recurWeek;
    }

    public void setRecurWeek(String recurWeek) {
        this.recurWeek = recurWeek;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public String getWeekRank() {
        return weekRank;
    }

    public void setWeekRank(String weekRank) {
        this.weekRank = weekRank;
    }

    public String getRecurrenceId() {
        return recurrenceId;
    }

    public void setRecurrenceId(String recurrenceId) {
        this.recurrenceId = recurrenceId;
    }

    public List<PaymentDataHtmlBean> getPaymentDataBeans() {
        return this.paymentDataBeans;
    }

    public String getGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(String gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountTypeId() {
        return accountTypeId;
    }

    public void setAccountTypeId(String accountTypeId) {
        this.accountTypeId = accountTypeId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getGlobalAccountNum() {
        return globalAccountNum;
    }

    public void setGlobalAccountNum(String globalAccountNum) {
        this.globalAccountNum = globalAccountNum;
    }

    public String getPrdOfferingName() {
        return prdOfferingName;
    }

    public void setPrdOfferingName(String prdOfferingName) {
        this.prdOfferingName = prdOfferingName;
    }

    public String getPrdOfferingId() {
        return prdOfferingId;
    }

    public void setPrdOfferingId(String prdOfferingId) {
        this.prdOfferingId = prdOfferingId;
    }

    public String getBusinessActivityId() {
        return businessActivityId;
    }

    public void setBusinessActivityId(String businessActivityId) {
        this.businessActivityId = businessActivityId;
    }

    public String getCollateralNote() {
        return collateralNote;
    }

    public void setCollateralNote(String collateralNote) {
        this.collateralNote = collateralNote;
    }

    public String getCollateralTypeId() {
        return collateralTypeId;
    }

    public void setCollateralTypeId(String collateralTypeId) {
        this.collateralTypeId = collateralTypeId;
    }

    public String getDisbursementDate() {
        return disbursementDate;
    }

    public void setDisbursementDate(String disbursementDate) {
        this.disbursementDate = disbursementDate;
    }

    public String getGracePeriodDuration() {
        return gracePeriodDuration;
    }

    public void setGracePeriodDuration(String gracePeriodDuration) {
        this.gracePeriodDuration = gracePeriodDuration;
    }

    public String getIntDedDisbursement() {
        return intDedDisbursement;
    }

    public void setIntDedDisbursement(String intDedDisbursement) {
        this.intDedDisbursement = intDedDisbursement;
    }

    public String getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(String interestRate) {
        this.interestRate = interestRate;
    }

    public String getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(String loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getLoanOfferingFund() {
        return loanOfferingFund;
    }

    public void setLoanOfferingFund(String loanOfferingFund) {
        this.loanOfferingFund = loanOfferingFund;
    }

    public String getNoOfInstallments() {
        return noOfInstallments;
    }

    public void setNoOfInstallments(String noOfInstallments) {
        this.noOfInstallments = noOfInstallments;
    }

    public List<CustomFieldDto> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(List<CustomFieldDto> customFields) {
        this.customFields = customFields;
    }

    public CustomFieldDto getCustomField(int i) {
        while (i >= customFields.size()) {
            customFields.add(new CustomFieldDto());
        }
        return customFields.get(i);
    }

    public List<FeeDto> getAdditionalFees() {
        return additionalFees;
    }

    public void setAdditionalFees(List<FeeDto> additionalFees) {
        this.additionalFees = additionalFees;
    }

    public List<FeeDto> getDefaultFees() {
        return defaultFees;
    }

    public void setDefaultFees(List<FeeDto> defaultFees) {
        this.defaultFees = defaultFees;
    }

    public String getStateSelected() {
        return stateSelected;
    }

    public void setStateSelected(String stateSelected) {
        this.stateSelected = stateSelected;
    }

    public AccountState getState() {
        return AccountState.fromShort(getShortValue(getStateSelected()));
    }

    public Integer getCustomerIdValue() {
        return getIntegerValue(getCustomerId());
    }

    public Short getPrdOfferingIdValue() {
        return getShortValue(getPrdOfferingId());
    }

    public Short getGracePeriodDurationValue() {
        return getShortValue(getGracePeriodDuration());
    }

    public Short getNoOfInstallmentsValue() {
        return getShortValue(getNoOfInstallments());
    }

    public Date getDisbursementDateValue(Locale locale) throws InvalidDateException {
        return DateUtils.getLocaleDate(locale, getDisbursementDate());
    }

    public Date getFirstRepaymentDayValue(Locale locale) throws InvalidDateException {
        return DateUtils.getLocaleDate(locale, getFirstRepaymentDay());
    }

    public boolean isInterestDedAtDisbValue() {
        return getBooleanValue(getIntDedDisbursement());
    }

    public Double getInterestDoubleValue() {
        return getDoubleValue(getInterestRate());
    }

    public Short getLoanOfferingFundValue() {
        return getShortValue(getLoanOfferingFund());
    }

    public Integer getBusinessActivityIdValue() {
        return getIntegerValue(getBusinessActivityId());
    }

    public Integer getCollateralTypeIdValue() {
        return getIntegerValue(getCollateralTypeId());
    }

    public Double getInterestRateValue() {
        return getDoubleValue(interestRate);
    }

    public FeeDto getDefaultFee(int i) {
        while (i >= defaultFees.size()) {
            defaultFees.add(new FeeDto());
        }
        return defaultFees.get(i);
    }

    public Boolean isInterestDeductedAtDisbursement() {
        if (getIntDedDisbursement().equals("1")) {
            return true;
        }
        return false;
    }

    public void initializeTransactionFields(List<PaymentDataHtmlBean> paymentDataBeans) {
        this.paymentDataBeans = paymentDataBeans;
    }

    public List<FeeDto> getFeesToApply() {
        List<FeeDto> feesToApply = new ArrayList<FeeDto>();
        for (FeeDto fee : getAdditionalFees()) {
            if (fee.getFeeIdValue() != null) {
                feesToApply.add(fee);
            }
        }
        for (FeeDto fee : getDefaultFees()) {
            if (!fee.isRemoved()) {
                feesToApply.add(fee);
            }
        }
        return feesToApply;
    }

    public FeeDto getSelectedFee(int index) {
        while (index >= additionalFees.size()) {
            additionalFees.add(new FeeDto());
        }
        return additionalFees.get(index);
    }

    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        String method = request.getParameter(Methods.method.toString());
        if (method.equals(Methods.schedulePreview.toString())) {
            intDedDisbursement = null;
            for (int i = 0; i < defaultFees.size(); i++) {
                // if an already checked fee is unchecked then the value set to
                // 0
                if (request.getParameter("defaultFee[" + i + "].feeRemoved") == null) {
                    defaultFees.get(i).setFeeRemoved(YesNoFlag.NO.getValue());
                }
            }
        } else if (method.equals(Methods.load.toString())) {
            clients = new ArrayList<String>();
        } else if (method.equals(Methods.managePreview.toString())) {
            intDedDisbursement = "0";
        }

    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        String method = request.getParameter(Methods.method.toString());
        ActionErrors errors = new ActionErrors();
        UserContext userContext = getUserContext(request);
        if (null == request.getAttribute(Constants.CURRENTFLOWKEY)) {
            request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
        }

        try {
            if (method.equals(Methods.getPrdOfferings.toString())) {
                checkValidationForGetPrdOfferings(errors, userContext);
            } else if (method.equals(Methods.load.toString())) {
                checkValidationForLoad(errors, userContext);
            } else if (method.equals(Methods.schedulePreview.toString())) {
                checkValidationForSchedulePreview(errors, getCurrencyFromLoanOffering(request), request);
            } else if (method.equals(Methods.managePreview.toString())) {
                checkValidationForManagePreview(errors, getCurrencyFromLoanOffering(request), request);
            } else if (method.equals(Methods.preview.toString())) {
                checkValidationForPreview(errors, getCurrencyFromLoanOffering(request), request);

            }
        } catch (ApplicationException ae) {
            // Discard other errors (is that right?)
            ae.printStackTrace();
            errors = new ActionErrors();
            errors.add(ae.getKey(), new ActionMessage(ae.getKey(), ae.getValues()));
        }
        if (!errors.isEmpty()) {
            request.setAttribute(LoanConstants.METHODCALLED, method);
        }
        return errors;
    }

    protected void validateLoanAmount(ActionErrors errors, Locale locale, MifosCurrency currency) {
        DoubleConversionResult conversionResult = validateAmount(getLoanAmount(), currency,
                LoanConstants.LOAN_AMOUNT_KEY, errors, locale, FilePaths.LOAN_UI_RESOURCE_PROPERTYFILE);
        if (conversionResult.getErrors().size() == 0 && !(conversionResult.getDoubleValue() > 0.0)) {
            addError(errors, LoanConstants.LOAN_AMOUNT_KEY, LoanConstants.ERRORS_MUST_BE_GREATER_THAN_ZERO,
                    lookupLocalizedPropertyValue(LoanConstants.LOAN_AMOUNT_KEY, locale,
                            FilePaths.LOAN_UI_RESOURCE_PROPERTYFILE));
        }
    }

    protected void validateInterest(ActionErrors errors, Locale locale) {
        DoubleConversionResult conversionResult = validateInterest(getInterestRate(),
                LoanConstants.LOAN_INTEREST_RATE_KEY, errors, locale, FilePaths.LOAN_UI_RESOURCE_PROPERTYFILE);
        if (conversionResult.getErrors().size() == 0 && !(conversionResult.getDoubleValue() >= 0.0)) {
            addError(errors, LoanConstants.LOAN_INTEREST_RATE_KEY, LoanConstants.ERRORS_MUST_NOT_BE_NEGATIVE,
                    lookupLocalizedPropertyValue(LoanConstants.LOAN_INTEREST_RATE_KEY, locale,
                            FilePaths.LOAN_UI_RESOURCE_PROPERTYFILE));
        }
    }

    protected void validateDefaultFee(ActionErrors errors, Locale locale, MifosCurrency currency) {
        for (FeeDto defaultFee : defaultFees) {
            if (defaultFee.getFeeType().equals(RateAmountFlag.AMOUNT)) {
                DoubleConversionResult conversionResult = validateAmount(defaultFee.getAmount(), currency,
                        LoanConstants.LOAN_DEFAULT_FEE_KEY, errors, locale, FilePaths.LOAN_UI_RESOURCE_PROPERTYFILE);
                if (conversionResult.getErrors().size() == 0 && !(conversionResult.getDoubleValue() > 0.0)) {
                    addError(errors, LoanConstants.LOAN_DEFAULT_FEE_KEY,
                            LoanConstants.ERRORS_MUST_BE_GREATER_THAN_ZERO,
                            lookupLocalizedPropertyValue(LoanConstants.LOAN_DEFAULT_FEE_KEY, locale,
                                    FilePaths.LOAN_UI_RESOURCE_PROPERTYFILE));
                }
            } else {
                DoubleConversionResult conversionResult = validateInterest(defaultFee.getAmount(),
                        LoanConstants.LOAN_DEFAULT_FEE_KEY, errors, locale, FilePaths.LOAN_UI_RESOURCE_PROPERTYFILE);
                if (conversionResult.getErrors().size() == 0 && !(conversionResult.getDoubleValue() > 0.0)) {
                    addError(errors, LoanConstants.LOAN_DEFAULT_FEE_KEY,
                            LoanConstants.ERRORS_MUST_BE_GREATER_THAN_ZERO,
                            lookupLocalizedPropertyValue(LoanConstants.LOAN_DEFAULT_FEE_KEY, locale,
                                    FilePaths.LOAN_UI_RESOURCE_PROPERTYFILE));
                }
            }
        }
    }

    protected void validateAdditionalFee(ActionErrors errors, Locale locale, MifosCurrency currency, HttpServletRequest request)
            throws PageExpiredException {
        List<FeeDto> additionalFeeList = (List<FeeDto>) SessionUtils.getAttribute(LoanConstants.ADDITIONAL_FEES_LIST,
                request);
        for (FeeDto additionalFee : additionalFees) {
            if (additionalFee.getAmount() != null && !additionalFee.getAmount().equals("")) {
                if (getAdditionalFeeType(additionalFeeList, additionalFee.getFeeId()).equals(RateAmountFlag.AMOUNT)) {

                    DoubleConversionResult conversionResult = validateAmount(additionalFee.getAmount(),
                            currency, LoanConstants.LOAN_ADDITIONAL_FEE_KEY, errors, locale,
                            FilePaths.LOAN_UI_RESOURCE_PROPERTYFILE);
                    if (conversionResult.getErrors().size() == 0 && !(conversionResult.getDoubleValue() > 0.0)) {
                        addError(errors, LoanConstants.LOAN_ADDITIONAL_FEE_KEY,
                                LoanConstants.ERRORS_MUST_BE_GREATER_THAN_ZERO, lookupLocalizedPropertyValue(
                                        LoanConstants.LOAN_ADDITIONAL_FEE_KEY, locale,
                                        FilePaths.LOAN_UI_RESOURCE_PROPERTYFILE));
                    }
                } else {
                    DoubleConversionResult conversionResult = validateInterest(additionalFee.getAmount(),
                            LoanConstants.LOAN_ADDITIONAL_FEE_KEY, errors, locale,
                            FilePaths.LOAN_UI_RESOURCE_PROPERTYFILE);
                    if (conversionResult.getErrors().size() == 0 && !(conversionResult.getDoubleValue() > 0.0)) {
                        addError(errors, LoanConstants.LOAN_ADDITIONAL_FEE_KEY,
                                LoanConstants.ERRORS_MUST_BE_GREATER_THAN_ZERO, lookupLocalizedPropertyValue(
                                        LoanConstants.LOAN_ADDITIONAL_FEE_KEY, locale,
                                        FilePaths.LOAN_UI_RESOURCE_PROPERTYFILE));
                    }
                }
            }
        }
    }

    private  RateAmountFlag getAdditionalFeeType(List<FeeDto> additionalFeeList, String feeId) {
        for (FeeDto fee : additionalFeeList) {
            if (fee.getFeeId().equals(feeId)) {
                return fee.getFeeType();
            }
        }
        return null;
    }

    // TODO: use localized strings for error messages rather than hardcoded
    private void checkValidationForGetPrdOfferings(ActionErrors errors, UserContext userContext) {
        if (StringUtils.isBlank(getCustomerId())) {
            addError(errors, LoanConstants.CUSTOMER, LoanConstants.CUSTOMERNOTSELECTEDERROR, getLabel(
                    ConfigurationConstants.CLIENT, userContext), getLabel(ConfigurationConstants.GROUP, userContext));
        }
    }

    // TODO: use localized strings for error messages rather than hardcoded
    private void checkValidationForLoad(ActionErrors errors, UserContext userContext) {
        checkValidationForGetPrdOfferings(errors, userContext);
        if (StringUtils.isBlank(getPrdOfferingId())) {
            Locale locale = userContext.getCurrentLocale();
            ResourceBundle resources = ResourceBundle.getBundle(FilePaths.LOAN_UI_RESOURCE_PROPERTYFILE, locale);
            String instanceName = resources.getString("loan.instanceName");
            addError(errors, LoanConstants.PRDOFFERINGID, LoanConstants.LOANOFFERINGNOTSELECTEDERROR, getLabel(
                    ConfigurationConstants.LOAN, userContext), instanceName);
        }
    }

    private MifosCurrency getCurrencyFromLoanOffering(HttpServletRequest request){
        try {
            LoanOfferingBO loanOfferingBO = ((LoanOfferingBO) SessionUtils.getAttribute(LoanConstants.LOANOFFERING, request));
            if (loanOfferingBO != null) {
                return loanOfferingBO.getCurrency();
            }
        } catch (PageExpiredException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void checkValidationForSchedulePreview(ActionErrors errors, MifosCurrency currency, HttpServletRequest request)
            throws ApplicationException {
        Locale locale = getUserContext(request).getPreferredLocale();
        checkValidationForPreviewBefore(errors, request);
        validateFees(request, errors);
        validateCustomFields(request, errors);
        performGlimSpecificValidations(errors, currency, request);
        validateRepaymentDayRequired(errors);
        if (!configService.isGlimEnabled() || !getCustomer(request).isGroup()) {
            validatePurposeOfLoanFields(errors, getMandatoryFields(request));
        }
        validateSourceOfFundFields(errors, getMandatoryFields(request));
        validateExternalIDFields(errors, getMandatoryFields(request));
        validateLoanAmount(errors, locale, currency);
        validateInterest(errors, locale);
        validateCollateralNotes(errors, locale);
        validateDefaultFee(errors, locale, currency);
        validateAdditionalFee(errors, locale, currency, request);
        if (configService.isGlimEnabled() && getCustomer(request).isGroup()) {

        }
    }

    private void validateCollateralNotes(ActionErrors errors, Locale userLocale) {
        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.LOAN_UI_RESOURCE_PROPERTYFILE, userLocale);
        String note = resources.getString("loan.collateral_notes");
        String collateralNote = getCollateralNote();
        if (collateralNote != null && collateralNote.length() > 500) {
            addError(errors, LoanConstants.NOTE, LoanConstants.MAX_LENGTH, note, String
                    .valueOf(LoanConstants.COMMENT_LENGTH));
        }
    }

    private void validateClientDetails(ActionErrors errors, Locale locale) {
        for (LoanAccountDetailsDto clientDetail : clientDetails) {

        }
    }

    private void performGlimSpecificValidations(ActionErrors errors, MifosCurrency currency, HttpServletRequest request)
            throws PageExpiredException, ServiceException {
        if (configService.isGlimEnabled() && getCustomer(request).isGroup()) {
            Locale locale = getUserContext(request).getPreferredLocale();
            removeClientsNotCheckedInForm(request);
            validateIndividualLoanFieldsForGlim(errors, locale, currency);
            validateSelectedClients(errors);
            validatePurposeOfLoanForGlim(errors, getMandatoryFields(request));
        }
    }

    // Since this Struts form is set up as "session" scope, we need to clear
    // the form fields in cases where they are used across pages.
    // Currently we do that in the following case: When old values of clientId
    // are
    // retained from the account creation page, form validation
    // on submission from the Edit Account Information page is messed up.
    // Calling
    // this method fixes that problem
    private void removeClientsNotCheckedInForm(HttpServletRequest request) {
        List<String> clientIds = getSelectedClientIdsFromRequest(request);
        setClientsNotPresentInInputToEmptyString(clientIds);
    }

    void setClientsNotPresentInInputToEmptyString(List<String> clientIds) {
        for (int i = 0; i < clients.size(); i++) {
            if (!clientIds.contains(clients.get(i))) {
                clients.set(i, "");
            }
        }
    }

    List<String> getSelectedClientIdsFromRequest(HttpServletRequest request) {
        Collection<?> paramsStartingWithClients = CollectionUtils.select(convertEnumerationToList(request
                .getParameterNames()), new Predicate() {
            public boolean evaluate(Object object) {
                return ((String) object).startsWith("clients");
            }
        });
        List<String> indices = extractClientIdsFromRequest(request, paramsStartingWithClients);
        return indices;
    }

    private List<String> extractClientIdsFromRequest(HttpServletRequest request, Collection<?> paramsStartingWithClients) {
        List<String> clientIds = new ArrayList<String>();
        for (Object name : paramsStartingWithClients) {
            String element = (String) name;
            clientIds.add(request.getParameter(element));
        }
        return clientIds;
    }

    private List<String> convertEnumerationToList(Enumeration<?> parameterNames) {
        List<String> params = new ArrayList<String>();
        while (parameterNames.hasMoreElements()) {
            params.add((String) parameterNames.nextElement());
        }
        return params;
    }

    void validatePurposeOfLoanForGlim(ActionErrors errors, List<FieldConfigurationEntity> mandatoryFields) {
        if (!isPurposeOfLoanMandatory(mandatoryFields)) {
            return;
        }
        List<String> ids_clients_selected = getClients();
        List<LoanAccountDetailsDto> listdetail = getClientDetails();

        for (LoanAccountDetailsDto loanAccount : listdetail) {
            if (ids_clients_selected.contains(loanAccount.getClientId())) {
                if (StringUtils.isBlank(loanAccount.getBusinessActivity())) {
                    addErrorInvalidPurpose(errors);
                    return;
                }
            }
        }
    }

    private void addErrorInvalidPurpose(ActionErrors errors) {
        addError(errors, LoanExceptionConstants.CUSTOMER_PURPOSE_OF_LOAN_FIELD);
    }

    private void addErrorInvalidSource(ActionErrors errors) {
        addError(errors, LoanExceptionConstants.CUSTOMER_SOURCE_OF_FUND_FIELD);
    }

    private void addErrorInvalidExternalId(ActionErrors errors) {
        addError(errors, LoanExceptionConstants.CUSTOMER_EXTERNAL_ID_FIELD);
    }

    private void validatePurposeOfLoanFields(ActionErrors errors, List<FieldConfigurationEntity> mandatoryFields) {
        if (isPurposeOfLoanMandatory(mandatoryFields) && StringUtils.isBlank(getBusinessActivityId())) {
            addErrorInvalidPurpose(errors);
        }
    }

    private void validateSourceOfFundFields(ActionErrors errors, List<FieldConfigurationEntity> mandatoryFields) {
        if (isSourceOfFundMandatory(mandatoryFields) && StringUtils.isBlank(getLoanOfferingFund())) {
            addErrorInvalidSource(errors);
        }
    }

    private void validateExternalIDFields(ActionErrors errors, List<FieldConfigurationEntity> mandatoryFields) {
        if (isExternalIDMandatory(mandatoryFields) && StringUtils.isBlank(getExternalId())) {
            addErrorInvalidExternalId(errors);
        }
    }

    private void checkValidationForPreview(ActionErrors errors, MifosCurrency currency, HttpServletRequest request) {
        validateRedoLoanPayments(request, errors, currency);
    }

    private void checkValidationForManagePreview(ActionErrors errors, MifosCurrency currency, HttpServletRequest request)
            throws ApplicationException {
        Locale locale = getUserContext(request).getPreferredLocale();
        if (getState().equals(AccountState.LOAN_PARTIAL_APPLICATION)
                || getState().equals(AccountState.LOAN_PENDING_APPROVAL)) {
            checkValidationForPreviewBefore(errors, request);
            checkValidationForPreview(errors, currency, request);
            // Only validate the disbursement date before a loan has been approved. After
            // approval, it cannot be edited.
            try {
                // only validate if the disbursement date has changed
                if (!getDisbursementDateValue(getUserContext(request).getPreferredLocale()).equals(
                        getOriginalDisbursementDate())) {
                    validateDisbursementDate(errors, getCustomer(request), getDisbursementDateValue(getUserContext(
                            request).getPreferredLocale()));
                }
            } catch (InvalidDateException dateException) {
                addError(errors, LoanExceptionConstants.ERROR_INVALIDDISBURSEMENTDATE);
            }
        }
        performGlimSpecificValidations(errors, currency, request);
        validateCustomFields(request, errors);
        validateRepaymentDayRequired(errors);
        if (!configService.isGlimEnabled() || !getCustomer(request).isGroup()) {
            validatePurposeOfLoanFields(errors, getMandatoryFields(request));
        }
        validateSourceOfFundFields(errors, getMandatoryFields(request));
        validateExternalIDFields(errors, getMandatoryFields(request));
        validateLoanAmount(errors, locale, currency);
        validateInterest(errors, locale);
        validateCollateralNotes(errors, locale);
    }

    private void validateDisbursementDate(ActionErrors errors, CustomerBO customer, java.sql.Date disbursementDateValue)
            throws AccountException, ServiceException {
        if (!configService.isRepaymentIndepOfMeetingEnabled()
                && !LoanBO.isDisbursementDateValid(customer, disbursementDateValue)) {
            addError(errors, LoanExceptionConstants.INVALIDDISBURSEMENTDATE);
        }
    }

    private void checkValidationForPreviewBefore(ActionErrors errors, HttpServletRequest request)
            throws ApplicationException {
        Locale locale = getUserContext(request).getPreferredLocale();
        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.LOAN_UI_RESOURCE_PROPERTYFILE, locale);
        LoanOfferingBO loanOffering = (LoanOfferingBO) SessionUtils.getAttribute(LoanConstants.LOANOFFERING, request);

        if (!(configService.isGlimEnabled() && getCustomer(request).isGroup())) {
            checkForMinMax(errors, loanAmount, amountRange, resources.getString("loan.amount"));
        }
        checkForMinMax(errors, interestRate, maxInterestRate, minInterestRate,
                resources.getString("loan.interestRate"));
        checkForMinMax(errors, noOfInstallments, installmentRange, resources.getString("loan.noOfInstallments"));
        if (StringUtils.isBlank(getDisbursementDate())) {
            addError(errors, "Proposed/Actual disbursal date", "errors.validandmandatory", resources
                    .getString("loan.disbursalDate"));
        }
        // Check for invalid data format
        try {
            DateUtils.getLocaleDate(locale, getDisbursementDate());
        } catch (InvalidDateException ide) {
            addError(errors, "Proposed/Actual disbursal date",
                    LoanExceptionConstants.ERROR_INVALID_DISBURSEMENT_DATE_FORMAT, resources
                            .getString("loan.disbursalDate"));
        }
        if (isInterestDedAtDisbValue()) {
            setGracePeriodDuration("0");
        }
        if (((!isInterestDedAtDisbValue()) && StringUtils.isBlank(getGracePeriodDuration()))
                || (getDoubleValue(getGracePeriodDuration()) != null && getDoubleValue(getNoOfInstallments()) != null && getDoubleValue(getGracePeriodDuration()) >= getDoubleValue(getNoOfInstallments()))) {
            String gracePeriodForRepayments = resources.getString("loan.grace_period");
            String noInst = StringUtils.isBlank(getNoOfInstallments()) ? getStringValue(installmentRange
                    .getMaxNoOfInstall()) : getNoOfInstallments();
            addError(errors, LoanConstants.GRACEPERIODDURATION, LoanConstants.GRACEPERIODERROR,
                    gracePeriodForRepayments, noInst);
        }

    }

    void checkForMinMax(ActionErrors errors, String currentValue, AmountRange amountRange, String field) {
        try {
            if (isBlank(currentValue) || !amountRange.isInRange(getDoubleValue(currentValue))) {
                addError(errors, field, LoanExceptionConstants.INVALIDMINMAX, field, getStringValue(amountRange
                        .getMinLoanAmount()), getStringValue(amountRange.getMaxLoanAmount()));
            }
        } catch (NumberFormatException nfe) {
            addError(errors, field, LoanExceptionConstants.INVALIDMINMAX, field, getStringValue(amountRange
                    .getMinLoanAmount()), getStringValue(amountRange.getMaxLoanAmount()));
        }
    }

    void checkForMinMax(ActionErrors errors, String currentValue, InstallmentRange installmentRange, String field) {
        try {
            if (StringUtils.isBlank(currentValue) || !installmentRange.isInRange(getShortValue(currentValue))) {
                addError(errors, field, LoanExceptionConstants.INVALIDMINMAX, field, getStringValue(installmentRange
                        .getMinNoOfInstall()), getStringValue(installmentRange.getMaxNoOfInstall()));
            }
        } catch (NumberFormatException nfe) {
            addError(errors, field, LoanExceptionConstants.INVALIDMINMAX, field, getStringValue(installmentRange
                    .getMinNoOfInstall()), getStringValue(installmentRange.getMaxNoOfInstall()));
        }
    }

    private void checkForMinMax(ActionErrors errors, String currentValue, Double maxValue, Double minValue, String field) {
        try {
            if (StringUtils.isBlank(currentValue)
                    || getDoubleValue(currentValue) > maxValue
                    || getDoubleValue(currentValue) < minValue) {
                addError(errors, field, LoanExceptionConstants.INVALIDMINMAX, field, getStringValue(minValue),
                        getStringValue(maxValue));
            }
        } catch (NumberFormatException nfe) {
            addError(errors, field, LoanExceptionConstants.INVALIDMINMAX, field, getStringValue(minValue),
                    getStringValue(maxValue));
        }
    }

    protected void validateFees(HttpServletRequest request, ActionErrors errors) throws ApplicationException {
        validateForFeeAmount(errors);
        validateForDuplicatePeriodicFee(request, errors);
    }

    protected void validateForFeeAmount(ActionErrors errors) {
        List<FeeDto> feeList = getFeesToApply();
        for (FeeDto fee : feeList) {
            if (StringUtils.isBlank(fee.getAmount())) {
                errors.add(LoanConstants.FEE, new ActionMessage(LoanConstants.ERRORS_SPECIFY_FEE_AMOUNT));
            }
        }
    }

    protected void validateForDuplicatePeriodicFee(HttpServletRequest request, ActionErrors errors)
            throws ApplicationException {
        List<FeeDto> additionalFeeList = (List<FeeDto>) SessionUtils.getAttribute(LoanConstants.ADDITIONAL_FEES_LIST,
                request);
        for (FeeDto selectedFee : getAdditionalFees()) {
            int count = 0;
            for (FeeDto duplicateSelectedfee : getAdditionalFees()) {
                if (selectedFee.getFeeIdValue() != null
                        && selectedFee.getFeeId().equals(duplicateSelectedfee.getFeeId())) {
                    if (isSelectedFeePeriodic(selectedFee, additionalFeeList)) {
                        count++;
                    }
                }
            }
            if (count > 1) {
                errors.add(LoanConstants.FEE, new ActionMessage(LoanConstants.ERRORS_DUPLICATE_PERIODIC_FEE));
                break;
            }
        }
    }

    private boolean isSelectedFeePeriodic(FeeDto selectedFee, List<FeeDto> additionalFeeList) {
        for (FeeDto fee : additionalFeeList) {
            if (fee.getFeeId().equals(selectedFee.getFeeId())) {
                return fee.isPeriodic();
            }
        }
        return false;
    }

    private void validateCustomFields(HttpServletRequest request, ActionErrors errors) {
        try {
            List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
                    .getAttribute(LoanConstants.CUSTOM_FIELDS, request);
            for (CustomFieldDto customField : customFields) {
                for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
                    if (customField.getFieldId().equals(customFieldDef.getFieldId())) {
                        if (customFieldDef.isMandatory() && StringUtils.isBlank(customField.getFieldValue())) {
                            errors.add(LoanConstants.CUSTOM_FIELDS, new ActionMessage(
                                    LoanConstants.ERRORS_SPECIFY_CUSTOM_FIELD_VALUE, customFieldDef.getLabel()));
                        }
                        if (customField.getFieldTypeAsEnum().equals(CustomFieldType.DATE) &&
                                (StringUtils.isNotBlank(customField.getFieldValue()))) {
                            try {
                                DateUtils.getDate(customField.getFieldValue());
                            } catch (Exception e) {
                                errors.add(LoanConstants.CUSTOM_FIELDS, new ActionMessage(
                                        LoanConstants.ERRORS_CUSTOM_DATE_FIELD, customFieldDef.getLabel()));
                            }
                        }
                        break;
                    }
                }
            }
        } catch (PageExpiredException pee) {
            errors.add(ExceptionConstants.PAGEEXPIREDEXCEPTION, new ActionMessage(
                    ExceptionConstants.PAGEEXPIREDEXCEPTION));
        }
    }

    void validateSelectedClients(ActionErrors errors) {
        List<String> selectedClients = new ArrayList<String>();
        for (String id : getClients()) {
            if (StringUtils.isNotBlank(id)) {
                selectedClients.add(id);
            }
        }

        if (selectedClients.size() < LoanConstants.MINIMUM_NUMBER_OF_CLIENTS_IN_GROUP_LOAN) {
            addError(errors, "", LoanExceptionConstants.NUMBER_OF_SELECTED_MEMBERS_IS_LESS_THAN_TWO, "");
        }
    }

    void validateSumOfTheAmountsSpecified(ActionErrors errors) {
        List<String> ids_clients_selected = getClients();
        double totalAmount = new Double(0);
        boolean foundInvalidAmount = false;
        for (LoanAccountDetailsDto loanDetail : getClientDetails()) {
            if (!foundInvalidAmount) {
                if (ids_clients_selected.contains(loanDetail.getClientId())) {
                    if (loanDetail.isAmountZeroOrNull()) {
                        addError(errors, LoanExceptionConstants.CUSTOMER_LOAN_AMOUNT_FIELD);
                        foundInvalidAmount = true;
                    } else {
                        totalAmount = totalAmount
                                + new LocalizationConverter()
                                        .getDoubleValueForCurrentLocale(loanDetail.getLoanAmount());
                    }
                }
            }
        }

        if (!foundInvalidAmount
                && (StringUtils.isBlank(Double.valueOf(totalAmount).toString()) || !amountRange.isInRange(totalAmount))) {
            addError(errors, LoanConstants.LOANAMOUNT,
                    LoanExceptionConstants.SUM_OF_INDIVIDUAL_AMOUNTS_IS_NOT_IN_THE_RANGE_OF_ALLOWED_AMOUNTS,
                    getStringValue(amountRange.getMinLoanAmount()), getStringValue(amountRange.getMaxLoanAmount()));
        }
    }

    private void addError(ActionErrors errors, String errorCode) {
        errors.add(errorCode, new ActionMessage(errorCode));
    }

    private void validateRepaymentDayRequired(ActionErrors errors) {
        Short recurrenceId = RecurrenceType.WEEKLY.getValue();
        if (null != this.getRecurrenceId()) {
            recurrenceId = new Short(this.getRecurrenceId());
        }
        if (new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled()) {
            if (StringUtils.isBlank(this.getFrequency())) {
                addError(errors, "", LoanExceptionConstants.REPAYMENTDAYISREQUIRED, "");
            } else if (RecurrenceType.WEEKLY.getValue().equals(recurrenceId)) {
                if (StringUtils.isBlank(this.getRecurWeek()) || StringUtils.isBlank(this.getWeekDay())) {
                    addError(errors, "", LoanExceptionConstants.REPAYMENTDAYISREQUIRED, "");
                }
            } else {
                if (getMonthType().equals("1")) {
                    // "10th day of the month"
                    if (StringUtils.isBlank(this.getMonthDay()) || StringUtils.isBlank(this.getDayRecurMonth())) {
                        addError(errors, "", LoanExceptionConstants.REPAYMENTDAYISREQUIRED, "");
                    }
                } else {
                    // "1st Monday of every month"
                    if (StringUtils.isBlank(this.getMonthRank()) || StringUtils.isBlank(this.getMonthWeek())
                            || StringUtils.isBlank(this.getRecurMonth())) {
                        addError(errors, "", LoanExceptionConstants.REPAYMENTDAYISREQUIRED, "");
                    }
                }
            }
        }
    }

    private void validateIndividualLoanFieldsForGlim(ActionErrors errors, Locale locale, MifosCurrency currency) {
        int errorsBeforeLoanAmountsValidation = errors.size();
        for (LoanAccountDetailsDto listDetail : clientDetails) {
            if (getClients().contains(listDetail.getClientId())) {
                DoubleConversionResult conversionResult = validateAmount(listDetail.getLoanAmount(), currency,
                        LoanConstants.LOAN_AMOUNT_KEY, errors, locale, FilePaths.LOAN_UI_RESOURCE_PROPERTYFILE);
                if (conversionResult.getErrors().size() == 0 && !(conversionResult.getDoubleValue() > 0.0)) {
                    addError(errors, LoanConstants.LOAN_AMOUNT_KEY, LoanConstants.ERRORS_MUST_BE_GREATER_THAN_ZERO,
                            lookupLocalizedPropertyValue(LoanConstants.LOAN_AMOUNT_KEY, locale,
                                    FilePaths.LOAN_UI_RESOURCE_PROPERTYFILE));
                }
            } else {
                if (!listDetail.isEmpty()) {
                    addError(errors, "", LoanExceptionConstants.LOAN_DETAILS_ENTERED_WITHOUT_SELECTING_INDIVIDUAL, "");
                    break;

                }
            }
        }
        int amountValidationErrors = errors.size() - errorsBeforeLoanAmountsValidation;
        if (amountValidationErrors == 0) {
            validateSumOfTheAmountsSpecified(errors);
        }
    }

    private List<FieldConfigurationEntity> getMandatoryFields(HttpServletRequest request) {
        Map<Short, List<FieldConfigurationEntity>> entityMandatoryFieldMap = (Map<Short, List<FieldConfigurationEntity>>) request
                .getSession().getServletContext().getAttribute(Constants.FIELD_CONFIGURATION);
        List<FieldConfigurationEntity> mandatoryfieldList = entityMandatoryFieldMap.get(EntityType.LOAN.getValue());
        return mandatoryfieldList;
    }

    private boolean isPurposeOfLoanMandatory(List<FieldConfigurationEntity> mandatoryfieldList) {
        boolean isMandatory = false;

        for (FieldConfigurationEntity entity : mandatoryfieldList) {

            if (entity.getFieldName().equalsIgnoreCase(LoanConstants.PURPOSE_OF_LOAN)) {
                isMandatory = true;
                break;
            }
        }
        return isMandatory;
    }

    private boolean isSourceOfFundMandatory(List<FieldConfigurationEntity> mandatoryfieldList) {
        boolean isMandatory = false;

        for (FieldConfigurationEntity entity : mandatoryfieldList) {

            if (entity.getFieldName().equalsIgnoreCase(LoanConstants.SOURCE_OF_FUND)) {
                isMandatory = true;
                break;
            }
        }
        return isMandatory;
    }

    private boolean isExternalIDMandatory(List<FieldConfigurationEntity> mandatoryfieldList) {
        boolean isMandatory = false;

        for (FieldConfigurationEntity entity : mandatoryfieldList) {

            if (entity.getFieldName().equalsIgnoreCase(LoanConstants.EXTERNAL_ID)) {
                isMandatory = true;
                break;
            }
        }
        return isMandatory;
    }

    private void validateRedoLoanPayments(HttpServletRequest request, ActionErrors errors, MifosCurrency currency) {
        Locale locale = getUserContext(request).getPreferredLocale();
        try {
            if (paymentDataBeans == null || paymentDataBeans.size() <= 0) {
                return;
            }
            CustomerBO customer = getCustomer(request);
            for (PaymentDataTemplate template : paymentDataBeans) {
                // No data for amount and transaction date, validation not
                // applicable
                if (!template.hasValidAmount() || template.getTransactionDate() == null) {
                    continue;
                }
                // Meeting date is invalid
                if (!customer.getCustomerMeeting().getMeeting().isValidMeetingDate(template.getTransactionDate(),
                        DateUtils.getLastDayOfNextYear())) {
                    errors.add(LoanExceptionConstants.INVALIDTRANSACTIONDATE, new ActionMessage(
                            LoanExceptionConstants.INVALIDTRANSACTIONDATE));
                    continue;
                }

                validateTotalAmount(errors, template.getTotalAmount().toString(), locale, currency);
                // User has enter a payment for future date
                validateTransactionDate(errors, template, getDisbursementDateValue(locale));
            }
        } catch (InvalidDateException invalidDate) {
            errors.add(LoanExceptionConstants.INVALIDTRANSACTIONDATE, new ActionMessage(
                    LoanExceptionConstants.INVALIDTRANSACTIONDATE));
        } catch (MeetingException e) {
            errors.add(ExceptionConstants.FRAMEWORKRUNTIMEEXCEPTION, new ActionMessage(
                    ExceptionConstants.FRAMEWORKRUNTIMEEXCEPTION));
        } catch (PageExpiredException e) {
            errors.add(ExceptionConstants.PAGEEXPIREDEXCEPTION, new ActionMessage(
                    ExceptionConstants.PAGEEXPIREDEXCEPTION));
        } catch (ServiceException e) {
            errors.add(ExceptionConstants.FRAMEWORKRUNTIMEEXCEPTION, new ActionMessage(
                    ExceptionConstants.FRAMEWORKRUNTIMEEXCEPTION));
        }
    }

    protected void validateTotalAmount(ActionErrors errors, String amount, Locale locale, MifosCurrency currency) {
        DoubleConversionResult conversionResult = validateAmount(amount, currency, LoanConstants.LOAN_AMOUNT_KEY, errors, locale,
                FilePaths.LOAN_UI_RESOURCE_PROPERTYFILE);
        if (conversionResult.getErrors().size() == 0 && !(conversionResult.getDoubleValue() > 0.0)) {
            addError(errors, LoanConstants.LOAN_AMOUNT_KEY, LoanConstants.ERRORS_MUST_BE_GREATER_THAN_ZERO,
                    lookupLocalizedPropertyValue(LoanConstants.LOAN_AMOUNT_KEY, locale,
                            FilePaths.LOAN_UI_RESOURCE_PROPERTYFILE));
        }
    }

    void validateTransactionDate(ActionErrors errors, PaymentDataTemplate template, java.util.Date disbursementDate) {
        if (template.getTotalAmount() == null) {
            return;
        }
        try {
            if (!DateUtils.dateFallsOnOrBeforeDate(template.getTransactionDate(), DateUtils.currentDate())) {
                errors.add(LoanExceptionConstants.INVALIDTRANSACTIONDATEFORPAYMENT, new ActionMessage(
                        LoanExceptionConstants.INVALIDTRANSACTIONDATEFORPAYMENT));
            } else if (!DateUtils.dateFallsBeforeDate(disbursementDate, template.getTransactionDate())) {
                errors.add(LoanExceptionConstants.INVALIDTRANSACTIONDATE, new ActionMessage(
                        LoanExceptionConstants.INVALIDTRANSACTIONDATE));
            }
        } catch (InvalidDateException ide) {
            errors.add(LoanExceptionConstants.INVALIDTRANSACTIONDATEFORPAYMENT, new ActionMessage(
                    LoanExceptionConstants.INVALIDTRANSACTIONDATEFORPAYMENT));
        }
    }

    protected CustomerBO getCustomer(Integer customerId) throws ServiceException {
        return getCustomerBusinessService().getCustomer(customerId);
    }

    protected CustomerBusinessService getCustomerBusinessService() {
        return new CustomerBusinessService();
    }

    private CustomerBO getCustomer(HttpServletRequest request) throws PageExpiredException, ServiceException {
        CustomerDetailDto oldCustomer = (CustomerDetailDto) SessionUtils.getAttribute(LoanConstants.LOANACCOUNTOWNER, request);
        Integer oldCustomerId;
        if (oldCustomer == null) {
            oldCustomerId = Integer.parseInt(getCustomerId());
        } else {
            oldCustomerId = oldCustomer.getCustomerId();
        }
        return getCustomer(oldCustomerId);
    }

    public String getPerspective() {
        return perspective;
    }

    public void setPerspective(String perspective) {
        this.perspective = perspective;
    }

    public List<String> getClients() {
        return clients;
    }

    public void setClients(List<String> clients) {
        this.clients = clients;
    }

    public String getClients(int i) {
        while (i >= clients.size()) {
            clients.add("");
        }
        return clients.get(i).toString();
    }

    public void setClients(int i, String string) {

        while (this.clients.size() <= i) {
            this.clients.add(new String());
        }
        this.clients.set(i, string);
    }

    public List<LoanAccountDetailsDto> getClientDetails() {
        return clientDetails;
    }

    public void setClientDetails(List<LoanAccountDetailsDto> clientDetails) {
        this.clientDetails = clientDetails;
    }

    public String getFirstRepaymentDay() {
        return firstRepaymentDay;
    }

    public void setFirstRepaymentDay(String firstRepaymentDay) {
        this.firstRepaymentDay = firstRepaymentDay;
    }

    public void setLoanAmountRange(AmountRange amountRange) {
        this.amountRange = amountRange;
    }

    public void setMaxInterestRate(Double maxInterestRate) {
        this.maxInterestRate = maxInterestRate;
    }

    public void setMinInterestRate(Double minInterestRate) {
        this.minInterestRate = minInterestRate;
    }

    public void setInstallmentRange(InstallmentRange installmentRange) {
        this.installmentRange = installmentRange;
    }

    public String getMinInterestRate() {
        return getDoubleStringForMoney(getMinInterestRateValue());
    }

    public Double getMinInterestRateValue() {
        return minInterestRate;
    }

    public String getMaxInterestRate() {
        return getDoubleStringForMoney(getMaxInterestRateValue());
    }

    public Double getMaxInterestRateValue() {
        return maxInterestRate;
    }

    public String getMinLoanAmount() {
        return getDoubleStringForMoney(getMinLoanAmountValue());
    }

    public Double getMinLoanAmountValue() {
        return amountRange.getMinLoanAmount();
    }

    public String getMaxLoanAmount() {
        return getDoubleStringForMoney(getMaxLoanAmountValue());
    }

    public Double getMaxLoanAmountValue() {
        return amountRange.getMaxLoanAmount();
    }

    public String getMinNoInstallments() {
        return getMinNoInstallmentsValue().toString();
    }

    public Short getMinNoInstallmentsValue() {
        return installmentRange.getMinNoOfInstall();
    }

    public String getMaxNoInstallments() {
        return getMaxNoInstallmentsValue().toString();
    }

    public Short getMaxNoInstallmentsValue() {
        return installmentRange.getMaxNoOfInstall();
    }

    public void removeClientDetailsWithNoMatchingClients() {
        List<LoanAccountDetailsDto> clientDetailsCopy = new ArrayList<LoanAccountDetailsDto>(
                clientDetails);
        CollectionUtils.filter(clientDetailsCopy, new RemoveEmptyClientDetailsForUncheckedClients(getClients()));
        clientDetails = new ArrayList<LoanAccountDetailsDto>(clientDetailsCopy);
    }

    private static class RemoveEmptyClientDetailsForUncheckedClients implements Predicate {

        private final List<String> clients2;

        RemoveEmptyClientDetailsForUncheckedClients(List<String> clients) {
            clients2 = clients;
        }

        public boolean evaluate(Object object) {
            LoanAccountDetailsDto loanDetail = ((LoanAccountDetailsDto) object);
            return !(!clients2.contains(loanDetail.getClientId()) && (loanDetail.isEmpty()));
        }
    }

    public void clearDetailsForLoan() {
        // clear cached additional fees (MIFOS-2547)
        this.additionalFees = new ArrayList<FeeDto>();
        this.externalId = null;
        this.businessActivityId = null;
        this.collateralTypeId = null;
    }
}
