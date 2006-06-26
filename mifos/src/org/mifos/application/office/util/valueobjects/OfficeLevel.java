/**

 * OfficeLevel.java    version: 1.0



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
package org.mifos.application.office.util.valueobjects;

import java.util.Set;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class represet the OfficeLevel in the system e.g. headoffice
 * @author rajenders
 *
 */
public class OfficeLevel extends ValueObject{
	private static final long serialVersionUID=666666666666l;
	/**
	 * This would hold the interactionFlag office .Interaction flag
	 * tells whether it can have clients or not
	 */
	private Short interactionFlag;
	/**
	 * This would hold the levelId of the office
	 */
	private Short levelId;
	private Integer lookUpId;
    private Set lookUpValueLocale;
	/**
	 * This would hold the parent of this office
	 */
	private OfficeLevel parent;

	private OfficeLevel child;
	/**
	 * This would hold the whether office is configured or not
	 */
	private Short configured;

	/**
	 * Default constructor for the OfficeLevel
	 *
	 */
	public OfficeLevel()
	{

	}
	/**
	 * Constructor taking levelId
	 * @param levelId
	 */
	public OfficeLevel(Short levelId)
	{
		this.levelId = levelId;
	}

	/**
	 * This function returns the configured
	 * @return Returns the configured.
	 */
	public Short getConfigured() {
		return configured;
	}
	/**
	 * This function sets the configured
	 * @param configured The configured to set.
	 */
	public void setConfigured(Short configured) {
		this.configured = configured;
	}
	/**
	 * This Function returns the interactionFlag
	 * @return Returns the interactionFlag.
	 */
	public Short getInteractionFlag() {
		return interactionFlag;
	}
	/**
	 * This function sets the interactionFlag
	 * @param interactionFlag The interactionFlag to set.
	 */
	public void setInteractionFlag(Short interactionFlag) {
		this.interactionFlag = interactionFlag;
	}
	/**
	 * This Function returns the levelId
	 * @return Returns the levelId.
	 */
	public Short getLevelId() {
		return levelId;
	}
	/**
	 * This function sets the levelId
	 * @param levelId The levelId to set.
	 */
	public void setLevelId(Short levelId) {
		this.levelId = levelId;
	}
	/**
	 * This function sets the lookUpValueLocale
	 * @param levelId The lookUpValueLocale to set.
	 */

	public void setLookUpValueLocale(Set lookUpValueLocale)
	{

			  this.lookUpValueLocale = lookUpValueLocale;
	}

	/**
	 * This function returns the lookUpValueLocale
	 * @return Returns the lookUpValueLocale.
	 */

	public Set getLookUpValueLocale()
	{
			    return lookUpValueLocale;

    }
	/**
	 * This function returns the lookUpId
	 * @return Returns the lookUpId.
	 */
	
	public Integer getLookUpId()
	{
			return lookUpId;
	}
	/**
	 * This function sets the lookUpId
	 * @param levelId The lookUpId to set.
	 */

		public void setLookUpId(Integer lookUpId) {
			this.lookUpId = lookUpId;
	}
	/**
	 * This Function returns the parent
	 * @return Returns the parent.
	 */
	public OfficeLevel getParent() {
		return parent;
	}
	/**
	 * This function sets the parent
	 * @param parent The parent to set.
	 */
	public void setParent(OfficeLevel parent) {
		this.parent = parent;
	}

	/**
	 * This Function returns the parent
	 * @return Returns the parent.
	 */
	public OfficeLevel getChild() {
		return child;
	}
	/**
	 * This function sets the parent
	 * @param parent The parent to set.
	 */
	public void setChild(OfficeLevel child) {
		this.child = child;
	}

}
