package org.mifos.application.servicefacade;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.dto.domain.ApplicableAccountFeeDto;

public class CreateClientCreationDetail {

    private Short officeId;
    private Short loanOfficerId;
    private Short formedBy;
    private Short groupFlag;
    private String parentGroupId;
    private String externalId;
    private String governmentId;
    private boolean trained;
    private LocalDate trainedDate;
    private LocalDate dateOfBirth;
    private LocalDate activationDate;
    private LocalDate mfiJoiningDate;
    private Short customerStatus;
    private CreatePersonalDetailDto personalDetail;
    private CreateClientNameDetailDto clientNameDetail;
    private CreationAddresDto address;
    private List<CreationFeeDto> accountFees;
    private CreationMeetingDto meeting;

    public Short getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Short officeId) {
        this.officeId = officeId;
    }

    public Short getLoanOfficerId() {
        return loanOfficerId;
    }

    public void setLoanOfficerId(Short loanOfficerId) {
        this.loanOfficerId = loanOfficerId;
    }

    public Short getFormedBy() {
        return formedBy;
    }

    public void setFormedBy(Short formedBy) {
        this.formedBy = formedBy;
    }

    public Short getGroupFlag() {
        return groupFlag;
    }

    public void setGroupFlag(Short groupFlag) {
        this.groupFlag = groupFlag;
    }

    public String getParentGroupId() {
        return parentGroupId;
    }

    public void setParentGroupId(String parentGroupId) {
        this.parentGroupId = parentGroupId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getGovernmentId() {
        return governmentId;
    }

    public void setGovernmentId(String governmentId) {
        this.governmentId = governmentId;
    }

    public boolean getTrained() {
        return trained;
    }

    public void setTrained(boolean trained) {
        this.trained = trained;
    }

    public LocalDate getTrainedDate() {
        return trainedDate;
    }

    public void setTrainedDate(LocalDate trainedDate) {
        this.trainedDate = trainedDate;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(LocalDate activationDate) {
        this.activationDate = activationDate;
    }

    public LocalDate getMfiJoiningDate() {
        return mfiJoiningDate;
    }

    public void setMfiJoiningDate(LocalDate mfiJoiningDate) {
        this.mfiJoiningDate = mfiJoiningDate;
    }

    public Short getCustomerStatus() {
        return customerStatus;
    }

    public void setAccountFees(List<CreationFeeDto> accountFees) {
        this.accountFees = accountFees;
    }

    public void setCustomerStatus(Short customerStatus) {
        this.customerStatus = customerStatus;
    }

    public CreatePersonalDetailDto getPersonalDetail() {
        return personalDetail;
    }

    public void setPersonalDetail(CreatePersonalDetailDto personalDetail) {
        this.personalDetail = personalDetail;
    }

    public CreateClientNameDetailDto getClientNameDetail() {
        return clientNameDetail;
    }

    public void setClientNameDetail(CreateClientNameDetailDto clientNameDetail) {
        this.clientNameDetail = clientNameDetail;
    }

    public CreationAddresDto getAddress() {
        return address;
    }

    public void setAddress(CreationAddresDto address) {
        this.address = address;
    }

    public CreationMeetingDto getMeeting() {
        return meeting;
    }

    public void setMeeting(CreationMeetingDto meeting) {
        this.meeting = meeting;
    }

    public List<CreationFeeDto> getAccountFees() {
        return accountFees;
    }

    public List<ApplicableAccountFeeDto> feeAsAccountFeeDto(List<CreationFeeDto> feesToApply) {
        List<ApplicableAccountFeeDto> feeDto = new ArrayList<ApplicableAccountFeeDto>();
        for (CreationFeeDto f : feesToApply) {
            ApplicableAccountFeeDto accFee = new ApplicableAccountFeeDto();
            accFee.setFeeId(f.getFeeId());
            accFee.setAmount(f.getAmount());
            feeDto.add(accFee);
        }
        return feeDto;
    }

}
