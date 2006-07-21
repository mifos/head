package org.mifos.framework.components.cronjobs.helpers;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.CustomerAccountBO;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.valueobjects.AccountActionDate;
import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.accounts.util.valueobjects.AccountFeesActionDetail;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.productdefinition.struts.action.PaymentTypesAction;
import org.mifos.application.productdefinition.util.valueobjects.PaymentType;
import org.mifos.framework.components.cronjobs.TaskHelper;
import org.mifos.framework.components.repaymentschedule.RepaymentSchedule;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleConstansts;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleException;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleFactory;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleHelper;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleIfc;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleInputsIfc;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;

public class GenMeetingsForCustomerNSavingsHelper extends TaskHelper {

	@Override
	public void execute(long timeInMillis) {
		try {

			AccountPersistanceService accountPersistanceService = new AccountPersistanceService();
			List<CustomerAccountBO> customerAccounts = accountPersistanceService
					.getActiveCustomerAccounts();
			for (CustomerAccountBO accountBO : customerAccounts) {
				HibernateUtil.startTransaction();
				generateInstallmentForNextYear(accountBO,
						accountPersistanceService.getLastInstallment(accountBO
								.getAccountId()));
				HibernateUtil.commitTransaction();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void generateInstallmentForNextYear(CustomerAccountBO accountBO,
			AccountActionDateEntity installment)
			throws RepaymentScheduleException {
		RepaymentScheduleInputsIfc repaymntScheduleInputs = RepaymentScheduleFactory
				.getRepaymentScheduleInputs();
		RepaymentScheduleIfc repaymentScheduler = RepaymentScheduleFactory
				.getRepaymentScheduler();
		MeetingBO meetingBO = accountBO.getCustomer().getCustomerMeeting()
				.getMeeting();
		Meeting meeting = convertM2StyleToM1(meetingBO);
		meeting.setMeetingStartDate(DateUtils.getFistDayOfNextYear(Calendar
				.getInstance()));
		repaymntScheduleInputs.setMeeting(meeting);
		repaymntScheduleInputs
				.setMeetingToConsider(RepaymentScheduleConstansts.MEETING_CUSTOMER);
		repaymntScheduleInputs.setRepaymentFrequency(meeting);
		repaymntScheduleInputs.setAccountFee(getAccountFeesSet(accountBO));
		repaymentScheduler.setRepaymentScheduleInputs(repaymntScheduleInputs);
		RepaymentSchedule repaymentSchedule = repaymentScheduler
				.getRepaymentSchedule();
		Set<AccountActionDateEntity> installments = RepaymentScheduleHelper
				.getActionDateEntity(repaymentSchedule);
		Short lastInstallmentId = installment.getInstallmentId();
		for (AccountActionDateEntity date : installments) {
			date.setAccount(accountBO);
			date.setCustomer(accountBO.getCustomer());
			date
					.setInstallmentId((short) (date.getInstallmentId() + lastInstallmentId));
			date.setPaymentStatus(AccountConstants.PAYMENT_UNPAID);
		}
		accountBO.getAccountActionDates().addAll(installments);
	}

	// TODO this method will go when code repayment schedule generator will move
	// to m2 style

	private Meeting convertM2StyleToM1(MeetingBO meeting) {

		Meeting meetingM1 = null;
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			meetingM1 = (Meeting) session.get(Meeting.class, meeting
					.getMeetingId());
		} catch (HibernateProcessException e) {
			e.printStackTrace();
		} finally {
			try {
				HibernateUtil.closeSession(session);
			} catch (HibernateProcessException e) {
				e.printStackTrace();
			}
		}
		return meetingM1;
	}

	// TODO this method will go once scheduler is moved to m2 style
	private AccountFees getAccountFees(Integer accountFeeId) {
		AccountFees accountFees = new AccountFees();
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			accountFees = (AccountFees) session.get(AccountFees.class,
					accountFeeId);
			Fees fees = accountFees.getFees();
			initializeMeetings(fees);
			if (null != fees) {
				fees.getFeeFrequency().getFeeFrequencyId();
			}
		} catch (HibernateProcessException e) {
			e.printStackTrace();
		} finally {
			try {
				HibernateUtil.closeSession(session);
			} catch (HibernateProcessException e) {
				e.printStackTrace();
			}
		}
		Hibernate.initialize(accountFees);
		return accountFees;
	}

	private void initializeMeetings(Fees fees) {

		if (fees.getFeeFrequency().getFeeFrequencyTypeId().equals(
				FeeFrequencyType.PERIODIC.getValue())) {
			Meeting meeting = fees.getFeeFrequency().getFeeMeetingFrequency();
			meeting.getMeetingType().getMeetingPurpose();
		}

	}

	private Set<AccountFees> getAccountFeesSet(CustomerAccountBO accountBO) {
		Set<AccountFees> accountFeesSet = new HashSet<AccountFees>();
		for (AccountFeesEntity accountFeesEntity : accountBO.getAccountFees()) {
			initAccFeeIntNotDeductedAtDisbursal(accountFeesEntity,
					accountFeesSet);

		}

		return accountFeesSet;
	}

	private void initAccFeeIntNotDeductedAtDisbursal(
			AccountFeesEntity accountFeesEntity, Set<AccountFees> accountFeesSet) {

		addFee(accountFeesEntity, accountFeesSet);

	}

	private void addFee(AccountFeesEntity accountFeesEntity,
			Set<AccountFees> accountFeesSet) {
		if (accountFeesEntity.getFeeStatus() == null
				|| accountFeesEntity.getFeeStatus().equals(
						AccountConstants.ACTIVE_FEES))
			accountFeesSet.add(getAccountFees(accountFeesEntity
					.getAccountFeeId()));

	}
}
