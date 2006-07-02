/**

 * Activity.java    version: 1.0

 

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

/**
 * 
 */
package org.mifos.application.rolesandpermission.util.valueobjects;

import java.util.Set;

import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.util.valueobjects.LookUpValue;
import org.mifos.application.master.util.valueobjects.LookUpValueLocale;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class maps the activity table in the system 
 * @author rajenders
 */
public class Activity extends ValueObject {

	private static final long serialVersionUID = 93123214235l;

	/**
	 * This would hold the activity id 
	 */
	private Short id;

	/**
	 * This would hold the name of the activity
	 */
	private String name;

	/**
	 * This would hold the description of the activity
	 */
	private String Description;

	/**
	 * This would hole the parent of the activity
	 */
	private Short parent;

	private LookUpValue activityNameLookupValues;

	private LookUpValue descriptionLookupValues;

	/**
	 * Default constructor for the activity
	 */
	public Activity() {
	}

	/**
	 * This Function returns the description
	 * @return Returns the description.
	 */
	public String getDescription() {
		return Description;
	}

	/**
	 * This function set the description
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		Description = description;
	}

	/**
	 * This Function returns the id
	 * @return Returns the id.
	 */
	public Short getId() {
		return id;
	}

	/**
	 * This function set the id
	 * @param id The id to set.
	 */
	public void setId(Short id) {
		this.id = id;
	}

	/**
	 * This Function returns the name
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * This function set the name
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This Function returns the parent
	 * @return Returns the parent.
	 */
	public Short getParent() {
		return parent;
	}

	/**
	 * This function set the parent
	 * @param parent The parent to set.
	 */
	public void setParent(Short parent) {
		this.parent = parent;
	}

	public LookUpValue getActivityNameLookupValues() {
		return activityNameLookupValues;
	}

	public void setActivityNameLookupValues(LookUpValue activityNameLookupValues) {
		this.activityNameLookupValues = activityNameLookupValues;
	}

	public LookUpValue getDescriptionLookupValues() {
		return descriptionLookupValues;
	}

	public void setDescriptionLookupValues(LookUpValue descriptionLookupValues) {
		this.descriptionLookupValues = descriptionLookupValues;
	}

	public String getName(Short localeId) {
		return getLookupValue(localeId, getActivityNameLookupValues());
	}

	public String getDescription(Short localeId) {
		return getLookupValue(localeId, getDescriptionLookupValues());
	}

	private String getLookupValue(Short localeId, LookUpValue lookupValues) {
		if (localeId == null)
			return null;
		String name = null;
		Set<LookUpValueLocale> lookupSet = lookupValues
				.getLookUpValueLocaleSet();
		for (LookUpValueLocale entity : lookupSet) {
			if (entity.getLocaleId().equals(localeId.shortValue())) {
				name = entity.getLookUpValue();
			}
		}
		return name;
	}
}
