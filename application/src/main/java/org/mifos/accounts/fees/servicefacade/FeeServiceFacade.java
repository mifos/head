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
import org.mifos.application.servicefacade.FeeDetailsForLoadDto;
import org.mifos.application.servicefacade.FeeDetailsForManageDto;
import org.mifos.application.servicefacade.FeeDetailsForPreviewDto;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.security.util.UserContext;

public interface FeeServiceFacade {

    public List<FeeDto> getProductFees();

    public List<FeeDto> getCustomerFees();

    public FeeParameters parameters(Short localeId) throws ApplicationException;

    public FeeDto createFee(FeeCreateRequest feeCreateRequest, UserContext userContext) throws ServiceException;

    public FeeDto getFeeDetails(Short feeId);

    public void updateFee(FeeUpdateRequest feeUpdateRequest, UserContext userContext) throws FeeException;

    public FeeDetailsForLoadDto retrieveDetailsForFeeLoad(Short localeId) throws ApplicationException;

    public FeeDetailsForPreviewDto retrieveDetailsforFeePreview(Short currencyId);

    public FeeDetailsForManageDto retrieveDetailsForFeeManage(Short feeId, Short localeId) throws ApplicationException;
}