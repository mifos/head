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
import java.util.List;

import org.mifos.accounts.fees.business.CategoryTypeEntity;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeFormulaEntity;
import org.mifos.accounts.fees.business.FeeFrequencyTypeEntity;
import org.mifos.accounts.fees.business.FeePaymentEntity;
import org.mifos.accounts.fees.business.FeeStatusEntity;
import org.mifos.accounts.fees.business.service.FeeService;
import org.mifos.accounts.fees.persistence.FeeDao;
import org.mifos.accounts.fees.struts.action.FeeParameters;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.financial.business.service.GeneralLedgerDao;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.servicefacade.FeeDetailsForLoadDto;
import org.mifos.application.servicefacade.FeeDetailsForManageDto;
import org.mifos.application.servicefacade.FeeDetailsForPreviewDto;
import org.mifos.config.AccountingRules;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;

public class WebTierFeeServiceFacade implements FeeServiceFacade {

    private final FeeDao feeDao;
    private final GeneralLedgerDao generalLedgerDao;
    private final FeeService feeService;

    public WebTierFeeServiceFacade(FeeService feeService, FeeDao feeDao, GeneralLedgerDao generalLedgerDao) {
        this.feeService = feeService;
        this.feeDao = feeDao;
        this.generalLedgerDao = generalLedgerDao;
    }

    public List<FeeDto> getProductFees() {

        return this.feeDao.retrieveAllProductFees();
    }

    public List<FeeDto> getCustomerFees() {

        return this.feeDao.retrieveAllCustomerFees();
    }

    @Override
    public FeeParameters parameters(Short localeId) throws ApplicationException {
        List<GLCodeEntity> glCodes = generalLedgerDao.retreiveGlCodesBy(FinancialActionConstants.FEEPOSTING, FinancialConstants.CREDIT);

        List<CategoryTypeEntity> categories = this.feeDao.retrieveFeeCategories();
        List<FeeFormulaEntity> formulas = this.feeDao.retrieveFeeFormulae();
        List<FeeFrequencyTypeEntity> frequencies = this.feeDao.retrieveFeeFrequencies();
        List<FeePaymentEntity> timesOfCharging = this.feeDao.retrieveFeePayments();

        List<MasterDataEntity> timesOfChargeingCustomer = new ArrayList<MasterDataEntity>();
        for (MasterDataEntity timeOfCharging : timesOfCharging) {
            if (timeOfCharging.getId().equals(FeePayment.UPFRONT.getValue())) {
                timesOfChargeingCustomer.add(timeOfCharging);
            }
        }

        return new FeeParameters(categories, timesOfCharging, timesOfChargeingCustomer, formulas, frequencies, glCodes);
    }

    @Override
    public FeeDto getFeeDetails(Short feeId) {
        return this.feeDao.findDtoById(feeId);
    }

    @Override
    public FeeDto createFee(FeeCreateRequest feeCreateRequest, UserContext userContext) throws ApplicationException {

        FeeBO fee = this.feeService.create(feeCreateRequest, userContext);
        return fee.toDto();
    }

    @Override
    public void updateFee(FeeUpdateRequest feeUpdateRequest, UserContext userContext) throws ApplicationException {
        this.feeService.update(feeUpdateRequest, userContext);
    }

    @Override
    public FeeDetailsForLoadDto retrieveDetailsForFeeLoad(Short localeId) throws ApplicationException {
        FeeParameters feeParameters = parameters(localeId);
        boolean isMultiCurrencyEnabled = AccountingRules.isMultiCurrencyEnabled();
        List<MifosCurrency> currencies = AccountingRules.getCurrencies();
        return new FeeDetailsForLoadDto(feeParameters, isMultiCurrencyEnabled, currencies);
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

    @Override
    public FeeDetailsForManageDto retrieveDetailsForFeeManage(Short feeId) throws ApplicationException {
        FeeDto fee = getFeeDetails(feeId);
        List<FeeStatusEntity> feeStatuses = this.feeDao.findAllFeeStatuses();

        return new FeeDetailsForManageDto(fee, feeStatuses);
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