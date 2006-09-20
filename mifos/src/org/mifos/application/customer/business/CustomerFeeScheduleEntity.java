package org.mifos.application.customer.business;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.framework.util.helpers.Money;

public class CustomerFeeScheduleEntity extends
		AccountFeesActionDetailEntity {

	public CustomerFeeScheduleEntity(
			AccountActionDateEntity accountActionDate,
			FeeBO fee, AccountFeesEntity accountFee, Money feeAmount) {
		super(accountActionDate, fee, accountFee, feeAmount);
	}

	protected CustomerFeeScheduleEntity() {
		super(null, null, null, null);
	}
}
