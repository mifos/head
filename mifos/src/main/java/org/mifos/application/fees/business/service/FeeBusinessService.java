/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
 
package org.mifos.application.fees.business.service;

import java.util.List;

import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;

public class FeeBusinessService implements BusinessService {

	private FeePersistence feePersistence;

	public FeeBusinessService() {
		feePersistence = new FeePersistence();
	}

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	};

	public FeeBO getFee(Short feeId) {
		return feePersistence.getFee(feeId);
	}

	public List<FeeBO> retrieveCustomerFees() throws ServiceException {
		try {
			return feePersistence.retrieveCustomerFees();
		} catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}
	}

	public List<FeeBO> retrieveProductFees() throws ServiceException {
		try {
			return feePersistence.retrieveProductFees();
		} catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}
	}
	
	public List<FeeBO> retrieveCustomerFeesByCategaroyType(FeeCategory feeCategory) throws ServiceException {
		try {
			return feePersistence.retrieveCustomerFeesByCategaroyType(feeCategory);
		} catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}
	}
	
	public List<FeeBO> getAllApplicableFeesForLoanCreation()
			throws ServiceException {
		try {
			return feePersistence.getAllAppllicableFeeForLoanCreation();
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
}
