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

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * A simple bean class representing a Mifos customer in a basic form.
 * Used on list - provides a display name and an identifier which can
 * be used to fetch more detailed data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="should disable at filter level and also for pmd - not important for us")
@SuppressWarnings("PMD")
public class OverdueLoan implements Serializable {

	private String totalAmountInArrears;
    private String globalAccountNum;
	private String prdOfferingName;
	private String accountStateName;
	private Integer accountStateId;
	private String totalAmountDue;

	public OverdueLoan(String totalAmountInArrears, String globalAccountNum,
			String prdOfferingName, String accountStateName,
			Integer accountStateId, String totalAmountDue) {
		super();
		this.totalAmountInArrears = totalAmountInArrears;
		this.globalAccountNum = globalAccountNum;
		this.prdOfferingName = prdOfferingName;
		this.accountStateName = accountStateName;
		this.accountStateId = accountStateId;
		this.totalAmountDue = totalAmountDue;
	}

	public Integer getAccountStateId() {
		return accountStateId;
	}

	public void setAccountStateId(Integer accountStateId) {
		this.accountStateId = accountStateId;
	}

	public String getPrdOfferingName() {
		return prdOfferingName;
	}

	public void setPrdOfferingName(String prdOfferingName) {
		this.prdOfferingName = prdOfferingName;
	}

	public String getAccountStateName() {
		return accountStateName;
	}

	public void setAccountStateName(String accountStateName) {
		this.accountStateName = accountStateName;
	}
	
	public String getTotalAmountDue() {
		return totalAmountDue;
	}

	public void setTotalAmountDue(String totalAmountDue) {
		this.totalAmountDue = totalAmountDue;
	}

	public String getTotalAmountInArrears() {
		return totalAmountInArrears;
	}

	public void setTotalAmountInArrears(String totalAmountInArrears) {
		this.totalAmountInArrears = totalAmountInArrears;
	}

	public String getGlobalAccountNum() {
		return globalAccountNum;
	}

	public void setGlobalAccountNum(String globalAccountNum) {
		this.globalAccountNum = globalAccountNum;
	}
	
}
