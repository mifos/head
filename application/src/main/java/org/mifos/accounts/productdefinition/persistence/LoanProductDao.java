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
package org.mifos.accounts.productdefinition.persistence;

import java.util.List;

import org.mifos.accounts.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.PrdApplicableMasterEntity;
import org.mifos.accounts.productdefinition.business.ProductCategoryBO;
import org.mifos.accounts.productdefinition.business.ProductTypeEntity;
import org.mifos.accounts.productdefinition.business.RecommendedAmntUnitEntity;
import org.mifos.accounts.productdefinition.business.SavingsTypeEntity;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.customers.business.CustomerLevelEntity;

/**
 *
 */
public interface LoanProductDao {

    LoanOfferingBO findById(Integer productId);

    List<LoanOfferingBO> findActiveLoanProductsApplicableToCustomerLevel(CustomerLevelEntity customerLevel);

    List<ValueListElement> findAllLoanPurposes();

    ProductTypeEntity findLoanProductConfiguration();

    void save(ProductTypeEntity loanProductConfiguration);

    void save(ProductCategoryBO productCategoryBO);

    void save(LoanOfferingBO loanProduct);

    List<Object[]> findAllLoanProducts();

    List<PrdApplicableMasterEntity> retrieveLoanApplicableProductCategories();

    List<PrdApplicableMasterEntity> retrieveSavingsApplicableProductCategories();

    List<GracePeriodTypeEntity> retrieveGracePeriodTypes();

    List<InterestTypesEntity> retrieveInterestTypes();

    InterestTypesEntity retrieveInterestType(InterestType interestType);

    InterestTypesEntity findInterestType(InterestType interestType);

    PrdApplicableMasterEntity findApplicableProductType(ApplicableTo applicableTo);

    GracePeriodTypeEntity findGracePeriodType(GraceType gracePeriodType);

    ProductCategoryBO findActiveProductCategoryById(Integer category);

    ProductCategoryBO findProductCategoryByNameAndType(String productCategoryName, Short productTypeEntityId);

    void validateNameIsAvailableForCategory(String productCategoryName, Short productTypeEntityId);

    ProductCategoryBO findProductCategoryByGlobalNum(String globalNumber);

    List<SavingsTypeEntity> retrieveSavingsTypes();

    SavingsTypeEntity retrieveSavingsType(SavingsType savingsType);

    List<RecommendedAmntUnitEntity> retrieveRecommendedAmountTypes();

    RecommendedAmntUnitEntity retrieveRecommendedAmountType(RecommendedAmountUnit recommendedAmountType);
}