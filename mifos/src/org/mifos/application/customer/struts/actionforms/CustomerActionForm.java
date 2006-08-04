/**

* CustomerActionForm    version: 1.0



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
package org.mifos.application.customer.struts.actionforms;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.fees.business.FeeView;
import org.mifos.framework.business.util.Address;

public class CustomerActionForm {

	private String customerId;

	private String displayName;

	private Address address;

	private String officeId;

	private String officeName;

	private String loanOfficerId;

	private String externalId;

	private String status;

	private List<FeeView> defaultFees;

	private List<FeeView> additionalFees;

	private List<CustomFieldView> customFields;
	
	public CustomerActionForm() {
		address = new Address();
		defaultFees = new ArrayList<FeeView>();
		additionalFees = new ArrayList<FeeView>();
		customFields = new ArrayList<CustomFieldView>();
	}

	public List<FeeView> getAdditionalFees() {
		return additionalFees;
	}

	public void setAdditionalFees(List<FeeView> additionalFees) {
		this.additionalFees = additionalFees;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public List<CustomFieldView> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(List<CustomFieldView> customFields) {
		this.customFields = customFields;
	}

	public List<FeeView> getDefaultFees() {
		return defaultFees;
	}

	public void setDefaultFees(List<FeeView> defaultFees) {
		this.defaultFees = defaultFees;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getLoanOfficerId() {
		return loanOfficerId;
	}

	public void setLoanOfficerId(String loanOfficerId) {
		this.loanOfficerId = loanOfficerId;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
