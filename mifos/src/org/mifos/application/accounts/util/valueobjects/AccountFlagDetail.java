/**

 * AccountFlagDetail.java    version: 1.0



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

import java.sql.Date;

import org.mifos.framework.util.valueobjects.ValueObject;

public class AccountFlagDetail extends ValueObject{
	private Integer accountFlagId;
	private Integer accountId;
	private Short flagId;
	private Short createdBy;
	private Date createdDate;
	public AccountFlagDetail(){}
	public Integer getAccountFlagId() {
		return accountFlagId;
	}
	public void setAccountFlagId(Integer accountFlagId) {
		this.accountFlagId = accountFlagId;
	}
	public Integer getAccountId() {
		return accountId;
	}
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	public Short getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Short createdBy) {
		this.createdBy = createdBy;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Short getFlagId() {
		return flagId;
	}
	public void setFlagId(Short flagId) {
		this.flagId = flagId;
	}
}
