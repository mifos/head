/**

 * CustomerAccountBO.java    version: xxx

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */

package org.mifos.application.accounts.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.hibernate.Hibernate;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.application.accounts.util.helpers.AccountPaymentData;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.CustomerAccountPaymentData;
import org.mifos.application.accounts.util.helpers.FeeInstallment;
import org.mifos.application.accounts.util.helpers.InstallmentDate;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerFeeScheduleEntity;
import org.mifos.application.customer.business.CustomerScheduleEntity;
import org.mifos.application.customer.business.CustomerTrxnDetailEntity;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.service.MasterPersistenceService;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

public class CustomerAccountBO extends AccountBO {

	Set<CustomerActivityEntity> customerActivitDetails = null;

	protected CustomerAccountBO() {
		super();
		customerActivitDetails = new HashSet<CustomerActivityEntity>();
	}

	public CustomerAccountBO(UserContext userContext, CustomerBO customer,
			List<FeeView> fees) throws AccountException {
		super(userContext, customer, AccountTypes.CUSTOMERACCOUNT,
				AccountState.CUSTOMERACCOUNT_ACTIVE);
		if (fees != null) {
			for (FeeView feeView : fees) {
				FeeBO fee = new FeePersistence()
						.getFee(feeView.getFeeIdValue());
				this.addAccountFees(new AccountFeesEntity(this, fee,
						new Double(feeView.getAmount())));
			}
			generateCustomerFeeSchedule(customer);
		}

		customerActivitDetails = new HashSet<CustomerActivityEntity>();
	}

	private void generateCustomerFeeSchedule(CustomerBO customer)
			throws AccountException {
		if (customer.getCustomerMeeting() != null
				&& (customer.getCustomerLevel().isGroup()
						&& customer.getCustomerStatus().getId().equals(
								CustomerStatus.GROUP_ACTIVE.getValue())
						|| customer.getCustomerLevel().isClient()
						&& customer.getCustomerStatus().getId().equals(
								CustomerStatus.CLIENT_ACTIVE.getValue()) || customer
						.getCustomerLevel().isCenter()
						&& customer.getCustomerStatus().getId().equals(
								CustomerStatus.CENTER_ACTIVE.getValue()))) {
			Calendar meetingStartDate = customer.getCustomerMeeting()
					.getMeeting().getMeetingStartDate();
			if (customer.getParentCustomer() != null) {
				Calendar nextMeetingDate = new GregorianCalendar();
				nextMeetingDate.setTime(customer.getParentCustomer()
						.getCustomerAccount().getNextMeetingDate());
				customer.getCustomerMeeting().getMeeting().setMeetingStartDate(
						nextMeetingDate);
			}
			generateMeetingSchedule();
			customer.getCustomerMeeting().getMeeting().setMeetingStartDate(
					meetingStartDate);
		}
	}

	@Override
	public AccountTypes getType() {
		return AccountTypes.CUSTOMERACCOUNT;
	}

	public void generateCustomerFeeSchedule() throws AccountException {
		generateCustomerFeeSchedule(getCustomer());
	}

	public Set<CustomerActivityEntity> getCustomerActivitDetails() {
		return customerActivitDetails;
	}

	private void setCustomerActivitDetails(
			Set<CustomerActivityEntity> customerActivitDetails) {
		this.customerActivitDetails = customerActivitDetails;
	}

	public void addCustomerActivity(
			CustomerActivityEntity customerActivityEntity) {
		customerActivitDetails.add(customerActivityEntity);
	}

