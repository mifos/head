package org.mifos.application.productdefinition.business.service;




import java.util.List;

import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.application.productdefinition.persistence.ProductCategoryPersistence;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.security.util.UserContext;

public class ProductCategoryBusinessService extends BusinessService {

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}
	
	public List<ProductTypeEntity> getProductTypes() throws PersistenceException{
		return new ProductCategoryPersistence().getProductTypes();
	}

}
