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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.acceptedpaymenttype.business.AcceptedPaymentType;
import org.mifos.accounts.acceptedpaymenttype.business.TransactionTypeEntity;
import org.mifos.accounts.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.persistence.FeePersistence;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.financial.business.service.FinancialBusinessService;
import org.mifos.accounts.financial.business.service.GeneralLedgerDao;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.fund.persistence.FundDao;
import org.mifos.accounts.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.accounts.productdefinition.business.InterestCalcTypeEntity;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.PrdApplicableMasterEntity;
import org.mifos.accounts.productdefinition.business.PrdOfferingBO;
import org.mifos.accounts.productdefinition.business.PrdOfferingMeetingEntity;
import org.mifos.accounts.productdefinition.business.PrdStatusEntity;
import org.mifos.accounts.productdefinition.business.ProductCategoryBO;
import org.mifos.accounts.productdefinition.business.ProductTypeEntity;
import org.mifos.accounts.productdefinition.business.RecommendedAmntUnitEntity;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsTypeEntity;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.accounts.productdefinition.business.service.ProductCategoryBusinessService;
import org.mifos.accounts.productdefinition.business.service.ProductService;
import org.mifos.accounts.productdefinition.business.service.SavingsPrdBusinessService;
import org.mifos.accounts.productdefinition.persistence.LoanPrdPersistence;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.productdefinition.persistence.PrdOfferingPersistence;
import org.mifos.accounts.productdefinition.persistence.ProductCategoryPersistence;
import org.mifos.accounts.productdefinition.persistence.SavingsProductDao;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.productdefinition.util.helpers.PrdCategoryStatus;
import org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.accounts.productdefinition.util.helpers.ProductType;
import org.mifos.accounts.productsmix.business.ProductMixBO;
import org.mifos.accounts.productsmix.business.service.ProductMixBusinessService;
import org.mifos.accounts.productsmix.persistence.ProductMixPersistence;
import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.LookUpEntity;
import org.mifos.application.master.business.LookUpLabelEntity;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.RecurrenceTypeEntity;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.AccountingRules;
import org.mifos.config.ClientRules;
import org.mifos.config.Localization;
import org.mifos.config.persistence.ApplicationConfigurationDao;
import org.mifos.config.persistence.ApplicationConfigurationPersistence;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.config.util.helpers.HiddenMandatoryFieldNamesConstants;
import org.mifos.config.util.helpers.LabelConfigurations;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.office.business.OfficeLevelEntity;
import org.mifos.customers.office.business.service.MandatoryHiddenFieldService;
import org.mifos.customers.office.business.service.OfficeHierarchyService;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.domain.AcceptedPaymentTypeDto;
import org.mifos.dto.domain.AccountStatusesLabelDto;
import org.mifos.dto.domain.AuditLogDto;
import org.mifos.dto.domain.ConfigurableLookupLabelDto;
import org.mifos.dto.domain.CreateOrUpdateProductCategory;
import org.mifos.dto.domain.GracePeriodDto;
import org.mifos.dto.domain.LoanProductRequest;
import org.mifos.dto.domain.MandatoryHiddenFieldsDto;
import org.mifos.dto.domain.OfficeLevelDto;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.dto.domain.ProductTypeDto;
import org.mifos.dto.domain.ReportCategoryDto;
import org.mifos.dto.domain.SavingsProductDto;
import org.mifos.dto.domain.UpdateConfiguredOfficeLevelRequest;
import org.mifos.dto.screen.ConfigureApplicationLabelsDto;
import org.mifos.dto.screen.ListElement;
import org.mifos.dto.screen.LoanProductFormDto;
import org.mifos.dto.screen.PaymentTypeDto;
import org.mifos.dto.screen.ProductCategoryDetailsDto;
import org.mifos.dto.screen.ProductCategoryDisplayDto;
import org.mifos.dto.screen.ProductCategoryDto;
import org.mifos.dto.screen.ProductCategoryTypeDto;
import org.mifos.dto.screen.ProductConfigurationDto;
import org.mifos.dto.screen.ProductDisplayDto;
import org.mifos.dto.screen.ProductDto;
import org.mifos.dto.screen.ProductMixDetailsDto;
import org.mifos.dto.screen.ProductMixDto;
import org.mifos.dto.screen.SavingsProductFormDto;
import org.mifos.framework.components.audit.business.service.AuditBusinessService;
import org.mifos.framework.components.audit.util.helpers.AuditLogView;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.persistence.FieldConfigurationPersistence;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelperForStaticHibernateUtil;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.reports.business.ReportsCategoryBO;
import org.mifos.reports.persistence.ReportsPersistence;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.springframework.security.core.context.SecurityContextHolder;

public class AdminServiceFacadeWebTier implements AdminServiceFacade {

    private final ProductService productService;
    private final OfficeHierarchyService officeHierarchyService;
    private final MandatoryHiddenFieldService mandatoryHiddenFieldService;
    private final LoanProductDao loanProductDao;
    private final SavingsProductDao savingsProductDao;
    private final OfficeDao officeDao;
    private final ApplicationConfigurationDao applicationConfigurationDao;
    private final FundDao fundDao;
    private final GeneralLedgerDao generalLedgerDao;
    private LoanProductAssembler loanProductAssembler;

