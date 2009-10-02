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

package org.mifos.framework.struts.actionforms;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorActionForm;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigurationConstant;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigurationHelper;
import org.mifos.framework.components.tabletag.TableTagConstants;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.FormUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.ConversionResult;
import org.mifos.framework.util.helpers.ConversionError;
import org.mifos.config.AccountingRules;
import org.mifos.framework.util.helpers.FilePaths;

public class BaseActionForm extends ValidatorActionForm {

    protected void checkForMandatoryFields(Short entityId, ActionErrors errors, HttpServletRequest request) {
        Map<Short, List<FieldConfigurationEntity>> entityMandatoryFieldMap = (Map<Short, List<FieldConfigurationEntity>>) request
                .getSession().getServletContext().getAttribute(Constants.FIELD_CONFIGURATION);
        List<FieldConfigurationEntity> mandatoryfieldList = entityMandatoryFieldMap.get(entityId);
        for (FieldConfigurationEntity fieldConfigurationEntity : mandatoryfieldList) {
            String propertyName = request.getParameter(fieldConfigurationEntity.getLabel());
            if (propertyName != null && !propertyName.equals("")) {
                String propertyValue = request.getParameter(propertyName);
                UserContext userContext = ((UserContext) request.getSession().getAttribute(LoginConstants.USERCONTEXT));
                if (propertyValue == null || propertyValue.equals(""))
                    errors.add(fieldConfigurationEntity.getLabel(), new ActionMessage(
                            FieldConfigurationConstant.EXCEPTION_MANDATORY, FieldConfigurationHelper
                                    .getLocalSpecificFieldNames(fieldConfigurationEntity.getLabel(), userContext)));
            }
        }
    }

    protected ConversionResult parseDoubleForMoney(String doubleString) {
        return new LocalizationConverter().parseDoubleForMoney(doubleString);

    }

    protected String getConversionErrorText(ConversionError error, Locale locale) {

        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.UI_RESOURCE_PROPERTYFILE, locale);
        String errorText = resources.getString(error.toString());
        if (error.equals(ConversionError.EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_INTEREST)) {
            errorText = errorText.replaceFirst("%s", AccountingRules.getDigitsAfterDecimalForInterest().toString());
        } else if (error.equals(ConversionError.EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_INTEREST)) {
            errorText = errorText.replaceFirst("%s", AccountingRules.getDigitsBeforeDecimalForInterest().toString());
        } else if (error.equals(ConversionError.EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_MONEY)) {
            errorText = errorText.replaceFirst("%s", AccountingRules.getDigitsBeforeDecimal().toString());
        } else if (error.equals(ConversionError.EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_MONEY)) {
            errorText = errorText.replaceFirst("%s", AccountingRules.getDigitsAfterDecimal().toString());
        } else if (error.equals(ConversionError.INTEREST_OUT_OF_RANGE)) {
            errorText = errorText.replaceFirst("%s1", AccountingRules.getMinInterest().toString());
            errorText = errorText.replaceFirst("%s2", AccountingRules.getMaxInterest().toString());
        }
        return errorText;

    }

    protected ConversionResult parseDoubleForInterest(String doubleString) {
        return new LocalizationConverter().parseDoubleForInterest(doubleString);

    }

    protected Short getShortValue(String str) {
        return StringUtils.isNullAndEmptySafe(str) ? Short.valueOf(str) : null;
    }

    public static Integer getIntegerValue(String str) {
        return StringUtils.isNullAndEmptySafe(str) ? Integer.valueOf(str) : null;
    }

    protected Double getDoubleValue(String str) {
        return FormUtils.getDoubleValue(str);
    }

    protected boolean getBooleanValue(String value) {
        Short shortValue = getShortValue(value);
        return shortValue != null && shortValue > 0;
    }

    protected Money getMoney(String str) {
        return (StringUtils.isNullAndEmptySafe(str) && !str.trim().equals(".")) ? new Money(str) : new Money();
    }

    protected String getStringValue(Double value) {
        // return value != null ? String.valueOf(value) : null;
        return value != null ? new LocalizationConverter().getDoubleValueString(value) : null;
    }

    protected String getStringValue(Short value) {
        return value != null ? String.valueOf(value) : null;
    }

    protected String getStringValue(boolean value) {
        return value ? "1" : "0";
    }

    // TODO: check the usage of this method for hardcoded strings
    // There appear to be many uses of hardcoded strings being passed as
    // a message parameter rather than using localized strings
    protected void addError(ActionErrors errors, String property, String key, String... arg) {
        errors.add(property, new ActionMessage(key, arg));
    }

    protected Date getDateFromString(String strDate, Locale locale) throws InvalidDateException {
        if (StringUtils.isNullAndEmptySafe(strDate)) {
            return new Date(DateUtils.getLocaleDate(locale, strDate).getTime());
        }
        return null;
    }

    protected UserContext getUserContext(HttpServletRequest request) {
        return (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
    }

    protected String getLabel(String key, HttpServletRequest request) {
        return MessageLookup.getInstance().lookupLabel(key, getUserContext(request));
    }

    protected String getLabel(String key, UserContext userContext) {
        return MessageLookup.getInstance().lookupLabel(key, userContext);
    }

    protected String getMessageText(String key, UserContext userContext) {
        return MessageLookup.getInstance().lookup(key, userContext);
    }

    protected void cleanUpSearch(HttpServletRequest request) throws PageExpiredException {
        SessionUtils.setRemovableAttribute("TableCache", null, TableTagConstants.PATH, request.getSession());
        SessionUtils.setRemovableAttribute("current", null, TableTagConstants.PATH, request.getSession());
        SessionUtils.setRemovableAttribute("meth", null, TableTagConstants.PATH, request.getSession());
        SessionUtils.setRemovableAttribute("forwardkey", null, TableTagConstants.PATH, request.getSession());
        SessionUtils.setRemovableAttribute("action", null, TableTagConstants.PATH, request.getSession());
        SessionUtils.removeAttribute(Constants.SEARCH_RESULTS, request);
    }

    public CustomFieldView getCustomField(List<CustomFieldView> customFields, int i) {
        while (i >= customFields.size()) {
            customFields.add(new CustomFieldView());
        }
        return customFields.get(i);
    }

}
