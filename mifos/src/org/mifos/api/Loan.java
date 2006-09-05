package org.mifos.api;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.BusinessServiceName;


public class Loan {
	protected static LoanBusinessService loanBusinessService = null;
	
	private LoanBO bo = null;
	
	Loan() throws ServiceException {
		if (loanBusinessService == null) {
			loanBusinessService = (LoanBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Loan);
		}
	}
	
	public static Loan getLoan(Integer id) throws Exception { // We may want to check exceptions in this one...
		Loan result = new Loan();
		result.bo = loanBusinessService.getAccount(id);
		return result;
	}
	
	public Integer getId() {
		return bo.getAccountId();
	}
	
	public String getBorrowerName() {
		return bo.getCustomer().getDisplayName();
	}
	
	public double getLoanBalance() {
		return bo.getLoanBalance().getAmountDoubleValue();
	}
	
	public String getLoanCurrency() {
		return bo.getTotalAmountDue().getCurrency().getCurrencyName();
	}
}
