package org.mifos.application.productdefinition.persistence;

import java.util.List;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.productdefinition.business.PrdCategoryStatusEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
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
	
	public Integer getProductCategory(String productCategoryName,
			Short productCategoryId) {
		return (Integer) HibernateUtil.getSessionTL().getNamedQuery(
				NamedQueryConstants.PRODUCTCATEGORIES_COUNT_UPDATE).setString(
				ProductDefinitionConstants.PRODUCTCATEGORYNAME,
				productCategoryName)
				.setShort(ProductDefinitionConstants.PRODUCTCATEGORYID,
						productCategoryId).uniqueResult();
	}
	
	public List<ProductTypeEntity> getProductTypes(){
		return (List<ProductTypeEntity>) HibernateUtil.getSessionTL().getNamedQuery(
				NamedQueryConstants.GET_PRD_TYPES).list();
	}
	
	public ProductCategoryBO findByGlobalNum(String globalNum) {
		return (ProductCategoryBO) HibernateUtil.getSessionTL().getNamedQuery(
				NamedQueryConstants.GET_PRODUCTCATEGORY).setString("globalNum",
				globalNum).uniqueResult();
	}
	
	public List<PrdCategoryStatusEntity> getProductCategoryStatusList(){
		return (List<PrdCategoryStatusEntity>) HibernateUtil.getSessionTL().getNamedQuery(
				NamedQueryConstants.GET_PRDCATEGORYSTATUS).list(); 
	}
	
	public List<ProductCategoryBO> getAllCategories(){
		return (List<ProductCategoryBO>) HibernateUtil.getSessionTL().getNamedQuery(
				NamedQueryConstants.PRODUCTCATEGORIES_SEARCH).list(); 
	}
	
}
