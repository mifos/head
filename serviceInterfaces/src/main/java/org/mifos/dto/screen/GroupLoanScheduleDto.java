package org.mifos.dto.screen;

import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.mifos.dto.domain.LoanCreationInstallmentDto;

public class GroupLoanScheduleDto extends LoanScheduleDto {
    
    /**
     * 
     */
    private static final long serialVersionUID = 7340531381114653515L;
    
    private Map<Integer, LoanScheduleDto> memberSchedules;

    public GroupLoanScheduleDto(String accountOwner, Double loanAmount, LocalDate disbursementDate, Integer graceType,
            List<LoanCreationInstallmentDto> installments) {
        super(accountOwner, loanAmount, disbursementDate, graceType, installments);
    }
    
    public Map<Integer, LoanScheduleDto> getMemberSchedules() {
        return memberSchedules;
    }

    public void setMemberSchedules(Map<Integer, LoanScheduleDto> memberSchedules) {
        this.memberSchedules = memberSchedules;
    }

}
