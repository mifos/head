/**

 * RoleActivity.java    version: 1.0

 

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

/**
 * 
 */
package org.mifos.application.rolesandpermission.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class represent association between role and activities
 * @author rajenders
 */
public class RoleActivity extends ValueObject{
	
	
	private static final long serialVersionUID=9992323214235l;

	
	private RoleActivitiesKey  compositeKey;
	  private Integer versionNo;

	/**
	 * This Function returns the compositeKey
	 * @return Returns the compositeKey.
	 */
	public RoleActivitiesKey getCompositeKey() {
		return compositeKey;
	}

	/**
	 * This function set the compositeKey
	 * @param compositeKey The compositeKey to set.
	 */
	public void setCompositeKey(RoleActivitiesKey compositeKey) {
		this.compositeKey = compositeKey;
	}
	public Integer getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}

	public RoleActivity() {
	}


}
