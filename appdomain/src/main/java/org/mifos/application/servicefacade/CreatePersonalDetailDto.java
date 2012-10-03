package org.mifos.application.servicefacade;

import org.mifos.dto.screen.ClientPersonalDetailDto;

public class CreatePersonalDetailDto {
    private Integer ethnicity;
    private Integer citizenship;
    private Integer handicapped;
    private Integer businessActivities;
    private Integer maritalStatus;
    private Integer educationLevel;
    private Short numberOfChildren;
    private Short gender;
    private Short povertyStatus;

    public Integer getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(Integer ethnicity) {
        this.ethnicity = ethnicity;
    }

    public Integer getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(Integer citizenship) {
        this.citizenship = citizenship;
    }

    public Integer getHandicapped() {
        return handicapped;
    }

    public void setHandicapped(Integer handicapped) {
        this.handicapped = handicapped;
    }

    public Integer getBusinessActivities() {
        return businessActivities;
    }

    public void setBusinessActivities(Integer businessActivities) {
        this.businessActivities = businessActivities;
    }

    public Integer getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(Integer maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public Integer getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(Integer educationLevel) {
        this.educationLevel = educationLevel;
    }

    public Short getNumberOfChildren() {
        return numberOfChildren;
    }

    public void setNumberOfChildren(Short numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
    }

    public Short getGender() {
        return gender;
    }

    public void setGender(Short gender) {
        this.gender = gender;
    }

    public Short getPovertyStatus() {
        return povertyStatus;
    }

    public void setPovertyStatus(Short povertyStatus) {
        this.povertyStatus = povertyStatus;
    }
    
    public ClientPersonalDetailDto toDto() {
        return new ClientPersonalDetailDto(getEthnicity(), getCitizenship(), getHandicapped(), getBusinessActivities(), getBusinessActivities(), getEducationLevel(), getNumberOfChildren(), getGender(), getPovertyStatus());
    }

}
