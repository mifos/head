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

package org.mifos.accounts.productdefinition.struts.actionforms;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.loan.util.helpers.LoanExceptionConstants;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.DoubleConversionResult;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.security.util.UserContext;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.core.MifosRuntimeException;

public class SavingsPrdActionForm extends BaseActionForm {
    private MifosLogger prdDefLogger = MifosLogManager.getLogger(LoggerConstants.PRDDEFINITIONLOGGER);

    private String prdOfferingId;

    private String prdOfferingName;

    private String prdOfferingShortName;

    private String description;

    private String prdCategory;

    private String startDate;

    private String endDate;

    private String prdApplicableMaster;

    private String savingsType;

    private String recommendedAmount = "0";

    private String recommendedAmntUnit;

    private String maxAmntWithdrawl = "0";

    private String interestRate;

    private String interestCalcType;

    private String timeForInterestCacl;

    private String recurTypeFortimeForInterestCacl;

    private String freqOfInterest;

    private String minAmntForInt = "0";

    private String depositGLCode;

    private String interestGLCode;

    private String status;

    private String input;

    public String getPrdOfferingId() {
        return prdOfferingId;
    }

    public void setPrdOfferingId(String prdOfferingId) {
        this.prdOfferingId = prdOfferingId;
    }

    public String getDepositGLCode() {
        return depositGLCode;
    }

