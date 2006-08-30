/**

 * OfficeCodeMaster.java    version: 1

 

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
 * This class is used to reprsent the office code 
 */
public class OfficeCodeMaster extends ValueObject
{
	private static final long serialVersionUID=9876;

	/**
	 * This would hold the office codeId for the office
	 */
	private Short codeId;
	/**
	 * This would hold the officeCode name for the office 
	 */
	private String lookUpName;

	/**
	 * Two argument constructor for the OfficeCodeMaster
	 * @param codeId
	 * @param lookUpName
	 */
	public OfficeCodeMaster(Short codeId , String lookUpName)
	{
		this.codeId = codeId;
		this.lookUpName = lookUpName;
	}

	/**
	 * This function returns the codeId
	 * @return Returns the codeId.
	 */
	public Short getCodeId() {
		return codeId;
	}

	/**
	 * This function sets the codeId
	 * @param codeId The codeId to set.
	 */
	public void setCodeId(Short codeId) {
		this.codeId = codeId;
	}

	/**
	 * This function returns the lookUpName
	 * @return Returns the lookUpName.
	 */
	public String getLookUpName() {
		return lookUpName;
	}

	/**
	 * This function sets the lookUpName
	 * @param lookUpName The lookUpName to set.
	 */
	public void setLookUpName(String lookUpName) {
		this.lookUpName = lookUpName;
	}



}
