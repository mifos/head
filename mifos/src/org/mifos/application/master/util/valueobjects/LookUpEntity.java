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

package org.mifos.application.master.util.valueobjects;

import java.util.Set;

import org.mifos.application.customer.util.valueobjects.CustomFieldDefinition;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author ashishsm
 *
 */
public class LookUpEntity extends ValueObject {

	/**
	 *
	 */
	public LookUpEntity() {
		super();
		// TODO Auto-generated constructor stub
	}
	/* {src_lang=Java}*/


	private Short entityId;
	/* {transient=false, volatile=false}*/

	private String entityType;
	/* {transient=false, volatile=false}*/

	private Set lookUpLabelSet;

	private Set lookUpValueSet;

	/**
	 * @return Returns the entityId}.
	 */
	public Short getEntityId() {
		return entityId;
	}
	/**
	 * @param entityId The entityId to set.
	 */
	public void setEntityId(Short entityId) {
		this.entityId = entityId;
	}
	/**
	 * @return Returns the entityType}.
	 */
	public String getEntityType() {
		return entityType;
	}
	/**
	 * @param entityType The entityType to set.
	 */
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	/**
	 * @return Returns the lookUpLabelSet}.
	 */
	public Set getLookUpLabelSet() {
		return lookUpLabelSet;
	}
	/**
	 * @param lookUpLabelSet The lookUpLabelSet to set.
	 */
	public void setLookUpLabelSet(Set lookUpLabelSet) {
		this.lookUpLabelSet = lookUpLabelSet;
	}
	/**
	 * @return Returns the lookUpValueSet}.
	 */
	public Set getLookUpValueSet() {
		return lookUpValueSet;
	}
	/**
	 * @param lookUpValueSet The lookUpValueSet to set.
	 */
	public void setLookUpValueSet(Set lookUpValueSet) {
		this.lookUpValueSet = lookUpValueSet;
	}

public boolean equals(Object obj)
{
	/*
		LookUpEntity lookUpEntity = (LookUpEntity)obj;
		if(this.entityName.equals(lookUpEntity.getEntityName())){
			return true;
		}else{
			return false;
		}
		*/
		return true;
	}

	public int hashCode(){
		//return entityName.hashCode();
		return 0;
	}
}
