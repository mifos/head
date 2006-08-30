/**

 * OfficeHierarchy.java    version: 1.0

 

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
package org.mifos.application.office.business;

import java.util.Date;

import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.framework.business.BusinessObject;

/**
 * This class is value object for the officeHierarchy
 */
public class OfficeHierarchyEntity extends BusinessObject {

	private final Integer hierarchyId;

	private Date endDate;

	private OfficeBO office;

	private OfficeBO parentOffice;

	private Short status;

	public Date getEndDate() {
		return endDate;
	}
	public OfficeHierarchyEntity(OfficeBO office,OfficeBO parentOffice){
		super();
		this.hierarchyId = null;
		this.office=office;
		this.parentOffice=parentOffice;
		this.status=OfficeConstants.ACTIVE;
		this.createdDate=new Date(System.currentTimeMillis());
		
    }
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public OfficeBO getOffice() {
		return office;
	}

	public void setOffice(OfficeBO office) {
		this.office = office;
	}

	public OfficeBO getParentOffice() {
		return parentOffice;
	}

	public void setParentOffice(OfficeBO parentOffice) {
		this.parentOffice = parentOffice;
	}

	public Short getStatus() {
		return status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	protected  OfficeHierarchyEntity() {
		super();
		this.hierarchyId = null;
	}
	public Integer getHierarchyId() {
		return hierarchyId;
	}


}
