/**

 * PrdOfferingDAO.java    version: xxx

 

 * Copyright © 2005-2006 Grameen Foundation USA

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

import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.valueobjects.PrdOffering;
import org.mifos.application.productdefinition.util.valueobjects.ProductCategory;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.dao.helpers.MasterDataRetriever;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.valueobjects.SearchResults;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class acts as a base class for product offering daos and has methods 
 * common to all of them.
 * @author ashishsm
 *
 */
public class PrdOfferingDAO extends DAO {

	/**
	 * Default Constructor
	 */
	public PrdOfferingDAO() {
	}
	
	/**
	 * This method is used to return a product offering object irrespective of the type of product offering.
	 * It obtains a new HibernateSession and works on it.
	 * @param prdOfferingId
	 * @return
	 * @throws SystemException
	 */
	public ValueObject getPrdOffering(Short prdOfferingId)throws SystemException{
		ValueObject prdOffering = null;
		Session session = null;
		
		try{
			session= HibernateUtil.getSession();
			prdOffering = getPrdOffering(prdOfferingId,session);
		}catch(HibernateException he){
			
			throw new SystemException(ExceptionConstants.SYSTEMEXCEPTION,he);
		}finally{
			HibernateUtil.closeSession(session);
		}
		return prdOffering;
	}
	
	/**
	 * This method is used to return a product offering object irrespective of the type of product offering.
	 * It works on the hibernate session object passed to it.
	 * @param prdOfferingId
	 * @param session
	 * @return
	 * @throws HibernateException
	 */
	public ValueObject getPrdOffering(Short prdOfferingId,Session session)throws HibernateException{
		ValueObject prdOffering = null;
		if(null != session){
			
			prdOffering = (PrdOffering)session.get(PrdOffering.class, prdOfferingId);
			
		}
		return prdOffering;
	}
	
