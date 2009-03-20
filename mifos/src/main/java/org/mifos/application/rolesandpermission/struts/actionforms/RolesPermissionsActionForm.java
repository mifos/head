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
 
package org.mifos.application.rolesandpermission.struts.actionforms;

import java.util.HashMap;
import java.util.Map;

import org.mifos.framework.struts.actionforms.BaseActionForm;

public class RolesPermissionsActionForm extends BaseActionForm {
	
	private String id;
	
	private String name;
	
	private Map<String, String> activities = new HashMap<String, String>();

	public Map<String, String> getActivities() {
		return activities;
	}

	public void setActivities(Map<String, String> activities) {
		this.activities = activities;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setActivity(String key, String value) {
		this.activities.put(key, value);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
