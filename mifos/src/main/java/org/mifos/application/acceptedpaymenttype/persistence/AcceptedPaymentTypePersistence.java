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
 
package org.mifos.application.acceptedpaymenttype.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.acceptedpaymenttype.business.AcceptedPaymentType;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.Persistence;




	public class AcceptedPaymentTypePersistence extends Persistence {

		public AcceptedPaymentType getAcceptedPaymentType(Short paymentTypeId) {
			Session session = StaticHibernateUtil.getSessionTL();
			return (AcceptedPaymentType) session.get(AcceptedPaymentType.class, paymentTypeId);
		}
		
		public List<AcceptedPaymentType> getAcceptedPaymentTypesForATransaction(Short transactionId) throws PersistenceException {
			HashMap<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("transactionId", transactionId);
			
			return executeNamedQuery(
				NamedQueryConstants.GET_ACCEPTED_PAYMENT_TYPES_FOR_A_TRANSACTION, queryParameters);
			
		}
		
		public void addAcceptedPaymentTypes(List<AcceptedPaymentType> acceptedPaymentTypeList) throws PersistenceException {
			for (AcceptedPaymentType paymentType : acceptedPaymentTypeList)
				createOrUpdate(paymentType);
		}


		// delete a list of accepted payment type for account actions
		public void deleteAcceptedPaymentTypes(List<AcceptedPaymentType> acceptedPaymentTypeList) throws PersistenceException {
			for (AcceptedPaymentType paymentType : acceptedPaymentTypeList)
				delete(paymentType);
		}
		
		
		public List<PaymentTypeEntity> getAcceptedPaymentTypesForATransaction(Short localeId, 
				Short transactionId) throws Exception {

			HashMap<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("transactionId", transactionId);
			
			List<AcceptedPaymentType> acceptedPaymentTypeList =  executeNamedQuery(
				NamedQueryConstants.GET_ACCEPTED_PAYMENT_TYPES_FOR_A_TRANSACTION, queryParameters);
			List<PaymentTypeEntity> paymentTypeList = new ArrayList<PaymentTypeEntity>();
			for (AcceptedPaymentType acceptedPaymentType :  acceptedPaymentTypeList)
			{
				PaymentTypeEntity paymentTypeEntity = acceptedPaymentType.getPaymentTypeEntity();
				// the localeId is set so when the paymentTypeEntity.getName is called the localeId will be used
				// to determine what the name to match with that localeId
				paymentTypeEntity.setLocaleId(localeId);
				paymentTypeList.add(paymentTypeEntity);		
			}
			
			return paymentTypeList;
		}

		
	}

