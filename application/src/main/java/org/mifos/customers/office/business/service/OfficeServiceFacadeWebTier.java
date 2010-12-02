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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.application.admin.servicefacade.OfficeServiceFacade;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.MessageLookup;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.exceptions.OfficeException;
import org.mifos.customers.office.exceptions.OfficeValidationException;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.office.struts.OfficeUpdateRequest;
import org.mifos.customers.office.util.helpers.OfficeConstants;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.office.util.helpers.OfficeStatus;
import org.mifos.customers.office.util.helpers.OperationMode;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.OfficeDetailsDto;
import org.mifos.dto.domain.OfficeDto;
import org.mifos.dto.domain.OfficeHierarchyDto;
import org.mifos.dto.screen.ListElement;
import org.mifos.dto.screen.OfficeFormDto;
import org.mifos.dto.screen.OfficeHierarchyByLevelDto;
import org.mifos.dto.screen.OnlyBranchOfficeHierarchyDto;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.springframework.security.core.context.SecurityContextHolder;

public class OfficeServiceFacadeWebTier implements LegacyOfficeServiceFacade, OfficeServiceFacade {

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
    public String topLevelOfficeNames(String ids) {
        String[] idArray = ids.split(",");
        List<Short> idList = new LinkedList<Short>();
        for (String id : idArray) {
            idList.add(new Short(id));
        }
        List<String> topLevelOffices = officeDao.topLevelOfficeNames(idList);
        StringBuffer stringBuffer = new StringBuffer();
        for (Iterator<String> iterator = topLevelOffices.iterator(); iterator.hasNext();) {
            stringBuffer.append(iterator.next());
            if (iterator.hasNext()) {
                stringBuffer.append(", ");
            }
        }
        return stringBuffer.toString();
    }

    @Override
    public boolean updateOffice(UserContext userContext, Short officeId, Integer versionNum, OfficeUpdateRequest officeUpdateRequest) throws ApplicationException {

        boolean isParentOfficeChanged = false;

        OfficeBO office = officeDao.findOfficeById(officeId);
        office.validateVersion(versionNum);

        OfficeBO parentOffice = null;
        if (officeUpdateRequest.getParentOfficeId() != null) {

            parentOffice = officeDao.findOfficeById(officeUpdateRequest.getParentOfficeId());

            if (office.isDifferentParentOffice(parentOffice)) {
                holidayDao.validateNoExtraFutureHolidaysApplicableOnParentOffice(office.getParentOffice().getOfficeId(), officeUpdateRequest.getParentOfficeId());
            }
        }

        if (office.isNameDifferent(officeUpdateRequest.getOfficeName())) {
            officeDao.validateOfficeNameIsNotTaken(officeUpdateRequest.getOfficeName());
        }

        if (office.isShortNameDifferent(officeUpdateRequest.getShortName())) {
            officeDao.validateOfficeShortNameIsNotTaken(officeUpdateRequest.getShortName());
        }

        if (!office.isStatusDifferent(officeUpdateRequest.getNewStatus())) {

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

        try {
            StaticHibernateUtil.startTransaction();
            office.update(userContext, officeUpdateRequest, parentOffice);
            StaticHibernateUtil.commitTransaction();
            return isParentOfficeChanged;
        } catch (ApplicationException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new ApplicationException(e.getKey(), e);
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

        List<CustomFieldDefinitionEntity> customFieldDefinitions = this.officeDao.retrieveCustomFieldsForOffice();
        List<CustomFieldDto> customFields = CustomFieldDefinitionEntity.toDto(customFieldDefinitions, Locale.getDefault());

        OfficeLevel officeLevel = OfficeLevel.HEADOFFICE;
        if (officeLevelId != null) {
            officeLevel = OfficeLevel.getOfficeLevel(officeLevelId);
        }

        List<OfficeDto> parents = this.officeDao.findActiveParents(officeLevel);

        for (OfficeDto office : parents) {
            String levelName = MessageLookup.getInstance().lookup(office.getLookupNameKey());
            office.setLevelName(levelName);
        }

        return new OfficeFormDto(customFields, parents);
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

        OfficeDto officeDto = new OfficeDto(officeBO.getOfficeId(), officeBO.getOfficeName(), officeBO.getSearchId(), officeBO.getShortName(),
                officeBO.getGlobalOfficeNum(), parentOfficeId, officeBO.getStatus().getId(), officeBO.getLevel().getId(),
                parentOffineName, officeBO.getVersionNo(), officeBO.getStatus().getName(), officeBO.getLevel().getName(),
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
    public ListElement createOffice(Short userId, Locale preferredLocale, Short operationMode, OfficeDto officeDto) {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);
        userContext.setPreferredLocale(preferredLocale);

        OfficeLevel level = OfficeLevel.getOfficeLevel(officeDto.getLevelId());
        OfficeBO parentOffice = officeDao.findOfficeById(officeDto.getParentId());
        AddressDto addressDto = officeDto.getAddress();
        Address address = new Address(addressDto.getLine1(), addressDto.getLine2(), addressDto.getLine3(), addressDto.getCity(), addressDto.getState(),
                addressDto.getCountry(), addressDto.getZip(), addressDto.getPhoneNumber());

        try {
            OfficeBO officeBO = new OfficeBO(userContext, level, parentOffice, officeDto.getCustomFields(),
                    officeDto.getName(), officeDto.getOfficeShortName(), address, OperationMode.fromInt(operationMode.intValue()));
            //not sure why it is needed - copied from OffAction
            StaticHibernateUtil.flushAndCloseSession();
            officeBO.save();
            //Shahid - this is hackish solution to return officeId and globalOfficeNum via ListElement, it should be fixed, at least
            //a proper data storage class can be created
            ListElement element = new ListElement(new Integer(officeBO.getOfficeId()), officeBO.getGlobalOfficeNum());
            StaticHibernateUtil.commitTransaction();
            return element;
        } catch (OfficeValidationException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new BusinessRuleException(e.getMessage());
        } catch (PersistenceException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } catch (OfficeException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
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
}