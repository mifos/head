/**

 * BranchOffice.java    version: 1.0

 

 * Copyright © 2005-2006 Grameen Foundation USA

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

import org.mifos.framework.business.View;

/**
 * This class represent a branch office
 * @author rajenders
 */
public class BranchOfficeView extends View {

	private Short officeId;

	private String officeName;

	private Short parentOfficeId;

	public BranchOfficeView(Short officeId, String officeName,
			Short parentOfficeId) {
		this.officeId = officeId;
		this.officeName = officeName;
		this.parentOfficeId = parentOfficeId;

	}

	public java.lang.Short getOfficeId() {
		return officeId;
	}

	public java.lang.String getOfficeName() {
		return officeName;
	}

	public java.lang.Short getParentOfficeId() {
		return parentOfficeId;
	}

}
