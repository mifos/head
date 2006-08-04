package org.mifos.application.accounts.util.helpers;


import java.util.Date;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.util.helpers.Money;

public class AccountHelper {

/*	public static AccountActionDateEntity createEmptyInstallment(Date date,CustomerBO customer,Short installmentId){
		AccountActionDateEntity actionDate = new AccountActionDateEntity();
		actionDate.setActionDate(new java.sql.Date(date.getTime()));
		actionDate.setCustomer(customer);
		actionDate.setPaymentStatus(YesNoFlag.NO.getValue());
		actionDate.setPrincipal(new Money());
		actionDate.setInterest(new Money());
		actionDate.setPenalty(new Money());
		actionDate.setMiscFee(new Money());
		actionDate.setMiscPenalty(new Money());
		actionDate.setDeposit(new Money());
		actionDate.setInstallmentId(installmentId);
		return actionDate;
	}
	*/
}
