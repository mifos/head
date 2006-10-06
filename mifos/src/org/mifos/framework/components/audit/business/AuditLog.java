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

package org.mifos.framework.components.audit.business;

import java.sql.Date;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.components.audit.persistence.AuditPersistence;

public class AuditLog extends PersistentObject {

	private final Integer id;

	private final Integer entityId;
	
	private final Short entityType;

	private final String modifierName;

	private final Set<AuditLogRecord> auditLogRecords;
	

	protected AuditLog() {
		id = null;
		entityId = null;
		entityType=null;
		modifierName = null;
		auditLogRecords = null;
	}

	public AuditLog(Integer entityId,Short entityType,String modifierName,
			Date updatedDate, Short updatedBy) {
		this.id = null;
		this.entityId = entityId;
		this.entityType=entityType;
		this.modifierName = modifierName;
		this.updatedDate = updatedDate;
		this.updatedBy = updatedBy;
		this.auditLogRecords = new HashSet<AuditLogRecord>();
	}

	public Set<AuditLogRecord> getAuditLogRecords() {
		return auditLogRecords;
	}

	public Integer getEntityId() {
		return entityId;
	}

	public Integer getId() {
		return id;
	}

	public String getModifierName() {
		return modifierName;
	}
	
	public Short getEntityType() {
		return entityType;
	}

	public void addAuditLogRecords(Set<AuditLogRecord> auditLogRecords){
		this.auditLogRecords.addAll(auditLogRecords);	
	}
	
	public void save(){
		new AuditPersistence().save(this);
	}

}
