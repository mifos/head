package org.mifos.application.servicefacade;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifos.dto.domain.ApplicableAccountFeeDto;

public class CreateGroupCreationDetailDto {
    
    private DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    
    private Short centerId;
    private Short officeId;
    private Short loanOfficerId;
    private String displayName;
    private String externalId;
    private CreationAddresDto addressDto;
    private List<CreationFeeDto> feesToApply;
    private Short customerStatus;
    private Boolean trained;
    private String trainedOn;
    private String mfiJoiningDate;
    private String activationDate;
    private String parentSystemId;
    private CreationMeetingDto meeting;

    public CreateGroupCreationDetailDto(Short centerId, Short officeId, Short loanOfficerId, String displayName,
            String externalId, CreationAddresDto addressDto, List<CreationFeeDto> feesToApply, Short customerStatus,
            Boolean trained, String trainedOn, String mfiJoiningDate, String activationDate,
            String parentSystemId, CreationMeetingDto meeting) {
        this.centerId = centerId;
        this.officeId = officeId;
        this.loanOfficerId = loanOfficerId;
        this.displayName = displayName;
        this.externalId = externalId;
        this.addressDto = addressDto;
        this.feesToApply = feesToApply;
        this.customerStatus = customerStatus;
        this.trained = trained;
        this.trainedOn = trainedOn;
        this.mfiJoiningDate = mfiJoiningDate;
        this.activationDate = activationDate;
        this.parentSystemId = parentSystemId;
        this.meeting = meeting;
    }

    public Short getCenterId() {
        return centerId;
    }

    public void setCenterId(Short centerId) {
        this.centerId = centerId;
    }

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

    public CreationAddresDto getAddressDto() {
        return addressDto;
    }

    public void setAddressDto(CreationAddresDto addressDto) {
        this.addressDto = addressDto;
    }

    public List<CreationFeeDto> getFeesToApply() {
        return feesToApply;
    }

    public void setFeesToApply(List<CreationFeeDto> feesToApply) {
        this.feesToApply = feesToApply;
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

    public Short getCustomerStatus() {
        return customerStatus;
    }

    public void setCustomerStatus(Short customerStatus) {
        this.customerStatus = customerStatus;
    }

    public boolean isTrained() {
        return trained;
    }

    public void setTrained(boolean trained) {
        this.trained = trained;
    }

    public DateTime getTrainedOn() {
        return formatter.parseDateTime(trainedOn);
    }

    public void setTrainedOn(String trainedOn) {
        this.trainedOn = trainedOn;
    }

    public DateTime getMfiJoiningDate() {
        return formatter.parseDateTime(mfiJoiningDate);
    }

    public void setMfiJoiningDate(String mfiJoiningDate) {
        this.mfiJoiningDate = mfiJoiningDate;
    }

    public DateTime getActivationDate() {
        return formatter.parseDateTime(activationDate);
    }

    public void setActivationDate(String activationDate) {
        this.activationDate = activationDate;
    }

    public String getParentSystemId() {
        return parentSystemId;
    }

    public void setParentSystemId(String parentSystemId) {
        this.parentSystemId = parentSystemId;
    }

    public CreationMeetingDto getMeeting() {
        return meeting;
    }

    public void setMeeting(CreationMeetingDto meeting) {
        this.meeting = meeting;
    }

}
