/**
 
 * IDGenerator.java    version: 1.0
 
 
 
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

package org.mifos.application.personnel.util.helpers;

/**
 * This class is used for ID generation based on the predefined algorithm 
 * for generating ids for the personnel.
 */
public class IDGenerator {
	
	/**
	 * This method is used to generate the global personnel number for a new personnel. 
	 * A global personnel number is generated from the global office number, and max personnel Id in that office.
	 * EG: if the global office number is BRANCH01 and max personnelId of personnels in that branch is 14 then the 
	 * new id generated will be BRANCH01-00015
	 * @param officeGlobalNum Global office number for the office of the personnel
	 * @param maxPersonnelId is the maximum number of id that has been assigned to personnel till now
	 * @return The global personnel number that is generated
	 */
	public static String generateIdForUser(String officeGlobalNum ,  int maxPersonnelId) {
		String userId="";
		int numberOfZeros = 5 - String.valueOf(maxPersonnelId).length();
		for(int i=0;i<numberOfZeros;i++)
		{
			userId = userId + "0";
		}
		userId = userId + maxPersonnelId;
		String userGlobalNum =officeGlobalNum + "-" +  userId;
		return userGlobalNum ; 
	}
	
}
