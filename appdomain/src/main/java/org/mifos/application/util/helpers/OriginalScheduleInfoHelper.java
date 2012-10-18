package org.mifos.application.util.helpers;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.accounts.loan.business.service.OriginalScheduleInfoDto;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.dto.domain.RepaymentScheduleInstallmentDto;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

public class OriginalScheduleInfoHelper {

    public static OriginalScheduleInfoDto domainScheduletoBusinessDto(org.mifos.dto.domain.OriginalScheduleInfoDto domainSchedule) throws ParseException {
        return new OriginalScheduleInfoDto(domainSchedule.getLoanAmount(), domainSchedule.getDisbursementDate(), 
                generateRepaymentScheduleInstallmentFromDto(domainSchedule.getOriginalLoanScheduleInstallments()));
    }
    
    public static org.mifos.dto.domain.OriginalScheduleInfoDto businessScheduleToDomainDto(OriginalScheduleInfoDto originalBusinessSchedule) {
        return new org.mifos.dto.domain.OriginalScheduleInfoDto(originalBusinessSchedule.getLoanAmount(), originalBusinessSchedule.getDisbursementDate(), 
                generateRepaymentScheduleInstallmentToDto(originalBusinessSchedule.getOriginalLoanScheduleInstallment()));
    }
    
    public static List<RepaymentScheduleInstallment> generateRepaymentScheduleInstallmentFromDto(List<RepaymentScheduleInstallmentDto> repaymentScheduleDto) throws ParseException {
        List<RepaymentScheduleInstallment> repaymentScheduleInstallment = new ArrayList<RepaymentScheduleInstallment>();
        
        for (RepaymentScheduleInstallmentDto element : repaymentScheduleDto) {
            repaymentScheduleInstallment.add(new RepaymentScheduleInstallment(element.getInstallment(), DateUtils.parseDate(element.getDueDate()), stringToMony(element.getPrincipal()),
                    stringToMony(element.getInterest()), stringToMony(element.getFees()), stringToMony(element.getMiscFees()), stringToMony(element.getMiscPenalty())));
        }
        return repaymentScheduleInstallment;
    }
    
    private static Money stringToMony(String amount){
        return new Money(Money.getDefaultCurrency(), amount);
    }
    
    public static List<RepaymentScheduleInstallmentDto> generateRepaymentScheduleInstallmentToDto(List<RepaymentScheduleInstallment> repaymentScheduleInstallment) {
        List<RepaymentScheduleInstallmentDto> repaymentDto = new ArrayList<RepaymentScheduleInstallmentDto>();
        for (RepaymentScheduleInstallment instalment : repaymentScheduleInstallment) {
            repaymentDto.add(new RepaymentScheduleInstallmentDto(instalment.getInstallment(), instalment.getPrincipal().toString(),
                    instalment.getInterest().toString(), instalment.getFees().toString(), instalment.getMiscFees().toString(),
                    instalment.getFeesWithMiscFee().toString(), instalment.getMiscPenalty().toString(), instalment.getTotal(), instalment.getDueDate()));
        }
        return repaymentDto;        
    }
    
    public static OriginalScheduleInfoDto sumRepaymentSchedule(List<OriginalScheduleInfoDto> originalScheduleInfoDtos) {
        Money sumAmount = new Money(Money.getDefaultCurrency(), new Double(0.0));
        Date disburseDate = null;
        
        List<RepaymentScheduleInstallment> newRepayments = new ArrayList<RepaymentScheduleInstallment>();
        RepaymentScheduleInstallment sumRepayment;
        for (OriginalScheduleInfoDto dto : originalScheduleInfoDtos) {
            sumRepayment = new RepaymentScheduleInstallment();
            Money dtoAmount = new Money(Money.getDefaultCurrency(), new Double(dto.getLoanAmount()));
            sumAmount = sumAmount.add(dtoAmount);
            for (RepaymentScheduleInstallment scheduleInstallment : dto.getOriginalLoanScheduleInstallment()) {
                sumRepayment.setFees(sumRepayment.getFees().add(scheduleInstallment.getFees()));
                sumRepayment.setMiscFees(sumRepayment.getMiscFees().add(scheduleInstallment.getMiscFees()));
                sumRepayment.setMiscPenalty(sumRepayment.getMiscPenalty().add(scheduleInstallment.getMiscPenalty()));
                sumRepayment.setPrincipal(sumRepayment.getPrincipal().add(scheduleInstallment.getPrincipal()));
                sumRepayment.setInterest(sumRepayment.getInterest().add(scheduleInstallment.getInterest()));
                sumRepayment.setTotalAndTotalValue(sumRepayment.getTotalValue().add(scheduleInstallment.getTotalValue()));
                sumRepayment.setDueDate(scheduleInstallment.getDueDate());
            }
            newRepayments.add(sumRepayment);
            if (null == disburseDate) {
                disburseDate = dto.getDisbursementDate();
            }
        }
        return new OriginalScheduleInfoDto(sumAmount.toString(), disburseDate, newRepayments);
    }
    
}
