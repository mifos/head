/**

 * CustomerAccountPaymentData.java    version: xxx



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

package org.mifos.application.accounts.util.helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.bulkentry.business.BulkEntryAccountFeeActionView;
import org.mifos.application.bulkentry.business.BulkEntryCustomerAccountInstallmentView;
import org.mifos.application.bulkentry.business.BulkEntryInstallmentView;
import org.mifos.application.customer.business.CustomerScheduleEntity;
import org.mifos.framework.util.helpers.Money;

public class CustomerAccountPaymentData extends AccountPaymentData {

	private Money miscFeePaid;

	private Money miscPenaltyPaid;

	private Map<Short, Money> feesPaid;

	public Map<Short, Money> getFeesPaid() {
		return feesPaid;
	}

	private void setFeesPaid(Map<Short, Money> feesPaid) {
		this.feesPaid = feesPaid;
	}

	public Money getMiscFeePaid() {
		return miscFeePaid;
	}

	private void setMiscFeePaid(Money miscFeePaid) {
		this.miscFeePaid = miscFeePaid;
	}

	public Money getMiscPenaltyPaid() {
		return miscPenaltyPaid;
	}

	private void setMiscPenaltyPaid(Money miscPenaltyPaid) {
		this.miscPenaltyPaid = miscPenaltyPaid;
	}

	public CustomerAccountPaymentData(AccountActionDateEntity accountAction) {
		super(accountAction);
		CustomerScheduleEntity customerSchedule = (CustomerScheduleEntity) accountAction;
		Map<Short, Money> feesPaid = new HashMap<Short, Money>();
		setMiscFeePaid(customerSchedule.getMiscFee());
		setMiscPenaltyPaid(customerSchedule.getMiscPenalty());
		for (AccountFeesActionDetailEntity accountFees : customerSchedule
				.getAccountFeesActionDetails()) {
			feesPaid.put(accountFees.getFee().getFeeId(), accountFees
					.getFeeAmount());
		}
		setFeesPaid(feesPaid);
		setPaymentStatus(PaymentStatus.PAID.getValue());
	}

	public CustomerAccountPaymentData(
			BulkEntryInstallmentView bulkEntryAccountAction) {
		super(bulkEntryAccountAction);
		BulkEntryCustomerAccountInstallmentView installmentView = (BulkEntryCustomerAccountInstallmentView)bulkEntryAccountAction;
		Map<Short, Money> feesPaid = new HashMap<Short, Money>();
		setMiscFeePaid(installmentView.getMiscFee());
		setMiscPenaltyPaid(installmentView.getMiscPenalty());
		List<BulkEntryAccountFeeActionView> bulkEntryAccountFeeActionViews = installmentView
				.getBulkEntryAccountFeeActions();
		if (bulkEntryAccountFeeActionViews != null
				&& bulkEntryAccountFeeActionViews.size() > 0) {
			for (BulkEntryAccountFeeActionView accountFeesActionDetailEntity : bulkEntryAccountFeeActionViews) {
				if (accountFeesActionDetailEntity.getFeeAmount() != null
						&& accountFeesActionDetailEntity.getFeeAmount()
								.getAmountDoubleValue() != 0)
					feesPaid.put(accountFeesActionDetailEntity.getFee()
							.getFeeId(), accountFeesActionDetailEntity
							.getFeeAmount());
			}
		}
		setFeesPaid(feesPaid);
	}

	public Money getTotalPaidAmnt() {
		Money totalAmount = new Money();
		return totalAmount.add(getMiscFeePaid()).add(getMiscPenaltyPaid());
	}
}
