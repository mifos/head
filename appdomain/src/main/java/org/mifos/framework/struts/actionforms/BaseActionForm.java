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

package org.mifos.framework.struts.actionforms;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorActionForm;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigurationConstant;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigurationHelper;
import org.mifos.framework.components.tabletag.TableTagConstants;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ConversionError;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.DoubleConversionResult;
import org.mifos.framework.util.helpers.FormUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.security.util.UserContext;
import org.springframework.context.MessageSource;

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
                if (propertyValue == null || propertyValue.equals("")) {
                    errors.add(fieldConfigurationEntity.getLabel(), new ActionMessage(
                            FieldConfigurationConstant.EXCEPTION_MANDATORY, FieldConfigurationHelper
                                    .getLocalSpecificFieldNames(fieldConfigurationEntity.getLabel(), userContext)));
                }
            }
        }
    }

    protected DoubleConversionResult parseDoubleForMoney(String doubleString, MifosCurrency currency) {
        LocalizationConverter localizationConverter;
        if (currency == null) {
            localizationConverter = new LocalizationConverter();
        } else {
            localizationConverter = new LocalizationConverter(currency);
        }
        return localizationConverter.parseDoubleForMoney(doubleString);

    }

    protected DoubleConversionResult parseDoubleForMoney(String doubleString) {
        return parseDoubleForMoney(doubleString, null);

    }

    protected String getConversionErrorText(ConversionError error) {
        return error.toLocalizedMessage(null);
    }


    protected Short getShortValue(String str) {
        return StringUtils.isNotBlank(str) ? Short.valueOf(str) : null;
    }

    public static Integer getIntegerValue(String str) {
        return StringUtils.isNotBlank(str) ? Integer.valueOf(str) : null;
    }

    protected Double getDoubleValue(String str) {
        return FormUtils.getDoubleValue(str);
    }

    protected boolean getBooleanValue(String value) {
        Short shortValue = getShortValue(value);
        return shortValue != null && shortValue > 0;
    }

    protected String getStringValue(Double value) {
        // return value != null ? String.valueOf(value) : null;
        return value != null ? new LocalizationConverter().getDoubleValueString(value) : null;
    }

    protected String getDoubleStringForMoney(Double dNumber) {
        return dNumber != null ? new LocalizationConverter().getDoubleStringForMoney(dNumber) : null;
    }

    protected String getDoubleStringForInterest(Double dNumber) {
        return dNumber != null ? new LocalizationConverter().getDoubleStringForInterest(dNumber) : null;
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
        if (StringUtils.isNotBlank(strDate)) {
            return new Date(DateUtils.getLocaleDate(locale, strDate).getTime());
        }
        return null;
    }

    protected UserContext getUserContext(HttpServletRequest request) {
        return (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
    }

    protected String getLabel(String key) {
        return ApplicationContextProvider.getBean(MessageLookup.class).lookupLabel(key);
    }

    protected String getMessageText(String key) {
        return ApplicationContextProvider.getBean(MessageLookup.class).lookup(key);
    }

    protected void cleanUpSearch(HttpServletRequest request) throws PageExpiredException {
        SessionUtils.setRemovableAttribute("TableCache", null, TableTagConstants.PATH, request.getSession());
        SessionUtils.setRemovableAttribute("current", null, TableTagConstants.PATH, request.getSession());
        SessionUtils.setRemovableAttribute("meth", null, TableTagConstants.PATH, request.getSession());
        SessionUtils.setRemovableAttribute("forwardkey", null, TableTagConstants.PATH, request.getSession());
        SessionUtils.setRemovableAttribute("action", null, TableTagConstants.PATH, request.getSession());
        SessionUtils.removeAttribute(Constants.SEARCH_RESULTS, request);
    }

    public CustomFieldDto getCustomField(List<CustomFieldDto> customFields, int i) {
        while (i >= customFields.size()) {
            customFields.add(new CustomFieldDto());
        }
        return customFields.get(i);
    }

    protected DoubleConversionResult validateAmount(String amountString, MifosCurrency currency,
                                                    String fieldPropertyKey, ActionErrors errors, String installmentNo) {
        String fieldName = getLocalizedMessage(fieldPropertyKey);
        DoubleConversionResult conversionResult = parseDoubleForMoney(amountString, currency);
        for (ConversionError error : conversionResult.getErrors()) {
            String errorText = error.toLocalizedMessage(currency);
            addError(errors, fieldPropertyKey, "errors.generic", fieldName, errorText);
        }
        return conversionResult;
    }

    protected DoubleConversionResult validateAmount(String amountString, String fieldPropertyKey, ActionErrors errors) {
        return validateAmount(amountString, null, fieldPropertyKey, errors, "");
    }

    protected DoubleConversionResult validateInterest(String interestString, String fieldPropertyKey,
            ActionErrors errors) {
        String fieldName = getLocalizedMessage(fieldPropertyKey);
        DoubleConversionResult conversionResult = parseDoubleForInterest(interestString);
        for (ConversionError error : conversionResult.getErrors()) {
            addError(errors, fieldPropertyKey, "errors.generic", fieldName, getConversionErrorText(error));
        }
        return conversionResult;
    }

    protected String getLocalizedMessage(String key) {
    	// nothing found so return the key
    	String message = key;
    	PersonnelServiceFacade personnelServiceFacade = ApplicationContextProvider.getBean(PersonnelServiceFacade.class);
    	MessageSource messageSource = ApplicationContextProvider.getBean(MessageSource.class);
    	if(personnelServiceFacade != null) {
    		String value = messageSource.getMessage(key, null, personnelServiceFacade.getUserPreferredLocale());
    		if(StringUtils.isNotEmpty(message)) {
    			message = value;
    		}
    	}
        return message;
    }

    protected DoubleConversionResult parseDoubleForInterest(String doubleString) {
        return new LocalizationConverter().parseDoubleForInterest(doubleString);
    }

}
