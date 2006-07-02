/**

 * PrdConfigurationDAO.java    version: xxx

 

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

package org.mifos.application.productdefinition.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.valueobjects.ProductType;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ConcurrencyException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

/**
 * This class is used to make the databse related calls in product configuration.
 * 
 * @author mohammedn
 *
 */
public class PrdConfigurationDAO extends DAO {

	/**
	 * default constructor
	 */
	public PrdConfigurationDAO() {
	}
	
	/**
	 * Updates the product configuration like lateness and dormancy information.
	 * Before updating it checks if there is already any product instance with the same name and id  
	 * @see org.mifos.framework.dao.DAO#update(org.mifos.framework.util.valueobjects.Context)
	 */
	public void update(ProductType productType)throws SystemException,ApplicationException{
		Session session = null;
		Transaction transaction =null;
		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();
			session.update(productType);
			transaction.commit();
		} catch (StaleObjectStateException sse) {
			if(null != transaction) {
				transaction.rollback();
			}
			throw new ConcurrencyException(sse);
		} catch (HibernateProcessException hbe) {
			if(null != transaction) {
				transaction.rollback();
			}
			throw new SystemException(hbe);
		} catch (Exception exception) {
			if(null !=transaction) {
				transaction.rollback();
			}
			throw new ProductDefinitionException(ProductDefinitionConstants.PRDINVALID);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}
	
	/**
	 * Gets the product type value object which has the configuration info like lateness and dormancy.
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public List<ProductType> search(short loanId,short savingsId)throws SystemException,ApplicationException{
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			Query query = session.getNamedQuery(NamedQueryConstants.PRDCONFIGURATION_SEARCH);
			query.setShort(ProductDefinitionConstants.LOANPRODUCTID,loanId);
			query.setShort(ProductDefinitionConstants.SAVINGSPRODUCTID,savingsId);
			List<ProductType> productTypeList=query.list();
			return productTypeList;
		} catch (HibernateProcessException hbe) {
			throw new SystemException(hbe);
		} catch (Exception exception) {
			throw new ProductDefinitionException(ProductDefinitionConstants.PRDINVALID);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

}
