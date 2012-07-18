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
package org.mifos.application.importexport.servicefacade;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.importexport.xls.XlsClientsImporter;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.MeetingFactory;
import org.mifos.calendar.CalendarEvent;
import org.mifos.config.ClientRules;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerHierarchyEntity;
import org.mifos.customers.business.service.CustomerAccountFactory;
import org.mifos.customers.business.service.DefaultCustomerAccountFactory;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.ClientDetailEntity;
import org.mifos.customers.client.business.ClientInitialSavingsOfferingEntity;
import org.mifos.customers.client.business.ClientNameDetailEntity;
import org.mifos.customers.client.util.helpers.NewClientDto;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.ClientCreationDetail;
import org.mifos.dto.domain.ImportedClientDetail;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.domain.ParsedClientsDto;
import org.mifos.dto.screen.ClientNameDetailDto;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.util.DateTimeService;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

public class ImportClientsServiceFacadeWebTier implements ImportClientsServiceFacade {

    private XlsClientsImporter xlsClientsImporter;
    private CustomerDao customerDao;
    private PersonnelDao personnelDao;
    private HolidayDao holidayDao;
    private OfficeDao officeDao;
    private HibernateTransactionHelper hibernateTransactionHelper;
    private CustomerAccountFactory customerAccountFactory = DefaultCustomerAccountFactory.createNew();
    private PersonnelServiceFacade personnelServiceFacade;

    @Autowired
    public ImportClientsServiceFacadeWebTier(XlsClientsImporter xlsClientsImporter, CustomerDao customerDao,
            HibernateTransactionHelper hibernateTransactionHelper, PersonnelDao personnelDao, HolidayDao holidayDao,
            OfficeDao officeDao, PersonnelServiceFacade personnelServiceFacade) {

        this.customerDao = customerDao;
        this.personnelDao = personnelDao;
        this.holidayDao = holidayDao;
        this.officeDao = officeDao;

        this.hibernateTransactionHelper = hibernateTransactionHelper;

        this.personnelServiceFacade = personnelServiceFacade;

        this.xlsClientsImporter = xlsClientsImporter;
    }

    @Override
    public ParsedClientsDto parseImportClients(InputStream inputStream) {
        this.xlsClientsImporter.setLocale(this.personnelServiceFacade.getUserPreferredLocale());
        return this.xlsClientsImporter.parse(inputStream);
    }

    @Override
    public ParsedClientsDto createDtoFromSingleError(String error) {
        List<ImportedClientDetail> parsedRows = new ArrayList<ImportedClientDetail>();
        List<String> parseErrors = new ArrayList<String>();
        parseErrors.add(error);

        return new ParsedClientsDto(parseErrors, parsedRows);
    }

