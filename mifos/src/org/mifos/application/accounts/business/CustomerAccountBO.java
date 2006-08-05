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

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountPaymentData;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.CustomerAccountPaymentData;
import org.mifos.application.accounts.util.helpers.PaymentData;

import org.mifos.application.accounts.util.helpers.CustomerAccountPaymentData;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerScheduleEntity;
import org.mifos.application.customer.business.CustomerTrxnDetailEntity;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.service.MasterPersistenceService;
import org.mifos.application.master.util.valueobjects.AccountType;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.service.PersonnelPersistenceService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.repaymentschedule.RepaymentSchedule;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleConstansts;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleException;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleFactory;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleHelper;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleIfc;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleInputsIfc;
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.framework.components.scheduler.SchedulerIntf;
import org.mifos.framework.components.scheduler.helpers.SchedulerHelper;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.PersistenceServiceName;

public class CustomerAccountBO extends AccountBO {

	Set<CustomerActivityEntity> customerActivitDetails = null;

	public CustomerAccountBO() {
		super();
		customerActivitDetails = new HashSet<CustomerActivityEntity>();
	}

	public CustomerAccountBO(UserContext userContext, CustomerBO customer,
			List<FeeView> fees) throws AccountException {
		super(userContext,	customer, 
				new AccountType(AccountTypes.CUSTOMERACCOUNT.getValue()), 
				AccountState.CUSTOMERACCOUNT_ACTIVE);
		if(fees !=null){
			for(FeeView feeView: fees){
				FeeBO fee = new FeePersistence().getFee(feeView.getFeeId());
				this.addAccountFees(new AccountFeesEntity(this,fee, feeView.getAmount()));
			}
			generateCustomerFeeSchedule(customer);
		}
			
		customerActivitDetails = new HashSet<CustomerActivityEntity>();
	}

	private void generateCustomerFeeSchedule(CustomerBO customer)throws AccountException{
		if(customer.getCustomerMeeting()!=null && (
				customer.getCustomerLevel().isGroup()&& customer.getCustomerStatus().getId().equals(CustomerStatus.GROUP_ACTIVE.getValue())
				||customer.getCustomerLevel().isClient()&& customer.getCustomerStatus().getId().equals(CustomerStatus.CLIENT_ACTIVE.getValue())
				||customer.getCustomerLevel().isCenter()&& customer.getCustomerStatus().getId().equals(CustomerStatus.CENTER_ACTIVE.getValue()))
				){
		try{
			generateFeeSchedule(customer.getCustomerMeeting().getMeeting());
		}catch(RepaymentScheduleException rse){
			throw new AccountException(rse);
		}
	}
	}
	
	private void generateFeeSchedule(MeetingBO meeting)throws RepaymentScheduleException{
		RepaymentScheduleInputsIfc repaymntScheduleInputs = RepaymentScheduleFactory.getRepaymentScheduleInputs();
		RepaymentScheduleIfc repaymentScheduler = RepaymentScheduleFactory.getRepaymentScheduler();
		repaymntScheduleInputs.setMeeting(meeting);
		repaymntScheduleInputs.setMeetingToConsider(RepaymentScheduleConstansts.MEETING_CUSTOMER);
		repaymntScheduleInputs.setRepaymentFrequency(meeting);
		repaymntScheduleInputs.setAccountFeeEntity(getAccountFees());
		repaymentScheduler.setRepaymentScheduleInputs(repaymntScheduleInputs);
		RepaymentSchedule repaymentSchedule = repaymentScheduler.getRepaymentSchedule();
		Set<AccountActionDateEntity> accntActionDateSet = RepaymentScheduleHelper.getActionDateEntity(repaymentSchedule, "", this, customer);
        //this will insert records in account action date which is noting but installments.
		if(accntActionDateSet!=null){
			for(AccountActionDateEntity accountActionDate : accntActionDateSet){
				//accountActionDate.setCurrency(Short.valueOf("1"));
				this.addAccountActionDate(accountActionDate);
			}
		}
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
		customerActivityEntity.setCustomerAccount(this);
		customerActivitDetails.add(customerActivityEntity);
	}

