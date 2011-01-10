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
package org.mifos.customers.office.business.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.application.admin.servicefacade.OfficeServiceFacade;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.exceptions.OfficeException;
import org.mifos.customers.office.exceptions.OfficeValidationException;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.office.util.helpers.OfficeConstants;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.office.util.helpers.OfficeStatus;
import org.mifos.customers.office.util.helpers.OperationMode;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.OfficeDetailsDto;
import org.mifos.dto.domain.OfficeDto;
import org.mifos.dto.domain.OfficeHierarchyDto;
import org.mifos.dto.domain.OfficeUpdateRequest;
import org.mifos.dto.screen.ListElement;
import org.mifos.dto.screen.OfficeDetailsForEdit;
import org.mifos.dto.screen.OfficeFormDto;
import org.mifos.dto.screen.OfficeHierarchyByLevelDto;
import org.mifos.dto.screen.OnlyBranchOfficeHierarchyDto;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.security.MifosUser;
import org.mifos.security.authorization.HierarchyManager;
import org.mifos.security.util.EventManger;
import org.mifos.security.util.OfficeSearch;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.springframework.security.core.context.SecurityContextHolder;

public class OfficeServiceFacadeWebTier implements OfficeServiceFacade {

    private final OfficeDao officeDao;
    private final HolidayDao holidayDao;

    public OfficeServiceFacadeWebTier(OfficeDao officeDao, HolidayDao holidayDao) {
        this.officeDao = officeDao;
        this.holidayDao = holidayDao;
    }

    @Override
    public OfficeHierarchyDto headOfficeHierarchy() {
        return officeDao.headOfficeHierarchy();
    }

