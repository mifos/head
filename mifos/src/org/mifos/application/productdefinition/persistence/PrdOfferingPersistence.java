package org.mifos.application.productdefinition.persistence;

import java.util.HashMap;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.productdefinition.business.PrdStatusEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.util.helpers.PrdCategoryStatus;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;

public class PrdOfferingPersistence extends Persistence {
	private MifosLogger prdLogger = MifosLogManager
			.getLogger(LoggerConstants.PRDDEFINITIONLOGGER);

	public Short getMaxPrdOffering() throws PersistenceException {
		prdLogger.debug("getting the max prd offering id");
		try {
			return (Short) HibernateUtil.getSessionTL().getNamedQuery(
					NamedQueryConstants.PRODUCTOFFERING_MAX).uniqueResult();
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
	}

	public PrdStatusEntity getPrdStatus(PrdStatus prdStatus)
			throws PersistenceException {
		prdLogger.debug("getting the product status for :" + prdStatus);
		try {
			Session session = HibernateUtil.getSessionTL();
			PrdStatusEntity prdStatusEntity = (PrdStatusEntity) session.get(
					PrdStatusEntity.class, prdStatus.getValue());
			return prdStatusEntity;
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
	}

	public Integer getProductOfferingNameCount(String productOfferingName)
			throws PersistenceException {
		prdLogger.debug("getting the product offering name count for :"
				+ productOfferingName);
		try {
			return (Integer) HibernateUtil
					.getSessionTL()
					.getNamedQuery(
							NamedQueryConstants.PRODUCTOFFERING_CREATEOFFERINGNAMECOUNT)
					.setString(ProductDefinitionConstants.PRDOFFERINGNAME,
							productOfferingName).uniqueResult();
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
	}

	public Integer getProductOfferingShortNameCount(
			String productOfferingShortName) throws PersistenceException {
		prdLogger.debug("getting the product offering short name count for :"
				+ productOfferingShortName);
		try {
			return (Integer) HibernateUtil
					.getSessionTL()
					.getNamedQuery(
							NamedQueryConstants.PRODUCTOFFERING_CREATEOFFERINGSHORTNAMECOUNT)
					.setString(ProductDefinitionConstants.PRDOFFERINGSHORTNAME,
							productOfferingShortName).uniqueResult();
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
	}

	public List<ProductCategoryBO> getApplicableProductCategories(
			ProductType productType, PrdCategoryStatus prdCategoryStatus)
			throws PersistenceException {
		prdLogger.debug("getting the applicable product categories");
		try {
			HashMap<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put(ProductDefinitionConstants.PRODUCTTYPEID,
					productType.getValue());
			queryParameters.put(
					ProductDefinitionConstants.PRODUCTCATEGORYSTATUSID,
					prdCategoryStatus.getValue());
			List<ProductCategoryBO> queryResult = executeNamedQuery(
					NamedQueryConstants.PRDAPPLICABLE_CATEGORIES,
					queryParameters);

			if (null != queryResult && queryResult.size() > 0) {
				for (ProductCategoryBO productCategory : queryResult) {
					productCategory.getProductType();
				}
			}
			prdLogger
					.debug("getting the applicable product categories Done and : "
							+ queryResult);
			return queryResult;
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
	}
}
