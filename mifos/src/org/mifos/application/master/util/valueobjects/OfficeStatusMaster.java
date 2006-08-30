/**

 * OfficeStatusMaster.java    version: 1

 

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

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class represent officeStatus master data 
 */
public class OfficeStatusMaster extends ValueObject
{
	private static final long serialVersionUID=9876;

	/**
	 * This would hold the statusId for the office 
	 */
	private Short statusId;
	/**
	 * This would hold the statusName 
	 */
	private String statusName;
	
	/**
	 * Constructor taking the statusId and statusName as arguments
	 * @param statusId
	 * @param statusName
	 */

	public OfficeStatusMaster(Short statusId , String statusName)
	{
		this.statusId = statusId;
		this.statusName = statusName;
	}

	/**
	 * This function returns the statusId 
	 * @return
	 */
	public Short getStatusId()
	{
		return statusId;
	}

	/**
	 * This function returns the  StatusName
	 * @return
	 */
	public String getStatusName()
	{
		return statusName;
	}



}
