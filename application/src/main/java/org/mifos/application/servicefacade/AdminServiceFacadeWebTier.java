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
import java.util.Set;

import org.mifos.accounts.acceptedpaymenttype.business.AcceptedPaymentType;
import org.mifos.accounts.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
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
import org.mifos.application.master.business.LookUpLabelEntity;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.config.ClientRules;
import org.mifos.config.persistence.ApplicationConfigurationDao;
import org.mifos.config.persistence.ApplicationConfigurationPersistence;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.config.util.helpers.HiddenMandatoryFieldNamesConstants;
import org.mifos.config.util.helpers.LabelConfigurations;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.office.business.OfficeLevelEntity;
import org.mifos.customers.office.business.service.MandatoryHiddenFieldService;
import org.mifos.customers.office.business.service.OfficeHierarchyService;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.domain.AcceptedPaymentTypeDto;
import org.mifos.dto.domain.AccountStatusesLabelDto;
import org.mifos.dto.domain.ConfigurableLookupLabelDto;
import org.mifos.dto.domain.GracePeriodDto;
import org.mifos.dto.domain.MandatoryHiddenFieldsDto;
import org.mifos.dto.domain.OfficeLevelDto;
import org.mifos.dto.domain.UpdateConfiguredOfficeLevelRequest;
import org.mifos.dto.screen.ConfigureApplicationLabelsDto;
import org.mifos.dto.screen.PaymentTypeDto;
import org.mifos.dto.screen.ProductCategoryDto;
import org.mifos.dto.screen.ProductConfigurationDto;
import org.mifos.dto.screen.ProductDisplayDto;
import org.mifos.dto.screen.ProductDto;
import org.mifos.dto.screen.ProductMixDetailsDto;
import org.mifos.dto.screen.ProductMixDto;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.persistence.FieldConfigurationPersistence;
import org.mifos.framework.exceptions.ServiceException;

public class AdminServiceFacadeWebTier implements AdminServiceFacade {

    private final ProductService productService;
    private final OfficeHierarchyService officeHierarchyService;
    private final MandatoryHiddenFieldService mandatoryHiddenFieldService;
    private final LoanProductDao loanProductDao;
    private final SavingsProductDao savingsProductDao;
    private final OfficeDao officeDao;
    private final ApplicationConfigurationDao applicationConfigurationDao;



