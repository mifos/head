package org.mifos.application.accounts.loan.business;

import java.util.Iterator;
import java.util.Set;

import org.mifos.application.accounts.TestAccount;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.util.helpers.OverDueAmounts;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.framework.util.helpers.Money;


public class TestLoanScheduleEntity extends TestAccount {

	public void testGetPrincipalDue() {
		LoanScheduleEntity accountActionDate = (LoanScheduleEntity) accountBO
				.getAccountActionDates().toArray()[0];
		accountActionDate.setPrincipalPaid(new Money("10.0"));
		assertEquals(90.0, accountActionDate.getPrincipalDue()
				.getAmountDoubleValue());

	}

	public void testGetInterestDue() {
		LoanScheduleEntity accountActionDate = (LoanScheduleEntity) accountBO
				.getAccountActionDates().toArray()[0];
		accountActionDate.setInterestPaid(new Money("2.0"));
		assertEquals(10.0, accountActionDate.getInterestDue()
				.getAmountDoubleValue());

	}

	public void testGetPenaltyDue() {
		LoanScheduleEntity accountActionDate = (LoanScheduleEntity) accountBO
				.getAccountActionDates().toArray()[0];
		accountActionDate.setPenalty(new Money("20.0"));
		accountActionDate.setPenaltyPaid(new Money("5.0"));
		accountActionDate.setMiscPenalty(new Money("10.0"));
		accountActionDate.setMiscPenaltyPaid(new Money("5.0"));
		assertEquals(20.0, accountActionDate.getPenaltyDue()
				.getAmountDoubleValue());

	}

	public void testGetTotalDue() {
		LoanScheduleEntity accountActionDate = (LoanScheduleEntity) accountBO
				.getAccountActionDates().toArray()[0];
		accountActionDate.setPrincipalPaid(new Money("10.0"));
		accountActionDate.setInterestPaid(new Money("2.0"));
		accountActionDate.setPenalty(new Money("20.0"));
		accountActionDate.setPenaltyPaid(new Money("5.0"));
		accountActionDate.setMiscPenalty(new Money("10.0"));
		accountActionDate.setMiscFee(new Money("20.0"));
		accountActionDate.setMiscFeePaid(new Money("5.0"));
		assertEquals(140.0, accountActionDate.getTotalDue()
				.getAmountDoubleValue());

	}

	public void testGetTotalDueWithFees() {
		LoanScheduleEntity accountActionDate = (LoanScheduleEntity) accountBO
				.getAccountActionDates().toArray()[0];
		accountActionDate.setPrincipalPaid(new Money("10.0"));
		accountActionDate.setInterestPaid(new Money("2.0"));
		accountActionDate.setPenalty(new Money("20.0"));
		accountActionDate.setPenaltyPaid(new Money("5.0"));
		accountActionDate.setMiscPenalty(new Money("10.0"));
		accountActionDate.setMiscFee(new Money("20.0"));
		accountActionDate.setMiscFeePaid(new Money("5.0"));
		assertEquals(240.0, accountActionDate.getTotalDueWithFees()
				.getAmountDoubleValue());

	}

	public void testGetDueAmounts() {
		LoanScheduleEntity accountActionDate = (LoanScheduleEntity) accountBO
				.getAccountActionDates().toArray()[0];
		accountActionDate.setPrincipalPaid(new Money("10.0"));
		accountActionDate.setInterestPaid(new Money("2.0"));
		accountActionDate.setPenalty(new Money("20.0"));
		accountActionDate.setPenaltyPaid(new Money("5.0"));
		accountActionDate.setMiscPenalty(new Money("10.0"));
		accountActionDate.setMiscFee(new Money("20.0"));
		accountActionDate.setMiscFeePaid(new Money("5.0"));
		assertEquals(115.0, accountActionDate.getDueAmnts().getFeesOverdue()
				.getAmountDoubleValue());

	}

	public void testGetTotalDueAmounts() {
		LoanScheduleEntity accountActionDate = (LoanScheduleEntity) accountBO
				.getAccountActionDates().toArray()[0];
		accountActionDate.setPrincipalPaid(new Money("10.0"));
		accountActionDate.setInterestPaid(new Money("2.0"));
		accountActionDate.setPenalty(new Money("20.0"));
		accountActionDate.setPenaltyPaid(new Money("5.0"));
		accountActionDate.setMiscPenalty(new Money("10.0"));
		accountActionDate.setMiscFee(new Money("20.0"));
		accountActionDate.setMiscFeePaid(new Money("5.0"));
		OverDueAmounts totalDue = accountActionDate.getDueAmnts();
		assertEquals(115.0, totalDue.getFeesOverdue().getAmountDoubleValue());
		assertEquals(90.0, totalDue.getPrincipalOverDue()
				.getAmountDoubleValue());
		assertEquals(10.0, totalDue.getInterestOverdue().getAmountDoubleValue());
		assertEquals(25.0, totalDue.getPenaltyOverdue().getAmountDoubleValue());

	}
	
