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

import org.mifos.accounts.acceptedpaymenttype.business.AcceptedPaymentType;
import org.mifos.accounts.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.accounts.acceptedpaymenttype.util.helpers.PaymentTypeData;
import org.mifos.accounts.productdefinition.business.PrdOfferingBO;
import org.mifos.accounts.productdefinition.business.ProductCategoryBO;
import org.mifos.accounts.productdefinition.business.ProductTypeEntity;
import org.mifos.accounts.productdefinition.business.service.ProductCategoryBusinessService;
import org.mifos.accounts.productdefinition.business.service.ProductService;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.productdefinition.persistence.SavingsProductDao;
import org.mifos.accounts.productsmix.business.service.ProductMixBusinessService;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.config.ClientRules;
import org.mifos.config.util.helpers.HiddenMandatoryFieldNamesConstants;
import org.mifos.customers.office.business.service.MandatoryHiddenFieldService;
import org.mifos.customers.office.business.service.OfficeHierarchyService;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.dto.domain.AcceptedPaymentTypeDto;
import org.mifos.dto.domain.MandatoryHiddenFieldsDto;
import org.mifos.dto.domain.OfficeLevelDto;
import org.mifos.dto.domain.UpdateConfiguredOfficeLevelRequest;
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

    private ProductService productService;
    private OfficeHierarchyService officeHierarchyService;
    private MandatoryHiddenFieldService mandatoryHiddenFieldService;
    private LoanProductDao loanProductDao;
    private SavingsProductDao savingsProductDao;
    private OfficeDao officeDao;


    public AdminServiceFacadeWebTier(ProductService productService, OfficeHierarchyService officeHierarchyService,
                                    MandatoryHiddenFieldService mandatoryHiddenFieldService, LoanProductDao loanProductDao,
                                    SavingsProductDao savingsProductDao, OfficeDao officeDao) {
        this.productService = productService;
        this.officeHierarchyService = officeHierarchyService;
        this.mandatoryHiddenFieldService = mandatoryHiddenFieldService;
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