	@Override
	protected AccountPaymentEntity makePayment(PaymentData paymentData)
			throws AccountException {
		MasterPersistenceService masterPersistenceService = new MasterPersistenceService();
		AccountPaymentEntity accountPayment = new AccountPaymentEntity(this,
				paymentData.getTotalAmount(), paymentData.getRecieptNum(),
				paymentData.getRecieptDate(), new PaymentTypeEntity(paymentData
						.getPaymentTypeId()));
		for (AccountPaymentData accountPaymentData : paymentData
				.getAccountPayments()) {
			CustomerScheduleEntity accountAction = (CustomerScheduleEntity) getAccountActionDate(accountPaymentData
					.getInstallmentId());
			if (accountAction.getPaymentStatus().equals(
					PaymentStatus.PAID.getValue()))
				throw new AccountException("errors.update",
						new String[] { getGlobalAccountNum() });
			CustomerAccountPaymentData customerAccountPaymentData = (CustomerAccountPaymentData) accountPaymentData;
			accountAction.setPaymentDetails(customerAccountPaymentData,
					new java.sql.Date(paymentData.getTransactionDate()
							.getTime()));
			customerAccountPaymentData.setAccountActionDate(accountAction);
			CustomerTrxnDetailEntity accountTrxn;
			try {
				accountTrxn = new CustomerTrxnDetailEntity(
						accountPayment,
						customerAccountPaymentData,
						paymentData.getPersonnel(),
						paymentData.getTransactionDate(),
						(AccountActionEntity) masterPersistenceService
								.findById(
										AccountActionEntity.class,
										AccountConstants.ACTION_CUSTOMER_ACCOUNT_REPAYMENT),
						"Payment rcvd.");
			} catch (PersistenceException e) {
				throw new AccountException(e);
			}
			accountPayment.addAcountTrxn(accountTrxn);
		}
		addCustomerActivity(new CustomerActivityEntity(this, paymentData
				.getPersonnel(), paymentData.getTotalAmount(), "Payment rcvd.",
				paymentData.getTransactionDate()));
		return accountPayment;
	}

