/**

 * AuditLog.java    version: xxx

 

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

package org.mifos.framework.components.audit.util.valueobjects;

import java.sql.Date;
import java.util.Collection;
import java.util.Calendar;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * Class that contains the changed module details and a collection of changed
 * fields
 */

public class AuditLog extends ValueObject {

	private Integer logId;

	private Integer featureId;

	private Short featureName;

	private Short userName;
	
	private String actualName;

	private Date updatedDate;

	private Collection<AuditLogRecord> auditLogRecords;

	public AuditLog() {

	}

	public AuditLog(Integer featureId, Short featureName, Short userName,
			Date updatedDate, Collection<AuditLogRecord> auditLogRecords) {
		this.featureId = featureId;
		this.featureName = featureName;
		this.userName = userName;
		this.updatedDate = updatedDate;
		this.auditLogRecords = auditLogRecords;
	}

	public AuditLog(Integer featureId, Short featureName, Short userName,
			Collection<AuditLogRecord> auditLogRecords) {
		this.featureId = featureId;
		this.featureName = featureName;
		this.userName = userName;
		this.auditLogRecords = auditLogRecords;
	}

	public Integer getFeatureId() {
		return featureId;
	}

	public Short getFeatureName() {
		return featureName;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public Short getUserName() {
		return userName;
	}

	public Collection<AuditLogRecord> getAuditLogRecords() {
		return auditLogRecords;
	}

	public void setFeatureId(Integer featureId) {
		this.featureId = featureId;
	}

	public void setFeatureName(Short featureName) {
		this.featureName = featureName;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public void setUserName(Short userName) {
		this.userName = userName;
	}

	public void setAuditLogRecords(Collection<AuditLogRecord> auditLogRecords) {
		this.auditLogRecords = auditLogRecords;
	}

	public Integer getLogId() {
		return logId;
	}

	public void setLogId(Integer logId) {
		this.logId = logId;
	}

	public String getActualName() {
		return actualName;
	}

	public void setActualName(String actualName) {
		this.actualName = actualName;
	}

}
