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

package org.mifos.customers.client.business;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.persistence.SavingsPrdPersistence;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.persistence.SavingsPersistence;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.ClientDetailDto;
import org.mifos.application.servicefacade.ClientFamilyInfoUpdate;
import org.mifos.application.servicefacade.ClientPersonalInfoUpdate;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.config.business.MifosConfiguration;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerCustomFieldEntity;
import org.mifos.customers.business.CustomerHierarchyEntity;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.client.persistence.ClientPersistence;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.surveys.business.SurveyInstance;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;

/**
 * FIXME - keithw - move all usage of deprecated constructors in tests and TestObjectFactory towards newer builder + IntegrationTestObjectMother pattern
 */
public class ClientBO extends CustomerBO {

    private static final MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CLIENTLOGGER);

    private CustomerPictureEntity customerPicture;
    private Date dateOfBirth;
    private String governmentId;
    private ClientPerformanceHistoryEntity clientPerformanceHistory;
    private Short groupFlag;
    private String firstName;
    private String lastName;
    private String secondLastName;
    private ClientDetailEntity customerDetail;

    private final Set<ClientNameDetailEntity> nameDetailSet;
    private Set<ClientFamilyDetailEntity> familyDetailSet;
    private final Set<ClientAttendanceBO> clientAttendances;
    private Set<ClientInitialSavingsOfferingEntity> offeringsAssociatedInCreate;

    private ClientPersistence clientPersistence = null;
    private SavingsPersistence savingsPersistence = null;
    private SavingsPrdPersistence savingsPrdPersistence = null;
    private OfficePersistence officePersistence = null;

    public static ClientBO createNewInGroupHierarchy(UserContext userContext, String clientName,
            CustomerStatus clientStatus, DateTime mfiJoiningDate, CustomerBO group, PersonnelBO formedBy,
            List<CustomerCustomFieldEntity> customerCustomFields, ClientNameDetailEntity clientNameDetailEntity,
            DateTime dateOfBirth, String governmentId, boolean trained, DateTime trainedDate, Short groupFlag,
            String clientFirstName, String clientLastName, String secondLastName,
            ClientNameDetailEntity spouseFatherNameDetailEntity, ClientDetailEntity clientDetailEntity,
            Blob pictureAsBlob, List<ClientInitialSavingsOfferingEntity> associatedOfferings, String externalId, Address address) {

        // inherit settings from parent (group)
        OfficeBO office = group.getOffice();
        MeetingBO meeting = group.getCustomerMeetingValue();
        PersonnelBO loanOfficer = group.getPersonnel();

        ClientBO client = new ClientBO(userContext, clientName, clientStatus, mfiJoiningDate, office, meeting,
                loanOfficer, formedBy, dateOfBirth, governmentId, trained, trainedDate, groupFlag, clientFirstName,
                clientLastName, secondLastName, clientDetailEntity);

        client.setParentCustomer(group);
        client.updateAddress(address);
        client.setExternalId(externalId);
        client.addNameDetailSet(clientNameDetailEntity);
        client.addNameDetailSet(spouseFatherNameDetailEntity);
        client.createOrUpdatePicture(pictureAsBlob);

        for (ClientInitialSavingsOfferingEntity clientInitialSavingsOfferingEntity : associatedOfferings) {
            client.addOfferingAssociatedInCreate(clientInitialSavingsOfferingEntity);
        }

        List<CustomerCustomFieldEntity> populatedWithCustomerReference = CustomerCustomFieldEntity
                .fromCustomerCustomFieldEntity(customerCustomFields, client);
        for (CustomerCustomFieldEntity customerCustomFieldEntity : populatedWithCustomerReference) {
            client.addCustomField(customerCustomFieldEntity);
        }

        return client;
    }

    public static ClientBO createNewOutOfGroupHierarchy(UserContext userContext, String clientName,
            CustomerStatus clientStatus, DateTime mfiJoiningDate, OfficeBO office, PersonnelBO loanOfficer,
            MeetingBO meeting, PersonnelBO formedBy, List<CustomerCustomFieldEntity> customerCustomFields,
            ClientNameDetailEntity clientNameDetailEntity, DateTime dob, String governmentId, boolean trainedBool,
            DateTime trainedDateTime, Short groupFlagValue, String clientFirstName, String clientLastName,
            String secondLastName, ClientNameDetailEntity spouseFatherNameDetailEntity,
            ClientDetailEntity clientDetailEntity, Blob pictureAsBlob,
            List<ClientInitialSavingsOfferingEntity> associatedOfferings, String externalId, Address address) {

        ClientBO client = new ClientBO(userContext, clientName, clientStatus, mfiJoiningDate, office, meeting,
                loanOfficer, formedBy, dob, governmentId, trainedBool, trainedDateTime, groupFlagValue,
                clientFirstName, clientLastName, secondLastName, clientDetailEntity);

        client.setParentCustomer(null);
        client.updateAddress(address);
        client.setExternalId(externalId);
        client.addNameDetailSet(clientNameDetailEntity);
        client.addNameDetailSet(spouseFatherNameDetailEntity);
        client.createOrUpdatePicture(pictureAsBlob);

        for (ClientInitialSavingsOfferingEntity clientInitialSavingsOfferingEntity : associatedOfferings) {
            client.addOfferingAssociatedInCreate(clientInitialSavingsOfferingEntity);
        }

        List<CustomerCustomFieldEntity> populatedWithCustomerReference = CustomerCustomFieldEntity
                .fromCustomerCustomFieldEntity(customerCustomFields, client);
        for (CustomerCustomFieldEntity customerCustomFieldEntity : populatedWithCustomerReference) {
            client.addCustomField(customerCustomFieldEntity);
        }

        return client;
    }

    /**
     * default cosntructor for hibernate
     */
    protected ClientBO() {
        super();
        this.nameDetailSet = new HashSet<ClientNameDetailEntity>();
        this.clientAttendances = new HashSet<ClientAttendanceBO>();
        this.clientPerformanceHistory = null;
        this.offeringsAssociatedInCreate = null;
        this.familyDetailSet = null;
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

        this.nameDetailSet = new HashSet<ClientNameDetailEntity>();
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
        setTrainedDate(trainedDate.toDate());

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
    public ClientBO(final UserContext userContext, final String displayName, final CustomerStatus customerStatus,
            final String externalId, final Date mfiJoiningDate, final Address address,
            final List<CustomFieldView> customFields, final List<FeeView> fees,
            final List<SavingsOfferingBO> offeringsSelected, final PersonnelBO formedBy, final OfficeBO office,
            final CustomerBO parentCustomer, final Date dateOfBirth, final String governmentId, final Short trained,
            final Date trainedDate, final Short groupFlag, final ClientNameDetailView clientNameDetailView,
            final ClientNameDetailView spouseNameDetailView, final ClientDetailView clientDetailView,
            final InputStream picture) throws CustomerException {
        this(userContext, displayName, customerStatus, externalId, mfiJoiningDate, address, customFields, fees,
                offeringsSelected, formedBy, office, parentCustomer, null, null, dateOfBirth, governmentId, trained,
                trainedDate, groupFlag, clientNameDetailView, spouseNameDetailView, clientDetailView, picture);
    }

    /**
     * @deprecated - use static factory classes
     */
    public ClientBO(final UserContext userContext, final String displayName, final CustomerStatus customerStatus,
            final String externalId, final Date mfiJoiningDate, final Address address,
            final List<CustomFieldView> customFields, final List<FeeView> fees,
            final List<SavingsOfferingBO> offeringsSelected, final PersonnelBO formedBy, final OfficeBO office,
            final MeetingBO meeting, final PersonnelBO loanOfficer, final Date dateOfBirth, final String governmentId,
            final Short trained, final Date trainedDate, final Short groupFlag,
            final ClientNameDetailView clientNameDetailView, final ClientNameDetailView spouseNameDetailView,
            final ClientDetailView clientDetailView, final InputStream picture) throws CustomerException {
        this(userContext, displayName, customerStatus, externalId, mfiJoiningDate, address, customFields, fees,
                offeringsSelected, formedBy, office, null, meeting, loanOfficer, dateOfBirth, governmentId, trained,
                trainedDate, groupFlag, clientNameDetailView, spouseNameDetailView, clientDetailView, picture);
    }

    /**
     * @deprecated - use static factory classes
     */
    private ClientBO(final UserContext userContext, final String displayName, final CustomerStatus customerStatus,
            final String externalId, final Date mfiJoiningDate, final Address address,
            final List<CustomFieldView> customFields, final List<FeeView> fees,
            final List<SavingsOfferingBO> offeringsSelected, final PersonnelBO formedBy, final OfficeBO office,
            final CustomerBO parentCustomer, final MeetingBO meeting, final PersonnelBO loanOfficer,
            final Date dateOfBirth, final String governmentId, final Short trained, final Date trainedDate,
            final Short groupFlag, final ClientNameDetailView clientNameDetailView,
            final ClientNameDetailView spouseNameDetailView, final ClientDetailView clientDetailView,
            final InputStream picture) throws CustomerException {
        super(userContext, displayName, CustomerLevel.CLIENT, customerStatus, externalId, mfiJoiningDate, address,
                customFields, fees, formedBy, office, parentCustomer, meeting, loanOfficer);
        validateOffice(office);
        validateOfferings(offeringsSelected);
        nameDetailSet = new HashSet<ClientNameDetailEntity>();
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
        this.firstName = clientNameDetailView.getFirstName();
        this.lastName = clientNameDetailView.getLastName();
        this.secondLastName = clientNameDetailView.getSecondLastName();

        this.addNameDetailSet(new ClientNameDetailEntity(this, null, clientNameDetailView));
        if (spouseNameDetailView != null) {
            this.addNameDetailSet(new ClientNameDetailEntity(this, null, spouseNameDetailView));
        }
        this.customerDetail = new ClientDetailEntity(this, clientDetailView);
        createPicture(picture);
        createAssociatedOfferings(offeringsSelected);
//        validateForDuplicateNameOrGovtId(displayName, dateOfBirth, governmentId);

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

    public void setFamilyAndNameDetailSets(final List<ClientNameDetailView> familyNameDetailView,
            final List<ClientFamilyDetailView> familyDetailView) {
        Iterator<ClientFamilyDetailView> iterator2 = familyDetailView.iterator();
        familyDetailSet = new HashSet<ClientFamilyDetailEntity>();
        for (Object element : familyNameDetailView) {
            ClientNameDetailView clientNameDetailView2 = (ClientNameDetailView) element;
            ClientFamilyDetailView clientFamilyDetailView2 = iterator2.next();
            ClientNameDetailEntity nameEntity = new ClientNameDetailEntity(this, null, clientNameDetailView2);
            this.addNameDetailSet(nameEntity);
            this.addFamilyDetailSet(new ClientFamilyDetailEntity(this, nameEntity, clientFamilyDetailView2));
        }

    }

    public Set<ClientNameDetailEntity> getNameDetailSet() {
        return nameDetailSet;
    }

    public Set<ClientFamilyDetailEntity> getFamilyDetailSet() {
        return this.familyDetailSet;
    }

    public CustomerPictureEntity getCustomerPicture() {
        return customerPicture;
    }

    public void setCustomerPicture(final CustomerPictureEntity customerPicture) {
        this.customerPicture = customerPicture;
    }

    public Set<ClientAttendanceBO> getClientAttendances() {
        return clientAttendances;
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    void setDateOfBirth(final Date dateOfBirth) {
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
            this.nameDetailSet.add(customerNameDetail);
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
            throws ServiceException, CustomerException {
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

    public void handleAttendance(final Date meetingDate, final AttendanceType attendance) throws ServiceException,
            CustomerException {
        boolean persist = true;
        handleAttendance(meetingDate, attendance.getValue(), persist);
    }

    @Override
    public void updateMeeting(final MeetingBO meeting) throws CustomerException {
        if (getCustomerMeeting() == null) {
            this.setCustomerMeeting(createCustomerMeeting(meeting));
        } else {
            saveUpdatedMeeting(meeting);
        }
        this.update();
    }

    @Override
    protected void saveUpdatedMeeting(final MeetingBO meeting) throws CustomerException {
        MeetingBO newMeeting = getCustomerMeeting().getUpdatedMeeting();
        super.saveUpdatedMeeting(meeting);
        if (getParentCustomer() == null) {
            deleteMeeting(newMeeting);
        }
    }

    @Override
    public boolean isActiveForFirstTime(final Short oldStatus, final Short newStatusId) {
        return (oldStatus.equals(CustomerStatus.CLIENT_PARTIAL.getValue()) || oldStatus
                .equals(CustomerStatus.CLIENT_PENDING.getValue()))
                && newStatusId.equals(CustomerStatus.CLIENT_ACTIVE.getValue());
    }

    public void updatePersonalInfo(ClientPersonalInfoUpdate personalInfo) throws InvalidDateException {

        this.governmentId = personalInfo.getGovernmentId();
        this.dateOfBirth = DateUtils.getDateAsSentFromBrowser(personalInfo.getDateOfBirth());
        ClientNameDetailView clientName = personalInfo.getClientNameDetails();
        this.getClientName().updateNameDetails(clientName);
        this.firstName = clientName.getFirstName();
        this.lastName = clientName.getLastName();
        this.secondLastName = clientName.getSecondLastName();

        this.updateClientDetails(personalInfo.getClientDetail());
        this.getSpouseName().updateNameDetails(personalInfo.getSpouseFather());

        setDisplayName(personalInfo.getClientDisplayName());
        updateAddress(personalInfo.getAddress());
        setUpdateDetails();
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
                List<ClientNameDetailView> clientNameDetailView = clientFamilyInfoUpdate.getFamilyNames();
                for (ClientNameDetailEntity clientNameDetailEntity : nameDetailSet) {
                    if (clientNameDetailEntity.getCustomerNameId().intValue() == primaryKeys.get(key).intValue()) {
                        clientNameDetailEntity.updateNameDetails(clientNameDetailView.get(key));
                    }
                }

                List<ClientFamilyDetailView> clientFamilyDetailView = clientFamilyInfoUpdate.getFamilyDetails();
                for (ClientFamilyDetailEntity clientFamilyDetailEntity : familyDetailSet) {
                    if (clientFamilyDetailEntity.getClientName().getCustomerNameId().intValue() == primaryKeys.get(key)
                            .intValue()) {
                        clientFamilyDetailEntity.updateClientFamilyDetails(clientFamilyDetailView.get(key));
                    }
                }
            }
        }
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
        for (ClientNameDetailEntity clientNameDetailEntity : nameDetailSet) {
            // Ignoring name entities of client type
            if (!clientNameDetailEntity.getNameType().equals(ClientConstants.CLIENT_NAME_TYPE)) {
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
            nameDetailSet.remove(deleteNameDetailEntity.get(i));
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
                nameDetailSet.add(nameDetail);
                familyDetailSet.add(new ClientFamilyDetailEntity(this, nameDetail, clientFamilyInfoUpdate
                        .getFamilyDetails().get(i)));
            }
        }
    }

    public void updateMfiInfo(final PersonnelBO personnel) throws CustomerException {
        if (isActive() || isOnHold()) {
            validateLO(personnel);
        }
        setPersonnel(personnel);
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
        generateSearchId();
        super.update();
        logger.debug("In ClientBO::transferToBranch(), successfully transfered, customerId :" + getCustomerId());
    }

    public void transferToGroup(final GroupBO newParent) throws CustomerException {
        validateGroupTransfer(newParent);
        logger.debug("In ClientBO::transferToGroup(), transfering customerId: " + getCustomerId() + "to Group Id : "
                + newParent.getCustomerId());

        if (!isSameBranch(newParent.getOffice())) {
            makeCustomerMovementEntries(newParent.getOffice());
        }

        CustomerBO oldParent = getParentCustomer();
        changeParentCustomer(newParent);
        resetPositions(oldParent);

        if (oldParent.getParentCustomer() != null) {
            CustomerBO center = oldParent.getParentCustomer();
            resetPositions(center);
            center.setUserContext(getUserContext());
            center.update();
        }
        update();
        logger.debug("In ClientBO::transferToGroup(), successfully transfered, customerId :" + getCustomerId());
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
        if (getParentCustomer().getParentCustomer() != null) {
            setPersonnel(getParentCustomer().getPersonnel());
            setUpdatedMeeting(getParentCustomer().getParentCustomer().getCustomerMeeting().getMeeting());
        }
    }

    public ClientNameDetailEntity getClientName() {
        for (ClientNameDetailEntity nameDetail : nameDetailSet) {
            if (nameDetail.getNameType().equals(ClientConstants.CLIENT_NAME_TYPE)) {
                return nameDetail;
            }
        }
        return null;
    }

    public ClientNameDetailEntity getSpouseName() {
        for (ClientNameDetailEntity nameDetail : nameDetailSet) {
            if (!nameDetail.getNameType().equals(ClientConstants.CLIENT_NAME_TYPE)) {
                return nameDetail;
            }
        }
        return null;
    }

    /**
     * @deprecated -
     */
    @Deprecated
    public void updateClientDetails(final ClientDetailView clientDetailView) {
        customerDetail.updateClientDetails(clientDetailView);
    }

    @Deprecated
    private void createPicture(final InputStream picture) throws CustomerException {
        try {
            if (picture != null && picture.available() > 0) {
                try {
                    this.customerPicture = new CustomerPictureEntity(this, getClientPersistence().createBlob(picture));
                } catch (PersistenceException e) {
                    throw new CustomerException(e);
                }
            }

        } catch (IOException e) {
            throw new CustomerException(e);
        }
    }

    public void createAssociatedOfferings(final List<SavingsOfferingBO> offeringsSelected) {
        if (offeringsSelected != null) {
            for (SavingsOfferingBO offering : offeringsSelected) {
                this.offeringsAssociatedInCreate.add(new ClientInitialSavingsOfferingEntity(this, offering));
            }
        }
    }

    private void validateForActiveAccounts() throws CustomerException {
        if (isAnyLoanAccountOpen() || isAnySavingsAccountOpen()) {
            throw new CustomerException(ClientConstants.ERRORS_ACTIVE_ACCOUNTS_PRESENT, new Object[] { MessageLookup
                    .getInstance().lookupLabel(ConfigurationConstants.GROUP, userContext) });
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
        return getParentCustomer().getCustomerId().equals(group.getCustomerId());
    }

    private void validateGroupTransfer(final GroupBO toGroup) throws CustomerException {
        if (toGroup == null) {
            throw new CustomerException(CustomerConstants.INVALID_PARENT);
        }

        if (isSameGroup(toGroup)) {
            throw new CustomerException(CustomerConstants.ERRORS_SAME_PARENT_TRANSFER);
        }

        validateForGroupStatus(toGroup.getStatus());
        validateForActiveAccounts();
        if (getCustomerMeeting() != null && toGroup.getCustomerMeeting() != null) {
            validateMeetingRecurrenceForTransfer(getCustomerMeeting().getMeeting(), toGroup.getCustomerMeeting()
                    .getMeeting());
        }
    }

    private void validateForGroupStatus(final CustomerStatus groupStatus) throws CustomerException {
        if (isGroupStatusLower(getStatus(), groupStatus)) {
            MifosConfiguration labelConfig = MifosConfiguration.getInstance();
            throw new CustomerException(ClientConstants.ERRORS_LOWER_GROUP_STATUS, new Object[] {
                    MessageLookup.getInstance().lookupLabel(ConfigurationConstants.GROUP, userContext),
                    MessageLookup.getInstance().lookupLabel(ConfigurationConstants.CLIENT, userContext) });
        }
        if (groupStatus.equals(CustomerStatus.GROUP_CANCELLED) || groupStatus.equals(CustomerStatus.GROUP_CLOSED)) {
            throw new CustomerException(CustomerConstants.ERRORS_INTRANSFER_PARENT_INACTIVE);
        }

    }

    public void generateSearchId() throws CustomerException {
        int count;
        if (getParentCustomer() != null) {
            childAddedForParent(getParentCustomer());
            this.setSearchId(getParentCustomer().getSearchId() + "." + getParentCustomer().getMaxChildCount());
        } else {
            try {
                count = getCustomerPersistence().getCustomerCountForOffice(CustomerLevel.CLIENT,
                        getOffice().getOfficeId());
            } catch (PersistenceException pe) {
                throw new CustomerException(pe);
            }
            String searchId = GroupConstants.PREFIX_SEARCH_STRING + ++count;
            this.setSearchId(searchId);
        }
    }

    public void validateFieldsForActiveClient() throws CustomerException {
        if (isActive()) {
            if (!isClientUnderGroup()) {
                validateLO(this.getPersonnel());
                validateMeeting(this.getCustomerMeetingValue());
            }
        }
    }

    /**
     *
     */
    @Deprecated
    public void validateFieldsForActiveClient(final PersonnelBO loanOfficer, final MeetingBO meeting)
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
                throw new CustomerException(ClientConstants.ERRORS_GROUP_CANCELLED, new Object[] { MessageLookup
                        .getInstance().lookupLabel(ConfigurationConstants.GROUP, this.getUserContext()) });
            }

            if (isGroupStatusLower(clientStatusId, groupStatus)) {

                throw new CustomerException(ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION, new Object[] {
                        MessageLookup.getInstance().lookupLabel(ConfigurationConstants.GROUP, this.getUserContext()),
                        MessageLookup.getInstance().lookupLabel(ConfigurationConstants.CLIENT, this.getUserContext()) });
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
                throw new CustomerException(ClientConstants.ERRORS_GROUP_CANCELLED, new Object[] { MessageLookup
                        .getInstance().lookupLabel(ConfigurationConstants.GROUP, this.getUserContext()) });
            }

            if (isGroupStatusLower(clientStatusId, groupStatus)) {

                throw new CustomerException(ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION, new Object[] {
                        MessageLookup.getInstance().lookupLabel(ConfigurationConstants.GROUP, this.getUserContext()),
                        MessageLookup.getInstance().lookupLabel(ConfigurationConstants.CLIENT, this.getUserContext()) });
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
                    SavingsOfferingBO savingsOffering = getSavingsPrdPersistence().getSavingsProduct(
                            clientOffering.getSavingsOffering().getPrdOfferingId());
                    if (savingsOffering.isActive()) {
                        List<CustomFieldDefinitionEntity> customFieldDefs = getSavingsPersistence()
                                .retrieveCustomFieldsDefinition(EntityType.SAVINGS.getValue());
                        addAccount(new SavingsBO(getUserContext(), savingsOffering, this, AccountState.SAVINGS_ACTIVE,
                                savingsOffering.getRecommendedAmount(),
                                createCustomFieldViewsForClientSavingsAccount(customFieldDefs)));
                    }
                } catch (PersistenceException pe) {
                    throw new CustomerException(pe);
                } catch (AccountException pe) {
                    throw new CustomerException(pe);
                }
            }
        }
    }

    private List<CustomFieldView> createCustomFieldViewsForClientSavingsAccount(
            final List<CustomFieldDefinitionEntity> customFieldDefs) {
        List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();
        for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
            customFields.add(new CustomFieldView(customFieldDef.getFieldId(), customFieldDef.getDefaultValue(),
                    customFieldDef.getFieldType()));
        }
        return customFields;
    }

    public boolean isGroupStatusLower(final Short clientStatusId, final Short parentStatus) {
        return isGroupStatusLower(CustomerStatus.fromInt(clientStatusId), CustomerStatus.fromInt(parentStatus));
    }

    @Deprecated
    private void validateOfferings(final List<SavingsOfferingBO> offeringsSelected) throws CustomerException {
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

    public void updateClientFlag() throws CustomerException, PersistenceException {
        this.groupFlag = YesNoFlag.NO.getValue();
        update();
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

    private boolean isClientCancelledOrClosed() throws CustomerException {
        return getStatus() == CustomerStatus.CLIENT_CLOSED || getStatus() == CustomerStatus.CLIENT_CANCELLED ? true
                : false;
    }

    public void addClientToGroup(final GroupBO newParent) throws CustomerException {
        validateAddClientToGroup(newParent);

        if (!isSameBranch(newParent.getOffice())) {
            makeCustomerMovementEntries(newParent.getOffice());
        }

        setParentCustomer(newParent);
        addCustomerHierarchy(new CustomerHierarchyEntity(this, newParent));
        handleAddClientToGroup();
        childAddedForParent(newParent);
        setSearchId(newParent.getSearchId() + "." + String.valueOf(newParent.getMaxChildCount()));
        newParent.setUserContext(getUserContext());
        newParent.update();

        groupFlag = YesNoFlag.YES.getValue();
        update();
    }

    private void validateAddClientToGroup(final GroupBO toGroup) throws CustomerException {

        if (toGroup == null) {
            throw new CustomerException(CustomerConstants.INVALID_PARENT);
        }
        validateForGroupStatus(toGroup.getStatus());

    }

    public void attachPpiSurveyInstance(final SurveyInstance ppiSurvey) {
        /* TODO not implemented yet */
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
        clientPerfHistory.updateOnRepayment(totalAmount);
    }

    @Override
    public void updatePerformanceHistoryOnLastInstlPayment(final LoanBO loan, final Money totalAmount)
            throws CustomerException {
        updatePerformanceHistoryOnRepayment(loan, totalAmount);
    }

    public void setSavingsPersistence(final SavingsPersistence savingsPersistence) {
        this.savingsPersistence = savingsPersistence;
    }

    public SavingsPersistence getSavingsPersistence() {
        if (null == savingsPersistence) {
            savingsPersistence = new SavingsPersistence();
        }
        return savingsPersistence;
    }

    public void setSavingsPrdPersistence(final SavingsPrdPersistence savingsPrdPersistence) {
        this.savingsPrdPersistence = savingsPrdPersistence;
    }

    public SavingsPrdPersistence getSavingsPrdPersistence() {
        if (null == savingsPrdPersistence) {
            savingsPrdPersistence = new SavingsPrdPersistence();
        }
        return savingsPrdPersistence;
    }

    public ClientPersistence getClientPersistence() {
        if (null == clientPersistence) {
            clientPersistence = new ClientPersistence();
        }
        return clientPersistence;
    }

    public void setClientPersistence(final ClientPersistence clientPersistence) {
        this.clientPersistence = clientPersistence;
    }

    public boolean isClosedOrCancelled() {
        return getCustomerStatus().getId().equals(CustomerStatus.CLIENT_CLOSED.getValue())
                || getCustomerStatus().getId().equals(CustomerStatus.CLIENT_CANCELLED.getValue());
    }

    public boolean isPending() {
        return this.getStatus().isClientPending();
    }

    public final List<ClientNameDetailView> toClientNameDetailViews() {

        List<ClientNameDetailView> clientNameDetailViews = new ArrayList<ClientNameDetailView>();
        for (ClientNameDetailEntity clientNameDetail : this.nameDetailSet) {

            ClientNameDetailView clientNameDetailView = clientNameDetail.toDto();
            clientNameDetailViews.add(clientNameDetailView);
        }
        return clientNameDetailViews;
    }

    public ClientDetailDto toClientDetailDto(boolean isFamilyDetailsRequired) {

        ClientDetailView customerDetailView = this.customerDetail.toDto();

        String dateOfBirthAsString = "";
        if (this.dateOfBirth != null) {
            dateOfBirthAsString = DateUtils.makeDateAsSentFromBrowser(this.dateOfBirth);
        }

        List<ClientNameDetailView> clientNameViews = toClientNameDetailViews();

        ClientNameDetailView clientName = null;
        ClientNameDetailView spouseName = null;
        for (ClientNameDetailView nameView : clientNameViews) {
            if (nameView.getNameType().equals(ClientConstants.CLIENT_NAME_TYPE)) {
                clientName = nameView;
            } else if (!isFamilyDetailsRequired) {
                spouseName = nameView;
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

    public void createOrUpdatePicture(Blob pictureAsBlob) {
        if (this.customerPicture != null) {
            this.customerPicture.setPicture(pictureAsBlob);
        } else {
            this.customerPicture = new CustomerPictureEntity(this, pictureAsBlob);
        }
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
}