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
 
package org.mifos.application.rolesandpermission.business;

import java.util.HashSet;
import java.util.Set;

import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.framework.business.PersistentObject;

public class ActivityEntity extends PersistentObject {

	private final Short id;

	private ActivityEntity parent;

	private final LookUpValueEntity activityNameLookupValues;

	private final LookUpValueEntity descriptionLookupValues;
	private final Set<RoleActivityEntity> roles = 
		new HashSet<RoleActivityEntity>();

	protected ActivityEntity() {
		this.id = null;
		this.parent = null;
		this.activityNameLookupValues = null;
		this.descriptionLookupValues = null;
	}
	
	ActivityEntity(int id) {
		this.id = (short)id;
		this.parent = null;
		this.activityNameLookupValues = null;
		this.descriptionLookupValues = null;
	}
	
	public ActivityEntity(short id, ActivityEntity parentActivityEntity, LookUpValueEntity lookUpValueEntity) {
		this.id = id;
		this.parent = parentActivityEntity;
		this.activityNameLookupValues = lookUpValueEntity;
		this.descriptionLookupValues = this.activityNameLookupValues;
	}

	public Short getId() {
		return id;
	}

	public ActivityEntity getParent() {
		return parent;
	}

	public LookUpValueEntity getActivityNameLookupValues() {
		return activityNameLookupValues;
	}

	public LookUpValueEntity getDescriptionLookupValues() {
		return descriptionLookupValues;
	}

	public String getDescription() {
		return MessageLookup.getInstance().lookup(getActivityNameLookupValues());
	}

	public String getActivityName() {
		return MessageLookup.getInstance().lookup(getActivityNameLookupValues());
	}

	public void setParent(ActivityEntity parent) {
		this.parent = parent;
	}

	public Set<RoleActivityEntity> getRoles() {
		return roles;
	}

}
