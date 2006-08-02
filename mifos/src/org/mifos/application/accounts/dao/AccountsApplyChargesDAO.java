/**

 * AccountsApplyChargesDAO.java    version: 1.0

 

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
package org.mifos.application.accounts.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.business.CustomerActivityEntity;
import org.mifos.application.accounts.exceptions.AccountsApplyChargesException;
import org.mifos.application.accounts.loan.util.valueobjects.Loan;
import org.mifos.application.accounts.loan.util.valueobjects.LoanActivity;
import org.mifos.application.accounts.loan.util.valueobjects.LoanSummary;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.accounts.util.valueobjects.Account;
import org.mifos.application.accounts.util.valueobjects.AccountActionDate;
import org.mifos.application.accounts.util.valueobjects.AccountApplyChargesMaster;
import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.accounts.util.valueobjects.AccountFeesActionDetail;
import org.mifos.application.accounts.util.valueobjects.AccountsApplyCharges;
import org.mifos.application.accounts.util.valueobjects.CustomerAccount;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.customer.util.valueobjects.CustomerMeeting;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fees.util.helpers.FeeConstants;
import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.meeting.util.valueobjects.MeetingDetails;
import org.mifos.application.meeting.util.valueobjects.MeetingRecurrence;
import org.mifos.application.meeting.util.valueobjects.RecurrenceType;
import org.mifos.application.office.util.helpers.OfficeHelper;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.dao.helpers.MasterDataRetriever;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.valueobjects.Context;

/**
 * This is dao for the AccountsApplyCharges action
 * 
 * @author rajenders
 * 
 */
public class AccountsApplyChargesDAO extends DAO {

	public void loadFeeMasterData(Context context) throws SystemException,
			ApplicationException {
		Session session = null;
		Transaction transaction = null;
		List<AccountApplyChargesMaster> accountApplyChargesMasterList = new ArrayList<AccountApplyChargesMaster>();
		List<Fees> feeList = null;
		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();
			AccountsApplyCharges aac = (AccountsApplyCharges) context
					.getValueObject();
			String comingFrom = aac.getInput();

			// if account is loan type, query has to be different
			StringBuilder queryString = new StringBuilder();

			queryString
					.append("from org.mifos.application.fees.util.valueobjects.Fees as fees where fees.status=1 ");

			if (comingFrom.equals(AccountConstants.APPLYLOANCHARGES)) {
				queryString.append(" and fees.categoryId ="
						+ FeeCategory.LOAN.getValue());
			} else if (comingFrom.equals(AccountConstants.VIEWCENTERCHARGES)) {
				queryString.append(" and ( fees.categoryId ="
						+ FeeCategory.CENTER.getValue());
				queryString.append(" or fees.categoryId ="
						+ FeeCategory.ALLCUSTOMERS.getValue());
				queryString.append(" )");
			} else if (comingFrom.equals(AccountConstants.VIEWGROUPCHARGES)) {
				queryString.append(" and ( fees.categoryId ="
						+ FeeCategory.GROUP.getValue());
				queryString.append(" or fees.categoryId ="
						+ FeeCategory.ALLCUSTOMERS.getValue());
				queryString.append(" )");
			} else if (comingFrom.equals(AccountConstants.VIEWCLIENTCHARGES)) {
				queryString.append(" and ( fees.categoryId ="
						+ FeeCategory.CLIENT.getValue());
				queryString.append(" or fees.categoryId ="
						+ FeeCategory.ALLCUSTOMERS.getValue());
				queryString.append(" )");
			}

			Integer accountId = aac.getAccountId();

			queryString
					.append(" and fees.feeId not in ( select af.fees.feeId from AccountFees af where af.account.accountId=:accountId and af.fees.feeFrequency.feeFrequencyTypeId=:feeFrequencyTypeId and (af.feeStatus=null or af.feeStatus=1))");
			feeList = session.createQuery(queryString.toString()).setInteger(
					"accountId", accountId).setShort("feeFrequencyTypeId",
					FeeFrequencyType.PERIODIC.getValue()).list();

			if (feeList != null) {

				for (Fees fees : feeList) {
					AccountApplyChargesMaster accountApplyChargesMaster = new AccountApplyChargesMaster();
					populateFeeDetailsInAccountApplyChargeMaster(
							accountApplyChargesMaster, fees);
					accountApplyChargesMasterList
							.add(accountApplyChargesMaster);
				}

				Account account = (Account) session.get(Account.class,
						accountId);

				// Check for same recurrenceType
				accountApplyChargesMasterList = checkForRecurranceType(
						accountApplyChargesMasterList, account);

				boolean currentDateGreaterThenFirstInstallmentDate = isCurrentDateGreaterThenFirstInstallmentDate(
						account, session);
				// Filtering fees that are time of disbursment type when account
				// type is loan
				filteringTimeOfDisbursmentFees(accountApplyChargesMasterList,
						account, currentDateGreaterThenFirstInstallmentDate);
				// Filtering fees that are time of disbursment type when account
				// type is loan
				filteringTimeOfFirstRepaymentFees(
						accountApplyChargesMasterList, account,
						currentDateGreaterThenFirstInstallmentDate);

			}

			// Adding misc penalty and misc fees to the
			// accountApplyChargesMasterList
			addMiscFeeAndPenalty(accountApplyChargesMasterList);

			transaction.commit();
		} catch (HibernateProcessException e) {
			transaction.rollback();
			throw new SystemException(e);
		} catch (HibernateException he) {
			transaction.rollback();
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}

