/**

 * OfficeLevelMaster.java    version: 1

 

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

import java.util.List;

import org.mifos.framework.util.valueobjects.ValueObject;

public class OfficeLevelMaster extends ValueObject{
	private static final long serialVersionUID=9876543;

	/**
	 * This would hold the id of perticular office level
	 */
	private Short levelId;
	/**
	 * This would hold the name of the office level
	 */
	private String levelName;
	
	/**
	 * This would hold the all the childern for this level
	 */
	private List officeLevelChildren;


	/**
	 * This function returns the levelId
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
	 * This function returns the levelName
	 * @return Returns the levelName.
	 */
	public String getLevelName() {
		return levelName;
	}
	/**
	 * This function sets the levelName
	 * @param levelName The levelName to set.
	 */
	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}
	/**
	 * @param levelId
	 * @param levelName
	 */
	public OfficeLevelMaster(Short levelId, String levelName) {
		// TODO Auto-generated constructor stub
		this.levelId = levelId;
		this.levelName = levelName;
	}

    public void setOfficeLevelChildren(List officeLevelChildren)
    {
		this.officeLevelChildren = officeLevelChildren;
	}

	public List getOfficeLevelChildren()
	{
		return officeLevelChildren;
	}

}
