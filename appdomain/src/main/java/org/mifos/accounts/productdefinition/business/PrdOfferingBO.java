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

package org.mifos.accounts.productdefinition.business;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.accounts.productdefinition.persistence.PrdOfferingPersistence;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.accounts.productdefinition.util.helpers.ProductType;
import org.mifos.accounts.productsmix.business.ProductMixBO;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.dto.domain.ProductDetailsDto;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A product is a set of rules (interest rate, number of installments, maximum
 * amount, etc) which describes what an MFI offers.
 *
 * Although we may sometimes call these "offerings", the preferred word is
 * "products" (that is what they are called in the functional specification, and
 * the wider microfinance industry).
 */
public abstract class PrdOfferingBO extends AbstractBusinessObject {

    private static final Logger logger = LoggerFactory.getLogger(PrdOfferingBO.class);

    private final Short prdOfferingId;
    private String prdOfferingName;
    private String prdOfferingShortName;
    private final String globalPrdOfferingNum;
    private ProductTypeEntity prdType;
    private ProductCategoryBO prdCategory;
    private PrdStatusEntity prdStatus;
    private PrdApplicableMasterEntity prdApplicableMaster;
    private Date startDate;
    private Date endDate;
    private MifosCurrency currency;
    private OfficeBO office;
    private String description;
    private Set<ProductMixBO> collectionProductMix;
    private Set<PrdOfferingBO> prdOfferingNotAllowedId; // For Not allowed products
    private Short prdMixFlag; // Tagging products for which mixes were defined
    private Set<QuestionGroupReference> questionGroups;

    /**
     * default constructor for hibernate usage
     */
    protected PrdOfferingBO() {
        this(null, null, null, null);
    }

    /**
     * minimal legal constructor (based on table schema)
     */
    public PrdOfferingBO(Integer userId, String globalProductId, String name, String shortName, ProductCategoryBO productCategory,
            PrdStatusEntity status, PrdApplicableMasterEntity applicableToEntity, DateTime startDate, MifosCurrency currency) {
        this.prdOfferingId = null;
        this.prdOfferingName = name;
        this.prdOfferingShortName = shortName;
        this.globalPrdOfferingNum = globalProductId;
        this.prdCategory = productCategory;
        this.prdType = productCategory.getProductType();
        this.prdStatus = status;
        this.prdApplicableMaster = applicableToEntity;
        this.startDate = startDate.toDate();
        this.createdBy = userId.shortValue();
        this.createdDate = new DateTime().toDate();
        this.currency = currency;
    }

    @Deprecated
    protected PrdOfferingBO(final Short prdOfferingId) {
        this(prdOfferingId, null, null, null);
    }

    @Deprecated
    protected PrdOfferingBO(final Short prdOfferingId, final String globalPrdOfferingNum, final ProductTypeEntity prdType, final OfficeBO office) {
        this.prdOfferingId = prdOfferingId;
        this.globalPrdOfferingNum = globalPrdOfferingNum;
        this.prdType = prdType;
        this.office = office;
    }

    /**
     *
     */
    @Deprecated
    public PrdOfferingBO(final String name, final String shortName, final String globalProductNumber,
            final Date startDate, final ApplicableTo applicableToCustomer, final ProductCategoryBO category,
            final PrdStatusEntity prdStatus, final Date createdDate, final Short createdByUserId) {
        this.prdOfferingName = name;
        this.prdOfferingShortName = shortName;
        this.globalPrdOfferingNum = globalProductNumber;
        this.startDate = startDate;
        this.prdApplicableMaster = new PrdApplicableMasterEntity(applicableToCustomer);
        this.prdCategory = category;
        this.createdDate = createdDate;
        this.createdBy = createdByUserId;
        this.prdType = category.getProductType();
        this.prdStatus = prdStatus;
        this.prdOfferingId = null;
        this.office = null;
    }

    @Deprecated
    protected PrdOfferingBO(final UserContext userContext, final String prdOfferingName, final String prdOfferingShortName,
            final ProductCategoryBO prdCategory, final PrdApplicableMasterEntity prdApplicableMaster, final Date startDate)
            throws ProductDefinitionException {
        this(userContext, prdOfferingName, prdOfferingShortName, prdCategory, prdApplicableMaster, startDate, null,
                null);
    }

