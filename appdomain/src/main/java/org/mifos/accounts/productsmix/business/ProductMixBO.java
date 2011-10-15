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

package org.mifos.accounts.productsmix.business;

import org.mifos.accounts.productdefinition.business.PrdOfferingBO;
import org.mifos.accounts.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.accounts.productsmix.persistence.LegacyProductMixDao;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class encapsulate the product mix (Allowed / Not Allowed products)
 */

public class ProductMixBO extends AbstractBusinessObject {

    private final Integer prdOfferingMixId;
    private PrdOfferingBO prdOfferingId;
    private PrdOfferingBO prdOfferingNotAllowedId;

    private static final Logger logger = LoggerFactory.getLogger(ProductMixBO.class);

    public ProductMixBO() {
        this.prdOfferingMixId = null;
        this.prdOfferingId = null;
        this.prdOfferingNotAllowedId = null;
    }

    public ProductMixBO(PrdOfferingBO prdOfferingId, PrdOfferingBO prdOfferingNotAllowedId) {
        this.prdOfferingMixId = null;
        this.prdOfferingId = prdOfferingId;
        this.prdOfferingNotAllowedId = prdOfferingNotAllowedId;

    }

    public PrdOfferingBO getPrdOfferingId() {
        return prdOfferingId;
    }

    public void setPrdOfferingId(PrdOfferingBO prdOfferingId) {
        this.prdOfferingId = prdOfferingId;
    }

    public Integer getPrdOfferingMixId() {
        return prdOfferingMixId;
    }

    public PrdOfferingBO getPrdOfferingNotAllowedId() {
        return prdOfferingNotAllowedId;
    }

    public void setPrdOfferingNotAllowedId(PrdOfferingBO prdOfferingNotAllowedId) {
        this.prdOfferingNotAllowedId = prdOfferingNotAllowedId;
    }

    public void update() throws ProductDefinitionException {
        try {
            setUpdateDetails();
            ApplicationContextProvider.getBean(LegacyProductMixDao.class).createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new ProductDefinitionException(e);
        }
    }

    public void delete() throws ProductDefinitionException {
        try {
            ApplicationContextProvider.getBean(LegacyProductMixDao.class).delete(this);
        } catch (PersistenceException e) {
            throw new ProductDefinitionException(e);
        }
    }

    public void save() throws ProductDefinitionException {
        try {
            ApplicationContextProvider.getBean(LegacyProductMixDao.class).createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new ProductDefinitionException(e);
        }
    }

    public boolean doesPrdOfferingsCanCoexist(Short idPrdOff_A, Short idPrdOff_B) throws PersistenceException {
        try {
            return ApplicationContextProvider.getBean(LegacyProductMixDao.class).doesPrdOfferingsCanCoexist(idPrdOff_A, idPrdOff_B);
        } catch (PersistenceException e) {
            throw new PersistenceException(e);
        }
    }

}
