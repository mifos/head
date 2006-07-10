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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountHelper;
import org.mifos.application.accounts.util.helpers.AccountPaymentData;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.accounts.util.helpers.CustomerAccountPaymentData;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.business.CustomerTrxnDetailEntity;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.fees.business.FeesBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.service.PersonnelPersistenceService;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleException;
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.framework.components.scheduler.SchedulerIntf;
import org.mifos.framework.components.scheduler.helpers.SchedulerHelper;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.Money;

/**
 * @author ashishsm
 * 
 */
public class CustomerAccountBO extends AccountBO {

	Set<CustomerActivityEntity> customerActivitDetails = null;

	public CustomerAccountBO() {
		super();
		customerActivitDetails = new HashSet<CustomerActivityEntity>();
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
		AccountPaymentEntity accountPayment = new AccountPaymentEntity();
		accountPayment.setPaymentDetails(paymentData.getTotalAmount(),
				paymentData.getRecieptNum(), paymentData.getRecieptDate(),
				paymentData.getPaymentTypeId());
		for (AccountPaymentData accountPaymentData : paymentData
				.getAccountPayments()) {
			AccountActionDateEntity accountAction = getAccountActionDate(accountPaymentData
					.getInstallmentId());
			if (accountAction.getPaymentStatus().equals(
					AccountConstants.PAYMENT_PAID))
				throw new AccountException("errors.update",
						new String[] { getGlobalAccountNum() });
			CustomerAccountPaymentData customerAccountPaymentData = (CustomerAccountPaymentData) accountPaymentData;
			accountAction.setPaymentDetails(customerAccountPaymentData,
					new java.sql.Date(paymentData.getTransactionDate()
							.getTime()));

			CustomerTrxnDetailEntity accountTrxn = new CustomerTrxnDetailEntity();
			accountTrxn.setAccount(this);
			accountTrxn.setPaymentDetails(accountAction,
					customerAccountPaymentData, paymentData.getPersonnelId(),
					paymentData.getTransactionDate());
			accountPayment.addAcountTrxn(accountTrxn);
			addCustomerActivity(buildCustomerActivity(paymentData
					.getTotalAmount(), "Payment rcvd.", paymentData
					.getPersonnelId()));
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
			Money totalAmountAdj=new Money();
			for (AccountTrxnEntity accntTrxn : reversedTrxns) {
				CustomerTrxnDetailEntity custTrxn = (CustomerTrxnDetailEntity) accntTrxn;
				AccountActionDateEntity accntActionDate = getAccountActionDate(custTrxn
						.getInstallmentId());
				accntActionDate.setPaymentStatus(AccountConstants.PAYMENT_UNPAID);
				accntActionDate.setPaymentDate(null);
				accntActionDate.setMiscFeePaid(accntActionDate.getMiscFeePaid()
						.add(custTrxn.getMiscFeeAmount()));
				totalAmountAdj=totalAmountAdj.add(removeSign(custTrxn.getMiscFeeAmount()));
				accntActionDate.setMiscPenaltyPaid(accntActionDate
						.getMiscPenaltyPaid().add(custTrxn.getMiscPenaltyAmount()));
				totalAmountAdj=totalAmountAdj.add(removeSign(custTrxn.getMiscPenaltyAmount()));
				if (null != accntActionDate.getAccountFeesActionDetails()
						&& accntActionDate.getAccountFeesActionDetails().size() > 0) {
					for (AccountFeesActionDetailEntity accntFeesAction : accntActionDate
							.getAccountFeesActionDetails()) {
						Money feeAmntAdjusted = custTrxn.getFeesTrxn(
								accntFeesAction.getAccountFee().getAccountFeeId())
								.getFeeAmount();
						accntFeesAction.setFeeAmountPaid(accntFeesAction
								.getFeeAmountPaid().add(feeAmntAdjusted));
						totalAmountAdj=totalAmountAdj.add(removeSign(feeAmntAdjusted));
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
		Money chargeWaived = accountActionDateEntity.waiveCharges();
		if (chargeWaived != null && chargeWaived.getAmountDoubleValue() > 0.0) {
			addCustomerActivity(buildCustomerActivity(chargeWaived,
					"Amnt waived", userContext.getId()));
		}
		getAccountPersistenceService().update(this);
	}

	@Override
	public void waiveAmountOverDue(WaiveEnum chargeType) throws ServiceException {
		Money chargeWaived = new Money();
		List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForDueInstallments();
		accountActionDateList.remove(accountActionDateList.size() - 1);
		for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
			chargeWaived = chargeWaived.add(accountActionDateEntity
					.waiveCharges());
		}
		if (chargeWaived != null && chargeWaived.getAmountDoubleValue() > 0.0) {
			addCustomerActivity(buildCustomerActivity(chargeWaived,
					"Amnt waived", userContext.getId()));
		}
		getAccountPersistenceService().update(this);
	}

	public void applyPeriodicFees(Date date) throws RepaymentScheduleException, SchedulerException, PersistenceException, ServiceException {		
		Set<AccountActionDateEntity> accountActionDateSet = getAccountActionDates();		
		for (AccountActionDateEntity accountActionDate : accountActionDateSet) {		
			if (date.equals(accountActionDate.getActionDate())) {				
				List<AccountFeesEntity> periodicFeeList = getPeriodicFeeList();		
				for (AccountFeesEntity accountFeesEntity : periodicFeeList) {		
					if (accountFeesEntity.isApplicable(date) == true) {
						accountFeesEntity.setLastAppliedDate(date);
						accountActionDate.applyPeriodicFees(accountFeesEntity.getFees().getFeeId());						
						FeesBO feesBO = getAccountFeesObject(accountFeesEntity.getFees().getFeeId());
						String description = feesBO.getFeeName()+ " " + AccountConstants.FEES_APPLIED;		
						updateAccountActivity(feesBO.getFeeAmount(),null,description);						
						getAccountPersistenceService().save(this);						
					}
				}
				break;
			}
		}
	}

	private CustomerActivityEntity buildCustomerActivity(Money amount,
			String description, Short personnelId) {
		PersonnelBO personnel =null;
		if(personnelId!=null){
			personnel = new PersonnelPersistenceService()
				.getPersonnel(personnelId);
		}
		return new CustomerActivityEntity(personnel, description, amount);
	}
	
	public void updateAccountActivity(Money totalAmount,Short personnelId,String description){
		this.addCustomerActivity(buildCustomerActivity(totalAmount,description,personnelId));
	}

	protected Money getDueAmount(AccountActionDateEntity installment){
		return installment.getTotalDueWithFees();
	}

	@Override
	protected void regenerateFutureInstallments(Short nextIntallmentId) throws HibernateException, ServiceException, SchedulerException {
		if (!this.getCustomer().getCustomerStatus().getStatusId().equals(
				ClientConstants.STATUS_CANCELLED)
				&& !this.getCustomer().getCustomerStatus().getStatusId()
						.equals(ClientConstants.STATUS_CLOSED)
				&& !this.getCustomer().getCustomerStatus().getStatusId()
						.equals(GroupConstants.CANCELLED)
				&& !this.getCustomer().getCustomerStatus().getStatusId()
						.equals(GroupConstants.CLOSED)) {
			SchedulerIntf scheduler = SchedulerHelper.getScheduler(getCustomer().getCustomerMeeting().getMeeting());
			List<Date> meetingDates= scheduler.getAllDates();
			meetingDates.remove(0);
			deleteFutureInstallments();
			for (Date date : meetingDates) {
				addAccountActionDate(AccountHelper.createEmptyInstallment(date,
						getCustomer(), nextIntallmentId++));
			}
		}
	}

}
