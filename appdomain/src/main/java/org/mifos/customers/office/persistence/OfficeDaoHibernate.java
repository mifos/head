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

package org.mifos.customers.office.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficeLevelEntity;
import org.mifos.customers.office.exceptions.OfficeException;
import org.mifos.customers.office.util.helpers.OfficeConstants;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.office.util.helpers.OfficeStatus;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.dto.domain.OfficeDetailsDto;
import org.mifos.dto.domain.OfficeDto;
import org.mifos.dto.domain.OfficeHierarchyDto;
import org.mifos.dto.domain.OfficeLevelDto;
import org.mifos.security.authorization.HierarchyManager;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;

import edu.emory.mathcs.backport.java.util.Collections;

public class OfficeDaoHibernate implements OfficeDao {

    private final GenericDao genericDao;

    public OfficeDaoHibernate(GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    @Override
    public void save(OfficeLevelEntity entity) {
        this.genericDao.createOrUpdate(entity);
    }

    @Override
    public void save(OfficeBO office) {
        this.genericDao.createOrUpdate(office);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<OfficeBO> findBranchsOnlyWithParentsMatching(String searchId) {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("LEVEL_ID", OfficeLevel.BRANCHOFFICE.getValue());
        queryParameters.put("SEARCH_ID", searchId + "%");
        queryParameters.put("STATUS_ID", OfficeStatus.ACTIVE.getValue());
        List<OfficeBO> queryResult = (List<OfficeBO>) genericDao.executeNamedQuery(
                "office.getBranchParents", queryParameters);

        if (queryResult == null) {
            queryResult = new ArrayList<OfficeBO>();
        }

        return queryResult;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<OfficeDto> findNonBranchesOnlyWithParentsMatching(String searchId) {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("LEVEL_ID", OfficeLevel.BRANCHOFFICE.getValue());
        queryParameters.put("SEARCH_ID", searchId + "%");
        queryParameters.put("STATUS_ID", OfficeStatus.ACTIVE.getValue());
        List<OfficeDto> queryResult = (List<OfficeDto>) genericDao.executeNamedQuery("office.retrieveAllActiveOfficesNotAtAGivenLevelMatchingSearchIdPattern", queryParameters);

        if (queryResult == null) {
            queryResult = new ArrayList<OfficeDto>();
        }

        return queryResult;
    }

    @Override
    public OfficeBO findOfficeById(Short officeId) {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("OFFICE_ID", officeId);

        return (OfficeBO) genericDao.executeUniqueResultNamedQuery("findOfficeById", queryParameters);
    }

    @Override
    public OfficeDto findOfficeDtoById(Short officeId) {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("OFFICE_ID", officeId);

        return (OfficeDto) genericDao.executeUniqueResultNamedQuery("findOfficeDtoById", queryParameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<OfficeDto> findAllOffices() {

        List<OfficeDto> headOffices = (List<OfficeDto>) genericDao.executeNamedQuery("findAllHeadOffices", null);

        List<OfficeDto> allNonHeadOffices = (List<OfficeDto>) genericDao.executeNamedQuery("findAllNonHeadOfficesApplicableToOfficeHierarchy", null);

        List<OfficeDto> allOffices = new ArrayList<OfficeDto>(allNonHeadOffices);
        allOffices.addAll(headOffices);

        return allOffices;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<OfficeDetailsDto> findActiveOfficeLevels() {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        List<OfficeDetailsDto> queryResult = (List<OfficeDetailsDto>) genericDao.executeNamedQuery(
                "masterdata.activeLevels", queryParameters);
        if (queryResult == null) {
            queryResult = new ArrayList<OfficeDetailsDto>();
        }

        return queryResult;
    }

    @SuppressWarnings("unchecked")
    @Override
    public OfficeLevelDto findOfficeLevelsWithConfiguration() {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        List<OfficeLevelEntity> officeLevelEntities = (List<OfficeLevelEntity>) genericDao.executeNamedQuery("officeLevel.getOfficeLevelsWithConfiguration", queryParameters);
        if (officeLevelEntities == null) {
            officeLevelEntities = new ArrayList<OfficeLevelEntity>();
        }

        OfficeLevelDto officeLevels = new OfficeLevelDto();
        for (OfficeLevelEntity officeLevelEntity : officeLevelEntities) {

            LookUpValueEntity lookupValue = officeLevelEntity.getLookUpValue();
            String messageText = lookupValue.getMessageText();
            if (StringUtils.isBlank(messageText)) {
                messageText = MessageLookup.getInstance().lookup(lookupValue.getPropertiesKey());
            }

            OfficeLevel level = OfficeLevel.getOfficeLevel(officeLevelEntity.getId());
            switch (level) {
            case HEADOFFICE:
                officeLevels.setHeadOfficeEnabled(officeLevelEntity.isConfigured());
                officeLevels.setHeadOfficeNameKey(messageText);
                break;
            case REGIONALOFFICE:
                officeLevels.setRegionalOfficeEnabled(officeLevelEntity.isConfigured());
                officeLevels.setRegionalOfficeNameKey(messageText);
                break;
            case SUBREGIONALOFFICE:
                officeLevels.setSubRegionalOfficeEnabled(officeLevelEntity.isConfigured());
                officeLevels.setSubRegionalOfficeNameKey(messageText);
                break;
            case AREAOFFICE:
                officeLevels.setAreaOfficeEnabled(officeLevelEntity.isConfigured());
                officeLevels.setAreaOfficeNameKey(messageText);
                break;
            case BRANCHOFFICE:
                officeLevels.setBranchOfficeEnabled(officeLevelEntity.isConfigured());
                officeLevels.setBranchOfficeNameKey(messageText);
                break;
            default:
                break;
            }
        }

        return officeLevels;
    }

    @Override
    public OfficeHierarchyDto headOfficeHierarchy() {
        OfficeBO headOffice = getHeadOffice();
        return officeHierarchy(headOffice);
    }

    @SuppressWarnings("unchecked")
    private OfficeBO getHeadOffice() {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("LEVEL_ID", OfficeConstants.HEADOFFICE);

        List<OfficeBO> queryResult = (List<OfficeBO>) this.genericDao.executeNamedQuery("office.getHeadOffice", queryParameters);

        if (queryResult != null && queryResult.size() != 0) {
            return queryResult.get(0);
        }
        throw new MifosRuntimeException("No head office found: ");
    }

    private OfficeHierarchyDto officeHierarchy(OfficeBO office) {
        List<OfficeHierarchyDto> childOfficeList = new LinkedList<OfficeHierarchyDto>();
        Set<OfficeBO> children = office.getChildren();
        for (OfficeBO child : children) {
            childOfficeList.add(officeHierarchy(child));
        }
        Collections.sort(childOfficeList);
        OfficeHierarchyDto hierarchy = new OfficeHierarchyDto(office.getOfficeId(), office.getOfficeName().trim(), office.getSearchId(), office.isActive(), childOfficeList);
        return hierarchy;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> topLevelOfficeNames(Collection<Short> officeIds) {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("OFFICE_IDS", officeIds);

        return (List<String>) this.genericDao.executeNamedQuery("holiday.topLevelOfficeNames", queryParameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void validateNoActiveChildrenExist(Short officeId) throws OfficeException {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("OFFICE_ID", officeId);
        List queryResult = this.genericDao.executeNamedQuery("getCountOfActiveChildren", queryParameters);
        int activeChildren = ((Number) queryResult.get(0)).intValue();

        if (activeChildren > 0) {
            throw new OfficeException(OfficeConstants.KEYHASACTIVECHILDREN);
        }
    }

    @Override
    public void validateBranchIsActiveWithNoActivePersonnel(Short officeId, UserContext userContext) throws CustomerException {

        OfficeBO office = findOfficeById(officeId);

        if (!office.isActive()) {
            throw new CustomerException(GroupConstants.BRANCH_INACTIVE, new Object[] { MessageLookup.getInstance()
                    .lookupLabel(ConfigurationConstants.GROUP, userContext) });
        }

        if (hasActivePeronnel(office.getOfficeId())) {
            throw new CustomerException(GroupConstants.LOANOFFICER_INACTIVE, new Object[] { MessageLookup.getInstance()
                    .lookup(ConfigurationConstants.BRANCHOFFICE, userContext) });
        }
    }

    @SuppressWarnings("unchecked")
    private boolean hasActivePeronnel(Short officeId) {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("OFFICE_ID", officeId);
        queryParameters.put("STATUS_ID", PersonnelConstants.ACTIVE);
        List queryResult = this.genericDao.executeNamedQuery("getCountActivePersonnel", queryParameters);

        return ((Number) queryResult.get(0)).longValue() > 0;
    }

    @Override
    public void validateNoActivePeronnelExist(Short officeId) throws OfficeException {

        if (hasActivePeronnel(officeId)) {
            throw new OfficeException(OfficeConstants.KEYHASACTIVEPERSONNEL);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void validateOfficeNameIsNotTaken(String officeName) throws OfficeException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("OFFICE_NAME", officeName);
        List queryResult = this.genericDao.executeNamedQuery("office.getOfficeWithName", queryParameters);

        int officeCount = ((Number) queryResult.get(0)).intValue();

        if (officeCount > 0) {
            throw new OfficeException(OfficeConstants.OFFICENAMEEXIST);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void validateOfficeShortNameIsNotTaken(String shortName) throws OfficeException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SHORT_NAME", shortName);
        List queryResult = this.genericDao.executeNamedQuery("office.getOfficeWithShortName", queryParameters);
        int officeCount = ((Number) queryResult.get(0)).intValue();

        if (officeCount > 0) {
            throw new OfficeException(OfficeConstants.OFFICESHORTNAMEEXIST);
        }
    }

    @Override
    public List<CustomFieldDefinitionEntity> retrieveCustomFieldsForOffice() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(MasterConstants.ENTITY_TYPE, EntityType.OFFICE.getValue());

        return retrieveCustomFieldDefinitions(queryParameters);
    }

    @SuppressWarnings("unchecked")
    private List<CustomFieldDefinitionEntity> retrieveCustomFieldDefinitions(Map<String, Object> queryParameters) {
        List<CustomFieldDefinitionEntity> customFieldsForCenter = (List<CustomFieldDefinitionEntity>) genericDao.executeNamedQuery("retrieveCustomFields", queryParameters);
        return customFieldsForCenter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final List<CustomFieldDefinitionEntity> retrieveCustomFieldEntitiesForOffice() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(MasterConstants.ENTITY_TYPE, EntityType.OFFICE.getValue());
        return (List<CustomFieldDefinitionEntity>) genericDao.executeNamedQuery(NamedQueryConstants.RETRIEVE_CUSTOM_FIELDS, queryParameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object[]> getCustomFieldResponses(List<Short> customFieldIds) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CUSTOM_FIELD_ID", customFieldIds);
        return (List<Object[]>) genericDao.executeNamedQuery("OfficeCustomFieldEntity.getResponses", queryParameters);
    }



    @SuppressWarnings("unchecked")
    @Override
    public List<OfficeDto> findActiveParents(OfficeLevel officeLevel) {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("LEVEL_ID", officeLevel.getValue());
        queryParameters.put("STATUS_ID", OfficeStatus.ACTIVE.getValue());

        return (List<OfficeDto>) this.genericDao.executeNamedQuery("office.findActiveParents", queryParameters);
    }

    @Override
    public OfficeLevelEntity retrieveOfficeLevel(OfficeLevel officeLevel) {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("OFFICE_LEVEL_ID", officeLevel.getValue());

        return (OfficeLevelEntity) this.genericDao.executeUniqueResultNamedQuery("officeLevel.findById", queryParameters);
    }

    @Override
    public void validateNoOfficesExistGivenOfficeLevel(OfficeLevel officeLevel) {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("LEVEL_ID", officeLevel.getValue());
        Number count = (Number) this.genericDao.executeUniqueResultNamedQuery("office.getOfficeCountForLevel", queryParameters);
        if (count != null && count.longValue() > 0) {
            throw new BusinessRuleException(OfficeConstants.KEYHASACTIVEOFFICEWITHLEVEL);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<OfficeDetailsDto> findActiveBranches(Short officeId) {

        List<OfficeDetailsDto> matchingActiveBranches = new ArrayList<OfficeDetailsDto>();

        String searchId = HierarchyManager.getInstance().getSearchId(officeId);
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("levelId", OfficeConstants.BRANCHOFFICE);
        queryParameters.put("OFFICESEARCHID", searchId);
        queryParameters.put("OFFICE_LIKE_SEARCHID", searchId + "%.");
        queryParameters.put("statusId", OfficeConstants.ACTIVE);
        List<OfficeDetailsDto> queryResult = (List<OfficeDetailsDto>) this.genericDao.executeNamedQuery(NamedQueryConstants.MASTERDATA_ACTIVE_BRANCHES,queryParameters);
        if (queryResult != null) {
            matchingActiveBranches = new ArrayList<OfficeDetailsDto>(queryResult);
        }
        return matchingActiveBranches;
    }
}