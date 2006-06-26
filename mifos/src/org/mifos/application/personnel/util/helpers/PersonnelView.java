/**
 
 * PersonnelMaster.java    version: xxx
 
 
 
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

package org.mifos.application.personnel.util.helpers;

import org.mifos.framework.business.PersistentObject;

/**
 * This represents a lightweight personnel object which could be used in places
 * where entire personnel object would be an overhead . for e.g. displaying a
 * list of personnels in a combo box in the UI.
 * 
 * @author ashishsm
 * 
 */
public class PersonnelView extends PersistentObject {

	private Short personnelId;

	private String globalPersonnelNum;

	private String displayName;

	public PersonnelView() {
		super();

	}

	public PersonnelView(Short personnelId) {
		this.personnelId = personnelId;

	}

	public PersonnelView(Short personnelId, String displayName) {
		this.personnelId = personnelId;
		this.displayName = displayName;

	}

	public PersonnelView(Short personnelId, String displayName,
			Integer versionNo) {
		this.personnelId = personnelId;
		this.displayName = displayName;
		this.versionNo = versionNo;

	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getGlobalPersonnelNum() {
		return globalPersonnelNum;
	}

	public void setGlobalPersonnelNum(String globalPersonnelNum) {
		this.globalPersonnelNum = globalPersonnelNum;
	}

	public Short getPersonnelId() {
		return personnelId;
	}

	public void setPersonnelId(Short personnelId) {
		this.personnelId = personnelId;
	}
}
