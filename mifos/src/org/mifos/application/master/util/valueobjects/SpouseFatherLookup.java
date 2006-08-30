/**

* SpouseFatherLookup.java   version: 1.0



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

package org.mifos.application.master.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

public class SpouseFatherLookup extends ValueObject {
	private java.lang.Integer spouseFatherId;

    private Integer lookUpId;

	/**
	 * Method which returns the lookUpId	
	 * @return Returns the lookUpId.
	 */
	public Integer getLookUpId() {
		return lookUpId;
	}


	/**
	 * Method which sets the lookUpId
	 * @param lookUpId The lookUpId to set.
	 */
	public void setLookUpId(Integer lookUpId) {
		this.lookUpId = lookUpId;
	}


	/**
	 * Method which returns the spouseFatherId	
	 * @return Returns the spouseFatherId.
	 */
	public java.lang.Integer getSpouseFatherId() {
		return spouseFatherId;
	}


	/**
	 * Method which sets the spouseFatherId
	 * @param spouseFatherId The spouseFatherId to set.
	 */
	public void setSpouseFatherId(java.lang.Integer spouseFatherId) {
		this.spouseFatherId = spouseFatherId;
	}


	
}
