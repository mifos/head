package org.mifos.application.productdefinition.persistence;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;

public class TestProductCategoryPersistence extends MifosTestCase {

	private ProductCategoryPersistence productCategoryPersistence;
	
	private UserContext userContext=null;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		productCategoryPersistence=new ProductCategoryPersistence();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		productCategoryPersistence=null;
		userContext=null;
	}
	
	public void testGetMaxPrdCategoryId() throws Exception{
		assertEquals(Short.valueOf("2"),productCategoryPersistence.getMaxPrdCategoryId());
	}
	
	public void testGetProductCategory() throws Exception{
		assertEquals(Integer.valueOf("0"),productCategoryPersistence.getProductCategory("product"));
	}
	
	/**
	 * TODO: this test depends on data ("Savings"->"Margin Money") 
	 * which is set up in
	 * ConfigurationTestSuite, so it won't pass if run on its own.
	 */
	public void testGetProductTypes() throws Exception {
		List<ProductTypeEntity> productTypeList=
			productCategoryPersistence.getProductTypes();
		assertEquals(2,productTypeList.size());
		for(ProductTypeEntity productTypeEntity : productTypeList){
			productTypeEntity.setUserContext(TestUtils.makeUser());
			if (productTypeEntity.getType() == ProductType.LOAN) {
				assertEquals("Loan",productTypeEntity.getName());
			}
			else {
				assertEquals("Margin Money",productTypeEntity.getName());
			}
		}
	}
	
	public void testGetPrdCategory() throws Exception {
		assertEquals(Integer.valueOf("0"),
			productCategoryPersistence.getProductCategory(
				"product category",getProductCategory().get(0).getProductCategoryID()));
		ProductCategoryBO productCategoryBO = createProductCategory();
		assertEquals(Integer.valueOf("0"),productCategoryPersistence.getProductCategory("product category",getProductCategory().get(2).getProductCategoryID()));
		assertEquals(Integer.valueOf("1"),productCategoryPersistence.getProductCategory("product category",getProductCategory().get(1).getProductCategoryID()));
		deleteProductCategory(productCategoryBO);
	}
	
	public void testFindByGlobalNum() throws Exception {
		ProductCategoryBO productCategoryBO = createProductCategory();
		assertNotNull(productCategoryPersistence.findByGlobalNum(productCategoryBO.getGlobalPrdCategoryNum()));
		deleteProductCategory(productCategoryBO);
	}
	
	public void testGetProductCategoryStatusList()throws Exception{
		assertEquals(2,productCategoryPersistence.getProductCategoryStatusList().size());
	}
	
	public void testGetAllCategories() throws Exception{
		assertEquals(2,productCategoryPersistence.getAllCategories().size());
		ProductCategoryBO productCategoryBO = createProductCategory();
		assertEquals(3,productCategoryPersistence.getAllCategories().size());
		deleteProductCategory(productCategoryBO);
	}
	

	private List<ProductCategoryBO> getProductCategory() {
		return HibernateUtil
			.getSessionTL()
			.createQuery(
				"from " +
				ProductCategoryBO.class.getName() +
				" pcb order by pcb.productCategoryID")
			.list();
	}

	private void deleteProductCategory(ProductCategoryBO productCategoryBO) {
		Session session = HibernateUtil.getSessionTL();
		Transaction transaction = HibernateUtil.startTransaction();
		session.delete(productCategoryBO);
		transaction.commit();
	}
	
	private ProductCategoryBO createProductCategory() throws Exception{
		userContext=TestUtils.makeUser();
		ProductCategoryBO productCategoryBO =new ProductCategoryBO(userContext,productCategoryPersistence.getProductTypes().get(0),"product category","created a category");
		productCategoryBO.save();
		HibernateUtil.commitTransaction();
		return getProductCategory().get(2);
	}

}
