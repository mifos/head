package org.mifos.application.accounts.savings.business;

import java.sql.Date;
import java.sql.Timestamp;

import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.savings.util.helpers.SavingsHelper;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.master.persistence.service.MasterPersistenceService;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.PersistenceServiceName;

public class SavingsTrxnDetailEntity extends AccountTrxnEntity {
	private Money depositAmount;

	private Money withdrawlAmount;

	private Money interestAmount;

	private Money balance;

	public Money getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(Money depositAmount) {
		this.depositAmount = depositAmount;
	}

	public Money getWithdrawlAmount() {
		return withdrawlAmount;
	}

	public void setWithdrawlAmount(Money withdrawlAmount) {
		this.withdrawlAmount = withdrawlAmount;
	}

	public Money getBalance() {
		return balance;
	}

	public void setBalance(Money balance) {
		this.balance = balance;
	}

	public Money getInterestAmount() {
		return interestAmount;
	}

	public void setInterestAmount(Money interestAmount) {
		this.interestAmount = interestAmount;
	}

	public void setTrxnDetails(Short action, Money amount, Money balance,
			CustomerBO customer, PersonnelBO createdBy) throws SystemException {
		this.setAccountActionEntity(getAccountDBService().getAccountAction(
				action));
		this.setAmount(amount);
		this.setCustomer(customer);
		this.setPersonnel(createdBy);
		this.setCreatedDate(new SavingsHelper().getCurrentDate());
		this.setActionDate(this.getCreatedDate());
		this.setBalance(balance);
		if (action.equals(AccountConstants.ACTION_SAVINGS_WITHDRAWAL))
			this.setWithdrawlAmount(amount);
		else if (action.equals(AccountConstants.ACTION_SAVINGS_DEPOSIT))
			this.setDepositAmount(amount);
		else if (action
				.equals(AccountConstants.ACTION_SAVINGS_INTEREST_POSTING))
			this.setInterestAmount(amount);
		setTrxnCreatedDate(new Timestamp(System.currentTimeMillis()));
	}

	public void setTrxnDetails(Short action, Money amount, Money balance,
			CustomerBO customer, PersonnelBO createdBy, java.util.Date dueDate,
			java.util.Date transactionDate) throws SystemException {
		setTrxnDetails(action, amount, balance, customer, createdBy);
		setActionDate(transactionDate);
		setDueDate(dueDate);
	}

	private AccountPersistanceService getAccountDBService()
			throws ServiceException {
		return (AccountPersistanceService) ServiceFactory.getInstance()
				.getPersistenceService(PersistenceServiceName.Account);
	}

	public void setPaymentDetails(Money depositAmount, Date actionDate,
			CustomerBO customer, PersonnelBO personnel,
			java.util.Date transactionDate) throws ServiceException {
		MasterPersistenceService masterPersistenceService = (MasterPersistenceService) ServiceFactory
				.getInstance().getPersistenceService(
						PersistenceServiceName.MasterDataService);
		setActionDate(transactionDate);
		setDueDate(actionDate);
		setPersonnel(personnel);
		setAccountActionEntity((AccountActionEntity) masterPersistenceService
				.findById(AccountActionEntity.class,
						AccountConstants.ACTION_SAVINGS_DEPOSIT));
		setCustomer(customer);
		setTrxnCreatedDate(new Timestamp(System.currentTimeMillis()));

		balance = ((SavingsBO) getAccount()).getSavingsBalance();
		this.depositAmount = depositAmount;
		setAmount(depositAmount);
	}

	public void setWithdrawalDetails(Money amount, Date actionDate,
			CustomerBO customer, PersonnelBO personnel,
			java.util.Date transactionDate) throws ServiceException {
		MasterPersistenceService masterPersistenceService = (MasterPersistenceService) ServiceFactory
				.getInstance().getPersistenceService(
						PersistenceServiceName.MasterDataService);
		setActionDate(transactionDate);
		setDueDate(actionDate);
		setPersonnel(personnel);
		setAccountActionEntity((AccountActionEntity) masterPersistenceService
				.findById(AccountActionEntity.class,
						AccountConstants.ACTION_SAVINGS_WITHDRAWAL));
		setCustomer(customer);
		setTrxnCreatedDate(new Timestamp(System.currentTimeMillis()));
		setWithdrawlAmount(amount);
		balance = ((SavingsBO) getAccount()).getSavingsBalance();
		setAmount(amount);
	}

	public AccountTrxnEntity generateReverseTrxn(String adjustmentComment)
			throws ApplicationException, SystemException {
		SavingsTrxnDetailEntity reverseAccntTrxn = new SavingsTrxnDetailEntity();
		Money balAfterAdjust = null;
		if (getAccountActionEntity().getId().equals(
				AccountConstants.ACTION_SAVINGS_DEPOSIT)) {
			balAfterAdjust = getBalance().subtract(getDepositAmount());
			reverseAccntTrxn.setDepositAmount(getDepositAmount().negate());
		} else if (getAccountActionEntity().getId().equals(
				AccountConstants.ACTION_SAVINGS_WITHDRAWAL)) {
			balAfterAdjust = getBalance().add(getWithdrawlAmount());
			reverseAccntTrxn.setWithdrawlAmount(getWithdrawlAmount().negate());
		}
		reverseAccntTrxn.setTrxnDetails(
				AccountConstants.ACTION_SAVINGS_ADJUSTMENT, getAmount()
						.negate(), balAfterAdjust, getCustomer(),
				getPersonnel(), getDueDate(), getActionDate());
		reverseAccntTrxn.setComments(adjustmentComment);
		reverseAccntTrxn.setRelatedTrxn(this);
		return reverseAccntTrxn;
	}
}
