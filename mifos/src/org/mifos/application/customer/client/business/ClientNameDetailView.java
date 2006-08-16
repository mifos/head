/**

* ClientNameDetailView.java version: 1.0



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

package org.mifos.application.customer.client.business;

import org.mifos.application.customer.center.util.helpers.ValidateMethods;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.framework.business.util.Name;

public class ClientNameDetailView {

	public ClientNameDetailView() {
		super();
	}
	private Short nameType;

	private Integer salutation;

	private StringBuilder displayName;
	
	private Name name;
	
	public void setDisplayName(StringBuilder displayName) {
		this.displayName = displayName;
	}

	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public Short getNameType() {
		return nameType;
	}

	public void setNameType(Short nameType) {
		this.nameType = nameType;
	}

	public Integer getSalutation() {
		return salutation;
	}

	public void setSalutation(Integer salutation) {
		this.salutation = salutation;
	}

	public String getDisplayName() {
		displayName.append(name.getFirstName());
		if(!ValidateMethods.isNullOrBlank(name.getMiddleName())){
			displayName.append(CustomerConstants.BLANK);
			displayName.append(name.getMiddleName());
		}
		if(!ValidateMethods.isNullOrBlank(name.getSecondLastName())){
			displayName.append(CustomerConstants.BLANK);
			displayName.append(name.getSecondLastName());
		}
		if(!ValidateMethods.isNullOrBlank(name.getLastName())){
			displayName.append(CustomerConstants.BLANK);
			displayName.append(name.getLastName());
		}
		return displayName.toString().trim();
	}

	
	
	

}
