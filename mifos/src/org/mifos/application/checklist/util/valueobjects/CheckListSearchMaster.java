/**

 * CheckListSearchMaster.java    version: 1.0

 

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

import org.mifos.framework.util.valueobjects.SearchResults;

public class CheckListSearchMaster extends SearchResults {

	/**
	 * @return Returns the checklistStatus.
	 */
	public Short getChecklistStatus() {
		return checklistStatus;
	}

	/**
	 * @param checklistStatus The checklistStatus to set.
	 */
	public void setChecklistStatus(Short checklistStatus) {
		this.checklistStatus = checklistStatus;
	}

	public CheckListSearchMaster(Short checkListId, String checkListName,
			String stateName, Short stateId, Short typeNameId ,Short checklistStatus) {		
		this.checkListId = checkListId.shortValue();
		this.checkListName = checkListName;
		this.stateName = stateName;
		this.stateId = stateId;
		this.typeNameId = typeNameId.intValue();
		this.checklistStatus = checklistStatus;
	}

	/**
	 * @param checkListId
	 * @param checkListName
	 * @param stateName
	 * @param stateId
	 */
	public CheckListSearchMaster(Short checkListId, String checkListName,
			String stateName, Short stateId,Short checklistStatus) {		
		this.checkListId = checkListId;
		this.checkListName = checkListName;
		this.stateName = stateName;
		this.stateId = stateId;
		this.checklistStatus = checklistStatus;
	}

	/**
	 * @param checkListId
	 * @param checkListName
	 * @param stateName
	 * @param stateId
	 * @param typeNameId
	 */
	public CheckListSearchMaster(Short checkListId, String checkListName,
			String stateName, Short stateId, Integer typeNameId,Short checklistStatus) {		
		this.checkListId = checkListId;
		this.checkListName = checkListName;
		this.stateName = stateName;
		this.stateId = stateId;
		this.typeNameId = typeNameId;
		this.checklistStatus = checklistStatus;
	}

	/**
	 * @param checkListId
	 * @param checkListName
	 * @param stateName
	 * @param stateId
	 * @param typeNameId
	 * @param recordType
	 */
	public CheckListSearchMaster(Short checkListId, String checkListName,
			String stateName, Short stateId, Integer typeNameId,
			Integer recordType) {
		this.checkListId = checkListId;
		this.checkListName = checkListName;
		this.stateName = stateName;
		this.stateId = stateId;
		this.typeNameId = typeNameId;
		this.recordType = recordType;
	}

	/**
	 * simple constructor
	 */
	public CheckListSearchMaster() {
	}

	/**
	 * the value of checklistId
	 */
	private Short checkListId;

	/**
	 * the value of checkListName
	 */
	private String checkListName;

	/**
	 * the value of stateName
	 */
	private String stateName;

	/**
	 * the value of stateId
	 */
	private Short stateId;

	/**
	 * the value of typeNameId
	 */
	private Integer typeNameId;
	
	private Short checklistStatus;

	/**
	 * the value of recordType
	 */
	private Integer recordType;

	/**
	 * This function returns the recordType
	 * 
	 * @return Returns the recordType.
	 */

	public Integer getRecordType() {
		return recordType;
	}

	/**
	 * This function sets the recordType
	 * 
	 * @param recordType
	 *            the recordType to set.
	 */

	public void setRecordType(Integer recordType) {
		this.recordType = recordType;
	}

	/**
	 * This function returns the checkListId
	 * 
	 * @return Returns the checkListId.
	 */

	public Short getCheckListId() {
		return checkListId;
	}

	/**
	 * This function sets the checkListId
	 * 
	 * @param checkListId
	 *            the checkListId to set.
	 */

	public void setCheckListId(Short checkListId) {
		this.checkListId = checkListId;
	}

	/**
	 * This function returns the checkListName
	 * 
	 * @return Returns the checkListName.
	 */

	public String getCheckListName() {
		return checkListName;
	}

	/**
	 * This function sets the checkListName
	 * 
	 * @param checkListName
	 *            the checkListName to set.
	 */

	public void setCheckListName(String checkListName) {
		this.checkListName = checkListName;
	}

	/**
	 * This function returns the stateId
	 * 
	 * @return Returns the stateId.
	 */

	public Short getStateId() {
		return stateId;
	}

	/**
	 * This function sets the stateId
	 * 
	 * @param stateId
	 *            the stateId to set.
	 */

	public void setStateId(Short stateId) {
		this.stateId = stateId;
	}

	/**
	 * This function returns the stateName
	 * 
	 * @return Returns the stateName.
	 */

	public String getStateName() {
		return stateName;
	}

	/**
	 * This function sets the stateName
	 * 
	 * @param stateName
	 *            the stateName to set.
	 */

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	/**
	 * This function returns the typeNameId
	 * 
	 * @return Returns the typeNameId.
	 */

	public Integer getTypeNameId() {
		return typeNameId;
	}

	/**
	 * This function sets the typeNameId
	 * 
	 * @param typeNameId
	 *            the typeNameId to set.
	 */

	public void setTypeNameId(Integer typeNameId) {
		this.typeNameId = typeNameId;
	}

}
