/**

 * Office.java    version: 1.0



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
package org.mifos.application.office.business;

import java.util.Date;
import java.util.Set;

import org.mifos.application.office.util.helpers.OperationMode;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.plugin.helper.EntityMasterConstants;

/**
 * This is office business object encapsulate the office related functionality
 * 
 * @author rajenders
 * 
 */
public class OfficeBO extends BusinessObject {

	private Short officeId;

	private String globalOfficeNum;

	private OfficeLevelEntity level;

	private Integer maxChildCount;

	private String searchId;

	private Short operationMode;

	private OfficeBO parentOffice;

	private OfficeStatusEntity status;

	private Set<OfficeCustomFieldEntity> customFields;

	private String officeName;

	private String shortName;

	private OfficeAddressEntity address;

	public OfficeBO() {
		super(null);
		this.status = new OfficeStatusEntity();
		this.address = new OfficeAddressEntity();
	}

	public OfficeBO(UserContext userContext) {
		super(userContext);
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getGlobalOfficeNum() {
		return globalOfficeNum;
	}

	public void setGlobalOfficeNum(String globalOfficeNum) {
		this.globalOfficeNum = globalOfficeNum;
	}

	public OfficeLevelEntity getLevel() {
		return level;
	}

	public void setLevel(OfficeLevelEntity level) {
		this.level = level;
	}

	public Integer getMaxChildCount() {
		return maxChildCount;
	}

	public void setMaxChildCount(Integer maxChildCount) {
		this.maxChildCount = maxChildCount;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public Short getOfficeId() {
		return officeId;
	}

	public void setOfficeId(Short officeId) {
		this.officeId = officeId;
	}

	
	public Short getOperationMode() {
		return operationMode;
	}

	public void setOperationMode(Short operationMode) {
		this.operationMode = operationMode;
	}

	public OfficeBO getParentOffice() {
		return parentOffice;
	}

	public void setParentOffice(OfficeBO parentOffice) {
		this.parentOffice = parentOffice;
	}

	public String getSearchId() {
		return searchId;
	}

	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public OfficeStatusEntity getStatus() {
		return status;
	}

	public void setStatus(OfficeStatusEntity status) {
		this.status = status;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public OfficeAddressEntity getAddress() {
		return address;
	}

	public void setAddress(OfficeAddressEntity address) {
		if (address != null)
			address.setOffice(this);
		this.address = address;
	}

	public Set getCustomFields() {
		return customFields;
	}

	public void setCustomFields(Set<OfficeCustomFieldEntity> customFields) {
		this.customFields = customFields;
	}

	public void setCustomField(OfficeCustomFieldEntity officeCustomFieldView) {
		if (null != officeCustomFieldView) {
			officeCustomFieldView.setOffice(this);
		}
	}

	@Override
	public Short getEntityID() {
		return EntityMasterConstants.Office;
	}
}
