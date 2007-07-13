/**

 * LookUpEntity.java    version: xxx



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

import org.mifos.application.fees.business.FeeTypeEntity;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.components.audit.persistence.AuditConfigurationPersistence;

/**
 * Not sure why {@link LookUpEntity} and {@link MifosLookUpEntity} both exist.
 * They seem to both represent the same object.
 * LookUpEntity is used by {@link CustomFieldDefinitionEntity}, {@link FeeTypeEntity}
 * and {@link AuditConfigurationPersistence}.
 * 
 * Note that these values are not the same as {@link EntityType}.
 * I'm not sure we have an enum for them yet.
 */
public class LookUpEntity {

	public static final int ETHNICITY = 19;
	public static final int ACCOUNT_ACTION = 69;
	public static final int ACCOUNT_STATE_FLAG = 70;
	public static final int ACTIVITY = 87;
	public static final int REPAYMENT_RULE = 91;

	public LookUpEntity() {
		super();
	}

	private Short entityId;

	private String entityType;

	private Set lookUpLabelSet;

	private Set lookUpValueSet;


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

	public Set getLookUpLabelSet() {
		return lookUpLabelSet;
	}

	public void setLookUpLabelSet(Set lookUpLabelSet) {
		this.lookUpLabelSet = lookUpLabelSet;
	}

	public Set getLookUpValueSet() {
		return lookUpValueSet;
	}

	public void setLookUpValueSet(Set lookUpValueSet) {
		this.lookUpValueSet = lookUpValueSet;
	}

}
