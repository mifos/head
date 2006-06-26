/**

 * PersonnelLevel.java    version: xxx

 

 * Copyright © 2005-2006 Grameen Foundation USA

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

package org.mifos.application.personnel.business;

import org.mifos.framework.business.PersistentObject;

/**
 * This represents the master data of defined personnel levels in the system.
 * 
 * @author ashishsm
 * 
 */
public class PersonnelLevelEntity extends PersistentObject {

	private Short levelId;

	private Short interactionFlag;

	private Integer lookUpId;

	private PersonnelLevelEntity parent;

	public PersonnelLevelEntity() {
		super();

	}

	private Short getInteractionFlag() {
		return interactionFlag;
	}

	private void setInteractionFlag(Short interactionFlag) {
		this.interactionFlag = interactionFlag;
	}

	public boolean isInteractionFlag() {
		return this.interactionFlag > 0;
	}

	public Short getLevelId() {
		return levelId;
	}

	public void setLevelId(Short levelId) {
		this.levelId = levelId;
	}

	public Integer getLookUpId() {
		return lookUpId;
	}

	public void setLookUpId(Integer lookUpId) {
		this.lookUpId = lookUpId;
	}

	public PersonnelLevelEntity getParent() {
		return parent;
	}

	public void setParent(PersonnelLevelEntity parent) {
		this.parent = parent;
	}

}
