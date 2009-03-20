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
 
package org.mifos.framework.struts.actionforms;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts.action.ActionForm;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class GenericActionForm extends ActionForm {
	
	private Map<String, String> values = new HashMap<String, String>();
	
	public Map<String, String> getMap() {
		return values;
	}
	
	public void setValue(String key, String object) {
		values.put(key, object);
	}
	
	public void setValue(String key, int value) {
		values.put(key, Integer.toString(value));
	}
	
	public String getValue(String key) {
		return values.get(key);
	}
	
	public boolean containsKey(String key) {
		return values.containsKey(key);
	}
	
	public void setDateValue(String keyPrefix, Date date) {
		Calendar calendar = DateUtils.getCalendar(date);
		setValue(keyPrefix + "_DD", calendar.get(Calendar.DAY_OF_MONTH));
		setValue(keyPrefix + "_MM", calendar.get(Calendar.MONTH) + 1);
		setValue(keyPrefix + "_YY", calendar.get(Calendar.YEAR));
	}
	
	public String getDateValue(String keyPrefix) {
		String day = getValue(keyPrefix + "_DD");
		String month = getValue(keyPrefix + "_MM");
		String year = getValue(keyPrefix + "_YY");
		
		if (StringUtils.isNullOrEmpty(day) ||
				StringUtils.isNullOrEmpty(month) || StringUtils.isNullOrEmpty(year))
			return null;
		
		return day + "/" + month + "/" + year;
	}
	
	public Map<String, String> getAll(String prefix) {
		Map<String, String> result = new HashMap<String, String>();
		for (String key : values.keySet()) {
			if (key.startsWith(prefix))
				result.put(key.substring(prefix.length()), values.get(key));
		}
		return result;
	}
	
	public void clear() {
		values.clear();
	}

}
