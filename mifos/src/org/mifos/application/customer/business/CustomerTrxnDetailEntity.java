package org.mifos.application.customer.business;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.CustomerAccountPaymentData;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.Money;

public class CustomerTrxnDetailEntity extends AccountTrxnEntity {

	private final Set<FeesTrxnDetailEntity> feesTrxnDetails;

	private final Money totalAmount;

	private final Money miscPenaltyAmount;

	private final Money miscFeeAmount;

	protected CustomerTrxnDetailEntity() {
		totalAmount = null;
		miscPenaltyAmount = null;
		miscFeeAmount = null;
		feesTrxnDetails = new HashSet<FeesTrxnDetailEntity>();
	}

	public CustomerTrxnDetailEntity(AccountPaymentEntity accountPaymentEntity,
			CustomerAccountPaymentData customerAccountPaymentDataView,
			PersonnelBO personnel, java.util.Date transactionDate,
			AccountActionEntity accountActionEntity, String comments) {

		super(accountPaymentEntity, accountActionEntity,
				customerAccountPaymentDataView.getInstallmentId(),
				customerAccountPaymentDataView.getAccountActionDate()
						.getActionDate(), personnel, null, transactionDate,
				customerAccountPaymentDataView.getTotalPaidAmnt(), comments,
				null);
		totalAmount = customerAccountPaymentDataView.getTotalPaidAmnt();
		miscFeeAmount = customerAccountPaymentDataView.getMiscFeePaid();
		miscPenaltyAmount = customerAccountPaymentDataView.getMiscPenaltyPaid();
		feesTrxnDetails = new HashSet<FeesTrxnDetailEntity>();
		CustomerScheduleEntity customerScheduleEntity = (CustomerScheduleEntity) customerAccountPaymentDataView
				.getAccountActionDate();
		for (AccountFeesActionDetailEntity accountFeesActionDetail : customerScheduleEntity
				.getAccountFeesActionDetails()) {
			if (customerAccountPaymentDataView.getFeesPaid().containsKey(
					accountFeesActionDetail.getFee().getFeeId())) {
				((CustomerFeeScheduleEntity)accountFeesActionDetail)
						.makePayment(customerAccountPaymentDataView
								.getFeesPaid().get(
										accountFeesActionDetail.getFee()
												.getFeeId()));
				FeesTrxnDetailEntity feesTrxnDetailBO = new FeesTrxnDetailEntity(
						this, accountFeesActionDetail.getAccountFee(),
						accountFeesActionDetail.getFeeAmount());
				addFeesTrxnDetail(feesTrxnDetailBO);
			}
		}
	}

	public CustomerTrxnDetailEntity(AccountPaymentEntity accountPayment,
			AccountActionEntity accountActionEntity, Short installmentId,
			Date dueDate, PersonnelBO personnel, Date actionDate, Money amount,
			String comments, AccountTrxnEntity relatedTrxn,
			Money miscFeeAmount, Money miscPenaltyAmount) {
		super(accountPayment, accountActionEntity, installmentId, dueDate,
				personnel, null, actionDate, amount, comments, relatedTrxn);
		this.miscFeeAmount = miscFeeAmount;
		this.miscPenaltyAmount = miscPenaltyAmount;
		this.totalAmount = amount;
		feesTrxnDetails = new HashSet<FeesTrxnDetailEntity>();
	}

	public Money getTotalAmount() {
		return totalAmount;
	}

	public Money getMiscFeeAmount() {
		return miscFeeAmount;
	}

	public Money getMiscPenaltyAmount() {
		return miscPenaltyAmount;
	}

	public Set<FeesTrxnDetailEntity> getFeesTrxnDetails() {
		return feesTrxnDetails;
	}

	void addFeesTrxnDetail(FeesTrxnDetailEntity feesTrxn) {
		feesTrxnDetails.add(feesTrxn);
	}

	public FeesTrxnDetailEntity getFeesTrxn(Integer accountFeeId) {
		if (null != getFeesTrxnDetails() && feesTrxnDetails.size() > 0) {
			for (FeesTrxnDetailEntity feesTrxn : feesTrxnDetails) {

				if (feesTrxn.getAccountFees().getAccountFeeId().equals(
						accountFeeId)) {
					return feesTrxn;
				}
			}
		}
		return null;
	}

	@Override
	protected AccountTrxnEntity generateReverseTrxn(String adjustmentComment)
			throws AccountException {
		MasterPersistence masterPersistence = new MasterPersistence();
		MifosLogManager
				.getLogger(LoggerConstants.ACCOUNTSLOGGER)
				.debug(
						"Inside generate reverse transaction method of loan trxn detail");
		String comment = null;
		if (null == adjustmentComment)
			comment = getComments();
		else
			comment = adjustmentComment;

		CustomerTrxnDetailEntity reverseAccntTrxn;
		try {
			reverseAccntTrxn = new CustomerTrxnDetailEntity(
					getAccountPayment(),
					(AccountActionEntity) masterPersistence
							.getPersistentObject(AccountActionEntity.class,
									AccountConstants.ACTION_CUSTOMER_ADJUSTMENT),
					getInstallmentId(), getDueDate(), getPersonnel(),
					getActionDate(), getAmount().negate(), comment, this,
					getMiscFeeAmount().negate(), getMiscPenaltyAmount()
							.negate());
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}

		if (null != getFeesTrxnDetails() && getFeesTrxnDetails().size() > 0) {
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"Before generating reverse entries for fees");
			for (FeesTrxnDetailEntity feeTrxnDetail : getFeesTrxnDetails()) {
				reverseAccntTrxn.addFeesTrxnDetail(feeTrxnDetail
						.generateReverseTrxn(reverseAccntTrxn));
			}
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"after generating reverse entries for fees");
		}
		return reverseAccntTrxn;
	}

}
