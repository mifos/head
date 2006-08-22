package org.mifos.application.productdefinition.business;

import java.util.List;

import org.mifos.application.master.business.BusinessActivityEntity;
import org.mifos.application.productdefinition.business.service.ProductCategoryBusinessService;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidUserException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestProductCategoryBusinessService extends MifosTestCase {

	private ProductCategoryBusinessService productCategoryBusinessService=null;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		productCategoryBusinessService=new ProductCategoryBusinessService();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		productCategoryBusinessService=null;
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
}
