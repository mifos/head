/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.dto.domain;


public class MatchedClientDto {

	private final String globalCustNum;
    private final String displayName;
    private final String phone;
    private final String governmentId;
    private final String displayAddress;
    private final String officeName;
    private final String officeNum;
	
    public MatchedClientDto(String globalCustNum, String displayName,
			String phone, String governmentId, String displayAddress,
			String officeName, String officeNum) {
		super();
		this.globalCustNum = globalCustNum;
		this.displayName = displayName;
		this.phone = phone;
		this.governmentId = governmentId;
		this.displayAddress = displayAddress;
		this.officeName = officeName;
		this.officeNum = officeNum;
	}
    
	public String getGlobalCustNum() {
		return globalCustNum;
	}
	public String getDisplayName() {
		return displayName;
	}
	public String getPhone() {
		return phone;
	}
	public String getGovernmentId() {
		return governmentId;
	}
	public String getDisplayAddress() {
		return displayAddress;
	}
	public String getOfficeName() {
		return officeName;
	}
	public String getOfficeNum() {
		return officeNum;
	}
}