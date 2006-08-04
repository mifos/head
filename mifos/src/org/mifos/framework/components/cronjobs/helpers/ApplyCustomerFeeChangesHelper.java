package org.mifos.framework.components.cronjobs.helpers;

import java.util.Date;
import java.util.List;

import org.hibernate.PropertyNotFoundException;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.customer.business.CustomerFeeScheduleEntity;
import org.mifos.application.customer.business.CustomerScheduleEntity;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.fees.util.helpers.FeeChangeType;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.framework.components.cronjobs.TaskHelper;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;

public class ApplyCustomerFeeChangesHelper extends TaskHelper {

	@Override
	public void execute(long timeInMillis) {
		try {
			List fees = new FeePersistence().getUpdatedFeesForCustomer();
			AccountPersistanceService accountPersistanceService = new AccountPersistanceService();
			if (fees != null && fees.size() > 0) {
				HibernateUtil.startTransaction();
				for (Object fee : fees) {
					FeeBO feeBO = (FeeBO) fee;
					if (!feeBO.getFeeChangeType().equals(
							FeeChangeType.NOT_UPDATED)) {
						List<Integer> accounts = accountPersistanceService
								.getCustomerAccountsForFee(feeBO.getFeeId());
						if (accounts != null && accounts.size() > 0) {
							for (Integer accountId : accounts) {
								updateAccountFee(accountId, feeBO,
										accountPersistanceService);

							}
						}
					}
					feeBO.updateFeeChangeType(FeeChangeType.NOT_UPDATED);
					UserContext userContext = new UserContext();
					userContext.setId(Short.valueOf("1"));
					feeBO.setUserContext(userContext);
					feeBO.save();
					HibernateUtil.commitTransaction();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateAccountFee(Integer accountId, FeeBO feesBO,
			AccountPersistanceService accountPersistanceService)
			throws NumberFormatException, SystemException, ApplicationException {
		AccountBO accountBO = accountPersistanceService.getAccount(accountId);
		updateFee(accountBO.getAccountFees(feesBO.getFeeId()), feesBO,
				accountBO);

	}

	private void updateFee(AccountFeesEntity fee, FeeBO feeBO,
			AccountBO accountBO) throws NumberFormatException, SystemException,
			ApplicationException, PropertyNotFoundException {

		boolean feeApplied = isFeeAlreadyApplied(fee, feeBO, accountBO);
		if (!feeApplied) {
			// update this account fee

			if (feeBO.getFeeChangeType().equals(
					FeeChangeType.AMOUNT_AND_STATUS_UPDATED)) {
				if (!feeBO.isActive()) {
					accountBO.removeFees(feeBO.getFeeId(), Short.valueOf("1"));
					updateAccountFee(fee, feeBO);

				} else {
					// generate repayment schedule and enable fee
					updateAccountFee(fee, feeBO);
					fee.changeFeesStatus(FeeStatus.ACTIVE.getValue(), new Date(
							System.currentTimeMillis()));
					AddTonextInstallment(accountBO, fee);

				}

			} else if (feeBO.getFeeChangeType().equals(
					FeeChangeType.STATUS_UPDATED)) {
				if (!feeBO.isActive()) {
					accountBO.removeFees(feeBO.getFeeId(), Short.valueOf("1"));

				} else {
					fee.changeFeesStatus(FeeStatus.ACTIVE.getValue(), new Date(
							System.currentTimeMillis()));
					AddTonextInstallment(accountBO, fee);
				}

			} else if (feeBO.getFeeChangeType().equals(
					FeeChangeType.AMOUNT_UPDATED)) {
				updateAccountFee(fee, feeBO);
				updateNextInstallment(accountBO, fee);
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
		fee.setFeeAmount(((AmountFeeBO) feeBO).getFeeAmount());
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

	private void AddTonextInstallment(AccountBO accountBO, AccountFeesEntity fee) {
		CustomerScheduleEntity nextInstallment = (CustomerScheduleEntity) accountBO
				.getDetailsOfNextInstallment();
		AccountFeesActionDetailEntity accountFeesaction = new CustomerFeeScheduleEntity(
				nextInstallment, nextInstallment.getInstallmentId(), fee
						.getFees(), fee, fee.getAccountFeeAmount());
		accountFeesaction.setFeeAmountPaid(new Money("0.0"));
		nextInstallment.addAccountFeesAction(accountFeesaction);
		String description = fee.getFees().getFeeName() + " "
				+ AccountConstants.FEES_APPLIED;
		accountBO.updateAccountActivity(fee.getAccountFeeAmount(), Short
				.valueOf("1"), description);

	}
}
