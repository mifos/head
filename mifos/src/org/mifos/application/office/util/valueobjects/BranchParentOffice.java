/**

 * BranchParentOffice.java    version: 1.0

 

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

import java.io.Serializable;
import java.util.List;

/**
 * This class is used to get the leaf level offices i.e branch offices
 * along with the parent for that list
 */
public class BranchParentOffice implements Serializable{
	private static final long serialVersionUID=9222;

	/**
	 * This would hold the branch list parentId's
	 */
	private java.lang.Short branchParentOfficeId;

	/**
	 * This would hold the parent name
	 */
	private String branchParentOfficeName;

	/**
	 * This would hold the list of all the branch offices for this level
	 */
	private java.util.List branchOffice;

	/**
	 * Two argument constructor for the BranchParentOffice
	 * @param branchParentOfficeId parent officeId
	 * @param branchParentOfficeName parent office name
	 */
	public BranchParentOffice(Short branchParentOfficeId,
			String branchParentOfficeName) {
		this.branchParentOfficeId = branchParentOfficeId;
		this.branchParentOfficeName = branchParentOfficeName;

	}

	/**
	 * This function gets the parent officeId
	 * @return
	 */
	public java.lang.Short getOfficeId() {
		return branchParentOfficeId;
	}
	/**
	 * This function gets parent office name
	 * @return
	 */

	public java.lang.String getOfficeName() {
		return branchParentOfficeName;
	}

	/**
	 * This functio sets the list of branch offices
	 * @param branchOffice
	 */
	public void setBranchOffice(List branchOffice) {
		this.branchOffice = branchOffice;
	}

	/**
	 * This function gets the list of branch offices
	 * @return
	 */
	public List getBranchOffice() {
		return branchOffice;
	}

	/**
	 * This function returns the no of childern this parent office has or zero 
	 * if it does not have childern
	 * @return
	 */
	public int getNoOfChildren() {
		if (null != branchOffice) {
			return branchOffice.size();
		} else {
			return 0;
		}
	}

}
