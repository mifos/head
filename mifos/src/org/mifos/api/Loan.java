package org.mifos.api;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.BusinessServiceName;


public class Loan {
	
	private final LoanBO bo;
	
	Loan(LoanBO businessObject) throws ServiceException {
		bo = businessObject;
	}

	private static LoanBusinessService getBusinessService() {
		return (LoanBusinessService) 
			ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Loan);
	}
	
	public static Loan getLoan(Integer id) throws Exception { 
		// We may want to check exceptions in this one...
		LoanBO loanBo = getBusinessService().getAccount(id);
		if (loanBo == null) {
			throw new Exception("Loan " + id + " not found");
		}
		return new Loan(loanBo);
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
	
	// This is, in fact, a little more complicated.
	//public void writeOff(String reason) throws AccountException {
	//	bo.writeOff(reason);
	//}
}
