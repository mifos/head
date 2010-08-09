package org.mifos.dto.screen;

import java.io.Serializable;

import org.joda.time.DateTime;
import org.mifos.dto.domain.AddressDto;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="should disable at filter level and also for pmd - not important for us")
public class PersonnelDetailsDto implements Serializable {
    private final String governmentIdNumber;
    private final DateTime dob;
    private final Integer maritalStatus;
    private final Integer gender;
    private final DateTime dateOfJoiningMFI;
    private final DateTime dateOfJoiningBranch;
    private final DateTime dateOfLeavingBranch;
    private final AddressDto address;

    public PersonnelDetailsDto(String governmentIdNumber, DateTime dob, Integer maritalStatus, Integer gender,
            DateTime dateOfJoiningMFI, DateTime dateOfJoiningBranch, DateTime dateOfLeavingBranch, AddressDto address) {
        super();
        this.governmentIdNumber = governmentIdNumber;
        this.dob = dob;
        this.maritalStatus = maritalStatus;
        this.gender = gender;
        this.dateOfJoiningMFI = dateOfJoiningMFI;
        this.dateOfJoiningBranch = dateOfJoiningBranch;
        this.dateOfLeavingBranch = dateOfLeavingBranch;
        this.address = address;
    }

    public String getGovernmentIdNumber() {
        return this.governmentIdNumber;
    }
    public DateTime getDob() {
        return this.dob;
    }
    public Integer getMaritalStatus() {
        return this.maritalStatus;
    }
    public Integer getGender() {
        return this.gender;
    }
    public DateTime getDateOfJoiningMFI() {
        return this.dateOfJoiningMFI;
    }
    public DateTime getDateOfJoiningBranch() {
        return this.dateOfJoiningBranch;
    }
    public DateTime getDateOfLeavingBranch() {
        return this.dateOfLeavingBranch;
    }
    public AddressDto getAddress() {
        return this.address;
    }
}
