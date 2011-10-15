package org.mifos.accounts.loan.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.loan.schedule.domain.Installment;
import org.mifos.accounts.loan.schedule.domain.InstallmentPayment;
import org.mifos.accounts.loan.schedule.domain.Schedule;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.util.helpers.Money;

public class ScheduleMapper {

    public Schedule mapToSchedule(Collection<LoanScheduleEntity> loanScheduleEntities, Date disbursementDate, Double dailyInterestRate, BigDecimal loanAmount) {
        return new Schedule(disbursementDate, dailyInterestRate, loanAmount, mapToInstallments(loanScheduleEntities));
    }

    public void populateExtraInterestInLoanScheduleEntities(Schedule schedule, Map<Integer, LoanScheduleEntity> loanScheduleEntities) {
        for (Installment installment : schedule.getInstallments().values()) {
            LoanScheduleEntity loanScheduleEntity = loanScheduleEntities.get(installment.getId());
            loanScheduleEntity.setExtraInterest(new Money(loanScheduleEntity.getCurrency(), installment.getExtraInterest()));
        }
    }

    private List<Installment> mapToInstallments(Collection<LoanScheduleEntity> loanScheduleEntities) {
        List<Installment> installments = new ArrayList<Installment>();
        for (LoanScheduleEntity loanScheduleEntity : loanScheduleEntities) {
            installments.add(mapToInstallment(loanScheduleEntity));
        }
        return installments;
    }

    private Installment mapToInstallment(LoanScheduleEntity loanScheduleEntity) {
        Installment installment = new Installment(loanScheduleEntity.getInstallmentId().intValue(),
                loanScheduleEntity.getActionDate(), loanScheduleEntity.getPrincipal().getAmount(),
                loanScheduleEntity.getInterest().getAmount(), loanScheduleEntity.getExtraInterest().getAmount(),
                loanScheduleEntity.getTotalFees().getAmount(), loanScheduleEntity.getMiscFee().getAmount(),
                loanScheduleEntity.getPenalty().getAmount(), loanScheduleEntity.getMiscPenalty().getAmount());
        if (loanScheduleEntity.isPaymentApplied()) {
            installment.addPayment(getInstallmentPayment(loanScheduleEntity));
        }
        return installment;
    }

    private InstallmentPayment getInstallmentPayment(LoanScheduleEntity loanScheduleEntity) {
        InstallmentPayment installmentPayment = new InstallmentPayment();
        installmentPayment.setPaidDate(loanScheduleEntity.getPaymentDate());
        installmentPayment.setPrincipalPaid(loanScheduleEntity.getPrincipalPaid().getAmount());
        installmentPayment.setInterestPaid(loanScheduleEntity.getInterestPaid().getAmount());
        installmentPayment.setExtraInterestPaid(loanScheduleEntity.getExtraInterestPaid().getAmount());
        installmentPayment.setFeesPaid(loanScheduleEntity.getTotalFeesPaid().getAmount());
        installmentPayment.setMiscFeesPaid(loanScheduleEntity.getMiscFeePaid().getAmount());
        installmentPayment.setPenaltyPaid(loanScheduleEntity.getPenaltyPaid().getAmount());
        installmentPayment.setMiscPenaltyPaid(loanScheduleEntity.getMiscPenaltyPaid().getAmount());
        return installmentPayment;
    }

    public void populatePaymentDetails(Schedule schedule, LoanBO loanBO, Date paymentDate, PersonnelBO personnel,
                                       AccountPaymentEntity accountPaymentEntity) {
        Map<Integer, Installment> installments = schedule.getInstallments();
        MifosCurrency currency = loanBO.getCurrency();
        for (LoanScheduleEntity loanScheduleEntity : loanBO.getLoanScheduleEntities()) {
            if (loanScheduleEntity.isNotPaid()) {
                Installment installment = installments.get(Integer.valueOf(loanScheduleEntity.getInstallmentId()));
                Money originalInterest = loanScheduleEntity.getInterest();
                Money extraInterestPaid = new Money(currency, installment.getCurrentExtraInterestPaid());
                Money interestDueTillPaid = new Money(currency, installment.getCurrentInterestPaid());
                loanScheduleEntity.payComponents(installment, currency, paymentDate);
                if (loanScheduleEntity.getPaymentAllocation().hasAllocation()) {
                    LoanTrxnDetailEntity loanTrxnDetailEntity = loanScheduleEntity.updateSummaryAndPerformanceHistory(
                            accountPaymentEntity, personnel, paymentDate);
                    loanTrxnDetailEntity.computeAndSetCalculatedInterestOnPayment(originalInterest, extraInterestPaid, interestDueTillPaid);
                }
            }
        }
    }

}