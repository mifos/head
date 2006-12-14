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
}
