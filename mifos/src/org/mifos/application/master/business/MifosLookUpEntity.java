/**

 * MifosLookUpEntity.java    version: 1.0



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

package org.mifos.application.master.business;

import java.util.Set;

import org.mifos.framework.business.PersistentObject;

/**
 * {@link MifosLookUpEntity} and {@link LookUpEntity} were redundant
 * classes. {@link LookUpEntity} usage has now been replaced by this class.
 * 
 * The entityType field should be a CamelCase name containing no whitespace
 * (since it is used as part of a properties file key value)  
 * The no whitespace requirement is enforced by the unit test 
 * TestApplicationConfigurationPersistence.testGetLookupEntities()
 */

public class MifosLookUpEntity extends PersistentObject {

	public static final Short DEFAULT_LOCALE_ID = 1;
	
	public static final int ETHNICITY = 19;
	public static final int ACCOUNT_ACTION = 69;
	public static final int ACCOUNT_STATE_FLAG = 70;
	public static final int ACTIVITY = 87;
	public static final int REPAYMENT_RULE = 91;
	
	private Short entityId;

	private String entityType;

	private Set<LookUpLabelEntity> lookUpLabels;

	private Set<LookUpValueEntity> lookUpValues;

	public MifosLookUpEntity() {
		super();
	}

	public Short getEntityId() {
		return entityId;
	}

	public void setEntityId(Short entityId) {
		this.entityId = entityId;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public Set<LookUpLabelEntity> getLookUpLabels() {
		return lookUpLabels;
	}

	public void setLookUpLabels(Set<LookUpLabelEntity> lookUpLabels) {
		this.lookUpLabels = lookUpLabels;
	}

	public Set<LookUpValueEntity> getLookUpValues() {
		return lookUpValues;
	}

	public void setLookUpValues(Set<LookUpValueEntity> lookUpValues) {
		this.lookUpValues = lookUpValues;
	}

	public String getLabel() {
		return getLabelForLocale(DEFAULT_LOCALE_ID);
	}
	
	private String getLabelForLocale(Short localeId) {
		for (LookUpLabelEntity lookUpLabel : lookUpLabels) {
			if (lookUpLabel.getLocaleId().equals(localeId)) {
				return lookUpLabel.getLabelText();
			}
		}
		throw new RuntimeException("Label not found for locale with id: \"" + localeId + "\"");
	}

}
