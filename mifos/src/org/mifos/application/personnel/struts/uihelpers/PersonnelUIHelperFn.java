/**

 * PersonnelUIHelperFn.java    version: xxx

 

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

package org.mifos.application.personnel.struts.uihelpers;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mifos.application.personnel.util.valueobjects.PersonnelRole;
import org.mifos.application.rolesandpermission.util.valueobjects.Role;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;

/**
 * This class has got helper functions which could be called from jsp as part of jsp2.0 specifications.
 * @author navitas
 *
 */
public class PersonnelUIHelperFn {

	/**
	 * Simple Constructor
	 */
	public PersonnelUIHelperFn() {
		super();
		
	}
	
	/**
	 * It returns a comma seperated string of role names from the list of roles received as parameter.  
	 * @param object
	 * @return String
	 */
	public static String getRoleNamesFromSet(Object object) {
		MifosLogManager.getLogger(LoggerConstants.PERSONNEL_LOGGER).debug("Inside UI helper function getCommaSeparatedRoles");
		StringBuilder stringBuilder=new StringBuilder();
		if(object !=null) {
			MifosLogManager.getLogger(LoggerConstants.PERSONNEL_LOGGER).debug("Iterating over role list");
			Set roleList=(Set)object;
			for(Iterator<PersonnelRole> iter=roleList.iterator();iter.hasNext();) {
				Role role = iter.next().getRole();
				if(null != role){
					String roleName = role.getName();
					MifosLogManager.getLogger(LoggerConstants.PERSONNEL_LOGGER).debug("The role name is " + roleName);
					stringBuilder.append(roleName);
					stringBuilder.append(iter.hasNext()?", ":"");
				}
			}
		}
		return stringBuilder.toString();
	}
	
	/**
	 * It returns a comma seperated string of role names from the list of roles received as parameter.  
	 * @param object
	 * @return String
	 */
	public static String getRoleNamesFromList(Object object) {
		MifosLogManager.getLogger(LoggerConstants.PERSONNEL_LOGGER).debug("Inside UI helper function getCommaSeparatedRoles");
		StringBuilder stringBuilder=new StringBuilder();
		if(object !=null) {
			MifosLogManager.getLogger(LoggerConstants.PERSONNEL_LOGGER).debug("Iterating over role list");
			List roleList=(List)object;
			for(Iterator<Role> iter=roleList.iterator();iter.hasNext();) {
				Role role = iter.next();
				if(null != role){
					String roleName = role.getName();
					MifosLogManager.getLogger(LoggerConstants.PERSONNEL_LOGGER).debug("The role name is " + roleName);
					stringBuilder.append(roleName);
					stringBuilder.append(iter.hasNext()?", ":"");
				}
			}
		}
		return stringBuilder.toString();
	}
}