		OfficeHelper.saveInContext("FeeMaster", accountApplyChargesMasterList,
				context);
	}

	public void loadFormulaMaster(Context context) throws SystemException,
			ApplicationException {
		MasterDataRetriever masterDataRetriever = new MasterDataRetriever();

		context
				.addAttribute(masterDataRetriever.retrieveMasterData(
						FeeConstants.FEEFORMULA, context.getUserContext()
								.getLocaleId(), FeeConstants.FORMULA,
						AccountConstants.FEEFORMULAPATH,
						AccountConstants.FEEFORMULAID));

	}

	public Account getAccount(Integer accountId) throws SystemException,
			ApplicationException {
		Session session = null;
		Transaction transaction = null;
		Account account = null;
		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();
			Query query = session
					.createQuery("from org.mifos.application.accounts.util.valueobjects.Account as acc where acc.accountId=?");
			query.setInteger(0, accountId);
			account = (Account) query.uniqueResult();
			Set<AccountFees> accountFeesSet = account.getAccountFeesSet();

			if (accountFeesSet != null) {
				for (AccountFees accountFees : accountFeesSet) {
					Meeting meeting = accountFees.getFees().getFeeFrequency()
							.getFeeMeetingFrequency();
					if (meeting != null) {
						meeting.getMeetingStartDate();
					}
				}
			}

			if (account instanceof Loan) {
				Loan loan = (Loan) account;
				loan.getLoanOffering().getPrinDueLastInstFlag();
				Meeting meeting = loan.getLoanMeeting();
				loan.getLoanSummary();
				if (null != meeting) {

					MeetingDetails md = meeting.getMeetingDetails();
					md.getRecurAfter();
					if (null != md) {
						MeetingRecurrence mr = md.getMeetingRecurrence();
						if (null != mr) {
							mr.getRankOfDays();
						}

						RecurrenceType rt = md.getRecurrenceType();
						if (null != rt) {
							rt.getRecurrenceName();
						}
					}
				} else {
					throw new AccountsApplyChargesException(
							AccountConstants.KEYNOMEETING);
				}
			}
			if (account instanceof CustomerAccount) {
				Meeting meeting = null;
				account.getAccountFeesSet();
				Customer customer = account.getCustomer();
				if (null != customer) {
					CustomerMeeting cm = customer.getCustomerMeeting();

					if (null != cm) {
						meeting = cm.getMeeting();
						if (null != meeting) {

							MeetingDetails md = meeting.getMeetingDetails();
							md.getRecurAfter();
							if (null != md) {
								MeetingRecurrence mr = md
										.getMeetingRecurrence();
								if (null != mr) {
									mr.getRankOfDays();
								}

								RecurrenceType rt = md.getRecurrenceType();
								if (null != rt) {
									rt.getRecurrenceName();
								}
							}
						} else {
							throw new AccountsApplyChargesException(
									AccountConstants.KEYNOMEETING);
						}
					} else {

						throw new AccountsApplyChargesException(
								AccountConstants.KEYNOMEETING);

					}

				}
			}
			transaction.commit();
		} catch (HibernateProcessException e) {
			transaction.rollback();
			throw new SystemException(e);

		} catch (HibernateException he) {
			transaction.rollback();
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}

		return account;
	}

	public List<AccountActionDate> getAccountActionDate(Integer accountId)
			throws SystemException, ApplicationException {
		Session session = null;
		Transaction transaction = null;
		List<AccountActionDate> accountActionDateList = null;
		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();

			accountActionDateList = session
					.createQuery(
							" from org.mifos.application.accounts.util.valueobjects.AccountActionDate as aad  where aad.actionDate>=current_date and aad.paymentStatus=:paymentStatus and aad.account.accountId=:accountId order by aad.installmentId")
					.setShort("paymentStatus", PaymentStatus.UNPAID.getValue())
					.setInteger("accountId", accountId).list();

			transaction.commit();

		} catch (HibernateProcessException e) {
			transaction.rollback();
			throw new SystemException(e);

		} catch (HibernateException he) {
			transaction.rollback();
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}

		return accountActionDateList;
	}

	public Boolean doesLastPaidInstallmentFallsOnCurrentDate(Integer accountId,
			Short paymentStatus) throws SystemException, ApplicationException {
		HashMap queryParameters = new HashMap();
		queryParameters.put("accountId", accountId);
		queryParameters.put("paymentStatus", paymentStatus);
		List queryResult = executeNamedQuery(
				NamedQueryConstants.GET_LASTPAIDINSTALLMNENT_ON_CURRENTDATE,
				queryParameters);
		if (null != queryResult && queryResult.size() > 0) {
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"After executing the query . ");
			return true;
		}
		return false;
	}

	public void saveAccountActionDateList(List accountActionDateList,
			AccountFees accountFee, LoanSummary loanSummary,
			LoanActivity accountNonTrxn,
			CustomerActivityEntity customerActivityEntity)
			throws SystemException, ApplicationException {
		Session session = null;
		Transaction transaction = null;

		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();

			if (accountFee != null)
				session.saveOrUpdate(accountFee);

			int count = accountActionDateList.size();
			AccountActionDate accountActionDate = null;

			for (int i = 0; i < count; i++) {
				accountActionDate = (AccountActionDate) accountActionDateList
						.get(i);
				session.update(accountActionDate);
			}

			if (null != loanSummary)
				session.update(loanSummary);

			if (accountNonTrxn != null)
				session.save(accountNonTrxn);

			if (customerActivityEntity != null)
				session.save(customerActivityEntity);

			transaction.commit();

		} catch (HibernateProcessException e) {
			transaction.rollback();
			throw new SystemException(e);

		} catch (HibernateException he) {
			transaction.rollback();
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * This method adds misc fees and misc penalty to the list
	 * 
	 * @param feeList
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	private void addMiscFeeAndPenalty(List feeList) throws SystemException,
			ApplicationException {
		if (feeList == null) {
			feeList = new ArrayList();
		}
		AccountApplyChargesMaster accountApplyChargesMaster = new AccountApplyChargesMaster();
		accountApplyChargesMaster.setFeeId(AccountConstants.MISC_FEES);
		accountApplyChargesMaster.setFeeName("Misc Fees");
		feeList.add(accountApplyChargesMaster);
		accountApplyChargesMaster = new AccountApplyChargesMaster();
		accountApplyChargesMaster.setFeeId(AccountConstants.MISC_PENALTY);
		accountApplyChargesMaster.setFeeName("Misc Penalty");
		feeList.add(accountApplyChargesMaster);
	}

	/**
	 * This method fetched an array of accountActionDate objects for next
	 * installments for a given accountid
	 * 
	 * @param accountId
	 * @return
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public AccountActionDate getNextInstallmentDetail(Integer accountId)
			throws ApplicationException, SystemException {

		AccountActionDate acct = null;

		Session session = null;
		Transaction transaction = null;
		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();
			Query query = session
					.getNamedQuery(NamedQueryConstants.RETRIEVE_NEXT_INTALLMENT);
			query.setInteger("accountId", accountId);
			Integer id = (Integer) query.uniqueResult();
			if (id != null && id != 0) {
				acct = (AccountActionDate) session.get(AccountActionDate.class,
						id);
				// get the associated fees details as well
				acct.getAccountFeesActionDetail();
				for (AccountFeesActionDetail feeDetail : acct
						.getAccountFeesActionDetail())
					feeDetail.getAccountFee().getFees().getFeeName();
			}
			transaction.commit();
		} catch (HibernateProcessException hbe) {
			hbe.printStackTrace();
			transaction.rollback();
			throw new ApplicationException(hbe);
		} finally {
			HibernateUtil.closeSession(session);
		}

		return acct;
	}

	public Short getReccurenceType(Account account) throws SystemException,
			ApplicationException {
		Short reccurenceType = null;
		if (account instanceof Loan) {
			Loan loan = (Loan) account;
			Meeting meeting = loan.getLoanMeeting();
			loan.getLoanSummary();
			if (null != meeting) {

				MeetingDetails md = meeting.getMeetingDetails();
				md.getRecurAfter();
				if (null != md) {
					MeetingRecurrence mr = md.getMeetingRecurrence();
					if (null != mr) {
						mr.getRankOfDays();
					}

					RecurrenceType rt = md.getRecurrenceType();
					if (null != rt) {
						reccurenceType = rt.getRecurrenceId();
					}
				}
			} 
		}
		if (account instanceof CustomerAccount) {
			Meeting meeting = null;
			account.getAccountFeesSet();
			Customer customer = account.getCustomer();
			if (null != customer) {
				CustomerMeeting cm = customer.getCustomerMeeting();

				if (null != cm) {
					meeting = cm.getMeeting();
					if (null != meeting) {

						MeetingDetails md = meeting.getMeetingDetails();
						md.getRecurAfter();
						if (null != md) {
							MeetingRecurrence mr = md.getMeetingRecurrence();
							if (null != mr) {
								mr.getRankOfDays();
							}

							RecurrenceType rt = md.getRecurrenceType();
							if (null != rt) {
								reccurenceType = rt.getRecurrenceId();
							}
						}
					}
				}
			}
		}
		return reccurenceType;
	}

	/*
	 * This method populates accountApplyChargesMaster with recurrence type,
	 * periodicity and payment type.
	 */
	private void populateFeeDetailsInAccountApplyChargeMaster(
			AccountApplyChargesMaster accountApplyChargesMaster, Fees fees)
			throws SystemException {
		accountApplyChargesMaster.setFeeId(fees.getFeeId());
		accountApplyChargesMaster.setFeeName(fees.getFeeName());
		accountApplyChargesMaster.setRateOrAmount(fees.getRateOrAmount());
		accountApplyChargesMaster.setRateFlatFalg(fees.getRateFlatFalg());
		accountApplyChargesMaster.setFormulaId(fees.getFormulaId());
		Meeting meeting = fees.getFeeFrequency().getFeeMeetingFrequency();
		if (meeting != null) {
			accountApplyChargesMaster.setRecurrenceTypeId(meeting
					.getMeetingDetails().getRecurrenceType().getRecurrenceId());
			accountApplyChargesMaster.setPeriodicity(meeting
					.getFeeMeetingSchedule().toString());
		}
		Short paymentType = fees.getFeeFrequency().getFeePaymentId();
		if (paymentType != null) {
			accountApplyChargesMaster.setPaymentType(paymentType);
		}
	}

	/*
	 * This method checks the recurrence type of fees and account and filters
	 * feeList.
	 */
	private List<AccountApplyChargesMaster> checkForRecurranceType(
			List<AccountApplyChargesMaster> feeList, Account account)
			throws SystemException, ApplicationException {
		Short accountReccurenceTypeId = getReccurenceType(account);
		List<AccountApplyChargesMaster> chargeFeeList = new ArrayList<AccountApplyChargesMaster>();
		for (AccountApplyChargesMaster accountApplyChargesMaster : feeList) {
			Short feesReccurenceTypeId = accountApplyChargesMaster
					.getRecurrenceTypeId();
			if (feesReccurenceTypeId == null) {
				chargeFeeList.add(accountApplyChargesMaster);
			} else if (feesReccurenceTypeId != null
					&& feesReccurenceTypeId.equals(accountReccurenceTypeId)) {
				chargeFeeList.add(accountApplyChargesMaster);
			}
		}
		return chargeFeeList;
	}

	/*
	 * Checks whether current date is greater than account's first installment
	 * date.
	 */
	private boolean isCurrentDateGreaterThenFirstInstallmentDate(
			Account account, Session session) throws ApplicationException {
		Short id = null;
		Query query = null;
		try {
			query = session
					.getNamedQuery(NamedQueryConstants.ACCOUNT_ISCURRENTDATEGREATERTHENFIRSTINSTALLMENTDATE);
			query.setInteger("accountId", account.getAccountId());
			id = (Short) query.uniqueResult();
		} catch (HibernateException e) {
			throw new AccountsApplyChargesException(e);
		}
		return (id != null ? true : false);
	}

	/*
	 * Filters fees that are time of disbursment type when account type is loan
	 */
	private void filteringTimeOfDisbursmentFees(
			List<AccountApplyChargesMaster> feeList, Account account,
			boolean currentDateGreaterThenFirstInstallmentDate)
			throws SystemException, ApplicationException {
		if (account.getAccountTypeId().equals(AccountConstants.LOAN_TYPE)) {
			Iterator<AccountApplyChargesMaster> accountApplyChargesMasterList = feeList
					.iterator();
			while (accountApplyChargesMasterList.hasNext()) {
				AccountApplyChargesMaster accountApplyChargesMaster = accountApplyChargesMasterList
						.next();
				Short paymentType = accountApplyChargesMaster.getPaymentType();
				if (paymentType != null
						&& paymentType.equals(FeePayment.TIME_OF_DISBURSMENT
								.getValue())) {
					Short accountState = account.getAccountStateId();
					if ((accountState
							.equals(AccountStates.LOANACC_PARTIALAPPLICATION)
							|| accountState
									.equals(AccountStates.LOANACC_PENDINGAPPROVAL)
							|| accountState
									.equals(AccountStates.LOANACC_APPROVED) || accountState
							.equals(AccountStates.LOANACC_DBTOLOANOFFICER))) {
						continue;
					} else {
						accountApplyChargesMasterList.remove();
					}
				}
			}
		}
	}

	/*
	 * Filters fees that are time of disbursment type when account type is loan
	 */
	private void filteringTimeOfFirstRepaymentFees(
			List<AccountApplyChargesMaster> feeList, Account account,
			boolean currentDateGreaterThenFirstInstallmentDate)
			throws SystemException, ApplicationException {
		if (account.getAccountTypeId().equals(AccountConstants.LOAN_TYPE)) {
			Iterator<AccountApplyChargesMaster> accountApplyChargesMasterList = feeList
					.iterator();
			while (accountApplyChargesMasterList.hasNext()) {
				AccountApplyChargesMaster accountApplyChargesMaster = accountApplyChargesMasterList
						.next();
				Short paymentType = accountApplyChargesMaster.getPaymentType();
				if (paymentType != null
						&& paymentType
								.equals(FeePayment.TIME_OF_FIRSTLOANREPAYMENT
										.getValue())) {
					if (currentDateGreaterThenFirstInstallmentDate) {
						accountApplyChargesMasterList.remove();
					}
				}
			}
		}
	}

}
