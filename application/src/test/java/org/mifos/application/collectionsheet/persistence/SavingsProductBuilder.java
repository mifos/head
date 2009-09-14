/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
package org.mifos.application.collectionsheet.persistence;

import java.util.Date;

import org.joda.time.DateTime;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestCalcType;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

/**
 *
 */
public class SavingsProductBuilder {

    private final Double interestRate = Double.valueOf("2.0");
    private final SavingsType savingsType = SavingsType.VOLUNTARY;
    private final InterestCalcType interestCalcType = InterestCalcType.MINIMUM_BALANCE;
    
    // PRD_OFFERING FIELDS
    private final String globalProductNumber = "XXXXX-1111";
    private final Date startDate = new DateTime().minusDays(14).toDate();
    private final String name = "testProduct";
    private final String shortName = "VS2";
    private final Date createdDate = new DateTime().minusDays(14).toDate();
    private final Short createdByUserId = TestUtils.makeUserWithLocales().getId();
    private final GLCodeEntity depositGLCode = new GLCodeEntity(Short.valueOf("1"), "10000");
    private final GLCodeEntity interesetGLCode = new GLCodeEntity(Short.valueOf("2"), "11000");
    
    private final ApplicableTo applicableToCustomer = ApplicableTo.CENTERS;
    
    private ProductCategoryBO category = new ProductCategoryBO(Short.valueOf("1"), "savtest");
    
    public SavingsOfferingBO buildForUnitTests() {

        return build();
    }
    
    public SavingsOfferingBO buildForIntegrationTests() {
        category = (ProductCategoryBO) StaticHibernateUtil.getSessionTL().get(ProductCategoryBO.class,
                Short.valueOf("2"));
        
        return build();
    }

    private SavingsOfferingBO build() {
        final SavingsOfferingBO savingsProduct = new SavingsOfferingBO(savingsType, name, shortName,
                globalProductNumber, startDate, applicableToCustomer, category, interestCalcType, interestRate,
                depositGLCode, interesetGLCode, createdDate, createdByUserId);
        return savingsProduct;
    }
}
