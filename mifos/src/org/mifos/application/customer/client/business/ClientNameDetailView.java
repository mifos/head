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

public class ClientNameDetailView {

	private Short nameType;

	private Integer salutation;

	private StringBuilder displayName;

	private String firstName;

	private String middleName;

	private String lastName;

	private String secondLastName;

	public ClientNameDetailView() {
		super();
	}

	public ClientNameDetailView(Short nameType, Integer salutation,
			StringBuilder displayName, String firstName, String middleName,
			String lastName, String secondLastName) {
		this.nameType = nameType;
		this.salutation = salutation;
		this.displayName = displayName;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.secondLastName = secondLastName;
	}

	public void setDisplayName(StringBuilder displayName) {
		this.displayName = displayName;
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
		displayName = new StringBuilder();
		displayName.append(firstName);
		if (!ValidateMethods.isNullOrBlank(middleName)) {
			displayName.append(CustomerConstants.BLANK);
			displayName.append(middleName);
		}
		if (!ValidateMethods.isNullOrBlank(secondLastName)) {
			displayName.append(CustomerConstants.BLANK);
			displayName.append(secondLastName);
		}
		if (!ValidateMethods.isNullOrBlank(lastName)) {
			displayName.append(CustomerConstants.BLANK);
			displayName.append(lastName);
		}
		return displayName.toString().trim();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getSecondLastName() {
		return secondLastName;
	}

	public void setSecondLastName(String secondLastName) {
		this.secondLastName = secondLastName;
	}

}
