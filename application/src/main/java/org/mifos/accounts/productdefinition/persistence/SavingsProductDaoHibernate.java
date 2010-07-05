package org.mifos.accounts.productdefinition.persistence;

import java.util.HashMap;

import org.mifos.accounts.productdefinition.persistence.SavingsProductDao;
import org.mifos.accounts.productdefinition.business.ProductTypeEntity;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.util.helpers.AccountTypes;

public class SavingsProductDaoHibernate implements SavingsProductDao {

    private final GenericDao genericDao;

    public SavingsProductDaoHibernate(final GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    @Override
    public ProductTypeEntity findSavingsProductConfiguration() {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("productTypeId", AccountTypes.SAVINGS_ACCOUNT.getValue());

        return (ProductTypeEntity) this.genericDao.executeUniqueResultNamedQuery("findProductTypeConfigurationById", queryParameters);
    }

    @Override
    public void save(ProductTypeEntity productType) {
        this.genericDao.createOrUpdate(productType);
    }
}