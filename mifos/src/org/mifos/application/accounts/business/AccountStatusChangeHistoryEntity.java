/**
 
 * AccountStatusChangeHistory.java    version: 1.0
 
 
 
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
package org.mifos.application.accounts.business;

import java.util.Date;
import java.util.Locale;

import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.struts.tags.DateHelper;

public class AccountStatusChangeHistoryEntity extends PersistentObject {
	private final Integer accountStatusChangeId;

	private final AccountBO account;

	private final AccountStateEntity oldStatus;

	private final AccountStateEntity newStatus;

	private final PersonnelBO personnel;

	private Locale locale = null;

	protected AccountStatusChangeHistoryEntity() {
		accountStatusChangeId = null;
		this.oldStatus = null;
		this.newStatus = null;
		this.personnel = null;
		this.account = null;
	}

	public AccountStatusChangeHistoryEntity(AccountStateEntity oldStatus,
			AccountStateEntity newStatus, PersonnelBO personnel,
			AccountBO account) {
		accountStatusChangeId = null;
		this.oldStatus = oldStatus;
		this.newStatus = newStatus;
		this.personnel = personnel;
		this.account = account;
		this.setCreatedDate(new Date(System.currentTimeMillis()));
	}

	public AccountBO getAccount() {
		return account;
	}

	public Integer getAccountStatusChangeId() {
		return accountStatusChangeId;
	}

	public AccountStateEntity getNewStatus() {
		return newStatus;
	}

	public AccountStateEntity getOldStatus() {
		return oldStatus;
	}

	public PersonnelBO getPersonnel() {
		return personnel;
	}

	public String getPersonnelName() {
		return personnel.getDisplayName();
	}

	public String getOldStatusName() {
		return oldStatus.getName(account.getUserContext().getLocaleId());
	}

	public String getNewStatusName() {
		return newStatus.getName(account.getUserContext().getLocaleId());
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getUserPrefferedTransactionDate() {
		return DateHelper.getUserLocaleDate(getLocale(), getCreatedDate()
				.toString());
	}

}
