/**

* IdGenerator    version: 1.0



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
package org.mifos.application.customer.center.util.helpers;


/**
 * This class is used to generate the global customer number or the system id for the customer.The global customer 
 * number for a customer is a combination of the globalcustomer number of the office to which the customer belongs,
 * its level and the max count of the number of customers under that office
 * 
 * @author sumeethaec
 *
 */
public class IdGenerator {
	
	/**
	 * This method is used to generate the global customer number for a particular customer. 
	 * A global customer number is generated from the office global customer number, customer level id and the current 
	 * customer count for that office. EG: if the office global customer number is BRANCH01 and the customer level 
	 * is 3 and max count of customers in that branch is 14 then the new id generated will be BRANCH01-3-000015
	 *  
	 * @param officeGlobalNum Global customer number for the office of the customer
	 * @param customerLevelId Level of the customer
	 * @param maxCustomerCount Number of customers in that branch currently
	 * @return The global customer number that is generated
	 */
	public static String generateId(String officeGlobalNum , String customerLevelId , String maxCustomerCount){
		
		String customerId=CenterConstants.BLANK ;
		int numberOfZeros = 6 - maxCustomerCount.length();
		//Depending on the number of digits in the customer count, zeros are appended before.
		for(int i=0;i<numberOfZeros;i++)
		{
			customerId = customerId + CenterConstants.ZERO;
		}
		customerId =customerId+maxCustomerCount;
		//The global customer number is generated from all three passed parameters including hyphens in between
		String customerGlobalNum =officeGlobalNum + CenterConstants.HYPHEN + customerLevelId + CenterConstants.HYPHEN + customerId;
		return customerGlobalNum ; 
	}
	
	
}
