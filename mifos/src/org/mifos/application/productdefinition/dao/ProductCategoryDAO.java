/**

 * ProductCategoryDAO.java    version: xxx

 

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.valueobjects.ProductCategory;
import org.mifos.application.productdefinition.util.valueobjects.ProductType;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.dao.helpers.MasterDataRetriever;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ConcurrencyException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This class is used to make the databse related calls in the creation and update of
 * product category.
 * 
 * @author mohammedn
 * 
 */
public class ProductCategoryDAO extends DAO {

	/**
	 * Default Constructor
	 */
	public ProductCategoryDAO() {
	}

	/**
	 * This will get the product category value object from the database using
	 * hibernate.
	 * 
	 * @return --ProductCategory
	 */
	public ProductCategory get(Short productCategoryID)
			throws ApplicationException, SystemException {
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			ProductCategory productCategory = (ProductCategory) session.get(
					ProductCategory.class, productCategoryID);
			ProductType productType = productCategory.getProductType();
			productType.getProductTypeID();
			return productCategory;
		} catch (HibernateProcessException hbe) {
			throw new SystemException(hbe);
		} catch (Exception exception) {
			throw new ProductDefinitionException(ProductDefinitionConstants.PRDINVALID);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * This will check if the product category exists in the database using
	 * hibernate.
	 * 
	 * @return --true if the product category name exists
	 */
	public boolean ifProductCategoryExists(String productCategoryName)
			throws SystemException, ApplicationException {
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			Query query = session
					.getNamedQuery(NamedQueryConstants.PRODUCTCATEGORIES_COUNT_CREATE);
			query.setString(ProductDefinitionConstants.PRODUCTCATEGORYNAME,
					productCategoryName);
			int size = (Integer) query.uniqueResult();
			if (size > 0) {
				return true;
			}
			return false;
		} catch (HibernateProcessException hbe) {
			throw new SystemException(hbe);
		} catch (Exception exception) {
			throw new ProductDefinitionException(ProductDefinitionConstants.PRDINVALID);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * This will check if the product category exists in the database using
	 * hibernate.
	 * 
	 * @return --true if the product category name exists and the product
	 *         category id is not equal to this product category id.
	 */
	public boolean ifProductCategoryForUpdateExists(String productCategoryName,
			Short productCategoryID) throws SystemException,
			ApplicationException {
		Session session = null;
		try {
			session = HibernateUtil.getSession();

			Query query = session
					.getNamedQuery(NamedQueryConstants.PRODUCTCATEGORIES_COUNT_UPDATE);
			query.setString(ProductDefinitionConstants.PRODUCTCATEGORYNAME,
					productCategoryName);
			query.setShort(ProductDefinitionConstants.PRODUCTCATEGORYID,
					productCategoryID);
			int size = (Integer) query.uniqueResult();
			if (size > 0) {
				return true;
			}
			return false;
		} catch (HibernateProcessException hbe) {
			throw new SystemException(hbe);
		} catch (Exception exception) {
			throw new ProductDefinitionException(ProductDefinitionConstants.PRDINVALID);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * This method saves the product category. Before saving it will generate
	 * the global product category number
	 * 
	 * @param productCategory
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void saveProductCategory(ProductCategory productCategory)
			throws SystemException, ApplicationException {
		Session session = null;
		Transaction transaction =null;
		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();

			Query query = session.getNamedQuery(NamedQueryConstants.PRODUCTCATEGORIES_MAX);
			int maxPrdID = query.uniqueResult() != null ? (Short) query
					.uniqueResult() : ProductDefinitionConstants.DEFAULTMAX;
			StringBuilder globalPrdCategoryNum = new StringBuilder();
			globalPrdCategoryNum.append(productCategory
					.getGlobalPrdOfferingNum());
			globalPrdCategoryNum.append(StringUtils.lpad(String.valueOf(++maxPrdID),'0',3));
			productCategory.setGlobalPrdOfferingNum(globalPrdCategoryNum.toString());

			session.save(productCategory);
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
			if(null != transaction) {
				transaction.rollback();
			}
			throw new ProductDefinitionException(ProductDefinitionConstants.PRDINVALID);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * This will update the existing product category
	 * 
	 * @param productCategory
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void updateProductCategory(ProductCategory productCategory)
			throws SystemException, ApplicationException {
		Session session = null;
		Transaction transaction =null;
		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();
			session.update(productCategory);
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
			if(null != transaction) {
				transaction.rollback();
			}
			throw new ProductDefinitionException(ProductDefinitionConstants.PRDINVALID);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}
	
	/**
	 * This method will return the list of all product categories present in the database.
	 * 
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public List<ProductCategory> search() throws SystemException,
			ApplicationException {
		Session session = null;
		List<ProductCategory> productCategoryList = new ArrayList<ProductCategory>();
		try {
			session = HibernateUtil.getSession();
			Query query = session.getNamedQuery(NamedQueryConstants.PRODUCTCATEGORIES_SEARCH);
			Iterator<ProductCategory> iter = query.iterate();
			while (iter.hasNext()) {
				ProductCategory productCategory = iter.next();
				productCategory.getProductType().getProductTypeID();
				productCategory.getPrdCategoryStatus();
				productCategory.getPrdCategoryStatus().getPrdCategoryStatusId();
				productCategoryList.add(productCategory);
			}
			return productCategoryList;
		} catch (HibernateProcessException hbe) {
			throw new SystemException(hbe);
		} catch (Exception e) {
			throw new ProductDefinitionException(ProductDefinitionConstants.PRDINVALID);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * This method will get the product types in the given locale
	 * 
	 * @param localeId
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public SearchResults getProductTypeSearchResults(Short localeId)
			throws SystemException, ApplicationException {
		try {
			MasterDataRetriever masterDataRetriever = new MasterDataRetriever();
			masterDataRetriever.prepare(NamedQueryConstants.PRDTYPE,
					ProductDefinitionConstants.PRODUCTTYPE);
			masterDataRetriever.setParameter(
					ProductDefinitionConstants.LOCALEID, localeId);
			return masterDataRetriever.retrieve();
		} catch (HibernateProcessException hbe) {
			throw new SystemException(hbe);
		} catch (Exception e) {
			throw new ProductDefinitionException(ProductDefinitionConstants.PRDINVALID);
		}
	}
	
	/**
	 * This method will get all the product category status in the given locale 
	 * @param localeId
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public SearchResults getPrdCategoryStatusSearchResults(Short localeId)
			throws SystemException, ApplicationException {
		try {
			MasterDataRetriever masterDataRetriever = new MasterDataRetriever();
			masterDataRetriever.prepare(NamedQueryConstants.PRDCATEGORYSTATUS,
					ProductDefinitionConstants.PRDCATEGORYSTATUSLIST);
			masterDataRetriever.setParameter(
					ProductDefinitionConstants.LOCALEID, localeId);
			return masterDataRetriever.retrieve();
		} catch (HibernateProcessException hbe) {
			throw new SystemException(hbe);
		} catch (Exception e) {
			throw new ProductDefinitionException(ProductDefinitionConstants.PRDINVALID);
		}
	}
}
