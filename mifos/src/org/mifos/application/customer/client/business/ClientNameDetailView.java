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
import org.mifos.config.ClientRules;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.components.configuration.util.helpers.ConfigConstants;

public class ClientNameDetailView {

	private Short nameType;

	/* 47=Mr, 48=Mrs, 228=Ms (is this right?)*/
	private Integer salutation;

	private StringBuilder displayName;

	private String firstName;

	private String middleName;

	private String lastName;

	private String secondLastName;

	public ClientNameDetailView() {
		super();
	}

	public ClientNameDetailView(NameType nameType, Integer salutation,
			String firstName, String middleName, String lastName,
			String secondLastName) {
		this(nameType.getValue(), salutation, 
			new StringBuilder(), firstName, middleName,
			lastName, secondLastName);
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
		String[] names = ClientRules.getNameSequence();
		addToName(displayName, names[0], false);
		for (int i = 1; i < names.length; i++) {
			addToName(displayName, names[i], true);
		}
		return displayName.toString().trim();
	}

	private void addToName(StringBuilder displayName, String nameToBeAppend,
			boolean isBlankRequired) {
		if (nameToBeAppend.equals(ConfigConstants.FIRST_NAME)) {
			appendToName(displayName, firstName, isBlankRequired);
		} else if (nameToBeAppend.equals(ConfigConstants.MIDDLE_NAME)) {
			appendToName(displayName, middleName, isBlankRequired);
		} else if (nameToBeAppend.equals(ConfigConstants.LAST_NAME)) {
			appendToName(displayName, lastName, isBlankRequired);
		} else if (nameToBeAppend.equals(ConfigConstants.SECOND_LAST_NAME)) {
			appendToName(displayName, secondLastName, isBlankRequired);
		}
	}

	private void appendToName(StringBuilder displayName,
			String valueToBeAppend, boolean isBlankRequired) {
		if (!ValidateMethods.isNullOrBlank(valueToBeAppend)) {
			if (isBlankRequired)
				displayName.append(CustomerConstants.BLANK);
			displayName.append(valueToBeAppend);
		}
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

	public Name asName() {
		return new Name(getFirstName(), getMiddleName(), getSecondLastName(),
				getLastName());
	}

}
