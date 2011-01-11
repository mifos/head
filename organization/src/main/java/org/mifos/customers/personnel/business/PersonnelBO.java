/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.customers.personnel.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.exceptions.PersonnelException;
import org.mifos.customers.personnel.util.helpers.LockStatus;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.customers.personnel.util.helpers.PersonnelStatus;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.UserDetailDto;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.security.authentication.EncryptionService;
import org.mifos.security.rolesandpermission.business.RoleBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonnelBO extends AbstractBusinessObject {

    private static final Logger logger = LoggerFactory.getLogger(PersonnelBO.class);
    private static final String INVALIDOLDPASSWORD = "errors.invalidoldpassword";

    public static PersonnelBO ALL_PERSONNEL = new PersonnelBO();
    static {
        ALL_PERSONNEL.setDisplayName("ALL");
    }

    private Short personnelId;
    private PersonnelLevelEntity level;
    private String globalPersonnelNum;
    private OfficeBO office;
    private Integer title;
    private String displayName;
    private String searchId;
    private Integer maxChildCount;
    private byte[] encryptedPassword;
    private String userName;
    private String emailId;
    private Short passwordChanged = Short.valueOf("0");
    private Date lastLogin;
    private Short locked = Short.valueOf("0");
    private Short noOfTries = Short.valueOf("0");

    private PersonnelStatusEntity status;
    private SupportedLocalesEntity preferredLocale;
    private PersonnelDetailsEntity personnelDetails;
    private Set<PersonnelRoleEntity> personnelRoles;
    private Set<PersonnelCustomFieldEntity> customFields;
    private Set<PersonnelMovementEntity> personnelMovements;
    private Set<PersonnelNotesEntity> personnelNotes;

    public PersonnelBO(final PersonnelLevel level, final OfficeBO office, final Integer title, final Short preferredLocale, final String password,
            final String userName, final String emailId, final List<RoleBO> roles, final List<CustomFieldDto> customFields, final Name name,
            final String governmentIdNumber, final Date dob, final Integer maritalStatus, final Integer gender, final Date dateOfJoiningMFI,
            final Date dateOfJoiningBranch, final Address address, final Short createdBy) {
        super();
        setCreateDetails(createdBy, new DateTime().toDate());
        this.displayName = name.getDisplayName();
        this.level = new PersonnelLevelEntity(level);
        this.office = office;
        this.title = title;
        this.preferredLocale = new SupportedLocalesEntity(preferredLocale);
        this.userName = userName;
        this.emailId = emailId;
        this.personnelDetails = new PersonnelDetailsEntity(name, governmentIdNumber, dob, maritalStatus, gender,
                dateOfJoiningMFI, dateOfJoiningBranch, this, address);
        this.personnelRoles = new HashSet<PersonnelRoleEntity>();
        if (roles != null) {
            for (RoleBO role : roles) {
                this.personnelRoles.add(new PersonnelRoleEntity(role, this));
            }
        }
        this.customFields = new HashSet<PersonnelCustomFieldEntity>();
        this.personnelMovements = new HashSet<PersonnelMovementEntity>();
        this.personnelNotes = new HashSet<PersonnelNotesEntity>();
        this.personnelId = null;
        this.globalPersonnelNum = new Long(new DateTime().toDate().getTime()).toString();
        this.status = new PersonnelStatusEntity(PersonnelStatus.ACTIVE);
        this.passwordChanged = Constants.NO;
        this.locked = LockStatus.UNLOCK.getValue();
        this.noOfTries = 0;
        this.encryptedPassword = getEncryptedPassword(password);
        this.status = new PersonnelStatusEntity(PersonnelStatus.ACTIVE);
    }

    /**
     * default constructor for hibernate
     */
    @Deprecated
    public PersonnelBO() {
        this(null, null, null, null);
        this.personnelDetails = new PersonnelDetailsEntity();
        this.preferredLocale = new SupportedLocalesEntity();
        this.customFields = new HashSet<PersonnelCustomFieldEntity>();
        this.personnelNotes = new HashSet<PersonnelNotesEntity>();
    }

    public PersonnelBO(final Short personnelId, final String userName, final String displayName, final PersonnelLevelEntity level) {
        this.personnelId = personnelId;
        this.userName = userName;
        this.displayName = displayName;
        this.level = level;
    }

    public String getAge() {
        if (this.personnelDetails != null && this.personnelDetails.getDob() != null
                && !this.personnelDetails.getDob().equals("")) {

            LocalDate fromDate = new LocalDate(this.personnelDetails.getDob());
            Years years = Years.yearsBetween(new LocalDate(), fromDate);



            return Integer.valueOf(years.getYears()).toString();
        }

        return "";
    }

    public Set<PersonnelCustomFieldEntity> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(final Set<PersonnelCustomFieldEntity> customFields) {
        this.customFields = customFields;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getGlobalPersonnelNum() {
        return globalPersonnelNum;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public PersonnelLevelEntity getLevel() {
        return level;
    }

    public PersonnelLevel getLevelEnum() {
        return PersonnelLevel.fromInt(level.getId());
    }

    public Integer getMaxChildCount() {
        return maxChildCount;
    }

    public OfficeBO getOffice() {
        return office;
    }

    public boolean isPasswordChanged() {
        return this.passwordChanged > 0;
    }

    public PersonnelDetailsEntity getPersonnelDetails() {
        return personnelDetails;
    }

    public void setPersonnelDetails(final PersonnelDetailsEntity personnelDetails) {
        this.personnelDetails = personnelDetails;
    }

    public Short getPersonnelId() {
        return personnelId;
    }

    public SupportedLocalesEntity getPreferredLocale() {
        return preferredLocale;
    }

    public Integer getTitle() {
        return title;
    }

    public String getUserName() {
        return userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isLocked() {
        return this.locked > 0;
    }

    private void lock() {
        this.locked = LockStatus.LOCK.getValue();
    }

    private void unLock() {
        this.locked = LockStatus.UNLOCK.getValue();
    }

    public Set<PersonnelRoleEntity> getPersonnelRoles() {
        return personnelRoles;
    }

    public void setPersonnelRoles(final Set<PersonnelRoleEntity> personnelRoles) {
        this.personnelRoles = personnelRoles;
    }

    public Set<PersonnelMovementEntity> getPersonnelMovements() {
        return personnelMovements;
    }

    public Set<PersonnelNotesEntity> getPersonnelNotes() {
        return personnelNotes;
    }

    public Short getNoOfTries() {
        return noOfTries;
    }

    public Short getPasswordChanged() {
        return passwordChanged;
    }

    public void setPasswordChanged(final Short passwordChanged) {
        this.passwordChanged = passwordChanged;
    }

    public String getSearchId() {
        return searchId;
    }

    void setSearchId(final String searchId) {
        this.searchId = searchId;
    }

    public byte[] getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public void setEmailId(final String emailId) {
        this.emailId = emailId;
    }

    void setGlobalPersonnelNum(final String globalPersonnelNum) {
        this.globalPersonnelNum = globalPersonnelNum;
    }

    void setLastLogin(final Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setLevel(final PersonnelLevelEntity level) {
        this.level = level;
    }

    public void setMaxChildCount(final Integer maxChildCount) {
        this.maxChildCount = maxChildCount;
    }

    public void setOffice(final OfficeBO office) {
        this.office = office;
    }

    public void setPersonnelMovements(final Set<PersonnelMovementEntity> personnelMovements) {
        this.personnelMovements = personnelMovements;
    }

    public void setPersonnelNotes(final Set<PersonnelNotesEntity> personnelNotes) {
        this.personnelNotes = personnelNotes;
    }

    public void setPreferredLocale(final SupportedLocalesEntity preferredLocale) {
        this.preferredLocale = preferredLocale;
    }

    public void setStatus(final PersonnelStatusEntity status) {
        this.status = status;
    }

    public void setTitle(final Integer title) {
        this.title = title;
    }

    public void setEncryptedPassword(final byte[] encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public PersonnelStatusEntity getStatus() {
        return status;
    }

    public PersonnelStatus getStatusAsEnum() {
        return PersonnelStatus.getPersonnelStatus(status.getId());
    }

    public void addPersonnelMovement(final PersonnelMovementEntity personnelMovement) {
        if (personnelMovement != null) {
            this.personnelMovements.add(personnelMovement);
        }
    }

    public void addNotes(final Short userId, final PersonnelNotesEntity personnelNotes) {
        setUpdateDetails(userId);
        this.personnelNotes.add(personnelNotes);
    }

    public void generateGlobalPersonnelNum() {
        String paddedSystemId = generateGlobalPersonnelNum(this.office.getGlobalOfficeNum(), this.personnelId);
        this.globalPersonnelNum = paddedSystemId;
    }

    private String generateGlobalPersonnelNum(final String officeGlobalNum, final int maxPersonnelId) {
        logger.debug("Passed office global no is : ".concat(officeGlobalNum).concat(
                " and maxpersonnelid is : " + maxPersonnelId));
        String userId = "";
        int numberOfZeros = 5 - String.valueOf(maxPersonnelId).length();
        for (int i = 0; i < numberOfZeros; i++) {
            userId = userId + "0";
        }
        userId = userId + maxPersonnelId;
        String userGlobalNum = officeGlobalNum + "-" + userId;
        logger.debug("Generated userGlobalNum is : ".concat(userGlobalNum));
        return userGlobalNum;
    }

    private byte[] getEncryptedPassword(final String password) {
        byte[] encryptedPassword = null;
        encryptedPassword = EncryptionService.getInstance().createEncryptedPassword(password);
        return encryptedPassword;
    }

    private void updatePersonnelRoles(final List<RoleBO> roles) {
        this.personnelRoles.clear();
        if (roles != null) {
            for (RoleBO role : roles) {
                this.personnelRoles.add(new PersonnelRoleEntity(role, this));
            }
        }

    }

    public void update(final String emailId, final Name name, final Integer maritalStatus, final Integer gender, final Address address, final Short preferredLocale) {

        this.emailId = emailId;
        if (preferredLocale != null && preferredLocale != 0) {
            this.preferredLocale = new SupportedLocalesEntity(preferredLocale);
        }
        setDisplayName(name.getDisplayName());
        updatePersonnelDetails(name, maritalStatus, gender, address, null);
    }

    public void updatePersonnelDetails(final Name name, final Integer maritalStatus, final Integer gender, final Address address,
            final Date dateOfJoiningBranch) {
        if (this.personnelDetails != null) {
            this.personnelDetails.updateNameDetails(name.getFirstName(), name.getMiddleName(), name.getSecondLastName(), name.getLastName());
            this.personnelDetails.updateDetails(maritalStatus, gender);
            this.personnelDetails.updateAddress(address);
            this.personnelDetails.setDateOfJoiningBranch(dateOfJoiningBranch);
        }
    }

    public List<PersonnelNotesEntity> getRecentPersonnelNotes() {
        List<PersonnelNotesEntity> notes = new ArrayList<PersonnelNotesEntity>();
        int count = 0;
        for (PersonnelNotesEntity personnelNotes : getPersonnelNotes()) {
            if (count > 2) {
                break;
            }
            notes.add(personnelNotes);
            count++;
        }
        return notes;
    }

    public PersonnelMovementEntity getActiveCustomerMovement() {
        PersonnelMovementEntity movement = null;
        for (PersonnelMovementEntity personnelMovementEntity : personnelMovements) {
            if (personnelMovementEntity.isActive()) {
                movement = personnelMovementEntity;
                break;
            }
        }
        return movement;
    }

    public boolean isActive() {
        return getStatusAsEnum() == PersonnelStatus.ACTIVE;
    }

    public boolean isLoanOfficer() {
        return getLevelEnum() == PersonnelLevel.LOAN_OFFICER;
    }

    public void login(final String password) throws PersonnelException {

        final String KEYUSERINACTIVE = "AbstractUserDetailsAuthenticationProvider.disabled";
        final String KEYUSERLOCKED = "AbstractUserDetailsAuthenticationProvider.locked";

        if (!isActive()) {
            throw new PersonnelException(KEYUSERINACTIVE);
        }
        if (isLocked()) {
            throw new PersonnelException(KEYUSERLOCKED);
        }
        if (!isPasswordValid(password)) {
            updateNoOfTries();
            throw new PersonnelException(INVALIDOLDPASSWORD);
        }
        resetNoOfTries();

        if (isPasswordChanged()) {
            this.lastLogin = new DateTime().toDate();
        }
        logger.info("Login successful for user=" + this.userName + ", branchID=" + this.office.getGlobalOfficeNum());
    }

    public void updatePassword(final String oldPassword, final String newPassword) throws PersonnelException {
        byte[] encryptedPassword = getEncryptedPassword(oldPassword, newPassword);
        this.setEncryptedPassword(encryptedPassword);
        this.setPasswordChanged(passwordChangedShortValue());
        if (this.getLastLogin() == null) {
            this.setLastLogin(new DateTime().toDate());
        }
    }

    private Short passwordChangedShortValue() {
        return Short.valueOf("1");
    }

    public void changePasswordTo(String newPassword, final Short changedByUserId) {
        byte[] encryptedPassword = getEncryptedPassword(newPassword);
        this.setEncryptedPassword(encryptedPassword);
        this.setPasswordChanged(passwordChangedShortValue());
        if (this.getLastLogin() == null) {
            this.setLastLogin(new DateTime().toDate());
        }
        setUpdateDetails(changedByUserId);
    }

    public void unlockPersonnel() {
        if (isLocked()) {
            this.unLock();
            this.noOfTries = 0;
        }
    }

    private void updateNoOfTries() {
        final Short MAXTRIES = 5;
        if (!isLocked()) {
            Short newNoOfTries = (short) (getNoOfTries() + 1);
            if (newNoOfTries.equals(MAXTRIES)) {
                lock();
            }
            this.noOfTries = newNoOfTries;
        }
    }

    private void resetNoOfTries() {
        if (noOfTries.intValue() > 0) {
            this.noOfTries = 0;
        }
    }

    private boolean isPasswordValid(final String password) throws PersonnelException {
        logger.debug("Checking password valid or not");
        try {
            return EncryptionService.getInstance().verifyPassword(password, getEncryptedPassword());
        } catch (SystemException se) {
            throw new PersonnelException(se);
        }
    }

    public Short getLocaleId() {
        return getPreferredLocale().getLocaleId();
    }

    public Set<Short> getRoles() {
        Set<Short> roles = new HashSet<Short>();
        for (PersonnelRoleEntity personnelRole : getPersonnelRoles()) {
            roles.add(personnelRole.getRole().getId());
        }
        return roles;
    }

    private byte[] getEncryptedPassword(final String oldPassword, final String newPassword) throws PersonnelException {
        logger.debug("Matching oldpassword with entered password.");
        byte[] newEncryptedPassword = null;
        if (isPasswordValid(oldPassword)) {
            newEncryptedPassword = getEncryptedPassword(newPassword);
        } else {
            throw new PersonnelException(INVALIDOLDPASSWORD);
        }
        logger.debug("New encrypted password returned.");
        return newEncryptedPassword;
    }

    public String getOfficeSearchId() {
        return office.getSearchId();
    }

    @Override
    public String toString() {
        return "{" + getDisplayName() + ", " + getPersonnelId() + "}";
    }

    public boolean isDifferentIdentityTo(PersonnelBO personnel) {
        return !this.personnelId.equals(personnel.getPersonnelId());
    }

    public UserDetailDto toDto() {
        boolean loanOfficer = isLoanOfficer();
        return new UserDetailDto(this.office.getOfficeName(), this.personnelId.intValue(), this.globalPersonnelNum, this.personnelDetails.getName().getFirstName(), this.personnelDetails.getName().getLastName(), loanOfficer);
    }

    public void updateUserDetails(String firstName, String middleName, String secondLastName, String lastName,
            String email, Integer gender, Integer maritalStatus,
            Short preferredLocale, PersonnelStatusEntity personnelStatus, Address address, Integer title, PersonnelLevelEntity personnelLevel, List<RoleBO> roles, String password) {

        this.emailId = email;
        this.personnelDetails.updateNameDetails(firstName, middleName, secondLastName, lastName);
        this.personnelDetails.updateDetails(maritalStatus, gender);
        this.personnelDetails.updateAddress(address);
        this.displayName = this.personnelDetails.getDisplayName();

        if (title != null && title.intValue() == 0) {
            this.title = null;
        } else {
            this.title = title;
        }

        this.preferredLocale = new SupportedLocalesEntity(preferredLocale);
        this.status = personnelStatus;
        this.level = personnelLevel;

        // fix me, use encrpytion service outside of pojo?
        if (StringUtils.isNotBlank(password)) {
            this.encryptedPassword = getEncryptedPassword(password);
        }

        updatePersonnelRoles(roles);
    }

    public boolean isNonLoanOfficer() {
        return getLevelEnum().equals(PersonnelLevel.NON_LOAN_OFFICER);
    }

    public boolean isLevelDifferent(PersonnelLevel newLevel) {
        return !newLevel.getValue().equals(this.level.getId());
    }

    public boolean isOfficeDifferent(OfficeBO newOffice) {
        return this.office.isDifferent(newOffice);
    }

    public boolean isInActive() {
        return !isActive();
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}