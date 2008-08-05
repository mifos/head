package org.mifos.application.accounts.loan.struts.action;

import java.util.ArrayList;

import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountDetailsViewHelper;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.Money;

public class GlimLoanUpdater {

	public void createIndividualLoan(LoanAccountActionForm loanAccountActionForm, LoanBO loan, boolean isRepaymentIndepOfMeetingEnabled, LoanAccountDetailsViewHelper loanAccountDetail) throws AccountException, ServiceException, PropertyNotFoundException {
			LoanBO individualLoan = LoanBO.createIndividualLoan(loan
					.getUserContext(), loan.getLoanOffering(),
					new CustomerBusinessService().getCustomer(Integer.valueOf(loanAccountDetail.getClientId())),
					loanAccountActionForm.getState(), new Money(loanAccountDetail
							.getLoanAmount().toString()), loan
							.getNoOfInstallments(), loan
							.getDisbursementDate(), false,
					isRepaymentIndepOfMeetingEnabled, loan
							.getInterestRate(), loan
							.getGracePeriodDuration(), loan.getFund(),
					new ArrayList<FeeView>(),
					new ArrayList<CustomFieldView>());

			individualLoan.setParentAccount(loan);

			if (null != loanAccountDetail.getBusinessActivity())
				individualLoan.setBusinessActivityId(Integer
						.valueOf(loanAccountDetail.getBusinessActivity()));

			individualLoan.save();
	}

	void updateIndividualLoan(final LoanAccountDetailsViewHelper loanAccountDetail, LoanBO individualLoan) throws AccountException {
		Double loanAmount = loanAccountDetail.getLoanAmount();
		Money loanMoney = new Money(
				!loanAmount.toString().equals("-") ? loanAmount
						.longValue()
						+ "" : "0");
		individualLoan.updateLoan(loanMoney, !"-".equals(loanAccountDetail
				.getBusinessActivity()) ? Integer
				.valueOf(loanAccountDetail.getBusinessActivity()) : 0);
	}

	public void delete(LoanBO loan) throws AccountException {
		try {
			new LoanPersistence().delete(loan);
		}
		catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}
}
