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

package org.mifos.application.admin.servicefacade;

import java.util.List;
import java.util.Locale;

import org.mifos.dto.domain.AcceptedPaymentTypeDto;
import org.mifos.dto.domain.AuditLogDto;
import org.mifos.dto.domain.CreateOrUpdateProductCategory;
import org.mifos.dto.domain.LoanProductRequest;
import org.mifos.dto.domain.MandatoryHiddenFieldsDto;
import org.mifos.dto.domain.OfficeLevelDto;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.dto.domain.ProductTypeDto;
import org.mifos.dto.domain.ReportCategoryDto;
import org.mifos.dto.domain.SavingsProductDto;
import org.mifos.dto.domain.UpdateConfiguredOfficeLevelRequest;
import org.mifos.dto.screen.LoanProductFormDto;
import org.mifos.dto.screen.ProductCategoryDetailsDto;
import org.mifos.dto.screen.ProductCategoryDisplayDto;
import org.mifos.dto.screen.ProductCategoryTypeDto;
import org.mifos.dto.screen.ProductConfigurationDto;
import org.mifos.dto.screen.ProductDisplayDto;
import org.mifos.dto.screen.ProductDto;
import org.mifos.dto.screen.ProductMixDetailsDto;
import org.mifos.dto.screen.SavingsProductFormDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface AdminServiceFacade {

    @PreAuthorize("isFullyAuthenticated()")
    ProductConfigurationDto retrieveProductConfiguration();

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_UPDATE_LATENESS_DORMANCY')")
    void updateProductConfiguration(ProductConfigurationDto productConfiguration);

    @PreAuthorize("isFullyAuthenticated()")
    OfficeLevelDto retrieveOfficeLevelsWithConfiguration();

    @PreAuthorize("isFullyAuthenticated()")
    void updateOfficeLevelHierarchies(UpdateConfiguredOfficeLevelRequest updateRequest);

    @PreAuthorize("isFullyAuthenticated()")
    List<ProductDisplayDto> retrieveLoanProducts();

    @PreAuthorize("isFullyAuthenticated()")
    List<ProductDisplayDto> retrieveSavingsProducts();

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_DEFINE_PRODUCT_MIX')")
    ProductMixDetailsDto retrieveProductMixDetails(Short prdOfferingId, String productType);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_DEFINE_PRODUCT_MIX')")
    ProductDto retrieveAllProductMix();

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_EDIT_PRODUCT_MIX')")
    List<ProductTypeDto> retrieveProductTypesApplicableToProductMix();

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_DEFINE_PRODUCT_MIX')")
    List<PrdOfferingDto> retrieveAllowedProductsForMix(Integer productTypeId, Integer productId);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_DEFINE_PRODUCT_MIX')")
    List<PrdOfferingDto> retrieveNotAllowedProductsForMix(Integer productTypeId, Integer productId);

    @PreAuthorize("isFullyAuthenticated() and hasAnyRole('ROLE_CAN_DEFINE_PRODUCT_MIX', 'ROLE_CAN_EDIT_PRODUCT_MIX')")
    void createOrUpdateProductMix(Integer productId, List<Integer> notAllowedProductIds);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_DEFINE_HIDDEN_MANDATORY_FIELDS')")
    MandatoryHiddenFieldsDto retrieveHiddenMandatoryFields();

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_DEFINE_HIDDEN_MANDATORY_FIELDS')")
    void updateHiddenMandatoryFields(MandatoryHiddenFieldsDto dto);

    @PreAuthorize("isFullyAuthenticated()")
    AcceptedPaymentTypeDto retrieveAcceptedPaymentTypes();

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_DEFINE_ACCEPTED_PAYMENT_TYPES')")
    void updateAcceptedPaymentTypes(String[] chosenAcceptedFees, String[] chosenAcceptedLoanDisbursements,
            String[] chosenAcceptedLoanRepayments, String[] chosenAcceptedSavingDeposits,
            String[] chosenAcceptedSavingWithdrawals);

    @PreAuthorize("isFullyAuthenticated()")
    ProductCategoryDisplayDto retrieveAllProductCategories();

    @PreAuthorize("isFullyAuthenticated()")
    ProductCategoryDetailsDto retrieveProductCateogry(String globalProductCategoryNumber);

    @PreAuthorize("isFullyAuthenticated()")
    List<ProductCategoryTypeDto> retrieveProductCategoryTypes();

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_CREATE_PRODUCT_CATEGORIES')")
    void createProductCategory(CreateOrUpdateProductCategory productCategory);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_EDIT_PRODUCT_CATEGORIES')")
    void updateProductCategory(CreateOrUpdateProductCategory productCategory);

    List<PrdOfferingDto> retrieveLoanProductsNotMixed();

    @PreAuthorize("isFullyAuthenticated()")
    LoanProductRequest retrieveLoanProductDetails(Integer productId);

    @PreAuthorize("isFullyAuthenticated()")
    SavingsProductDto retrieveSavingsProductDetails(Integer productId);

    @PreAuthorize("isFullyAuthenticated() and hasAnyRole('ROLE_CAN_CREATE_NEW_SAVINGS_PRODUCT', 'ROLE_CAN_EDIT_SAVINGS_PRODUCT')")
    SavingsProductFormDto retrieveSavingsProductFormReferenceData();

    @PreAuthorize("isFullyAuthenticated() and hasAnyRole('ROLE_CAN_CREATE_NEW_LOAN_PRODUCT', 'ROLE_CAN_EDIT_LOAN_PRODUCT')")
    LoanProductFormDto retrieveLoanProductFormReferenceData();

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_CREATE_NEW_SAVINGS_PRODUCT')")
    PrdOfferingDto createSavingsProduct(SavingsProductDto savingsProductRequest);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_CREATE_NEW_LOAN_PRODUCT')")
    PrdOfferingDto createLoanProduct(LoanProductRequest loanProduct);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_EDIT_SAVINGS_PRODUCT')")
    PrdOfferingDto updateSavingsProduct(SavingsProductDto savingsProduct);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_EDIT_LOAN_PRODUCT')")
    PrdOfferingDto updateLoanProduct(LoanProductRequest loanProductRequest);

    @PreAuthorize("isFullyAuthenticated()")
    List<AuditLogDto> retrieveSavingsProductAuditLogs(Integer productId);

    @PreAuthorize("isFullyAuthenticated()")
    List<AuditLogDto> retrieveLoanProductAuditLogs(Integer productId);

    @PreAuthorize("isFullyAuthenticated()")
    List<ReportCategoryDto> retrieveReportCategories();

    @PreAuthorize("isFullyAuthenticated()")
    void createReportsCategory(ReportCategoryDto reportCategory);

    @PreAuthorize("isFullyAuthenticated()")
    ReportCategoryDto retrieveReportCategory(Integer reportCategoryId);

    Locale retreiveLocaleFromConfiguration();

	String replaceSubstitutions(String message);
}