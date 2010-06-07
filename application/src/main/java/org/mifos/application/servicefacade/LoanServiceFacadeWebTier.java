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
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fees.business.service.FeeBusinessService;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.loan.struts.action.LoanCreationGlimDto;
import org.mifos.accounts.loan.util.helpers.LoanAccountDetailsDto;
import org.mifos.accounts.productdefinition.business.LoanAmountOption;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingFundEntity;
import org.mifos.accounts.productdefinition.business.LoanOfferingInstallmentRange;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.accounts.productdefinition.business.service.LoanProductService;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.productdefinition.util.helpers.PrdOfferingDto;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldDto;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomValueDto;
import org.mifos.application.master.business.CustomValueListElementDto;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.MeetingDetailsEntity;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.config.AccountingRules;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.util.helpers.CustomerDetailDto;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.security.util.UserContext;

/**
 * Implementation of {@link LoanServiceFacade} for web application usage.
 */
public class LoanServiceFacadeWebTier implements LoanServiceFacade {

    private final LoanProductDao loanProductDao;
    private final CustomerDao customerDao;

    public LoanServiceFacadeWebTier(final LoanProductDao loanProductDao, final CustomerDao customerDao) {
        this.loanProductDao = loanProductDao;
        this.customerDao = customerDao;
    }

    @Override
    public List<PrdOfferingDto> retrieveActiveLoanProductsApplicableForCustomer(final CustomerBO customer) {

        final List<LoanOfferingBO> applicableLoanProducts = new ArrayList<LoanOfferingBO>();

        final List<LoanOfferingBO> loanOfferings = loanProductDao.findActiveLoanProductsApplicableToCustomerLevel(customer
                .getCustomerLevel());

        final MeetingBO customerMeeting = customer.getCustomerMeetingValue();
        for (LoanOfferingBO loanProduct : loanOfferings) {
            if (MeetingBO.isMeetingMatched(customerMeeting, loanProduct.getLoanOfferingMeetingValue())) {
                applicableLoanProducts.add(loanProduct);
            }
        }

        List<PrdOfferingDto> applicationLoanProductDtos = new ArrayList<PrdOfferingDto>();
        for (LoanOfferingBO loanProduct : applicableLoanProducts) {
            applicationLoanProductDtos.add(loanProduct.toDto());
        }

        return applicationLoanProductDtos;
    }

    @Override
    public LoanCreationGlimDto retrieveGlimSpecificDataForGroup(final CustomerBO customer) {

        final List<ValueListElement> loanPurposes = loanProductDao.findAllLoanPurposes();

        final List<ClientBO> activeClientsOfGroup = customerDao.findActiveClientsUnderGroup(customer);

        return new LoanCreationGlimDto(loanPurposes, activeClientsOfGroup);
    }

    @Override
    public LoanCreationProductDetailsDto retrieveGetProductDetailsForLoanAccountCreation(final Integer customerId) throws ApplicationException {

        final CustomerBO customer = customerDao.findCustomerById(customerId);

        final CustomerDetailDto customerDetailDto = customer.toCustomerDetailDto();
        final Date nextMeetingDate = customer.getCustomerAccount().getNextMeetingDate();
        final String recurMonth = customer.getCustomerMeeting().getMeeting().getMeetingDetails().getRecurAfter().toString();
        final boolean isGroup = customer.isGroup();
        final boolean isGlimEnabled = new ConfigurationPersistence().isGlimEnabled();

        final List<PrdOfferingDto> loanProductDtos = retrieveActiveLoanProductsApplicableForCustomer(customer);

        LoanCreationGlimDto loanCreationGlimDto = null;
        List<LoanAccountDetailsDto> clientDetails = new ArrayList<LoanAccountDetailsDto>();

        if (isGroup && isGlimEnabled) {
            loanCreationGlimDto = retrieveGlimSpecificDataForGroup(customer);

            final List<ClientBO> activeClientsOfGroup = loanCreationGlimDto.getActiveClientsOfGroup();

            if (activeClientsOfGroup == null || activeClientsOfGroup.isEmpty()) {
                throw new ApplicationException(GroupConstants.IMPOSSIBLE_TO_CREATE_GROUP_LOAN);
            }

            for (ClientBO client : activeClientsOfGroup) {
                LoanAccountDetailsDto clientDetail = new LoanAccountDetailsDto();
                clientDetail.setClientId(client.getCustomerId().toString());
                clientDetail.setClientName(client.getDisplayName());
                clientDetails.add(clientDetail);
            }
        }

        return new LoanCreationProductDetailsDto(loanProductDtos, customerDetailDto, nextMeetingDate, recurMonth, isGroup, isGlimEnabled, loanCreationGlimDto, clientDetails);
    }

