package org.mifos.accounts.productdefinition.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.accounts.productdefinition.persistence.SavingsProductDao;
import org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.accounts.productdefinition.business.ProductTypeEntity;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.NamedQueryConstants;
import org.mifos.service.BusinessRuleException;

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

    @SuppressWarnings("unchecked")
    @Override
    public List<Object[]> findAllSavingsProducts() {
        return (List<Object[]>) genericDao.executeNamedQuery("findAllSavingsProducts", null);
    }

    @Override
    public void save(SavingsOfferingBO product) {
        this.genericDao.createOrUpdate(product);
    }

    @Override
    public SavingsOfferingBO findById(Integer productId) {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("prdOfferingId", productId.shortValue());

        return (SavingsOfferingBO) this.genericDao.executeUniqueResultNamedQuery("savingsProduct.byid", queryParameters);
    }

    @Override
    public void validateProductWithSameNameDoesNotExist(final String name) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(ProductDefinitionConstants.PRDOFFERINGNAME, name);
        Number matchingProducts = (Number) this.genericDao.executeUniqueResultNamedQuery(NamedQueryConstants.PRODUCTOFFERING_CREATEOFFERINGNAMECOUNT, queryParameters);
        if (matchingProducts.intValue() > 0) {
            throw new BusinessRuleException("Duplicate.generalDetails.name");
        }
    }

    @Override
    public void validateProductWithSameShortNameDoesNotExist(final String shortName) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(ProductDefinitionConstants.PRDOFFERINGSHORTNAME, shortName);
        Number matchingProducts = (Number) this.genericDao.executeUniqueResultNamedQuery(NamedQueryConstants.PRODUCTOFFERING_CREATEOFFERINGSHORTNAMECOUNT, queryParameters);
        if (matchingProducts.intValue() > 0) {
            throw new BusinessRuleException("Duplicate.generalDetails.shortName");
        }
    }
}