    public void setDepositGLCode(String depositGLCode) {
        this.depositGLCode = depositGLCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getFreqOfInterest() {
        return freqOfInterest;
    }

    public void setFreqOfInterest(String freqOfInterest) {
        this.freqOfInterest = freqOfInterest;
    }

    public String getInterestCalcType() {
        return interestCalcType;
    }

    public void setInterestCalcType(String interestCalcType) {
        this.interestCalcType = interestCalcType;
    }

    public String getInterestGLCode() {
        return interestGLCode;
    }

    public void setInterestGLCode(String interestGLCode) {
        this.interestGLCode = interestGLCode;
    }

    public String getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(String interestRate) {
        this.interestRate = interestRate;
    }

    public String getMaxAmntWithdrawl() {
        return maxAmntWithdrawl;
    }

    public void setMaxAmntWithdrawl(String maxAmntWithdrawl) {
        this.maxAmntWithdrawl = maxAmntWithdrawl;
    }

    public String getMinAmntForInt() {
        return minAmntForInt;
    }

    public void setMinAmntForInt(String minAmntForInt) {
        this.minAmntForInt = minAmntForInt;
    }

    public String getPrdApplicableMaster() {
        return prdApplicableMaster;
    }

    public void setPrdApplicableMaster(String prdApplicableMaster) {
        this.prdApplicableMaster = prdApplicableMaster;
    }

    public String getPrdCategory() {
        return prdCategory;
    }

    public void setPrdCategory(String prdCategory) {
        this.prdCategory = prdCategory;
    }

    public String getPrdOfferingName() {
        return prdOfferingName;
    }

    public void setPrdOfferingName(String prdOfferingName) {
        this.prdOfferingName = prdOfferingName;
    }

    public String getPrdOfferingShortName() {
        return prdOfferingShortName;
    }

    public void setPrdOfferingShortName(String prdOfferingShortName) {
        this.prdOfferingShortName = prdOfferingShortName;
    }

    public String getRecommendedAmntUnit() {
        return recommendedAmntUnit;
    }

    public void setRecommendedAmntUnit(String recommendedAmntUnit) {
        this.recommendedAmntUnit = recommendedAmntUnit;
    }

    public String getRecommendedAmount() {
        return recommendedAmount;
    }

    public void setRecommendedAmount(String recommendedAmount) {
        this.recommendedAmount = recommendedAmount;
    }

    public String getRecurTypeFortimeForInterestCacl() {
        return recurTypeFortimeForInterestCacl;
    }

    public void setRecurTypeFortimeForInterestCacl(String recurTypeFortimeForInterestCacl) {
        this.recurTypeFortimeForInterestCacl = recurTypeFortimeForInterestCacl;
    }

    public String getSavingsType() {
        return savingsType;
    }

    public void setSavingsType(String savingsType) {
        this.savingsType = savingsType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getTimeForInterestCacl() {
        return timeForInterestCacl;
    }

    public void setTimeForInterestCacl(String timeForInterestCacl) {
        this.timeForInterestCacl = timeForInterestCacl;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public SavingsType getSavingsTypeValue() {
        return (StringUtils.isNotBlank(getSavingsType())) ? SavingsType.fromInt(getShortValue(getSavingsType())) : null;
    }

    public Double getInterestRateValue() {
        return getDoubleValue(getInterestRate());
    }

    public Date getStartDateValue(Locale locale) throws ApplicationException {
        try {
            return DateUtils.getLocaleDate(locale, getStartDate());
        } catch (InvalidDateException ide) {
            throw new ApplicationException(ProductDefinitionConstants.INVALIDSTARTDATE);
        }
    }

    public Date getEndDateValue(Locale locale) throws ApplicationException {
        try {
            return DateUtils.getLocaleDate(locale, getEndDate());
        } catch (InvalidDateException ide) {
            throw new ApplicationException(ProductDefinitionConstants.INVALIDENDDATE);
        }
    }

    public Short getPrdCategoryValue() {
        return getShortValue(getPrdCategory());
    }

    public Short getPrdApplicableMasterValue() {
        return getShortValue(getPrdApplicableMaster());
    }

    public Short getRecommendedAmntUnitValue() {
        return getShortValue(getRecommendedAmntUnit());
    }

    public Short getInterestCalcTypeValue() {
        return getShortValue(getInterestCalcType());
    }

    public Short getRecurTypeFortimeForInterestCaclValue() {
        return getShortValue(getRecurTypeFortimeForInterestCacl());
    }

    public Short getTimeForInterestCalcValue() {
        return getShortValue(getTimeForInterestCacl());
    }

    public Short getFreqOfInterestValue() {
        return getShortValue(getFreqOfInterest());
    }

    public Short getDepositGLCodeValue() {
        return getShortValue(getDepositGLCode());
    }

    public Short getInterestGLCodeValue() {
        return getShortValue(getInterestGLCode());
    }

    public String getStatus() {
        return status;
    }

    public Short getStatusValue() {
        return getShortValue(getStatus());
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        prdDefLogger.debug("start reset method of Savings Product Action form method ");
        super.reset(mapping, request);
        String method = request.getParameter(ProductDefinitionConstants.METHOD);
        if (method != null && method.equals(Methods.load.toString())) {
            try {
                startDate = DateUtils.getCurrentDate(getUserContext(request).getPreferredLocale());
            } catch (InvalidDateException ide) {
                throw new MifosRuntimeException(ide);
            }
        }
        if (method != null && method.equals(Methods.preview.toString())) {
            recommendedAmntUnit = null;
        }
        prdDefLogger.debug("reset method of Savings Product Action form method called ");
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
        String method = request.getParameter(ProductDefinitionConstants.METHOD);
        prdDefLogger.debug("validate method of Savings Product Action form method called :" + method);
        try {
            if (method != null) {

                if (method.equals(Methods.preview.toString())) {
                    errors.add(super.validate(mapping, request));
                    checkPreviewValidation(errors, request);
                } else if (method.equals(Methods.previewManage.toString())) {
                    errors.add(super.validate(mapping, request));
                    checkPreviewManageValidation(errors, request);
                }
            }
        } catch (ApplicationException ae) {
            errors.add(ae.getKey(), new ActionMessage(ae.getKey(), ae.getValues()));
        }

        if (method != null && !method.equals(Methods.validate.toString())) {
            request.setAttribute(ProductDefinitionConstants.METHODCALLED, method);
        }
        prdDefLogger.debug("validate method of Savings Product Action form called and error size:" + errors.size());
        return errors;
    }

    private void checkPreviewValidation(ActionErrors errors, HttpServletRequest request) throws ApplicationException {
        Date startingDate = getStartDateValue(getUserContext(request).getPreferredLocale());
        Date endingDate = getEndDateValue(getUserContext(request).getPreferredLocale());
        //validate start date
        if (startingDate != null
                && ((DateUtils.getDateWithoutTimeStamp(startingDate.getTime()).compareTo(
                        DateUtils.getCurrentDateWithoutTimeStamp()) < 0) || (DateUtils.getDateWithoutTimeStamp(
                        startingDate.getTime()).compareTo(DateUtils.getCurrentDateOfNextYearWithOutTimeStamp()) > 0))) {
            addError(errors, "startDate", ProductDefinitionConstants.INVALIDSTARTDATE);
        }
      //validate end date
        if (startingDate != null && endingDate != null && startingDate.compareTo(endingDate) >= 0) {
            addError(errors, "endDate", ProductDefinitionConstants.INVALIDENDDATE);
        }

        validateRecommendedAmount(errors, request);
        validateMaxAmntWithdrawl(errors, request);
        validateInterestRate(errors, request);
        validateMinAmntForInt(errors, request);
        validateInterestGLCode(request, errors);
    }

    private void checkPreviewManageValidation(ActionErrors errors, HttpServletRequest request) throws ApplicationException {
        validateRecommendedAmount(errors, request);
        validateMaxAmntWithdrawl(errors, request);
        validateInterestRate(errors, request);
        Date startingDate = getStartDateValue(getUserContext(request).getPreferredLocale());
        Date endingDate = getEndDateValue(getUserContext(request).getPreferredLocale());
        SavingsOfferingBO savingsOffering = (SavingsOfferingBO) SessionUtils.getAttribute(
                Constants.BUSINESS_KEY, request);
        validateStartDate(errors, savingsOffering.getStartDate(), startingDate);
        validateEndDateAgainstCurrentDate(errors, startingDate, savingsOffering.getEndDate(), endingDate);
        validateMinAmntForInt(errors, request);
        validateInterestGLCode(request, errors);
    }

    private void validateRecommendedAmount(ActionErrors errors, HttpServletRequest request) {
        Locale locale = getUserContext(request).getPreferredLocale();
        try {
            if (getSavingsTypeValue() != null && getSavingsTypeValue().equals(SavingsType.MANDATORY)
                    && Double.valueOf(getRecommendedAmount()) <= 0.0) {
                addError(errors, "recommendedAmount", ProductDefinitionConstants.ERRORMANDAMOUNT);
                return;
            }
        } catch (NumberFormatException nfe) {
            addError(errors, "recommendedAmount", ProductDefinitionConstants.ERRORMANDAMOUNT);
            return;
        }
        if (getRecommendedAmount() != null && !getRecommendedAmount().equals("")) {
            if (getSavingsTypeValue() != null && getSavingsTypeValue().equals(SavingsType.MANDATORY)) {
                validateAmount(getRecommendedAmount(), ProductDefinitionConstants.MANDATORY_AMOUNT_FOR_DEPOSIT_KEY,
                        errors, locale, FilePaths.PRODUCT_DEFINITION_UI_RESOURCE_PROPERTYFILE);
            } else {
                validateAmount(getRecommendedAmount(), ProductDefinitionConstants.RECOMMENDED_AMOUNT_FOR_DEPOSIT_KEY,
                        errors, locale, FilePaths.PRODUCT_DEFINITION_UI_RESOURCE_PROPERTYFILE);
            }
        }

    }

    private void validateMaxAmntWithdrawl(ActionErrors errors, HttpServletRequest request) {
        Locale locale = getUserContext(request).getPreferredLocale();
        if(getMaxAmntWithdrawl() != null && !getMaxAmntWithdrawl().equals("")){
            validateAmount(getMaxAmntWithdrawl(), ProductDefinitionConstants.MAX_AMOUNT_WITHDRAWL_KEY, errors, locale,
                    FilePaths.PRODUCT_DEFINITION_UI_RESOURCE_PROPERTYFILE);
        }
    }

    private void validateMinAmntForInt(ActionErrors errors, HttpServletRequest request) {
        Locale locale = getUserContext(request).getPreferredLocale();
        if(getMinAmntForInt() != null && !getMinAmntForInt().equals("")){
            validateAmount(getMinAmntForInt(), ProductDefinitionConstants.MIN_BALANCE_FOR_CALC_KEY, errors, locale,
                    FilePaths.PRODUCT_DEFINITION_UI_RESOURCE_PROPERTYFILE);
        }
    }

    private void validateInterestRate(ActionErrors errors, HttpServletRequest request) {
        Locale locale = getUserContext(request).getPreferredLocale();
        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.PRODUCT_DEFINITION_UI_RESOURCE_PROPERTYFILE,
                locale);
        String prdrate = resources.getString("product.prdrate");
        String balanceInterest = resources.getString("product.balanceInterest");
        String timePeriodString = resources.getString("product.timePeriod");
        String frequencyString = resources.getString("product.frequency");
        String calculation = resources.getString("product.calc");
        String postingAccounts = resources.getString("product.postingAccounts");
        if (StringUtils.isBlank(getInterestRate())) {
            addError(errors, "interestRate", ProductDefinitionConstants.ERROR_MANDATORY, getLabel(
                    ConfigurationConstants.INTEREST, request)
                    + " " + prdrate);
        } else {
            try {
                Double intRate = getInterestRateValue();
                // FIXME: hardcoded limit for maximum interest rate.
                if (intRate != null && intRate > 100) {
                    addError(errors, "interestRate", ProductDefinitionConstants.ERRORINTRATE, getLabel(
                            ConfigurationConstants.INTEREST, request)
                            + " " + prdrate);
                }
            } catch (NumberFormatException nfe) {
                addError(errors, "interestRate", ProductDefinitionConstants.ERRORINTRATE, getLabel(
                        ConfigurationConstants.INTEREST, request)
                        + " " + prdrate);
            }
            DoubleConversionResult conversionResult = validateInterest(getInterestRate(),
                    ProductDefinitionConstants.ERRORINTRATE, errors, locale, FilePaths.PRODUCT_DEFINITION_UI_RESOURCE_PROPERTYFILE);
            if (conversionResult.getErrors().size() == 0 && conversionResult.getDoubleValue() < 0.0) {
                addError(errors, ProductDefinitionConstants.ERRORINTRATE, ProductDefinitionConstants.ERROR_MUST_NOT_BE_NEGATIVE,
                        lookupLocalizedPropertyValue(ProductDefinitionConstants.ERRORINTRATE, locale,
                                FilePaths.PRODUCT_DEFINITION_UI_RESOURCE_PROPERTYFILE));
            }
        }
        if (StringUtils.isBlank(getInterestCalcType())) {
            addError(errors, "interestCalcType", ProductDefinitionConstants.ERROR_SELECT, balanceInterest
                    + getLabel(ConfigurationConstants.INTEREST, request) + " " + calculation);
        }
        if (StringUtils.isBlank(getTimeForInterestCacl())) {
            addError(errors, "timeForInterestCacl", ProductDefinitionConstants.ERROR_MANDATORY, timePeriodString
                    + getLabel(ConfigurationConstants.INTEREST, request) + " " + calculation);
        } else {
            int timePeriod = getTimeForInterestCalcValue();
            if (timePeriod <= 0 || timePeriod > 32767) {
                addError(errors, "timeForInterestCacl", ProductDefinitionConstants.ERRORINTRATE, timePeriodString
                        + getLabel(ConfigurationConstants.INTEREST, request) + " " + calculation);
            }
        }
        if (StringUtils.isBlank(getFreqOfInterest())) {
            addError(errors, "freqOfInterest", ProductDefinitionConstants.ERROR_MANDATORY, frequencyString
                    + getLabel(ConfigurationConstants.INTEREST, request) + " " + postingAccounts);
        } else {
            int frequency = getFreqOfInterestValue();
            if (frequency <= 0 || frequency > 32767) {
                addError(errors, "freqOfInterest", ProductDefinitionConstants.ERRORINTRATE, frequencyString
                        + getLabel(ConfigurationConstants.INTEREST, request) + " " + postingAccounts);
            }
        }

    }

    private void validateStartDate(ActionErrors errors, java.util.Date oldStartDate, Date changedStartDate) {
        if (DateUtils.getDateWithoutTimeStamp(oldStartDate.getTime()).compareTo(
                DateUtils.getCurrentDateWithoutTimeStamp()) <= 0
                && (changedStartDate != null && DateUtils.getDateWithoutTimeStamp(oldStartDate.getTime()).compareTo(
                        DateUtils.getDateWithoutTimeStamp(changedStartDate.getTime())) != 0)) {
            addError(errors, "startDate", ProductDefinitionConstants.STARTDATEUPDATEEXCEPTION);

        } else if (changedStartDate != null
                && DateUtils.getDateWithoutTimeStamp(oldStartDate.getTime()).compareTo(
                        DateUtils.getCurrentDateWithoutTimeStamp()) > 0) {
            validateStartDateAgainstCurrentDate(errors, changedStartDate);
            validateStartDateAgainstNextYearDate(errors, changedStartDate);
        }

    }

    private void validateStartDateAgainstNextYearDate(ActionErrors errors, Date changedStartDate) {
        Calendar currentDateCalendar = new DateTimeService().getCurrentDateTime().toGregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        Calendar nextCalendar = new GregorianCalendar(year + 1, month, day);
        Date nextYearDate = new Date(nextCalendar.getTimeInMillis());
        if (DateUtils.getDateWithoutTimeStamp(changedStartDate.getTime()).compareTo(nextYearDate) > 0) {
            addError(errors, "startDate", ProductDefinitionConstants.INVALIDSTARTDATE);
        }

    }

    private void validateStartDateAgainstCurrentDate(ActionErrors errors, Date startDate) {
        if (DateUtils.getDateWithoutTimeStamp(startDate.getTime())
                .compareTo(DateUtils.getCurrentDateWithoutTimeStamp()) < 0) {
            addError(errors, "startDate", ProductDefinitionConstants.INVALIDSTARTDATE);
        }
    }

    private void validateEndDateAgainstCurrentDate(ActionErrors errors, Date startDate, java.util.Date oldEndDate,
            Date endDate) {
        if ((oldEndDate == null && endDate != null)
                || (oldEndDate != null && endDate != null && DateUtils.getDateWithoutTimeStamp(oldEndDate.getTime())
                        .compareTo(DateUtils.getDateWithoutTimeStamp(endDate.getTime())) != 0)) {
            if (DateUtils.getDateWithoutTimeStamp(endDate.getTime()).compareTo(
                            DateUtils.getCurrentDateWithoutTimeStamp()) < 0 || DateUtils.getDateWithoutTimeStamp(
                            startDate.getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(endDate.getTime())) >= 0) {
                addError(errors, "endDate", ProductDefinitionConstants.INVALIDENDDATE);
            }
        }

    }

    private void validateInterestGLCode(HttpServletRequest request, ActionErrors errors) {
        if (StringUtils.isBlank(getInterestGLCode())) {
            UserContext userContext = (UserContext) request.getSession().getAttribute(LoginConstants.USERCONTEXT);
            Locale locale = userContext.getPreferredLocale();
            ResourceBundle resources = ResourceBundle.getBundle(FilePaths.PRODUCT_DEFINITION_UI_RESOURCE_PROPERTYFILE,
                    locale);
            String glCodeFor = resources.getString("product.glCodeFor");
            addError(errors, ProductDefinitionConstants.INTERESTGLCODE, ProductDefinitionConstants.ERROR_SELECT,
                    glCodeFor + getLabel(ConfigurationConstants.INTEREST, request));
        }
    }

    public void clear() {
        this.prdOfferingId = null;
        this.prdOfferingName = null;
        this.prdOfferingShortName = null;
        this.description = null;
        this.prdCategory = null;
        this.startDate = null;
        this.endDate = null;
        this.prdApplicableMaster = null;
        this.savingsType = null;
        this.recommendedAmount = null;
        this.recommendedAmntUnit = null;
        this.maxAmntWithdrawl = null;
        this.interestRate = null;
        this.interestCalcType = null;
        this.timeForInterestCacl = null;
        this.recurTypeFortimeForInterestCacl = null;
        this.freqOfInterest = null;
        this.minAmntForInt = null;
        this.depositGLCode = null;
        this.interestGLCode = null;
        this.status = null;

    }

}
