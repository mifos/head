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

import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.accounts.productdefinition.business.PrdOfferingBO;
import org.mifos.accounts.productdefinition.business.ProductCategoryBO;
import org.mifos.accounts.productdefinition.business.ProductTypeEntity;
import org.mifos.accounts.productdefinition.business.service.ProductCategoryBusinessService;
import org.mifos.accounts.productdefinition.business.service.ProductService;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.productdefinition.persistence.SavingsProductDao;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.productsmix.business.service.ProductMixBusinessService;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.application.master.business.LookUpEntity;
import org.mifos.config.persistence.ApplicationConfigurationDao;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.office.business.service.OfficeHierarchyService;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.domain.ConfigurableLookupLabelDto;
import org.mifos.dto.domain.GracePeriodDto;
import org.mifos.dto.domain.AccountStatusesLabelDto;
import org.mifos.dto.domain.OfficeLevelDto;
import org.mifos.dto.domain.UpdateConfiguredOfficeLevelRequest;
import org.mifos.dto.screen.ConfigureApplicationLabelsDto;
import org.mifos.dto.screen.ProductCategoryDto;
import org.mifos.dto.screen.ProductConfigurationDto;
import org.mifos.dto.screen.ProductDisplayDto;
import org.mifos.dto.screen.ProductDto;
import org.mifos.dto.screen.ProductMixDetailsDto;
import org.mifos.dto.screen.ProductMixDto;

public class AdminServiceFacadeWebTier implements AdminServiceFacade {

    private final ProductService productService;
    private final OfficeHierarchyService officeHierarchyService;
    private final LoanProductDao loanProductDao;
    private final SavingsProductDao savingsProductDao;
    private final OfficeDao officeDao;
    private final ApplicationConfigurationDao applicationConfigurationDao;

