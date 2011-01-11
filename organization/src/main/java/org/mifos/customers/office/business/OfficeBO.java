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

package org.mifos.customers.office.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.customers.office.exceptions.OfficeException;
import org.mifos.customers.office.exceptions.OfficeValidationException;
import org.mifos.customers.office.util.helpers.OfficeConstants;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.office.util.helpers.OfficeStatus;
import org.mifos.customers.office.util.helpers.OperationMode;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.OfficeHierarchyDto;
import org.mifos.dto.domain.OfficeUpdateRequest;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.security.util.UserContext;

public class OfficeBO extends AbstractBusinessObject implements Comparable<OfficeBO> {

    private Short officeId;
    private final Short operationMode;
    @SuppressWarnings("unused")
    // see .hbm.xml file
    private Integer maxChildCount = Integer.valueOf(0);
    private String officeName;
    private String shortName;
    private String globalOfficeNum;
    private String searchId;
    private OfficeLevelEntity level;
    private OfficeBO parentOffice;
    private OfficeStatusEntity status;
    private OfficeAddressEntity address;
    private Set<OfficeCustomFieldEntity> customFields;
    private Set<OfficeBO> children;
    private Set<HolidayBO> holidays = new HashSet<HolidayBO>();

    public static List<OfficeHierarchyDto> convertToBranchOnlyHierarchyWithParentsOfficeHierarchy(
            List<OfficeBO> branchParents) {

        List<OfficeHierarchyDto> hierarchy = new ArrayList<OfficeHierarchyDto>();

        for (OfficeBO officeBO : branchParents) {

            List<OfficeHierarchyDto> children = new ArrayList<OfficeHierarchyDto>();

            Set<OfficeBO> branchOnlyChildren = officeBO.getBranchOnlyChildren();
            if (branchOnlyChildren != null && !branchOnlyChildren.isEmpty()) {
                children = convertToBranchOnlyHierarchyWithParentsOfficeHierarchy(new ArrayList<OfficeBO>(
                        branchOnlyChildren));
            }
            OfficeHierarchyDto officeHierarchy = new OfficeHierarchyDto(officeBO.getOfficeId(), officeBO
                    .getOfficeName(), officeBO.getSearchId(), officeBO.isActive(), children);

            hierarchy.add(officeHierarchy);
        }

        return hierarchy;
    }

    /**
     * default constructor for hibernate usuage
     */
    public OfficeBO() {
        this(null, null, null, null);
        status = new OfficeStatusEntity(OfficeStatus.ACTIVE);
        address = new OfficeAddressEntity();
    }

    /**
     * minimal legal constructor
     *
     * @param officeId
     */
    public OfficeBO(Short officeId, final String name, final String shortName, String globalOfficeNum,
            OfficeBO parentOffice, OfficeLevel officeLevel, String searchId, OfficeStatus status) {
        this.officeId = officeId;
        this.officeName = name;
        this.shortName = shortName;
        this.globalOfficeNum = globalOfficeNum;
        this.searchId = searchId;
        this.parentOffice = parentOffice;
        this.level = new OfficeLevelEntity(officeLevel);
        this.operationMode = OperationMode.REMOTE_SERVER.getValue();
        this.status = new OfficeStatusEntity(status);
        this.address = null;
    }

    /**
     * For tests. Does not require that the names in question actually exist in the database.
     */
    public static OfficeBO makeForTest(final UserContext userContext, final OfficeLevel level,
            final OfficeBO parentOffice, final String searchId, final List<CustomFieldDto> customFields,
            final String officeName, final String shortName, final Address address, final OperationMode operationMode,
            final OfficeStatus status) throws OfficeException {
        return new OfficeBO(userContext, null, level, parentOffice, searchId, customFields, officeName, shortName,
                address, operationMode, status);
    }

    public static OfficeBO makeForTest(final UserContext userContext, final Short officeId, final String officeName,
            final String shortName) throws OfficeException {
        return new OfficeBO(userContext, officeId, OfficeLevel.AREAOFFICE, null, null, null, officeName, shortName,
                null, OperationMode.LOCAL_SERVER, OfficeStatus.ACTIVE);
    }

