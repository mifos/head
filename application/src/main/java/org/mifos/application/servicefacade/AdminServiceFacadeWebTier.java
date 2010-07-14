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

package org.mifos.application.servicefacade;

import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.productdefinition.business.PrdOfferingBO;
import org.mifos.accounts.productdefinition.business.ProductCategoryBO;
import org.mifos.accounts.productdefinition.business.ProductTypeEntity;
import org.mifos.accounts.productdefinition.business.service.ProductCategoryBusinessService;
import org.mifos.accounts.productdefinition.business.service.ProductService;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.productdefinition.persistence.SavingsProductDao;
import org.mifos.accounts.productsmix.business.service.ProductMixBusinessService;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.customers.office.business.service.OfficeHierarchyService;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.dto.domain.OfficeLevelDto;
import org.mifos.dto.domain.UpdateConfiguredOfficeLevelRequest;
import org.mifos.dto.screen.LoanProductDto;
import org.mifos.dto.screen.ProductCategoryDto;
import org.mifos.dto.screen.ProductConfigurationDto;
import org.mifos.dto.screen.ProductDto;
import org.mifos.dto.screen.ProductMixDetailsDto;
import org.mifos.dto.screen.ProductMixDto;

public class AdminServiceFacadeWebTier implements AdminServiceFacade {

    private ProductService productService;
    private OfficeHierarchyService officeHierarchyService;
    private LoanProductDao loanProductDao;
    private SavingsProductDao savingsProductDao;
    private OfficeDao officeDao;


    public AdminServiceFacadeWebTier(ProductService productService, OfficeHierarchyService officeHierarchyService, LoanProductDao loanProductDao,
                                    SavingsProductDao savingsProductDao, OfficeDao officeDao) {
        this.productService = productService;
        this.officeHierarchyService = officeHierarchyService;
        this.loanProductDao = loanProductDao;
        this.savingsProductDao = savingsProductDao;
        this.officeDao = officeDao;
    }

    @Override
    public ProductConfigurationDto retrieveProductConfiguration() {
        ProductTypeEntity loanProductConfiguration = this.loanProductDao.findLoanProductConfiguration();
        ProductTypeEntity savingsProductConfiguration = this.savingsProductDao.findSavingsProductConfiguration();

        return new ProductConfigurationDto(loanProductConfiguration.getLatenessDays().intValue(),
                savingsProductConfiguration.getDormancyDays().intValue());
    }

    @Override
    public void updateProductConfiguration(ProductConfigurationDto productConfiguration) {

        ProductTypeEntity loanProductConfiguration = this.loanProductDao.findLoanProductConfiguration();
        ProductTypeEntity savingsProductConfiguration = this.savingsProductDao.findSavingsProductConfiguration();

        this.productService.updateLatenessAndDormancy(loanProductConfiguration, savingsProductConfiguration,
                productConfiguration);
    }

    @Override
    public List<LoanProductDto> retrieveLoanProducts() {

        List<Object[]> queryResult = this.loanProductDao.findAllLoanProducts();
        if (queryResult.size() == 0) {
            return null;
        }

        List<LoanProductDto> loanProducts = new ArrayList<LoanProductDto>();
        Short prdOfferingId;
        String prdOfferingName;
        Short prdOfferingStatusId;
        String prdOfferingStatusName;

        for (Object[] loanRow : queryResult) {
            prdOfferingId = (Short) loanRow[0];
            prdOfferingName = (String) loanRow[1];
            prdOfferingStatusId = (Short) loanRow[2];
            prdOfferingStatusName = (String) loanRow[3];
            LoanProductDto loanProduct = new LoanProductDto(prdOfferingId, prdOfferingName, prdOfferingStatusId,
                    prdOfferingStatusName);
            loanProducts.add(loanProduct);
        }
        return loanProducts;

    }

    @Override
    public OfficeLevelDto retrieveOfficeLevelsWithConfiguration() {
        return officeDao.findOfficeLevelsWithConfiguration();
    }

    @Override
    public void updateOfficeLevelHierarchies(UpdateConfiguredOfficeLevelRequest updateRequest) {
        officeHierarchyService.updateOfficeHierarchyConfiguration(updateRequest);
    }

    @Override
    public ProductDto retrieveAllProductMix() throws Exception {
        List<ProductCategoryBO> productCategoryList = new ProductCategoryBusinessService().getAllCategories();
        List<PrdOfferingBO> prdOfferingList = new ProductMixBusinessService().getPrdOfferingMix();

        List<ProductCategoryDto> pcList = new ArrayList<ProductCategoryDto>();
        for(ProductCategoryBO pcBO: productCategoryList) {
            ProductCategoryDto pcDto = new ProductCategoryDto(pcBO.getProductType().getProductTypeID(),
                                        pcBO.getProductType().getLookUpValue().getLookUpName());
            pcList.add(pcDto);
        }

        List<ProductMixDto> pmList = new ArrayList<ProductMixDto>();
        for(PrdOfferingBO poBO: prdOfferingList) {
            ProductMixDto pmDto = new ProductMixDto(poBO.getPrdCategory().getProductType().getProductTypeID(), poBO.getPrdOfferingId(),
                                    poBO.getPrdType().getProductTypeID(), poBO.getPrdOfferingName());
            pmList.add(pmDto);
        }

        ProductDto productDto = new ProductDto(pcList, pmList);
        return productDto;
    }

    @Override
    public ProductMixDetailsDto retrieveProductMixDetails(Short prdOfferingId, String productType) throws Exception {

        ProductMixBusinessService service = new ProductMixBusinessService();
        PrdOfferingBO prdOfferingBO = service.getPrdOfferingByID(prdOfferingId);
        List<PrdOfferingBO> allowedPrdOfferingList = service.getAllowedPrdOfferingsByType(prdOfferingId.toString(), productType);
        List<PrdOfferingBO> notAllowedPrdOfferingList = service.getNotAllowedPrdOfferingsByType(prdOfferingId.toString());

        List<String> allowedPrdOfferingNames = new ArrayList<String>();
        List<String> notAllowedPrdOfferingNames = new ArrayList<String>();
        for(PrdOfferingBO prdOffering: allowedPrdOfferingList) {
            allowedPrdOfferingNames.add(prdOffering.getPrdOfferingName());
        }
        for(PrdOfferingBO prdOffering: notAllowedPrdOfferingList) {
            notAllowedPrdOfferingNames.add(prdOffering.getPrdOfferingName());
        }

        ProductMixDetailsDto dto = new ProductMixDetailsDto(prdOfferingId, prdOfferingBO.getPrdOfferingName(),
                                    prdOfferingBO.getPrdType().getProductTypeID(), allowedPrdOfferingNames, notAllowedPrdOfferingNames);
        return dto;
    }
}