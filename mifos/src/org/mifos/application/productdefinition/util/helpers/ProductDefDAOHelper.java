package org.mifos.application.productdefinition.util.helpers;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.productdefinition.util.valueobjects.ProductCategory;
import org.mifos.framework.dao.helpers.MasterDataRetriever;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.valueobjects.SearchResults;

public class ProductDefDAOHelper {

	private ProductDefDAOHelper() {
		super();
	}
	
	private static ProductDefDAOHelper instance=new ProductDefDAOHelper();
	
	public static ProductDefDAOHelper getInstance() {
		return instance;
	}
	
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

}
