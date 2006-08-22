package org.mifos.application.productdefinition.persistence;

import java.util.List;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;



public class ProductCategoryPersistence extends Persistence {

	public Short getMaxPrdCategoryId() {
		return (Short) HibernateUtil.getSessionTL().getNamedQuery(
				NamedQueryConstants.PRODUCTCATEGORIES_MAX).uniqueResult();
	}

	public Integer getProductCategory(String productCategoryName) {
		return (Integer) HibernateUtil.getSessionTL().getNamedQuery(
				NamedQueryConstants.PRODUCTCATEGORIES_COUNT_CREATE).setString(
				ProductDefinitionConstants.PRODUCTCATEGORYNAME,
				productCategoryName).uniqueResult();
	}
	
	public List<ProductTypeEntity> getProductTypes(){
		return (List<ProductTypeEntity>) HibernateUtil.getSessionTL().getNamedQuery(
				NamedQueryConstants.GET_PRD_TYPES).list();
	}

}