    public AdminServiceFacadeWebTier(ProductService productService, OfficeHierarchyService officeHierarchyService, LoanProductDao loanProductDao,
                                    SavingsProductDao savingsProductDao, OfficeDao officeDao, ApplicationConfigurationDao applicationConfigurationDao) {
        this.productService = productService;
        this.officeHierarchyService = officeHierarchyService;
        this.loanProductDao = loanProductDao;
        this.savingsProductDao = savingsProductDao;
        this.officeDao = officeDao;
        this.applicationConfigurationDao = applicationConfigurationDao;
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
    public List<ProductDisplayDto> retrieveLoanProducts() {

        List<Object[]> queryResult = this.loanProductDao.findAllLoanProducts();
        return productsToDto(queryResult);

    }

    @Override
    public List<ProductDisplayDto> retrieveSavingsProducts() {

        List<Object[]> queryResult = this.savingsProductDao.findAllSavingsProducts();
        return productsToDto(queryResult);

    }

    private List<ProductDisplayDto> productsToDto(final List<Object[]> queryResult) {

        if (queryResult.size() == 0) {
            return null;
        }

        List<ProductDisplayDto> products = new ArrayList<ProductDisplayDto>();
        Short prdOfferingId;
        String prdOfferingName;
        Short prdOfferingStatusId;
        String prdOfferingStatusName;

        for (Object[] row : queryResult) {
            prdOfferingId = (Short) row[0];
            prdOfferingName = (String) row[1];
            prdOfferingStatusId = (Short) row[2];
            prdOfferingStatusName = (String) row[3];
            ProductDisplayDto product = new ProductDisplayDto(prdOfferingId, prdOfferingName, prdOfferingStatusId,
                    prdOfferingStatusName);
            products.add(product);
        }
        return products;
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

    @Override
    public void updateApplicationLabels(ConfigureApplicationLabelsDto applicationLabels) {

        ConfigureApplicationLabelsDto currentLabels = retrieveConfigurableLabels();

        // 1. find labels that are different and return domain entity type for
        //  - OfficeLevelEntity
        //  - GracePeriodTypeEntity
        //  - LookUpEntity
        //  - LookUpEntity
        //
//        officeHierarchyService.updateApplicationLabels();
    }

    @Override
    public ConfigureApplicationLabelsDto retrieveConfigurableLabels() {
        OfficeLevelDto officeLevels = officeDao.findOfficeLevelsWithConfiguration();

        List<GracePeriodTypeEntity> gracePeriodTypes = applicationConfigurationDao.findGracePeriodTypes();
        GracePeriodDto gracePeriodDtos = assemble(gracePeriodTypes);

        List<LookUpEntity> lookupEntities = applicationConfigurationDao.findLookupValueTypes();
        ConfigurableLookupLabelDto lookupLabels = assemble(lookupEntities);

        List<AccountStateEntity> accountStateEntities = applicationConfigurationDao.findAllAccountStateEntities();
        AccountStatusesLabelDto accountStatusLabels = assemble(accountStateEntities);

        List<CustomerStatusEntity> customerStatuses = applicationConfigurationDao.findAllCustomerStatuses();
        for (CustomerStatusEntity customerStatus : customerStatuses) {
            if (customerStatus.getId().equals(CustomerStatus.CLIENT_HOLD.getValue())) {
                accountStatusLabels.setOnhold(customerStatus.getName());
            }
        }

        return new ConfigureApplicationLabelsDto(officeLevels, gracePeriodDtos, lookupLabels, accountStatusLabels);
    }

    private AccountStatusesLabelDto assemble(List<AccountStateEntity> accountStateEntities) {

        AccountStatusesLabelDto loanAccountLabel = new AccountStatusesLabelDto();

        for (AccountStateEntity accountState : accountStateEntities) {
            if (accountState.getId().equals(AccountState.LOAN_PARTIAL_APPLICATION.getValue())) {
                loanAccountLabel.setPartialApplication(accountState.getName());
            } else if (accountState.getId().equals(AccountState.LOAN_PENDING_APPROVAL.getValue())) {
                loanAccountLabel.setPendingApproval(accountState.getName());
            } else if (accountState.getId().equals(AccountState.LOAN_APPROVED.getValue())) {
                loanAccountLabel.setApproved(accountState.getName());
            } else if (accountState.getId().equals(AccountState.LOAN_CANCELLED.getValue())) {
                loanAccountLabel.setCancel(accountState.getName());
            } else if (accountState.getId().equals(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING.getValue())) {
                loanAccountLabel.setActiveInGoodStanding(accountState.getName());
            } else if (accountState.getId().equals(AccountState.LOAN_ACTIVE_IN_BAD_STANDING.getValue())) {
                loanAccountLabel.setActiveInBadStanding(accountState.getName());
            } else if (accountState.getId().equals(AccountState.LOAN_CLOSED_OBLIGATIONS_MET.getValue())) {
                loanAccountLabel.setClosedObligationMet(accountState.getName());
            } else if (accountState.getId().equals(AccountState.LOAN_CLOSED_WRITTEN_OFF.getValue())) {
                loanAccountLabel.setClosedWrittenOff(accountState.getName());
            } else if (accountState.getId().equals(AccountState.LOAN_CLOSED_RESCHEDULED.getValue())) {
                loanAccountLabel.setClosedRescheduled(accountState.getName());
            } else if (accountState.getId().equals(AccountState.SAVINGS_CLOSED.getValue())) {
                loanAccountLabel.setClosed(accountState.getName());
            } else if (accountState.getId().equals(AccountState.SAVINGS_INACTIVE.getValue())) {
                loanAccountLabel.setInActive(accountState.getName());
            } else if (accountState.getId().equals(AccountState.SAVINGS_ACTIVE.getValue())) {
                loanAccountLabel.setActive(accountState.getName());
            }
        }

        return loanAccountLabel;
    }

    private ConfigurableLookupLabelDto assemble(List<LookUpEntity> lookupEntities) {

        ConfigurableLookupLabelDto lookupLabels = new ConfigurableLookupLabelDto();

        for (LookUpEntity entity : lookupEntities) {
            if (entity.getEntityType().equals(ConfigurationConstants.CLIENT)) {
                lookupLabels.setClient(entity.findLabel());
            } else if (entity.getEntityType().equals(ConfigurationConstants.GROUP)) {
                lookupLabels.setGroup(entity.findLabel());
            } else if (entity.getEntityType().equals(ConfigurationConstants.CENTER)) {
                lookupLabels.setCenter(entity.findLabel());
            } else if (entity.getEntityType().equals(ConfigurationConstants.LOAN)) {
                lookupLabels.setLoans(entity.findLabel());
            } else if (entity.getEntityType().equals(ConfigurationConstants.SAVINGS)) {
                lookupLabels.setSavings(entity.findLabel());
            } else if (entity.getEntityType().equals(ConfigurationConstants.STATE)) {
                lookupLabels.setState(entity.findLabel());
            } else if (entity.getEntityType().equals(ConfigurationConstants.POSTAL_CODE)) {
                lookupLabels.setPostalCode(entity.findLabel());
            } else if (entity.getEntityType().equals(ConfigurationConstants.ETHINICITY)) {
                lookupLabels.setEthnicity(entity.findLabel());
            } else if (entity.getEntityType().equals(ConfigurationConstants.CITIZENSHIP)) {
                lookupLabels.setCitizenship(entity.findLabel());
            } else if (entity.getEntityType().equals(ConfigurationConstants.HANDICAPPED)) {
                lookupLabels.setHandicapped(entity.findLabel());
            } else if (entity.getEntityType().equals(ConfigurationConstants.GOVERNMENT_ID)) {
                lookupLabels.setGovtId(entity.findLabel());
            } else if (entity.getEntityType().equals(ConfigurationConstants.ADDRESS1)) {
                lookupLabels.setAddress1(entity.findLabel());
            } else if (entity.getEntityType().equals(ConfigurationConstants.ADDRESS2)) {
                lookupLabels.setAddress2(entity.findLabel());
            } else if (entity.getEntityType().equals(ConfigurationConstants.ADDRESS3)) {
                lookupLabels.setAddress3(entity.findLabel());
            } else if (entity.getEntityType().equals(ConfigurationConstants.INTEREST)) {
                lookupLabels.setInterest(entity.findLabel());
            } else if (entity.getEntityType().equals(ConfigurationConstants.EXTERNALID)) {
                lookupLabels.setExternalId(entity.findLabel());
            } else if (entity.getEntityType().equals(ConfigurationConstants.BULKENTRY)) {
                lookupLabels.setBulkEntry(entity.findLabel());
            }
        }

        return lookupLabels;
    }

    private GracePeriodDto assemble(List<GracePeriodTypeEntity> gracePeriodTypes) {

        GracePeriodDto gracePeriods = new GracePeriodDto();

        for (GracePeriodTypeEntity entity : gracePeriodTypes) {
            GraceType periodType = GraceType.fromInt(entity.getId());
            switch (periodType) {
            case NONE:
                gracePeriods.setNone(entity.getLookUpValue().getMessageText());
                break;
            case GRACEONALLREPAYMENTS:
                gracePeriods.setGraceOnAllRepayments(entity.getLookUpValue().getMessageText());
                break;
            case PRINCIPALONLYGRACE:
                gracePeriods.setPrincipalOnlyGrace(entity.getLookUpValue().getMessageText());
                break;
            default:
                break;
            }
        }

        return gracePeriods;
    }
}