    public AdminServiceFacadeWebTier(ProductService productService, OfficeHierarchyService officeHierarchyService,
            MandatoryHiddenFieldService mandatoryHiddenFieldService, LoanProductDao loanProductDao,
            SavingsProductDao savingsProductDao, OfficeDao officeDao,
            ApplicationConfigurationDao applicationConfigurationDao, FundDao fundDao, GeneralLedgerDao generalLedgerDao) {
        this.productService = productService;
        this.officeHierarchyService = officeHierarchyService;
        this.mandatoryHiddenFieldService = mandatoryHiddenFieldService;
        this.loanProductDao = loanProductDao;
        this.savingsProductDao = savingsProductDao;
        this.officeDao = officeDao;
        this.applicationConfigurationDao = applicationConfigurationDao;
        this.fundDao = fundDao;
        this.generalLedgerDao = generalLedgerDao;
        this.loanProductAssembler = new LoanProductAssembler(this.loanProductDao, this.generalLedgerDao, this.fundDao);
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
    public ProductDto retrieveAllProductMix() {
        try {
            List<ProductCategoryBO> productCategoryList = new ProductCategoryBusinessService().getAllCategories();

            List<PrdOfferingBO> prdOfferingList = new ProductMixBusinessService().getPrdOfferingMix();

            List<ProductCategoryTypeDto> pcList = new ArrayList<ProductCategoryTypeDto>();
            for (ProductCategoryBO pcBO : productCategoryList) {
                ProductCategoryTypeDto pcDto = new ProductCategoryTypeDto(pcBO.getProductType().getProductTypeID(),
                        pcBO.getProductType().getLookUpValue().getLookUpName());
                pcList.add(pcDto);
            }

            List<ProductMixDto> pmList = new ArrayList<ProductMixDto>();
            for (PrdOfferingBO poBO : prdOfferingList) {
                ProductMixDto pmDto = new ProductMixDto(poBO.getPrdCategory().getProductType().getProductTypeID(), poBO
                        .getPrdOfferingId(), poBO.getPrdType().getProductTypeID(), poBO.getPrdOfferingName());
                pmList.add(pmDto);
            }

            ProductDto productDto = new ProductDto(pcList, pmList);
            return productDto;
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public ProductMixDetailsDto retrieveProductMixDetails(Short prdOfferingId, String productType) {

        try {
            ProductMixBusinessService service = new ProductMixBusinessService();
            PrdOfferingBO product = service.getPrdOfferingByID(prdOfferingId);

            List<PrdOfferingBO> allowedPrdOfferingList = service.getAllowedPrdOfferingsByType(prdOfferingId.toString(),productType);
            List<PrdOfferingBO> notAllowedPrdOfferingList = service.getNotAllowedPrdOfferingsByType(prdOfferingId.toString());

            List<PrdOfferingDto> allowedPrdOfferingNames = new ArrayList<PrdOfferingDto>();
            List<PrdOfferingDto> notAllowedPrdOfferingNames = new ArrayList<PrdOfferingDto>();

            for (PrdOfferingBO prdOffering : allowedPrdOfferingList) {
                allowedPrdOfferingNames.add(prdOffering.toDto());
            }
            for (PrdOfferingBO prdOffering : notAllowedPrdOfferingList) {
                notAllowedPrdOfferingNames.add(prdOffering.toDto());
            }

            ProductMixDetailsDto dto = new ProductMixDetailsDto(prdOfferingId, product.getPrdOfferingName(), product
                    .getPrdType().getProductTypeID(), allowedPrdOfferingNames, notAllowedPrdOfferingNames);
            return dto;
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public void updateApplicationLabels(ConfigureApplicationLabelsDto updatedLabels) {

        ConfigureApplicationLabelsDto currentLabels = retrieveConfigurableLabels();

        List<OfficeLevelEntity> changedOfficeLabels = findDifferentOfficeLevelEntitys(currentLabels.getOfficeLevels(),
                updatedLabels.getOfficeLevels());
        List<LookUpEntity> lookupEntities = findDifferentLookupEntities(currentLabels.getLookupLabels(), updatedLabels
                .getLookupLabels());
        List<GracePeriodTypeEntity> gracePeriods = findDifferentGracePeriodTypes(currentLabels.getGracePeriodDto(),
                updatedLabels.getGracePeriodDto());

        List<LookUpValueEntity> accountStatuses = findAccountStates(updatedLabels.getAccountStatusLabels());

        officeHierarchyService.updateApplicationLabels(changedOfficeLabels, lookupEntities, gracePeriods,
                accountStatuses);
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
            // if (value.getLookUpId().equals(LabelConfigurations.FEE_CATEGORY_CLIENT.getValue())) {
            // name = labelConfigurationActionForm.getClient();
            // } else if (value.getLookUpId().equals(LabelConfigurations.FEE_CATEGORY_GROUP.getValue())) {
            // name = labelConfigurationActionForm.getGroup();
            // } else if (value.getLookUpId().equals(LabelConfigurations.FEE_CATEGORY_CENTER.getValue())
            // || value.getLookUpId().equals(Integer.valueOf("499"))) {
            // name = labelConfigurationActionForm.getCenter();
            // } else if (isLoanLookup(value)) {
            // name = labelConfigurationActionForm.getLoans();
            // } else if (value.getLookUpId().equals(Integer.valueOf("55"))
            // || value.getLookUpId().equals(Integer.valueOf("87"))) {
            // name = labelConfigurationActionForm.getSavings();
            // }
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

    private List<GracePeriodTypeEntity> findDifferentGracePeriodTypes(GracePeriodDto existingDetails,
            GracePeriodDto updatedDetails) {

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

    private List<LookUpEntity> findDifferentLookupEntities(ConfigurableLookupLabelDto existingDetails,
            ConfigurableLookupLabelDto updatedDetails) {

        List<LookUpEntity> lookupEntitiesForUpdate = new ArrayList<LookUpEntity>();

        if (!existingDetails.getClient().equals(updatedDetails.getClient())) {
            LookUpEntity entity = applicationConfigurationDao
                    .findLookupValueByEntityType(ConfigurationConstants.CLIENT);
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
            LookUpEntity entity = applicationConfigurationDao
                    .findLookupValueByEntityType(ConfigurationConstants.CENTER);
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
            LookUpEntity entity = applicationConfigurationDao
                    .findLookupValueByEntityType(ConfigurationConstants.SAVINGS);
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
            LookUpEntity entity = applicationConfigurationDao
                    .findLookupValueByEntityType(ConfigurationConstants.POSTAL_CODE);
            Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
            for (LookUpLabelEntity label : labels) {
                label.setLabelName(updatedDetails.getPostalCode());
            }
            lookupEntitiesForUpdate.add(entity);
        }

        if (!existingDetails.getEthnicity().equals(updatedDetails.getEthnicity())) {
            LookUpEntity entity = applicationConfigurationDao
                    .findLookupValueByEntityType(ConfigurationConstants.ETHINICITY);
            Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
            for (LookUpLabelEntity label : labels) {
                label.setLabelName(updatedDetails.getEthnicity());
            }
            lookupEntitiesForUpdate.add(entity);
        }

        if (!existingDetails.getCitizenship().equals(updatedDetails.getCitizenship())) {
            LookUpEntity entity = applicationConfigurationDao
                    .findLookupValueByEntityType(ConfigurationConstants.CITIZENSHIP);
            Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
            for (LookUpLabelEntity label : labels) {
                label.setLabelName(updatedDetails.getCitizenship());
            }
            lookupEntitiesForUpdate.add(entity);
        }

        if (!existingDetails.getHandicapped().equals(updatedDetails.getHandicapped())) {
            LookUpEntity entity = applicationConfigurationDao
                    .findLookupValueByEntityType(ConfigurationConstants.HANDICAPPED);
            Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
            for (LookUpLabelEntity label : labels) {
                label.setLabelName(updatedDetails.getHandicapped());
            }
            lookupEntitiesForUpdate.add(entity);
        }

        if (!existingDetails.getGovtId().equals(updatedDetails.getGovtId())) {
            LookUpEntity entity = applicationConfigurationDao
                    .findLookupValueByEntityType(ConfigurationConstants.GOVERNMENT_ID);
            Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
            for (LookUpLabelEntity label : labels) {
                label.setLabelName(updatedDetails.getGovtId());
            }
            lookupEntitiesForUpdate.add(entity);
        }

        if (!existingDetails.getAddress1().equals(updatedDetails.getAddress1())) {
            LookUpEntity entity = applicationConfigurationDao
                    .findLookupValueByEntityType(ConfigurationConstants.ADDRESS1);
            Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
            for (LookUpLabelEntity label : labels) {
                label.setLabelName(updatedDetails.getAddress1());
            }
            lookupEntitiesForUpdate.add(entity);
        }

        if (!existingDetails.getAddress2().equals(updatedDetails.getAddress2())) {
            LookUpEntity entity = applicationConfigurationDao
                    .findLookupValueByEntityType(ConfigurationConstants.ADDRESS2);
            Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
            for (LookUpLabelEntity label : labels) {
                label.setLabelName(updatedDetails.getAddress2());
            }
            lookupEntitiesForUpdate.add(entity);
        }

        if (!existingDetails.getAddress3().equals(updatedDetails.getAddress3())) {
            LookUpEntity entity = applicationConfigurationDao
                    .findLookupValueByEntityType(ConfigurationConstants.ADDRESS3);
            Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
            for (LookUpLabelEntity label : labels) {
                label.setLabelName(updatedDetails.getAddress3());
            }
            lookupEntitiesForUpdate.add(entity);
        }

        if (!existingDetails.getInterest().equals(updatedDetails.getInterest())) {
            LookUpEntity entity = applicationConfigurationDao
                    .findLookupValueByEntityType(ConfigurationConstants.INTEREST);
            Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
            for (LookUpLabelEntity label : labels) {
                label.setLabelName(updatedDetails.getInterest());
            }
            lookupEntitiesForUpdate.add(entity);
        }

        if (!existingDetails.getExternalId().equals(updatedDetails.getExternalId())) {
            LookUpEntity entity = applicationConfigurationDao
                    .findLookupValueByEntityType(ConfigurationConstants.EXTERNALID);
            Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
            for (LookUpLabelEntity label : labels) {
                label.setLabelName(updatedDetails.getExternalId());
            }
            lookupEntitiesForUpdate.add(entity);
        }

        if (!existingDetails.getBulkEntry().equals(updatedDetails.getBulkEntry())) {
            LookUpEntity entity = applicationConfigurationDao
                    .findLookupValueByEntityType(ConfigurationConstants.BULKENTRY);
            Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
            for (LookUpLabelEntity label : labels) {
                label.setLabelName(updatedDetails.getBulkEntry());
            }
            lookupEntitiesForUpdate.add(entity);
        }

        return lookupEntitiesForUpdate;
    }

    private List<OfficeLevelEntity> findDifferentOfficeLevelEntitys(OfficeLevelDto existingDetails,
            OfficeLevelDto updatedDetails) {

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
        GracePeriodDto gracePeriodDtos = assembleGracePeriod(gracePeriodTypes);

        List<LookUpEntity> lookupEntities = applicationConfigurationDao.findLookupValueTypes();
        ConfigurableLookupLabelDto lookupLabels = assembleLookupEntities(lookupEntities);

        List<AccountStateEntity> accountStateEntities = applicationConfigurationDao.findAllAccountStateEntities();
        AccountStatusesLabelDto accountStatusLabels = assembleAccountStateEntities(accountStateEntities);

        List<CustomerStatusEntity> customerStatuses = applicationConfigurationDao.findAllCustomerStatuses();
        for (CustomerStatusEntity customerStatus : customerStatuses) {
            if (customerStatus.getId().equals(CustomerStatus.CLIENT_HOLD.getValue())) {
                accountStatusLabels.setOnhold(customerStatus.getName());
            }
        }

        return new ConfigureApplicationLabelsDto(officeLevels, gracePeriodDtos, lookupLabels, accountStatusLabels);
    }

    private AccountStatusesLabelDto assembleAccountStateEntities(List<AccountStateEntity> accountStateEntities) {

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

    private ConfigurableLookupLabelDto assembleLookupEntities(List<LookUpEntity> lookupEntities) {

        ConfigurableLookupLabelDto lookupLabels = new ConfigurableLookupLabelDto();

        for (LookUpEntity entity : lookupEntities) {

            String labelText = entity.findLabel();
            if (StringUtils.isBlank(labelText)) {
                labelText = MessageLookup.getInstance().lookupLabel(entity.findLabelKey());
            }

            if (entity.getEntityType().equals(ConfigurationConstants.CLIENT)) {
                lookupLabels.setClient(labelText);
            } else if (entity.getEntityType().equals(ConfigurationConstants.GROUP)) {
                lookupLabels.setGroup(labelText);
            } else if (entity.getEntityType().equals(ConfigurationConstants.CENTER)) {
                lookupLabels.setCenter(labelText);
            } else if (entity.getEntityType().equals(ConfigurationConstants.LOAN)) {
                lookupLabels.setLoans(labelText);
            } else if (entity.getEntityType().equals(ConfigurationConstants.SAVINGS)) {
                lookupLabels.setSavings(labelText);
            } else if (entity.getEntityType().equals(ConfigurationConstants.STATE)) {
                lookupLabels.setState(labelText);
            } else if (entity.getEntityType().equals(ConfigurationConstants.POSTAL_CODE)) {
                lookupLabels.setPostalCode(labelText);
            } else if (entity.getEntityType().equals(ConfigurationConstants.ETHINICITY)) {
                lookupLabels.setEthnicity(labelText);
            } else if (entity.getEntityType().equals(ConfigurationConstants.CITIZENSHIP)) {
                lookupLabels.setCitizenship(labelText);
            } else if (entity.getEntityType().equals(ConfigurationConstants.HANDICAPPED)) {
                lookupLabels.setHandicapped(labelText);
            } else if (entity.getEntityType().equals(ConfigurationConstants.GOVERNMENT_ID)) {
                lookupLabels.setGovtId(labelText);
            } else if (entity.getEntityType().equals(ConfigurationConstants.ADDRESS1)) {
                lookupLabels.setAddress1(labelText);
            } else if (entity.getEntityType().equals(ConfigurationConstants.ADDRESS2)) {
                lookupLabels.setAddress2(labelText);
            } else if (entity.getEntityType().equals(ConfigurationConstants.ADDRESS3)) {
                lookupLabels.setAddress3(labelText);
            } else if (entity.getEntityType().equals(ConfigurationConstants.INTEREST)) {
                lookupLabels.setInterest(labelText);
            } else if (entity.getEntityType().equals(ConfigurationConstants.EXTERNALID)) {
                lookupLabels.setExternalId(labelText);
            } else if (entity.getEntityType().equals(ConfigurationConstants.BULKENTRY)) {
                lookupLabels.setBulkEntry(labelText);
            }
        }

        return lookupLabels;
    }

    private GracePeriodDto assembleGracePeriod(List<GracePeriodTypeEntity> gracePeriodTypes) {

        GracePeriodDto gracePeriods = new GracePeriodDto();

        for (GracePeriodTypeEntity entity : gracePeriodTypes) {

            LookUpValueEntity lookupValue = entity.getLookUpValue();
            String messageText = lookupValue.getMessageText();
            if (StringUtils.isBlank(messageText)) {
                messageText = MessageLookup.getInstance().lookup(lookupValue.getPropertiesKey());
            }

            GraceType periodType = GraceType.fromInt(entity.getId());
            switch (periodType) {
            case NONE:
                gracePeriods.setNone(messageText);
                break;
            case GRACEONALLREPAYMENTS:
                gracePeriods.setGraceOnAllRepayments(messageText);
                break;
            case PRINCIPALONLYGRACE:
                gracePeriods.setPrincipalOnlyGrace(messageText);
                break;
            default:
                break;
            }
        }

        return gracePeriods;
    }

    @Override
    public MandatoryHiddenFieldsDto retrieveHiddenMandatoryFields() {

        try {
            List<FieldConfigurationEntity> confFieldList = new FieldConfigurationPersistence()
                    .getAllConfigurationFieldList();
            MandatoryHiddenFieldsDto dto = new MandatoryHiddenFieldsDto();
            populateDto(dto, confFieldList);
            dto.setFamilyDetailsRequired(ClientRules.isFamilyDetailsRequired());
            return dto;
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
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
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.FAMILY_DETAILS)) {
            dto.setMandatoryClientFamilyDetails(getBooleanValue(fieldConfiguration.getMandatoryFlag()));
        } else if (fieldConfiguration.getFieldName().equals(
                HiddenMandatoryFieldNamesConstants.SPOUSE_FATHER_MIDDLE_NAME)) {
            dto.setHideClientSpouseFatherMiddleName(getBooleanValue(fieldConfiguration.getHiddenFlag()));
        } else if (fieldConfiguration.getFieldName().equals(
                HiddenMandatoryFieldNamesConstants.SPOUSE_FATHER_SECOND_LAST_NAME)) {
            dto.setHideClientSpouseFatherSecondLastName(getBooleanValue(fieldConfiguration.getHiddenFlag()));
            dto.setMandatoryClientSpouseFatherSecondLastName(getBooleanValue(fieldConfiguration.getMandatoryFlag()));
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
        return ((s != null) && (s != 0)) ? true : false;
    }

    @Override
    public void updateHiddenMandatoryFields(MandatoryHiddenFieldsDto dto) {
        try {
            List<FieldConfigurationEntity> confFieldList = new FieldConfigurationPersistence()
                    .getAllConfigurationFieldList();
            mandatoryHiddenFieldService.updateMandatoryHiddenFields(dto, confFieldList);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public AcceptedPaymentTypeDto retrieveAcceptedPaymentTypes() {
        List<PaymentTypeDto> payments = getAllPaymentTypes(null);
        AcceptedPaymentTypePersistence paymentTypePersistence = new AcceptedPaymentTypePersistence();
        AcceptedPaymentTypeDto dto = new AcceptedPaymentTypeDto();
        for (int i = 0; i < TrxnTypes.values().length; i++) {
            setPaymentTypesForATransaction(payments, TrxnTypes.values()[i], paymentTypePersistence, dto);
        }
        return dto;
    }

    private List<PaymentTypeDto> getAllPaymentTypes(Short localeId) {
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

    private <T extends MasterDataEntity> List<T> getMasterEntities(Class<T> type, Short localeId) {
        try {
            return new MasterDataService().retrieveMasterEntities(type, localeId);
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private void setPaymentTypesForATransaction(List<PaymentTypeDto> payments, TrxnTypes transactionType,
            AcceptedPaymentTypePersistence paymentTypePersistence, AcceptedPaymentTypeDto dto) {

        try {
            Short transactionId = transactionType.getValue();
            List<AcceptedPaymentType> paymentTypeList = paymentTypePersistence
                    .getAcceptedPaymentTypesForATransaction(transactionId, TrxnTypes.loan_repayment);

            List<PaymentTypeDto> inList = new ArrayList<PaymentTypeDto>(payments);
            List<PaymentTypeDto> outList = new ArrayList<PaymentTypeDto>();

            PaymentTypeDto data = null;
            for (AcceptedPaymentType paymentType : paymentTypeList) {
                Short paymentTypeId = paymentType.getPaymentTypeEntity().getId();
                data = new PaymentTypeDto(paymentTypeId, paymentType.getPaymentTypeEntity().getName(), paymentType
                        .getAcceptedPaymentTypeId());
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
                throw new MifosRuntimeException("Unknown account action for accepted payment type "
                        + transactionType.toString());
            }
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private void RemoveFromInList(List<PaymentTypeDto> list, Short paymentTypeId) {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).getId().shortValue() == paymentTypeId.shortValue()) {
                list.remove(i);
            }
        }
    }

    @Override
    public void updateAcceptedPaymentTypes(String[] chosenAcceptedFees, String[] chosenAcceptedLoanDisbursements,
            String[] chosenAcceptedLoanRepayments, String[] chosenAcceptedSavingDeposits,
            String[] chosenAcceptedSavingWithdrawals) {

        AcceptedPaymentTypePersistence persistence = new AcceptedPaymentTypePersistence();

        List<AcceptedPaymentType> deletedPaymentTypeList = new ArrayList<AcceptedPaymentType>();
        List<AcceptedPaymentType> addedPaymentTypeList = new ArrayList<AcceptedPaymentType>();
        List<PaymentTypeDto> allPayments = getAllPaymentTypes(null);
        AcceptedPaymentTypeDto oldAcceptedPaymentTypeDto = retrieveAcceptedPaymentTypes();

        for (int i = 0; i < TrxnTypes.values().length; i++) {

            TrxnTypes transactionType = TrxnTypes.values()[i];
            List<PaymentTypeDto> selectedPaymentTypes = new ArrayList<PaymentTypeDto>();
            List<PaymentTypeDto> outList = null;

            if (transactionType == TrxnTypes.fee) {
                selectedPaymentTypes = populateSelectedPayments(chosenAcceptedFees, allPayments);
                outList = oldAcceptedPaymentTypeDto.getOutFeeList();
            } else if (transactionType == TrxnTypes.loan_disbursement) {
                selectedPaymentTypes = populateSelectedPayments(chosenAcceptedLoanDisbursements, allPayments);
                outList = oldAcceptedPaymentTypeDto.getOutDisbursementList();
            } else if (transactionType == TrxnTypes.loan_repayment) {
                selectedPaymentTypes = populateSelectedPayments(chosenAcceptedLoanRepayments, allPayments);
                outList = oldAcceptedPaymentTypeDto.getOutRepaymentList();
            } else if (transactionType == TrxnTypes.savings_deposit) {
                selectedPaymentTypes = populateSelectedPayments(chosenAcceptedSavingDeposits, allPayments);
                outList = oldAcceptedPaymentTypeDto.getOutDepositList();
            } else if (transactionType == TrxnTypes.savings_withdrawal) {
                selectedPaymentTypes = populateSelectedPayments(chosenAcceptedSavingWithdrawals, allPayments);
                outList = oldAcceptedPaymentTypeDto.getOutWithdrawalList();
            } else {
                throw new MifosRuntimeException("Unknown account action for accepted payment type "
                        + transactionType.toString());
            }
            process(selectedPaymentTypes, outList, deletedPaymentTypeList, addedPaymentTypeList, persistence,
                    transactionType);
        }

        try {
            if (addedPaymentTypeList.size() > 0) {
                persistence.addAcceptedPaymentTypes(addedPaymentTypeList);
                StaticHibernateUtil.commitTransaction();
            }

            if (deletedPaymentTypeList.size() > 0) {
                persistence.deleteAcceptedPaymentTypes(deletedPaymentTypeList);
                StaticHibernateUtil.commitTransaction();
            }
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private List<PaymentTypeDto> populateSelectedPayments(String[] selectedPayments, List<PaymentTypeDto> allPayments) {

        List<PaymentTypeDto> selectedPaymentTypes = new ArrayList<PaymentTypeDto>();

        if (null != allPayments && null != selectedPayments) {
            List<String> acceptedFees = Arrays.asList(selectedPayments);
            for (PaymentTypeDto paymentType : allPayments) {
                if (acceptedFees.contains(paymentType.getId().toString())) {
                    selectedPaymentTypes.add(paymentType);
                }
            }
        }

        return selectedPaymentTypes;
    }

    private void process(List<PaymentTypeDto> selectedPaymentTypes, List<PaymentTypeDto> outList,
            List<AcceptedPaymentType> deletedPaymentTypeList, List<AcceptedPaymentType> addedPaymentTypeList,
            AcceptedPaymentTypePersistence persistence, TrxnTypes transactionType) {

        AcceptedPaymentType acceptedPaymentType = null;
        if ((outList != null) && (outList.size() > 0)) {
            for (PaymentTypeDto paymentType : outList) {
                if (findDelete(paymentType, selectedPaymentTypes)) {
                    acceptedPaymentType = persistence.getAcceptedPaymentType(paymentType.getAcceptedPaymentTypeId());
                    deletedPaymentTypeList.add(acceptedPaymentType);
                }
            }
        }

        for (PaymentTypeDto selectedPaymentType : selectedPaymentTypes) {
            Short paymentTypeId = selectedPaymentType.getId();
            if (findNew(paymentTypeId, outList)) {
                acceptedPaymentType = new AcceptedPaymentType();
                PaymentTypeEntity paymentTypeEntity = new PaymentTypeEntity(paymentTypeId);
                acceptedPaymentType.setPaymentTypeEntity(paymentTypeEntity);
                TransactionTypeEntity transactionEntity = new TransactionTypeEntity();
                transactionEntity.setTransactionId(transactionType.getValue());
                acceptedPaymentType.setTransactionTypeEntity(transactionEntity);
                addedPaymentTypeList.add(acceptedPaymentType);
            }
        }
    }

    private boolean findDelete(PaymentTypeDto paymentType, List<PaymentTypeDto> paymentTypes) {
        if (paymentTypes == null) {
            return true;
        }
        Short paymentTypeId = paymentType.getId();
        for (PaymentTypeDto paymentType2 : paymentTypes) {
            Short paymentId = paymentType2.getId();
            if (paymentId.shortValue() == paymentTypeId.shortValue()) {
                return false;
            }
        }
        return true;
    }

    private boolean findNew(Short paymentTypeId, List<PaymentTypeDto> paymentTypes) {

        for (PaymentTypeDto paymentTypeData : paymentTypes) {
            Short paymentId = paymentTypeData.getId();
            if (paymentId.shortValue() == paymentTypeId.shortValue()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ProductCategoryDisplayDto retrieveAllProductCategories() {
        try {
            List<ProductCategoryBO> productCategoryList = new ProductCategoryBusinessService().getAllCategories();

            List<ProductCategoryTypeDto> pcTypeList = new ArrayList<ProductCategoryTypeDto>();
            List<ProductCategoryDto> pcList = new ArrayList<ProductCategoryDto>();
            for (ProductCategoryBO pcBO : productCategoryList) {
                ProductCategoryTypeDto pcTypeDto = new ProductCategoryTypeDto(pcBO.getProductType().getProductTypeID(),
                        pcBO.getProductType().getLookUpValue().getLookUpName());
                pcTypeList.add(pcTypeDto);

                ProductCategoryDto pcDto = new ProductCategoryDto(pcBO.getProductCategoryName(), pcBO
                        .getPrdCategoryStatus().getId(), pcBO.getGlobalPrdCategoryNum());
                pcList.add(pcDto);
            }

            ProductCategoryDisplayDto productCategoryDisplayDto = new ProductCategoryDisplayDto(pcTypeList, pcList);
            return productCategoryDisplayDto;

        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public ProductCategoryDetailsDto retrieveProductCateogry(String globalProductCategoryNumber) {

        ProductCategoryBusinessService service = new ProductCategoryBusinessService();
        try {
            ProductCategoryBO pcBO = service.findByGlobalNum(globalProductCategoryNumber);
            String productTypeName = service.getProductType(pcBO.getProductType().getProductTypeID()).getLookUpValue()
                    .getLookUpName();
            ProductCategoryDetailsDto productCategoryDetailsDto = new ProductCategoryDetailsDto(pcBO
                    .getProductCategoryName(), pcBO.getPrdCategoryStatus().getId(), pcBO.getProductType()
                    .getProductTypeID(), pcBO.getProductCategoryDesc(), productTypeName);
            return productCategoryDetailsDto;
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public void createProductCategory(CreateOrUpdateProductCategory productCategoryDto) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);

        this.loanProductDao.validateNameIsAvailableForCategory(productCategoryDto.getProductCategoryName(),
                productCategoryDto.getProductTypeEntityId());

        HibernateTransactionHelper transactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();
        try {

            // FIXME - delegate to globalNumberGenerationStrategy
            StringBuilder globalPrdOfferingNum = new StringBuilder();
            globalPrdOfferingNum.append(userContext.getBranchId());
            globalPrdOfferingNum.append("-");
            Short maxPrdID = new ProductCategoryPersistence().getMaxPrdCategoryId();
            globalPrdOfferingNum.append(StringUtils.leftPad(String.valueOf(maxPrdID != null ? maxPrdID + 1
                    : ProductDefinitionConstants.DEFAULTMAX), 3, '0'));
            String globalNumber = globalPrdOfferingNum.toString();

            ProductTypeEntity productType = new ProductTypeEntity(productCategoryDto.getProductTypeEntityId());
            ProductCategoryBO productCategoryBO = new ProductCategoryBO(productType, productCategoryDto
                    .getProductCategoryName(), productCategoryDto.getProductCategoryDesc(), globalNumber);

            transactionHelper.startTransaction();
            this.loanProductDao.save(productCategoryBO);
            transactionHelper.commitTransaction();

        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        } catch (Exception e) {
            transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            transactionHelper.closeSession();
        }
    }

    @Override
    public List<ProductCategoryTypeDto> retrieveProductCategoryTypes() {
        try {
            List<ProductTypeEntity> productCategoryList = new ProductCategoryBusinessService().getProductTypes();
            List<ProductCategoryTypeDto> productCategoryTypeDtoList = new ArrayList<ProductCategoryTypeDto>();
            for (ProductTypeEntity productType : productCategoryList) {
                ProductCategoryTypeDto productCategoryTypeDto = new ProductCategoryTypeDto(productType
                        .getProductTypeID(), productType.getLookUpValue().getLookUpName());
                productCategoryTypeDtoList.add(productCategoryTypeDto);
            }

            return productCategoryTypeDtoList;
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public void updateProductCategory(CreateOrUpdateProductCategory productCategoryDto) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);

        HibernateTransactionHelper transactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();
        try {
            ProductCategoryBO categoryForUpdate = this.loanProductDao.findProductCategoryByGlobalNum(productCategoryDto.getGlobalPrdCategoryNum());
            if (categoryForUpdate.hasDifferentName(productCategoryDto.getProductCategoryName())) {
                this.loanProductDao.validateNameIsAvailableForCategory(productCategoryDto.getProductCategoryName(), productCategoryDto.getProductTypeEntityId());
            }

            transactionHelper.startTransaction();
            categoryForUpdate.update(productCategoryDto.getProductCategoryName(), productCategoryDto.getProductCategoryDesc(), PrdCategoryStatus.fromInt(productCategoryDto.getProductCategoryStatusId()));
            this.loanProductDao.save(categoryForUpdate);
            transactionHelper.commitTransaction();

        } catch (BusinessRuleException e) {
            transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getMessageKey(), e);
        } catch (Exception e) {
            transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            transactionHelper.closeSession();
        }
    }

    @Override
    public List<ProductTypeDto> retrieveProductTypesApplicableToProductMix() {
        List<ProductTypeDto> poductTypes = new ArrayList<ProductTypeDto>();

        poductTypes.add(new ProductTypeDto(ProductType.LOAN.getValue().intValue(), "manageProduct.viewProductMix.loan"));

        return poductTypes;
    }

    @Override
    public List<PrdOfferingDto> retrieveLoanProductsNotMixed() {

        List<PrdOfferingDto> productTypes = new ArrayList<PrdOfferingDto>();

        try {
            List<LoanOfferingBO> loanProductsNotMixed = new LoanPrdPersistence().getLoanOfferingsNotMixed(null);
            for (LoanOfferingBO loanOfferingBO : loanProductsNotMixed) {
                productTypes.add(loanOfferingBO.toDto());
            }
            return productTypes;
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public List<PrdOfferingDto> retrieveAllowedProductsForMix(Integer productTypeId, Integer productId) {
        try {
            List<PrdOfferingDto> allowedProductDtos = new ArrayList<PrdOfferingDto>();
            List<PrdOfferingBO> allowedProducts = new PrdOfferingPersistence().getAllowedPrdOfferingsForMixProduct(
                    productId.toString(), productTypeId.toString());
            for (PrdOfferingBO product : allowedProducts) {
                allowedProductDtos.add(product.toDto());
            }
            return allowedProductDtos;
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public List<PrdOfferingDto> retrieveNotAllowedProductsForMix(Integer productTypeId, Integer productId) {

        try {
            List<PrdOfferingDto> notAllowedProductDtos = new ArrayList<PrdOfferingDto>();
            List<PrdOfferingBO> allowedProducts = new PrdOfferingPersistence().getNotAllowedPrdOfferingsForMixProduct(
                    productId.toString(), productTypeId.toString());
            for (PrdOfferingBO product : allowedProducts) {
                notAllowedProductDtos.add(product.toDto());
            }
            return notAllowedProductDtos;
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public void createOrUpdateProductMix(Integer productId, List<Integer> notAllowedProductIds) {

        try {
            PrdOfferingBO product = new PrdOfferingPersistence().getPrdOfferingByID(productId.shortValue());

            StaticHibernateUtil.startTransaction();
            product.setPrdMixFlag(YesNoFlag.YES.getValue());
            applicationConfigurationDao.save(product);
            StaticHibernateUtil.flushSession();

            List<PrdOfferingBO> newNotAllowedProducts = new ArrayList<PrdOfferingBO>();
            for (Integer notAllowedProductId : notAllowedProductIds) {
                PrdOfferingBO notAllowedProduct = new PrdOfferingPersistence().getPrdOfferingByID(notAllowedProductId
                        .shortValue());
                newNotAllowedProducts.add(notAllowedProduct);
            }

            for (ProductMixBO oldNotAllowedProduct : product.getCollectionProductMix() ) {

                ProductMixBO productMix = new ProductMixPersistence().getPrdOfferingMixByPrdOfferingID(productId
                        .shortValue(), oldNotAllowedProduct.getPrdOfferingNotAllowedId().getPrdOfferingId());

                if (null != productMix) {
                    applicationConfigurationDao.delete(productMix);
                    StaticHibernateUtil.flushSession();
                }
                ProductMixBO alternateproductmix = new ProductMixPersistence().getPrdOfferingMixByPrdOfferingID(
                        oldNotAllowedProduct.getPrdOfferingNotAllowedId().getPrdOfferingId(), productId.shortValue());

                if (null != alternateproductmix) {
                    applicationConfigurationDao.delete(alternateproductmix);
                    StaticHibernateUtil.flushSession();
                }
            }

            for (PrdOfferingBO notAllowedProduct : newNotAllowedProducts) {
                ProductMixBO productMix = new ProductMixBO(product, notAllowedProduct);
                productMix.setUpdatedDate(new DateTime().toDate());
                productMix.setUpdatedBy(Short.valueOf("1"));
                applicationConfigurationDao.save(productMix);
                StaticHibernateUtil.flushSession();
            }
            StaticHibernateUtil.commitTransaction();

        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    @Override
    public SavingsProductFormDto retrieveSavingsProductFormReferenceData() {

        try {
            SavingsPrdBusinessService service = new SavingsPrdBusinessService();

            List<ListElement> statusOptions = new ArrayList<ListElement>();
            List<PrdStatusEntity> applicableStatuses = service.getApplicablePrdStatus(Short.valueOf("1"));
            for (PrdStatusEntity entity : applicableStatuses) {
                statusOptions.add(new ListElement(entity.getOfferingStatusId().intValue(), entity.getPrdState().getName()));
            }

            List<ListElement> productCategoryOptions = new ArrayList<ListElement>();
            List<ProductCategoryBO> productCategories = service.getActiveSavingsProductCategories();
            for (ProductCategoryBO category : productCategories) {
                productCategoryOptions.add(new ListElement(category.getProductCategoryID().intValue(), category
                        .getProductCategoryName()));
            }

            List<ListElement> applicableForOptions = new ArrayList<ListElement>();
            List<PrdApplicableMasterEntity> applicableCustomerTypes = this.loanProductDao
                    .retrieveSavingsApplicableProductCategories();
            for (PrdApplicableMasterEntity entity : applicableCustomerTypes) {
                applicableForOptions.add(new ListElement(entity.getId().intValue(), entity.getName()));
            }

            List<ListElement> savingsTypeOptions = new ArrayList<ListElement>();
            List<SavingsTypeEntity> savingsTypes = this.loanProductDao.retrieveSavingsTypes();
            for (SavingsTypeEntity entity : savingsTypes) {
                savingsTypeOptions.add(new ListElement(entity.getId().intValue(), entity.getName()));
            }

            List<ListElement> recommendedAmountTypeOptions = new ArrayList<ListElement>();
            List<RecommendedAmntUnitEntity> recommendedAmountTypes = this.loanProductDao
                    .retrieveRecommendedAmountTypes();
            for (RecommendedAmntUnitEntity entity : recommendedAmountTypes) {
                recommendedAmountTypeOptions.add(new ListElement(entity.getId().intValue(), entity.getName()));
            }

            List<ListElement> interestCalcTypeOptions = new ArrayList<ListElement>();
            List<InterestCalcTypeEntity> interestCalcTypes = this.savingsProductDao.retrieveInterestCalculationTypes();
            for (InterestCalcTypeEntity entity : interestCalcTypes) {
                interestCalcTypeOptions.add(new ListElement(entity.getId().intValue(), entity.getName()));
            }

            List<ListElement> timePeriodOptions = new ArrayList<ListElement>();
            List<RecurrenceTypeEntity> applicableRecurrences = service.getSavingsApplicableRecurrenceTypes();
            for (RecurrenceTypeEntity entity : applicableRecurrences) {
                timePeriodOptions.add(new ListElement(entity.getRecurrenceId().intValue(), entity.getRecurrenceName()));
            }

            List<GLCodeEntity> depositGlCodeList = new ArrayList<GLCodeEntity>();
            depositGlCodeList.addAll(new FinancialBusinessService().getGLCodes(FinancialActionConstants.MANDATORYDEPOSIT, FinancialConstants.CREDIT));
            depositGlCodeList.addAll(new FinancialBusinessService().getGLCodes(FinancialActionConstants.VOLUNTORYDEPOSIT, FinancialConstants.CREDIT));

            List<ListElement> depositGlCodeOptions = new ArrayList<ListElement>();
            for (GLCodeEntity glCode : depositGlCodeList) {
                depositGlCodeOptions.add(new ListElement(glCode.getGlcodeId().intValue(), glCode.getGlcode()));
            }

            List<GLCodeEntity> interestGlCodeList = new FinancialBusinessService().getGLCodes(FinancialActionConstants.SAVINGS_INTERESTPOSTING, FinancialConstants.DEBIT);
            List<ListElement> interestGlCodes = new ArrayList<ListElement>();
            for (GLCodeEntity glCode : interestGlCodeList) {
                interestGlCodes.add(new ListElement(glCode.getGlcodeId().intValue(), glCode.getGlcode()));
            }

            return new SavingsProductFormDto(productCategoryOptions, applicableForOptions, savingsTypeOptions, recommendedAmountTypeOptions, interestCalcTypeOptions, timePeriodOptions, depositGlCodeOptions, interestGlCodes, statusOptions);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        } catch (SystemException e) {
            throw new MifosRuntimeException(e);
        } catch (ApplicationException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    @Override
    public LoanProductFormDto retrieveLoanProductFormReferenceData() {

        try {
            LoanPrdBusinessService service = new LoanPrdBusinessService();

            List<ListElement> productCategoryOptions = new ArrayList<ListElement>();
            List<ProductCategoryBO> productCategories = service.getActiveLoanProductCategories();
            for (ProductCategoryBO category : productCategories) {
                productCategoryOptions.add(new ListElement(category.getProductCategoryID().intValue(), category.getProductCategoryName()));
            }

            List<ListElement> applicableForOptions = new ArrayList<ListElement>();
            List<PrdApplicableMasterEntity> applicableCustomerTypes = this.loanProductDao.retrieveLoanApplicableProductCategories();
            for (PrdApplicableMasterEntity entity : applicableCustomerTypes) {
                applicableForOptions.add(new ListElement(entity.getId().intValue(), entity.getName()));
            }

            List<ListElement> gracePeriodTypeOptions = new ArrayList<ListElement>();
            List<GracePeriodTypeEntity> gracePeriodTypes = this.loanProductDao.retrieveGracePeriodTypes();
            for (GracePeriodTypeEntity gracePeriodTypeEntity : gracePeriodTypes) {
                gracePeriodTypeOptions.add(new ListElement(gracePeriodTypeEntity.getId().intValue(), gracePeriodTypeEntity.getName()));
            }

            List<ListElement> interestCalcTypesOptions = new ArrayList<ListElement>();
            List<InterestTypesEntity> interestCalcTypes = this.loanProductDao.retrieveInterestTypes();
            for (InterestTypesEntity entity : interestCalcTypes) {
                interestCalcTypesOptions.add(new ListElement(entity.getId().intValue(), entity.getName()));
            }

            List<ListElement> sourceOfFunds = new ArrayList<ListElement>();
            List<FundBO> funds = this.fundDao.findAllFunds();
            for (FundBO fund : funds) {
                sourceOfFunds.add(new ListElement(fund.getFundId().intValue(), fund.getFundName()));
            }

            List<ListElement> loanFee = new ArrayList<ListElement>();
            List<FeeBO> fees = new FeePersistence().getAllAppllicableFeeForLoanCreation();
            for (FeeBO fee : fees) {
                loanFee.add(new ListElement(fee.getFeeId().intValue(), fee.getFeeName()));
            }

            List<ListElement> principalGlCodes = new ArrayList<ListElement>();
            List<GLCodeEntity> principalGlCodeEntities = new FinancialBusinessService().getGLCodes(FinancialActionConstants.PRINCIPALPOSTING, FinancialConstants.CREDIT);
            for (GLCodeEntity glCode : principalGlCodeEntities) {
                principalGlCodes.add(new ListElement(glCode.getGlcodeId().intValue(), glCode.getGlcode()));
            }

            List<ListElement> interestGlCodes = new ArrayList<ListElement>();
            List<GLCodeEntity> interestGlCodeEntities = new FinancialBusinessService().getGLCodes(FinancialActionConstants.INTERESTPOSTING, FinancialConstants.CREDIT);
            for (GLCodeEntity glCode : interestGlCodeEntities) {
                interestGlCodes.add(new ListElement(glCode.getGlcodeId().intValue(), glCode.getGlcode()));
            }

            List<ListElement> statusOptions = new ArrayList<ListElement>();
            List<PrdStatusEntity> applicableStatuses = service.getApplicablePrdStatus(Short.valueOf("1"));
            for (PrdStatusEntity entity : applicableStatuses) {
                statusOptions.add(new ListElement(entity.getOfferingStatusId().intValue(), entity.getPrdState().getName()));
            }

            boolean multiCurrencyEnabled = AccountingRules.isMultiCurrencyEnabled();
            List<ListElement> currencyOptions = new ArrayList<ListElement>();
            if (multiCurrencyEnabled) {
                LinkedList<MifosCurrency> currencies = AccountingRules.getCurrencies();
                for (MifosCurrency mifosCurrency : currencies) {
                    currencyOptions.add(new ListElement(mifosCurrency.getCurrencyId().intValue(), mifosCurrency.getCurrencyCode()));
                }
            }

            return new LoanProductFormDto(productCategoryOptions, gracePeriodTypeOptions, sourceOfFunds, loanFee, principalGlCodes, interestGlCodes, interestCalcTypesOptions, applicableForOptions, statusOptions, currencyOptions, multiCurrencyEnabled);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        } catch (SystemException e) {
            throw new MifosRuntimeException(e);
        } catch (ApplicationException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    @Override
    public PrdOfferingDto updateLoanProduct(LoanProductRequest loanProductRequest) {

        LoanOfferingBO loanProductForUpdate = this.loanProductDao.findById(loanProductRequest.getProductDetails().getId());

        // enforced by integrity constraints on table also.
        if (loanProductForUpdate.isDifferentName(loanProductRequest.getProductDetails().getName())) {
            this.savingsProductDao.validateProductWithSameNameDoesNotExist(loanProductRequest.getProductDetails().getName());
        }

        if (loanProductForUpdate.isDifferentShortName(loanProductRequest.getProductDetails().getShortName())) {
            this.savingsProductDao.validateProductWithSameShortNameDoesNotExist(loanProductRequest.getProductDetails().getShortName());
        }

        // domain rule validation - put on domain entity
        if (loanProductForUpdate.isDifferentStartDate(loanProductRequest.getProductDetails().getStartDate())) {
            validateStartDateIsNotBeforeToday(loanProductRequest.getProductDetails().getStartDate());
            validateStartDateIsNotOverOneYearFromToday(loanProductRequest.getProductDetails().getStartDate());

            validateEndDateIsPastStartDate(loanProductRequest.getProductDetails().getStartDate(), loanProductRequest.getProductDetails().getEndDate());
        }

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);

        LoanOfferingBO newLoanProductDetails = this.loanProductAssembler.fromDto(user, loanProductRequest);
        loanProductForUpdate.updateDetails(userContext);

        HibernateTransactionHelper transactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();

        try {
            transactionHelper.startTransaction();
            transactionHelper.beginAuditLoggingFor(loanProductForUpdate);

            loanProductForUpdate.updateDetailsOfProductNotInUse(newLoanProductDetails.getPrdOfferingName(), newLoanProductDetails.getPrdOfferingShortName(),
                    newLoanProductDetails.getDescription(), newLoanProductDetails.getPrdCategory(), newLoanProductDetails.getStartDate(), newLoanProductDetails.getEndDate(),
                    newLoanProductDetails.getPrdApplicableMaster(), newLoanProductDetails.getPrdStatus());

            loanProductForUpdate.update(newLoanProductDetails.isIncludeInLoanCounter(), newLoanProductDetails.isInterestWaived());

            if (newLoanProductDetails.isLoanAmountTypeSameForAllLoan()) {
                loanProductForUpdate.updateLoanAmountDetails(newLoanProductDetails.getEligibleLoanAmountSameForAllLoan());
            } else if (newLoanProductDetails.isLoanAmountTypeAsOfLastLoanAmount()) {
                loanProductForUpdate.updateLoanAmountByLastLoanDetails(newLoanProductDetails.getLoanAmountFromLastLoan());
            } else if (newLoanProductDetails.isLoanAmountTypeFromLoanCycle()) {
                loanProductForUpdate.updateLoanAmountLoanCycleDetails(newLoanProductDetails.getLoanAmountFromLoanCycle());
            }

            loanProductForUpdate.updateInterestRateDetails(newLoanProductDetails.getMinInterestRate(), newLoanProductDetails.getMaxInterestRate(), newLoanProductDetails.getDefInterestRate());

            PrdOfferingMeetingEntity entity = newLoanProductDetails.getLoanOfferingMeeting();
            MeetingBO meeting = new MeetingBO(entity.getMeeting().getRecurrenceType(), entity.getMeeting().getRecurAfter(), entity.getMeeting().getStartDate() ,MeetingType.LOAN_INSTALLMENT);
            loanProductForUpdate.updateRepaymentDetails(meeting, newLoanProductDetails.getGracePeriodType(), newLoanProductDetails.getGracePeriodDuration());

            if (newLoanProductDetails.isNoOfInstallTypeSameForAllLoan()) {
                loanProductForUpdate.updateInstallmentDetails(newLoanProductDetails.getNoOfInstallSameForAllLoan());
            } else if (newLoanProductDetails.isNoOfInstallTypeFromLastLoan()) {
                loanProductForUpdate.updateInstallmentByLastLoanDetails(newLoanProductDetails.getNoOfInstallFromLastLoan());
            } else if (newLoanProductDetails.isNoOfInstallTypeFromLoanCycle()) {
                loanProductForUpdate.updateInstallmentLoanCycleDetails(newLoanProductDetails.getNoOfInstallFromLoanCycle());
            }

            loanProductForUpdate.updateFees(newLoanProductDetails.getLoanOfferingFees());
            loanProductForUpdate.updateFunds(newLoanProductDetails.getLoanOfferingFunds());

            this.loanProductDao.save(loanProductForUpdate);
            transactionHelper.commitTransaction();
            return loanProductForUpdate.toDto();
        } catch (Exception e) {
            transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            transactionHelper.closeSession();
        }
    }

    @Override
    public PrdOfferingDto updateSavingsProduct(SavingsProductDto savingsProductRequest) {

        SavingsOfferingBO savingsProductForUpdate = this.savingsProductDao.findById(savingsProductRequest.getProductDetails().getId());

        // enforced by integrity constraints on table also.
        if (savingsProductForUpdate.isDifferentName(savingsProductRequest.getProductDetails().getName())) {
            this.savingsProductDao.validateProductWithSameNameDoesNotExist(savingsProductRequest.getProductDetails().getName());
        }

        if (savingsProductForUpdate.isDifferentShortName(savingsProductRequest.getProductDetails().getShortName())) {
            this.savingsProductDao.validateProductWithSameShortNameDoesNotExist(savingsProductRequest.getProductDetails().getShortName());
        }

        // domain rule validation - put on domain entity
        if (savingsProductForUpdate.isDifferentStartDate(savingsProductRequest.getProductDetails().getStartDate())) {
            validateStartDateIsNotBeforeToday(savingsProductRequest.getProductDetails().getStartDate());
            validateStartDateIsNotOverOneYearFromToday(savingsProductRequest.getProductDetails().getStartDate());

            validateEndDateIsPastStartDate(savingsProductRequest.getProductDetails().getStartDate(), savingsProductRequest.getProductDetails().getEndDate());
        }

        boolean activeOrInactiveSavingsAccountExist = this.savingsProductDao.activeOrInactiveSavingsAccountsExistForProduct(savingsProductRequest.getProductDetails().getId());

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);

        SavingsOfferingBO newSavingsDetails = new SavingsProductAssembler(this.loanProductDao, this.savingsProductDao, this.generalLedgerDao).fromDto(user, savingsProductRequest);
        savingsProductForUpdate.updateDetails(userContext);

        HibernateTransactionHelper transactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();

        try {
            transactionHelper.startTransaction();
            transactionHelper.beginAuditLoggingFor(savingsProductForUpdate);

            if (activeOrInactiveSavingsAccountExist) {
                LocalDate updateDate = new LocalDate();
                savingsProductForUpdate.updateProductDetails(newSavingsDetails.getPrdOfferingName(), newSavingsDetails.getPrdOfferingShortName(),
                        newSavingsDetails.getDescription(), newSavingsDetails.getPrdCategory(), newSavingsDetails.getStartDate(), newSavingsDetails.getEndDate(),
                        newSavingsDetails.getPrdStatus());

                savingsProductForUpdate.updateSavingsDetails(newSavingsDetails.getRecommendedAmount(),
                        newSavingsDetails.getRecommendedAmntUnit(), newSavingsDetails.getMaxAmntWithdrawl(),
                        newSavingsDetails.getInterestRate(), newSavingsDetails.getMinAmntForInt(), updateDate);
            } else {
                savingsProductForUpdate.updateDetailsOfProductNotInUse(newSavingsDetails.getPrdOfferingName(), newSavingsDetails.getPrdOfferingShortName(),
                        newSavingsDetails.getDescription(), newSavingsDetails.getPrdCategory(), newSavingsDetails.getStartDate(), newSavingsDetails.getEndDate(),
                        newSavingsDetails.getPrdApplicableMaster(), newSavingsDetails.getPrdStatus());

                savingsProductForUpdate.updateDetailsOfSavingsProductNotInUse(newSavingsDetails.getSavingsType(), newSavingsDetails
                        .getRecommendedAmount(), newSavingsDetails.getRecommendedAmntUnit(), newSavingsDetails
                        .getMaxAmntWithdrawl(), newSavingsDetails.getInterestRate(), newSavingsDetails
                        .getInterestCalcType(), newSavingsDetails.getTimePerForInstcalc(), newSavingsDetails
                        .getFreqOfPostIntcalc(), newSavingsDetails.getMinAmntForInt());
            }

            this.savingsProductDao.save(savingsProductForUpdate);
            transactionHelper.commitTransaction();
            return savingsProductForUpdate.toDto();
        } catch (Exception e) {
            transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            transactionHelper.closeSession();
        }
    }

    @Override
    public PrdOfferingDto createSavingsProduct(SavingsProductDto savingsProductRequest) {

        // enforced by integrity constraints on table also.
        this.savingsProductDao.validateProductWithSameNameDoesNotExist(savingsProductRequest.getProductDetails().getName());
        this.savingsProductDao.validateProductWithSameShortNameDoesNotExist(savingsProductRequest.getProductDetails().getShortName());

        // domain rule validation - put on domain entity
        validateStartDateIsNotBeforeToday(savingsProductRequest.getProductDetails().getStartDate());
        validateStartDateIsNotOverOneYearFromToday(savingsProductRequest.getProductDetails().getStartDate());
        validateEndDateIsPastStartDate(savingsProductRequest.getProductDetails().getStartDate(), savingsProductRequest.getProductDetails().getEndDate());

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        SavingsOfferingBO savingsProduct = new SavingsProductAssembler(this.loanProductDao, this.savingsProductDao, this.generalLedgerDao).fromDto(user, savingsProductRequest);

        HibernateTransactionHelper transactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();

        try {
            transactionHelper.startTransaction();
            this.savingsProductDao.save(savingsProduct);
            transactionHelper.commitTransaction();
            return savingsProduct.toDto();
        } catch (Exception e) {
            transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            transactionHelper.closeSession();
        }
    }

    private void validateEndDateIsPastStartDate(DateTime startDate, DateTime endDate) {
        if (endDate != null) {
            if (new LocalDate(endDate).isBefore(new LocalDate(startDate))) {
                throw new BusinessRuleException("Min.generalDetails.endDate");
            }
        }
    }

    private void validateStartDateIsNotOverOneYearFromToday(DateTime startDate) {
        LocalDate oneYearFromToday = new LocalDate(new DateTime().plusYears(1));
        if (new LocalDate(startDate).isAfter(oneYearFromToday)) {
            throw new BusinessRuleException("Max.generalDetails.startDate");
        }
    }

    private void validateStartDateIsNotBeforeToday(DateTime startDate) {
        LocalDate today = new LocalDate(new DateTime());
        if (new LocalDate(startDate).isBefore(today)) {
            throw new BusinessRuleException("Min.generalDetails.startDate");
        }
    }

    @Override
    public PrdOfferingDto createLoanProduct(LoanProductRequest loanProductRequest) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        LoanOfferingBO loanProduct = this.loanProductAssembler.fromDto(user, loanProductRequest);

        HibernateTransactionHelper transactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();

        try {
            transactionHelper.startTransaction();
            this.loanProductDao.save(loanProduct);
            transactionHelper.commitTransaction();
            return loanProduct.toDto();
        } catch (Exception e) {
            transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            transactionHelper.closeSession();
        }
    }

    @Override
    public SavingsProductDto retrieveSavingsProductDetails(Integer productId) {

        SavingsOfferingBO savingsProduct = this.savingsProductDao.findById(productId);

        boolean openSavingsAccountsExist = this.savingsProductDao.activeOrInactiveSavingsAccountsExistForProduct(productId);

        SavingsProductDto dto = savingsProduct.toFullDto();
        dto.setOpenSavingsAccountsExist(openSavingsAccountsExist);
        return dto;
    }

    @Override
    public LoanProductRequest retrieveLoanProductDetails(Integer productId) {
        LoanOfferingBO loanProduct = this.loanProductDao.findById(productId);
        boolean multiCurrencyEnabled = AccountingRules.isMultiCurrencyEnabled();

        LoanProductRequest productDetails = loanProduct.toFullDto();
        productDetails.setMultiCurrencyEnabled(multiCurrencyEnabled);
        return productDetails;
    }

    @Override
    public List<AuditLogDto> retrieveSavingsProductAuditLogs(Integer productId) {
        List<AuditLogDto> auditLogDtos = new ArrayList<AuditLogDto>();
        AuditBusinessService auditBusinessService = new AuditBusinessService();
        try {
            List<AuditLogView> auditLogs = auditBusinessService.getAuditLogRecords(EntityType.SAVINGSPRODUCT.getValue(), productId);
            for (AuditLogView auditLogView : auditLogs) {
                auditLogDtos.add(auditLogView.toDto());
            }
            return auditLogDtos;
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public List<AuditLogDto> retrieveLoanProductAuditLogs(Integer productId) {
        List<AuditLogDto> auditLogDtos = new ArrayList<AuditLogDto>();
        AuditBusinessService auditBusinessService = new AuditBusinessService();
        try {
            List<AuditLogView> auditLogs = auditBusinessService.getAuditLogRecords(EntityType.LOANPRODUCT.getValue(), productId);
            for (AuditLogView auditLogView : auditLogs) {
                auditLogDtos.add(auditLogView.toDto());
            }
            return auditLogDtos;
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public List<ReportCategoryDto> retrieveReportCategories() {

        List<ReportCategoryDto> reportCategories = new ArrayList<ReportCategoryDto>();

        List<ReportsCategoryBO> allCategories = new ReportsPersistence().getAllReportCategories();
        for (ReportsCategoryBO category : allCategories) {
            reportCategories.add(category.toDto());
        }

        return reportCategories;
    }

    @Override
    public void createReportsCategory(ReportCategoryDto reportCategory) {

        ReportsCategoryBO newReportCategory = new ReportsCategoryBO();
        newReportCategory.setReportCategoryName(reportCategory.getName());
        try {
            new ReportsPersistence().createOrUpdate(newReportCategory);
            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    @Override
    public ReportCategoryDto retrieveReportCategory(Integer reportCategoryId) {

        ReportsCategoryBO reportsCategoryBO = new ReportsPersistence().getReportCategoryByCategoryId(reportCategoryId.shortValue());
        return reportsCategoryBO.toDto();
    }

    @Override
    public Locale retreiveLocaleFromConfiguration() {
        return Localization.getInstance().getMainLocale();
    }
}