	@Override
	public boolean isAdjustPossibleOnLastTrxn() {
		if (!(getCustomer().isActive())) {
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"State is not active hence adjustment is not possible");
			return false;
		}
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"Total payments on this account is  "
						+ getAccountPayments().size());
		if (null == getLastPmnt() && getLastPmntAmnt() == 0) {

			return false;
		}

		for (AccountTrxnEntity accntTrxn : getLastPmnt().getAccountTrxns()) {
			if (accntTrxn.getAccountActionEntity().getId().equals(
					AccountConstants.ACTION_CUSTOMER_ADJUSTMENT))
				return false;
		}

		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"Adjustment is not possible ");
		return true;
	}

	@Override
	protected void updateInstallmentAfterAdjustment(
			List<AccountTrxnEntity> reversedTrxns) throws AccountException {
		if (null != reversedTrxns && reversedTrxns.size() > 0) {
			Money totalAmountAdj = new Money();
			for (AccountTrxnEntity accntTrxn : reversedTrxns) {
				CustomerTrxnDetailEntity custTrxn = (CustomerTrxnDetailEntity) accntTrxn;
				CustomerScheduleEntity accntActionDate = (CustomerScheduleEntity) getAccountActionDate(custTrxn
						.getInstallmentId());
				accntActionDate.setPaymentStatus(PaymentStatus.UNPAID
						.getValue());
				accntActionDate.setPaymentDate(null);
				accntActionDate.setMiscFeePaid(accntActionDate.getMiscFeePaid()
						.add(custTrxn.getMiscFeeAmount()));
				totalAmountAdj = totalAmountAdj.add(removeSign(custTrxn
						.getMiscFeeAmount()));
				accntActionDate.setMiscPenaltyPaid(accntActionDate
						.getMiscPenaltyPaid().add(
								custTrxn.getMiscPenaltyAmount()));
				totalAmountAdj = totalAmountAdj.add(removeSign(custTrxn
						.getMiscPenaltyAmount()));
				if (null != accntActionDate.getAccountFeesActionDetails()
						&& accntActionDate.getAccountFeesActionDetails().size() > 0) {
					for (AccountFeesActionDetailEntity accntFeesAction : accntActionDate
							.getAccountFeesActionDetails()) {
						Money feeAmntAdjusted = custTrxn.getFeesTrxn(
								accntFeesAction.getAccountFee()
										.getAccountFeeId()).getFeeAmount();
						accntFeesAction.setFeeAmountPaid(accntFeesAction
								.getFeeAmountPaid().add(feeAmntAdjusted));
						totalAmountAdj = totalAmountAdj
								.add(removeSign(feeAmntAdjusted));
					}
				}
			}
			addCustomerActivity(buildCustomerActivity(totalAmountAdj,
					"Amnt Adjusted", userContext.getId()));
		}
	}

	@Override
	public void waiveAmountDue(WaiveEnum chargeType) throws AccountException {
		List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForDueInstallments();
		AccountActionDateEntity accountActionDateEntity = accountActionDateList
				.get(accountActionDateList.size() - 1);
		Money chargeWaived = ((CustomerScheduleEntity) accountActionDateEntity)
				.waiveCharges();
		if (chargeWaived != null && chargeWaived.getAmountDoubleValue() > 0.0) {
			addCustomerActivity(buildCustomerActivity(chargeWaived,
					"Amnt waived", userContext.getId()));
		}
		try {
			(new AccountPersistence()).createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	@Override
	public void waiveAmountOverDue(WaiveEnum chargeType)
			throws AccountException {
		Money chargeWaived = new Money();
		List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForDueInstallments();
		accountActionDateList.remove(accountActionDateList.size() - 1);
		for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
			chargeWaived = chargeWaived
					.add(((CustomerScheduleEntity) accountActionDateEntity)
							.waiveCharges());
		}
		if (chargeWaived != null && chargeWaived.getAmountDoubleValue() > 0.0) {
			addCustomerActivity(buildCustomerActivity(chargeWaived,
					"Amnt waived", userContext.getId()));
		}
		try {
			(new AccountPersistence()).createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	public void applyPeriodicFees() throws AccountException {
		if (isUpcomingInstallmentUnpaid()) {
			AccountActionDateEntity accountActionDate = getDetailsOfUpcomigInstallment();
			List<AccountFeesEntity> periodicFeeList = getPeriodicFeeList();
			for (AccountFeesEntity accountFeesEntity : periodicFeeList) {
				Integer applicableDatesCount = accountFeesEntity
						.getApplicableDatesCount(accountActionDate
								.getActionDate());
				if (applicableDatesCount > 0) {
					Hibernate.initialize(accountFeesEntity.getFees());
					accountFeesEntity.setLastAppliedDate(accountActionDate
							.getActionDate());
					FeeBO feesBO = getAccountFeesObject(accountFeesEntity
							.getFees().getFeeId());
					Money totalAmount = ((AmountFeeBO) feesBO).getFeeAmount()
							.multiply(
									new Double(Integer
											.toString(applicableDatesCount)));
					((CustomerScheduleEntity) accountActionDate)
							.applyPeriodicFees(accountFeesEntity.getFees()
									.getFeeId(), totalAmount);
					String description = feesBO.getFeeName() + " "
							+ AccountConstants.FEES_APPLIED;
					addCustomerActivity(buildCustomerActivity(totalAmount,
							description, null));
					try {
						(new AccountPersistence()).createOrUpdate(this);
					} catch (PersistenceException e) {
						throw new AccountException(e);
					}
				}
			}
		}
	}

	private CustomerActivityEntity buildCustomerActivity(Money amount,
			String description, Short personnelId) throws AccountException {
		try {
			PersonnelBO personnel = null;
			if (personnelId != null) {
				personnel = new PersonnelPersistence()
						.getPersonnel(personnelId);
			}
			return new CustomerActivityEntity(this, personnel, amount,
					description, new Date(System.currentTimeMillis()));
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	@Override
	public void updateAccountActivity(Money principal, Money interest,
			Money fee, Money penalty, Short personnelId, String description)
			throws AccountException {
		addCustomerActivity(buildCustomerActivity(fee, description, personnelId));
	}

	@Override
	public final void removeFees(Short feeId, Short personnelId)
			throws AccountException {
		List<Short> installmentIds = getApplicableInstallmentIdsForRemoveFees();
		if (installmentIds != null && installmentIds.size() != 0
				&& isFeeActive(feeId)) {
			updateAccountActionDateEntity(installmentIds, feeId);
		}
		updateAccountFeesEntity(feeId);
		FeeBO feesBO = getAccountFeesObject(feeId);
		String description = feesBO.getFeeName() + " "
				+ AccountConstants.FEES_REMOVED;
		updateAccountActivity(null, null, null, null, personnelId, description);
		try {
			(new AccountPersistence()).createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}

	}

	@Override
	protected Money getDueAmount(AccountActionDateEntity installment) {
		return ((CustomerScheduleEntity) installment).getTotalDueWithFees();
	}

	@Override
	protected void regenerateFutureInstallments(Short nextInstallmentId)
			throws AccountException {
		if (!this.getCustomer().getCustomerStatus().getId().equals(
						ClientConstants.STATUS_CLOSED)
				&& !this.getCustomer().getCustomerStatus().getId().equals(
						GroupConstants.CLOSED)) {

			List<Date> meetingDates = null;
			int installmentSize = getLastInstallmentId();
			int totalInstallmentDatesToBeChanged = installmentSize
					- nextInstallmentId + 1;
			try {
				getCustomer().changeUpdatedMeeting();
				meetingDates = getCustomer().getCustomerMeeting().getMeeting()
						.getAllDates(totalInstallmentDatesToBeChanged + 1);
				if (meetingDates.get(0).compareTo(
						DateUtils.getCurrentDateWithoutTimeStamp()) == 0) {
					meetingDates.remove(0);
				} else {
					meetingDates.remove(totalInstallmentDatesToBeChanged);
				}
			} catch (MeetingException me) {
				throw new AccountException(me);
			} catch (CustomerException ce) {
				throw new AccountException(ce);
			}
			for (int count = 0; count < meetingDates.size(); count++) {
				short installmentId = (short) (nextInstallmentId.intValue() + count);
				AccountActionDateEntity accountActionDate = getAccountActionDate(installmentId);
				if (accountActionDate != null)
					accountActionDate.setActionDate(new java.sql.Date(
							meetingDates.get(count).getTime()));
			}
		}
	}

	public void generateNextSetOfMeetingDates() throws AccountException {
		Short lastInstallmentId = getLastInstallmentId();
		AccountActionDateEntity lastInstallment = getAccountActionDate(lastInstallmentId);
		MeetingBO meeting = getCustomer().getCustomerMeeting().getMeeting();
		Calendar meetingStartDate = meeting.getMeetingStartDate();
		meeting.setMeetingStartDate(DateUtils.getCalendar(lastInstallment
				.getActionDate()));

		List<Date> installmentDates = null;
		try {
			installmentDates = meeting.getAllDates((short) 11);
			if (installmentDates.get(0).compareTo(
					lastInstallment.getActionDate()) == 0) {
				installmentDates.remove(0);
			} else {
				installmentDates.remove(10);
			}
		} catch (MeetingException me) {
			throw new AccountException(me);
		}
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"Fee installment obtained ");
		int count = 1;
		for (Date installmentDate : installmentDates) {
			CustomerScheduleEntity customerScheduleEntity = new CustomerScheduleEntity(
					this, getCustomer(), Short.valueOf(String.valueOf(count
							+ lastInstallmentId)), new java.sql.Date(
							installmentDate.getTime()), PaymentStatus.UNPAID);
			count++;
			addAccountActionDate(customerScheduleEntity);
		}
		meeting.setMeetingStartDate(meetingStartDate);
	}

	@Override
	public Money updateAccountActionDateEntity(List<Short> intallmentIdList,
			Short feeId) {
		Money totalFeeAmount = new Money();
		Set<AccountActionDateEntity> accountActionDateEntitySet = this
				.getAccountActionDates();
		for (AccountActionDateEntity accountActionDateEntity : accountActionDateEntitySet) {
			if (intallmentIdList.contains(accountActionDateEntity
					.getInstallmentId())) {
				totalFeeAmount = totalFeeAmount
						.add(((CustomerScheduleEntity) accountActionDateEntity)
								.removeFees(feeId));
			}
		}
		return totalFeeAmount;
	}

	@Override
	public void applyCharge(Short feeId, Double charge) throws AccountException {
		if (!isCustomerValid()) {
			if (feeId.equals(Short.valueOf(AccountConstants.MISC_FEES))
					|| feeId.equals(Short
							.valueOf(AccountConstants.MISC_PENALTY))) {
				throw new AccountException(
						AccountConstants.MISC_CHARGE_NOT_APPLICABLE);
			}
			addFeeToAccountFee(feeId, charge);
			FeeBO fee = new FeePersistence().getFee(feeId);
			updateCustomerActivity(feeId, new Money(charge.toString()), fee
					.getFeeName()
					+ " applied");
		} else {
			Money chargeAmount = new Money(String.valueOf(charge));
			List<AccountActionDateEntity> dueInstallments = null;
			if (feeId.equals(Short.valueOf(AccountConstants.MISC_FEES))
					|| feeId.equals(Short
							.valueOf(AccountConstants.MISC_PENALTY))) {
				dueInstallments = getTotalDueInstallments();
				if (dueInstallments.isEmpty())
					throw new AccountException(
							AccountConstants.NOMOREINSTALLMENTS);
				applyMiscCharge(feeId, chargeAmount, dueInstallments.get(0));
			} else {
				dueInstallments = getTotalDueInstallments();
				if (dueInstallments.isEmpty())
					throw new AccountException(
							AccountConstants.NOMOREINSTALLMENTS);
				FeeBO fee = new FeePersistence().getFee(feeId);
				if (fee.getFeeFrequency().getFeePayment() != null) {
					applyOneTimeFee(fee, chargeAmount, dueInstallments.get(0));
				} else {
					applyPeriodicFee(fee, chargeAmount, dueInstallments);
				}
			}
		}
	}

	public Date getUpcomingChargesDate() {
		AccountActionDateEntity nextAccountAction = null;
		if (getTotalDueInstallments().size() > 0) {
			nextAccountAction = getTotalDueInstallments().get(0);
		}
		return nextAccountAction != null ? nextAccountAction.getActionDate()
				: new java.sql.Date(System.currentTimeMillis());
	}

	@Override
	public Money getTotalAmountDue() {
		Money totalAmt = getTotalAmountInArrears();
		List<AccountActionDateEntity> dueActionDateList = getTotalDueInstallments();
		if (dueActionDateList.size() > 0) {
			AccountActionDateEntity nextInstallment = dueActionDateList.get(0);
			totalAmt = totalAmt.add(getDueAmount(nextInstallment));
		}
		return totalAmt;
	}

	public AccountActionDateEntity getUpcomingInstallment() {
		List<AccountActionDateEntity> dueActionDateList = getTotalDueInstallments();
		if (dueActionDateList.size() > 0)
			return dueActionDateList.get(0);
		return null;
	}

	private void addFeeToAccountFee(Short feeId, Double charge) {
		FeeBO fee = new FeePersistence().getFee(feeId);
		AccountFeesEntity accountFee = null;
		if ((fee.isPeriodic() && !isFeeAlreadyApplied(fee))
				|| !fee.isPeriodic()) {
			accountFee = new AccountFeesEntity(this, fee, charge,
					FeeStatus.ACTIVE.getValue(), new Date(System
							.currentTimeMillis()), null);
			addAccountFees(accountFee);
		} else {
			accountFee = getAccountFees(fee.getFeeId());
			accountFee.setFeeAmount(charge);
			accountFee.setFeeStatus(FeeStatus.ACTIVE.getValue());
			accountFee
					.setStatusChangeDate(new Date(System.currentTimeMillis()));
		}
	}

	private void applyPeriodicFee(FeeBO fee, Money charge,
			List<AccountActionDateEntity> dueInstallments)
			throws AccountException {
		AccountFeesEntity accountFee = getAccountFee(fee, charge
				.getAmountDoubleValue());
		List<InstallmentDate> installmentDates = new ArrayList<InstallmentDate>();
		for (AccountActionDateEntity accountActionDateEntity : dueInstallments)
			installmentDates.add(new InstallmentDate(accountActionDateEntity
					.getInstallmentId(), accountActionDateEntity
					.getActionDate()));
		List<FeeInstallment> feeInstallmentList = mergeFeeInstallments(handlePeriodic(
				accountFee, installmentDates));
		Money totalFeeAmountApplied = applyFeeToInstallments(
				feeInstallmentList, dueInstallments);
		updateCustomerActivity(fee.getFeeId(), totalFeeAmountApplied, fee
				.getFeeName()
				+ " applied");
		accountFee.setFeeStatus(FeeStatus.ACTIVE.getValue());
	}

	private void applyOneTimeFee(FeeBO fee, Money charge,
			AccountActionDateEntity accountActionDateEntity)
			throws AccountException {
		CustomerScheduleEntity customerScheduleEntity = (CustomerScheduleEntity) accountActionDateEntity;
		AccountFeesEntity accountFee = new AccountFeesEntity(this, fee, charge
				.getAmountDoubleValue(), FeeStatus.ACTIVE.getValue(), new Date(
				System.currentTimeMillis()), null);
		List<AccountActionDateEntity> customerScheduleList = new ArrayList<AccountActionDateEntity>();
		customerScheduleList.add(customerScheduleEntity);
		List<InstallmentDate> installmentDates = new ArrayList<InstallmentDate>();
		installmentDates.add(new InstallmentDate(accountActionDateEntity
				.getInstallmentId(), accountActionDateEntity.getActionDate()));
		List<FeeInstallment> feeInstallmentList = new ArrayList<FeeInstallment>();
		feeInstallmentList.add(handleOneTime(accountFee, installmentDates));
		Money totalFeeAmountApplied = applyFeeToInstallments(
				feeInstallmentList, customerScheduleList);
		updateCustomerActivity(fee.getFeeId(), totalFeeAmountApplied, fee
				.getFeeName()
				+ " applied");
		accountFee.setFeeStatus(FeeStatus.ACTIVE.getValue());
	}

	private void applyMiscCharge(Short chargeType, Money charge,
			AccountActionDateEntity accountActionDateEntity)
			throws AccountException {
		CustomerScheduleEntity customerScheduleEntity = (CustomerScheduleEntity) accountActionDateEntity;
		customerScheduleEntity.applyMiscCharge(chargeType, charge);
		updateCustomerActivity(chargeType, charge, "");
	}

	private void updateCustomerActivity(Short chargeType, Money charge,
			String comments) throws AccountException {
		try {
			PersonnelBO personnel = new PersonnelPersistence()
					.getPersonnel(getUserContext().getId());
			CustomerActivityEntity customerActivityEntity = null;
			if (chargeType != null
					&& chargeType.equals(Short
							.valueOf(AccountConstants.MISC_PENALTY)))
				customerActivityEntity = new CustomerActivityEntity(this,
						personnel, charge,
						AccountConstants.MISC_PENALTY_APPLIED, new Date(System
								.currentTimeMillis()));
			else if (chargeType != null
					&& chargeType.equals(Short
							.valueOf(AccountConstants.MISC_FEES)))
				customerActivityEntity = new CustomerActivityEntity(this,
						personnel, charge, AccountConstants.MISC_FEES_APPLIED,
						new Date(System.currentTimeMillis()));
			else
				customerActivityEntity = new CustomerActivityEntity(this,
						personnel, charge, comments, new Date(System
								.currentTimeMillis()));
			addCustomerActivity(customerActivityEntity);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	private Money applyFeeToInstallments(
			List<FeeInstallment> feeInstallmentList,
			List<AccountActionDateEntity> accountActionDateList) {
		Date lastAppliedDate = null;
		Money totalFeeAmountApplied = new Money();
		AccountFeesEntity accountFeesEntity = null;
		for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
			CustomerScheduleEntity customerScheduleEntity = (CustomerScheduleEntity) accountActionDateEntity;
			for (FeeInstallment feeInstallment : feeInstallmentList)
				if (feeInstallment.getInstallmentId().equals(
						customerScheduleEntity.getInstallmentId())) {
					lastAppliedDate = customerScheduleEntity.getActionDate();
					totalFeeAmountApplied = totalFeeAmountApplied
							.add(feeInstallment.getAccountFee());
					AccountFeesActionDetailEntity accountFeesActionDetailEntity = new CustomerFeeScheduleEntity(
							customerScheduleEntity, feeInstallment
									.getAccountFeesEntity().getFees(),
							feeInstallment.getAccountFeesEntity(),
							feeInstallment.getAccountFee());
					customerScheduleEntity
							.addAccountFeesAction(accountFeesActionDetailEntity);
					accountFeesEntity = feeInstallment.getAccountFeesEntity();
				}
		}
		accountFeesEntity.setLastAppliedDate(lastAppliedDate);
		addAccountFees(accountFeesEntity);
		return totalFeeAmountApplied;
	}

	private boolean isCustomerValid() {
		if (getCustomer().getCustomerStatus().getId().equals(
				CustomerConstants.CENTER_ACTIVE_STATE)
				|| getCustomer().getCustomerStatus().getId().equals(
						CustomerConstants.GROUP_ACTIVE_STATE)
				|| getCustomer().getCustomerStatus().getId().equals(
						GroupConstants.HOLD)
				|| getCustomer().getCustomerStatus().getId().equals(
						CustomerConstants.CLIENT_APPROVED)
				|| getCustomer().getCustomerStatus().getId().equals(
						CustomerConstants.CLIENT_ONHOLD))
			return true;
		return false;
	}

	public Money getNextDueAmount() {

		AccountActionDateEntity accountAction = null;
		for (AccountActionDateEntity accountActionDate : getAccountActionDates()) {

			if (accountActionDate.getPaymentStatus().equals(
					PaymentStatus.UNPAID.getValue())) {
				if (accountActionDate.compareDate(DateUtils
						.getCurrentDateWithoutTimeStamp()) >= 0) {
					if (accountAction == null)
						accountAction = accountActionDate;
					else {
						if (accountAction.getInstallmentId() > accountActionDate
								.getInstallmentId())
							accountAction = accountActionDate;
					}

				}
			}
		}

		if (accountAction != null)
			return getDueAmount(accountAction);
		else
			return new Money("0.0");
	}

	public void generateCustomerAccountSystemId(String officeGlobalNum)
			throws CustomerException {
		try {
			if (getGlobalAccountNum() == null)
				this.setGlobalAccountNum(generateId(userContext
						.getBranchGlobalNum()));
			else {
				throw new CustomerException(
						AccountExceptionConstants.IDGenerationException);
			}
		} catch (AccountException e) {
			throw new CustomerException(e);
		}
	}

	@Override
	protected final List<FeeInstallment> handlePeriodic(
			AccountFeesEntity accountFees,
			List<InstallmentDate> installmentDates) throws AccountException {
		Money accountFeeAmount = accountFees.getAccountFeeAmount();
		MeetingBO feeMeetingFrequency = accountFees.getFees().getFeeFrequency()
				.getFeeMeetingFrequency();
		List<Date> feeDates = getFeeDates(feeMeetingFrequency, installmentDates);
		ListIterator<Date> feeDatesIterator = feeDates.listIterator();
		List<FeeInstallment> feeInstallmentList = new ArrayList<FeeInstallment>();
		while (feeDatesIterator.hasNext()) {
			Date feeDate = feeDatesIterator.next();
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"FeeInstallmentGenerator:handlePeriodic date considered after removal.."
							+ feeDate);
			Short installmentId = getMatchingInstallmentId(installmentDates,
					feeDate);
			feeInstallmentList.add(buildFeeInstallment(installmentId,
					accountFeeAmount, accountFees));
			break;
		}
		return feeInstallmentList;
	}

	private void generateMeetingSchedule() throws AccountException {
		List<InstallmentDate> installmentDates = getInstallmentDates(
				getCustomer().getCustomerMeeting().getMeeting(), (short) 10,
				(short) 0);
		MifosLogManager
				.getLogger(LoggerConstants.ACCOUNTSLOGGER)
				.debug(
						"RepamentSchedular:getRepaymentSchedule , installment dates obtained ");
		List<FeeInstallment> feeInstallmentList = mergeFeeInstallments(getFeeInstallment(installmentDates));
		MifosLogManager
				.getLogger(LoggerConstants.ACCOUNTSLOGGER)
				.debug(
						"RepamentSchedular:getRepaymentSchedule , fee installment obtained ");
		for (InstallmentDate installmentDate : installmentDates) {
			CustomerScheduleEntity customerScheduleEntity = new CustomerScheduleEntity(
					this, getCustomer(), installmentDate.getInstallmentId(),
					new java.sql.Date(installmentDate.getInstallmentDueDate()
							.getTime()), PaymentStatus.UNPAID);
			addAccountActionDate(customerScheduleEntity);
			for (FeeInstallment feeInstallment : feeInstallmentList) {
				if (feeInstallment.getInstallmentId().equals(
						installmentDate.getInstallmentId())) {
					CustomerFeeScheduleEntity customerFeeScheduleEntity = new CustomerFeeScheduleEntity(
							customerScheduleEntity, feeInstallment
									.getAccountFeesEntity().getFees(),
							feeInstallment.getAccountFeesEntity(),
							feeInstallment.getAccountFee());
					customerScheduleEntity
							.addAccountFeesAction(customerFeeScheduleEntity);
				}
			}
		}
		MifosLogManager
				.getLogger(LoggerConstants.ACCOUNTSLOGGER)
				.debug(
						"RepamentSchedular:getRepaymentSchedule , repayment schedule generated  ");
	}

}