	public void testGetTotalScheduleAmountWithFees() {
		LoanScheduleEntity accountActionDate = new LoanScheduleEntity(
				accountBO, accountBO.getCustomer(), Short.valueOf("1"),
				new java.sql.Date(System.currentTimeMillis()),
				PaymentStatus.UNPAID, new Money("100"), new Money("10"));
		accountActionDate.setPenalty(new Money("10.0"));
		accountActionDate.setMiscPenalty(new Money("10.0"));
		accountActionDate.setMiscFee(new Money("20.0"));

		accountActionDate.setPrincipalPaid(new Money());
		accountActionDate.setInterestPaid(new Money());
		accountActionDate.setPenaltyPaid(new Money());
		accountActionDate.setMiscPenaltyPaid(new Money());
		accountActionDate.setMiscFeePaid(new Money());

		LoanFeeScheduleEntity loanFeeSchedule = new LoanFeeScheduleEntity(
				accountActionDate, null, null, new Money("10"));
		loanFeeSchedule.setFeeAmountPaid(new Money());
		LoanFeeScheduleEntity loanFeeSchedule1 = new LoanFeeScheduleEntity(
				accountActionDate, null, null, new Money("10"));
		loanFeeSchedule1.setFeeAmountPaid(new Money());
		accountActionDate.addAccountFeesAction(loanFeeSchedule);
		accountActionDate.addAccountFeesAction(loanFeeSchedule1);

		assertEquals(new Money("170"), accountActionDate
				.getTotalScheduleAmountWithFees());
	}
	
	public void testIsPricipalZero(){
		for (AccountActionDateEntity accountAction : accountBO
				.getAccountActionDates()) {
			LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountAction;
			if(accountActionDateEntity.getInstallmentId() == 1){
				accountActionDateEntity.setPrincipal(new Money());
				assertTrue(accountActionDateEntity.isPrincipalZero());
			}else
				assertFalse(accountActionDateEntity.isPrincipalZero());
		}
	}
	
	  public static void modifyData(LoanScheduleEntity accntActionDate,
			Money penalty,Money penaltyPaid, Money miscPenalty, Money miscPenaltyPaid, Money miscFee,
			Money miscFeePaid, Money principal, Money principalPaid,
			Money interest, Money interestPaid) {
		accntActionDate.setPenalty(penalty);
		accntActionDate.setMiscPenalty(miscPenalty);
		accntActionDate.setMiscPenaltyPaid(miscPenaltyPaid);
		accntActionDate.setPenaltyPaid(penaltyPaid);
		accntActionDate.setMiscFee(miscFee);
		accntActionDate.setMiscFeePaid(miscFeePaid);
		accntActionDate.setPrincipal(principal);
		accntActionDate.setPrincipalPaid(principalPaid);
		accntActionDate.setInterest(interest);
		accntActionDate.setInterestPaid(interestPaid);
	}
	  
	  public void testMakeEarlyRepaymentEnteriesForFeePayment() {
			for (AccountActionDateEntity accountAction : accountBO
					.getAccountActionDates()) {
				LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountAction;
				accountActionDateEntity
						.makeEarlyRepaymentEnteries(LoanConstants.PAY_FEES_PENALTY_INTEREST);
				assertEquals(accountActionDateEntity.getPrincipal(),
						accountActionDateEntity.getPrincipalPaid());
				assertEquals(accountActionDateEntity.getInterest(),
						accountActionDateEntity.getInterestPaid());
				assertEquals(accountActionDateEntity.getPenalty(),
						accountActionDateEntity.getPenaltyPaid());
				assertEquals(accountActionDateEntity.getMiscFee(),
						accountActionDateEntity.getMiscFeePaid());
				assertTrue(accountActionDateEntity.isPaid());
			}
		}

		public void testMakeEarlyRepaymentEnteriesForNotPayingFee() {
			for (AccountActionDateEntity accountAction : accountBO
					.getAccountActionDates()) {
				LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountAction;
				accountActionDateEntity
						.makeEarlyRepaymentEnteries(LoanConstants.DONOT_PAY_FEES_PENALTY_INTEREST);
				assertEquals(accountActionDateEntity.getPrincipal(),
						accountActionDateEntity.getPrincipalPaid());
				assertEquals(accountActionDateEntity.getInterest(),
						accountActionDateEntity.getInterestPaid());
				assertEquals(accountActionDateEntity.getPenalty(),
						accountActionDateEntity.getPenaltyPaid());
				assertEquals(accountActionDateEntity.getMiscFee(),
						accountActionDateEntity.getMiscFeePaid());
				assertTrue(accountActionDateEntity.isPaid());
			}
		}

		public void testSuccessRemoveFees() {
			Short feeId = null;
			Set<AccountFeesEntity> accountFeesSet = accountBO.getAccountFees();
			for (AccountFeesEntity accountFeesEntity : accountFeesSet) {
				feeId = accountFeesEntity.getFees().getFeeId();
				break;
			}
			Set<AccountActionDateEntity> accountActionDateEntitySet = accountBO
					.getAccountActionDates();
			Iterator itr = accountActionDateEntitySet.iterator();
			while (itr.hasNext()) {
				LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) itr
						.next();
				accountActionDateEntity.removeFees(feeId);
				assertTrue(true);
			}
		}
  


}
