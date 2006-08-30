/**

 * OfficeHierarchyActionForm.java    version: 1.0



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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.office.util.valueobjects.OfficeLevel;
import org.mifos.framework.struts.actionforms.MifosActionForm;

public class OfficeHierarchyActionForm extends MifosActionForm {

	private static final long serialVersionUID=0;

	/**
	 * This would hold the regional office
	 */
	private String regionalOffice;
	/**
	 * This would hold the subregional office
	 */
	private String subRegionalOffice;

	/**
	 * This would hold the area office
	 */
	private String AreaOffice;
	/**
	 * This function returns the areaOffice
	 * @return Returns the areaOffice.
	 */
	public String getAreaOffice() {
		return AreaOffice;
	}
	/**
	 * This function sets the areaOffice
	 * @param areaOffice The areaOffice to set.
	 */
	public void setAreaOffice(String areaOffice) {
		AreaOffice = areaOffice;
	}
	/**
	 * This function returns the regionalOffice
	 * @return Returns the regionalOffice.
	 */
	public String getRegionalOffice() {
		return regionalOffice;
	}
	/**
	 * This function sets the regionalOffice
	 * @param regionalOffice The regionalOffice to set.
	 */
	public void setRegionalOffice(String regionalOffice) {
		this.regionalOffice = regionalOffice;
	}
	/**
	 * This function returns the subRegionalOffice
	 * @return Returns the subRegionalOffice.
	 */
	public String getSubRegionalOffice() {
		return subRegionalOffice;
	}
	/**
	 * This function sets the subRegionalOffice
	 * @param subRegionalOffice The subRegionalOffice to set.
	 */
	public void setSubRegionalOffice(String subRegionalOffice) {
		this.subRegionalOffice = subRegionalOffice;
	}

	/**
	 * This function returns the all the three levels to user which he can configure
	 * as a list of OfficeLevel objects
	 * @return
	 */
	public List getLevelList() {

		List levelList = new ArrayList();
		if( regionalOffice.equalsIgnoreCase(OfficeConstants.CHECKED))
		{
			OfficeLevel level = new OfficeLevel();
			level.setLevelId(OfficeConstants.REGIONALOFFICE);
			level.setConfigured(OfficeConstants.CONFIURE);
			levelList.add(level);
		}
		else
		{
			OfficeLevel level = new OfficeLevel();
			level.setLevelId(OfficeConstants.REGIONALOFFICE);
			level.setConfigured(OfficeConstants.UNCONFIURE);
			levelList.add(level);

		}
		if( subRegionalOffice.equalsIgnoreCase(OfficeConstants.CHECKED))
		{

			// System.out.println("");
			OfficeLevel level = new OfficeLevel();
			level.setLevelId(OfficeConstants.SUBREGIONALOFFICE);
			level.setConfigured(OfficeConstants.CONFIURE);
			levelList.add(level);
		}
		else
		{
			OfficeLevel level = new OfficeLevel();
			level.setLevelId(OfficeConstants.SUBREGIONALOFFICE);
			level.setConfigured(OfficeConstants.UNCONFIURE);
			levelList.add(level);

		}
		if( AreaOffice.equalsIgnoreCase(OfficeConstants.CHECKED))
		{
			OfficeLevel level = new OfficeLevel();
			level.setLevelId(OfficeConstants.AREAOFFICE);
			level.setConfigured(OfficeConstants.CONFIURE);
			levelList.add(level);
		}
		else
		{
			OfficeLevel level = new OfficeLevel();
			level.setLevelId(OfficeConstants.AREAOFFICE);
			level.setConfigured(OfficeConstants.UNCONFIURE);
			levelList.add(level);

		}

		return levelList;
	}
	/* (non-Javadoc)
	 * @see org.apache.struts.validator.ValidatorForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		regionalOffice="";
		subRegionalOffice="";
		AreaOffice="";
		super.reset(arg0, arg1);
	}

}
