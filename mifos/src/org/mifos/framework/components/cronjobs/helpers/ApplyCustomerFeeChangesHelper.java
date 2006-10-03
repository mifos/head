package org.mifos.framework.components.cronjobs.helpers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.customer.business.CustomerFeeScheduleEntity;
import org.mifos.application.customer.business.CustomerScheduleEntity;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.fees.util.helpers.FeeChangeType;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.framework.components.cronjobs.MifosTask;
import org.mifos.framework.components.cronjobs.SchedulerConstants;
import org.mifos.framework.components.cronjobs.TaskHelper;
import org.mifos.framework.components.cronjobs.exceptions.CronJobException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;

public class ApplyCustomerFeeChangesHelper extends TaskHelper {

	public ApplyCustomerFeeChangesHelper(MifosTask mifosTask) {
		super(mifosTask);
	}

	@Override
	public void execute(long timeInMillis) throws CronJobException {
		List<String> errorList = new ArrayList<String>();
		List<FeeBO> fees = null;
		try {
			fees = new FeePersistence().getUpdatedFeesForCustomer();
		} catch (Exception e) {
			throw new CronJobException(e);
		}
		AccountPersistence accountPersistence = new AccountPersistence();
		if (fees != null && fees.size() > 0) {
			for (FeeBO fee : fees) {
				try {
					if (!fee.getFeeChangeType().equals(
							FeeChangeType.NOT_UPDATED)) {
						List<Integer> accounts = new CustomerPersistence()
								.getCustomerAccountsForFee(fee.getFeeId());
						if (accounts != null && accounts.size() > 0) {
							for (Integer accountId : accounts) {
								updateAccountFee(accountId, fee,
										accountPersistence);
							}
						}
					}
					fee.updateFeeChangeType(FeeChangeType.NOT_UPDATED);
					UserContext userContext = new UserContext();
					userContext.setId(Short.valueOf("1"));
					fee.setUserContext(userContext);
					fee.save();
					HibernateUtil.commitTransaction();
				} catch (Exception e) {
					HibernateUtil.rollbackTransaction();
					errorList.add(fee.getFeeName());
				}
			}
		}
		if (errorList.size() > 0)
			throw new CronJobException(SchedulerConstants.FAILURE, errorList);
	}

	private void updateAccountFee(Integer accountId, FeeBO feesBO,
			AccountPersistence accountPersistence) throws CronJobException {
		try {
			AccountBO accountBO = accountPersistence.getAccount(accountId);
			updateFee(accountBO.getAccountFees(feesBO.getFeeId()), feesBO,
					accountBO);
		} catch (PersistenceException e) {
			throw new CronJobException(e);
		}
	}

	private void updateFee(AccountFeesEntity fee, FeeBO feeBO,
			AccountBO accountBO) throws CronJobException {
		boolean feeApplied = isFeeAlreadyApplied(fee, feeBO, accountBO);
		if (!feeApplied) {
			// update this account fee
			try {
				if (feeBO.getFeeChangeType().equals(
						FeeChangeType.AMOUNT_AND_STATUS_UPDATED)) {
					if (!feeBO.isActive()) {
						accountBO.removeFees(feeBO.getFeeId(), Short
								.valueOf("1"));
						updateAccountFee(fee, feeBO);
					} else {
						// generate repayment schedule and enable fee
						updateAccountFee(fee, feeBO);
						fee.changeFeesStatus(FeeStatus.ACTIVE.getValue(),
								new Date(System.currentTimeMillis()));
						addTonextInstallment(accountBO, fee);
					}

				} else if (feeBO.getFeeChangeType().equals(
						FeeChangeType.STATUS_UPDATED)) {
					if (!feeBO.isActive()) {
						accountBO.removeFees(feeBO.getFeeId(), Short
								.valueOf("1"));

					} else {
						fee.changeFeesStatus(FeeStatus.ACTIVE.getValue(),
								new Date(System.currentTimeMillis()));
						addTonextInstallment(accountBO, fee);
					}

				} else if (feeBO.getFeeChangeType().equals(
						FeeChangeType.AMOUNT_UPDATED)) {
					updateAccountFee(fee, feeBO);
					updateNextInstallment(accountBO, fee);
				}
			} catch (PropertyNotFoundException e) {
				throw new CronJobException(e);
			} catch (AccountException e) {
				throw new CronJobException(e);
			}
		}
	}

	private boolean isFeeAlreadyApplied(AccountFeesEntity fee, FeeBO feeBO,
			AccountBO accountBO) {
		boolean feeApplied = false;
		if (feeBO.isOneTime()) {
			for (AccountActionDateEntity accountActionDateEntity : accountBO
					.getPastInstallments()) {
				CustomerScheduleEntity installment = (CustomerScheduleEntity) accountActionDateEntity;
				if (installment.getAccountFeesAction(fee.getAccountFeeId()) != null) {
					feeApplied = true;
					break;
				}
			}
		}
		return feeApplied;

	}

	private void updateAccountFee(AccountFeesEntity fee, FeeBO feeBO) {
		fee.changeFeesStatus(FeeStatus.INACTIVE.getValue(), new Date(System
				.currentTimeMillis()));
		fee.setFeeAmount(((AmountFeeBO) feeBO).getFeeAmount()
				.getAmountDoubleValue());
		fee.setAccountFeeAmount(((AmountFeeBO) feeBO).getFeeAmount());
	}

	private void updateNextInstallment(AccountBO accountBO,
			AccountFeesEntity fee) {
		CustomerScheduleEntity installment = (CustomerScheduleEntity) accountBO
				.getDetailsOfNextInstallment();
		AccountFeesActionDetailEntity accountFeesActionDetail = installment
				.getAccountFeesAction(fee.getAccountFeeId());
		if (accountFeesActionDetail != null) {
			accountFeesActionDetail.setFeeAmount(fee.getAccountFeeAmount());
			accountFeesActionDetail.setUpdatedBy(Short.valueOf("1"));
			accountFeesActionDetail.setUpdatedDate(new Date(System
					.currentTimeMillis()));
		}
	}

	private void addTonextInstallment(AccountBO accountBO, AccountFeesEntity fee)
			throws AccountException {
		CustomerScheduleEntity nextInstallment = (CustomerScheduleEntity) accountBO
				.getDetailsOfNextInstallment();
		AccountFeesActionDetailEntity accountFeesaction = new CustomerFeeScheduleEntity(
				nextInstallment, fee.getFees(), fee, fee.getAccountFeeAmount());
		accountFeesaction.setFeeAmountPaid(new Money("0.0"));
		nextInstallment.addAccountFeesAction(accountFeesaction);
		String description = fee.getFees().getFeeName() + " "
				+ AccountConstants.FEES_APPLIED;
		accountBO.updateAccountActivity(null, null, fee.getAccountFeeAmount(),
				null, Short.valueOf("1"), description);

	}
}
