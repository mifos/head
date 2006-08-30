/**

 * CategoryType.java    version: xxx

 

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

public class CategoryType extends ValueObject{

	public CategoryType() {
		super();
		// TODO Auto-generated constructor stub
	}
	
 /** The composite primary key value. */
    private java.lang.Short categoryId;

    /** The value of the lookupEntity association. */
    private LookUpEntity lookUpEntity;

	/**
	 * @return Returns the categoryId}.
	 */
	public java.lang.Short getCategoryId() {
		return categoryId;
	}

	/**
	 * @param categoryId The categoryId to set.
	 */
	public void setCategoryId(java.lang.Short categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * @return Returns the lookUpEntity}.
	 */
	public LookUpEntity getLookUpEntity() {
		return lookUpEntity;
	}

	/**
	 * @param lookUpEntity The lookUpEntity to set.
	 */
	public void setLookUpEntity(LookUpEntity lookUpEntity) {
		this.lookUpEntity = lookUpEntity;
	}

}
