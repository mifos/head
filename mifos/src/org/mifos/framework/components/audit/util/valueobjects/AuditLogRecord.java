/**

 * AuditLogRecord.java    version: xxx

 

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

import java.lang.String;
import java.util.Date;
import java.util.List;
import java.util.Iterator;

/**
 * Class which contains the details of the audited field.
 * @author rajitha
 */
public class AuditLogRecord {

	private Integer auditId;

	private Integer squenceNo;

	private String fieldName;

	private String oldValue;

	private String newValue;

	private AuditLog auditLog;

	public AuditLogRecord() {

	}

	public String getFieldName() {
		return fieldName;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public Integer getAuditId() {
		return auditId;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void setValue(String oldValue, String newValue) {
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public void setValue(long oldValue, long newValue) {
		this.oldValue = Long.toString(oldValue);
		this.newValue = Long.toString(newValue);
	}

	public void setValue(short oldValue, short newValue) {
		this.oldValue = Short.toString(oldValue);
		this.newValue = Short.toString(newValue);
	}

	public void setValue(Date oldValue, Date newValue) {
		this.oldValue = oldValue.toString();
		this.newValue = newValue.toString();
	}

	public void setValue(int oldValue, int newValue) {
		this.oldValue = Integer.toString(oldValue);
		this.newValue = Integer.toString(newValue);
	}

	public void setValue(float oldValue, float newValue) {
		this.oldValue = Float.toString(oldValue);
		this.newValue = Float.toString(newValue);
	}

	public void setValue(List oldValues, List newValues) {
		Iterator it = oldValues.iterator();
		while (it.hasNext()) {
			this.oldValue = it.next().toString();
			this.oldValue = this.oldValue + ",";
		}
		Iterator iter = newValues.iterator();
		while (iter.hasNext()) {
			this.newValue = iter.next().toString();
			this.newValue = this.newValue + ",";
		}
	}

	public void setAuditId(Integer auditId) {
		this.auditId = auditId;
	}

	public Integer getSquenceNo() {
		return squenceNo;
	}

	public void setSquenceNo(Integer squenceNo) {
		this.squenceNo = squenceNo;
	}

	public AuditLog getAuditLog() {
		return auditLog;
	}

	public void setAuditLog(AuditLog auditLog) {
		this.auditLog = auditLog;
	}

}
