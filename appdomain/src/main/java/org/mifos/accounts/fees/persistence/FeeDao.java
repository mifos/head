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

package org.mifos.accounts.fees.persistence;

import java.util.List;

import org.mifos.accounts.fees.business.CategoryTypeEntity;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeFormulaEntity;
import org.mifos.accounts.fees.business.FeeFrequencyTypeEntity;
import org.mifos.accounts.fees.business.FeePaymentEntity;
import org.mifos.accounts.fees.business.FeeStatusEntity;
import org.mifos.accounts.fees.business.RateFeeBO;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeeFormula;
import org.mifos.accounts.fees.util.helpers.FeeFrequencyType;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.dto.domain.FeeDto;

public interface FeeDao {

    FeeBO findById(Short feeId);

    FeeDto findDtoById(Short feeId);

    void save(FeeBO feeBO);

    List<FeeDto> retrieveAllProductFees();

    List<FeeDto> retrieveAllCustomerFees();

    List<Short> getUpdatedFeesForCustomer();

    List<FeeBO> getAllAppllicableFeeForLoanCreation();

    List<CategoryTypeEntity> doRetrieveFeeCategories();

    List<FeeFormulaEntity> retrieveFeeFormulae();

    List<FeeFrequencyTypeEntity> retrieveFeeFrequencies();

    List<FeePaymentEntity> retrieveFeePayments();

    List<FeeStatusEntity> retrieveFeeStatuses();

    FeeFrequencyTypeEntity findFeeFrequencyEntityByType(FeeFrequencyType feeFrequencyType);

    CategoryTypeEntity findFeeCategoryTypeEntityByType(FeeCategory categoryType);

    FeeFormulaEntity findFeeFormulaEntityByType(FeeFormula feeFormula);

    FeePaymentEntity findFeePaymentEntityByType(FeePayment feePaymentType);
    
    <T> T initializeAndUnproxy(T var); 
    
    Short findFeeAppliedToLoan(Short feeId);
    
    List<Short> getAllUsedLoansWithAttachedFee();
    
    void remove(FeeBO fee, boolean isInProducts, boolean isFeeAppliedToLoan, boolean remove) throws Exception;

	Short findFeeInSchedule(Short feeId);
	
	RateFeeBO findRateFeeById(Short feeId);
}
