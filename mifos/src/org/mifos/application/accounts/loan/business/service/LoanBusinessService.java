package org.mifos.application.accounts.loan.business.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.ViewInstallmentDetails;
import org.mifos.application.accounts.exceptions.AccountExceptionConstants;
import org.mifos.application.accounts.loan.business.LoanActivityEntity;
import org.mifos.application.accounts.loan.business.LoanActivityView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.loan.persistance.LoanPersistance;
import org.mifos.application.customer.business.CustomerLevelEntity;
import org.mifos.application.master.business.BusinessActivityEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.PersistenceServiceName;

public class LoanBusinessService extends BusinessService {

	private LoanPersistance loanPersistence=null;

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}

	public LoanBO findBySystemId(String accountGlobalNum) throws ServiceException {
		LoanBO loanBO = null;
		try {
			loanPersistence=new LoanPersistance();
			loanBO =  loanPersistence.findBySystemId(accountGlobalNum);
		} catch (PersistenceException e) {
			throw new ServiceException(AccountExceptionConstants.FINDBYGLOBALACCNTEXCEPTION,e,new Object[]{accountGlobalNum});
		}
		return loanBO;
	}
	
	public List<LoanActivityView> getRecentActivityView(String globalAccountNumber,Short localeId) throws ServiceException {
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
	
	public LoanBO getAccount(Integer accountId) throws ServiceException {			
		loanPersistence=new LoanPersistance();	
		try {
			return loanPersistence.getAccount(accountId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	
	public ViewInstallmentDetails getUpcomingInstallmentDetails(AccountActionDateEntity upcomingAccountActionDate){
		if(upcomingAccountActionDate != null) {
			LoanScheduleEntity upcomingInstallment = (LoanScheduleEntity)upcomingAccountActionDate; 
		return new ViewInstallmentDetails(upcomingInstallment.getPrincipalDue(),
				upcomingInstallment.getInterestDue(), upcomingInstallment
						.getTotalFeeDueWithMiscFeeDue(), upcomingInstallment.getPenaltyDue());
		}
		return null;
	}
	
	public ViewInstallmentDetails getOverDueInstallmentDetails(List<AccountActionDateEntity> overDueInstallmentList){
		Money principalDue = new Money();
		Money interestDue = new Money();
		Money feesDue = new Money();
		Money penaltyDue = new Money();		
		for(AccountActionDateEntity accountActionDate : overDueInstallmentList){
			LoanScheduleEntity installment = (LoanScheduleEntity)accountActionDate;
			principalDue = principalDue.add(installment.getPrincipalDue());			
			interestDue = interestDue.add(installment.getInterestDue());
			feesDue = feesDue.add(installment.getTotalFees());
			penaltyDue = penaltyDue.add(installment.getPenaltyDue());			
		}
		return new ViewInstallmentDetails(principalDue, interestDue, feesDue, penaltyDue);
	}
	
	public Short getLastPaymentAction(Integer accountId) throws ServiceException{
		try {
			return new LoanPersistance().getLastPaymentAction(accountId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	
	public List<LoanOfferingBO> getApplicablePrdOfferings(
			CustomerLevelEntity customerLevel) throws ServiceException {
		try {
			return new LoanPersistance()
					.getApplicablePrdOfferings(customerLevel);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	
	public LoanOfferingBO getLoanOffering(Short loanOfferingId,Short localeId) throws ServiceException {
		try {
			return new LoanPersistance().getLoanOffering(loanOfferingId,localeId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	
	public List<BusinessActivityEntity> retrieveMasterEntities(String entityName, Short localeId) throws ServiceException  {
		try {
			return new MasterPersistence().retrieveMasterEntities(entityName,localeId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	
	public List<LoanBO> getSearchResults(String officeId,
			 String personnelId,
			 String type,
			 String currentStatus) throws ServiceException{
		try{
			LoanPersistance serviceImpl = new LoanPersistance();
			return serviceImpl.getSearchResults(officeId, personnelId, type, currentStatus);
		}catch(PersistenceException he){
			throw new ServiceException(he);
		}
	
	}
}
