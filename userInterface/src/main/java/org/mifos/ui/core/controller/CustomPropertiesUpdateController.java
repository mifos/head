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

package org.mifos.ui.core.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mifos.config.AccountingRulesConstants;
import org.mifos.core.MifosException;
import org.mifos.framework.business.LogUtils;
import org.mifos.framework.util.AccountingRulesParameters;
import org.mifos.service.test.TestMode;
import org.mifos.service.test.TestingService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class CustomPropertiesUpdateController extends AbstractController {

    private TestingService testingService;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) {
        List<String> errorMessages = new ArrayList<String>();
        ModelAndView returnValue = new ModelAndView("pageNotFound");
        Map<String, Object> model = new HashMap<String, Object>();
        if (TestMode.MAIN == getTestingService().getTestMode()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            String languageCode = request.getParameter("Localization.LanguageCode");
            String countryCode = request.getParameter("Localization.CountryCode");
            if (neitherAreNull(languageCode, countryCode)) {
                try {
                    testingService.setLocale(languageCode, countryCode);
                    model.put("localeResult", "languageCode: " + languageCode + " countryCode: " + countryCode);
                } catch (MifosException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    errorMessages.add("This language code and country code combination are not supported by Mifos.");
                }
            } else if (onlyOneIsNull(languageCode, countryCode)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                errorMessages.add("You must include both Localization.LanguageCode and Localization.CountryCode as parameters!");
            }
            
            String numberOfInterestDays = request.getParameter(AccountingRulesConstants.NUMBER_OF_INTEREST_DAYS);
            String digitsAfterDecimal = request.getParameter(AccountingRulesConstants.DIGITS_AFTER_DECIMAL);
            String digitsAfterDecimalForInterest = request.getParameter(AccountingRulesConstants.DIGITS_AFTER_DECIMAL_FOR_INTEREST);
            String maxInterest = request.getParameter(AccountingRulesConstants.MAX_INTEREST);
            String minInterest = request.getParameter(AccountingRulesConstants.MIN_INTEREST);
            String backDatedTransactionsAllowed = request.getParameter(AccountingRulesConstants.BACKDATED_TRANSACTIONS_ALLOWED);
            try {
                AccountingRulesParameters accountingRulesParameters = new AccountingRulesParameters();
                accountingRulesParameters.setParameters(numberOfInterestDays, digitsAfterDecimal, digitsAfterDecimalForInterest, maxInterest, minInterest, backDatedTransactionsAllowed);
                testingService.setAccountingRules(accountingRulesParameters);
                model.put("accountingRulesResult", accountingRulesParameters.toString());
            } catch (MifosException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                errorMessages.add("Something was wrong with your Accounting Rules parameters: " + new LogUtils().getStackTrace(e) );
            }

            String workingDays = request.getParameter("FiscalCalendarRules.WorkingDays");
            String scheduleTypeForMeetingOnHoliday = request.getParameter("FiscalCalendarRules.ScheduleTypeForMeetingOnHoliday");
            try {
                testingService.setFiscalCalendarRules(workingDays, scheduleTypeForMeetingOnHoliday);
                model.put("fiscalCalendarRulesResult", "workingDays: " + workingDays + " scheduleTypeForMeetingOnHoliday: " + scheduleTypeForMeetingOnHoliday);
            } catch (MifosException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                errorMessages.add("Something was wrong with your Fiscal Calendar Rules parameters: " + new LogUtils().getStackTrace(e) );
            }
 
            model.put("request", request);
            Map<String, Object> status = new HashMap<String, Object>();
            status.put("errorMessages", errorMessages);
            ModelAndView modelAndView = new ModelAndView("customPropertiesUpdate", "model", model);
            modelAndView.addObject("status", status);
            returnValue = modelAndView;
        }
        return returnValue;
    }

    private boolean onlyOneIsNull(String languageCode, String countryCode) {
        return (languageCode == null && countryCode != null) || (languageCode != null && countryCode == null);
    }

    private boolean neitherAreNull(String languageCode, String countryCode) {
        return languageCode != null && countryCode != null;
    }

    public TestingService getTestingService() {
        return testingService;
    }

    public void setTestingService(TestingService testingService) {
        this.testingService = testingService;
    }


}
