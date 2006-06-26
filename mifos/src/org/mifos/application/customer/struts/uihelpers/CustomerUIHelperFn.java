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

package org.mifos.application.customer.struts.uihelpers;

import java.util.Iterator;
import java.util.List;

import org.mifos.application.customer.util.valueobjects.CustomerMaster;
import org.mifos.application.customer.util.valueobjects.CustomerPositionDisplay;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;

/**
 * This class has got helper functions which could be called from jsp as part of jsp2.0 specifications.
 * @author navitas
 *
 */
public class CustomerUIHelperFn {

	/**
	 * Simple Constructor
	 */
	public CustomerUIHelperFn() {
		super();
		
	}
	
	/**
	 * It returns a comma seperated string of titles a client is assigned to in the group.  
	 * @param object list of CustomerPositionDisplay
	 * @param object instance of customermaster
	 * @return String
	 */
	public static String getClientPositions(Object customerPositions,Object customerMaster) {
		MifosLogManager.getLogger(LoggerConstants.GROUP_LOGGER).debug("Inside UI helper function getClientPositions");
		StringBuilder stringBuilder=new StringBuilder();
		if(customerPositions !=null && customerMaster!=null) {
			MifosLogManager.getLogger(LoggerConstants.GROUP_LOGGER).debug("Iterating over customerPositions list");
			List customerPositionList=(List)customerPositions;
			String positionNames[]=new String[customerPositionList.size()];
			int i=0;
			for(Iterator<CustomerPositionDisplay> iter=customerPositionList.iterator();iter.hasNext();) {
				CustomerPositionDisplay custpos = iter.next();
				CustomerMaster custMast=(CustomerMaster)customerMaster;
				if(null != custpos && custpos.getCustomerId()!=null && custMast.getCustomerId()!=null){
					if(custpos.getCustomerId().intValue()==(custMast).getCustomerId().intValue()){
						String posName = custpos.getPositionName();
						MifosLogManager.getLogger(LoggerConstants.GROUP_LOGGER).debug("The position name is " + posName);
						positionNames[i]=posName;
						i++;
					}
				}
			}
			for(;i<positionNames.length;i++){
				positionNames[i]=null;
			}
			stringBuilder.append("(");
			for(int j=0;j<positionNames.length;j++){
				if(positionNames[j]!=null && positionNames[j]!="")
					stringBuilder.append(positionNames[j]);
				if(j+1<positionNames.length && positionNames[j+1]!=null && positionNames[j+1]!="")
					stringBuilder.append(",");
			}
			stringBuilder.append(")");
		}
		if(stringBuilder.toString().equals("()"))
			return "";
		else
			return stringBuilder.toString();
	}
}
