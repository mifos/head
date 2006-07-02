/**

* CustomerSearchInput.java   version: 1.0



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

package org.mifos.application.customer.util.valueobjects;

import java.io.Serializable;

/**
 * This class is helper class and provides input for center search
 * @author navitas
 *
 */
public class CustomerSearchInput implements Serializable {
	
	/** Denotes for which purpose customer is using the search*/
	private String customerInputPage;
	
	/** Denotes for officeId for which centers are to be searched*/
	private Short officeId;

	/**
	 * Method which returns the customerInputPage	
	 * @return Returns the customerInputPage.
	 */
	public String getCustomerInputPage() {
		return customerInputPage;
	}

	/**
	 * Method which sets the customerInputPage
	 * @param customerInputPage The customerInputPage to set.
	 */
	public void setCustomerInputPage(String customerInputPage) {
		this.customerInputPage = customerInputPage;
	}

	/**
	 * Method which returns the officeId	
	 * @return Returns the officeId.
	 */
	public Short getOfficeId() {
		return officeId;
	}

	/**
	 * Method which sets the officeId
	 * @param officeId The officeId to set.
	 */
	public void setOfficeId(Short officeId) {
		this.officeId = officeId;
	}

}
