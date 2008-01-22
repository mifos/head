/**

 * FilePaths.java    version: 1.0

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */
package org.mifos.framework.util.helpers;

/**
 * This interface has all file paths defined as constants.
 * These paths are relative to the context of the application.
 * So any body who wants to access an file should get the relative path 
 * from this file.
 */
public interface FilePaths {
	public static final String INITIALIZATIONFILE = 
		"org/mifos/framework/util/resources/Initialization.xml"; 
	public static final String DEPENDENCYFILE = 
		"org/mifos/framework/util/resources/Dependency.xml";
	public static final String LOGFILE = 
		"org/mifos/framework/util/resources/loggerresources/loggerconfiguration.xml";
	public static final String MENUPATH =
		"org/mifos/framework/util/resources/menuresources/menu.xml";
	public static final String MENUSCHEMA =
		"org/mifos/framework/util/resources/menuresources/menu.xsd";
	public static final String JAXBPACKAGEPATH = 
		"org.mifos.framework.util.jaxb.";
	public static final String HIBERNATE_PROPERTIES = 
		"conf/hibernate.properties";
	public static final String HIBERNATECFGFILE = 
		"org/mifos/framework/util/resources/hibernate.cfg.xml";
	public static final String CUSTOMTABLETAGXSD = 
		"org/mifos/framework/util/resources/customTableTag/tabletag.xsd";	
	public static final String CONFIGURABLEMIFOSDBPROPERTIESFILE = 
		"deploymifosDB.properties";
	public static final String REPORT_PRODUCT_OFFERING_CONFIG = "/org/mifos/application/reports/resources/reportproducts.properties";
	public static final String REPORT_SERVICE_BEAN_FILE = "/org/mifos/config/reportServices.xml";
	public static final String LOCALIZED_RESOURCE_PATH="org/mifos/config/localizedResources/";
	public static final String COLUMN_MAPPING_BUNDLE_PROPERTYFILE=LOCALIZED_RESOURCE_PATH + "ColumnMappingBundle";
	public static String FIELD_CONF_PROPERTYFILE=LOCALIZED_RESOURCE_PATH + "FieldUIResources";
	// org.mifos.config.localizedResources.
	public static String LOAN_UI_RESOURCE_PROPERTYFILE=LOCALIZED_RESOURCE_PATH + "LoanUIResources";
	//org.mifos.application.accounts.savings.util.resources.SavingsUIResources
	public static String SAVING_UI_RESOURCE_PROPERTYFILE=LOCALIZED_RESOURCE_PATH + "SavingsUIResources";
	// "org.mifos.application.accounts.util.resources.accountsUIResources"
	public static String ACCOUNTS_UI_RESOURCE_PROPERTYFILE=LOCALIZED_RESOURCE_PATH + "accountsUIResources";
	//org.mifos.application.configuration.util.resources.ConfigurationUIResources
	public static String CONFIGURATION_UI_RESOURCE_PROPERTYFILE=LOCALIZED_RESOURCE_PATH + "ConfigurationUIResources";
	// "org/mifos/application/holiday/util/resources/HolidayUIResources"
	public static String HOLIDAYSOURCEPATH = LOCALIZED_RESOURCE_PATH + "HolidayUIResources";
	//"org.mifos.framework.components.tabletag.Resources"
	public static final String TABLE_TAG_PROPERTIESFILE = LOCALIZED_RESOURCE_PATH + "Resources";
	// "org.mifos.application.meeting.util.resources.Meeting"
	public final String MEETING_RESOURCE = LOCALIZED_RESOURCE_PATH + "Meeting";
	//"org/mifos/application/office/util/resources/OfficeUIResources"
	public static final String OFFICERESOURCEPATH = LOCALIZED_RESOURCE_PATH + "OfficeUIResources";
	//"org/mifos/application/personnel/util/resources/PersonnelUIResources"
	public static final String PERSONNELUIRESOURCESPATH = LOCALIZED_RESOURCE_PATH + "PersonnelUIResources";
	// "org.mifos.application.productdefinition.util.resources.ProductDefinitionResources"
	public static final String PRODUCT_DEFINITION_UI_RESOURCE_PROPERTYFILE = LOCALIZED_RESOURCE_PATH + "ProductDefinitionResources";
}
