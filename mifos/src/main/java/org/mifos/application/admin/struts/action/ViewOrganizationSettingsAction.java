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

package org.mifos.application.admin.struts.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.AccountingRules;
import org.mifos.config.ClientRules;
import org.mifos.config.ConfigLocale;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.config.ProcessFlowRules;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.batchjobs.helpers.CollectionSheetHelper;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class ViewOrganizationSettingsAction extends BaseAction {
    /** Name of request attribute where organization settings are stored. */
    public static final String ORGANIZATION_SETTINGS = "orgSettings";

    private static final String DELIMITER = ", ";

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("viewOrganizationSettingsAction");
        security.allow("get", SecurityConstants.CAN_VIEW_SYSTEM_INFO);
        return security;
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        Properties orgSettings = new Properties();

        orgSettings.putAll(getFiscalRules());
        orgSettings.putAll(getLocaleInfo());
        orgSettings.putAll(getAccountingRules());
        orgSettings.putAll(getClientRules());
        orgSettings.putAll(getProcessFlowRules());
        orgSettings.putAll(getMiscRules(request.getSession()));

        request.setAttribute(ORGANIZATION_SETTINGS, orgSettings);

        return mapping.findForward(ActionForwards.load_success.toString());
    }

    private Properties getFiscalRules() {
        Properties fiscalRules = new Properties();

        fiscalRules.setProperty("workingDays", getWorkingDays());
        fiscalRules
                .setProperty("allowCalDefForNextYear", FiscalCalendarRules.getDaysForCalendarDefinition().toString());
        fiscalRules.setProperty("startOfWeek", WeekDay.getWeekDay(FiscalCalendarRules.getStartOfWeek()).getName());
        fiscalRules.setProperty("offDays", getOffDays());
        fiscalRules.setProperty("holidayMeeting", FiscalCalendarRules.getScheduleTypeForMeetingOnHoliday());

        return fiscalRules;
    }

    private Properties getLocaleInfo() {
        ConfigLocale configLocale = new ConfigLocale();
        Properties localeInfo = new Properties();

        localeInfo.setProperty("localeCountryCode", configLocale.getCountryCode());
        localeInfo.setProperty("localeLanguageCode", configLocale.getLanguageCode());

        return localeInfo;
    }

    private Properties getAccountingRules() {
        Properties accountingRules = new Properties();

        accountingRules.setProperty("currency", AccountingRules.getCurrencyCode());
        accountingRules.setProperty("maxInterest", AccountingRules.getMaxInterest().toString());
        accountingRules.setProperty("minInterest", AccountingRules.getMinInterest().toString());
        accountingRules.setProperty("digitsAfterDecimal", AccountingRules.getDigitsAfterDecimal().toString());
        accountingRules.setProperty("digitsBeforeDecimal", AccountingRules.getDigitsBeforeDecimal().toString());
        accountingRules.setProperty("intDigitsAfterDecimal", AccountingRules.getDigitsAfterDecimalForInterest()
                .toString());
        accountingRules.setProperty("intDigitsBeforeDecimal", AccountingRules.getDigitsBeforeDecimalForInterest()
                .toString());
        accountingRules.setProperty("interestDays", AccountingRules.getNumberOfInterestDays().toString());
        accountingRules.setProperty("currencyRoundingMode", AccountingRules.getCurrencyRoundingMode().toString());
        accountingRules.setProperty("initialRoundingMode", AccountingRules.getInitialRoundingMode().toString());
        accountingRules.setProperty("finalRoundingMode", AccountingRules.getFinalRoundingMode().toString());
        accountingRules.setProperty("finalRoundOffMultiple", AccountingRules.getFinalRoundOffMultiple().toString());
        accountingRules.setProperty("initialRoundOffMultiple", AccountingRules.getInitialRoundOffMultiple().toString());
        return accountingRules;
    }

    private Properties getClientRules() throws ConfigurationException {
        Properties clientRules = new Properties();

        clientRules.setProperty("centerHierarchyExists", booleanToYesNo(ClientRules.getCenterHierarchyExists()));
        clientRules.setProperty("loansForGroups", booleanToYesNo(ClientRules.getGroupCanApplyLoans()));
        clientRules.setProperty("clientsOutsideGroups", booleanToYesNo(ClientRules.getClientCanExistOutsideGroup()));
        clientRules.setProperty("nameSequence", StringUtils.join(ClientRules.getNameSequence(), DELIMITER));

        return clientRules;
    }

    private Properties getProcessFlowRules() {
        Properties processFlowRules = new Properties();

        processFlowRules.setProperty("clientPendingState", booleanToYesNo(ProcessFlowRules
                .isClientPendingApprovalStateEnabled()));
        processFlowRules.setProperty("groupPendingState", booleanToYesNo(ProcessFlowRules
                .isGroupPendingApprovalStateEnabled()));
        processFlowRules.setProperty("loanDisbursedState", booleanToYesNo(ProcessFlowRules
                .isLoanDisbursedToLoanOfficerStateEnabled()));
        processFlowRules.setProperty("loanPendingState", booleanToYesNo(ProcessFlowRules
                .isLoanPendingApprovalStateEnabled()));
        processFlowRules.setProperty("savingsPendingState", booleanToYesNo(ProcessFlowRules
                .isSavingsPendingApprovalStateEnabled()));

        return processFlowRules;
    }

    private Properties getMiscRules(HttpSession httpSession) {
        Properties misc = new Properties();

        Integer timeoutVal = httpSession.getMaxInactiveInterval() / 60;
        misc.setProperty("sessionTimeout", timeoutVal.toString());

        try {
            Integer advanceDaysVal = CollectionSheetHelper.getDaysInAdvance();
            misc.setProperty("collectionSheetAdvanceDays", advanceDaysVal.toString());
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }

        misc.setProperty("backDatedTransactions", booleanToYesNo(AccountingRules.isBackDatedTxnAllowed()));

        return misc;
    }

    private String getWorkingDays() {
        List<WeekDay> workDaysList = FiscalCalendarRules.getWorkingDays();
        List<String> workDayNames = new ArrayList<String>();
        for (WeekDay workDay : workDaysList) {
            workDayNames.add(workDay.getName());
        }
        return StringUtils.join(workDayNames, DELIMITER);
    }

    private String getWeekDays() {
        List<WeekDay> weekDaysList = FiscalCalendarRules.getWeekDaysList();
        List<String> weekDayNames = new ArrayList<String>();
        for (WeekDay weekDay : weekDaysList)
            weekDayNames.add(weekDay.getName());
        return StringUtils.join(weekDayNames, DELIMITER);
    }

    private String getOffDays() {
        List<Short> offDaysList = FiscalCalendarRules.getWeekDayOffList();
        List<String> offDayNames = new ArrayList<String>();
        for (Short offDayNum : offDaysList)
            offDayNames.add(WeekDay.getWeekDay(offDayNum).getName());
        return StringUtils.join(offDayNames, DELIMITER);
    }

    private String booleanToYesNo(boolean bool) {
        MessageLookup m = MessageLookup.getInstance();
        if (bool)
            return m.lookup(YesNoFlag.YES);
        else
            return m.lookup(YesNoFlag.NO);
    }

    @Override
    protected BusinessService getService() throws ServiceException {
        return null;
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
    }
}
