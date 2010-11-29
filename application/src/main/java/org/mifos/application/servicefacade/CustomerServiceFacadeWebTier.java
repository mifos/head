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

package org.mifos.application.servicefacade;

import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.joda.time.DateTime;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.ClientRules;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.center.util.helpers.CenterConstants;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.struts.actionforms.ClientCustActionForm;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.business.service.GroupBusinessService;
import org.mifos.customers.group.struts.action.GroupSearchResultsDto;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.domain.CenterCreation;
import org.mifos.dto.domain.ClientRulesDto;
import org.mifos.dto.domain.CustomerDetailDto;
import org.mifos.dto.domain.OfficeDetailsDto;
import org.mifos.dto.domain.OfficeDto;
import org.mifos.dto.domain.OfficeHierarchyDto;
import org.mifos.dto.domain.PersonnelDto;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.dto.screen.ClientDetailDto;
import org.mifos.dto.screen.ClientDropdownsDto;
import org.mifos.dto.screen.ClientNameDetailDto;
import org.mifos.dto.screen.ClientPersonalDetailDto;
import org.mifos.dto.screen.OnlyBranchOfficeHierarchyDto;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class CustomerServiceFacadeWebTier implements CustomerServiceFacade {

    private final OfficeDao officeDao;
    private final PersonnelDao personnelDao;
    private final CustomerDao customerDao;
    private final CustomerService customerService;

    public CustomerServiceFacadeWebTier(CustomerService customerService, OfficeDao officeDao,
            PersonnelDao personnelDao, CustomerDao customerDao) {
        this.customerService = customerService;
        this.officeDao = officeDao;
        this.personnelDao = personnelDao;
        this.customerDao = customerDao;
    }

    @Override
    public OnlyBranchOfficeHierarchyDto retrieveBranchOnlyOfficeHierarchy() {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        OfficeDto office = officeDao.findOfficeDtoById(userContext.getBranchId());
        List<OfficeBO> branchParents = officeDao.findBranchsOnlyWithParentsMatching(office.getSearchId());
        List<OfficeDetailsDto> levels = officeDao.findActiveOfficeLevels();

        List<OfficeHierarchyDto> branchOnlyOfficeHierarchy = OfficeBO.convertToBranchOnlyHierarchyWithParentsOfficeHierarchy(branchParents);

        return new OnlyBranchOfficeHierarchyDto(userContext.getPreferredLocale(), levels, office.getSearchId(), branchOnlyOfficeHierarchy);
    }

    private UserContext toUserContext(MifosUser user) {
        UserContext userContext = new UserContext();
        userContext.setBranchId(user.getBranchId());
        userContext.setId(Short.valueOf((short) user.getUserId()));
        userContext.setName(user.getUsername());
        userContext.setLevelId(user.getLevelId());
        userContext.setRoles(new HashSet<Short>(user.getRoleIds()));
        return userContext;
    }


    private ClientDropdownsDto retrieveClientDropdownData() {
        List<ValueListElement> salutations = this.customerDao.retrieveSalutations();
        List<ValueListElement> genders = this.customerDao.retrieveGenders();
        List<ValueListElement> maritalStatuses = this.customerDao.retrieveMaritalStatuses();
        List<ValueListElement> citizenship = this.customerDao.retrieveCitizenship();
        List<ValueListElement> ethinicity = this.customerDao.retrieveEthinicity();
        List<ValueListElement> educationLevels = this.customerDao.retrieveEducationLevels();
        List<ValueListElement> businessActivity = this.customerDao.retrieveBusinessActivities();
        List<ValueListElement> poverty = this.customerDao.retrievePoverty();
        List<ValueListElement> handicapped = this.customerDao.retrieveHandicapped();
        List<ValueListElement> livingStatus = this.customerDao.retrieveLivingStatus();

        ClientDropdownsDto clientDropdowns = new ClientDropdownsDto(salutations, genders, maritalStatuses, citizenship,
                ethinicity, educationLevels, businessActivity, poverty, handicapped, livingStatus);
        return clientDropdowns;
    }

    private ClientNameDetailDto spouseFatherName(ClientCustActionForm actionForm) {
        return actionForm.getSpouseName();
    }

    private InputStream picture(ClientCustActionForm actionForm) {
        return actionForm.getCustomerPicture();
    }

    private ClientPersonalDetailDto clientPersonalDetailDto(ClientCustActionForm actionForm) {
        return actionForm.getClientDetailView();
    }

    private ClientNameDetailDto clientNameDetailName(ClientCustActionForm actionForm) {
        return actionForm.getClientName();
    }

    private Short groupFlagValue(ClientCustActionForm actionForm) {
        return actionForm.getGroupFlagValue();
    }

    private Date trainedDate(ClientCustActionForm actionForm) throws InvalidDateException {
        return DateUtils.getDateAsSentFromBrowser(actionForm.getTrainedDate());
    }

    private Short trainedValue(ClientCustActionForm actionForm) {
        return actionForm.getTrainedValue();
    }

    private String governmentId(ClientCustActionForm actionForm) {
        return actionForm.getGovernmentId();
    }

    private Address address(ClientCustActionForm actionForm) {
        return actionForm.getAddress();
    }

    private String externalId(ClientCustActionForm actionForm) {
        return actionForm.getExternalId();
    }

    private String clientName(ClientCustActionForm actionForm) {
        return clientNameDetailName(actionForm).getDisplayName();
    }

    private void checkVersionMismatch(Integer oldVersionNum, Integer newVersionNum) throws ApplicationException {
        if (!oldVersionNum.equals(newVersionNum)) {
            throw new ApplicationException(Constants.ERROR_VERSION_MISMATCH);
        }
    }

    @Override
    public CustomerSearch search(String searchString) throws ApplicationException {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        if (searchString == null) {
            throw new CustomerException(CenterConstants.NO_SEARCH_STRING);
        }

        String normalisedSearchString = org.mifos.framework.util.helpers.SearchUtils
                .normalizeSearchString(searchString);

        if (normalisedSearchString.equals("")) {
            throw new CustomerException(CenterConstants.NO_SEARCH_STRING);
        }

        String officeId = userContext.getBranchId().toString();
        String officeName = this.officeDao.findOfficeDtoById(userContext.getBranchId()).getName();

        PersonnelBO loggedInUser = personnelDao.findPersonnelById(userContext.getId());

        QueryResult searchResult = customerDao.search(normalisedSearchString, loggedInUser);

        return new CustomerSearch(searchResult, searchString, officeId, officeName);
    }

    @Override
    public GroupBO transferGroupToCenter(String groupSystemId, String centerSystemId, Integer previousGroupVersionNo) throws ApplicationException {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        CenterBO transferToCenter = this.customerDao.findCenterBySystemId(centerSystemId);
        transferToCenter.setUserContext(userContext);

        GroupBO group = this.customerDao.findGroupBySystemId(groupSystemId);

        checkVersionMismatch(previousGroupVersionNo, group.getVersionNo());
        group.setUserContext(userContext);

        GroupBO transferedGroup = this.customerService.transferGroupTo(group, transferToCenter);

        return transferedGroup;
    }

    @Override
    public GroupBO transferGroupToBranch(String globalCustNum, Short officeId, Integer previousGroupVersionNo) throws ApplicationException {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        OfficeBO transferToOffice = this.officeDao.findOfficeById(officeId);
        transferToOffice.setUserContext(userContext);

        GroupBO group = this.customerDao.findGroupBySystemId(globalCustNum);

        checkVersionMismatch(previousGroupVersionNo, group.getVersionNo());
        group.setUserContext(userContext);

        GroupBO transferedGroup = this.customerService.transferGroupTo(group, transferToOffice);

        return transferedGroup;
    }

    @Override
    public ClientBO transferClientToGroup(Integer groupId, String clientGlobalCustNum, Integer previousClientVersionNo) throws ApplicationException {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);
        return this.customerService.transferClientTo(userContext, groupId, clientGlobalCustNum, previousClientVersionNo);
    }

    @Override
    public boolean isGroupHierarchyRequired() {
        return ClientRules.getClientCanExistOutsideGroup();
    }

    @Override
    public GroupSearchResultsDto searchGroups(boolean searchForAddingClientsToGroup, String normalizedSearchString,
            Short loggedInUserId) {

        try {
            // FIXME - #000001 - keithw - move search logic off group business service over to customerDAO
            QueryResult searchResults = new GroupBusinessService().search(normalizedSearchString, loggedInUserId);
            QueryResult searchForAddingClientToGroupResults = null;
            if (searchForAddingClientsToGroup) {
                searchForAddingClientToGroupResults = new GroupBusinessService().searchForAddingClientToGroup(
                        normalizedSearchString, loggedInUserId);
            }

            return new GroupSearchResultsDto(searchResults, searchForAddingClientToGroupResults);
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private ClientRulesDto retrieveClientRules() {
        boolean centerHierarchyExists = ClientRules.getCenterHierarchyExists();
        int maxNumberOfFamilyMembers = ClientRules.getMaximumNumberOfFamilyMembers();
        boolean familyDetailsRequired = ClientRules.isFamilyDetailsRequired();

        ClientRulesDto clientRules = new ClientRulesDto(centerHierarchyExists, maxNumberOfFamilyMembers,
                familyDetailsRequired);
        return clientRules;
    }

    @Override
    public ClientMfiInfoDto retrieveMfiInfoForEdit(String clientSystemId) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        ClientBO client = this.customerDao.findClientBySystemId(clientSystemId);

        String groupDisplayName = "";
        String centerDisplayName = "";
        if (client.getParentCustomer() != null) {
            groupDisplayName = client.getParentCustomer().getDisplayName();
            if (client.getParentCustomer().getParentCustomer() != null) {
                centerDisplayName = client.getParentCustomer().getParentCustomer().getDisplayName();
            }
        }

        List<PersonnelDto> loanOfficersList = new ArrayList<PersonnelDto>();
        if (!client.isClientUnderGroup()) {
            CenterCreation centerCreation = new CenterCreation(client.getOffice().getOfficeId(), userContext.getId(),
                    userContext.getLevelId(), userContext.getPreferredLocale());
            loanOfficersList = this.personnelDao.findActiveLoanOfficersForOffice(centerCreation);
        }

        CustomerDetailDto customerDetail = client.toCustomerDetailDto();
        ClientDetailDto clientDetail = client.toClientDetailDto(ClientRules.isFamilyDetailsRequired());
        return new ClientMfiInfoDto(groupDisplayName, centerDisplayName, loanOfficersList, customerDetail, clientDetail);
    }

    @Override
    public void updateClientMfiInfo(Integer clientId, Integer oldVersionNumber, ClientCustActionForm actionForm) throws CustomerException {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        boolean trained = false;
        if (trainedValue(actionForm) != null && trainedValue(actionForm).equals(YesNoFlag.YES.getValue())) {
            trained = true;
        }

        DateTime trainedDate = null;
        try {
            java.sql.Date inputDate = trainedDate(actionForm);
            if (inputDate != null) {
                trainedDate = new DateTime(trainedDate(actionForm));
            }
        } catch (InvalidDateException e) {
            throw new CustomerException(ClientConstants.TRAINED_DATE_MANDATORY);
        }

        Short personnelId = Short.valueOf("-1");
        if (groupFlagValue(actionForm).equals(YesNoFlag.NO.getValue())) {
            if (actionForm.getLoanOfficerIdValue() != null) {
                personnelId = actionForm.getLoanOfficerIdValue();
            }
        } else if (groupFlagValue(actionForm).equals(YesNoFlag.YES.getValue())) {
            // TODO for an urgent fix this reads client to get personnelId.
            // Client is read again in updateClientMfiInfo. Refactor to only read in
            // updateClientMfiInfo.
            ClientBO client = (ClientBO) this.customerDao.findCustomerById(clientId);
            personnelId = client.getPersonnel().getPersonnelId();
        }

        ClientMfiInfoUpdate clientMfiInfoUpdate = new ClientMfiInfoUpdate(clientId, oldVersionNumber, personnelId,
                externalId(actionForm), trained, trainedDate);

        this.customerService.updateClientMfiInfo(userContext, clientMfiInfoUpdate);
    }
}