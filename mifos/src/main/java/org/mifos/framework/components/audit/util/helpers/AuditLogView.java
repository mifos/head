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
 
package org.mifos.framework.components.audit.util.helpers;

import java.util.Locale;

import org.mifos.framework.struts.tags.MifosTagUtils;
import org.mifos.framework.util.helpers.DateUtils;

public class AuditLogView {

	private String date;
	private String field;
	private String oldValue;
	private String newValue;
	private String user;
	private Locale mfiLocale;
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getMfiDate() {
		//return DateUtils.getUserLocaleDate(getMfiLocale(), getDate().toString());
		return DateUtils.getUserLocaleDate(getDate().toString());
	}

	public Locale getMfiLocale() {
		return mfiLocale;
	}
	public void setMfiLocale(Locale mfiLocale) {
		this.mfiLocale = mfiLocale;
	}
	public String getNewValue() {
		return MifosTagUtils.xmlEscape(newValue);
	}
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	public String getOldValue() {
		return MifosTagUtils.xmlEscape(oldValue);
	}
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}

	
}
