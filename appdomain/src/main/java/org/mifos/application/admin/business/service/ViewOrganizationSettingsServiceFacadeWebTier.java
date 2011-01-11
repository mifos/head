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

package org.mifos.application.admin.business.service;

import org.apache.commons.lang.StringUtils;
import org.mifos.accounts.api.TransactionImport;
import org.mifos.application.admin.servicefacade.ViewOrganizationSettingsServiceFacade;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.AccountingRules;
import org.mifos.config.ClientRules;
import org.mifos.config.ConfigLocale;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.config.ProcessFlowRules;
import org.mifos.config.business.service.ConfigurationBusinessService;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.mifos.framework.plugin.PluginManager;

public class ViewOrganizationSettingsServiceFacadeWebTier implements ViewOrganizationSettingsServiceFacade {
    private static final String DELIMITER = ", ";

    @Override
    public Properties getOrganizationSettings(HttpSession httpSession) {
        Properties orgSettings = new Properties();

        orgSettings.putAll(getFiscalRules());
        orgSettings.putAll(getLocaleInfo());
        orgSettings.putAll(getAccountingRules());
        orgSettings.put("currencies", getCurrencies());
        orgSettings.putAll(getClientRules());
        orgSettings.putAll(getProcessFlowRules());
        orgSettings.putAll(getMiscRules(httpSession));

        return orgSettings;
    }

    private Properties getFiscalRules() {
        Properties fiscalRules = new Properties();

        fiscalRules.setProperty("workingDays", getWorkingDays());

        FiscalCalendarRules fiscalCalendarRules = new FiscalCalendarRules();
        Short startOfWeekValue = fiscalCalendarRules.getStartOfWeek();
        WeekDay startOfWeek = WeekDay.getWeekDay(startOfWeekValue);
        String weekdayName = MessageLookup.getInstance().lookup(startOfWeek.getPropertiesKey());
        startOfWeek.setWeekdayName(weekdayName);

        fiscalRules.setProperty("startOfWeek", startOfWeek.getName());
        fiscalRules.setProperty("offDays", getOffDays());
        fiscalRules.setProperty("holidayMeeting", fiscalCalendarRules.getScheduleMeetingIfNonWorkingDay());

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

        accountingRules.setProperty("maxInterest", AccountingRules.getMaxInterest().toString());
        accountingRules.setProperty("minInterest", AccountingRules.getMinInterest().toString());
        accountingRules.setProperty("digitsBeforeDecimal", AccountingRules.getDigitsBeforeDecimal().toString());
        accountingRules.setProperty("intDigitsAfterDecimal", AccountingRules.getDigitsAfterDecimalForInterest()
                .toString());
        accountingRules.setProperty("intDigitsBeforeDecimal", AccountingRules.getDigitsBeforeDecimalForInterest()
                .toString());
        accountingRules.setProperty("interestDays", AccountingRules.getNumberOfInterestDays().toString());
        accountingRules.setProperty("currencyRoundingMode", AccountingRules.getCurrencyRoundingMode().toString());
        accountingRules.setProperty("initialRoundingMode", AccountingRules.getInitialRoundingMode().toString());
        accountingRules.setProperty("finalRoundingMode", AccountingRules.getFinalRoundingMode().toString());
        return accountingRules;
    }

    private List<Properties> getCurrencies() {
        List<Properties> currencies = new ArrayList<Properties>();

        for (MifosCurrency currency : AccountingRules.getCurrencies()) {
            Properties currencyRules = new Properties();
            currencyRules.setProperty("code", currency.getCurrencyCode());
            currencyRules.setProperty("digitsAfterDecimal", AccountingRules.getDigitsAfterDecimal(currency).toString());
            currencyRules.setProperty("finalRoundOffMultiple", AccountingRules.getFinalRoundOffMultiple(currency).toString());
            currencyRules.setProperty("initialRoundOffMultiple", AccountingRules.getInitialRoundOffMultiple(currency).toString());
            currencies.add(currencyRules);
        }

        return currencies;
    }

