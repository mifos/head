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

package org.mifos.application.admin.business.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.mifos.accounts.api.TransactionImport;
import org.mifos.application.admin.servicefacade.ViewOrganizationSettingsServiceFacade;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.AccountingRules;
import org.mifos.config.ClientRules;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.config.LocaleSetting;
import org.mifos.config.ProcessFlowRules;
import org.mifos.config.business.MifosConfigurationManager;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.framework.image.service.ImageStorageManager;
import org.mifos.framework.plugin.PluginManager;
import org.mifos.framework.util.ConfigurationLocator;

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
        orgSettings.putAll(getConfigurationContent());
        orgSettings.putAll(getGeneralConfig());
        orgSettings.putAll(getMpesa());
        orgSettings.putAll(getRest());
        
        return orgSettings;
    }

    private Properties getFiscalRules() {
        Properties fiscalRules = new Properties();

        fiscalRules.setProperty("workingDays", getWorkingDays());

        FiscalCalendarRules fiscalCalendarRules = new FiscalCalendarRules();
        Short startOfWeekValue = fiscalCalendarRules.getStartOfWeek();
        WeekDay startOfWeek = WeekDay.getWeekDay(startOfWeekValue);
        String weekdayName = ApplicationContextProvider.getBean(MessageLookup.class).lookup(startOfWeek.getPropertiesKey());
        startOfWeek.setWeekdayName(weekdayName);

        fiscalRules.setProperty("startOfWeek", startOfWeek.getName());
        fiscalRules.setProperty("offDays", getOffDays());
        fiscalRules.setProperty("holidayMeeting", fiscalCalendarRules.getScheduleMeetingIfNonWorkingDay());

        return fiscalRules;
    }

    private Properties getLocaleInfo() {
        LocaleSetting configLocale = new LocaleSetting();
        Properties localeInfo = new Properties();

        localeInfo.setProperty("localeCountryCode", configLocale.getCountryCode());
        localeInfo.setProperty("localeLanguageCode", configLocale.getLanguageCode());
        localeInfo.setProperty("localeDirection", configLocale.getDirection());

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

        accountingRules.setProperty("minCashFlowThreshold", AccountingRules.getMinCashFlowThreshold().toString());
        accountingRules.setProperty("maxCashFlowThreshold", AccountingRules.getMaxCashFlowThreshold().toString());
        accountingRules.setProperty("minRepaymentCapacity", AccountingRules.getMinRepaymentCapacity().toString());
        accountingRules.setProperty("maxRepaymentCapacity", AccountingRules.getMaxRepaymentCapacity().toString());
        accountingRules.setProperty("minIndebtednessRatio", AccountingRules.getMinIndebtednessRatio().toString());
        accountingRules.setProperty("maxIndebtednessRatio", AccountingRules.getMaxIndebtednessRatio().toString());
        accountingRules.setProperty("digitsAfterDecimalForCashFlow", AccountingRules.getDigitsAfterDecimalForCashFlowValidations().toString());
        accountingRules.setProperty("GLNamesMode", String.valueOf(AccountingRules.getGlNamesMode()));
        accountingRules.setProperty("simpleAccountingModule", booleanToYesNo(AccountingRules.getSimpleAccountingStatus()));
        accountingRules.setProperty("overdueInterestPaidFirst", booleanToYesNo(AccountingRules.isOverdueInterestPaidFirst()));

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
        misc.setProperty("backDatedApprovals", booleanToYesNo(AccountingRules.isBackDatedApprovalAllowed()));
        ConfigurationBusinessService cbs = new ConfigurationBusinessService();
        misc.setProperty("glim", booleanToYesNo(cbs.isGlimEnabled()));
        misc.setProperty("lsim", booleanToYesNo(cbs.isRepaymentIndepOfMeetingEnabled()));
        
        return misc;
    }

    private String getWorkingDays() {
        List<WeekDay> workDaysList = new FiscalCalendarRules().getWorkingDays();
        List<String> workDayNames = new ArrayList<String>();
        for (WeekDay workDay : workDaysList) {
            String weekdayName = ApplicationContextProvider.getBean(MessageLookup.class).lookup(workDay.getPropertiesKey());
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
            String weekdayName = ApplicationContextProvider.getBean(MessageLookup.class).lookup(weekDay.getPropertiesKey());
            weekDay.setWeekdayName(weekdayName);
            offDayNames.add(weekDay.getName());
        }
        return StringUtils.join(offDayNames, DELIMITER);
    }

    private String booleanToYesNo(boolean bool) {
        MessageLookup m = ApplicationContextProvider.getBean(MessageLookup.class);
        if (bool) {
            return m.lookup(YesNoFlag.YES);
        }

        return m.lookup(YesNoFlag.NO);
    }

    @Override
    public Map<String, String> getDisplayablePluginsProperties() {
        Map<String, String> result = new LinkedHashMap<String, String>();
        for (TransactionImport ti : new PluginManager().loadImportPlugins()) {
            Map<String, String> properties = ti.getPropertiesForAdminDisplay();
            Iterator<String> iterator = properties.keySet().iterator();
            while(iterator.hasNext())
            {
                String key = String.valueOf(iterator.next());
                String value = String.valueOf(properties.get(key));
                if (key != null && value != null) {
                    result.put(key, value);
                }
            }
        }
        return result;
    }
    
    private Properties getConfigurationContent() {
    	Properties configContent = new Properties();
    	
    	MifosConfigurationManager configuration = MifosConfigurationManager.getInstance();
    	String branchMangerRoleName = configuration.getString("RolesAndPermissions.BranchManager.RoleName");
    	
    	configContent.put("branchManagerRoleName", branchMangerRoleName);
    	
    	return configContent;
    }
    
    private Properties getMpesa() {
    	Properties configContent = new Properties();
    	
    	MifosConfigurationManager configuration = MifosConfigurationManager.getInstance();
    	String disbursalMax = configuration.getString("MPESA.DisbursalMax");
    	
    	configContent.put("disbursalMax", disbursalMax);
    	
    	return configContent;
    }
    
    private Properties getRest() {
    	Properties configContent = new Properties();
    	
    	MifosConfigurationManager configuration = MifosConfigurationManager.getInstance();
    	String approvalRequired = configuration.getString("REST.approvalRequired");
    	
    	configContent.put("approvalRequired", approvalRequired);
    	
    	return configContent;
    }
    

    public String getUploadStorageDirectory() {
        String uploadsDir = MifosConfigurationManager.getInstance().getString("GeneralConfig.UploadStorageDirectory",
                "$HOME/.mifos/uploads");
        if (File.separatorChar == '\\') { // windows platform
            uploadsDir = uploadsDir.replaceAll("/", "\\\\");
        }
        int id = uploadsDir.indexOf("$HOME");
        if (id != -1) {
            uploadsDir = uploadsDir.substring(0, id) + System.getProperty("user.home") + uploadsDir.substring(id + 5);
        }
        return uploadsDir;
    }
    
    public String getAdminDocumentStorageDirectory() {
    	
        return getUploadStorageDirectory().endsWith(File.separator) ? getUploadStorageDirectory() + "adminReport"
                : getUploadStorageDirectory() + File.separator + "adminReport";
    }  
    
    public String getUploadQGDirectory() {
    	
        String uploadsQGDir = MifosConfigurationManager.getInstance().getString("GeneralConfig.UploadQGDirectory",
                "file:$MIFOS_CONF/uploads/questionGroups");        
        return uploadsQGDir;
    }
       
    private Properties getGeneralConfig() {
    	
    	Properties configContent = new Properties();
    	
    	MifosConfigurationManager configuration = MifosConfigurationManager.getInstance();
    	String maxPointsPerPPISurvey = configuration.getString("GeneralConfig.MaxPointsPerPPISurvey"); 
    	String batchSizeForBatchJobs = configuration.getString("GeneralConfig.BatchSizeForBatchJobs"); 
    	String recordCommittingSizeForBatchJobs = configuration.getString("GeneralConfig.RecordCommittingSizeForBatchJobs"); 
    	String outputIntervalForBatchJobs = configuration.getString("GeneralConfig.OutputIntervalForBatchJobs"); 
    	String allowDataPrefetchingWhenSavingCollectionSheets = configuration.getString("GeneralConfig.allowDataPrefetchingWhenSavingCollectionSheets"); 
       	String shutdownCountdownNotificationThreshold = configuration.getString("GeneralConfig.ShutdownCountdownNotificationThreshold");
    	String imageStorageType = configuration.getString("GeneralConfig.ImageStorageType"); 
    	String uploadStorageDirectory = getUploadStorageDirectory();  	
    	String uploadQGDirectory = new ConfigurationLocator().resolvePath(getUploadQGDirectory());
    	String imageStorageDirectory = ImageStorageManager.getStorageLocation();

    	
    	configContent.put("maxPointsPerPPISurvey", maxPointsPerPPISurvey);
    	configContent.put("batchSizeForBatchJobs", batchSizeForBatchJobs);
    	configContent.put("recordCommittingSizeForBatchJobs", recordCommittingSizeForBatchJobs);
    	configContent.put("outputIntervalForBatchJobs", outputIntervalForBatchJobs);
    	configContent.put("allowDataPrefetchingWhenSavingCollectionSheets", allowDataPrefetchingWhenSavingCollectionSheets);
    	configContent.put("shutdownCountdownNotificationThreshold", shutdownCountdownNotificationThreshold);
    	configContent.put("imageStorageType", imageStorageType);
    	configContent.put("uploadStorageDirectory", uploadStorageDirectory);
    	configContent.put("uploadQGDirectory", uploadQGDirectory);
    	configContent.put("imageStorageDirectory", imageStorageDirectory);
    	
    	return configContent;
    }
}
