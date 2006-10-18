package org.mifos.application.accounts.loan.business;

import java.sql.Date;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.helpers.DateUtils;

public class LoanPerformanceHistoryEntity extends PersistentObject {

	private final Integer id;

	private final LoanBO loan;

	private Integer noOfPayments;

	private Date loanMaturityDate;

	protected LoanPerformanceHistoryEntity() {
		super();
		this.id = null;
		this.loan = null;
	}

	public LoanPerformanceHistoryEntity(LoanBO loan) {
		this.id = null;
		this.loan = loan;
		this.noOfPayments = 0;
	}
	
	public Date getLoanMaturityDate() {
		return loanMaturityDate;
	}

	void setLoanMaturityDate(Date loanMaturityDate) {
		this.loanMaturityDate = loanMaturityDate;
	}

	public Integer getNoOfPayments() {
		return noOfPayments;
	}

	void setNoOfPayments(Integer noOfPayments) {
		this.noOfPayments = noOfPayments;
	}
	
	public Short getDaysInArrears() {
		return loan.getDaysInArrears();
	}
	
	public Integer getTotalNoOfMissedPayments() {
		int noOfMissedPayments = 0;
		if (loan.getAccountState().getId().equals(
				AccountStates.LOANACC_ACTIVEINGOODSTANDING)
				|| loan.getAccountState().getId().equals(
						AccountStates.LOANACC_OBLIGATIONSMET)
				|| loan.getAccountState().getId().equals(
						AccountStates.LOANACC_WRITTENOFF)
				|| loan.getAccountState().getId().equals(
						AccountStates.LOANACC_RESCHEDULED)
				|| loan.getAccountState().getId().equals(
						AccountStates.LOANACC_BADSTANDING)) {
			List<AccountActionDateEntity> accountActionDateList = loan.getDetailsOfInstallmentsInArrears();
			if (!accountActionDateList.isEmpty())
				noOfMissedPayments = +accountActionDateList.size();
			noOfMissedPayments = noOfMissedPayments
					+ getNoOfBackDatedPayments();
		}
		return noOfMissedPayments;
	}
	
	private Integer getNoOfBackDatedPayments() {
		int noOfMissedPayments = 0;
		for (AccountPaymentEntity accountPaymentEntity : loan.getAccountPayments()) {
			Set<AccountTrxnEntity> accountTrxnEntityList = accountPaymentEntity
					.getAccountTrxns();
			for (AccountTrxnEntity accountTrxnEntity : accountTrxnEntityList) {
				if (accountTrxnEntity.getAccountActionEntity().getId().equals(
						AccountConstants.ACTION_LOAN_REPAYMENT)
						&& DateUtils
								.getDateWithoutTimeStamp(
										accountTrxnEntity.getActionDate()
												.getTime())
								.compareTo(
										DateUtils
												.getDateWithoutTimeStamp(accountTrxnEntity
														.getDueDate().getTime())) > 0) {
					noOfMissedPayments++;
				}
				if (accountTrxnEntity.getAccountActionEntity().getId().equals(
						AccountConstants.ACTION_LOAN_ADJUSTMENT)
						&& DateUtils
								.getDateWithoutTimeStamp(
										accountTrxnEntity.getRelatedTrxn()
												.getActionDate().getTime())
								.compareTo(
										DateUtils
												.getDateWithoutTimeStamp(accountTrxnEntity
														.getRelatedTrxn()
														.getDueDate().getTime())) > 0) {
					noOfMissedPayments--;
				}
			}
		}
		return noOfMissedPayments;
	}
}
