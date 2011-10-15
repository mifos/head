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

import org.apache.commons.lang.StringUtils;
import org.mifos.accounts.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.accounts.productdefinition.persistence.LegacyProductCategoryDao;
import org.mifos.accounts.productdefinition.util.helpers.PrdCategoryStatus;
import org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductCategoryBO extends AbstractBusinessObject {

    private static final Logger logger = LoggerFactory.getLogger(ProductCategoryBO.class);

    private final Short productCategoryID;

    private final ProductTypeEntity productType;

    @SuppressWarnings("unused")
    // see .hbm.xml file
    private OfficeBO office;

    private final String globalPrdCategoryNum;

    private String productCategoryName;

    private String productCategoryDesc;

    private PrdCategoryStatusEntity prdCategoryStatus;

    /**
     * default constructor for hibernate usage
     */
    protected ProductCategoryBO() {
        productCategoryID = null;
        productType = null;
        office = null;
        globalPrdCategoryNum = null;
    }

    /**
     * minimal cosntructor
     */
    public ProductCategoryBO(ProductTypeEntity productType, String name, String description, final String globalPrdCategoryNum) {
        this.productCategoryID = null;
        this.productType = productType;
        this.globalPrdCategoryNum = globalPrdCategoryNum;
        this.productCategoryName = name;
        this.productCategoryDesc = description;
        this.prdCategoryStatus = new PrdCategoryStatusEntity(PrdCategoryStatus.ACTIVE);
    }

    /**
     * use minimal constructor
     */
    @Deprecated
    public ProductCategoryBO(final Short id, final String globalPrdCategoryNum) {
        this.productCategoryID = id;
        this.productType = null;
        this.office = null;
        this.globalPrdCategoryNum = globalPrdCategoryNum;
    }

    /**
     * use minimal constructor
     */
    @Deprecated
    public ProductCategoryBO(final UserContext userContext, final ProductTypeEntity productType, final String productCategoryName,
            final String productCategoryDesc) throws ProductDefinitionException {
        super(userContext);
        try {
            logger.debug("Creating product category");
            validateDuplicateProductCategoryName(productCategoryName);
            this.productCategoryID = null;
            this.productType = productType;
            this.office = new OfficePersistence().getOffice(userContext.getBranchId());
            this.globalPrdCategoryNum = generatePrdCategoryNum();
            this.productCategoryName = productCategoryName;
            this.productCategoryDesc = productCategoryDesc;
            this.prdCategoryStatus = new PrdCategoryStatusEntity(PrdCategoryStatus.ACTIVE);
            setCreateDetails();
            logger.debug("Creation of product category done");
        } catch (PersistenceException e) {
            throw new ProductDefinitionException(e);
        }
    }

    public ProductTypeEntity getProductType() {
        return productType;
    }

    public String getProductCategoryDesc() {
        return productCategoryDesc;
    }

    public void setProductCategoryDesc(final String productCategoryDesc) {
        this.productCategoryDesc = productCategoryDesc;
    }

    public Short getProductCategoryID() {
        return productCategoryID;
    }

    public String getProductCategoryName() {
        return productCategoryName;
    }

    void setProductCategoryName(final String productCategoryName) {
        this.productCategoryName = productCategoryName;
    }

    public PrdCategoryStatusEntity getPrdCategoryStatus() {
        return prdCategoryStatus;
    }

    void setPrdCategoryStatus(final PrdCategoryStatusEntity prdCategoryStatus) {
        this.prdCategoryStatus = prdCategoryStatus;
    }

    public String getGlobalPrdCategoryNum() {
        return globalPrdCategoryNum;
    }

    /**
     * @deprecated
     */
    @Deprecated
    private String generatePrdCategoryNum() throws ProductDefinitionException {
        logger.debug("Generating new product category global number");
        StringBuilder globalPrdOfferingNum = new StringBuilder();
        globalPrdOfferingNum.append(userContext.getBranchId());
        globalPrdOfferingNum.append("-");
        Short maxPrdID;
        try {
            maxPrdID = getLegacyProductCategoryDao().getMaxPrdCategoryId();
        } catch (PersistenceException e) {
            throw new ProductDefinitionException(e);
        }
        globalPrdOfferingNum.append(StringUtils.leftPad(String.valueOf(maxPrdID != null ? maxPrdID + 1
                : ProductDefinitionConstants.DEFAULTMAX), 3, '0'));
        logger.debug("Generation of new product category global number done");
        return globalPrdOfferingNum.toString();
    }

    @Deprecated
    private void validateDuplicateProductCategoryName(final String productCategoryName) throws ProductDefinitionException {
        logger.debug("Checking for duplicate product category name");
        try {
            if (!getLegacyProductCategoryDao().getProductCategory(productCategoryName).equals(Integer.valueOf("0"))) {
                throw new ProductDefinitionException(ProductDefinitionConstants.DUPLICATE_CATEGORY_NAME);
            }
        } catch (PersistenceException e) {
            throw new ProductDefinitionException(e);
        }
    }

    @Deprecated
    public void validateDuplicateProductCategoryName(final String productCategoryName, final Short productCategoryId)
            throws ProductDefinitionException {
        logger.debug("Checking for duplicate product category name");
        try {
            if (!getLegacyProductCategoryDao().getProductCategory(productCategoryName, productCategoryId).equals(Integer.valueOf("0"))) {
                throw new ProductDefinitionException(ProductDefinitionConstants.DUPLICATE_CATEGORY_NAME);
            }
        } catch (PersistenceException e) {
            throw new ProductDefinitionException(e);
        }
    }

    /**
     * @deprecated use service facade or appropriate test factory method
     */
    @Deprecated
    public void updateProductCategory(final String productCategoryName, final String productCategoryDesc, final PrdCategoryStatusEntity prdCategoryStatus) throws ProductDefinitionException {
        logger.debug("Updating product category name");
        validateDuplicateProductCategoryName(productCategoryName, productCategoryID);
        this.productCategoryName = productCategoryName;
        this.productCategoryDesc = productCategoryDesc;
        this.prdCategoryStatus = prdCategoryStatus;
        try {
            getLegacyProductCategoryDao().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new ProductDefinitionException(e);
        }
        logger.debug("Updating product category done");
    }

    /**
     * @deprecated use service facade or appropriate factory method for use in tests.
     */
    @Deprecated
    public void save() throws ProductDefinitionException {
        try {
            getLegacyProductCategoryDao().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new ProductDefinitionException(e);
        }
    }

    public boolean hasDifferentName(String productCategoryName) {
        return !this.productCategoryName.equals(productCategoryName);
    }

    private LegacyProductCategoryDao getLegacyProductCategoryDao() {
      return ApplicationContextProvider.getBean(LegacyProductCategoryDao.class);
    }

    public void update(String name, String description, PrdCategoryStatus status) {
        this.productCategoryName = name;
        this.productCategoryDesc = description;
        this.prdCategoryStatus = new PrdCategoryStatusEntity(status);
    }
}