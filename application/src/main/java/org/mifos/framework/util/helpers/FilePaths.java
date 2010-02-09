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

package org.mifos.framework.util.helpers;

/**
 * This interface has all file paths defined as constants. These paths are
 * relative to the context of the application. So any body who wants to access
 * an file should get the relative path from this file.
 */
public interface FilePaths {
    public static final String INITIALIZATIONFILE = "org/mifos/framework/util/resources/Initialization.xml";
    public static final String DEPENDENCYFILE = "org/mifos/framework/util/resources/Dependency.xml";
    public static final String LOG_CONFIGURATION_FILE = "loggerconfiguration.xml";
    public static final String MENUPATH = "org/mifos/framework/util/resources/menuresources/menu.xml";
    public static final String MENUSCHEMA = "org/mifos/framework/util/resources/menuresources/menu.xsd";
    public static final String JAXBPACKAGEPATH = "org.mifos.framework.util.jaxb.";
    /**
     * Default database settings for production/deployment/runtime.
     */
    public static final String DATABASE_CONFIGURATION = "mainDatabase.properties";

    /**
     * Default database settings for integration tests.
     */
    public static final String INTEGRATION_DATABASE_CONFIGURATION = "integrationDatabase.properties";

    /**
     * Default database settings for acceptance tests.
     */
    public static final String ACCEPTANCE_DATABASE_CONFIGURATION = "acceptanceDatabase.properties";

    /**
     * Contains overrides for database (and other) properties.
     */
    public static final String LOCAL_CONFIGURATION_OVERRIDES = "local.properties";

    public static final String HIBERNATECFGFILE = "org/mifos/framework/util/resources/hibernate.cfg.xml";
    public static final String CUSTOMTABLETAGXSD = "org/mifos/framework/util/resources/customTableTag/tabletag.xsd";