    public AdminServiceFacadeWebTier(ProductService productService, OfficeHierarchyService officeHierarchyService,
                                    MandatoryHiddenFieldService mandatoryHiddenFieldService, LoanProductDao loanProductDao,
                                    SavingsProductDao savingsProductDao, OfficeDao officeDao, ApplicationConfigurationDao applicationConfigurationDao) {
        this.productService = productService;
        this.officeHierarchyService = officeHierarchyService;
        this.mandatoryHiddenFieldService = mandatoryHiddenFieldService;
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
    public void updateApplicationLabels(ConfigureApplicationLabelsDto updatedLabels) {

        ConfigureApplicationLabelsDto currentLabels = retrieveConfigurableLabels();

        List<OfficeLevelEntity> changedOfficeLabels = findDifferentOfficeLevelEntitys(currentLabels.getOfficeLevels(), updatedLabels.getOfficeLevels());
        List<LookUpEntity> lookupEntities = findDifferentLookupEntities(currentLabels.getLookupLabels(), updatedLabels.getLookupLabels());
        List<GracePeriodTypeEntity> gracePeriods = findDifferentGracePeriodTypes(currentLabels.getGracePeriodDto(), updatedLabels.getGracePeriodDto());

        List<LookUpValueEntity> accountStatuses = findAccountStates(updatedLabels.getAccountStatusLabels());

        officeHierarchyService.updateApplicationLabels(changedOfficeLabels, lookupEntities, gracePeriods, accountStatuses);
    }

    private List<LookUpValueEntity> findAccountStates(AccountStatusesLabelDto updatedDetails) {
        List<LookUpValueEntity> entitiesForUpdate = new ArrayList<LookUpValueEntity>();

        List<LookUpValueEntity> accountStateEntities = new ApplicationConfigurationPersistence().getLookupValues();
        for (LookUpValueEntity value : accountStateEntities) {

            if (isPartialApplicationLookup(value)) {
                value.update(updatedDetails.getPartialApplication());
                entitiesForUpdate.add(value);
            }

            if (isPendingApprovalLookup(value)) {
                value.update(updatedDetails.getPendingApproval());
                entitiesForUpdate.add(value);
            }

            if (value.getLookUpId().equals(LabelConfigurations.LOAN_APPROVED.getValue())) {
                value.update(updatedDetails.getApproved());
                entitiesForUpdate.add(value);
            }

            if (isCancelLookup(value)) {
                value.update(updatedDetails.getCancel());
                entitiesForUpdate.add(value);
            }

            if (isClosedLookup(value)) {
                value.update(updatedDetails.getClosed());
                entitiesForUpdate.add(value);
            }

            if (isOnHold(value)) {
                value.update(updatedDetails.getOnhold());
                entitiesForUpdate.add(value);
            }

            if (isActiveLookup(value)) {
                value.update(updatedDetails.getActive());
                entitiesForUpdate.add(value);
            }

            if (isInActiveLookup(value)) {
                value.update(updatedDetails.getInActive());
                entitiesForUpdate.add(value);
            }

            if (value.getLookUpId().equals(LabelConfigurations.LOAN_ACTIVE_GOOD_STANDING.getValue())) {
                value.update(updatedDetails.getActiveInGoodStanding());
                entitiesForUpdate.add(value);
            }

            if (value.getLookUpId().equals(LabelConfigurations.LOAN_ACTIVE_BAD_STANDING.getValue())) {
                value.update(updatedDetails.getActiveInBadStanding());
                entitiesForUpdate.add(value);
            }

            if (value.getLookUpId().equals(LabelConfigurations.LOAN_CLOSED_OBLIGATION_MET.getValue())) {
                value.update(updatedDetails.getClosedObligationMet());
                entitiesForUpdate.add(value);
            }

            if (value.getLookUpId().equals(LabelConfigurations.LOAN_CLOSED_RESCHEDULE.getValue())) {
                value.update(updatedDetails.getClosedRescheduled());
                entitiesForUpdate.add(value);
            }

            if (value.getLookUpId().equals(LabelConfigurations.LOAN_CLOSED_WRITTEN_OFF.getValue())) {
                value.update(updatedDetails.getClosedWrittenOff());
                entitiesForUpdate.add(value);
            }

            // FIXME - keithw - not added when refactoring to spring mvc away from struts action.
//            if (value.getLookUpId().equals(LabelConfigurations.FEE_CATEGORY_CLIENT.getValue())) {
//                name = labelConfigurationActionForm.getClient();
//            } else if (value.getLookUpId().equals(LabelConfigurations.FEE_CATEGORY_GROUP.getValue())) {
//                name = labelConfigurationActionForm.getGroup();
//            } else if (value.getLookUpId().equals(LabelConfigurations.FEE_CATEGORY_CENTER.getValue())
//                    || value.getLookUpId().equals(Integer.valueOf("499"))) {
//                name = labelConfigurationActionForm.getCenter();
//            } else if (isLoanLookup(value)) {
//                name = labelConfigurationActionForm.getLoans();
//            } else if (value.getLookUpId().equals(Integer.valueOf("55"))
//                    || value.getLookUpId().equals(Integer.valueOf("87"))) {
//                name = labelConfigurationActionForm.getSavings();
//            }
        }

        return entitiesForUpdate;
    }

    private boolean isOnHold(LookUpValueEntity value) {
        return value.getLookUpId().equals(LabelConfigurations.CLIENT_ON_HOLD.getValue())
        || value.getLookUpId().equals(LabelConfigurations.GROUP_ON_HOLD.getValue());
    }

    private boolean isClosedLookup(LookUpValueEntity value) {
        return value.getLookUpId().equals(LabelConfigurations.CLIENT_CLOSED.getValue())
                || value.getLookUpId().equals(LabelConfigurations.GROUP_CLOSED.getValue())
                || value.getLookUpId().equals(Integer.valueOf("117"))
                || value.getLookUpId().equals(LabelConfigurations.SAVINGS_CLOSED.getValue());
    }

    private boolean isCancelLookup(LookUpValueEntity value) {
        return value.getLookUpId().equals(LabelConfigurations.CLIENT_CANCEL.getValue())
                || value.getLookUpId().equals(LabelConfigurations.GROUP_CANCEL.getValue())
                || value.getLookUpId().equals(LabelConfigurations.LOAN_CANCEL.getValue())
                || value.getLookUpId().equals(LabelConfigurations.SAVINGS_CANCEL.getValue());
    }

    private boolean isPendingApprovalLookup(LookUpValueEntity value) {
        return value.getLookUpId().equals(LabelConfigurations.CLIENT_PENDING_APPROVAL.getValue())
                || value.getLookUpId().equals(LabelConfigurations.GROUP_PENDING_APPROVAL.getValue())
                || value.getLookUpId().equals(LabelConfigurations.LOAN_PENDING_APPROVAL.getValue())
                || value.getLookUpId().equals(LabelConfigurations.SAVINGS_PENDING_APPROVAL.getValue());
    }

    private boolean isActiveLookup(LookUpValueEntity value) {
        return value.getLookUpId().equals(LabelConfigurations.CLIENT_ACTIVE.getValue())
                || value.getLookUpId().equals(LabelConfigurations.GROUP_ACTIVE.getValue())
                || value.getLookUpId().equals(LabelConfigurations.CENTER_ACTIVE.getValue())
                || value.getLookUpId().equals(LabelConfigurations.OFFICE_ACTIVE.getValue())
                || value.getLookUpId().equals(Integer.valueOf("26"))
                || value.getLookUpId().equals(Integer.valueOf("51"))
                || value.getLookUpId().equals(LabelConfigurations.PRD_CATEGORY_ACTIVE.getValue())
                || value.getLookUpId().equals(LabelConfigurations.PRD_ACTIVE.getValue())
                || value.getLookUpId().equals(LabelConfigurations.PERSONNEL_ACTIVE.getValue())
                || value.getLookUpId().equals(LabelConfigurations.FEE_ACTIVE.getValue())
                || value.getLookUpId().equals(LabelConfigurations.SAVINGS_ACTIVE.getValue());
    }

    private boolean isInActiveLookup(LookUpValueEntity value) {
        return value.getLookUpId().equals(LabelConfigurations.CENTER_INACTIVE.getValue())
                || value.getLookUpId().equals(LabelConfigurations.OFFICE_INACTIVE.getValue())
                || value.getLookUpId().equals(Integer.valueOf("27"))
                || value.getLookUpId().equals(Integer.valueOf("52"))
                || value.getLookUpId().equals(LabelConfigurations.PRD_CATEGORY_INACTIVE.getValue())
                || value.getLookUpId().equals(LabelConfigurations.PRD_INACTIVE.getValue())
                || value.getLookUpId().equals(LabelConfigurations.PERSONNEL_INACTIVE.getValue())
                || value.getLookUpId().equals(LabelConfigurations.FEE_INACTIVE.getValue())
                || value.getLookUpId().equals(LabelConfigurations.SAVINGS_INACTIVE.getValue());
    }

    private boolean isPartialApplicationLookup(LookUpValueEntity value) {
        return value.getLookUpId().equals(LabelConfigurations.CLIENT_PARTIAL_APPLICATION.getValue())
                || value.getLookUpId().equals(LabelConfigurations.GROUP_PARTIAL_APPLICATION.getValue())
                || value.getLookUpId().equals(LabelConfigurations.LOAN_PARTIAL_APPLICATION.getValue())
                || value.getLookUpId().equals(LabelConfigurations.SAVINGS_PARTIAL_APPLICATION.getValue());
    }

    private List<GracePeriodTypeEntity> findDifferentGracePeriodTypes(GracePeriodDto existingDetails, GracePeriodDto updatedDetails) {

        List<GracePeriodTypeEntity> gracePeriodForUpdate = new ArrayList<GracePeriodTypeEntity>();

        List<GracePeriodTypeEntity> gracePeriodTypes = applicationConfigurationDao.findGracePeriodTypes();
        for (GracePeriodTypeEntity gracePeriod : gracePeriodTypes) {

            GraceType graceType = GraceType.fromInt(gracePeriod.getId());
            switch (graceType) {
            case NONE:
                if (!existingDetails.getNone().equals(updatedDetails.getNone())) {
                    gracePeriod.update(updatedDetails.getNone());
                    gracePeriodForUpdate.add(gracePeriod);
                }
                break;
            case GRACEONALLREPAYMENTS:
                if (!existingDetails.getGraceOnAllRepayments().equals(updatedDetails.getGraceOnAllRepayments())) {
                    gracePeriod.update(updatedDetails.getGraceOnAllRepayments());
                    gracePeriodForUpdate.add(gracePeriod);
                }
                break;
            case PRINCIPALONLYGRACE:
                if (!existingDetails.getPrincipalOnlyGrace().equals(updatedDetails.getPrincipalOnlyGrace())) {
                    gracePeriod.update(updatedDetails.getPrincipalOnlyGrace());
                    gracePeriodForUpdate.add(gracePeriod);
                }
                break;
            default:
                break;
            }
        }

        return gracePeriodForUpdate;
    }

    private List<LookUpEntity> findDifferentLookupEntities(ConfigurableLookupLabelDto existingDetails, ConfigurableLookupLabelDto updatedDetails) {

        List<LookUpEntity> lookupEntitiesForUpdate = new ArrayList<LookUpEntity>();

            if (!existingDetails.getClient().equals(updatedDetails.getClient())) {
                LookUpEntity entity = applicationConfigurationDao.findLookupValueByEntityType(ConfigurationConstants.CLIENT);
                Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
                for (LookUpLabelEntity label : labels) {
                    label.setLabelName(updatedDetails.getClient());
                }
                lookupEntitiesForUpdate.add(entity);
            }

            if (!existingDetails.getGroup().equals(updatedDetails.getGroup())) {
                LookUpEntity entity = applicationConfigurationDao.findLookupValueByEntityType(ConfigurationConstants.GROUP);
                Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
                for (LookUpLabelEntity label : labels) {
                    label.setLabelName(updatedDetails.getGroup());
                }
                lookupEntitiesForUpdate.add(entity);
            }

            if (!existingDetails.getCenter().equals(updatedDetails.getCenter())) {
                LookUpEntity entity = applicationConfigurationDao.findLookupValueByEntityType(ConfigurationConstants.CENTER);
                Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
                for (LookUpLabelEntity label : labels) {
                    label.setLabelName(updatedDetails.getCenter());
                }
                lookupEntitiesForUpdate.add(entity);
            }

            if (!existingDetails.getLoans().equals(updatedDetails.getLoans())) {
                LookUpEntity entity = applicationConfigurationDao.findLookupValueByEntityType(ConfigurationConstants.LOAN);
                Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
                for (LookUpLabelEntity label : labels) {
                    label.setLabelName(updatedDetails.getLoans());
                }
                lookupEntitiesForUpdate.add(entity);
            }

            if (!existingDetails.getSavings().equals(updatedDetails.getSavings())) {
                LookUpEntity entity = applicationConfigurationDao.findLookupValueByEntityType(ConfigurationConstants.SAVINGS);
                Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
                for (LookUpLabelEntity label : labels) {
                    label.setLabelName(updatedDetails.getSavings());
                }
                lookupEntitiesForUpdate.add(entity);
            }

            if (!existingDetails.getState().equals(updatedDetails.getState())) {
                LookUpEntity entity = applicationConfigurationDao.findLookupValueByEntityType(ConfigurationConstants.STATE);
                Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
                for (LookUpLabelEntity label : labels) {
                    label.setLabelName(updatedDetails.getState());
                }
                lookupEntitiesForUpdate.add(entity);
            }

            if (!existingDetails.getPostalCode().equals(updatedDetails.getPostalCode())) {
                LookUpEntity entity = applicationConfigurationDao.findLookupValueByEntityType(ConfigurationConstants.POSTAL_CODE);
                Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
                for (LookUpLabelEntity label : labels) {
                    label.setLabelName(updatedDetails.getPostalCode());
                }
                lookupEntitiesForUpdate.add(entity);
            }

            if (!existingDetails.getEthnicity().equals(updatedDetails.getEthnicity())) {
                LookUpEntity entity = applicationConfigurationDao.findLookupValueByEntityType(ConfigurationConstants.ETHINICITY);
                Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
                for (LookUpLabelEntity label : labels) {
                    label.setLabelName(updatedDetails.getEthnicity());
                }
                lookupEntitiesForUpdate.add(entity);
            }

            if (!existingDetails.getCitizenship().equals(updatedDetails.getCitizenship())) {
                LookUpEntity entity = applicationConfigurationDao.findLookupValueByEntityType(ConfigurationConstants.CITIZENSHIP);
                Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
                for (LookUpLabelEntity label : labels) {
                    label.setLabelName(updatedDetails.getCitizenship());
                }
                lookupEntitiesForUpdate.add(entity);
            }

            if (!existingDetails.getHandicapped().equals(updatedDetails.getHandicapped())) {
                LookUpEntity entity = applicationConfigurationDao.findLookupValueByEntityType(ConfigurationConstants.HANDICAPPED);
                Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
                for (LookUpLabelEntity label : labels) {
                    label.setLabelName(updatedDetails.getHandicapped());
                }
                lookupEntitiesForUpdate.add(entity);
            }

            if (!existingDetails.getGovtId().equals(updatedDetails.getGovtId())) {
                LookUpEntity entity = applicationConfigurationDao.findLookupValueByEntityType(ConfigurationConstants.GOVERNMENT_ID);
                Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
                for (LookUpLabelEntity label : labels) {
                    label.setLabelName(updatedDetails.getGovtId());
                }
                lookupEntitiesForUpdate.add(entity);
            }

            if (!existingDetails.getAddress1().equals(updatedDetails.getAddress1())) {
                LookUpEntity entity = applicationConfigurationDao.findLookupValueByEntityType(ConfigurationConstants.ADDRESS1);
                Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
                for (LookUpLabelEntity label : labels) {
                    label.setLabelName(updatedDetails.getAddress1());
                }
                lookupEntitiesForUpdate.add(entity);
            }

            if (!existingDetails.getAddress2().equals(updatedDetails.getAddress2())) {
                LookUpEntity entity = applicationConfigurationDao.findLookupValueByEntityType(ConfigurationConstants.ADDRESS2);
                Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
                for (LookUpLabelEntity label : labels) {
                    label.setLabelName(updatedDetails.getAddress2());
                }
                lookupEntitiesForUpdate.add(entity);
            }

            if (!existingDetails.getAddress3().equals(updatedDetails.getAddress3())) {
                LookUpEntity entity = applicationConfigurationDao.findLookupValueByEntityType(ConfigurationConstants.ADDRESS3);
                Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
                for (LookUpLabelEntity label : labels) {
                    label.setLabelName(updatedDetails.getAddress3());
                }
                lookupEntitiesForUpdate.add(entity);
            }

            if (!existingDetails.getInterest().equals(updatedDetails.getInterest())) {
                LookUpEntity entity = applicationConfigurationDao.findLookupValueByEntityType(ConfigurationConstants.INTEREST);
                Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
                for (LookUpLabelEntity label : labels) {
                    label.setLabelName(updatedDetails.getInterest());
                }
                lookupEntitiesForUpdate.add(entity);
            }

            if (!existingDetails.getExternalId().equals(updatedDetails.getExternalId())) {
                LookUpEntity entity = applicationConfigurationDao.findLookupValueByEntityType(ConfigurationConstants.EXTERNALID);
                Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
                for (LookUpLabelEntity label : labels) {
                    label.setLabelName(updatedDetails.getExternalId());
                }
                lookupEntitiesForUpdate.add(entity);
            }

            if (!existingDetails.getBulkEntry().equals(updatedDetails.getBulkEntry())) {
                LookUpEntity entity = applicationConfigurationDao.findLookupValueByEntityType(ConfigurationConstants.BULKENTRY);
                Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
                for (LookUpLabelEntity label : labels) {
                    label.setLabelName(updatedDetails.getBulkEntry());
                }
                lookupEntitiesForUpdate.add(entity);
            }

            return lookupEntitiesForUpdate;
    }

    private List<OfficeLevelEntity> findDifferentOfficeLevelEntitys(OfficeLevelDto existingDetails, OfficeLevelDto updatedDetails) {

        List<OfficeLevelEntity> officeLevels = new ArrayList<OfficeLevelEntity>();

        if (!existingDetails.getHeadOfficeNameKey().equals(updatedDetails.getHeadOfficeNameKey())) {
            OfficeLevelEntity entity = officeDao.retrieveOfficeLevel(OfficeLevel.HEADOFFICE);
            entity.update(updatedDetails.getHeadOfficeNameKey());
            officeLevels.add(entity);
        }

        if (!existingDetails.getRegionalOfficeNameKey().equals(updatedDetails.getRegionalOfficeNameKey())) {
            OfficeLevelEntity entity = officeDao.retrieveOfficeLevel(OfficeLevel.REGIONALOFFICE);
            entity.update(updatedDetails.getRegionalOfficeNameKey());
            officeLevels.add(entity);
        }

        if (!existingDetails.getSubRegionalOfficeNameKey().equals(updatedDetails.getSubRegionalOfficeNameKey())) {
            OfficeLevelEntity entity = officeDao.retrieveOfficeLevel(OfficeLevel.SUBREGIONALOFFICE);
            entity.update(updatedDetails.getSubRegionalOfficeNameKey());
            officeLevels.add(entity);
        }

        if (!existingDetails.getAreaOfficeNameKey().equals(updatedDetails.getAreaOfficeNameKey())) {
            OfficeLevelEntity entity = officeDao.retrieveOfficeLevel(OfficeLevel.AREAOFFICE);
            entity.update(updatedDetails.getAreaOfficeNameKey());
            officeLevels.add(entity);
        }

        if (!existingDetails.getBranchOfficeNameKey().equals(updatedDetails.getBranchOfficeNameKey())) {
            OfficeLevelEntity entity = officeDao.retrieveOfficeLevel(OfficeLevel.BRANCHOFFICE);
            entity.update(updatedDetails.getBranchOfficeNameKey());
            officeLevels.add(entity);
        }

        return officeLevels;
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

    public MandatoryHiddenFieldsDto retrieveHiddenMandatoryFields() throws Exception {
        List<FieldConfigurationEntity> confFieldList = new FieldConfigurationPersistence().getAllConfigurationFieldList();
        MandatoryHiddenFieldsDto dto = new MandatoryHiddenFieldsDto();
        populateDto(dto, confFieldList);
        dto.setFamilyDetailsRequired(ClientRules.isFamilyDetailsRequired());
        return dto;
    }

    private void populateDto(MandatoryHiddenFieldsDto dto, List<FieldConfigurationEntity> confFieldList) {
        if (confFieldList != null && confFieldList.size() > 0) {
            for (FieldConfigurationEntity fieldConfiguration : confFieldList) {
                if (fieldConfiguration.getEntityType() == EntityType.CLIENT) {
                    populateClientDetails(dto, fieldConfiguration);
                } else if (fieldConfiguration.getEntityType() == EntityType.GROUP) {
                    populateGrouptDetails(dto, fieldConfiguration);
                } else {
                    populateSystemFields(dto, fieldConfiguration);
                }
            }
        }
    }

    private void populateClientDetails(MandatoryHiddenFieldsDto dto, FieldConfigurationEntity fieldConfiguration) {
        if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.MIDDLE_NAME)) {
            dto.setHideClientMiddleName(getBooleanValue(fieldConfiguration.getHiddenFlag()));
            dto.setMandatoryClientMiddleName(getBooleanValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.SECOND_LAST_NAME)) {
            dto.setHideClientSecondLastName(getBooleanValue(fieldConfiguration.getHiddenFlag()));
            dto.setMandatoryClientSecondLastName(getBooleanValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.GOVERNMENT_ID)) {
            dto.setHideClientGovtId(getBooleanValue(fieldConfiguration.getHiddenFlag()));
            dto.setMandatoryClientGovtId(getBooleanValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.MARITAL_STATUS)) {
            dto.setMandatoryMaritalStatus(getBooleanValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.POVERTY_STATUS)) {
            dto.setHideClientPovertyStatus(getBooleanValue(fieldConfiguration.getHiddenFlag()));
            dto.setMandatoryClientPovertyStatus(getBooleanValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(
                HiddenMandatoryFieldNamesConstants.SPOUSE_FATHER_INFORMATION)) {
            dto.setHideClientSpouseFatherInformation(getBooleanValue(fieldConfiguration.getHiddenFlag()));
            dto.setMandatoryClientSpouseFatherInformation(getBooleanValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(
                HiddenMandatoryFieldNamesConstants.FAMILY_DETAILS)) {
            dto.setMandatoryClientFamilyDetails(getBooleanValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(
                HiddenMandatoryFieldNamesConstants.SPOUSE_FATHER_MIDDLE_NAME)) {
            dto.setHideClientSpouseFatherMiddleName(getBooleanValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(
                HiddenMandatoryFieldNamesConstants.SPOUSE_FATHER_SECOND_LAST_NAME)) {
            dto.setHideClientSpouseFatherSecondLastName(getBooleanValue(fieldConfiguration.getHiddenFlag()));
            dto.setMandatoryClientSpouseFatherSecondLastName(getBooleanValue(fieldConfiguration
                    .getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.PHONE_NUMBER)) {
            dto.setHideClientPhone(getBooleanValue(fieldConfiguration.getHiddenFlag()));
            dto.setMandatoryClientPhone(getBooleanValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.TRAINED)) {
            dto.setHideClientTrained(getBooleanValue(fieldConfiguration.getHiddenFlag()));
            dto.setMandatoryClientTrained(getBooleanValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.TRAINED_DATE)) {
            dto.setMandatoryClientTrainedOn(getBooleanValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.BUSINESS_ACTIVITIES)) {
            dto.setHideClientBusinessWorkActivities(getBooleanValue(fieldConfiguration.getHiddenFlag()));
            dto.setMandatoryClientBusinessWorkActivities(getBooleanValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.NUMBER_OF_CHILDREN)) {
            dto.setMandatoryNumberOfChildren(getBooleanValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ETHINICITY)) {
            dto.setHideSystemEthnicity(getBooleanValue(fieldConfiguration.getHiddenFlag()));
            dto.setMandatorySystemEthnicity(getBooleanValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.CITIZENSHIP)) {
            dto.setHideSystemCitizenShip(getBooleanValue(fieldConfiguration.getHiddenFlag()));
            dto.setMandatorySystemCitizenShip(getBooleanValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.HANDICAPPED)) {
            dto.setHideSystemHandicapped(getBooleanValue(fieldConfiguration.getHiddenFlag()));
            dto.setMandatorySystemHandicapped(getBooleanValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.EDUCATION_LEVEL)) {
            dto.setHideSystemEducationLevel(getBooleanValue(fieldConfiguration.getHiddenFlag()));
            dto.setMandatorySystemEducationLevel(getBooleanValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.PHOTO)) {
            dto.setHideSystemPhoto(getBooleanValue(fieldConfiguration.getHiddenFlag()));
            dto.setMandatorySystemPhoto(getBooleanValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS1)) {
            dto.setMandatorySystemAddress1(getBooleanValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS2)) {
            dto.setHideSystemAddress2(getBooleanValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS3)) {
            dto.setHideSystemAddress3(getBooleanValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.CITY)) {
            dto.setHideSystemCity(getBooleanValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.STATE)) {
            dto.setHideSystemState(getBooleanValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.COUNTRY)) {
            dto.setHideSystemCountry(getBooleanValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.POSTAL_CODE)) {
            dto.setHideSystemPostalCode(getBooleanValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ASSIGN_CLIENTS)) {
            dto.setHideSystemAssignClientPostions(getBooleanValue(fieldConfiguration.getHiddenFlag()));
        }
    }

    private void populateGrouptDetails(MandatoryHiddenFieldsDto dto, FieldConfigurationEntity fieldConfiguration) {
        if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.TRAINED)) {
            dto.setHideGroupTrained(getBooleanValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS1)) {
            dto.setMandatorySystemAddress1(getBooleanValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS2)) {
            dto.setHideSystemAddress2(getBooleanValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS3)) {
            dto.setHideSystemAddress3(getBooleanValue(fieldConfiguration.getHiddenFlag()));
        }

    }

    private void populateSystemFields(MandatoryHiddenFieldsDto dto, FieldConfigurationEntity fieldConfiguration) {
        if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.EXTERNAL_ID)) {
            dto.setHideSystemExternalId(getBooleanValue(fieldConfiguration.getHiddenFlag()));
            dto.setMandatorySystemExternalId(getBooleanValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.RECEIPT_ID)) {
            dto.setHideSystemReceiptIdDate(getBooleanValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.PURPOSE_OF_LOAN)) {
            dto.setMandatoryLoanAccountPurpose(getBooleanValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.SOURCE_OF_FUND)) {
            dto.setMandatoryLoanSourceOfFund(getBooleanValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.COLLATERAL_TYPE)) {
            dto.setHideSystemCollateralTypeNotes(getBooleanValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS1)) {
            dto.setMandatorySystemAddress1(getBooleanValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS2)) {
            dto.setHideSystemAddress2(getBooleanValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS3)) {
            dto.setHideSystemAddress3(getBooleanValue(fieldConfiguration.getHiddenFlag()));
        }
    }

    private boolean getBooleanValue(Short s) {
        return ((s != null)&&(s != 0))? true: false;
    }

    @Override
    public void updateHiddenMandatoryFields(MandatoryHiddenFieldsDto dto) throws Exception {
        List<FieldConfigurationEntity> confFieldList = new FieldConfigurationPersistence().getAllConfigurationFieldList();
        mandatoryHiddenFieldService.updateMandatoryHiddenFields(dto, confFieldList);
    }

    @Override
    public AcceptedPaymentTypeDto retrieveAcceptedPaymentTypes() throws Exception {
        List<PaymentTypeDto> payments = getAllPaymentTypes((short)0);
        // acceptedPaymentTypeActionForm.setAllPaymentTypes(payments);
        AcceptedPaymentTypePersistence paymentTypePersistence = new AcceptedPaymentTypePersistence();
        for (int i = 0; i < TrxnTypes.values().length; i++) {
            setPaymentTypesForATransaction(payments, TrxnTypes.values()[i], paymentTypePersistence, new AcceptedPaymentTypeDto());
        }
        return null;
    }

    private List<PaymentTypeDto> getAllPaymentTypes(Short localeId) throws Exception {
        List<PaymentTypeDto> paymentTypeList = new ArrayList<PaymentTypeDto>();
        PaymentTypeDto payment = null;
        Short id = 0;
        List<PaymentTypeEntity> paymentTypes = getMasterEntities(PaymentTypeEntity.class, localeId);
        for (PaymentTypeEntity masterDataEntity : paymentTypes) {
            PaymentTypeEntity paymentType = masterDataEntity;
            id = paymentType.getId();
            payment = new PaymentTypeDto(id, paymentType.getName());
            paymentTypeList.add(payment);
        }

        return paymentTypeList;
    }

    private <T extends MasterDataEntity>  List<T> getMasterEntities(Class<T> type, Short localeId) throws ServiceException {
        return new MasterDataService().retrieveMasterEntities(type, localeId);
    }
    private void setPaymentTypesForATransaction(List<PaymentTypeDto> payments, TrxnTypes transactionType,
            AcceptedPaymentTypePersistence paymentTypePersistence, AcceptedPaymentTypeDto dto)
            throws Exception {

        Short transactionId = transactionType.getValue();
        List<AcceptedPaymentType> paymentTypeList = paymentTypePersistence
                .getAcceptedPaymentTypesForATransaction(transactionId);
        List<PaymentTypeDto> inList = new ArrayList<PaymentTypeDto>(payments);
        List<PaymentTypeDto> outList = new ArrayList<PaymentTypeDto>();

        PaymentTypeDto data = null;
        for (AcceptedPaymentType paymentType : paymentTypeList) {
            Short paymentTypeId = paymentType.getPaymentTypeEntity().getId();
            data = new PaymentTypeDto(paymentTypeId, paymentType.getPaymentTypeEntity().getName());
            outList.add(data);
            RemoveFromInList(inList, paymentTypeId);
        }
        if (transactionType == TrxnTypes.loan_repayment) {
            dto.setInRepaymentList(inList);
            dto.setOutRepaymentList(outList);
        } else if (transactionType == TrxnTypes.fee) {
            dto.setInFeeList(inList);
            dto.setOutFeeList(outList);
        } else if (transactionType == TrxnTypes.loan_disbursement) {
            dto.setInDisbursementList(inList);
            dto.setOutDisbursementList(outList);
        } else if (transactionType == TrxnTypes.savings_deposit) {
            dto.setInDepositList(inList);
            dto.setOutDepositList(outList);
        } else if (transactionType == TrxnTypes.savings_withdrawal) {
            dto.setInWithdrawalList(inList);
            dto.setOutWithdrawalList(outList);
        } else {
            throw new Exception("Unknow account action for accepted payment type " + transactionType.toString());
        }
    }

    private void RemoveFromInList(List<PaymentTypeDto> list, Short paymentTypeId) {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).getId().shortValue() == paymentTypeId.shortValue()) {
                list.remove(i);
            }
        }
    }
}