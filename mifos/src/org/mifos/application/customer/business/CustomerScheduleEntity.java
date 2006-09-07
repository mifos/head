package org.mifos.application.customer.business;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.CustomerAccountPaymentData;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.framework.util.helpers.Money;

public class CustomerScheduleEntity extends AccountActionDateEntity {

	private Money miscFee;

	private Money miscFeePaid;

	private Money miscPenalty;

	private Money miscPenaltyPaid;

	private Set<AccountFeesActionDetailEntity> accountFeesActionDetails;

	protected CustomerScheduleEntity() {
		super(null, null, null, null, null);
	}

	public CustomerScheduleEntity(AccountBO account, CustomerBO customer,
			Short installmentId, Date actionDate, PaymentStatus paymentStatus) {
		super(account, customer, installmentId, actionDate, paymentStatus);
		accountFeesActionDetails = new HashSet<AccountFeesActionDetailEntity>();
		miscFee=new Money();
		miscFeePaid=new Money();
		miscPenalty=new Money();
		miscPenaltyPaid=new Money();
	}

	public Set<AccountFeesActionDetailEntity> getAccountFeesActionDetails() {
		return accountFeesActionDetails;
	}

	public void addAccountFeesAction(
			AccountFeesActionDetailEntity accountFeesAction) {
		accountFeesActionDetails.add(accountFeesAction);
	}

	public Money getMiscFee() {
		return miscFee;
	}

	public void setMiscFee(Money miscFee) {
		this.miscFee = miscFee;
	}

	public Money getMiscFeePaid() {
		return miscFeePaid;
	}

	public void setMiscFeePaid(Money miscFeePaid) {
		this.miscFeePaid = miscFeePaid;
	}

	public Money getMiscPenalty() {
		return miscPenalty;
	}

	public void setMiscPenalty(Money miscPenalty) {
		this.miscPenalty = miscPenalty;
	}

	public Money getMiscPenaltyPaid() {
		return miscPenaltyPaid;
	}

	public void setMiscPenaltyPaid(Money miscPenaltyPaid) {
		this.miscPenaltyPaid = miscPenaltyPaid;
	}

	public Money getMiscFeeDue() {
		return getMiscFee().subtract(getMiscFeePaid());
	}

	public Money getMiscPenaltyDue() {
		return getMiscPenalty().subtract(getMiscPenaltyPaid());
	}

	public Money getTotalFeeDue() {
		Money totalFees = new Money();
		for (AccountFeesActionDetailEntity obj : accountFeesActionDetails) {
			totalFees = totalFees.add(obj.getFeeDue());
		}
		return totalFees;
	}

	public Money getTotalFees() {
		return getMiscFee().add(getTotalFeeDue());
	}

	public Money getTotalDueWithFees() {
		return getMiscPenaltyDue().add(getTotalFees());
	}

	public void applyPeriodicFees(Short feeId,Money totalAmount) {
		AccountFeesEntity accountFeesEntity = account
				.getAccountFees(feeId);
		AccountFeesActionDetailEntity accountFeesActionDetailEntity = new CustomerFeeScheduleEntity(
				this, accountFeesEntity.getFees(),
				accountFeesEntity, totalAmount);
		addAccountFeesAction(accountFeesActionDetailEntity);
	}

	public void setPaymentDetails(
			CustomerAccountPaymentData customerAccountPaymentData,
			Date paymentDate) {
		this.miscFeePaid = customerAccountPaymentData.getMiscFeePaid();
		this.miscPenaltyPaid = customerAccountPaymentData.getMiscPenaltyPaid();
		this.paymentStatus = customerAccountPaymentData.getPaymentStatus();
		this.paymentDate = paymentDate;
	}

	public Money waiveCharges() {
		Money chargeWaived = new Money();
		chargeWaived = chargeWaived.add(getMiscFee()).add(getMiscPenalty());
		setMiscFee(new Money());
		setMiscPenalty(new Money());
		for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : getAccountFeesActionDetails()) {
			chargeWaived = chargeWaived.add(accountFeesActionDetailEntity
					.waiveCharges());
		}
		return chargeWaived;
	}

	public Money waiveFeeCharges() {
		Money chargeWaived = new Money();
		chargeWaived = chargeWaived.add(getMiscFee());
		setMiscFee(new Money());
		for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : getAccountFeesActionDetails()) {
			chargeWaived = chargeWaived.add(accountFeesActionDetailEntity
					.waiveCharges());
		}
		return chargeWaived;
	}

	public void removeAccountFeesActionDetailEntity(
			AccountFeesActionDetailEntity accountFeesActionDetailEntity) {
		accountFeesActionDetails.remove(accountFeesActionDetailEntity);
	}

	public AccountFeesActionDetailEntity getAccountFeesAction(
			Integer accountFeeId) {
		for (AccountFeesActionDetailEntity accountFeesAction : getAccountFeesActionDetails()) {
			if (accountFeesAction.getAccountFee().getAccountFeeId().equals(
					accountFeeId)) {
				return accountFeesAction;
			}
		}
		return null;
	}

	public Money removeFees(Short feeId) {
		Money feeAmount = null;
		AccountFeesActionDetailEntity objectToRemove = null;
		Set<AccountFeesActionDetailEntity> accountFeesActionDetailSet = this
				.getAccountFeesActionDetails();
		for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountFeesActionDetailSet) {
			if (accountFeesActionDetailEntity.getFee().getFeeId().equals(feeId)) {
				objectToRemove = accountFeesActionDetailEntity;
				break;
			}
		}
		if (objectToRemove != null) {
			feeAmount = objectToRemove.getFeeAmount();
			this.removeAccountFeesActionDetailEntity(objectToRemove);
		}

		return feeAmount;
	}
	
	public void applyMiscCharge(Short chargeType,Money charge){
		if(chargeType.equals(Short.valueOf(AccountConstants.MISC_FEES)))
			setMiscFee(getMiscFee().add(charge));
		else if(chargeType.equals(Short.valueOf(AccountConstants.MISC_PENALTY)))
			setMiscPenalty(getMiscPenalty().add(charge));
	}
	
}
