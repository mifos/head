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

package org.mifos.accounts.fees.servicefacade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.accounts.fees.business.CategoryTypeEntity;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeFormulaEntity;
import org.mifos.accounts.fees.business.FeeFrequencyTypeEntity;
import org.mifos.accounts.fees.business.FeePaymentEntity;
import org.mifos.accounts.fees.business.service.FeeService;
import org.mifos.accounts.fees.persistence.FeeDao;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeeFormula;
import org.mifos.accounts.fees.util.helpers.FeeFrequencyType;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.financial.business.service.GeneralLedgerDao;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.application.admin.servicefacade.FeeServiceFacade;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.config.AccountingRules;
import org.mifos.customers.personnel.business.PersonnelLevelEntity;
import org.mifos.customers.personnel.business.PersonnelStatusEntity;
import org.mifos.dto.domain.FeeCreateDto;
import org.mifos.dto.domain.FeeUpdateRequest;
import org.mifos.dto.screen.FeeDetailsForLoadDto;
import org.mifos.dto.screen.FeeDetailsForPreviewDto;
import org.mifos.dto.screen.FeeParameters;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.springframework.security.core.context.SecurityContextHolder;

public class FeeServiceFacadeWebTier implements FeeServiceFacade {

    private final FeeDao feeDao;
    private final GeneralLedgerDao generalLedgerDao;
    private final FeeService feeService;

    public FeeServiceFacadeWebTier(FeeService feeService, FeeDao feeDao, GeneralLedgerDao generalLedgerDao) {
        this.feeService = feeService;
        this.feeDao = feeDao;
        this.generalLedgerDao = generalLedgerDao;
    }

    private FeeParameters parameters() {
        try {
            List<GLCodeEntity> glCodes = generalLedgerDao.retreiveGlCodesBy(FinancialActionConstants.FEEPOSTING, FinancialConstants.CREDIT);

            List<CategoryTypeEntity> categories = this.feeDao.doRetrieveFeeCategories();
            List<FeeFormulaEntity> formulas = this.feeDao.retrieveFeeFormulae();
            List<FeeFrequencyTypeEntity> frequencies = this.feeDao.retrieveFeeFrequencies();
            List<FeePaymentEntity> timesOfCharging = this.feeDao.retrieveFeePayments();

            List<MasterDataEntity> timesOfChargeingCustomer = new ArrayList<MasterDataEntity>();
            for (MasterDataEntity timeOfCharging : timesOfCharging) {
                if (timeOfCharging.getId().equals(FeePayment.UPFRONT.getValue())) {
                    timesOfChargeingCustomer.add(timeOfCharging);
                }
            }

            return new FeeParameters(listToMap(categories), listToMap(timesOfCharging), listToMap(timesOfChargeingCustomer), listToMap(formulas),
                    listToMap(frequencies), glCodesToMap(glCodes));
        } catch (FinancialException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    private Map<Short, String> glCodesToMap(List<GLCodeEntity> glCodes) {
        Map<Short, String> idCodeMap = new HashMap<Short, String>();
        for (GLCodeEntity glCode : glCodes) {
            idCodeMap.put(glCode.getGlcodeId(), glCode.getGlcode());
        }
        return idCodeMap;
    }

    private Map<Short, String> listToMap(List<? extends MasterDataEntity> masterDataEntityList) {
        Map<Short, String> idNameMap = new HashMap<Short, String>();
        for (MasterDataEntity masterDataEntity : masterDataEntityList) {

            if (masterDataEntity instanceof PersonnelStatusEntity) {
                String name = MessageLookup.getInstance().lookup(masterDataEntity.getLookUpValue());
                ((PersonnelStatusEntity) masterDataEntity).setName(name);
            }

            if (masterDataEntity instanceof PersonnelLevelEntity) {
                String name = MessageLookup.getInstance().lookup(masterDataEntity.getLookUpValue());
                ((PersonnelLevelEntity) masterDataEntity).setName(name);
            }

            idNameMap.put(masterDataEntity.getId(), masterDataEntity.getName());
        }
        return idNameMap;
    }

    @Override
    public String createFee(FeeCreateDto feeCreateDto) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);

        try {
            FeeCategory feeCategory = (feeCreateDto.getCategoryType() != null) ? FeeCategory.getFeeCategory(feeCreateDto.getCategoryType()) : null;
            FeeFrequencyType feeFrequencyType = (feeCreateDto.getFeeFrequencyType() != null) ? FeeFrequencyType.getFeeFrequencyType(feeCreateDto.getFeeFrequencyType()) : null;
            FeePayment feePayment = (feeCreateDto.getFeePaymentType() != null) ? FeePayment.getFeePayment(feeCreateDto.getFeePaymentType()) : null;
            FeeFormula feeFormula = (feeCreateDto.getFeeFormula() != null) ? FeeFormula.getFeeFormula(feeCreateDto.getFeeFormula()) : null;
            RecurrenceType feeRecurrenceType = (feeCreateDto.getFeeRecurrenceType() != null) ? RecurrenceType.fromInt(feeCreateDto.getFeeRecurrenceType()) : null;

            FeeCreateRequest feeCreateRequest = new FeeCreateRequest(feeCategory, feeFrequencyType,
                    feeCreateDto.getGlCode(), feePayment, feeFormula, feeCreateDto.getFeeName(),
                    feeCreateDto.isRateFee(), feeCreateDto.isCustomerDefaultFee(),
                    feeCreateDto.getRate(), feeCreateDto.getCurrencyId(),
                    feeCreateDto.getAmount(), feeRecurrenceType, feeCreateDto.getMonthRecurAfter(), feeCreateDto.getWeekRecurAfter());

            FeeBO fee = this.feeService.create(feeCreateRequest, userContext);
            return fee.getFeeId().toString();
        } catch (ApplicationException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    @Override
    public void updateFee(FeeUpdateRequest feeUpdateRequest) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);

        try {
            this.feeService.update(feeUpdateRequest, userContext);
        } catch (ApplicationException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    @Override
    public FeeDetailsForLoadDto retrieveDetailsForFeeLoad() {

        FeeParameters feeParameters = parameters();
        boolean isMultiCurrencyEnabled = AccountingRules.isMultiCurrencyEnabled();
        return new FeeDetailsForLoadDto(feeParameters, isMultiCurrencyEnabled);
    }

    @Override
    public FeeDetailsForPreviewDto retrieveDetailsforFeePreview(Short currencyId) {

        boolean isMultiCurrencyEnabled = AccountingRules.isMultiCurrencyEnabled();
        String currencyCode = "";

        if (isMultiCurrencyEnabled) {
            currencyCode = getCurrency(currencyId).getCurrencyCode();
        }

        return new FeeDetailsForPreviewDto(isMultiCurrencyEnabled, currencyCode);
    }

    private MifosCurrency getCurrency(Short currencyId) {
        MifosCurrency currency;
        if (currencyId == null) {
            // Currency is passed from Form only for Loan (Amount) Fees in multi-currency settings
            currency = Money.getDefaultCurrency();
        } else {
            currency = AccountingRules.getCurrencyByCurrencyId(currencyId);
        }
        return currency;
    }
}