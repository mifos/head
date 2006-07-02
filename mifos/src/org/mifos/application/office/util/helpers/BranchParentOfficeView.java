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
package org.mifos.application.office.util.helpers;

import java.util.List;

import org.mifos.framework.business.View;

/**
 * This class is used to get the leaf level offices i.e branch offices along
 * with the parent for that list
 * 
 * @author rajenders
 * 
 */
public class BranchParentOfficeView extends View {
	
	private Short branchParentOfficeId;

	private String branchParentOfficeName;

	private List<BranchOfficeView> branchOffice;

	public BranchParentOfficeView(Short branchParentOfficeId,
			String branchParentOfficeName) {
		this.branchParentOfficeId = branchParentOfficeId;
		this.branchParentOfficeName = branchParentOfficeName;
	}

	public java.lang.Short getOfficeId() {
		return branchParentOfficeId;
	}

	public java.lang.String getOfficeName() {
		return branchParentOfficeName;
	}

	public void setBranchOffice(List<BranchOfficeView> branchOffice) {
		this.branchOffice = branchOffice;
	}

	public List getBranchOffice() {
		return branchOffice;
	}

	public int getNoOfChildren() {
		if (null != branchOffice) {
			return branchOffice.size();
		} else {
			return 0;
		}
	}

}
