package org.mifos.application.productdefinition.business.service;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestProductCategoryBusinessService extends MifosIntegrationTest {

	public TestProductCategoryBusinessService() throws SystemException, ApplicationException {
        super();
    }

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
		HibernateUtil.closeSession();
	}

	/**
	 * TODO: this test depends on data ("Savings"->"Margin Money") 
	 * which is set up in
	 * ConfigurationTestSuite, so it won't pass if run on its own.
	 */
	public void testGetProductTypes() throws Exception {
		UserContext context = TestUtils.makeUser();

		List<ProductTypeEntity> productTypeList=
			productCategoryBusinessService.getProductTypes();
		assertEquals(2,productTypeList.size());
		for(ProductTypeEntity productTypeEntity : productTypeList){
			productTypeEntity.setUserContext(context);
			if(productTypeEntity.getProductTypeID().equals(
					ProductType.LOAN.getValue()))
				assertEquals("Loan",productTypeEntity.getName());
			else
				//assertEquals("Margin Money",productTypeEntity.getName());
				assertEquals("Savings",productTypeEntity.getName());
		}
	}
	
	public void testGetProductTypesFailure() throws Exception{
		TestObjectFactory.simulateInvalidConnection();
		try {
			productCategoryBusinessService.getProductTypes();
			fail();
		} catch (ServiceException e) {
			assertTrue(true);
		}
	}

	
	public void testFindByGlobalNum() throws Exception {
		ProductCategoryBO productCategoryBO = createProductCategory();
		assertNotNull(productCategoryBusinessService.findByGlobalNum(productCategoryBO.getGlobalPrdCategoryNum()));
		deleteProductCategory(productCategoryBO);
	}
	
	public void testFindByGlobalNumFailure() throws Exception{
		ProductCategoryBO productCategoryBO = createProductCategory();
		TestObjectFactory.simulateInvalidConnection();
		try {
			productCategoryBusinessService.findByGlobalNum(productCategoryBO.getGlobalPrdCategoryNum());
			fail();
		} catch (ServiceException e) {
			HibernateUtil.closeSession();
			deleteProductCategory(productCategoryBO);
		}
	}
	
	public void testGetProductCategoryStatusList() throws Exception{
		assertEquals(2,
			productCategoryBusinessService
				.getProductCategoryStatusList().size());
	}
	
	public void testGetProductCategoryStatusListFailure() throws Exception{
		TestObjectFactory.simulateInvalidConnection();
		try {
			productCategoryBusinessService.getProductCategoryStatusList();
			fail();
		} catch (ServiceException e) {
			assertTrue(true);
		}
	}

	
	public void testGetAllCategories() throws Exception{
		assertEquals(2,productCategoryBusinessService.getAllCategories().size());
		ProductCategoryBO productCategoryBO = createProductCategory();
		assertEquals(3,productCategoryBusinessService.getAllCategories().size());
		deleteProductCategory(productCategoryBO);
	}
	
	public void testGetAllCategoriesFailure() throws Exception{
		TestObjectFactory.simulateInvalidConnection();
		try {
			productCategoryBusinessService.getAllCategories();
			fail();
		} catch (ServiceException e) {
			assertTrue(true);
		}
	}
	

	private void deleteProductCategory(ProductCategoryBO productCategoryBO) {
		Session session = HibernateUtil.getSessionTL();
		Transaction transaction = HibernateUtil.startTransaction();
		session.delete(productCategoryBO);
		transaction.commit();
	}
	
	private ProductCategoryBO createProductCategory() throws Exception{
		userContext = TestUtils.makeUser();
		ProductCategoryBO productCategoryBO =new ProductCategoryBO(
			userContext,
			productCategoryBusinessService.getProductTypes().get(0),
			"product category",
			"created a category");
		productCategoryBO.save();
		HibernateUtil.commitTransaction();
		return (ProductCategoryBO)HibernateUtil
			.getSessionTL()
			.createQuery(
				"from " +
				ProductCategoryBO.class.getName() +
				" pcb order by pcb.productCategoryID")
			.list().get(2);
	}

}
