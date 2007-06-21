package org.mifos.application.accounts.loan.business.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.loan.business.LoanActivityEntity;
import org.mifos.application.accounts.loan.business.LoanActivityView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;

public class LoanBusinessService extends BusinessService {

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}

	public LoanBO findBySystemId(String accountGlobalNum)
			throws ServiceException {
		try {
			return new LoanPersistence().findBySystemId(accountGlobalNum);
		} catch (PersistenceException e) {
			throw new ServiceException(
					AccountExceptionConstants.FINDBYGLOBALACCNTEXCEPTION, e,
					new Object[] { accountGlobalNum });
		}
	}

	public List<LoanActivityView> getRecentActivityView(
			String globalAccountNumber, Short localeId) throws ServiceException {
		LoanBO loanBO = findBySystemId(globalAccountNumber);
		Set<LoanActivityEntity> loanAccountActivityDetails = loanBO
				.getLoanActivityDetails();
		List<LoanActivityView> recentActivityView = new ArrayList<LoanActivityView>();

		int count = 0;
		for (LoanActivityEntity loanActivity : loanAccountActivityDetails) {
			recentActivityView.add(getLoanActivityView(loanActivity));
			if (++count == 3)
				break;
		}
		return recentActivityView;
	}

	public List<LoanActivityView> getAllActivityView(
			String globalAccountNumber, Short localeId) throws ServiceException {
		LoanBO loanBO = findBySystemId(globalAccountNumber);
		Set<LoanActivityEntity> loanAccountActivityDetails = loanBO
				.getLoanActivityDetails();
		List<LoanActivityView> loanActivityViewSet = new ArrayList<LoanActivityView>();
		for (LoanActivityEntity loanActivity : loanAccountActivityDetails) {
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
		loanActivityView.setTotal(removeSign(loanActivity.getFee()).add(
				removeSign(loanActivity.getPenalty())).add(
				removeSign(loanActivity.getPrincipal())).add(
				removeSign(loanActivity.getInterest())));
		loanActivityView.setTimeStamp(loanActivity.getTrxnCreatedDate());
		loanActivityView.setRunningBalanceInterest(loanActivity
				.getInterestOutstanding());
		loanActivityView.setRunningBalancePrinciple(loanActivity
				.getPrincipalOutstanding());
		loanActivityView
				.setRunningBalanceFees(loanActivity.getFeeOutstanding());
		loanActivityView.setRunningBalancePenalty(loanActivity
				.getPenaltyOutstanding());

		return loanActivityView;
	}

	private Money removeSign(Money amount) {
		if (amount != null && amount.getAmountDoubleValue() < 0)
			return amount.negate();
		else
			return amount;
	}

	public LoanBO getAccount(Integer accountId) throws ServiceException {
		try {
			return new LoanPersistence().getAccount(accountId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	public  List<LoanBO> getLoanAccountsActiveInGoodBadStanding(
			Integer customerId) throws ServiceException {
		try {
			return new LoanPersistence().getLoanAccountsActiveInGoodBadStanding(customerId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	
	public Short getLastPaymentAction(Integer accountId)
			throws ServiceException {
		try {
			return new LoanPersistence().getLastPaymentAction(accountId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<LoanBO> getSearchResults(String officeId, String personnelId,
			String type, String currentStatus) throws ServiceException {
		try {
			return new LoanPersistence().getSearchResults(officeId,
					personnelId, type, currentStatus);
		} catch (PersistenceException he) {
			throw new ServiceException(he);
		}
	}
	
	public void initialize(Object object) {
		new LoanPersistence().initialize(object);
	}
}
