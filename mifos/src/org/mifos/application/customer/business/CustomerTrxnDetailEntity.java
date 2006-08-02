package org.mifos.application.customer.business;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.accounts.business.LoanTrxnDetailEntity;
import org.mifos.application.accounts.loan.persistance.LoanPersistance;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.CustomerAccountPaymentData;
import org.mifos.application.accounts.util.helpers.LoanPaymentData;
import org.mifos.application.master.persistence.service.MasterPersistenceService;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.PersistenceServiceName;

public class CustomerTrxnDetailEntity extends AccountTrxnEntity {
	protected CustomerTrxnDetailEntity() {
		totalAmount=null;
		miscPenaltyAmount=null;
		miscFeeAmount=null;
		feesTrxnDetails = new HashSet<FeesTrxnDetailEntity>();
	}

	private final Set<FeesTrxnDetailEntity> feesTrxnDetails;

	private final Money totalAmount;

	private final Money miscPenaltyAmount;

	private final Money miscFeeAmount;

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
	
	public void addFeesTrxnDetail(FeesTrxnDetailEntity feesTrxn) {
		feesTrxn.setAccountTrxn(this);
		feesTrxnDetails.add(feesTrxn);
	}
	
	public CustomerTrxnDetailEntity(AccountPaymentEntity accountPaymentEntity,
			CustomerAccountPaymentData customerAccountPaymentDataView, PersonnelBO personnel,
			java.util.Date transactionDate,
			AccountActionEntity accountActionEntity,
			String comments) {
		
		super(accountPaymentEntity, accountActionEntity,
				customerAccountPaymentDataView.getInstallmentId(), customerAccountPaymentDataView.getAccountActionDate()
						.getActionDate(),personnel, transactionDate,customerAccountPaymentDataView.getTotalPaidAmnt(), 
				 comments);
		totalAmount=customerAccountPaymentDataView.getTotalPaidAmnt();
		miscFeeAmount = customerAccountPaymentDataView.getMiscFeePaid();
		miscPenaltyAmount = customerAccountPaymentDataView.getMiscPenaltyPaid();
		feesTrxnDetails = new HashSet<FeesTrxnDetailEntity>();
		for (AccountFeesActionDetailEntity accountFeesActionDetail : customerAccountPaymentDataView.getAccountActionDate().getAccountFeesActionDetails()) {
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
			}
		}
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
	
	public CustomerTrxnDetailEntity(AccountPaymentEntity accountPayment,
			AccountActionEntity accountActionEntity, Short installmentId,
			Date dueDate, PersonnelBO personnel,
			Date actionDate, Money amount, 
			String comments, AccountTrxnEntity relatedTrxn,
			Money miscFeeAmount, Money miscPenaltyAmount) {
		super(accountPayment, accountActionEntity, installmentId, dueDate, 
				personnel, actionDate, amount, comments,
				relatedTrxn);
		this.miscFeeAmount = miscFeeAmount;
		this.miscPenaltyAmount = miscPenaltyAmount;
		this.totalAmount=amount;
		feesTrxnDetails = new HashSet<FeesTrxnDetailEntity>();
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
		String comment=null;
		if (null == adjustmentComment)
			comment=getComments();
		else
			comment=adjustmentComment;

		CustomerTrxnDetailEntity  reverseAccntTrxn=new CustomerTrxnDetailEntity(getAccountPayment(),
				(AccountActionEntity) masterPersistenceService
				.findById(AccountActionEntity.class,
						AccountConstants.ACTION_CUSTOMER_ADJUSTMENT), 
						getInstallmentId(),
						getDueDate(), getPersonnel(),
						getActionDate(), getAmount().negate(), 
				 comment, this,
				getMiscFeeAmount().negate(), getMiscPenaltyAmount().negate()); 
		

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
