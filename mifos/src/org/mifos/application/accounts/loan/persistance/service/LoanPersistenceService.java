package org.mifos.application.accounts.loan.persistance.service;

import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.LoanAccountView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.persistance.LoanPersistance;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.service.PersistenceService;

public class LoanPersistenceService extends PersistenceService {

	private LoanPersistance serviceImpl = new LoanPersistance();

	public List<LoanAccountView> getLoanAccountsForCustomer(Integer customerId,Date disbursementDate) {
		return serviceImpl.getLoanAccountsForCustomer(customerId,disbursementDate);
	}

	public List<AccountActionDateEntity> getTransactionDetailForLoanAccount(
			Integer accountId, Date transactionDate) {
		return serviceImpl.getLoanAccountTransactionDetail(accountId,
				transactionDate);
	}
	
	public List<PrdOfferingBO> getLoanOfferingBOForCustomer(String customerSearchId,Date disbursmentDate){
	   return serviceImpl.getLoanOfferingBOForCustomer(customerSearchId,disbursmentDate);
	}
	
	public Double getFeeAmountAtDisbursement(Integer accountId,Date date){
	   return serviceImpl.getFeeAmountAtDisbursement(accountId,date);
	}	  
    
	public LoanBO findBySystemId(String accountGlobalNum)throws PersistenceException {
		return serviceImpl.findBySystemId(accountGlobalNum);
	}
	public List<LoanBO> getLoanAccountsInArrears(Short latenessDays) throws PersistenceException{
		return serviceImpl.getLoanAccountsInArrears(latenessDays);
				
	}
	
}
