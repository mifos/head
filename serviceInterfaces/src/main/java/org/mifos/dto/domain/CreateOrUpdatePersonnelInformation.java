package org.mifos.dto.domain;

import java.util.List;

import org.joda.time.DateTime;
import org.mifos.dto.screen.ListElement;

@SuppressWarnings("PMD")
public class CreateOrUpdatePersonnelInformation {
    private final Short personnelLevelId;
    private final Short officeId;
    private final Integer title;
    private final Short preferredLocale;
    private final String password;
    private final String userName;
    private final String emailId;
    private final List<ListElement> roles;
    private final List<CustomFieldDto> customFields;
    private final String firstName;
    private final String middleName;
    private final String lastName;
    private final String secondLastName;
    private final String governmentIdNumber;
    private final DateTime dob;
    private final Integer maritalStatus;
    private final Integer gender;
    private final DateTime dateOfJoiningMFI;
    private final DateTime dateOfJoiningBranch;
    private final AddressDto address;
    private final Short createdBy;

    private final Short personnelStatusId;
    private final Short updatedById;

    public CreateOrUpdatePersonnelInformation(Short personnelLevelId, Short officeId, Integer title,
            Short preferredLocale, String password, String userName, String emailId, List<ListElement> roles,
            List<CustomFieldDto> customFields, String firstName, String middleName, String lastName,
            String secondLastName, String governmentIdNumber, DateTime dob, Integer maritalStatus, Integer gender,
            DateTime dateOfJoiningMFI, DateTime dateOfJoiningBranch, AddressDto address, Short createdBy,
            Short personnelStatusId, Short updatedById) {
        super();
        this.personnelLevelId = personnelLevelId;
        this.officeId = officeId;
        this.title = title;
        this.preferredLocale = preferredLocale;
        this.password = password;
        this.userName = userName;
        this.emailId = emailId;
        this.roles = roles;
        this.customFields = customFields;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.secondLastName = secondLastName;
        this.governmentIdNumber = governmentIdNumber;
        this.dob = dob;
        this.maritalStatus = maritalStatus;
        this.gender = gender;
        this.dateOfJoiningMFI = dateOfJoiningMFI;
        this.dateOfJoiningBranch = dateOfJoiningBranch;
        this.address = address;
        this.createdBy = createdBy;
        this.personnelStatusId = personnelStatusId;
        this.updatedById = updatedById;
    }

    public Short getPersonnelLevelId() {
        return this.personnelLevelId;
    }

    public Short getOfficeId() {
        return this.officeId;
    }

    public Integer getTitle() {
        return this.title;
    }

    public Short getPreferredLocale() {
        return this.preferredLocale;
    }

    public String getPassword() {
        return this.password;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getEmailId() {
        return this.emailId;
    }

    public List<ListElement> getRoles() {
        return this.roles;
    }

    public List<CustomFieldDto> getCustomFields() {
        return this.customFields;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getMiddleName() {
        return this.middleName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getSecondLastName() {
        return this.secondLastName;
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

    public AddressDto getAddress() {
        return this.address;
    }

    public Short getCreatedBy() {
        return this.createdBy;
    }

    public Short getPersonnelStatusId() {
        return this.personnelStatusId;
    }

    public Short getUpdatedById() {
        return this.updatedById;
    }
}