    /**
     * Construct an object without validating it against the database.
     */
    private OfficeBO(final UserContext userContext, final Short officeId, final OfficeLevel level,
            final OfficeBO parentOffice, final String searchId, final List<CustomFieldDto> customFields,
            final String officeName, final String shortName, final Address address, final OperationMode operationMode,
            final OfficeStatus status) throws OfficeValidationException {
        super(userContext);
        verifyFieldsNoDatabase(level, operationMode);

        setCreateDetails();

        this.globalOfficeNum = null;
        this.operationMode = operationMode.getValue();
        this.searchId = searchId;
        this.officeId = officeId;
        this.level = new OfficeLevelEntity(level);
        this.status = new OfficeStatusEntity(status);
        this.parentOffice = parentOffice;
        if (parentOffice != null && parentOffice.getHolidays() != null) {
            this.setHolidays(new HashSet<HolidayBO>(parentOffice.getHolidays()));
        }

        this.officeName = officeName;
        this.shortName = shortName;
        if (address != null) {
            this.address = new OfficeAddressEntity(this, address);
        }
        this.customFields = new HashSet<OfficeCustomFieldEntity>();
    }


    /**
     * @throws OfficeValidationException
     *             Thrown when there's a validation error
     */
    public OfficeBO(final UserContext userContext, final OfficeLevel level, final OfficeBO parentOffice,
            final List<CustomFieldDto> customFields, final String officeName, final String shortName,
            final Address address, final OperationMode operationMode) throws OfficeValidationException {
        this(userContext, null, level, parentOffice, null, customFields, officeName, shortName, address, operationMode, OfficeStatus.ACTIVE);
        verifyFields(officeName, shortName, parentOffice);
    }

    public OfficeBO(final Short officeId, final String officeName, final Integer maxChildCount,
            final Short operationMode) {
        super();
        this.officeId = officeId;
        this.officeName = officeName;
        this.maxChildCount = maxChildCount;
        this.operationMode = operationMode;
    }

