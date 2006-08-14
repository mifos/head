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

import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.exceptions.PropertyNotFoundException;

/**
 * This class represet the OfficeLevel in the system e.g. headoffice
 * 
 * @author rajenders
 * 
 */
public class OfficeLevelEntity extends MasterDataEntity {

	private final OfficeLevelEntity parent;

	private final OfficeLevelEntity child;

	private Short configured;

	private Short interactionFlag;

	protected  OfficeLevelEntity() {
		parent=null;
		child=null;

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
		 setConfigured((short)(configured?1:0));
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
		 setInteractionFlag((short)(interactionFlag?1:0));
	}

	public OfficeLevelEntity getParent() {
		return parent;
	}
	public OfficeLevelEntity getChild() {
		return child;
	}
	
	public OfficeLevel getLevel() throws PropertyNotFoundException{
		return OfficeLevel.getOfficeLevel(this.getId());
	}
}
