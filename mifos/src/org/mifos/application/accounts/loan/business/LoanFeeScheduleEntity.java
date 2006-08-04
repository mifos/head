package org.mifos.application.accounts.loan.business;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.framework.util.helpers.Money;

public class LoanFeeScheduleEntity extends AccountFeesActionDetailEntity {

	protected LoanFeeScheduleEntity() {
		super(null, null, null, null, null);
	}

	public LoanFeeScheduleEntity(
			AccountActionDateEntity accountActionDate, Short installmentId,
			FeeBO fee, AccountFeesEntity accountFee, Money feeAmount) {
		super(accountActionDate, installmentId, fee, accountFee, feeAmount);
	}

}