	/**
	 * This method returns the product categories associated with the product type and status.
	 * 
	 * @param productTypeID
	 * @param prdCategoryStatusId
	 * @return
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public List<ProductCategory> getProductCategories(Short productTypeID,Short prdCategoryStatusId) throws ApplicationException,SystemException {
		Session session=null;
		try {
			session = HibernateUtil.getSession();
			Query query = session.getNamedQuery(NamedQueryConstants.PRDLOAN_CATEGORIES);
			query.setShort(ProductDefinitionConstants.PRODUCTTYPEID,productTypeID);
			query.setShort(ProductDefinitionConstants.PRODUCTCATEGORYSTATUSID,prdCategoryStatusId);
			List<ProductCategory> productCategoryList=query.list();
			for(Iterator<ProductCategory> iter=productCategoryList.iterator();iter.hasNext();) {
				ProductCategory productCategory = iter.next();
				productCategory.getProductType();
			}
			return productCategoryList;
		}catch(HibernateProcessException hbe) {
			throw new SystemException();
		}catch(Exception e) {
			throw new ApplicationException(e);
		}finally {
			HibernateUtil.closeSession(session);
		}
	}
	
	/**
	 * This method returns the applicable for  associated with the product type.
	 * 
	 * @param localeId
	 * @param searchName
	 * @return
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults  getPrdApplFor(Short localeId,String searchName) throws ApplicationException,SystemException {
		try {
			MasterDataRetriever masterDataRetriever=new MasterDataRetriever();
			masterDataRetriever.prepare(NamedQueryConstants.PRDAPPLFOR,searchName);
			masterDataRetriever.setParameter(ProductDefinitionConstants.LOCALEID,localeId);
			return masterDataRetriever.retrieve();
		} catch (HibernateProcessException hbe) {
			throw new SystemException();
		} catch(Exception e) {
			throw new ApplicationException(e);
		}
	}
	
	/**
	 * This method returns the product status  associated with the product type,loacle id  and status.
	 * 
	 * @param productTypeID
	 * @param localeId
	 * @param status
	 * @param searchName
	 * @return
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults  getPrdStatus(Short productTypeID,Short localeId,Short status,String searchName) throws ApplicationException,SystemException {
		try {
			MasterDataRetriever masterDataRetriever=new MasterDataRetriever();
			masterDataRetriever.prepare(NamedQueryConstants.PRDSTATUS,searchName);
			masterDataRetriever.setParameter(ProductDefinitionConstants.PRODUCTTYPEID,productTypeID);
			masterDataRetriever.setParameter(ProductDefinitionConstants.STATUS,status);
			masterDataRetriever.setParameter(ProductDefinitionConstants.LOCALEID,localeId);
			return masterDataRetriever.retrieve();
		} catch (HibernateProcessException hbe) {
			throw new SystemException();
		} catch(Exception e) {
			throw new ApplicationException(e);
		}
	}
	
	/**
	 * Checks if the product with the specified name already exists in the database.
	 * @param productInstanceName - Name of the productInstance to be checked in the database.
	 * @return - true if product with same name already exists
	 * @throws SystemException- this is thrown if there is any hibernate exception
	 */
	public boolean ifCreateProductNameExists(String prdOfferingName) throws SystemException, ApplicationException {
		Session session = null;
		try {
			session = HibernateUtil.getSession();
		
			Query query = session.getNamedQuery(NamedQueryConstants.PRD_CREATE_NAME_COUNT);
			query.setString(ProductDefinitionConstants.PRDOFFERINGNAME, prdOfferingName);
			int size = (Integer) query.uniqueResult();
			if (size > 0) {
				return true;
			}
			return false;
		} catch (HibernateProcessException hbe) {
			throw new SystemException();
		} catch (Exception exception) {
			throw new ApplicationException(exception);
		}finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * Checks if the product with the specified name and different id already exists in the database.
	 * @param productInstanceName - Name of the productInstance to be checked in the database.
	 * @return - true if product with same name already exists
	 * @throws SystemException- this is thrown if there is any hibernate exception
	 */
	public boolean ifUpdateProductNameExists(String prdOfferingName,Short prdOfferingId)
	throws SystemException, ApplicationException {
		Session session = null;
		try {
			session = HibernateUtil.getSession();
		
			Query query = session.getNamedQuery(NamedQueryConstants.PRD_UPDATE_NAME_COUNT);
			query.setString(ProductDefinitionConstants.PRDOFFERINGNAME, prdOfferingName);
			query.setShort(ProductDefinitionConstants.PRDOFFERINGID, prdOfferingId);
			int size = (Integer) query.uniqueResult();
			if (size > 0) {
				return true;
			}
			return false;
		} catch (HibernateProcessException hbe) {
			throw new SystemException();
		} catch (Exception exception) {
			throw new ApplicationException(exception);
		}finally {
			HibernateUtil.closeSession(session);
		}
	}
	
	/**
	 * Checks if the product with the specified short name already exists in the database.
	 * @param prdOfferingShortName -Short Name of the productInstance to be checked in the database.
	 * @return - true if product with same short name already exists
	 * @throws SystemException- this is thrown if there is any hibernate exception
	 */
	public boolean ifCreateProductShortNameExists(String prdOfferingShortName) throws SystemException, ApplicationException {
		Session session = null;
		try {
			session = HibernateUtil.getSession();
		
			Query query = session.getNamedQuery(NamedQueryConstants.PRD_CREATE_SHORTNAME_COUNT);
			query.setString(ProductDefinitionConstants.PRDOFFERINGSHORTNAME, prdOfferingShortName);
			int size = (Integer) query.uniqueResult();
			if (size > 0) {
				return true;
			}
			return false;
		} catch (HibernateProcessException hbe) {
			throw new SystemException();
		} catch (Exception exception) {
			throw new ApplicationException(exception);
		}finally {
			HibernateUtil.closeSession(session);
		}
	}
	
	/**
	 * Checks if the product with the specified short name and different id already exists in the database.
	 * @param prdOfferingShortName - Short Name of the productInstance to be checked in the database.
	 * @return - true if product with same short name already exists
	 * @throws SystemException- this is thrown if there is any hibernate exception
	 */
	public boolean ifUpdateProductShortNameExists(String prdOfferingShortName,Short prdOfferingId)
	throws SystemException, ApplicationException {
		Session session = null;
		try {
			session = HibernateUtil.getSession();
		
			Query query = session.getNamedQuery(NamedQueryConstants.PRD_UPDATE_SHORTNAME_COUNT);
			query.setString(ProductDefinitionConstants.PRDOFFERINGSHORTNAME, prdOfferingShortName);
			query.setShort(ProductDefinitionConstants.PRDOFFERINGID, prdOfferingId);
			int size = (Integer) query.uniqueResult();
			if (size > 0) {
				return true;
			}
			return false;
		} catch (HibernateProcessException hbe) {
			throw new SystemException();
		} catch (Exception exception) {
			throw new ApplicationException(exception);
		}finally {
			HibernateUtil.closeSession(session);
		}
	}

}
