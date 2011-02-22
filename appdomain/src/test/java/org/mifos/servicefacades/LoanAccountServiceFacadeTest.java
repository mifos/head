package org.mifos.servicefacades;

import org.junit.Ignore;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.servicefacade.LoanAccountServiceFacadeWebTier;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.dto.screen.LoanAccountInfoDto;
import org.mifos.dto.screen.LoanAccountMeetingDto;
import org.mifos.dto.screen.LoanScheduledInstallmentDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@Ignore
public class LoanAccountServiceFacadeTest extends ServiceFacadeTest {
    @Autowired
    LoanAccountServiceFacadeWebTier loanAccountService;

    public void createLoan() {
        OfficeBO office = addOffice("KMGL");
        PersonnelBO personnel = addPersonnel("Govinda");
        ClientBO client = addClient("Marie", office, personnel);

        loanAccountService.createLoan(meeting().weekly().on(WeekDay.MONDAY.getValue()).recurring((short) 1).build(),
                loanAccount().forCustomer(client.getCustomerId()).build(),
                repayments());
    }

    LoanAccountInfoDto.LoanAccountInfoDtoBuilder loanAccount() {
        return new LoanAccountInfoDto.LoanAccountInfoDtoBuilder();
    }

    ArrayList<LoanScheduledInstallmentDto> repayments() {
        return new ArrayList<LoanScheduledInstallmentDto>();
    }

    LoanAccountMeetingDto.Builder meeting() {
        return new LoanAccountMeetingDto.Builder();
    }
}