    @Override
    public boolean updateOffice(Short officeId, Integer versionNum, OfficeUpdateRequest officeUpdateRequest) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);

        try {
            boolean isParentOfficeChanged = false;

            OfficeBO office = officeDao.findOfficeById(officeId);
            office.validateVersion(versionNum);

            OfficeBO parentOffice = null;
            if (officeUpdateRequest.getParentOfficeId() != null) {

                parentOffice = officeDao.findOfficeById(officeUpdateRequest.getParentOfficeId());

                if (office.isDifferentParentOffice(parentOffice)) {
                    holidayDao.validateNoExtraFutureHolidaysApplicableOnParentOffice(office.getParentOffice()
                            .getOfficeId(), officeUpdateRequest.getParentOfficeId());
                }
            }

            if (office.isNameDifferent(officeUpdateRequest.getOfficeName())) {
                officeDao.validateOfficeNameIsNotTaken(officeUpdateRequest.getOfficeName());
            }

            if (office.isShortNameDifferent(officeUpdateRequest.getShortName())) {
                officeDao.validateOfficeShortNameIsNotTaken(officeUpdateRequest.getShortName());
            }

            OfficeStatus newStatus = OfficeStatus.getOfficeStatus(officeUpdateRequest.getNewStatus());
            if (!office.isStatusDifferent(newStatus)) {

                if (OfficeStatus.INACTIVE.equals(officeUpdateRequest.getNewStatus())) {
                    officeDao.validateNoActiveChildrenExist(office.getOfficeId());
                    officeDao.validateNoActivePeronnelExist(office.getOfficeId());
                }

                if (parentOffice != null) {
                    if (parentOffice.isInActive()) {
                        throw new OfficeException(OfficeConstants.KEYPARENTNOTACTIVE);
                    }
                }
            }

            StaticHibernateUtil.startTransaction();
            office.update(userContext, officeUpdateRequest, parentOffice);
            StaticHibernateUtil.commitTransaction();
            return isParentOfficeChanged;
        } catch (OfficeException e1) {
            throw new BusinessRuleException(e1.getKey(), e1);
        } catch (ApplicationException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e.getMessage(), e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    @Override
    public OfficeHierarchyByLevelDto retrieveAllOffices() {
        List<OfficeDto> allOffices = this.officeDao.findAllOffices();
        return new OfficeHierarchyByLevelDto(headOfficeSpecification(allOffices), regionalOfficeSpecification(allOffices), divisionalOffices(allOffices), areaOffices(allOffices), branchOffices(allOffices));
    }

    private List<OfficeDto> branchOffices(List<OfficeDto> allOffices) {
        List<OfficeDto> branchOffices = new ArrayList<OfficeDto>();

        for (OfficeDto officeDto : allOffices) {
            if (OfficeLevel.BRANCHOFFICE.getValue().equals(officeDto.getLevelId())) {
                branchOffices.add(officeDto);
            }
        }

        return branchOffices;
    }

    private List<OfficeDto> areaOffices(List<OfficeDto> allOffices) {
        List<OfficeDto> areaOffices = new ArrayList<OfficeDto>();

        for (OfficeDto officeDto : allOffices) {
            if (OfficeLevel.AREAOFFICE.getValue().equals(officeDto.getLevelId())) {
                areaOffices.add(officeDto);
            }
        }

        return areaOffices;
    }

    private List<OfficeDto> divisionalOffices(List<OfficeDto> allOffices) {
        List<OfficeDto> divisionalOffices = new ArrayList<OfficeDto>();

        for (OfficeDto officeDto : allOffices) {
            if (OfficeLevel.SUBREGIONALOFFICE.getValue().equals(officeDto.getLevelId())) {
                divisionalOffices.add(officeDto);
            }
        }

        return divisionalOffices;
    }

    private List<OfficeDto> regionalOfficeSpecification(List<OfficeDto> allOffices) {
        List<OfficeDto> regionalOffices = new ArrayList<OfficeDto>();

        for (OfficeDto officeDto : allOffices) {
            if (OfficeLevel.REGIONALOFFICE.getValue().equals(officeDto.getLevelId())) {
                regionalOffices.add(officeDto);
            }
        }

        return regionalOffices;
    }

    private List<OfficeDto> headOfficeSpecification(List<OfficeDto> allOffices) {
        List<OfficeDto> headOffices = new ArrayList<OfficeDto>();

        for (OfficeDto officeDto : allOffices) {
            if (OfficeLevel.HEADOFFICE.getValue().equals(officeDto.getLevelId())) {
                headOffices.add(officeDto);
            }
        }

        return headOffices;
    }

    @Override
    public OfficeFormDto retrieveOfficeFormInformation(Short officeLevelId) {

        try {
            List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();

            OfficeLevel officeLevel = OfficeLevel.HEADOFFICE;
            if (officeLevelId != null) {
                officeLevel = OfficeLevel.getOfficeLevel(officeLevelId);
            }

            List<OfficeDto> parents = this.officeDao.findActiveParents(officeLevel);

            for (OfficeDto office : parents) {
                String levelName = MessageLookup.getInstance().lookup(office.getLookupNameKey());
                office.setLevelName(levelName);
            }

            List<OfficeDetailsDto> officeLevels = new OfficePersistence().getActiveLevels();
            for (OfficeDetailsDto officeDetailsDto : officeLevels) {
                String levelName = MessageLookup.getInstance().lookup(officeDetailsDto.getLevelNameKey());
                officeDetailsDto.setLevelName(levelName);
            }

            return new OfficeFormDto(customFields, parents, officeLevels);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public OfficeDto retrieveOfficeById(Short id) {
        OfficeBO officeBO = officeDao.findOfficeById(id);
        List<CustomFieldDto> customFields = retrieveCustomFieldsForOffice();
        Short parentOfficeId = null;
        String parentOffineName = null;
        if(officeBO.getParentOffice() != null) {
            parentOfficeId = officeBO.getParentOffice().getOfficeId();
            parentOffineName = officeBO.getParentOffice().getOfficeName();
        }
        Address address = officeBO.getAddress() != null ? officeBO.getAddress().getAddress(): null;
        AddressDto addressDto = address != null ? Address.toDto(officeBO.getAddress().getAddress()): null;

        String officeLevelName = MessageLookup.getInstance().lookup(officeBO.getLevel().getLookUpValue());
        String officeStatusName = MessageLookup.getInstance().lookup(officeBO.getStatus().getLookUpValue());
        OfficeDto officeDto = new OfficeDto(officeBO.getOfficeId(), officeBO.getOfficeName(), officeBO.getSearchId(), officeBO.getShortName(),
                officeBO.getGlobalOfficeNum(), parentOfficeId, officeBO.getStatus().getId(), officeBO.getLevel().getId(),
                parentOffineName, officeBO.getVersionNo(), officeStatusName, officeLevelName,
                addressDto, customFields);

        return officeDto;
    }

    @Override
    public List<CustomFieldDto> retrieveCustomFieldsForOffice() {
        if (officeDao.retrieveCustomFieldsForOffice() != null) {
            return CustomFieldDefinitionEntity.toDto(officeDao.retrieveCustomFieldsForOffice(), Locale.getDefault());
        }
        return null;
    }

    @Override
    public ListElement createOffice(Short operationMode, OfficeDto officeDto) {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);

        OfficeLevel level = OfficeLevel.getOfficeLevel(officeDto.getLevelId());
        OfficeBO parentOffice = officeDao.findOfficeById(officeDto.getParentId());
        AddressDto addressDto = officeDto.getAddress();
        Address address = new Address(addressDto.getLine1(), addressDto.getLine2(), addressDto.getLine3(), addressDto.getCity(), addressDto.getState(),
                addressDto.getCountry(), addressDto.getZip(), addressDto.getPhoneNumber());

        try {
            OfficeBO officeBO = new OfficeBO(userContext, level, parentOffice, officeDto.getCustomFields(),
                    officeDto.getName(), officeDto.getOfficeShortName(), address, OperationMode.fromInt(operationMode.intValue()));

            OfficePersistence officePersistence = new OfficePersistence();
            if (officePersistence.isOfficeNameExist(officeDto.getName())) {
                throw new OfficeValidationException(OfficeConstants.OFFICENAMEEXIST);
            }

            if (officePersistence.isOfficeShortNameExist(officeDto.getOfficeShortName())) {
                throw new OfficeValidationException(OfficeConstants.OFFICESHORTNAMEEXIST);
            }

            String searchId = generateSearchId(parentOffice);
            officeBO.setSearchId(searchId);

            String globalOfficeNum = generateOfficeGlobalNo();
            officeBO.setGlobalOfficeNum(globalOfficeNum);

            StaticHibernateUtil.startTransaction();
            this.officeDao.save(officeBO);
            StaticHibernateUtil.commitTransaction();

            //Shahid - this is hackish solution to return officeId and globalOfficeNum via ListElement, it should be fixed, at least
            //a proper data storage class can be created
            ListElement element = new ListElement(new Integer(officeBO.getOfficeId()), officeBO.getGlobalOfficeNum());

            // if we are here it means office created sucessfully
            // we need to update hierarchy manager cache
            OfficeSearch os = new OfficeSearch(officeBO.getOfficeId(), officeBO.getSearchId(), officeBO.getParentOffice().getOfficeId());
            List<OfficeSearch> osList = new ArrayList<OfficeSearch>();
            osList.add(os);
            EventManger.postEvent(Constants.CREATE, osList, SecurityConstants.OFFICECHANGEEVENT);

            return element;
        } catch (OfficeValidationException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new BusinessRuleException(e.getMessage());
        } catch (PersistenceException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } catch (OfficeException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    private String generateSearchId(OfficeBO parentOffice) throws OfficeException {
        Integer noOfChildern;
        try {
            noOfChildern = new OfficePersistence().getChildCount(parentOffice.getOfficeId());
        } catch (PersistenceException e) {
            throw new OfficeException(e);
        }
        String parentSearchId = HierarchyManager.getInstance().getSearchId(parentOffice.getOfficeId());
        parentSearchId += ++noOfChildern;
        parentSearchId += ".";
        return parentSearchId;
    }

    private String generateOfficeGlobalNo() throws OfficeException {

        try {
            /*
             * TODO: Why not auto-increment? Fetching the max and adding one would seem to have a race condition.
             */
            String officeGlobelNo = String.valueOf(new OfficePersistence().getMaxOfficeId().intValue() + 1);
            if (officeGlobelNo.length() > 4) {
                throw new OfficeException(OfficeConstants.MAXOFFICELIMITREACHED);
            }
            StringBuilder temp = new StringBuilder("");
            for (int i = officeGlobelNo.length(); i < 4; i++) {
                temp.append("0");
            }

            return officeGlobelNo = temp.append(officeGlobelNo).toString();
        } catch (PersistenceException e) {
            throw new OfficeException(e);
        }
    }

    @Override
    public OnlyBranchOfficeHierarchyDto retrieveBranchOnlyOfficeHierarchy() {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        OfficeDto office = officeDao.findOfficeDtoById(user.getBranchId());
        List<OfficeBO> branchParents = officeDao.findBranchsOnlyWithParentsMatching(office.getSearchId());
        List<OfficeDetailsDto> levels = officeDao.findActiveOfficeLevels();

        List<OfficeHierarchyDto> branchOnlyOfficeHierarchy = OfficeBO
                .convertToBranchOnlyHierarchyWithParentsOfficeHierarchy(branchParents);

        return new OnlyBranchOfficeHierarchyDto(Locale.getDefault(), levels, office.getSearchId(), branchOnlyOfficeHierarchy);
    }

    @Override
    public List<OfficeDto> retrieveAllNonBranchOfficesApplicableToLoggedInUser() {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        OfficeDto office = officeDao.findOfficeDtoById(user.getBranchId());
        return officeDao.findNonBranchesOnlyWithParentsMatching(office.getSearchId());
    }

    @Override
    public List<OfficeDetailsDto> retrieveActiveParentOffices(Short officeLevelId) {
        OfficeLevel Level = OfficeLevel.getOfficeLevel(officeLevelId);
        try {
            List<OfficeDetailsDto> officeParents = new OfficePersistence().getActiveParents(Level);
            for (OfficeDetailsDto officeDetailsDto : officeParents) {
                String levelName = MessageLookup.getInstance().lookup(officeDetailsDto.getLevelNameKey());
                officeDetailsDto.setLevelName(levelName);
            }
            return officeParents;
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public OfficeDetailsForEdit retrieveOfficeDetailsForEdit(String officeLevel) {

        List<OfficeDetailsDto> parents = new ArrayList<OfficeDetailsDto>();
        if (StringUtils.isNotBlank(officeLevel)) {
            parents = retrieveActiveParentOffices(Short.valueOf(officeLevel));
        }

        try {
            List<OfficeDetailsDto> configuredOfficeLevels = new OfficePersistence().getActiveLevels();
            for (OfficeDetailsDto officeDetailsDto : configuredOfficeLevels) {
                String levelName = MessageLookup.getInstance().lookup(officeDetailsDto.getLevelNameKey());
                officeDetailsDto.setLevelName(levelName);
            }

            List<OfficeDetailsDto> statusList = new OfficePersistence().getStatusList();
            for (OfficeDetailsDto officeDetailsDto : statusList) {
                officeDetailsDto.setLevelName(MessageLookup.getInstance().lookup(officeDetailsDto.getLevelNameKey()));
            }

            return new OfficeDetailsForEdit(parents, statusList, configuredOfficeLevels);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }
}