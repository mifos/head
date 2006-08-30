/**

 * AccountSearchResults.java    version: 1.0



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
package org.mifos.application.accounts.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * Thsi class would hold the account search results
 */
public class AccountSearchResults extends ValueObject{



	private static final long serialVersionUID =6666666l;

	/**
	 * This would hold the officeName
	 */
	private String officeName;
	private short levelId;
	/**
	 * This would hold the centerName
	 */
	private String centerName;

	/**
	 * This would hold the group name
	 */
	private String groupName;

	/**
	 * This would hold the Client Name
	 */
	private String clientName;

	/**
	 * This would hold the clientId
	 */

	private int clientId;

	/**
	 * This function returns the centerName
	 * @return Returns the centerName.
	 */

	private String globelNo;

	public String getCenterName() {
		/*
		if(null== this.centerName||this.centerName.equalsIgnoreCase(""))
		{
			return this.groupName;
		}
		else
		{
			return this.centerName;
		}
		*/
		return this.centerName;
	}

	/**
	 * This function sets the centerName
	 * @param centerName the centerName to set.
	 */

	public void setCenterName(String centerName)
	{
		if(centerName==null)
		{
			centerName="";

		}
		this.centerName = centerName;
	}
	/**
	 * This function returns the clientId
	 * @return Returns the clientId.
	 */

	public int getClientId() {
		return clientId;
	}

	/**
	 * This function sets the clientId
	 * @param clientId the clientId to set.
	 */

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	/**
	 * This function returns the levelId
	 * @return Returns the levelId.
	 */

	public short getLevelId() {
		return levelId;
	}

	/**
	 * This function sets the levelId
	 * @param levelId the levelId to set.
	 */

	public void setLevelId(short levelId) {
		this.levelId = levelId;
	}

	/**
	 * This function returns the clientName
	 * @return Returns the clientName.
	 */

	public String getClientName() {


		return clientName;
		/*
		if(null== this.centerName||this.centerName.equalsIgnoreCase(""))
		{
			return "";
		}
		else
		{
			return this.clientName;
		}
		*/
	}

	/**
	 * This function sets the clientName
	 * @param clientName the clientName to set.
	 */

	public void setClientName(String clientName) {

		if(null==clientName)
		{
			clientName="";
		}
		this.clientName = clientName;
	}

	/**
	 * This function returns the groupName
	 * @return Returns the groupName.
	 */

	public String getGroupName() {


		return groupName;


	}

	/**
	 * This function sets the groupName
	 * @param groupName the groupName to set.
	 */

	public void setGroupName(String groupName) {

		if(null==groupName)
		{
			groupName="";
		}
		this.groupName = groupName;
	}

	/**
	 * This function returns the officeName
	 * @return Returns the officeName.
	 */

	public String getOfficeName() {
		return officeName;
	}

	/**
	 * This function sets the officeName
	 * @param officeName the officeName to set.
	 */

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	/**
	 * This would return the groupId
	 */

	public String getGroupId()
	{
		// System.out.println("+++++++++++++++++++centername is :"+centerName+"id is "+this.clientId);
		if(null== this.centerName)
		{
			return String.valueOf(this.clientId);
		}
		else
		{
			return "";
		}

	}
	public String getGroupLink()
	{
		if(null== this.centerName)
		{
			return "true";
		}
		else
		{
			return "false";
		}


	}

	public String getClientLink()
	{
		if(null!= this.centerName)
		{
			return "true";
		}
		else
		{
			return "false";
		}


	}
	public String getIdClient()
	{
		if(null== this.centerName)
		{
			return "";
		}
		else
		{
			return String.valueOf(this.clientId);
		}

	}

	/**
	 * This function returns the globelNo
	 * @return Returns the globelNo.
	 */

	public String getGlobelNo() {
		return globelNo;
	}

	/**
	 * This function sets the globelNo
	 * @param globelNo the globelNo to set.
	 */

	public void setGlobelNo(String globelNo) {
		this.globelNo = globelNo;
	}
}
