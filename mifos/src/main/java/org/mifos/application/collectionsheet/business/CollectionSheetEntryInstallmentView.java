/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
 
package org.mifos.application.collectionsheet.business;

import java.util.Date;

import org.mifos.framework.business.View;

public abstract class CollectionSheetEntryInstallmentView extends View {

	private final Integer actionDateId;

	private final Integer accountId;

	private final Integer customerId;

	private final Date actionDate;

	private final Short installmentId;

	public CollectionSheetEntryInstallmentView(Integer accountId, Integer customerId,
			Short installmentId, Integer actionDateId, Date actionDate) {
		this.actionDateId = actionDateId;
		this.accountId = accountId;
		this.customerId = customerId;
		this.actionDate = actionDate;
		this.installmentId = installmentId;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public Integer getActionDateId() {
		return actionDateId;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public Short getInstallmentId() {
		return installmentId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof CollectionSheetEntryInstallmentView) {
			CollectionSheetEntryInstallmentView collectionSheetEntryAccountActionView = (CollectionSheetEntryInstallmentView) obj;
			if (collectionSheetEntryAccountActionView.getAccountId()
					.equals(getAccountId())
					&& collectionSheetEntryAccountActionView.getCustomerId().equals(
							getCustomerId()))
				return true;
		}
		return false;
	}

}