    private Properties getClientRules() {
        Properties clientRules = new Properties();

        clientRules.setProperty("centerHierarchyExists", booleanToYesNo(ClientRules.getCenterHierarchyExists()));
        clientRules.setProperty("loansForGroups", booleanToYesNo(ClientRules.getGroupCanApplyLoans()));
        clientRules.setProperty("clientsOutsideGroups", booleanToYesNo(ClientRules.getClientCanExistOutsideGroup()));
        clientRules.setProperty("nameSequence", StringUtils.join(ClientRules.getNameSequence(), DELIMITER));
        clientRules.setProperty("isAgeCheckEnabled",booleanToYesNo(ClientRules.isAgeCheckEnabled()));
        clientRules.setProperty("maximumAge", String.valueOf(ClientRules.getMaximumAgeForNewClient()));
        clientRules.setProperty("minimumAge", String.valueOf(ClientRules.getMinimumAgeForNewClient()));
        clientRules.setProperty("isFamilyDetailsRequired",booleanToYesNo(ClientRules.isFamilyDetailsRequired()));
        clientRules.setProperty("maximumNumberOfFamilyMembers",String.valueOf(ClientRules.getMaximumNumberOfFamilyMembers()));
        return clientRules;
    }

    private Properties getProcessFlowRules() {
        Properties processFlowRules = new Properties();

        processFlowRules.setProperty("clientPendingState", booleanToYesNo(ProcessFlowRules
                .isClientPendingApprovalStateEnabled()));
        processFlowRules.setProperty("groupPendingState", booleanToYesNo(ProcessFlowRules
                .isGroupPendingApprovalStateEnabled()));
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

        // FIXME - #00001 - keithw - Check days in advance usage in CollectionsheetHelper
//            Integer advanceDaysVal = CollectionSheetHelper.getDaysInAdvance();
        misc.setProperty("collectionSheetAdvanceDays", "1");

        misc.setProperty("backDatedTransactions", booleanToYesNo(AccountingRules.isBackDatedTxnAllowed()));
        ConfigurationBusinessService cbs = new ConfigurationBusinessService();
        misc.setProperty("glim", booleanToYesNo(cbs.isGlimEnabled()));
        misc.setProperty("lsim", booleanToYesNo(cbs.isRepaymentIndepOfMeetingEnabled()));

        return misc;
    }

    private String getWorkingDays() {
        List<WeekDay> workDaysList = new FiscalCalendarRules().getWorkingDays();
        List<String> workDayNames = new ArrayList<String>();
        for (WeekDay workDay : workDaysList) {
            String weekdayName = MessageLookup.getInstance().lookup(workDay.getPropertiesKey());
            workDay.setWeekdayName(weekdayName);
            workDayNames.add(workDay.getName());
        }
        return StringUtils.join(workDayNames, DELIMITER);
    }

    private String getOffDays() {
        List<Short> offDaysList = new FiscalCalendarRules().getWeekDayOffList();
        List<String> offDayNames = new ArrayList<String>();
        for (Short offDayNum : offDaysList) {
            WeekDay weekDay = WeekDay.getWeekDay(offDayNum);
            String weekdayName = MessageLookup.getInstance().lookup(weekDay.getPropertiesKey());
            weekDay.setWeekdayName(weekdayName);
            offDayNames.add(weekDay.getName());
        }
        return StringUtils.join(offDayNames, DELIMITER);
    }

    private String booleanToYesNo(boolean bool) {
        MessageLookup m = MessageLookup.getInstance();
        if (bool) {
            return m.lookup(YesNoFlag.YES);
        }

        return m.lookup(YesNoFlag.NO);
    }

	@Override
	public Map<String, String> getDisplayablePluginsProperties() {
		Map<String, String> result = new LinkedHashMap<String, String>();
		for (TransactionImport ti : new PluginManager().loadImportPlugins()) {
			String key = ti.getPropertyNameForAdminDisplay();
			String value = ti.getPropertyValueForAdminDisplay();
			if (key != null && value != null) {
                result.put(key, value);
            }
		}
		return result;
	}
}
