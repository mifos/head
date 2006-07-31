package org.mifos.application.accounts.loan.business.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.ViewInstallmentDetails;
import org.mifos.application.accounts.exceptions.AccountExceptionConstants;
import org.mifos.application.accounts.loan.business.LoanActivityEntity;
import org.mifos.application.accounts.loan.business.LoanActivityView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.persistance.service.LoanPersistenceService;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.PersistenceServiceName;

public class LoanBusinessService extends BusinessService {

	private LoanPersistenceService loanPersistenceService=null;

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}

	public LoanBO findBySystemId(String accountGlobalNum)throws SystemException,ApplicationException {
		LoanBO loanBO = null;
		try {
			loanPersistenceService=(LoanPersistenceService)ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.Loan);
			loanBO =  loanPersistenceService.findBySystemId(accountGlobalNum);
		} catch (PersistenceException e) {
			
			throw new ApplicationException(AccountExceptionConstants.FINDBYGLOBALACCNTEXCEPTION,e,new Object[]{accountGlobalNum});
			
		}
		return loanBO;
	}
	
	public List<LoanActivityView> getRecentActivityView(String globalAccountNumber,Short localeId) throws SystemException, ApplicationException {
		LoanBO loanBO = findBySystemId(globalAccountNumber);
		Set<LoanActivityEntity> loanAccountActivityDetails = loanBO.getLoanActivityDetails();
		List<LoanActivityView> recentActivityView = new ArrayList<LoanActivityView>();
		
		int count=0;
		for(LoanActivityEntity loanActivity : loanAccountActivityDetails) {
			recentActivityView.add(getLoanActivityView(loanActivity));
			if(++count == 3)
				break;
		}
		return recentActivityView;
	}
	
	public List<LoanActivityView> getAllActivityView(String globalAccountNumber,Short localeId) throws SystemException, ApplicationException {
		LoanBO loanBO = findBySystemId(globalAccountNumber);
		Set<LoanActivityEntity> loanAccountActivityDetails = loanBO.getLoanActivityDetails();
		List<LoanActivityView> loanActivityViewSet = new ArrayList<LoanActivityView>();
		for(LoanActivityEntity loanActivity:loanAccountActivityDetails) {
			loanActivityViewSet.add(getLoanActivityView(loanActivity));
		}
		return loanActivityViewSet;
	}
	
	private LoanActivityView getLoanActivityView(LoanActivityEntity loanActivity) {
		LoanActivityView loanActivityView = new LoanActivityView();
		loanActivityView.setId(loanActivity.getAccount().getAccountId());
		loanActivityView.setActionDate(loanActivity.getTrxnCreatedDate());
		loanActivityView.setActivity(loanActivity.getComments());
		loanActivityView.setPrincipal(removeSign(loanActivity.getPrincipal()));
		loanActivityView.setInterest(removeSign(loanActivity.getInterest()));
		loanActivityView.setPenalty(removeSign(loanActivity.getPenalty()));
		loanActivityView.setFees(removeSign(loanActivity.getFee()));
		loanActivityView.setTotal(removeSign(loanActivity.getFee()).add(removeSign(loanActivity.getPenalty()))
				.add(removeSign(loanActivity.getPrincipal())).add(removeSign(loanActivity.getInterest())));
		loanActivityView.setTimeStamp(loanActivity.getTrxnCreatedDate());
		loanActivityView.setRunningBalanceInterest(loanActivity.getInterestOutstanding());
		loanActivityView.setRunningBalancePrinciple(loanActivity.getPrincipalOutstanding());
		loanActivityView.setRunningBalanceFees(loanActivity.getFeeOutstanding());
		loanActivityView.setRunningBalancePenalty(loanActivity.getPenaltyOutstanding());
		
		return loanActivityView;
	}
	
	private Money removeSign(Money amount){
		if(amount!=null && amount.getAmountDoubleValue()<0)
			return amount.negate();
		else
			return amount;
	}
	
	public LoanBO getAccount(Integer accountId) throws Exception{			
		loanPersistenceService=(LoanPersistenceService)ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.Loan);	
		return loanPersistenceService.getAccount(accountId);

	}
	
	public ViewInstallmentDetails getUpcomingInstallmentDetails(AccountActionDateEntity upcomingInstallment){
		if(upcomingInstallment != null)
		return new ViewInstallmentDetails(upcomingInstallment.getPrincipal(),
				upcomingInstallment.getInterest(), upcomingInstallment
						.getTotalFees(), upcomingInstallment.getTotalPenalty());
		return null;
	}
	
	public ViewInstallmentDetails getOverDueInstallmentDetails(List<AccountActionDateEntity> overDueInstallmentList){		
		Money principalDue = new Money();
		Money interestDue = new Money();
		Money feesDue = new Money();
		Money penaltyDue = new Money();		
		for(AccountActionDateEntity installment : overDueInstallmentList){
			principalDue = principalDue.add(installment.getPrincipalDue());			
			interestDue = interestDue.add(installment.getInterestDue());
			feesDue = feesDue.add(installment.getTotalFees());
			penaltyDue = penaltyDue.add(installment.getPenaltyDue());			
		}
		return new ViewInstallmentDetails(principalDue, interestDue, feesDue, penaltyDue);
	}
	
}
