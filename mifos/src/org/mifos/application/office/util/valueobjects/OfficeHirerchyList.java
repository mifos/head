/**

 * OfficeHirerchyList.java    version: 1.0

 

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

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class represents the list current officeLevels in the system
 * @author rajenders
 *
 */
public class OfficeHirerchyList extends ValueObject {
	
	public static final long serialVersionUID=0l;
	/**
	 * This would hold the list of officeLevels
	 */
	List levelList = new ArrayList();

	/**
	 * This function returns the levelList
	 * @return Returns the levelList.
	 */
	public List getLevelList() {
		return levelList;
	}

	/**
	 * This function sets the levelList
	 * @param levelList The levelList to set.
	 */
	public void setLevelList(List levelList) {
		this.levelList = levelList;
	}

	/* (non-Javadoc)
	 * @see org.mifos.framework.util.valueobjects.ValueObject#getResultName()
	 */
	@Override
	public String getResultName() {
		
		return OfficeConstants.OFFFICEHIERARCHYVO;

	}
	

}