    /**
     * Main Spring configuration file. This is a relative path, meant to be
     * resolved by something that walks the classpath.
     */
    public static final String SPRING_CONFIG_CORE = "org/mifos/config/resources/applicationContext.xml";
    public static final String REPORT_PRODUCT_OFFERING_CONFIG = "/org/mifos/application/reports/resources/CollectionSheetReport.properties";
    public static final String BRANCH_REPORT_CONFIG = "/org/mifos/application/reports/resources/ProgressReport.properties";
    public static final String HO_CASH_CONFIRMATION_REPORT_CONFIG = "/org/mifos/application/reports/resources/HOCashConfirmationReport.properties";
    public static final String REPORT_PARAMETER_VALIDATOR_CONFIG = "/org/mifos/config/reportParameterValidators.xml";
    /**
     * A Mifos specialist or systems administrator can use this file to override
     * bean definitions in {@link #SPRING_CONFIG_CORE}. This is a relative path,
     * meant to be resolved by something that walks the classpath.
     */
    public static final String SPRING_CONFIG_CUSTOM_BEANS = "mifosBeanConfig.custom.xml";
    /**
     * Default chart of accounts. Will be bundled inside WAR.
     */
    public static final String CHART_OF_ACCOUNTS_DEFAULT = "org/mifos/config/resources/mifosChartOfAccounts.xml";
    /**
     * Custom chart of accounts configuration file. If a chart of accounts other
     * than the default is desired, a file with this name must be placed in the
     * application server's classpath before the first deployment of a
     * particular Mifos instance.
     */
    public static final String CHART_OF_ACCOUNTS_CUSTOM = "mifosChartOfAccounts.custom.xml";
    /**
     * Validates {@link #CHART_OF_ACCOUNTS_DEFAULT} (or
     * {@link #CHART_OF_ACCOUNTS_CUSTOM}, if one exists). There may be problems
     * using this XML schema to validate the chart of accounts with Java 6,
     * perhaps with respect to the unit tests.
     */
    public static final String CHART_OF_ACCOUNTS_SCHEMA = "org/mifos/config/resources/mifosChartOfAccounts.xsd";
    public static final String REPORT_SERVICE_BEAN_FILE = "/org/mifos/config/reportServices.xml";
    public static final String LOCALIZED_RESOURCE_PATH = "org/mifos/config/localizedResources/";
    public static final String COLUMN_MAPPING_BUNDLE_PROPERTYFILE = LOCALIZED_RESOURCE_PATH + "ColumnMappingBundle";
    public static String FIELD_CONF_PROPERTYFILE = LOCALIZED_RESOURCE_PATH + "FieldUIResources";
    public static String FEE_UI_RESOURCE_PROPERTYFILE = LOCALIZED_RESOURCE_PATH + "FeesUIResources";
    // org.mifos.config.localizedResources.
    public static String LOAN_UI_RESOURCE_PROPERTYFILE = LOCALIZED_RESOURCE_PATH + "LoanUIResources";
    // org.mifos.accounts.savings.util.resources.SavingsUIResources
    public static String SAVING_UI_RESOURCE_PROPERTYFILE = LOCALIZED_RESOURCE_PATH + "SavingsUIResources";
    // org.mifos.accounts.savings.util.resources.CustomerUIResources
    public static String CUSTOMER_UI_RESOURCE_PROPERTYFILE = LOCALIZED_RESOURCE_PATH + "CustomerUIResources";
    // "org.mifos.accounts.util.resources.accountsUIResources"
    public static String ACCOUNTS_UI_RESOURCE_PROPERTYFILE = LOCALIZED_RESOURCE_PATH + "accountsUIResources";
    // org.mifos.application.configuration.util.resources.ConfigurationUIResources
    public static String CONFIGURATION_UI_RESOURCE_PROPERTYFILE = LOCALIZED_RESOURCE_PATH + "ConfigurationUIResources";
    // "org/mifos/application/holiday/util/resources/HolidayUIResources"
    public static String HOLIDAYSOURCEPATH = LOCALIZED_RESOURCE_PATH + "HolidayUIResources";
    // "org.mifos.framework.components.tabletag.Resources"
    public static final String TABLE_TAG_PROPERTIESFILE = LOCALIZED_RESOURCE_PATH + "Resources";
    // "org.mifos.application.meeting.util.resources.Meeting"
    public final String MEETING_RESOURCE = LOCALIZED_RESOURCE_PATH + "Meeting";
    // "org/mifos/application/office/util/resources/OfficeUIResources"
    public static final String OFFICERESOURCEPATH = LOCALIZED_RESOURCE_PATH + "OfficeUIResources";
    // "org/mifos/application/personnel/util/resources/PersonnelUIResources"
    public static final String PERSONNELUIRESOURCESPATH = LOCALIZED_RESOURCE_PATH + "PersonnelUIResources";
    // "org/mifos/application/personnel/util/resources/PersonnelUIResources"
    public static final String FUND_UI_RESOURCE_PROPERTYFILE = LOCALIZED_RESOURCE_PATH + "FundUIResources";
    // "org.mifos.application.productdefinition.util.resources.ProductDefinitionResources"
    public static final String PRODUCT_DEFINITION_UI_RESOURCE_PROPERTYFILE = LOCALIZED_RESOURCE_PATH
            + "ProductDefinitionResources";
    public static final String UI_RESOURCE_PROPERTYFILE = LOCALIZED_RESOURCE_PATH + "UIResources";
    public static final String SVN_REVISION_PROPERTYFILE = "org/mifos/config/resources/versionInfo.properties";
    public final String BULKENTRY_RESOURCE = LOCALIZED_RESOURCE_PATH + "BulkEntryUIResources";
    public final String CHECKLIST_RESOURCE = LOCALIZED_RESOURCE_PATH + "CheckListUIResources";

    public final String LOGIN_UI_PROPERTY_FILE = LOCALIZED_RESOURCE_PATH + "LoginUIResources";

}
