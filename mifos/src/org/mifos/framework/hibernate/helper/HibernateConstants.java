/**

 * HibernateConstants.java    version: xxx



 * Copyright © 2005-2006 Grameen Foundation USA

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
package org.mifos.framework.hibernate.helper;

public interface HibernateConstants
{
	public static String CFGFILENOTFOUND = "errors.cfgfilenotfound";
	public static String HIBERNATEPROPNOTFOUND = "errors.hibernatepropnotfound";
	public static String CONFIGPATHNOTFOUND = "errors.configpathnotfound";
	public static String STARTUPEXCEPTION = "errors.startupexception";  
	public static String BUILDDTO = "errors.builddto";
	public static String FAILED_OPENINGSESSION = "errors.failedopeningsession";
	public static String FAILED_CLOSINGSESSION = "errors.failedclosingsession";
	public static String SEARCH_INPUTNULL= "errors.searchinputnull";
	public static String SEARCH_FAILED= "errors.searchfailed";
	
	public static String UPDATE_FAILED= "errors.updatefailed";
	public static String CREATE_FAILED= "errors.createfailed";
	public static String DELETE_FAILED= "errors.deletefailed";
	public static String VERSION_MISMATCH= "errors.versionmismatch";
}
