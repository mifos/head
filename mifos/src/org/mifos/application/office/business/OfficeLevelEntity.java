/**

 * OfficeLevel.java    version: 1.0



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
package org.mifos.application.office.business;

import java.util.Set;

import org.mifos.framework.business.PersistentObject;

/**
 * This class represet the OfficeLevel in the system e.g. headoffice
 * 
 * @author rajenders
 * 
 */
public class OfficeLevelEntity extends PersistentObject {


	private Short levelId;

	private Integer lookUpId;

	private Set lookUpValueLocale;

	private OfficeLevelEntity parent;

	private OfficeLevelEntity child;

	private Short configured;

	private Short interactionFlag;

	public OfficeLevelEntity() {

	}

	public OfficeLevelEntity(Short levelId) {
		this.levelId = levelId;
	}

	private Short getConfigured() {
		return configured;
	}

	private void setConfigured(Short configured) {
		this.configured = configured;
	}
	
	public boolean isConfigured()
	{
		return this.configured >0;
	}
	public void  addConfigured(boolean configured)
	{
		 this.configured=(short)(configured?1:0);
	}

	private Short getInteractionFlag() {
		return interactionFlag;
	}

	private void setInteractionFlag(Short interactionFlag) {
		this.interactionFlag = interactionFlag;
	}

	public boolean isInteractionFlag()
	{
		return this.interactionFlag>0;
	}
	public void addInteractionFlag(boolean interactionFlag)
	{
		 this.interactionFlag=(short)(interactionFlag?1:0);
	}
	
	public Short getLevelId() {
		return levelId;
	}

	public void setLevelId(Short levelId) {
		this.levelId = levelId;
	}

	public void setLookUpValueLocale(Set lookUpValueLocale) {

		this.lookUpValueLocale = lookUpValueLocale;
	}

	public Set getLookUpValueLocale() {
		return lookUpValueLocale;

	}

	public Integer getLookUpId() {
		return lookUpId;
	}

	public void setLookUpId(Integer lookUpId) {
		this.lookUpId = lookUpId;
	}

	public OfficeLevelEntity getParent() {
		return parent;
	}

	public void setParent(OfficeLevelEntity parent) {
		this.parent = parent;
	}

	public OfficeLevelEntity getChild() {
		return child;
	}

	public void setChild(OfficeLevelEntity child) {
		this.child = child;
	}
}
