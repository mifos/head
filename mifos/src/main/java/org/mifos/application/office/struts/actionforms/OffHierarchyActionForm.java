/**

 * OffHierarchyActionForm.java    version: 1.0

 

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
package org.mifos.application.office.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;
import org.mifos.framework.struts.actionforms.BaseActionForm;

public class OffHierarchyActionForm extends BaseActionForm {

	private String headOffice;

	private String branchOffice;

	private String regionalOffice;

	private String subRegionalOffice;

	private String areaOffice;

	public OffHierarchyActionForm() {
		super();
	}

	public String getAreaOffice() {
		return areaOffice;
	}

	public void setAreaOffice(String areaOffice) {
		this.areaOffice = areaOffice;
	}

	public String getRegionalOffice() {
		return regionalOffice;
	}

	public void setRegionalOffice(String regionalOffice) {
		this.regionalOffice = regionalOffice;
	}

	public String getSubRegionalOffice() {
		return subRegionalOffice;
	}

	public void setSubRegionalOffice(String subRegionalOffice) {
		this.subRegionalOffice = subRegionalOffice;
	}

	public String getBranchOffice() {
		return branchOffice;
	}

	public void setBranchOffice(String branchOffice) {
		this.branchOffice = branchOffice;
	}

	public String getHeadOffice() {
		return headOffice;
	}

	public void setHeadOffice(String headOffice) {
		this.headOffice = headOffice;
	}

	public boolean getRegionalOfficeValue() {
		return getBooleanValue(getRegionalOffice());
	}

	public boolean getSubRegionalOfficeValue() {
		return getBooleanValue(getSubRegionalOffice());
	}

	public boolean getAreaOfficeValue() {
		return getBooleanValue(getAreaOffice());
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		regionalOffice = null;
		subRegionalOffice = null;
		areaOffice = null;
	}

}