    @Deprecated
    protected PrdOfferingBO(final UserContext userContext, final String prdOfferingName, final String prdOfferingShortName,
            final ProductCategoryBO prdCategory, final PrdApplicableMasterEntity prdApplicableMaster, final Date startDate, final Date endDate,
            final String description, boolean validateStartDate) throws ProductDefinitionException {
        super(userContext);
        logger.debug("creating product offering");
        validateUserContext(userContext);
        vaildate(prdOfferingName, prdOfferingShortName, prdCategory, prdApplicableMaster, startDate);
        
        if (validateStartDate) {
            validateStartDateAgainstCurrentDate(startDate);
        }
        
        validateEndDateAgainstCurrentDate(startDate, endDate);
        validateDuplicateProductOfferingName(prdOfferingName);
        validateDuplicateProductOfferingShortName(prdOfferingShortName);
        prdOfferingId = null;
        this.prdOfferingName = prdOfferingName;
        this.prdOfferingShortName = prdOfferingShortName;
        this.prdCategory = prdCategory;
        this.prdType = prdCategory.getProductType();
        this.prdApplicableMaster = prdApplicableMaster;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.globalPrdOfferingNum = generatePrdOfferingGlobalNum();
        this.prdStatus = getPrdStatus(startDate, prdType);

        try {
            this.office = new OfficePersistence().getOffice(userContext.getBranchId());
        } catch (PersistenceException e) {
            throw new ProductDefinitionException(e);
        }
        logger.debug("creating product offering done");
    }
    
    @Deprecated
    protected PrdOfferingBO(final UserContext userContext, final String prdOfferingName, final String prdOfferingShortName,
            final ProductCategoryBO prdCategory, final PrdApplicableMasterEntity prdApplicableMaster, final Date startDate, final Date endDate,
            final String description) throws ProductDefinitionException {
        this(userContext, prdOfferingName, prdOfferingShortName, prdCategory, prdApplicableMaster, startDate, endDate, description, true);
    }

    public Short getPrdOfferingId() {
        return prdOfferingId;
    }

    public String getPrdOfferingName() {
        return prdOfferingName;
    }

    public String getPrdOfferingShortName() {
        return prdOfferingShortName;
    }

    public String getGlobalPrdOfferingNum() {
        return globalPrdOfferingNum;
    }

    public ProductTypeEntity getPrdType() {
        return prdType;
    }

    public ProductCategoryBO getPrdCategory() {
        return prdCategory;
    }

    /**
     * Most callers will want {@link #isActive()} instead (or perhaps
     * {@link #getStatus()}.
     */
    public PrdStatusEntity getPrdStatus() {
        return prdStatus;
    }

    public PrdStatus getStatus() {
        return PrdStatus.fromInt(prdStatus.getOfferingStatusId());
    }

    public abstract boolean isActive();

    public PrdApplicableMasterEntity getPrdApplicableMaster() {
        return prdApplicableMaster;
    }

