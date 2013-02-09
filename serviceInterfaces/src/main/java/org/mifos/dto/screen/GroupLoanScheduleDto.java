package org.mifos.dto.screen;

import java.io.Serializable;
import java.util.Map;

public class GroupLoanScheduleDto implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1635046317252143916L;

    private LoanScheduleDto groupSchedule;
    
    private Map<Integer, LoanScheduleDto> memberSchedules;

    public Map<Integer, LoanScheduleDto> getMemberSchedules() {
        return memberSchedules;
    }

    public void setMemberSchedules(Map<Integer, LoanScheduleDto> memberSchedules) {
        this.memberSchedules = memberSchedules;
    }

    public LoanScheduleDto getGroupSchedule() {
        return groupSchedule;
    }

    public void setGroupSchedule(LoanScheduleDto groupSchedule) {
        this.groupSchedule = groupSchedule;
    }
    
}
