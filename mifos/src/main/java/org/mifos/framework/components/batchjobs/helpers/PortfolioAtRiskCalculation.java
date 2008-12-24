package org.mifos.framework.components.batchjobs.helpers;

import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.framework.util.helpers.Money;

public class PortfolioAtRiskCalculation {
	
	// This method is used by the PortfolioAtRiskHelper to calculate the PAR for each group when the PAR task is running
	// and not intended to be used anywhere else. This method calculated the PAR assuming that loans are in arrears 
	// are already put in the state active in bad standing (the query will calculated for loans in active in bad standing only
	public static double generatePortfolioAtRiskForTask(Integer groupId, Short branchId, String searchId) throws Exception {
		double portfolioAtRisk = -1;
		CustomerPersistence customerPersistence = new CustomerPersistence();
		
		Money atRiskLoanAmount = customerPersistence.getTotalAmountForGroup(groupId, AccountState.LOAN_ACTIVE_IN_BAD_STANDING);
		Money goupLoanAmountForActiveLoans = 
			customerPersistence.getTotalAmountForGroup(groupId, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING);
		Money outstandingLoanAmount = atRiskLoanAmount.add(goupLoanAmountForActiveLoans); 
		
		Money clientAtRiskLoanAmount = customerPersistence.getTotalAmountForAllClientsOfGroup
					(branchId, AccountState.LOAN_ACTIVE_IN_BAD_STANDING, searchId);
		Money clientLoanAmountForActiveLoans = customerPersistence.getTotalAmountForAllClientsOfGroup
		(branchId, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, searchId);
		Money clientOutstandingLoanAmount =  clientAtRiskLoanAmount.add(clientLoanAmountForActiveLoans);
		
		
		outstandingLoanAmount = outstandingLoanAmount.add(clientOutstandingLoanAmount);
		atRiskLoanAmount = atRiskLoanAmount.add(clientAtRiskLoanAmount);
		
		if (!outstandingLoanAmount.equals(new Money()))
		{
			portfolioAtRisk = atRiskLoanAmount.divide(outstandingLoanAmount).getAmountDoubleValue();
		}
		else if (atRiskLoanAmount.equals(new Money()))
		{
			portfolioAtRisk = 0.0;
		}
		return portfolioAtRisk;
	}

}
