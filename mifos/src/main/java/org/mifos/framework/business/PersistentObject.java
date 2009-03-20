/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.framework.business;

import java.io.Serializable;
import java.util.Date;

/**
 * Superclass for many of our persistent objects.
 * Note that the responsibility for mapping the fields declared
 * here to the database (or not) still resides with each
 * persistent class.
 */
public abstract class PersistentObject implements Serializable {
	
	protected Date createdDate;
	protected Short createdBy;
	protected Date updatedDate;
	protected Short updatedBy;  
	protected Integer versionNo;
	
	public Short getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Short createdBy) {
		this.createdBy = createdBy;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Short getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(Short updatedBy) {
		this.updatedBy = updatedBy;
	}
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	public Integer getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}
}