    @Override
    public ParsedClientsDto save(ParsedClientsDto parsedClientsDto) {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        OfficeBO userOffice = this.officeDao.findOfficeById(userContext.getBranchId());
        userContext.setBranchGlobalNum(userOffice.getGlobalOfficeNum());

        DateTimeService dateTimeService = new DateTimeService();

        /* Construct ClientBO objects */
        List<NewClientDto> newClients = new ArrayList<NewClientDto>();

        for (ImportedClientDetail importedClient : parsedClientsDto.getSuccessfullyParsedRows()) {
            String secondMiddleName = null;
            ClientCreationDetail clientCreationDetail = importedClient.getClientCreationDetail();

            PersonnelBO formedBy = null;

            /* Client name details */
            ClientNameDetailDto clientNameDetails = clientCreationDetail.getClientNameDetailDto();
            ClientNameDetailEntity clientNameDetailEntity = new ClientNameDetailEntity(null, secondMiddleName,
                    clientNameDetails);

            ClientDetailEntity clientDetailEntity = new ClientDetailEntity();
            clientDetailEntity.updateClientDetails(clientCreationDetail.getClientPersonalDetailDto());

            String clientFirstName = clientNameDetails.getFirstName();
            String clientLastName = clientNameDetails.getLastName();
            String secondLastName = clientNameDetails.getSecondLastName();

            /* Spouse/father name details */
            ClientNameDetailEntity spouseFatherNameDetailEntity = null;
            if (clientCreationDetail.getSpouseFatherName() != null) {
                spouseFatherNameDetailEntity = new ClientNameDetailEntity(null, secondMiddleName,
                        clientCreationDetail.getSpouseFatherName());
            }
            /* Data conversion */
            DateTime dateOfBirth = new DateTime(clientCreationDetail.getDateOfBirth());
            DateTime mfiJoiningDate = new DateTime(clientCreationDetail.getMfiJoiningDate());
            DateTime trainedDateTime = null;
            if (clientCreationDetail.getTrainedDate() != null) {
                trainedDateTime = new DateTime(clientCreationDetail.getTrainedDate());
            }
            /* Status */
            CustomerStatus clientStatus = CustomerStatus.fromInt(clientCreationDetail.getClientStatus());
            CustomerStatus finalStatus = clientStatus;
            if (clientStatus == CustomerStatus.CLIENT_ACTIVE) {
                clientStatus = CustomerStatus.CLIENT_PENDING;
            }
            /* Address */
            Address address = null;
            if (clientCreationDetail.getAddress() != null) {
                AddressDto dto = clientCreationDetail.getAddress();
                address = new Address(dto.getLine1(), dto.getLine2(), dto.getLine3(), dto.getCity(), dto.getState(),
                        dto.getCountry(), dto.getZip(), dto.getPhoneNumber());
            }
            // empty list
            List<ClientInitialSavingsOfferingEntity> associatedOfferings = new ArrayList<ClientInitialSavingsOfferingEntity>();
            // client object
            ClientBO client;
            if (clientCreationDetail.getGroupFlag() == 1) {
                CustomerBO group = customerDao.findCustomerBySystemId(clientCreationDetail.getParentGroupId());

                if (clientCreationDetail.getFormedBy() != null) {
                    formedBy = this.personnelDao.findPersonnelById(clientCreationDetail.getFormedBy());
                } else {
                    formedBy = group.getPersonnel();
                }

                client = ClientBO.createNewInGroupHierarchy(userContext, clientCreationDetail.getClientName(),
                        clientStatus, mfiJoiningDate, group, formedBy, clientNameDetailEntity, dateOfBirth,
                        clientCreationDetail.getGovernmentId(), clientCreationDetail.isTrained(), trainedDateTime,
                        clientCreationDetail.getGroupFlag(), clientFirstName, clientLastName, secondLastName,
                        spouseFatherNameDetailEntity, clientDetailEntity, associatedOfferings,
                        clientCreationDetail.getExternalId(), address, clientCreationDetail.getActivationDate());
            } else {
                Short officeId = clientCreationDetail.getOfficeId();
                Short officerId = clientCreationDetail.getLoanOfficerId();

                PersonnelBO loanOfficer = personnelDao.findPersonnelById(officerId);
                OfficeBO office = this.officeDao.findOfficeById(officeId);

                if (clientCreationDetail.getFormedBy() != null) {
                    formedBy = this.personnelDao.findPersonnelById(clientCreationDetail.getFormedBy());
                } else {
                    formedBy = loanOfficer;
                }

                int lastSearchIdCustomerValue = customerDao
                        .retrieveLastSearchIdValueForNonParentCustomersInOffice(officeId);

                /* meeting */
                final MeetingDto meetingDto = importedClient.getMeeting();
                MeetingBO clientMeeting = null;
                if (meetingDto != null) {
                    clientMeeting = new MeetingFactory().create(meetingDto);
                    clientMeeting.setUserContext(userContext);
                }

                client = ClientBO.createNewOutOfGroupHierarchy(userContext, clientCreationDetail.getClientName(),
                        clientStatus, mfiJoiningDate, office, loanOfficer, clientMeeting, formedBy,
                        clientNameDetailEntity, dateOfBirth, clientCreationDetail.getGovernmentId(),
                        clientCreationDetail.isTrained(), trainedDateTime, clientCreationDetail.getGroupFlag(),
                        clientFirstName, clientLastName, secondLastName, spouseFatherNameDetailEntity,
                        clientDetailEntity, associatedOfferings, clientCreationDetail.getExternalId(), address,
                        lastSearchIdCustomerValue);
            }
            // global id
            if (importedClient.getClientGlobalNum() != null) {
                client.setGlobalCustNum(importedClient.getClientGlobalNum());
            }

            /* Family data */
            if (ClientRules.isFamilyDetailsRequired()) {
                client.setFamilyAndNameDetailSets(clientCreationDetail.getFamilyNames(),
                        clientCreationDetail.getFamilyDetails());
            }

            NewClientDto newClient = new NewClientDto(client, finalStatus);

            newClients.add(newClient);
        }

        /* Validate client data */
        for (NewClientDto newClient : newClients) {
            ClientBO client = newClient.getClientBO();
            try {
                client.validate();
                customerDao.validateClientForDuplicateNameOrGovtId(client.getDisplayName(), client.getDateOfBirth(),
                        client.getGovernmentId());
            } catch (CustomerException ex) {
                throw new MifosRuntimeException(ex);
            }
        }

        /* Save clients */
        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>(); // empty list
        try {
            hibernateTransactionHelper.startTransaction();
            for (NewClientDto newClient : newClients) {

                ClientBO client = newClient.getClientBO();
                CustomerStatus finalStatus = newClient.getCustomerStatus();
                // status to pending approval if active

                MeetingBO meeting = client.getCustomerMeetingValue();

                customerDao.save(client);
                hibernateTransactionHelper.flushSession();

                CalendarEvent applicableCalendarEvents = holidayDao.findCalendarEventsForThisYearAndNext(client
                        .getOfficeId());
                CustomerAccountBO customerAccount = customerAccountFactory.create(client, accountFees, meeting,
                        applicableCalendarEvents);
                client.addAccount(customerAccount);
                customerDao.save(client);
                hibernateTransactionHelper.flushSession();

                if (client.getParentCustomer() != null) {
                    customerDao.save(client.getParentCustomer());
                }

                if (client.getGlobalCustNum() == null) {
                    client.generateGlobalCustomerNumber();
                }
                client.generateSearchId();
                customerDao.save(client);
                hibernateTransactionHelper.flushSession();

                if (client.getParentCustomer() != null) {
                    customerDao.save(client.getParentCustomer());
                }

                /* activate client */
                if (finalStatus == CustomerStatus.CLIENT_ACTIVE) {
                    hibernateTransactionHelper.flushSession();
                    hibernateTransactionHelper.beginAuditLoggingFor(client);
                    client.clearCustomerFlagsIfApplicable(client.getStatus(), finalStatus);

                    client.updateCustomerStatus(finalStatus);
                    // changeStatus(client, oldStatus, newStatus);
                    if (client.getParentCustomer() != null) {
                        CustomerHierarchyEntity hierarchy = new CustomerHierarchyEntity(client,
                                client.getParentCustomer());
                        client.addCustomerHierarchy(hierarchy);
                    }

                    client.setCustomerActivationDate(dateTimeService.getCurrentJavaDateTime());
                    customerAccount.createSchedulesAndFeeSchedulesForFirstTimeActiveCustomer(client, accountFees,
                            meeting, applicableCalendarEvents, new DateTime(client.getCustomerActivationDate()));
                    
                    customerDao.save(client);
                }
            }
            hibernateTransactionHelper.commitTransaction();
        } catch (Exception ex) {
            hibernateTransactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(ex);
        }

        return parsedClientsDto;
    }

    private UserContext toUserContext(MifosUser user) {
        return new UserContextFactory().create(user);
    }

}