    @Override
    public void setCreatedDate(final Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getGlobalOfficeNum() {
        return globalOfficeNum;
    }

    public OfficeLevel getOfficeLevel() {
        return OfficeLevel.getOfficeLevel(level.getId());
    }

    public OfficeStatus getOfficeStatus() throws OfficeException {
        return OfficeStatus.getOfficeStatus(status.getId());
    }

    public OfficeStatusEntity getStatus() {
        return this.status;
    }

    public OfficeLevelEntity getLevel() {
        return this.level;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(final String officeName) {
        this.officeName = officeName;
    }

    public Short getOfficeId() {
        return officeId;
    }

    public OperationMode getMode() {
        return OperationMode.fromInt(operationMode);
    }

    public OfficeBO getParentOffice() {
        return parentOffice;
    }

    void setParentOffice(final OfficeBO parentOffice) {
        this.parentOffice = parentOffice;
    }

    public String getSearchId() {
        return searchId;
    }

    public String getShortName() {
        return shortName;
    }

    void setShortName(final String shortName) {
        this.shortName = shortName;
    }

    @Override
    public void setUpdatedDate(final Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public OfficeAddressEntity getAddress() {
        return address;
    }

    public Set<OfficeCustomFieldEntity> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(final Set<OfficeCustomFieldEntity> customFields) {
        if (customFields != null) {
            this.customFields = customFields;
        }
    }

    public void setAddress(final OfficeAddressEntity address) {
        this.address = address;
    }

    public void setChildren(final Set<OfficeBO> children) {
        this.children = children;
    }

    public void setHolidays(final Set<HolidayBO> holidays) {
        this.holidays = holidays;
    }

    public void setGlobalOfficeNum(final String globalOfficeNum) {
        this.globalOfficeNum = globalOfficeNum;
    }

    void setLevel(final OfficeLevelEntity level) {
        this.level = level;
    }

    public void setSearchId(final String searchId) {
        this.searchId = searchId;
    }

    void setStatus(final OfficeStatusEntity status) {
        this.status = status;
    }

    public Set<OfficeBO> getChildren() {
        if (children == null) {
            children = new HashSet<OfficeBO>();
        }
        return children;
    }

    public Set<HolidayBO> getHolidays() {
        return holidays;
    }

    private void removeChild(final OfficeBO office) {
        Set<OfficeBO> childerns = null;
        if (getChildren().size() > 0) {
            childerns = getChildren();
            childerns.remove(office);
        }
        setChildren(childerns);
    }

    private void addChild(final OfficeBO office) {
        Set<OfficeBO> childerns = getChildren();
        office.setParentOffice(this);
        childerns.add(office);
        setChildren(childerns);
    }

    // Changed this method signature so that it throws the PersistenceExcpetion
    // instead of wrapping in an OfficeExcpetion. The idea being that we are
    // seperating validation exceptions from PersistenceExceptions, which should
    // ultimately be runtime errors because they occurr, most likely, from
    // configuration/binding problems which are exceptions that we shouldn't
    // be catching.
    private void verifyFields(final String officeName, final String shortName, final OfficeBO parentOffice)
            throws OfficeValidationException {

        if (StringUtils.isBlank(officeName)) {
            throw new OfficeValidationException(OfficeConstants.ERRORMANDATORYFIELD,
                    new Object[] { getLocaleString(OfficeConstants.OFFICE_NAME) });
        }

        if (StringUtils.isBlank(shortName)) {
            throw new OfficeValidationException(OfficeConstants.ERRORMANDATORYFIELD,
                    new Object[] { getLocaleString(OfficeConstants.OFFICESHORTNAME) });
        }

        if (parentOffice == null) {
            throw new OfficeValidationException(OfficeConstants.ERRORMANDATORYFIELD,
                    new Object[] { getLocaleString(OfficeConstants.PARENTOFFICE) });
        }
    }

    private void verifyFieldsNoDatabase(final OfficeLevel level, final OperationMode operationMode)
            throws OfficeValidationException {
        if (level == null) {
            throw new OfficeValidationException(OfficeConstants.ERRORMANDATORYFIELD,
                    new Object[] { getLocaleString(OfficeConstants.OFFICELEVEL) });
        }
        if (operationMode == null) {
            throw new OfficeValidationException(OfficeConstants.ERRORMANDATORYFIELD,
                    new Object[] { getLocaleString(OfficeConstants.OFFICEOPERATIONMODE) });
        }
    }

    private String getLocaleString(final String key) {
        final String LOCALIZED_RESOURCE_PATH = "org/mifos/config/localizedResources/";
        final String OFFICERESOURCEPATH = LOCALIZED_RESOURCE_PATH + "OfficeUIResources";
        ResourceBundle resourceBundle = ResourceBundle.getBundle(OFFICERESOURCEPATH, userContext.getPreferredLocale());
        return resourceBundle.getString(key);
    }

    public boolean isActive() {
        return OfficeStatus.ACTIVE.getValue().equals(this.status.getId());
    }

    public boolean isInActive() {
        return OfficeStatus.INACTIVE.getValue().equals(this.status.getId());
    }

    public void update(UserContext userContext, OfficeUpdateRequest officeUpdateRequest, OfficeBO newParentOffice) throws OfficeException {
        updateDetails(userContext);

        OfficeStatus officeStatus = OfficeStatus.getOfficeStatus(officeUpdateRequest.getNewStatus());
        OfficeLevel officeLevel = OfficeLevel.getOfficeLevel(officeUpdateRequest.getNewlevel());
        Address address = null;
        AddressDto dto = officeUpdateRequest.getAddress();
        if (dto != null) {
            address = new Address(dto.getLine1(), dto.getLine2(), dto.getLine3(), dto.getCity(), dto.getState(), dto.getCountry(), dto.getZip(), dto.getPhoneNumber());
        }
        update(officeUpdateRequest.getOfficeName(), officeUpdateRequest.getShortName(), officeStatus, officeLevel, newParentOffice, address, new ArrayList<CustomFieldDto>());
    }

    public void update(final String newName, final String newShortName, final OfficeStatus newStatus,
            final OfficeLevel newLevel, final OfficeBO newParent, final Address address,
            final List<CustomFieldDto> customFileds) throws OfficeException {

        this.officeName = newName;
        this.shortName = newShortName;

        if (isDifferentOfficeLevel(newLevel)) {

            if (!canUpdateLevel(newLevel)) {
                throw new OfficeException(OfficeConstants.ERROR_INVALID_LEVEL);
            }

            this.level = new OfficeLevelEntity(newLevel);
        }

        if (isNotHeadOffice()) {
            updateParent(newParent);
        }

        this.status = new OfficeStatusEntity(newStatus);
        updateAddress(address);
    }

    private boolean isNotHeadOffice() {
        return !isHeadOffice();
    }

    private boolean isHeadOffice() {
        return this.getOfficeLevel().equals(OfficeLevel.HEADOFFICE);
    }

    private void updateParent(final OfficeBO newParent) throws OfficeException {
        if (newParent != null) {
            if (this.getParentOffice() != null) {
                if (!this.getParentOffice().getOfficeId().equals(newParent.getOfficeId())) {
                    OfficeBO oldParent = this.getParentOffice();

                    if (this.getOfficeLevel().getValue().shortValue() < newParent.getOfficeLevel().getValue()
                            .shortValue()) {
                        throw new OfficeException(OfficeConstants.ERROR_INVALID_PARENT);
                    }
                    OfficeBO oldParent1 = getIfChildPresent(newParent, oldParent);
                    if (oldParent1 == null) {
                        oldParent1 = oldParent;
                    }
                    oldParent1.removeChild(this);
                    OfficeBO newParent1 = getIfChildPresent(oldParent, newParent);
                    if (newParent1 == null) {
                        newParent1 = newParent;
                    }
                    newParent1.addChild(this);
                    newParent1.updateSearchId(newParent1.getSearchId());
                    oldParent1.updateSearchId(oldParent1.getSearchId());

                }
            }
        }
    }

    private void updateSearchId(final String searchId) {
        this.setSearchId(searchId);
        int i = 1;
        if (this.getChildren() != null) {
            Iterator<OfficeBO> iter = this.getChildren().iterator();
            while (iter.hasNext()) {
                OfficeBO element = iter.next();
                element.updateSearchId(this.getSearchId() + i + ".");
                i++;

            }

        }
    }

    private boolean canUpdateLevel(final OfficeLevel level) {
        if (this.getOfficeLevel().getValue() > level.getValue()) {
            return true;
        }
        for (OfficeBO office : this.children) {
            if (office.getLevel().getId() <= level.getValue()) {
                return false;
            }
        }
        return true;

    }

    private void updateAddress(final Address address) {
        if (this.address != null && address != null) {
            this.address.setAddress(address);
        } else if (this.address == null && address != null) {
            this.address = new OfficeAddressEntity(this, address);
        }
    }

    public Set<OfficeBO> getBranchOnlyChildren() {
        Set<OfficeBO> offices = new HashSet<OfficeBO>();
        for (OfficeBO office : getChildren()) {
            if (office.getOfficeLevel().equals(OfficeLevel.BRANCHOFFICE)) {
                offices.add(office);
            }
        }
        return offices;
    }

    /* FIXME: we also need to implement hashCode() */
    @Override
    public boolean equals(final Object o) {
        return officeId.equals(((OfficeBO) o).getOfficeId());
    }

    public int compareTo(final OfficeBO o) {
        return officeId.compareTo(o.getOfficeId());
    }

    public OfficeBO getIfChildPresent(final OfficeBO parent, final OfficeBO child) {
        for (OfficeBO childl : parent.getChildren()) {
            if (childl.getOfficeId().equals(child.getOfficeId())) {
                return childl;
            }
            return getIfChildPresent(childl, child);
        }

        return null;
    }

    public boolean isParent(final OfficeBO child) {
        boolean isParent = false;
        for (OfficeBO children : getChildren()) {
            if (children.equals(child)) {
                return true;
            }
            isParent = children.isParent(child);
            if (isParent) {
                return true;
            }
        }

        return isParent;
    }

    public void addHoliday(HolidayBO holiday) {
        getHolidays().add(holiday);
    }

    public boolean hasChildWithAnyOf(List<Short> officeIds) {
        boolean childFound = false;

        for (OfficeBO child : getChildren()) {
            if (officeIds.contains(child.getOfficeId())) {
                childFound = true;
                return childFound;
            }

            childFound = child.hasChildWithAnyOf(officeIds);
        }

        return childFound;
    }

    public void validateVersion(Integer previousVersionNum) throws OfficeException {
        if (!this.versionNo.equals(previousVersionNum)) {
            throw new OfficeException(Constants.ERROR_VERSION_MISMATCH);
        }
    }

    public boolean isNameDifferent(String newOfficeName) {
        return !this.officeName.equalsIgnoreCase(newOfficeName);
    }

    public boolean isShortNameDifferent(String newShortName) {
        return !this.shortName.equalsIgnoreCase(newShortName);
    }

    private boolean isDifferentOfficeLevel(OfficeLevel newLevel) {
        return !this.level.getLevel().equals(newLevel);
    }

    public boolean isStatusDifferent(OfficeStatus newStatus) {
        return !newStatus.getValue().equals(this.status.getId());
    }

    public boolean isDifferentParentOffice(OfficeBO newParentOffice) {
        return !newParentOffice.equals(this.parentOffice);
    }

    public boolean isNotBranch() {
        return OfficeLevel.BRANCHOFFICE.getValue().equals(this.level.getId());
    }

    public boolean isDifferent(OfficeBO newOffice) {
        return !this.officeId.equals(newOffice.getOfficeId());
    }

    @Deprecated
    public void setOfficeId(Short officeId) {
        this.officeId = officeId;
    }
}