/**

 * FeePersistence.java    version: 1.0

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */
package org.mifos.application.fees.persistence;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.ApplicableAccountsTypeEntity;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.RateFeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.fees.util.helpers.RateAmountFlag;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;

public class FeePersistence extends Persistence {

	public FeeBO getFee(Short feeId) {
		Session session = HibernateUtil.getSessionTL();
		return (FeeBO) session.get(FeeBO.class, feeId);
	}
	public FeeBO getFee(Short feeId,RateAmountFlag rateflag) throws PersistenceException {

			if ( rateflag.equals(RateAmountFlag.AMOUNT))
			{
				return (AmountFeeBO) getPersistentObject(AmountFeeBO.class
						,feeId);
			}
			else
			{
				return (RateFeeBO) getPersistentObject(RateFeeBO.class
						,feeId);
			}
	}
	public List<FeeBO> getUpdatedFeesForCustomer() throws PersistenceException {
		return executeNamedQuery(
			NamedQueryConstants.GET_UPDATED_FEES_FOR_CUSTOMERS, null);
	}

	// Seems not to be used by anything
	public ApplicableAccountsTypeEntity getUpdateTypeEntity(Short id) 
	throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("ID", id);
		return (ApplicableAccountsTypeEntity) executeNamedQuery(
			NamedQueryConstants.GET_FEE_UPDATETYPE, queryParameters).get(0);
	}

	public List<FeeBO> retrieveCustomerFees() throws PersistenceException {
		return executeNamedQuery(
			NamedQueryConstants.RETRIEVE_CUSTOMER_FEES, null);
	}

	public List<FeeBO> retrieveProductFees() throws PersistenceException {
		
			return executeNamedQuery(NamedQueryConstants.RETRIEVE_PRODUCT_FEES,
					null);
		
	}
	
	public List<FeeBO> retrieveCustomerFeesByCategaroyType(FeeCategory feeCategory) throws PersistenceException {
		
			HashMap<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put(FeeCategory.ALLCUSTOMERS.toString(), FeeCategory.ALLCUSTOMERS.getValue());
			queryParameters.put("CUSTOMER_CATEGAORY", feeCategory.getValue());
			return executeNamedQuery(
					NamedQueryConstants.RETRIEVE_CUSTOMER_FEES_BY_CATEGORY_TYPE, queryParameters);
		
	}
	
	public List<FeeBO> getAllAppllicableFeeForLoanCreation()
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("active", FeeStatus.ACTIVE.getValue());
		queryParameters.put("category", FeeCategory.LOAN.getValue());
		return executeNamedQuery(
				NamedQueryConstants.GET_ALL_APPLICABLE_FEE_FOR_LOAN_CREATION,
				queryParameters);
	}
	
	public RateFeeBO getRateFee(Short feeId) {
		Session session = HibernateUtil.getSessionTL();
		return (RateFeeBO) session.get(RateFeeBO.class, feeId);
	}
}
