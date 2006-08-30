/**

 * CheckListMaster.java    version: 1.0

 

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

package org.mifos.application.checklist.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

public class CheckListMaster extends ValueObject {

	/**
	 * The value of checklistId
	 */
	private Short checkListId;

	/**
	 * The value of the simple checklistName property.
	 */
	private String checkListName;

	/**
	 * The value of the simple checkListStatus property.
	 */
	private Short checkListStatus;

	/**
	 * simple constructor
	 */
	public CheckListMaster() {
	}

	/**
	 * @param checkListId
	 */
	public CheckListMaster(Short checkListId) {

		this.checkListId = checkListId;

	}

	/**
	 * @param checkListId
	 * @param checkListName
	 */
	public CheckListMaster(Short checkListId, String checkListName) {

		this.checkListId = checkListId;
		this.checkListName = checkListName;
	}

	/**
	 * @param checkListId
	 * @param checkListName
	 * @param checkListStatus
	 */
	public CheckListMaster(Short checkListId, String checkListName,
			Short checkListStatus) {

		this.checkListId = checkListId;
		this.checkListName = checkListName;
		this.checkListStatus = checkListStatus;
	}

	/**
	 * @param checkListId
	 */
	public void setCheckListId(Short checkListId) {
		this.checkListId = checkListId;
	}

	/**
	 * @return Returns the checkListStatus.
	 */
	public Short getCheckListStatus() {
		return checkListStatus;
	}

	/**
	 * @param checkListStatus
	 *            The checkListStatus to set.
	 */
	public void setCheckListStatus(Short checkListStatus) {
		this.checkListStatus = checkListStatus;
	}

	/**
	 * @return checkListId
	 */
	public Short getCheckListId() {
		return checkListId;
	}

	/**
	 * @param checkListName
	 */
	public void setCheckListName(String checkListName) {
		this.checkListName = checkListName;
	}

	/**
	 * @return checkListNames
	 */
	public String getCheckListName() {
		return checkListName;
	}

}
