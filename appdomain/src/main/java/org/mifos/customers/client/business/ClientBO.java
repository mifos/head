/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.customers.client.business;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.persistence.SavingsProductDao;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.ClientRules;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerHierarchyEntity;
import org.mifos.customers.business.CustomerMeetingEntity;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.domain.ClientFamilyInfoUpdate;
import org.mifos.dto.domain.ClientMfiInfoUpdate;
import org.mifos.dto.domain.ClientPersonalInfoUpdate;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.screen.ClientDetailDto;
import org.mifos.dto.screen.ClientFamilyDetailDto;
import org.mifos.dto.screen.ClientNameDetailDto;
import org.mifos.dto.screen.ClientPersonalDetailDto;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * FIXME - keithw - move all usage of deprecated constructors in tests and TestObjectFactory towards newer builder + IntegrationTestObjectMother pattern
 */
public class ClientBO extends CustomerBO {

    private static final Logger logger = LoggerFactory.getLogger(ClientBO.class);

    //business meta data
    private Date dateOfBirth;
    private String governmentId;
    private ClientDetailEntity customerDetail;
    private String firstName;
    private String lastName;
    private String secondLastName;
    private Set<ClientFamilyDetailEntity> familyDetailSet;

    //encapsulated business attributes
    private Short groupFlag;
    private Set<ClientAttendanceBO> clientAttendances;
    private Set<ClientInitialSavingsOfferingEntity> offeringsAssociatedInCreate;
    private OfficePersistence officePersistence;

    //internal business attributes
    private ClientPerformanceHistoryEntity clientPerformanceHistory;


    public static ClientBO createNewInGroupHierarchy(UserContext userContext, String clientName,
            CustomerStatus clientStatus, DateTime mfiJoiningDate, CustomerBO group, PersonnelBO formedBy,
            ClientNameDetailEntity clientNameDetailEntity,
            DateTime dateOfBirth, String governmentId, boolean trained, DateTime trainedDate, Short groupFlag,
            String clientFirstName, String clientLastName, String secondLastName,
            ClientNameDetailEntity spouseFatherNameDetailEntity, ClientDetailEntity clientDetailEntity,
            List<ClientInitialSavingsOfferingEntity> associatedOfferings, String externalId, Address address, LocalDate activationDate) {

        // inherit settings from parent (group)
        OfficeBO office = group.getOffice();
        MeetingBO meeting = group.getCustomerMeetingValue();

        if (clientStatus.isClientActive()) {
            Assert.notNull(meeting, "meeting inherited from parent group should not be null when client is active");
        }

        PersonnelBO loanOfficer = group.getPersonnel();

        ClientBO client = new ClientBO(userContext, clientName, clientStatus, mfiJoiningDate, office, meeting,
                loanOfficer, formedBy, dateOfBirth, governmentId, trained, trainedDate, groupFlag, clientFirstName,
                clientLastName, secondLastName, clientDetailEntity);

        client.setParentCustomer(group);

        client.generateSearchId();
        client.updateAddress(address);
        client.setExternalId(externalId);
        client.addNameDetailSet(clientNameDetailEntity);
        client.addNameDetailSet(spouseFatherNameDetailEntity);

        if (clientStatus.isClientActive()) {
            CustomerHierarchyEntity hierarchy = new CustomerHierarchyEntity(client, group);
            client.addCustomerHierarchy(hierarchy);
            client.setCustomerActivationDate(activationDate.toDateMidnight().toDate());
        }

        for (ClientInitialSavingsOfferingEntity clientInitialSavingsOfferingEntity : associatedOfferings) {
            client.addOfferingAssociatedInCreate(clientInitialSavingsOfferingEntity);
        }

        return client;
    }

    public static ClientBO createNewOutOfGroupHierarchy(UserContext userContext, String clientName,
            CustomerStatus clientStatus, DateTime mfiJoiningDate, OfficeBO office, PersonnelBO loanOfficer,
            MeetingBO meeting, PersonnelBO formedBy,
            ClientNameDetailEntity clientNameDetailEntity, DateTime dob, String governmentId, boolean trainedBool,
            DateTime trainedDateTime, Short groupFlagValue, String clientFirstName, String clientLastName,
            String secondLastName, ClientNameDetailEntity spouseFatherNameDetailEntity,
            ClientDetailEntity clientDetailEntity, List<ClientInitialSavingsOfferingEntity> associatedOfferings, String externalId, Address address, int numberOfCustomersInOfficeAlready) {

        ClientBO client = new ClientBO(userContext, clientName, clientStatus, mfiJoiningDate, office, meeting,
                loanOfficer, formedBy, dob, governmentId, trainedBool, trainedDateTime, groupFlagValue,
                clientFirstName, clientLastName, secondLastName, clientDetailEntity);

        client.setParentCustomer(null);

        client.generateSearchId();
        client.updateAddress(address);
        client.setExternalId(externalId);
        client.addNameDetailSet(clientNameDetailEntity);
        client.addNameDetailSet(spouseFatherNameDetailEntity);

        for (ClientInitialSavingsOfferingEntity clientInitialSavingsOfferingEntity : associatedOfferings) {
            client.addOfferingAssociatedInCreate(clientInitialSavingsOfferingEntity);
        }

        return client;
    }

    /**
     * default cosntructor for hibernate
     */
    protected ClientBO() {
        super();
        this.clientAttendances = new HashSet<ClientAttendanceBO>();
    }

