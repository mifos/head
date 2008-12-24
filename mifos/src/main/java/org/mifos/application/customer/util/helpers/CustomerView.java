/**

 * CustomerMaster.java    version: xxx



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
package org.mifos.application.customer.util.helpers;

import org.mifos.framework.business.View;

public class CustomerView extends View {

	private Integer customerId;

	private String globalCustNum;

	private String displayName;

	private Short statusId;

	private Integer versionNo;

	private Short customerLevelId;

	private Short officeId;

	private Short personnelId;

	public CustomerView() {

	}

	public CustomerView(java.lang.Integer customerId,
			java.lang.String displayName, java.lang.String globalCustNum,
			java.lang.Short statusId) {
		this.customerId = customerId;
		this.displayName = displayName;
		this.globalCustNum = globalCustNum;
		this.statusId = statusId;
	}

	public CustomerView(java.lang.Integer customerId,
			java.lang.String displayName, java.lang.String globalCustNum,
			java.lang.Short statusId, Short customerLevelId, Integer versionNo,
			Short officeId, Short personnelId) {
		this.customerId = customerId;
		this.displayName = displayName;
		this.globalCustNum = globalCustNum;
		this.statusId = statusId;
		this.customerLevelId = customerLevelId;
		this.versionNo = versionNo;
		this.officeId = officeId;
		this.personnelId = personnelId;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getGlobalCustNum() {
		return globalCustNum;
	}

	public void setGlobalCustNum(String globalCustNum) {
		this.globalCustNum = globalCustNum;
	}

	public Short getStatusId() {
		return statusId;
	}

	public void setStatusId(Short statusId) {
		this.statusId = statusId;
	}

	public Short getCustomerLevelId() {
		return customerLevelId;
	}

	public void setCustomerLevelId(Short customerLevelId) {
		this.customerLevelId = customerLevelId;
	}

	public Integer getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}

	public Short getOfficeId() {
		return officeId;
	}

	public void setOfficeId(Short officeId) {
		this.officeId = officeId;
	}

	public Short getPersonnelId() {
		return personnelId;
	}

	public void setPersonnelId(Short personnelId) {
		this.personnelId = personnelId;
	}

}
