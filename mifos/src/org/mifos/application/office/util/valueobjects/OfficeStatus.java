/**

 * OfficeStatus.java    version: 1.0



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
package org.mifos.application.office.util.valueobjects;

import java.util.Set;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class represent the office status 
 * @author rajenders
 *
 */
public class OfficeStatus extends ValueObject
{
	private static final long serialVersionUID=666666666666l;
	/**
	 * This would hold the statusId 
	 */
	private Short statusId;
	/**
	 * this would hold the lookupId
	 */
	private Integer lookUpId;
	/**
	 * This would hold the lookUpValueLocale
	 */
	private Set lookUpValueLocale;

	/**
	 * Default constructor for the OfficeStatus
	 *
	 */
	public OfficeStatus()
	{

	}
	/**
	 * Constructor taking statusId as parameter
	 * @param statusId
	 */
	public OfficeStatus(Short statusId)
	{
		this.statusId = statusId;
	}
	/**
	 * This function returns the lookUpId
	 * @return Returns the lookUpId.
	 */
	
	public Integer getLookUpId() {
		return lookUpId;
	}
	/**
	 * This function sets the lookUpId
	 * @param lookUpId the lookUpId to set.
	 */
	
	public void setLookUpId(Integer lookUpId) {
		this.lookUpId = lookUpId;
	}
	/**
	 * This function returns the lookUpValueLocale
	 * @return Returns the lookUpValueLocale.
	 */
	
	public Set getLookUpValueLocale() {
		return lookUpValueLocale;
	}
	/**
	 * This function sets the lookUpValueLocale
	 * @param lookUpValueLocale the lookUpValueLocale to set.
	 */
	
	public void setLookUpValueLocale(Set lookUpValueLocale) {
		this.lookUpValueLocale = lookUpValueLocale;
	}
	/**
	 * This function returns the statusId
	 * @return Returns the statusId.
	 */
	
	public Short getStatusId() {
		return statusId;
	}
	/**
	 * This function sets the statusId
	 * @param statusId the statusId to set.
	 */
	
	public void setStatusId(Short statusId) {
		this.statusId = statusId;
	}


}