    /**
     * minimal constructor for client creation
     */
    public ClientBO(UserContext userContext, String clientName, CustomerStatus clientStatus, DateTime mfiJoiningDate,
            OfficeBO office, MeetingBO meeting, PersonnelBO loanOfficer, PersonnelBO formedBy, DateTime dateOfBirth,
            String governmentId, boolean trained, DateTime trainedDate, Short groupFlag, String clientFirstName,
            String clientLastName, String secondLastName, ClientDetailEntity clientDetailEntity) {
        super(userContext, clientName, CustomerLevel.CLIENT, clientStatus, mfiJoiningDate, office, meeting,
                loanOfficer, formedBy);

        this.clientAttendances = new HashSet<ClientAttendanceBO>();
        this.offeringsAssociatedInCreate = new HashSet<ClientInitialSavingsOfferingEntity>();
        this.clientPerformanceHistory = new ClientPerformanceHistoryEntity(this);
        this.familyDetailSet = null;
        this.dateOfBirth = dateOfBirth.toDate();
        this.governmentId = governmentId;

        if (trained) {
            setTrained(trained);
        } else {
            setTrained(YesNoFlag.NO.getValue());
        }
        if (trainedDate != null) {
            setTrainedDate(trainedDate.toDate());
        }

        this.groupFlag = groupFlag;
        this.firstName = clientFirstName;
        this.lastName = clientLastName;
        this.secondLastName = secondLastName;

        if (clientDetailEntity != null) {
            clientDetailEntity.setClient(this);
            this.customerDetail = clientDetailEntity;
        }

        if (isActive()) {
            this.setCustomerActivationDate(this.getCreatedDate());
        }
    }

    /**
     * @deprecated - use static factory classes
     */
    @Deprecated
    public ClientBO(final UserContext userContext, final String displayName, final CustomerStatus customerStatus,
            final String externalId, final Date mfiJoiningDate, final Address address,
            final List<CustomFieldDto> customFields, final List<FeeDto> fees,
            final List<SavingsOfferingBO> offeringsSelected, final PersonnelBO formedBy, final OfficeBO office,
            final CustomerBO parentCustomer, final Date dateOfBirth, final String governmentId, final Short trained,
            final Date trainedDate, final Short groupFlag, final ClientNameDetailDto clientNameDetailDto,
            final ClientNameDetailDto spouseNameDetailView, final ClientPersonalDetailDto clientPersonalDetailDto) throws CustomerException {
        this(userContext, displayName, customerStatus, externalId, mfiJoiningDate, address, customFields, fees,
                offeringsSelected, formedBy, office, parentCustomer, null, null, dateOfBirth, governmentId, trained,
                trainedDate, groupFlag, clientNameDetailDto, spouseNameDetailView, clientPersonalDetailDto);
    }

    /**
     * @deprecated - use static factory classes
     */
    @Deprecated
    public ClientBO(final UserContext userContext, final String displayName, final CustomerStatus customerStatus,
            final String externalId, final Date mfiJoiningDate, final Address address,
            final List<CustomFieldDto> customFields, final List<FeeDto> fees,
            final List<SavingsOfferingBO> offeringsSelected, final PersonnelBO formedBy, final OfficeBO office,
            final MeetingBO meeting, final PersonnelBO loanOfficer, final Date dateOfBirth, final String governmentId,
            final Short trained, final Date trainedDate, final Short groupFlag,
            final ClientNameDetailDto clientNameDetailDto, final ClientNameDetailDto spouseNameDetailView,
            final ClientPersonalDetailDto clientPersonalDetailDto, final InputStream picture) throws CustomerException {
        this(userContext, displayName, customerStatus, externalId, mfiJoiningDate, address, customFields, fees,
                offeringsSelected, formedBy, office, null, meeting, loanOfficer, dateOfBirth, governmentId, trained,
                trainedDate, groupFlag, clientNameDetailDto, spouseNameDetailView, clientPersonalDetailDto);
    }

    /**
     * @deprecated - use static factory classes
     */
    @Deprecated
    private ClientBO(final UserContext userContext, final String displayName, final CustomerStatus customerStatus,
            final String externalId, final Date mfiJoiningDate, final Address address,
            final List<CustomFieldDto> customFields, final List<FeeDto> fees,
            final List<SavingsOfferingBO> offeringsSelected, final PersonnelBO formedBy, final OfficeBO office,
            final CustomerBO parentCustomer, final MeetingBO meeting, final PersonnelBO loanOfficer,
            final Date dateOfBirth, final String governmentId, final Short trained, final Date trainedDate,
            final Short groupFlag, final ClientNameDetailDto clientNameDetailDto,
            final ClientNameDetailDto spouseNameDetailView, final ClientPersonalDetailDto clientPersonalDetailDto)
                    throws CustomerException {
        super(userContext, displayName, CustomerLevel.CLIENT, customerStatus, externalId, mfiJoiningDate, address,
                customFields, fees, formedBy, office, parentCustomer, meeting, loanOfficer);
        validateOffice(office);
        validateNoDuplicateSavings(offeringsSelected);
        clientAttendances = new HashSet<ClientAttendanceBO>();
        offeringsAssociatedInCreate = new HashSet<ClientInitialSavingsOfferingEntity>();
        this.clientPerformanceHistory = new ClientPerformanceHistoryEntity(this);
        this.familyDetailSet = null;
        this.dateOfBirth = dateOfBirth;
        this.governmentId = governmentId;

        if (trained != null) {
            setTrained(trained);
        } else {
            setTrained(YesNoFlag.NO.getValue());
        }
        setTrainedDate(trainedDate);

        this.groupFlag = groupFlag;
        this.firstName = clientNameDetailDto.getFirstName();
        this.lastName = clientNameDetailDto.getLastName();
        this.secondLastName = clientNameDetailDto.getSecondLastName();
        clientNameDetailDto.setNames(ClientRules.getNameSequence());

        this.addNameDetailSet(new ClientNameDetailEntity(this, null, clientNameDetailDto));
        if (spouseNameDetailView != null) {
            this.addNameDetailSet(new ClientNameDetailEntity(this, null, spouseNameDetailView));
        }
        this.customerDetail = new ClientDetailEntity(this, clientPersonalDetailDto);
        createAssociatedOfferings(offeringsSelected);

        if (parentCustomer != null) {
            checkIfClientStatusIsLower(getStatus().getValue(), parentCustomer.getStatus().getValue());
        }

        if (isActive()) {
            validateFieldsForActiveClient(loanOfficer, meeting);
            this.setCustomerActivationDate(this.getCreatedDate());
            createAccountsForClient();

            List<Days> workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
            List<Holiday> holidays = new ArrayList<Holiday>();
            createDepositSchedule(workingDays, holidays);
        }
        generateSearchId();
    }