    @Override
    public LoanCreationLoanDetailsDto retrieveLoanDetailsForLoanAccountCreation(UserContext userContext, Integer customerId, Short productId) throws ApplicationException {

        List<FeeDto> additionalFees = new ArrayList<FeeDto>();
        List<FeeDto> defaultFees = new ArrayList<FeeDto>();

        new LoanProductService(new LoanPrdBusinessService(), new FeeBusinessService()).getDefaultAndAdditionalFees(productId, userContext, defaultFees, additionalFees);

        LoanOfferingBO loanOffering = new LoanPrdBusinessService().getLoanOffering(productId, userContext.getLocaleId());

        if (AccountingRules.isMultiCurrencyEnabled()) {
            defaultFees = getFilteredFeesByCurrency(defaultFees, loanOffering.getCurrency().getCurrencyId());
            additionalFees = getFilteredFeesByCurrency(additionalFees, loanOffering.getCurrency().getCurrencyId());
        }

        // setDateIntoForm
        CustomerBO customer = customerDao.findCustomerById(customerId);
        LoanAmountOption eligibleLoanAmount = loanOffering.eligibleLoanAmount(customer.getMaxLoanAmount(loanOffering), customer.getMaxLoanCycleForProduct(loanOffering));
        LoanOfferingInstallmentRange eligibleNoOfInstall = loanOffering.eligibleNoOfInstall(customer.getMaxLoanAmount(loanOffering), customer.getMaxLoanCycleForProduct(loanOffering));

        CustomValueDto customValueDto = new MasterPersistence().getLookUpEntity(MasterConstants.COLLATERAL_TYPES, userContext.getLocaleId());
        List<CustomValueListElementDto> collateralTypes = customValueDto.getCustomValueListElements();

        // Business activities got in getPrdOfferings also but only for glim.
        List<ValueListElement> loanPurposes = new MasterDataService().retrieveMasterEntities(MasterConstants.LOAN_PURPOSES, userContext.getLocaleId());

        List<CustomFieldDefinitionEntity> customFieldDefs = new AccountBusinessService().retrieveCustomFieldsDefinition(EntityType.LOAN);

        List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();
        for (CustomFieldDefinitionEntity fieldDef : customFieldDefs) {
            if (StringUtils.isNotBlank(fieldDef.getDefaultValue())
                    && fieldDef.getFieldType().equals(CustomFieldType.DATE.getValue())) {
                customFields.add(new CustomFieldDto(fieldDef.getFieldId(), DateUtils.getUserLocaleDate(userContext.getPreferredLocale(), fieldDef.getDefaultValue()), fieldDef.getFieldType()));
            } else {
                customFields.add(new CustomFieldDto(fieldDef.getFieldId(), fieldDef.getDefaultValue(), fieldDef
                        .getFieldType()));
            }
        }

        MeetingDetailsEntity loanOfferingMeetingDetail = loanOffering.getLoanOfferingMeeting().getMeeting().getMeetingDetails();
        RecurrenceType loanProductRecurrenceType = loanOfferingMeetingDetail.getRecurrenceTypeEnum();

        List<FundBO> funds = getFunds(loanOffering);

        boolean isRepaymentIndependentOfMeetingEnabled = new ConfigurationBusinessService().isRepaymentIndepOfMeetingEnabled();

        return new LoanCreationLoanDetailsDto(isRepaymentIndependentOfMeetingEnabled, additionalFees, defaultFees,
                loanOfferingMeetingDetail, loanProductRecurrenceType, customer.getCustomerMeetingValue().getMeetingDetails(), loanOffering, eligibleLoanAmount, eligibleNoOfInstall, collateralTypes, loanPurposes, customFieldDefs, customFields, funds);
    }

    private List<FundBO> getFunds(final LoanOfferingBO loanOffering) {
        List<FundBO> funds = new ArrayList<FundBO>();
        if (loanOffering.getLoanOfferingFunds() != null && loanOffering.getLoanOfferingFunds().size() > 0) {
            for (LoanOfferingFundEntity loanOfferingFund : loanOffering.getLoanOfferingFunds()) {
                funds.add(loanOfferingFund.getFund());
            }
        }
        return funds;
    }

    private List<FeeDto> getFilteredFeesByCurrency(List<FeeDto> defaultFees, Short currencyId) {
        List<FeeDto> filteredFees = new ArrayList<FeeDto>();
        for (FeeDto feeDto : defaultFees) {
            if (feeDto.isValidForCurrency(currencyId)) {
                filteredFees.add(feeDto);
            }
        }
        return filteredFees;
    }
}