	protected AccountPaymentEntity makePayment(PaymentData paymentData)
			throws AccountException, SystemException {
		MasterPersistenceService masterPersistenceService = (MasterPersistenceService) ServiceFactory
				.getInstance().getPersistenceService(
						PersistenceServiceName.MasterDataService);
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
			CustomerTrxnDetailEntity accountTrxn = new CustomerTrxnDetailEntity(
					accountPayment,
					customerAccountPaymentData,
					paymentData.getPersonnel(),
					paymentData.getTransactionDate(),
					(AccountActionEntity) masterPersistenceService.findById(
							AccountActionEntity.class,
							AccountConstants.ACTION_CUSTOMER_ACCOUNT_REPAYMENT),
					"Payment rcvd.");
			accountPayment.addAcountTrxn(accountTrxn);
			addCustomerActivity(new CustomerActivityEntity(paymentData
					.getPersonnel(), "Payment rcvd.", paymentData
					.getTotalAmount()));

		}
		return accountPayment;
	}

	public boolean isAdjustPossibleOnLastTrxn() {
		if (!(getCustomer().isCustomerActive())) {
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

	protected void updateInstallmentAfterAdjustment(
			List<AccountTrxnEntity> reversedTrxns) {
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
	public void waiveAmountDue(WaiveEnum chargeType) throws ServiceException {
		List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForDueInstallments();
		AccountActionDateEntity accountActionDateEntity = accountActionDateList
				.get(accountActionDateList.size() - 1);
		Money chargeWaived = ((CustomerScheduleEntity) accountActionDateEntity)
				.waiveCharges();
		if (chargeWaived != null && chargeWaived.getAmountDoubleValue() > 0.0) {
			addCustomerActivity(buildCustomerActivity(chargeWaived,
					"Amnt waived", userContext.getId()));
		}
		getAccountPersistenceService().update(this);
	}

	@Override
	public void waiveAmountOverDue(WaiveEnum chargeType)
			throws ServiceException {
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
		getAccountPersistenceService().update(this);
	}

	public void applyPeriodicFees(Date date) throws RepaymentScheduleException,
			SchedulerException, PersistenceException, ServiceException {
		Set<AccountActionDateEntity> accountActionDateSet = getAccountActionDates();
		for (AccountActionDateEntity accountActionDate : accountActionDateSet) {
			if (date.equals(accountActionDate.getActionDate())) {
				List<AccountFeesEntity> periodicFeeList = getPeriodicFeeList();
				for (AccountFeesEntity accountFeesEntity : periodicFeeList) {
					if (accountFeesEntity.isApplicable(date) == true) {
						Hibernate.initialize(accountFeesEntity.getFees());
						accountFeesEntity.setLastAppliedDate(date);
						((CustomerScheduleEntity) accountActionDate)
								.applyPeriodicFees(accountFeesEntity.getFees()
										.getFeeId());
						FeeBO feesBO = getAccountFeesObject(accountFeesEntity
								.getFees().getFeeId());

						String description = feesBO.getFeeName() + " "
								+ AccountConstants.FEES_APPLIED;

						updateAccountActivity(((AmountFeeBO) feesBO)
								.getFeeAmount(), null, description);
						getAccountPersistenceService().save(this);
					}
				}
				break;
			}
		}
	}

	private CustomerActivityEntity buildCustomerActivity(Money amount,
			String description, Short personnelId) {
		PersonnelBO personnel = null;
		if (personnelId != null) {
			personnel = new PersonnelPersistenceService()
					.getPersonnel(personnelId);
		}
		return new CustomerActivityEntity(personnel, description, amount);
	}

	public void updateAccountActivity(Money totalAmount, Short personnelId,
			String description) {
		this.addCustomerActivity(buildCustomerActivity(totalAmount,
				description, personnelId));
	}

	protected Money getDueAmount(AccountActionDateEntity installment) {
		return ((CustomerScheduleEntity) installment).getTotalDueWithFees();
	}

	@Override
	protected void regenerateFutureInstallments(Short nextIntallmentId)
			throws HibernateException, ServiceException, SchedulerException {
		if (!this.getCustomer().getCustomerStatus().getId().equals(
				ClientConstants.STATUS_CANCELLED)
				&& !this.getCustomer().getCustomerStatus().getId().equals(
						ClientConstants.STATUS_CLOSED)
				&& !this.getCustomer().getCustomerStatus().getId().equals(
						GroupConstants.CANCELLED)
				&& !this.getCustomer().getCustomerStatus().getId().equals(
						GroupConstants.CLOSED)) {
			SchedulerIntf scheduler = SchedulerHelper
					.getScheduler(getCustomer().getCustomerMeeting()
							.getMeeting());
			List<Date> meetingDates = scheduler.getAllDates();
			meetingDates.remove(0);
			deleteFutureInstallments();
			for (Date date : meetingDates) {
				addAccountActionDate(new CustomerScheduleEntity(this,
						getCustomer(), nextIntallmentId++, new java.sql.Date(
								date.getTime()), PaymentStatus.UNPAID));
			}
		}
	}

	private Set<AccountFees> getAccountFeesSet() {
		Set<AccountFees> accountFeesSet = new HashSet<AccountFees>();
		for (AccountFeesEntity accountFeesEntity : getAccountFees()) {
			addFee(accountFeesEntity, accountFeesSet);
		}
		return accountFeesSet;
	}

	private void addFee(AccountFeesEntity accountFeesEntity,
			Set<AccountFees> accountFeesSet) {
		if (accountFeesEntity.getFeeStatus() == null
				|| accountFeesEntity.getFeeStatus().equals(
						AccountConstants.ACTIVE_FEES))
			accountFeesSet.add(getAccountFees(accountFeesEntity
					.getAccountFeeId()));

	}

	public void generateMeetingsForNextYear() throws RepaymentScheduleException {

		RepaymentScheduleInputsIfc repaymntScheduleInputs = RepaymentScheduleFactory
				.getRepaymentScheduleInputs();
		RepaymentScheduleIfc repaymentScheduler = RepaymentScheduleFactory
				.getRepaymentScheduler();

		MeetingBO meetingBO = getCustomer().getCustomerMeeting().getMeeting();
		Meeting meeting = convertM2StyleToM1(meetingBO);
		meeting.setMeetingStartDate(DateUtils.getFistDayOfNextYear(Calendar
				.getInstance()));
		repaymntScheduleInputs.setMeeting(meeting);
		repaymntScheduleInputs
				.setMeetingToConsider(RepaymentScheduleConstansts.MEETING_CUSTOMER);
		repaymntScheduleInputs.setRepaymentFrequency(meeting);

		repaymntScheduleInputs.setAccountFee(getAccountFeesSet());
		repaymentScheduler.setRepaymentScheduleInputs(repaymntScheduleInputs);
		RepaymentSchedule repaymentSchedule = repaymentScheduler
				.getRepaymentSchedule();
		Set<AccountActionDateEntity> installments = RepaymentScheduleHelper
				.getActionDateEntity(repaymentSchedule, "customer", this,
						getCustomer(), getLastInstallmentId());
		getAccountActionDates().addAll(installments);

	}

	/* Need to remove while refactoring */
	public void setOffice(OfficeBO office) {
		this.office = office;
	}

	/* Need to remove while refactoring */
	public void setGlobalAccountNum(String globalAccountNum) {
		this.globalAccountNum = globalAccountNum;
	}

	/* Need to remove while refactoring */
	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	/* Need to remove while refactoring */
	public void setCustomer(CustomerBO customer) {
		this.customer = customer;
	}

	/* Need to remove while refactoring */
	public void setPersonnel(PersonnelBO personnel) {
		this.personnel = personnel;
	}

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

}
