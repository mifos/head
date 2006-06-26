package org.mifos.application.customer.business;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.CustomerAccountPaymentData;
import org.mifos.application.master.persistence.service.MasterPersistenceService;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.service.PersonnelPersistenceService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.PersistenceServiceName;

public class CustomerTrxnDetailEntity extends AccountTrxnEntity {
	public CustomerTrxnDetailEntity() {
	}

	private Set<FeesTrxnDetailEntity> feesTrxnDetails = new HashSet<FeesTrxnDetailEntity>();

	private Money totalAmount;

	private Money miscPenaltyAmount;

	private Money miscFeeAmount;

	public Money getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Money totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Money getMiscFeeAmount() {
		return miscFeeAmount;
	}

	public void setMiscFeeAmount(Money miscFeeAmount) {
		this.miscFeeAmount = miscFeeAmount;
	}

	public Money getMiscPenaltyAmount() {
		return miscPenaltyAmount;
	}

	public void setMiscPenaltyAmount(Money miscPenaltyAmount) {
		this.miscPenaltyAmount = miscPenaltyAmount;
	}

	public Set<FeesTrxnDetailEntity> getFeesTrxnDetails() {
		return feesTrxnDetails;
	}

	private void setFeesTrxnDetails(Set<FeesTrxnDetailEntity> feesTrxnDetails) {
		this.feesTrxnDetails = feesTrxnDetails;
	}

	public void addFeesTrxnDetail(FeesTrxnDetailEntity feesTrxn) {
		feesTrxn.setAccountTrxn(this);
		feesTrxnDetails.add(feesTrxn);
	}

	public void setPaymentDetails(AccountActionDateEntity accountAction,
			CustomerAccountPaymentData customerAccountPaymentDataView,
			Short personnelId, java.util.Date transactionDate)
			throws ServiceException {
		MasterPersistenceService masterPersistenceService = (MasterPersistenceService) ServiceFactory
				.getInstance().getPersistenceService(
						PersistenceServiceName.MasterDataService);
		PersonnelBO personnel = new PersonnelPersistenceService()
				.getPersonnel(personnelId);

		setActionDate(transactionDate);
		setDueDate(accountAction.getActionDate());
		setPersonnel(personnel);
		setAccountActionEntity((AccountActionEntity) masterPersistenceService
				.findById(AccountActionEntity.class,
						AccountConstants.ACTION_CUSTOMER_ACCOUNT_REPAYMENT));
		setCustomer(accountAction.getCustomer());
		setComments("Payment rcvd.");
		setTrxnCreatedDate(new Timestamp(System.currentTimeMillis()));

		setInstallmentId(customerAccountPaymentDataView.getInstallmentId());
		miscFeeAmount = customerAccountPaymentDataView.getMiscFeePaid();
		miscPenaltyAmount = customerAccountPaymentDataView.getMiscPenaltyPaid();

		Money totalFees = new Money();
		for (AccountFeesActionDetailEntity accountFeesActionDetail : accountAction
				.getAccountFeesActionDetails()) {
			if (customerAccountPaymentDataView.getFeesPaid().containsKey(
					accountFeesActionDetail.getFee().getFeeId())) {
				accountFeesActionDetail
						.makePayment(customerAccountPaymentDataView
								.getFeesPaid().get(
										accountFeesActionDetail.getFee()
												.getFeeId()));
				FeesTrxnDetailEntity feesTrxnDetailBO = new FeesTrxnDetailEntity();
				feesTrxnDetailBO.makePayment(accountFeesActionDetail);
				addFeesTrxnDetail(feesTrxnDetailBO);
				totalFees = totalFees.add(accountFeesActionDetail
						.getFeeAmountPaid());
			}
		}
		totalAmount = miscFeeAmount.add(miscPenaltyAmount).add(totalFees);
		setAmount(totalAmount);
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

	public AccountTrxnEntity generateReverseTrxn(String adjustmentComment)
			throws ApplicationException, SystemException {
		MasterPersistenceService masterPersistenceService = (MasterPersistenceService) ServiceFactory
				.getInstance().getPersistenceService(
						PersistenceServiceName.MasterDataService);
		MifosLogManager
				.getLogger(LoggerConstants.ACCOUNTSLOGGER)
				.debug(
						"Inside generate reverse transaction method of loan trxn detail");
		CustomerTrxnDetailEntity reverseAccntTrxn = new CustomerTrxnDetailEntity();
		reverseAccntTrxn.setAccount(getAccount());
		reverseAccntTrxn.setAccountPayment(getAccountPayment());
		reverseAccntTrxn.setPersonnel(getPersonnel());
		reverseAccntTrxn
				.setAccountActionEntity((AccountActionEntity) masterPersistenceService
						.findById(AccountActionEntity.class,
								AccountConstants.ACTION_CUSTOMER_ADJUSTMENT));
		reverseAccntTrxn.setAmount(getAmount().negate());
		reverseAccntTrxn.setTotalAmount(getAmount().negate());

		reverseAccntTrxn.setDueDate(getDueDate());
		if (null == adjustmentComment)
			reverseAccntTrxn.setComments(getComments());
		else
			reverseAccntTrxn.setComments(adjustmentComment);

		reverseAccntTrxn.setActionDate(getActionDate());
		reverseAccntTrxn.setCustomer(getCustomer());
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"Just before setting created date");
		reverseAccntTrxn.setTrxnCreatedDate(new Timestamp(System
				.currentTimeMillis()));
		
		
		if (getMiscFeeAmount().getAmountDoubleValue() > 0)
			reverseAccntTrxn.setMiscFeeAmount(getMiscFeeAmount().negate());
		else
			reverseAccntTrxn.setMiscFeeAmount(new Money());
		if (getMiscPenaltyAmount().getAmountDoubleValue() > 0)
			reverseAccntTrxn.setMiscPenaltyAmount(getMiscPenaltyAmount()
					.negate());
		else 
			reverseAccntTrxn.setMiscPenaltyAmount(new Money());

		reverseAccntTrxn.setInstallmentId(getInstallmentId());
		reverseAccntTrxn.setRelatedTrxn(this);

		if (null != getFeesTrxnDetails() && getFeesTrxnDetails().size() > 0) {
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"Before generating reverse entries for fees");
			for (FeesTrxnDetailEntity feeTrxnDetail : getFeesTrxnDetails()) {
				reverseAccntTrxn.addFeesTrxnDetail(feeTrxnDetail
						.generateReverseTrxn());
			}
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"after generating reverse entries for fees");
		}
		return reverseAccntTrxn;
	}

}
