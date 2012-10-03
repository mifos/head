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

package org.mifos.test.acceptance.framework.testhelpers;

import com.thoughtworks.selenium.Selenium;

public class CustomPropertiesHelper {
    private static final String UPDATE_PAGE = "customPropertiesUpdate.ftl";

    public static final String ROUNDING_MODE_FLOOR = "FLOOR";
    public static final String ROUNDING_MODE_CEILING = "CEILING";
    public static final String ROUNDING_MODE_HALF_UP = "HALF_UP";

    private final Selenium selenium;

    public CustomPropertiesHelper(Selenium selenium) {
        this.selenium = selenium;
    }

    /**
     * Sets the language and country code.
     * See application\src\main\resources\org\mifos\config\resources\applicationConfiguration.default.properties
     * for more details about these two settings.
     * @param languageCode The language code.
     * @param countryCode The country code.
     */
    public void setLocale(String languageCode, String countryCode) {
        selenium.open(UPDATE_PAGE + "?Localization.LanguageCode="+languageCode+"&Localization.CountryCode=" + countryCode);
    }

    /**
     * Sets the number of digits after the decimal sign to <tt>digits</tt>.
     * @param digits Number of digits after the decimal.
     */
    public void setDigitsAfterDecimal(int digits) {
        selenium.open(UPDATE_PAGE + "?AccountingRules.DigitsAfterDecimal=" + digits);
    }

    /**
     * Sets the minimum age constraint for the clients
     * See application\src\main\resources\org\mifos\config\resources\applicationConfiguration.default.properties
     * @param minimumAge The minimum age for clients.
     */
    public void setMinimumAgeForClients(int minimumAge) {
        selenium.open(UPDATE_PAGE + "?ClientRules.MinimumAgeForNewClients=" + minimumAge);
    }

    /**
     * Sets the maximum age constraint for the clients
     * See application\src\main\resources\org\mifos\config\resources\applicationConfiguration.default.properties
     * @param maximumAge The maximum age for clients.
     */
    public void setMaximumAgeForClients(int maximumAge) {
        selenium.open(UPDATE_PAGE + "?ClientRules.MaximumAgeForNewClients=" + maximumAge);
    }

    public void setCenterHierarchyExists(String exists){
        selenium.open(UPDATE_PAGE + "?ClientRules.CenterHierarchyExists=" + exists);
    }

    public void setGroupCanApplyLoans(String flag){
        selenium.open(UPDATE_PAGE + "?ClientRules.GroupCanApplyLoans=" + flag);
    }

    public void setClientCanExistOutsideGroup(String flag){
        selenium.open(UPDATE_PAGE + "?ClientRules.ClientCanExistOutsideGroup=" + flag);
    }

    public void setBackDatedTransactionsAllowed(String flag){
        selenium.open(UPDATE_PAGE + "?BackDatedTransactionsAllowed=" + flag);
    }

    /**
     * Determines ordering of displayed client name.
     * @param sequence Name sequence, comma-separated values.
     * ex. "first_name,middle_name,last_name,second_last_name"
     */
    public void setClientsNameSequence(String sequence) {
        selenium.open(UPDATE_PAGE + "?ClientRules.NameSequence=" + sequence);
    }

    /**
     * This is to decide whether the user requires to store the client family information
     * See application\src\main\resources\org\mifos\config\resources\applicationConfiguration.default.properties
     * @param req True if its required and false if its not required
     */
    public void setAreFamilyDetailsRequired(boolean req) {
        selenium.open(UPDATE_PAGE + "?ClientInformation.AreFamilyDetailsRequired=" + req);
    }

    /**
     * This is to set the maximum number of family members per client
     * See application\src\main\resources\org\mifos\config\resources\applicationConfiguration.default.properties
     * @param maximumNumberOfFamilyMembers Specifies the maximum number of family members for each client
     */
    public void setMaximumNumberOfFamilyMemebers(int maximumNumberOfFamilyMembers) {
        selenium.open(UPDATE_PAGE + "?ClientInformation.MaximumNumberOfFamilyMembers=" + maximumNumberOfFamilyMembers);
    }

