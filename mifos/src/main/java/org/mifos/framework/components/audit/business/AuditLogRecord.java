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
 
package org.mifos.framework.components.audit.business;

import org.mifos.framework.business.PersistentObject;

public class AuditLogRecord extends PersistentObject {

	private final Integer squenceNo;

	private final String fieldName;

	private final String oldValue;

	private final String newValue;

	@SuppressWarnings("unused") // see .hbm.xml file
	private final AuditLog auditLog;

	protected AuditLogRecord() {
		squenceNo = null;
		fieldName = null;
		oldValue = null;
		newValue = null;
		auditLog = null;
	}

	public AuditLogRecord(String fieldName, String oldValue, String newValue,
			AuditLog auditLog) {
		this.squenceNo = null;
		this.fieldName = fieldName;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.auditLog = auditLog;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getNewValue() {
		return newValue;
	}

	public String getOldValue() {
		return oldValue;
	}

	public Integer getSquenceNo() {
		return squenceNo;
	}

}
