package org.mifos.application.servicefacade;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.dto.domain.CreateAccountFeeDto;

public class CreateCenterDetailsDto {

    private LocalDate mfiJoiningDate;
    private String displayName;
    private String externalId;
    private Integer loanOfficerId;
    private Integer officeId;
    private CreationAddresDto addressDto;
    private List<CreationFeeDto> accountFees;
    private CreationMeetingDto meeting;

    public CreateCenterDetailsDto(LocalDate mfiJoiningDate, String displayName, String externalId,
            Integer loanOfficerId, Integer officeId, CreationAddresDto addressDto, List<CreationFeeDto> accountFees,
            CreationMeetingDto meeting) {
        this.mfiJoiningDate = mfiJoiningDate;
        this.displayName = displayName;
        this.externalId = externalId;
        this.loanOfficerId = loanOfficerId;
        this.officeId = officeId;
        this.addressDto = addressDto;
        this.accountFees = accountFees;
        this.meeting = meeting;
    }

    public LocalDate getMfiJoiningDate() {
        return mfiJoiningDate;
    }

    public void setMfiJoiningDate(LocalDate mfiJoiningDate) {
        this.mfiJoiningDate = mfiJoiningDate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Integer getLoanOfficerId() {
        return loanOfficerId;
    }

    public void setLoanOfficerId(Integer loanOfficerId) {
        this.loanOfficerId = loanOfficerId;
    }

    public Integer getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Integer officeId) {
        this.officeId = officeId;
    }

    public CreationAddresDto getAddressDto() {
        return addressDto;
    }

    public void setAddressDto(CreationAddresDto addressDto) {
        this.addressDto = addressDto;
    }

    public List<CreationFeeDto> getAccountFees() {
        return accountFees;
    }

    public void setAccountFees(List<CreationFeeDto> accountFees) {
        this.accountFees = accountFees;
    }

    public CreationMeetingDto getMeeting() {
        return meeting;
    }

    public void setMeeting(CreationMeetingDto meeting) {
        this.meeting = meeting;
    }

    public List<CreateAccountFeeDto> feeAsAccountFeeDto(List<CreationFeeDto> accountFees) {
        List<CreateAccountFeeDto> feeDto = new ArrayList<CreateAccountFeeDto>();
        for (CreationFeeDto f : accountFees) {
            feeDto.add(f.toDto());
        }
        return feeDto;
    }

}