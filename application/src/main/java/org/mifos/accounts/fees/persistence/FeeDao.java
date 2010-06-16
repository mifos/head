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

package org.mifos.accounts.fees.persistence;

import java.util.List;

import org.mifos.accounts.fees.business.CategoryTypeEntity;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeFormulaEntity;
import org.mifos.accounts.fees.business.FeeFrequencyTypeEntity;
import org.mifos.accounts.fees.business.FeePaymentEntity;
import org.mifos.accounts.fees.servicefacade.FeeDto;

public interface FeeDao {

    FeeBO findById(Short feeId);

    FeeDto findDtoById(Short feeId);

    List<FeeDto> retrieveAllProductFees();

    List<FeeDto> retrieveAllCustomerFees();

    List<CategoryTypeEntity> retrieveFeeCategories();

    List<FeeFormulaEntity> retrieveFeeFormulae();

    List<FeeFrequencyTypeEntity> retrieveFeeFrequencies();

    List<FeePaymentEntity> retrieveFeePayments();
}
