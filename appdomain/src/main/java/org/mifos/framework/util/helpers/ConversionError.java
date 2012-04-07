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

package org.mifos.framework.util.helpers;

import java.util.Locale;
import java.util.ResourceBundle;

import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.config.AccountingRules;
import org.springframework.context.MessageSource;

public enum ConversionError {

    EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_MONEY {
        @Override
        public String toLocalizedMessage(MifosCurrency currency) {
            String errorText = super.toLocalizedMessage(currency);
            return errorText.replaceFirst("%s", AccountingRules.getDigitsBeforeDecimal().toString());
        }},

    EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_MONEY {
        @Override
        public String toLocalizedMessage(MifosCurrency currency) {
            String errorText = super.toLocalizedMessage(currency);
            return errorText.replaceFirst("%s", AccountingRules.getDigitsAfterDecimal(currency).toString());
        }},

    EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_INTEREST {
        @Override
        public String toLocalizedMessage(MifosCurrency currency) {
            String errorText = super.toLocalizedMessage(currency);
            return errorText.replaceFirst("%s", AccountingRules.getDigitsBeforeDecimalForInterest().toString());
        }},

    EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_INTEREST {
        @Override
        public String toLocalizedMessage(MifosCurrency currency) {
            String errorText = super.toLocalizedMessage(currency);
            return errorText.replaceFirst("%s", AccountingRules.getDigitsAfterDecimalForInterest().toString());
        }},

    INTEREST_OUT_OF_RANGE {
        @Override
        public String toLocalizedMessage(MifosCurrency currency) {
            String errorText = super.toLocalizedMessage(currency);
            errorText = errorText.replaceFirst("%s1", AccountingRules.getMinInterest().toString());
            errorText = errorText.replaceFirst("%s2", AccountingRules.getMaxInterest().toString());
            return errorText;
        }},

    CASH_FLOW_THRESHOLD_OUT_OF_RANGE {
        @Override
        public String toLocalizedMessage(MifosCurrency currency) {
            String errorText = super.toLocalizedMessage(currency);
            errorText = errorText.replaceFirst("%s1", AccountingRules.getMinCashFlowThreshold().toString());
            errorText = errorText.replaceFirst("%s2", AccountingRules.getMaxCashFlowThreshold().toString());
            return errorText;
        }},

    INDEBTEDNESS_RATIO_OUT_OF_RANGE {
        @Override
        public String toLocalizedMessage(MifosCurrency currency) {
            String errorText = super.toLocalizedMessage(currency);
            errorText = errorText.replaceFirst("%s1", AccountingRules.getMinIndebtednessRatio().toString());
            errorText = errorText.replaceFirst("%s2", AccountingRules.getMaxIndebtednessRatio().toString());
            return errorText;
        }},

    REPAYMENT_CAPACITY_OUT_OF_RANGE {
        @Override
        public String toLocalizedMessage(MifosCurrency currency) {
            String errorText = super.toLocalizedMessage(currency);
            errorText = errorText.replaceFirst("%s1", AccountingRules.getMinRepaymentCapacity().toString());
            errorText = errorText.replaceFirst("%s2", AccountingRules.getMaxRepaymentCapacity().toString());
            return errorText;
        }},

    EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_CASHFLOW_VALIDATION {
        @Override
        public String toLocalizedMessage(MifosCurrency currency) {
            String errorText = super.toLocalizedMessage(currency);
            return errorText.replaceFirst("%s", AccountingRules.getDigitsBeforeDecimalForCashFlowValidations().toString());
        }},

    EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_CASHFLOW_VALIDATION {
        @Override
        public String toLocalizedMessage(MifosCurrency currency) {
            String errorText = super.toLocalizedMessage(currency);
            return errorText.replaceFirst("%s", AccountingRules.getDigitsAfterDecimalForCashFlowValidations().toString());
        }},

    NOT_ALL_NUMBER, CONVERSION_ERROR, NO_ERROR;

    public String toLocalizedMessage(MifosCurrency currency) {
    	Locale locale = ApplicationContextProvider.getBean(PersonnelServiceFacade.class).getUserPreferredLocale();
        return ApplicationContextProvider.getBean(MessageSource.class).getMessage(this.name(), null, locale);
    }

}
