package org.mifos.framework.components.cronjobs.helpers;

import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.fees.business.FeesBO;
import org.mifos.application.fees.persistence.service.FeePersistenceService;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.fees.util.helpers.FeeUpdateTypes;
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
			List fees = new FeePersistenceService().getUpdatedFeesForCustomer();
			AccountPersistanceService accountPersistanceService = new AccountPersistanceService();
			if (fees != null && fees.size() > 0) {
				HibernateUtil.startTransaction();
				for (Object fee : fees) {
					FeesBO feesBO = (FeesBO) fee;
					if (feesBO.getFeeUpdateType().getId().intValue() == 1) {
						List<Integer> accounts = accountPersistanceService
								.getCustomerAccountsForFee(feesBO.getFeeId());
						if (accounts != null && accounts.size() > 0) {
							for (Integer accountId : accounts) {
								updateAccountFee(accountId, feesBO,
										accountPersistanceService);

							}
						}
					}
					feesBO.setUpdateFlag(Short.valueOf("0"));
					UserContext userContext = new UserContext();
					userContext.setId(Short.valueOf("1"));
					feesBO.setUserContext(userContext);
					feesBO.save(false);
					HibernateUtil.commitTransaction();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateAccountFee(Integer accountId, FeesBO feesBO,
			AccountPersistanceService accountPersistanceService)
			throws NumberFormatException, SystemException, ApplicationException {
		AccountBO accountBO = accountPersistanceService.getAccount(accountId);
		updateFee(accountBO.getAccountFees(feesBO.getFeeId()), feesBO,
				accountBO);

	}

	private void updateFee(AccountFeesEntity fee, FeesBO feesBO,
			AccountBO accountBO) throws NumberFormatException, SystemException,
			ApplicationException {

		Short updateType = feesBO.getUpdateFlag().shortValue();
		boolean feeApplied = isFeeAlreadyApplied(fee, feesBO, accountBO);
		if (!feeApplied) {
			// update this account fee

			if (updateType == FeeUpdateTypes.AMOUNT_AND_STATUS_UPDATED
					.getValue()) {
				if (feesBO.getFeeStatus().getStatusId().shortValue() == FeeStatus.INACTIVE
						.getValue().shortValue()) {
					accountBO.removeFees(feesBO.getFeeId(), Short.valueOf("1"));
					updateAccountFee(fee, feesBO);

				} else {
					// generate repayment schedule and enable fee
					updateAccountFee(fee, feesBO);
					fee.changeFeesStatus(FeeStatus.ACTIVE.getValue(), new Date(System
							.currentTimeMillis()));
					AddTonextInstallment(accountBO, fee);

				}

			} else if (updateType == FeeUpdateTypes.STATUS_UPDATED.getValue()) {
				if (feesBO.getFeeStatus().getStatusId().shortValue() == FeeStatus.INACTIVE
						.getValue().shortValue()) {
					accountBO.removeFees(feesBO.getFeeId(), Short.valueOf("1"));

				} else {
					fee.changeFeesStatus(FeeStatus.ACTIVE.getValue(), new Date(System
				.currentTimeMillis()));
					AddTonextInstallment(accountBO, fee);
				}

			} else if (updateType == FeeUpdateTypes.AMOUNT_UPDATED.getValue()) {

				updateAccountFee(fee, feesBO);
				updateNextInstallment(accountBO, fee);
			}
		}
	}

	private boolean isFeeAlreadyApplied(AccountFeesEntity fee, FeesBO feesBO,
			AccountBO accountBO) {
		boolean feeApplied = false;
		if (feesBO.getFeeFrequency().getFeeFrequencyType()
				.getFeeFrequencyTypeId().shortValue() == FeeFrequencyType.ONETIME
				.getValue().shortValue()) {
			for (AccountActionDateEntity installment : accountBO
					.getPastInstallments()) {
				if (installment.getAccountFeesAction(fee.getAccountFeeId()) != null) {
					feeApplied = true;
					break;
				}
			}
		}
		return feeApplied;

	}

	private void updateAccountFee(AccountFeesEntity fee, FeesBO feesBO) {
		fee.changeFeesStatus(FeeStatus.INACTIVE.getValue(), new Date(System
				.currentTimeMillis()));
		fee.setFeeAmount(feesBO.getFeeAmount());
		fee.setAccountFeeAmount(feesBO.getFeeAmount());
	}

	private void updateNextInstallment(AccountBO accountBO,
			AccountFeesEntity fee) {

		AccountFeesActionDetailEntity accountFeesActionDetail = accountBO
				.getDetailsOfNextInstallment().getAccountFeesAction(
						fee.getAccountFeeId());
		if (accountFeesActionDetail != null) {
			accountFeesActionDetail.setFeeAmount(fee.getAccountFeeAmount());
			accountFeesActionDetail.setUpdatedBy(Short.valueOf("1"));
			accountFeesActionDetail.setUpdatedDate(new Date(System
					.currentTimeMillis()));
		}

	}
	
	private void AddTonextInstallment(AccountBO accountBO,
			AccountFeesEntity fee)
	{
		AccountActionDateEntity nextInstallment = accountBO.getDetailsOfNextInstallment();
		AccountFeesActionDetailEntity accountFeesaction = new AccountFeesActionDetailEntity();
		accountFeesaction.setAccountFee(fee);
		accountFeesaction.setAccountActionDate(nextInstallment);
		accountFeesaction.setFee(fee.getFees());
		accountFeesaction.setFeeAmount(fee.getAccountFeeAmount());
		accountFeesaction.setFeeAmountPaid(new Money("0.0"));
		accountFeesaction.setInstallmentId(nextInstallment.getInstallmentId());
		nextInstallment.addAccountFeesAction(accountFeesaction);
		String description = fee.getFees().getFeeName()+ " " + AccountConstants.FEES_APPLIED;
		accountBO.updateAccountActivity(fee.getAccountFeeAmount(),Short.valueOf("1"),description);

	}
}