    public void addOfferingAssociatedInCreate(
            final ClientInitialSavingsOfferingEntity clientInitialSavingsOfferingEntity) {
        clientInitialSavingsOfferingEntity.setClient(this);
        this.offeringsAssociatedInCreate.add(clientInitialSavingsOfferingEntity);
    }

    public void setFamilyAndNameDetailSets(final List<ClientNameDetailDto> familyNameDetailView,
            final List<ClientFamilyDetailDto> familyDetailView) {
        Iterator<ClientFamilyDetailDto> iterator2 = familyDetailView.iterator();
        familyDetailSet = new HashSet<ClientFamilyDetailEntity>();
        for (Object element : familyNameDetailView) {
            ClientNameDetailDto clientNameDetailView2 = (ClientNameDetailDto) element;
            ClientFamilyDetailDto clientFamilyDetailView2 = iterator2.next();
            ClientNameDetailEntity nameEntity = new ClientNameDetailEntity(this, null, clientNameDetailView2);
            this.addNameDetailSet(nameEntity);
            this.addFamilyDetailSet(new ClientFamilyDetailEntity(this, nameEntity, clientFamilyDetailView2));
        }

    }

    public Set<ClientFamilyDetailEntity> getFamilyDetailSet() {
        return this.familyDetailSet;
    }

