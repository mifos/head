package org.mifos.application.productdefinition.business.service;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.master.business.BusinessActivityEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.application.productdefinition.business.service.ProductCategoryBusinessService;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidUserException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestProductCategoryBusinessService extends MifosTestCase {

	private ProductCategoryBusinessService productCategoryBusinessService=null;
	
	private UserContext userContext=null;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		productCategoryBusinessService=new ProductCategoryBusinessService();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		productCategoryBusinessService=null;
		userContext=null;
	}

	public void testGetProductTypes() throws NumberFormatException, InvalidUserException, SystemException, ApplicationException{
		List<ProductTypeEntity> productTypeList=productCategoryBusinessService.getProductTypes();
		assertEquals(2,productTypeList.size());
		for(ProductTypeEntity productTypeEntity : productTypeList){
			productTypeEntity.setUserContext(TestObjectFactory.getUserContext());
			if(productTypeEntity.getProductTypeID().equals(Short.valueOf("1")))
				assertEquals("Loan",productTypeEntity.getName());
			else
				assertEquals("Margin Money",productTypeEntity.getName());
		}
	}
	
	public void testFindByGlobalNum() throws Exception {
		ProductCategoryBO productCategoryBO = createProductCategory();
		assertNotNull(productCategoryBusinessService.findByGlobalNum(productCategoryBO.getGlobalPrdCategoryNum()));
		deleteProductCategory(productCategoryBO);
	}
	
	public void testGetProductCategoryStatusList() throws PersistenceException{
		assertEquals(2,productCategoryBusinessService.getProductCategoryStatusList().size());
	}
	
	public void testGetAllCategories() throws Exception{
		assertEquals(2,productCategoryBusinessService.getAllCategories().size());
		ProductCategoryBO productCategoryBO = createProductCategory();
		assertEquals(3,productCategoryBusinessService.getAllCategories().size());
		deleteProductCategory(productCategoryBO);
	}
	
	
	private List<ProductCategoryBO> getProductCategory() {
		return (List<ProductCategoryBO>) HibernateUtil
				.getSessionTL()
				.createQuery(
						"from org.mifos.application.productdefinition.business.ProductCategoryBO pcb order by pcb.productCategoryID")
				.list();
	}

	private void deleteProductCategory(ProductCategoryBO productCategoryBO) {
		Session session = HibernateUtil.getSessionTL();
		Transaction transaction = HibernateUtil.startTransaction();
		session.delete(productCategoryBO);
		transaction.commit();
	}
	
	private ProductCategoryBO createProductCategory() throws Exception{
		userContext=TestObjectFactory.getUserContext();
		ProductCategoryBO productCategoryBO =new ProductCategoryBO(userContext,productCategoryBusinessService.getProductTypes().get(0),"product category","created a category");
		productCategoryBO.save();
		HibernateUtil.commitTransaction();
		return (ProductCategoryBO)HibernateUtil
		.getSessionTL()
		.createQuery(
				"from org.mifos.application.productdefinition.business.ProductCategoryBO pcb order by pcb.productCategoryID")
		.list().get(2);
	}

}
