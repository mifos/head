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

import java.util.List;

import org.mifos.accounts.fees.exceptions.FeeException;
import org.mifos.accounts.fees.struts.action.FeeParameters;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.security.util.UserContext;

public interface FeeServiceFacade {

    public List<FeeDto> getProductFees() throws ServiceException;

    public List<FeeDto> getCustomerFees() throws ServiceException;

    public FeeParameters getFeeParameters(Short localeId) throws ServiceException;

    public FeeDto createFee(FeeCreateRequest feeCreateRequest, UserContext userContext) throws ServiceException;

    public FeeDto getFeeDetails(Short feeId) throws ServiceException;

    public void updateFee(FeeUpdateRequest feeUpdateRequest, UserContext userContext) throws FeeException;

}