    public ApplicableTo getPrdApplicableMasterEnum() {
        return prdApplicableMaster.asEnum();
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public OfficeBO getOffice() {
        return office;
    }

    public String getDescription() {
        return description;
    }

    void setPrdOfferingName(final String prdOfferingName) {
        this.prdOfferingName = prdOfferingName;
    }

    public void setPrdOfferingShortName(final String prdOfferingShortName) {
        this.prdOfferingShortName = prdOfferingShortName;
    }

    public void setPrdCategory(final ProductCategoryBO prdCategory) {
        this.prdCategory = prdCategory;
    }

    public void setPrdStatus(final PrdStatusEntity prdStatus) {
        this.prdStatus = prdStatus;
    }

    public void setPrdApplicableMaster(final PrdApplicableMasterEntity prdApplicableMaster) {
        this.prdApplicableMaster = prdApplicableMaster;
    }

    void setStartDate(final Date startDate) {
        this.startDate = startDate;
    }

    void setEndDate(final Date endDate) {
        this.endDate = endDate;
    }

    public void setCurrency(MifosCurrency currency) {
        this.currency = currency;
    }

    public MifosCurrency getCurrency() {
        if(currency == null) {
            return Money.getDefaultCurrency();
        }
        return currency;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Set<ProductMixBO> getCollectionProductMix() {
        return collectionProductMix;
    }

    public void setCollectionProductMix(final Set<ProductMixBO> collectionProductMix) {
        this.collectionProductMix = collectionProductMix;
    }

    public Set<PrdOfferingBO> getPrdOfferingNotAllowedId() {
        return prdOfferingNotAllowedId;
    }

    public void setPrdOfferingNotAllowedId(final Set<PrdOfferingBO> prdOfferingNotAllowedId) {
        this.prdOfferingNotAllowedId = prdOfferingNotAllowedId;
    }

    public Short getPrdMixFlag() {
        return prdMixFlag;
    }

    public void setPrdMixFlag(final Short prdMixFlag) {
        this.prdMixFlag = prdMixFlag;
    }

    private String generatePrdOfferingGlobalNum() throws ProductDefinitionException {
        logger.debug("Generating new product Offering global number");
        StringBuilder globalPrdOfferingNum = new StringBuilder();
        globalPrdOfferingNum.append(userContext.getBranchId());
        globalPrdOfferingNum.append("-");
        Short maxPrdID = null;
        try {
            maxPrdID = new PrdOfferingPersistence().getMaxPrdOffering();
        } catch (PersistenceException e) {
            throw new ProductDefinitionException(e);
        }
        globalPrdOfferingNum.append(StringUtils.leftPad(String.valueOf(maxPrdID != null ? maxPrdID + 1
                : ProductDefinitionConstants.DEFAULTMAX + 1), 3, '0'));
        logger.debug("Generation of new product Offering global number done" + globalPrdOfferingNum);
        return globalPrdOfferingNum.toString();
    }

    private void vaildate(final String prdOfferingName, final String prdOfferingShortName, final ProductCategoryBO prdCategory,
            final PrdApplicableMasterEntity prdApplicableMaster, final Date startDate) throws ProductDefinitionException {
        logger.debug("Validating the fields in Prd Offering");
        if (prdOfferingName == null || prdOfferingShortName == null || prdCategory == null
                || prdApplicableMaster == null || prdOfferingShortName.length() > 4 || startDate == null) {
            throw new ProductDefinitionException(ProductDefinitionConstants.ERROR_CREATE);
        }
        logger.debug("Validation of the fields in Prd Offering done.");
    }

    private void validateUserContext(final UserContext userContext) throws ProductDefinitionException {
        logger.debug("Validating the usercontext in Prd Offering");
        if (userContext == null) {
            throw new ProductDefinitionException(ProductDefinitionConstants.ERROR_CREATE);
        }
        logger.debug("Validation of the fields in Prd Offering done.");
    }

    protected void validateStartDateAgainstCurrentDate(final Date startDate) throws ProductDefinitionException {
        if (DateUtils.getDateWithoutTimeStamp(startDate.getTime())
                .compareTo(DateUtils.getCurrentDateWithoutTimeStamp()) < 0) {
            throw new ProductDefinitionException(ProductDefinitionConstants.INVALIDSTARTDATE);
        }
    }

    private void validateEndDateAgainstCurrentDate(final Date startDate, final Date endDate) throws ProductDefinitionException {
        if (endDate != null
                && DateUtils.getDateWithoutTimeStamp(startDate.getTime()).compareTo(
                        DateUtils.getDateWithoutTimeStamp(endDate.getTime())) >= 0) {
            throw new ProductDefinitionException(ProductDefinitionConstants.INVALIDENDDATE);
        }
    }

    private PrdStatusEntity getPrdStatus(final Date startDate, final ProductTypeEntity prdType) throws ProductDefinitionException {
        logger.debug("getting the Product status for prdouct offering with start date :" + startDate
                + " and product Type :" + prdType.getProductTypeID());
        PrdStatus prdStatus = null;
        if (!prdType.getProductTypeID().equals(ProductType.LOAN.getValue()) && 
                startDate.compareTo(DateUtils.getCurrentDateWithoutTimeStamp()) == 0) {
            prdStatus = getActivePrdStatus(prdType);
        } else if (prdType.getProductTypeID().equals(ProductType.LOAN.getValue()) &&
                startDate.compareTo(DateUtils.getCurrentDateWithoutTimeStamp()) <= 0) {
            prdStatus = getActivePrdStatus(prdType);
        } else {
            prdStatus = getInActivePrdStatus(prdType);
        }
        try {
            logger.debug("getting the Product status for product status :" + prdStatus);
            return new PrdOfferingPersistence().getPrdStatus(prdStatus);
        } catch (PersistenceException e) {
            throw new ProductDefinitionException(e);
        }
    }

    private PrdStatus getActivePrdStatus(final ProductTypeEntity prdType) {
        logger.debug("getting the Active Product status for product Type :" + prdType.getProductTypeID());
        if (prdType.getProductTypeID().equals(ProductType.LOAN.getValue())) {
            return PrdStatus.LOAN_ACTIVE;
        }

        return PrdStatus.SAVINGS_ACTIVE;
    }

    private PrdStatus getInActivePrdStatus(final ProductTypeEntity prdType) {
        logger.debug("getting the In Active Product status for product Type :" + prdType.getProductTypeID());
        if (prdType.getProductTypeID().equals(ProductType.LOAN.getValue())) {
            return PrdStatus.LOAN_INACTIVE;
        }

        return PrdStatus.SAVINGS_INACTIVE;
    }

    private void validateDuplicateProductOfferingName(final String productOfferingName) throws ProductDefinitionException {
        logger.debug("Checking for duplicate product offering name");
        try {
            PrdOfferingPersistence prdOfferingPersistence = new PrdOfferingPersistence();
            Integer count = prdOfferingPersistence.getProductOfferingNameCount(productOfferingName);
            if (!count.equals(0)) {
                throw new ProductDefinitionException(ProductDefinitionConstants.DUPLPRDINSTNAME);
            }
        } catch (PersistenceException e) {
            throw new ProductDefinitionException(e);
        }
    }

    private void validateDuplicateProductOfferingShortName(final String productOfferingShortName)
            throws ProductDefinitionException {
        logger.debug("Checking for duplicate product offering short name");
        try {
            if (!new PrdOfferingPersistence().getProductOfferingShortNameCount(productOfferingShortName).equals(
                    Integer.valueOf("0"))) {
                throw new ProductDefinitionException(ProductDefinitionConstants.DUPLPRDINSTSHORTNAME);
            }
        } catch (PersistenceException e) {
            throw new ProductDefinitionException(e);
        }
    }

    public void update(final Short userId, final String prdOfferingName, final String prdOfferingShortName,
            final ProductCategoryBO prdCategory, final PrdApplicableMasterEntity prdApplicableMaster, final Date startDate, final Date endDate,
            final String description, final PrdStatus status) throws ProductDefinitionException {
        vaildate(prdOfferingName, prdOfferingShortName, prdCategory, prdApplicableMaster, startDate);
        validateStartDateForUpdate(startDate);
        validateEndDateAgainstCurrentDate(startDate, endDate);
        if (!prdOfferingName.equals(this.prdOfferingName)) {
            validateDuplicateProductOfferingName(prdOfferingName);
        }
        if (!prdOfferingShortName.equals(this.prdOfferingShortName)) {
            validateDuplicateProductOfferingShortName(prdOfferingShortName);
        }
        this.prdOfferingName = prdOfferingName;
        this.prdOfferingShortName = prdOfferingShortName;
        this.prdCategory = prdCategory;
        this.prdApplicableMaster = prdApplicableMaster;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        try {
            this.prdStatus = new PrdOfferingPersistence().getPrdStatus(status);
        } catch (PersistenceException pe) {
            throw new ProductDefinitionException(pe);
        }
        logger.debug("creating product offering done");

    }

    private void validateStartDateForUpdate(final Date startDate) throws ProductDefinitionException {
        if (DateUtils.getDateWithoutTimeStamp(this.startDate.getTime()).compareTo(
                DateUtils.getCurrentDateWithoutTimeStamp()) <= 0
                && DateUtils.getDateWithoutTimeStamp(startDate.getTime()).compareTo(
                        DateUtils.getDateWithoutTimeStamp(this.startDate.getTime())) != 0) {
            throw new ProductDefinitionException(ProductDefinitionConstants.STARTDATEUPDATEEXCEPTION);
        } else if (DateUtils.getDateWithoutTimeStamp(this.startDate.getTime()).compareTo(
                DateUtils.getCurrentDateWithoutTimeStamp()) > 0) {
            validateStartDateAgainstCurrentDate(startDate);
        }

    }

    public void updatePrdOfferingFlag() throws PersistenceException {
        this.setPrdMixFlag(YesNoFlag.YES.getValue());
        new PrdOfferingPersistence().createOrUpdate(this);
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + (prdOfferingId == null ? 0 : prdOfferingId.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PrdOfferingBO other = (PrdOfferingBO) obj;
        return isOfSameOffering(other);
    }

    public boolean isOfSameOffering(final PrdOfferingBO other) {
        if (prdOfferingId == null) {
            if (other.prdOfferingId != null) {
                return false;
            }
        } else if (!prdOfferingId.equals(other.getPrdOfferingId())) {
            return false;
        }

        if (this.globalPrdOfferingNum != null && other.getGlobalPrdOfferingNum() != null) {
            if (!other.getGlobalPrdOfferingNum().equals(this.globalPrdOfferingNum)) {
                return false;
            }
        }

        return true;
    }

    public PrdOfferingDto toDto() {
        return new PrdOfferingDto(this.prdOfferingId, this.prdOfferingName, this.globalPrdOfferingNum);
    }

    public ProductDetailsDto toDetailsDto() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        DateTime startDateTime = new DateTime(this.startDate);
        String startDateFormatted = format.format(this.startDate);
        String endDateFormatted = "";
        DateTime endDateTime = null;
        if (this.endDate != null) {
            endDateTime = new DateTime(this.endDate);
            endDateFormatted = format.format(this.endDate);
        }
        ProductDetailsDto detailsDto = new ProductDetailsDto(this.prdOfferingName, this.prdOfferingShortName, this.description, this.prdCategory.getProductCategoryID().intValue(), startDateTime, endDateTime, this.prdApplicableMaster.getId().intValue());
        detailsDto.setId(this.prdOfferingId.intValue());
        detailsDto.setGlobalNumber(this.globalPrdOfferingNum);
        detailsDto.setStatus(this.prdStatus.getOfferingStatusId().intValue());
        detailsDto.setCategoryName(this.prdCategory.getProductCategoryName());
        detailsDto.setStartDateFormatted(startDateFormatted);
        detailsDto.setEndDateFormatted(endDateFormatted);
        detailsDto.setCreatedDate(new DateTime(this.getCreatedDate()));
        detailsDto.setCreatedDateFormatted(format.format(this.getCreatedDate()));
        return detailsDto;
    }

    public void updateDetailsOfProductNotInUse(String name, String shortName, String description, ProductCategoryBO productCategory,
            Date startDate, Date endDate, PrdApplicableMasterEntity applicableMasterEntity, PrdStatusEntity prdStatusEntity) {
        updateProductDetails(name, shortName, description, productCategory, startDate, endDate, prdStatusEntity);
        this.prdApplicableMaster = applicableMasterEntity;
    }

    public void updateProductDetails(String name, String shortName, String description,
            ProductCategoryBO productCategory, Date startDate, Date endDate, PrdStatusEntity prdStatusEntity) {
        this.prdOfferingName = name;
        this.prdOfferingShortName = shortName;
        this.description = description;
        this.prdCategory = productCategory;
        this.startDate = startDate;
        this.endDate = endDate;
        this.prdStatus = prdStatusEntity;
    }

    public void updateStatus(PrdStatusEntity status) {
        this.prdStatus = status;
    }

    public boolean isDifferentName(final String name) {
        return !this.prdOfferingName.equals(name);
    }

    public boolean isDifferentShortName(final String shortName) {
        return !this.prdOfferingShortName.equals(shortName);
    }

    public boolean isDifferentStartDate(DateTime startDate) {
        return !new LocalDate(startDate).equals(new LocalDate(this.startDate));
    }

    public Set<QuestionGroupReference> getQuestionGroups() {
        return questionGroups;
    }

    public void setQuestionGroups(Set<QuestionGroupReference> questionGroups) {
        this.questionGroups = questionGroups;
    }

    public void mergeQuestionGroups(Set<QuestionGroupReference> questionGroups) {
        if (this.questionGroups == null || questionGroups == null) {
            setQuestionGroups(questionGroups);
        } else {
            for (Iterator<QuestionGroupReference> iterator = this.questionGroups.iterator(); iterator.hasNext();) {
                QuestionGroupReference questionGroup = iterator.next();
                if (questionGroups.contains(questionGroup)) {
                    questionGroups.remove(questionGroup);
                } else {
                    iterator.remove();
                }
            }
            this.questionGroups.addAll(questionGroups);
        }
    }
}
