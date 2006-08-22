package org.mifos.application.productdefinition.persistence;

import java.util.List;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidUserException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestProductCategoryPersistence extends MifosTestCase {

	private ProductCategoryPersistence productCategoryPersistence;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		productCategoryPersistence=new ProductCategoryPersistence();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		productCategoryPersistence=null;
	}
	
	public void testGetMaxPrdCategoryId(){
		assertEquals(Short.valueOf("2"),productCategoryPersistence.getMaxPrdCategoryId());
	}
	
	public void testGetProductCategory() {
		assertEquals(Integer.valueOf("0"),productCategoryPersistence.getProductCategory("product"));
	}
	
	public void testGetProductTypes() throws InvalidUserException, SystemException, ApplicationException{
		List<ProductTypeEntity> productTypeList=productCategoryPersistence.getProductTypes();
		assertEquals(2,productTypeList.size());
		for(ProductTypeEntity productTypeEntity : productTypeList){
			productTypeEntity.setUserContext(TestObjectFactory.getUserContext());
			if(productTypeEntity.getProductTypeID().equals(Short.valueOf("1")))
				assertEquals("Loan",productTypeEntity.getName());
			else
				assertEquals("Margin Money",productTypeEntity.getName());
		}
	}
	

	
}
