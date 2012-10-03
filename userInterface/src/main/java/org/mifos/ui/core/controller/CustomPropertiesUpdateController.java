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

package org.mifos.ui.core.controller;

import org.apache.commons.lang.StringUtils;
import org.mifos.core.MifosException;
import org.mifos.framework.business.LogUtils;
import org.mifos.service.test.TestMode;
import org.mifos.service.test.TestingService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
@Controller
public class CustomPropertiesUpdateController extends AbstractController {

    private TestingService testingService;

    @Override
    @RequestMapping("/customPropertiesUpdate.ftl")
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) {
        List<String> errorMessages = new ArrayList<String>();
        ModelAndView returnValue = new ModelAndView("pageNotFound");
        Map<String, Object> model = new HashMap<String, Object>();
        if (TestMode.MAIN == getTestingService().getTestMode()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            handleLocalization(request, response, errorMessages, model);

            handleAccountingRules(request, response, errorMessages, model);

            handleCalendarRules(request, response, errorMessages, model);

            handleMinMaxClientAge(request, response, errorMessages, model);

            handleFamilyDetails(request, response, errorMessages, model);

            handleImport(request, response, errorMessages, model);

            handleProcessFLow(request, response, errorMessages, model);

            handleCenterHierarchy(request, model);

            handleClientNameSequence(request, model);

            handleGroupCanApplyLoans(request, model);

            handleClientCanExistOutsideGroup(request, model);

            handleBackDatedTransactionsAllowed(request, model);

            handleOverdueInterestPaidFirst(request, model);
            
            model.put("request", request);
            Map<String, Object> status = new HashMap<String, Object>();
            status.put("errorMessages", errorMessages);
            ModelAndView modelAndView = new ModelAndView("customPropertiesUpdate", "model", model);
            modelAndView.addObject("status", status);
            returnValue = modelAndView;
        }
        return returnValue;
    }

    private void handleClientNameSequence(HttpServletRequest request, Map<String, Object> model) {
        String nameSequenceString = request.getParameter("ClientRules.NameSequence");
        if (StringUtils.isNotBlank(nameSequenceString)) {
            String[] nameSequence = nameSequenceString.split(",");
            testingService.setClientNameSequence(nameSequence);
            model.put("clientRulesResult", "NameSequence: " + nameSequenceString);
        }
    }

    private void handleCenterHierarchy(HttpServletRequest request, Map<String, Object> model) {
        String centerHierarchyExists = request.getParameter("ClientRules.CenterHierarchyExists");
        if (StringUtils.isNotBlank(centerHierarchyExists)) {
            boolean required=Boolean.valueOf(centerHierarchyExists);
            testingService.setCenterHierarchyExists(required);
            model.put("clientRulesResult", "centerHierarchyExists: " + centerHierarchyExists);
        }
    }

    private void handleClientCanExistOutsideGroup(HttpServletRequest request, Map<String, Object> model) {
        String clientCanExistOutsideGroup = request.getParameter("ClientRules.ClientCanExistOutsideGroup");
        if (StringUtils.isNotBlank(clientCanExistOutsideGroup)) {
            boolean required=Boolean.valueOf(clientCanExistOutsideGroup);
            testingService.setClientCanExistOutsideGroup(required);
            model.put("clientRulesResult", "clientCanExistOutsideGroup: " + clientCanExistOutsideGroup);
        }
    }

    private void handleGroupCanApplyLoans(HttpServletRequest request, Map<String, Object> model) {
        String groupCanApplyLoans = request.getParameter("ClientRules.GroupCanApplyLoans");
        if (StringUtils.isNotBlank(groupCanApplyLoans)) {
            boolean required=Boolean.valueOf(groupCanApplyLoans);
            testingService.setGroupCanApplyLoans(required);
            model.put("clientRulesResult", "groupCanApplyLoans: " + groupCanApplyLoans);
        }
    }

    private void handleBackDatedTransactionsAllowed(HttpServletRequest request, Map<String, Object> model) {
        String backDatedTransactionsAllowed = request.getParameter("BackDatedTransactionsAllowed");
        if (StringUtils.isNotBlank(backDatedTransactionsAllowed)) {
            boolean allowed=Boolean.valueOf(backDatedTransactionsAllowed);
            testingService.setBackDatedTransactionsAllowed(allowed);
            model.put("clientRulesResult", "backDatedTransactionsAllowed: " + backDatedTransactionsAllowed);
        }
    }

    private void handleMinMaxClientAge(HttpServletRequest request, HttpServletResponse response,
            List<String> errorMessages, Map<String, Object> model) {
        String minimumAge = request.getParameter("ClientRules.MinimumAgeForNewClients");
        if (StringUtils.isNotBlank(minimumAge)) {
            try {
                int minimumAgeForNewClient = Integer.parseInt(minimumAge);
                testingService.setMinimumAgeForNewClient(minimumAgeForNewClient);
                model.put("clientRulesResult", "minimumAge: " + minimumAge);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                errorMessages.add("Unable to parse and int from ClientRules.MinimumAgeForNewClients: " + new LogUtils().getStackTrace(e) );
            }
        }

        String maximumAge = request.getParameter("ClientRules.MaximumAgeForNewClients");
        if (StringUtils.isNotBlank(maximumAge)) {
            try {
                int maximumAgeForNewClient = Integer.parseInt(maximumAge);
                testingService.setMaximumAgeForNewClient(maximumAgeForNewClient);
                model.put("clientRulesResult", "maximumAge: " + maximumAge);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                errorMessages.add("Unable to parse and int from ClientRules.MaximumAgeForNewClients: " + new LogUtils().getStackTrace(e) );
            }
        }
    }

    private void handleFamilyDetails(HttpServletRequest request, HttpServletResponse response,
            List<String> errorMessages, Map<String, Object> model){
        String areFamilyDetailsRequired=request.getParameter("ClientInformation.AreFamilyDetailsRequired");
        if (StringUtils.isNotBlank(areFamilyDetailsRequired)) {
             boolean required=Boolean.valueOf(areFamilyDetailsRequired);
             testingService.setAreFamilyDetailsRequired(required);
             model.put("clientRulesResult", "areFamilyDetailsRequired: " + required);
        }

        String numberOfFamilyMembers=request.getParameter("ClientInformation.MaximumNumberOfFamilyMembers");
        if (StringUtils.isNotBlank(numberOfFamilyMembers)) {
            try {
                int maximumNumberOfFamilyMembers = Integer.parseInt(numberOfFamilyMembers);
                testingService.setMaximumNumberOfFamilyMembers(maximumNumberOfFamilyMembers);
                model.put("clientRulesResult", "maximumNumberOfFamilyMembers: " + maximumNumberOfFamilyMembers);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                errorMessages.add("Unable to parse and int from ClientInformation.MaximumNumberOfFamilyMembers: " + new LogUtils().getStackTrace(e) );
            }
        }

    }

    private void handleCalendarRules(HttpServletRequest request, HttpServletResponse response,
            List<String> errorMessages, Map<String, Object> model) {
        String workingDays = request.getParameter("FiscalCalendarRules.WorkingDays");
        String ScheduleMeetingIfNonWorkingDay = request.getParameter("FiscalCalendarRules.ScheduleMeetingIfNonWorkingDay");
        if (StringUtils.isNotBlank(workingDays) || StringUtils.isNotBlank(ScheduleMeetingIfNonWorkingDay)) {
            try {
                testingService.setFiscalCalendarRules(workingDays, ScheduleMeetingIfNonWorkingDay);
                model.put("fiscalCalendarRulesResult", "workingDays: " + workingDays + " ScheduleMeetingIfNonWorkingDay: " + ScheduleMeetingIfNonWorkingDay);
            } catch (MifosException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                errorMessages.add("Something was wrong with your Fiscal Calendar Rules parameters: " + new LogUtils().getStackTrace(e) );
            }
        }
    }

    private void handleAccountingRules(HttpServletRequest request, HttpServletResponse response,
            List<String> errorMessages, Map<String, Object> model) {
        try {
            Enumeration<?> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String accountingRulesParamName = (String) paramNames.nextElement();
                if (accountingRulesParamName.startsWith("AccountingRules")) {
                    String accountingRulesParamValue = request.getParameter(accountingRulesParamName);
                    testingService.setAccountingRules(accountingRulesParamName, accountingRulesParamValue);
                    model.put("accountingRulesResult", accountingRulesParamName + ": " + accountingRulesParamValue);
                }
            }
        } catch (MifosException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            errorMessages.add("Something was wrong with your Accounting Rules parameters: "
                    + new LogUtils().getStackTrace(e));
        }
    }

    private void handleLocalization(HttpServletRequest request, HttpServletResponse response,
            List<String> errorMessages, Map<String, Object> model) {
        String languageCode = request.getParameter("Localization.LanguageCode");
        String countryCode = request.getParameter("Localization.CountryCode");
        if (StringUtils.isNotBlank(languageCode) && StringUtils.isNotBlank(countryCode)) {
            try {
                testingService.setLocale(languageCode, countryCode);
                model.put("localeResult", "languageCode: " + languageCode + " countryCode: " + countryCode);
            } catch (MifosException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                errorMessages.add("This language code and country code combination are not supported by Mifos.");
            }
        } else if (StringUtils.isNotBlank(languageCode) || StringUtils.isNotBlank(countryCode)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            errorMessages.add("You must include both Localization.LanguageCode and Localization.CountryCode as parameters!");
        }
    }

    private void handleImport(HttpServletRequest request, HttpServletResponse response,
            List<String> errorMessages, Map<String, Object> model) {
        try {
            Enumeration<?> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String importParamName = (String) paramNames.nextElement();
                if (importParamName.startsWith("ke.co.safaricom.MPesaXlsImporter")) {
                    String importParamValue = request.getParameter(importParamName);
                    testingService.setImport(importParamName, importParamValue);
                    model.put("ImportResult", importParamName + ": " + importParamValue);
                }
            }
        } catch (MifosException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            errorMessages.add("Something was wrong with your Import parameters: "
                    + new LogUtils().getStackTrace(e));
        }
    }

    private void handleProcessFLow(HttpServletRequest request, HttpServletResponse response,
            List<String> errorMessages, Map<String, Object> model) {
        try {
            Enumeration<?> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String processFlowParamName = (String) paramNames.nextElement();
                if (processFlowParamName.startsWith("ProcessFlow")) {
                    String processFlowParamValue = request.getParameter(processFlowParamName);
                    testingService.setProcessFlow(processFlowParamName, processFlowParamValue);
                    model.put("processFlowResult", processFlowParamName + ": " + processFlowParamValue);
                }
            }
        } catch (MifosException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            errorMessages.add("Something was wrong with your Process Flow parameters: "
                    + new LogUtils().getStackTrace(e));
        }
    }

    public TestingService getTestingService() {
        return testingService;
    }

    public void setTestingService(TestingService testingService) {
        this.testingService = testingService;
    }
    
    private void handleOverdueInterestPaidFirst(HttpServletRequest request, Map<String, Object> model) {
        String overdueInterestPaidFirst = request.getParameter("OverdueInterestPaidFirst");
        if (StringUtils.isNotBlank(overdueInterestPaidFirst)) {
            boolean allowed=Boolean.valueOf(overdueInterestPaidFirst);
            testingService.setOverdueInterestPaidFirst(allowed);
            model.put("accountingRulesResult", "OverdueInterestPaidFirst: " + overdueInterestPaidFirst);
        }
    }


}
