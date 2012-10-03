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

package org.mifos.accounts.fees.business.service;

import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.servicefacade.FeeCreateRequest;
import org.mifos.dto.domain.FeeUpdateRequest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.security.util.UserContext;

public interface FeeService {

    FeeBO create(FeeCreateRequest feeCreateRequest, UserContext userContext) throws ApplicationException;

    void update(FeeUpdateRequest feeUpdateRequest, UserContext userContext) throws ApplicationException;
    
    void remove(FeeBO fee, boolean isInProducts, boolean isFeeAppliedToLoan, boolean remove);

}