    /**
     * Sets the number of digits after the decimal for interest sign to <tt>digits</tt>.
     * @param digits Number of digits after the decimal.
     */
    public void setDigitsAfterDecimalForInterest(int digits) {
        selenium.open(UPDATE_PAGE + "?AccountingRules.DigitsAfterDecimalForInterest=" + digits);
    }

    /**
     * Sets maximum allowed interest rate <tt>digits</tt>.
     * @param interest maximum interest rate.
     */
    public void setMaxInterest(int interest) {
        selenium.open(UPDATE_PAGE + "?AccountingRules.MaxInterest=" + interest);
    }

    /**
     * Sets minimum allowed interest rate <tt>digits</tt>.
     * @param interest minimum interest rate.
     */
    public void setMinInterest(int interest) {
        selenium.open(UPDATE_PAGE + "?AccountingRules.MinInterest=" + interest);
    }

    /**
     * Sets Working days of the week.
     * @param workingDays working days.
     */
    public void setWorkingDays(String workingDays) {
        selenium.open(UPDATE_PAGE + "?FiscalCalendarRules.WorkingDays=" + workingDays);
    }

    /**
     * This is to set the additional currencies
     * See application\src\main\resources\org\mifos\config\resources\applicationConfiguration.default.properties
     * @param additionalCurrencies additional currencies
     */
    public void setAdditionalCurrenciesCode(String additionalCurrencies) {
        selenium.open(UPDATE_PAGE + "?AccountingRules.AdditionalCurrencyCodes=" + additionalCurrencies);
    }

    public void setCurrencyRoundingMode(String currencyRoundingMode) {
        selenium.open(UPDATE_PAGE + "?AccountingRules.CurrencyRoundingMode=" + currencyRoundingMode);
    }

    public void setInitialRoundingMode(String initialRoundingMode) {
        selenium.open(UPDATE_PAGE + "?AccountingRules.InitialRoundingMode=" + initialRoundingMode);
    }

    public void setFinalRoundingMode(String finalRoundingMode) {
        selenium.open(UPDATE_PAGE + "?AccountingRules.FinalRoundingMode=" + finalRoundingMode);
    }

    public void setFinalRoundOffMultiple(String finalRoundOffMultiple) {
        selenium.open(UPDATE_PAGE + "?AccountingRules.FinalRoundOffMultiple=" + finalRoundOffMultiple);
    }

    public void setInitialRoundOffMultiple(String initialRoundOffMultiple) {
        selenium.open(UPDATE_PAGE + "?AccountingRules.InitialRoundOffMultiple=" + initialRoundOffMultiple);
    }
    
    public void setOverdueInterestPaidFirst(String overdueInterestPaidFirst) {
        selenium.open(UPDATE_PAGE + "?OverdueInterestPaidFirst=" + overdueInterestPaidFirst);
    }
    

    /**
     * Set order of products a transaction import
     * @param order
     */
    public void setImportTransactionOrder(String order){
        selenium.open(UPDATE_PAGE + "?ke.co.safaricom.MPesaXlsImporter.ImportTransactionOrder=" + order);
    }

    public void setGroupPendingApprovalStateEnabled(String enabled){
        selenium.open(UPDATE_PAGE + "?ProcessFlow.GroupPendingApprovalStateEnabled=" + enabled);
    }

    public void setSavingsPendingApprovalStateEnabled(String enabled){
        selenium.open(UPDATE_PAGE + "?ProcessFlow.SavingsPendingApprovalStateEnabled=" + enabled);
    }

    public void setLoanPendingApprovalStateEnabled(String enabled){
        selenium.open(UPDATE_PAGE + "?ProcessFlow.LoanPendingApprovalStateEnabled=" + enabled);
    }

    public void setClientPendingApprovalStateEnabled(String enabled){
        selenium.open(UPDATE_PAGE + "?ProcessFlow.ClientPendingApprovalStateEnabled=" + enabled);
    }

    public void setAllowBackdatedApproval(boolean allow) {
        selenium.open(UPDATE_PAGE + "?BackDatedApprovalsAllowed?=" + allow);
    }
}
