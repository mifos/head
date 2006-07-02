/**

 * OfficeCode.java    version: 1.0



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
 * This class represent the office code for the office
 * 
 * @author rajenders
 * 
 */
public class OfficeCode extends ValueObject {
	private static final long serialVersionUID = 666666666666l;

	/**
	 * This would hold the codeId for a code
	 */
	private Short codeId;

	/**
	 * This would hold the lookupId for a code
	 */
	private Integer lookUpId;

	/**
	 * This would hold the set of lookup value locale for a given code
	 */
	private Set lookUpValueLocale;

	/**
	 * Default constructor for for officeCode
	 * 
	 */
	public OfficeCode() {

	}

	public OfficeCode(Short codeId) {
		this.codeId = codeId;
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
	 * @param codeId the codeId to set.
	 */
	
	public void setCodeId(Short codeId) {
		this.codeId = codeId;
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


}