    public Set<ClientAttendanceBO> getClientAttendances() {
        return clientAttendances;
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(final Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGovernmentId() {
        return governmentId;
    }

    void setGovernmentId(final String governmentId) {
        this.governmentId = governmentId;
    }

    public ClientDetailEntity getCustomerDetail() {
        return customerDetail;
    }

    public void setCustomerDetail(final ClientDetailEntity customerDetail) {
        this.customerDetail = customerDetail;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getSecondLastName() {
        return secondLastName;
    }

    public void setSecondLastName(final String secondLastName) {
        this.secondLastName = secondLastName;
    }

    public Double getPovertyLikelihoodPercent() {
        return this.customerDetail.getPovertyLikelihoodPercent();
    }

    /**
     * TODO: This method is deprecated and should be removed once method attachPpiSurvey is implemented. Poverty
     * likelihood should be set based on the results of a PPI survey and should not be over-writable in order to
     * maintain the integrity of this data.
     *
     * The method is included here in order to test hibernate mappings through customerDetail.
     */
    public void setPovertyLikelihoodPercent(final Double pct) {
        this.customerDetail.setPovertyLikelihoodPercent(pct);
    }

    public Set<ClientInitialSavingsOfferingEntity> getOfferingsAssociatedInCreate() {
        return offeringsAssociatedInCreate;
    }

    public ClientPerformanceHistoryEntity getClientPerformanceHistory() {
        return clientPerformanceHistory;
    }

    public void setClientPerformanceHistory(final ClientPerformanceHistoryEntity clientPerformanceHistory) {
        if (clientPerformanceHistory != null) {
            clientPerformanceHistory.setClient(this);
        }
        this.clientPerformanceHistory = clientPerformanceHistory;
    }

    public void addClientAttendance(final ClientAttendanceBO clientAttendance) {
        clientAttendance.setCustomer(this);
        clientAttendances.add(clientAttendance);
    }

    public void addNameDetailSet(final ClientNameDetailEntity customerNameDetail) {

        if (customerNameDetail != null) {
            customerNameDetail.setClient(this);
            getNameDetailSet().add(customerNameDetail);
        }
    }

    public void addFamilyDetailSet(final ClientFamilyDetailEntity clientFamilyDetail) {
        this.familyDetailSet.add(clientFamilyDetail);
    }

    @Override
    public final boolean isActive() {
        return getStatus() == CustomerStatus.CLIENT_ACTIVE;
    }

    public boolean isOnHold() {
        return getStatus() == CustomerStatus.CLIENT_HOLD;
    }

    public boolean isClientUnderGroup() {
        return groupFlag.equals(YesNoFlag.YES.getValue());
    }

    public ClientAttendanceBO getClientAttendanceForMeeting(final Date meetingDate) {
        for (ClientAttendanceBO clientAttendance : getClientAttendances()) {
            if (DateUtils.getDateWithoutTimeStamp(clientAttendance.getMeetingDate().getTime()).compareTo(
                    DateUtils.getDateWithoutTimeStamp(meetingDate.getTime())) == 0) {
                return clientAttendance;
            }
        }
        return null;
    }

    // when this method is called from Bulk Entry preview persist will be false
    public void handleAttendance(final Date meetingDate, final Short attendance, final boolean persist)
            throws CustomerException {
        ClientAttendanceBO clientAttendance = getClientAttendanceForMeeting(meetingDate);
        if (clientAttendance == null) {
            clientAttendance = new ClientAttendanceBO();
            clientAttendance.setMeetingDate(meetingDate);
            addClientAttendance(clientAttendance);
        }
        clientAttendance.setAttendance(attendance);
        if (persist) {
            try {
                getCustomerPersistence().createOrUpdate(this);
            } catch (PersistenceException e) {
                throw new CustomerException(e);
            }
        }
    }

    public void handleAttendance(final Date meetingDate, final AttendanceType attendance) throws CustomerException {
        boolean persist = true;
        handleAttendance(meetingDate, attendance.getValue(), persist);
    }

    @Override
    public boolean isActiveForFirstTime(final Short oldStatus, final Short newStatusId) {
        return (oldStatus.equals(CustomerStatus.CLIENT_PARTIAL.getValue()) || oldStatus
                .equals(CustomerStatus.CLIENT_PENDING.getValue()))
                && newStatusId.equals(CustomerStatus.CLIENT_ACTIVE.getValue());
    }

    public void updatePersonalInfo(ClientPersonalInfoUpdate personalInfo) throws CustomerException {

        this.governmentId = personalInfo.getGovernmentId();
        try {
            setDateOfBirth(DateUtils.getDateAsSentFromBrowser(personalInfo.getDateOfBirth()));
        } catch (InvalidDateException e) {
            throw new CustomerException(ClientConstants.INVALID_DOB_EXCEPTION);
        }
        ClientNameDetailDto clientName = personalInfo.getClientNameDetails();
        this.getClientName().updateNameDetails(clientName);
        this.firstName = clientName.getFirstName();
        this.lastName = clientName.getLastName();
        this.secondLastName = clientName.getSecondLastName();

        if (personalInfo.getSpouseFather() != null) {
            // can be null when family details configuration is turned on
            if (this.getSpouseName() != null) {
                this.getSpouseName().updateNameDetails(personalInfo.getSpouseFather());
            } else {
                ClientNameDetailEntity spouseFatherNameDetailEntity = new ClientNameDetailEntity(this, personalInfo.getSpouseFather().getSecondLastName(), personalInfo.getSpouseFather());
                addNameDetailSet(spouseFatherNameDetailEntity);
            }
        }
        this.updateClientDetails(personalInfo.getClientDetail());
        setDisplayName(personalInfo.getClientDisplayName());

        Address address = null;
        if (personalInfo.getAddress() != null) {
            ;
        } {
            address = new Address(personalInfo.getAddress().getLine1(), personalInfo.getAddress().getLine2(), personalInfo.getAddress().getLine3(),
                    personalInfo.getAddress().getCity(), personalInfo.getAddress().getState(), personalInfo.getAddress().getCountry(),
                    personalInfo.getAddress().getZip(), personalInfo.getAddress().getPhoneNumber());
            updateAddress(address);
        }
    }

    /**
     * This method is called for client family and name details update
     */
    public void updateFamilyInfo(ClientFamilyInfoUpdate clientFamilyInfoUpdate) throws PersistenceException {

        updateFamilyAndNameDetails(clientFamilyInfoUpdate);
        deleteFamilyAndNameDetails(clientFamilyInfoUpdate.getFamilyPrimaryKey());
        insertFamilyAndNameDetails(clientFamilyInfoUpdate);
    }

    /**
     * This method return boolean value
     *
     * @param clientNameId
     * @param primaryKeys
     * @return
     */
    private boolean isKeyExists(final int clientNameId, final List<Integer> primaryKeys) {
        boolean keyFound = false;
        for (int i = 0; i < primaryKeys.size(); i++) {
            if (primaryKeys.get(i) != null && primaryKeys.get(i) == clientNameId) {
                keyFound = true;
            }
        }
        return keyFound;
    }

    /**
     * This method is used to update the Client Family and Name Details
     */
    public void updateFamilyAndNameDetails(ClientFamilyInfoUpdate clientFamilyInfoUpdate) {

        List<Integer> primaryKeys = clientFamilyInfoUpdate.getFamilyPrimaryKey();

        for (int key = 0; key < primaryKeys.size(); key++) {
            if (primaryKeys.get(key) != null) {

                List<ClientNameDetailDto> clientNameDetailDto = clientFamilyInfoUpdate.getFamilyNames();
                for (ClientNameDetailEntity clientNameDetailEntity : getNameDetailSet()) {
                    if (clientNameDetailEntity.getCustomerNameId().intValue() == primaryKeys.get(key).intValue()) {

                        ClientNameDetailDto nameView = clientNameDetailDto.get(key);
                        if (nameView != null) {
                            clientNameDetailEntity.updateNameDetails(nameView);

                            // if switched from familyDetailsRequired=false to true then migrate clientNameDetail to
                            // family
                            // details table.
                            if (ClientRules.isFamilyDetailsRequired()) {
                                List<ClientFamilyDetailDto> clientFamilyDetailDto = clientFamilyInfoUpdate
                                        .getFamilyDetails();

                                // check each detail to see if it is part of familyDetails and if not, add it to
                                // familyDetails
                                for (ClientFamilyDetailDto clientFamilyDetail : clientFamilyDetailDto) {

                                    if (familyDetailsDoesNotAlreadyContain(clientNameDetailEntity.getCustomerNameId())) {
                                        ClientFamilyDetailEntity clientFamilyEntity = new ClientFamilyDetailEntity(
                                                this, clientNameDetailEntity, clientFamilyDetail);
                                        familyDetailSet.add(clientFamilyEntity);
                                    }
                                }
                            }
                        }
                    }

                    List<ClientFamilyDetailDto> clientFamilyDetailDto = clientFamilyInfoUpdate.getFamilyDetails();
                    for (ClientFamilyDetailEntity clientFamilyDetailEntity : familyDetailSet) {
                        if (clientFamilyDetailEntity.getClientName().getCustomerNameId().intValue() == primaryKeys.get(
                                key).intValue()) {
                            clientFamilyDetailEntity.updateClientFamilyDetails(clientFamilyDetailDto.get(key));
                        }
                    }
                }
            }
        }
    }

    private boolean familyDetailsDoesNotAlreadyContain(Integer customerNameId) {

        boolean resultNotFound = true;
        for (ClientFamilyDetailEntity clientFamilyDetailEntity : familyDetailSet) {
            if (clientFamilyDetailEntity.getClientName().getCustomerNameId().intValue() == customerNameId.intValue()) {
                return false;
            }
        }

        return resultNotFound;
    }

    /**
     * This method is used to delete the Client Family and Name Details
     *
     * @param primaryKeys
     * @throws PersistenceException
     */
    public void deleteFamilyAndNameDetails(final List<Integer> primaryKeys) throws PersistenceException {
        // check for the primary if that is null crate the data
        // get all the family entities to delete
        List<ClientFamilyDetailEntity> deleteFamilyDetailEntity = new ArrayList<ClientFamilyDetailEntity>();
        for (ClientFamilyDetailEntity clientFamilyDetailEntity : familyDetailSet) {
            if (!isKeyExists(clientFamilyDetailEntity.getClientName().getCustomerNameId(), primaryKeys)) {
                deleteFamilyDetailEntity.add(clientFamilyDetailEntity);
            }
        }
        // get all the name entities to delete
        List<ClientNameDetailEntity> deleteNameDetailEntity = new ArrayList<ClientNameDetailEntity>();
        for (ClientNameDetailEntity clientNameDetailEntity : getNameDetailSet()) {
            // Ignoring name entities of client type
            if (!ClientConstants.CLIENT_NAME_TYPE.equals(clientNameDetailEntity.getNameType())) {
                if (!isKeyExists(clientNameDetailEntity.getCustomerNameId(), primaryKeys)) {
                    deleteNameDetailEntity.add(clientNameDetailEntity);
                }
            }
        }
        // Delete ClientFamilyDetailEntity
        for (int i = 0; i < deleteFamilyDetailEntity.size(); i++) {
            familyDetailSet.remove(deleteFamilyDetailEntity.get(i));
            getCustomerPersistence().delete(deleteFamilyDetailEntity.get(i));
        }
        // Delete ClientNameDetailEntity
        for (int i = 0; i < deleteNameDetailEntity.size(); i++) {
            getNameDetailSet().remove(deleteNameDetailEntity.get(i));
            getCustomerPersistence().delete(deleteNameDetailEntity.get(i));
        }
    }

    /**
     * This method is used to insert the Client Family and Name Details
     */
    public void insertFamilyAndNameDetails(ClientFamilyInfoUpdate clientFamilyInfoUpdate) {

        List<Integer> primaryKeys = clientFamilyInfoUpdate.getFamilyPrimaryKey();

        for (int i = 0; i < primaryKeys.size(); i++) {
            if (primaryKeys.get(i) == null) {
                ClientNameDetailEntity nameDetail = new ClientNameDetailEntity(this, null, clientFamilyInfoUpdate
                        .getFamilyNames().get(i));
                getNameDetailSet().add(nameDetail);
                familyDetailSet.add(new ClientFamilyDetailEntity(this, nameDetail, clientFamilyInfoUpdate
                        .getFamilyDetails().get(i)));
            }
        }
    }

    public void updateMfiInfo(final PersonnelBO personnel, ClientMfiInfoUpdate clientMfiInfoUpdate) throws CustomerException {

        setExternalId(clientMfiInfoUpdate.getExternalId());
        setTrained(clientMfiInfoUpdate.isTrained());
        if (clientMfiInfoUpdate.getTrainedDate() != null) {
            setTrainedDate(clientMfiInfoUpdate.getTrainedDate().toDate());
        }

        setPersonnel(personnel);
        if (isActive() || isOnHold()) {
            validateLoanOfficer();
        }

        for (AccountBO account : this.getAccounts()) {
            account.setPersonnel(this.getPersonnel());
        }
    }

    public void transferToBranch(final OfficeBO officeToTransfer) throws CustomerException {
        validateBranchTransfer(officeToTransfer);
        logger.debug("In ClientBO::transferToBranch(), transfering customerId: " + getCustomerId() + "to branch : "
                + officeToTransfer.getOfficeId());
        if (isActive()) {
            setCustomerStatus(new CustomerStatusEntity(CustomerStatus.CLIENT_HOLD));
        }

        makeCustomerMovementEntries(officeToTransfer);
        this.setPersonnel(null);
        logger.debug("In ClientBO::transferToBranch(), successfully transfered, customerId :" + getCustomerId());
    }

    public void handleGroupTransfer() {
        if (!isSameBranch(getParentCustomer().getOffice())) {
            makeCustomerMovementEntries(getParentCustomer().getOffice());
            if (isActive()) {
                setCustomerStatus(new CustomerStatusEntity(CustomerStatus.CLIENT_HOLD));
            }
            this.setPersonnel(null);
        }
        setSearchId(getParentCustomer().getSearchId() + getSearchId().substring(getSearchId().lastIndexOf(".")));

        if (getParentCustomer() != null) {
            setPersonnel(getParentCustomer().getPersonnel());

            CustomerMeetingEntity clientMeetingEntity = this.getCustomerMeeting();
            clientMeetingEntity.setMeeting(this.getParentCustomer().getCustomerMeetingValue());
        }
    }

    public ClientNameDetailEntity getClientName() {
        for (ClientNameDetailEntity nameDetail : getNameDetailSet()) {
            if (ClientConstants.CLIENT_NAME_TYPE.equals(nameDetail.getNameType())) {
                return nameDetail;
            }
        }
        return null;
    }

    public ClientNameDetailEntity getSpouseName() {
        for (ClientNameDetailEntity nameDetail : getNameDetailSet()) {
            if (!ClientConstants.CLIENT_NAME_TYPE.equals(nameDetail.getNameType())) {
                return nameDetail;
            }
        }
        return null;
    }

    public void updateClientDetails(final ClientPersonalDetailDto clientPersonalDetailDto) {
        customerDetail.updateClientDetails(clientPersonalDetailDto);
    }

    public void createAssociatedOfferings(final List<SavingsOfferingBO> offeringsSelected) {
        if (offeringsSelected != null) {
            for (SavingsOfferingBO offering : offeringsSelected) {
                this.offeringsAssociatedInCreate.add(new ClientInitialSavingsOfferingEntity(this, offering));
            }
        }
    }

    public void validateForActiveAccounts() throws CustomerException {
        if (isAnyAccountActive()) {
            throw new CustomerException(ClientConstants.ERRORS_ACTIVE_ACCOUNTS_PRESENT,
            		new Object[] { ApplicationContextProvider.getBean(MessageLookup.class).lookupLabel(ConfigurationConstants.GROUP) });
        }
    }
    
    public void validateForPeriodicFees() throws CustomerException {
    	if (isAnyPeriodicFeeActive()) {
    		throw new CustomerException(ClientConstants.ERRORS_ACTIVE_PERIODIC_FEES_PRESENT);
    	}
    }

    private void validateBranchTransfer(final OfficeBO officeToTransfer) throws CustomerException {
        if (officeToTransfer == null) {
            throw new CustomerException(CustomerConstants.INVALID_OFFICE);
        }

        if (isSameBranch(officeToTransfer)) {
            throw new CustomerException(CustomerConstants.ERRORS_SAME_BRANCH_TRANSFER);
        }

        if (!officeToTransfer.isActive()) {
            throw new CustomerException(CustomerConstants.ERRORS_TRANSFER_IN_INACTIVE_OFFICE);
        }
    }

    private boolean isSameGroup(final GroupBO group) {
        return isSameGroup(group.getCustomerId());
    }

    private boolean isSameGroup(final Integer groupId) {

        boolean isSameGroup = false;
        if (this.getParentCustomer() != null) {
            isSameGroup = getParentCustomer().getCustomerId().equals(groupId);
        }

        return isSameGroup;
    }


    public void validateIsSameGroup(Integer groupId) throws CustomerException {
        if (isSameGroup(groupId)) {
            throw new CustomerException(CustomerConstants.ERRORS_SAME_GROUP_TRANSFER);
        }
    }

    public void validateReceivingGroup(final GroupBO toGroup) throws CustomerException {
        if (toGroup == null) {
            throw new CustomerException(CustomerConstants.INVALID_PARENT);
        }

        validateForGroupStatus(toGroup.getStatus());
    }

    private void validateForGroupStatus(final CustomerStatus groupStatus) throws CustomerException {
        if (isGroupStatusLower(getStatus(), groupStatus)) {
            throw new CustomerException(ClientConstants.ERRORS_LOWER_GROUP_STATUS);
        }
        if (groupStatus.equals(CustomerStatus.GROUP_CANCELLED) || groupStatus.equals(CustomerStatus.GROUP_CLOSED)) {
            throw new CustomerException(CustomerConstants.ERRORS_INTRANSFER_PARENT_INACTIVE);
        }

    }

    public void validateFieldsForActiveClient() throws CustomerException {
        if (isActive()) {
            if (!isClientUnderGroup()) {
                validateLoanOfficer();
                validateMeetingEntity(this.getCustomerMeeting());
            }
        }
    }

    /**
     *
     */
    @Deprecated
    private void validateFieldsForActiveClient(final PersonnelBO loanOfficer, final MeetingBO meeting)
            throws CustomerException {
        if (isActive()) {
            if (!isClientUnderGroup()) {
                validateLO(loanOfficer);
                validateMeeting(meeting);
            }
        }
    }

    private boolean isGroupStatusLower(final CustomerStatus clientStatus, final CustomerStatus groupStatus) {
        if (clientStatus.equals(CustomerStatus.CLIENT_PENDING)) {
            if (groupStatus.equals(CustomerStatus.GROUP_PARTIAL)) {
                return true;
            }
        } else if (clientStatus.equals(CustomerStatus.CLIENT_ACTIVE) || clientStatus.equals(CustomerStatus.CLIENT_HOLD)) {
            if (groupStatus.equals(CustomerStatus.GROUP_PARTIAL) || groupStatus.equals(CustomerStatus.GROUP_PENDING)) {
                return true;
            }
        }
        return false;
    }

    public void validateClientStatus() throws CustomerException {

        final Short clientStatusId = getStatus().getValue();
        final Short groupStatus = getParentCustomer().getStatus().getValue();

        if ((clientStatusId.equals(CustomerStatus.CLIENT_ACTIVE.getValue()) || clientStatusId
                .equals(CustomerStatus.CLIENT_PENDING.getValue()))
                && this.isClientUnderGroup()) {
            if (groupStatus.equals(CustomerStatus.GROUP_CANCELLED.getValue())) {
                throw new CustomerException(ClientConstants.ERRORS_GROUP_CANCELLED,
                        new Object[] { ApplicationContextProvider.getBean(MessageLookup.class).lookupLabel(ConfigurationConstants.GROUP) });
            }

            if (isGroupStatusLower(clientStatusId, groupStatus)) {

                throw new CustomerException(ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION);
            }
        }
    }

    @Deprecated
    public void checkIfClientStatusIsLower(final Short clientStatusId, final Short groupStatus)
            throws CustomerException {
        if ((clientStatusId.equals(CustomerStatus.CLIENT_ACTIVE.getValue()) || clientStatusId
                .equals(CustomerStatus.CLIENT_PENDING.getValue()))
                && this.isClientUnderGroup()) {
            if (groupStatus.equals(CustomerStatus.GROUP_CANCELLED.getValue())) {
                throw new CustomerException(ClientConstants.ERRORS_GROUP_CANCELLED,
                        new Object[] { ApplicationContextProvider.getBean(MessageLookup.class).lookupLabel(ConfigurationConstants.GROUP) });
            }

            if (isGroupStatusLower(clientStatusId, groupStatus)) {

                throw new CustomerException(ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION);
            }
        }
    }

    /**
     * remove when constructor is removed
     */
    @Deprecated
    public void createAccountsForClient() throws CustomerException {
        if (offeringsAssociatedInCreate != null) {
            for (ClientInitialSavingsOfferingEntity clientOffering : offeringsAssociatedInCreate) {
                try {
                    SavingsOfferingBO savingsOffering = ApplicationContextProvider.getBean(SavingsProductDao.class).findById(
                            clientOffering.getSavingsOffering().getPrdOfferingId().intValue());
                    if (savingsOffering.isActive()) {
                        addAccount(new SavingsBO(getUserContext(), savingsOffering, this, AccountState.SAVINGS_ACTIVE,
                                savingsOffering.getRecommendedAmount(),
                                new ArrayList<CustomFieldDto>()));
                    }
                } catch (AccountException pe) {
                    throw new CustomerException(pe);
                }
            }
        }
    }

    public boolean isGroupStatusLower(final Short clientStatusId, final Short parentStatus) {
        return isGroupStatusLower(CustomerStatus.fromInt(clientStatusId), CustomerStatus.fromInt(parentStatus));
    }

    public void validateNoDuplicateSavings(final List<SavingsOfferingBO> offeringsSelected) throws CustomerException {
        if (offeringsSelected != null) {
            for (int i = 0; i < offeringsSelected.size() - 1; i++) {
                for (int j = i + 1; j < offeringsSelected.size(); j++) {
                    if (offeringsSelected.get(i).getPrdOfferingId().equals(offeringsSelected.get(j).getPrdOfferingId())) {
                        throw new CustomerException(ClientConstants.ERRORS_DUPLICATE_OFFERING_SELECTED);
                    }
                }
            }
        }
    }

    /**
     * delete when usage in constructor is removed...
     */
    @Deprecated
    public void createDepositSchedule(final List<Days> workingDays, final List<Holiday> holidays)
            throws CustomerException {
        try {
            if (getParentCustomer() != null) {
                List<SavingsBO> savingsList = getCustomerPersistence().retrieveSavingsAccountForCustomer(
                        getParentCustomer().getCustomerId());
                if (getParentCustomer().getParentCustomer() != null) {
                    savingsList.addAll(getCustomerPersistence().retrieveSavingsAccountForCustomer(
                            getParentCustomer().getParentCustomer().getCustomerId()));
                }
                for (SavingsBO savings : savingsList) {
                    savings.setUserContext(getUserContext());
                    savings.generateAndUpdateDepositActionsForClient(this, workingDays, holidays);
                }
            }
        } catch (PersistenceException pe) {
            throw new CustomerException(pe);
        } catch (AccountException ae) {
            throw new CustomerException(ae);
        }
    }

    public void validateBeforeAddingClientToGroup(GroupBO targetGroup) throws CustomerException {
        if (isClientCancelledOrClosed()) {
            throw new CustomerException(CustomerConstants.CLIENT_IS_CLOSED_OR_CANCELLED_EXCEPTION);
        }
        if (isAnyLoanAccountOpen()) {
            throw new CustomerException(CustomerConstants.CLIENT_HAVE_OPEN_LOAN_ACCOUNT_EXCEPTION);
        }
        if (isGroupStatusLower(getStatus(), targetGroup.getStatus())) {
            throw new CustomerException(CustomerConstants.TARGET_GROUP_STATUS_LOWER_THAN_CLIENT);
        }
    }

    private boolean isClientCancelledOrClosed() {
        return getStatus() == CustomerStatus.CLIENT_CLOSED || getStatus() == CustomerStatus.CLIENT_CANCELLED ? true
                : false;
    }

    @Override
    public void updatePerformanceHistoryOnDisbursement(final LoanBO loan, final Money disburseAmount) {
        ClientPerformanceHistoryEntity performanceHistory = (ClientPerformanceHistoryEntity) getPerformanceHistory();
        performanceHistory.updateOnDisbursement(loan.getLoanOffering());
    }

    @Override
    public void updatePerformanceHistoryOnWriteOff(final LoanBO loan) {
        ClientPerformanceHistoryEntity performanceHistory = (ClientPerformanceHistoryEntity) getPerformanceHistory();
        performanceHistory.updateOnWriteOff(loan.getLoanOffering());
    }

    @Override
    public void updatePerformanceHistoryOnReversal(final LoanBO loan, final Money lastLoanAmount)
            throws CustomerException {
        ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) getPerformanceHistory();
        clientPerfHistory.updateOnReversal(loan.getLoanOffering(), lastLoanAmount);
    }

    @Override
    public void updatePerformanceHistoryOnRepayment(final LoanBO loan, final Money totalAmount) {
        ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) getPerformanceHistory();
        clientPerfHistory.updateOnFullRepayment(loan.getLoanAmount());
    }

    @Override
    public void updatePerformanceHistoryOnLastInstlPayment(final LoanBO loan, final Money totalAmount)
            throws CustomerException {
        updatePerformanceHistoryOnRepayment(loan, totalAmount);
    }

    public boolean isClosedOrCancelled() {
        return getCustomerStatus().getId().equals(CustomerStatus.CLIENT_CLOSED.getValue())
                || getCustomerStatus().getId().equals(CustomerStatus.CLIENT_CANCELLED.getValue());
    }

    public boolean isPending() {
        return this.getStatus().isClientPending();
    }

    public final List<ClientNameDetailDto> toClientNameDetailViews() {

        List<ClientNameDetailDto> clientNameDetailDtos = new ArrayList<ClientNameDetailDto>();
        for (ClientNameDetailEntity clientNameDetail : getNameDetailSet()) {

            ClientNameDetailDto clientNameDetailDto = clientNameDetail.toDto();
            clientNameDetailDtos.add(clientNameDetailDto);
        }
        return clientNameDetailDtos;
    }

    public ClientDetailDto toClientDetailDto(boolean isFamilyDetailsRequired) {

        ClientPersonalDetailDto customerDetailView = this.customerDetail.toDto();

        String dateOfBirthAsString = "";
        if (this.dateOfBirth != null) {
            dateOfBirthAsString = DateUtils.makeDateAsSentFromBrowser(this.dateOfBirth);
        }

        List<ClientNameDetailDto> clientNameViews = toClientNameDetailViews();

        ClientNameDetailDto clientName = null;
        ClientNameDetailDto spouseName = null;
        for (ClientNameDetailDto nameView : clientNameViews) {
            if (nameView.getNameType() != null) {
                if (nameView.getNameType().equals(ClientConstants.CLIENT_NAME_TYPE)) {
                    clientName = nameView;
                } else if (!isFamilyDetailsRequired) {
                    spouseName = nameView;
                }
            }
            else {
                spouseName = new ClientNameDetailDto();
            }
        }

        boolean groupFlagIsSet = false;
        Integer parentGroupId = Integer.valueOf(0);
        if (isClientUnderGroup()) {
            groupFlagIsSet = true;
            parentGroupId = getParentCustomer().getCustomerId();
        }

        boolean trained = isTrained();

        String trainedDate = "";
        if (getTrainedDate() != null) {
            trainedDate = DateUtils.makeDateAsSentFromBrowser(getTrainedDate());
        }

        return new ClientDetailDto(this.governmentId, dateOfBirthAsString, customerDetailView, clientName, spouseName,
                groupFlagIsSet, parentGroupId, trained, trainedDate);
    }

    public OfficePersistence getOfficePersistence() {
        if (null == officePersistence) {
            officePersistence = new OfficePersistence();
        }
        return officePersistence;
    }

    public void setOfficePersistence(final OfficePersistence officePersistence) {
        this.officePersistence = officePersistence;
    }

    public void setOfferingsAssociatedInCreate(Set<ClientInitialSavingsOfferingEntity> offeringsAssociatedInCreate) {
        this.offeringsAssociatedInCreate = offeringsAssociatedInCreate;
    }

    public void addSavingsAccounts(List<SavingsBO> savingsAccounts) {
        for (SavingsBO savingsAccount : savingsAccounts) {
            addAccount(savingsAccount);
        }
    }

    public boolean isStatusValidationRequired() {
        return getParentCustomer() != null;
    }

    @Override
    public void validate() throws CustomerException {
        super.validate();
        if (isStatusValidationRequired()) {
            this.validateClientStatus();
        }
    }

    public boolean transferTo(GroupBO receivingGroup) {

        boolean regenerateClientSchedules = false;

        this.setParentCustomer(receivingGroup);
        this.setPersonnel(receivingGroup.getPersonnel());

        CustomerHierarchyEntity currentHierarchy = this.getActiveCustomerHierarchy();
        if (null != currentHierarchy) {
            currentHierarchy.makeInactive(userContext.getId());
        }
        addCustomerHierarchy(new CustomerHierarchyEntity(this, receivingGroup));

        MeetingBO parentGroupMeeting = receivingGroup.getCustomerMeetingValue();
        MeetingBO clientMeeting = this.getCustomerMeetingValue();
        if (parentGroupMeeting != null) {
            if (clientMeeting != null) {
                regenerateClientSchedules = receivingGroup.hasMeetingDifferentTo(clientMeeting);

                CustomerMeetingEntity clientMeetingEntity = this.getCustomerMeeting();
                clientMeetingEntity.setMeeting(receivingGroup.getCustomerMeetingValue());
            } else {
                CustomerMeetingEntity customerMeeting = this.createCustomerMeeting(parentGroupMeeting);
                this.setCustomerMeeting(customerMeeting);
            }
        } else if (clientMeeting != null) {
            this.setCustomerMeeting(null);
        }

        if (!isSameBranch(receivingGroup.getOffice())) {
            this.makeCustomerMovementEntries(receivingGroup.getOffice());
            regenerateClientSchedules = true;
        }

        this.addGroupMembership();
        this.generateSearchId();

        return regenerateClientSchedules;
    }

    private void addGroupMembership() {
        this.groupFlag = YesNoFlag.YES.getValue();
    }

    public void removeGroupMembership() {
        this.groupFlag = YesNoFlag.NO.getValue();
    }


}